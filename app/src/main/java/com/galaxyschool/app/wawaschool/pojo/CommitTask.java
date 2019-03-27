package com.galaxyschool.app.wawaschool.pojo;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.lqwawa.intleducation.module.learn.vo.LqTaskCommitVo;

import java.io.Serializable;

/**
 * Created by Administrator on 2016.06.22.
 * ѧ  �����
 */
public class CommitTask implements Serializable, Parcelable {
    private int CommitTaskId;
    private int TaskId;//����ID
    private String TeacherResId;//��ʦ���õ���ҵ��ԴID
    private String StudentId;//ѧ��ID
    private String StudentName;//ѧ������
    private String HeadPicUrl;//ѧ��ͷ��
    private int TaskState;//����״̬(0-δ�ύ,1-���ύ)
    private String CommitTime;//ѧ���ύ��ҵʱ��
    private String StudentResId;//ѧ���ύ��ҵ����ԴID
    private String StudentResTitle;//ѧ���ύ����Դ��ҵ����
    private String StudentResUrl;//ѧ���ύ��ҵ����ԴURL
    private String StudentResThumbnailUrl;
    private boolean IsRead;//��ʦ�Ƿ��Ѳ鿴��ҵ
    private String ReadTime;//��ʦ�鿴��ҵʱ��
    private String CreateId;
    private String CreateName;
    private String CreateTime;
    private String UpdateId;
    private String UpdateName;
    private String UpdateTime;
    private boolean Deleted;
    private boolean isNeedSplit;
    private int CommitType;

    //以下字段为"英文写作"作业类型时返回
    private String WritingRequire;//作文要求
    private int MarkFormula;//打分公式
    private int WordCountMin;//作文字数最小值
    private int WordCountMax;//作文字数，最大值

    private String WritingContent;//内容
    private int ModifyTimes;//修改次数
    private int WordCount;//字数
    private String Score;//分数
    private String CorrectResult;//批改网返回结果


    private boolean HasCommitTaskReview;//是否有批阅
    private String TaskScore;//作业成绩
    private int Id;
    private boolean showDeleted;

    //自动批阅
    private int AutoEvalCompanyType;
    private String AutoEvalContent;
    private boolean HasVoiceReview;//老师点评
    private String TaskScoreRemark;
    private int screenType;
    private int scoreRule;
    private int ResPropType;
    private String parentResourceUrl;
    private String level;
    private int airClassId;
    private boolean isAssistantMark;//帮辅批阅
    private int EQId;//任务的题号
    private int commitTaskOnlineId;
    private String assistantRoleType;
    private boolean hasTutorialPermission;
    /**
     * @return 是否是语音评测类型
     */
    public boolean isEvalType(){
        if (!TextUtils.isEmpty(StudentResId)){
            if (StudentResId.contains("-")){
                int type = Integer.valueOf(StudentResId.split("-")[1]);
                return type == ResType.RES_TYPE_EVALUATE;
            }
        }
        return false;
    }

    /**
     * @return 提交类型是不是视频类型
     */
    public boolean isVideoType(){
        if (!TextUtils.isEmpty(StudentResId)){
            if (StudentResId.contains("-")){
                int type = Integer.valueOf(StudentResId.split("-")[1]);
                return type == ResType.RES_TYPE_VIDEO;
            }
        }
        return false;
    }

    public boolean isCourseType(){
        if (!TextUtils.isEmpty(StudentResId)){
            if (StudentResId.contains("-")){
                int type = Integer.valueOf(StudentResId.split("-")[1]);
                return type == ResType.RES_TYPE_COURSE_SPEAKER || type == ResType.RES_TYPE_ONEPAGE;
            }
        }
        return false;
    }

    public boolean isStudyCard(){
        if (!TextUtils.isEmpty(StudentResId)){
            if (StudentResId.contains("-")){
                int type = Integer.valueOf(StudentResId.split("-")[1]);
                return type == ResType.RES_TYPE_STUDY_CARD;
            }
        }
        return false;
    }

    /**
     * @return 任务单自动批阅的数据
     */
    public boolean isMarkCard(){
        return CommitType == 6;
    }

    public boolean isHasTutorialPermission() {
        return hasTutorialPermission;
    }

    public void setHasTutorialPermission(boolean hasTutorialPermission) {
        this.hasTutorialPermission = hasTutorialPermission;
    }

    public String getAssistantRoleType() {
        return assistantRoleType;
    }

    public void setAssistantRoleType(String assistantRoleType) {
        this.assistantRoleType = assistantRoleType;
    }

    public int getEQId() {
        return EQId;
    }

    public void setEQId(int EQId) {
        this.EQId = EQId;
    }

    public int getCommitTaskOnlineId() {
        return commitTaskOnlineId;
    }

    public void setCommitTaskOnlineId(int commitTaskOnlineId) {
        this.commitTaskOnlineId = commitTaskOnlineId;
    }

    public boolean isAssistantMark() {
        return isAssistantMark;
    }

    public void setIsAssistantMark(boolean assistantMark) {
        isAssistantMark = assistantMark;
    }

    public int getAirClassId() {
        return airClassId;
    }

    public void setAirClassId(int airClassId) {
        this.airClassId = airClassId;
    }

    public int getScoreRule() {
        return scoreRule;
    }

    public void setScoreRule(int scoreRule) {
        this.scoreRule = scoreRule;
    }

    public int getScreenType() {
        return screenType;
    }

    public void setScreenType(int screenType) {
        this.screenType = screenType;
    }

    public String getTaskScoreRemark() {
        return TaskScoreRemark;
    }

    public void setTaskScoreRemark(String taskScoreRemark) {
        TaskScoreRemark = taskScoreRemark;
    }

    public boolean isHasVoiceReview() {
        return HasVoiceReview;
    }

    public void setHasVoiceReview(boolean hasVoiceReview) {
        HasVoiceReview = hasVoiceReview;
    }

    public int getAutoEvalCompanyType() {
        return AutoEvalCompanyType;
    }

    public void setAutoEvalCompanyType(int autoEvalCompanyType) {
        AutoEvalCompanyType = autoEvalCompanyType;
    }

    public String getAutoEvalContent() {
        return AutoEvalContent;
    }

    public void setAutoEvalContent(String autoEvalContent) {
        AutoEvalContent = autoEvalContent;
    }

    public boolean isShowDeleted() {
        return showDeleted;
    }

    public void setShowDeleted(boolean showDeleted) {
        this.showDeleted = showDeleted;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public boolean isHasCommitTaskReview() {
        return HasCommitTaskReview;
    }

    public void setHasCommitTaskReview(boolean hasCommitTaskReview) {
        HasCommitTaskReview = hasCommitTaskReview;
    }

    public String getTaskScore() {
        return TaskScore;
    }

    public void setTaskScore(String taskScore) {
        TaskScore = taskScore;
    }

    public void setRead(boolean read) {
        IsRead = read;
    }

    public String getWritingContent() {
        return WritingContent;
    }

    public void setWritingContent(String writingContent) {
        WritingContent = writingContent;
    }

    public int getModifyTimes() {
        return ModifyTimes;
    }

    public void setModifyTimes(int modifyTimes) {
        ModifyTimes = modifyTimes;
    }

    public int getWordCount() {
        return WordCount;
    }

    public void setWordCount(int wordCount) {
        WordCount = wordCount;
    }

    public String getScore() {
        return Score;
    }

    public void setScore(String score) {
        Score = score;
    }

    public String getCorrectResult() {
        return CorrectResult;
    }

    public void setCorrectResult(String correctResult) {
        CorrectResult = correctResult;
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

    public void setCommitType(int type){
        this.CommitType=type;
    }
    public int getCommitType(){
        return CommitType;
    }

    public void setNeedSplit(boolean needSplit) {
        isNeedSplit = needSplit;
    }

    public boolean isNeedSplit() {
        return isNeedSplit;
    }

    public void setCommitTaskId(int commitTaskId) {
        CommitTaskId = commitTaskId;
    }

    public int getCommitTaskId() {
        return CommitTaskId;
    }

    public void setStudentResTitle(String studentResTitle) {
        StudentResTitle = studentResTitle;
    }

    public String getStudentResTitle() {
        return StudentResTitle;
    }


    public int getTaskId() {
        return TaskId;
    }

    public void setTaskId(int taskId) {
        TaskId = taskId;
    }

    public String getTeacherResId() {
        return TeacherResId;
    }

    public void setTeacherResId(String teacherResId) {
        TeacherResId = teacherResId;
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

    public int getTaskState() {
        return TaskState;
    }

    public void setTaskState(int taskState) {
        TaskState = taskState;
    }

    public String getCommitTime() {
        return CommitTime;
    }

    public void setCommitTime(String commitTime) {
        CommitTime = commitTime;
    }

    public String getStudentResId() {
        return StudentResId;
    }

    public void setStudentResId(String studentResId) {
        StudentResId = studentResId;
    }

    public String getStudentResUrl() {
        return StudentResUrl;
    }

    public void setStudentResUrl(String studentResUrl) {
        StudentResUrl = studentResUrl;
    }

    public boolean isRead() {
        return IsRead;
    }

    public void setIsRead(boolean isRead) {
        IsRead = isRead;
    }

    public String getReadTime() {
        return ReadTime;
    }

    public void setReadTime(String readTime) {
        ReadTime = readTime;
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

    public String getStudentResThumbnailUrl() {
        return StudentResThumbnailUrl;
    }

    public void setStudentResThumbnailUrl(String studentResThumbnailUrl) {
        StudentResThumbnailUrl = studentResThumbnailUrl;
    }

    public int getResPropType() {
        return ResPropType;
    }

    public void setResPropType(int resPropType) {
        ResPropType = resPropType;
    }

    public String getParentResourceUrl() {
        return parentResourceUrl;
    }

    public void setParentResourceUrl(String parentResourceUrl) {
        this.parentResourceUrl = parentResourceUrl;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.CommitTaskId);
        dest.writeInt(this.TaskId);
        dest.writeString(this.TeacherResId);
        dest.writeString(this.StudentId);
        dest.writeString(this.StudentName);
        dest.writeString(this.HeadPicUrl);
        dest.writeInt(this.TaskState);
        dest.writeString(this.CommitTime);
        dest.writeString(this.StudentResId);
        dest.writeString(this.StudentResTitle);
        dest.writeString(this.StudentResUrl);
        dest.writeByte(this.IsRead ? (byte) 1 : (byte) 0);
        dest.writeString(this.ReadTime);
        dest.writeString(this.CreateId);
        dest.writeString(this.CreateName);
        dest.writeString(this.CreateTime);
        dest.writeString(this.UpdateId);
        dest.writeString(this.UpdateName);
        dest.writeString(this.UpdateTime);
        dest.writeByte(this.Deleted ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isNeedSplit ? (byte) 1 : (byte) 0);
        dest.writeInt(this.CommitType);
        dest.writeString(this.WritingRequire);
        dest.writeInt(this.MarkFormula);
        dest.writeInt(this.WordCountMin);
        dest.writeInt(this.WordCountMax);
        dest.writeString(this.WritingContent);
        dest.writeInt(this.ModifyTimes);
        dest.writeInt(this.WordCount);
        dest.writeString(this.Score);
        dest.writeString(this.CorrectResult);
        dest.writeByte(this.HasCommitTaskReview ? (byte) 1 : (byte) 0);
        dest.writeString(this.TaskScore);
        dest.writeByte(this.showDeleted ? (byte) 1 : (byte) 0);
        dest.writeByte(this.HasVoiceReview ? (byte) 1 : (byte) 0);
        dest.writeString(this.TaskScoreRemark);
        dest.writeInt(this.screenType);
        dest.writeInt(this.scoreRule);
        dest.writeInt(this.AutoEvalCompanyType);
        dest.writeString(this.AutoEvalContent);
        dest.writeString(this.StudentResThumbnailUrl);
        dest.writeInt(this.ResPropType);
        dest.writeString(this.level);
        dest.writeString(this.parentResourceUrl);
        dest.writeInt(this.airClassId);
        dest.writeByte(this.isAssistantMark ? (byte) 1 : (byte) 0);
        dest.writeInt(this.Id);
        dest.writeInt(this.EQId);
        dest.writeInt(this.commitTaskOnlineId);
        dest.writeString(this.assistantRoleType);
        dest.writeByte(this.hasTutorialPermission ? (byte) 1 : (byte) 0);
    }

    public CommitTask() {
    }

    protected CommitTask(Parcel in) {
        this.CommitTaskId = in.readInt();
        this.TaskId = in.readInt();
        this.TeacherResId = in.readString();
        this.StudentId = in.readString();
        this.StudentName = in.readString();
        this.HeadPicUrl = in.readString();
        this.TaskState = in.readInt();
        this.CommitTime = in.readString();
        this.StudentResId = in.readString();
        this.StudentResTitle = in.readString();
        this.StudentResUrl = in.readString();
        this.IsRead = in.readByte() != 0;
        this.ReadTime = in.readString();
        this.CreateId = in.readString();
        this.CreateName = in.readString();
        this.CreateTime = in.readString();
        this.UpdateId = in.readString();
        this.UpdateName = in.readString();
        this.UpdateTime = in.readString();
        this.Deleted = in.readByte() != 0;
        this.isNeedSplit = in.readByte() != 0;
        this.CommitType = in.readInt();
        this.WritingRequire = in.readString();
        this.MarkFormula = in.readInt();
        this.WordCountMin = in.readInt();
        this.WordCountMax = in.readInt();
        this.WritingContent = in.readString();
        this.ModifyTimes = in.readInt();
        this.WordCount = in.readInt();
        this.Score = in.readString();
        this.CorrectResult = in.readString();
        this.HasCommitTaskReview = in.readByte() != 0;
        this.TaskScore = in.readString();
        this.showDeleted = in.readByte() != 0;
        this.HasVoiceReview = in.readByte() != 0;
        this.TaskScoreRemark = in.readString();
        this.screenType = in.readInt();
        this.scoreRule = in.readInt();
        this.AutoEvalCompanyType = in.readInt();
        this.AutoEvalContent = in.readString();
        this.StudentResThumbnailUrl = in.readString();
        this.ResPropType = in.readInt();
        this.level = in.readString();
        this.parentResourceUrl = in.readString();
        this.airClassId = in.readInt();
        this.isAssistantMark = in.readByte() != 0;
        this.Id = in.readInt();
        this.EQId = in.readInt();
        this.commitTaskOnlineId = in.readInt();
        this.assistantRoleType = in.readString();
        this.hasTutorialPermission = in.readByte() != 0;
    }

    public static final Creator<CommitTask> CREATOR = new Creator<CommitTask>() {
        @Override
        public CommitTask createFromParcel(Parcel source) {
            return new CommitTask(source);
        }

        @Override
        public CommitTask[] newArray(int size) {
            return new CommitTask[size];
        }
    };

    public static CommitTask buildVo(@NonNull LqTaskCommitVo vo){
        String jsonString = JSON.toJSONString(vo);
        TypeReference<CommitTask> typeReference = new TypeReference<CommitTask>(){};
        CommitTask commitTask = JSON.parseObject(jsonString, typeReference);
        return commitTask;
    }
}
