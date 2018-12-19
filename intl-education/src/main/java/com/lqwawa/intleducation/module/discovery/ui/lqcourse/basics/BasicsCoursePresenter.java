package com.lqwawa.intleducation.module.discovery.ui.lqcourse.basics;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.LanguageUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.LQBasicsOuterEntity;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.helper.LQCourseHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;

import java.util.List;

/**
 * @author mrmedici
 * @desc 基础课程列表的Presenter
 */
public class BasicsCoursePresenter extends BasePresenter<BasicsCourseContract.View>
    implements BasicsCourseContract.Presenter{

    public BasicsCoursePresenter(BasicsCourseContract.View view) {
        super(view);
    }

    @Override
    public void requestConfigWithBasicsEntity(@NonNull LQBasicsOuterEntity.LQBasicsInnerEntity entity) {
        int isZh = LanguageUtil.isZh();
        int parentId = entity.getId();
        // 获取二级标签
        int _level = 2;
        String level = entity.getLevel();
        if(level.contains(".")){
            // 获取二级标签的parentId，获取二级标签列表，然后model跟parentId比较
            String[] levelStrings = level.split("[\\.]");
            if(EmptyUtil.isNotEmpty(levelStrings) && levelStrings.length == 2){
                int _parentId = Integer.parseInt(levelStrings[0]);

                LQCourseHelper.requestLQCourseConfigData(isZh, _level, _parentId, new DataSource.Callback<List<LQCourseConfigEntity>>() {
                    @Override
                    public void onDataNotAvailable(int strRes) {
                        UIUtil.showToastSafe(strRes);
                    }

                    @Override
                    public void onDataLoaded(List<LQCourseConfigEntity> entities) {
                        final BasicsCourseContract.View view = getView();
                        if(EmptyUtil.isNotEmpty(view)){
                            view.updateConfigView(parentId,entity,entities);
                        }
                    }
                });
            }
        }
    }
}
