package com.lxk.mapper;


import com.lxk.pojo.OrderStatus;
import com.lxk.pojo.vo.MyOrdersVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author songshiyu
 * @date 2020/7/5 15:46
 **/
public interface OrdersMapperCustom {

    public List<MyOrdersVO> queryMyOrders(@Param("paramsMap")Map<String,Object> map);

    public int getMyOrdersCounts(@Param("paramsMap")Map<String,Object> map);

    public List<OrderStatus> getMyOrderTrend(@Param("paramsMap")Map<String,Object> map);
}
