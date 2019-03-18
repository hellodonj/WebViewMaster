package com.galaxyschool.app.wawaschool.pojo.weike;

import com.galaxyschool.app.wawaschool.pojo.ExerciseAnswerCardParam;
import com.galaxyschool.app.wawaschool.pojo.TaskMarkParam;
import com.lqwawa.intleducation.module.tutorial.marking.choice.QuestionResourceModel;

import java.io.Serializable;

/**
 * @author: wangchao
 * @date: 2017/09/09 14:55
 */

public class PlaybackParam implements Serializable {

    // 是不是自己的课件
    public boolean mIsOwner;
    
    // 判断微课是否授权
    public boolean mIsAuth;

    // 批阅相关参数
    public TaskMarkParam taskMarkParam;

    // 判断微课是否隐藏收藏标签
    public boolean mIsHideCollectTip;

    // 是否隐藏工具栏
    public boolean mIsHideToolBar;

    //答题卡信息处理类
    public ExerciseAnswerCardParam exerciseCardParam;

    //打开指定页的pageIndex
    public int pageIndex = -1;

    //申请批阅
    public boolean applyMark;

    //申请批阅的相关数据
    public QuestionResourceModel applyMarkdata;

    public boolean isAssistanceModel;//帮辅模式批阅
}
