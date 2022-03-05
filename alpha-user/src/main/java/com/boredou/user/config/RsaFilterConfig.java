package com.boredou.user.config;


import com.boredou.user.filter.RsaFilter.RsaFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 拦截器注册
 *
 * @author yb
 * @since 2021-8-31
 */
@Configuration
public class RsaFilterConfig {

    @Value("${environment}")
    private String environment;
    @Value("${rsa.rsaPubKey}")
    private String rsaPubKey;
    @Value("${rsa.rsaPriKey}")
    private String rsaPriKey;

    @Bean
    public FilterRegistrationBean<RsaFilter> RsaFilterRegistration() {
        FilterRegistrationBean<RsaFilter> registration = new FilterRegistrationBean<>(new RsaFilter());
        registration.addInitParameter("environment", environment);
        registration.addInitParameter("rsaPubKey", rsaPubKey);
        registration.addInitParameter("rsaPriKey", rsaPriKey);
        return registration;
    }
}
