package com.lqwawa.intleducation.factory.data.entity.course;

import java.io.Serializable;

/**
 * @author mrmedici
 * @desc 帮辅群的实体
 */
public class TutorialGroupEntity implements Serializable {
    private boolean addedTutor;
    private String markingPrice;
    private String courseId;
    private String HeadPicUrl;
    private int type;
    private String createName;
    private int taskNum;
    private String createId;


    public boolean isAddedTutor() {
        return addedTutor;
    }

    public void setAddedTutor(boolean addedTutor) {
        this.addedTutor = addedTutor;
    }

    public String getMarkingPrice() {
        return markingPrice;
    }

    public void setMarkingPrice(String markingPrice) {
        this.markingPrice = markingPrice;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getHeadPicUrl() {
        return HeadPicUrl;
    }

    public void setHeadPicUrl(String headPicUrl) {
        HeadPicUrl = headPicUrl;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public int getTaskNum() {
        return taskNum;
    }

    public void setTaskNum(int taskNum) {
        this.taskNum = taskNum;
    }

    public String getCreateId() {
        return createId;
    }

    public void setCreateId(String createId) {
        this.createId = createId;
    }
}
