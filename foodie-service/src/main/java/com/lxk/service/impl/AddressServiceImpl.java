package com.lxk.service.impl;

import com.lxk.enums.YesOrNo;
import com.lxk.mapper.UserAddressMapper;
import com.lxk.pojo.UserAddress;
import com.lxk.pojo.bo.AddressBO;
import com.lxk.service.AddressService;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * @author songshiyu
 * @date 2020/6/29 21:01
 **/

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<UserAddress> queryAll(String userId) {

        UserAddress ua = new UserAddress();
        ua.setUserId(userId);
        return userAddressMapper.select(ua);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {})
    @Override
    public void addNewUserAddress(AddressBO addressBO) {
        //1.判断当前用户是否存在地址，如果没有，则新增为"默认地址"
        Integer isDefault = 0;
        List<UserAddress> addressList = this.queryAll(addressBO.getUserId());
        if (CollectionUtils.isEmpty(addressList) || addressList.size() == 0) {
            isDefault = 1;
        }

        String addressId = sid.nextShort();

        //保存地址到数据库
        UserAddress userAddress = new UserAddress();
        BeanUtils.copyProperties(addressBO, userAddress);

        userAddress.setId(addressId);
        userAddress.setIsDefault(isDefault);
        userAddress.setCreatedTime(new Date());
        userAddress.setUpdatedTime(new Date());

        userAddressMapper.insert(userAddress);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {})
    @Override
    public void updateUserAddress(AddressBO addressBO) {

        String addressId = addressBO.getAddressId();

        UserAddress pendingUserAddress = new UserAddress();
        BeanUtils.copyProperties(addressBO, pendingUserAddress);

        pendingUserAddress.setId(addressId);
        pendingUserAddress.setUpdatedTime(new Date());

        userAddressMapper.updateByPrimaryKeySelective(pendingUserAddress);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {})
    @Override
    public void deleteUseraddress(String userId, String addressId) {
        UserAddress address = new UserAddress();
        address.setId(addressId);
        address.setUserId(userId);
        userAddressMapper.delete(address);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {})
    @Override
    public void setDefalutUserAddress(String userId, String addressId) {
        //1.查找默认地址，修改为不默认
        UserAddress queryAddress = new UserAddress();
        queryAddress.setUserId(userId);
        queryAddress.setIsDefault(YesOrNo.YES.type);
        List<UserAddress> queryAddressList = userAddressMapper.select(queryAddress);
        for (UserAddress ua:queryAddressList){
            ua.setIsDefault(YesOrNo.NO.type);
            userAddressMapper.updateByPrimaryKeySelective(ua);
        }
        //2.根据地址id修改为默认地址
        UserAddress defaultAddress = new UserAddress();
        defaultAddress.setUserId(userId);
        defaultAddress.setId(addressId);
        defaultAddress.setIsDefault(YesOrNo.YES.type);
        userAddressMapper.updateByPrimaryKeySelective(defaultAddress);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public UserAddress queryUserAddress(String userId, String addressId) {
        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);
        userAddress.setId(addressId);
        return userAddressMapper.selectByPrimaryKey(userAddress);
    }
}
