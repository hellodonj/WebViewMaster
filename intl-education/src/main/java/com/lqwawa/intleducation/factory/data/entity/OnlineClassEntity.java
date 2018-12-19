package com.lqwawa.intleducation.factory.data.entity;

import com.lqwawa.intleducation.base.vo.BaseVo;

import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 在线课堂班级数据
 * @date 2018/05/29 16:08
 * @history v1.0
 * **********************************
 */
public class OnlineClassEntity extends BaseVo{
    // 即将开始
    public static final int PROGRESS_STATUS_IDLE = 0;
    // 正在授课
    public static final int PROGRESS_STATUS_START = 1;
    // 结束授课
    public static final int PROGRESS_STATUS_FINISH = 2;
    // 历史班
    public static final int PROGRESS_STATUS_HISTORY = 3;

    private String organId;
    private String createTime;
    private int totalScore;
    private String teachersOrganName;
    private int progressStatus;
    private String teachersId;
    private String introduce;
    private String courseId;
    private String endTime;
    private String teachersOrganId;
    private int firstId;
    private String startTime;
    private int id;
    private boolean isDelete;
    private int scoreNum;
    private String verifyTime;
    private String thumbnailUrl;
    private String suitObj;
    private String secondName;
    private String name;
    private String firstName;
    private String organName;
    private String createId;
    private int secondId;
    private String classId;
    private String onlineId;
    private String teachersName;
    private String createName;
    private int commentNum;
    private int price;
    private int originalPrice;
    private boolean isDiscount;
    private int joinCount;
    private String classCode;
    private String learnGoal;
    private int verifyStatus;
    private String deleteTime;

    public String getOrganId() {
        return organId;
    }

    public void setOrganId(String organId) {
        this.organId = organId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public String getTeachersOrganName() {
        return teachersOrganName;
    }

    public void setTeachersOrganName(String teachersOrganName) {
        this.teachersOrganName = teachersOrganName;
    }

    public int getProgressStatus() {
        return progressStatus;
    }

    public void setProgressStatus(int progressStatus) {
        this.progressStatus = progressStatus;
    }

    public String getTeachersId() {
        return teachersId;
    }

    public void setTeachersId(String teachersId) {
        this.teachersId = teachersId;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getTeachersOrganId() {
        return teachersOrganId;
    }

    public void setTeachersOrganId(String teachersOrganId) {
        this.teachersOrganId = teachersOrganId;
    }

    public int getFirstId() {
        return firstId;
    }

    public void setFirstId(int firstId) {
        this.firstId = firstId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isIsDelete() {
        return isDelete;
    }

    public void setIsDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }

    public int getScoreNum() {
        return scoreNum;
    }

    public void setScoreNum(int scoreNum) {
        this.scoreNum = scoreNum;
    }

    public String getVerifyTime() {
        return verifyTime;
    }

    public void setVerifyTime(String verifyTime) {
        this.verifyTime = verifyTime;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getSuitObj() {
        return suitObj;
    }

    public void setSuitObj(String suitObj) {
        this.suitObj = suitObj;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getOrganName() {
        return organName;
    }

    public void setOrganName(String organName) {
        this.organName = organName;
    }

    public String getCreateId() {
        return createId;
    }

    public void setCreateId(String createId) {
        this.createId = createId;
    }

    public int getSecondId() {
        return secondId;
    }

    public void setSecondId(int secondId) {
        this.secondId = secondId;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getOnlineId() {
        return onlineId;
    }

    public void setOnlineId(String onlineId) {
        this.onlineId = onlineId;
    }

    public String getTeachersName() {
        return teachersName;
    }

    public void setTeachersName(String teachersName) {
        this.teachersName = teachersName;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public int getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(int commentNum) {
        this.commentNum = commentNum;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(int originalPrice) {
        this.originalPrice = originalPrice;
    }

    public boolean isDiscount() {
        return isDiscount;
    }

    public void setDiscount(boolean discount) {
        isDiscount = discount;
    }

    public int getJoinCount() {
        return joinCount;
    }

    public void setJoinCount(int joinCount) {
        this.joinCount = joinCount;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getLearnGoal() {
        return learnGoal;
    }

    public void setLearnGoal(String learnGoal) {
        this.learnGoal = learnGoal;
    }

    public int getVerifyStatus() {
        return verifyStatus;
    }

    public void setVerifyStatus(int verifyStatus) {
        this.verifyStatus = verifyStatus;
    }

    public String getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(String deleteTime) {
        this.deleteTime = deleteTime;
    }

    /**
     * 是否结束授课
     * @return true 结束授课
     */
    public boolean isGiveFinish(){
        return progressStatus == PROGRESS_STATUS_FINISH;
    }

    /**
     * 是否是历史班
     * @return true 历史班
     */
    public boolean isGiveHistory(){
        return progressStatus == PROGRESS_STATUS_HISTORY;
    }
}
