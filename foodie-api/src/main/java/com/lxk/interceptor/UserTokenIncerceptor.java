package com.lxk.interceptor;

import com.lxk.utils.JsonUtils;
import com.lxk.utils.RedisOperator;
import com.lxk.utils.ResultJSONResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 自定义拦截器
 *
 * @author songshiyu
 * @date 2020/8/31 21:36
 **/
public class UserTokenIncerceptor implements HandlerInterceptor {

    public static final String USER_REDIS_TOKEN = "user_redis_token";

    @Autowired
    private RedisOperator redisOperator;

    /**
     * 拦截请求，在访问controller之前调用
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        /**
         * false:请求被拦截，被驳回，验证出现了问题
         * true：请求在经过校验以后，是ok的，是可以放行的
         * */
        String userId = request.getHeader("headerUserId");
        String userToken = request.getHeader("headerUserToken");

        if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(userToken)) {

            String uniqueToken = redisOperator.get(USER_REDIS_TOKEN + ":" + userId);
            if (StringUtils.isNotBlank(uniqueToken)) {
                if (!uniqueToken.equals(userToken)) {
                    sendErrorMessage(response, ResultJSONResult.errorMsg("请的账号在异地登录~"));
                    return false;
                }
            } else {
                sendErrorMessage(response, ResultJSONResult.errorMsg("请登录~"));
                return false;
            }
        } else {
            sendErrorMessage(response, ResultJSONResult.errorMsg("请登录~"));
            return false;
        }
        return true;
    }

    /**
     * 拦截请求在调用controller之后，渲染视图之前
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 拦截请求在渲染之后
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

    private void sendErrorMessage(HttpServletResponse response, ResultJSONResult resultJSONResult) {

        OutputStream out = null;
        try {
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/json");
            out = response.getOutputStream();
            out.write(JsonUtils.objectToJson(resultJSONResult).getBytes("utf-8"));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
