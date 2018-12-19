package com.lqwawa.intleducation.factory.data.entity.school;

import java.io.Serializable;

/**
 * @author medici
 * @desc 机构列表响应数据
 */
public class OrganResponseVo<T> implements Serializable {

    public static final int SUCCEED = 0;

    public static final int ERROR_UNKNOWN = -1;

    private int total;
    private int code;
    private T organList;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getOrganList() {
        return organList;
    }

    public void setOrganList(T organList) {
        this.organList = organList;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    /**
     * 网络请求是否成功
     * @return true 请求成功 false 请求失败
     */
    public boolean isSucceed(){
        return code == SUCCEED;
    }
}
