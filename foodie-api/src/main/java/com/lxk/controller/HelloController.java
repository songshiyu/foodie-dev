package com.lxk.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author songshiyu
 * @date 2020/6/14 17:59
 **/

/**在swagger文档中不显示*/
@ApiIgnore
@RestController
public class HelloController {

    @GetMapping("/hello")
    public Object hello() {
        return "hello world";
    }

    @GetMapping("/setSession")
    public Object setSession(HttpServletRequest request){
        HttpSession session = request.getSession();

        session.setAttribute("userInfo","new user");
        session.setMaxInactiveInterval(3600);

        return "ok";
    }
}
