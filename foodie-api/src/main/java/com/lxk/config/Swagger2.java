package com.lxk.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author songshiyu
 * @date 2020/6/20 22:32
 * <p>
 * Swagger配置
 **/

/**
 * 开启spring管理
 */
@Configuration
/**开启swagger2*/
@EnableSwagger2
public class Swagger2 {

    /**
     *
     * 访问地址：
     *      1.原地址：http://localhost:8088/swagger-ui.html
     *      2.新地址：http://localhost:8088/doc.html
     * swagger的核心配置  docket
     */
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)   //用于指定api类型为swagger2
                .apiInfo(apiInfo())                              //用于定义api文档汇总信息
                .select()
                .apis(RequestHandlerSelectors
                        .basePackage("com.lxk.controller"))                 //指定controller包
                .paths(PathSelectors.any())                                 //所有controller
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("宝宝吃货 美食平台接口api")       //文档页标题
                .contact(new Contact("飞半天的鱼儿",
                        "",
                        "songshiyu_s@163.com"))     //联系人信息
                .description("专为宝宝吃货准备的api文档")      //详细信息
                .version("1.0.0")                              //文档版本号
                .termsOfServiceUrl("")                         //网站地址
                .build();
    }
}
