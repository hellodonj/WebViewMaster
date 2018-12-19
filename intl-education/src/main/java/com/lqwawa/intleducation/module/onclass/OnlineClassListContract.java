package com.lqwawa.intleducation.module.onclass;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.online.OnlineStudyOrganEntity;
import com.lqwawa.intleducation.factory.data.entity.school.SchoolInfoEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;

/**
 * @author medici
 * @desc 机构在线课堂Activity的Presenter
 */
public interface OnlineClassListContract {

    interface Presenter extends BaseContract.Presenter{
        // 获取机构信息
        void requestSchoolInfo(@NonNull String schoolId);
        // 获取关注取消关注机构
        void requestSubscribeSchool(@NonNull String organId, boolean subscribe);
    }

    interface View extends BaseContract.View<Presenter>{
        // 返回机构信息
        void updateSchoolInfoView(@NonNull SchoolInfoEntity infoEntity);
        // 关注取消关注的返回
        void updateAttentionView(boolean state);
    }

}
