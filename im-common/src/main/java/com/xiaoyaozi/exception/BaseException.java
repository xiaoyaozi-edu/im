package com.xiaoyaozi.exception;

/**
 * tip: 异常基类
 *
 * @author xiaoyaozi
 * createTime: 2021-03-22 14:18
 */
public class BaseException extends RuntimeException {

    private static final long serialVersionUID = 6430163184298240762L;
    /**
     * 具体异常码
     */
    protected int code;
    /**
     * 异常信息
     */
    protected String message;

    public BaseException(Throwable cause) {
        super(cause);
    }

    public BaseException(int code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public BaseException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BaseException(int code, String format, Object... args) {
        super(String.format(format, args));
        this.code = code;
        this.message = String.format(format, args);
    }

    @Override
    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }
}
