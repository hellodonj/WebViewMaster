package com.lqwawa.intleducation.module.discovery.ui.myonline;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.JoinClassEntity;
import com.lqwawa.intleducation.factory.data.entity.OnlineClassEntity;
import com.lqwawa.intleducation.factory.helper.MyCourseHelper;
import com.lqwawa.intleducation.factory.helper.OnlineCourseHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.discovery.ui.mycourse.TabCourseContract;
import com.lqwawa.intleducation.module.onclass.pager.OnlineClassPagerContract;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.List;

/**
 * @author medici
 * @desc 我的在线学习的Presenter
 */
public class MyOnlinePagerPresenter extends BasePresenter<MyOnlinePagerContract.View>
    implements MyOnlinePagerContract.Presenter{

    public MyOnlinePagerPresenter(MyOnlinePagerContract.View view) {
        super(view);
    }

    @Override
    public void requestOnlineCourseData(@NonNull String curMemberId, @NonNull String keyWord, int pageIndex) {
        MyCourseHelper.requestOnlineCourseData(curMemberId,keyWord, pageIndex, AppConfig.PAGE_SIZE, new DataSource.Callback<List<OnlineClassEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final MyOnlinePagerContract.View view = getView();
                if(!EmptyUtil.isEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<OnlineClassEntity> entities) {
                final MyOnlinePagerContract.View view = getView();
                if(!EmptyUtil.isEmpty(view)){
                    if(!EmptyUtil.isEmpty(view) && entities != null){
                        if(pageIndex == 0){
                            view.updateOnlineCourseView(entities);
                        }else{
                            // 回调更多数据
                            view.updateMoreOnlineCourseView(entities);
                        }
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
                final MyOnlinePagerContract.View view = getView();
                if(!EmptyUtil.isEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(JoinClassEntity joinClassEntity) {
                // 进行验证
                final MyOnlinePagerContract.View view = getView();
                if(!EmptyUtil.isEmpty(view) && !EmptyUtil.isEmpty(joinClassEntity)){
                    view.onClassCheckSucceed(joinClassEntity);
                }
            }
        });
    }
}
