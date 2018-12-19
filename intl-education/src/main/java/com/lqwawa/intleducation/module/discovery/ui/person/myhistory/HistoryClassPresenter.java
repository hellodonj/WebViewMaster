package com.lqwawa.intleducation.module.discovery.ui.person.myhistory;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.JoinClassEntity;
import com.lqwawa.intleducation.factory.data.entity.OnlineClassEntity;
import com.lqwawa.intleducation.factory.helper.MyCourseHelper;
import com.lqwawa.intleducation.factory.helper.OnlineCourseHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.discovery.ui.person.mygive.MyGiveInstructionContract;
import com.lqwawa.intleducation.module.discovery.ui.person.mygive.pager.MyGiveInstructionPagerContract;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.List;

/**
 * @author medici
 * @desc 我的授课历史班契约类
 */
public class HistoryClassPresenter extends BasePresenter<HistoryClassContract.View>
    implements HistoryClassContract.Presenter{

    public HistoryClassPresenter(HistoryClassContract.View view) {
        super(view);
    }

    @Override
    public void requestMyGiveOnlineCourse(@NonNull String memberId, @NonNull String keyWord, int pageIndex) {
        MyCourseHelper.requestMyGiveOnlineCourseData(memberId, keyWord,3, pageIndex, AppConfig.PAGE_SIZE, new DataSource.Callback<List<OnlineClassEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final HistoryClassContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<OnlineClassEntity> entities) {
                final HistoryClassContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    if(pageIndex == 0){
                        view.updateMyGiveOnlineCourseView(entities);
                    }else{
                        view.updateMyGiveOnlineMoreCourseView(entities);
                    }
                }
            }
        });
    }

    @Override
    public void requestLoadClassInfo(@NonNull String classId) {
        String memberId = UserHelper.getUserId();
        // 发送获取班级详情细信息的请求
        OnlineCourseHelper.loadOnlineClassInfo(memberId, classId, new DataSource.Callback<JoinClassEntity>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final HistoryClassContract.View view = getView();
                if(!EmptyUtil.isEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(JoinClassEntity joinClassEntity) {
                // 进行验证
                final HistoryClassContract.View view = getView();
                if(!EmptyUtil.isEmpty(view) && !EmptyUtil.isEmpty(joinClassEntity)){
                    view.onClassCheckSucceed(joinClassEntity);
                }
            }
        });
    }
}
