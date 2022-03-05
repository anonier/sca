package com.boredou.auth.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.boredou.auth.entity.UserJwt;
import com.boredou.auth.service.SysUserService;
import com.boredou.common.module.entity.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    SysUserService sysUserService;
    @Resource
    ClientDetailsService clientDetailsService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!Optional.ofNullable(authentication).isPresent()) {
            ClientDetails clientDetails = clientDetailsService.loadClientByClientId(username);
            if (Optional.ofNullable(clientDetails).isPresent()) {
                String clientSecret = clientDetails.getClientSecret();
                return new User(username, clientSecret, AuthorityUtils.commaSeparatedStringToAuthorityList(""));
            }
        }
        if (StringUtils.isEmpty(username)) {
            return null;
        }
        SysUser sysUser = sysUserService.getSysUserByUsername(username);
        UserJwt userDetails;
        if (ObjectUtil.isNotEmpty(sysUser)) {
            String password = sysUser.getPassword();
            userDetails = new UserJwt(sysUser.getId(), sysUser.getName(), sysUser.getEmployeeId()
                    , sysUser.getPosition(), sysUser.getDepartment(), sysUser.getRank()
                    , sysUser.getPhone(), sysUser.getEmail(), sysUser.getQq(), sysUser.getEntryTime()
                    , sysUser.getCompany(), sysUser.getDingTalkBindStatus(), username, password
                    , AuthorityUtils.commaSeparatedStringToAuthorityList(""));
            return userDetails;
        }
        return null;
    }

    public UserDetails loadUserByPhone(String phone) {
        SysUser sysUser = sysUserService.getSysUserByPhone(phone);
        UserJwt userDetails;
        if (ObjectUtil.isNotEmpty(sysUser)) {
            String password = sysUser.getPassword();
            userDetails = new UserJwt(sysUser.getId(), sysUser.getName(), sysUser.getEmployeeId()
                    , sysUser.getPosition(), sysUser.getDepartment(), sysUser.getRank()
                    , sysUser.getPhone(), sysUser.getEmail(), sysUser.getQq(), sysUser.getEntryTime()
                    , sysUser.getCompany(), sysUser.getDingTalkBindStatus(), sysUser.getUsername(), password
                    , AuthorityUtils.commaSeparatedStringToAuthorityList(""));
            return userDetails;
        }
        return null;
    }
}
