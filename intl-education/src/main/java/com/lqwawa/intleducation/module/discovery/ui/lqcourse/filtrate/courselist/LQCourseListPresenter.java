package com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.courselist;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.OnlineClassEntity;
import com.lqwawa.intleducation.factory.data.entity.OnlineSchoolInfoEntity;
import com.lqwawa.intleducation.factory.helper.LQCourseHelper;
import com.lqwawa.intleducation.factory.helper.OnlineCourseHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.CourseFiltrateContract;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.onclass.school.SchoolInfoContract;

import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 课程列表片段的Presenter
 * @date 2018/05/03 13:40
 * @history v1.0
 * **********************************
 */
public class LQCourseListPresenter extends BasePresenter<LQCourseListContract.View>
    implements LQCourseListContract.Presenter{

    public LQCourseListPresenter(LQCourseListContract.View view) {
        super(view);
    }

    @Override
    public void requestCourseData(@Nullable String organId,int pageIndex, int pageSize, int dataType,@NonNull String sort, int payType, String keyString) {
        LQCourseHelper.requestLQHotCourseData(dataType, pageIndex, pageSize, keyString, new DataSource.Callback<List<CourseVo>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final LQCourseListContract.View view = (LQCourseListContract.View) getView();
                if(!EmptyUtil.isEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<CourseVo> courseVos) {
                final LQCourseListContract.View view = (LQCourseListContract.View) getView();
                if(null != courseVos && !EmptyUtil.isEmpty(view)){
                    view.onCourseLoaded(courseVos);
                }
            }
        });

        /*LQCourseHelper.requestLQCourseData(organId,pageIndex, pageSize, "", sort,keyString, payType, 0, 0, 0, new DataSource.Callback<List<CourseVo>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final LQCourseListContract.View view = (LQCourseListContract.View) getView();
                if(!EmptyUtil.isEmpty(view)) {
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<CourseVo> courseVos) {
                final LQCourseListContract.View view = (LQCourseListContract.View) getView();
                if(null != courseVos && !EmptyUtil.isEmpty(view)){
                    view.onCourseLoaded(courseVos);
                }
            }
        });*/
    }

    @Override
    public void requestMoreCourseData(@Nullable String organId,int pageIndex, int pageSize,int dataType, @NonNull String sort, int payType, String keyString) {
        LQCourseHelper.requestLQHotCourseData(dataType, pageIndex, pageSize, keyString, new DataSource.Callback<List<CourseVo>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final LQCourseListContract.View view = (LQCourseListContract.View) getView();
                if(!EmptyUtil.isEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<CourseVo> courseVos) {
                final LQCourseListContract.View view = (LQCourseListContract.View) getView();
                if(null != courseVos && !EmptyUtil.isEmpty(view)){
                    view.onMoreCourseLoaded(courseVos);
                }
            }
        });

        /*LQCourseHelper.requestLQCourseData(organId,pageIndex, pageSize, "", sort,keyString, payType, 0, 0, 0, new DataSource.Callback<List<CourseVo>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final LQCourseListContract.View view = (LQCourseListContract.View) getView();
                if(!EmptyUtil.isEmpty(view)) {
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<CourseVo> courseVos) {
                final LQCourseListContract.View view = (LQCourseListContract.View) getView();
                if(null != courseVos && !EmptyUtil.isEmpty(view)){
                    view.onMoreCourseLoaded(courseVos);
                }
            }
        });*/
    }

    @Override
    public void requestOnlineSchoolInfoData(int pageIndex,@NonNull String schoolId) {
        OnlineCourseHelper.requestOnlineSchoolInfoData(schoolId,pageIndex,AppConfig.PAGE_SIZE, new DataSource.Callback<OnlineSchoolInfoEntity>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final LQCourseListContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(OnlineSchoolInfoEntity onlineSchoolInfoEntity) {
                final LQCourseListContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    List<CourseVo> listCourse = onlineSchoolInfoEntity.getCourseList();
                    // 进行UI回调
                    view.updateOnlineSchoolCourseView(listCourse);
                }
            }
        });
    }
}
