package com.boredou.user.config;


import com.boredou.user.interceptor.SqlLoggerInterceptor;
import com.boredou.user.model.log.BaseDataLog;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author:yb
 * @date:2021-7-2
 * @description:mybatis插件配置类
 */
@Configuration
@EnableTransactionManagement
public class BatisPlusConfig {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(BaseDataLog.class)
    public String myInterceptor(SqlSessionFactory sqlSessionFactory) {
        SqlLoggerInterceptor sqlInterceptor = new SqlLoggerInterceptor();
        sqlSessionFactory.getConfiguration().addInterceptor(sqlInterceptor);
        return "interceptor";
    }
}
