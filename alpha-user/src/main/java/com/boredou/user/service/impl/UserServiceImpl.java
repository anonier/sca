package com.boredou.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boredou.common.entity.AuthToken;
import com.boredou.common.util.CookieUtil;
import com.boredou.user.exception.BusiException;
import com.boredou.user.exception.enums.Exceptions;
import com.boredou.user.mapper.SysUserMapper;
import com.boredou.user.model.dto.NewUserDto;
import com.boredou.user.model.dto.SysUserDto;
import com.boredou.user.model.entity.SysUser;
import com.boredou.user.service.ApplyTokenService;
import com.boredou.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import sun.misc.BASE64Decoder;

import javax.annotation.Resource;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RefreshScope
@Transactional
public class UserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements UserService {

    @Value("${auth.clientId}")
    String clientId;
    @Value("${auth.clientSecret}")
    String clientSecret;
    @Value("${auth.cookieDomain}")
    String cookieDomain;
    @Value("${auth.cookieMaxAge}")
    int cookieMaxAge;
    @Value("${auth.tokenValiditySeconds}")
    int tokenValiditySeconds;

    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    ApplyTokenService applyTokenService;

    @Override
    public AuthToken login(String username, String password) {
        //请求spring security申请令牌
        AuthToken authToken;
        try {
            authToken = this.applyToken(username, password, clientId, clientSecret);
        } catch (Exception e) {
            throw new BusiException(Exceptions.FAILURE);
        }
        //用户身份令牌
        String access_token = authToken.getJwt_token();
        //存储到redis中的内容
        String jsonString = JSON.toJSONString(authToken);
        //将令牌存储到redis
        boolean result = saveToken(access_token, jsonString, tokenValiditySeconds);
        if (!result) {
            throw new RuntimeException();
        }
        return authToken;
    }

    @Override
    public void newUser(NewUserDto dto) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (StringUtils.isBlank(dto.getPassword())) {
            dto.setPassword(encoder.encode("boredou-1234"));
        } else {
            dto.setPassword(encoder.encode(dto.getPassword()));
        }
        SysUser user = BeanUtil.copyProperties(dto, SysUser.class);
        try {
            int id = this.baseMapper.insert(user);

        } catch (Exception e) {
            throw new BusiException("添加用户失败");
        }
    }

    private AuthToken applyToken(String username, String password, String clientId, String clientSecret) {
        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("username", username);
        body.add("password", password);
        String Authorization = getHttpBasic(clientId, clientSecret);
        Map<String, String> oauth2 = applyTokenService.applyToken(body, Authorization);
        AuthToken authToken = new AuthToken();
        authToken.setAccess_token(oauth2.get("access_token"));
        authToken.setRefresh_token(oauth2.get("refresh_token"));
        authToken.setJwt_token(oauth2.get("jti"));
        return authToken;
    }

    public boolean saveToken(String access_token, String content, long ttl) {
        String key = "user_token:" + access_token;
        stringRedisTemplate.boundValueOps(key).set(content, ttl, TimeUnit.SECONDS);
        Long expire = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        return expire > 0;
    }

    @Override
    public void saveCookie(String token) {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        //HttpServletResponse response,String domain,String path, String name, String value, int maxAge,boolean httpOnly
        CookieUtil.addCookie(response, cookieDomain, "/", "uid", token, cookieMaxAge, false);

    }

    private String getHttpBasic(String clientId, String clientSecret) {
        String string = clientId + ":" + clientSecret;
        //将串进行base64编码
        byte[] encode = Base64Utils.encode(string.getBytes());
        return "Basic " + new String(encode);
    }

    @Override
    @DS("read")
    public SysUser getUserById(int id) {
        return this.getById(id);
    }

    @Override
    @DS("write")
    public String newSysUser(SysUserDto dto) {
        SysUser user = BeanUtil.copyProperties(dto, SysUser.class);
        return this.save(user) == true ? "success" : "failure";
    }

    @Override
    public String getTokenFormCookie() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Map<String, String> map = CookieUtil.readCookie(request, "uid");
        if (map.get("uid") != null) {
            String uid = map.get("uid");
            return uid;
        }
        return null;
    }

    //从redis查询令牌
    @Override
    public AuthToken getUserToken(String token) {
        String key = "user_token:" + token;
        //从redis中取到令牌信息
        String value = stringRedisTemplate.opsForValue().get(key);
        //转成对象
        try {
            return JSON.parseObject(value, AuthToken.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void delToken(String accessToken) {
        String key = "user_token:" + accessToken;
        stringRedisTemplate.delete(key);
    }

    @Override
    public void clearCookie(String token) {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        //HttpServletResponse response,String domain,String path, String name, String value, int maxAge,boolean httpOnly
        CookieUtil.addCookie(response, cookieDomain, "/", "uid", token, 0, false);
    }


    /**
     * 根据密钥对指定的密文cipherText进行解密.
     *
     * @param cipherText 密文
     * @return 解密后的明文.
     */
    public static final String decrypt(String cipherText) {
        Key secretKey = getKey("fendo888");
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] c = decoder.decodeBuffer(cipherText);
            byte[] result = cipher.doFinal(c);
            String plainText = new String(result, "UTF-8");
            return plainText;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static Key getKey(String keySeed) {
        if (keySeed == null) {
            keySeed = System.getenv("AES_SYS_KEY");
        }
        if (keySeed == null) {
            keySeed = System.getProperty("AES_SYS_KEY");
        }
        if (keySeed == null || keySeed.trim().length() == 0) {
            keySeed = "abcd1234!@#$";// 默认种子
        }
        try {
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(keySeed.getBytes());
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(secureRandom);
            return generator.generateKey();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
