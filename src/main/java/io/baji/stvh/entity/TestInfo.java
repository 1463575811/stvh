package io.baji.stvh.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName(value = "test_info")
public class TestInfo {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String testName;

    private LocalDateTime testTime;

}
