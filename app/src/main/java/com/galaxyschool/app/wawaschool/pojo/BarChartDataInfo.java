package com.galaxyschool.app.wawaschool.pojo;

import java.io.Serializable;

/**
 * ======================================================
 * Describe:任务单柱状图数据
 * ======================================================
 */
public class BarChartDataInfo implements Serializable {
    private String MemberId;
    private String RealName;
    private String NickName;
    private String HeadPicUrl;
    private String EQAnswer;
    private double EQScore;
    private int commonAnswerCount;

    public int getCommonAnswerCount() {
        return commonAnswerCount;
    }

    public void setCommonAnswerCount(int commonAnswerCount) {
        this.commonAnswerCount = commonAnswerCount;
    }

    public String getMemberId() {
        return MemberId;
    }

    public void setMemberId(String memberId) {
        MemberId = memberId;
    }

    public String getRealName() {
        return RealName;
    }

    public void setRealName(String realName) {
        RealName = realName;
    }

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String nickName) {
        NickName = nickName;
    }

    public String getHeadPicUrl() {
        return HeadPicUrl;
    }

    public void setHeadPicUrl(String headPicUrl) {
        HeadPicUrl = headPicUrl;
    }

    public String getEQAnswer() {
        return EQAnswer;
    }

    public void setEQAnswer(String EQAnswer) {
        this.EQAnswer = EQAnswer;
    }

    public double getEQScore() {
        return EQScore;
    }

    public void setEQScore(double EQScore) {
        this.EQScore = EQScore;
    }
}
