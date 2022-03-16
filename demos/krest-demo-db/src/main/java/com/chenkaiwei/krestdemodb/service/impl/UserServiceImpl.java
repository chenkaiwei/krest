package com.chenkaiwei.krestdemodb.service.impl;

import com.chenkaiwei.krestdemodb.entity.User;
import com.chenkaiwei.krestdemodb.dao.UserDao;
import com.chenkaiwei.krestdemodb.service.UserService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * (User)表服务实现类
 *
 * @author makejava
 * @since 2022-03-17 05:24:57
 */
@Service("userService")
public class UserServiceImpl implements UserService {
    //https://blog.csdn.net/he90227/article/details/52981747
    //具体实现和接口分开，一个好处是便于使用Profile来区分场景，自动装载不同的Service实现类

    @Resource
    private UserDao userDao;

    @Override
    public List<User> listAllUsers() {
        return userDao.listAllUsers();
    }

    @Override
    public User queryUserByName(String username) {
        return userDao.queryUserByName(username);
    }

    @Override
    public boolean addUser(String username, String password) {

        //生成盐、加工密码
        String salt;//TODO

        new User();


        return false;
    }

    @Override
    public boolean register(User userInput) {
//
//        String newId= BaseUtil.generateUUIDString();
//        String salt = RandomStringUtils.randomGraph(24);//不带空格
//        String passwordInDB= FrameworkUtil.createPasswordInDB(salt,userInput.getPassword());
//
//        User newUser=User.builder()
//                .id(newId)
//                .username(userInput.getUsername())
//                .password(passwordInDB)
//                .salt(salt)
//                .build();
//        try{
//            userDao.addUser(newUser);
//        }catch (DuplicateKeyException e){
////            log.error(e.getLocalizedMessage());
//            throw new ApiResponseException(ApiStatus.DATA_ACCESS_ERROR,"用户名已存在");
//        }

        return true;
    }


}