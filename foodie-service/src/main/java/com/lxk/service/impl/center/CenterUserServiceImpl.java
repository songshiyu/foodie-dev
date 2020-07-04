package com.lxk.service.impl.center;

import com.lxk.mapper.UsersMapper;
import com.lxk.pojo.Users;
import com.lxk.pojo.bo.center.CenterUserBO;
import com.lxk.service.center.CenterUserService;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author songshiyu
 * @date 2020/7/4 10:13
 **/
@Service
public class CenterUserServiceImpl implements CenterUserService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserInfo(String userId) {
        Users users = usersMapper.selectByPrimaryKey(userId);
        users.setPassword(null);
        return users;
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = {})
    @Override
    public Users updateUserInfo(String userId, CenterUserBO centerUserBO) {
        Users updateUsers = new Users();
        BeanUtils.copyProperties(centerUserBO,updateUsers);
        updateUsers.setId(userId);
        updateUsers.setUpdatedTime(new Date());

        usersMapper.updateByPrimaryKeySelective(updateUsers);
        return queryUserInfo(userId);
    }
}
