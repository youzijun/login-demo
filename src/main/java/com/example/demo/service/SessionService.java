package com.example.demo.service;

import com.example.demo.vo.BusinessUmSession;

import java.util.Map;

/**
 * Created by ZJX-BJ-01-00057 on 2019/2/28.
 */
public interface SessionService {

    //保存用户session
    Map<String,Object> saveUserSession(String sessionId, BusinessUmSession businessUmSession, String systemType1, String systemType2)throws Exception;

    //根据sessionId获取当前登录的用户
    Map<String,Object> getCurrentSession(String userNameEn, String sessionId, String systemType1, String systemType2)throws  Exception;

    //注销时将session清除
    Map<String,Object> deleteUserSession(String userNameEn, String sessionId, String systemType1, String systemType2);

}
