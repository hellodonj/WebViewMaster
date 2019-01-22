package com.lqwawa.intleducation.module.discovery.ui.lqbasic;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.LanguageUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.Utils;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.LQBasicsOuterEntity;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.data.entity.OnlineClassEntity;
import com.lqwawa.intleducation.factory.data.entity.response.LQConfigResponseVo;
import com.lqwawa.intleducation.factory.data.entity.response.LQRmResponseVo;
import com.lqwawa.intleducation.factory.helper.LQCourseHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.LanguageType;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mrmedici
 * @desc 国家课程的Presenter
 */
public class LQBasicPresenter extends BasePresenter<LQBasicContract.View>
    implements LQBasicContract.Presenter{

    public LQBasicPresenter(LQBasicContract.View view) {
        super(view);
    }


    @Override
    public void requestConfigData() {
        // 获取中英文数据
        int languageRes = Utils.isZh(UIUtil.getContext()) ? LanguageType.LANGUAGE_CHINESE : LanguageType.LANGUAGE_OTHER;
        // 获取分类数据 英语国际课程,英语国内课程 等 获取第一级别
        LQCourseHelper.requestLQHomeConfigData(languageRes, 1, 0, new DataSource.Callback<LQConfigResponseVo<List<LQCourseConfigEntity>,List<LQBasicsOuterEntity>>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                // 重要的数据发生异常了，才弹提示
                UIUtil.showToastSafe(strRes);
            }

            @Override
            public void onDataLoaded(LQConfigResponseVo<List<LQCourseConfigEntity>,List<LQBasicsOuterEntity>> responseVo) {
                final LQBasicContract.View view = (LQBasicContract.View) getView();
                List<LQCourseConfigEntity> data = responseVo.getData();
                if(!EmptyUtil.isEmpty(data) && !EmptyUtil.isEmpty(view)){
                    view.updateConfigViews(data);
                }

                List<LQBasicsOuterEntity> basicConfig = responseVo.getBasicConfig();
                if(!EmptyUtil.isEmpty(basicConfig) && !EmptyUtil.isEmpty(view)){
                    view.updateNewBasicsConfigView(basicConfig);
                }
            }
        });
    }

    @Override
    public void requestNewBasicConfig() {
        // 获取中英文数据
        int languageRes = Utils.isZh(UIUtil.getContext()) ? LanguageType.LANGUAGE_CHINESE : LanguageType.LANGUAGE_OTHER;
        // 获取分类数据 英语国际课程,英语国内课程 等 获取第一级别
        LQCourseHelper.requestLQBasicsConfigData(languageRes, new DataSource.Callback<List<LQBasicsOuterEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                // 重要的数据发生异常了，才弹提示
                final LQBasicContract.View view = (LQBasicContract.View) getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<LQBasicsOuterEntity> entities) {
                final LQBasicContract.View view = (LQBasicContract.View) getView();
                if(!EmptyUtil.isEmpty(entities) && !EmptyUtil.isEmpty(view)){
                    view.updateNewBasicsConfigView(entities);
                }
            }
        });
    }

    @Override
    public void requestLQRmCourseData() {
        // 1 代表只或许国家课程的信息
        LQCourseHelper.requestLQRmCourseData(1, new DataSource.Callback<LQRmResponseVo>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final LQBasicContract.View view = (LQBasicContract.View) getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(LQRmResponseVo lqRmResponseVo) {
                final LQBasicContract.View view = (LQBasicContract.View) getView();
                if(EmptyUtil.isNotEmpty(view)){
                    // 热门课程数据
                    List<CourseVo> rmCourseList = lqRmResponseVo.getRmCourseList();
                    if(rmCourseList != null) {
                        if(rmCourseList.size() > 3){
                            // 只显示3个热门课程数据,包头不包尾
                            rmCourseList = new ArrayList<>(rmCourseList.subList(0, 3));
                        }


                        view.updateLQRmCourseData(rmCourseList);
                    }


                    // 名师课数据
                    List<OnlineClassEntity> reOnlineCourseList = lqRmResponseVo.getGjOnlineCourseList();
                    if(reOnlineCourseList != null) {
                        if(reOnlineCourseList.size() > 2){
                            // 只显示3个名师课数据,包头不包尾
                            reOnlineCourseList = new ArrayList<>(reOnlineCourseList.subList(0, 2));
                        }


                        view.updateLQRmOnlineCourseData(reOnlineCourseList);
                    }
                }
            }
        });
    }

    @Override
    public void requestConfigWithBasicsEntity(@NonNull final LQBasicsOuterEntity.LQBasicsInnerEntity entity) {
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
                        final LQBasicContract.View view = getView();
                        if(EmptyUtil.isNotEmpty(view)){
                            view.updateConfigView(parentId,entity,entities);
                        }
                    }
                });
            }
        }
    }
}
