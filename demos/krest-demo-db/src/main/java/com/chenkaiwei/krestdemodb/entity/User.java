package com.chenkaiwei.krestdemodb.entity;

import java.io.Serializable;
import java.util.List;

/**
 * (User)实体类
 *
 * @author makejava
 * @since 2022-03-17 05:24:47
 */
public class User implements Serializable {
    private static final long serialVersionUID = 204458155487303744L;
    
    private String id;
    
    private String username;
    /**
    * md5加盐两次散列
    */
    private String password;
    
    private String salt;

    private List<String> roles;//用一对多映射查询，联u,u-r,r三表，集合里只放role的name


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}