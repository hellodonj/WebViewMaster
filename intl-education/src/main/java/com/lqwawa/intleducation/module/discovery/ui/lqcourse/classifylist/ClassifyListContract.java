package com.lqwawa.intleducation.module.discovery.ui.lqcourse.classifylist;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;

import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 分类列表数据的契约类
 * @date 2018/05/02 11:10
 * @history v1.0
 * **********************************
 */
public interface ClassifyListContract{

    interface Presenter extends BaseContract.Presenter{
        void requestClassifyData(@NonNull int parentId, @NonNull int level);
    }

    interface View extends BaseContract.View<Presenter>{
        void updateClassifyView(List<LQCourseConfigEntity> entities);
    }

}
