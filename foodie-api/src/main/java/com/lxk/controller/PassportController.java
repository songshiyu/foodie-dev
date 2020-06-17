package com.lxk.controller;

import com.lxk.service.UserService;
import com.lxk.utils.ResultJSONResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author songshiyu
 * @date 2020/6/17 21:43
 **/

@RestController
@RequestMapping("passport")
public class PassportController {

    @Autowired
    private UserService userService;

    @GetMapping("/usernameIsExist")
    public ResultJSONResult usernameIsExist(@RequestParam String username) {
        //1.判断用户名是否为空
        if (StringUtils.isBlank(username)) {
            return ResultJSONResult.errorMsg("用户名不能为空");
        }
        //2.查找注册的用户名是否存在
        boolean isExist = userService.queryUsernameIsExist(username);
        if (isExist) {
            return ResultJSONResult.errorMsg("用户名已存在");
        }
        //3.请求成功，用户名没有重复
        return ResultJSONResult.ok();
    }

}
