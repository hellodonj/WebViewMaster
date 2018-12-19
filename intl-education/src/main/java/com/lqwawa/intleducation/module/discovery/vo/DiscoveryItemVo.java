package com.lqwawa.intleducation.module.discovery.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;

import java.util.List;

/**
 * Created by XChen on 2016/11/8.
 * email:man0fchina@foxmail.com
 */

public class DiscoveryItemVo extends BaseVo {
    public static final int SUCCEED = 0;

    public static final int ERROR_UNKNOWN = -1;

    private List<OrganVo> organList;//机构列表
    private List<CourseVo> zjCourseList;//最近更新
    private List<CourseVo> rmCourseList;//热门推荐
    private int code;
    private String message;

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

    public List<OrganVo> getOrganList() {
        return organList;
    }

    public void setOrganList(List<OrganVo> organList) {
        this.organList = organList;
    }

    public List<CourseVo> getRmCourseList() {
        return rmCourseList;
    }

    public void setRmCourseList(List<CourseVo> rmCourseList) {
        this.rmCourseList = rmCourseList;
    }

    public List<CourseVo> getZjCourseList() {
        return zjCourseList;
    }

    public void setZjCourseList(List<CourseVo> zjCourseList) {
        this.zjCourseList = zjCourseList;
    }

    /**
     * 网络请求是否成功
     * @return true 请求成功 false 请求失败
     */
    public boolean isSucceed(){
        return code == SUCCEED;
    }
}
