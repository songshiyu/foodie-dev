package com.lxk.controller;

import com.lxk.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author songshiyu
 * @date 2020/8/22 20:35
 **/
@ApiIgnore
@RestController
@RequestMapping("/redis")
public class RedisController {

    @Autowired
    private RedisOperator redisOperator;

    @GetMapping("/set")
    public String set(String key,String value) {
        redisOperator.set(key,value);
        return "ok";
    }

    @GetMapping("/get")
    public String get(String key) {
        String value = redisOperator.get(key);
        return value;
    }

    @GetMapping("/delete")
    public String delete(String key) {
        redisOperator.del(key);
        return "ok";
    }
}
