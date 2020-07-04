package com.lxk.service.center;


import com.lxk.pojo.Users;
import com.lxk.pojo.bo.UserBO;
import com.lxk.pojo.bo.center.CenterUserBO;

/**
 * @author songshiyu
 * @date 2020/6/17 21:36
 **/
public interface CenterUserService {

    /**
     * 根据用户id查询用户信息
     * @param userId
     *
     * @return Users
     * */
    public Users queryUserInfo(String userId);

    /**
     * 修改用户信息
     * @param userId
     * @param centerUserBO
     * */
    public Users updateUserInfo(String userId, CenterUserBO centerUserBO);
}
