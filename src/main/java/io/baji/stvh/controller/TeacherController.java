package io.baji.stvh.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.baji.stvh.Utils.ExcelUtils;
import io.baji.stvh.dto.PageInfo;
import io.baji.stvh.dto.Result;
import io.baji.stvh.dto.StudentScoreDto;
import io.baji.stvh.entity.Score;
import io.baji.stvh.entity.TestInfo;
import io.baji.stvh.entity.User;
import io.baji.stvh.entity.UserRole;
import io.baji.stvh.enums.ResponseStateEnum;
import io.baji.stvh.enums.UserRoleEnum;
import io.baji.stvh.exception.SystemException;
import io.baji.stvh.mapper.ScoreMapper;
import io.baji.stvh.mapper.TestInfoMapper;
import io.baji.stvh.service.impl.ScoreServiceImpl;
import io.baji.stvh.service.impl.TestInfoServiceImpl;
import io.baji.stvh.service.impl.UserServiceImpl;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@RestController
@RequestMapping("/teacher")
public class TeacherController {

    @Resource
    UserServiceImpl userService;

    @Resource
    TestInfoServiceImpl testInfoService;

    @Resource
    ScoreMapper scoreMapper;

    @Resource
    ScoreServiceImpl scoreService;

    @GetMapping("/list")
    public Result list(@RequestParam Map<String, Object> params) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        PageInfo pageInfo = objectMapper.readValue((String) params.get("pageInfo"), PageInfo.class);

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", params.get("teacherId"));
        User teacher = userService.getOne(queryWrapper);

        String keyword = (String) params.get("keyword");

        IPage<User> teacherStudents = userService.getTeacherStudents(teacher.getGrade(), teacher.getClassName(), keyword, pageInfo);
        pageInfo.setTotal(teacherStudents.getTotal());
        return Result.success(teacherStudents.getRecords(), pageInfo);
    }

    @GetMapping("/downloadAddStudentScoreExcelTemp")
    public Result downloadAddStudentScoreExcelTemp(HttpServletResponse response) throws IOException{
        org.springframework.core.io.Resource resource = new ClassPathResource("/tempFile/学生成绩添加模板.xlsx");

        //文件名编码，防止中文乱码
        String filename = URLEncoder.encode("学生成绩添加模板", "UTF-8");
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

    @GetMapping("/getStudentScoresByTestInfoId")
    public Result getStudentScoresByTestInfoId(@RequestParam Map<String, Object> params) throws JsonProcessingException {
        Integer testInfoId = Integer.parseInt((String) params.get("testInfoId"));
        List<StudentScoreDto> dtos = new ArrayList<>();
        scoreMapper.selectList(new QueryWrapper<Score>().eq("test_info_id", testInfoId)).forEach(score -> {
            TestInfo testInfo = testInfoService.getOne(new QueryWrapper<TestInfo>().eq("id", score.getTestInfoId()));
            User user = userService.getOne(new QueryWrapper<User>().eq("id", score.getUserId()));
            StudentScoreDto dto = new StudentScoreDto();
            dto.setId(score.getId());
            dto.setUserId(score.getUserId());
            dto.setUsername(user.getUsername());
            dto.setTestInfoId(score.getTestInfoId());
            dto.setTestName(testInfo.getTestName());
            dto.setTestTime(testInfo.getTestTime());
            dto.setUserFullName(user.getFullName());
            dto.setChinese(score.getChinese());
            dto.setMath(score.getMath());
            dto.setEnglish(score.getEnglish());
            dto.setPolitics(score.getPolitics());
            dto.setSport(score.getSport());
            dtos.add(dto);
        });

        return Result.success(dtos);

    }

    @GetMapping("getStudentScores")
    public Result getStudentScores(@RequestParam Map<String, Object> params) {
        Integer userId = Integer.parseInt((String) params.get("userId"));
        List<StudentScoreDto> dtos = new ArrayList<>();
        scoreMapper.selectList(new QueryWrapper<Score>().eq("user_id", userId)).forEach(score -> {
            TestInfo testInfo = testInfoService.getOne(new QueryWrapper<TestInfo>().eq("id", score.getTestInfoId()));
            User user = userService.getOne(new QueryWrapper<User>().eq("id", userId));
            StudentScoreDto dto = new StudentScoreDto();
            dto.setId(score.getId());
            dto.setUserId(userId);
            dto.setUsername(user.getUsername());
            dto.setTestInfoId(score.getTestInfoId());
            dto.setTestName(testInfo.getTestName());
            dto.setTestTime(testInfo.getTestTime());
            dto.setUserFullName(user.getFullName());
            dto.setChinese(score.getChinese());
            dto.setMath(score.getMath());
            dto.setEnglish(score.getEnglish());
            dto.setPolitics(score.getPolitics());
            dto.setSport(score.getSport());
            dtos.add(dto);
        });

        return Result.success(dtos);
    }

    @PostMapping("/deleteStudentScore")
    public Result deleteStudentScore(@RequestBody Score score) {
        scoreMapper.deleteById(score);
        return Result.success();
    }

    @PostMapping("/addStudentScoreByExcel")
    public Result addStudentScoreByExcel(@RequestParam("file") MultipartFile file, @RequestParam Integer testInfoId) throws IOException {
        if (file.isEmpty()) {
            return Result.error(ResponseStateEnum.ADD_USER_EXCEL_ERROR);
        }

        String fileName = file.getOriginalFilename();  //获得上传的excel文件名
        String fileSuffix = fileName.substring(fileName.lastIndexOf(".")+1);  //获取上传的excel文件名后缀

        XSSFWorkbook xwb = new XSSFWorkbook(file.getInputStream()); //获取excel工作簿

        XSSFSheet xssfSheet = xwb.getSheetAt(0); //获取excel的sheet

        if(xssfSheet == null) {
            return Result.error(ResponseStateEnum.ADD_USER_EXCEL_ERROR);
        }
        QueryWrapper wrapper = new QueryWrapper();

        for(int rowNum = 1; rowNum < xssfSheet.getLastRowNum()+1; rowNum++) {
            XSSFRow xssfRow = xssfSheet.getRow(rowNum);
            if(xssfRow == null) {
                continue;
            }

            Score score = new Score();

            //循环获取excel每一行的每一列
            for(int cellNum = 0; cellNum < xssfRow.getLastCellNum(); cellNum++) {
                wrapper.clear();

                XSSFCell xssCell = xssfRow.getCell(cellNum);
                if(xssCell == null) {
                    continue;
                }

                // 第一列 username
                if(cellNum == 0) {
                    String username = (String) ExcelUtils.getXSSFValue(xssCell);
                    if (StringUtils.isEmpty(username)) {
                        continue;
                    }
                    wrapper.eq("username", username);
                    User one = userService.getOne(wrapper);
                    if (one == null) {
                        return Result.error(ResponseStateEnum.ADD_STUDENT_SCORE_ERROR1.getCode(), ResponseStateEnum.ADD_STUDENT_SCORE_ERROR1.getMsg() + "  <" + username + ">");
                    }

                    wrapper.clear();
                    wrapper.eq("user_id", one.getId());
                    wrapper.eq("test_info_id", testInfoId);

                    Score score1 = scoreMapper.selectOne(wrapper);
                    if (score1 != null) {
                        return Result.error(ResponseStateEnum.ADD_STUDENT_SCORE_ERROR2.getCode(), ResponseStateEnum.ADD_STUDENT_SCORE_ERROR2.getMsg() + "  <" + username + ">");

                    }
                    wrapper.clear();

                    score.setUserId(one.getId());
                    score.setTestInfoId(testInfoId);

                }else if(cellNum == 2) {
                    if (Objects.isNull(ExcelUtils.getXSSFValue(xssCell))) {
                        continue;
                    }
                    score.setChinese(((Double) ExcelUtils.getXSSFValue(xssCell)).intValue());
                }else if(cellNum == 3) {
                    if (Objects.isNull(ExcelUtils.getXSSFValue(xssCell))) {
                        continue;
                    }
                    score.setMath(((Double) ExcelUtils.getXSSFValue(xssCell)).intValue());
                }else if(cellNum == 4) {
                    if (Objects.isNull(ExcelUtils.getXSSFValue(xssCell))) {
                        continue;
                    }
                    score.setEnglish(((Double) ExcelUtils.getXSSFValue(xssCell)).intValue());
                }else if(cellNum == 5) {
                    if (Objects.isNull(ExcelUtils.getXSSFValue(xssCell))) {
                        continue;
                    }
                    score.setPolitics(((Double) ExcelUtils.getXSSFValue(xssCell)).intValue());
                }else if(cellNum == 6) {
                    if (Objects.isNull(ExcelUtils.getXSSFValue(xssCell))) {
                        continue;
                    }
                    score.setSport(((Double) ExcelUtils.getXSSFValue(xssCell)).intValue());
                }
            }

            scoreMapper.insert(score);
        }

        return Result.success();
    }

    @GetMapping("/listAllTestInfo")
    public Result listAllTestInfo(@RequestParam Map<String, Object> params) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        PageInfo pageInfo = objectMapper.readValue((String) params.get("pageInfo"), PageInfo.class);

        Page<TestInfo> page = new Page<>(pageInfo.getCurrentPage(), pageInfo.getPageSize());

        String keyword = (String) params.get("keyword");

        QueryWrapper<TestInfo> queryWrapper = new QueryWrapper<>();

        if (!StringUtils.isEmpty(keyword)) {
            queryWrapper.like("test_name", keyword);
        }

        Page<TestInfo> page1 = testInfoService.page(page, queryWrapper);


        return Result.success(page1.getRecords(), pageInfo);
    }

    @PostMapping("/addTestInfo")
    public Result addTestInfo(@RequestBody TestInfo testInfo) {
        try {
            testInfoService.saveOrUpdate(testInfo);
        } catch (Exception e) {
            return Result.error(ResponseStateEnum.ADD_TEST_INFO_DUPLICATE_ERROR);
        }
        return Result.success();
    }

    @PostMapping("/deleteTestInfo")
    public Result deleteTestInfo(@RequestBody TestInfo testInfo) {
        testInfoService.removeById(testInfo.getId());
        QueryWrapper<Score> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("test_info_id", testInfo.getId());
        scoreService.remove(queryWrapper);
        return Result.success();
    }


    @GetMapping("/getAllTestInfo")
    public Result getAllTestInfo(@RequestParam Map<String, Object> params) {
        return Result.success(testInfoService.list());
    }

    @PostMapping("/addStudentScore")
    public Result addStudentScore(@RequestBody Score score) {

        if (Objects.isNull(score.getId())) {
            QueryWrapper<Score> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", score.getUserId());
            queryWrapper.eq("test_info_id", score.getTestInfoId());
            Score one = scoreService.getOne(queryWrapper);
            if (one != null) {
                return Result.error(ResponseStateEnum.ADD_STUDENT_SCORE_ERROR2);
            }
            scoreService.save(score);
        } else {
            scoreService.updateById(score);
        }

        return Result.success();
    }

    @GetMapping("/getOneScoreByTestInfoId")
    public Result getOneScoreByTestInfoId(@RequestParam Map<String, Object> params) {
        Integer userId = Integer.parseInt((String) params.get("userId"));
        if (Objects.isNull(params.get("testInfoId"))) {
            return Result.success(scoreMapper.getUserRecentlyScore(userId));
        } else {
            Integer testInfoId = Integer.parseInt((String) params.get("testInfoId"));
            return Result.success(scoreMapper.getStuScore(userId, testInfoId));
        }
    }

}
