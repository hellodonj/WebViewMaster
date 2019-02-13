package com.lqwawa.intleducation.module.discovery.ui.lqcourse.basics.home.pager;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.Utils;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.helper.LQCourseHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.basics.home.LQBasicFiltrateContract;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.LanguageType;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;

import java.util.List;

/**
 * @author mrmedici
 * @desc 国家课程二级列表筛选片段的Presenter
 */
public class LQBasicFiltratePagerPresenter extends BasePresenter<LQBasicFiltratePagerContract.View>
        implements LQBasicFiltratePagerContract.Presenter{

    public LQBasicFiltratePagerPresenter(LQBasicFiltratePagerContract.View view) {
        super(view);
    }

    @Override
    public void requestCourseData(boolean more, int pageIndex, int pageSize, @NonNull String level,@NonNull String sort, String keyString, int paramOneId, int paramTwoId, int paramThreeId) {
        // 加载课程
        LQCourseHelper.requestLQCourseData(null,pageIndex, pageSize, level, sort,keyString,Integer.MAX_VALUE, paramOneId, paramTwoId, paramThreeId, new DataSource.Callback<List<CourseVo>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                UIUtil.showToastSafe(strRes);
            }

            @Override
            public void onDataLoaded(List<CourseVo> courseVos) {
                final LQBasicFiltratePagerContract.View view = (LQBasicFiltratePagerContract.View) getView();
                if(null != courseVos && !EmptyUtil.isEmpty(view)){
                    if(more){
                        view.updateMoreCourseView(courseVos);
                    }else{
                        view.updateCourseView(courseVos);
                    }
                }
            }
        });
    }
}