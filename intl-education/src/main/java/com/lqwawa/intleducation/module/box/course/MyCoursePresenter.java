package com.lqwawa.intleducation.module.box.course;

import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.helper.MyCourseHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.box.tutorial.TutorialSpaceContract;
import com.lqwawa.intleducation.module.discovery.ui.mycourse.tab.TabCourseTypeContract;
import com.lqwawa.intleducation.module.learn.vo.ChildrenListVo;

import java.util.List;

/**
 * @author medici
 * @desc 我的课程的Presenter
 */
public class MyCoursePresenter extends BasePresenter<MyCourseContract.View>
    implements MyCourseContract.Presenter{

    public MyCoursePresenter(MyCourseContract.View view) {
        super(view);
    }

    @Override
    public void requestParentChildData() {
        MyCourseHelper.requestParentChildData(new DataSource.Callback<List<ChildrenListVo>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final MyCourseContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<ChildrenListVo> childrenListVos) {
                final MyCourseContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.updateParentChildrenData(childrenListVos);
                }
            }
        });
    }
}
