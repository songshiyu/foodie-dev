package com.lxk.service.impl.center;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lxk.enums.YesOrNo;
import com.lxk.mapper.*;
import com.lxk.pojo.OrderItems;
import com.lxk.pojo.OrderStatus;
import com.lxk.pojo.Orders;
import com.lxk.pojo.bo.center.OrderItemsCommentBO;
import com.lxk.pojo.vo.MyCommentVO;
import com.lxk.service.center.MyCommentService;
import com.lxk.service.impl.BaseService;
import com.lxk.utils.PagedGridResult;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author songshiyu
 * @date 2020/7/5 21:12
 **/
@Service
public class MyCommentServiceImpl extends BaseService implements MyCommentService {

    @Autowired
    private OrderItemsMapper orderItemsMapper;

    @Autowired
    private Sid sid;

    @Autowired
    private ItemsCommentsMapperCustom itemsCommentsMapperCustom;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<OrderItems> queryPendingComment(String orderId) {
        OrderItems query = new OrderItems();
        query.setOrderId(orderId);

        List<OrderItems> itemsList = orderItemsMapper.select(query);
        return itemsList;
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = {})
    @Override
    public void saveComments(String userId, String orderId, List<OrderItemsCommentBO> itemsComments) {

        //1.保存评价 items_comments
        for (OrderItemsCommentBO oic:itemsComments){
            oic.setCommentId(sid.nextShort());
        }
        Map<String,Object> queryMap = new HashMap<>();
        queryMap.put("userId",userId);
        queryMap.put("commentList",itemsComments);
        itemsCommentsMapperCustom.saveComments(queryMap);

        //2.修改订单表已评价
        Orders orders = new Orders();
        orders.setIsComment(YesOrNo.YES.type);
        orders.setId(orderId);
        ordersMapper.updateByPrimaryKeySelective(orders);

        //3.修改订单状态表的留言时间 -- order_status
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setCommentTime(new Date());
        orderStatusMapper.updateByPrimaryKeySelective(orderStatus);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult queryMyComments(String userId, Integer page, Integer pageSize) {

        Map<String,Object> map = new HashMap<>();
        map.put("userId",userId);

        PageHelper.startPage(page,pageSize);
        List<MyCommentVO> myCommentVOSList = itemsCommentsMapperCustom.queryMyComments(map);

        return setterPagedgrid(myCommentVOSList,page);
    }

}
