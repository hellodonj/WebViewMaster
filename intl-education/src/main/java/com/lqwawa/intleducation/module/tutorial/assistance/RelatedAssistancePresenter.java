package com.lqwawa.intleducation.module.tutorial.assistance;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.helper.SchoolHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;

import java.util.List;

/**
 * 描述: 三习教案关联教辅的Presenter
 * 作者|时间: djj on 2019/7/19 0019 下午 2:00
 */
public class RelatedAssistancePresenter extends BasePresenter<RelatedAssistanceContract.View>
    implements RelatedAssistanceContract.Presenter{

    public RelatedAssistancePresenter(RelatedAssistanceContract.View view) {
        super(view);
    }

    @Override
    public void requestSxRelationCourse(@NonNull String courseId, int pageIndex) {
        SchoolHelper.requestSxRelationCourseList(courseId, pageIndex, AppConfig.PAGE_SIZE,
                new DataSource.Callback<List<CourseVo>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final RelatedAssistanceContract.View view = getView();
                if(!EmptyUtil.isEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<CourseVo> entity) {
                final RelatedAssistanceContract.View view = getView();
                if(!EmptyUtil.isEmpty(view) && !EmptyUtil.isEmpty(entity)){
                    if(pageIndex == 0){
                        view.updateSxRelationCourseView(entity);
                    }else{
                        view.updateMoreSxRelationCourseView(entity);
                    }
                }
            }
        });
    }
}
