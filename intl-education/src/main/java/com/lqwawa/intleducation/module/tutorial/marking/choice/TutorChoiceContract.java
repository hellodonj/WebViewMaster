package com.lqwawa.intleducation.module.tutorial.marking.choice;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lqwawa.intleducation.factory.data.entity.tutorial.TutorChoiceEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;

import java.util.List;

/**
 * @author mrmedici
 * @desc 帮辅老师选择的契约类
 */
public interface TutorChoiceContract {

    interface Presenter extends BaseContract.Presenter{
        void requestChoiceTutorData(@NonNull String memberId,
                                    @Nullable String courseId,
                                    @Nullable String chapterId,
                                    int pageIndex);
    }

    interface View extends BaseContract.View<Presenter>{
        void updateChoiceTutorView(List<TutorChoiceEntity> entities);
        void updateMoreChoiceTutorView(List<TutorChoiceEntity> entities);
    }

}
