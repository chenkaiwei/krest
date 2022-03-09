package com.chenkaiwei.krestdemoquickstart;

import com.chenkaiwei.krest.KrestUtil;
import com.chenkaiwei.krest.entity.JwtUser;
import com.chenkaiwei.krest.exceptions.KrestAuthenticationException;
import com.chenkaiwei.krest.realms.JwtUtil;
import com.chenkaiwei.krestdemoquickstart.entity.User;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
public class DemoController {

    /**
     * 登陆
     */
    @PostMapping("/login")
    public Map login(@RequestBody User userInput) throws Exception {

        Map res=new HashMap();

        if(userInput.getUsername().equals("zhang3")&&userInput.getPassword().equals("12345")){//你自己的验证规则

            JwtUser jwtUser=new JwtUser("zhang3", Arrays.asList("admin"));

            res.put("token",KrestUtil.createJwtTokenByUser(jwtUser));
            res.put("message","login success");
        }else if(userInput.getUsername().equals("li4")&&userInput.getPassword().equals("66666")){

            JwtUser jwtUser=new JwtUser("li4", Arrays.asList("user"));
            res.put("token",KrestUtil.createJwtTokenByUser(jwtUser));
            res.put("message","login success");
        }else{
//            res.put("message","login failed");
            throw new KrestAuthenticationException("登录失败");

        }
        return res;
    }


    @GetMapping("/whoami")
    public Map whoami(){


        Map<String,String> res=new HashMap<>();
        res.put("result","you are "+KrestUtil.getJwtUser().toString());
        res.put("token",KrestUtil.getNewJwtTokenIfNeeded());

        return res;
    }

    @GetMapping("/permissionDemo")
    @RequiresPermissions("p1")
    public Map permissionDemo(){

        Map<String,String> res=new HashMap<>();
        res.put("result","you have got the permission [permissionDemo]");
        res.put("token",KrestUtil.getNewJwtTokenIfNeeded());

        return res;
    }
}
