package com.test;

import com.lxk.Application;
import com.lxk.service.TransService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author songshiyu
 * @date 2020/6/16 21:32
 **/

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class TransTest {

    @Autowired
    private TransService transService;

    @Test
    public void myTest(){
        transService.testPropagationTrans();
    }
}
