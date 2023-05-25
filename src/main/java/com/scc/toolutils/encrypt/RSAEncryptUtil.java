package com.scc.toolutils.encrypt;


import org.apache.commons.codec.binary.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * rsa （非对称）加密
 * @author : scc
 * @date : 2023/05/25
 **/
public class RSAEncryptUtil {

    private static final Map<String, String> KEY_MAP = new HashMap<>();
 
    private RSAEncryptUtil() {
    }
 
    /**
     * 随机生成密钥对
     *
     * @throws NoSuchAlgorithmException
     */
    public static void genKeyPair() throws NoSuchAlgorithmException {
        // 密钥生成器
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        // 设置为2048位长度
        generator.initialize(2048, new SecureRandom());
        // 生成密钥对
        KeyPair keyPair = generator.generateKeyPair();
        // 获取私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        // 获取公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        // 进行64位编码得到公钥的字符串
        String publicKeyString = new String(Base64.encodeBase64(publicKey.getEncoded()));
        // 进行64位编码得到私钥的字符串
        String privateKeyString = new String(Base64.encodeBase64((privateKey.getEncoded())));
        // 将公钥和私钥保存到Map
        KEY_MAP.put("publicKey", publicKeyString);
        KEY_MAP.put("privateKey", privateKeyString);
    }
 
    /**
     * RSA公钥加密
     *
     * @param source    待加密字符串
     * @param publicKey 公钥
     * @return 加密后的字符串
     * @throws Exception 加密过程中的异常信息
     */
    public static String encrypt(String source, String publicKey) throws NoSuchAlgorithmException,
            InvalidKeySpecException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        // 把字符串转成字节
        byte[] decoded = Base64.decodeBase64(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(decoded));
        // rsa实例
        Cipher cipher = Cipher.getInstance("RSA");
        // 加密模式为加密，然后加密
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        return Base64.encodeBase64String(cipher.doFinal(source.getBytes(StandardCharsets.UTF_8)));
    }
 
    /**
     * RSA私钥解密
     *
     * @param encText    加密字符串
     * @param privateKey 私钥
     * @return 解密后的字符串
     * @throws Exception 解密过程中的异常信息
     */
    public static String decrypt(String encText, String privateKey) throws NoSuchAlgorithmException,
            InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException {
        // 把字符串转成字节
        byte[] inputByte = Base64.decodeBase64(encText.getBytes(StandardCharsets.UTF_8));
        byte[] decoded = Base64.decodeBase64(privateKey);
        // 获取rsa密钥实例
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(decoded));
        // rsa实例
        Cipher cipher = Cipher.getInstance("RSA");
        // 加密模式为解密，然后解密
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        return new String(cipher.doFinal(inputByte));
    }
 
    public static String getRSAPrivateKey() {
        return KEY_MAP.get("privateKey");
    }
 
    public static String getRSAPublicKey() {
        return KEY_MAP.get("publicKey");
    }
 
}