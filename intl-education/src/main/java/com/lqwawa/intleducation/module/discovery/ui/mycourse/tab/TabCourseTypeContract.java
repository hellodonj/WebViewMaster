package com.lqwawa.intleducation.module.discovery.ui.mycourse.tab;

import android.support.annotation.Nullable;

import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.module.learn.vo.ChildrenListVo;

import java.util.List;

/**
 * @author medici
 * @desc 我的课程Type分类的Fragment的契约类
 */
public interface TabCourseTypeContract {

    interface Presenter extends BaseContract.Presenter{

    }

    interface View extends BaseContract.View<Presenter>{
        // 获取家长孩子的回调
        void updateParentChildrenData(@Nullable List<ChildrenListVo> childrenListVos);
    }

}
