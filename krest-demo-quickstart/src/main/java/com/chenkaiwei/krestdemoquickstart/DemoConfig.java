package com.chenkaiwei.krestdemoquickstart;

import com.auth0.jwt.algorithms.Algorithm;
import com.chenkaiwei.krest.config.KrestConfigurer;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration//←勿漏
public class DemoConfig implements KrestConfigurer {

    @Override
    public Map<String, List<String>> createRolePermissionsMap() {
        Map<String, List<String>> res=new HashMap<String, List<String>>();
        res.put("admin", Arrays.asList("p1","p2","p3","p4"));
        res.put("user", Arrays.asList("p3","p4"));
        return res;
    }

    @Override
    public Algorithm createJwtAlgorithm() {
        return Algorithm.HMAC256("mydemosecretkey");
    }
}
