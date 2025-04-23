package io.baji.stvh.service.impl;

import io.baji.stvh.entity.Menu;
import io.baji.stvh.mapper.MenuMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Transactional
@Service
public class MenuServiceImpl {

    @Resource
    private MenuMapper menuMapper;

    public List<Menu> getMenuTree() {
        return menuMapper.getMenuTree();
    }

}
