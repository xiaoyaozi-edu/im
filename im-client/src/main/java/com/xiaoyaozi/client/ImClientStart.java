package com.xiaoyaozi.client;

import com.xiaoyaozi.manage.ImClientConnect;
import com.xiaoyaozi.util.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * tip: 启动服务
 *
 * @author xiaoyaozi
 * createTime: 2021-03-26 14:59
 */
@Order(10)
@Component
public class ImClientStart {

    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void postConstruct() {
        // 激活springUtil工具类
        SpringUtil.setApplicationContext(applicationContext);
        // 尝试连接
        ImClientConnect.prepareConnectImServer();
    }
}
