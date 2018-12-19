package com.lqwawa.intleducation.module.learn.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;
import com.lqwawa.intleducation.module.discovery.vo.ChapterVo;

import java.util.List;

/**
 * Created by XChen on 2016/11/29.
 * email:man0fchina@foxmail.com
 */

public class MyCourseChapterListVo extends BaseVo{
    private List<MyCourseChapterVo> chapters;
    private int code;
    private String message;

    public List<MyCourseChapterVo> getChapters() {
        return chapters;
    }

    public void setChapters(List<MyCourseChapterVo> chapters) {
        this.chapters = chapters;
    }

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
}
