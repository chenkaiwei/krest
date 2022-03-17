package com.chenkaiwei.krest.cryption;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.AsymmetricCrypto;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.chenkaiwei.krest.KrestUtil;
import com.chenkaiwei.krest.config.KrestConfigurer;
import com.chenkaiwei.krest.cryption.annotation.Cryption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "krest.cryption",value = "enable-cryption",matchIfMissing = false)
public class CryptionInterceptor implements HandlerInterceptor {

    //本类功能：含@Cryption注解时，则解密出头信息Cryption中的临时秘钥装载在attribute中供后续使用。

    @Autowired
    KrestConfigurer krestConfiguration;

    @Autowired(required=false)
    AsymmetricCrypto tempSecretKeyCryptoAlgorithm;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //当目标方法被Cryption修饰时，解密出临时秘钥，存入request.attributes。

        Method method=((HandlerMethod)handler).getMethod();
        if(method.isAnnotationPresent(Cryption.class)){
            String cryptionString=(String)request.getHeader("cryption");

            log.debug("cryption in header:"+cryptionString);
            //标注为Cryption必须要求客户端提供cryption头属性。
            Assert.notNull(cryptionString, "缺少cryption头信息");
            Assert.notNull(tempSecretKeyCryptoAlgorithm,"您尚未实现krestConfigurer.configTempSecretKeyCryptoAlgorithm方法");

//        String tempAesKey = RSAUtils.privateDecrypt(cryptionString, privateKey);
            //改用hutool加密解密：https://www.hutool.cn/docs/#/crypto/%E9%9D%9E%E5%AF%B9%E7%A7%B0%E5%8A%A0%E5%AF%86-AsymmetricCrypto

            byte[] encryptBytes = Base64.decode(cryptionString);

            byte[] decrypt = this.tempSecretKeyCryptoAlgorithm.decrypt(encryptBytes, KeyType.PrivateKey);//解密
            String tempSecretKey = StrUtil.str(decrypt, CharsetUtil.CHARSET_UTF_8);
            log.info("tempSecretKey（应为128bitsSecretkey）:" + tempSecretKey);//
//            //对key的要求：JDK默认只支持不大于128bits 的密钥，而128bits已能够满足商用需求，在此使用128bits长度。
//            //实现：key为(16位)128bits，自己定义，保证各端一致
//
//            WebDelegatingSubject wdSubject=(WebDelegatingSubject)KrestUtil.getSubject();
//            wdSubject.getServletRequest().setAttribute("com.chenkaiwei.krest.TEMP_SECRET_KEY",tempSecretKey);
////            wdSubject.getSession().setAttribute("jwtToken",bearerToken.getToken());
//
//            Object obj=((WebSubject) KrestUtil.getSubject()).getServletRequest();
//
//            request.getServletContext().setAttribute("com.chenkaiwei.krest.TEMP_SECRET_KEY",tempSecretKey);
//
//            log.debug(KrestUtil.getTempSecretKey());

            //↓ 存入request.attribute，供其他CryptionModel共享。
            request.setAttribute("com.chenkaiwei.krest.TEMP_SECRET_KEY",tempSecretKey);

            //消息体加解密策略也生成一次存着备用，起码在一次请求内可以复用。
            SymmetricCrypto messageBodyCryptoAlgorithm= krestConfiguration.createMessageBodyCryptoAlgorithm(tempSecretKey);
            Assert.notNull(messageBodyCryptoAlgorithm,"您尚未实现krestConfiguration.createMessageBodyCryptoAlgorithm接口");
            request.setAttribute("com.chenkaiwei.krest.messageBodyCryptoAlgorithm",messageBodyCryptoAlgorithm);


            log.debug("setTempSecretKey:"+KrestUtil.getTempSecretKey());
        }

        return true;
    }
}
