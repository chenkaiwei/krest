package com.chenkaiwei.krestdemo1.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;
import java.util.List;

/**
 * (User)实体类
 *
 * @author makejava
 * @since 2021-12-05 14:58:28
 */
@Data
@Builder
@AllArgsConstructor
public class User implements Serializable {
    private static final long serialVersionUID = -7820187121607483744L;

    private String id;

    private String username;

    private String password;

    private String salt;

    private List<String> roles;//用一对多映射查询，联u,u-r,r三表，集合里只放role的name

    @Tolerate//@Data@Builder共用时会失去无参构造器，故手动补上，且让lombok不感知。
    public User() {
    }

}