package com.boredou.common.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@EnableKnife4j
public class SwaggerConfig {

    @Value("${swagger2.enable}")
    private boolean enable;
    @Value("${spring.application.name}")
    private String applicationName;

    public static final String USER = "用户模块";
    public static final String COMPANY = "企业管理模块";

    @Bean
    public Docket createDocket() {
        return new Docket(DocumentationType.SWAGGER_2).enable(enable).select()
                .apis(RequestHandlerSelectors.basePackage("com.boredou.user.controller"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())  //进入swagger-ui的信息
                .tags(new Tag(USER, "用户相关模块接口"))
                .tags(new Tag(COMPANY, "企业管理相关模块接口"));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(applicationName)
                .description(applicationName + "系统接口")
                .version("1.0")
                .build();
    }

}
