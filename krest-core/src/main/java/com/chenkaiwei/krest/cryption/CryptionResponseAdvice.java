package com.chenkaiwei.krest.cryption;

import com.alibaba.fastjson.JSON;
import com.chenkaiwei.krest.KrestUtil;
import com.chenkaiwei.krest.cryption.annotation.Cryption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
@ConditionalOnProperty(prefix = "krest.cryption",value = "enable-cryption",matchIfMissing = false)
//@Order(1)//先加密msg再封装，本类优先调用
public class CryptionResponseAdvice implements ResponseBodyAdvice<Object> {

//    @Value("${cryption.rsa.private-key}")//rsa秘钥，用来解密客户端传来的临时AES Key
//    private String privateKey;

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> converterType) {
        boolean res = false;

        if (methodParameter.getExecutable().isAnnotationPresent(Cryption.class)) {

            Cryption cryption = methodParameter.getExecutable().getAnnotation(Cryption.class);
            res = cryption.value().isWholeResponse();
        }
        log.debug("is supports:" + res);
        return res;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
       log.debug("beforeBodyWrite");

//        List cl = request.getHeaders().get("cryption");
//        Assert.notNull(cl, "缺少cryption头信息");
//        Assert.notEmpty(cl, "缺少cryption头信息");
//
//        String cryptionString = (String) cl.get(0);
//        String tempSecretKey = RSAUtils.privateDecrypt(cryptionString, privateKey);
//        log.info("tempSecretKey:" + tempSecretKey);


//        String encryptedResponseMsg= AESUtils.encrypt(tempSecretKey, JSON.toJSONString(body,true));


        String encryptedResponseMsg = KrestUtil.encryptMessageBody( JSON.toJSONString(body,true));
        log.info("encrypted response message :" + encryptedResponseMsg);

        String decryptTest= KrestUtil.decryptMessageBody(encryptedResponseMsg);
        log.info(decryptTest);

        return encryptedResponseMsg;
    }
}
