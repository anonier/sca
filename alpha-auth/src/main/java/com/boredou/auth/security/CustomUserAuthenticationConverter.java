package com.boredou.auth.security;

import cn.hutool.core.util.ObjectUtil;
import com.boredou.auth.entity.UserJwt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 自定义鉴权转换
 *
 * @author yb
 * @since 2021/5/27
 */
@Component
public class CustomUserAuthenticationConverter extends DefaultUserAuthenticationConverter {

    @Value("${jwt.location}")
    private String location;
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.alias}")
    private String alias;
    @Value("${jwt.password}")
    private String password;

    @Resource
    UserDetailsService userDetailsService;

    @Override
    public Map<String, ?> convertUserAuthentication(Authentication authentication) {
        LinkedHashMap<String, String> response = new LinkedHashMap<>();
        String name = authentication.getName();
        response.put("user_name", name);
        Object principal = authentication.getPrincipal();
        UserJwt userJwt;
        if (principal instanceof UserJwt) {
            userJwt = (UserJwt) principal;
        } else {
            userJwt = (UserJwt) userDetailsService.loadUserByUsername(name);
        }
        if (ObjectUtil.isNotEmpty(userJwt)) {
            response.put("id", userJwt.getId());
            response.put("name", userJwt.getName());
            response.put("employeeId", userJwt.getEmployeeId());
            response.put("position", userJwt.getPosition());
            response.put("department", userJwt.getDepartment());
            response.put("rank", userJwt.getRank());
            response.put("roleId", userJwt.getRoleId());
            response.put("phone", userJwt.getPhone());
            response.put("email", userJwt.getEmail());
            response.put("qq", String.valueOf(userJwt.getQq()));
            response.put("dingTalkBindStatus", userJwt.getDingTalkBindStatus());
            response.put("entryTime", String.valueOf(userJwt.getEntryTime()));
            response.put("company", userJwt.getCompany());
            if (ObjectUtil.isEmpty(authentication.getAuthorities()) && !authentication.getAuthorities().isEmpty()) {
                response.put("authorities", AuthorityUtils.authorityListToSet(authentication.getAuthorities()).toString());
            }
        }
        return response;
    }

    public String getLocation() {
        return location;
    }

    public String getSecret() {
        return secret;
    }

    public String getAlias() {
        return alias;
    }

    public String getPassword() {
        return password;
    }
}
