package com.xiaoyaozi.config;

import com.xiaoyaozi.manage.ImServerChannelManage;
import com.xiaoyaozi.util.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.annotation.PostConstruct;

/**
 * tip: 其他配置项
 *
 * @author xiaoyaozi
 * createTime: 2021-03-24 10:37
 */
@Order(10)
@Configuration
public class OtherConfig {

    @Value("${im.ping.timeout}")
    private Integer pingTimeout;

    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void postConstruct() {
        // 注入pingTimeout
        ImServerChannelManage.PING_TIMEOUT = pingTimeout;
        // 激活springutil工具类
        SpringUtil.setApplicationContext(applicationContext);
    }
}
