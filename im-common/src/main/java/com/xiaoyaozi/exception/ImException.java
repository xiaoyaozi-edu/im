package com.xiaoyaozi.exception;

import com.xiaoyaozi.enums.ImExceptionStatus;

/**
 * tip: Im异常类
 *
 * @author xiaoyaozi
 * createTime: 2021-03-22 14:15
 */
public class ImException extends BaseException{

    private static final long serialVersionUID = -5833972655525213735L;

    public ImException(Throwable cause) {
        super(cause);
    }

    public ImException(int code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public ImException(String message) {
        super(-1, message);
    }

    public ImException(int code, String format, Object... args) {
        super(code, format, args);
    }

    public static ImException exception(ImExceptionStatus status) {
        return new ImException(status.getCode(), status.getMsg());
    }
}
