package com.lqwawa.intleducation.module.discovery.ui.lqcourse.classifylist;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.LanguageUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.helper.LQCourseHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;

import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 分类列表Presenter
 * @date 2018/05/02 11:14
 * @history v1.0
 * **********************************
 */
public class ClassifyListPresenter extends BasePresenter<ClassifyListContract.View>
    implements ClassifyListContract.Presenter{

    public ClassifyListPresenter(ClassifyListContract.View view) {
        super(view);
    }

    @Override
    public void requestClassifyData(@NonNull int parentId, @NonNull int level) {
        int isZh = LanguageUtil.isZh();
        LQCourseHelper.requestLQCourseConfigData(isZh, level, parentId, new DataSource.Callback<List<LQCourseConfigEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                ClassifyListContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<LQCourseConfigEntity> entities) {
                ClassifyListContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view) && null != entities){
                    view.updateClassifyView(entities);
                }
            }
        });
    }
}
