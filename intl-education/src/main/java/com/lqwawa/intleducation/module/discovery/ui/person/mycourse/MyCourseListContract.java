package com.lqwawa.intleducation.module.discovery.ui.person.mycourse;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.module.learn.vo.ChildrenListVo;

import java.util.List;

/**
 * @author medici
 * @desc 我的学程契约类
 */
public interface MyCourseListContract {

    interface Presenter extends BaseContract.Presenter{

    }

    interface View extends BaseContract.View<Presenter>{
        // 获取家长孩子的回调
        void updateParentChildrenData(@Nullable List<ChildrenListVo> childrenListVos);
    }

}
