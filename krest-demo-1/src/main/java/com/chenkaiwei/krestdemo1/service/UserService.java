package com.chenkaiwei.krestdemo1.service;

import com.chenkaiwei.krestdemo1.entity.User;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;

public interface UserService {

    Map<String, List<String>> getRolePermissionMap();
    User queryUserByName(String username);
}
