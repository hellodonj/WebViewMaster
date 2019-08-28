package com.lqwawa.intleducation.module.discovery.ui.classcourse.addHistory;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.course.ClassCourseEntity;
import com.lqwawa.intleducation.factory.helper.ClassCourseHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;

import java.util.List;

/**
 * @author mrmedici
 * @desc 班级学程页面的Presenter
 */
public class AddHistoryCoursePresenter extends BasePresenter<AddHistoryCourseContract.View>
    implements AddHistoryCourseContract.Presenter{

    public AddHistoryCoursePresenter(AddHistoryCourseContract.View view) {
        super(view);
    }

    @Override
    public void requestClassCourseData(@NonNull String classId,
                                       int role,@NonNull String name,
                                       @NonNull String level,
                                       int paramOneId,int paramTwoId,
                                       final int pageIndex) {

        //默认-1 类型传全部
        ClassCourseHelper.requestClassCourseData(classId, 0, role, name, level, paramOneId, paramTwoId, pageIndex, Integer.MAX_VALUE, -1,
                new DataSource.Callback<List<ClassCourseEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final AddHistoryCourseContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<ClassCourseEntity> classCourseEntities) {
                final AddHistoryCourseContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    if(pageIndex == 0){
                        // 下拉刷新
                        view.updateClassCourseView(classCourseEntities);
                    }else{
                        // 加载更多
                        view.updateMoreClassCourseView(classCourseEntities);
                    }
                }
            }
        });
    }
}
