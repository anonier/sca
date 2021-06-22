package com.boredou.auth.security;

import cn.hutool.core.util.ObjectUtil;
import com.boredou.auth.entity.UserJwt;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class CustomUserAuthenticationConverter extends DefaultUserAuthenticationConverter {

    @Resource
    UserDetailsService userDetailsService;

    @Override
    public Map<String, ?> convertUserAuthentication(Authentication authentication) {
        LinkedHashMap response = new LinkedHashMap();
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
            response.put("name", userJwt.getName());
            response.put("id", userJwt.getId());
            response.put("utype", userJwt.getUtype());
            response.put("userpic", userJwt.getUserpic());
            response.put("company", userJwt.getCompany());
            if (authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
                response.put("authorities", AuthorityUtils.authorityListToSet(authentication.getAuthorities()));
            }
        }
        return response;
    }
}
