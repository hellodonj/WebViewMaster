package com.lqwawa.intleducation.module.watchcourse;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.helper.CourseHelper;
import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.discovery.vo.CourseDetailsVo;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function
 * @date 2018/06/26 11:16
 * @history v1.0
 * **********************************
 */
public class WatchCourseResourcePresenter extends BasePresenter<WatchCourseResourceContract.View>
    implements WatchCourseResourceContract.Presenter{

    public WatchCourseResourcePresenter(WatchCourseResourceContract.View view) {
        super(view);
    }

    @Override
    public void getCourseDetailWithCourseId(@NonNull String courseId) {
        String token = UserHelper.getUserId();
        CourseHelper.getCourseDetailsData(token, 1,courseId, null, new DataSource.Callback<CourseVo>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                UIUtil.showToastSafe(strRes);
            }

            @Override
            public void onDataLoaded(CourseVo courseVo) {
                final WatchCourseResourceContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view) && EmptyUtil.isNotEmpty(courseVo)){
                    view.updateLoadedCourseDetailView(courseVo);
                }
            }
        });
    }
}
