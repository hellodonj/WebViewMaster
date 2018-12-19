package com.lqwawa.client.pojo;

/**
 * ======================================================
 * Created by : Brave_Qu on 2017/12/20 16:02
 * E-Mail Address:863378689@qq.com
 * Describe:source来自哪个模块（如 个人资源库 、 校本资源库 、 精品资源库 、、、 ）
 * ======================================================
 */

public interface SourceFromType {
    //其他
    int OTHER = 0;
    //lq学程
    int LQ_COURSE = 1;
    //直播
    int ONLINE_LIVE = 2;
    //校本资源库
    int PUBLIC_LIBRARY = 11;
    //精品资源库
    int CHOICE_LIBRARY = 12;
    //lq精品学程
    int LQ_CHOICE_COURSE = 13;
    //学习任务
    int STUDY_TASK = 14;
    //空中课堂
    int AIRCLASS_ONLINE = 15;
    //创意秀
    int CREATIVE_SHOW = 21;
    //个人资源库
    int PERSONAL_LIBRARY = 31;
    //我的学程
    int LQ_MY_COURSE = 32;
    //我的直播
    int MY_ONLINE_LIVE = 33;

}
