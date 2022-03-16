package com.chenkaiwei.krestdemo1.service.impl;

import com.chenkaiwei.krestdemo1.entity.User;
import com.chenkaiwei.krestdemo1.service.FakeDatabaseData;
import com.chenkaiwei.krestdemo1.service.UserService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service//用法再吃透点
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    FakeDatabaseData fakeDatabaseData;

    @SneakyThrows
    public Map<String, Collection<String>> getRolePermissionMap() {
        log.info("getRolePermissionMap");

        Thread.sleep(1000);//假冒访问数据库的延迟

        Map<String, Collection<String>> res=new HashMap<String, Collection<String>>();
        res.put("user", Arrays.asList("p1","p2","p3"));
        res.put("admin", Arrays.asList("p1","p2","p3","p4","p5","user:delete","pd"));
        return res;
    }

    //模拟获取对应用户，供realm验证
    public User queryUserByName(String username) {
        User res = null;
        for (User u :
                fakeDatabaseData.getAllUserList() ) {
            if (u.getUsername().equals(username)){
                res=u;
                break;
            }
        }
        Assert.notNull(res,"用户不存在");
        return res;
    }
}
