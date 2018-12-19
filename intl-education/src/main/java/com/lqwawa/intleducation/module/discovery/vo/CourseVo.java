package com.lqwawa.intleducation.module.discovery.vo;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.base.utils.StringUtils;
import com.lqwawa.intleducation.base.vo.BaseVo;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.module.learn.vo.ExamVo;

import java.util.List;

/**
 * Created by XChen on 2016/11/8.
 * email:man0fchina@foxmail.com
 */

public class CourseVo extends BaseVo {
    private String weekCount;// 3,
    private String organId;// 1,
    private int progress;// 1,
    private String createTime;// "2016-11-04 11:17:38",
    private int totalScore;// 0,
    private int progressStatus;// 1,
    private String passScore;// 0,
    private String endTime;// "",
    private String startTime;// "2016-11-04 00:00:00",
    private String isDelete;// false,
    private String id;// 193,
    private String level;// "28.29",
    private String scoreCriteria;// "",
    private String examWeight;// 0,
    private String verifyTime;// "2016-11-04 13:47:10",
    private String thumbnailUrl;// "http://192.168.99.181/image/2016/11/04/5be99079-8d28-448d-a3c2-ae8d4fe22ebd.jpg",
    private String suitObj;// "freshman",
    private String name;// "test02",
    private String studentNum;// 0,
    private String levelName;// "语言学习|SAT",
    private String organName;// "合肥大学",
    private String createId;// 166,
    private String classifId;// 29,
    private int status;// 0,
    private String teachersName;// "测试01",
    private String createName;// "测试01",
    private int commentNum;// 0,
    private String praiseNum;// 0,
    private int originalPrice;
    private boolean isDiscount;
    private long price;// 50,
    private int payType;// 1,
    private String trainWeight;// 0,
    private String learnGoal;// "完成",
    private String verifyStatus;// 1,
    private String introduction;// "test",
    private String deleteTime;// ""
    private int courseStar;
    private String firstTitle;
    private String secondTitle;
    private List<ExamVo> examList;
    private List<ChapterVo> chapList;
    private String courseId;
    private String courseName;
    private String scoreCriteriaEn;
    // @date   :2018/4/11 0011 下午 3:40
    // @func   :V5.5版本新添的字段
    //讲师
    private String teachersId;// "|166|",
    //助教
    private String tutorId;
    //辅导老师
    private String counselorId;

    // 是否选中
    private boolean tag;
    // 是否指定到班级
    private boolean inClass;
    // 绑定的班级,学校
    private String bindSchoolId;
    private String bindClassId;

    public boolean isTag() {
        return tag;
    }

    public void setTag(boolean tag) {
        this.tag = tag;
    }

    public String getCounselorId() {
        // 如果没有辅导老师，就返回""
        if(EmptyUtil.isEmpty(counselorId)) counselorId = "";
        return counselorId;
    }

    public void setCounselorId(String counselorId) {
        this.counselorId = counselorId;
    }

    public String getTutorId() {
        return tutorId;
    }

    public void setTutorId(String tutorId) {
        this.tutorId = tutorId;
    }

    public String getClassifId() {
        return classifId;
    }

    public void setClassifId(String classifId) {
        this.classifId = classifId;
    }

    public int getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(int commentNum) {
        this.commentNum = commentNum;
    }

    public String getCreateId() {
        return createId;
    }

    public void setCreateId(String createId) {
        this.createId = createId;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(String deleteTime) {
        this.deleteTime = deleteTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getExamWeight() {
        return examWeight;
    }

    public void setExamWeight(String examWeight) {
        this.examWeight = examWeight;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }

    public String getLearnGoal() {
        return learnGoal;
    }

    public void setLearnGoal(String learnGoal) {
        this.learnGoal = learnGoal;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getPassScore() {
        return passScore;
    }

    public void setPassScore(String passScore) {
        this.passScore = passScore;
    }

    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public String getPraiseNum() {
        return praiseNum;
    }

    public void setPraiseNum(String praiseNum) {
        this.praiseNum = praiseNum;
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

    public long getPrice() {
        if (AppConfig.BaseConfig.needShowPay()) {
            if (payType == 0) {
                return 0;
            } else {
                return price;
            }
        } else {
            return 0;
        }
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getProgressStatus() {
        return progressStatus;
    }

    public void setProgressStatus(int progressStatus) {
        this.progressStatus = progressStatus;
    }

    public String getScoreCriteria() {
        if(StringUtils.languageIsEnglish()){
            return getScoreCriteriaEn();
        }else {
            return scoreCriteria;
        }
    }

    public void setScoreCriteria(String scoreCriteria) {
        this.scoreCriteria = scoreCriteria;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStudentNum() {
        return studentNum;
    }

    public void setStudentNum(String studentNum) {
        this.studentNum = studentNum;
    }

    public String getSuitObj() {
        return suitObj;
    }

    public void setSuitObj(String suitObj) {
        this.suitObj = suitObj;
    }

    public String getTeachersId() {
        return teachersId;
    }

    public void setTeachersId(String teachersId) {
        this.teachersId = teachersId;
    }

    public String getTeachersName() {
        return teachersName;
    }

    public void setTeachersName(String teachersName) {
        this.teachersName = teachersName;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public String getTrainWeight() {
        return trainWeight;
    }

    public void setTrainWeight(String trainWeight) {
        this.trainWeight = trainWeight;
    }

    public String getVerifyStatus() {
        return verifyStatus;
    }

    public void setVerifyStatus(String verifyStatus) {
        this.verifyStatus = verifyStatus;
    }

    public String getVerifyTime() {
        return verifyTime;
    }

    public void setVerifyTime(String verifyTime) {
        this.verifyTime = verifyTime;
    }

    public String getWeekCount() {
        return weekCount;
    }

    public void setWeekCount(String weekCount) {
        this.weekCount = weekCount;
    }

    public int getCourseStar() {
        return courseStar;
    }

    public void setCourseStar(int courseStar) {
        this.courseStar = courseStar;
    }

    public String getFirstTitle() {
        return firstTitle;
    }

    public void setFirstTitle(String firstTitle) {
        this.firstTitle = firstTitle;
    }

    public String getSecondTitle() {
        return secondTitle;
    }

    public void setSecondTitle(String secondTitle) {
        this.secondTitle = secondTitle;
    }

    public List<ExamVo> getExamList() {
        return examList;
    }

    public void setExamList(List<ExamVo> examList) {
        this.examList = examList;
    }

    public List<ChapterVo> getChapList() {
        return chapList;
    }

    public void setChapList(List<ChapterVo> chapList) {
        this.chapList = chapList;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }


    public String getScoreCriteriaEn() {
        return scoreCriteriaEn;
    }

    public void setScoreCriteriaEn(String scoreCriteriaEn) {
        this.scoreCriteriaEn = scoreCriteriaEn;
    }

    public boolean isInClass() {
        return inClass;
    }

    public void setInClass(boolean inClass) {
        this.inClass = inClass;
    }

    public String getBindSchoolId() {
        return bindSchoolId;
    }

    public void setBindSchoolId(String bindSchoolId) {
        this.bindSchoolId = bindSchoolId;
    }

    public String getBindClassId() {
        return bindClassId;
    }

    public void setBindClassId(String bindClassId) {
        this.bindClassId = bindClassId;
    }
}
