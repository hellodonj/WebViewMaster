package com.lqwawa.intleducation.module.onclass;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.JoinClassEntity;
import com.lqwawa.intleducation.factory.data.entity.OnlineClassEntity;
import com.lqwawa.intleducation.factory.data.entity.online.NewOnlineConfigEntity;
import com.lqwawa.intleducation.factory.data.entity.online.ParamResponseVo;
import com.lqwawa.intleducation.factory.presenter.BaseContract;

import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 在线课堂班级列表的契约类
 * @date 2018/05/31 14:58
 * @history v1.0
 * **********************************
 */
public interface OnlineClassContract {

    interface Presenter extends BaseContract.Presenter{
        // 进行关键词搜索班级或者讲师
        void onSearch(@NonNull String schoolId, @NonNull String keyword, int pageIndex);
        // 获取在线课堂班级列表
        void requestOnlineClassData(@NonNull String schoolId, int pageIndex);
        // 获取课程关联在线课堂列表
        void requestOnlineClassByCourseId(@NonNull String courseId, int pageIndex);
        // 获取班级详情信息
        void requestLoadClassInfo(@NonNull String classId);
        // 获取在线学习标签
        void requestOnlineStudyLabelData();
    }

    interface View extends BaseContract.View<Presenter>{
        // 在线课堂班级列表数据回调
        void updateClassListView(@NonNull ParamResponseVo<List<OnlineClassEntity>> vo);
        // 在线课堂班级列表更多数据回调
        void updateClassMoreListView(@NonNull ParamResponseVo<List<OnlineClassEntity>> vo);
        // 用户已经参加该课程的回调
        void onClassCheckSucceed(@NonNull JoinClassEntity entity);
        // 在线学校标签数据的UI回调
        void updateOnlineStudyLabelView(@NonNull List<NewOnlineConfigEntity> entities);
    }

}
