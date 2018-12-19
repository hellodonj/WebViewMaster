package com.lqwawa.intleducation.module.learn.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;

/**
 * Created by XChen on 2017/1/6.
 * email:man0fchina@foxmail.com
 */

public class AnswerVo extends BaseVo{
    private int exerId;
    private String answer = "";
    private int type; //题目类型
    private String score; //老师打分

    public int getExerId() {
        return exerId;
    }

    public void setExerId(int exerId) {
        this.exerId = exerId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}
