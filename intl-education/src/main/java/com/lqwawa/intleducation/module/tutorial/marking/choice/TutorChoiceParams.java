package com.lqwawa.intleducation.module.tutorial.marking.choice;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;

/**
 * @author mrmedici
 * @desc 帮辅老师选择参数
 */
public class TutorChoiceParams implements Serializable {
    private String memberId;
    private String courseId;
    private String chapterId;
    private QuestionResourceModel model;

    public TutorChoiceParams() {
    }

    public TutorChoiceParams(@Nullable String memberId,
                             @Nullable String courseId,
                             @Nullable String chapterId,
                             @NonNull QuestionResourceModel model) {
        this.memberId = memberId;
        this.courseId = courseId;
        this.chapterId = chapterId;
        this.model = model;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public QuestionResourceModel getModel() {
        return model;
    }

    public void setModel(QuestionResourceModel model) {
        this.model = model;
    }
}
