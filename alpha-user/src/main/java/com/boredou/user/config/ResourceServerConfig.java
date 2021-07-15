package com.boredou.user.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * 资源授权的配置类
 * EnableGlobalMethodSecurity：激活本服务工程的PreAuthorize注解（对应方法授权）
 *
 * @author yb
 * @date 2021/5/27 21:17
 */
@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    //读取配置文件中的enable，true为显示，false为隐藏
    @Value("${swagger2.enable}")
    private boolean swaggerEnable;

    /**
     * 公钥
     */
    private static final String PUBLIC_KEY = "publickey.txt";

    /**
     * 定义JwtTokenStore，使用jwt令牌
     *
     * @param jwtAccessTokenConverter
     * @return
     */
    @Bean
    public TokenStore tokenStore(JwtAccessTokenConverter jwtAccessTokenConverter) {
        return new JwtTokenStore(jwtAccessTokenConverter);
    }

    /**
     * 定义JwtAccessTokenConverter，使用jwt令牌
     *
     * @return
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setVerifierKey(getPubKey());
        return converter;
    }

    /**
     * 获取非对称加密公钥 Key
     *
     * @return
     */
    private String getPubKey() {
        Resource resource = new ClassPathResource(PUBLIC_KEY);
        try {
            InputStreamReader inputStreamReader = new
                    InputStreamReader(resource.getInputStream());
            BufferedReader br = new BufferedReader(inputStreamReader);
            return br.lines().collect(Collectors.joining("\n"));
        } catch (IOException ioe) {
            return null;
        }
    }

    /**
     * Http安全配置，对每个到达系统的http请求链接进行校验
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        if (swaggerEnable) {
            http.authorizeRequests().antMatchers("/v2/**", "/swagger-resources/**", "/swagger-ui/**", "/webjars/**", "/doc.html/**", "/favicon.ico/**").permitAll();
        }
        //所有请求必须认证通过
        http.authorizeRequests()
                .antMatchers("/user/signIn", "/user/userJwt").permitAll()
                .anyRequest().authenticated();
    }

}
