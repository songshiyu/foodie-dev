package com.lxk.controller;

import com.lxk.pojo.bo.UserBO;
import com.lxk.service.UserService;
import com.lxk.utils.ResultJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author songshiyu
 * @date 2020/6/17 21:43
 **/

@Api(value = "注册登录",tags = "用于登录注册的接口")
@RestController
@RequestMapping("passport")
public class PassportController {

    @Autowired
    private UserService userService;

    private static final int PASSWORD_LENGTH = 6;

    @ApiOperation(value = "用户名是否存在",notes = "用户名是否存在",httpMethod = "GET")
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


    @ApiOperation(value = "用户注册",notes = "用户注册",httpMethod = "POST")
    @PostMapping("/regist")
    public ResultJSONResult regist(@RequestBody UserBO userBO){

        String username = userBO.getUsername();
        String password = userBO.getPassword();
        String confirmPassword = userBO.getConfirmPassword();

        //0.判断用户名和密码必须不为空
        if (StringUtils.isBlank(username) ||
                StringUtils.isBlank(password) ||
                StringUtils.isBlank(confirmPassword)){
            return ResultJSONResult.errorMsg("用户名或密码不能为空");
        }

        //1.查询用户是否存在
        boolean isExist = userService.queryUsernameIsExist(username);
        if (isExist){
            return ResultJSONResult.errorMsg("用户名已经存在");
        }

        //2.密码长度不能少于6位
        if (password.length() < PASSWORD_LENGTH){
            return ResultJSONResult.errorMsg("密码长度不能小于6");
        }

        //3.判断两次密码是否一致
        if (!password.equals(confirmPassword)){
            return ResultJSONResult.errorMsg("两次密码输入不一致，请重新输入");
        }

        //4.实现注册
        userService.createUser(userBO);
        return ResultJSONResult.ok();
    }


}
