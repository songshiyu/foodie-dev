package com.lxk.service.impl;

import com.lxk.mapper.CarouselMapper;
import com.lxk.pojo.Carousel;
import com.lxk.service.CarouselService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author songshiyu
 * @date 2020/6/23 8:03
 **/
@Service
public class CarouselServiceImpl implements CarouselService {

    @Autowired
    private CarouselMapper carouselMapper;

    @Override
    public List<Carousel> queryAll(Integer isShow) {

        Example example = new Example(Carousel.class);
        //查询结果进行排序
        example.orderBy("sort").asc();
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isShow",isShow);

        List<Carousel> carouselList = carouselMapper.selectByExample(example);
        return carouselList;
    }
}
