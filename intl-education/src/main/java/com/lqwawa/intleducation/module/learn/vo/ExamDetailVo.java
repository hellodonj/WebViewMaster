package com.lqwawa.intleducation.module.learn.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;

/**
 * Created by XChen on 2017/1/5.
 * email:man0fchina@foxmail.com
 */

public class ExamDetailVo extends BaseVo {
    private int score;
    private ExamPaperVo paper;
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public ExamPaperVo getPaper() {
        return paper;
    }

    public void setPaper(ExamPaperVo paper) {
        this.paper = paper;
    }
}
