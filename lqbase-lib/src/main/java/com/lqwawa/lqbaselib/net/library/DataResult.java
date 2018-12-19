package com.lqwawa.lqbaselib.net.library;

public class DataResult {

    public DataResult() {

    }

    public DataResult(int errorCode, String errorMessage) {
        ErrorCode = errorCode;
        ErrorMessage = errorMessage;
    }

    private int ErrorCode;
    private String ErrorMessage;

    public int getErrorCode() {
        return ErrorCode;
    }

    public void setErrorCode(int errorCode) {
        ErrorCode = errorCode;
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        ErrorMessage = errorMessage;
    }

    public boolean isSuccess() {
        return ErrorCode == 0;
    }

}
