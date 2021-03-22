package com.xiaoyaozi.route;

import com.xiaoyaozi.enums.ImExceptionStatus;
import com.xiaoyaozi.exception.ImException;

import java.util.List;

/**
 * tip: 路由策略
 *
 * @author xiaoyaozi
 * createTime: 2021-03-22 14:01
 */
public abstract class RouteStrategy {

    List<String> serverIpList = null;

    /**
     * tip: 根据key路由到某个服务器
     *
     * @param key key
     * @return String 服务器ip
     * @author xiaoyaozi
     * createTime: 2021-03-23 00:20
     */
    public abstract String routeServerIp(Long key);

    protected void checkServerIsAvailable() {
        if (serverIpList == null || serverIpList.size() == 0) {
            throw ImException.exception(ImExceptionStatus.SERVER_NOT_AVAILABLE);
        }
    }
}
