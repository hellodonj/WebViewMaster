package com.lqwawa.lqbaselib.net.library;

public class Result {

    private boolean HasError;
    private String ErrorMessage;
    /**
     * 保存server返回的错误码
     */
    private String ErrorCode;

    public Result() {

    }

    public Result(boolean hasError, String errorMessage) {
        HasError = hasError;
        ErrorMessage = errorMessage;
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

    public String getErrorCode() {
        return ErrorCode;
    }

    public void setErrorCode(String errorCode) {
        ErrorCode = errorCode;
    }

    public boolean isSuccess() {
        return !isHasError();
    }

}
