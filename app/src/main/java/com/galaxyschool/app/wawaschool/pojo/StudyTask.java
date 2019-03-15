package com.galaxyschool.app.wawaschool.pojo;

import java.io.Serializable;

/**
 * Created by Administrator on 2016.06.22.
 * ��������
 */
public class StudyTask implements Serializable{
    private int Id;
    private String TaskCreateId;
    private String TaskCreateName;
    private String SchoolId;
    private String SchoolName;
    private String ClassId;
    private String ClassName;
    private int Type;
    private String TaskTitle;
    private int TaskNum;
    private int FinishTaskCount;
    private String ResId;
    private String DiscussContent;//��������
    private String StartTime;
    private String EndTime;
    private String CreateId;
    private String CreateName;
    private String CreateTime;
    private String UpdateId;
    private String UpdateName;
    private String UpdateTime;
    private boolean Deleted;
    private int CommentCount;
    //资源地址
    private String ResUrl;
    //资源缩略图地址
    private String ResThumbnailUrl;
    //页面分享地址
    private String ShareUrl;
    private String WorkOrderId;

    //英文写作需要返回的字段
    private String WritingRequire;
    private int MarkFormula;
    private int WordCountMin;
    private int WordCountMax;

    private String ResAuthor;
    private String CollectSchoolId;
    private int ScoringRule; //1:十分制  2:百分制
    private int ResCourseId;
    private int ResPropType;
    private int RepeatCourseCompletionMode;
    private String CourseId;
    private String CourseName;

    public String getCourseId() {
        return CourseId;
    }

    public void setCourseId(String courseId) {
        CourseId = courseId;
    }

    public String getCourseName() {
        return CourseName;
    }

    public void setCourseName(String courseName) {
        CourseName = courseName;
    }

    public int getRepeatCourseCompletionMode() {
        return RepeatCourseCompletionMode;
    }

    public void setRepeatCourseCompletionMode(int repeatCourseCompletionMode) {
        RepeatCourseCompletionMode = repeatCourseCompletionMode;
    }

    public int getResPropType() {
        return ResPropType;
    }

    public void setResPropType(int resPropType) {
        ResPropType = resPropType;
    }

    public int getResCourseId() {
        return ResCourseId;
    }

    public void setResCourseId(int resCourseId) {
        ResCourseId = resCourseId;
    }

    public int getScoringRule() {
        return ScoringRule;
    }

    public void setScoringRule(int scoringRule) {
        ScoringRule = scoringRule;
    }

    public String getResAuthor() {
        return ResAuthor;
    }

    public String getCollectSchoolId() {
        return CollectSchoolId;
    }

    public void setCollectSchoolId(String collectSchoolId) {
        CollectSchoolId = collectSchoolId;
    }

    public void setResAuthor(String resAuthor) {
        ResAuthor = resAuthor;
    }

    public String getWritingRequire() {
        return WritingRequire;
    }

    public void setWritingRequire(String writingRequire) {
        WritingRequire = writingRequire;
    }

    public int getMarkFormula() {
        return MarkFormula;
    }

    public void setMarkFormula(int markFormula) {
        MarkFormula = markFormula;
    }

    public int getWordCountMin() {
        return WordCountMin;
    }

    public void setWordCountMin(int wordCountMin) {
        WordCountMin = wordCountMin;
    }

    public int getWordCountMax() {
        return WordCountMax;
    }

    public void setWordCountMax(int wordCountMax) {
        WordCountMax = wordCountMax;
    }

    public String getWorkOrderId() {
        return WorkOrderId;
    }
    public void setWorkOrderId(String workOrderId) {
        WorkOrderId = workOrderId;
    }

    public void setShareUrl(String shareUrl) {
        ShareUrl = shareUrl;
    }

    public String getShareUrl() {
        return ShareUrl;
    }

    public String getResUrl() {
        return ResUrl;
    }

    public void setResUrl(String resUrl) {
        ResUrl = resUrl;
    }

    public String getResThumbnailUrl() {
        return ResThumbnailUrl;
    }

    public void setResThumbnailUrl(String resThumbnailUrl) {
        ResThumbnailUrl = resThumbnailUrl;
    }

    public void setDiscussContent(String discussContent) {
        DiscussContent = discussContent;
    }

    public String getDiscussContent() {
        return DiscussContent;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getTaskCreateId() {
        return TaskCreateId;
    }

    public void setTaskCreateId(String taskCreateId) {
        TaskCreateId = taskCreateId;
    }

    public String getTaskCreateName() {
        return TaskCreateName;
    }

    public void setTaskCreateName(String taskCreateName) {
        TaskCreateName = taskCreateName;
    }

    public String getSchoolId() {
        return SchoolId;
    }

    public void setSchoolId(String schoolId) {
        SchoolId = schoolId;
    }

    public String getSchoolName() {
        return SchoolName;
    }

    public void setSchoolName(String schoolName) {
        SchoolName = schoolName;
    }

    public String getClassId() {
        return ClassId;
    }

    public void setClassId(String classId) {
        ClassId = classId;
    }

    public String getClassName() {
        return ClassName;
    }

    public void setClassName(String className) {
        ClassName = className;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public String getTaskTitle() {
        return TaskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        TaskTitle = taskTitle;
    }

    public int getTaskNum() {
        return TaskNum;
    }

    public void setTaskNum(int taskNum) {
        TaskNum = taskNum;
    }

    public int getFinishTaskCount() {
        return FinishTaskCount;
    }

    public void setFinishTaskCount(int finishTaskCount) {
        FinishTaskCount = finishTaskCount;
    }

    public String getResId() {
        return ResId;
    }

    public void setResId(String resId) {
        ResId = resId;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getCreateId() {
        return CreateId;
    }

    public void setCreateId(String createId) {
        CreateId = createId;
    }

    public String getCreateName() {
        return CreateName;
    }

    public void setCreateName(String createName) {
        CreateName = createName;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public String getUpdateId() {
        return UpdateId;
    }

    public void setUpdateId(String updateId) {
        UpdateId = updateId;
    }

    public String getUpdateName() {
        return UpdateName;
    }

    public void setUpdateName(String updateName) {
        UpdateName = updateName;
    }

    public String getUpdateTime() {
        return UpdateTime;
    }

    public void setUpdateTime(String updateTime) {
        UpdateTime = updateTime;
    }

    public boolean isDeleted() {
        return Deleted;
    }

    public void setDeleted(boolean deleted) {
        Deleted = deleted;
    }

    public int getCommentCount() {
        return CommentCount;
    }

    public void setCommentCount(int commentCount) {
        CommentCount = commentCount;
    }

    public StudyTaskInfo toStudyTaskInfo(){
        StudyTaskInfo info = new StudyTaskInfo();
        info.setTaskId(String.valueOf(Id));
        info.setTaskType(Type);
        info.setTaskTitle(TaskTitle);
        info.setTaskContent(DiscussContent);
        info.setTaskNum(TaskNum);
        info.setFinishTaskCount(FinishTaskCount);
        info.setStartTime(StartTime);
        info.setEndTime(EndTime);
        info.setCommentCount(CommentCount);
        info.setResId(ResId);
        info.setWorkOrderId(WorkOrderId);
        //资源地址
        info.setResUrl(ResUrl);
        info.setCollectSchoolId(CollectSchoolId);
        return info;
    }
}
