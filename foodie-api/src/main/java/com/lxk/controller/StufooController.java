package com.lxk.controller;

import com.lxk.service.StuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author songshiyu
 * @date 2020/6/14 17:59
 **/

@ApiIgnore
@RestController
public class StufooController {

    @Autowired
    private StuService stuService;

    @GetMapping("/getStu")
    public Object hello(Integer id) {
        return stuService.getStuInfo(id);
    }

    @PostMapping("/saveStu")
    public Object saveStu(){
        stuService.saveStu();
        return "ok";
    }

    @PostMapping("/updateStu")
    public Object updateStu(Integer id){
        stuService.updateStu(id);
        return  stuService.getStuInfo(id);
    }

    @PostMapping("/deleteStu")
    public Object deleteStu(Integer id){
        stuService.deleteStu(id);
        return "ok";
    }
}
