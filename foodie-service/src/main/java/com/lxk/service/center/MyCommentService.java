package com.lxk.service.center;


import com.lxk.pojo.OrderItems;
import com.lxk.pojo.bo.center.OrderItemsCommentBO;
import com.lxk.utils.PagedGridResult;

import java.util.List;

/**
 * @author songshiyu
 * @date 2020/7/5 16:10
 **/
public interface MyCommentService {

    /**
     * 根据订单id查询与订单相关联的商品
     * @param orderId
     * @return
     * */
    public List<OrderItems> queryPendingComment(String orderId);

    /**
     * @param orderId
     * @param userId
     * @param itemsComments
     * 评价商品
     * */
    public void saveComments(String userId,String orderId, List<OrderItemsCommentBO> itemsComments);


    /**
     * 我的评价查询，分页说
     * @param userId
     * @param page
     * @param pageSize
     * */
    public PagedGridResult queryMyComments(String userId,Integer page,Integer pageSize);
}
