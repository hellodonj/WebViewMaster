package com.lqwawa.intleducation.module.discovery.ui.study.joinorgan;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lqwawa.intleducation.factory.data.entity.online.OnlineStudyOrganEntity;
import com.lqwawa.intleducation.factory.data.entity.school.SchoolInfoEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;

import java.util.List;

/**
 * @author mrmedici
 * @desc 入驻机构列表的契约类
 */
public class JoinOrganListContract {
    interface Presenter extends BaseContract.Presenter{
        // 获取机构列表信息
        void requestSchoolData(int pageIndex, @Nullable String keySearch);
        // 获取机构信息
        void requestSchoolInfo(@NonNull String schoolId, @NonNull OnlineStudyOrganEntity entity);
    }

    interface View extends BaseContract.View<Presenter>{// 返回机构信息
        void updateSchoolDataView(@NonNull List<OnlineStudyOrganEntity> entities);
        void updateSchoolMoreDataView(@NonNull List<OnlineStudyOrganEntity> entities);
        void updateSchoolInfoView(@NonNull SchoolInfoEntity infoEntity, @NonNull OnlineStudyOrganEntity entity);
    }
}
