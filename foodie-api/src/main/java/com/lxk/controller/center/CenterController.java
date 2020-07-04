package com.lxk.controller.center;

import com.lxk.pojo.Users;
import com.lxk.service.center.CenterUserService;
import com.lxk.utils.ResultJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author songshiyu
 * @date 2020/7/4 10:09
 **/

@Api(value = "用户中心",tags = {"用户中心的相关接口"})
@RestController
@RequestMapping("center")
public class CenterController {

    @Autowired
    private CenterUserService centerUserService;

    @ApiOperation(value = "获取用户信息",notes = "获取用户信息",httpMethod = "GET")
    @GetMapping("userInfo")
    public ResultJSONResult userInfo(
            @ApiParam(name = "userId",value = "用户id",required = true)
            @RequestParam String userId){
        Users users = centerUserService.queryUserInfo(userId);
        return ResultJSONResult.ok(users);
    }
}
