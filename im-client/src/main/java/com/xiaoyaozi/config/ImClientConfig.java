package com.xiaoyaozi.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * tip: yml参数配置项
 *
 * @author xiaoyaozi
 * createTime: 2021-03-26 09:50
 */
@Getter
@Configuration
public class ImClientConfig {

    @Value("${user.account}")
    private String account;
    @Value("${user.password}")
    private String password;
    /**
     * 最大重连次数
     */
    @Value("${im.reconnect.max-count}")
    private int reconnectMaxCount;

}
