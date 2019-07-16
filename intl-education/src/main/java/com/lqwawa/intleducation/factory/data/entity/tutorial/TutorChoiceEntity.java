package com.lqwawa.intleducation.factory.data.entity.tutorial;

import java.io.Serializable;

/**
 * @author mrmedici
 * @desc 帮辅老师选择的页面
 */
public class TutorChoiceEntity implements Serializable {

    private String tutorName;
    private String headPicUrl;
    private String markingPrice;
    private int taskNum;
    private String memberId;
    private float starLevel;

    private boolean checked;

    public String getTutorName() {
        return tutorName;
    }

    public void setTutorName(String tutorName) {
        this.tutorName = tutorName;
    }

    public String getHeadPicUrl() {
        return headPicUrl;
    }

    public void setHeadPicUrl(String headPicUrl) {
        this.headPicUrl = headPicUrl;
    }

    public String getMarkingPrice() {
        return markingPrice;
    }

    public void setMarkingPrice(String markingPrice) {
        this.markingPrice = markingPrice;
    }

    public int getTaskNum() {
        return taskNum;
    }

    public void setTaskNum(int taskNum) {
        this.taskNum = taskNum;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public float getStarLevel() {
        return starLevel;
    }

    public void setStarLevel(float starLevel) {
        this.starLevel = starLevel;
    }

    public static TutorChoiceEntity build(TutorEntity tutorEntity) {
        TutorChoiceEntity tutorChoiceEntity = new TutorChoiceEntity();
        if (tutorEntity != null) {
            tutorChoiceEntity.headPicUrl = tutorEntity.getHeadPicUrl();
            tutorChoiceEntity.tutorName = tutorEntity.getTutorName();
            tutorChoiceEntity.memberId = tutorEntity.getTutorMemberId();
            tutorChoiceEntity.markingPrice = tutorEntity.getMarkingPrice();
            tutorChoiceEntity.taskNum = tutorEntity.getTaskNum();
            tutorChoiceEntity.starLevel = tutorEntity.getStarLevel();
        }
        return tutorChoiceEntity;
    }
}
