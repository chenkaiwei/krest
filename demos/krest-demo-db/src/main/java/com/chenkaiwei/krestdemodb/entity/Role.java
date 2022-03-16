package com.chenkaiwei.krestdemodb.entity;

import java.io.Serializable;
import java.util.List;

/**
 * (Role)实体类
 *
 * @author makejava
 * @since 2022-03-17 06:02:54
 */
public class Role implements Serializable {
    private static final long serialVersionUID = -97997327952099240L;
    
    private Long id;
    
    private String name;
    
    private String desc_;

    private List<String> permissions;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc_() {
        return desc_;
    }

    public void setDesc_(String desc_) {
        this.desc_ = desc_;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
}