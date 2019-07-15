package com.lqwawa.intleducation.module.discovery.ui.navigator;

import com.lqwawa.intleducation.common.ui.CommentDialog;

import java.util.Observable;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 课程详情定义接口回调
 * @date 2018/04/10 11:23
 * @history v1.0
 * **********************************
 */
public interface CourseDetailsNavigator{
    // 课程评价片段显示
    void courseCommentVisible();
    // 其它片段显示 课程介绍 课程大纲 直播
    void otherFragmentVisible();
    // 显示EditText文本与评分信息
    void setContent(CommentDialog.CommentData data);
    // 清除EditText文本与评分信息
    void clearContent();
    // 完成提交评论
    void commitComment();
    // 返回课程信息的观察者对象
    Observable getCourseObservable();
    //播放列表按钮显示
    void coursePlayListVisible();
}
