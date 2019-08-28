package com.lqwawa.intleducation.module.discovery.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;
import com.lqwawa.intleducation.module.learn.vo.ExamVo;
import com.lqwawa.intleducation.module.learn.vo.SectionResListVo;

import java.util.List;

/**
 * Created by XChen on 2016/11/14.
 * email:man0fchina@foxmail.com
 */

public class ChapterVo extends BaseVo {
    private boolean isChildren;
    private String organId;//0,
    private String createTime;//"2016-10-20 16:52:13",
    private String isShield;//false,
    private List<ChapterVo> children;//
    private String courseId;//174,
    private int type;//2,
    private String resourceUrl;//"",
    private String resId;//0,
    private String isDelete;//false,
    private String id;//767,
    private List<ExamVo> courseExams;//[],
    private String parentId;//766,
    private String level;//"",
    private String lessonType;//0,
    private String name;//"第一节",
    private String reportNum;//0,
    private String organName;//"",
    private String createId;//65,
    private String originName;//"",
    private int status;//-1,
    private String createName;//"合肥大学管理员",
    private String size;//0,
    private String saveName;//"",
    private String weekNum;//1,
    private String isPublish;//true,
    private String introduction;//"",
    private String deleteTime;//"",
    private String courseName;//"",
    private int resType;//0
    private boolean hide = false;
    private boolean containAssistantWork = false;
    private String vuid;
    private String chapterName;
    private String sectionName;
    private int cworkSize;
    private List<ChapterVo> sectionList;
    private List<SectionResListVo> resList;
    private List<SectionResListVo> taskList;
    private int flag = 0;
    // 版本V5.9新添加的字段
    private boolean buyed;
    private int price;

    // 一共多少任务
    private int totalNum;
    // 完成了多少任务
    private int finishNum;

    //0普通教案，1考试/测试
    private int examType = -1;
    //记录是否选中
    protected boolean isChoosed;
    //教案类型 1：预习 2:练习 3：复习
    private int exerciseType;
    //true 没锁  false锁住了
    private boolean isUnlock;

    public boolean isContainAssistantWork() {
        return containAssistantWork;
    }

    public void setContainAssistantWork(boolean containAssistantWork) {
        this.containAssistantWork = containAssistantWork;
    }

    public int getCworkSize() {
        return cworkSize;
    }

    public void setCworkSize(int cworkSize) {
        this.cworkSize = cworkSize;
    }

    public String getVuid() {
        return vuid;
    }

    public void setVuid(String vuid) {
        this.vuid = vuid;
    }

    public boolean isIsHide() {
        return hide;
    }

    public void setIsHide(boolean hide) {
        this.hide = hide;
    }

    public boolean getIsChildren() {
        return isChildren;
    }

    public void setIsChildren(boolean children) {
        isChildren = children;
    }

    public List<ChapterVo> getChildren() {
        return children;
    }

    public void setChildren(List<ChapterVo> children) {
        this.children = children;
    }

    public List<ExamVo> getCourseExams() {
        return courseExams;
    }

    public void setCourseExams(List<ExamVo> courseExams) {
        this.courseExams = courseExams;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCreateId() {
        return createId;
    }

    public void setCreateId(String createId) {
        this.createId = createId;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(String deleteTime) {
        this.deleteTime = deleteTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }

    public String getIsPublish() {
        return isPublish;
    }

    public void setIsPublish(String isPublish) {
        this.isPublish = isPublish;
    }

    public String getIsShield() {
        return isShield;
    }

    public void setIsShield(String isShield) {
        this.isShield = isShield;
    }

    public String getLessonType() {
        return lessonType;
    }

    public void setLessonType(String lessonType) {
        this.lessonType = lessonType;
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

    public String getOrganId() {
        return organId;
    }

    public void setOrganId(String organId) {
        this.organId = organId;
    }

    public String getOrganName() {
        return organName;
    }

    public void setOrganName(String organName) {
        this.organName = organName;
    }

    public String getOriginName() {
        return originName;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getReportNum() {
        return reportNum;
    }

    public void setReportNum(String reportNum) {
        this.reportNum = reportNum;
    }

    public String getResId() {
        return resId;
    }

    public void setResId(String resId) {
        this.resId = resId;
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    public int getResType() {
        return resType;
    }

    public void setResType(int resType) {
        this.resType = resType;
    }

    public String getSaveName() {
        return saveName;
    }

    public void setSaveName(String saveName) {
        this.saveName = saveName;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getWeekNum() {
        return weekNum;
    }

    public void setWeekNum(String weekNum) {
        this.weekNum = weekNum;
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

    public List<ChapterVo> getSectionList() {
        return sectionList;
    }

    public void setSectionList(List<ChapterVo> sectionList) {
        this.sectionList = sectionList;
    }

    public List<SectionResListVo> getResList() {
        return resList;
    }

    public void setResList(List<SectionResListVo> resList) {
        this.resList = resList;
    }

    public List<SectionResListVo> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<SectionResListVo> taskList) {
        this.taskList = taskList;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public boolean isBuyed() {
        return buyed;
    }

    public void setBuyed(boolean buyed) {
        this.buyed = buyed;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public int getFinishNum() {
        return finishNum;
    }

    public void setFinishNum(int finishNum) {
        this.finishNum = finishNum;
    }

    public boolean isChoosed() {
        return isChoosed;
    }

    public void setChoosed(boolean choosed) {
        isChoosed = choosed;
    }

    public void setExamType(int examType) {
        this.examType = examType;
    }

    public int getExamType() {
        return examType;
    }

    public int getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(int exerciseType) {
        this.exerciseType = exerciseType;
    }

    public boolean isUnlock() {
        return isUnlock;
    }

    public void setUnlock(boolean unlock) {
        isUnlock = unlock;
    }
}
