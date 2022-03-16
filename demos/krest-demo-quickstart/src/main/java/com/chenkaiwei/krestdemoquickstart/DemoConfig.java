package com.chenkaiwei.krestdemoquickstart;

import com.auth0.jwt.algorithms.Algorithm;
import com.chenkaiwei.krest.config.KrestConfigurer;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration//←勿漏
public class DemoConfig implements KrestConfigurer {

    @Override
    public Map<String, Collection<String>> configRolePermissionsMap() {
        Map<String, Collection<String>> res=new HashMap<String, Collection<String>>();
        res.put("admin", Arrays.asList("p1","p2","p3","p4"));
        res.put("user", Arrays.asList("p3","p4"));
        return res;
    }

    @Override
    public Algorithm configJwtAlgorithm() {
        return Algorithm.HMAC256("mydemosecretkey");
    }
}
