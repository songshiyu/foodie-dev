package com.lxk.controller.center;

import com.lxk.controller.BaseController;
import com.lxk.pojo.Orders;
import com.lxk.pojo.Users;
import com.lxk.pojo.bo.center.CenterUserBO;
import com.lxk.resource.FileUpload;
import com.lxk.service.center.CenterUserService;
import com.lxk.service.center.MyOrdersService;
import com.lxk.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author songshiyu
 * @date 2020/7/4 10:41
 **/
@Api(value = "用户中心我的订单", tags = {"用户中心我的订单"})
@RestController
@RequestMapping("myorders")
public class MyOrdersController extends BaseController {

    @Autowired
    private MyOrdersService myOrdersService;

    @ApiOperation(value = "查询订单列表", notes = "查询订单列表", httpMethod = "POST")
    @PostMapping("/query")
    public ResultJSONResult query(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "orderStatus", value = "订单状态", required = false)
            @RequestParam String orderStatus,
            @ApiParam(name = "page", value = "查询第几页", required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize", value = "每页几条", required = false)
            @RequestParam Integer pageSize) {

        if (StringUtils.isBlank(userId)) {
            return ResultJSONResult.errorMsg(null);
        }

        if (page == null) {
            page = 0;
        }

        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        PagedGridResult gridResult = myOrdersService.queryMyOrders(userId, orderStatus, page, pageSize);
        return ResultJSONResult.ok(gridResult);
    }

    /**
     * 因为没有商家发货功能，因此，此处只是模拟
     */
    @ApiOperation(value = "商家发货", notes = "商家发货", httpMethod = "GET")
    @GetMapping("/deliver")
    public ResultJSONResult deliver(
            @ApiParam(name = "orderId", value = "订单Id", required = true)
            @RequestParam String orderId
    ) {
        if (StringUtils.isNotBlank(orderId)) {
            return ResultJSONResult.errorMsg("订单ID不能为空");
        }
        myOrdersService.updateDeliverOrderStatus(orderId);
        return ResultJSONResult.ok();
    }

    @ApiOperation(value = "用户确认收货", notes = "用户确认收货", httpMethod = "POST")
    @PostMapping("/confirmReceive")
    public ResultJSONResult confirmReceive(
            @ApiParam(name = "orderId", value = "订单Id", required = true)
            @RequestParam String orderId,
            @ApiParam(name = "userId", value = "用户Id", required = true)
            @RequestParam String userId
    ) {
        ResultJSONResult checkResult = checkUserOrder(userId, orderId);
        if (checkResult.getStatus() != HttpStatus.OK.value()) {
            return checkResult;
        }

        boolean result = myOrdersService.updateReceiverOrderStatus(orderId);

        if (!result) {
            return ResultJSONResult.errorMsg("订单确认收货失败！");
        }
        return ResultJSONResult.ok();
    }

    @ApiOperation(value = "用户删除订单", notes = "用户删除订单", httpMethod = "POST")
    @PostMapping("/delete")
    public ResultJSONResult delete(
            @ApiParam(name = "orderId", value = "订单Id", required = true)
            @RequestParam String orderId,
            @ApiParam(name = "userId", value = "用户Id", required = true)
            @RequestParam String userId
    ) {
        ResultJSONResult checkResult = checkUserOrder(userId, orderId);
        if (checkResult.getStatus() != HttpStatus.OK.value()) {
            return checkResult;
        }

        boolean result = myOrdersService.deleteOrder(userId, orderId);
        if (!result){
            return ResultJSONResult.errorMsg("订单删除失败！");
        }

        return ResultJSONResult.ok();
    }
}
