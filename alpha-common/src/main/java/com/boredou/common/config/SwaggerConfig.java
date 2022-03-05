package com.boredou.common.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@Configuration
@EnableSwagger2
@EnableKnife4j
public class SwaggerConfig {

    @Value("${swagger2.enable}")
    private boolean enable;
    @Value("${spring.application.name}")
    private String applicationName;

    public static final String USER = "用户";
    public static final String COMPANY = "企业";
    public static final String FINANCE = "财务";
    public static final String DingTalk = "钉钉";

    @Bean
    public Docket createDocket() {
        return new Docket(DocumentationType.OAS_30).enable(enable)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.boredou.user.controller"))
                .build()
                .apiInfo(apiInfo())
                .securitySchemes(Collections.singletonList(HttpAuthenticationScheme.JWT_BEARER_BUILDER.name("Authorization").build()))
                .securityContexts(Collections.singletonList(SecurityContext.builder()
                        .securityReferences(Collections.singletonList(SecurityReference.builder()
                                .scopes(new AuthorizationScope[0])
                                .reference("Authorization")
                                .build()))
                        .operationSelector(o -> !o.requestMappingPattern().contains("/signIn")
                                && !o.requestMappingPattern().contains("/dingTalk)")
                                && !o.requestMappingPattern().contains("/sendDingTalkCode"))
                        .build()))
                .tags(new Tag(USER, "用户模块接口"))
                .tags(new Tag(DingTalk, "钉钉模块接口"))
                .tags(new Tag(FINANCE, "财务模块接口"))
                .tags(new Tag(COMPANY, "企业管理模块接口"));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(applicationName)
                .description(applicationName + "系统接口")
                .version("1.0")
                .build();
    }
}
