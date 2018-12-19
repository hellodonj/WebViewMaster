package com.galaxyschool.app.wawaschool.common;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.Request;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.Note.OnlineMediaPaperActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.lqwawa.lqbaselib.net.PostByMapParamsModelRequest;
import com.lqwawa.lqbaselib.net.ThisStringRequest;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.ResourceType;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseUploadResult;
import com.galaxyschool.app.wawaschool.pojo.weike.SplitCourseInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.SplitCourseInfoListResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class WawaCourseUtils<T> {

    private Activity activity;
    private UserInfo userInfo;
    private DialogHelper.LoadingDialog loadingDialog;
    private OnCoursePraiseFinishListener praiseListener;
    private OnCourseDetailFinishListener detailListener;
    private OnCommentSendFinishListener sendListener;
    private OnSplitCourseDetailFinishListener splitDetailListener;

    public interface OnCoursePraiseFinishListener {
        void onCoursePraiseFinish(String courseId, int code, int praiseNum);
    }
    public interface OnCourseDetailFinishListener {
        void onCourseDetailFinish(CourseData courseData);
    }

    public interface OnCommentSendFinishListener {
        void onCommentSendFinish(int code);
    }

    public interface OnSplitCourseDetailFinishListener {
        void onSplitCourseDetailFinish(SplitCourseInfo info);
    }

    public WawaCourseUtils(Activity activity) {
        this.activity = activity;
        userInfo = ((MyApplication) activity.getApplication()).getUserInfo();
    }

    public void setOnCoursePraiseFinishListener(OnCoursePraiseFinishListener listener) {
        this.praiseListener = listener;
    }

    public void setOnCourseDetailFinishListener(OnCourseDetailFinishListener listener) {
        this.detailListener = listener;
    }

    public void setOnCommentSendFinishListener(OnCommentSendFinishListener listener) {
        this.sendListener = listener;
    }

    public void setOnSplitCourseDetailFinishListener(OnSplitCourseDetailFinishListener listener) {
        this.splitDetailListener = listener;
    }


    public void updateVideoViewCount(int id, SetHasReadParam param, boolean isNote) {
        updateVideoViewCountToMicroCourse(id, param, isNote);
    }

    private void updateVideoViewCountToMicroCourse(final int id, final SetHasReadParam param, final boolean isNote) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("courseId", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String jsonString = jsonObject.toString();
        StringBuilder builder = new StringBuilder();
        builder.append("?j=" + jsonString);
        String url = ServerUrl.WAWATV_UPDATE_VIEWCOUNT_URL + builder.toString();
        ThisStringRequest request = new ThisStringRequest(
            Request.Method.GET, url, new Listener<String>() {
            @Override
            public void onSuccess(String jsonString) {
                try {
                    JSONObject dataJsonObject = new JSONObject(jsonString);
                    if(dataJsonObject != null) {
                        int viewCount = dataJsonObject.optInt("total");
                        if(viewCount > 0 && !isNote) {
                            updateVideoViewCountToWawachat(id, viewCount, param);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(NetroidError error) {
            }
        });
        request.addHeader("Accept-Encoding", "*");
        request.start(activity);
    }

    private void updateVideoViewCountToWawachat(final int id, int viewCount, SetHasReadParam param) {
        String url = ServerUrl.CS_UPDATE_VIEWCOUNT_URL;
        Map<String, String> mParams = new HashMap<String, String>();
        mParams.put("MicroID", String.valueOf(id));
        mParams.put("Type", "1");
        mParams.put("PlayerNumber", String.valueOf(viewCount));
        UserInfo info = ((MyApplication) activity.getApplication()).getUserInfo();
        if (info == null){
            return;
        }
        if (param != null && !TextUtils.isEmpty(info.getMemberId())) {
        	mParams.put("MemberId", info.getMemberId());
        	mParams.put("TargetType", String.valueOf(param.mType));
        	mParams.put("Id", param.mId);
		}

        PostByMapParamsModelRequest request = new PostByMapParamsModelRequest(
            url, mParams, new Listener<String>() {
            @Override
            public void onSuccess(String json) {
                LogUtils.logi("WawaCourseUtils", "json = " + json);
                Intent intent = new Intent("CommitReadCountOk");
				intent.putExtra("Id", String.valueOf(id));
				intent.putExtra("ResourceType", ResourceType.VIDEO);
				if (activity != null) {
					activity.sendBroadcast(intent);
				}
            }

            @Override
            public void onError(NetroidError error) {
                super.onError(error);
            }
        });
        request.addHeader("Accept-Encoding", "*");
        request.start(activity);
    }

//    public void collectCourse(CourseInfo courseInfo) {
//        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
//            ActivityUtils.enterLogin(activity);
//            return;
//        }
//        String url = ServerUrl.CS_UPLOAD_TO_PERSONAL_SPACE_URL;
//        Map<String, String> mParams = new HashMap<String, String>();
//        mParams.put("MemberId", userInfo.getMemberId());
//        mParams.put("MicroID", courseInfo.getMicroId());
//        mParams.put("Type", "3");
//        mParams.put("Thumbnail", courseInfo.getImgurl());
//        mParams.put("Title", courseInfo.getNickname());
//        mParams.put("CreateType", "2");
//        mParams.put("ResourceType", String.valueOf(courseInfo.getResourceType()));
//        mParams.put("Author", courseInfo.getCode());
//        mParams.put("Knowledge", courseInfo.getPoint());
//        mParams.put("Description", courseInfo.getDescription());
//        mParams.put("PlayAddress", courseInfo.getResourceurl());
//        mParams.put("Resourceurl", courseInfo.getResourceurl());
//
//        PostByMapParamsModelRequest request = new PostByMapParamsModelRequest(
//            url, mParams, new Listener<String>() {
//            @Override
//            public void onSuccess(String json) {
//                if (activity == null) {
//                    return;
//                }
//                if (loadingDialog != null) {
//                    loadingDialog.dismiss();
//                }
//                TipMsgHelper.ShowMsg(activity, activity.getString(R.string.collect_success));
//            }
//
//            @Override
//            public void onError(NetroidError error) {
//                super.onError(error);
//                if (activity == null) {
//                    return;
//                }
//                if (loadingDialog != null) {
//                    loadingDialog.dismiss();
//                }
//                String es = error.getMessage();
//                try {
//                    NetErrorResult result = JSON.parseObject(es, NetErrorResult.class);
//                    if (result != null) {
//                        if (result.isHasError()) {
//                            TipMsgHelper.ShowMsg(activity, result.getErrorMessage());
//                        }
//                    }
//                } catch (Exception e) {
//                    TipMsgHelper.ShowLMsg(activity, activity.getString(R.string.network_error));
//                }
//            }
//        });
//        loadingDialog = DialogHelper.getIt(activity).GetLoadingDialog(0);
//        request.addHeader("Accept-Encoding", "*");
//        request.start(activity);
//    }

    public void praiseCourse(final String courseId, final int type, final int resourceType) { //type 0 praise course, 1
        // praise
        // comment
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("courseId", courseId);
            jsonObject.put("type", type);
            //support anonym praise
            try {
                if (userInfo != null && !TextUtils.isEmpty(userInfo.getMemberId())) {
                    jsonObject.put("account", userInfo.getNickName());
                    jsonObject.put("createName", URLEncoder.encode(userInfo.getRealName(), "utf-8"));
                    jsonObject.put("headPic", userInfo.getHeaderPic());
                    jsonObject.put("memberId", userInfo.getMemberId());
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringBuilder builder = new StringBuilder();
        builder.append("?j=" + jsonObject.toString());
        String url = ServerUrl.WAWATV_PRAISE_COURSE_URL + builder.toString();
        ThisStringRequest request = new ThisStringRequest(
            Request.Method.GET, url, new Listener<String>() {
            @Override
            public void onSuccess(String jsonString) {
                if (activity == null) {
                    return;
                }
                try {
                    JSONObject dataJsonObject = new JSONObject(jsonString);
                    if (dataJsonObject != null) {
                        int praiseCount = dataJsonObject.optInt("praiseNum");
                        int code = dataJsonObject.optInt("code");
                        String message = "";
                        if (code == 0){
                            //点赞成功
                            message = activity.getString(R.string.praise_success);
                        }else {
                            //点赞失败
                            message = activity.getString(R.string.praise_fail);
                        }
                        TipMsgHelper.ShowLMsg(activity, message);
                        if (praiseListener != null) {
                            praiseListener.onCoursePraiseFinish(courseId, code, praiseCount);
                        }
                        //点赞成功
                        OnlineMediaPaperActivity.setHasContentChanged(true);
                        //only update course praise count to qingdao server
                        if (type == 0) {
                            updatePraiseCommentCount(courseId, resourceType, praiseCount, 0);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(NetroidError error) {
                if (activity == null) {
                    return;
                }
                TipMsgHelper.ShowMsg(activity, activity.getString(R.string.network_error));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (activity == null) {
                    return;
                }
                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                }
            }
        });
        loadingDialog = DialogHelper.getIt(activity).GetLoadingDialog(0);
        request.addHeader("Accept-Encoding", "*");
        request.start(activity);
    }

    public void updatePraiseCommentCount(final String id, int resourceType, int praiseCount, int commentCount) {
        String url = ServerUrl.UPDATE_PRAISE_COMMENT_NUMBER_URL;
        Map<String, String> mParams = new HashMap<String, String>();
        mParams.put("MicroID", id);
        mParams.put("ResourceType", String.valueOf(resourceType));
        if (praiseCount > 0) {
            mParams.put("PraiseNumber", String.valueOf(praiseCount));
        }
        if (commentCount > 0) {
            mParams.put("CommentNumber", String.valueOf(commentCount));
        }

        PostByMapParamsModelRequest request = new PostByMapParamsModelRequest(
            url, mParams, new Listener<String>() {
            @Override
            public void onSuccess(String json) {
                if (json != null) {

                }
            }

            @Override
            public void onError(NetroidError error) {
                super.onError(error);
            }
        });
        request.addHeader("Accept-Encoding", "*");
        request.start(activity);
    }

    private int type=0;
    public void sendComment(long resId, final String content,int type){
        this.type=type;
        sendComment(resId, content);
    }

    public void sendComment(long resId, final String content) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", type);//兼容noc大赛视频评论，以前不传默认type=0
            jsonObject.put("courseId", String.valueOf(resId));
            //support anonym comment
            try {
                if (userInfo != null && !TextUtils.isEmpty(userInfo.getMemberId())) {
                    jsonObject.put("account", userInfo.getNickName());
                    jsonObject.put("createName", URLEncoder.encode(userInfo.getRealName()
                            .toString().trim(), "utf-8"));
                    jsonObject.put("headPic", userInfo.getHeaderPic());
                    jsonObject.put("memberId", userInfo.getMemberId());
                }
                jsonObject.put("content", URLEncoder.encode(content, "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringBuilder builder = new StringBuilder();
        builder.append("?j=" + jsonObject.toString());
        String url = ServerUrl.WAWATV_CREATE_COMMENT_URL + builder.toString();
        ThisStringRequest request = new ThisStringRequest(
                Request.Method.GET, url, new Listener<String>() {
            @Override
            public void onSuccess(String jsonString) {
                if (activity == null) {
                    return;
                }
                try {
                    JSONObject dataJsonObject = new JSONObject(jsonString);
                    if (dataJsonObject != null) {
                        int code = dataJsonObject.optInt("code");
                        String message = "";
                        if (code == 0){
                            //评论成功
                            message = activity.getString(R.string.send_comment_success);
                        }else {
                            //评论失败
                            message = activity.getString(R.string.send_comment_failed);
                        }
                        TipMsgHelper.ShowLMsg(activity, message);
                        if(sendListener != null) {
                            sendListener.onCommentSendFinish(code);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(NetroidError error) {
                if (activity == null) {
                    return;
                }
                TipMsgHelper.ShowMsg(activity, activity.getString(R.string.network_error));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (activity == null) {
                    return;
                }
                loadingDialog.dismiss();
            }
        });
        loadingDialog = DialogHelper.getIt(activity).GetLoadingDialog(0);
        request.addHeader("Accept-Encoding", "*");
        request.start(activity);
    }

    public void loadCourseDetail(String resId) {
        loadCourseDetail(resId, false);
    }
    public void loadCourseDetail(String resId, final boolean isFinish) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("resId", resId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringBuilder builder = new StringBuilder();
        builder.append("?j=" + jsonObject.toString());
        String url = ServerUrl.WAWATV_COURSE_DETAIL_URL + builder.toString();
        ThisStringRequest request = new ThisStringRequest(
            Request.Method.GET, url, new Listener<String>() {
            @Override
            public void onSuccess(String jsonString) {
                if (activity == null) {
                    return;
                }
                if (jsonString != null) {
                    CourseUploadResult uploadResult = JSON.parseObject(
                        jsonString,
                        CourseUploadResult.class);
                    if (uploadResult != null && uploadResult.code == 0) {
                        if (uploadResult.getData() != null && uploadResult.getData().size()
                            > 0) {
                            if (detailListener != null) {
                                detailListener.onCourseDetailFinish(uploadResult.getData().get(0));
                            }
                        } else {
                            TipMsgHelper.ShowLMsg(activity, R.string.resource_not_exist);
                            if(isFinish && activity != null) {
                                activity.finish();
                            }
                        }
                    } else {
                        TipMsgHelper.ShowLMsg(activity, R.string.resource_not_exist);
                        if(isFinish && activity != null) {
                            activity.finish();
                        }
                    }
                }
            }

            @Override
            public void onError(NetroidError error) {
                if (activity == null) {
                    return;
                }
//                TipMsgHelper.ShowLMsg(activity, R.string.resource_not_exist);
                if(isFinish && activity != null) {
                    activity.finish();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (activity == null) {
                    return;
                }
                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                }
            }
        });
        loadingDialog = DialogHelper.getIt(activity).GetLoadingDialog(0);
        request.addHeader("Accept-Encoding", "*");
        request.start(activity);
    }

    public void loadSplitCourseDetail(long splitResId) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("splitResId", splitResId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringBuilder builder = new StringBuilder();
        builder.append("?j=" + jsonObject.toString());
        String url = ServerUrl.SPLIT_COURSE_DETAIL_URL + builder.toString();
        ThisStringRequest request = new ThisStringRequest(
            Request.Method.GET, url, new Listener<String>() {
            @Override
            public void onSuccess(String jsonString) {
                if (activity == null) {
                    return;
                }
                if (jsonString != null) {
                    SplitCourseInfoListResult uploadResult = JSON.parseObject(
                        jsonString,
                        SplitCourseInfoListResult.class);
                    if (uploadResult != null && uploadResult.getCode() == 0) {
                        if (uploadResult.getData() != null && uploadResult.getData().size()
                            > 0) {
                            if (splitDetailListener != null) {
                                splitDetailListener.onSplitCourseDetailFinish(uploadResult.getData().get(0));
                            }
                        } else {
                            TipMsgHelper.ShowLMsg(activity, R.string.resource_not_exist);
                        }
                    } else {
                        TipMsgHelper.ShowLMsg(activity, R.string.resource_not_exist);
                    }
                }
            }

            @Override
            public void onError(NetroidError error) {
                if (activity == null) {
                    return;
                }
//                TipMsgHelper.ShowLMsg(activity, R.string.resource_not_exist);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (activity == null) {
                    return;
                }
                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                }
            }
        });
        loadingDialog = DialogHelper.getIt(activity).GetLoadingDialog(0);
        request.addHeader("Accept-Encoding", "*");
        request.start(activity);
    }

    public void loadSplitLearnCardDetail(String courseId) {
        loadSplitLearnCardDetail(courseId, false);
    }

    public void loadSplitLearnCardDetail(String courseId, final boolean isFinish) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("pid", courseId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringBuilder builder = new StringBuilder();
        builder.append("?j=" + jsonObject.toString());
        String url = ServerUrl.SPLIT_LEARN_CARD_DETAIL_URL + builder.toString();
        ThisStringRequest request = new ThisStringRequest(
                Request.Method.GET, url, new Listener<String>() {
            @Override
            public void onSuccess(String jsonString) {
                if (activity == null) {
                    return;
                }
                if (jsonString != null) {
                    SplitCourseInfoListResult uploadResult = JSON.parseObject(
                            jsonString, SplitCourseInfoListResult.class);
                    if (uploadResult != null && uploadResult.getCode() == 0) {
                        if (uploadResult.getData() != null && uploadResult.getData().size()
                                > 0) {
                            if (splitDetailListener != null) {
                                splitDetailListener.onSplitCourseDetailFinish(uploadResult.getData().get(0));
                            }
                        } else {
                            TipMsgHelper.ShowLMsg(activity, R.string.please_retry_for_resource);
                            if(isFinish && activity != null) {
                                activity.finish();
                            }
                        }
                    } else {
                        TipMsgHelper.ShowLMsg(activity, R.string.please_retry_for_resource);
                        if(isFinish && activity != null) {
                            activity.finish();
                        }
                    }
                }
            }

            @Override
            public void onError(NetroidError error) {
                if (activity == null) {
                    return;
                }
                TipMsgHelper.ShowLMsg(activity, R.string.please_retry_for_resource);
                if(isFinish && activity != null) {
                    activity.finish();
                }
            }
        });
        request.addHeader("Accept-Encoding", "*");
        request.start(activity);
    }

    public static class SetHasReadParam {
    	public String mMemeber;
    	public String mId;
    	public int mType;
    }
}
