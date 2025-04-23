package io.baji.stvh.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.baji.stvh.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserMapperTest {

    @Resource
    private UserMapper userMapper;


    @Test
    void addUser() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");

        userMapper.insert(user);
    }

    @Test
    void getUser(){

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username","test");
        queryWrapper.eq("password","123456");
        User user = userMapper.selectOne(queryWrapper);
        System.out.println(user);
    }
}