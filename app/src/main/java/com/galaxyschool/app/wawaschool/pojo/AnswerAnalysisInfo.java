package com.galaxyschool.app.wawaschool.pojo;

import java.io.Serializable;

/**
 * ======================================================
 * Describe:任务单答案解析数据
 * ======================================================
 */
public class AnswerAnalysisInfo implements Serializable {
    private int Id;
    private int EQType;
    private int SubimtNum;
    private int WrongNum;
    private double AverageScore;
    private String EQId;
    private String CommonError;
    private int EmptyNum;
    public int getId() {
        return Id;
    }

    public int getEmptyNum() {
        return EmptyNum;
    }

    public void setEmptyNum(int emptyNum) {
        EmptyNum = emptyNum;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getEQType() {
        return EQType;
    }

    public void setEQType(int EQType) {
        this.EQType = EQType;
    }

    public int getSubimtNum() {
        return SubimtNum;
    }

    public void setSubimtNum(int subimtNum) {
        SubimtNum = subimtNum;
    }

    public int getWrongNum() {
        return WrongNum;
    }

    public void setWrongNum(int wrongNum) {
        WrongNum = wrongNum;
    }

    public double getAverageScore() {
        return AverageScore;
    }

    public void setAverageScore(double averageScore) {
        AverageScore = averageScore;
    }

    public String getEQId() {
        return EQId;
    }

    public void setEQId(String EQId) {
        this.EQId = EQId;
    }

    public String getCommonError() {
        return CommonError;
    }

    public void setCommonError(String commonError) {
        CommonError = commonError;
    }
}
