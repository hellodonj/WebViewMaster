package com.lqwawa.mooc.select;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.presenter.BaseContract;

/**
 * @author mrmedici
 * @desc 学校班级选择的契约来
 */
public interface SchoolClassSelectContract{

    interface Presenter extends BaseContract.Presenter{
    }

    interface View extends BaseContract.View<Presenter>{
    }

}
