package com.lqwawa.intleducation.module.tutorial.course.filtrate;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.Utils;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.data.entity.course.TutorialGroupEntity;
import com.lqwawa.intleducation.factory.helper.CourseHelper;
import com.lqwawa.intleducation.factory.helper.LQConfigHelper;
import com.lqwawa.intleducation.factory.helper.TutorialHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.LanguageType;
import com.lqwawa.intleducation.module.discovery.ui.subject.SetupConfigType;
import com.lqwawa.intleducation.module.tutorial.course.TutorialGroupContract;

import java.util.List;

/**
 * @author mrmedici
 * @desc 帮辅群标签筛选页面的Presenter
 */
public class TutorialFiltrateGroupPresenter extends BasePresenter<TutorialFiltrateGroupContract.View>
    implements TutorialFiltrateGroupContract.Presenter{

    public TutorialFiltrateGroupPresenter(TutorialFiltrateGroupContract.View view) {
        super(view);
    }


    @Override
    public void requestTutorialConfigData(@NonNull String memberId) {
        int languageRes = Utils.isZh(UIUtil.getContext()) ? LanguageType.LANGUAGE_CHINESE : LanguageType.LANGUAGE_OTHER;
        LQConfigHelper.requestAssignConfigData(memberId, languageRes, new DataSource.Callback<List<LQCourseConfigEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final TutorialFiltrateGroupContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<LQCourseConfigEntity> entities) {
                final TutorialFiltrateGroupContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.updateTutorialConfigView(entities);
                }
            }
        });
    }
}
