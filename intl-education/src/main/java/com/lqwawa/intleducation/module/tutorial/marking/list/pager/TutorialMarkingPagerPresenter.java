package com.lqwawa.intleducation.module.tutorial.marking.list.pager;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.tutorial.DateFlagEntity;
import com.lqwawa.intleducation.factory.data.entity.tutorial.TaskEntity;
import com.lqwawa.intleducation.factory.helper.TutorialHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.tutorial.marking.list.OrderByType;
import com.lqwawa.intleducation.module.tutorial.marking.list.TutorialMarkingListContract;

import java.util.List;

/**
 * @author mrmedici
 * @desc 帮辅批阅列表页面的Presenter
 */
public class TutorialMarkingPagerPresenter extends BasePresenter<TutorialMarkingPagerContract.View>
    implements TutorialMarkingPagerContract.Presenter {

    public TutorialMarkingPagerPresenter(TutorialMarkingPagerContract.View view) {
        super(view);
    }

    @Override
    public void requestDateFlagForAssist(int position,@NonNull String memberId, @NonNull String role, @NonNull String startTimeBegin, @NonNull String startTimeEnd, int state) {
        TutorialHelper.requestDateFlagForAssist(memberId, role, startTimeBegin, startTimeEnd, state, new DataSource.Callback<List<DateFlagEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final TutorialMarkingPagerContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<DateFlagEntity> flagEntities) {
                final TutorialMarkingPagerContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.updateDataFlagForAssist(startTimeBegin,position,flagEntities);
                }
            }
        });
    }

    @Override
    public void requestWorkDataWithIdentityId(@Nullable String memberId, @Nullable String tutorMemberId,
                                              @Nullable String assistStudent_Id, @Nullable String title,
                                              @Nullable String createTimeBegin, @Nullable String createTimeEnd,
                                              @Nullable String startTimeBegin, @Nullable String startTimeEnd,
                                              int state, @OrderByType.OrderByTypeRes int orderByType, int pageIndex) {
        // TODO 测试数据
        assistStudent_Id = "1";
        memberId = "";
        tutorMemberId = "";
        createTimeBegin = "";
        createTimeEnd = "";
        startTimeBegin = "";
        startTimeEnd = "";

        TutorialHelper.requestWorkDataWithIdentityId(memberId, tutorMemberId,
                assistStudent_Id, title,
                createTimeBegin,createTimeEnd,
                startTimeBegin, startTimeEnd,
                state, orderByType, pageIndex, AppConfig.PAGE_SIZE, new DataSource.Callback<List<TaskEntity>>() {
                    @Override
                    public void onDataNotAvailable(int strRes) {
                        final TutorialMarkingPagerContract.View view = getView();
                        if(EmptyUtil.isNotEmpty(view)){
                            view.showError(strRes);
                        }
                    }

                    @Override
                    public void onDataLoaded(List<TaskEntity> taskEntities) {
                        final TutorialMarkingPagerContract.View view = getView();
                        if(EmptyUtil.isNotEmpty(view)){
                            if(pageIndex == 0){
                                view.updateWorkDataWithIdentityIdView(taskEntities);
                            }else{
                                view.updateMoreWorkDataWithIdentityIdView(taskEntities);
                            }
                        }
                    }
                });
    }
}
