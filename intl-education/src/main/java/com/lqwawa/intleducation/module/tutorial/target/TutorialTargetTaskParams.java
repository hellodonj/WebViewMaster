package com.lqwawa.intleducation.module.tutorial.target;

import com.lqwawa.intleducation.module.tutorial.marking.list.MarkingStateType;
import com.lqwawa.intleducation.module.tutorial.marking.list.TutorialRoleType;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.io.Serializable;

/**
 * @author mrmedici
 * @desc 进入我的帮辅，我帮辅的学生传参
 */
public class TutorialTargetTaskParams implements Serializable, Cloneable {


    private String memberId;
    private String tutorMemberId;
    private String configValue;
    private String role;
    private boolean isParent;
    private int state;

    public TutorialTargetTaskParams(String memberId, String tutorMemberId, String configValue) {
        this.memberId = memberId;
        this.tutorMemberId = tutorMemberId;
        this.configValue = configValue;
    }

    public String getRole() {
        return role;
    }

    public void setRole(@TutorialRoleType.TutorialRoleRes String role) {
        this.role = role;
    }

    public void setParent(boolean parent) {
        isParent = parent;
    }

    public boolean isParent() {
        return isParent && !UserHelper.getUserId().equals(memberId);
    }

    public String getMemberId() {
        return memberId;
    }

    public String getTutorMemberId() {
        return tutorMemberId;
    }

    public String getConfigValue() {
        return configValue;
    }

    public int getState() {
        return state;
    }

    public void setState(@MarkingStateType.TutorialStateRes int state) {
        this.state = state;
    }

    @Override
    public TutorialTargetTaskParams clone() {
        try {
            TutorialTargetTaskParams params = (TutorialTargetTaskParams) super.clone();
            return params;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
