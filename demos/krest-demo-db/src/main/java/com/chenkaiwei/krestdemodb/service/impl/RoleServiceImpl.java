package com.chenkaiwei.krestdemodb.service.impl;

import com.chenkaiwei.krestdemodb.entity.Role;
import com.chenkaiwei.krestdemodb.dao.RoleDao;
import com.chenkaiwei.krestdemodb.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.*;

/**
 * (Role)表服务实现类
 *
 * @author makejava
 * @since 2022-03-17 06:02:54
 */
@Service("roleService")
public class RoleServiceImpl implements RoleService {
    @Resource
    private RoleDao roleDao;

    public Map<String, Collection<String>> listAllRoelsWithPermissionsFromDB() {
        List<Role> allRolesListFromDB = roleDao.listAllRoelsWithPermissions();

        Assert.notEmpty(allRolesListFromDB, "无法从数据库中获取role和permission的映射关系");

        // 将数据库中的原始格式转换成map形式，方便使用。key：role的name，value：Permission name的集合
        Map<String, Collection<String>> allRolesPermissionMap = new HashMap<>();
        for (Role role :
                allRolesListFromDB) {
            allRolesPermissionMap.put(role.getName(),role.getPermissions());
        }

        return allRolesPermissionMap;
    }

}