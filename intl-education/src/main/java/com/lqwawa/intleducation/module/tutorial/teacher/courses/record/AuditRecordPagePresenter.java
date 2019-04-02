package com.lqwawa.intleducation.module.tutorial.teacher.courses.record;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.helper.TutorialHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.tutorial.teacher.courses.TutorialCoursesContract;

import java.util.List;

/**
 * @authr mrmedici
 * @desc 帮辅审核记录的Presenter
 */
public class AuditRecordPagePresenter extends BasePresenter<AuditRecordPageContract.View>
    implements AuditRecordPageContract.Presenter{

    public AuditRecordPagePresenter(AuditRecordPageContract.View view) {
        super(view);
    }

    @Override
    public void requestTutorialCoursesData(@NonNull String memberId, @Nullable String name, @NonNull int type, int pageIndex) {
        TutorialHelper.requestTutorialCourses(memberId, name, type,1, pageIndex, AppConfig.PAGE_SIZE, new DataSource.Callback<List<CourseVo>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final AuditRecordPageContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<CourseVo> courseVos) {
                final AuditRecordPageContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    if(pageIndex == 0){
                        view.updateTutorialCoursesView(courseVos);
                    }else{
                        view.updateMoreTutorialCoursesView(courseVos);
                    }
                }
            }
        });
    }
}
