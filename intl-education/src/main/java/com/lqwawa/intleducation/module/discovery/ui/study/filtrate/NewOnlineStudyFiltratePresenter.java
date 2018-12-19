package com.lqwawa.intleducation.module.discovery.ui.study.filtrate;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.Utils;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.online.NewOnlineConfigEntity;
import com.lqwawa.intleducation.factory.helper.OnlineCourseHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.LanguageType;
import com.lqwawa.intleducation.module.discovery.ui.study.OnlineStudyContract;

import java.util.List;

/**
 * @author mrmedici
 * @desc V5.11讲授课堂类型筛选的Presenter
 */
public class NewOnlineStudyFiltratePresenter extends BasePresenter<NewOnlineStudyFiltrateContract.View>
    implements NewOnlineStudyFiltrateContract.Presenter{

    public NewOnlineStudyFiltratePresenter(NewOnlineStudyFiltrateContract.View view) {
        super(view);
    }

    @Override
    public void requestOnlineStudyLabelData() {
        // 获取中英文数据
        int languageRes = Utils.isZh(UIUtil.getContext()) ? LanguageType.LANGUAGE_CHINESE : LanguageType.LANGUAGE_OTHER;
        OnlineCourseHelper.requestNewOnlineStudyLabelData(languageRes,new DataSource.Callback<List<NewOnlineConfigEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final NewOnlineStudyFiltrateContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<NewOnlineConfigEntity> entities) {
                final NewOnlineStudyFiltrateContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view) && EmptyUtil.isNotEmpty(entities)){
                    view.updateOnlineStudyLabelView(entities);
                }
            }
        });
    }
}
