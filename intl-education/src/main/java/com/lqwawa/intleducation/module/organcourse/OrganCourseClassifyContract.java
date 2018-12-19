package com.lqwawa.intleducation.module.organcourse;

import com.lqwawa.intleducation.module.organcourse.base.SchoolPermissionContract;

/**
 * @desc 实体机构学程馆页面的契约类
 * @author medici
 */
public interface OrganCourseClassifyContract {


    interface Presenter extends SchoolPermissionContract.Presenter{
    }

    interface View extends SchoolPermissionContract.View<Presenter>{
    }

}
