package com.galaxyschool.app.wawaschool.pojo;

import java.io.Serializable;

/**
 * Created by E450 on 2016/12/13.
 * 校园巡查班级列表详情
 */
public class ClassDataStaticsInfo implements Serializable{

    private String ClassName;
    private String ClassId;
    private String ClassHeadPic;
    private int Msg_NoticeCount;
    private int Msg_MienCount;
    private int Msg_GenESchoolCount;
    private int Msg_Sum;
    private int St_LookCount;
    private int St_CommitFzktCount;
    private int St_DiscussTopicCount;
    private int St_RepeatCourseCount;
    private int St_Sum;
    private int Sum;
    private int St_GuideReadCount;//导读数量
    private int St_LookCourseCount;//看微课统计数
    private int St_LookCoursewareCount;//看课件统计数
    private int St_LookFzktCount;//看作业统计数

    public int getSt_LookCourseCount() {
        return St_LookCourseCount;
    }

    public void setSt_LookCourseCount(int st_LookCourseCount) {
        St_LookCourseCount = st_LookCourseCount;
    }

    public int getSt_LookCoursewareCount() {
        return St_LookCoursewareCount;
    }

    public void setSt_LookCoursewareCount(int st_LookCoursewareCount) {
        St_LookCoursewareCount = st_LookCoursewareCount;
    }

    public int getSt_LookFzktCount() {
        return St_LookFzktCount;
    }

    public void setSt_LookFzktCount(int st_LookFzktCount) {
        St_LookFzktCount = st_LookFzktCount;
    }

    public void setSt_GuideReadCount(int st_GuideReadCount) {
        St_GuideReadCount = st_GuideReadCount;
    }

    public int getSt_GuideReadCount() {
        return St_GuideReadCount;
    }

    public String getClassName() {
        return ClassName;
    }

    public void setClassName(String className) {
        ClassName = className;
    }

    public String getClassId() {
        return ClassId;
    }

    public void setClassId(String classId) {
        ClassId = classId;
    }

    public String getClassHeadPic() {
        return ClassHeadPic;
    }

    public void setClassHeadPic(String classHeadPic) {
        ClassHeadPic = classHeadPic;
    }

    public int getMsg_NoticeCount() {
        return Msg_NoticeCount;
    }

    public void setMsg_NoticeCount(int msg_NoticeCount) {
        Msg_NoticeCount = msg_NoticeCount;
    }

    public int getMsg_MienCount() {
        return Msg_MienCount;
    }

    public void setMsg_MienCount(int msg_MienCount) {
        Msg_MienCount = msg_MienCount;
    }

    public int getMsg_GenESchoolCount() {
        return Msg_GenESchoolCount;
    }

    public void setMsg_GenESchoolCount(int msg_GenESchoolCount) {
        Msg_GenESchoolCount = msg_GenESchoolCount;
    }

    public int getMsg_Sum() {
        return Msg_Sum;
    }

    public void setMsg_Sum(int msg_Sum) {
        Msg_Sum = msg_Sum;
    }

    public int getSt_LookCount() {
        return St_LookCount;
    }

    public void setSt_LookCount(int st_LookCount) {
        St_LookCount = st_LookCount;
    }

    public int getSt_CommitFzktCount() {
        return St_CommitFzktCount;
    }

    public void setSt_CommitFzktCount(int st_CommitFzktCount) {
        St_CommitFzktCount = st_CommitFzktCount;
    }

    public int getSt_DiscussTopicCount() {
        return St_DiscussTopicCount;
    }

    public void setSt_DiscussTopicCount(int st_DiscussTopicCount) {
        St_DiscussTopicCount = st_DiscussTopicCount;
    }

    public int getSt_RepeatCourseCount() {
        return St_RepeatCourseCount;
    }

    public void setSt_RepeatCourseCount(int st_RepeatCourseCount) {
        St_RepeatCourseCount = st_RepeatCourseCount;
    }

    public int getSt_Sum() {
        return St_Sum;
    }

    public void setSt_Sum(int st_Sum) {
        St_Sum = st_Sum;
    }

    public int getSum() {
        return Sum;
    }

    public void setSum(int sum) {
        Sum = sum;
    }
}
