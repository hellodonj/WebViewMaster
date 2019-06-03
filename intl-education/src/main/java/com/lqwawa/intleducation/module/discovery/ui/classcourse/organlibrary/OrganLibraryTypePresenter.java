package com.lqwawa.intleducation.module.discovery.ui.classcourse.organlibrary;

import com.lqwawa.intleducation.factory.presenter.BasePresenter;

/**
 * @author medici
 * @desc 主页面我的课程页面，显示我的自主学习和我的孩子学习的Presenter
 */
public class OrganLibraryTypePresenter extends BasePresenter<OrganLibraryTypeContract.View>
    implements OrganLibraryTypeContract.Presenter{

    public OrganLibraryTypePresenter(OrganLibraryTypeContract.View view) {
        super(view);
    }
}
