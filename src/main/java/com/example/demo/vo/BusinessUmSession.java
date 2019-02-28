package com.example.demo.vo;

import java.io.Serializable;

/**
 * Created by ZJX-BJ-01-00057 on 2019/2/28.
 */
public class BusinessUmSession implements Serializable{

    private String sessionId; //session ID

    private long loginTime; //最近一次登录时间

    private String loginName; //用户名

    /*
        下面可以扩展，添加用户对象、权限对象等等
     */

//    private User user;
//    private List<Privilege> listPrivilege;


    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(long loginTime) {
        this.loginTime = loginTime;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }
}
