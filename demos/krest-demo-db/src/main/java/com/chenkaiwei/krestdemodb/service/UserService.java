package com.chenkaiwei.krestdemodb.service;

import com.chenkaiwei.krestdemodb.entity.User;
import java.util.List;
/**
 * (User)表服务接口
 *
 * @author makejava
 * @since 2021-12-05 14:58:28
 */
public interface UserService {

    List<User> listAllUsers();

    User queryUserByName(String username);

    boolean addUser(String username,String password);

    boolean register(User userInput);
}