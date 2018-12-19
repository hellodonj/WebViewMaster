package com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.Utils;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.helper.LQCourseHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.LQCourseContract;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.LanguageType;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.oosic.apps.share.BaseShareUtils;
import com.oosic.apps.share.ShareInfo;
import com.umeng.socialize.media.UMImage;

import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 筛选页面的Presenter
 * @date 2018/05/03 16:57
 * @history v1.0
 * **********************************
 */
public class CourseFiltratePresenter extends BasePresenter<CourseFiltrateContract.View>
    implements CourseFiltrateContract.Presenter{

    public CourseFiltratePresenter(CourseFiltrateContract.View view) {
        super(view);
    }

    @Override
    public void requestConfigData(@NonNull int level, @NonNull int parentId) {
        // 获取中英文数据
        int languageRes = Utils.isZh(UIUtil.getContext()) ? LanguageType.LANGUAGE_CHINESE : LanguageType.LANGUAGE_OTHER;
        LQCourseHelper.requestLQCourseConfigData(languageRes, level, parentId, new DataSource.Callback<List<LQCourseConfigEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                UIUtil.showToastSafe(strRes);
            }

            @Override
            public void onDataLoaded(List<LQCourseConfigEntity> entities) {
                final CourseFiltrateContract.View view = (CourseFiltrateContract.View) getView();
                if(!EmptyUtil.isEmpty(entities) && !EmptyUtil.isEmpty(view)){
                    view.onConfigLoaded(entities);
                }
            }
        });
    }


    @Override
    public void requestCourseData(int pageIndex, int pageSize, @NonNull String level,String keyString, int paramOneId, int paramTwoId, int paramThreeId) {
        LQCourseHelper.requestLQCourseData(null,pageIndex, pageSize, level,"0",keyString,Integer.MAX_VALUE, paramOneId, paramTwoId, paramThreeId, new DataSource.Callback<List<CourseVo>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                UIUtil.showToastSafe(strRes);
            }

            @Override
            public void onDataLoaded(List<CourseVo> courseVos) {
                final CourseFiltrateContract.View view = (CourseFiltrateContract.View) getView();
                if(null != courseVos && !EmptyUtil.isEmpty(view)){
                    view.onCourseLoaded(courseVos);
                }
            }
        });
    }


    @Override
    public void requestMoreCourseData(int pageIndex, int pageSize, @NonNull String level,String keyString, int paramOneId, int paramTwoId, int paramThreeId) {
        LQCourseHelper.requestLQCourseData(null,pageIndex, pageSize, level,"0",keyString,Integer.MAX_VALUE, paramOneId, paramTwoId, paramThreeId, new DataSource.Callback<List<CourseVo>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                UIUtil.showToastSafe(strRes);
            }

            @Override
            public void onDataLoaded(List<CourseVo> courseVos) {
                final CourseFiltrateContract.View view = (CourseFiltrateContract.View) getView();
                if(null != courseVos && !EmptyUtil.isEmpty(view)){
                    view.onMoreCourseLoaded(courseVos);
                }
            }
        });
    }

    @Override
    public void share(String title,String description,String thumbnailUrl,String url) {
        ShareInfo shareInfo = new ShareInfo();
        shareInfo.setTitle(title);
        shareInfo.setContent(description);
        shareInfo.setTargetUrl(url);
        UMImage umImage = null;
        if(getView() instanceof Activity){
            Activity activity = (Activity) getView(); if (!TextUtils.isEmpty(thumbnailUrl)) {
                umImage = new UMImage(activity, thumbnailUrl);
            } else {
                umImage = new UMImage(activity, R.drawable.default_cover);
            }

            shareInfo.setuMediaObject(umImage);
            BaseShareUtils utils = new BaseShareUtils(activity);
            utils.share(activity.getWindow().getDecorView(),shareInfo);
        }

    }
}
