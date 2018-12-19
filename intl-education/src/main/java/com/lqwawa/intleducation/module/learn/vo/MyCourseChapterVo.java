package com.lqwawa.intleducation.module.learn.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;

import java.util.List;

/**
 * Created by XChen on 2016/11/28.
 * email:man0fchina@foxmail.com
 */

public class MyCourseChapterVo extends BaseVo {

    /**
     * courseExams : null
     * status : -1
     * children : null
     * parentId : 969
     * resourceUrl : http://192.168.99.181/image/2016/11/09/6f7e3902-c8c1-4a6a-879a-ed268ecfcd5b.jpg
     * saveName : 6f7e3902-c8c1-4a6a-879a-ed268ecfcd5b.jpg
     * resType : 1
     * courseName : xxq1110
     * organId : 1
     * organName : 合肥大学
     * weekNum : 1
     * lessonType : 3
     * isPublish : true
     * originName : 滚动3
     * introduction :
     * createTime : 1478766330000
     * courseId : 197
     * createId : 4
     * createName : cx1老师
     * isDelete : false
     * deleteTime : null
     * reportNum : 0
     * isShield : false
     * resId : 398
     * level :
     * name : 滚动3
     * id : 970
     * type : 3
     * size : 0
     */

    private List<ExamVo> courseExams;
    private int status;
    private List<MyCourseChapterVo> children;
    private int parentId;
    private String resourceUrl;
    private String saveName;
    private int resType;
    private String courseName;
    private int organId;
    private String organName;
    private String weekNum;
    private int lessonType;
    private boolean isPublish;
    private String originName;
    private String introduction;
    private String createTime;
    private int courseId;
    private int createId;
    private String createName;
    private boolean isDelete;
    private String deleteTime;
    private int reportNum;
    private boolean isShield;
    private int resId;
    private String level;
    private String name;
    private int id;
    private int type;
    private int size;
    private boolean isChildren;
    private String chapterName;
    private String sectionName;

    public boolean isIsChildren() {
        return isChildren;
    }

    public void setIsChildren(boolean children) {
        isChildren = children;
    }

    public List<ExamVo> getCourseExams() {
        return courseExams;
    }

    public void setCourseExams(List<ExamVo> courseExams) {
        this.courseExams = courseExams;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<MyCourseChapterVo> getChildren() {
        return children;
    }

    public void setChildren(List<MyCourseChapterVo> children) {
        this.children = children;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    public String getSaveName() {
        return saveName;
    }

    public void setSaveName(String saveName) {
        this.saveName = saveName;
    }

    public int getResType() {
        return resType;
    }

    public void setResType(int resType) {
        this.resType = resType;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getOrganId() {
        return organId;
    }

    public void setOrganId(int organId) {
        this.organId = organId;
    }

    public String getOrganName() {
        return organName;
    }

    public void setOrganName(String organName) {
        this.organName = organName;
    }

    public String getWeekNum() {
        return weekNum;
    }

    public void setWeekNum(String weekNum) {
        this.weekNum = weekNum;
    }

    public int getLessonType() {
        return lessonType;
    }

    public void setLessonType(int lessonType) {
        this.lessonType = lessonType;
    }

    public boolean isIsPublish() {
        return isPublish;
    }

    public void setIsPublish(boolean isPublish) {
        this.isPublish = isPublish;
    }

    public String getOriginName() {
        return originName;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getCreateId() {
        return createId;
    }

    public void setCreateId(int createId) {
        this.createId = createId;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public boolean isIsDelete() {
        return isDelete;
    }

    public void setIsDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }

    public String getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(String deleteTime) {
        this.deleteTime = deleteTime;
    }

    public int getReportNum() {
        return reportNum;
    }

    public void setReportNum(int reportNum) {
        this.reportNum = reportNum;
    }

    public boolean isIsShield() {
        return isShield;
    }

    public void setIsShield(boolean isShield) {
        this.isShield = isShield;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }
}
