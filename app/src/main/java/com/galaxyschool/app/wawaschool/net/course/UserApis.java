package com.galaxyschool.app.wawaschool.net.course;

import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.galaxyschool.app.wawaschool.common.DateUtils;
import com.galaxyschool.app.wawaschool.common.LogUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.UploadParameter;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseUploadResult;
import com.galaxyschool.app.wawaschool.pojo.weike.MediaUploadList;
import com.osastudio.common.utils.Utils;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserApis extends BaseApi {

    public static CourseUploadResult uploadResource(
            final Context context, UploadParameter uploadParameter) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        //fileName 后面拼接 ”_时间戳“
        parameters.put("fileName", uploadParameter.getFileName() + "_" + DateUtils.millSecToDateStr(SystemClock.currentThreadTimeMillis()));
        parameters.put("nickName", uploadParameter.getFileName());
        if(!TextUtils.isEmpty(uploadParameter.getDescription())) {
            parameters.put("description", uploadParameter.getDescription());
        } else {
            parameters.put("description", "");
        }
        parameters.put("size", uploadParameter.getSize());
        parameters.put("type", 5);
        parameters.put("totalTime", uploadParameter.getTotalTime());
        if(!TextUtils.isEmpty(uploadParameter.getKnowledge())) {
            parameters.put("point", uploadParameter.getKnowledge());
        } else {
            parameters.put("point", "");
        }
        if(!TextUtils.isEmpty(uploadParameter.getMemberId())) {
            parameters.put("memberId", uploadParameter.getMemberId());
        } else {
            parameters.put("memberId", "");
        }

        if(!TextUtils.isEmpty(uploadParameter.getAccount())) {
            parameters.put("account", uploadParameter.getAccount());
        } else {
            parameters.put("account", "");
        }
        if(!TextUtils.isEmpty(uploadParameter.getSchoolIds())) {
            parameters.put("schoolIds", uploadParameter.getSchoolIds());
        } else {
            parameters.put("schoolIds", "");
        }
        if(!TextUtils.isEmpty(uploadParameter.getCreateName())) {
            parameters.put("createName", uploadParameter.getCreateName());
        } else {
            parameters.put("createName", "");
        }
        if(uploadParameter.getChannelId() >= 0) {
            parameters.put("channelId", uploadParameter.getChannelId());
        }
        if(!TextUtils.isEmpty(uploadParameter.getColumns())) {
            parameters.put("columns", uploadParameter.getColumns());
        }
        if(uploadParameter.getResId() > 0) {
            parameters.put("resId", uploadParameter.getResId());
        }

        if(uploadParameter.getResType() > 0) {
            parameters.put("resType", uploadParameter.getResType());
        }

        if(uploadParameter.getColType() > 0) {
            parameters.put("colType", uploadParameter.getColType());
        }

        if(uploadParameter.getScreenType() >= 0) {
            parameters.put("screenType", uploadParameter.getScreenType());
        }

        if (uploadParameter.getResType() == ResType.RES_TYPE_COURSE_SPEAKER) {
            parameters.put("needSplit", uploadParameter.isNeedSplit() ? 1 : 0);
        }else {
            parameters.put("needSplit",0);
        }

        parameters.put("clientVersion", Utils.getApplicationStamp(context));

        LogUtils.logi("UserApis", "===>>>uploadUrl = " + uploadParameter.getUploadUrl());

        String param_str = formJSONString(parameters);
        String jsonString = null;
        try {
            jsonString = BaseApi.postFile(
                context, uploadParameter.getUploadUrl(),
                uploadParameter.getZipFilePath(), uploadParameter.getThumbPath(), param_str);

            final CourseUploadResult uploadResult = JSON.parseObject(
                jsonString,
                CourseUploadResult.class);
            if (uploadResult != null) {
                if(uploadResult.code == 0) {
                    List<CourseData> datas = uploadResult.getData();
                    if (datas != null && datas.size() > 0) {
                        CourseData courseData = datas.get(0);
                        if (courseData != null) {
//                            LocalCourseDTO dto = new LocalCourseDTO();
//                            dto.setmMicroId(courseData.id);
//                            String path = uploadParameter.getFilePath();
//                            if (path.endsWith(File.separator)) {
//                                path = path.substring(0, path.length() - 1);
//                            }
//                            LocalCourseDTO.updateLocalCourse((Activity) context, uploadParameter.getMemberId(),
//                                    path, dto);
                            if (!TextUtils.isEmpty(uploadParameter.getZipFilePath())) {
                                com.galaxyschool.app.wawaschool.common.Utils.deleteFile(uploadParameter
                                        .getZipFilePath());
                            }

                        }
                    }
                } else {
                    if(context != null) {
                        if(context instanceof Activity) {
                            ((Activity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TipMsgHelper.ShowLMsg(context, uploadResult.message);
                                }
                            });
                        }
                    }
                }
            }
            return uploadResult;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static MediaUploadList uploadMedia(Context context, UploadParameter uploadParameter) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        if(!TextUtils.isEmpty(uploadParameter.getMemberId())) {
            parameters.put("memberId", uploadParameter.getMemberId());
        } else {
            parameters.put("memberId", "");
        }

        if(!TextUtils.isEmpty(uploadParameter.getCreateName())) {
            parameters.put("createName", uploadParameter.getCreateName());
        } else {
            parameters.put("createName", "");
        }
        if(uploadParameter.getResType() > 0) {
            parameters.put("resType", uploadParameter.getResType());
        }

        if(uploadParameter.getColType() > 0) {
            parameters.put("colType", uploadParameter.getColType());
        }

        List<String> paths = uploadParameter.getPaths();
        if(paths != null && paths.size() > 0) {
            parameters.put("fileNum", paths.size());
        }

        if(!TextUtils.isEmpty(uploadParameter.getFileName())) {
            parameters.put("fileName", uploadParameter.getFileName());
        }

        parameters.put("clientVersion", Utils.getApplicationStamp(context));

        String param_str = formJSONString(parameters);
        String jsonString = null;
        try {
            jsonString = BaseApi.postFile(ServerUrl.UPLOAD_MEDIA_URL,
                uploadParameter.getPaths(), param_str);

            MediaUploadList uploadResult = JSON.parseObject(
                jsonString,
                MediaUploadList.class);
            return uploadResult;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
