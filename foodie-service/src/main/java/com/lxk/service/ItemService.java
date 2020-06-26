package com.lxk.service;

import com.lxk.pojo.Items;
import com.lxk.pojo.ItemsImg;
import com.lxk.pojo.ItemsParam;
import com.lxk.pojo.ItemsSpec;
import com.lxk.pojo.vo.CommentLevelCountsVO;

import java.util.List;

/**
 * @author songshiyu
 * @date 2020/6/24 7:25
 **/
public interface ItemService {

    /**
     * 根据商品id查询
     * */
    public Items queryItemById(String itemId);

    /**
     * 根据商品id查询商品图片列表
     * */
    public List<ItemsImg> queryItemImgList(String itemId);

    /**
     *  根据商品id查询商品规格
     * */
    public List<ItemsSpec> queryItemSpecList(String itemId);

    /**
     * 根据商品id查询商品参数
     * */
    public ItemsParam queryItemParam(String itemId);

    /**
     * 根据商品id查询商品的评价等级数量
     * */
    public CommentLevelCountsVO queryCommentCounts(String itemId);
}
