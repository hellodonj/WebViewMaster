package com.lqwawa.intleducation.factory.data.entity;

import com.lqwawa.intleducation.base.vo.BaseVo;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;

import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 班级详情信息
 * @date 2018/06/01 11:45
 * @history v1.0
 * **********************************
 */
public class ClassDetailEntity extends BaseVo{

    public static final int SUCCEED = 0;

    public static final int ERROR_UNKNOWN = -1;

    private int code;
    private boolean isJoin;
    private String message;
    private List<CourseVo> relatedCourse;
    // 推荐课程
    private List<RelatedCourseBean> tjCourse;
    private List<DataBean> data;
    private ParamBean param;
    private List<TeacherBean> teacher;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isIsJoin() {
        return isJoin;
    }

    public void setIsJoin(boolean isJoin) {
        this.isJoin = isJoin;
    }

    public List<CourseVo> getRelatedCourse() {
        return relatedCourse;
    }

    public void setRelatedCourse(List<CourseVo> relatedCourse) {
        this.relatedCourse = relatedCourse;
    }

    public List<RelatedCourseBean> getTjCourse() {
        return tjCourse;
    }

    public void setTjCourse(List<RelatedCourseBean> tjCourse) {
        this.tjCourse = tjCourse;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public ParamBean getParam() {
        return param;
    }

    public void setParam(ParamBean param) {
        this.param = param;
    }

    public List<TeacherBean> getTeacher() {
        return teacher;
    }

    public void setTeacher(List<TeacherBean> teacher) {
        this.teacher = teacher;
    }

    /**
     * 网络请求是否成功
     * @return true 请求成功 false 请求失败
     */
    public boolean isSucceed(){
        return code == SUCCEED;
    }

    public static class RelatedCourseBean extends BaseVo{
        private int progress;
        private String schoolBookId;
        private int totalScore;
        private int progressStatus;
        private String tutorName;
        private String endTime;
        private boolean isDelete;
        private String startTime;
        private String level;
        private String scoreCriteria;
        private int examWeight;
        private int paramThreeId;
        private String verifyTime;
        private String levelName;
        private String organName;
        private String createId;
        private int classifId;
        private int status;
        private String onlineId;
        private String counselorName;
        private String teachersName;
        private String paramTwoName;
        private String createName;
        private int commentNum;
        private int praiseNum;
        private int price;
        private int payType;
        private String paramOneId;
        private String introduction;
        private String deleteTime;
        private int weekCount;
        private String organId;
        private String createTime;
        private String teachersId;
        private int passScore;
        private int id;
        private String paramFourName;
        private String organCounseId;
        private int scoreNum;
        private String paramOneName;
        private String thumbnailUrl;
        private String name;
        private String suitObj;
        private int studentNum;
        private int bookPrice;
        private int paramTwoId;
        private int courseType;
        private int paramFourId;
        private int schoolBookState;
        private String paramThreeName;
        private boolean haveBook;
        private String tutorId;
        private int trainWeight;
        private String learnGoal;
        private String organCounseName;
        private int verifyStatus;
        private String counselorId;

        public int getProgress() {
            return progress;
        }

        public void setProgress(int progress) {
            this.progress = progress;
        }

        public String getSchoolBookId() {
            return schoolBookId;
        }

        public void setSchoolBookId(String schoolBookId) {
            this.schoolBookId = schoolBookId;
        }

        public int getTotalScore() {
            return totalScore;
        }

        public void setTotalScore(int totalScore) {
            this.totalScore = totalScore;
        }

        public int getProgressStatus() {
            return progressStatus;
        }

        public void setProgressStatus(int progressStatus) {
            this.progressStatus = progressStatus;
        }

        public String getTutorName() {
            return tutorName;
        }

        public void setTutorName(String tutorName) {
            this.tutorName = tutorName;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public boolean isIsDelete() {
            return isDelete;
        }

        public void setIsDelete(boolean isDelete) {
            this.isDelete = isDelete;
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

        public String getScoreCriteria() {
            return scoreCriteria;
        }

        public void setScoreCriteria(String scoreCriteria) {
            this.scoreCriteria = scoreCriteria;
        }

        public int getExamWeight() {
            return examWeight;
        }

        public void setExamWeight(int examWeight) {
            this.examWeight = examWeight;
        }

        public int getParamThreeId() {
            return paramThreeId;
        }

        public void setParamThreeId(int paramThreeId) {
            this.paramThreeId = paramThreeId;
        }

        public String getVerifyTime() {
            return verifyTime;
        }

        public void setVerifyTime(String verifyTime) {
            this.verifyTime = verifyTime;
        }

        public String getLevelName() {
            return levelName;
        }

        public void setLevelName(String levelName) {
            this.levelName = levelName;
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

        public int getClassifId() {
            return classifId;
        }

        public void setClassifId(int classifId) {
            this.classifId = classifId;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getOnlineId() {
            return onlineId;
        }

        public void setOnlineId(String onlineId) {
            this.onlineId = onlineId;
        }

        public String getCounselorName() {
            return counselorName;
        }

        public void setCounselorName(String counselorName) {
            this.counselorName = counselorName;
        }

        public String getTeachersName() {
            return teachersName;
        }

        public void setTeachersName(String teachersName) {
            this.teachersName = teachersName;
        }

        public String getParamTwoName() {
            return paramTwoName;
        }

        public void setParamTwoName(String paramTwoName) {
            this.paramTwoName = paramTwoName;
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

        public int getPraiseNum() {
            return praiseNum;
        }

        public void setPraiseNum(int praiseNum) {
            this.praiseNum = praiseNum;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        public int getPayType() {
            return payType;
        }

        public void setPayType(int payType) {
            this.payType = payType;
        }

        public String getParamOneId() {
            return paramOneId;
        }

        public void setParamOneId(String paramOneId) {
            this.paramOneId = paramOneId;
        }

        public String getIntroduction() {
            return introduction;
        }

        public void setIntroduction(String introduction) {
            this.introduction = introduction;
        }

        public String getDeleteTime() {
            return deleteTime;
        }

        public void setDeleteTime(String deleteTime) {
            this.deleteTime = deleteTime;
        }

        public int getWeekCount() {
            return weekCount;
        }

        public void setWeekCount(int weekCount) {
            this.weekCount = weekCount;
        }

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

        public String getTeachersId() {
            return teachersId;
        }

        public void setTeachersId(String teachersId) {
            this.teachersId = teachersId;
        }

        public int getPassScore() {
            return passScore;
        }

        public void setPassScore(int passScore) {
            this.passScore = passScore;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getParamFourName() {
            return paramFourName;
        }

        public void setParamFourName(String paramFourName) {
            this.paramFourName = paramFourName;
        }

        public String getOrganCounseId() {
            return organCounseId;
        }

        public void setOrganCounseId(String organCounseId) {
            this.organCounseId = organCounseId;
        }

        public int getScoreNum() {
            return scoreNum;
        }

        public void setScoreNum(int scoreNum) {
            this.scoreNum = scoreNum;
        }

        public String getParamOneName() {
            return paramOneName;
        }

        public void setParamOneName(String paramOneName) {
            this.paramOneName = paramOneName;
        }

        public String getThumbnailUrl() {
            return thumbnailUrl;
        }

        public void setThumbnailUrl(String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSuitObj() {
            return suitObj;
        }

        public void setSuitObj(String suitObj) {
            this.suitObj = suitObj;
        }

        public int getStudentNum() {
            return studentNum;
        }

        public void setStudentNum(int studentNum) {
            this.studentNum = studentNum;
        }

        public int getBookPrice() {
            return bookPrice;
        }

        public void setBookPrice(int bookPrice) {
            this.bookPrice = bookPrice;
        }

        public int getParamTwoId() {
            return paramTwoId;
        }

        public void setParamTwoId(int paramTwoId) {
            this.paramTwoId = paramTwoId;
        }

        public int getCourseType() {
            return courseType;
        }

        public void setCourseType(int courseType) {
            this.courseType = courseType;
        }

        public int getParamFourId() {
            return paramFourId;
        }

        public void setParamFourId(int paramFourId) {
            this.paramFourId = paramFourId;
        }

        public int getSchoolBookState() {
            return schoolBookState;
        }

        public void setSchoolBookState(int schoolBookState) {
            this.schoolBookState = schoolBookState;
        }

        public String getParamThreeName() {
            return paramThreeName;
        }

        public void setParamThreeName(String paramThreeName) {
            this.paramThreeName = paramThreeName;
        }

        public boolean isHaveBook() {
            return haveBook;
        }

        public void setHaveBook(boolean haveBook) {
            this.haveBook = haveBook;
        }

        public String getTutorId() {
            return tutorId;
        }

        public void setTutorId(String tutorId) {
            this.tutorId = tutorId;
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

        public String getOrganCounseName() {
            return organCounseName;
        }

        public void setOrganCounseName(String organCounseName) {
            this.organCounseName = organCounseName;
        }

        public int getVerifyStatus() {
            return verifyStatus;
        }

        public void setVerifyStatus(int verifyStatus) {
            this.verifyStatus = verifyStatus;
        }

        public String getCounselorId() {
            return counselorId;
        }

        public void setCounselorId(String counselorId) {
            this.counselorId = counselorId;
        }
    }

    public static class ParamBean extends BaseVo{
        private String level;
        private String paramTwoId;
        private String paramThreeId;
        private String parentId;
        private String relationName;

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getParamTwoId() {
            return paramTwoId;
        }

        public void setParamTwoId(String paramTwoId) {
            this.paramTwoId = paramTwoId;
        }

        public String getParamThreeId() {
            return paramThreeId;
        }

        public void setParamThreeId(String paramThreeId) {
            this.paramThreeId = paramThreeId;
        }

        public String getParentId() {
            return parentId;
        }

        public void setParentId(String parentId) {
            this.parentId = parentId;
        }

        public String getRelationName() {
            return relationName;
        }

        public void setRelationName(String relationName) {
            this.relationName = relationName;
        }
    }

    public static class DataBean extends BaseVo{
        // 即将开始
        public static final int PROGRESS_STATUS_IDLE = 0;
        // 正在授课
        public static final int PROGRESS_STATUS_START = 1;
        // 结束授课
        public static final int PROGRESS_STATUS_FINISH = 2;
        // 授课历史
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
         * 是否结束授课
         * @return true 结束授课
         */
        public boolean isGiveHistory(){
            return progressStatus == PROGRESS_STATUS_HISTORY;
        }
    }

    public static class TeacherBean extends BaseVo{
        private String thumbnail;
        private String userName;
        private String memberId;

        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getMemberId() {
            return memberId;
        }

        public void setMemberId(String memberId) {
            this.memberId = memberId;
        }
    }
}
