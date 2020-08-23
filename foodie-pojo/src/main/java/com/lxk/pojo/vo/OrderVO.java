package com.lxk.pojo.vo;

import com.lxk.pojo.bo.ShopcartBo;

import java.util.List;

public class OrderVO {

    private String orderId;
    private MerchantOrdersVO merchantOrdersVO;
    private List<ShopcartBo> tobeRemoveList;


    public List<ShopcartBo> getTobeRemoveList() {
        return tobeRemoveList;
    }

    public void setTobeRemoveList(List<ShopcartBo> tobeRemoveList) {
        this.tobeRemoveList = tobeRemoveList;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public MerchantOrdersVO getMerchantOrdersVO() {
        return merchantOrdersVO;
    }

    public void setMerchantOrdersVO(MerchantOrdersVO merchantOrdersVO) {
        this.merchantOrdersVO = merchantOrdersVO;
    }
}