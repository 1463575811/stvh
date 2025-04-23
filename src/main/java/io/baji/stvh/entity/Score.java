package io.baji.stvh.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName(value = "score")
public class Score {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Integer testInfoId;
    private Integer chinese;
    private Integer math;
    private Integer english;
    private Integer politics;
    private Integer sport;

}
