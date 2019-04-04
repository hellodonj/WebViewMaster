package com.lqwawa.intleducation.module.tutorial.student.courses;

import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.io.Serializable;

/**
 * @author mrmedici
 * @desc 学生的帮辅列表参数
 */
public class StudentTutorialParams implements Serializable {


    private String memberId;
    private String configValue;
    private boolean isParent;

    public StudentTutorialParams(boolean isParent,String memberId, String configValue) {
        this.isParent = isParent;
        this.memberId = memberId;
        this.configValue = configValue;
    }

    public boolean isParent() {
        return isParent && !UserHelper.getUserId().equals(memberId);
    }

    public String getMemberId() {
        return memberId;
    }

    public String getConfigValue() {
        return configValue;
    }
}
