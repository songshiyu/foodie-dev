package com.lxk.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lxk.enums.CommontLevel;
import com.lxk.enums.YesOrNo;
import com.lxk.mapper.*;
import com.lxk.pojo.*;
import com.lxk.pojo.vo.CommentLevelCountsVO;
import com.lxk.pojo.vo.ItemCommentsVO;
import com.lxk.pojo.vo.SearchItemsVO;
import com.lxk.pojo.vo.ShopcartVO;
import com.lxk.service.ItemService;
import com.lxk.utils.DesensitizationUtil;
import com.lxk.utils.PagedGridResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * @author songshiyu
 * @date 2020/6/24 7:25
 **/
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemsMapper itemsMapper;

    @Autowired
    private ItemsImgMapper itemsImgMapper;

    @Autowired
    private ItemsSpecMapper itemsSpecMapper;

    @Autowired
    private ItemsParamMapper itemsParamMapper;

    @Autowired
    private ItemsCommentsMapper itemsCommentsMapper;

    @Autowired
    private ItemsMapperCustom itemsMapperCustom;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Items queryItemById(String itemId) {
        return itemsMapper.selectByPrimaryKey(itemId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public List<ItemsImg> queryItemImgList(String itemId) {

        Example example = new Example(ItemsParam.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("itemId", itemId);

        return itemsImgMapper.selectByExample(example);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ItemsSpec> queryItemSpecList(String itemId) {
        Example example = new Example(ItemsSpec.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("itemId", itemId);

        return itemsSpecMapper.selectByExample(example);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public ItemsParam queryItemParam(String itemId) {
        Example example = new Example(ItemsParam.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("itemId", itemId);
        return itemsParamMapper.selectOneByExample(example);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public CommentLevelCountsVO queryCommentCounts(String itemId) {
        Integer goodCount = getCommentsCount(itemId, CommontLevel.GOOD.type);
        Integer normalCount = getCommentsCount(itemId, CommontLevel.NORMAL.type);
        Integer badCount = getCommentsCount(itemId, CommontLevel.BAD.type);

        int totalCounts = goodCount + normalCount + badCount;

        CommentLevelCountsVO countsVO = new CommentLevelCountsVO(totalCounts, goodCount, normalCount, badCount);

        return countsVO;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public PagedGridResult queryPagedComments(String itemId, Integer level, Integer page, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("itemId", itemId);
        map.put("level", level);

        //mybatis-pagehelper
        /**
         * page:第几页
         * pageSize：每页显示条数
         * */
        PageHelper.startPage(page, pageSize);
        List<ItemCommentsVO> itemCommentsVoList = itemsMapperCustom.queryItemComments(map);
        for (ItemCommentsVO itemCommentsVO : itemCommentsVoList) {
            itemCommentsVO.setNickname(DesensitizationUtil.commonDisplay(itemCommentsVO.getNickname()));
        }

        PagedGridResult gridResult = setterPagedgrid(itemCommentsVoList, page);
        return gridResult;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult searchItems(String keywords, String sort, Integer page, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("keywords", keywords);
        map.put("sort", sort);

        PageHelper.startPage(page, pageSize);
        List<SearchItemsVO> list = itemsMapperCustom.searchItems(map);

        return setterPagedgrid(list, page);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult searchItems(Integer catId, String sort, Integer page, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("catId", catId);
        map.put("sort", sort);

        PageHelper.startPage(page, pageSize);
        List<SearchItemsVO> list = itemsMapperCustom.searchItemsByThirdCat(map);

        return setterPagedgrid(list, page);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ShopcartVO> queryItemsBySpecIds(String specIds) {
        String[] ids = specIds.split("\\,");
        //List<String> specIdsList = Arrays.asList(ids);
        List<String> specIdsList = new ArrayList<>();
        Collections.addAll(specIdsList, ids);

        return itemsMapperCustom.queryItemsBySpecIds(specIdsList);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public ItemsSpec queryItemSpecById(String specId) {
        return itemsSpecMapper.selectByPrimaryKey(specId);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public String queryItemMainImgById(String itemId) {
        ItemsImg itemsImg = new ItemsImg();
        itemsImg.setItemId(itemId);
        itemsImg.setIsMain(YesOrNo.YES.type);

        ItemsImg result = itemsImgMapper.selectOne(itemsImg);

        return result != null ? result.getUrl() : "";
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = {})
    @Override
    public void decreaseItemSpecStock(String specId, int buyCount) {
        /**
         * 线程安全：
         *  1.synchronized 不推荐使用，集群下无用，性能低下
         *  2.锁数据库：不推荐，导致数据库性能低下
         *  3.分布式锁 zookeeper redis
         * */

        //1.TODO 查询库存,由于没有redis，暂时定为2
        int stock = 2;

        //2.判断库存，是否能够减少到0以下，此处会涉及到多线程问题，超卖
        if (stock - buyCount < 0){
            //提示用于库存不够
            throw new RuntimeException("订单创建失败，原因：库存不足");
        }
        int result = itemsMapperCustom.decreaseItemSpecStock(specId, buyCount);
        if (result != 1){
            throw new RuntimeException("订单创建失败，原因：库存不足");
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    Integer getCommentsCount(String itemId, Integer level) {
        ItemsComments condition = new ItemsComments();
        condition.setItemId(itemId);

        if (level != null) {
            condition.setCommentLevel(level);
        }
        return itemsCommentsMapper.selectCount(condition);
    }

    private PagedGridResult setterPagedgrid(List<?> list, Integer page) {
        PageInfo<?> pageList = new PageInfo<>(list);
        PagedGridResult gridResult = new PagedGridResult();
        gridResult.setPage(page);
        gridResult.setRows(list);
        gridResult.setTotal(pageList.getPages());
        gridResult.setRecords(pageList.getTotal());

        return gridResult;
    }
}
