package com.lxk.controller;

import com.lxk.pojo.bo.ShopcartBo;
import com.lxk.utils.ResultJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author songshiyu
 * @date 2020/6/27 21:43
 **/

@Api(value = "购物车接口controller", tags = {"购物车接口相关api"})
@RestController
@RequestMapping("shopcart")
public class ShopCatController {

    private static final Logger logger = LoggerFactory.getLogger(ShopCatController.class);

    @PostMapping("/add")
    @ApiOperation(value = "添加商品到购物车", notes = "添加商品到购物车", httpMethod = "POST")
    public ResultJSONResult add(
            @RequestParam String userId,
            @RequestBody ShopcartBo shopcartBo,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        if (StringUtils.isBlank(userId)) {
            return ResultJSONResult.errorMsg("");
        }
        logger.info(shopcartBo.toString());
        //TODO 前端用户在登录的情况下，添加商品到购物车，会同时同步购物车到redis缓存

        return ResultJSONResult.ok();
    }

    @PostMapping("/del")
    @ApiOperation(value = "从购物车中删除商品", notes = "从购物车中删除商品", httpMethod = "POST")
    public ResultJSONResult del(
            @RequestParam String userId,
            @RequestParam String itemSpecId,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(itemSpecId)) {
            return ResultJSONResult.errorMsg("未选中商品");
        }
        logger.info("删除用户：{} 购物车中的商品：{}",userId,itemSpecId);
        //TODO 前端用户在登录的情况下，删除购物车中商品，会同时同步删除对应的redis缓存
        return ResultJSONResult.ok();
    }
}
