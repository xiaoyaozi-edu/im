package com.xiaoyaozi.enums;

/**
 * tip: 消息类型
 *
 * @author xiaoyaozi
 * createTime: 2021-03-20 23:20
 */
public enum ImMessageType {

    /**
     * 消息类型：连接、心跳、消息
     */
    CONNECT(1, "连接"),
    PING(2, "心跳"),
    MESSAGE(3, "消息");

    private final int type;
    private final String msg;

    ImMessageType(int type, String msg) {
        this.type = type;
        this.msg = msg;
    }

    public int getType() {
        return this.type;
    }
}
