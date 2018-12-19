package com.galaxyschool.app.wawaschool.course;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.Request;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.MyAttendedSchoolListActivity;
import com.galaxyschool.app.wawaschool.Note.OnlineMediaPaperActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.ShellActivity;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.CommitCourseHelper;
import com.galaxyschool.app.wawaschool.common.CommitHelper;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.db.LocalCourseDao;
import com.galaxyschool.app.wawaschool.db.dto.LocalCourseDTO;
import com.galaxyschool.app.wawaschool.fragment.MediaListFragment;
import com.galaxyschool.app.wawaschool.fragment.MediaTypeListFragment;
import com.lqwawa.lqbaselib.net.NetErrorResult;
import com.lqwawa.lqbaselib.net.ThisStringRequest;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseType;
import com.galaxyschool.app.wawaschool.pojo.weike.LocalCourseInfo;
import com.galaxyschool.app.wawaschool.slide.SlideManagerHornForPhone;
import com.lqwawa.client.pojo.MediaType;
import com.lqwawa.client.pojo.ResourceInfo;
import com.oosic.apps.aidl.CollectParams;
import com.oosic.apps.iemaker.base.PlaybackActivity;
import com.oosic.apps.iemaker.base.SlideManager;
import com.umeng.socialize.UMShareAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaybackActivityNew extends PlaybackActivity implements CommitHelper.NoteCommitListener{
    private CommitCourseHelper commitCourseHelper;
    //制作的文件路径
    private String filePath;
    public static String COURSETYPEFORM = "courseTypeFrom";
    private int courseType = -1;
    public interface  CourseTypeFrom{
        int Do_Read_Course = 100;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if(intent != null){
            courseType = intent.getIntExtra(COURSETYPEFORM,-1);
        }
    }
    @Override
    public void deleteCourse(String path) {
        if (courseType == CourseTypeFrom.Do_Read_Course) {
            UserInfo userInfo = DemoApplication.getInstance().getUserInfo();
            if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
                ActivityUtils.enterLogin(PlaybackActivityNew.this, false);
                return;
            }
            if (path.endsWith(File.separator)) {
                path = path.substring(0, path.length() - 1);
            }
            LocalCourseDTO dto = LocalCourseDTO.getLocalCourse(PlaybackActivityNew.this, userInfo.getMemberId(),
                    path);
            if (dto != null) {
                LocalCourseDTO.deleteLocalCourseByPath(PlaybackActivityNew.this, userInfo.getMemberId(), path, true);
            } else {
                Utils.safeDeleteDirectory(path);
            }
        }
    }

    @Override
    public void saveCourse(String path, String title, String description) {
        UserInfo userInfo = DemoApplication.getInstance().getUserInfo();
//        String newPath = BaseUtils.joinFilePath(new File(path).getParent(), title);
//        LocalCourseDTO dto = new LocalCourseDTO();
//        dto.setPath(newPath);
//        int rtn = LocalCourseDTO.updateLocalCourse(this, userInfo.getMemberId(),
//                path, dto);
//        if(rtn > 0) {
//            File oldFile = new File(path);
//            File newFile = new File(newPath);
//            if(oldFile != null && newFile != null) {
//                oldFile.renameTo(newFile);
//            }
//        }
        saveCourse(path,title,description, 0, System.currentTimeMillis(), userInfo);
        TipMsgHelper.ShowMsg(PlaybackActivityNew.this,getString(R.string.lqcourse_save_local));
        Intent  intent = new Intent();
        intent.putExtra(SlideManagerHornForPhone.SAVE_PATH,path);
        setResult(Activity.RESULT_OK,intent);
        finish();
    }
    @Override
    public void sendCourse(String slidePath, String coursePath, String title, String description,
                           SlideManager.MoreParams moreParams) {
        UIUtils.hideSoftKeyboard(PlaybackActivityNew.this);
        UserInfo userInfo = DemoApplication.getInstance().getUserInfo();
//        String newPath = BaseUtils.joinFilePath(new File(coursePath).getParent(), title);
//        LocalCourseDTO dto = new LocalCourseDTO();
//        dto.setPath(newPath);
//        int rtn = LocalCourseDTO.updateLocalCourse(this, userInfo.getMemberId(),
//                coursePath, dto);
//        if(rtn > 0) {
//            File oldFile = new File(coursePath);
//            File newFile = new File(newPath);
//            if(oldFile != null && newFile != null) {
//                oldFile.renameTo(newFile);
//            }
//        }
        filePath = coursePath;
//        if (!coursePath.equals(newPath)){
//            filePath = newPath;
//            coursePath = newPath;
//        }
        //发送相关的代码
        saveCourse(coursePath, title, description, 0, System.currentTimeMillis(), userInfo);
        if (commitCourseHelper == null) {
            commitCourseHelper = new CommitCourseHelper(PlaybackActivityNew.this);
        }
        if (userInfo != null && userInfo.isTeacher()) {
            commitCourseHelper.setIsTeacher(true);
        }
        if (userInfo != null && userInfo.isStudent()) {
            commitCourseHelper.setIsStudent(true);
        }
        commitCourseHelper.setIsLocal(true);
        commitCourseHelper.setCourseTypeFrom(SlideActivityNew.CourseTypeFrom.FROMLQCOURSE);
        commitCourseHelper.commit(getWindow().getDecorView(), null);
        commitCourseHelper.setNoteCommitListener(PlaybackActivityNew.this);
    }

    public boolean saveCourse(String path, String title, String description, long duration,
                              long lastView, UserInfo userInfo) {
        boolean rtn = false;
        if (path == null) {
            return false;
        }
        if (path.endsWith(File.separator)) {
            path = path.substring(0, path.length() - 1);
        }
        String parent = new File(path).getParentFile().getPath();

        if (parent.endsWith(File.separator)) {
            parent = parent.substring(0, parent.length() - 1);
        }
        LocalCourseInfo info = new LocalCourseInfo(
                path, parent, duration,
                System.currentTimeMillis(), CourseType.COURSE_TYPE_LOCAL, null, description);
        info.mParentPath = parent;
        info.mOrientation = mOrientation;
        info.mTitle = title;
        try {
            LocalCourseDao localCourseDao = new LocalCourseDao(PlaybackActivityNew.this);
            List<LocalCourseDTO> localCourseDTOs = localCourseDao.getLocalCourseByPath(userInfo.getMemberId(),
                    path);
            if (localCourseDTOs != null && localCourseDTOs.size() > 0) {
                localCourseDao.updateLocalCourse(userInfo.getMemberId(), path, info);
            } else {
                LocalCourseDTO dao = info.toLocalCourseDTO();
                dao.setmMemberId(userInfo.getMemberId());
                localCourseDao.addOrUpdateLocalCourseDTO(dao);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rtn;
    }
    @Override
    public void noteCommit(int shareType) {
        UserInfo userInfo = DemoApplication.getInstance().getUserInfo();
        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
            TipMsgHelper.ShowLMsg(this, R.string.pls_login);
            return;
        }
        if (filePath.endsWith(File.separator)) {
            filePath = filePath.substring(0, filePath.length() - 1);
        }
        final LocalCourseDTO data = LocalCourseDTO.getLocalCourse(PlaybackActivityNew.this,
                userInfo.getMemberId(), filePath);
        if (data == null) {
            return;
        }
        accordingConditionToShare(data.toLocalCourseInfo(),userInfo,shareType);
    }
    private void accordingConditionToShare(LocalCourseInfo localCourseInfo,UserInfo userInfo,int
            mShareType){
        //更新MicroId
        String path = null;
        if(localCourseInfo != null) {
            path = localCourseInfo.mPath;
            if(!TextUtils.isEmpty(path) && path.endsWith(File.separator)) {
                path = path.substring(0, path.length() - 1);
            }
            LocalCourseDTO dto = LocalCourseDTO.getLocalCourse(PlaybackActivityNew.this,userInfo
                    .getMemberId(), path);
            if (dto != null && dto.getmMicroId() > 0) {
                localCourseInfo.mMicroId = dto.getmMicroId();
            }
            commitCourseHelper.setIsTempData(true);
            commitCourseHelper.uploadCourse(userInfo, localCourseInfo, null, mShareType);
        }
    }
    @Override
    protected void pickSpaceMaterial(int spaceType) {
        if (spaceType == 0) {
            //从个人资源库选取
            pickPersonalMaterial();
        } else if (spaceType == 1){
            //从公共资源库选取
        } else if (spaceType == 2) {
            //从校本资源库选取
            pickSchoolMaterial();
        }
    }

    /**
     * 从个人素材库选取图片，音频，视频，PDF， PPT
     */
    private void pickPersonalMaterial() {
        UserInfo userInfo = ((MyApplication)getApplication()).getUserInfo();
        if(userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
            TipMsgHelper.ShowLMsg(PlaybackActivityNew.this, R.string.pls_login);
            ActivityUtils.enterLogin(PlaybackActivityNew.this);
            return;
        }
        Intent intent = new Intent();
        intent.setClass(PlaybackActivityNew.this, ShellActivity.class);
        intent.putExtra("Window", "media_type_list");
        intent.putExtra(MediaTypeListFragment.EXTRA_IS_REMOTE, true);
        intent.putExtra(MediaListFragment.EXTRA_IS_PICK, true);
        intent.putExtra(ShellActivity.EXTRA_ORIENTAION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //0 landscape, 1 portrait
        intent.putExtra(MediaListFragment.EXTRA_IS_PICK, true);
        intent.putExtra(MediaTypeListFragment.EXTRA_IS_REMOTE, true);
        //过滤资源条目显示
        ArrayList<Integer> mediaTypes = new ArrayList<Integer>();
        mediaTypes.add(MediaType.PICTURE);
//        mediaTypes.add(MediaType.VIDEO);
//        mediaTypes.add(MediaType.AUDIO);
        mediaTypes.add(MediaType.PPT);
        mediaTypes.add(MediaType.PDF);
        mediaTypes.add(MediaType.DOC);
        intent.putIntegerArrayListExtra(MediaListFragment.EXTRA_SHOW_MEDIA_TYPES,
                mediaTypes);
        startActivityForResult(intent, SlideManager.REQUEST_PICK_SPACE_MATERAIL);
    }


    /**
     * 从校本资源库选取素材
     */
    private void pickSchoolMaterial() {
        Intent intent = new Intent();
        intent.setClass(PlaybackActivityNew.this, MyAttendedSchoolListActivity.class);
        intent.putExtra(ActivityUtils.EXTRA_IS_PICK, true);
        intent.putExtra(ActivityUtils.EXTRA_IS_PICK_SCHOOL_RESOURCE, true);
        ArrayList<Integer> mediaTypes = new ArrayList<Integer>();
        mediaTypes.add(MediaType.SCHOOL_PICTURE);
//        mediaTypes.add(MediaType.SCHOOL_AUDIO);
//        mediaTypes.add(MediaType.SCHOOL_VIDEO);
        mediaTypes.add(MediaType.SCHOOL_PPT);
        mediaTypes.add(MediaType.SCHOOL_PDF);
        mediaTypes.add(MediaType.SCHOOL_DOC);
        intent.putIntegerArrayListExtra(MediaListFragment.EXTRA_SHOW_MEDIA_TYPES,
                mediaTypes);
        startActivityForResult(intent, SlideManager.REQUEST_PICK_SPACE_MATERAIL);
    }

    @Override
    protected void collectCourse(CollectParams params) {
        UserInfo userInfo = ((MyApplication) getApplication()).getUserInfo();
        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
            TipMsgHelper.ShowMsg(PlaybackActivityNew.this, getString(R.string.pls_login));
            return;
        }
        Map<String, Object> mParams = new HashMap();
        mParams.put("MemberId", userInfo.getMemberId());
        mParams.put("MicroID", params.getMicroId() + "-" + params.getResourceType());
        mParams.put("Title", params.getTitle());
        mParams.put("Author", params.getAuthor());
        if (!params.isPublicRes){
            String collectSchoolId = params.CollectionOrigin;
            if (TextUtils.isEmpty(collectSchoolId)){
                String schoolId = DemoApplication.getInstance().getPrefsManager().getLatestSchool
                            (this,userInfo.getMemberId());
                mParams.put("CollectionOrigin",schoolId);
            }else {
                mParams.put("CollectionOrigin",collectSchoolId);
            }
        }
        mParams.put("IsQualityCourse",params.isQualityCourse());
        RequestHelper.sendPostRequest(PlaybackActivityNew.this, ServerUrl.COLLECT_RESOURCE_URL,
                mParams, new RequestHelper.RequestDataResultListener<DataModelResult>(PlaybackActivityNew.this, DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        super.onSuccess(jsonString);
                        DataModelResult result = getResult();
                        if (result != null && result.isSuccess()) {
//                            TipMsgHelper.ShowLMsg(CourseActionService.this, R.string.collect_success);
                            //设置收藏标志位
                            OnlineMediaPaperActivity.setHasMicroCourseCollected(true);
                            TipMsgHelper.ShowLMsg(PlaybackActivityNew.this, R.string.collect_to_lq_course);
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
                                    TipMsgHelper.ShowMsg(PlaybackActivityNew.this, result.getErrorMessage());
                                }
                            }
                        } catch (Exception e) {
                            TipMsgHelper.ShowLMsg(PlaybackActivityNew.this, getString(R.string.network_error));
                        }
                    }
                });

    }

    @Override
    protected void praiseCourse(CollectParams params) {
        UserInfo userInfo = ((MyApplication) getApplication()).getUserInfo();
        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
            TipMsgHelper.ShowMsg(PlaybackActivityNew.this, getString(R.string.pls_login));
            return;
        }
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("courseId", String.valueOf(params.microId));
            jsonObject.put("type", 0); //type 0 praise course, 1 praise comment
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
                try {
                    JSONObject dataJsonObject = new JSONObject(jsonString);
                    if (dataJsonObject != null) {

                        int code = dataJsonObject.optInt("code");
                        if (code == 0) {
                            TipMsgHelper.getInstance().showOneTips(PlaybackActivityNew.this, getString(R.string.praise_success));
                            //                            if (type == 0) {
                            int praiseCount = dataJsonObject.optInt("praiseNum");
                            //                                updatePraiseCommentCount(courseId, ResourceType.VIDEO, praiseCount, 0);
                            //                            }
                        } else {
                            TipMsgHelper.getInstance().showOneTips(PlaybackActivityNew.this, getString(R.string.praise_fail));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(NetroidError error) {
                TipMsgHelper.ShowMsg(PlaybackActivityNew.this, getString(R.string.network_error));
            }

        });
        request.addHeader("Accept-Encoding", "*");
        request.start(PlaybackActivityNew.this);
    }

//    @Override
//    protected void shareCourse(SharedResource resource) {
//        UserInfo userInfo = ((MyApplication) getApplication()).getUserInfo();
//        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
//            TipMsgHelper.ShowMsg(PlaybackActivityNew.this, getString(R.string.pls_login));
//            return;
//        }
//        PublishResourceFragment.enterContactsPicker(PlaybackActivityNew.this, resource);
//    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent resultData) {
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, resultData);
        if (resultCode == RESULT_OK) {
            if (requestCode == SlideManager.REQUEST_PICK_SPACE_MATERAIL) {
                List<ResourceInfo> resourceInfos = resultData.getParcelableArrayListExtra("resourseInfoList");
                if (resourceInfos != null && resourceInfos.size() > 0) {
                    for (ResourceInfo info : resourceInfos) {
                        if (info != null) {
                            if (info.getImgPath() != null && !info.getImgPath().startsWith("http")) {
                                info.setImgPath(AppSettings.getFileUrl(info.getImgPath()));
                            }
                            if (info.getResourcePath() != null && !info.getResourcePath().startsWith("http")) {
                                info.setResourcePath(AppSettings.getFileUrl(info.getResourcePath()));
                            }
                        }
                    }
                }
            }else if (requestCode == ActivityUtils.REQUEST_CODE_RETURN_REFRESH) {
                if (resultData != null) {
                    Intent intent = new Intent();
                    String filePath = resultData.getExtras().getString(SlideManagerHornForPhone.SAVE_PATH);
                    intent.putExtra(SlideManagerHornForPhone.SAVE_PATH, filePath);
                    this.setResult(Activity.RESULT_OK, intent);
                    this.finish();
                }
            }
        }
        if (mSlideInPlayback != null) {
            mSlideInPlayback.onActivityResult(requestCode, resultCode, resultData);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //我要加点读放弃时删除copy的资源
        if (courseType == CourseTypeFrom.Do_Read_Course && !changesSaved()){
            deleteCourse(mPath);
        }
    }
}
