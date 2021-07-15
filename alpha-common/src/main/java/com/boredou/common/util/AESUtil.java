package com.boredou.common.util;

import com.boredou.common.enums.BizException;
import com.boredou.common.enums.exception.Exceptions;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * AES解密工具
 *
 * @author yb
 * @since 2021-6-28
 */
public class AESUtil {

    /**
     * salt
     */
    private static final String AES_SALT = "jhyviaicdy751pp2";
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
     * @return String
     */
    public static String encrypt(String sSrc) {
        try {
            byte[] raw = AES_SALT.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec sKeySpec = new SecretKeySpec(raw, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM_STR);
            cipher.init(Cipher.ENCRYPT_MODE, sKeySpec);
            byte[] encrypted = cipher.doFinal(sSrc.getBytes(StandardCharsets.UTF_8));
            return new Base64().encodeToString(encrypted);
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
    public static String decrypt(String sSrc) {
        try {
            byte[] raw = AES_SALT.getBytes(StandardCharsets.UTF_8);
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
