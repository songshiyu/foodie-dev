package com.lxk.controller;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author songshiyu
 * @date 2020/6/28 22:25
 **/

@Api(value = "地址相关接口",tags = {"地址相关接口"})
@RestController
public class AddressController {

    /**
     *  用户在确认订单页面，可以针对收货地址做如下操作：
     *      1.查询用户的所有收货地址列表
     *      2.新增收货地址
     *      3.删除收货地址
     *      4.修改收货地址
     *      5.设置默认地址
     * */
}
