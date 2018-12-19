package com.lqwawa.intleducation.factory.data.entity;

import com.lqwawa.intleducation.base.vo.BaseVo;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 用来接收在线班级关联课程的返回
 * @date 2018/06/25 16:31
 * @history v1.0
 * **********************************
 */
public class OnlineRelevanceCourseEntity extends BaseVo {

    private static final int SUCCEED = 0;
    private static final String ERROR_COURSE_ID = "0";

    private int code;
    private String message;
    private String courseId;

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

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    /**
     * 网络请求是否成功
     * @return true 请求成功 false 请求失败
     */
    public boolean isSucceed(){
        return code == SUCCEED;
    }

    /**
     * 是否返回正确的courseId
     * @return true 关联课程了,有关联课程信息
     */
    public boolean isRightRequest(){
        return !ERROR_COURSE_ID.equals(courseId);
    }
}
