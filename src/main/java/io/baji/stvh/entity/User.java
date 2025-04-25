package io.baji.stvh.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 映射数据库表的实体类
 */
@Data
@TableName(value = "user")
public class User {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String username;

    private String password;

    private String fullName;

    private Integer sex;

    private String tel;

    private LocalDateTime birthDate;

    private Integer grade;

    private String className;

    private String token;

    private String avatar;

    private Boolean enabled;

    @TableField(exist = false)
    private List<Role> roles;

    @TableField(exist = false)
    private List<String> roleNames;

}
