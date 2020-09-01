package com.lxk.controller;

import com.sun.deploy.net.HttpResponse;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author songshiyu
 * @date 2020/6/14 17:59
 **/

@Controller
public class HelloController {

    @GetMapping("/login")
    public String login(String returnUrl,
                        Model model,
                        HttpServletRequest request,
                        HttpServletResponse response) {

        model.addAttribute("returnUrl",returnUrl);

        //TODO 后续完善是否登录

        //用户从未登录过，第一次进入则跳转到CAS登录页面
        return "login";
    }

}
