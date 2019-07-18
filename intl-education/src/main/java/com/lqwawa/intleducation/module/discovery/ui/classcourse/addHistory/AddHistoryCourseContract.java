package com.lqwawa.intleducation.module.discovery.ui.classcourse.addHistory;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.course.ClassCourseEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;

import java.util.List;

/**
 * @author mrmedici
 * @desc 班级历史学程添加的契约类
 */
public class AddHistoryCourseContract {

    interface Presenter extends BaseContract.Presenter{
        void requestClassCourseData(@NonNull String classId,
                                    int role, @NonNull String name,
                                    @NonNull String level,
                                    int paramOneId, int paramTwoId,
                                    int pageIndex);
    }

    interface View extends BaseContract.View<Presenter>{
        void updateClassCourseView(List<ClassCourseEntity> entities);
        void updateMoreClassCourseView(List<ClassCourseEntity> entities);
    }
}
