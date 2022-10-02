package com.zzl.cart.interceptor;

import com.zzl.cart.to.UserInfo;
import com.zzl.common.constant.AuthServerConstant;
import com.zzl.common.constant.CartConstant;
import com.zzl.common.vo.UserItemVo;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * 功能描述
 * 执行业务之前判断该用户是否登录
 *
 * @author 郑子浪
 * @date 2022/09/03  19:56
 */
public class CartInterceptor implements HandlerInterceptor {
    public static ThreadLocal<UserInfo> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserInfo userInfo = new UserInfo();
        //获取用户信息
        HttpSession session = request.getSession();
        UserItemVo user = (UserItemVo) session.getAttribute(AuthServerConstant.USER_LOGIN_SUCCESS);
        //判断是否登录，并存储对应信息
        if (user != null) {
            //登录
            userInfo.setUserId(user.getId());
        }
        //存储user-key的信息
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                //比较cookieName是否为指定的user-key来获取对应的value
                if (cookie.getName().equals(CartConstant.TEMP_USER_COOKIE_NAME)) {
                    //存储Value
                    userInfo.setUserKey(cookie.getValue());
                    userInfo.setIsTempUser(true);
                }
            }
        }
        //放行之前将该信息存储到共享线程中
        threadLocal.set(userInfo);
        return true;
    }

    /**
     * 业务执行之后
     *
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //判断user-key是否已经存在
        UserInfo userInfo = CartInterceptor.threadLocal.get();
        if (!userInfo.getIsTempUser()) {
            //没有设置，第一次登录,给浏览器保存cookie为user-key
            String s = UUID.randomUUID().toString();
            Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME, s);
            //设置cookie配置
            cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIME);
            cookie.setDomain("huayumall.com");
            response.addCookie(cookie);
        }
    }
}
