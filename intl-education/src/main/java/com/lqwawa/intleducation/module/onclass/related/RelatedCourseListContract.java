package com.lqwawa.intleducation.module.onclass.related;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.ClassDetailEntity;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;

import java.util.List;

/**
 * @author mrmedici
 * @desc 相关课程列表的契约类
 */
public class RelatedCourseListContract {

    interface Presenter extends BaseContract.Presenter{
        void requestConfigWithParam(@NonNull ClassDetailEntity.ParamBean param);
    }

    interface View extends BaseContract.View<Presenter>{
        void updateConfigView(@NonNull int parentId,
                              @NonNull ClassDetailEntity.ParamBean param,
                              @NonNull List<LQCourseConfigEntity> entities);
    }

}
