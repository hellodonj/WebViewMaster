package com.lqwawa.intleducation.module.discovery.ui.lqcourse.home;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.LanguageUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.Utils;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.ClassDetailEntity;
import com.lqwawa.intleducation.factory.data.entity.LQBasicsOuterEntity;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.data.entity.OnlineClassEntity;
import com.lqwawa.intleducation.factory.data.entity.response.LQConfigResponseVo;
import com.lqwawa.intleducation.factory.data.entity.response.LQRmResponseVo;
import com.lqwawa.intleducation.factory.helper.LQCourseHelper;
import com.lqwawa.intleducation.factory.helper.LiveHelper;
import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.discovery.vo.ClassifyVo;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.discovery.vo.DiscoveryItemVo;
import com.lqwawa.intleducation.module.discovery.vo.OrganVo;
import com.lqwawa.intleducation.module.learn.vo.LiveVo;
import com.lqwawa.intleducation.module.onclass.related.RelatedCourseListContract;

import java.util.ArrayList;
import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function LQ学程的Presenter
 * @date 2018/04/27 15:00
 * @history v1.0
 * **********************************
 */
public class LQCoursePresenter extends BasePresenter<LQCourseContract.View>
    implements LQCourseContract.Presenter{

    /**
     * 分类数据特殊编号
     */
    private static final String KEY_CLASSIFY_SPECIAL_DATA_ID = "93";

    public LQCoursePresenter(LQCourseContract.View view) {
        super(view);
    }

    @Override
    public void start() {
        super.start();
        // 获取LQ学程轮播图信息
        LQCourseHelper.requestLQCourseBanners(new DataSource.Callback<List<String>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                // UIUtil.showToastSafe(strRes);
            }

            @Override
            public void onDataLoaded(List<String> strings) {
                final LQCourseContract.View view = (LQCourseContract.View) getView();
                if(!EmptyUtil.isEmpty(view)){
                    view.updateBannerViews(strings);
                }
            }
        });


        // 获取中英文数据
        int languageRes = Utils.isZh(UIUtil.getContext()) ? LanguageType.LANGUAGE_CHINESE : LanguageType.LANGUAGE_OTHER;
        // 获取分类数据 英语国际课程,英语国内课程 等 获取第一级别
        /*LQCourseHelper.requestLQCourseConfigData(languageRes, 1, 0, new DataSource.Callback<List<LQCourseConfigEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                // 重要的数据发生异常了，才弹提示
                UIUtil.showToastSafe(strRes);
            }

            @Override
            public void onDataLoaded(List<LQCourseConfigEntity> lqCourseConfigEntities) {
                final LQCourseContract.View view = (LQCourseContract.View) getView();
                if(!EmptyUtil.isEmpty(lqCourseConfigEntities) && !EmptyUtil.isEmpty(view)){
                    view.updateConfigViews(lqCourseConfigEntities);
                }
            }
        });*/

        LQCourseHelper.requestLQHomeConfigData(languageRes, 1, 0, new DataSource.Callback<LQConfigResponseVo<List<LQCourseConfigEntity>,List<LQBasicsOuterEntity>>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                // 重要的数据发生异常了，才弹提示
                UIUtil.showToastSafe(strRes);
            }

            @Override
            public void onDataLoaded(LQConfigResponseVo<List<LQCourseConfigEntity>,List<LQBasicsOuterEntity>> responseVo) {
                final LQCourseContract.View view = (LQCourseContract.View) getView();
                List<LQCourseConfigEntity> data = responseVo.getData();
                if(!EmptyUtil.isEmpty(data) && !EmptyUtil.isEmpty(view)){
                    view.updateConfigViews(data);
                }

                List<LQBasicsOuterEntity> basicConfig = responseVo.getBasicConfig();
                if(!EmptyUtil.isEmpty(basicConfig) && !EmptyUtil.isEmpty(view)){
                    // view.updateNewBasicsConfigView(basicConfig);
                }
            }
        });


        // 添加直播
        // @date   :2018/6/7 0007 下午 6:27
        // @func   :V5.7 Mooc 取消直播显示
        /**
        LiveHelper.requestLiveData(0,10,new DataSource.Callback<List<LiveVo>>(){
            @Override
            public void onDataLoaded(List<LiveVo> liveVos) {
                final LQCourseContract.View view = (LQCourseContract.View) getView();
                // 即使只有一条数据，也显示在首页中
                if(liveVos != null && liveVos.size() > 1) {
                    // 只显示2个直播数据,包头不包尾
                    liveVos = liveVos.subList(0, 2);
                    if(!EmptyUtil.isEmpty(liveVos) && !EmptyUtil.isEmpty(view)){
                        view.updateLiveView(liveVos);
                    }
                }else if(liveVos != null){
                    if(!EmptyUtil.isEmpty(liveVos) && !EmptyUtil.isEmpty(view)){
                        view.updateLiveView(liveVos);
                    }
                }
            }

            @Override
            public void onDataNotAvailable(int strRes) {
                // UIUtil.showToastSafe(strRes);
            }
        });*/

        // 添加热门推荐/最近更新/入驻机构
        /*LQCourseHelper.requestDiscoveryData(true, new DataSource.Callback<DiscoveryItemVo>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                // UIUtil.showToastSafe(strRes);
            }

            @Override
            public void onDataLoaded(DiscoveryItemVo result) {
                final LQCourseContract.View view = (LQCourseContract.View) getView();
                // 热门课程数据
                List<CourseVo> hotCourseList = result.getRmCourseList();
                if(hotCourseList != null && hotCourseList.size() > 2) {
                    // 只显示3个热门课程数据,包头不包尾
                    hotCourseList = hotCourseList.subList(0, 3);
                    if(!EmptyUtil.isEmpty(hotCourseList) && !EmptyUtil.isEmpty(view)){
                        view.updateHotCourseView(hotCourseList);
                    }
                }else if(hotCourseList != null){
                    // 如果不足3个课程,也显示热门课程
                    if(!EmptyUtil.isEmpty(hotCourseList) && !EmptyUtil.isEmpty(view)){
                        view.updateHotCourseView(hotCourseList);
                    }
                }

                // 最近更新课程数据
                List<CourseVo> latestCourseList = result.getZjCourseList();
                if(latestCourseList != null && latestCourseList.size() > 2){
                    latestCourseList = latestCourseList.subList(0,3);
                }
                // 组织机构数据
                List<OrganVo> organList = result.getOrganList();
            }
        });*/


        LQCourseHelper.requestLQRmCourseData(0, new DataSource.Callback<LQRmResponseVo>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final LQCourseContract.View view = (LQCourseContract.View) getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(LQRmResponseVo lqRmResponseVo) {
                final LQCourseContract.View view = (LQCourseContract.View) getView();
                if(EmptyUtil.isNotEmpty(view)){
                    // 热门课程数据
                    List<CourseVo> rmCourseList = lqRmResponseVo.getRmCourseList();
                    if(rmCourseList != null) {
                        if(rmCourseList.size() > 3){
                            // 只显示3个热门课程数据,包头不包尾
                            rmCourseList = new ArrayList<>(rmCourseList.subList(0, 3));
                        }


                        view.updateHotCourseView(rmCourseList);
                    }


                    // 小语种名师课数据
                    List<OnlineClassEntity> xyzOnlineCourseList = lqRmResponseVo.getXyzOnlineCourseList();
                    if(xyzOnlineCourseList != null) {
                        if(xyzOnlineCourseList.size() > 2){
                            // 只显示3个名师课数据,包头不包尾
                            xyzOnlineCourseList = new ArrayList<>(xyzOnlineCourseList.subList(0, 2));
                        }


                        view.updateXyzOnlineClassView(xyzOnlineCourseList);
                    }

                    // 国际名师课数据
                    List<OnlineClassEntity> reOnlineCourseList = lqRmResponseVo.getGjOnlineCourseList();
                    if(reOnlineCourseList != null) {
                        if(reOnlineCourseList.size() > 2){
                            // 只显示3个名师课数据,包头不包尾
                            reOnlineCourseList = new ArrayList<>(reOnlineCourseList.subList(0, 2));
                        }


                        view.updateInternationalOnlineClassView(reOnlineCourseList);
                    }
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
                UIUtil.showToastSafe(strRes);
            }

            @Override
            public void onDataLoaded(List<LQBasicsOuterEntity> entities) {
                final LQCourseContract.View view = (LQCourseContract.View) getView();
                if(!EmptyUtil.isEmpty(entities) && !EmptyUtil.isEmpty(view)){
                    view.updateNewBasicsConfigView(entities);
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
                        final LQCourseContract.View view = getView();
                        if(EmptyUtil.isNotEmpty(view)){
                            view.updateConfigView(parentId,entity,entities);
                        }
                    }
                });
            }
        }
    }
}
