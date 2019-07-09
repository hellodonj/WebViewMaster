package com.galaxyschool.app.wawaschool.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.Request;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseUploadResult;
import com.galaxyschool.app.wawaschool.views.ResourcePlayListDialog;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.lqbaselib.net.ThisStringRequest;
import com.oosic.apps.iemaker.base.BaseUtils;
import com.oosic.apps.iemaker.base.PlaybackActivity;
import com.oosic.apps.iemaker.base.interactionlayer.data.SlideInPlaybackParam;

import java.util.List;

/**
 * ======================================================
 * Describe:资源播放工具类
 * ======================================================
 */
public class ResourcesPlayUtils {
    //播放完成返回的result
    public static final int RESOURCE_PLAY_COMPLETED_REQUEST_CODE = 168;
    private Activity activity;
    private ResourcePlayListDialog dialog;
    private List<String> resIds;
    private List<CourseData> playDataList;
    private int currentPosition;

    public static ResourcesPlayUtils getInstance() {
        return ResourcePlayUtilsHolder.instance;
    }

    public static class ResourcePlayUtilsHolder {
        public static ResourcesPlayUtils instance = new ResourcesPlayUtils();
    }

    public ResourcesPlayUtils setActivity(Activity activity) {
        this.activity = activity;
        return this;
    }

    /**
     * @param resIds resId-type
     */
    public ResourcesPlayUtils setResIds(List<String> resIds) {
        this.resIds = resIds;
        return this;
    }

    public void startPlay() {
        if (activity == null) {
            return;
        }
        if (resIds == null || resIds.size() == 0) {
            return;
        }
        loadResourceData();
    }

    /**
     * 获取播放列表课件的数量
     */
    public int getPlayResourceSize() {
        if (resIds == null) {
            return 0;
        }
        return resIds.size();
    }

    /**
     * 清空播放的数据
     */
    public void releasePlayResource() {
        currentPosition = 0;
        resIds = null;
    }

    /**
     * 显示播放列表
     */
    public void showPlayListDialog(Activity activity) {
        this.activity = activity;
        if (dialog != null) {
            dialog.show();
        }
    }

    private void loadResourceData() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("resIds", getResIds());
            jsonObject.put("haveMp3", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String jsonString = jsonObject.toString();
        StringBuilder builder = new StringBuilder();
        builder.append("?j=" + jsonString);
        String url = ServerUrl.GET_RESOURSE_LIST_BYIDS_BASE_URL + builder.toString();
        ThisStringRequest request = new ThisStringRequest(
                Request.Method.GET, url, new Listener<String>() {
            @Override
            public void onSuccess(String jsonString) {
                if (TextUtils.isEmpty(jsonString)) {
                    return;
                }
                CourseUploadResult result = JSONObject.parseObject(jsonString, CourseUploadResult.class);
                if (result != null && result.code == 0) {
                    playDataList = result.data;
                    if (playDataList != null && playDataList.size() > 0) {
                        dialog = new ResourcePlayListDialog(activity, playDataList,position -> {
                            currentPosition = (int) position;
                            openPlayActivity();
                        });
                        openPlayActivity();
                    }
                }
            }

            @Override
            public void onError(NetroidError error) {
            }
        });
        request.addHeader("Accept-Encoding", "*");
        request.start(UIUtil.getContext());
    }

    private String getResIds() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < resIds.size(); i++){
            if (builder.length() == 0){
                builder.append(resIds.get(i));
            } else {
                builder.append(",").append(resIds.get(i));
            }
        }
//        builder.append("712577-19").append(",").append("715481-19");
        return builder.toString();
    }

    private void openPlayActivity() {
        if (activity == null) {
            return;
        }
        CourseData courseData = playDataList.get(currentPosition);
        if (courseData == null) {
            return;
        }
        if (!courseData.isSelected()){
            courseData.setSelected(true);
            if (dialog != null) {
                dialog.notifyDataSetChanged();
            }
        }
        Intent intent = new Intent(activity, PlaybackActivity.class);
        Bundle extras = new Bundle();
        String courseUrl = playDataList.get(currentPosition).resourceurl;
        if (courseUrl.endsWith(".zip")) {
            courseUrl = courseUrl.substring(0, courseUrl.lastIndexOf('.'));
        } else if (courseUrl.contains(".zip?")) {
            courseUrl = courseUrl.substring(0, courseUrl.lastIndexOf(".zip?"));
        }
        extras.putString(PlaybackActivity.FILE_PATH, courseUrl);
        extras.putInt(PlaybackActivity.ORIENTATION, courseData.screentype);
        extras.putInt(PlaybackActivity.PLAYBACK_TYPE, BaseUtils.RES_TYPE_COURSE);
        extras.putBoolean(PlaybackActivity.IS_PLAY_ORIGIN_VOICE, true);
        extras.putBoolean(PlaybackActivity.EXIT_PLAYBACK_AFTER_COMPLETION, true);
        extras.putParcelable(SlideInPlaybackParam.class.getSimpleName(), new SlideInPlaybackParam());
        intent.putExtras(extras);
        activity.startActivityForResult(intent, RESOURCE_PLAY_COMPLETED_REQUEST_CODE);
    }

    /**
     * 课件播放完成之后继续播放一下
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESOURCE_PLAY_COMPLETED_REQUEST_CODE){
            if (data != null){
                boolean playCompleted = data.getBooleanExtra(PlaybackActivity.EXIT_PLAYBACK_AFTER_COMPLETION,false);
                if (playCompleted){
                    currentPosition++;
                    if (currentPosition < playDataList.size()) {
                        openPlayActivity();
                    } else {
                        currentPosition = 0;
                    }
                }
            }
        }
    }
}
