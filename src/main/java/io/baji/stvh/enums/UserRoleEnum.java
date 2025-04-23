package io.baji.stvh.enums;

public enum UserRoleEnum {

    ADMIN(100, "ADMIN"),
    STUDENT(200, "STUDENT"),
    TEACHER(300, "TEACHER");

    int id;
    String roleName;

    UserRoleEnum(int id, String roleName) {
        this.id = id;
        this.roleName = roleName;
    }

    public int getId() {
        return id;
    }

    public String getRoleName() {
        return roleName;
    }
}
