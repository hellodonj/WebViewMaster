package com.galaxyschool.app.wawaschool.pojo;

import java.io.Serializable;

/**
 * ======================================================
 * Describe:成绩统计辅助实体类
 * ======================================================
 */
public class StatisticBean implements Serializable {
    private int color;
    private String title;
    private int number;
    private int totalNum;
    private int importantNum;
    private int unImportantNum;
    private int classSize;
    private int alreadySetTaskNum;//已布置作业数
    private int statisticType;
    private int learningType;
    private boolean showRightArrow;

    public boolean isShowRightArrow() {
        return showRightArrow;
    }

    public void setShowRightArrow(boolean showRightArrow) {
        this.showRightArrow = showRightArrow;
    }

    public int getLearningType() {
        return learningType;
    }

    public void setLearningType(int learningType) {
        this.learningType = learningType;
    }

    public int getStatisticType() {
        return statisticType;
    }

    public void setStatisticType(int statisticType) {
        this.statisticType = statisticType;
    }

    public int getClassSize() {
        return classSize;
    }

    public void setClassSize(int classSize) {
        this.classSize = classSize;
    }

    public int getAlreadySetTaskNum() {
        return alreadySetTaskNum;
    }

    public void setAlreadySetTaskNum(int alreadySetTaskNum) {
        this.alreadySetTaskNum = alreadySetTaskNum;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public int getImportantNum() {
        return importantNum;
    }

    public void setImportantNum(int importantNum) {
        this.importantNum = importantNum;
    }

    public int getUnImportantNum() {
        return unImportantNum;
    }

    public void setUnImportantNum(int unImportantNum) {
        this.unImportantNum = unImportantNum;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
