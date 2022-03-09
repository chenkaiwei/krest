package com.chenkaiwei.krest.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KrestUsernamePasswordAuthenticationInfo {

    private JwtUser jwtUser;
    private String passwordInDB;
    private String salt;
}
