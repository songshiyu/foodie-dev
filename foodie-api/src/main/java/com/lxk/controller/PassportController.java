package com.lxk.controller;

import com.lxk.pojo.Users;
import com.lxk.pojo.bo.UserBO;
import com.lxk.service.UserService;
import com.lxk.utils.CookieUtils;
import com.lxk.utils.JsonUtils;
import com.lxk.utils.MD5Utils;
import com.lxk.utils.ResultJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.NoSuchAlgorithmException;

/**
 * @author songshiyu
 * @date 2020/6/17 21:43
 **/

@Api(value = "注册登录", tags = "用于登录注册的接口")
@RestController
@RequestMapping("passport")
public class PassportController {

    @Autowired
    private UserService userService;

    private static final int PASSWORD_LENGTH = 6;

    @ApiOperation(value = "用户名是否存在", notes = "用户名是否存在", httpMethod = "GET")
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


    @ApiOperation(value = "用户登录", notes = "用户登录", httpMethod = "POST")
    @PostMapping("/login")
    public ResultJSONResult login(@RequestBody UserBO userBO,
                                  HttpServletRequest request,
                                  HttpServletResponse response) throws NoSuchAlgorithmException {

        String username = userBO.getUsername();
        String password = userBO.getPassword();

        //0.判断用户名和密码必须不为空
        if (StringUtils.isBlank(username) ||
                StringUtils.isBlank(password)) {
            return ResultJSONResult.errorMsg("用户名或密码不能为空");
        }

        //1.实现登录
        Users usersResult = userService.queryUserForLogin(username, MD5Utils.getMD5Str(password));

        if (usersResult == null) {
            return ResultJSONResult.errorMsg("用户名或密码不正确");
        }
        usersResult = setNullProperty(usersResult);

        //设置cookie
        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(usersResult),true);

        //TODO 生成用户token，存入redis会话
        //TODO 同步购物车数据
        return ResultJSONResult.ok(usersResult);
    }

    @ApiOperation(value = "用户注册", notes = "用户注册", httpMethod = "POST")
    @PostMapping("/regist")
    public ResultJSONResult regist(@RequestBody UserBO userBO,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {

        String username = userBO.getUsername();
        String password = userBO.getPassword();
        String confirmPassword = userBO.getConfirmPassword();

        //0.判断用户名和密码必须不为空
        if (StringUtils.isBlank(username) ||
                StringUtils.isBlank(password) ||
                StringUtils.isBlank(confirmPassword)) {
            return ResultJSONResult.errorMsg("用户名或密码不能为空");
        }

        //1.查询用户是否存在
        boolean isExist = userService.queryUsernameIsExist(username);
        if (isExist) {
            return ResultJSONResult.errorMsg("用户名已经存在");
        }

        //2.密码长度不能少于6位
        if (password.length() < PASSWORD_LENGTH) {
            return ResultJSONResult.errorMsg("密码长度不能小于6");
        }

        //3.判断两次密码是否一致
        if (!password.equals(confirmPassword)) {
            return ResultJSONResult.errorMsg("两次密码输入不一致，请重新输入");
        }

        //4.实现注册
        Users usersResult = userService.createUser(userBO);
        usersResult = setNullProperty(usersResult);

        //设置cookie
        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(usersResult),true);
        //TODO 生成用户token，存入redis会话
        //TODO 同步购物车数据

        return ResultJSONResult.ok();
    }

    private Users setNullProperty(Users usersResult) {
        usersResult.setPassword(null);
        usersResult.setEmail(null);
        usersResult.setRealname(null);
        usersResult.setMobile(null);
        usersResult.setUpdatedTime(null);
        usersResult.setCreatedTime(null);
        usersResult.setBirthday(null);

        return usersResult;
    }

    @ApiOperation(value = "用户退出登录", notes = "用户退出登录", httpMethod = "POST")
    @PostMapping("/logout")
    public ResultJSONResult logout(@RequestParam String userId,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {

        //清除用户相关信息的cookie
        CookieUtils.deleteCookie(request,response,"user");

        //TODO 用户退出登录，需要清空购物车

        //TODO 分布式会话中需要清除用户数据
        return ResultJSONResult.ok();
    }

}
