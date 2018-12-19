package com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.pager;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.helper.LQCourseHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.CourseFiltrateContract;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.HideSortType;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;

import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 筛选页面的布局片段的Presenter 用来获取筛选的分类课程
 * @date 2018/07/12 11:53
 * @history v1.0
 * **********************************
 */
public class CourseFiltratePagerPresenter extends BasePresenter<CourseFiltratePagerContract.View>
    implements CourseFiltratePagerContract.Presenter{

    public CourseFiltratePagerPresenter(CourseFiltratePagerContract.View view) {
        super(view);
    }

    @Override
    public void requestCourseData(int pageIndex, int pageSize, @NonNull String level, String keyString, int paramOneId, int paramTwoId, int paramThreeId) {
        // 最近更新 由后台设定顺序
        // String sort = HideSortType.TYPE_SORT_ONLINE_SHOP_RECENT_UPDATE;
        LQCourseHelper.requestLQCourseData(null,pageIndex, pageSize, level,"0",keyString,Integer.MAX_VALUE, paramOneId, paramTwoId, paramThreeId, new DataSource.Callback<List<CourseVo>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                UIUtil.showToastSafe(strRes);
            }

            @Override
            public void onDataLoaded(List<CourseVo> courseVos) {
                final CourseFiltratePagerContract.View view = (CourseFiltratePagerContract.View) getView();
                if(null != courseVos && !EmptyUtil.isEmpty(view)){
                    view.onCourseLoaded(courseVos);
                }
            }
        });
    }


    @Override
    public void requestMoreCourseData(int pageIndex, int pageSize, @NonNull String level,String keyString, int paramOneId, int paramTwoId, int paramThreeId) {
        // 最近更新 由后台设定顺序
        // String sort = HideSortType.TYPE_SORT_ONLINE_SHOP_RECENT_UPDATE;
        LQCourseHelper.requestLQCourseData(null,pageIndex, pageSize, level,"0",keyString,Integer.MAX_VALUE, paramOneId, paramTwoId, paramThreeId, new DataSource.Callback<List<CourseVo>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                UIUtil.showToastSafe(strRes);
            }

            @Override
            public void onDataLoaded(List<CourseVo> courseVos) {
                final CourseFiltratePagerContract.View view = (CourseFiltratePagerContract.View) getView();
                if(null != courseVos && !EmptyUtil.isEmpty(view)){
                    view.onMoreCourseLoaded(courseVos);
                }
            }
        });
    }

}
