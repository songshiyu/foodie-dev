package com.lxk.service;

import com.lxk.pojo.Category;
import com.lxk.pojo.vo.CategoryVO;

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

    /**
     * 根据一级分类id查询子分类信息
     * */
    public List<CategoryVO> getSubCatLIst(Integer rootCatId);
}
