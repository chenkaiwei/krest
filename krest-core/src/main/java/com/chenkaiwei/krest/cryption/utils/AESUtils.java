package com.chenkaiwei.krest.cryption.utils;//package com.chenkaiwei.krest.cryption.utils;
//
//import org.apache.commons.codec.binary.Base64;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.crypto.Cipher;
//import javax.crypto.KeyGenerator;
//import javax.crypto.SecretKey;
//import javax.crypto.spec.SecretKeySpec;
//
///**
// * 功能：AES 工具类
// * 说明：
// * @author Mr.tjm
// * @date 2020-5-20 11:25
// *
// * from https://jiming.blog.csdn.net/article/details/111488947
// */
//@SuppressWarnings("all")
//public class AESUtils {
//	private static final Logger logger = LoggerFactory.getLogger(AESUtils.class);
//
//	public final static String KEY_ALGORITHMS = "AES";
//	public final static int KEY_SIZE = 128;
//	public static final String CHARSET = "UTF-8";
//	/**
//	 * 生成AES密钥，base64编码格式 (128)
//	 * @return
//	 * @throws Exception
//	 */
//	public static String getKeyAES_128() throws Exception{
//		KeyGenerator keyGen = KeyGenerator.getInstance(KEY_ALGORITHMS);
//		keyGen.init(KEY_SIZE);
//		SecretKey key = keyGen.generateKey();
//		String base64str = Base64.encodeBase64String(key.getEncoded());
//		return base64str;
//	}
//
//	/**
//	 * 生成AES密钥，base64编码格式 (256)
//	 * @return
//	 * @throws Exception
//	 */
//	public static String getKeyAES_256() throws Exception{
//		// 256需要换jar包暂时用128
//		String base64str = getKeyAES_128();
//		return base64str;
//	}
//
//	/**
//	 * 根据base64Key获取SecretKey对象
//	 * @param base64Key
//	 * @return
//	 */
//	public static SecretKey loadKeyAES(String base64Key) {
//		byte[] bytes = Base64.decodeBase64(base64Key);
//		SecretKeySpec secretKeySpec = new SecretKeySpec(bytes, KEY_ALGORITHMS);
//		return secretKeySpec;
//	}
//
//	/**
//	 * AES 加密字符串，SecretKey对象
//	 * @param key
//	 * @param encryptData
//	 * @param encode
//	 * @return
//	 */
//	public static String encrypt(SecretKey key, String encryptData, String encode) {
//		try {
//			final Cipher cipher = Cipher.getInstance(KEY_ALGORITHMS);
//			cipher.init(Cipher.ENCRYPT_MODE, key);
//			byte[] encryptBytes = encryptData.getBytes(encode);
//			byte[] result = cipher.doFinal(encryptBytes);
//			return Base64.encodeBase64String(result);
//		} catch (Exception e) {
//			logger.error("加密异常:" + e.getMessage());
//			return null;
//		}
//	}
//
//	/**
//	 * AES 加密字符串，base64Key对象
//	 * @param base64Key
//	 * @param encryptData
//	 * @param encode
//	 * @return
//	 */
//	public static String encrypt(String base64Key, String encryptData, String encode) {
//		SecretKey key = loadKeyAES(base64Key);
//		try {
//			final Cipher cipher = Cipher.getInstance(KEY_ALGORITHMS);
//			cipher.init(Cipher.ENCRYPT_MODE, key);
//			byte[] encryptBytes = encryptData.getBytes(encode);
//			byte[] result = cipher.doFinal(encryptBytes);
//			return Base64.encodeBase64String(result);
//		} catch (Exception e) {
//			logger.error("加密异常:" + e.getMessage());
//			return null;
//		}
//	}
//
//	/*简化写法*/
//	public static String encrypt(String base64Key,  String encryptData) {
//		return encrypt(base64Key, encryptData,CHARSET);
//	}
//
//
//	/**
//	 * AES 解密字符串，SecretKey对象
//	 * @param key
//	 * @param decryptData
//	 * @param encode
//	 * @return
//	 */
//	public static String decrypt(SecretKey key, String decryptData, String encode) {
//		try {
//			final Cipher cipher = Cipher.getInstance(KEY_ALGORITHMS);
//			cipher.init(Cipher.DECRYPT_MODE, key);
//			byte[] decryptBytes = Base64.decodeBase64(decryptData);
//			byte[] result = cipher.doFinal(decryptBytes);
//			return new String(result, encode);
//		} catch (Exception e) {
//			logger.error("加密异常:" + e.getMessage());
//			return null;
//		}
//	}
//
//	/**
//	 * AES 解密字符串，base64Key对象
//	 * @param base64Key
//	 * @param decryptData
//	 * @param encode
//	 * @return
//	 */
//	public static String decrypt(String base64Key, String decryptData, String encode) {
//		SecretKey key = loadKeyAES(base64Key);
//		try {
//			final Cipher cipher = Cipher.getInstance(KEY_ALGORITHMS);
//			cipher.init(Cipher.DECRYPT_MODE, key);
//			byte[] decryptBytes = Base64.decodeBase64(decryptData);
//			byte[] result = cipher.doFinal(decryptBytes);
//			return new String(result, encode);
//		} catch (Exception e) {
//			logger.error("加密异常:" + e.getMessage());
//			return null;
//		}
//	}
//
///*简化写法*/
//	public static String decrypt(String base64Key, String decryptData) {
//		return decrypt(base64Key,decryptData,CHARSET);
//	}
//
//	public static void main(String[] args) throws Exception{
////		String tempKey=AESUtils.getKeyAES_128();//客户端生成的临时aes key 不要超过128
//		String tempKey="Jabaw1aFZLcj4lnqnrgIKA==";
//
//		String stringForEncrypt="{\n" +
//				"    \"username\":\"mytestname\",\n" +
//				"    \"password\":\"aaa111xxx\"\n" +
//				"}";
//
//		logger.info("stringForEncrypt: "+stringForEncrypt);
//
//		logger.info("tempKey:"+tempKey);
//
//		String stringEncrypted=AESUtils.encrypt(tempKey,stringForEncrypt);
//		logger.info("stringEncrypted: "+stringEncrypted);
//
//		String stringDecrypted=AESUtils.decrypt(tempKey,stringEncrypted);
//		logger.info("stringDecrypted: "+stringDecrypted);
///*
//		tempKey:Jabaw1aFZLcj4lnqnrgIKA==      //模拟客户端临时生成的key
//		stringEncrypted: qCpH9izYhUTa1f2IF1yDU4ylB1qhgfljejSle1zbzuuYMMaqftrl3s2zeiCSzEIu0jHVOKBLpy9TNEOHyuFb9A==
//	*/
//	}
//}