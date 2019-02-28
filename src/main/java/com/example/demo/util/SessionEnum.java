package com.example.demo.util;

/**
 * Created by ZJX-BJ-01-00057 on 2019/2/28.
 */
public enum SessionEnum {

    USER_LOGIN_SUCCESS("0000","登录成功"),
    USER_ONLINE("0001","用户在线"),
    USER_TIME_OUT("0002","登录超时"),
    USER_THEOTHER_ONLINE("0003","其他用户在线"),//登录时使用
    USER_KNOCKED("0004","该用户已被蹬掉"),
    USER_QUIT("0005","用户注销"),
    USER_NOT_LOGIN("0006","用户未登录"),
    USER_LOGIN_EXCEPTION("0007","登录异常");

    private final String key;
    private final String value;

    public String getKey(){
        return key;
    }

    public String getValue()
    {
        return value;
    }

    SessionEnum(String key, String value){
        this.key = key;
        this.value = value;
    }

    public static String getValue(String key){
        for(SessionEnum atte : SessionEnum.values()){
            if(atte.getKey().equals(key)){
                return atte.getValue();
            }
        }
        return null;
    }

}
