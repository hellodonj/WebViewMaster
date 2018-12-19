package com.lqwawa.client.pojo;

/**
 * ======================================================
 * Describe:任务单答题卡的type
 * ======================================================
 */
public interface LearnTaskCardType {
    //单选题
    int SINGLE_CHOICE_QUESTION = 1;
    //多选题
    int MULTIPLE_CHOICE_QUESTIONS = 2;
    //判断题
    int JUDGMENT_PROBLEM = 3;
    //填空题
    int FILL_CONTENT = 4;
    //听力单选
    int LISTEN_SINGLE_SELECTION = 5;
    //听力判断
    int HEARING_JUDGMENT = 6;
    //听力填空
    int LISTEN_FILL_CONTENT = 7;
    //单选改错
    int SINGLE_CHOICE_CORRECTION = 8;
    //主观题
    int SUBJECTIVE_PROBLEM = 9;


}

