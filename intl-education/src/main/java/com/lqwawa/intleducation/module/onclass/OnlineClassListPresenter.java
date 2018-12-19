package com.lqwawa.intleducation.module.onclass;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.online.OnlineStudyOrganEntity;
import com.lqwawa.intleducation.factory.data.entity.school.SchoolInfoEntity;
import com.lqwawa.intleducation.factory.helper.SchoolHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.discovery.ui.study.OnlineStudyContract;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

/**
 * @author medici
 * @desc 机构在线课堂Activity的Presenter
 */
public class OnlineClassListPresenter extends BasePresenter<OnlineClassListContract.View>
    implements OnlineClassListContract.Presenter{

    public OnlineClassListPresenter(OnlineClassListContract.View view) {
        super(view);
    }

    @Override
    public void requestSchoolInfo(@NonNull String schoolId) {
        String userId = UserHelper.getUserId();
        SchoolHelper.requestSchoolInfo(userId, schoolId, new DataSource.Callback<SchoolInfoEntity>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final OnlineClassListContract.View view = getView();
                if(!EmptyUtil.isEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(SchoolInfoEntity entity) {
                final OnlineClassListContract.View view = getView();
                if(!EmptyUtil.isEmpty(view) && !EmptyUtil.isEmpty(entity)){
                    view.updateSchoolInfoView(entity);
                }
            }
        });
    }

    @Override
    public void requestSubscribeSchool(@NonNull String organId, final boolean subscribe) {
        if(subscribe){
            // 关注机构
            SchoolHelper.requestSubscribeSchool(organId, new DataSource.Callback<Object>() {
                @Override
                public void onDataNotAvailable(int strRes) {
                    final OnlineClassListContract.View view = getView();
                    if(!EmptyUtil.isEmpty(view)){
                        view.showError(strRes);
                    }
                }

                @Override
                public void onDataLoaded(Object object) {
                    final OnlineClassListContract.View view = getView();
                    if(!EmptyUtil.isEmpty(view)){
                        view.updateAttentionView(subscribe);
                    }
                }
            });
        }else{
            // 取消关注机构
            SchoolHelper.requestUnSubscribeSchool(organId, new DataSource.Callback<Object>() {
                @Override
                public void onDataNotAvailable(int strRes) {
                    final OnlineClassListContract.View view = getView();
                    if(!EmptyUtil.isEmpty(view)){
                        view.showError(strRes);
                    }
                }

                @Override
                public void onDataLoaded(Object object) {
                    final OnlineClassListContract.View view = getView();
                    if(!EmptyUtil.isEmpty(view)){
                        view.updateAttentionView(subscribe);
                    }
                }
            });
        }

    }
}
