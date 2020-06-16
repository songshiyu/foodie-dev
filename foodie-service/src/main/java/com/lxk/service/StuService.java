package com.lxk.service;

import com.lxk.pojo.Stu;

/**
 * @author songshiyu
 * @date 2020/6/15 21:59
 **/
public interface StuService {

    public Stu getStuInfo(int id);

    public void saveStu();

    public void updateStu(int id);

    public void deleteStu(int id);

    public void saveParent();

    public void saveChlidren();

}
