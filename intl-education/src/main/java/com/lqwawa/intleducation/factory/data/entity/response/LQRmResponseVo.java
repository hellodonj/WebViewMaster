package com.lqwawa.intleducation.factory.data.entity.response;

import com.lqwawa.intleducation.base.vo.BaseVo;
import com.lqwawa.intleducation.factory.data.entity.OnlineClassEntity;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;

import java.util.List;

/**
 * @author mrmedici
 * @desc V5.12版本首页标签数据的接受类
 */
public class LQRmResponseVo extends BaseVo {

    public static final int SUCCEED = 0;

    public static final int ERROR_UNKNOWN = -1;

    private int code;
    private String message;
    // 热门推荐
    private List<CourseVo> rmCourseList;
    // 国家或者国际课程在线课堂
    private List<OnlineClassEntity> gjOnlineCourseList;
    // 小语种在线课堂
    private List<OnlineClassEntity> xyzOnlineCourseList;

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

    public List<CourseVo> getRmCourseList() {
        return rmCourseList;
    }

    public void setRmCourseList(List<CourseVo> rmCourseList) {
        this.rmCourseList = rmCourseList;
    }

    public List<OnlineClassEntity> getGjOnlineCourseList() {
        return gjOnlineCourseList;
    }

    public void setGjOnlineCourseList(List<OnlineClassEntity> gjOnlineCourseList) {
        this.gjOnlineCourseList = gjOnlineCourseList;
    }

    public List<OnlineClassEntity> getXyzOnlineCourseList() {
        return xyzOnlineCourseList;
    }

    public void setXyzOnlineCourseList(List<OnlineClassEntity> xyzOnlineCourseList) {
        this.xyzOnlineCourseList = xyzOnlineCourseList;
    }

    /**
     * 网络请求是否成功
     * @return true 请求成功 false 请求失败
     */
    public boolean isSucceed(){
        return code == SUCCEED;
    }


}
