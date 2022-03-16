package com.chenkaiwei.krestdemodb.service;

import java.util.Collection;
import java.util.Map;

/**
 * (Role)表服务接口
 *
 * @author makejava
 * @since 2022-03-17 06:02:54
 */
public interface RoleService {
    Map<String, Collection<String>> listAllRoelsWithPermissionsFromDB();
}