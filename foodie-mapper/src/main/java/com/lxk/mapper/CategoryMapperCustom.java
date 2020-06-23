package com.lxk.mapper;


import com.lxk.pojo.vo.CategoryVO;
import com.lxk.pojo.vo.NewItemsVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 自定义mapper
 * */
public interface CategoryMapperCustom {

    public List<CategoryVO> getSubCatList(Integer rootCatId);

    public List<NewItemsVO> getSixNewItemslazy(@Param("paramsMap") Map<String,Object> map);
}