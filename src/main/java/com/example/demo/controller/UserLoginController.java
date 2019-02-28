package com.example.demo.controller;

import com.example.demo.service.SessionService;
import com.example.demo.util.CookieUtil;
import com.example.demo.util.GlobalCookieConstant;
import com.example.demo.util.SessionDeal;
import com.example.demo.util.SessionEnum;
import com.example.demo.vo.BusinessUmSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ZJX-BJ-01-00057 on 2019/2/28.
 */
@Controller
public class UserLoginController {

    @Autowired
    private SessionService sessionService;


    @RequestMapping("/")
    public ResponseEntity<Map<String,Object>> goLogin(HttpServletRequest request, HttpServletResponse response) throws Exception{

        Map<String,Object> resultMap = null;
        Map<String,Object> result = new HashMap<>();
        Map<String,Object> cookieMap = SessionDeal.getCurrentCookie(request);

        if(cookieMap != null){
            resultMap = sessionService.getCurrentSession(
                    (String)cookieMap.get("account"),(String)cookieMap.get("sessionId"),
                    GlobalCookieConstant.RISK_USER_NAME_SUFFIX,GlobalCookieConstant.RISK_USER_NAME_SESSION_SUFFIX);
        }

        if(!CollectionUtils.isEmpty(resultMap)){
            String resultCode = (String)resultMap.get("resultCode");
            if(!SessionEnum.USER_ONLINE.getKey().equals(resultCode)){

                //不是在线状态 返回需要登录
                result.put("code", "");
                result.put("msg", "");
            }else{
                request.setAttribute(GlobalCookieConstant.RISK_APP_ID, GlobalCookieConstant.XITONG_ID);

                //更新了用户登录时间
                request.setAttribute(GlobalCookieConstant.RISK_SESSION_KEY,(BusinessUmSession)resultMap.get("businessUmSession"));

                //返回 用户在线状态
                result.put("code", "");
                result.put("msg", "");
            }
        }else{

            // 用户未登录，返回相应码值
            result.put("code", "");
            result.put("msg", "");
        }

        return ResponseEntity.ok(result);
    }


    @RequestMapping(value = "userLogin/login",method = RequestMethod.POST)
    public ResponseEntity<Map<String,Object>> login(@RequestParam("loginName")String loginName,
                              @RequestParam("password")String password,
                              HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> resultMap = new HashMap<>();
        Map<String,Object> result = new HashMap<>();
        try{
            Map<String,Object> cookieMap = SessionDeal.getCurrentCookie(request);
            if(cookieMap!=null){
                String account = (String)cookieMap.get("account");
                if(account.equals(loginName)){
                    resultMap = sessionService.getCurrentSession(
                            (String)cookieMap.get("account"),(String)cookieMap.get("sessionId"),
                            GlobalCookieConstant.RISK_USER_NAME_SUFFIX,GlobalCookieConstant.RISK_USER_NAME_SESSION_SUFFIX);
                }
            }
            String resultCode = null;
            if(resultMap != null && resultMap.size()>0){
                resultCode = (String)resultMap.get("resultCode");
                if(SessionEnum.USER_ONLINE.getKey().equals(resultCode)){
                    request.setAttribute(GlobalCookieConstant.RISK_SESSION_KEY,(BusinessUmSession)resultMap.get("businessUmSession"));
                    request.setAttribute(GlobalCookieConstant.RISK_APP_ID, GlobalCookieConstant.XITONG_ID);

                    //返回 用户在线状态
                    result.put("code", "");
                    result.put("msg", "");
                    return ResponseEntity.ok(result);

                }else if(SessionEnum.USER_TIME_OUT.getKey().equals(resultCode)||
                        SessionEnum.USER_KNOCKED.getKey().equals(resultCode)){
                    //登录超时
                    //返回 用户在线状态
                    result.put("code", "");
                    result.put("msg", "");
                    return ResponseEntity.ok(result);
                }
            }

            /**
             * 此处调用远程登录接口
             * 返回 resultMap
             */
            // =================     TODO

            String sessionId = null;
            resultCode = (String)resultMap.get("resultCode");
            if("0000".equals(resultCode)){

                BusinessUmSession businessUmSession = new BusinessUmSession();
                businessUmSession.setLoginName(loginName);
                businessUmSession.setLoginTime(System.currentTimeMillis());
                sessionId = saveUserSession(request,businessUmSession,(String) resultMap.get("userManageSessionId"));

                //验证成功
                result.put("code", "");
                result.put("msg", "");

            }else{

                //验证失败
                result.put("code", "");
                result.put("msg", "");
                return ResponseEntity.ok(result);
            }
            if(!StringUtils.isEmpty(sessionId)){
                // 登录成功后加入cookie
                CookieUtil.addCookie(response, GlobalCookieConstant.ALL_RISK_SESSIONID,
                        sessionId, 60 * 60 * 24 * 30);
                CookieUtil.addCookie(response, GlobalCookieConstant.ALL_RISK_ACCOUNT, loginName,
                        60 * 60 * 24 * 30);
            }

            //登录成功
            result.put("code", "");
            result.put("msg", "");
            return ResponseEntity.ok(result);
        }catch (Exception e){
            SessionDeal.clearCookies(request,response,request.getSession());
            return ResponseEntity.ok(result);
        }

    }


    /**
     * 用户注销
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("userLogin/logout")
    public ResponseEntity<Map<String,Object>> logout(HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> result = new HashMap<>();
        HttpSession session = request.getSession();
        if (session == null) {
            result.put("code", "");
            result.put("msg", "");
            return ResponseEntity.ok(result);
        }
        String loginName = null;
        BusinessUmSession buSession = (BusinessUmSession)request.getSession().getAttribute(GlobalCookieConstant.RISK_SESSION_KEY);
        if (buSession != null) {
            loginName = buSession.getLoginName();
            sessionService.deleteUserSession(loginName,buSession.getSessionId(),GlobalCookieConstant.RISK_USER_NAME_SUFFIX,GlobalCookieConstant.RISK_USER_NAME_SESSION_SUFFIX);
        }
        session.invalidate();
        CookieUtil.clearCookieWithName(request, response, GlobalCookieConstant.ALL_RISK_SESSIONID, "/");

        result.put("code", "");
        result.put("msg", "");
        return ResponseEntity.ok(result);
    }


    /**
     * 保存用户session
     * @param request
     * @param businessUmSession
     * @return
     * @throws Exception
     */
    private String saveUserSession(HttpServletRequest request, BusinessUmSession businessUmSession,String userManageSessionId) throws Exception{
        String sessionId = null;
        Cookie cookie = CookieUtil.getCookieByName(request, GlobalCookieConstant.ALL_RISK_SESSIONID);
        String randomSessionId = null;
        if (cookie == null) {
            randomSessionId = CookieUtil.getRandomSessionId();
        } else {
            randomSessionId = cookie.getValue();
        }

        Map<String,Object> resultMap = sessionService.saveUserSession(randomSessionId, businessUmSession,
                GlobalCookieConstant.RISK_USER_NAME_SUFFIX, GlobalCookieConstant.RISK_USER_NAME_SESSION_SUFFIX);
        if(SessionEnum.USER_LOGIN_SUCCESS.getKey().equals(resultMap.get("resultCode"))){
            sessionId = randomSessionId;
            request.setAttribute(GlobalCookieConstant.RISK_APP_ID, GlobalCookieConstant.XITONG_ID);
            request.setAttribute(GlobalCookieConstant.RISK_SESSION_KEY, resultMap.get("user"));
        }
        return sessionId;
    }

}
