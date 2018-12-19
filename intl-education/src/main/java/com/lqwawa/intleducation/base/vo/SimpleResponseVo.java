package com.lqwawa.intleducation.base.vo;


/**
 * Created by taoshiyuan on 15/10/8.
 */
public class SimpleResponseVo extends BaseVo{
    private int code;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
