package com.lqwawa.intleducation.factory.data.entity.response;

import java.io.Serializable;

/**
 * @author
 * @desc 申请帮辅查询状态接口返回
 */
public class AppliedResponseVo implements Serializable {

    public static final int SUCCEED = 0;

    public static final int ERROR_UNKNOWN = -1;

    private int code;
    private String message;
    private boolean haveApplied;

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

    public boolean isHaveApplied() {
        return haveApplied;
    }

    public void setHaveApplied(boolean haveApplied) {
        this.haveApplied = haveApplied;
    }

    /**
     * 网络请求是否成功
     * @return true 请求成功 false 请求失败
     */
    public boolean isSucceed(){
        return code == SUCCEED;
    }

    public static class CourseTutorEntity implements Serializable{
        private boolean isTutorCourse;
        private int isOrganTutorStatus;

        public CourseTutorEntity() {
        }

        public CourseTutorEntity(boolean isTutorCourse, int isOrganTutorStatus) {
            this.isTutorCourse = isTutorCourse;
            this.isOrganTutorStatus = isOrganTutorStatus;
        }

        public boolean isTutorCourse() {
            return isTutorCourse;
        }

        public void setTutorCourse(boolean tutorCourse) {
            isTutorCourse = tutorCourse;
        }

        public int getIsOrganTutorStatus() {
            return isOrganTutorStatus;
        }

        public void setIsOrganTutorStatus(int isOrganTutorStatus) {
            this.isOrganTutorStatus = isOrganTutorStatus;
        }
    }
}
