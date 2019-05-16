package com.lqwawa.intleducation.module.organcourse.filtrate;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.Utils;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.helper.OrganCourseHelper;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.LanguageType;
import com.lqwawa.intleducation.module.organcourse.base.SchoolPermissionPresenter;

import java.util.List;

/**
 * @desc 学程馆二级筛选页面的Presenter
 * @author medici
 */
public class OrganCourseFiltratePresenter extends SchoolPermissionPresenter<OrganCourseFiltrateContract.View>
        implements OrganCourseFiltrateContract.Presenter{

    public OrganCourseFiltratePresenter(OrganCourseFiltrateContract.View view) {
        super(view);
    }

    @Override
    public void requestOrganCourseLabelData(@NonNull String organId, @NonNull int parentId,
                                            @NonNull String level, int libraryType) {
        // 获取中英文数据
        // organId = "5e069b1a-9d90-49ed-956c-946e9f934b68";
        int languageRes = Utils.isZh(UIUtil.getContext()) ? LanguageType.LANGUAGE_CHINESE : LanguageType.LANGUAGE_OTHER;
        OrganCourseHelper.requestOrganClassifyLabelData(organId, languageRes, parentId, level,
                libraryType, new DataSource.Callback<List<LQCourseConfigEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final OrganCourseFiltrateContract.View view = (OrganCourseFiltrateContract.View) getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<LQCourseConfigEntity> entities) {
                final OrganCourseFiltrateContract.View view = (OrganCourseFiltrateContract.View) getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.updateOrganCourseLabelView(entities);
                }
            }
        });
    }

}
