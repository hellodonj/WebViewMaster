package com.lqwawa.intleducation.module.organcourse;

/**
 * @desc:
 * @author: wangchao
 * @date: 2019/04/24
 * 学程馆类型 0 习课程馆 1练测馆  2 图书馆  3 视频馆 5三习教案馆
 */
public interface OrganLibraryType {
    // 习课程馆
    int TYPE_LQCOURSE_SHOP = 0;
    // 练测馆
    int TYPE_PRACTICE_LIBRARY = 1;
    // 图书馆
    int TYPE_LIBRARY = 2;
    // 视频馆
    int TYPE_VIDEO_LIBRARY = 3;

    // 全脑馆
    int TYPE_BRAIN_LIBRARY = 4;

    // 三习教案馆
    int TYPE_TEACHING_PLAN = 5;

    //校本资源库
    int TYPE_SCHOOL_LIBRARY = 1001;
    //精品资源库
    int TPYE_CHOICE_LIBRARY = 1002;
    //班级学程
    int TYPE_CLASS_COURSE = 1003;
    //关联学程
    int TYPE_CONNECT_COURSE = 1004;
    //线上课本馆
    int TYPE_ONLINE_COMMON_LIBRARY = 1005;
}
