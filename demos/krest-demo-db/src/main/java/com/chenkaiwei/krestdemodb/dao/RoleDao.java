package com.chenkaiwei.krestdemodb.dao;

import com.chenkaiwei.krestdemodb.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * (Role)表数据库访问层
 *
 * @author makejava
 * @since 2022-03-17 06:02:54
 */
@Mapper
public interface RoleDao {

    List<Role> listAllRoelsWithPermissions();

    List<String> getPermissionsByRoles();

}