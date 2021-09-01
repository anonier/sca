package com.boredou.auth.security;

import com.boredou.common.util.MD5Util;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 添加md5加密方法
 *
 * @author yb
 * @since 2021/5/27
 */
public class Md5PasswordEncoder implements PasswordEncoder {

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encodedPassword.equals(MD5Util.encode((String) rawPassword));
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return MD5Util.encode((String) rawPassword);
    }
}
