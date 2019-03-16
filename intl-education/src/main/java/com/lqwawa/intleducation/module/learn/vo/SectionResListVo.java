package com.lqwawa.intleducation.module.learn.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;
import com.lqwawa.intleducation.common.utils.EmptyUtil;

/**
 * Created by XChen on 2017/3/27.
 * email:man0fchina@foxmail.com
 */

public class SectionResListVo extends BaseVo {

    // 自动批阅的值
    public static final String EXTRAS_AUTO_READ_OVER = "1";

    // 任务单的自动批阅
    public static final int ORDER_TASK_AUTO_MARK = 1;

    /**
     * id : 2075
     * status : 1
     * isShield : false
     * name : 123456.jpg
     * resourceUrl : http://192.168.99.181/image/2016/12/27/deb408b7-cc4e-4783-bb89-f84083da0e18.jpg
     * resId : 457
     * vuid :
     * resType : 1
     * originName : 123456.jpg
     */

    private String id;
    private String createId;
    private int status;
    private boolean isShield;
    private String name;
    private String resourceUrl;
    private String resId;
    private String vuid;
    private int leStatus;
    private int resType;
    private String originName;
    private int taskType;
    // @date   :2018/4/12 0012 下午 7:40
    // @func   :新添加的taskId
    private String taskId;
    private String taskName;
    private int screenType;
    private int weekNum;
    private int type;
    private boolean isChecked;//课件选取
    private boolean isRead;
    // 自动批阅标志 1是自动批阅
    private String resProperties;

    // 章节Id
    private String chapterId;

    // V5.11.X新加的字段
    private String point;

    // V5.12
    private int resPropType;

    // V5.13新添加的字段
    private boolean assigned;

    private SectionTaskOriginVo sectionTaskOriginVo;

    // 是否作业库选中
    private boolean activated;

    // V5.14新添加的课程信息
    private String courseId;
    private String courseName;
    private String classId;
    private String className;


    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getCreateId() {
        return createId;
    }

    public void setCreateId(String createId) {
        this.createId = createId;
    }

    public int getScreenType() {
        return screenType;
    }

    public void setScreenType(int screenType) {
        this.screenType = screenType;
    }

    public SectionResListVo(){
        isTitle = false;
        isShow = true;
    }
    private boolean isTitle;
    private boolean isShow;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isIsShield() {
        return isShield;
    }

    public void setIsShield(boolean isShield) {
        this.isShield = isShield;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    public String getResId() {
        return resId;
    }

    public void setResId(String resId) {
        this.resId = resId;
    }

    public String getVuid() {
        return vuid;
    }

    public void setVuid(String vuid) {
        this.vuid = vuid;
    }

    public int getResType() {
        return resType;
    }

    public void setResType(int resType) {
        this.resType = resType;
    }

    public String getOriginName() {
        return originName;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }

    public boolean isIsTitle() {
        return isTitle;
    }

    public void setIsTitle(boolean title) {
        isTitle = title;
    }

    public boolean isIsShow() {
        return isShow;
    }

    public void setIsShow(boolean show) {
        isShow = show;
    }

    public int getTaskType() {
        return taskType;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getWeekNum() {
        return weekNum;
    }

    public void setWeekNum(int weekNum) {
        this.weekNum = weekNum;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getLeStatus() {
        return leStatus;
    }

    public void setLeStatus(int leStatus) {
        this.leStatus = leStatus;
    }

    public boolean isIsRead() {
        return isRead || getStatus() == 1;
    }

    public void setIsRead(boolean read) {
        isRead = read;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public SectionTaskOriginVo getSectionTaskOriginVo() {
        return sectionTaskOriginVo;
    }

    public void setSectionTaskOriginVo(SectionTaskOriginVo sectionTaskOriginVo) {
        this.sectionTaskOriginVo = sectionTaskOriginVo;
    }

    public String getPoint() {
        // 如果不是自动批阅类型，point = null
        if(!isAutoMark()) point = "";
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public int getResPropType() {
        return resPropType;
    }

    public void setResPropType(int resPropType) {
        this.resPropType = resPropType;
    }

    public String getResProperties() {
        return resProperties;
    }

    public void setResProperties(String resProperties) {
        this.resProperties = resProperties;
    }

    public boolean isAssigned() {
        return assigned;
    }

    public void setAssigned(boolean assigned) {
        this.assigned = assigned;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
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

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * 是否是任务单的自动批阅
     */
    public boolean isAutoMark(){
        // return taskType == 3 && EmptyUtil.isNotEmpty(point);
        return taskType == 3 && resPropType == ORDER_TASK_AUTO_MARK;
    }
}
