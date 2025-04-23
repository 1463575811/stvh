package io.baji.stvh.controller;

import io.baji.stvh.dto.Result;
import io.baji.stvh.entity.Menu;
import io.baji.stvh.entity.User;
import io.baji.stvh.enums.ResponseStateEnum;
import io.baji.stvh.exception.SystemException;
import io.baji.stvh.service.UserService;
import io.baji.stvh.service.impl.MenuServiceImpl;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private MenuServiceImpl menuService;


    /**
     * 用户登录, 前端登录表单会传入用户账户名和密码
     * @param user
     * @return
     */
    @PostMapping("/login")
    public Result<User> login(@RequestBody User user) {

        // 判断前端传入的对象是否为空, 如果为空, 那么后台系统返回异常. 登录相关错误提示统一为 "用户名或密码错误"
        if (Objects.isNull(user)) {
            throw new SystemException(ResponseStateEnum.LOGIN_ERROR);
        }

        // 如果用户不为空, 那么访问数据库根据用户名和密码进行查询.
        User login = userService.login(user.getUsername(), user.getPassword());

        // 如果根据传入的用户名和密码没有查询到用户, 后台系统返回异常
        if (Objects.isNull(login)) {
            throw new SystemException(ResponseStateEnum.LOGIN_ERROR);
        }

        // 查看当前登录用户是否被限制登录, 如果被限制了登录则返回异常
        if (!login.getEnabled()){
            throw new SystemException(ResponseStateEnum.NOT_ALLOW_LOGIN);
        }

        // 如果前面检查都没有问题, 那么正常执行, 并返回用户信息
        return new Result<User>(login);
    }


    @GetMapping("/user/info")
    public Result getUserInfo(String token) {
        User user = userService.getUserByToken(token);

        List<Menu> menuTree = menuService.getMenuTree();

        Map<String, Object> map = new HashMap<>();

        map.put("user", user);
        map.put("menuTree", menuTree);

        return Result.success(map);
    }
}
