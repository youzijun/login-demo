package com.example.demo.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by ZJX-BJ-01-00057 on 2019/2/28.
 */
public interface RedisService {

    boolean set(final String key, Object value, Long expireTime);

    boolean set(final String key,String value);

    boolean setIfAbsent(final String key, Object value, Long expireTime);

    Object get(final String key);

    Set<String> hkeys(final String key);

    boolean hset(final String key, final String field, Object value, Long expireTime);

    boolean hset(final String key,final String field,Object value);

    Object hget(final String key, final String field, Long expireTime);

    Object hget(final String key,final String field);

    boolean hdel(final String key,final String field);

    void del(final String key);

    boolean multiSet(Map<String, Object> map);

    boolean exists(final String key);

    Long decr(final String key);

    Long incr(final String key);

    void remove(final String... keys);

    void removePattern(final String pattern);

    Map<String, Object> rkeyLike(final String keyLike);

    boolean sadd(final String key,final List<String> values);

    Set<String> smembers(final String key);

    boolean checkAndSet(String key, Object oldValue, Object newValue, long expireTime);

    String getId(final String area);

}
