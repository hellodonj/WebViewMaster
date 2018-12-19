package com.lqwawa.mooc.select;

import com.lqwawa.intleducation.factory.presenter.BasePresenter;

/**
 * @author mrmedici
 * @desc 学校班级选择的Presenter
 */
public class SchoolClassSelectPresenter extends BasePresenter<SchoolClassSelectContract.View>
    implements SchoolClassSelectContract.Presenter{

    public SchoolClassSelectPresenter(SchoolClassSelectContract.View view) {
        super(view);
    }
}
