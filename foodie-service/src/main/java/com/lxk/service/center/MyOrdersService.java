package com.lxk.service.center;

import com.lxk.pojo.Orders;
import com.lxk.pojo.vo.OrderStatusCountsVO;
import com.lxk.utils.PagedGridResult;

/**
 * @author songshiyu
 * @date 2020/7/5 16:10
 **/
public interface MyOrdersService {

    /**
     * 查询我的订单列表
     * @param userId
     * @param orderStatus
     * @param page
     * @param pageSize
     *
     * @return
     * */
    public PagedGridResult queryMyOrders(String userId,String orderStatus,Integer page,Integer pageSize);

    /**
     * 模拟商家发货
     * @param orderId
     * */
    public void updateDeliverOrderStatus(String orderId);

    /**
     * 验证订单与用户的关系
     *  @param userId
     *  @param orderId
     *  @return
     * */
    public Orders queryMyOrder(String userId,String orderId);

    /**
     * 更新订单状态 -》确认收货
     * @param orderId
     * */
    public boolean updateReceiverOrderStatus(String orderId);

    /**
     * 更新订单状态 -》删除订单(逻辑删除)
     * @param userId
     * @param orderId
     * */
    public boolean deleteOrder(String userId,String orderId);


    /**
     * 根据用户id查询订单数
     * @param userId
     * */
    public OrderStatusCountsVO getOrderStatusCounts(String userId);

    /**
     * 获得分页的订单动向
     * @param userId
     * */
    public PagedGridResult getMyOrderTrend(String userId,Integer page,Integer pageSize);
}
