package com.lxk.controller;

import org.springframework.stereotype.Controller;

/**
 * @author songshiyu
 * @date 2020/6/26 21:41
 **/
@Controller
public class BaseController {

    /**
     * 评论默认的分页每页条数
     * */
    public static final Integer COMMENT_PAGE_SIZE = 10;

    /**
     * 通用分页
     * */
    public static final Integer PAGE_SIZE = 20;

    /**
     * 购物车
     * */
    public static final String FOODIE_SHOPCART = "shopcart";

}
