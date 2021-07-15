package com.boredou.auth.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.boredou.auth.entity.SysUser;
import com.boredou.auth.entity.UserJwt;
import com.boredou.auth.service.SysUserService;
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
import java.util.List;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    SysUserService sysUserService;
    @Resource
    ClientDetailsService clientDetailsService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //取出身份，如果身份为空说明没有认证
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //没有认证统一采用httpbasic认证，httpbasic中存储了client_id和client_secret，开始认证client_id和client_secret
        if (authentication == null) {
            ClientDetails clientDetails = clientDetailsService.loadClientByClientId(username);
            if (clientDetails != null) {
                //密码
                String clientSecret = clientDetails.getClientSecret();
                return new User(username, clientSecret, AuthorityUtils.commaSeparatedStringToAuthorityList(""));
            }
        }
        if (StringUtils.isEmpty(username)) {
            return null;
        }
        //远程调用用户中心根据账号查询用户信息
        SysUser sysUser = sysUserService.getSysUser(username);
        UserJwt userDetails;
        if (ObjectUtil.isNotEmpty(sysUser)) {
            //取出正确密码
            String password = sysUser.getPassword();
            List<String> user_permission = sysUserService.getSysUserPermission(sysUser.getId());
            //权限信息
            String user_permission_string = StringUtils.join(user_permission.toArray(), ",");
            userDetails = new UserJwt(sysUser.getId(), sysUser.getName(), sysUser.getEmployeeId()
                    , sysUser.getPosition(), sysUser.getDepartment(), sysUser.getRank()
                    , sysUser.getRoleId(), sysUser.getPhone(), sysUser.getEmail(), sysUser.getQq()
                    , sysUser.getEntryTime(), sysUser.getCompany()
                    , username, password, AuthorityUtils.commaSeparatedStringToAuthorityList(user_permission_string));
            return userDetails;
        }
        return null;
    }
}
