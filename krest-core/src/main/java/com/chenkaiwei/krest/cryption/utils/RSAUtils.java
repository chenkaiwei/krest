package com.chenkaiwei.krest.cryption.utils;//package com.chenkaiwei.krest.cryption.utils;
//
//import lombok.SneakyThrows;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.codec.binary.Base64;
//import org.apache.commons.io.IOUtils;
//import org.junit.Test;
////import org.junit.jupiter.api.Test;
//
//import javax.crypto.Cipher;
//import java.io.ByteArrayOutputStream;
//import java.security.*;
//import java.security.interfaces.RSAPrivateKey;
//import java.security.interfaces.RSAPublicKey;
//import java.security.spec.InvalidKeySpecException;
//import java.security.spec.PKCS8EncodedKeySpec;
//import java.security.spec.X509EncodedKeySpec;
//import java.util.HashMap;
//import java.util.Map;
//
////来自 https://blog.csdn.net/cz0217/article/details/78426733
////RSA相关知识 https://blog.csdn.net/u013758702/article/details/112510747
//@Slf4j
//public class RSAUtils {
//
//    public static final String CHARSET = "UTF-8";
//    public static final String RSA_ALGORITHM = "RSA";
//
//
//    public static Map<String, String> createKeys(int keySize){
//        //为RSA算法创建一个KeyPairGenerator对象
//        KeyPairGenerator kpg;
//        try{
//            kpg = KeyPairGenerator.getInstance(RSA_ALGORITHM);
//        }catch(NoSuchAlgorithmException e){
//            throw new IllegalArgumentException("No such algorithm-->[" + RSA_ALGORITHM + "]");
//        }
//
//        //初始化KeyPairGenerator对象,密钥长度
//        kpg.initialize(keySize);
//        //生成密匙对
//        KeyPair keyPair = kpg.generateKeyPair();
//        //得到公钥
//        Key publicKey = keyPair.getPublic();
//        String publicKeyStr = Base64.encodeBase64URLSafeString(publicKey.getEncoded());
//        //得到私钥
//        Key privateKey = keyPair.getPrivate();
//        String privateKeyStr = Base64.encodeBase64URLSafeString(privateKey.getEncoded());
//        Map<String, String> keyPairMap = new HashMap<String, String>();
//        keyPairMap.put("publicKey", publicKeyStr);
//        keyPairMap.put("privateKey", privateKeyStr);
//
//        return keyPairMap;
//    }
//
//    /**
//     * 得到公钥
//     * @param publicKey 密钥字符串（经过base64编码）
//     * @throws Exception
//     */
//    public static RSAPublicKey getPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
//        //通过X509编码的Key指令获得公钥对象
//        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
//        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey));
//        RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
//        return key;
//    }
//
//    /**
//     * 得到私钥
//     * @param privateKey 密钥字符串（经过base64编码）
//     * @throws Exception
//     */
//    public static RSAPrivateKey getPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
//        //通过PKCS#8编码的Key指令获得私钥对象
//        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
//        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
//        RSAPrivateKey key = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
//        return key;
//    }
//
//    /**
//     * 公钥加密
//     * @param data
//     * @param publicKey
//     * @return
//     */
//    public static String publicEncrypt(String data, RSAPublicKey publicKey){
//        try{
//            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
//            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
//            return Base64.encodeBase64URLSafeString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), publicKey.getModulus().bitLength()));
//        }catch(Exception e){
//            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
//        }
//    }
//
//    /**
//     * 私钥解密
//     * @param data
//     * @param privateKey
//     * @return
//     */
//    public static String privateDecrypt(String data, RSAPrivateKey privateKey){
//        try{
//            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
//            cipher.init(Cipher.DECRYPT_MODE, privateKey);
//            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data), privateKey.getModulus().bitLength()), CHARSET);
//        }catch(Exception e){
//            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
//        }
//    }
//
//    @SneakyThrows
//    public static String privateDecrypt(String data, String privateKeyString)  {
//        return privateDecrypt(data,getPrivateKey(privateKeyString));
//    }
//
//    /**
//     * 私钥加密
//     * @param data
//     * @param privateKey
//     * @return
//     */
//
//    public static String privateEncrypt(String data, RSAPrivateKey privateKey){
//        try{
//            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
//            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
//            return Base64.encodeBase64URLSafeString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), privateKey.getModulus().bitLength()));
//        }catch(Exception e){
//            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
//        }
//    }
//
//    /**
//     * 公钥解密
//     * @param data
//     * @param publicKey
//     * @return
//     */
//
//    public static String publicDecrypt(String data, RSAPublicKey publicKey){
//        try{
//            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
//            cipher.init(Cipher.DECRYPT_MODE, publicKey);
//            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data), publicKey.getModulus().bitLength()), CHARSET);
//        }catch(Exception e){
//            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
//        }
//    }
//
//    private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas, int keySize){
//        int maxBlock = 0;
//        if(opmode == Cipher.DECRYPT_MODE){
//            maxBlock = keySize / 8;
//        }else{
//            maxBlock = keySize / 8 - 11;
//        }
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        int offSet = 0;
//        byte[] buff;
//        int i = 0;
//        try{
//            while(datas.length > offSet){
//                if(datas.length-offSet > maxBlock){
//                    buff = cipher.doFinal(datas, offSet, maxBlock);
//                }else{
//                    buff = cipher.doFinal(datas, offSet, datas.length-offSet);
//                }
//                out.write(buff, 0, buff.length);
//                i++;
//                offSet = i * maxBlock;
//            }
//        }catch(Exception e){
//            throw new RuntimeException("加解密阀值为["+maxBlock+"]的数据时发生异常", e);
//        }
//        byte[] resultDatas = out.toByteArray();
//        IOUtils.closeQuietly(out);
//        return resultDatas;
//    }
//
//
//
//public static void main (String[] args) throws Exception {
//    Map<String, String> keyMap = RSAUtils.createKeys(1024);
//
///*
//（2） 一次能加密的密文长度与公钥长度成正比，如RSA1024，一次能加密的内容长度为 1024/8  = 128byte（包含填充字节）。
//所以非对称加密一般都用于加密对称加密算法的密钥，而不是直接加密内容。
//
//↑所以客户端生成的临时aes key 不要超过128
//*/
//
//    String  publicKey = keyMap.get("publicKey");
//    String  privateKey = keyMap.get("privateKey");
//    System.out.println("公钥: \n\r" + publicKey);
//    System.out.println("私钥： \n\r" + privateKey);
//
//    System.out.println("公钥加密——私钥解密");
//    String str = "128bitsSecretkey";//模拟客户端临时生成的 aes key
//    System.out.println("\r明文：\r\n" + str);
//    System.out.println("\r明文大小：\r\n" + str.getBytes().length);
//    String encodedData = RSAUtils.publicEncrypt(str, RSAUtils.getPublicKey(publicKey));
//    System.out.println("密文：\r\n" + encodedData);
//    String decodedData = RSAUtils.privateDecrypt(encodedData, RSAUtils.getPrivateKey(privateKey));
//    System.out.println("解密后文字: \r\n" + decodedData);
///*
//    公钥:
//    MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCXaK-7dD7xOfdBQK6_ob_6arbJNUfol2hV9LqVCfF5Jh6zFOqqWTNu6GczDputq_w8y01jSmWpfkB_fOzl0I9-U54a28C8Zeez2esH1f4YIB5TILZ6f-8mCQD4EtplTdBOVSJz5rjHBEuffKQftZb51CdPt_Op8vxUvfiFCUTAwQIDAQAB
//    私钥：
//    MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJdor7t0PvE590FArr-hv_pqtsk1R-iXaFX0upUJ8XkmHrMU6qpZM27oZzMOm62r_DzLTWNKZal-QH987OXQj35TnhrbwLxl57PZ6wfV_hggHlMgtnp_7yYJAPgS2mVN0E5VInPmuMcES598pB-1lvnUJ0-386ny_FS9-IUJRMDBAgMBAAECgYEAlmR00aT49FFYiOc_7Lc04v9myltzLtRd3at2PZ4fze-QZN9s7IIn9Y1BHNTwy8ReiuCB4RNAAeiXFks3YFsWe5yHHsW_Y3ntN0Tla_nkVkjm2iG_dIKHS5iY3ERoheR8i0d0T1BnmwbyCwdl7-QWmjVdeZ8YPFxAQ72Wr6DLY6UCQQDwgAGW8rdxbKQjqSIoFTRfSTLfI6Ba3dsb7xNiQE3RSiq_k4LskbnNCAqf7WNy85gNjENX-W8lmP1t6rJqC5tvAkEAoSrFs6HpF2I469ALkZH6iapi7k97W4nlnnOeaNAx9uuXy9hyQiKSGZafSidxPvmbV1qV2CVxc53FhTCD5b4OzwJAK4LtRrMZD1NZiv4hqODVPdwPcSGP9ICpEK-7cQ4zRgdGHq0Ahe6DkB3BVlfrozOBMgpLcNI3ErVQPJ-2scrxzwJAWybfzisCtBD_dI-kG17evkG51mLpt-oUDjwCGfG2cJrqrYXriXAYBZTk3oHUUPPHYe5_1VHICsXu0tePob6OjQJAeZXbdfkNx7-uZ295rTj3Yq3H11uB6hB317eODHtnnCMVH0ww50C9pGnRPO2dEaShCwLUeOucxBim_usmIBaPOw
//
//    密文：
//    c5MwgxRzJzn009Tmj58t0D1zmYMuJm5X9vuuFhB1ZuWEQGlNPY683XcTrbbcVocexBNEgK23X6cLR2RoW_BU-582a0aS3UswGKcLWXLM5ZgcwAttvUsIlx2pJ0DJk-vg5-Iv_nq9k2hXvKoXfbWvQt8AZ_KChHrWcoqU4P-CZvc
//    解密后文字:
//    Jabaw1aFZLcj4lnqnrgIKA==
//
//↑，即客户端临时秘钥，给AES解密用
//    */
//    }
//
///* 测试用 */
////    @Value("${cryption.rsa.private-key}")//单纯跑test没用，不加载application.yml
//    private String privateKey="MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJdor7t0PvE590FArr-hv_pqtsk1R-iXaFX0upUJ8XkmHrMU6qpZM27oZzMOm62r_DzLTWNKZal-QH987OXQj35TnhrbwLxl57PZ6wfV_hggHlMgtnp_7yYJAPgS2mVN0E5VInPmuMcES598pB-1lvnUJ0-386ny_FS9-IUJRMDBAgMBAAECgYEAlmR00aT49FFYiOc_7Lc04v9myltzLtRd3at2PZ4fze-QZN9s7IIn9Y1BHNTwy8ReiuCB4RNAAeiXFks3YFsWe5yHHsW_Y3ntN0Tla_nkVkjm2iG_dIKHS5iY3ERoheR8i0d0T1BnmwbyCwdl7-QWmjVdeZ8YPFxAQ72Wr6DLY6UCQQDwgAGW8rdxbKQjqSIoFTRfSTLfI6Ba3dsb7xNiQE3RSiq_k4LskbnNCAqf7WNy85gNjENX-W8lmP1t6rJqC5tvAkEAoSrFs6HpF2I469ALkZH6iapi7k97W4nlnnOeaNAx9uuXy9hyQiKSGZafSidxPvmbV1qV2CVxc53FhTCD5b4OzwJAK4LtRrMZD1NZiv4hqODVPdwPcSGP9ICpEK-7cQ4zRgdGHq0Ahe6DkB3BVlfrozOBMgpLcNI3ErVQPJ-2scrxzwJAWybfzisCtBD_dI-kG17evkG51mLpt-oUDjwCGfG2cJrqrYXriXAYBZTk3oHUUPPHYe5_1VHICsXu0tePob6OjQJAeZXbdfkNx7-uZ295rTj3Yq3H11uB6hB317eODHtnnCMVH0ww50C9pGnRPO2dEaShCwLUeOucxBim_usmIBaPOw";
//
////    @Value("${cryption.rsa.public-key}")
//    private String publicKey="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCXaK-7dD7xOfdBQK6_ob_6arbJNUfol2hV9LqVCfF5Jh6zFOqqWTNu6GczDputq_w8y01jSmWpfkB_fOzl0I9-U54a28C8Zeez2esH1f4YIB5TILZ6f-8mCQD4EtplTdBOVSJz5rjHBEuffKQftZb51CdPt_Op8vxUvfiFCUTAwQIDAQAB";
//
//    @Test
//    public void validateEncryption(){
//
//        //模拟被加密后的临时aes秘钥。本应由客户端生成
//        String stringEncrypted="c5MwgxRzJzn009Tmj58t0D1zmYMuJm5X9vuuFhB1ZuWEQGlNPY683XcTrbbcVocexBNEgK23X6cLR2RoW_BU-582a0aS3UswGKcLWXLM5ZgcwAttvUsIlx2pJ0DJk-vg5-Iv_nq9k2hXvKoXfbWvQt8AZ_KChHrWcoqU4P-CZvc";
//
//        String stringDecrypted=RSAUtils.privateDecrypt(stringEncrypted,privateKey);
//
//        log.info(stringDecrypted);
//
//    }
//}