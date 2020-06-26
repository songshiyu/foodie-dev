package com.lxk.controller;

import com.lxk.pojo.Items;
import com.lxk.pojo.ItemsImg;
import com.lxk.pojo.ItemsParam;
import com.lxk.pojo.ItemsSpec;
import com.lxk.pojo.vo.CommentLevelCountsVO;
import com.lxk.pojo.vo.ItemInfoVO;
import com.lxk.service.ItemService;
import com.lxk.utils.PagedGridResult;
import com.lxk.utils.ResultJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author songshiyu
 * @date 2020/6/24 7:40
 **/

@Api(value = "商品接口", tags = "商品相关信息展示的相关接口")
@RestController
@RequestMapping("/items")
public class ItemsController extends BaseController{

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


    @ApiOperation(value = "查询商品评价等级", notes = "查询商品评价等级", httpMethod = "GET")
    @GetMapping("/commentLevel")
    public ResultJSONResult commentLevel(
            @ApiParam(name = "itemId",value = "商品id",required = true)
            @RequestParam String itemId) {

        if (StringUtils.isBlank(itemId)){
            return ResultJSONResult.errorMsg(null);
        }

        CommentLevelCountsVO countsVO = itemService.queryCommentCounts(itemId);
        return ResultJSONResult.ok(countsVO);
    }

    @ApiOperation(value = "查询商品评论", notes = "查询商品评论", httpMethod = "GET")
    @GetMapping("/comments")
    public ResultJSONResult comments(
            @ApiParam(name = "itemId",value = "商品id",required = true)
            @RequestParam String itemId,
            @ApiParam(name = "level",value = "评价等级",required = false)
            @RequestParam Integer level,
            @ApiParam(name = "page",value = "查询第几页",required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize",value = "每页几条",required = false)
            @RequestParam Integer pageSize) {

        if (StringUtils.isBlank(itemId)){
            return ResultJSONResult.errorMsg(null);
        }

        if (page == null){
            page = 0;
        }

        if (pageSize == null){
            pageSize = COMMENT_PAGE_SIZE;
        }

        PagedGridResult gridResult = itemService.queryPagedComments(itemId, level, page, pageSize);
        return ResultJSONResult.ok(gridResult);
    }
}
