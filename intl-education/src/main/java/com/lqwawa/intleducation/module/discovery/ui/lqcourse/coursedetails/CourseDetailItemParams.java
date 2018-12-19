package com.lqwawa.intleducation.module.discovery.ui.lqcourse.coursedetails;

import com.lqwawa.intleducation.base.vo.BaseVo;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailParams;

/**
 * @author mrmedici
 * @desc 课程大纲 课程简介 课程评论的参数实体
 */
public class CourseDetailItemParams extends BaseVo implements Cloneable{
    // 课程简介类型
    public static final int COURSE_DETAIL_ITEM_INTRODUCTION = 1;
    // 课程大纲类型
    public static final int COURSE_DETAIL_ITEM_STUDY_PLAN = 2;
    // 课程评论类型
    public static final int COURSE_DETAIL_ITEM_COURSE_COMMENT = 3;

    // 是否是参加学习课程的状态
    private boolean isJoin;
    // 传入的MemberId if memberId == UserHelper.getUserId()  学生
    private String memberId;
    // 是否是家长角色
    private boolean isParentRole;
    // 课程Id
    private String courseId;
    // type 类型
    private int dataType;
    // 是否是评论Fragment
    private boolean isComment;

    // 课程详情的参数
    private CourseDetailParams courseParams;


    public CourseDetailItemParams(boolean isJoin, String memberId, boolean isParentRole, String courseId) {
        this.isJoin = isJoin;
        this.memberId = memberId;
        this.isParentRole = isParentRole;
        this.courseId = courseId;
    }

    public boolean isJoin() {
        return isJoin;
    }

    public void setJoin(boolean join) {
        isJoin = join;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public boolean isParentRole() {
        return isParentRole;
    }

    public void setParentRole(boolean parentRole) {
        isParentRole = parentRole;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public CourseDetailParams getCourseParams() {
        return courseParams;
    }

    public void setCourseParams(CourseDetailParams courseParams) {
        this.courseParams = courseParams;
    }

    public boolean isComment() {
        return isComment;
    }

    public void setComment(boolean comment) {
        isComment = comment;
    }

    @Override
    public Object clone() {
        try {
            // 浅克隆
            CourseDetailItemParams params = (CourseDetailItemParams) super.clone();
            params.setCourseParams(courseParams);
            return params;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
