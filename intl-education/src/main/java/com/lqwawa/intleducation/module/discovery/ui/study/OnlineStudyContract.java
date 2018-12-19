package com.lqwawa.intleducation.module.discovery.ui.study;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.JoinClassEntity;
import com.lqwawa.intleducation.factory.data.entity.online.NewOnlineConfigEntity;
import com.lqwawa.intleducation.factory.data.entity.OnlineClassEntity;
import com.lqwawa.intleducation.factory.data.entity.online.OnlineStudyOrganEntity;
import com.lqwawa.intleducation.factory.data.entity.school.SchoolInfoEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;

import java.util.List;

/**
 * @author medici
 * @desc 在线学习的契约类
 */
public interface OnlineStudyContract {

    interface Presenter extends BaseContract.Presenter{
        // 获取在线学习标签
        void requestOnlineStudyLabelData();
        // 获取到在线学习的机构信息，热门数据，最新数据
        void requestOnlineStudyOrganData();
        // 获取班级详情信息
        void requestLoadClassInfo(@NonNull String classId);
        // 获取机构信息
        void requestSchoolInfo(@NonNull String schoolId, @NonNull OnlineStudyOrganEntity entity);
    }

    interface View extends BaseContract.View<Presenter>{
        // 在线学校标签数据的UI回调
        void updateOnlineStudyLabelView(@NonNull List<NewOnlineConfigEntity> entities);
        // 在线学习机构信息数据回调
        void updateOnlineStudyOrganView(@NonNull List<OnlineStudyOrganEntity> entities);
        // 在线学习最新课堂数据回调
        void updateOnlineStudyLatestView(@NonNull List<OnlineClassEntity> entities);
        // 在线学习热门课堂数据回调
        void updateOnlineStudyHotView(@NonNull List<OnlineClassEntity> entities);
        // 用户已经参加该课程的回调
        void onClassCheckSucceed(@NonNull JoinClassEntity entity);
        // 返回机构信息
        void updateSchoolInfoView(@NonNull SchoolInfoEntity infoEntity, @NonNull OnlineStudyOrganEntity entity);
    }

}
