package com.lxk.controller;

import com.lxk.pojo.Items;
import com.lxk.pojo.ItemsImg;
import com.lxk.pojo.ItemsParam;
import com.lxk.pojo.ItemsSpec;
import com.lxk.pojo.vo.ItemInfoVO;
import com.lxk.service.ItemService;
import com.lxk.utils.ResultJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * @author songshiyu
 * @date 2020/6/24 7:40
 **/

@Api(value = "商品接口", tags = "商品相关信息展示的相关接口")
@RestController
@RequestMapping("/items")
public class ItemsController {

    @Autowired
    private ItemService itemService;

    @ApiOperation(value = "查询商品详情", notes = "查询商品详情", httpMethod = "GET")
    @GetMapping("/info/{itemId}")
    public ResultJSONResult info(
            @ApiParam(name = "itemId",value = "商品id",required = true)
            @PathVariable String itemId) {

        if (StringUtils.isBlank(itemId)){
            return ResultJSONResult.errorMsg(null);
        }

        Items item = itemService.queryItemById(itemId);
        List<ItemsImg> itemsImgsList = itemService.queryItemImgList(itemId);
        List<ItemsSpec> itemsSpecList = itemService.queryItemSpecList(itemId);
        ItemsParam itemsParam = itemService.queryItemParam(itemId);

        ItemInfoVO itemInfoVO = new ItemInfoVO(item,itemsImgsList,itemsSpecList,itemsParam);

        return ResultJSONResult.ok(itemInfoVO);
    }
}
