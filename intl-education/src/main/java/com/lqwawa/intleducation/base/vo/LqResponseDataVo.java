package com.lqwawa.intleducation.base.vo;

import java.io.Serializable;

/**
 * Created by XChen on 2017/6/29.
 * email:man0fchina@foxmail.com
 */

public class LqResponseDataVo<T> implements Serializable {

    private static final int SUCCEED = 0;

    private LqDataVo<T> Model;
    private boolean HasError;
    private String ErrorMessage;
    private int ErrorCode;

    public LqDataVo<T> getModel() {
        return Model;
    }

    public void setModel(LqDataVo<T> model) {
        Model = model;
    }

    public boolean isHasError() {
        return HasError;
    }

    public void setHasError(boolean hasError) {
        HasError = hasError;
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        ErrorMessage = errorMessage;
    }

    public int getErrorCode() {
        return ErrorCode;
    }

    public void setErrorCode(int errorCode) {
        ErrorCode = errorCode;
    }



    /**
     * 网络请求是否成功
     * @return true 请求成功 false 请求失败
     */
    public boolean isSucceed(){
        return !HasError && ErrorCode == SUCCEED;
    }
}