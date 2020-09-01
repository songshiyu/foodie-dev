package com.lxk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author songshiyu
 * @date 2020/6/14 17:52
 **/

@SpringBootApplication
@MapperScan(basePackages = "com.lxk.mapper")
@ComponentScan(basePackages = {"com.lxk","org.n3r.idworker"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
