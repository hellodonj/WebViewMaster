package com.lqwawa.intleducation.module.tutorial.teacher.schools;

import java.io.Serializable;

/**
 * @author mrmedici
 * @desc 我的帮辅机构参数
 */
public class TutorialSchoolsParams implements Serializable {

    private String memberId;
    private String configValue;

    public TutorialSchoolsParams(String memberId, String configValue) {
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
