package com.lqwawa.intleducation.module.discovery.ui.study.filtrate.pager;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.OnlineClassEntity;
import com.lqwawa.intleducation.factory.helper.OnlineCourseHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.discovery.ui.study.OnlineStudyType;
import com.lqwawa.intleducation.module.discovery.ui.study.classifyclass.OnlineClassClassifyContract;
import com.lqwawa.intleducation.module.discovery.ui.study.filtrate.NewOnlineStudyFiltrateContract;

import java.util.List;

/**
 * @author mrmedici
 * @desc V5.11讲授课堂类型筛选Pager的Presenter
 */
public class NewOnlineStudyFiltratePagerPresenter extends BasePresenter<NewOnlineStudyFiltratePagerContract.View>
    implements NewOnlineStudyFiltratePagerContract.Presenter{

    public NewOnlineStudyFiltratePagerPresenter(NewOnlineStudyFiltratePagerContract.View view) {
        super(view);
    }

    @Override
    public void requestOnlineClassData(@NonNull String keyWord,
                                       int pageIndex, int pageSize,
                                       @OnlineStudyType.OnlineStudyRes int sort,
                                       int firstId, int secondId, int thirdId, int fourthId) {

        OnlineCourseHelper.requestOnlineStudyClassData(null,
                keyWord, pageIndex,
                AppConfig.PAGE_SIZE,
                sort, firstId, secondId, thirdId, fourthId,
                new DataSource.Callback<List<OnlineClassEntity>>() {
                    @Override
                    public void onDataNotAvailable(int strRes) {
                        final NewOnlineStudyFiltratePagerContract.View view = getView();
                        if(EmptyUtil.isNotEmpty(view)){
                            view.showError(strRes);
                        }
                    }

                    @Override
                    public void onDataLoaded(List<OnlineClassEntity> entities) {
                        final NewOnlineStudyFiltratePagerContract.View view = getView();
                        if(EmptyUtil.isNotEmpty(view) && null != entities){
                            if(pageIndex == 0){
                                view.updateOnlineClassView(entities);
                            }else{
                                view.updateMoreOnlineClassView(entities);
                            }
                        }
                    }
                });
    }
}
