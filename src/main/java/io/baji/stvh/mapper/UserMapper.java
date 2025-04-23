package io.baji.stvh.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.baji.stvh.dto.PageInfo;
import io.baji.stvh.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface UserMapper extends BaseMapper<User> {

    @Select("select user.*\n" +
            "from user,\n" +
            "     user_role\n" +
            "where\n" +
            "    user.id = user_role.user_id\n" +
            "    and user_role.role_id = (select id from role where role_name = 'STUDENT')\n" +
            "    and user.grade = #{teacherGrade}\n" +
            "    and user.class_name = #{teacherClassName}\n" +
            "    and enabled = 1\n" +
            "order by  username")
    IPage<User> selectTeacherStudents(Page<User> page, @Param("teacherGrade") Integer teacherGrade, @Param("teacherClassName") String teacherClassName);

    @Select("select user.*\n" +
            "from user,\n" +
            "     user_role\n" +
            "where\n" +
            "    user.id = user_role.user_id\n" +
            "    and user_role.role_id = (select id from role where role_name = 'STUDENT')\n" +
            "    and user.grade = #{teacherGrade}\n" +
            "    and user.class_name = #{teacherClassName}\n" +
            "    and enabled = 1" +
            "    and (username like concat('%', #{keyword}, '%') or full_name like concat('%', #{keyword}, '%'))\n" +
            "order by  username")
    IPage<User> selectTeacherStudentsWithKeyWord(Page<User> page, @Param("teacherGrade") Integer teacherGrade, @Param("teacherClassName") String teacherClassName, @Param("keyword")String keyword);

}
