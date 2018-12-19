package com.lqwawa.intleducation.factory.data.entity.course;

import com.lqwawa.intleducation.base.vo.BaseVo;
import com.lqwawa.intleducation.factory.data.entity.PayChapterEntity;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;

import java.util.List;

/**
 * @author medici
 * @desc 接收学程未购买章和我的课程信息
 */
public class NotPurchasedChapterEntity extends BaseVo{
    private static final int SUCCEED = 0;

    private int code;
    private String message;
    private boolean chapterBuyed;
    List<CourseVo> course;
    List<PayChapterEntity> chapterList;

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

    public boolean isChapterBuyed() {
        return chapterBuyed;
    }

    public void setChapterBuyed(boolean chapterBuyed) {
        this.chapterBuyed = chapterBuyed;
    }

    public List<CourseVo> getCourse() {
        return course;
    }

    public void setCourse(List<CourseVo> course) {
        this.course = course;
    }

    public List<PayChapterEntity> getChapterList() {
        return chapterList;
    }

    public void setChapterList(List<PayChapterEntity> chapterList) {
        this.chapterList = chapterList;
    }

    /**
     * 网络请求是否成功
     * @return true 请求成功 false 请求失败
     */
    public boolean isSucceed(){
        return code == SUCCEED;
    }
}
