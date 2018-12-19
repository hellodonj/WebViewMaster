package com.lqwawa.intleducation.module.discovery.ui.coursedetail;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.base.vo.BaseVo;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

/**
 * @author mrmedici
 * @desc 课程详情参数
 */
public class CourseDetailParams extends BaseVo{

    private int  courseEnterType;
    // 班级学程进入
    private String classId;
    // 大厅有绑定班级
    private String bindClassId;
    // 学程馆进入
    private String schoolId;
    // 大厅有绑定机构
    private String bindSchoolId;
    // 是否有试听授权
    private boolean isAuthorized;
    // 机构学程馆入口的身份信息
    private String roles;
    // 是否是班级老师
    private boolean isClassTeacher;
    // 是否是班级家长
    private boolean isClassParent;
    // 是否已经加入过班级
    private boolean isJoin;

    public CourseDetailParams() {
        // 默认的进入方式
        courseEnterType = CourseDetailType.COURSE_DETAIL_MOOC_ENTER;
    }

    public CourseDetailParams(@NonNull String schoolId, String classId,boolean isAuthorized) {
        // 班级进入，设置大小写
        schoolId = schoolId.toLowerCase();
        classId = classId.toLowerCase();
        this.schoolId = schoolId;
        this.classId = classId;
        this.isAuthorized = isAuthorized;
        courseEnterType = CourseDetailType.COURSE_DETAIL_CLASS_ENTER;
    }

    public CourseDetailParams(boolean isAuthorized,String schoolId,String roles){
        schoolId = schoolId.toLowerCase();
        this.isAuthorized = isAuthorized;
        this.schoolId = schoolId;
        this.roles = roles;
        courseEnterType = CourseDetailType.COURSE_DETAIL_SCHOOL_ENTER;
    }

    public int getCourseEnterType() {
        return courseEnterType;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getBindClassId() {
        return bindClassId;
    }

    public void setBindClassId(String bindClassId) {
        this.bindClassId = bindClassId;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getBindSchoolId() {
        return bindSchoolId;
    }

    public void setBindSchoolId(String bindSchoolId) {
        this.bindSchoolId = bindSchoolId;
    }

    public boolean isClassTeacher() {
        return isClassTeacher;
    }

    public void setClassTeacher(boolean classTeacher) {
        isClassTeacher = classTeacher;
    }

    public boolean isClassParent() {
        return isClassParent;
    }

    public void setClassParent(boolean classParent) {
        isClassParent = classParent;
    }

    /**
     * 是否是班级学程入口
     * @return true 是
     */
    public boolean isClassCourseEnter(){
        return courseEnterType == CourseDetailType.COURSE_DETAIL_CLASS_ENTER;
    }

    /**
     * 是否机构学程入口
     * @return true 是
     */
    public boolean isOrganCourseEnter(){
        return courseEnterType == CourseDetailType.COURSE_DETAIL_SCHOOL_ENTER;
    }

    /**
     * 是否绑定班级
     * @return true 绑定班级
     */
    public boolean isBindClass(){
        return EmptyUtil.isNotEmpty(bindSchoolId) && EmptyUtil.isNotEmpty(bindClassId);
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }

    /**
     * 是否要作辅导老师身份处理
     * @return true
     * 机构学程馆入口，如果已经授权，并且是机构老师，作学程的辅导老师处理，
     * 如果是机构老师未授权，已购买也做辅导老师处理。
     * 其它从大厅进去是一样的
     */
    public boolean isOrganCounselor(){
        return courseEnterType == CourseDetailType.COURSE_DETAIL_SCHOOL_ENTER &&
                ((isAuthorized && UserHelper.isTeacher(roles)) || (isJoin && UserHelper.isTeacher(roles)));
    }

    /**
     * 已经购买过
     * @param isJoin true 是否购买
     */
    public void buildOrganJoinState(boolean isJoin){
        this.isJoin = isJoin;
    }
}
