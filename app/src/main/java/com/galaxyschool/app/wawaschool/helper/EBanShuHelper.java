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
        RequestHelper.getRequest(activity, urlBuilder.toString(), listener, params);
    }

    public static void loadEBanShuImageList(Activity activity,
                                            String roomId,
                                            String title,
                                            String defaultThumbnail) {
        roomId = "1004687";
        pollingImageList(activity, roomId, title, defaultThumbnail, 1,null);
    }

    private static void pollingImageList(Activity activity,
                                         String roomId,
                                         String title,
                                         String defaultThumbnail,
                                         int pageIndex,
                                         List<ImageInfo> infos) {
        if (infos == null){
            infos = new ArrayList<>();
        }
        StringBuilder urlBuilder = new StringBuilder("https://api.ebanshu.net/v1/rooms/frame/image/list/?room_id=");
        urlBuilder.append(roomId);
        urlBuilder.append("&page_size=").append(100);
        urlBuilder.append("&page=").append(pageIndex);
        Map<String, String> params = new HashMap<>();
        params.put("ebs-app-id", "2");
        params.put("ebs-app-key", "123456");
        List<ImageInfo> finalInfos = infos;
        RequestHelper.RequestListener listener = new RequestHelper
                .RequestListener(activity, ResourceResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                int totalCount = 0;
                int totalPage = 0;
                int page = 0;
                if (!TextUtils.isEmpty(jsonString)) {
                    JSONObject obj = JSONObject.parseObject(jsonString);
                    if (obj != null) {
                        JSONObject dataObj = obj.getJSONObject("data");
                        if (dataObj != null) {
                            JSONObject metaObj = dataObj.getJSONObject("meta");
                            if (metaObj != null){
                                totalCount = metaObj.getIntValue("total_count");
                                totalPage = metaObj.getIntValue("total_page");
                                page = metaObj.getIntValue("page");
                            }
                            JSONArray objectArray = dataObj.getJSONArray("object_list");
                            if (objectArray != null && objectArray.size() > 0) {
                                for (int i = 0; i < objectArray.size(); i++) {
                                    String url = objectArray.getString(i);
                                    if (!TextUtils.isEmpty(url)) {
                                        ImageInfo info = new ImageInfo();
                                        info.setResourceUrl(url);
                                        info.setTitle(title);
                                        finalInfos.add(info);
                                    }
                                }
                            }
                        }
                    }
                }
                if (page == totalPage){
                    //最后一页
                    if (finalInfos.size() == 0 && !TextUtils.isEmpty(defaultThumbnail)) {
                        ImageInfo info = new ImageInfo();
                        info.setResourceUrl(defaultThumbnail);
                        info.setTitle(title);
                        finalInfos.add(info);
                    }
                    GalleryActivity.newInstance(activity, finalInfos, roomId, title);
                } else {
                    pollingImageList(activity, roomId, title, defaultThumbnail, pageIndex + 1,finalInfos);
                }
            }
        };
        listener.setShowLoading(true);
        RequestHelper.getRequest(activity, urlBuilder.toString(), listener, params);
    }
}
