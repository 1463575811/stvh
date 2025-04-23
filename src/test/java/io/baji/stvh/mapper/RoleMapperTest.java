package io.baji.stvh.mapper;

import io.baji.stvh.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RoleMapperTest {

    @Resource
    private RoleMapper roleMapper;

    @Test
    void selectUserRole(){

        Role role = roleMapper.selectById(100);
        System.out.println(role);
    };


}