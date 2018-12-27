package com.galaxyschool.app.wawaschool.pojo;

/**
 * ======================================================
 * Describe: 极光推送的类型
 * ======================================================
 */
public interface PushModuleType {
    // 直播开课推送
    int AIRCLASS_NOW = 1;

    // 直播还有30分钟推送
    int AIRCLASS_HALFHOUR = 2;

    // 直播还有2小时推送
    int AIRCLASS_TWOHOURS = 3;

    // 发送学习任务到班级推送给学生、家长

    int STUDYTASK_TOCLASS = 4;

    // 发送学习任务到学习小组推送给学生、家长
    int STUDYTASK_TOSTUDYGROUP = 5;

    // 学习任务老师批阅点评推送给学生、家长
    int STUDYTASK_TOMEMBER = 6;
}
