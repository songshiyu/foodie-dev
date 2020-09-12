package com.lxk.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author songshiyu
 * @date 2020/9/12 21:26
 **/

@RestController
public class HelloController {

    @GetMapping("/hello")
    public Object hello() {
        return "hello Elasticserch~";
    }

}
