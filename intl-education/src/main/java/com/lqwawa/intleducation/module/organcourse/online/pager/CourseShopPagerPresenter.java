package com.lqwawa.intleducation.module.organcourse.online.pager;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.OnlineSchoolInfoEntity;
import com.lqwawa.intleducation.factory.helper.LQCourseHelper;
import com.lqwawa.intleducation.factory.helper.OnlineCourseHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.organcourse.online.CourseShopListContract;

import java.util.List;

/**
 * @desc 在线机构学程馆分页的Presenter
 * @author medici
 */
public class CourseShopPagerPresenter extends BasePresenter<CourseShopPagerContract.View>
        implements CourseShopPagerContract.Presenter{

    public CourseShopPagerPresenter(CourseShopPagerContract.View view) {
        super(view);
    }

    @Override
    public void requestCourseData(@Nullable String organId, int pageIndex, int pageSize, @NonNull String sort, int payType, String keyString) {
        LQCourseHelper.requestLQCourseData(organId,pageIndex, pageSize, "", sort,keyString,
                payType, 0, 0, 0, 1, new DataSource.Callback<List<CourseVo>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final CourseShopPagerContract.View view = (CourseShopPagerContract.View) getView();
                if(!EmptyUtil.isEmpty(view)) {
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<CourseVo> courseVos) {
                final CourseShopPagerContract.View view = (CourseShopPagerContract.View) getView();
                if(null != courseVos && !EmptyUtil.isEmpty(view)){
                    view.onCourseLoaded(courseVos);
                }
            }
        });
    }

    @Override
    public void requestMoreCourseData(@Nullable String organId,int pageIndex, int pageSize, @NonNull String sort, int payType, String keyString) {
        LQCourseHelper.requestLQCourseData(organId,pageIndex, pageSize, "", sort,keyString,
                payType, 0, 0, 0, 1, new DataSource.Callback<List<CourseVo>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final CourseShopPagerContract.View view = (CourseShopPagerContract.View) getView();
                if(!EmptyUtil.isEmpty(view)) {
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<CourseVo> courseVos) {
                final CourseShopPagerContract.View view = (CourseShopPagerContract.View) getView();
                if(null != courseVos && !EmptyUtil.isEmpty(view)){
                    view.onMoreCourseLoaded(courseVos);
                }
            }
        });
    }
}
