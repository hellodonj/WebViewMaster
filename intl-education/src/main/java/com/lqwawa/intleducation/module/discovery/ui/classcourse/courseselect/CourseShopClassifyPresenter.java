package com.lqwawa.intleducation.module.discovery.ui.classcourse.courseselect;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.Utils;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.helper.OrganCourseHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.LanguageType;
import com.lqwawa.intleducation.module.organcourse.base.SchoolPermissionPresenter;

import java.io.Serializable;
import java.util.List;

/**
 * @author mrmedici
 * @desc 班级学程供选择机构学程馆权限列表的页面的
 */
public class CourseShopClassifyPresenter extends SchoolPermissionPresenter<CourseShopClassifyContract.View>
    implements CourseShopClassifyContract.Presenter{

    public CourseShopClassifyPresenter(CourseShopClassifyContract.View view) {
        super(view);
    }

    @Override
    public void requestCourseShopClassifyData(@NonNull String organId, int libraryType) {
        // 获取中英文数据
        int languageRes = Utils.isZh(UIUtil.getContext()) ? LanguageType.LANGUAGE_CHINESE : LanguageType.LANGUAGE_OTHER;
        // organId = "5e069b1a-9d90-49ed-956c-946e9f934b68";
        OrganCourseHelper.requestOrganCourseClassifyData(organId, languageRes,
                libraryType, new DataSource.Callback<List<LQCourseConfigEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final CourseShopClassifyContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<LQCourseConfigEntity> lqCourseConfigEntities) {
                final CourseShopClassifyContract.View view = getView();
                // 只有一个分类
                if(EmptyUtil.isNotEmpty(view)){
                    view.updateCourseShopClassifyView(lqCourseConfigEntities);
                }
            }
        });
    }

    @Override
    public void requestCourseShopClassifyResourceData(@NonNull String organId, int libraryType) {
        // 获取中英文数据
        int languageRes = Utils.isZh(UIUtil.getContext()) ? LanguageType.LANGUAGE_CHINESE : LanguageType.LANGUAGE_OTHER;
        // organId = "5e069b1a-9d90-49ed-956c-946e9f934b68";
        OrganCourseHelper.requestOrganCourseClassifyResourceData(organId, languageRes,
                libraryType, new DataSource.Callback<List<LQCourseConfigEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final CourseShopClassifyContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<LQCourseConfigEntity> lqCourseConfigEntities) {
                final CourseShopClassifyContract.View view = getView();
                // 只有一个分类
                if(EmptyUtil.isNotEmpty(view)){
                    view.updateCourseShopClassifyResourceView(lqCourseConfigEntities);
                }
            }
        });
    }
}
