package com.lqwawa.intleducation.module.discovery.ui.person.mycourse;

import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.helper.MyCourseHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.learn.vo.ChildrenListVo;

import java.util.List;

/**
 * @author medici
 * @desc 我的学程的Presenter
 */
public class MyCourseListPresenter extends BasePresenter<MyCourseListContract.View>
    implements MyCourseListContract.Presenter{

    public MyCourseListPresenter(MyCourseListContract.View view) {
        super(view);
    }

    @Override
    public void start() {
        super.start();
        MyCourseHelper.requestParentChildData(new DataSource.Callback<List<ChildrenListVo>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final MyCourseListContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<ChildrenListVo> childrenListVos) {
                final MyCourseListContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.updateParentChildrenData(childrenListVos);
                }
            }
        });
    }
}
