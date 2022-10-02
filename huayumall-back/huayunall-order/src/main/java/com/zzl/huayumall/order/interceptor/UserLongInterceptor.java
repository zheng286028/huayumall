package com.zzl.huayumall.order.interceptor;

import com.zzl.common.constant.AuthServerConstant;
import com.zzl.common.vo.UserItemVo;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 功能描述
 *  执行业务之前判断是否登录
 * @author 郑子浪
 * @date 2022/09/08  18:51
 */
@Component
public class UserLongInterceptor implements HandlerInterceptor {
    public static ThreadLocal<UserItemVo> thread = new ThreadLocal<>();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        boolean match = antPathMatcher.match("/order/order/status/**", uri);
        boolean match1 = antPathMatcher.match("/order/orderPay", uri);
        if(match || match1){
            //包含以上路径，放行
            return true;
        }
        UserItemVo attribute = (UserItemVo) request.getSession().getAttribute(AuthServerConstant.USER_LOGIN_SUCCESS);
        if(attribute!=null){
            //1、登录了
            thread.set(attribute);
            return true;
        }
        //2、未登录
        request.getSession().setAttribute("msg","尚未登录！");
        response.sendRedirect("http://auth.huayumall.com/login.html");
        return false;
    }
}
