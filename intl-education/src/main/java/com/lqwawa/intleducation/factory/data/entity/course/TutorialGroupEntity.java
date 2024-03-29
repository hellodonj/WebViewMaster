package com.lqwawa.intleducation.factory.data.entity.course;

import java.io.Serializable;

/**
 * @author mrmedici
 * @desc 帮辅群的实体
 *
 */
public class TutorialGroupEntity implements Serializable {
    private boolean addedTutor;
    private String markingPrice;
    private String courseId;
    private String HeadPicUrl;
    private int type;
    private String createName;
    private int taskNum;
    private String createId;
    private float starLevel; //星级水平



    // 字段转换
    public void buildEntity(){
        this.addedTutor = hasAttention;
        this.HeadPicUrl = headPic;
        this.markingPrice = markingPriceStr;
        this.createId = memberId;
        this.createName = tutorName;
        this.taskNum = markingNum;
    }

    // 字段转换 班级帮辅
    public void buildClassTutor() {
        this.addedTutor = hasAttention;
//        this.HeadPicUrl = HeadPicUrl;
        this.createId = memberId;
        this.createName = realName;
//        this.taskNum = taskNum;
//        this.markingPrice = markingPrice;
    }



    public boolean isAddedTutor() {
        return addedTutor;
    }

    public void setAddedTutor(boolean addedTutor) {
        this.addedTutor = addedTutor;
    }

    public String getMarkingPrice() {
        return markingPrice;
    }

    public void setMarkingPrice(String markingPrice) {
        this.markingPrice = markingPrice;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getHeadPicUrl() {
        return HeadPicUrl;
    }

    public void setHeadPicUrl(String headPicUrl) {
        HeadPicUrl = headPicUrl;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public int getTaskNum() {
        return taskNum;
    }

    public void setTaskNum(int taskNum) {
        this.taskNum = taskNum;
    }

    public String getCreateId() {
        return createId;
    }

    public void setCreateId(String createId) {
        this.createId = createId;
    }

    public float getStarLevel() {
        return starLevel;
    }

    public void setStarLevel(float starLevel) {
        this.starLevel = starLevel;
    }

    private boolean hasAttention;
    private String headPic;
    private String level;
    private String levelName;
    private int markingNum;
    private String markingPriceStr;
    private String memberId;
    private String tutorName;
    private String tutorOrganId;
    private String tutorOrganName;
    private int verifyStatus;

    public boolean isHasAttention() {
        return hasAttention;
    }

    public void setHasAttention(boolean hasAttention) {
        this.hasAttention = hasAttention;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
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

    public int getMarkingNum() {
        return markingNum;
    }

    public void setMarkingNum(int markingNum) {
        this.markingNum = markingNum;
    }

    public String getMarkingPriceStr() {
        return markingPriceStr;
    }

    public void setMarkingPriceStr(String markingPriceStr) {
        this.markingPriceStr = markingPriceStr;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getTutorName() {
        return tutorName;
    }

    public void setTutorName(String tutorName) {
        this.tutorName = tutorName;
    }

    public String getTutorOrganId() {
        return tutorOrganId;
    }

    public void setTutorOrganId(String tutorOrganId) {
        this.tutorOrganId = tutorOrganId;
    }

    public String getTutorOrganName() {
        return tutorOrganName;
    }

    public void setTutorOrganName(String tutorOrganName) {
        this.tutorOrganName = tutorOrganName;
    }

    public int getVerifyStatus() {
        return verifyStatus;
    }

    public void setVerifyStatus(int verifyStatus) {
        this.verifyStatus = verifyStatus;
    }

//    private String headPicUrl;
//    private String memberId;
//    private int taskNum;
    private String realName;
//    private boolean hasAttention;


    public String getRealName() {
        return realName;
    }

    public TutorialGroupEntity setRealName(String realName) {
        this.realName = realName;
        return this;
    }
}
