package com.galaxyschool.app.wawaschool.pojo;

import java.io.Serializable;

/**
 * @author: wangchao
 * @date: 2017/10/17 15:52
 */

public class TaskMarkParam implements Serializable {

    public boolean isMarked; // 老师是否已批阅

    public boolean isNeedMark; // 作业是否需要打分

    public boolean isPercent; // 是否百分制
    public int roleType; // 用户角色
    public String commitTaskId; // 学生提交的作业ID

    public boolean isVisitor; // 是否是游客  游客没有批阅提交按钮
    /**
     * 分数
     */
    public String score;

    public boolean fromOnlineStudyTask;

    public ExerciseAnswerCardParam cardParam;

    public TaskMarkParam(boolean isMarked,
                         boolean isPercent,
                         int roleType,
                         String commitTaskId,
                         boolean isVisitor,
                         boolean isNeedMark,
                         String score,
                         boolean fromOnlineStudyTask) {
        this.isMarked = isMarked;
        this.isPercent = isPercent;
        this.roleType = roleType;
        this.commitTaskId = commitTaskId;
        this.isVisitor = isVisitor;
        this.isNeedMark = isNeedMark;
        this.score = score;
        this.fromOnlineStudyTask = fromOnlineStudyTask;
    }
}
