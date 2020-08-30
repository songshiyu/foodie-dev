package com.lxk.controller;

import com.lxk.pojo.Users;
import com.lxk.pojo.bo.ShopcartBo;
import com.lxk.pojo.bo.UserBO;
import com.lxk.pojo.vo.UsersVO;
import com.lxk.service.UserService;
import com.lxk.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author songshiyu
 * @date 2020/6/17 21:43
 **/

@Api(value = "注册登录", tags = "用于登录注册的接口")
@RestController
@RequestMapping("passport")
public class PassportController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisOperator redisOperator;

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

        //设置用户token，存入redis
        UsersVO usersVO = convertUserVo(usersResult);

        //设置cookie
        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(usersVO), true);

        //同步购物车数据
        synchShopcartData(usersResult.getId(), request, response);
        return ResultJSONResult.ok(usersResult);
    }

    /**
     * 注册登录成功后，同步cookie和redis中的购物车数据
     */
    private void synchShopcartData(String userId, HttpServletRequest request,
                                   HttpServletResponse response) {

        /**
         * 1. redis中无数据，如果cookie中的购物车为空，那么这个时候不做任何处理
         *                 如果cookie中的购物车不为空，此时直接放入redis中
         * 2. redis中有数据，如果cookie中的购物车为空，那么直接把redis的购物车覆盖本地cookie
         *                 如果cookie中的购物车不为空，
         *                      如果cookie中的某个商品在redis中存在，
         *                      则以cookie为主，删除redis中的，
         *                      把cookie中的商品直接覆盖redis中（参考京东）
         * 3. 同步到redis中去了以后，覆盖本地cookie购物车的数据，保证本地购物车的数据是同步最新的
         */

        //从redis中获取redis数据
        String shopcartJsonRedis = redisOperator.get(FOODIE_SHOPCART + ":" + userId);

        //从cookie中获取cookie数据
        String shopcaetCookieValue = CookieUtils.getCookieValue(request, FOODIE_SHOPCART, true);

        if (StringUtils.isBlank(shopcartJsonRedis)) {
            //redis数据为空，cookie数据不为空，直接将cookie数据放入redis
            if (StringUtils.isNotBlank(shopcaetCookieValue)) {
                redisOperator.set(FOODIE_SHOPCART + ":" + userId, shopcaetCookieValue);
            }
        } else {
            //redis不为空，cookie不为空，合并cookie和redis中购物车的商品数据（同一商品则覆盖redis）
            if (StringUtils.isNotBlank(shopcaetCookieValue)) {
                /**
                 * 1. 已经存在的，把cookie中对应的数量，覆盖redis（参考京东）
                 * 2. 该项商品标记为待删除，统一放入一个待删除的list
                 * 3. 从cookie中清理所有的待删除list
                 * 4. 合并redis和cookie中的数据
                 * 5. 更新到redis和cookie中
                 */
                List<ShopcartBo> shopcartListRedis = JsonUtils.jsonToList(shopcartJsonRedis, ShopcartBo.class);
                List<ShopcartBo> shopcartListCookie = JsonUtils.jsonToList(shopcaetCookieValue, ShopcartBo.class);

                // 定义一个待删除list
                List<ShopcartBo> pendingDeleteList = new ArrayList<>();

                for (ShopcartBo redisShopcart : shopcartListRedis) {
                    String redisSpecId = redisShopcart.getSpecId();

                    for (ShopcartBo cookieShopcart : shopcartListCookie) {
                        String cookieSpecId = cookieShopcart.getSpecId();

                        if (redisSpecId.equals(cookieSpecId)) {
                            // 覆盖购买数量，不累加，参考京东
                            redisShopcart.setBuyCounts(cookieShopcart.getBuyCounts());
                            // 把cookieShopcart放入待删除列表，用于最后的删除与合并
                            pendingDeleteList.add(cookieShopcart);
                        }
                    }
                }
                // 从现有cookie中删除对应的覆盖过的商品数据
                shopcartListCookie.removeAll(pendingDeleteList);
                // 合并两个list
                shopcartListRedis.addAll(shopcartListCookie);
                // 更新到redis和cookie
                CookieUtils.setCookie(request, response, FOODIE_SHOPCART, JsonUtils.objectToJson(shopcartListRedis), true);
                redisOperator.set(FOODIE_SHOPCART + ":" + userId, JsonUtils.objectToJson(shopcartListRedis));
            } else {
                // redis不为空，cookie为空，直接把redis覆盖cookie
                CookieUtils.setCookie(request, response, FOODIE_SHOPCART, shopcartJsonRedis, true);
            }
        }

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

        //设置用户token，存入redis
        UsersVO usersVO = convertUserVo(usersResult);
        //设置cookie
        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(usersVO), true);
        //同步购物车数据
        synchShopcartData(usersResult.getId(), request, response);
        return ResultJSONResult.ok();
    }


    @ApiOperation(value = "用户退出登录", notes = "用户退出登录", httpMethod = "POST")
    @PostMapping("/logout")
    public ResultJSONResult logout(@RequestParam String userId,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {

        //清除用户相关信息的cookie
        CookieUtils.deleteCookie(request, response, "user");

        //用户退出登录，需要清空购物车
        CookieUtils.deleteCookie(request, response, FOODIE_SHOPCART);

        //分布式会话中需要清除用户数据
        redisOperator.del(USER_REDIS_TOKEN + ":" + userId);

        return ResultJSONResult.ok();
    }
}
