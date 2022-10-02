package com.zzl.huayumall.member.interceptor;

import com.zzl.common.constant.AuthServerConstant;
import com.zzl.common.vo.UserItemVo;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/09/20  18:58
 */
@Configuration
public class UserLongInterceptor implements HandlerInterceptor {
    public static ThreadLocal<UserItemVo> thread = new ThreadLocal<>();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        AntPathMatcher pathMatcher = new AntPathMatcher();
        boolean register = pathMatcher.match("/register/**", uri);
        boolean match = pathMatcher.match("/member/**", uri);

        if(match||register){
            //包含以上路径，放行
            return true;
        }
        boolean match1 = pathMatcher.match("/social/login", uri);
        if(match1){
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
