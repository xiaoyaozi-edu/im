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
    CLIENT_CONNECT(1, "C-S"),
    SERVER_CONNECT(2, "S-S"),
    PING(3, "心跳"),
    MESSAGE(4, "消息");

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
