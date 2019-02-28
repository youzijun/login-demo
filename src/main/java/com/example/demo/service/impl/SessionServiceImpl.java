package com.example.demo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.service.SessionService;
import com.example.demo.util.SessionEnum;
import com.example.demo.vo.BusinessUmSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ZJX-BJ-01-00057 on 2019/2/28.
 */
@Service("sessionService")
public class SessionServiceImpl implements SessionService{

    @Autowired
    private RedisServiceImpl redisService;


    /**
     * 保存用户session
     * @param sessionId
     * @return
     * @throws Exception
     */
    @Override
    public Map<String,Object> saveUserSession(String sessionId, BusinessUmSession businessUmSession, String systemType1, String systemType2) throws Exception{
        Map<String,Object> resultMap = new HashMap<>();
        boolean nameEnResult = redisService.set(businessUmSession.getLoginName()+"_"+systemType1,sessionId);
        boolean userResult = redisService.set(sessionId+"_"+systemType2, JSONObject.toJSONString(businessUmSession));
        if(nameEnResult && userResult){
            resultMap.put("resultCode", SessionEnum.USER_LOGIN_SUCCESS.getKey());
            resultMap.put("resultMessage",SessionEnum.USER_LOGIN_SUCCESS.getValue());
            resultMap.put("businessUmSession",businessUmSession);
        }
        return resultMap;
    }


    //根据sessionId获取当前登录的用户
    @Override
    public Map<String,Object> getCurrentSession(String userNameEn, String sessionId,String systemType1,String systemType2)throws Exception {
        Map<String,Object> resultMap = new HashMap<>();
        String userNameSuffix = userNameEn+"_"+systemType1;
        String loginInfoSuffix = sessionId+"_"+systemType2;
        String redisSessionId = (String)redisService.get(userNameSuffix);
        if(StringUtils.isEmpty(redisSessionId)){
            //用户之前未做过登录操作，需要重新进行登录
            resultMap.put("resultCode",SessionEnum.USER_NOT_LOGIN.getKey());
            resultMap.put("resultMessage", SessionEnum.USER_NOT_LOGIN.getValue());
        }else{
            if(sessionId.equals(redisSessionId)){
                if(redisService.get(loginInfoSuffix)!=null){
                    String umStr = redisService.get(loginInfoSuffix)==null?"":redisService.get(loginInfoSuffix).toString();
                    BusinessUmSession umSession = JSONObject.parseObject(JSONObject.toJSON(umStr).toString(),BusinessUmSession.class);
                    long nowTime = System.currentTimeMillis();
                    boolean isExpired = isExpired(umSession.getLoginTime(),nowTime);
                    if(isExpired){
                        //登录超时
                        redisService.del(userNameSuffix);
                        redisService.del(loginInfoSuffix);
                        resultMap.put("resultCode",SessionEnum.USER_TIME_OUT.getKey());
                        resultMap.put("resultMessage",SessionEnum.USER_TIME_OUT.getValue());
                    }else{
                        //登录成功，更新登录时间
                        umSession.setLoginTime(System.currentTimeMillis());
                        redisService.set(loginInfoSuffix,JSONObject.toJSONString(umSession));
                        resultMap.put("resultCode",SessionEnum.USER_ONLINE.getKey());
                        resultMap.put("resultMessage",SessionEnum.USER_ONLINE.getValue());
                        resultMap.put("user",umSession);
                    }
                }else{
                    //异常
                    resultMap.put("resultCode",SessionEnum.USER_LOGIN_EXCEPTION.getKey());
                    resultMap.put("resultMessage",SessionEnum.USER_LOGIN_EXCEPTION.getValue());
                }
            }else{
                //该用户已被其他用户蹬掉，需要重新登录
                resultMap.put("resultCode",SessionEnum.USER_KNOCKED.getKey());
                resultMap.put("resultMessage",SessionEnum.USER_KNOCKED.getValue());
            }
        }
        return resultMap;
    }


    //注销时将session清除
    @Override
    public Map<String,Object> deleteUserSession(String userNameEn, String sessionId,String systemType1,String systemType2) {
        Map<String,Object> resultMap = new HashMap<>();
        redisService.del(userNameEn+"_"+systemType1);
        redisService.del(sessionId+"_"+systemType2);
        resultMap.put("resultCode",SessionEnum.USER_QUIT.getKey());
        resultMap.put("resultMessage",SessionEnum.USER_QUIT.getValue());
        return resultMap;
    }


    private boolean isExpired(long sessionTime,long nowTime){
        boolean isExpired = false;
        long nd = 1000*24*60*60;//一天的毫秒数
        long nh = 1000*60*60;//一小时的毫秒数
        long nm = 1000*60;//一分钟的毫秒数
        long ns = 1000;//一秒钟的毫秒数
        long diff = nowTime - sessionTime;
        long day = diff/nd;//计算差多少天
        long hour = diff%nd/nh;//计算差多少小时
        long min = diff%nd%nh/nm;//计算差多少分钟
        long sec = diff%nd%nh%nm/ns;//计算差多少秒//输出结果
        if(hour>=3){
            //如果用户登录时间超过两小时，则超时
            isExpired = true;
        }
        return isExpired;
    }

}
