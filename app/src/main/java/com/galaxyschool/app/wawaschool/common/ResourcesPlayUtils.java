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
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.helper.LqCourseHelper;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseUploadResult;
import com.galaxyschool.app.wawaschool.views.ResourcePlayListDialog;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.course.CourseResourceEntity;
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
    private int currentPosition;
    private List<CourseResourceEntity> playList;

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

    public ResourcesPlayUtils setPlayList(List<CourseResourceEntity> playList) {
        this.playList = playList;
        return this;
    }

    public void startPlay() {
        if (activity == null) {
            return;
        }
        if (playList == null || playList.size() == 0){
            return;
        }
        loadResourceData();
    }

    /**
     * 获取播放列表课件的数量
     */
    public int getPlayResourceSize() {
        if (playList == null) {
            return 0;
        }
        return playList.size();
    }

    /**
     * 清空播放的数据
     */
    public void releasePlayResource() {
        currentPosition = 0;
        playList = null;
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
                    List<CourseData> dataList = result.data;
                    if (dataList != null && dataList.size() > 0) {
                        mergePlayData(dataList);
                        dialog = new ResourcePlayListDialog(activity, playList, position -> {
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

    private void mergePlayData(List<CourseData> dataList){
        for (int i = 0; i < playList.size(); i++){
            CourseResourceEntity data = playList.get(i);
            for (int j = 0; j < dataList.size(); j++){
                CourseData courseData = dataList.get(j);
                if (data.getResId() == courseData.id){
                    data.setScreenType(courseData.screentype);
                    data.setResourceUrl(courseData.resourceurl);
                }
            }
        }
    }

    private String getResIds() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < playList.size(); i++) {
            CourseResourceEntity info = playList.get(i);
            if (builder.length() == 0) {
                builder.append(info.getResId() + "-" + info.getResType());
            } else {
                builder.append(",").append(info.getResId() + "-" + info.getResType());
            }
        }
        return builder.toString();
    }

    private void openPlayActivity() {
        if (activity == null) {
            return;
        }
        CourseResourceEntity playInfo = playList.get(currentPosition);
        if (playInfo == null) {
            return;
        }
        if (!playInfo.isSelected()) {
            playInfo.setSelected(true);
            if (dialog != null) {
                dialog.notifyDataSetChanged();
            }
        }
        Intent intent = new Intent(activity, PlaybackActivity.class);
        Bundle extras = new Bundle();
        String courseUrl = playInfo.getResourceUrl();
        if (courseUrl.endsWith(".zip")) {
            courseUrl = courseUrl.substring(0, courseUrl.lastIndexOf('.'));
        } else if (courseUrl.contains(".zip?")) {
            courseUrl = courseUrl.substring(0, courseUrl.lastIndexOf(".zip?"));
        }
        extras.putString(PlaybackActivity.FILE_PATH, courseUrl);
        extras.putInt(PlaybackActivity.ORIENTATION, playInfo.getScreenType());
        extras.putInt(PlaybackActivity.PLAYBACK_TYPE, BaseUtils.RES_TYPE_COURSE);
        extras.putBoolean(PlaybackActivity.IS_PLAY_ORIGIN_VOICE, true);
        extras.putBoolean(PlaybackActivity.EXIT_PLAYBACK_AFTER_COMPLETION, true);
        extras.putParcelable(SlideInPlaybackParam.class.getSimpleName(), new SlideInPlaybackParam());
        intent.putExtras(extras);
        activity.startActivityForResult(intent, RESOURCE_PLAY_COMPLETED_REQUEST_CODE);
        updateTaskReadState(playInfo);
    }

    private void updateTaskReadState(CourseResourceEntity playInfo){
        if (playInfo.getId() > 0){
            if (playInfo.getTaskType() == 1
                    || playInfo.getTaskType() == 4){
                LqCourseHelper.updateReadState(activity,playInfo.getId(),playInfo.getResId()+"",
                        DemoApplication.getInstance().getMemberId());
            }
        }
    }

    /**
     * 课件播放完成之后继续播放一下
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESOURCE_PLAY_COMPLETED_REQUEST_CODE) {
            if (data != null) {
                boolean playCompleted = data.getBooleanExtra(PlaybackActivity.EXIT_PLAYBACK_AFTER_COMPLETION, false);
                if (playCompleted) {
                    currentPosition++;
                    if (currentPosition < playList.size()) {
                        openPlayActivity();
                    } else {
                        currentPosition = 0;
                    }
                }
            }
        }
    }
}
