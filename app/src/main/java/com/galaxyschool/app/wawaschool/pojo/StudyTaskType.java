package com.galaxyschool.app.wawaschool.pojo;

/**
 * Created by Administrator on 2016/6/14.
 */
public interface StudyTaskType {
    int WATCH_WAWA_COURSE = 0;
    int WATCH_RESOURCE = 1;
    int WATCH_HOMEWORK = 2;
    int SUBMIT_HOMEWORK = 3;
    int TOPIC_DISCUSSION = 4;
    int RETELL_WAWA_COURSE = 5;
    int INTRODUCTION_WAWA_COURSE=6;
    int ENGLISH_WRITING=7;
    int TASK_ORDER = 8;
    //新版看课件
    int NEW_WATACH_WAWA_COURSE = 9;
    //听说+读写
    int LISTEN_READ_AND_WRITE = 10;
    //综合任务
    int SUPER_TASK = 11;
    //听说课多选
    int MULTIPLE_RETELL_COURSE = 12;
    //读写单多选
    int MULTIPLE_TASK_ORDER = 13;
    //q配音
    int Q_DUBBING = 14;
    //多选配音
    int MULTIPLE_Q_DUBBING = 15;
    //其他多选
    int MULTIPLE_OTHER = 17;
    //其他多选(需提交)
    int MULTIPLE_OTHER_SUBMIT = 18;
}
