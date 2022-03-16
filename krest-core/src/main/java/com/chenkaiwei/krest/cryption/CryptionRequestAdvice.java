package com.chenkaiwei.krest.cryption;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.symmetric.AES;
import com.chenkaiwei.krest.KrestUtil;
import com.chenkaiwei.krest.cryption.annotation.Cryption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

@Slf4j
@RestControllerAdvice
public class CryptionRequestAdvice extends RequestBodyAdviceAdapter {
    //
//////    //    @Value("${cryption.rsa.public-key}")
//    private String publicKey="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCXaK-7dD7xOfdBQK6_ob_6arbJNUfol2hV9LqVCfF5Jh6zFOqqWTNu6GczDputq_w8y01jSmWpfkB_fOzl0I9-U54a28C8Zeez2esH1f4YIB5TILZ6f-8mCQD4EtplTdBOVSJz5rjHBEuffKQftZb51CdPt_Op8vxUvfiFCUTAwQIDAQAB";
////
////    @Value("${cryption.rsa.private-key}")//单纯跑test没用，不加载application.yml
////    private String privateKey;
//    private String privateKey="MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJdor7t0PvE590FArr-hv_pqtsk1R-iXaFX0upUJ8XkmHrMU6qpZM27oZzMOm62r_DzLTWNKZal-QH987OXQj35TnhrbwLxl57PZ6wfV_hggHlMgtnp_7yYJAPgS2mVN0E5VInPmuMcES598pB-1lvnUJ0-386ny_FS9-IUJRMDBAgMBAAECgYEAlmR00aT49FFYiOc_7Lc04v9myltzLtRd3at2PZ4fze-QZN9s7IIn9Y1BHNTwy8ReiuCB4RNAAeiXFks3YFsWe5yHHsW_Y3ntN0Tla_nkVkjm2iG_dIKHS5iY3ERoheR8i0d0T1BnmwbyCwdl7-QWmjVdeZ8YPFxAQ72Wr6DLY6UCQQDwgAGW8rdxbKQjqSIoFTRfSTLfI6Ba3dsb7xNiQE3RSiq_k4LskbnNCAqf7WNy85gNjENX-W8lmP1t6rJqC5tvAkEAoSrFs6HpF2I469ALkZH6iapi7k97W4nlnnOeaNAx9uuXy9hyQiKSGZafSidxPvmbV1qV2CVxc53FhTCD5b4OzwJAK4LtRrMZD1NZiv4hqODVPdwPcSGP9ICpEK-7cQ4zRgdGHq0Ahe6DkB3BVlfrozOBMgpLcNI3ErVQPJ-2scrxzwJAWybfzisCtBD_dI-kG17evkG51mLpt-oUDjwCGfG2cJrqrYXriXAYBZTk3oHUUPPHYe5_1VHICsXu0tePob6OjQJAeZXbdfkNx7-uZ295rTj3Yq3H11uB6hB317eODHtnnCMVH0ww50C9pGnRPO2dEaShCwLUeOucxBim_usmIBaPOw";


    public CryptionRequestAdvice() {
        super();
        log.debug("CryptionRequestAdvice init");
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
//策略：带有Cryption注解且为model为in时会被本Advice支持，本方法拿不到请求头信息，放在beforeBodyRead里判断
        boolean res = false;

        if (methodParameter.getExecutable().isAnnotationPresent(Cryption.class)) {

            Cryption cryption = methodParameter.getExecutable().getAnnotation(Cryption.class);
            res = cryption.value().isWholeRequest();//仅须request全文解密时，执行本组件。
        }
        log.info("is supports:" + res);
        return res;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
//        这个方法在converter.read之前执行，用来解密是天作之合（详见AbstractMessageConverterMethodArgumentResolver.readWithMessageConverters源码）

        HttpInputMessage resultInputMessage;
//        String cryptionString;
//
//
//        List cl = inputMessage.getHeaders().get("cryption");
//        Assert.notNull(cl, "缺少cryption头信息");
//        Assert.notEmpty(cl, "缺少cryption头信息");
//
//        cryptionString = (String) cl.get(0);
//
////        String tempSecretKey = RSAUtils.privateDecrypt(cryptionString, privateKey);
//        //改用hutool加密解密：https://www.hutool.cn/docs/#/crypto/%E9%9D%9E%E5%AF%B9%E7%A7%B0%E5%8A%A0%E5%AF%86-AsymmetricCrypto
//        RSA rsa = new RSA(privateKey, null);
////        byte[] encrypt = StrUtil.bytes(cryptionString, CharsetUtil.CHARSET_UTF_8);//先转成二进制数组
//        byte[] encryptBytes = Base64.decode(cryptionString);
//
//        byte[] decrypt = rsa.decrypt(encryptBytes, KeyType.PrivateKey);//解密
//        String tempSecretKey = StrUtil.str(decrypt, CharsetUtil.CHARSET_UTF_8);
//        log.info("tempSecretKey（应为128bitsSecretkey）:" + tempSecretKey);//
//        //对key的要求：JDK默认只支持不大于128bits 的密钥，而128bits已能够满足商用需求，在此使用128bits长度。
//        //实现：key为(16位)128bits，自己定义，保证各端一致

        String tempSecretKey =KrestUtil.getTempSecretKey();

        //来自客户端的加密后消息体
        byte[] bytes = new byte[inputMessage.getBody().available()];
        inputMessage.getBody().read(bytes);
        String encryptedMessageBody = new String(bytes);
        log.info("encryptedMessageBody:" + encryptedMessageBody);

        //用tempAesKey解密消息体：
        // 解密
        String decryptedMessageBody = KrestUtil.decryptMessageBody(encryptedMessageBody);
        log.info("decrypted message body:" + decryptedMessageBody);


        // 使用解密后的数据，构造新的读取流
        InputStream rawInputStream = new ByteArrayInputStream(decryptedMessageBody.getBytes());

        resultInputMessage = new HttpInputMessage() {
            @Override
            public HttpHeaders getHeaders() {
                return inputMessage.getHeaders();
            }

            @Override
            public InputStream getBody() throws IOException {
                return rawInputStream;
            }
        };

        return super.beforeBodyRead(resultInputMessage, parameter, targetType, converterType);

    }

//    @Test
//    public void RSAEncryptTempAesKey(){
//        RSA rsa = new RSA(privateKey, publicKey);
//        String tempAesKey="128bitsSecretkey";
//        byte[] encrypt = rsa.encrypt(StrUtil.bytes(tempAesKey, CharsetUtil.CHARSET_UTF_8), KeyType.PublicKey);
//
//        String encryptedTempAesKey=Base64.encode(encrypt);
//        log.info(encryptedTempAesKey);
//    }
//
//    @Test
//    public void RSADecryptTest(){
//        String PRIVATE_KEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAIL7pbQ+5KKGYRhw7jE31hmA"
//                + "f8Q60ybd+xZuRmuO5kOFBRqXGxKTQ9TfQI+aMW+0lw/kibKzaD/EKV91107xE384qOy6IcuBfaR5lv39OcoqNZ"
//                + "5l+Dah5ABGnVkBP9fKOFhPgghBknTRo0/rZFGI6Q1UHXb+4atP++LNFlDymJcPAgMBAAECgYBammGb1alndta"
//                + "xBmTtLLdveoBmp14p04D8mhkiC33iFKBcLUvvxGg2Vpuc+cbagyu/NZG+R/WDrlgEDUp6861M5BeFN0L9O4hz"
//                + "GAEn8xyTE96f8sh4VlRmBOvVdwZqRO+ilkOM96+KL88A9RKdp8V2tna7TM6oI3LHDyf/JBoXaQJBAMcVN7fKlYP"
//                + "Skzfh/yZzW2fmC0ZNg/qaW8Oa/wfDxlWjgnS0p/EKWZ8BxjR/d199L3i/KMaGdfpaWbYZLvYENqUCQQCobjsuCW"
//                + "nlZhcWajjzpsSuy8/bICVEpUax1fUZ58Mq69CQXfaZemD9Ar4omzuEAAs2/uee3kt3AvCBaeq05NyjAkBme8SwB0iK"
//                + "kLcaeGuJlq7CQIkjSrobIqUEf+CzVZPe+AorG+isS+Cw2w/2bHu+G0p5xSYvdH59P0+ZT0N+f9LFAkA6v3Ae56OrI"
//                + "wfMhrJksfeKbIaMjNLS9b8JynIaXg9iCiyOHmgkMl5gAbPoH/ULXqSKwzBw5mJ2GW1gBlyaSfV3AkA/RJC+adIjsRGg"
//                + "JOkiRjSmPpGv3FOhl9fsBPjupZBEIuoMWOC8GXK/73DHxwmfNmN7C9+sIi4RBcjEeQ5F5FHZ";
//
//        RSA rsa = new RSA(PRIVATE_KEY, null);
//
//        String a = "2707F9FD4288CEF302C972058712F24A5F3EC62C5A14AD2FC59DAB93503AA0FA17113A020EE4EA35EB53F"
//                + "75F36564BA1DABAA20F3B90FD39315C30E68FE8A1803B36C29029B23EB612C06ACF3A34BE815074F5EB5AA3A"
//                + "C0C8832EC42DA725B4E1C38EF4EA1B85904F8B10B2D62EA782B813229F9090E6F7394E42E6F44494BB8";
//
//        byte[] aByte = HexUtil.decodeHex(a);
//        byte[] decrypt = rsa.decrypt(aByte, KeyType.PrivateKey);
//
//        log.info(StrUtil.str(decrypt, CharsetUtil.CHARSET_UTF_8));
//
//    }
//
//    @Test
//    public void AESDecryptTest() {
//
//        AES aes = new AES("CBC", "PKCS5Padding",
//                // 密钥，可以自定义
//                "128bitsSecretkey".getBytes(),
//                // iv加盐，按照实际需求添加
//                "1111222233334444".getBytes());
//
//// 加密为16进制表示
//        String encryptStr = aes.encryptBase64("wy123456");
//        log.info(encryptStr);
//// 解密
//        String decryptStr = aes.decryptStr(encryptStr);
//
//        log.info(decryptStr);
//    }

}
