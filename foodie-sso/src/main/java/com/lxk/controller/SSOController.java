package com.lxk.controller;

import com.lxk.pojo.Users;
import com.lxk.pojo.vo.UsersVO;
import com.lxk.service.UserService;
import com.lxk.utils.JsonUtils;
import com.lxk.utils.MD5Utils;
import com.lxk.utils.RedisOperator;
import com.lxk.utils.ResultJSONResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * @author songshiyu
 * @date 2020/6/14 17:59
 **/

@Controller
public class SSOController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisOperator redisOperator;

    public static final String USER_REDIS_TOKEN = "user_redis_token";

    public static final String USER_REDIS_TICKET = "user_redis_ticket";

    public static final String USER_TMP_TICKET = "user_tmp_ticket";

    public static final String TICKET_COOKIE = "ticket_cookie";

    @GetMapping("/login")
    public String login(String returnUrl,
                        Model model,
                        HttpServletRequest request,
                        HttpServletResponse response) {

        model.addAttribute("returnUrl", returnUrl);
        // 1. 获取userTicket门票，如果cookie中能够获取到，证明用户登录过，此时签发一个一次性的临时票据并且回跳
        String userTicket = getCookie(request, TICKET_COOKIE);

        boolean isVerified = verifyUserTicket(userTicket);
        if (isVerified) {
            String tmpTicket = createTmpTicket();
            return "redirect:" + returnUrl + "?tmpTicket=" + tmpTicket;
        }
        // 2. 用户从未登录过，第一次进入则跳转到CAS的统一登录页面
        return "login";
    }

    /**
     * 校验CAS全局用户门票
     *
     * @param userTicket
     * @return
     */
    private boolean verifyUserTicket(String userTicket) {
        // 0. 验证CAS门票不能为空
        if (StringUtils.isBlank(userTicket)) {
            return false;
        }
        // 1. 验证CAS门票是否有效
        String userId = redisOperator.get(USER_REDIS_TICKET + ":" + userTicket);
        if (StringUtils.isBlank(userId)) {
            return false;
        }
        // 2. 验证门票对应的user会话是否存在
        String userRedis = redisOperator.get(USER_REDIS_TOKEN + ":" + userId);
        if (StringUtils.isBlank(userRedis)) {
            return false;
        }
        return true;
    }


    /**
     * cas的统一登录接口
     * 目的：
     * 1.登录后创建用户的全局会话      -》uniqueToken
     * 2.创建用户全局门票，用以表示在cas端是否登录  --》userTicket
     * 3.创建用户的临时票据，用户跳转        --》tmpTicket
     */
    @PostMapping("/doLogin")
    public String doLogin(String username,
                          String password,
                          String returnUrl,
                          Model model,
                          HttpServletRequest request,
                          HttpServletResponse response) throws Exception {

        model.addAttribute("returnUrl", returnUrl);

        if (StringUtils.isBlank(username) ||
                StringUtils.isBlank(password)) {
            model.addAttribute("errmsg", "用户名或密码不能为空");
            return "login";
        }

        Users usersResult = userService.queryUserForLogin(username, MD5Utils.getMD5Str(password));

        if (usersResult == null) {
            model.addAttribute("errmsg", "用户名或密码不能为空");
            return "login";
        }

        //2.实现用户redis全局会话，也就是分布式会话
        String uniqueToken = UUID.randomUUID().toString().trim();
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(usersResult, usersVO);
        usersVO.setUserUniqueToken(uniqueToken);
        redisOperator.set(USER_REDIS_TOKEN + ":" + usersResult.getId(), JsonUtils.objectToJson(usersVO));

        //3.生成ticket门票，全局门票，代表用户在CAS端登录过
        String userTicket = UUID.randomUUID().toString().trim();

        //3.1 将ticket门票放在cookie中
        setCookie(TICKET_COOKIE, userTicket, response);

        //4.userTicket关联用户id，并且放在redis中
        redisOperator.set(USER_REDIS_TICKET + ":" + userTicket, usersResult.getId());

        //5.生成临时票据，回跳到调用网站，是由CAS端所签发的一个一次性的临时票据
        String tmpTicket = createTmpTicket();

        return "redirect:" + returnUrl + "?tmpTicket=" + tmpTicket;
    }

    @PostMapping("/verifyTmpTicket")
    @ResponseBody
    public ResultJSONResult verifyTmpTicket(String tmpTicket,
                                            HttpServletRequest request,
                                            HttpServletResponse response) throws Exception {

        // 使用一次性临时票据来验证用户是否登录，如果登录过，把用户会话信息返回给站点
        // 使用完毕后，需要销毁临时票据
        String tempTicketValue = redisOperator.get(USER_TMP_TICKET + ":" + tmpTicket);

        if (StringUtils.isBlank(tempTicketValue)) {
            return ResultJSONResult.errorUserTicket("用户票据异常");
        }

        if (!MD5Utils.getMD5Str(tmpTicket).equals(tempTicketValue)) {
            return ResultJSONResult.errorUserTicket("用户票据异常");
        } else {
            // 销毁临时票据
            redisOperator.del(USER_TMP_TICKET + ":" + tmpTicket);
        }

        //1. 验证并且获取用户的userTicket
        String userTicket = getCookie(request, TICKET_COOKIE);
        String userId = redisOperator.get(USER_REDIS_TICKET + ":" + userTicket);
        if (StringUtils.isBlank(userId)) {
            return ResultJSONResult.errorUserTicket("用户票据异常");
        }

        // 2. 验证门票对应的user会话是否存在
        String userRedis = redisOperator.get(USER_REDIS_TOKEN + ":" + userId);
        if (StringUtils.isBlank(userRedis)) {
            return ResultJSONResult.errorUserTicket("用户票据异常");
        }

        // 验证成功，返回OK，携带用户会话
        return ResultJSONResult.ok(JsonUtils.jsonToPojo(userRedis, UsersVO.class));
    }

    private String getCookie(HttpServletRequest request, String key) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null || StringUtils.isBlank(key)) {
            return null;
        }

        String cookieValue = null;
        for (int i = 0; i < cookies.length; i++) {
            if (cookies[i].getName().equals(key)) {
                cookieValue = cookies[i].getValue();
                break;
            }
        }
        return cookieValue;
    }

    private void setCookie(String key, String value, HttpServletResponse response) {
        Cookie cookie = new Cookie(key, value);
        cookie.setDomain("sso.com");
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    private String createTmpTicket() {
        String tmpTicket = UUID.randomUUID().toString().trim();
        try {
            redisOperator.set(USER_TMP_TICKET + ":" + tmpTicket, MD5Utils.getMD5Str(tmpTicket));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return tmpTicket;
    }

    @PostMapping("/logout")
    @ResponseBody
    public ResultJSONResult logout(String userId,
                                   HttpServletRequest request,
                                   HttpServletResponse response) throws Exception {

        // 0. 获取CAS中的用户门票
        String userTicket = getCookie(request, USER_REDIS_TICKET);

        // 1. 清除userTicket票据，redis/cookie
        deleteCookie(USER_REDIS_TICKET, response);
        redisOperator.del(USER_REDIS_TICKET + ":" + userTicket);

        // 2. 清除用户全局会话（分布式会话）
        redisOperator.del(USER_REDIS_TOKEN + ":" + userId);

        return ResultJSONResult.ok();
    }

    private void deleteCookie(String key,
                              HttpServletResponse response) {

        Cookie cookie = new Cookie(key, null);
        cookie.setDomain("sso.com");
        cookie.setPath("/");
        cookie.setMaxAge(-1);
        response.addCookie(cookie);
    }
}
