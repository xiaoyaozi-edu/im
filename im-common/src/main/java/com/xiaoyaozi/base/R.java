package com.xiaoyaozi.base;

import lombok.Getter;
import lombok.Setter;

/**
 * tip: 通用响应类
 *
 * @author xiaoyaozi
 * createTime: 2021-03-23 15:28
 */
@Setter
@Getter
public class R<T> {

    public static final int SUCCESS_CODE = 0;
    public static final int FAIL_CODE = -1;
    public static final String DEF_ERROR_MESSAGE = "系统繁忙，请稍候再试";

    /**
     * 调用是否成功标识，0：成功，-1:系统繁忙，此时请开发者稍候再试 详情见[ExceptionCode]
     */
    private int code;
    /**
     * 调用结果
     */
    private T data;
    /**
     * 结果消息，如果调用成功，消息通常为空T
     */
    private String msg;
    /**
     * 响应时间
     */
    private long timestamp;

    public R(int code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
        this.timestamp = System.currentTimeMillis();
    }

    public Boolean isSuccess() {
        return this.code == SUCCESS_CODE || this.code == 200;
    }

    public static <E> R<E> success(E data) {
        return new R<>(SUCCESS_CODE, data, "ok");
    }

    public static <E> R<E> success(E data, String msg) {
        return new R<>(SUCCESS_CODE, data, msg);
    }

    public static <E> R<E> fail(String msg) {
        return fail(FAIL_CODE, msg);
    }

    public static <E> R<E> fail(int code, String msg) {
        return new R<>(code, null, (msg == null || msg.isEmpty()) ? DEF_ERROR_MESSAGE : msg);
    }
}
