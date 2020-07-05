package com.lxk.controller.center;

import com.lxk.controller.BaseController;
import com.lxk.enums.YesOrNo;
import com.lxk.pojo.OrderItems;
import com.lxk.pojo.Orders;
import com.lxk.pojo.bo.center.OrderItemsCommentBO;
import com.lxk.service.center.MyCommentService;
import com.lxk.service.center.MyOrdersService;
import com.lxk.utils.PagedGridResult;
import com.lxk.utils.ResultJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author songshiyu
 * @date 2020/7/5 21:20
 **/
@Api(value = "用户中心-评价模块", tags = {"用户中心-评价模块"})
@RestController
@RequestMapping("mycomments")
public class MyCommentsController extends BaseController {

    @Autowired
    private MyOrdersService myOrdersService;

    @Autowired
    private MyCommentService myCommentService;

    @ApiOperation(value = "查询待评价的订单", notes = "查询待评价的订单", httpMethod = "POST")
    @PostMapping("pending")
    public ResultJSONResult query(
            @ApiParam(name = "用户id", value = "用户id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "订单id", value = "订单id", required = true)
            @RequestParam String orderId) {

        //判断用户与订单是否关联
        ResultJSONResult checkResult = checkUserOrder(userId, orderId);
        if (checkResult.getStatus() != HttpStatus.OK.value()) {
            return checkResult;
        }
        //判断该笔订单是否已经评价过，评价过就不在继续
        Orders orders = (Orders) checkResult.getData();
        if (orders.getIsComment().equals(YesOrNo.YES.type)) {
            return ResultJSONResult.errorMsg("该笔订单已经评价过");
        }

        List<OrderItems> OrderItemsList = myCommentService.queryPendingComment(orderId);

        return ResultJSONResult.ok(OrderItemsList);
    }

    @ApiOperation(value = "保存评价列表", notes = "保存评价列表", httpMethod = "POST")
    @PostMapping("saveList")
    public ResultJSONResult saveList(
            @ApiParam(name = "用户id", value = "用户id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "订单id", value = "订单id", required = true)
            @RequestParam String orderId,
            @RequestBody List<OrderItemsCommentBO> itemsComments) {

        //判断用户与订单是否关联
        ResultJSONResult checkResult = checkUserOrder(userId, orderId);
        if (checkResult.getStatus() != HttpStatus.OK.value()) {
            return checkResult;
        }
       //判断列表是否为空
        if(CollectionUtils.isEmpty(itemsComments)){
            return ResultJSONResult.errorMsg("评价列表为空！");
        }
        myCommentService.saveComments(userId,orderId,itemsComments);
        return ResultJSONResult.ok();
    }

    @ApiOperation(value = "查询我的评价", notes = "查询我的评价", httpMethod = "POST")
    @PostMapping("/query")
    public ResultJSONResult query(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
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

        PagedGridResult gridResult = myCommentService.queryMyComments(userId,page, pageSize);
        return ResultJSONResult.ok(gridResult);
    }
}
