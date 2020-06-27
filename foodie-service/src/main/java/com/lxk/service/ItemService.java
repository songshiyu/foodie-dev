package com.lxk.service;

import com.lxk.pojo.Items;
import com.lxk.pojo.ItemsImg;
import com.lxk.pojo.ItemsParam;
import com.lxk.pojo.ItemsSpec;
import com.lxk.pojo.vo.CommentLevelCountsVO;
import com.lxk.utils.PagedGridResult;

import java.util.List;

/**
 * @author songshiyu
 * @date 2020/6/24 7:25
 **/
public interface ItemService {

    /**
     * 根据商品id查询
     * @param itemId
     *
     * @return
     * */
    public Items queryItemById(String itemId);

    /**
     * 根据商品id查询商品图片列表
     * @param itemId
     *
     * @return
     * */
    public List<ItemsImg> queryItemImgList(String itemId);

    /**
     *  根据商品id查询商品规格
     *  @param itemId
     *
     *  @return
     * */
    public List<ItemsSpec> queryItemSpecList(String itemId);

    /**
     * 根据商品id查询商品参数
     * @param itemId
     *
     * @return
     * */
    public ItemsParam queryItemParam(String itemId);

    /**
     * 根据商品id查询商品的评价等级数量
     * @param itemId
     * @return
     * */
    public CommentLevelCountsVO queryCommentCounts(String itemId);

    /**
     *  根据商品id查询商品评价（带分页的）
     *  @param itemId
     *  @param level
     *  @param page
     *  @param pageSize
     *
     *  @return
     * */
    public PagedGridResult queryPagedComments(String itemId, Integer level, Integer page, Integer pageSize);

    /**
     * 搜索商品列表
     * @param keywords
     * @param sort
     * @param page
     * @param pageSize
     *
     * @return
     * */
    public PagedGridResult searchItems(String keywords, String sort, Integer page, Integer pageSize);

    /**
     * 根据分类id搜索商品列表
     * @param catId
     * @param sort
     * @param page
     * @param pageSize
     *
     * @return
     * */
    public PagedGridResult searchItems(Integer catId, String sort, Integer page, Integer pageSize);
}
