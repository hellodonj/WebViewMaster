package com.lqwawa.intleducation.base.vo;

import java.io.Serializable;

/**
 * Created by XChen on 2017/6/29.
 * email:man0fchina@foxmail.com
 */

public class LqResponseVo<T> implements Serializable {
    private T Model;
    private boolean HasError;
    private String ErrorMessage;

    public T getModel() {
        return Model;
    }

    public void setModel(T model) {
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
}