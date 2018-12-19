package com.lqwawa.intleducation.module.discovery.ui.classcourse.courseselect;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.data.entity.school.CheckSchoolPermissionEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.module.organcourse.base.SchoolPermissionContract;

import java.util.List;

/**
 * @author mrmedici
 * @desc 班级学程供选择机构学程馆权限列表的页面契约类
 */
public interface CourseShopClassifyContract {

    interface Presenter extends SchoolPermissionContract.Presenter{
        void requestCourseShopClassifyData(@NonNull String organId);
    }

    interface View extends SchoolPermissionContract.View<Presenter>{
        void updateCourseShopClassifyView(@NonNull List<LQCourseConfigEntity> entities);
    }
}
