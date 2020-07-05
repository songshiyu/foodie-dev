package com.lxk.controller;

import com.lxk.pojo.Orders;
import com.lxk.service.center.MyOrdersService;
import com.lxk.utils.ResultJSONResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * @author songshiyu
 * @date 2020/6/26 21:41
 **/
@Controller
public class BaseController {

    @Autowired
    private MyOrdersService myOrdersService;


    /**
     * 评论默认的分页每页条数
     * */
    public static final Integer COMMON_PAGE_SIZE = 10;

    /**
     * 通用分页
     * */
    public static final Integer PAGE_SIZE = 20;

    /**
     * 购物车
     * */
    public static final String FOODIE_SHOPCART = "shopcart";

    /**
     * 微信支付成功 -》支付中心 -》 天天吃货平台 -》回调通知的url
     * */
    String payReturnUrl = "localhost:8088/orders/notifyMerchantOrderPaid";

    /**
     * 支付中心的调用地址
     * */
    String paymentUrl = "http://payment.t.mukewang.com/foodie-payment/payment/createMerchantOrder";

    /**
     * 用户上传头像的位置
     * */
    public static final String IMAGE_USER_FACE_LOCATION = "E:\\工作软件\\data\\images\\foodie\\face";


    /**
     * 用户验证用户与订单是否有管理关系，防止非法用户调用
     */
    public ResultJSONResult checkUserOrder(String userId, String orderId) {
        Orders orders = myOrdersService.queryMyOrder(userId, orderId);
        if (orders == null) {
            return ResultJSONResult.errorMsg("订单不存在！");
        }
        return ResultJSONResult.ok(orders);
    }
}
