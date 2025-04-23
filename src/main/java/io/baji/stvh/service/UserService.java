package io.baji.stvh.service;

import io.baji.stvh.entity.User;

public interface UserService {

    /**
     * 通过用户名和密码进行登录
     * @param username
     * @param password
     * @return 用户信息
     */
    User login(String username, String password);

    /**
     * 用过token获取用户
     * @param token
     * @return
     */
    User getUserByToken(String token);
}
