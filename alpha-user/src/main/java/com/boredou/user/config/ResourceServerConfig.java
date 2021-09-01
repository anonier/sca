package com.boredou.user.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * 资源授权的配置类
 * EnableGlobalMethodSecurity：激活本服务工程的PreAuthorize注解（对应方法授权）
 *
 * @author yb
 * @date 2021/5/27
 */
@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Value("${jwt.jwtPubKey}")
    private String jwtPubKey;

    /**
     * 定义JwtTokenStore，使用jwt令牌
     *
     * @param jwtAccessTokenConverter jwt转换器
     * @return {@link TokenStore}
     */
    @Bean
    public TokenStore tokenStore(JwtAccessTokenConverter jwtAccessTokenConverter) {
        return new JwtTokenStore(jwtAccessTokenConverter);
    }

    /**
     * 定义JwtAccessTokenConverter，使用jwt令牌
     *
     * @return {@link JwtAccessTokenConverter }
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setVerifierKey(jwtPubKey);
        return converter;
    }

    /**
     * Http安全配置，对每个到达系统的http请求链接进行校验
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/v3/**", "/swagger-resources/**", "/swagger-ui/**", "/webjars/**", "/doc.html/**", "/favicon.ico", "/user/signIn", "/user/sendDingTalkCode", "/dingTalk/**").permitAll()
                .anyRequest().authenticated();
    }
}
