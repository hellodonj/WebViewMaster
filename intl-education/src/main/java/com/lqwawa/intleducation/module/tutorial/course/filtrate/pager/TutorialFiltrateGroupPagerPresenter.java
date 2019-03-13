package com.lqwawa.intleducation.module.tutorial.course.filtrate.pager;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.course.TutorialGroupEntity;
import com.lqwawa.intleducation.factory.helper.TutorialHelper;
import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.tutorial.course.filtrate.TutorialFiltrateGroupContract;

import java.util.List;

/**
 * @author mrmedici
 * @desc 帮辅筛选片段页面的Presenter
 */
public class TutorialFiltrateGroupPagerPresenter extends BasePresenter<TutorialFiltrateGroupPagerContract.View>
    implements TutorialFiltrateGroupPagerContract.Presenter{

    public TutorialFiltrateGroupPagerPresenter(TutorialFiltrateGroupPagerContract.View view) {
        super(view);
    }

    @Override
    public void requestTutorDataByParams(@NonNull String level, int paramOneId, int paramTwoId, int paramThereId, String sort, int pageIndex) {
        TutorialHelper.requestTutorData(level, paramOneId, paramTwoId, paramThereId, sort, pageIndex, AppConfig.PAGE_SIZE, new DataSource.Callback<List<TutorialGroupEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final TutorialFiltrateGroupPagerContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<TutorialGroupEntity> tutorialGroupEntities) {
                final TutorialFiltrateGroupPagerContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    if(pageIndex == 0){
                        view.updateTutorView(tutorialGroupEntities);
                    }else{
                        view.updateMoreTutorView(tutorialGroupEntities);
                    }
                }
            }
        });
    }

    @Override
    public void requestAddTutorByStudentId(@NonNull String memberId, @NonNull String tutorMemberId, @NonNull String tutorName) {
        TutorialHelper.requestAddTutorByStudentId(memberId, tutorMemberId, tutorName, new DataSource.Callback<Boolean>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final TutorialFiltrateGroupPagerContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(Boolean aBoolean) {
                final TutorialFiltrateGroupPagerContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.updateAddTutorByStudentIdView(aBoolean);
                }
            }
        });
    }
}
