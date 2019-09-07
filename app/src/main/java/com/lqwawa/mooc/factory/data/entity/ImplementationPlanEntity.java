package com.lqwawa.mooc.factory.data.entity;

import com.lqwawa.intleducation.base.vo.BaseVo;

public class ImplementationPlanEntity extends BaseVo {
    /**
     * "lgAppendixUrl": "学习目标附件url，用逗号分割",
     * "lgAppendixId": "学习目标附件id，用逗号分割",
     * "cpAppendixUrl": "",
     * "difficultPoint": "重点难点",
     * "dpAppendixUrl": "",
     * "dpAppendixId": "",
     * "cpAppendixId": "",
     * "learningGoal": "学习目标",
     * "commonProblem": "常见问题"
     */
    private String learningGoal;
    private String lgAppendixUrl;
    private String lgAppendixId;
    private String difficultPoint;
    private String dpAppendixUrl;
    private String dpAppendixId;
    private String commonProblem;
    private String cpAppendixUrl;
    private String cpAppendixId;

    public String getLearningGoal() {
        return learningGoal;
    }

    public void setLearningGoal(String learningGoal) {
        this.learningGoal = learningGoal;
    }

    public String getLgAppendixUrl() {
        return lgAppendixUrl;
    }

    public void setLgAppendixUrl(String lgAppendixUrl) {
        this.lgAppendixUrl = lgAppendixUrl;
    }

    public String getLgAppendixId() {
        return lgAppendixId;
    }

    public void setLgAppendixId(String lgAppendixId) {
        this.lgAppendixId = lgAppendixId;
    }

    public String getDifficultPoint() {
        return difficultPoint;
    }

    public void setDifficultPoint(String difficultPoint) {
        this.difficultPoint = difficultPoint;
    }

    public String getDpAppendixUrl() {
        return dpAppendixUrl;
    }

    public void setDpAppendixUrl(String dpAppendixUrl) {
        this.dpAppendixUrl = dpAppendixUrl;
    }

    public String getDpAppendixId() {
        return dpAppendixId;
    }

    public void setDpAppendixId(String dpAppendixId) {
        this.dpAppendixId = dpAppendixId;
    }

    public String getCommonProblem() {
        return commonProblem;
    }

    public void setCommonProblem(String commonProblem) {
        this.commonProblem = commonProblem;
    }

    public String getCpAppendixUrl() {
        return cpAppendixUrl;
    }

    public void setCpAppendixUrl(String cpAppendixUrl) {
        this.cpAppendixUrl = cpAppendixUrl;
    }

    public String getCpAppendixId() {
        return cpAppendixId;
    }

    public void setCpAppendixId(String cpAppendixId) {
        this.cpAppendixId = cpAppendixId;
    }
}
