package com.lqwawa.intleducation.module.discovery.ui.mycourse.tab;

import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.helper.MyCourseHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.discovery.ui.mycourse.TabCourseContract;
import com.lqwawa.intleducation.module.discovery.ui.person.mycourse.MyCourseListContract;
import com.lqwawa.intleducation.module.learn.vo.ChildrenListVo;

import java.util.List;

/**
 * @author medici
 * @desc 我的课程Type分类的Fragment的Presenter
 */
public class TabCourseTypePresenter extends BasePresenter<TabCourseTypeContract.View>
    implements TabCourseTypeContract.Presenter{

    public TabCourseTypePresenter(TabCourseTypeContract.View view) {
        super(view);
    }

    @Override
    public void start() {
        MyCourseHelper.requestParentChildData(new DataSource.Callback<List<ChildrenListVo>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final TabCourseTypeContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<ChildrenListVo> childrenListVos) {
                final TabCourseTypeContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.updateParentChildrenData(childrenListVos);
                }
            }
        });
    }
}
