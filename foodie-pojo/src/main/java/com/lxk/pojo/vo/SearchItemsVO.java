package com.lxk.pojo.vo;


/**
 * @author songshiyu
 * @date 2020/6/26 21:07
 *
 * 用于展示商品搜索列表的vo
 **/
public class SearchItemsVO {

    private String itemId;
    private String itemName;
    private Integer sellCounts;
    private String imgUrl;
    private Integer price;


    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getSellCounts() {
        return sellCounts;
    }

    public void setSellCounts(Integer sellCounts) {
        this.sellCounts = sellCounts;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}
