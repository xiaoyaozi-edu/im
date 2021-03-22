package com.xiaoyaozi.enums;

/**
 * tip: 异常类型
 *
 * @author xiaoyaozi
 * createTime: 2021-03-22 14:34
 */
public enum ImExceptionStatus {

    /**
     * 异常类型原因
     */
    SERVER_NOT_AVAILABLE(4001, "服务端不可用");

    private final int code;
    private final String msg;

    ImExceptionStatus(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }
}
