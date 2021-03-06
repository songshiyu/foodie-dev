package com.lxk.service.impl;

import com.lxk.enums.OrderStatusEnum;
import com.lxk.enums.YesOrNo;
import com.lxk.mapper.OrderItemsMapper;
import com.lxk.mapper.OrderStatusMapper;
import com.lxk.mapper.OrdersMapper;
import com.lxk.pojo.*;
import com.lxk.pojo.bo.ShopcartBo;
import com.lxk.pojo.bo.SubmitOrderBO;
import com.lxk.pojo.vo.MerchantOrdersVO;
import com.lxk.pojo.vo.OrderVO;
import com.lxk.service.AddressService;
import com.lxk.service.ItemService;
import com.lxk.service.OrderService;
import com.lxk.utils.DateUtil;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author songshiyu
 * @date 2020/6/30 21:12
 **/
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private Sid sid;

    @Autowired
    private AddressService addressService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private OrderItemsMapper orderItemsMapper;

    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {})
    @Override
    public OrderVO createOrder(SubmitOrderBO submitOrderBO, List<ShopcartBo> shopcartBoList) {
        String userId = submitOrderBO.getUserId();
        String addressId = submitOrderBO.getAddressId();
        String itemSpecIds = submitOrderBO.getItemSpecIds();
        Integer payMethod = submitOrderBO.getPayMethod();
        String leftMsg = submitOrderBO.getLeftMsg();
        //包邮费用设置为0
        Integer postAccount = 0;

        String orderId = sid.nextShort();
        UserAddress userAddress = addressService.queryUserAddress(userId, addressId);

        //1.新订单数据保存
        Orders orders = new Orders();
        orders.setId(orderId);
        orders.setUserId(userId);
        orders.setReceiverName(userAddress.getReceiver());
        orders.setReceiverMobile(userAddress.getMobile());

        orders.setReceiverAddress(userAddress.getProvince() + " "
                + userAddress.getCity() + " "
                + userAddress.getDistrict() + " "
                + userAddress.getDetail());

        orders.setPostAmount(postAccount);
        orders.setPayMethod(payMethod);
        orders.setLeftMsg(leftMsg);
        orders.setIsComment(YesOrNo.NO.type);
        orders.setIsDelete(YesOrNo.NO.type);
        orders.setCreatedTime(new Date());
        orders.setUpdatedTime(new Date());

        //2.循环根据itemSpecIds循序保存订单商品信息表
        String[] itemSpecIdArr = itemSpecIds.split("\\,");
        //商品原价累计
        Integer totalAount = 0;
        //优惠后的实际支付价格累计
        Integer realPayAmount = 0;
        List<ShopcartBo> tobeRemoveList = new ArrayList<>();
        for (String itemSpecId : itemSpecIdArr) {
            //2.1 根据规格id，查询商品信息
            ItemsSpec itemsSpec = itemService.queryItemSpecById(itemSpecId);
            ShopcartBo shopcart = getBuyCountsFromShopcart(shopcartBoList, itemSpecId);
            if (shopcart != null) {
                //整商品购买的数量重新从redis中获取
                Integer buyCounts = shopcart.getBuyCounts();
                tobeRemoveList.add(shopcart);

                totalAount += itemsSpec.getPriceNormal() * buyCounts;
                realPayAmount += itemsSpec.getPriceDiscount() * buyCounts;
                //2.2 根据商品id，获得商品信息以及商品图片
                String itemId = itemsSpec.getItemId();
                Items items = itemService.queryItemById(itemId);
                String imgUrl = itemService.queryItemMainImgById(itemId);
                //2.3 循环保存子订单数据到数据库
                String orderItemId = sid.nextShort();
                OrderItems orderItems = new OrderItems();
                orderItems.setId(orderItemId);
                orderItems.setOrderId(orderId);
                orderItems.setItemId(itemId);
                orderItems.setItemName(items.getItemName());
                orderItems.setItemImg(imgUrl);
                orderItems.setBuyCounts(buyCounts);
                orderItems.setItemSpecId(itemSpecId);
                orderItems.setPrice(itemsSpec.getPriceDiscount());
                orderItems.setItemSpecName(itemsSpec.getName());
                orderItemsMapper.insert(orderItems);
                //2.4 在用户提交订单以后，规格表中需要扣除库存
                itemService.decreaseItemSpecStock(itemSpecId, buyCounts);
            }
        }

        orders.setTotalAmount(totalAount);
        orders.setRealPayAmount(realPayAmount);
        ordersMapper.insert(orders);

        //3.保存订单状态表
        OrderStatus waitpayOrderStatus = new OrderStatus();
        waitpayOrderStatus.setOrderId(orderId);
        waitpayOrderStatus.setOrderStatus(OrderStatusEnum.WAIT_PAY.type);
        waitpayOrderStatus.setCreatedTime(new Date());

        orderStatusMapper.insert(waitpayOrderStatus);

        //4.构建商户订单，用于传给支付中心
        MerchantOrdersVO merchantOrdersVO = new MerchantOrdersVO();
        merchantOrdersVO.setMerchantOrderId(orderId);
        merchantOrdersVO.setMerchantUserId(userId);
        merchantOrdersVO.setAmount(realPayAmount + postAccount);
        merchantOrdersVO.setPayMethod(payMethod);

        //5.构建自定义订单
        OrderVO orderVO = new OrderVO();
        orderVO.setOrderId(orderId);
        orderVO.setMerchantOrdersVO(merchantOrdersVO);
        orderVO.setTobeRemoveList(tobeRemoveList);
        return orderVO;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {})
    @Override
    public void updateOrderStatus(String orderId, Integer orderStatus) {
        OrderStatus paidStatus = new OrderStatus();
        paidStatus.setOrderId(orderId);
        paidStatus.setOrderStatus(orderStatus);
        paidStatus.setPayTime(new Date());

        orderStatusMapper.updateByPrimaryKeySelective(paidStatus);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public OrderStatus queryOrderStatusInfo(String orderId) {
        return orderStatusMapper.selectByPrimaryKey(orderId);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {})
    @Override
    public void closeOrder() {
        //查询所有未付款订单，判断时间是否超时（1天）
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderStatus(OrderStatusEnum.WAIT_PAY.type);

        List<OrderStatus> list = orderStatusMapper.select(orderStatus);
        for (OrderStatus o : list) {
            //获得订单创建时间
            Date createdTime = o.getCreatedTime();
            //和当前时间进行对比
            int days = DateUtil.daysBetween(createdTime, new Date());
            if (days >= 1) {
                //超过一天，关闭订单
                doCloseOrder(o.getOrderId());
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {})
    void doCloseOrder(String orderId) {
        OrderStatus close = new OrderStatus();
        close.setOrderId(orderId);
        close.setOrderStatus(OrderStatusEnum.CLOSE.type);
        close.setCloseTime(new Date());

        orderStatusMapper.updateByPrimaryKeySelective(close);
    }


    /**
     * 从redis中的购物车里获取商品，目的：counts
     *
     * @param shopcartList
     * @param specId
     * @return
     */
    private ShopcartBo getBuyCountsFromShopcart(List<ShopcartBo> shopcartList, String specId) {
        for (ShopcartBo sc : shopcartList) {
            if (specId.equals(sc.getSpecId())) {
                return sc;
            }
        }
        return null;
    }
}
