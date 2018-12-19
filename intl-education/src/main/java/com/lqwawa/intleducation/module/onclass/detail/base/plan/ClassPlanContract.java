package com.lqwawa.intleducation.module.onclass.detail.base.plan;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.ClassDetailEntity;
import com.lqwawa.intleducation.factory.data.entity.JoinClassEntity;
import com.lqwawa.intleducation.factory.data.entity.LiveEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.module.learn.vo.LiveVo;
import com.lqwawa.intleducation.module.onclass.detail.base.introduction.ClassIntroductionContract;

import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 授课计划的契约类
 * @date 2018/06/01 16:11
 * @history v1.0
 * **********************************
 */
public interface ClassPlanContract {

    interface Presenter extends BaseContract.Presenter{
        // 请求获取空中课堂列表
        void requestOnlineClassLiveData(@NonNull String schoolId,
                                        @NonNull String classId,
                                        int pageIndex);
        // 获取班级详情信息
        void requestLoadClassInfo(@NonNull String classId);
        // 删除直播
        void requestDeleteLive(int id, @NonNull String classId, boolean deleteAll);
        // 判断有没有加入该直播
        void requestJudgeJoinLive(@NonNull LiveVo vo, @NonNull String schoolId, @NonNull String classId, @NonNull String liveId, boolean isAudition);
        // 请求完成授课
        void requestCompleteGive(@NonNull int id);
    }

    interface View extends BaseContract.View<Presenter>{
        // 直播数据回调
        void updateOnlineClassLiveView(@NonNull List<LiveEntity> entities);
        // 更多直播数据回调
        void updateOnlineClassMoreLiveView(@NonNull List<LiveEntity> entities);
        // 班级详细信息的回调
        void onClassCheckSucceed(@NonNull JoinClassEntity entity);
        // 删除直播返回
        void updateDeleteOnlineLiveView(boolean result);
        // 判断有没有加入该直播的信息回调
        void updateJudgeLiveView(@NonNull LiveVo vo, boolean result, boolean isAudition);
        // 完成授课成功
        void updateCompleteGiveView(boolean complete);
    }

}
