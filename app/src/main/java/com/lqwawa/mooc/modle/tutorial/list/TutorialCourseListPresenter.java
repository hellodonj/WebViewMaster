package com.lqwawa.mooc.modle.tutorial.list;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.helper.TutorialHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.tutorial.teacher.courses.record.AuditType;

import java.util.List;

/**
 * @author mrmeidici
 * @desc 帮辅主页帮辅课程的Presenter
 */
public class TutorialCourseListPresenter extends BasePresenter<TutorialCourseListContract.View>
    implements TutorialCourseListContract.Presenter{

    public TutorialCourseListPresenter(TutorialCourseListContract.View view) {
        super(view);
    }

    @Override
    public void requestTutorialCourseData(@NonNull String memberId, @Nullable String name, int type,int pageIndex) {
        TutorialHelper.requestTutorialCourses(memberId, name, AuditType.AUDITED_PASS,type, pageIndex, AppConfig.PAGE_SIZE, new DataSource.Callback<List<CourseVo>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final TutorialCourseListContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<CourseVo> courseVos) {
                final TutorialCourseListContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    if(pageIndex == 0){
                        view.updateTutorialCourseView(courseVos);
                    }else{
                        view.updateMoreTutorialCourseView(courseVos);
                    }
                }
            }
        });
    }


}
