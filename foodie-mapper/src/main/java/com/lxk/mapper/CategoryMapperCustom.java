package com.lxk.mapper;


import com.lxk.pojo.vo.CategoryVO;

import java.util.List;

/**
 * 自定义mapper
 * */
public interface CategoryMapperCustom {

    public List<CategoryVO> getSubCatList(Integer rootCatId);
}