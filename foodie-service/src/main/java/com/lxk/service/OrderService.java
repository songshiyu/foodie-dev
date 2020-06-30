package com.lxk.service;

import com.lxk.pojo.bo.SubmitOrderBO;

/**
 * @author songshiyu
 * @date 2020/6/30 21:13
 **/
public interface OrderService {

    /**
     * 用于创建订单
     * */
    public void createOrder(SubmitOrderBO submitOrderBO);

}
