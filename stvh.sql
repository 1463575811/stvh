
drop database if exists stvh;

create database stvh DEFAULT CHARACTER SET utf8;

use stvh;

drop table if exists user;

create table user(
                     id INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户id, 可以是学号/教师编号/管理员账号',
                     username varchar(100) not null comment '用户名',
                     password varchar(255) not null comment '密码',
                     full_name varchar(255) not null comment '姓名',
                     sex int default 0 comment '性别, 1为男 2为女, 若没有填写默认为0, 前端不显示',
                     tel varchar(100) comment '联系方式',
                     birth_date date comment '出生日期',
                     grade tinyint comment '年级',
                     class_name varchar(100) comment '班级',
                     token varchar(255) UNIQUE not null comment '用户token',
                     avatar varchar(255) default 'https://img2.baidu.com/it/u=1954199631,1815021844&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500',
                     enabled bool default true comment '是否允许登录'

) default charset='utf8' comment '用户表';
insert into user(id, username, password,full_name,sex, tel, birth_date,grade, class_name, token) values
                                                                                               (999,'test','123456','管理员小1', 1, '13511112222','1999-03-05', 0, '', '111222333'),
                                                                                               (111, 'teststu', '123456','张三', 1, '13511112223','1998-03-01', 1, '一班', '222222222'),
                                                                                               (222, 'testtea', '123456','数学王子秦老师', 1, '13511112223','1981-11-22', 1, '一班', '3333333');

drop table if exists role;
create table role(
                     id INT PRIMARY KEY AUTO_INCREMENT COMMENT '角色id',
                     role_name varchar(100) not null comment '角色名'

)default charset='utf8' comment '用户权限表';

insert into role(id, role_name) values (100,'ADMIN'),
                                       (200,'STUDENT'),
                                       (300,'TEACHER');

drop table if exists test_info;
create table test_info(
                     id INT PRIMARY KEY AUTO_INCREMENT COMMENT '考试信息id',
                     test_name varchar(100) unique not null comment '考试名称',
                     test_time date not null comment '考试时间'

)default charset='utf8' comment '考试信息表';

insert into test_info(id, test_name, test_time) values (1, '2024级上班学期周考1', '2024/4/1'),
                                                       (2, '2024级上班学期周考2', '2024/4/8'),
                                                       (3, '2024级上班学期月考1', '2024/4/15');


drop table if exists score;
create table score(
                          id INT PRIMARY KEY AUTO_INCREMENT COMMENT '成绩信息id',
                          user_id INT not null comment '用户id',
                          test_info_id int not null comment '考试信息id',
                          chinese tinyint comment '语文',
                          math tinyint comment '数学',
                          english tinyint comment '英语',
                          politics tinyint comment '政治',
                          sport tinyint comment '体育'
)default charset='utf8' comment '考试成绩表';

# insert into score(user_id, test_info_id, chinese, math, english, politics, sport) values
#                                                    (1000, 1, 95, 94, 93, 95, 92),
#                                                    (1000, 2, 95, 94, 93, 95, 94),
#                                                    (1000, 3, 91, 44, 93, 95, 93),
#                                                    (1001, 1, 95, 24, 93, 95, 96),
#                                                    (1001, 2, 92, 95, 94, 94, 93),
#                                                    (1001, 3, 91, 91, 97, 96, 92),
#                                                    (1003, 1, 95, 94, 95, 95, 97),
#                                                    (1003, 1, 95, 94, 93, 95, 99),
#                                                    (1003, 1, 95, 94, 93, 95, 91);



drop table if exists user_role;

create table user_role(
                          user_id int comment '用户id',
                          role_id int comment '角色id',
                          key (user_id, role_id)

)default charset='utf8' comment '用户角色关联表';

insert into user_role values (999, 100),
                             (111, 200),
                             (222, 300);

drop table if exists menu;
CREATE TABLE menu(
                     id INT PRIMARY KEY AUTO_INCREMENT COMMENT '菜单编号,主键自增',
                     name VARCHAR(100) DEFAULT NULL COMMENT '组件名称',
                     redirect VARCHAR(100) DEFAULT NULL COMMENT '重定向地址',
                     path VARCHAR(255) DEFAULT NULL COMMENT '组件地址',
                     component VARCHAR(255) DEFAULT NULL COMMENT '组件对象',
                     meta json DEFAULT NULL COMMENT '路由的基础配置',
                     parent_id INT DEFAULT 0 COMMENT '父ID,关联菜单主键,默认一级菜单的值为0'
)DEFAULT CHARSET='UTF8' COMMENT='菜单表';

insert into menu values
                        (4, 'Admin', '/admin/sysUser', '/admin', 'Layout', '{"title": "用户管理","icon": "el-icon-s-promotion","roles": ["ADMIN"]}', 0),
                        (5, 'SysUser', '', 'sysUser', 'admin/SysUser', '{"title": "系统用户管理","roles": ["ADMIN"]}', 4),
                        (6, 'Teacher', '/teacher/sysTeacher', '/teacher', 'Layout', '{"title": "学生管理","icon": "el-icon-s-promotion","roles": ["TEACHER", "ADMIN"]}', 0),
                        (7, 'SysTeacher', '', 'sysTeacher', 'teacher/SysTeacher', '{"title": "系统学生信息管理","roles": ["TEACHER"]}', 6),
                        (8, 'TestInfo', '', 'testInfo', 'teacher/TestInfo', '{"title": "考试信息管理","roles": ["ADMIN"]}', 6),
                        (9, 'Student', '/student/sysStudent', '/student', 'Layout', '{"title": "成绩信息","icon": "el-icon-s-promotion","roles": ["STUDENT"]}', 0),
                        (10, 'SysStudent', '', 'sysStudent', 'student/SysStudent', '{"title": "学生成绩信息查询","roles": ["STUDENT"]}', 9);



