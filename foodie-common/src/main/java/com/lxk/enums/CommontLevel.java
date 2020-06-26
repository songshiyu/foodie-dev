package com.lxk.enums;

/**
 * @author songshiyu
 * @date 2020/6/17 23:00
 *
 *  商品评价等级 枚举
 **/
public enum CommontLevel {

    GOOD(1,"好评"),
    NORMAL(2,"中评"),
    BAD(3,"差评"),
    ;

    public final Integer type;
    public final String value;

    CommontLevel(Integer type, String value){
        this.type = type;
        this.value = value;
    }

}
