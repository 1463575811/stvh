package io.baji.stvh.mapper;

import io.baji.stvh.entity.Menu;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MenuMapperTest {

    @Resource
    private MenuMapper menuMapper;

    @Test
    void getMenuTree() {
        List<Menu> menuTree = menuMapper.getMenuTree();

        menuTree.forEach(System.out::println);
    }

}