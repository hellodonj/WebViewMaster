package com.lqwawa.intleducation.module.tutorial.teacher.students;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.tutorial.AssistStudentEntity;
import com.lqwawa.intleducation.factory.data.entity.tutorial.TutorEntity;
import com.lqwawa.intleducation.factory.helper.TutorialHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.tutorial.student.courses.StudentTutorialContract;

import java.util.List;

/**
 * @authr mrmedici
 * @desc 帮辅的学生列表的Presenter
 */
public class TutorialStudentPresenter extends BasePresenter<TutorialStudentsContract.View>
    implements TutorialStudentsContract.Presenter{

    public TutorialStudentPresenter(TutorialStudentsContract.View view) {
        super(view);
    }

    @Override
    public void requestTutorialStudentData(@NonNull String memberId, @NonNull String name, int pageIndex) {
        TutorialHelper.requestPullTutorialStudents(memberId, name, pageIndex, AppConfig.PAGE_SIZE, new DataSource.Callback<List<AssistStudentEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final TutorialStudentsContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<AssistStudentEntity> entities) {
                final TutorialStudentsContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    if(pageIndex == 0){
                        view.updateTutorialStudentView(entities);
                    }else{
                        view.updateMoreTutorialStudentView(entities);
                    }
                }
            }
        });
    }
}
