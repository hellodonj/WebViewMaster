package com.lqwawa.intleducation.module.learn.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;

/**
 * Created by XChen on 2017/1/5.
 * email:man0fchina@foxmail.com
 */

public class ExamItemVo extends BaseVo{
    ExamCexerVo cexer;
    ExamUexer uexer;

    public ExamCexerVo getCexer() {
        return cexer;
    }

    public void setCexer(ExamCexerVo cexer) {
        this.cexer = cexer;
    }

    public ExamUexer getUexer() {
        return uexer;
    }

    public void setUexer(ExamUexer uexer) {
        this.uexer = uexer;
    }
}
