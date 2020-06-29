package com.lxk.controller;

import com.lxk.pojo.bo.SubmitOrderBO;
import com.lxk.utils.ResultJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;


/**
 * @author songshiyu
 * @date 2020/6/28 22:25
 **/

@Api(value = "订单相关接口", tags = {"订单相关接口api"})
@RestController
@RequestMapping("/orders")
public class OrdersController {

    private static final Logger logger = LoggerFactory.getLogger(OrdersController.class);

    @ApiOperation(value = "用户下单", notes = "用户下单", httpMethod = "POST")
    @PostMapping("/create")
    public ResultJSONResult create(@RequestBody SubmitOrderBO submitOrderBO) {
        /**
         * 1.创建订单
         * 2.创建订单以后，移除购物车中已结算(已提交)的商品
         * 3.向支付中心发送当前订单，用于保存支付中心的订单数据
         *
         * */
        logger.info(submitOrderBO.toString());
        return ResultJSONResult.ok();
    }

}
