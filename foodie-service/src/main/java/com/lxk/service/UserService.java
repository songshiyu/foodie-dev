package com.lxk.service;


import com.lxk.pojo.Users;
import com.lxk.pojo.bo.UserBO;

/**
 * @author songshiyu
 * @date 2020/6/17 21:36
 **/
public interface UserService {

    /**
     * 判断用户名是否存在
     * @param username
     * @return
     */
    public boolean queryUsernameIsExist(String username);

    /**
     * 注冊
     * @param userBO
     * @return User
     * */
    public Users createUser(UserBO userBO);

    /**
     * 检索用户名和密码是否匹配
     * */
    public Users queryUserForLogin(String username,String password);
}
