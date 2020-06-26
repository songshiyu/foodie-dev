package com.lxk.pojo.vo;


import com.lxk.pojo.Items;
import com.lxk.pojo.ItemsImg;
import com.lxk.pojo.ItemsParam;
import com.lxk.pojo.ItemsSpec;

import java.util.List;

/**
 * @author songshiyu
 * @date 2020/6/24 6:56
 *
 * 商品详情vo
 **/
public class ItemInfoVO {

    private Items item;
    private List<ItemsImg> itemImgsList;
    private List<ItemsSpec> itemSpecList;
    private ItemsParam itemParams;

    public ItemInfoVO(Items item, List<ItemsImg> itemImgsList, List<ItemsSpec> itemSpecList, ItemsParam itemParams) {
        this.item = item;
        this.itemImgsList = itemImgsList;
        this.itemSpecList = itemSpecList;
        this.itemParams = itemParams;
    }

    public Items getItem() {
        return item;
    }

    public void setItem(Items item) {
        this.item = item;
    }

    public List<ItemsImg> getItemImgsList() {
        return itemImgsList;
    }

    public void setItemImgsList(List<ItemsImg> itemImgsList) {
        this.itemImgsList = itemImgsList;
    }

    public List<ItemsSpec> getItemSpecList() {
        return itemSpecList;
    }

    public void setItemSpecList(List<ItemsSpec> itemSpecList) {
        this.itemSpecList = itemSpecList;
    }

    public ItemsParam getItemParams() {
        return itemParams;
    }

    public void setItemParams(ItemsParam itemParams) {
        this.itemParams = itemParams;
    }
}
