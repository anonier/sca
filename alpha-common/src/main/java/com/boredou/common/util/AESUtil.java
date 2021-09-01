package com.boredou.common.util;

import com.boredou.common.enums.BizException;
import com.boredou.common.enums.exception.Exceptions;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Map;

/**
 * AES解密工具
 *
 * @author yb
 * @since 2021-6-28
 */
public class AESUtil {

    /**
     * 密钥算法
     */
    private static final String ALGORITHM = "AES";

    /**
     * 加解密算法/工作模式/填充方式
     */
    private static final String ALGORITHM_STR = "AES/ECB/PKCS5Padding";

    /**
     * AES-16-ECB加密
     *
     * @param sSrc 加密密码
     * @return Map
     */
    public static Map<String, String> encrypt(String sSrc) {
        try {
            String salt = new Base64().encodeToString(SecureRandom.getInstanceStrong().generateSeed(12));
            SecretKeySpec sKeySpec = new SecretKeySpec(salt.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM_STR);
            cipher.init(Cipher.ENCRYPT_MODE, sKeySpec);
            byte[] encrypted = cipher.doFinal(sSrc.getBytes(StandardCharsets.UTF_8));
            return ImmutableMap.<String, String>builder().put("data", new Base64().encodeToString(encrypted)).put("aesKey", salt).build();
        } catch (Exception ex) {
            throw new BizException(Exceptions.FAILURE);
        }
    }

    /**
     * AES-16-ECB解密
     *
     * @param sSrc 加密密码
     * @return String
     */
    public static String decrypt(String sSrc, String salt) {
        try {
            byte[] raw = salt.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec sKeySpec = new SecretKeySpec(raw, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM_STR);
            cipher.init(Cipher.DECRYPT_MODE, sKeySpec);
            byte[] encrypted1 = new Base64().decode(sSrc);//先用base64解密
            byte[] original = cipher.doFinal(encrypted1);
            return new String(original, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            throw new BizException(Exceptions.FAILURE);
        }
    }
}
