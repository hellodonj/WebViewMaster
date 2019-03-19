package com.lqwawa.intleducation.module.learn.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.base.vo.BaseVo;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.module.learn.adapter.CommittedTasksAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XChen on 2017/7/10.
 * email:man0fchina@foxmail.com
 */

public class LqTaskCommitVo extends BaseVo {
    private int Id;
    private int TaskId;
    private String TeacherResId;
    private String StudentId;
    private String StudentName;
    // @date   :2018/4/16 0016 下午 2:28
    // @func   :V5.5 添加的是否批阅字段
    // 是否有批阅
    private boolean HasCommitTaskReview;
    private String HeadPicUrl;
    private String HeadPicUrlSrc;
    private boolean StudentIsRead;
    private int TaskState;
    private String CommitTime;
    private String StudentResId;
    private String StudentResTitle;
    private String StudentResUrl;
    private String StudentResThumbnailUrl;
    private boolean IsRead;
    private String ReadTime;
    private int CommitType;
    private String CreateId;
    private String CreateName;
    private String CreateTime;
    private String UpdateId;
    private String UpdateName;
    private String UpdateTime;
    private boolean Deleted;
    private int CommitTaskId;
    private String WritingContent;
    private int ModifyTimes;
    private int WordCount;
    private String Score;
    private String CorrectResult;

    // 作业成绩
    private String TaskScore;
    // 是否有语音评测
    private boolean HasVoiceReview;
    // 老师点评的文本
    private String TaskScoreRemark;
    // 自动评测单页的分数
    private String AutoEvalContent;
    // V5.14新添加的字段
    private String ResourceId;

    // 标志删除位
    private boolean isDeleteTag;

    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public int getTaskId() {
        return TaskId;
    }

    public void setTaskId(int TaskId) {
        this.TaskId = TaskId;
    }

    public String getTeacherResId() {
        return TeacherResId;
    }

    public void setTeacherResId(String TeacherResId) {
        this.TeacherResId = TeacherResId;
    }

    public String getStudentId() {
        return StudentId;
    }

    public void setStudentId(String StudentId) {
        this.StudentId = StudentId;
    }

    public String getStudentName() {
        return StudentName;
    }

    public void setStudentName(String StudentName) {
        this.StudentName = StudentName;
    }

    public boolean isHasCommitTaskReview() {
        return HasCommitTaskReview;
    }

    public void setHasCommitTaskReview(boolean hasCommitTaskReview) {
        HasCommitTaskReview = hasCommitTaskReview;
    }

    public String getHeadPicUrl() {
        return HeadPicUrl;
    }

    public void setHeadPicUrl(String HeadPicUrl) {
        this.HeadPicUrl = HeadPicUrl;
    }

    public String getHeadPicUrlSrc() {
        return HeadPicUrlSrc;
    }

    public void setHeadPicUrlSrc(String HeadPicUrlSrc) {
        this.HeadPicUrlSrc = HeadPicUrlSrc;
    }

    public boolean isStudentIsRead() {
        return StudentIsRead;
    }

    public void setStudentIsRead(boolean StudentIsRead) {
        this.StudentIsRead = StudentIsRead;
    }

    public int getTaskState() {
        return TaskState;
    }

    public void setTaskState(int TaskState) {
        this.TaskState = TaskState;
    }

    public String getCommitTime() {
        return CommitTime;
    }

    public void setCommitTime(String CommitTime) {
        this.CommitTime = CommitTime;
    }

    public String getStudentResId() {
        return StudentResId;
    }

    public void setStudentResId(String StudentResId) {
        this.StudentResId = StudentResId;
    }

    public String getStudentResTitle() {
        return StudentResTitle;
    }

    public void setStudentResTitle(String StudentResTitle) {
        this.StudentResTitle = StudentResTitle;
    }

    public String getStudentResUrl() {
        return StudentResUrl;
    }

    public void setStudentResUrl(String StudentResUrl) {
        this.StudentResUrl = StudentResUrl;
    }

    public String getStudentResThumbnailUrl() {
        return StudentResThumbnailUrl;
    }

    public void setStudentResThumbnailUrl(String StudentResThumbnailUrl) {
        this.StudentResThumbnailUrl = StudentResThumbnailUrl;
    }

    public boolean isIsRead() {
        return IsRead;
    }

    public void setIsRead(boolean IsRead) {
        this.IsRead = IsRead;
    }

    public String getReadTime() {
        return ReadTime;
    }

    public void setReadTime(String ReadTime) {
        this.ReadTime = ReadTime;
    }

    public int getCommitType() {
        return CommitType;
    }

    public void setCommitType(int CommitType) {
        this.CommitType = CommitType;
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

    public int getCommitTaskId() {
        return CommitTaskId;
    }

    public void setCommitTaskId(int CommitTaskId) {
        this.CommitTaskId = CommitTaskId;
    }

    public String getWritingContent() {
        return WritingContent;
    }

    public void setWritingContent(String WritingContent) {
        this.WritingContent = WritingContent;
    }

    public int getModifyTimes() {
        return ModifyTimes;
    }

    public void setModifyTimes(int ModifyTimes) {
        this.ModifyTimes = ModifyTimes;
    }

    public int getWordCount() {
        return WordCount;
    }

    public void setWordCount(int WordCount) {
        this.WordCount = WordCount;
    }

    public String getScore() {
        return Score;
    }

    public void setScore(String Score) {
        this.Score = Score;
    }

    public String getCorrectResult() {
        return CorrectResult;
    }

    public void setCorrectResult(String CorrectResult) {
        this.CorrectResult = CorrectResult;
    }

    public String getTaskScore() {
        /*if(EmptyUtil.isEmpty(TaskScore)){
            // 默认返回0分
            return "0";
        }*/
        return TaskScore;
    }

    public void setTaskScore(String taskScore) {
        TaskScore = taskScore;
    }

    public boolean isHasVoiceReview() {
        return HasVoiceReview;
    }

    public void setHasVoiceReview(boolean hasVoiceReview) {
        HasVoiceReview = hasVoiceReview;
    }

    public String getTaskScoreRemark() {
        return TaskScoreRemark;
    }

    public void setTaskScoreRemark(String taskScoreRemark) {
        TaskScoreRemark = taskScoreRemark;
    }

    public String getAutoEvalContent() {
        return AutoEvalContent;
    }

    public void setAutoEvalContent(String autoEvalContent) {
        AutoEvalContent = autoEvalContent;
    }

    public boolean isDeleteTag() {
        return isDeleteTag;
    }

    public void setDeleteTag(boolean deleteTag) {
        isDeleteTag = deleteTag;
    }

    public ArrayList<Integer> buildAutoEvalList(){
        if(EmptyUtil.isEmpty(AutoEvalContent)) return null;
        TypeReference<ArrayList<Integer>> typeReference = new TypeReference<ArrayList<Integer>>(){};
        return JSON.parseObject(AutoEvalContent,typeReference);
    }

    /**
     * 是否是语音评测的Cell
     * @return false 批阅
     */
    public boolean isSpeechEvaluation(){
        String studentResId = getStudentResId();
        if(EmptyUtil.isNotEmpty(studentResId) && studentResId.contains("-")) {
            String[] resIdStrings = studentResId.split("-");
            if (Integer.parseInt(resIdStrings[1]) == 26) {
                return true;
            }
        }

        return false;
    }

    /**
     * 是否是任务单的自动批阅Cell
     */
    public boolean isAutoMark(){
        return CommitType == 6 || EmptyUtil.isEmpty(StudentResId);
    }
}
