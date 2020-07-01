package com.lxk.controller;

import com.lxk.enums.OrderStatusEnum;
import com.lxk.pojo.bo.SubmitOrderBO;
import com.lxk.enums.PayMethod;
import com.lxk.pojo.vo.MerchantOrdersVO;
import com.lxk.pojo.vo.OrderVO;
import com.lxk.service.OrderService;
import com.lxk.utils.CookieUtils;
import com.lxk.utils.ResultJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author songshiyu
 * @date 2020/6/28 22:25
 **/

@Api(value = "订单相关接口", tags = {"订单相关接口api"})
@RestController
@RequestMapping("/orders")
public class OrdersController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(OrdersController.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private RestTemplate restTemplate;

    @ApiOperation(value = "用户下单", notes = "用户下单", httpMethod = "POST")
    @PostMapping("/create")
    public ResultJSONResult create(@RequestBody SubmitOrderBO submitOrderBO,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        //1.创建订单
        if (!submitOrderBO.getPayMethod().equals(PayMethod.WEIXIN.type)
                || !submitOrderBO.getPayMethod().equals(PayMethod.ALIPAY.type)) {
            return ResultJSONResult.errorMsg("不支持的支付方式~");
        }
        logger.info(submitOrderBO.toString());
        OrderVO orderVO = orderService.createOrder(submitOrderBO);

        //2.创建订单以后，移除购物车中已结算(已提交)的商品
        //TODO 整合redis后，完善购物车中的已结算商品清除，并且同步到前端的cookie
        CookieUtils.setCookie(request, response, FOODIE_SHOPCART, "", true);

        // 3.向支付中心发送当前订单，用于保存支付中心的订单数据
        MerchantOrdersVO merchantOrdersVO = orderVO.getMerchantOrdersVO();
        merchantOrdersVO.setReturnUrl(payReturnUrl);
        //为了方便测试购买，所有的支付金额统一改为1分钱
        merchantOrdersVO.setAmount(1);

        //构建HttpHeader
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add("'imoocUserId'",null);
        httpHeaders.add("password",null);

        HttpEntity<MerchantOrdersVO> entity = new HttpEntity<>(merchantOrdersVO,httpHeaders);
        ResponseEntity<ResultJSONResult> responseEntity =
                restTemplate.postForEntity(paymentUrl, entity, ResultJSONResult.class);

        ResultJSONResult paymentResult = responseEntity.getBody();
        if (paymentResult.getStatus() != HttpStatus.OK.value()){
            return ResultJSONResult.errorMsg("支付中心订单创建失败，请联系管理员");
        }

        return ResultJSONResult.ok(orderVO.getOrderId());
    }

    /**
     * 回调支付通知的地址
     */
    @PostMapping("notifyMerchantOrderPaid")
    public Integer notifyMerchantOrderPaid(String merchantOrderId) {
        orderService.updateOrderStatus(merchantOrderId, OrderStatusEnum.WAIT_DELIVER.type);
        return HttpStatus.OK.value();
    }

}
