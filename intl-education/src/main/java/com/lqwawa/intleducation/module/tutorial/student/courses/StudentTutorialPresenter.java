package com.lqwawa.intleducation.module.tutorial.student.courses;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.tutorial.TutorEntity;
import com.lqwawa.intleducation.factory.helper.TutorialHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;

import java.util.List;

/**
 * @authr mrmedici
 * @desc 学生帮辅列表的Presenter
 */
public class StudentTutorialPresenter extends BasePresenter<StudentTutorialContract.View>
    implements StudentTutorialContract.Presenter{

    public StudentTutorialPresenter(StudentTutorialContract.View view) {
        super(view);
    }

    @Override
    public void requestStudentTutorialData(@NonNull String memberId, @NonNull String tutorName, int pageIndex) {
        TutorialHelper.requestMyTutorData(memberId, tutorName, pageIndex, AppConfig.PAGE_SIZE, new DataSource.Callback<List<TutorEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final StudentTutorialContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<TutorEntity> entities) {
                final StudentTutorialContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    if(pageIndex == 0){
                        view.updateStudentTutorialView(entities);
                    }else{
                        view.updateMoreStudentTutorialView(entities);
                    }
                }
            }
        });
    }
}
