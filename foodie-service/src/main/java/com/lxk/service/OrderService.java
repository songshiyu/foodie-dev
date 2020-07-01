package com.lxk.service;

import com.lxk.pojo.bo.SubmitOrderBO;
import com.lxk.pojo.vo.OrderVO;

/**
 * @author songshiyu
 * @date 2020/6/30 21:13
 **/
public interface OrderService {

    /**
     * 用于创建订单
     * */
    public OrderVO createOrder(SubmitOrderBO submitOrderBO);


    /**
     * 修改订单状态
     * @param orderId
     * @param orderStatus
     * */
    public void updateOrderStatus(String orderId,Integer orderStatus);
}
