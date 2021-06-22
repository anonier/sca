package com.boredou.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
	  //读取配置文件中的enable，true为显示，false为隐藏
    @Value("${swagger2.enable}")
    private boolean enable;
    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    public Docket createDocke(){
    	
    	//添加sping security 配置
//    	ParameterBuilder ticketPar = new ParameterBuilder();
//        List<Parameter> pars = new ArrayList<Parameter>();
//        ticketPar.name("Authorization").description("Bearer ")//Token 以及Authorization 为自定义的参数，session保存的名字是哪个就可以写成那个
//                .modelRef(new ModelRef("string")).parameterType("header")
//                .required(false).build(); //header中的ticket参数非必填，传空也可以

    	
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())  //进入swagger-ui的信息
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.boredou.user.controller"))  //暴露所有controller类的所在的包路径
               // .apis(RequestHandlerSelectors.basePackage("com.shubing.user.controller"))
                .paths(PathSelectors.any())
                .build()
                .enable(enable);//.globalOperationParameters(pars);
    }
    //进入swagger-ui的信息
    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                //该项目的名字
                .title(applicationName +" 接口")
                //该项目的描述
                .description(applicationName + "系统接口")
                .version("1.0")
                .build();
    }

}
