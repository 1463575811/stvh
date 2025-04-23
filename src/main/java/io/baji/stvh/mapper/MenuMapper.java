package io.baji.stvh.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.baji.stvh.entity.Menu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MenuMapper extends BaseMapper<Menu> {

    List<Menu> getMenuTree();


    List<Menu> getSubMenuList(@Param("id") Integer id);
}
