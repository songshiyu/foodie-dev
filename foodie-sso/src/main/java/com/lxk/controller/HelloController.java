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
public class HelloController {

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

        //TODO 后续完善是否登录

        //用户从未登录过，第一次进入则跳转到CAS登录页面
        return "login";
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

        return "redict:" + returnUrl + "?tmpTicket=" + tmpTicket;
    }

    //TODO 缺少一步验证临时票据的过程


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
}
