package com.lqwawa.intleducation.module.discovery.ui.empty;

import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.Utils;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.helper.LQCourseHelper;
import com.lqwawa.intleducation.factory.presenter.BaseRecyclerPresenter;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.LQCourseContract;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.LanguageType;

import java.util.ArrayList;
import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 空布局Presenter
 * @date 2018/05/16 11:24
 * @history v1.0
 * **********************************
 */
public class EmptyPresenter extends BaseRecyclerPresenter<LQCourseConfigEntity,EmptyContract.View>
    implements EmptyContract.Presenter{

    private static final int LOAD_MORE_COUNT = 3;

    private int loadMoreCount;

    public EmptyPresenter(EmptyContract.View view) {
        super(view);
    }

    @Override
    public void getData(int pageIndex,int pageSize) {
        start();
        // 获取分类数据 英语国际课程,英语国内课程 等 获取第一级别
        int languageRes = Utils.isZh(UIUtil.getContext()) ? LanguageType.LANGUAGE_CHINESE : LanguageType.LANGUAGE_OTHER;
        LQCourseHelper.requestLQCourseConfigData(languageRes, 1, 0, new DataSource.Callback<List<LQCourseConfigEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final EmptyContract.View view = (EmptyContract.View) getView();
                if(!EmptyUtil.isEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<LQCourseConfigEntity> lqCourseConfigEntities) {
                final EmptyContract.View view = (EmptyContract.View) getView();
                if(!EmptyUtil.isEmpty(lqCourseConfigEntities) && !EmptyUtil.isEmpty(view)){
                    List<LQCourseConfigEntity> childList = lqCourseConfigEntities.get(0).getChildList();
                    childList.addAll(childList);
                    if(pageIndex == 0){
                        refreshData(childList);
                    }else{
                        loadMoreCount++;
                        if(loadMoreCount >= LOAD_MORE_COUNT){
                            refreshMoreData(new ArrayList<>());
                        }else{
                            refreshMoreData(childList);
                        }
                    }
                }
            }
        });
    }
}
