package com.lxk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author songshiyu
 * @date 2020/6/14 17:52
 **/

@SpringBootApplication
/**  扫描mybatis通用mapper 所在的包*/
@MapperScan(basePackages = "com.lxk.mapper")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}
