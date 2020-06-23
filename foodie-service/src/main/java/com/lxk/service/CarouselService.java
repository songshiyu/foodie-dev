package com.lxk.service;

import com.lxk.pojo.Carousel;

import java.util.List;

/**
 * @author songshiyu
 * @date 2020/6/23 8:01
 **/
public interface CarouselService {

    /**
     * 查询所有轮播图列表
     * */
    public List<Carousel> queryAll(Integer isShow);
}
