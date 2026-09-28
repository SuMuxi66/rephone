package com.rephone.common.exception;

/** 业务异常：code 会原样透传到统一返回结构的 code 字段。 */
public class BizException extends RuntimeException {

    private final int code;

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
