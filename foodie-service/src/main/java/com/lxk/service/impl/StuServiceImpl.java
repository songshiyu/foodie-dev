package com.lxk.service.impl;

import com.lxk.mapper.StuMapper;
import com.lxk.pojo.Stu;
import com.lxk.service.StuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * 测试
 * @author songshiyu
 * @date 2020/6/15 22:03
 **/

@Service
public class StuServiceImpl implements StuService {

    @Autowired
    private StuMapper stuMapper;

    /**仅仅提供事物的支持*/
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Stu getStuInfo(int id) {
        return stuMapper.selectByPrimaryKey(id);
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = {})
    @Override
    public void saveStu() {
        Stu stu = new Stu();
        stu.setName("宋时雨");
        stu.setAge(19);
        stuMapper.insert(stu);
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = {})
    @Override
    public void updateStu(int id) {
        Stu stu = new Stu();
        stu.setId(id);
        stu.setName("宋时雨");
        stu.setAge(20);
        stuMapper.updateByPrimaryKey(stu);
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = {})
    @Override
    public void deleteStu(int id) {
        stuMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void saveParent() {
        Stu stu = new Stu();
        stu.setName("parent");
        stu.setAge(49);
        stuMapper.insert(stu);
    }

    @Override
    //@Transactional(propagation = Propagation.MANDATORY)
    public void saveChlidren() {
        saveChild1();
        int num = 1 / 0;
        saveChild2();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveChild1(){
        Stu stu = new Stu();
        stu.setName("child1");
        stu.setAge(19);
        stuMapper.insert(stu);
    }

    public void saveChild2(){
        Stu stu = new Stu();
        stu.setName("child2");
        stu.setAge(19);
        stuMapper.insert(stu);
    }
}
