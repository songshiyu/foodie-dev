package com.lxk.service;


import com.lxk.pojo.UserAddress;
import com.lxk.pojo.bo.AddressBO;

import java.util.List;

/**
 * @author songshiyu
 * @date 2020/6/23 8:01
 **/
public interface AddressService {

    /**
     * 根据用户Id查询用户的收货地址列表
     *
     * @param userId
     * @return
     */
    public List<UserAddress> queryAll(String userId);

    /**
     * 用户新增地址
     *
     * @param addressBO
     */
    public void addNewUserAddress(AddressBO addressBO);

    /**
     * 修改用户地址
     *
     * @param addressBO
     */
    public void updateUserAddress(AddressBO addressBO);

    /**
     * 根据用户Id和地址id，删除对应的地址信息
     *
     * @param userId
     * @param addressId
     */
    public void deleteUseraddress(String userId, String addressId);

    /**
     * 设置默认收货地址
     * @param addressId
     * @param userId
     */
    public void setDefalutUserAddress(String userId, String addressId);

}