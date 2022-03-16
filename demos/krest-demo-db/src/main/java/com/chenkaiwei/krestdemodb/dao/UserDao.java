package com.chenkaiwei.krestdemodb.dao;

import com.chenkaiwei.krestdemodb.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * (User)表数据库访问层
 *
 * @author makejava
 * @since 2022-03-17 05:24:53
 */
@Mapper
public interface UserDao {

    User queryUserByName(String username);

    List<User> listAllUsers();//懒加载，默认不加载roles

    int addUser(User user);

}