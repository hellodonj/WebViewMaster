package com.lqwawa.intleducation.module.learn.vo;

import android.text.TextUtils;

import com.lqwawa.intleducation.base.vo.BaseVo;
import com.lqwawa.intleducation.common.utils.EmptyUtil;

/**
 * Created by XChen on 2017/1/5.
 * email:man0fchina@foxmail.com
 */

public class ExamListVo extends BaseVo {

    // 考试的标志
    private static final String EXAM_WEEKNUM_TAG = "0";

    private int score;
    private int chapterIsFinish;
    private String chapterName;
    private boolean buyed;
    private ExamVo cexam;

    public ExamVo getCexam() {
        return cexam;
    }

    public void setCexam(ExamVo cexam) {
        this.cexam = cexam;
    }

    public boolean isBuyed() {
        return buyed;
    }

    public void setBuyed(boolean buyed) {
        this.buyed = buyed;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getChapterIsFinish() {
        return chapterIsFinish;
    }

    public void setChapterIsFinish(int chapterIsFinish) {
        this.chapterIsFinish = chapterIsFinish;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    /**
     * @return 返回是否是考试 true 考试
     */
    public boolean isExam(){
        ExamVo vo = getCexam();
        return EmptyUtil.isNotEmpty(vo) && TextUtils.equals(vo.getWeekNum(),EXAM_WEEKNUM_TAG);
    }
}
