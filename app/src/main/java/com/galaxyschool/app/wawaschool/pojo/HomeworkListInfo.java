package com.galaxyschool.app.wawaschool.pojo;


import android.os.Bundle;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016.06.17.
 * ��ҵ�б�
 */
public class HomeworkListInfo implements Serializable {
    private String TaskId;//����ID
    private String TaskType;//��������:0-��΢��,1-���μ�,2-����ҵ,3-����ҵ,4-���ۻ���
    private String TaskTitle;//�������
    private String TaskCreateId;//���񴴽���ID(��ʦ)
    private String TaskCreateName;//���񴴽�������(��ʦ)
    private String StartTime;//����ʱ��
    private String EndTime;//���ʱ��
    private String CommentCount;//������Ŀ
    private int TaskNum;//����ҵ��Ŀ
    private int FinishTaskCount;//�������ҵ��Ŀ
    private String CreateTime;//���񴴽�ʱ��
    private String ResId;//��ʦ�ύ����ԴID
    private String DiscussContent;//���ۻ�������
    private boolean StudentIsRead;
    private boolean isSelect;
    private String WorkOrderId;

    //新版看课件资源list
    private List<LookResDto> LookResList;
    private int AirClassId;//空中课堂直播的id
    private boolean onlineReporter;//是不是空中课堂的小编
    private boolean onlineHost;//是不是主编身份

    //听说读写增加的新字段
    private int Id;
    private String SchoolId;
    private String SchoolName;
    private String ClassId;
    private String ClassName;
    private int CourseId;
    private int Type;
    private String ResUrl;
    private String ResThumbnailUrl;
    private String ResAuthor;
    private String CreateId;
    private String CreateName;
    private String UpdateId;
    private String UpdateName;
    private String UpdateTime;
    private String Deleted;
    private String ShareUrl;
    private String ScoringRuleDescription;
    private String ScoringRule;
    private String ST_StudyGroupId;
    private String ParentStId;
    private String WritingRequire;
    private String MarkFormula;
    private String WordCountMin;
    private String WordCountMax;
    private boolean IsStudentDoneTask;
    private int StudyTaskType;
    private boolean NeedCommit;
    private boolean isSuperChildTask;
    private String parentTaskTitle;
    private int ThirdTaskCount;
    private int ResCourseId;
    private boolean isHistoryClass;
    private boolean isOnlineSchoolClass;
    private int ResPropType;

    public int getResPropType() {
        return ResPropType;
    }

    public void setResPropType(int resPropType) {
        ResPropType = resPropType;
    }

    public boolean isOnlineSchoolClass() {
        return isOnlineSchoolClass;
    }

    public void setIsOnlineSchoolClass(boolean onlineSchoolClass) {
        isOnlineSchoolClass = onlineSchoolClass;
    }

    public boolean isHistoryClass() {
        return isHistoryClass;
    }

    public void setIsHistoryClass(boolean historyClass) {
        isHistoryClass = historyClass;
    }

    public int getResCourseId() {
        return ResCourseId;
    }

    public void setResCourseId(int resCourseId) {
        ResCourseId = resCourseId;
    }
    public int getThirdTaskCount() {
        return ThirdTaskCount;
    }

    public void setThirdTaskCount(int thirdTaskCount) {
        ThirdTaskCount = thirdTaskCount;
    }

    public String getParentTaskTitle() {
        return parentTaskTitle;
    }

    public void setParentTaskTitle(String parentTaskTitle) {
        this.parentTaskTitle = parentTaskTitle;
    }

    public boolean isSuperChildTask() {
        return isSuperChildTask;
    }

    public void setIsSuperChildTask(boolean superChildTask) {
        isSuperChildTask = superChildTask;
    }

    public boolean isNeedCommit() {
        return NeedCommit;
    }

    public void setNeedCommit(boolean needCommit) {
        NeedCommit = needCommit;
    }

    public int getStudyTaskType() {
        return StudyTaskType;
    }

    public void setStudyTaskType(int studyTaskType) {
        StudyTaskType = studyTaskType;
    }

    public boolean isStudentDoneTask() {
        return IsStudentDoneTask;
    }

    public void setIsStudentDoneTask(boolean studentDoneTask) {
        IsStudentDoneTask = studentDoneTask;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
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

    public int getCourseId() {
        return CourseId;
    }

    public void setCourseId(int courseId) {
        CourseId = courseId;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
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

    public String getResAuthor() {
        return ResAuthor;
    }

    public void setResAuthor(String resAuthor) {
        ResAuthor = resAuthor;
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

    public String getDeleted() {
        return Deleted;
    }

    public void setDeleted(String deleted) {
        Deleted = deleted;
    }

    public String getShareUrl() {
        return ShareUrl;
    }

    public void setShareUrl(String shareUrl) {
        ShareUrl = shareUrl;
    }

    public String getScoringRuleDescription() {
        return ScoringRuleDescription;
    }

    public void setScoringRuleDescription(String scoringRuleDescription) {
        ScoringRuleDescription = scoringRuleDescription;
    }

    public String getScoringRule() {
        return ScoringRule;
    }

    public void setScoringRule(String scoringRule) {
        ScoringRule = scoringRule;
    }

    public String getST_StudyGroupId() {
        return ST_StudyGroupId;
    }

    public void setST_StudyGroupId(String ST_StudyGroupId) {
        this.ST_StudyGroupId = ST_StudyGroupId;
    }

    public String getParentStId() {
        return ParentStId;
    }

    public void setParentStId(String parentStId) {
        ParentStId = parentStId;
    }

    public String getWritingRequire() {
        return WritingRequire;
    }

    public void setWritingRequire(String writingRequire) {
        WritingRequire = writingRequire;
    }

    public String getMarkFormula() {
        return MarkFormula;
    }

    public void setMarkFormula(String markFormula) {
        MarkFormula = markFormula;
    }

    public String getWordCountMin() {
        return WordCountMin;
    }

    public void setWordCountMin(String wordCountMin) {
        WordCountMin = wordCountMin;
    }

    public String getWordCountMax() {
        return WordCountMax;
    }

    public void setWordCountMax(String wordCountMax) {
        WordCountMax = wordCountMax;
    }

    public boolean isOnlineHost() {
        return onlineHost;
    }

    public void setOnlineHost(boolean onlineHost) {
        this.onlineHost = onlineHost;
    }

    public boolean isOnlineReporter() {
        return onlineReporter;
    }

    public void setOnlineReporter(boolean onlineReporter) {
        this.onlineReporter = onlineReporter;
    }

    public int getAirClassId() {
        return AirClassId;
    }

    public void setAirClassId(int airClassId) {
        this.AirClassId = airClassId;
    }

    public void setLookResList(List<LookResDto> lookResList) {
        LookResList = lookResList;
    }

    public List<LookResDto> getLookResList() {
        return LookResList;
    }


    public String getWorkOrderId() {
        return WorkOrderId;
    }

    public void setWorkOrderId(String workOrderId) {
        WorkOrderId = workOrderId;
    }

    public void setStudentIsRead(boolean studentIsRead) {
        StudentIsRead = studentIsRead;
    }

    public boolean isStudentIsRead() {
        return StudentIsRead;
    }

    public void setDiscussContent(String discussContent) {
        DiscussContent = discussContent;
    }

    public String getDiscussContent() {
        return DiscussContent;
    }

    public void setResId(String resId) {
        ResId = resId;
    }

    public String getResId() {
        return ResId;
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

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public String getTaskId() {
        return TaskId;
    }

    public void setTaskId(String taskId) {
        TaskId = taskId;
    }

    public String getTaskType() {
        return TaskType;
    }

    public void setTaskType(String taskType) {
        TaskType = taskType;
    }

    public String getTaskTitle() {
        return TaskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        TaskTitle = taskTitle;
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

    public String getCommentCount() {
        return CommentCount;
    }

    public void setCommentCount(String commentCount) {
        CommentCount = commentCount;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setIsSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }
}
