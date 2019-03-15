package com.lqwawa.intleducation.factory.data.entity.response;

import java.io.Serializable;

/**
 * @author
 * @desc 接收是否是课程的帮辅老师实体
 */
public class CourseTutorResponseVo implements Serializable {

    public static final int SUCCEED = 0;

    public static final int ERROR_UNKNOWN = -1;

    private int code;
    private String message;
    private boolean isTutorCourse;

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

    public boolean isTutorCourse() {
        return isTutorCourse;
    }

    public void setTutorCourse(boolean tutorCourse) {
        isTutorCourse = tutorCourse;
    }

    /**
     * 网络请求是否成功
     * @return true 请求成功 false 请求失败
     */
    public boolean isSucceed(){
        return code == SUCCEED;
    }
}
