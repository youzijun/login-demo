package com.example.demo.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by ZJX-BJ-01-00057 on 2019/2/28.
 */
public class CookieUtil {

    /**
     *
     * 设置cookie
     *
     * @param response
     *
     * @param name
     *            cookie名字
     *
     * @param value
     *            cookie值
     *
     * @param maxAge
     *            cookie生命周期 以秒为单位
     */

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {

        try {
            value = URLEncoder.encode(value, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Cookie cookie = new Cookie(name, value);

        cookie.setPath("/");

        if (maxAge > 0)
            cookie.setMaxAge(maxAge);

        response.addCookie(cookie);

    }

    /**
     *
     * 根据名字获取cookie
     *
     * @param request
     *
     * @param name
     *            cookie名字
     *
     * @return
     */

    public static Cookie getCookieByName(HttpServletRequest request, String name) {

        Map<String, Cookie> cookieMap = ReadCookieMap(request);

        if (cookieMap.containsKey(name)) {

            Cookie cookie = cookieMap.get(name);

            return cookie;

        } else {

            return null;

        }

    }

    /**
     *
     * 将cookie封装到Map里面
     *
     * @param request
     *
     * @return
     */

    private static Map<String, Cookie> ReadCookieMap(HttpServletRequest request) {

        Map<String, Cookie> cookieMap = new HashMap<String, Cookie>();

        Cookie[] cookies = request.getCookies();

        if (null != cookies) {

            for (Cookie cookie : cookies) {

                try {
                    cookieMap.put(URLEncoder.encode(cookie.getName(), "utf-8"), cookie);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }

        }

        return cookieMap;

    }

    public static void clearAllCookie(HttpServletRequest request, HttpServletResponse response, String path) {
        Cookie[] cookies = request.getCookies();
        try {
            for (int i = 0; i < cookies.length; i++) {
                Cookie cookie = new Cookie(cookies[i].getName(), null);
                cookie.setMaxAge(0);
                cookie.setPath(path);// 根据你创建cookie的路径进行填写
                response.addCookie(cookie);
            }
        } catch (Exception ex) {
            System.out.println("清空Cookies发生异常！");
        }

    }

    public static void clearCookieWithName(HttpServletRequest request, HttpServletResponse response, String name, String path) {


        try {
            Cookie cookie = new Cookie(name, null);
            cookie.setMaxAge(0);
            cookie.setPath(path);// 根据你创建cookie的路径进行填写
            response.addCookie(cookie);
        } catch (Exception ex) {
            System.out.println("清空 "+name+"的Cookies发生异常！");
        }

    }
    public static String getRandomSessionId() {

        return UUID.randomUUID().toString() + UUID.randomUUID().toString();
    }

}
