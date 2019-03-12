package com.lqwawa.intleducation.module.tutorial.marking.list;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * @author mrmedici
 * @desc 作业提交列表的参数
 */
public class TutorialMarkingParams implements Cloneable, Serializable {
    private int markType;
    private String curMemberId;
    private String role;

    public TutorialMarkingParams(String curMemberId,
                                 @TutorialRoleType.TutorialRoleRes String role) {
        this.curMemberId = curMemberId;
        this.role = role;
    }

    public int getMarkType() {
        return markType;
    }

    public void setMarkType(int markType) {
        this.markType = markType;
    }

    public String getCurMemberId() {
        return curMemberId;
    }

    public void setCurMemberId(String curMemberId) {
        this.curMemberId = curMemberId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(@NonNull String role) {
        this.role = role;
    }

    public boolean isTutor(){
        return TutorialRoleType.TUTORIAL_TYPE_TUTOR.equals(role);
    }

    public boolean notTutor(){
        return !isTutor();
    }

    public boolean isStudent(){
        return TutorialRoleType.TUTORIAL_TYPE_STUDENT.equals(role);
    }

    public boolean isParent(){
        return TutorialRoleType.TUTORIAL_TYPE_PARENT.equals(role);
    }

    @Override
    protected TutorialMarkingParams clone() {
        try {
            TutorialMarkingParams clone = (TutorialMarkingParams) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
