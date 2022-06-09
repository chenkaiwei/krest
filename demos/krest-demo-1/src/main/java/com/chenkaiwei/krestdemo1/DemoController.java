package com.chenkaiwei.krestdemo1;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.chenkaiwei.krest.cryption.annotation.Cryption;
import com.chenkaiwei.krest.cryption.annotation.CryptionModle;
import com.chenkaiwei.krest.KrestUtil;
import com.chenkaiwei.krestdemo1.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.context.annotation.Bean;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class DemoController {

    /**
     * 登陆
     */
    @PostMapping("/login")
//    @CrossOrigin
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
    @RequiresPermissions("pd")
    public Map permissionDemo(){

        Map<String,String> res=new HashMap<>();
        res.put("result","you have got the permission [permissionDemo]");
        res.put("token",KrestUtil.createNewJwtTokenIfNeeded());

        return res;
    }





    /*---加解密注解演示部分 ↓ ---*/

    @PostMapping("cryptBothTest")
    @Cryption(CryptionModle.BOTH)//默认REQUEST
    public User cryptApiTest(@RequestBody User inputUser) {
        return inputUser;
    }

    @PostMapping("cryptionTest")
    @Cryption(CryptionModle.WHOLE_REQUEST)//请求时加密（框架自动解密）
    public Map cryptionTest(@RequestBody User inputUser) {
        Map result = new HashMap<>();
        result.put("isEncrypted", true);
        result.put("msgFromClient", inputUser);
        return result;
    }

    @PostMapping("cryptJustOutTest")
    @Cryption(CryptionModle.WHOLE_RESPONSE)//返回值整体加密
    public User cryptJustOutTest(@RequestBody User inputUser) {
        return inputUser;
    }

    @PostMapping("cryptionCustomize")
    @Cryption(CryptionModle.CUSTOMIZE)
    public Map cryptionCustomize(@RequestBody Map inputObj){

        String cryptionPart=(String)inputObj.get("cryptionPart");
        String decryptMessageBody=KrestUtil.decryptMessageBody(cryptionPart);
        //↑解密请求信息中被加密的部分

        Map result = new HashMap<>();
        result.put("cryptionPart", decryptMessageBody);
        result.put("nocryptionPart", inputObj.get("nocryptionPart"));
        result.put("whoyouare",KrestUtil.getJwtUser());
        result.put("token",KrestUtil.createNewJwtTokenIfNeeded());
        return result;
    }


}
