package com.lxk.service;

import com.lxk.pojo.Category;

import java.util.List;

/**
 * @author songshiyu
 * @date 2020/6/23 8:01
 **/
public interface CategoryService {

    /**
     * 查询所有一级分类
     * */
    public List<Category> queryAllRootLevelcat();
}
