package com.lqwawa.intleducation.module.box.common;

import android.support.annotation.Nullable;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.tutorial.TaskEntity;
import com.lqwawa.intleducation.factory.helper.TutorialHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.tutorial.marking.list.OrderByType;
import com.lqwawa.intleducation.module.tutorial.marking.list.pager.TutorialMarkingPagerContract;

import java.util.List;

/**
 * @author mrmedici
 * @desc 最新作业页面的Presenter
 */
public class CommonMarkingListPresenter extends BasePresenter<CommonMarkingListContract.View>
    implements CommonMarkingListContract.Presenter{

    public CommonMarkingListPresenter(CommonMarkingListContract.View view) {
        super(view);
    }

    @Override
    public void requestWorkDataWithIdentityId(@Nullable String memberId, @Nullable String tutorMemberId,
                                              @Nullable String assistStudent_Id, @Nullable String title,
                                              @Nullable String createTimeBegin, @Nullable String createTimeEnd,
                                              @Nullable String startTimeBegin, @Nullable String startTimeEnd,
                                              int state, @OrderByType.OrderByTypeRes int orderByType, int pageIndex, int pageSize) {
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
                        final CommonMarkingListContract.View view = getView();
                        if(EmptyUtil.isNotEmpty(view)){
                            view.showError(strRes);
                        }
                    }

                    @Override
                    public void onDataLoaded(List<TaskEntity> taskEntities) {
                        final CommonMarkingListContract.View view = getView();
                        if(EmptyUtil.isNotEmpty(view)){
                            view.updateWorkDataWithIdentityIdView(taskEntities);
                        }
                    }
                });
    }
}
