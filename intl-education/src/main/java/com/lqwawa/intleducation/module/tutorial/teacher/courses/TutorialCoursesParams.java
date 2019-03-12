package com.lqwawa.intleducation.module.tutorial.teacher.courses;

import java.io.Serializable;

/**
 * @author mrmedici
 * @desc 我帮辅的课程参数
 */
public class TutorialCoursesParams implements Serializable {

    private String memberId;
    private String configValue;

    public TutorialCoursesParams(String memberId, String configValue) {
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
