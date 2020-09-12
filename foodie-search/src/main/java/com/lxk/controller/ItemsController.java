package com.lxk.controller;

import com.lxk.service.ItemESService;
import com.lxk.utils.PagedGridResult;
import com.lxk.utils.ResultJSONResult;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author songshiyu
 * @date 2020/9/12 21:47
 **/
@RestController
@RequestMapping("items")
public class ItemsController {

    @Autowired
    private ItemESService itemESService;

    @GetMapping("/es/search")
    public ResultJSONResult search(
            @ApiParam(name = "keywords", value = "关键字", required = true)
            @RequestParam String keywords,
            @ApiParam(name = "sort", value = "排序", required = false)
            @RequestParam String sort,
            @ApiParam(name = "page", value = "查询第几页", required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize", value = "每页几条", required = false)
            @RequestParam Integer pageSize) {

        if (StringUtils.isBlank(keywords)) {
            return ResultJSONResult.errorMsg(null);
        }

        if (page == null) {
            page = 0;
        }

        if (pageSize == null) {
            pageSize = 20;
        }

        page--;

        PagedGridResult gridResult = itemESService.searchItems(keywords, sort, page, pageSize);
        return ResultJSONResult.ok(gridResult);
    }

}
