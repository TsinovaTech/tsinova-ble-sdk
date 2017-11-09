package com.tsinova.bluetoothandroid.util;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by ihgoo on 2017/4/14.
 */

public class Des {

    /**
     * 加密数据
     * @param encryptString  注意：这里的数据长度只能为8的倍数
     * @param encryptKey
     * @return
     * @throws Exception
     */
    public static byte[] encryptDES(String encryptString, String encryptKey) throws Exception {
        SecretKeySpec key = new SecretKeySpec(getKey(encryptKey), "DES");
        Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedData = cipher.doFinal(encryptString.getBytes());
        return encryptedData;
    }

    /**
     * 自定义一个key
     */
    public static byte[] getKey(String keyRule) {
        Key key = null;
        byte[] keyByte = keyRule.getBytes();
        // 创建一个空的八位数组,默认情况下为0
        byte[] byteTemp = new byte[8];
        // 将用户指定的规则转换成八位数组
        for (int i = 0; i < byteTemp.length && i < keyByte.length; i++) {
            byteTemp[i] = keyByte[i];
        }
        key = new SecretKeySpec(byteTemp, "DES/ECB/PKCS7Padding");
        return key.getEncoded();
    }

    /***
     * 解密数据
     * @param decryptKey
     * @return
     * @throws Exception
     */
    public static byte[] decryptDES(byte[] decrypt, String decryptKey) throws Exception {
        SecretKeySpec key = new SecretKeySpec(decryptKey.getBytes("ASCII"), "DES/ECB/PKCS7Padding");
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS7Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte decryptedData[] = cipher.doFinal(decrypt);
        return decryptedData;
    }

    public static void main(String[] args) throws Exception {
//        String clearText = "springsk";  //这里的数据长度必须为8的倍数
//        String key = "12345678";
//        System.out.println("明文："+clearText+"\n密钥："+key);
//        String encryptText = encryptDES(clearText, key);
//        System.out.println("加密后："+encryptText);
//        String decryptText = decryptDES(encryptText, key);
//        System.out.println("解密后："+decryptText);
    }

}
