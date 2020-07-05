package com.lxk.service.impl;

import com.github.pagehelper.PageInfo;
import com.lxk.utils.PagedGridResult;

import java.util.List;

/**
 * @author songshiyu
 * @date 2020/7/5 22:42
 **/
public class BaseService {

    public PagedGridResult setterPagedgrid(List<?> list, Integer page) {
        PageInfo<?> pageList = new PageInfo<>(list);
        PagedGridResult gridResult = new PagedGridResult();
        gridResult.setPage(page);
        gridResult.setRows(list);
        gridResult.setTotal(pageList.getPages());
        gridResult.setRecords(pageList.getTotal());

        return gridResult;
    }
}
