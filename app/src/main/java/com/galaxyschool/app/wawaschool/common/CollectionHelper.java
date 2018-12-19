package com.galaxyschool.app.wawaschool.common;

import android.app.Activity;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.duowan.mobile.netroid.NetroidError;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.Note.OnlineMediaPaperActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.lqwawa.lqbaselib.net.NetErrorResult;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.oosic.apps.aidl.CollectionData;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: wangchao
 * Time: 2015/10/19 10:29
 */
public class CollectionHelper {

    private Activity activity;
    private UserInfo userInfo;
    private boolean isPublicRes = true;
    private boolean isFromChoiceLib;
    private String collectSchoolId;
    private DialogHelper.LoadingDialog loadingDialog;
    OnLoadCollectionStatusFinishListener loadCollectionStatusFinishListener;
    OnCollectionFinishListener collectionFinishListener;
    private boolean isFromLQTools = false;

    public interface OnLoadCollectionStatusFinishListener {
        void onLoadCollectionStatusFinish(CollectionData data);
    }

    public void setIsPublicRes(boolean isPublicRes){
        this.isPublicRes = isPublicRes;
    }

    public interface OnCollectionFinishListener {
        void onCollectionFinish(CollectionData data);
    }

    public void setFromChoiceLib(boolean isFromChoiceLib){
        this.isFromChoiceLib = isFromChoiceLib;
    }
    public void setCollectSchoolId(String collectSchoolId){
        this.collectSchoolId = collectSchoolId;
    }

    public boolean isFromLQTools() {
        return isFromLQTools;
    }

    public void setFromLQTools(boolean fromLQTools) {
        isFromLQTools = fromLQTools;
    }

    public CollectionHelper(Activity activity) {
        this.activity = activity;
        userInfo = ((MyApplication) activity.getApplication()).getUserInfo();
    }

    public void collectResource(String resId, String title, String authorId) {
        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
            ActivityUtils.enterLogin(activity);
            return;
        }

        if (TextUtils.isEmpty(resId)) {
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", userInfo.getMemberId());
        params.put("MicroID", resId);
        params.put("Title", title);
        params.put("Author", authorId);
        if (!isPublicRes) {
            if (TextUtils.isEmpty(collectSchoolId)) {
                String schoolId = DemoApplication.getInstance().getPrefsManager().getLatestSchool
                        (activity, userInfo.getMemberId());
                params.put("CollectionOrigin", schoolId);
            }else {
                params.put("CollectionOrigin", collectSchoolId);
            }
        }
        params.put("IsQualityCourse",isFromChoiceLib);
        RequestHelper.RequestDataResultListener listener = new RequestHelper
                .RequestDataResultListener<DataModelResult>(activity, DataModelResult.class){
            @Override
            public void onSuccess(String jsonString) {
                super.onSuccess(jsonString);
                DataModelResult result = getResult();
                if (activity != null) {
                    if (result != null) {
                        if (result.isSuccess()) {
                            //设置收藏刷新标志位
                            OnlineMediaPaperActivity.setHasContentChanged(true);
                            if (!isFromLQTools()) {
                                TipMsgHelper.ShowLMsg(activity, R.string.collect_to_yuntieba);
                            } else {
                                TipMsgHelper.ShowLMsg(activity, R.string.collect_to_lq_course);
                            }
                        }else{
                            String errorMessage = result.getErrorMessage();
                            if (result.getErrorCode() == -1){
                                //已收藏
                                errorMessage = activity.getString(
                                        R.string.have_been_collected);
                            }
                            TipMsgHelper.ShowLMsg(activity, errorMessage);
                        }
                    }
                }
            }
            @Override
            public void onError(NetroidError error) {
                super.onError(error);
                String es = error.getMessage();
                try {
                    NetErrorResult result = JSON.parseObject(es, NetErrorResult.class);
                    if (result != null) {
                        if (result.isHasError()) {
                            TipMsgHelper.ShowMsg(activity, result.getErrorMessage());
                        }
                    }
                } catch (Exception e) {
                    TipMsgHelper.ShowLMsg(activity, activity.getString(R.string.network_error));
                }
            }
        };
        listener.setShowErrorTips(false);
        RequestHelper.sendPostRequest(activity, ServerUrl.COLLECT_RESOURCE_URL,
            params,listener);
    }

    /**
     * 收藏到个人资源库相关的资源
     * @param resId
     * @param title
     * @param authorId
     * @param tag
     */
    public void collectDifferentResource(String resId, String title, String authorId,final String tag) {
        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
            ActivityUtils.enterLogin(activity);
            return;
        }

        if (TextUtils.isEmpty(resId)) {
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", userInfo.getMemberId());
        params.put("MicroID", resId);
        params.put("Title", title);
        params.put("Author", authorId);
        if (!isPublicRes) {
            if (TextUtils.isEmpty(collectSchoolId)) {
                String schoolId = DemoApplication.getInstance().getPrefsManager().getLatestSchool
                        (activity, userInfo.getMemberId());
                params.put("CollectionOrigin", schoolId);
            }else {
                params.put("CollectionOrigin", collectSchoolId);
            }
        }
        params.put("IsQualityCourse",isFromChoiceLib);
        RequestHelper.RequestDataResultListener listener = new RequestHelper
                .RequestDataResultListener<DataModelResult>(activity, DataModelResult.class){
            @Override
            public void onSuccess(String jsonString) {
                super.onSuccess(jsonString);
                DataModelResult result = getResult();
                if (activity != null) {
                    if (result != null) {
                        if (result.isSuccess()){
                            TipMsgHelper.ShowMsg(activity,activity.getString(R.string
                                    .collect_different_to_personalLibrary,tag));
                        }else{
                            String errorMessage = result.getErrorMessage();
                            if (result.getErrorCode() == -1){
                                //已收藏
                                errorMessage = activity.getString(
                                        R.string.have_been_collected);
                            }
                            TipMsgHelper.ShowLMsg(activity, errorMessage);
                        }
                    }
                }
            }
            @Override
            public void onError(NetroidError error) {
                super.onError(error);
                String es = error.getMessage();
                try {
                    NetErrorResult result = JSON.parseObject(es, NetErrorResult.class);
                    if (result != null) {
                        if (result.isHasError()) {
                            TipMsgHelper.ShowMsg(activity, result.getErrorMessage());
                        }
                    }
                } catch (Exception e) {
                    TipMsgHelper.ShowLMsg(activity, activity.getString(R.string.network_error));
                }
            }
        };
        listener.setShowErrorTips(false);
        RequestHelper.sendPostRequest(activity, ServerUrl.COLLECT_RESOURCE_URL,
                params,listener);
    }

}
