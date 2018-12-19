package com.galaxyschool.app.wawaschool.pojo;

public class ErrorInfo {
    private String errorCode;
    private String errorMessage;

    public ErrorInfo() {

    }

    public ErrorInfo(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
