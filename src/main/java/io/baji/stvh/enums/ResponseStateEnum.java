package io.baji.stvh.enums;

public enum ResponseStateEnum {
    // 成功
    SUCCESS(200, "操作成功"),
    // 登录
    NEED_LOGIN(401, "需要登录后操作"),
    NO_OPERATOR_AUTH(403, "无权限操作"),
    NOT_ALLOW_LOGIN(405, "该账号冻结, 无法登录"),
    SYSTEM_ERROR(500, "出现错误"),
    USERNAME_EXIST(501, "用户名已存在"),
    PHONENUMBER_EXIST(502, "手机号已存在"), EMAIL_EXIST(503, "邮箱已存在"),
    REQUIRE_USERNAME(504, "必需填写用户名"),
    CONTENT_NOT_NULL(506, "评论内容不能为空"),
    FILE_TYPE_ERROR(507, "文件类型错误"),
    USERNAME_NOT_NULL(508, "用户名不能为空"),
    NICKNAME_NOT_NULL(509, "昵称不能为空"),
    PASSWORD_NOT_NULL(510, "密码不能为空"),
    EMAIL_NOT_NULL(511, "邮箱不能为空"),
    NICKNAME_EXIST(512, "昵称已存在"),
    LOGIN_ERROR(505, "用户名或密码错误"),
    ADD_USER_EXCEL_ERROR(513, "上传文件类型错误"),
    ADD_USER_EXCEL_ERROR2(514, "用户学号重复!"),
    ADD_TEST_INFO_DUPLICATE_ERROR(516, "考试名称重复!"),
    ADD_STUDENT_SCORE_ERROR1(517, "要添加成绩的学生信息不存在!"),
    ADD_STUDENT_SCORE_ERROR2(517, "要添加成绩的学生已经有考试记录!"),
    ADD_USER_EXCEL_ERROR3(515, "用户年级信息错误!");

    int code;
    String msg;

    ResponseStateEnum(int code, String errorMessage) {
        this.code = code;
        this.msg = errorMessage;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
