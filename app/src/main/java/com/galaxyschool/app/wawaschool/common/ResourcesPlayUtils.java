package com.galaxyschool.app.wawaschool.common;

import android.app.Activity;
import android.text.TextUtils;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.Request;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.helper.AudioPlayerHelper;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseUploadResult;
import com.galaxyschool.app.wawaschool.views.ResourcePlayListDialog;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.lqbaselib.net.ThisStringRequest;
import java.util.List;

/**
 * ======================================================
 * Describe:资源播放工具类
 * ======================================================
 */
public class ResourcesPlayUtils {
    private Activity activity;
    private AudioPlayerHelper audioPlayerHelper;
    private ResourcePlayListDialog dialog;
    private List<String> resIds;
    private List<CourseData> playDataList;
    private int currentPosition;
    private int childMp3Position;

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
        if (resIds == null || resIds.size() == 0) {
            return;
        }
        loadResourceData();
    }

    /**
     * 重新播放
     */
    public void rePlayResource() {
        processMp3Url();
    }

    /**
     * 清空播放的数据
     */
    public void releasePlayResource() {
        if (audioPlayerHelper != null) {
            audioPlayerHelper.releaseAudio();
        }
        currentPosition = 0;
        childMp3Position = 0;
        resIds = null;
    }

    /**
     * 显示播放列表
     */
    public void showPlayListDialog() {
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
                CourseUploadResult result = JSONObject.parseObject(jsonString,
                        CourseUploadResult.class);
                if (result != null && result.code == 0) {
                    playDataList = result.data;
                    if (playDataList != null && playDataList.size() > 0) {
                        dialog = new ResourcePlayListDialog(activity, playDataList);
                        processMp3Url();
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

    private void processMp3Url() {
        if (audioPlayerHelper == null) {
            audioPlayerHelper = new AudioPlayerHelper(UIUtil.getContext());
            audioPlayerHelper.setCompleteListener(result -> {
                //单个播放完成执行下一个
                LogUtils.log("ResourcesPlayUtils", "complete");
                String mp3Path = getMp3Path(currentPosition);
                if (!TextUtils.isEmpty(mp3Path)) {
                    audioPlayerHelper.setPlayUrl(mp3Path);
                    audioPlayerHelper.play();
                    if (dialog != null) {
                        dialog.updateAlreadyPlayedMp3(currentPosition);
                    }
                }
            });
        }
        audioPlayerHelper.setPlayUrl(getMp3Path(0));
        audioPlayerHelper.play();
    }

    private String getMp3Path(int position) {
        String mp3Path = null;
        if (position < playDataList.size()) {
            List<String> mp3List = playDataList.get(position).getMp3List();
            if (mp3List != null && mp3List.size() > 0) {
                int length = mp3List.size();
                if (childMp3Position < length) {
                    mp3Path = mp3List.get(childMp3Position);
                    childMp3Position++;
                } else {
                    //下一段
                    childMp3Position = 0;
                    return getMp3Path(position + 1);
                }
            } else {
                childMp3Position = 0;
                return getMp3Path(position + 1);
            }
            currentPosition = position;
            LogUtils.log("ResourcesPlayUtils", "currentPosition=" + currentPosition);
            LogUtils.log("ResourcesPlayUtils", "childMp3Position=" + childMp3Position);
            LogUtils.log("ResourcesPlayUtils", "mp3Path=" + mp3Path);
        } else {
            if (dialog != null) {
                dialog.updateAlreadyPlayedMp3(position);
            }
        }
        return mp3Path;
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
}
