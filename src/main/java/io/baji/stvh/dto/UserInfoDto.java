package io.baji.stvh.dto;

import lombok.Data;

@Data
public class UserInfoDto {
    private String username;
    private String fullName;
    private String sex;
    private String birthDate;
    private String grade;
    private String className;
    private String roleName;

    public void setRoleName(String roleName) {
        switch (roleName) {
            case "ADMIN":{
                this.roleName = "管理员";
                break;
            }
            case "TEACHER":{
                this.roleName = "教师";
                break;
            }
            case "STUDENT":{
                this.roleName = "学生";
                break;
            }
        }
    }

    public void setGrade(Integer grade) {
        switch (grade) {
            case 1: {
                this.grade = "一年级";
                break;
            }
            case 2: {
                this.grade = "二年级";
                break;
            }
            case 3: {
                this.grade = "三年级";
                break;
            }
            case 4: {
                this.grade = "四年级";
                break;
            }
            case 5: {
                this.grade = "五年级";
                break;
            }
            case 6: {
                this.grade = "六年级";
                break;
            }
        }
    }
}
