package com.lxk.controller;

import com.lxk.pojo.bo.ShopcartBo;
import com.lxk.utils.JsonUtils;
import com.lxk.utils.RedisOperator;
import com.lxk.utils.ResultJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @author songshiyu
 * @date 2020/6/27 21:43
 **/

@Api(value = "购物车接口controller", tags = {"购物车接口相关api"})
@RestController
@RequestMapping("shopcart")
public class ShopCatController extends BaseController {

    @Autowired
    private RedisOperator redisOperator;

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
        //当前端用户在登录的情况下，添加商品到购物车，会同时同步购物车到redis缓存
        //需要判断当前购物车中已经存在的商品，如果已经存在，则累加数量
        String shopcartJson = redisOperator.get(FOODIE_SHOPCART + ":" + userId);
        List<ShopcartBo> shopcartBoList = null;
        if (StringUtils.isNotBlank(shopcartJson)) {
            //redis中已经有该用户的购物车
            shopcartBoList = JsonUtils.jsonToList(shopcartJson, ShopcartBo.class);
            //判断购物车中是否已有该商品
            boolean isHaving = false;
            for (ShopcartBo sc : shopcartBoList) {
                String tempSpecId = sc.getSpecId();
                if (tempSpecId.equals(shopcartBo.getSpecId())) {
                    sc.setBuyCounts(sc.getBuyCounts() + shopcartBo.getBuyCounts());
                    isHaving = true;
                }
            }
            if (!isHaving) {
                shopcartBoList.add(shopcartBo);
            }
        } else {
            //redis中没有存在该用户的购物车
            shopcartBoList = new ArrayList<>();
            //直接将该商品添加到购物车中
            shopcartBoList.add(shopcartBo);
        }

        //覆盖现有redis中该用户的购物车
        redisOperator.set(FOODIE_SHOPCART + ":" + userId, JsonUtils.objectToJson(shopcartBoList));

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
        logger.info("删除用户：{} 购物车中的商品：{}", userId, itemSpecId);
        //前端用户在登录的情况下，删除购物车中商品，会同时同步删除对应的redis缓存
        List<ShopcartBo> shopcartBoList = null;

        String shopcartJson = redisOperator.get(FOODIE_SHOPCART + ":" + userId);

        if (StringUtils.isNotBlank(shopcartJson)) {
            shopcartBoList = JsonUtils.jsonToList(shopcartJson, ShopcartBo.class);
            for (ShopcartBo sc : shopcartBoList) {
                if (sc.getSpecId().equals(itemSpecId)) {
                    shopcartBoList.remove(sc);
                    break;
                }
            }
        }

        redisOperator.set(FOODIE_SHOPCART + ":" + userId, JsonUtils.objectToJson(shopcartBoList));

        return ResultJSONResult.ok();
    }
}
