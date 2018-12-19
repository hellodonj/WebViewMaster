package com.galaxyschool.app.wawaschool.pojo;

public class SchoolMemberDetail {
    private SchoolMemberModel Model;
    private int ErrorCode;
    private String ErrorMessage;

    public SchoolMemberDetail() {
    }

    public SchoolMemberModel getModel() {
        return Model;
    }

    public void setModel(SchoolMemberModel model) {
        Model = model;
    }

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
}
