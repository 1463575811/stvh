package io.baji.stvh.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StudentScoreDto {

    private Integer id;
    private Integer userId;
    private String username;
    private String userFullName;
    private Integer testInfoId;
    private String testName;
    private LocalDateTime testTime;
    private Integer chinese;
    private Integer math;
    private Integer english;
    private Integer politics;
    private Integer sport;
}
