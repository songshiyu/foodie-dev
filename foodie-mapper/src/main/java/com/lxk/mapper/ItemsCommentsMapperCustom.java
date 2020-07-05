package com.lxk.mapper;

import com.lxk.my.mapper.MyMapper;
import com.lxk.pojo.ItemsComments;
import com.lxk.pojo.vo.MyCommentVO;
import io.swagger.annotations.ApiParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ItemsCommentsMapperCustom extends MyMapper<ItemsComments> {

    public void saveComments(@Param("paramsMap") Map<String,Object> map);

    public List<MyCommentVO> queryMyComments(@Param("paramsMap") Map<String,Object> map);
}