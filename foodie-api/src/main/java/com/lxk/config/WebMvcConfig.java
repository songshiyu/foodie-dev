package com.lxk.config;

import com.lxk.interceptor.UserTokenIncerceptor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author songshiyu
 * @date 2020/7/1 22:05
 **/
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    /**
     * 实现静态资源的映射
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/META-INF/resources/")
                .addResourceLocations("file:///E:\\工作软件\\data\\images\\");
    }

    @Bean
    public UserTokenIncerceptor userTokenIncerceptor() {
        return new UserTokenIncerceptor();
    }

    /**
     * 添加自定义的拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userTokenIncerceptor())
                .addPathPatterns("/hello")
                .addPathPatterns("/shopcart/add")
                .addPathPatterns("/shopcart/del")
                .addPathPatterns("/address/list")
                .addPathPatterns("/address/add")
                .addPathPatterns("/address/update")
                .addPathPatterns("/address/setDefalut")
                .addPathPatterns("/address/delete")
                .addPathPatterns("/orders/*")
                .addPathPatterns("/center/*")
                .addPathPatterns("/userInfo/*")
                .addPathPatterns("/myorders/*")
                .addPathPatterns("/mycomments/*")
                .excludePathPatterns("/myorders/deliver")
                .excludePathPatterns("/orders/notifyMerchantOrderPaid");

        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
