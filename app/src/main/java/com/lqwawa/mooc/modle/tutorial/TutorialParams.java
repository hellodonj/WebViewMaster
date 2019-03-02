package com.lqwawa.mooc.modle.tutorial;

import java.io.Serializable;

/**
 * @author mrmedici
 * @desc 帮辅主页参数
 */
public class TutorialParams implements Serializable {
    private String tutorMemberId;
    private String tutorName;

    public TutorialParams(String tutorMemberId) {
        this(tutorMemberId,null);
    }

    public TutorialParams(String tutorMemberId, String tutorName) {
        this.tutorMemberId = tutorMemberId;
        this.tutorName = tutorName;
    }

    public String getTutorMemberId() {
        return tutorMemberId;
    }

    public String getTutorName() {
        return tutorName;
    }
}
