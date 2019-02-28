package com.example.demo.interceptor;

import com.example.demo.util.GlobalCookieConstant;
import com.example.demo.vo.BusinessUmSession;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * Created by ZJX-BJ-01-00057 on 2019/2/28.
 */
@Configuration
public class UserLoginInterceptor implements HandlerInterceptor{


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 配置白名单路径
        if(request.getServletPath().equals("/") ||
            request.getServletPath().startsWith("/userLogin/login")||
            request.getServletPath().startsWith("/userLogin/logout")){

            return true;
        }

        // 获取request中session对象
        BusinessUmSession umSession = (BusinessUmSession) request.getSession().getAttribute(GlobalCookieConstant.RISK_SESSION_KEY);

        if(umSession == null){
            // 说明用户未登录
            dealResponse("/", response);
            return false;
        }

        return true;
    }


    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }


    private void dealResponse(String url,HttpServletResponse response)throws  Exception{
        StringBuffer sb = new StringBuffer();
        sb.append("<script type=\"text/javascript\">");
        sb.append("alert('用户未登录');window.top.location.href='"+url+"'");
        sb.append("</script>");
        response.setContentType("text/html");
        // 设置页面不缓存
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.print(sb);
        out.flush();
        out.close();
    }

}
