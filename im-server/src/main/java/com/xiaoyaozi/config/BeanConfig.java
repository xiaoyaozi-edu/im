package com.xiaoyaozi.config;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * tip: zk配置
 *
 * @author xiaoyaozi
 * createTime: 2021-03-22 16:31
 */
@Slf4j
@Configuration
public class BeanConfig {

    @Autowired
    private ImServerConfig serverConfig;

    @Bean
    public CuratorFramework zkClient() {
        // ExponentialBackoffRetry 重试策略 间隔时间是2的指数增长，比如第一次等待1s，第二次2s，第三次4s。。。
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(serverConfig.getZkAddress())
                .connectionTimeoutMs(serverConfig.getConnectTimeout())
                .sessionTimeoutMs(serverConfig.getSessionTimeout())
                .retryPolicy(new ExponentialBackoffRetry(1000, 5))
                .build();
        client.start();
        return client;
    }

    @Bean
    public RedisTemplate<String, Object> jsonRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // 1、JdkSerializationRedisSerializer（redis默认） 节省空间，可读性差，效率低
        // 2、Jackson2JsonRedisSerializer 浪费空间，可读性好，效率高
        // 3、FastJsonRedisSerializer 最省空间，可读性好，效率高，bug多，取出model和list需要手动转一下
        redisTemplate.setValueSerializer(new FastJsonRedisSerializer<>(Object.class));
        return redisTemplate;
    }

}
