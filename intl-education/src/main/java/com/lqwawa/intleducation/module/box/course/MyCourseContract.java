package com.lqwawa.intleducation.module.box.course;

import android.support.annotation.Nullable;

import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.module.learn.vo.ChildrenListVo;

import java.util.List;

/**
 * @author medici
 * @desc 我的课程的契约类
 */
public interface MyCourseContract {

    interface Presenter extends BaseContract.Presenter{
        void requestParentChildData();
    }

    interface View extends BaseContract.View<Presenter>{
        // 获取家长孩子的回调
        void updateParentChildrenData(@Nullable List<ChildrenListVo> childrenListVos);
    }

}