package com.lqwawa.intleducation.module.discovery.ui.classcourse.organlibrary;

import com.lqwawa.intleducation.module.organcourse.base.SchoolPermissionContract;

/**
 * @author medici
 * @desc 主页面我的课程页面，显示我的自主学习和我的孩子学习的契约类
 */
public interface OrganLibraryTypeContract {

    interface Presenter extends SchoolPermissionContract.Presenter{

    }

    interface View extends SchoolPermissionContract.View<OrganLibraryTypeContract.Presenter>{

    }

}
