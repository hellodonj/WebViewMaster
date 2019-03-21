package com.lqwawa.intleducation.module.tutorial.target.pager;

import android.support.annotation.Nullable;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.tutorial.TaskEntity;
import com.lqwawa.intleducation.factory.helper.TutorialHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.tutorial.marking.list.OrderByType;
import com.lqwawa.intleducation.module.tutorial.target.TutorialTargetTaskContract;

import java.util.List;

/**
 * @author mrmedici
 * @desc 我的帮辅学生，我的帮辅老师页面的Presenter
 */
public class TutorialTargetPagerTaskPresenter extends BasePresenter<TutorialTargetPagerTaskContract.View>
    implements TutorialTargetPagerTaskContract.Presenter {

    public TutorialTargetPagerTaskPresenter(TutorialTargetPagerTaskContract.View view) {
        super(view);
    }

    @Override
    public void requestWorkDataWithIdentityId(@Nullable String memberId, @Nullable String tutorMemberId,
                                              @Nullable String assistStudent_Id, @Nullable String title,
                                              @Nullable String createTimeBegin, @Nullable String createTimeEnd,
                                              @Nullable String startTimeBegin, @Nullable String startTimeEnd,
                                              int state, @OrderByType.OrderByTypeRes int orderByType, int pageIndex) {
        TutorialHelper.requestWorkDataWithIdentityId(memberId, tutorMemberId,
                assistStudent_Id, title,
                createTimeBegin,createTimeEnd,
                startTimeBegin, startTimeEnd,
                state, orderByType, pageIndex, AppConfig.PAGE_SIZE, new DataSource.Callback<List<TaskEntity>>() {
                    @Override
                    public void onDataNotAvailable(int strRes) {
                        final TutorialTargetPagerTaskContract.View view = getView();
                        if(EmptyUtil.isNotEmpty(view)){
                            view.showError(strRes);
                        }
                    }

                    @Override
                    public void onDataLoaded(List<TaskEntity> taskEntities) {
                        final TutorialTargetPagerTaskContract.View view = getView();
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
