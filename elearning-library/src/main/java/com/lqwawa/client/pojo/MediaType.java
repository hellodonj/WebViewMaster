package com.lqwawa.client.pojo;

/**
 * Created by wangchao on 12/31/15.
 */
public interface MediaType {
    int MICROCOURSE = 1;
    int PICTURE = 2;
    int AUDIO = 3;
    int VIDEO = 4;
    int PPT = 7;
    int PDF = 8;
    int TASK_ORDER=16;//
    int DOC = 17;
    int LESSON = 18;
    int ONE_PAGE = 10;
    int FLIPPED_CLASSROOM = 11;
    int BOOK_CLASS=12;//我的课程
    int lQ_CLASS=13;//LQ课件

    //cloud resource type as follows
    //公共资源库资源类型
    int CLOUD_EMATERIAL = 1;
    int CLOUD_HOMEWORK = 2;
    int CLOUD_PPT = 3;
    int CLOUD_PDF = 4;
    int CLOUD_PICTURE = 5;
    int CLOUD_AUDIO = 6;
    int CLOUD_VIDEO = 7;
    int CLOUD_OTHER = 8;
    int CLOUD_LQBOOK = 9;
    int CLOUD_LQLESSON = 10;

    //校本资源库资源类型
    int SCHOOL_PPT = 1;
    int SCHOOL_PDF = 2;
    int SCHOOL_PICTURE = 3;
    int SCHOOL_VIDEO = 4;
    int SCHOOL_AUDIO = 5;
    int SCHOOL_COURSEWARE = 6;
    int SCHOOL_TASKORDER = 7;
    //教案
    int SCHOOL_TEACHINGMATERIAL = 8;
    int SCHOOL_LESSON = 9;
    int SCHOOL_DOC = 10;
    int SCHOOL_LESSON_BOOK = 11;

}
