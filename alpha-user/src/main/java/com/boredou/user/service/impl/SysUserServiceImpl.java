package com.boredou.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boredou.common.entity.AuthToken;
import com.boredou.common.enums.BizException;
import com.boredou.common.enums.enums.*;
import com.boredou.common.enums.exception.ExceptionRedis;
import com.boredou.common.enums.exception.Exceptions;
import com.boredou.common.util.AESUtil;
import com.boredou.common.util.CookieUtil;
import com.boredou.common.util.MD5Util;
import com.boredou.user.model.dto.NewUserDto;
import com.boredou.user.model.entity.SysLog;
import com.boredou.user.model.entity.SysUser;
import com.boredou.user.model.entity.SysUserRole;
import com.boredou.user.model.mapper.SysUserMapper;
import com.boredou.user.service.SysLogService;
import com.boredou.user.service.SysUserRoleService;
import com.boredou.user.service.SysUserService;
import com.boredou.user.service.feign.ApplyTokenService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RefreshScope
@Transactional
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

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
    @Resource
    SysUserRoleService sysUserRoleService;
    @Resource
    SysLogService sysLogService;

    @Override
    public AuthToken login(String username, String aesPasswd) {
        String realPasswd = AESUtil.decrypt(aesPasswd);
        AuthToken authToken = this.applyToken(username, realPasswd, clientId, clientSecret);
        String access_token = authToken.getJti();
        String jsonString = JSON.toJSONString(authToken);
        //将令牌存储到redis
        boolean result = saveToken(access_token, jsonString, tokenValiditySeconds);
        if (!result) {
            throw new BizException(ExceptionRedis.SAVE_FAIL);
        }
        this.saveCookie(authToken.getJti());
        return authToken;
    }

    @Override
    @DS("write")
    public void modifyPasswd(String password) {
        SysUser user = this.getUserByName(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        user.setPassword(MD5Util.encode(AESUtil.decrypt(password)));
        this.updateById(user);
    }

    @Override
    @DS("write")
    public void resetPasswd() {
        SysUser user = this.getUserByName(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        user.setPassword(MD5Util.encode(ResponseBaseUser.USER_DEFALTE_PASSWD.getMessage()));
        user.setUpdateTime(new Date());
        this.updateById(user);
    }

    @Override
    public void logout() {
        String uid = this.getTokenFormCookie();
        if (StringUtils.isBlank(uid) || !stringRedisTemplate.hasKey("user_token:" + uid))
            throw new BizException(Exceptions.FAILURE);
        this.delToken(uid);
        this.clearCookie(uid);
    }

    @Override
    @DS("write")
    public void newUser(NewUserDto dto) {
        dto.setPassword(MD5Util.encode(ResponseBaseUser.USER_DEFALTE_PASSWD.getMessage()));
        SysUser user = BeanUtil.copyProperties(dto, SysUser.class).builder().status("1").build();
        try {
            this.save(user);
        } catch (Exception e) {
            throw new BizException(Exceptions.ADD_USER_FAIL);
        }
        SysUserRole role = SysUserRole.builder()
                .role_id(dto.getRoleId())
                .user_id(user.getId())
                .creator(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString())
                .build();
        try {
            sysUserRoleService.save(role);
        } catch (Exception e) {
            throw new BizException(Exceptions.ADD_USER_ROLE_FAIL);
        }
    }

    private AuthToken applyToken(String username, String password, String clientId, String clientSecret) {
        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add(EnumAuth.GRANT_TYPE.getDesc(), EnumAuth.PASSWD.getDesc());
        body.add(EnumAuth.USERNAME.getDesc(), username);
        body.add(EnumAuth.PASSWD.getDesc(), password);
        String Authorization = getHttpBasic(clientId, clientSecret);
        try {
            Map<String, String> oauth2 = applyTokenService.applyToken(body, Authorization);
            return BeanUtil.fillBeanWithMap(oauth2, new AuthToken(), false);
        } catch (Exception e) {
            throw new BizException(Exceptions.FAILURE);
        }
    }

    public boolean saveToken(String access_token, String content, long ttl) {
        String key = EnumRedis.USER_TOKEN.getDesc() + access_token;
        stringRedisTemplate.boundValueOps(key).set(content, ttl, TimeUnit.SECONDS);
        Long expire = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        return expire != null && expire > 0 ? Boolean.TRUE : Boolean.FALSE;
    }

    private String getHttpBasic(String clientId, String clientSecret) {
        String string = clientId + EnumPunctuation.COLON.getDesc() + clientSecret;
        //将串进行base64编码
        byte[] encode = Base64Utils.encode(string.getBytes());
        return EnumCookie.BASIC.getDesc() + new String(encode);
    }

    @Override
    @DS("read")
    public SysUser getUserById(int id) {
        return this.getById(id);
    }

    @Override
    @DS("read")
    public SysUser getUserByName(String username) {
        return this.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username).eq(SysUser::getStatus, "1"));
    }

    @Override
    @DS("write")
    public void banUser(String id) {
        SysUser user = this.getById(id);
        user.setStatus("0");
        this.updateById(user);
    }

    @Override
    public String getTokenFormCookie() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Map<String, String> map = CookieUtil.readCookie(request, EnumCookie.UID.getDesc());
        if (StrUtil.isNotBlank(map.get(EnumCookie.UID.getDesc()))) {
            return map.get(EnumCookie.UID.getDesc());
        }
        return null;
    }

    @Override
    public AuthToken getUserToken(String token) {
        String key = EnumRedis.USER_TOKEN.getDesc() + token;
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
        String key = EnumRedis.USER_TOKEN.getDesc() + accessToken;
        try {
            stringRedisTemplate.delete(key);
        } catch (Exception e) {
            throw new BizException(ExceptionRedis.DEL_FAIL);
        }
    }

    @Override
    public void saveCookie(String token) {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        //HttpServletResponse response,String domain,String path, String name, String value, int maxAge,boolean httpOnly
        CookieUtil.addCookie(response, cookieDomain, EnumPunctuation.SLASH.getDesc(), EnumCookie.UID.getDesc(), token, cookieMaxAge, false);
    }

    @Override
    public void clearCookie(String token) {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        //HttpServletResponse response,String domain,String path, String name, String value, int maxAge,boolean httpOnly
        CookieUtil.addCookie(response, cookieDomain, EnumPunctuation.SLASH.getDesc(), EnumCookie.UID.getDesc(), token, 0, false);
    }

    @Override
    public List<SysLog> RecentDynamic() {
        return sysLogService.getLogsByName(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
    }

}
