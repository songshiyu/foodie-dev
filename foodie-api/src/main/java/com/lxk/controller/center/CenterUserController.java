package com.lxk.controller.center;

import com.lxk.pojo.Users;
import com.lxk.pojo.bo.center.CenterUserBO;
import com.lxk.service.center.CenterUserService;
import com.lxk.utils.CookieUtils;
import com.lxk.utils.JsonUtils;
import com.lxk.utils.ResultJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author songshiyu
 * @date 2020/7/4 10:41
 **/
@Api(value = "用户信息接口", tags = {"用户信息相关接口"})
@RestController
@RequestMapping("userInfo")
public class CenterUserController {

    @Autowired
    private CenterUserService centerUserService;

    @ApiOperation(value = "修改用户信息", notes = "修改用户信息", httpMethod = "POST")
    @PostMapping("update")
    public ResultJSONResult update(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "centerUserBO", value = "表单对象", required = true)
            @RequestBody CenterUserBO centerUserBO,
            HttpServletRequest request, HttpServletResponse response
    ) {
        Users userResult = centerUserService.updateUserInfo(userId, centerUserBO);
        userResult = setNullProperty(userResult);

        //TODO 后续要改，增加令牌token，会整合进redis，分布式会话
        //设置cookie
        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(userResult), true);
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
}
