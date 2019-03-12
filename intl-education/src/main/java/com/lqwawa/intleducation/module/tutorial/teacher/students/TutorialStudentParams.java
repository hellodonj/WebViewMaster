package com.lqwawa.intleducation.module.tutorial.teacher.students;

import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.io.Serializable;

/**
 * @author mrmedici
 * @desc 老师的帮辅学生列表参数
 */
public class TutorialStudentParams implements Serializable {


    private String memberId;
    private String configValue;

    public TutorialStudentParams(String memberId, String configValue) {
        this.memberId = memberId;
        this.configValue = configValue;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getConfigValue() {
        return configValue;
    }
}
