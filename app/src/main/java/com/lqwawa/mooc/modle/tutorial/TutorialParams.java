package com.lqwawa.mooc.modle.tutorial;

import java.io.Serializable;

/**
 * @author mrmedici
 * @desc 帮辅主页参数
 */
public class TutorialParams implements Serializable {
    private String tutorMemberId;
    private String tutorName;
    private boolean isTutorialMarkedEnter;
    private String classId;

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

    public boolean isTutorialMarkedEnter() {
        return isTutorialMarkedEnter;
    }

    public void setTutorialMarkedEnter(boolean tutorialMarkedEnter) {
        isTutorialMarkedEnter = tutorialMarkedEnter;
    }

    public String getClassId() {
        return classId;
    }

    public TutorialParams setClassId(String classId) {
        this.classId = classId;
        return this;
    }
}
