package com.lqwawa.intleducation.module.onclass.related;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.LanguageUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.ClassDetailEntity;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.helper.LQCourseHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.CourseFiltrateActivity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.state.GroupFiltrateState;

import java.util.List;

/**
 * @author mrmedici
 * @desc 相关课程列表的Presenter
 */
public class RelatedCourseListPresenter extends BasePresenter<RelatedCourseListContract.View>
    implements RelatedCourseListContract.Presenter{

    public RelatedCourseListPresenter(RelatedCourseListContract.View view) {
        super(view);
    }

    @Override
    public void requestConfigWithParam(@NonNull ClassDetailEntity.ParamBean param) {
        int isZh = LanguageUtil.isZh();
        String parentId = param.getParentId();
        // 获取二级标签
        int _level = 2;
        String level = param.getLevel();
        if(level.contains(".")){
            // 获取二级标签的parentId，获取二级标签列表，然后model跟parentId比较
            String[] levelStrings = level.split("[\\.]");
            if(EmptyUtil.isNotEmpty(levelStrings) && levelStrings.length == 2){
                int _parentId = Integer.parseInt(levelStrings[0]);
                final int resultParentId = Integer.parseInt(parentId);

                LQCourseHelper.requestLQCourseConfigData(isZh, _level, _parentId, new DataSource.Callback<List<LQCourseConfigEntity>>() {
                    @Override
                    public void onDataNotAvailable(int strRes) {
                        UIUtil.showToastSafe(strRes);
                    }

                    @Override
                    public void onDataLoaded(List<LQCourseConfigEntity> entities) {
                        final RelatedCourseListContract.View view = getView();
                        if(EmptyUtil.isNotEmpty(view)){
                            view.updateConfigView(resultParentId,param,entities);
                        }
                    }
                });
            }
        }
    }
}
