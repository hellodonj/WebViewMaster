package com.galaxyschool.app.wawaschool.common;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.Request;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.lqwawa.lqbaselib.net.ThisStringRequest;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.SubscribeSchoolListResult;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckResPermissionHelper {
    private Activity activity;
    private String memberId;
    private String sourceTag;
    private int resType;
    private String courseId;
    private String schoolId;
    private String collectionSchoolId;
    private String shareUrl;
    private String parentId;
    private boolean isSplitCourse;
    private CheckResourceResultListener checkListener;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            //检查精品资源库的权限
            checkChoiceResourcePermission();
            return false;
        }
    });

    public CheckResPermissionHelper(Activity activity) {
        this.activity = activity;
    }

    public CheckResPermissionHelper setMemberId(String memberId) {
        this.memberId = memberId;
        return this;
    }

    public CheckResPermissionHelper setCouseId(String courseId) {
        this.courseId = courseId;
        return this;
    }

    public CheckResPermissionHelper setSourceTag(String sourceTag) {
        this.sourceTag = sourceTag;
        return this;
    }

    public CheckResPermissionHelper setResType(int resType) {
        this.resType = resType;
        return this;
    }

    public CheckResPermissionHelper setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
        return this;
    }

    public CheckResPermissionHelper setParentId(String parentId) {
        this.parentId = parentId;
        return this;
    }

    public CheckResPermissionHelper setCollectionId(String collectionSchoolId) {
        this.collectionSchoolId = collectionSchoolId;
        return this;
    }

    public CheckResPermissionHelper setCheckListener(CheckResourceResultListener listener) {
        this.checkListener = listener;
        return this;
    }

    public void checkResource() {
        if (!TextUtils.isEmpty(memberId) && !TextUtils.isEmpty(courseId)) {
            //lq课件的分页
            if (!TextUtils.isEmpty(parentId)) {
                isSplitCourse = true;
                int type;
                if (resType > 10000) {
                    type = resType % ResType.RES_TYPE_BASE;
                } else {
                    type = resType;
                }
                parentId = parentId + "-" + type;
            } else {
                if (resType % ResType.RES_TYPE_BASE == ResType.RES_TYPE_STUDY_CARD) {
                    //这里判断是否是任务单的分页
                    String[] splitArray = courseId.split("-");
                    if ((Integer.valueOf(splitArray[1]) != ResType.RES_TYPE_STUDY_CARD) && Integer
                            .valueOf(splitArray[1]) != (ResType.RES_TYPE_BASE + ResType.RES_TYPE_STUDY_CARD)) {
                        isSplitCourse = true;
                        parentId = splitArray[0] + "-" + ResType.RES_TYPE_STUDY_CARD;
                    }
                }
            }
            loadSchools();
        }
    }
    //============================================================================================
    /**
     * 分析扫码识别出来的资源时否含有打开的权限
     *
     * @return
     */
    private void analysisCoursePermissions(String schoolId) {
        this.schoolId = schoolId;
        //如果collectionSchoolId 不为空表示查询收藏的资源权限
        if (TextUtils.isEmpty(collectionSchoolId)){
            checkLqwawaCoursePermission(schoolId, true);
        }else {
            checkLqwawaCoursePermission(schoolId,false);
        }
    }

    private void loadSchools() {
        Map<String, Object> params = new HashMap();
        params.put("MemberId", memberId);
        RequestHelper.RequestListener listener =
                new RequestHelper.RequestModelResultListener<SubscribeSchoolListResult>(
                        activity, SubscribeSchoolListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        super.onSuccess(jsonString);
                        SubscribeSchoolListResult result = getResult();
                        StringBuilder builder = new StringBuilder();
                        List<String> joinSchoolIds = new ArrayList<>();
                        List<SchoolInfo> schoolInfoList = result.getModel().getSubscribeNoList();
                        if (schoolInfoList != null && schoolInfoList.size() > 0) {
                            for (int i = 0; i < schoolInfoList.size(); i++) {
                                SchoolInfo info = schoolInfoList.get(i);
                                if (info.hasJoinedSchool()) {
                                    joinSchoolIds.add(info.getSchoolId());
                                    if (i == 0) {
                                        builder.append(info.getSchoolId());
                                    } else {
                                        builder.append(",").append(info.getSchoolId());
                                    }
                                }
                            }
                        }
                        //分析权限的信息
                        String allSchoolId = builder.toString();
                        if (TextUtils.isEmpty(allSchoolId)) {
                            //表示没有加入的机构 校验学程的接口有没有权限
                            checkLqmoocCoursePermission(allSchoolId);
                        } else {
                            if (TextUtils.isEmpty(collectionSchoolId)) {
                                analysisCoursePermissions(allSchoolId);
                            } else {
                                //校验收藏的资源权限
                                boolean flag = false;
                                String[] splitArray = null;
                                if (collectionSchoolId.contains(",")) {
                                    splitArray = collectionSchoolId.split(",");
                                } else {
                                    splitArray = new String[]{collectionSchoolId};
                                }
                                for (int i = 0, len = joinSchoolIds.size(); i < len; i++) {
                                    String schoolId = joinSchoolIds.get(i);
                                    for (int j = 0; j < splitArray.length; j++) {
                                        String collectId = splitArray[j];
                                        if (TextUtils.equals(schoolId.toLowerCase(), collectId.toLowerCase())) {
                                            flag = true;
                                        }
                                    }
                                }
                                if (flag) {
                                    analysisCoursePermissions(collectionSchoolId);
                                } else {
                                    checkListener.onCheckResult(resType, courseId, false);
                                }
                            }
                        }
                    }
                };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(activity, ServerUrl.SUBSCRIBE_SCHOOL_LIST_URL, params, listener);
    }

    /**
     * 检测lqwawa中资源的权限
     */
    private void checkLqwawaCoursePermission(String schoolId, final boolean checkTag) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("SchoolIds", schoolId);
        if (isSplitCourse) {
            params.put("ResId", parentId);
        } else {
            params.put("ResId", courseId);
        }
        params.put("MemberId", memberId);
        final RequestHelper.RequestListener listener =
                new RequestHelper.RequestModelResultListener<ModelResult>(
                        activity, ModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(jsonString);
                            if (jsonObject != null) {
                                boolean flag = jsonObject.optBoolean("Model");
                                if (flag) {
                                    checkListener.onCheckResult(resType, courseId, flag);
                                } else {
                                    if (checkTag) {
                                        mHandler.sendEmptyMessage(0);
                                    } else {
                                        checkListener.onCheckResult(resType, courseId, flag);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(activity, ServerUrl.OPEN_LQWAWA_COURSE_PERMISSION_BASE_URL, params,
                listener);
    }

    /**
     * 检测lqmooc中资源的权限
     */
    private void checkLqmoocCoursePermission(String schoolId) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("schoolIds", schoolId);
            if (isSplitCourse) {
                jsonObject.put("resId", parentId);
            } else {
                jsonObject.put("resId", courseId);
            }
            jsonObject.put("memberId", memberId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final StringBuilder builder = new StringBuilder();
        builder.append("?j=" + jsonObject.toString());
        String url = ServerUrl.OPEN_LQMOOC_COURSE_PERMISSION_BASE_URL + builder.toString();
        ThisStringRequest request = new ThisStringRequest(
                Request.Method.GET, url, new Listener<String>() {
            @Override
            public void onSuccess(String jsonString) {
                if (!TextUtils.isEmpty(jsonString)) {
                    try {
                        JSONObject jsonObject = new JSONObject(jsonString);
                        if (jsonObject != null) {
                            boolean flag = jsonObject.optBoolean("havePermission");
                            checkListener.onCheckResult(resType, courseId, flag);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        request.addHeader("Accept-Encoding", "*");
        request.start(activity);
    }

    /**
     * 检查是否有精品资源库的权限
     */
    private void checkChoiceResourcePermission(){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("SchoolIds", schoolId);
        if (isSplitCourse) {
            params.put("ResId", parentId);
        } else {
            params.put("ResId", courseId);
        }
        params.put("MemberId", memberId);
        final RequestHelper.RequestListener listener =
                new RequestHelper.RequestModelResultListener<ModelResult>(
                        activity, ModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(jsonString);
                            if (jsonObject != null) {
                                boolean flag = jsonObject.optBoolean("Model");
                                if (flag) {
                                    checkListener.onCheckResult(resType, courseId, flag);
                                } else {
                                    checkLqmoocCoursePermission(schoolId);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(activity, ServerUrl.CHECK_AUTH_LIB_PERMISSION_BASE_URL, params,
                listener);
    }

    public interface CheckResourceResultListener {
        void onCheckResult(int resType, String courseId, boolean isPublicResource);
    }
}
