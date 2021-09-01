package com.boredou.user.util;

import com.alibaba.fastjson.JSON;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Security;
import java.util.*;

/**
 * 钉钉开放平台加解密方法
 * 在ORACLE官方网站下载JCE无限制权限策略文件
 * JDK6的下载地址：http://www.oracle.com/technetwork/java/javase/downloads/jce-6-download-429243.html
 * JDK7的下载地址： http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html
 * JDK8的下载地址 https://www.oracle.com/java/technologies/javase-jce8-downloads.html
 */
public class DingCallbackCrypto {

    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final Base64 base64 = new Base64();
    private final byte[] aesKey;
    private final String token;
    private final String corpId;
    /**
     * ask getPaddingBytes key固定长度
     **/
    private static final Integer AES_ENCODE_KEY_LENGTH = 43;
    /**
     * 加密随机字符串字节长度
     **/
    private static final Integer RANDOM_LENGTH = 16;

    /**
     * 构造函数
     *
     * @param token          钉钉开放平台上，开发者设置的token
     * @param encodingAesKey 钉钉开放台上，开发者设置的EncodingAESKey
     * @param corpId         企业自建应用-事件订阅, 使用appKey
     *                       企业自建应用-注册回调地址, 使用corpId
     *                       第三方企业应用, 使用suiteKey
     * @throws DingTalkEncryptException 执行失败，请查看该异常的错误码和具体的错误信息
     */
    public DingCallbackCrypto(String token, String encodingAesKey, String corpId) throws DingTalkEncryptException {
        if (StringUtils.isBlank(encodingAesKey) || encodingAesKey.length() != AES_ENCODE_KEY_LENGTH) {
            throw new DingTalkEncryptException(DingTalkEncryptException.AES_KEY_ILLEGAL);
        }
        this.token = token;
        this.corpId = corpId;
        aesKey = Base64.decodeBase64(encodingAesKey + "=");
    }

    public Map<String, String> getEncryptedMap(String plaintext) throws DingTalkEncryptException {
        return getEncryptedMap(plaintext, System.currentTimeMillis(), Utils.getRandomStr(16));
    }

    /**
     * 将和钉钉开放平台同步的消息体加密,返回加密Map
     *
     * @param plaintext 传递的消息体明文
     * @param timeStamp 时间戳
     * @param nonce     随机字符串
     */
    public Map<String, String> getEncryptedMap(String plaintext, Long timeStamp, String nonce)
            throws DingTalkEncryptException {
        if (StringUtils.isBlank(plaintext)) {
            throw new DingTalkEncryptException(DingTalkEncryptException.ENCRYPTION_PLAINTEXT_ILLEGAL);
        }
        if (null == timeStamp) {
            throw new DingTalkEncryptException(DingTalkEncryptException.ENCRYPTION_TIMESTAMP_ILLEGAL);
        }
        if (StringUtils.isBlank(nonce)) {
            throw new DingTalkEncryptException(DingTalkEncryptException.ENCRYPTION_NONCE_ILLEGAL);
        }
        // 加密
        String encrypt = encrypt(Utils.getRandomStr(RANDOM_LENGTH), plaintext);
        String signature = getSignature(token, String.valueOf(timeStamp), nonce, encrypt);
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("msg_signature", signature);
        resultMap.put("encrypt", encrypt);
        resultMap.put("timeStamp", String.valueOf(timeStamp));
        resultMap.put("nonce", nonce);
        return resultMap;
    }

    /**
     * 密文解密
     *
     * @param msgSignature 签名串
     * @param timeStamp    时间戳
     * @param nonce        随机串
     * @param encryptMsg   密文
     * @return 解密后的原文
     */
    public String getDecryptMsg(String msgSignature, String timeStamp, String nonce, String encryptMsg)
            throws DingTalkEncryptException {
        //校验签名
        String signature = getSignature(token, timeStamp, nonce, encryptMsg);
        if (!signature.equals(msgSignature)) {
            throw new DingTalkEncryptException(DingTalkEncryptException.COMPUTE_SIGNATURE_ERROR);
        }
        // 解密
        return decrypt(encryptMsg);
    }

    /*
     * 对明文加密.
     * @param text 需要加密的明文
     * @return 加密后base64编码的字符串
     */
    private String encrypt(String random, String plaintext) throws DingTalkEncryptException {
        try {
            byte[] randomBytes = random.getBytes(CHARSET);
            byte[] plainTextBytes = plaintext.getBytes(CHARSET);
            byte[] lengthByte = Utils.int2Bytes(plainTextBytes.length);
            byte[] corpidBytes = corpId.getBytes(CHARSET);
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            byteStream.write(randomBytes);
            byteStream.write(lengthByte);
            byteStream.write(plainTextBytes);
            byteStream.write(corpidBytes);
            byte[] padBytes = PKCS7Padding.getPaddingBytes(byteStream.size());
            byteStream.write(padBytes);
            byte[] unencrypted = byteStream.toByteArray();
            byteStream.close();
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(aesKey, "AES");
            IvParameterSpec iv = new IvParameterSpec(aesKey, 0, 16);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
            byte[] encrypted = cipher.doFinal(unencrypted);
            return base64.encodeToString(encrypted);
        } catch (Exception e) {
            throw new DingTalkEncryptException(DingTalkEncryptException.COMPUTE_ENCRYPT_TEXT_ERROR);
        }
    }

    /*
     * 对密文进行解密.
     * @param text 需要解密的密文
     * @return 解密得到的明文
     */
    private String decrypt(String text) throws DingTalkEncryptException {
        byte[] originalArr;
        try {
            // 设置解密模式为AES的CBC模式
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(aesKey, "AES");
            IvParameterSpec iv = new IvParameterSpec(Arrays.copyOfRange(aesKey, 0, 16));
            cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
            // 使用BASE64对密文进行解码
            byte[] encrypted = Base64.decodeBase64(text);
            // 解密
            originalArr = cipher.doFinal(encrypted);
        } catch (Exception e) {
            throw new DingTalkEncryptException(DingTalkEncryptException.COMPUTE_DECRYPT_TEXT_ERROR);
        }

        String plainText;
        String fromCorpid;
        try {
            // 去除补位字符
            byte[] bytes = PKCS7Padding.removePaddingBytes(originalArr);
            // 分离16位随机字符串,网络字节序和corpId
            byte[] networkOrder = Arrays.copyOfRange(bytes, 16, 20);
            int plainTextLegth = Utils.bytes2int(networkOrder);
            plainText = new String(Arrays.copyOfRange(bytes, 20, 20 + plainTextLegth), CHARSET);
            fromCorpid = new String(Arrays.copyOfRange(bytes, 20 + plainTextLegth, bytes.length), CHARSET);
        } catch (Exception e) {
            throw new DingTalkEncryptException(DingTalkEncryptException.COMPUTE_DECRYPT_TEXT_LENGTH_ERROR);
        }

        // corpid不相同的情况
        if (!fromCorpid.equals(corpId)) {
            throw new DingTalkEncryptException(DingTalkEncryptException.COMPUTE_DECRYPT_TEXT_CORPID_ERROR);
        }
        return plainText;
    }

    /**
     * 数字签名
     *
     * @param token     isv token
     * @param timestamp 时间戳
     * @param nonce     随机串
     * @param encrypt   加密文本
     */
    public String getSignature(String token, String timestamp, String nonce, String encrypt)
            throws DingTalkEncryptException {
        try {
            String[] array = new String[]{token, timestamp, nonce, encrypt};
            Arrays.sort(array);
            System.out.println(JSON.toJSONString(array));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 4; i++) {
                sb.append(array[i]);
            }
            String str = sb.toString();
            System.out.println(str);
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(str.getBytes());
            byte[] digest = md.digest();

            StringBuilder hexstr = new StringBuilder();
            String shaHex;
            for (byte b : digest) {
                shaHex = Integer.toHexString(b & 0xFF);
                if (shaHex.length() < 2) {
                    hexstr.append(0);
                }
                hexstr.append(shaHex);
            }
            return hexstr.toString();
        } catch (Exception e) {
            throw new DingTalkEncryptException(DingTalkEncryptException.COMPUTE_SIGNATURE_ERROR);
        }
    }

    public static class Utils {
        public Utils() {
        }

        public static String getRandomStr(int count) {
            String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
            Random random = new Random();
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < count; ++i) {
                int number = random.nextInt(base.length());
                sb.append(base.charAt(number));
            }

            return sb.toString();
        }

        public static byte[] int2Bytes(int count) {
            return new byte[]{(byte) (count >> 24 & 255), (byte) (count >> 16 & 255), (byte) (count >> 8 & 255),
                    (byte) (count & 255)};
        }

        public static int bytes2int(byte[] byteArr) {
            int count = 0;

            for (int i = 0; i < 4; ++i) {
                count <<= 8;
                count |= byteArr[i] & 255;
            }

            return count;
        }
    }

    public static class PKCS7Padding {
        private static final Charset CHARSET = StandardCharsets.UTF_8;

        public PKCS7Padding() {
        }

        public static byte[] getPaddingBytes(int count) {
            int amountToPad = 32 - count % 32;

            char padChr = chr(amountToPad);
            StringBuilder tmp = new StringBuilder();

            for (int index = 0; index < amountToPad; ++index) {
                tmp.append(padChr);
            }

            return tmp.toString().getBytes(CHARSET);
        }

        public static byte[] removePaddingBytes(byte[] decrypted) {
            int pad = decrypted[decrypted.length - 1];
            if (pad < 1 || pad > 32) {
                pad = 0;
            }

            return Arrays.copyOfRange(decrypted, 0, decrypted.length - pad);
        }

        private static char chr(int a) {
            byte target = (byte) (a & 255);
            return (char) target;
        }
    }

    public static class DingTalkEncryptException extends Exception {
        public static final int SUCCESS = 0;
        public static final int ENCRYPTION_PLAINTEXT_ILLEGAL = 900001;
        public static final int ENCRYPTION_TIMESTAMP_ILLEGAL = 900002;
        public static final int ENCRYPTION_NONCE_ILLEGAL = 900003;
        public static final int AES_KEY_ILLEGAL = 900004;
        public static final int SIGNATURE_NOT_MATCH = 900005;
        public static final int COMPUTE_SIGNATURE_ERROR = 900006;
        public static final int COMPUTE_ENCRYPT_TEXT_ERROR = 900007;
        public static final int COMPUTE_DECRYPT_TEXT_ERROR = 900008;
        public static final int COMPUTE_DECRYPT_TEXT_LENGTH_ERROR = 900009;
        public static final int COMPUTE_DECRYPT_TEXT_CORPID_ERROR = 900010;
        private static final Map<Integer, String> msgMap = new HashMap<>();
        private final Integer code;

        static {
            msgMap.put(0, "成功");
            msgMap.put(900001, "加密明文文本非法");
            msgMap.put(900002, "加密时间戳参数非法");
            msgMap.put(900003, "加密随机字符串参数非法");
            msgMap.put(900005, "签名不匹配");
            msgMap.put(900006, "签名计算失败");
            msgMap.put(900004, "不合法的aes key");
            msgMap.put(900007, "计算加密文字错误");
            msgMap.put(900008, "计算解密文字错误");
            msgMap.put(900009, "计算解密文字长度不匹配");
            msgMap.put(900010, "计算解密文字corpid不匹配");
        }

        public Integer getCode() {
            return this.code;
        }

        public DingTalkEncryptException(Integer exceptionCode) {
            super(msgMap.get(exceptionCode));
            this.code = exceptionCode;
        }
    }

    static {
        try {
            Security.setProperty("crypto.policy", "limited");
            RemoveCryptographyRestrictions();
        } catch (Exception ignored) {
        }

    }

    private static void RemoveCryptographyRestrictions() throws Exception {
        Class<?> jceSecurity = getClazz("javax.crypto.JceSecurity");
        Class<?> cryptoPermissions = getClazz("javax.crypto.CryptoPermissions");
        Class<?> cryptoAllPermission = getClazz("javax.crypto.CryptoAllPermission");
        if (Optional.ofNullable(jceSecurity).isPresent()) {
            setFinalStaticValue(jceSecurity);
            PermissionCollection defaultPolicy = getFieldValue(jceSecurity, "defaultPolicy", null, PermissionCollection.class);
            if (Optional.ofNullable(cryptoPermissions).isPresent()) {
                Map<?, ?> map = (Map) getFieldValue(cryptoPermissions, "perms", defaultPolicy, Map.class);
                map.clear();
            }

            if (Optional.ofNullable(cryptoAllPermission).isPresent()) {
                Permission permission = getFieldValue(cryptoAllPermission, "INSTANCE", null, Permission.class);
                defaultPolicy.add(permission);
            }
        }

    }

    private static Class<?> getClazz(String className) {
        Class clazz = null;

        try {
            clazz = Class.forName(className);
        } catch (Exception ignored) {
        }

        return clazz;
    }

    private static void setFinalStaticValue(Class<?> srcClazz) throws Exception {
        Field field = srcClazz.getDeclaredField("isRestricted");
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & -17);
        field.set(null, false);
    }

    private static <T> T getFieldValue(Class<?> srcClazz, String fieldName, Object owner, Class<T> dstClazz) throws Exception {
        Field field = srcClazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return dstClazz.cast(field.get(owner));
    }
}
