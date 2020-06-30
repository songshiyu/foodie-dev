package com.lxk.controller;

import com.lxk.pojo.bo.SubmitOrderBO;
import com.lxk.enums.PayMethod;
import com.lxk.service.OrderService;
import com.lxk.utils.CookieUtils;
import com.lxk.utils.ResultJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author songshiyu
 * @date 2020/6/28 22:25
 **/

@Api(value = "订单相关接口", tags = {"订单相关接口api"})
@RestController
@RequestMapping("/orders")
public class OrdersController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(OrdersController.class);

    @Autowired
    private OrderService orderService;

    @ApiOperation(value = "用户下单", notes = "用户下单", httpMethod = "POST")
    @PostMapping("/create")
    public ResultJSONResult create(@RequestBody SubmitOrderBO submitOrderBO,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        //1.创建订单
        if(!submitOrderBO.getPayMethod().equals(PayMethod.WEIXIN.type)
                || !submitOrderBO.getPayMethod().equals(PayMethod.WEIXIN.type)){
            return ResultJSONResult.errorMsg("不支持的支付方式~");
        }
        logger.info(submitOrderBO.toString());
        String orderId = orderService.createOrder(submitOrderBO);

        //2.创建订单以后，移除购物车中已结算(已提交)的商品
        //TODO 整合redis后，完善购物车中的已结算商品清除，并且同步到前端的cookie
        CookieUtils.setCookie(request,response,FOODIE_SHOPCART,"",true);

        // 3.向支付中心发送当前订单，用于保存支付中心的订单数据
        return ResultJSONResult.ok(orderId);
    }

}
