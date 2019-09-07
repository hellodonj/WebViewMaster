package com.galaxyschool.app.wawaschool.helper;

import android.app.Activity;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.galaxyschool.app.wawaschool.MultiVideoPlayActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.imagebrowser.GalleryActivity;
import com.libs.gallery.ImageInfo;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.lqwawa.lqbaselib.net.library.ResourceResult;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EBanShuHelper {

    public static void loadEBanShuVideoDetail(Activity activity,
                                              String roomId,
                                              String title) {
        StringBuilder urlBuilder = new StringBuilder("https://api.ebanshu.net/v1/rooms/video/list?room_id=");
        urlBuilder.append(roomId);
        Map<String, String> params = new HashMap<>();
        params.put("ebs-app-id", "2");
        params.put("ebs-app-key", "123456");
        RequestHelper.RequestListener listener = new RequestHelper
                .RequestListener(activity, ResourceResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (TextUtils.isEmpty(jsonString)) {
                    TipMsgHelper.ShowMsg(activity, R.string.str_blackboard_live_overed);
                } else {
                    try {
                        org.json.JSONObject jsonObject = new org.json.JSONObject(jsonString);
                        boolean isOk = jsonObject.getBoolean("ok");
                        if (isOk) {
                            org.json.JSONObject dataObj = jsonObject.getJSONObject("data");
                            if (dataObj != null) {
                                org.json.JSONArray videoList = dataObj.getJSONArray("object_list");
                                if (videoList != null && videoList.length() > 0) {
                                    ArrayList<String> videoPaths = new ArrayList<>();
                                    ArrayList<Integer> videoDurations = new ArrayList<>();
                                    for (int i = 0, len = videoList.length(); i < len; i++) {
                                        org.json.JSONObject videoObj = (org.json.JSONObject) videoList.get(i);
                                        if (videoObj != null) {
                                            org.json.JSONObject video = videoObj.getJSONObject("video");
                                            if (video != null) {
                                                videoPaths.add(video.getString("location_url"));
                                                double duration = video.getDouble("duration");
                                                int millisecond = (int) (duration * 1000);
                                                videoDurations.add(millisecond);
                                            }
                                        }
                                    }
                                    if (videoDurations.size() > 0 && videoPaths.size() > 0) {
                                        MultiVideoPlayActivity.start(activity, title,
                                                videoPaths, videoDurations);
                                        return;
                                    }
                                }
                            }
                        }
                        TipMsgHelper.ShowMsg(activity, R.string.str_blackboard_live_overed);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        listener.setShowLoading(true);
        RequestHelper.getRequest(activity, urlBuilder.toString(),listener, params);
    }

    public static void loadEBanShuImageList(Activity activity,
                                            String roomId,
                                            String title,
                                            String defaultThumbnail) {
        StringBuilder urlBuilder = new StringBuilder("https://api.ebanshu.net/v1/rooms/frame/list/?room_id=");
        urlBuilder.append(roomId);
        Map<String, String> params = new HashMap<>();
        params.put("ebs-app-id", "2");
        params.put("ebs-app-key", "123456");
        RequestHelper.RequestListener listener =new RequestHelper
                .RequestListener(activity, ResourceResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                List<ImageInfo> infos = new ArrayList<>();
                if (!TextUtils.isEmpty(jsonString)) {
                    JSONObject obj = JSONObject.parseObject(jsonString);
                    if (obj != null) {
                        JSONObject dataObj = obj.getJSONObject("data");
                        if (dataObj != null) {
                            JSONArray objectArray = dataObj.getJSONArray("object_list");
                            if (objectArray != null && objectArray.size() > 0) {
                                for (int i = 0; i < objectArray.size(); i++) {
                                    JSONArray itemArray = objectArray.getJSONArray(i);
                                    if (itemArray != null && itemArray.size() > 0) {
                                        for (int j = 0; j < itemArray.size(); j++) {
                                            JSONObject detailObj = itemArray.getJSONObject(j);
                                            if (detailObj != null) {
                                                int type = detailObj.getIntValue("ty");
                                                if (type == 4) {
                                                    //含有图片
                                                    JSONObject coObj = detailObj.getJSONObject("co");
                                                    if (coObj != null) {
                                                        String url = coObj.getString("location");
                                                        if (!TextUtils.isEmpty(url)) {
                                                            ImageInfo info = new ImageInfo();
                                                            info.setResourceUrl(url);
                                                            info.setTitle(title);
                                                            infos.add(info);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (infos.size() == 0 && !TextUtils.isEmpty(defaultThumbnail)){
                    ImageInfo info = new ImageInfo();
                    info.setResourceUrl(defaultThumbnail);
                    info.setTitle(title);
                    infos.add(info);
                }
                GalleryActivity.newInstance(activity, infos, roomId, title);
            }
        };
        listener.setShowLoading(true);
        RequestHelper.getRequest(activity, urlBuilder.toString(),listener , params);
    }
}
