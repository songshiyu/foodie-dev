package com.lxk.config;

import com.lxk.service.OrderService;
import com.lxk.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author songshiyu
 * @date 2020/7/4 9:17
 **/
@Component
public class OrderJob {

    private static final Logger logger = LoggerFactory.getLogger(OrderJob.class);

    @Autowired
    private OrderService orderService;

    /**
     * 定时自动关闭订单
     * 每隔一小时查询
     *
     * 使用定时任务关闭超期未支付订单，会存在的弊端
     *  1.会有时间差
     *      10:39下单，11:00检查不足一小时，12:00检查，超过一个小时多余39分钟
     *  2.不支持集群
     *      单机没有毛病，使用集群后，就会有多个定时任务
     *      解决方案：只使用一台计算机节点，单独用来运行所有的定时任务。
     *  3.会对数据库全表搜索，及其影响数据库性能
     *  定时任务只适用于小型轻量级项目，传统项目
     *
     *  后续加入消息队列：MQ ==》延时队列
     * */
    /*@Scheduled(cron = "0/3 * * * * ?")*/
    @Scheduled(cron = "0 0 0/1 * * * ")
    public void autoCloseOrder(){
        orderService.closeOrder();
    }
}
