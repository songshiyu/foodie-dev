package com.lxk;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * @author songshiyu
 * @date 2020/7/8 22:22
 *
 * 添加war包的启动类
 **/
public class WarStarterApplication extends SpringBootServletInitializer{

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        /**
         * 此处需要指向spring-boot的启动类
         * */
        return builder.sources(Application.class);
    }
}
