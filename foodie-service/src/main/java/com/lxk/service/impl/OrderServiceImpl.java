package com.lxk.service.impl;

import com.lxk.enums.OrderStatusEnum;
import com.lxk.enums.YesOrNo;
import com.lxk.mapper.OrderItemsMapper;
import com.lxk.mapper.OrderStatusMapper;
import com.lxk.mapper.OrdersMapper;
import com.lxk.pojo.*;
import com.lxk.pojo.bo.SubmitOrderBO;
import com.lxk.service.AddressService;
import com.lxk.service.ItemService;
import com.lxk.service.OrderService;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

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
    public String createOrder(SubmitOrderBO submitOrderBO) {
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
        for (String itemSpecId:itemSpecIdArr){
            //2.1 根据规格id，查询商品信息
            ItemsSpec itemsSpec = itemService.queryItemSpecById(itemSpecId);
            //TODO 整合redis后，商品购买的数量重新从redis中获取,此处暂定购买数量为1
            Integer buyCounts = 1;
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
            itemService.decreaseItemSpecStock(itemSpecId,buyCounts);
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

        return orderId;
    }
}
