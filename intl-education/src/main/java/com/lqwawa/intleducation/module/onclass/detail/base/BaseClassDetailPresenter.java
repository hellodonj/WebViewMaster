package com.lqwawa.intleducation.module.onclass.detail.base;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.ClassDetailEntity;
import com.lqwawa.intleducation.factory.data.entity.school.SchoolInfoEntity;
import com.lqwawa.intleducation.factory.helper.OnlineCourseHelper;
import com.lqwawa.intleducation.factory.helper.SchoolHelper;
import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.oosic.apps.iemaker.base.ooshare.ShareUtil;
import com.oosic.apps.share.BaseShareUtils;
import com.oosic.apps.share.ShareHelper;
import com.oosic.apps.share.ShareInfo;
import com.umeng.socialize.media.UMImage;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 班级详情的Presenter基类
 * @date 2018/06/07 14:38
 * @history v1.0
 * **********************************
 */
public abstract class BaseClassDetailPresenter<T extends BaseClassDetailContract.View> extends BasePresenter<T>
    implements BaseClassDetailContract.Presenter {

    public BaseClassDetailPresenter(T view) {
        super(view);
    }

    @Override
    public void requestSchoolInfo(@NonNull String schoolId) {
        String userId = UserHelper.getUserId();
        SchoolHelper.requestSchoolInfo(userId, schoolId, new DataSource.Callback<SchoolInfoEntity>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final BaseClassDetailContract.View view = getView();
                if(!EmptyUtil.isEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(SchoolInfoEntity entity) {
                final BaseClassDetailContract.View view = getView();
                if(!EmptyUtil.isEmpty(view) && !EmptyUtil.isEmpty(entity)){
                    view.updateSchoolInfoView(entity);
                }
            }
        });
    }

    @Override
    public void requestClassDetail(int id,final boolean refreshHeader) {
        OnlineCourseHelper.requestClassDetail(id, new DataSource.Callback<ClassDetailEntity>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final BaseClassDetailContract.View view = getView();
                if(!EmptyUtil.isEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(ClassDetailEntity entity) {
                final BaseClassDetailContract.View view = getView();
                if(!EmptyUtil.isEmpty(view) && !EmptyUtil.isEmpty(entity)){
                    view.updateClassDetailView(refreshHeader,entity);
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