package com.lqwawa.intleducation.factory.data.entity;

import com.lqwawa.intleducation.base.vo.BaseVo;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;

import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 在线机构信息相关数据实体
 * @date 2018/06/05 14:29
 * @history v1.0
 * **********************************
 */
public class OnlineSchoolInfoEntity extends BaseVo{

    private static final int SUCCEED = 0;

    private int code;
    private String message;
    private List<OnlineClassEntity> CourseOnLineList;
    private List<CourseVo> courseList;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<OnlineClassEntity> getCourseOnLineList() {
        return CourseOnLineList;
    }

    public void setCourseOnLineList(List<OnlineClassEntity> courseOnLineList) {
        CourseOnLineList = courseOnLineList;
    }

    public List<CourseVo> getCourseList() {
        return courseList;
    }

    public void setCourseList(List<CourseVo> courseList) {
        this.courseList = courseList;
    }

    /**
     * 网络请求是否成功
     * @return true 请求成功 false 请求失败
     */
    public boolean isSucceed(){
        return code == SUCCEED;
    }
}
