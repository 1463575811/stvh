package io.baji.stvh.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.baji.stvh.handler.MetaTypeHandler;
import lombok.Data;

import java.util.List;

@Data
@TableName("menu")
public class Menu {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String name;
    private String redirect;
    private String path;
    private String component;
    private Integer parentId;

    @TableField(typeHandler = MetaTypeHandler.class)
    private Meta meta;

    @TableField(exist = false)
    private List<Menu> children;

}

