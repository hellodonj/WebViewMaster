package com.lqwawa.intleducation.module.onclass.detail.base;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.ClassDetailEntity;
import com.lqwawa.intleducation.factory.data.entity.school.SchoolInfoEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.osastudio.common.popmenu.EntryBean;

import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 班级详情基类契约类
 * @date 2018/06/01 11:42
 * @history v1.0
 * **********************************
 */
public interface BaseClassDetailContract {

    interface Presenter extends BaseContract.Presenter{
        // 获取班级详情
        void requestClassDetail(int id, boolean refreshHeader);
        // 获取机构详情
        void requestSchoolInfo(@NonNull String schoolId);
        // 分享
        void share(@NonNull String title,
                   @NonNull String description,
                   @NonNull String thumbnailUrl,
                   @NonNull String url);
        /**
         * 提交评论
         * @param type 0评论 1回复
         * @param courseId 班级Id
         * @param commentId 回复人的id
         * @param content 评论内容
         * @param starLevel 评星
         */
        void requestCommitComment(int type, int courseId, @NonNull String commentId,
                                  @NonNull String content, int starLevel);
    }

    interface View<T extends Presenter> extends BaseContract.View<T>{
        // 获取班级详情,UI回调
        void updateClassDetailView(boolean refreshHeader, @NonNull ClassDetailEntity entity);
        // 获取机构详情,UI回调
        void updateSchoolInfoView(@NonNull SchoolInfoEntity entity);
        // 提交评论，返回评论结果
        void commitCommentResult(boolean isSucceed);
    }

}
