package io.baji.stvh.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.baji.stvh.dto.PageInfo;
import io.baji.stvh.entity.Role;
import io.baji.stvh.entity.User;
import io.baji.stvh.entity.UserRole;
import io.baji.stvh.mapper.RoleMapper;
import io.baji.stvh.mapper.UserMapper;
import io.baji.stvh.mapper.UserRoleMapper;
import io.baji.stvh.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserRoleMapper userRoleMapper;
    @Resource
    private RoleMapper roleMapper;

    @Override
    public User login(String username, String password) {

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        queryWrapper.eq("password", password);

        return userMapper.selectOne(queryWrapper);
    }

    public List<User> getUserRoles(List<User> users) {

        for (User user : users) {
            List<UserRole> userRoles = userRoleMapper.selectList(new QueryWrapper<UserRole>().eq("user_id", user.getId()));
            List<Integer> roles = userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toList());

            List<Role> roles1 = roleMapper.selectByIds(roles);
            user.setRoles(roles1);

            user.setRoleNames(roles1.stream().map(Role::getRoleName).collect(Collectors.toList()));
        }
        return users;

    }

    /**
     * 获取用户信息
     * @param token
     * @return
     */
    @Override
    public User getUserByToken(String token) {
        // 根据用户token查询单个用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("token", token);
        User user = userMapper.selectOne(queryWrapper);

        //
        List<UserRole> userRoles = userRoleMapper.selectList(new QueryWrapper<UserRole>().eq("user_id", user.getId()));
        List<Integer> roles = userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toList());

        List<Role> roles1 = roleMapper.selectByIds(roles);
        user.setRoles(roles1);

        user.setRoleNames(roles1.stream().map(Role::getRoleName).collect(Collectors.toList()));
        return user;
    }

    public IPage<User> getTeacherStudents(Integer teacherGrade, String teacherClassName, String keyword, PageInfo pageInfo) {
        Page<User> page = new Page<>(pageInfo.getCurrentPage(), pageInfo.getPageSize());
        if (StringUtils.isEmpty(keyword)) {
            return userMapper.selectTeacherStudents(page, teacherGrade, teacherClassName);
        }else {
            return userMapper.selectTeacherStudentsWithKeyWord(page, teacherGrade, teacherClassName, keyword);
        }
    }

}
