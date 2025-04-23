package io.baji.stvh.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@TableName("user_role")
public class UserRole {

    private Integer userId;
    private Integer roleId;
}
