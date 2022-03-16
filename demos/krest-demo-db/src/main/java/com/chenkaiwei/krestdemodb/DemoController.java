package com.chenkaiwei.krestdemodb;

import com.chenkaiwei.krest.KrestUtil;
import com.chenkaiwei.krest.cryption.annotation.Cryption;
import com.chenkaiwei.krest.cryption.annotation.CryptionModle;
import com.chenkaiwei.krestdemodb.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class DemoController {
//public class DemoController implements ErrorController {
//

    /**
     * 登陆
     */
    @PostMapping("/login")
    public Map login(@RequestBody User userInput) throws Exception {

        log.info(userInput.toString());
        String username = userInput.getUsername();
        String password = userInput.getPassword();

        Assert.notNull(username, "username不能为空");
        Assert.notNull(password, "password不能为空");

        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(username, password);

//        try{
            KrestUtil.login(usernamePasswordToken);//全局捕获和trycatch都行
//        }catch (KrestPasswordLoginException e){
//            log.error("用户名或密码错误");
//
//            Map<String,String> res=new HashMap<>();
//            res.put("result","登录失败，用户名或密码错误");
//            return res;
//        }

        //只要登陆成功，对应jwtUser就会装载到框架里。
        //KrestUtil.getJwtUser();//如果要获取用户信息的话

        Map<String,String> res=new HashMap<>();
        res.put("token",KrestUtil.createJwtTokenOfCurrentUser());
        res.put("result","login success or other result message");
        return res;
    }

    @GetMapping("/whoami")
    public Map whoami(){


        Map<String,String> res=new HashMap<>();
        res.put("result","you are "+KrestUtil.getJwtUser().toString());
        res.put("token",KrestUtil.createNewJwtTokenIfNeeded());

        return res;
    }

    @GetMapping("/permissionDemo")
    @RequiresPermissions("listAllUsers")
    public Map permissionDemo(){

        Map<String,String> res=new HashMap<>();
        res.put("result","you have got the permission [listAllUsers]");
        res.put("token",KrestUtil.createNewJwtTokenIfNeeded());

        return res;
    }


//
//
//    /*---加解密注解演示部分 ↓ ---*/
//
//    @PostMapping("cryptBothTest")
//    @Cryption(CryptionModle.BOTH)//默认REQUEST
//    public User cryptApiTest(@RequestBody User inputUser) {
//        return inputUser;
//    }
//
//    @PostMapping("cryptionTest")
//    @Cryption(CryptionModle.WHOLE_REQUEST)//请求时加密（框架自动解密）
//    public Map cryptionTest(@RequestBody Map inputMap) {
//        Map result = new HashMap<>();
//        result.put("isEncrypted", true);
//        result.put("msgFromClient", inputMap);
//        return result;
//    }
//
//    @PostMapping("cryptJustOutTest")
//    @Cryption(CryptionModle.WHOLE_RESPONSE)//返回值加密
//    public User cryptJustOutTest(@RequestBody User inputUser) {
//        return inputUser;
//    }
//

}
