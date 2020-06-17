package com.lxk.service.impl;

import com.lxk.enums.Sex;
import com.lxk.mapper.UsersMapper;
import com.lxk.pojo.Users;
import com.lxk.pojo.bo.UserBO;
import com.lxk.service.UserService;
import com.lxk.utils.DateUtil;
import com.lxk.utils.MD5Utils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * @author songshiyu
 * @date 2020/6/17 21:37
 **/
@Service
public class UserServiceImpl implements UserService {

    private static final String USER_FACE = "";

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private Sid sid;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean queryUsernameIsExist(String username) {

        Example userExample = new Example(Users.class);
        Example.Criteria userCriteria = userExample.createCriteria();

        userCriteria.andEqualTo("username", username);

        Users result = usersMapper.selectOneByExample(userExample);

        return result == null ? false : true;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Users createUser(UserBO userBO) {
        String userId = sid.nextShort();

        Users users = new Users();
        users.setId(userId);
        try {
            users.setUsername(userBO.getUsername());
            users.setPassword(MD5Utils.getMD5Str(userBO.getPassword()));
            users.setNickname(userBO.getUsername());
            /**设置默认头像*/
            users.setFace(USER_FACE);
            users.setBirthday(DateUtil.stringToDate("1900-01-01"));

            /**默认性别为保密*/
            users.setSex(Sex.secret.type);
            users.setCreatedTime(new Date());
            users.setUpdatedTime(new Date());

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        usersMapper.insert(users);

        return users;
    }
}
