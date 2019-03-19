package com.lqwawa.intleducation.module.tutorial.target;

import com.lqwawa.intleducation.module.tutorial.marking.list.TutorialRoleType;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.io.Serializable;

/**
 * @author mrmedici
 * @desc 进入我的帮辅，我帮辅的学生传参
 */
public class TutorialTargetTaskParams implements Serializable {


    private String memberId;
    private String tutorMemberId;
    private String configValue;
    private String role;
    private boolean isParent;

    public TutorialTargetTaskParams(String memberId,String tutorMemberId,String configValue) {
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
}
