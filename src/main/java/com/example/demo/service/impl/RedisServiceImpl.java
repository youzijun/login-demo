package com.example.demo.service.impl;

import com.example.demo.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by ZJX-BJ-01-00057 on 2019/2/28.
 */
@Service("redisService")
public class RedisServiceImpl implements RedisService{

    public static final String REDIS_CODE = "UTF-8";
    @SuppressWarnings("rawtypes")
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 批量删除对应的value
     *
     * @param keys
     */
    public void remove(final String... keys) {
        for (String key : keys) {
            del(key);
        }
    }

    public boolean checkAndSet(String key, Object oldValue, Object newValue, long expireTime) {
        redisTemplate.watch(key);
        Object object = this.get(key);
        if ((oldValue == null && object == null) || oldValue.equals(object)) {
            this.set(key, newValue, expireTime);
            if (redisTemplate.exec() == null) {
                return false;
            }
            return true;
        }
        return false;

    }

    @Override
    public String getId(String area) {
        return null;
    }

    /**
     * 批量删除key
     *
     * @param pattern
     */
    public void removePattern(final String pattern) {
        Set<Serializable> keys = redisTemplate.keys(pattern);
        if (keys.size() > 0)
            redisTemplate.delete(keys);
    }

    /**
     * 删除对应的value
     *
     * @param key
     */
    public void del(final String key) {
        if (exists(key)) {
            redisTemplate.delete(key);
        }
    }

    /**
     * 判断缓存中是否有对应的value
     *
     * @param key
     * @return
     */
    public boolean exists(final String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 读取缓存
     *
     * @param key
     * @return
     */
    public Object get(final String key) {
        Object result = null;
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        result = operations.get(key);
        return result;
    }



    /**
     * 写入缓存
     * setIfAbsent目前实际上基本=set_nx,但是会有死锁问题
     *
     * @param key
     * @param value
     * @return
     */
    public boolean setIfAbsent(final String key, Object value, Long expireTime) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            boolean res = operations.setIfAbsent(key, value);
            //未设置失效时间,则默认10分钟
            if (null == expireTime) {
                expireTime = 300L;
            }
            //死锁就是因为这两步不是原子的
            if (res) {
                redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 多个键值对写入缓存
     * multiSet
     *
     * @param map
     * @return
     */
    public boolean multiSet(Map<String, Object> map) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.multiSet(map);
            result = true;
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, Object value, Long expireTime) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            if (null != expireTime && 0 < expireTime) {
                redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            }
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean set(String key, String value) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 递减
     *
     * @param key
     * @return
     */
    public Long decr(final String key) {
        RedisSerializer keySerializer = new RedisSerializer() {
            @Override
            public byte[] serialize(Object o) throws SerializationException {
                return new byte[0];
            }

            @Override
            public Object deserialize(byte[] bytes) throws SerializationException {
                return null;
            }
        };
        final byte[] rawKey = keySerializer.serialize(key);

        return (Long) this.redisTemplate.execute(new RedisCallback() {
            public Long doInRedis(RedisConnection connection) {
                return connection.decr(rawKey);
            }
        }, true);

    }

    /**
     * 递增
     *
     * @param key
     * @return
     */
    public Long incr(final String key) {
        RedisSerializer keySerializer = new RedisSerializer() {
            @Override
            public byte[] serialize(Object o) throws SerializationException {
                return new byte[0];
            }

            @Override
            public Object deserialize(byte[] bytes) throws SerializationException {
                return null;
            }
        };
        final byte[] rawKey = keySerializer.serialize(key);

        return (Long) this.redisTemplate.execute(new RedisCallback() {
            public Long doInRedis(RedisConnection connection) {
                return connection.incr(rawKey);
            }
        }, true);

    }


    /**
     * 写入缓存,目前主要是先完成从列表里面增加和删除
     *
     * @param key
     * @param value
     * @return
     */
    public boolean hset(final String key, final String field, Object value, Long expireTime) {
        boolean result = false;
        try {
            HashOperations<String, String, Object> operations = redisTemplate.opsForHash();
            operations.put(key, field, value);
            if (null != expireTime && 0 < expireTime) {
                redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            }
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取某一hashmap类型下的所有field
     *
     * @param key
     * @return
     */
    public Set<String> hkeys(final String key) {
        Set<String> fieldSet = null;
        try {
            HashOperations<String, String, Object> operations = redisTemplate.opsForHash();
            fieldSet = operations.keys(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fieldSet;
    }

    /**
     * 设置hashmap形式的值
     *
     * @param key
     * @param field
     * @param value
     * @return
     */
    public boolean hset(final String key, final String field, Object value) {
        boolean result = false;
        try {
            HashOperations<String, String, Object> operations = redisTemplate.opsForHash();
            operations.put(key, field, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 写入缓存,目前主要是先完成从列表里面增加和删除
     *
     * @param key
     * @param field
     * @return
     */
    public Object hget(final String key, final String field, Long expireTime) {
        Object result = null;
        try {
            HashOperations<String, String, Object> operations = redisTemplate.opsForHash();
            result = operations.get(key, field);
            if (null != result && null != expireTime && 0 < expireTime) {
                redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 根据key和field获取哈希值
     *
     * @param key
     * @param field
     * @return
     */
    public Object hget(final String key, final String field) {
        Object result = null;
        try {
            HashOperations<String, String, Object> operations = redisTemplate.opsForHash();
            result = operations.get(key, field);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 按照key和field删除hashmap值
     *
     * @param key
     * @param field
     * @return
     */
    public boolean hdel(final String key, final String field) {
        boolean result = false;
        try {
            HashOperations<String, String, Object> operations = redisTemplate.opsForHash();
            operations.delete(key, field);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    //向redis里添加set
    public boolean sadd(final String key, final List<String> values) {
        boolean result = false;
        try {
            SetOperations<String, Object> operations = redisTemplate.opsForSet();
            result = operations.add(key, values) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    //获取set里的集合
    public Set<String> smembers(final String key) {
        Set result = new HashSet<>();
        try {
            SetOperations<String, Object> operations = redisTemplate.opsForSet();
            result = operations.members(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public Map<String, Object> rkeyLike(final String keyLike) {

        Map<String, Object> values = new HashMap<String, Object>();
        try {
            Set<String> keys = redisTemplate.keys(keyLike + "*");
            for (String b : keys) {
                Object v = get(b);
                if (v != null) {
                    values.put(b, v);
                }
            }
        } catch (Exception e) {
        }
        return values;
    }



}
