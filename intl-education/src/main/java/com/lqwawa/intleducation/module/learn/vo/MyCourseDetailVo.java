package com.lqwawa.intleducation.module.learn.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;

/**
 * Created by XChen on 2016/11/28.
 * email:man0fchina@foxmail.com
 */

public class MyCourseDetailVo extends BaseVo {

    /**
     * name : 化学与社会
     * id : 166
     * progress : 5
     * startTime : 1472400000000
     * level : 28.29
     * levelName : 语言学习|SAT
     * status : 0
     * endTime : null
     * createId : 4
     * passScore : 70
     * organId : 1
     * organName : 合肥大学
     * teachersId : |4|,|7|
     * commentNum : 14
     * totalScore : 44
     * weekCount : 6
     * price : 100
     * createName : cx1老师
     * studentNum : 3
     * classifId : 29
     * isDelete : false
     * examWeight : 60
     * praiseNum : 0
     * createTime : 1472410878000
     * deleteTime : null
     * scoreCriteria : 总评成绩60分以上为合格：包括客观题与主观题
     * thumbnailUrl : http://192.168.99.181/course/interCourse/img/2016/08/29/0d5bb356-3b12-4992-800c-83061a608690.jpg
     * introduction : 化学与社会先修课是通识课程，教学内容与高等学校本科阶段的通识课程内容相同。主要围绕9个专题展开，包括“化学发展简史”。
     * teachersName : 李老师,陈老师
     * progressStatus : 1
     * verifyStatus : 1
     * trainWeight : 40
     * learnGoal : 掌握高中化学基础知识
     * suitObj : 高中生
     * payType : 1
     * verifyTime : 1472410936000
     */

    private String name;
    private String id;
    private int progress;
    private String startTime;
    private String level;
    private String levelName;
    private int status;
    private String endTime;
    private String createId;
    private int passScore;
    private String organId;
    private String organName;
    private String teachersId;
    private int commentNum;
    private int totalScore;
    private int weekCount;
    private int price;
    private String createName;
    private int studentNum;
    private int classifId;
    private boolean isDelete;
    private int examWeight;
    private int praiseNum;
    private long createTime;
    private String deleteTime;
    private String scoreCriteria;
    private String thumbnailUrl;
    private String introduction;
    private String teachersName;
    private int progressStatus;
    private int verifyStatus;
    private int trainWeight;
    private String learnGoal;
    private String suitObj;
    private int payType;
    private long verifyTime;
    private boolean isCollect;
    private String chapterName;
    private String sectionName;

    public boolean isIsCollect() {
        return isCollect;
    }

    public void setIsCollect(boolean collect) {
        isCollect = collect;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getCreateId() {
        return createId;
    }

    public void setCreateId(String createId) {
        this.createId = createId;
    }

    public int getPassScore() {
        return passScore;
    }

    public void setPassScore(int passScore) {
        this.passScore = passScore;
    }

    public String getOrganId() {
        return organId;
    }

    public void setOrganId(String organId) {
        this.organId = organId;
    }

    public String getOrganName() {
        return organName;
    }

    public void setOrganName(String organName) {
        this.organName = organName;
    }

    public String getTeachersId() {
        return teachersId;
    }

    public void setTeachersId(String teachersId) {
        this.teachersId = teachersId;
    }

    public int getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(int commentNum) {
        this.commentNum = commentNum;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public int getWeekCount() {
        return weekCount;
    }

    public void setWeekCount(int weekCount) {
        this.weekCount = weekCount;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public int getStudentNum() {
        return studentNum;
    }

    public void setStudentNum(int studentNum) {
        this.studentNum = studentNum;
    }

    public int getClassifId() {
        return classifId;
    }

    public void setClassifId(int classifId) {
        this.classifId = classifId;
    }

    public boolean isIsDelete() {
        return isDelete;
    }

    public void setIsDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }

    public int getExamWeight() {
        return examWeight;
    }

    public void setExamWeight(int examWeight) {
        this.examWeight = examWeight;
    }

    public int getPraiseNum() {
        return praiseNum;
    }

    public void setPraiseNum(int praiseNum) {
        this.praiseNum = praiseNum;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(String deleteTime) {
        this.deleteTime = deleteTime;
    }

    public String getScoreCriteria() {
        return scoreCriteria;
    }

    public void setScoreCriteria(String scoreCriteria) {
        this.scoreCriteria = scoreCriteria;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getTeachersName() {
        return teachersName;
    }

    public void setTeachersName(String teachersName) {
        this.teachersName = teachersName;
    }

    public int getProgressStatus() {
        return progressStatus;
    }

    public void setProgressStatus(int progressStatus) {
        this.progressStatus = progressStatus;
    }

    public int getVerifyStatus() {
        return verifyStatus;
    }

    public void setVerifyStatus(int verifyStatus) {
        this.verifyStatus = verifyStatus;
    }

    public int getTrainWeight() {
        return trainWeight;
    }

    public void setTrainWeight(int trainWeight) {
        this.trainWeight = trainWeight;
    }

    public String getLearnGoal() {
        return learnGoal;
    }

    public void setLearnGoal(String learnGoal) {
        this.learnGoal = learnGoal;
    }

    public String getSuitObj() {
        return suitObj;
    }

    public void setSuitObj(String suitObj) {
        this.suitObj = suitObj;
    }

    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public long getVerifyTime() {
        return verifyTime;
    }

    public void setVerifyTime(long verifyTime) {
        this.verifyTime = verifyTime;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }
}
