//package com.boredou.user.config;
//
//import com.alibaba.druid.pool.DruidDataSource;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.cloud.context.config.annotation.RefreshScope;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * Nacos配置
// */
//@EnableAutoConfiguration
//@Configuration
//public class NacosConfigConfiguration {
//    private final Logger LOGGER = LoggerFactory.getLogger(NacosConfigConfiguration.class);
//
//    @Bean(initMethod = "init")
//    @ConditionalOnMissingBean
//    @RefreshScope
//    public DruidDataSource dataSource() {
//        LOGGER.info("Init DruidDataSource");
//        return new DruidDataSourceWrapper();
//    }
//}
