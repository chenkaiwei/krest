package com.chenkaiwei.krestdemo1.service;

import com.chenkaiwei.krestdemo1.entity.User;

import java.util.*;

public interface UserService {

    Map<String, Collection<String>> getRolePermissionMap();
    User queryUserByName(String username);
}
