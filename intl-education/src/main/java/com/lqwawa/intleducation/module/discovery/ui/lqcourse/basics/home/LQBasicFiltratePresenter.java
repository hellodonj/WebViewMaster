package com.lqwawa.intleducation.module.discovery.ui.lqcourse.basics.home;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.Utils;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.helper.LQCourseHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.LanguageType;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;

import java.util.List;

/**
 * @author mrmedici
 * @desc 国家课程二级列表筛选的Presenter
 */
public class LQBasicFiltratePresenter extends BasePresenter<LQBasicFiltrateContract.View>
    implements LQBasicFiltrateContract.Presenter{

    public LQBasicFiltratePresenter(LQBasicFiltrateContract.View view) {
        super(view);
    }

    @Override
    public void requestBasicConfigData(int parentId, int dataType) {
        // 获取中英文数据
        int languageRes = Utils.isZh(UIUtil.getContext()) ? LanguageType.LANGUAGE_CHINESE : LanguageType.LANGUAGE_OTHER;
        LQCourseHelper.requestLQBasicCourseConfigData(dataType, languageRes, parentId, new DataSource.Callback<List<LQCourseConfigEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final LQBasicFiltrateContract.View view = (LQBasicFiltrateContract.View) getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<LQCourseConfigEntity> entities) {
                final LQBasicFiltrateContract.View view = (LQBasicFiltrateContract.View) getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.updateBasicConfigView(entities);
                }
            }
        });
    }

    @Override
    public void requestCourseData(boolean more, int pageIndex, int pageSize, @NonNull String level, String keyString, int paramOneId, int paramTwoId, int paramThreeId) {
        // 加载课程
        LQCourseHelper.requestLQCourseData(null,pageIndex, pageSize, level,"0",keyString,Integer.MAX_VALUE, paramOneId, paramTwoId, paramThreeId, new DataSource.Callback<List<CourseVo>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                UIUtil.showToastSafe(strRes);
            }

            @Override
            public void onDataLoaded(List<CourseVo> courseVos) {
                final LQBasicFiltrateContract.View view = (LQBasicFiltrateContract.View) getView();
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
