package com.lqwawa.intleducation.module.organcourse.filtrate;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.module.organcourse.base.SchoolPermissionContract;

import java.util.List;

/**
 * @desc 学程馆二级筛选页面
 * @author medici
 */
public interface OrganCourseFiltrateContract {

    interface Presenter extends SchoolPermissionContract.Presenter{
        // 获取机构相关的所有标签
        void requestOrganCourseLabelData(@NonNull String organId, @NonNull int parentId,
                                         @NonNull String level, int libraryType);

    }

    interface View extends SchoolPermissionContract.View<Presenter>{
        // 获取标签成功的回调
        void updateOrganCourseLabelView(@NonNull List<LQCourseConfigEntity> entities);
    }
}
