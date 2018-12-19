package com.galaxyschool.app.wawaschool.pojo;

import java.util.List;

public class ScoreFormula {

    private String Key;
    private String Value;
    private List<ScoreFormula> Model;
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

    public List<ScoreFormula> getModel() {
        return Model;
    }

    public void setModel(List<ScoreFormula> model) {
        Model = model;
    }

    public ScoreFormula() {
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }
}
