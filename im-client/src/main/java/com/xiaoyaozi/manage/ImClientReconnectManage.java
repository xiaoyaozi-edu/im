package com.xiaoyaozi.manage;

import cn.hutool.core.thread.ThreadFactoryBuilder;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * tip: 重连服务
 *
 * @author xiaoyaozi
 * createTime: 2021-03-25 13:43
 */
public class ImClientReconnectManage {

    private static final ScheduledExecutorService SCHEDULED_EXECUTOR = new ScheduledThreadPoolExecutor(1, ThreadFactoryBuilder.create()
            .setNamePrefix("reconnect-job").setDaemon(true).build());

    private static ScheduledFuture<?> scheduledFuture;

    public static void appendReconnectTask() {
        // 先取消延迟任务
        cancelReconnectTask();
        // 重连
        scheduledFuture = SCHEDULED_EXECUTOR.scheduleAtFixedRate(ImClientConnect::prepareConnectImServer,
                0, 10, TimeUnit.SECONDS);

    }

    public static void cancelReconnectTask() {
        if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
            scheduledFuture.cancel(false);
        }
    }
}
