package io.baji.stvh.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.baji.stvh.Utils.ExcelUtils;
import io.baji.stvh.dto.PageInfo;
import io.baji.stvh.dto.Result;
import io.baji.stvh.dto.UserInfoDto;
import io.baji.stvh.entity.User;
import io.baji.stvh.entity.UserRole;
import io.baji.stvh.enums.ResponseStateEnum;
import io.baji.stvh.enums.UserRoleEnum;
import io.baji.stvh.exception.SystemException;
import io.baji.stvh.service.UserService;
import io.baji.stvh.service.impl.UserRoleServiceImpl;
import io.baji.stvh.service.impl.UserServiceImpl;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.core.io.Resource;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @javax.annotation.Resource
    private UserServiceImpl userService;

    @javax.annotation.Resource
    private UserRoleServiceImpl userRoleService;

    private static Map<String, Integer> gradeMap;

    {
        gradeMap = new HashMap<>();
        gradeMap.put("一年级", 1);
        gradeMap.put("二年级", 2);
        gradeMap.put("三年级", 3);
        gradeMap.put("四年级", 4);
        gradeMap.put("五年级", 5);
        gradeMap.put("六年级", 6);
    }


    //TODO 用户数据导出

    @GetMapping("/exportUserInfo")
    public Result exportUserInfo() {

        List<User> userWithRoles = userService.getUserRoles(userService.list());
        System.out.println(userWithRoles);
        List<UserInfoDto> userInfoDtoList = new ArrayList<>();

        userWithRoles.stream().filter(user -> "TEACHER".equals(user.getRoleNames().get(0)) || "STUDENT".equals(user.getRoleNames().get(0))).forEach(user -> {
            UserInfoDto userInfoDto = new UserInfoDto();
            userInfoDto.setUsername(user.getUsername());
            userInfoDto.setFullName(user.getFullName());
            userInfoDto.setSex(user.getSex() == 1 ? "男" : "女");
            userInfoDto.setBirthDate(user.getBirthDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            userInfoDto.setGrade(user.getGrade());
            userInfoDto.setClassName(user.getClassName().replace("班", "").concat("班"));
            userInfoDto.setRoleName(user.getRoleNames().get(0));
            userInfoDtoList.add(userInfoDto);
        });

        return Result.success(userInfoDtoList);
    }

    @PostMapping("/updateUserPassword")
    public Result updateUserPassword(@RequestBody User user) {
        userService.updateById(user);
        return Result.success();
    }

    @PostMapping("/resetUserLoginPassword")
    public Result resetUserLoginPassword(@RequestBody User user) {
        user.setPassword("123456");
        userService.updateById(user);
        return Result.success();
    }

    @GetMapping("/search")
    public Result searchUser(@RequestParam Map<String, Object> params) throws JsonProcessingException {

        String keyword = (String) params.get("keyword");
        ObjectMapper objectMapper = new ObjectMapper();
        PageInfo pageInfo = objectMapper.readValue((String) params.get("pageInfo"), PageInfo.class);

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("username", keyword);
        IPage<User> page = userService.page(new Page<>(pageInfo.getCurrentPage(), pageInfo.getPageSize()), queryWrapper);

        List<User> userList = page.getRecords();

        if (userList == null || userList.size() == 0) {
            queryWrapper.clear();
            queryWrapper.like("full_name", keyword);
            page = userService.page(new Page<>(pageInfo.getCurrentPage(), pageInfo.getPageSize()), queryWrapper);
            userList = page.getRecords();
        }

        List<User> userWithRoles = userService.getUserRoles(userList);

        pageInfo.setTotal(page.getTotal());

        return Result.success(userWithRoles, pageInfo);
    }

    @GetMapping("/listAllUser")
    public Result listAllUsers(PageInfo pageInfo) {

        IPage<User> page = userService.page(new Page<>(pageInfo.getCurrentPage(), pageInfo.getPageSize()));

        List<User> list = page.getRecords();

        List<User> userWithRoles = userService.getUserRoles(list);

        pageInfo.setTotal(page.getTotal());

        return Result.success(userWithRoles, pageInfo);
    }

    @PostMapping("/changeUserStatus")
    public Result changeUserStatus(@RequestBody User user) {

        user.setEnabled(!user.getEnabled());

        userService.updateById(user);

        return Result.success();
    }

    /**
     * 根据用户id, 删除单个选中的用户
     * @param id
     * @return
     */
    @PostMapping("/deleteUser")
    public Result deleteUser(@RequestBody User id) {
        userService.removeById(id);
        return Result.success();
    }

    /**
     * 根据用户id, 可以删除多个选中的用户
     * @param params
     * @return
     */
    @PostMapping("/deleteBunchUser")
    public Result deleteBunchUser(@RequestBody Map<String, Object> params) {
        if (params.get("ids") != null) {
            List<Integer> ids = (List<Integer>) params.get("ids");
            userService.removeByIds(ids);
        }
        return Result.success();
    }


    @PostMapping("/addUserByExcel")
    public Result addUserByExcel(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return Result.error(ResponseStateEnum.ADD_USER_EXCEL_ERROR);
        }

        String fileName = file.getOriginalFilename();  //获得上传的excel文件名
        String fileSuffix = fileName.substring(fileName.lastIndexOf(".")+1);  //获取上传的excel文件名后缀

        List<User> list = null;

        XSSFWorkbook xwb = new XSSFWorkbook(file.getInputStream()); //获取excel工作簿

        XSSFSheet xssfSheet = xwb.getSheetAt(0); //获取excel的sheet

        if(xssfSheet == null) {
            return Result.error(ResponseStateEnum.ADD_USER_EXCEL_ERROR);
        }

        list = new ArrayList<>();

        List<String> roleList = new ArrayList<>();

        //循环获取excel每一行
        for(int rowNum = 1; rowNum < xssfSheet.getLastRowNum()+1; rowNum++) {
            XSSFRow xssfRow = xssfSheet.getRow(rowNum);
            if(xssfRow == null) {
                continue;
            }

            User user = new User();

            //循环获取excel每一行的每一列
            for(int cellNum = 0; cellNum < xssfRow.getLastCellNum(); cellNum++) {
                XSSFCell xssCell = xssfRow.getCell(cellNum);
                if(xssCell == null) {
                    continue;
                }

                // 第一列 username
                if(cellNum == 0) {
                    Object value = ExcelUtils.getValue(xssCell);

                    if (value instanceof String) {
                        user.setUsername((String) value);
                    }else if(value instanceof Double) {
                        user.setUsername(String.valueOf(((Double) value).intValue()));
                    }

                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("username", user.getUsername());
                    List<User> list1 = userService.list(queryWrapper);
                    if (list1 != null && list1.size() > 0) {
                        throw new SystemException(ResponseStateEnum.ADD_USER_EXCEL_ERROR2);
                    }

                }else if(cellNum == 1) {
                    // 第二列 full name
                    user.setFullName((String) ExcelUtils.getXSSFValue(xssCell));

                }else if(cellNum == 2) {
                    // 第三列 性别
                    //Integer类型需要自行处理
                    user.setSex(ExcelUtils.getUserSex(xssCell));

                }else if(cellNum == 3) {
                    // 第四列 出生日期
                    user.setBirthDate(DateUtil.toLocalDateTime(xssCell.getDateCellValue()));

                } else if(cellNum == 4) {
                    // 第五列 年级

                    Integer grade = gradeMap.get((String) ExcelUtils.getXSSFValue(xssCell));
                    if (Objects.isNull(grade)) {
                        throw new SystemException(ResponseStateEnum.ADD_USER_EXCEL_ERROR3);
                    }
                    user.setGrade(grade);

                } else if(cellNum == 5) {
                    // 第六列 班级
                    user.setClassName((String)ExcelUtils.getXSSFValue(xssCell));

                } else if(cellNum == 6) {
                    // 第七列 用户权限
                    roleList.add((String) ExcelUtils.getXSSFValue(xssCell));
                }

            }

            user.setPassword("123456");
            user.setToken(UUID.randomUUID().toString());

            list.add(user);  //将excel每一行的数据封装到user对象,并将user对象添加到list
        }


        userService.saveBatch(list);

        System.out.println("保存之后");
        System.out.println(list);
        for (int i = 0; i < list.size(); i++) {

            User user = list.get(i);

            String userRole = roleList.get(i);

            Integer userRoleId = null;
            if (userRole.equals(UserRoleEnum.ADMIN.getRoleName())) {
                userRoleId = UserRoleEnum.ADMIN.getId();
            } else if (userRole.equals(UserRoleEnum.STUDENT.getRoleName())) {
                userRoleId = UserRoleEnum.STUDENT.getId();
            } else if (userRole.equals(UserRoleEnum.TEACHER.getRoleName())) {
                userRoleId = UserRoleEnum.TEACHER.getId();
            } else{
                throw new SystemException(ResponseStateEnum.ADD_USER_EXCEL_ERROR);
            }

            UserRole userRole1 = new UserRole(user.getId(), userRoleId);

            userRoleService.save(userRole1);
        }
        return Result.success();
    }

    @GetMapping("/downloadAddUserExcelTemp")
    public Result downloadAddUserExcelTemp(HttpServletResponse response) throws IOException{
        Resource resource = new ClassPathResource("/tempFile/用户添加模板.xlsx");
        System.out.println();

        //文件名编码，防止中文乱码
        String filename = URLEncoder.encode(resource.getFilename(), "UTF-8");
        // 设置响应头信息
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        // 内容类型为通用类型，表示二进制数据流
        response.setContentType("application/octet-stream");
        // 循环，边读取边输出，可避免大文件时OOM
        try (InputStream inputStream = new FileInputStream(resource.getFile()); OutputStream os = response.getOutputStream()) {
            byte[] bytes = new byte[1024];
            int readLength;
            while ((readLength = inputStream.read(bytes)) != -1) {
                os.write(bytes, 0, readLength);
            }
        }

        return Result.success();
    }

}
