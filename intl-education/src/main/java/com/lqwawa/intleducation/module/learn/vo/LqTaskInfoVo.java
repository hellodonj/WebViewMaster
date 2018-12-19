package com.lqwawa.intleducation.module.learn.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;

/**
 * Created by XChen on 2017/7/10.
 * email:man0fchina@foxmail.com
 */

public class LqTaskInfoVo extends BaseVo {
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
    private String ResUrl;
    private String ResThumbnailUrl;
    private String WorkOrderId;
    private String DiscussContent;
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
    private String ShareUrl;
    private String WritingRequire;
    private int MarkFormula;
    private int WordCountMin;
    private int WordCountMax;
    // 打分标准 1 十分制，2百分制
    private int ScoringRule;
    // 打分标准描述
    private String ScoringRuleDescription;

    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public String getTaskCreateId() {
        return TaskCreateId;
    }

    public void setTaskCreateId(String TaskCreateId) {
        this.TaskCreateId = TaskCreateId;
    }

    public String getTaskCreateName() {
        return TaskCreateName;
    }

    public void setTaskCreateName(String TaskCreateName) {
        this.TaskCreateName = TaskCreateName;
    }

    public String getSchoolId() {
        return SchoolId;
    }

    public void setSchoolId(String SchoolId) {
        this.SchoolId = SchoolId;
    }

    public String getSchoolName() {
        return SchoolName;
    }

    public void setSchoolName(String SchoolName) {
        this.SchoolName = SchoolName;
    }

    public String getClassId() {
        return ClassId;
    }

    public void setClassId(String ClassId) {
        this.ClassId = ClassId;
    }

    public String getClassName() {
        return ClassName;
    }

    public void setClassName(String ClassName) {
        this.ClassName = ClassName;
    }

    public int getType() {
        return Type;
    }

    public void setType(int Type) {
        this.Type = Type;
    }

    public String getTaskTitle() {
        return TaskTitle;
    }

    public void setTaskTitle(String TaskTitle) {
        this.TaskTitle = TaskTitle;
    }

    public int getTaskNum() {
        return TaskNum;
    }

    public void setTaskNum(int TaskNum) {
        this.TaskNum = TaskNum;
    }

    public int getFinishTaskCount() {
        return FinishTaskCount;
    }

    public void setFinishTaskCount(int FinishTaskCount) {
        this.FinishTaskCount = FinishTaskCount;
    }

    public String getResId() {
        return ResId;
    }

    public void setResId(String ResId) {
        this.ResId = ResId;
    }

    public String getResUrl() {
        return ResUrl;
    }

    public void setResUrl(String ResUrl) {
        this.ResUrl = ResUrl;
    }

    public String getResThumbnailUrl() {
        return ResThumbnailUrl;
    }

    public void setResThumbnailUrl(String ResThumbnailUrl) {
        this.ResThumbnailUrl = ResThumbnailUrl;
    }

    public String getWorkOrderId() {
        return WorkOrderId;
    }

    public void setWorkOrderId(String WorkOrderId) {
        this.WorkOrderId = WorkOrderId;
    }

    public String getDiscussContent() {
        return DiscussContent;
    }

    public void setDiscussContent(String DiscussContent) {
        this.DiscussContent = DiscussContent;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String StartTime) {
        this.StartTime = StartTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String EndTime) {
        this.EndTime = EndTime;
    }

    public String getCreateId() {
        return CreateId;
    }

    public void setCreateId(String CreateId) {
        this.CreateId = CreateId;
    }

    public String getCreateName() {
        return CreateName;
    }

    public void setCreateName(String CreateName) {
        this.CreateName = CreateName;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String CreateTime) {
        this.CreateTime = CreateTime;
    }

    public String getUpdateId() {
        return UpdateId;
    }

    public void setUpdateId(String UpdateId) {
        this.UpdateId = UpdateId;
    }

    public String getUpdateName() {
        return UpdateName;
    }

    public void setUpdateName(String UpdateName) {
        this.UpdateName = UpdateName;
    }

    public String getUpdateTime() {
        return UpdateTime;
    }

    public void setUpdateTime(String UpdateTime) {
        this.UpdateTime = UpdateTime;
    }

    public boolean isDeleted() {
        return Deleted;
    }

    public void setDeleted(boolean Deleted) {
        this.Deleted = Deleted;
    }

    public int getCommentCount() {
        return CommentCount;
    }

    public void setCommentCount(int CommentCount) {
        this.CommentCount = CommentCount;
    }

    public String getShareUrl() {
        return ShareUrl;
    }

    public void setShareUrl(String ShareUrl) {
        this.ShareUrl = ShareUrl;
    }

    public String getWritingRequire() {
        return WritingRequire;
    }

    public void setWritingRequire(String WritingRequire) {
        this.WritingRequire = WritingRequire;
    }

    public int getMarkFormula() {
        return MarkFormula;
    }

    public void setMarkFormula(int MarkFormula) {
        this.MarkFormula = MarkFormula;
    }

    public int getWordCountMin() {
        return WordCountMin;
    }

    public void setWordCountMin(int WordCountMin) {
        this.WordCountMin = WordCountMin;
    }

    public int getWordCountMax() {
        return WordCountMax;
    }

    public void setWordCountMax(int WordCountMax) {
        this.WordCountMax = WordCountMax;
    }

    public int getScoringRule() {
        return ScoringRule;
    }

    public void setScoringRule(int scoringRule) {
        ScoringRule = scoringRule;
    }

    public String getScoringRuleDescription() {
        return ScoringRuleDescription;
    }

    public void setScoringRuleDescription(String scoringRuleDescription) {
        ScoringRuleDescription = scoringRuleDescription;
    }
}
