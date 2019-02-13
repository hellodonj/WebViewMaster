package com.lqwawa.intleducation.module.discovery.ui.study.newfiltrate;

import android.support.annotation.Nullable;

import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.Utils;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.online.NewOnlineConfigEntity;
import com.lqwawa.intleducation.factory.helper.OnlineCourseHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.LanguageType;

import java.util.List;

/**
 * @author mrmedici
 * @desc 小语种,国际课程,国家课程 在线课堂更多页面的Presenter
 */
public class NewOnlineClassifyFiltratePresenter extends BasePresenter<NewOnlineClassifyFiltrateContract.View>
    implements NewOnlineClassifyFiltrateContract.Presenter{

    public NewOnlineClassifyFiltratePresenter(NewOnlineClassifyFiltrateContract.View view) {
        super(view);
    }

    @Override
    public void requestNewOnlineClassifyConfigData(@Nullable NewOnlineClassifyFiltrateActivity.DataType dataType) {
        // 获取中英文数据
        int languageRes = Utils.isZh(UIUtil.getContext()) ? LanguageType.LANGUAGE_CHINESE : LanguageType.LANGUAGE_OTHER;
        OnlineCourseHelper.requestNewOnlineClassifyConfigData(dataType.getIndex(), languageRes, new DataSource.Callback<List<NewOnlineConfigEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final NewOnlineClassifyFiltrateContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<NewOnlineConfigEntity> entities) {
                final NewOnlineClassifyFiltrateContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.updateNewOnlineClassifyConfigView(entities);
                }
            }
        });
    }
}
