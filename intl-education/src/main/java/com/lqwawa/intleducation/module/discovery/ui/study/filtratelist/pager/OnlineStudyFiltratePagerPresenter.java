package com.lqwawa.intleducation.module.discovery.ui.study.filtratelist.pager;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.JoinClassEntity;
import com.lqwawa.intleducation.factory.data.entity.OnlineClassEntity;
import com.lqwawa.intleducation.factory.helper.OnlineCourseHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.factory.presenter.BaseRecyclerPresenter;
import com.lqwawa.intleducation.module.discovery.ui.study.OnlineStudyContract;
import com.lqwawa.intleducation.module.discovery.ui.study.filtratelist.OnlineStudyFiltrateContract;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.List;

/**
 * @author medici
 * @desc 在线学习二级列表Pager页的Presenter
 */
public class OnlineStudyFiltratePagerPresenter extends BasePresenter<OnlineStudyFiltratePagerContract.View>
    implements OnlineStudyFiltratePagerContract.Presenter{

    public OnlineStudyFiltratePagerPresenter(OnlineStudyFiltratePagerContract.View view) {
        super(view);
    }

    @Override
    public void requestOnlineStudyDataFromLabel(int pageIndex, @NonNull String keyWord, int sort, int firstId, int secondId, int thirdId, int fourthId) {
        OnlineCourseHelper.requestOnlineStudyClassData(null,
                keyWord, pageIndex,
                AppConfig.PAGE_SIZE,
                sort, firstId, secondId, thirdId, fourthId,
                new DataSource.Callback<List<OnlineClassEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final OnlineStudyFiltratePagerContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<OnlineClassEntity> entities) {
                final OnlineStudyFiltratePagerContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view) && null != entities){
                    if(pageIndex == 0){
                        view.updateOnlineStudyClassDataView(entities);
                    }else{
                        view.updateOnlineStudyMoreClassDataView(entities);
                    }
                }
            }
        });
    }


    @Override
    public void requestLoadClassInfo(@NonNull String classId) {
        String memberId = UserHelper.getUserId();
        // 获取班级详情信息时候,弹出Dialog
        start();
        // 发送获取班级详情细信息的请求
        OnlineCourseHelper.loadOnlineClassInfo(memberId, classId, new DataSource.Callback<JoinClassEntity>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final OnlineStudyFiltratePagerContract.View view = getView();
                if(!EmptyUtil.isEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(JoinClassEntity joinClassEntity) {
                // 进行验证
                final OnlineStudyFiltratePagerContract.View view = getView();
                if(!EmptyUtil.isEmpty(view) && !EmptyUtil.isEmpty(joinClassEntity)){
                    view.onClassCheckSucceed(joinClassEntity);
                }
            }
        });
    }
}
