package com.xiaoyaozi.manage;

import cn.hutool.core.thread.ThreadFactoryBuilder;

import java.util.Map;
import java.util.concurrent.*;

/**
 * tip: 重连服务
 *
 * @author xiaoyaozi
 * createTime: 2021-03-25 13:43
 */
public class ImServerReconnectManage {

    private static final ScheduledExecutorService SCHEDULED_EXECUTOR = new ScheduledThreadPoolExecutor(1, ThreadFactoryBuilder.create()
            .setNamePrefix("reconnect-job").setDaemon(true).build());

    private static final Map<String, ScheduledFuture<?>> SCHEDULED_FUTURE_MAP = new ConcurrentHashMap<>(8);

    public static void appendReconnectTask(String serverIp) {
        // 先取消延迟任务
        cancelReconnectTask(serverIp);
        // 重连
        ScheduledFuture<?> scheduledFuture = SCHEDULED_EXECUTOR.scheduleAtFixedRate(() -> ImServerConnect.tryConnectOtherImServer(serverIp),
                0, 10, TimeUnit.SECONDS);
        SCHEDULED_FUTURE_MAP.put(serverIp, scheduledFuture);
    }

    public static void cancelReconnectTask(String serverIp) {
        ScheduledFuture<?> scheduledFuture = SCHEDULED_FUTURE_MAP.get(serverIp);
        if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
            scheduledFuture.cancel(false);
            SCHEDULED_FUTURE_MAP.remove(serverIp);
        }
    }
}
