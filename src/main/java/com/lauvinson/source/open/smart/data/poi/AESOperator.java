/*
 *
 * projectName: face-detect
 * fileName: AESOperator.java
 * packageName: com.wangxiaobao.facedetect.utils
 * date: 2019-10-31 16:16 PM
 * copyright(c) 2009-2019 成都旺小宝科技有限公司
 * https://www.wangxiaobao.com/
 *
 */
package com.lauvinson.source.open.smart.data.poi;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * AES 是一种可逆加密算法，对用户的敏感信息加密处理 对原始数据进行AES加密后，在进行Base64编码转化；
 *
 * @author created by vinson on 2019/10/31
 */
public class AESOperator {
    /*
     * 加密用的Key 可以用26个字母和数字组成 此处使用AES-128-CBC加密模式，key需要为16位。
     */
    private static AESOperator instance = null;

    private AESOperator() {

    }

    public static AESOperator getInstance() {
        if (instance == null) {
            instance = new AESOperator();
        }
        return instance;
    }

    // 加密
    public static String encrypt(String sSrc, String S_KEY, String IV_PARAMETER) {
        String result = "";
        try {
            if (null == sSrc || sSrc.length() <= 0) {
                return "";
            }
            Cipher cipher;
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] raw = S_KEY.getBytes();
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            // 使用CBC模式，需要一个向量iv，可增加加密算法的强度
            IvParameterSpec iv = new IvParameterSpec(IV_PARAMETER.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(sSrc.getBytes(StandardCharsets.UTF_8));
            result = new BASE64Encoder().encode(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 此处使用BASE64做转码。
        return result;

    }

    // 解密
    public static String decrypt(String sSrc, String S_KEY, String IV_PARAMETER) {
        try {
            if (null == sSrc || sSrc.length() <= 0) {
                return null;
            }
            byte[] raw = S_KEY.getBytes(StandardCharsets.US_ASCII);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(IV_PARAMETER.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] encrypted1 = new BASE64Decoder().decodeBuffer(sSrc);
            byte[] original = cipher.doFinal(encrypted1);
            return new String(original, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
