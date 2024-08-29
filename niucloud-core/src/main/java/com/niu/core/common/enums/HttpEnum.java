package com.niu.core.common.enums;

public enum HttpEnum {

    SUCCESS(1, "操作成功"),
    Fail(0, "操作失败");


    private final int code;
    private final String msg;

    HttpEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 获取code
     * @return
     */
    public int getCode() {
        return this.code;
    }

    /**
     * 获取msg
     * @return
     */
    public String getMsg() {
        return this.msg;
    }

}
