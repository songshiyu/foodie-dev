package com.lxk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author songshiyu
 * @date 2020/6/14 17:52
 **/

@SpringBootApplication
/**  扫描mybatis通用mapper 所在的包*/
@MapperScan(basePackages = "com.lxk.mapper")
/**开启事务管理，默认开启，此处可以不用*/
@EnableTransactionManagement
/**扫描所有包以及相关组件包*/
@ComponentScan(basePackages = {"com.lxk","org.n3r.idworker"})
/**开启定时任务*/
@EnableScheduling
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}
