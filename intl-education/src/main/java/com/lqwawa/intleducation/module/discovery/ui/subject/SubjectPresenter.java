package com.lqwawa.intleducation.module.discovery.ui.subject;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.Utils;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.helper.LQConfigHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.LanguageType;

import java.util.List;

/**
 * 科目设置的Presenter
 */
public class SubjectPresenter extends BasePresenter<SubjectContract.View>
    implements SubjectContract.Presenter{

    public SubjectPresenter(SubjectContract.View view) {
        super(view);
    }

    @Override
    public void requestTeacherConfigData(@NonNull String memberId) {
        // 获取中英文数据
        int languageRes = Utils.isZh(UIUtil.getContext()) ? LanguageType.LANGUAGE_CHINESE : LanguageType.LANGUAGE_OTHER;
        LQConfigHelper.requestSetupConfigData(memberId, SetupConfigType.TYPE_TEACHER,languageRes, new DataSource.Callback<List<LQCourseConfigEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final SubjectContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<LQCourseConfigEntity> entities) {
                final SubjectContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.updateTeacherConfigView(entities);
                }
            }
        });
    }
}
