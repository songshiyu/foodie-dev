package com.lxk.service.impl;

import com.lxk.service.StuService;
import com.lxk.service.TransService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author songshiyu
 * @date 2020/6/16 21:41
 **/

@Service
public class TransServiceImpl implements TransService{

    /**
     *  REQUIRED(传播，默认的) :使用当前的事务，如果当前没有事务，则自己新建一个事务，子方法必须运行在一个事务中；
     *            如果当前存在事务，子方法会加入这个事务，称为一个整体
     *
     *  SUPPORTS ：如果当前有事务，则使用事务；如果当前没有事务，则不使用事务。（主要用来做查询）
     *
     *  MANDATORY：该传播属性必须强制存在一个事务，如果不存在，则抛出异常。
     *
     *  REQUIRES_NEW ：如果当前有事务，则挂起该事务，并创建一个新的事务给自己使用。
     *                 如果当前没有事务，则等同于required
     *
     *  NOT_SUPPORTED ：如果当前有事务，则把事务挂起，自己不使用事务运行数据库操作。
     *
     *  NEVER ：如果当前有事务存在，则抛出异常；
     *          如果不存在事务，则正常执行。
     *
     *  NESTED： 如果当前有事务，则开启事务(嵌套事务)，嵌套事务是独立提交或者回滚
     *           如果当前没有事务，则同required。
     *           但是如果主事务提交，则会携带子事务一起提交
     *           如果主事务回滚，则子事务会一起回滚。相反，子事务异常，则父事务可以回滚或不回滚。
     *
     * */

    @Autowired
    private StuService stuService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void testPropagationTrans() {
        stuService.saveParent();

        stuService.saveChlidren();
    }
}
