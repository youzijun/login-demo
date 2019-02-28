package com.example.demo.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ZJX-BJ-01-00057 on 2019/2/28.
 */
public class SessionDeal {

    public static Map<String,Object> getCurrentCookie(HttpServletRequest request)throws Exception{
        Map<String,Object> resultMap = new HashMap<>();
        Cookie cookie = CookieUtil.getCookieByName(request, GlobalCookieConstant.ALL_RISK_SESSIONID);
        if(cookie == null){
            return null;
        }
        resultMap = dealLogin(request);
        return resultMap;
    }

    public static Map<String, Object> dealLogin(HttpServletRequest request) throws  Exception{
        Map<String,Object> paramMap = new HashMap<>();
        Cookie cookie = CookieUtil.getCookieByName(request, GlobalCookieConstant.ALL_RISK_SESSIONID);
        Cookie account = CookieUtil.getCookieByName(request, GlobalCookieConstant.ALL_RISK_ACCOUNT);
        if(cookie == null || account == null){
            return null;
        }
        paramMap.put("account",account.getValue());
        paramMap.put("sessionId",cookie.getValue());
        return paramMap;
    }

    protected void dealNullCookie(HttpServletRequest request, HttpServletResponse response,
                                  HttpSession session)throws Exception{
        clearCookies(request, response, session);
        StringBuffer sb = new StringBuffer();
        sb.append("<script type=\"text/javascript\">");
        sb.append("window.top.location.href='/'");
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

    public static void clearCookies(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        CookieUtil.clearCookieWithName(request, response, GlobalCookieConstant.ALL_RISK_ACCOUNT, "/");
        CookieUtil.clearCookieWithName(request, response, GlobalCookieConstant.ALL_RISK_SESSIONID, "/");
        session.invalidate();
    }

}
