package com.boredou.common.config;


import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.boredou.common.interceptor.SqlLoggerInterceptor;
import com.boredou.common.module.log.BaseDataLog;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * mybatis plus配置类
 *
 * @author yb
 * @since 2021-7-2
 */
@Configuration
@EnableTransactionManagement
public class MyBatisPlusConfig {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(BaseDataLog.class)
    public String myInterceptor(SqlSessionFactory sqlSessionFactory) {
        SqlLoggerInterceptor sqlInterceptor = new SqlLoggerInterceptor();
        sqlSessionFactory.getConfiguration().addInterceptor(sqlInterceptor);
        return "interceptor";
    }

    @Bean
    public MybatisPlusInterceptor innerInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
