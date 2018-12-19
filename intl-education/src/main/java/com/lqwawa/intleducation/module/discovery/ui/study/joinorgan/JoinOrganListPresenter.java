package com.lqwawa.intleducation.module.discovery.ui.study.joinorgan;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.online.OnlineStudyOrganEntity;
import com.lqwawa.intleducation.factory.data.entity.school.SchoolInfoEntity;
import com.lqwawa.intleducation.factory.helper.SchoolHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.List;

/**
 * @author mrmedici
 * @desc 入驻机构列表的Presenter
 */
public class JoinOrganListPresenter extends BasePresenter<JoinOrganListContract.View>
    implements JoinOrganListContract.Presenter{

    public JoinOrganListPresenter(JoinOrganListContract.View view) {
        super(view);
    }

    @Override
    public void requestSchoolData(int pageIndex, @Nullable String keySearch) {
        // 获取机构列表
        SchoolHelper.requestSchoolData(pageIndex, AppConfig.PAGE_SIZE, keySearch, new DataSource.Callback<List<OnlineStudyOrganEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final JoinOrganListContract.View view = getView();
                if(!EmptyUtil.isEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<OnlineStudyOrganEntity> entities) {
                final JoinOrganListContract.View view = getView();
                if(!EmptyUtil.isEmpty(view) && null != entities){
                    if(pageIndex == 0){
                        view.updateSchoolDataView(entities);
                    }else{
                        view.updateSchoolMoreDataView(entities);
                    }
                }
            }
        });
    }

    @Override
    public void requestSchoolInfo(@NonNull String schoolId, @NonNull final OnlineStudyOrganEntity organEntity) {
        String userId = UserHelper.getUserId();
        SchoolHelper.requestSchoolInfo(userId, schoolId, new DataSource.Callback<SchoolInfoEntity>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final JoinOrganListContract.View view = getView();
                if(!EmptyUtil.isEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(SchoolInfoEntity entity) {
                final JoinOrganListContract.View view = getView();
                if(!EmptyUtil.isEmpty(view) && !EmptyUtil.isEmpty(entity)){
                    view.updateSchoolInfoView(entity,organEntity);
                }
            }
        });
    }
}
