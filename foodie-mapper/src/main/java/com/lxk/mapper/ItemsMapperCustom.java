package com.lxk.mapper;


import com.lxk.pojo.vo.ItemCommentsVO;
import com.lxk.pojo.vo.SearchItemsVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ItemsMapperCustom{

    public List<ItemCommentsVO> queryItemComments(@Param("paramsMap")Map<String,Object> map);

    public List<SearchItemsVO> searchItems(@Param("paramsMap")Map<String,Object> map);

    public List<SearchItemsVO> searchItemsByThirdCat(@Param("paramsMap")Map<String,Object> map);
}