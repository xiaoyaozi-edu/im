package com.xiaoyaozi.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * tip:
 *
 * @author xiaoyaozi
 * createTime: 2021-03-24 11:06
 */
public class RedisUtil {

    @SuppressWarnings("unchecked")
    private static final RedisTemplate<String, Object> REDIS_TEMPLATE = SpringUtil.getBean("jsonRedisTemplate", RedisTemplate.class);

    public static void delete(String key) {
        REDIS_TEMPLATE.delete(key);
    }

    public static void set(String key, Object value) {
        REDIS_TEMPLATE.opsForValue().set(key, value);
    }

    public static void set(String key, Object value, long timeout, TimeUnit unit) {
        REDIS_TEMPLATE.opsForValue().set(key, value, timeout, unit);
    }

    public static Boolean isExist(String key) {
        return REDIS_TEMPLATE.hasKey(key);
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(String key) {
        return (T) REDIS_TEMPLATE.opsForValue().get(key);
    }

    public static Set<String> patternKey(String key) {
        return REDIS_TEMPLATE.keys(key);
    }

    @SuppressWarnings("unchecked")
    public static <T> Map<String, T> patternList(String key) {
        Set<String> keySet = REDIS_TEMPLATE.keys(key);
        if (CollectionUtil.isEmpty(keySet)) {
            return MapUtil.empty();
        }
        List<String> keyList = new ArrayList<>(keySet);
        List<T> dataList = (List<T>) REDIS_TEMPLATE.opsForValue().multiGet(keyList);
        if (CollectionUtil.isEmpty(dataList)) {
            return MapUtil.empty();
        }
        Map<String, T> map = new HashMap<>(16);
        for (int i = 0; i < keyList.size(); i++) {
            map.put(keyList.get(i), dataList.get(i));
        }
        return map;
    }

    public static Long getLongData(String key) {
        // redis中取出number类型是没有语义的。按照你存入数据的长度，如果 < int.max，那么返回的是Integer，如果 > int.max，那么返回的是BigInteger
        Object result = REDIS_TEMPLATE.opsForValue().get(key);
        if (result == null) {
            return null;
        }
        return Long.valueOf(String.valueOf(result));
    }

    public static  <T> T loadModelFromRedis(String key, Class<T> clazz) {
        JSONObject jsonObject = (JSONObject) REDIS_TEMPLATE.opsForValue().get(key);
        return jsonObject != null ? jsonObject.toJavaObject(clazz) : null;
    }

    public static  <T> List<T> loadArrayFromRedis(String key, Class<T> clazz) {
        JSONArray jsonArray = (JSONArray) REDIS_TEMPLATE.opsForValue().get(key);
        return jsonArray != null ? jsonArray.toJavaList(clazz) : null;
    }
}
