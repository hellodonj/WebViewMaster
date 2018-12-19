package com.lqwawa.lqbaselib.net;

/**
 * @author 作者 shouyi:
 * @version 创建时间：Mar 30, 2015 6:56:08 PM
 *          类说明
 */
public class NetErrorResult {
    boolean HasError;
    String ErrorMessage;

    public NetErrorResult() {

    }

    public NetErrorResult(boolean hasError, String message) {
        HasError = hasError;
        ErrorMessage = message;
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
