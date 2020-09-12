package com.lxk.service;

import com.lxk.utils.PagedGridResult;

/**
 * @author songshiyu
 * @date 2020/9/12 21:42
 **/
public interface ItemESService {

    /**
     * @param keyword
     * @param sort
     * @param page
     * @param pageSize
     * */
    public PagedGridResult searchItems(
            String keyword,
            String sort,
            Integer page,
            Integer pageSize);


}
