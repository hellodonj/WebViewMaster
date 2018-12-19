package com.lqwawa.intleducation.module.discovery.ui.study.classifyclass;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.JoinClassEntity;
import com.lqwawa.intleducation.factory.data.entity.OnlineClassEntity;
import com.lqwawa.intleducation.factory.helper.OnlineCourseHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.discovery.ui.study.filtratelist.pager.OnlineStudyFiltratePagerContract;
import com.lqwawa.intleducation.module.onclass.OnlineSortType;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 在线学习在线班级分类列表的Presenter
 * @date 2018/05/31 15:05
 * @history v1.0
 * **********************************
 */
public class OnlineClassClassifyPresenter extends BasePresenter<OnlineClassClassifyContract.View>
    implements OnlineClassClassifyContract.Presenter{

    public OnlineClassClassifyPresenter(OnlineClassClassifyContract.View view) {
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
                        final OnlineClassClassifyContract.View view = getView();
                        if(EmptyUtil.isNotEmpty(view)){
                            view.showError(strRes);
                        }
                    }

                    @Override
                    public void onDataLoaded(List<OnlineClassEntity> entities) {
                        final OnlineClassClassifyContract.View view = getView();
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
                final OnlineClassClassifyContract.View view = getView();
                if(!EmptyUtil.isEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(JoinClassEntity joinClassEntity) {
                // 进行验证
                final OnlineClassClassifyContract.View view = getView();
                if(!EmptyUtil.isEmpty(view) && !EmptyUtil.isEmpty(joinClassEntity)){
                    view.onClassCheckSucceed(joinClassEntity);
                }
            }
        });
    }
}
