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
    //学生统计列表
    private String StudentId;
    private String StudentName;
    private String HeadPicUrl;
    private int ExcellentNum;
    private int GoodNum;
    private int FairNum;
    private int FailNum;
    private int NoCorrectNum;
    private int NotCompletedNum;
    private int TeacherSetNormalTaskNum;
    private int TeacherSetTestTaskNum;
    private int TeacherSetExamTaskNum;
    private int StudentCompletedNum;
    private String StudentCompletedRate;
    private int percent;
    public int getTaskTotalNum() {
        return ExcellentNum + GoodNum + FairNum + FailNum + NoCorrectNum + NotCompletedNum;
    }

    public int getTotalMarkNum(){
        return ExcellentNum + GoodNum + FairNum + FailNum;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public String getStudentId() {
        return StudentId;
    }

    public void setStudentId(String studentId) {
        StudentId = studentId;
    }

    public String getStudentName() {
        return StudentName;
    }

    public void setStudentName(String studentName) {
        StudentName = studentName;
    }

    public String getHeadPicUrl() {
        return HeadPicUrl;
    }

    public void setHeadPicUrl(String headPicUrl) {
        HeadPicUrl = headPicUrl;
    }

    public int getExcellentNum() {
        return ExcellentNum;
    }

    public void setExcellentNum(int excellentNum) {
        ExcellentNum = excellentNum;
    }

    public int getGoodNum() {
        return GoodNum;
    }

    public void setGoodNum(int goodNum) {
        GoodNum = goodNum;
    }

    public int getFairNum() {
        return FairNum;
    }

    public void setFairNum(int fairNum) {
        FairNum = fairNum;
    }

    public int getFailNum() {
        return FailNum;
    }

    public void setFailNum(int failNum) {
        FailNum = failNum;
    }

    public int getNoCorrectNum() {
        return NoCorrectNum;
    }

    public void setNoCorrectNum(int noCorrectNum) {
        NoCorrectNum = noCorrectNum;
    }

    public int getNotCompletedNum() {
        return NotCompletedNum;
    }

    public void setNotCompletedNum(int notCompletedNum) {
        NotCompletedNum = notCompletedNum;
    }

    public int getTeacherSetNormalTaskNum() {
        return TeacherSetNormalTaskNum;
    }

    public void setTeacherSetNormalTaskNum(int teacherSetNormalTaskNum) {
        TeacherSetNormalTaskNum = teacherSetNormalTaskNum;
    }

    public int getTeacherSetTestTaskNum() {
        return TeacherSetTestTaskNum;
    }

    public void setTeacherSetTestTaskNum(int teacherSetTestTaskNum) {
        TeacherSetTestTaskNum = teacherSetTestTaskNum;
    }

    public int getTeacherSetExamTaskNum() {
        return TeacherSetExamTaskNum;
    }

    public void setTeacherSetExamTaskNum(int teacherSetExamTaskNum) {
        TeacherSetExamTaskNum = teacherSetExamTaskNum;
    }

    public int getStudentCompletedNum() {
        return StudentCompletedNum;
    }

    public void setStudentCompletedNum(int studentCompletedNum) {
        StudentCompletedNum = studentCompletedNum;
    }

    public String getStudentCompletedRate() {
        return StudentCompletedRate;
    }

    public void setStudentCompletedRate(String studentCompletedRate) {
        StudentCompletedRate = studentCompletedRate;
    }

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
