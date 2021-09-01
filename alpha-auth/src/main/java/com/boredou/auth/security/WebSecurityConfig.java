package com.boredou.auth.security;

import com.boredou.auth.security.dingTalkCodeGranter.DingTalkCodeAuthenticationProvider;
import com.boredou.auth.security.dingTalkQrcodeGanter.DingTalkQrcodeAuthenticationProvider;
import com.boredou.auth.security.smsCodeGranter.SmsCodeAuthenticationProvider;
import com.boredou.auth.service.impl.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;

import javax.annotation.Resource;

/**
 * WebSecurity配置
 *
 * @author yb
 * @since 2021/5/27
 */
@Configuration
@EnableWebSecurity
@Order(-1)
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private UserDetailsServiceImpl userDetailsService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/v3/**", "/swagger-resources/**", "/swagger-ui/**", "/webjars/**", "/doc.html/**", "/favicon.ico/**");
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * 这里是对认证管理器的添加配置
     *
     * @param auth {@link AuthenticationManagerBuilder}
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(smsCodeProvider())
                .authenticationProvider(dingTalkCodeProvider())
                .authenticationProvider(dingTalkQrcodeProvider())
                .userDetailsService(userDetailsService)
                .passwordEncoder(new Md5PasswordEncoder());
    }

    /**
     * 自定义手机验证码认证提供者
     */
    @Bean
    public SmsCodeAuthenticationProvider smsCodeProvider() {
        return new SmsCodeAuthenticationProvider() {
            {
                setStringRedisTemplate(stringRedisTemplate);
                setHideUserNotFoundExceptions(false);
                setUserDetailsService(userDetailsService);
            }
        };
    }

    /**
     * 自定义钉钉验证码认证提供者
     */
    @Bean
    public DingTalkCodeAuthenticationProvider dingTalkCodeProvider() {
        return new DingTalkCodeAuthenticationProvider() {
            {
                setStringRedisTemplate(stringRedisTemplate);
                setHideUserNotFoundExceptions(false);
                setUserDetailsService(userDetailsService);
            }
        };
    }

    /**
     * 自定义钉钉扫码认证提供者
     */
    @Bean
    public DingTalkQrcodeAuthenticationProvider dingTalkQrcodeProvider() {
        return new DingTalkQrcodeAuthenticationProvider() {
            {
                setStringRedisTemplate(stringRedisTemplate);
                setHideUserNotFoundExceptions(false);
                setUserDetailsService(userDetailsService);
            }
        };
    }

    //采用md5对密码进行编码
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Md5PasswordEncoder();
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .httpBasic()
                .and()
                .formLogin()
                .and()
                .authorizeRequests()
                .anyRequest().authenticated();
    }

    @Bean
    public AuthorizationCodeServices authorizationCodeServices() {
        return new InMemoryAuthorizationCodeServices();
    }
}
