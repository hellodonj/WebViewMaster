package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.Request;
import com.galaxyschool.app.wawaschool.HomeActivity;
import com.galaxyschool.app.wawaschool.LqCourseSelectActivity;
import com.galaxyschool.app.wawaschool.NocMyWorkActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.CallbackListener;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.common.UploadUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.common.WawaCourseUtils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.lqwawa.lqbaselib.net.ThisStringRequest;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.MediaInfo;
import com.galaxyschool.app.wawaschool.pojo.NocUserInfo;
import com.galaxyschool.app.wawaschool.pojo.UploadParameter;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseUploadResult;
import com.galaxyschool.app.wawaschool.pojo.weike.LocalCourseInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.ShortCourseInfo;
import com.galaxyschool.app.wawaschool.views.WheelPopupView;
import com.lqwawa.apps.views.ContainsEmojiEditText;
import com.lqwawa.client.pojo.MediaType;
import com.lqwawa.tools.FileZipHelper;
import com.oosic.apps.iemaker.base.BaseUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NocResourceInfoFragment extends ContactsListFragment {
    public static final int REQUESE_CODE_RESOURCE = 2001;
    public static final String TAG = NocResourceInfoFragment.class.getSimpleName();
    private TextView joinGroupView;
    private TextView joinSubjectView;
    private TextView resourceNameView;
    private int groupIndex = 0;
    private int subjectIndex = 0;
    private boolean hasSelectData=false;
    private ImageView selectResourceBtn;
    private NocUserInfo nocUserInfo;
    private Object nocData;
    private int dataType;//本地 （lq云坂） 、远端（Lq课件）
    private ContainsEmojiEditText remarkView;
    public static final int BACK_NOC_MY_WORK_PAGE=1;
    public static final int BACK_NOC_ALL_WORKS_PAGE=2;
    public static int backArgs=BACK_NOC_MY_WORK_PAGE;
    private TextView categoryContentView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_noc_resource_info, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void init() {
        getIntent();
        initViews();
    }
    private void  getIntent(){
        Intent intent = getActivity().getIntent();
        nocUserInfo=(NocUserInfo)intent.getSerializableExtra(NocUserInfo.class.getSimpleName());
        System.out.print(nocUserInfo);
    }

    private void initViews() {
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        textView.setText(R.string.join_game);
        findViewById(R.id.join_group_layout).setOnClickListener(this);
        findViewById(R.id.cancel_view).setOnClickListener(this);
        findViewById(R.id.sure_view).setOnClickListener(this);
        findViewById(R.id.join_subject_layout).setOnClickListener(this);

        joinGroupView = (TextView) findViewById(R.id.join_group_content);
        joinSubjectView = (TextView) findViewById(R.id.join_subject_view);
        selectResourceBtn= (ImageView) findViewById(R.id.select_resource_btn);
        selectResourceBtn.setSelected(hasSelectData);
        selectResourceBtn.setOnClickListener(this);
        resourceNameView = (TextView) findViewById(R.id.select_resource_name);
        remarkView = (ContainsEmojiEditText) findViewById(R.id.remark_view);//备注
        categoryContentView = (TextView) findViewById(R.id.category_content_view);
    }

   @Override
   public void onClick(View v) {
      super.onClick(v);
       switch (v.getId()){
           case R.id.join_group_layout:
               joinGroup(v);
               break;
           case R.id.join_subject_layout:
               joinSubject(v);
               break;
           case R.id.select_resource_btn:
               selectResource();
               break;
           case R.id.sure_view:
               upload();
               break;
           case R.id.cancel_view:
               finish();
               break;

       }
   }
    private void upload(){
        if(nocData==null){
            TipMsgHelper.ShowLMsg(getActivity(), R.string.no_file_select);
            return;
        }else if(TextUtils.isEmpty(remarkView.getText().toString().trim())){
            TipsHelper.showToast(getActivity(),getString(R.string.please_finish_info));
            return;
        }
        if(dataType==NocLqselectFragment.DATA_TYPE_LOCAL){
            uploadToLqCourse();
        }else{
            CourseData courseData=(CourseData)nocData;
            showLoadingDialog();
            uploadNoc(courseData);
        }
    }
    private void selectResource(){
        if(hasSelectData){
            hasSelectData=false;
            selectResourceBtn.setSelected(hasSelectData);
            resourceNameView.setText("");
            nocData=null;
        }else{
            Intent intent=new Intent(getActivity(), LqCourseSelectActivity.class);
            startActivityForResult(intent,REQUESE_CODE_RESOURCE);
        }
        selectResourceBtn.setSelected(hasSelectData);
    }
    private void joinGroup(View v){
        UIUtils.hideSoftKeyboard(getActivity());
        String [] strings=getResources().getStringArray(R.array.noc_group_types);
        WheelPopupView    wheelPopupView = new WheelPopupView(getActivity(),
                groupIndex, new WheelPopupView.OnSelectChangeListener() {
            @Override
            public void onSelectChange(int index, String relationType) {
                groupIndex = index;
                joinGroupView.setText(relationType);
            }
        }, strings);
        wheelPopupView.showAtLocation(v, Gravity.BOTTOM, 0, 0);
    }

    private void joinSubject(View v){
        UIUtils.hideSoftKeyboard(getActivity());
        String [] strings=getResources().getStringArray(R.array.noc_subject_types);
        WheelPopupView    wheelPopupView = new WheelPopupView(getActivity(),
                subjectIndex, new WheelPopupView.OnSelectChangeListener() {
            @Override
            public void onSelectChange(int index, String relationType) {
                subjectIndex = index;
                joinSubjectView.setText(relationType);
            }
        }, strings);
        wheelPopupView.showAtLocation(v, Gravity.BOTTOM, 0, 0);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUESE_CODE_RESOURCE&&resultCode== Activity.RESULT_OK){
             dataType=data.getIntExtra("Data_Type",0);
            if(dataType==NocLqselectFragment.DATA_TYPE_REMOTE){
                nocData= data.getSerializableExtra("NOC_CourseData");
                if(nocData!=null){
                    hasSelectData=true;
                    selectResourceBtn.setSelected(hasSelectData);
                    resourceNameView.setText(((CourseData)nocData).getNickname());
                    if(((CourseData)nocData).type==3){
                        categoryContentView.setText(getString(R.string.noc_ctp_programme));
                    }else{
                        categoryContentView.setText(getString(R.string.noc_csp_programme));
                    }
                }
            }else{
                nocData= data.getSerializableExtra("NOC_CourseData");
                if(nocData!=null){
                    hasSelectData=true;
                    selectResourceBtn.setSelected(hasSelectData);
                    resourceNameView.setText(((LocalCourseInfo)nocData).mTitle);
                    categoryContentView.setText(getString(R.string.noc_csp_programme));

                }
            }
        }
    }

    private MediaInfo localCourseInfo2MediaInfo( LocalCourseInfo data){
        if(data==null){
            return null;
        }
        MediaInfo mediaInfo = new MediaInfo();
        mediaInfo.setPath(data.mPath);
        mediaInfo.setTitle(data.mTitle);
        mediaInfo.setThumbnail(BaseUtils.joinFilePath(data.mPath,
                BaseUtils.RECORD_HEAD_IMAGE_NAME));
        mediaInfo.setMediaType(MediaType.ONE_PAGE);
        mediaInfo.setMicroId(String.valueOf(data.mMicroId));
        mediaInfo.setLocalCourseInfo(data);
        mediaInfo.setDescription(data.mDescription);
        return mediaInfo;
    }
    private void uploadToLqCourse(){
        if (nocData != null) {
            LocalCourseInfo data=(LocalCourseInfo)nocData;
           final MediaInfo mediaInfo= localCourseInfo2MediaInfo(data);
            final UserInfo userInfo=getUserInfo();
            if(!TextUtils.isEmpty(mediaInfo.getMicroId()) && Long.parseLong(mediaInfo.getMicroId()) > 0) {
                WawaCourseUtils wawaCourseUtils = new WawaCourseUtils(getActivity());
                wawaCourseUtils.loadCourseDetail(mediaInfo.getMicroId());
                wawaCourseUtils.setOnCourseDetailFinishListener(new WawaCourseUtils.OnCourseDetailFinishListener() {
                    @Override
                    public void onCourseDetailFinish(CourseData courseData) {
                        if (courseData != null && !TextUtils.isEmpty(courseData.resourceurl)) {
                            mediaInfo.setResourceUrl(courseData.resourceurl);
                            uploadCourseToServer(userInfo, mediaInfo);
                        }
                    }
                });
            } else {
                uploadCourseToServer(userInfo, mediaInfo);
            }
        }
    }
    private void uploadCourseToServer(final UserInfo userInfo, final MediaInfo mediaInfo) {
        final UploadParameter uploadParameter = UploadUtils.getUploadParameter(userInfo, mediaInfo, 1);
        if (uploadParameter != null) {
            showLoadingDialog();
            FileZipHelper.ZipUnzipParam param = new FileZipHelper.ZipUnzipParam(
                    mediaInfo.getPath(), Utils.TEMP_FOLDER + Utils.getFileNameFromPath(
                    mediaInfo.getPath()) + Utils.COURSE_SUFFIX);
            FileZipHelper.zip(param,
                    new FileZipHelper.ZipUnzipFileListener() {
                        @Override
                        public void onFinish(
                                FileZipHelper.ZipUnzipResult result) {
                            if (result != null && result.mIsOk) {
                                uploadParameter.setZipFilePath(result.mParam.mOutputPath);
                                UploadUtils.uploadResource(getActivity(), uploadParameter, new CallbackListener() {
                                    @Override
                                    public void onBack(Object result) {
                                        dismissLoadingDialog();
                                        if (result != null ) {
                                            CourseUploadResult uploadResult = (CourseUploadResult) result;
                                            if (uploadResult != null && uploadResult.code == 0){
                                                if (uploadResult.data != null && uploadResult.data.size() > 0) {
                                                    CourseData courseData = uploadResult.data.get(0);
                                                    if (courseData != null) {
                                                        uploadNoc(courseData);
                                                        updateMediaInfo(getActivity(), getUserInfo(),
                                                                uploadResult.getShortCourseInfoList(),
                                                                MediaType.ONE_PAGE);
                                                    }
                                                }
                                            }else {
                                                if(getActivity() != null) {
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                           TipMsgHelper.ShowLMsg(getContext(),
                                                                   getString(R.string.upload_failure));
                                                        }
                                                    });
                                                }

                                            }

                                        }
                                    }
                                });
                            }
                        }
                    });
        }
    }
    public void updateMediaInfo(final Activity activity, UserInfo userInfo,
                                List<ShortCourseInfo> shortCourseInfos, final int mediaType) {
        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
            TipMsgHelper.ShowLMsg(activity, R.string.pls_login);
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", userInfo.getMemberId());
        params.put("MType", String.valueOf(mediaType));
        params.put("MaterialList", shortCourseInfos);
        RequestHelper.sendPostRequest(activity, ServerUrl.PR_UPLOAD_WAWAWEIKE_URL,
                params,
                new RequestHelper.RequestDataResultListener<DataModelResult>(
                        activity, DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()) {
                            return;
                        }
//                        if(mediaType != com.lqwawa.client.pojo.MediaType.FLIPPED_CLASSROOM) {
//                            TipMsgHelper.ShowLMsg(activity, R.string.upload_file_sucess);
//                        }
                    }
                });
    }



    //上传报名信息
    private void uploadNoc(CourseData courseData) {
        JSONObject jsonObject = new JSONObject();
        if(nocUserInfo!=null) {
            try {
                jsonObject.put("realName", URLEncoder.encode( nocUserInfo.getUsername(), "utf-8"));
                jsonObject.put("mobile", nocUserInfo.getUserPhone());
                jsonObject.put("type", nocUserInfo.getJoinNameFor());

                jsonObject.put("orgName", URLEncoder.encode(nocUserInfo.getSchoolName(), "utf-8"));
                jsonObject.put("schoolId", nocUserInfo.getSchoolId());
                jsonObject.put("address", URLEncoder.encode(nocUserInfo.getAddress(), "utf-8"));
                jsonObject.put("resId", courseData.id);
                String remark=URLEncoder.encode(remarkView.getText().toString().trim(), "utf-8");
                remark= remark.replaceAll("%0A","\n");
                jsonObject.put("remark", remark);
                //学科
                jsonObject.put("subject",subjectIndex+1 );
                //组别
                jsonObject.put("groupType", groupIndex+1 );
                jsonObject.put("resType", courseData.type);
                if(courseData!=null&&courseData.type==3){
                    jsonObject.put("category", 2);
                }else{
                    jsonObject.put("category", 1);
                }
                jsonObject.put("memberId", getMemeberId());
            } catch (JSONException e) {
                e.printStackTrace();
            }catch (UnsupportedEncodingException e){
                e.printStackTrace();
            }
        }
        StringBuilder builder = new StringBuilder();
        builder.append("?j=" + jsonObject.toString());
        ThisStringRequest request = new ThisStringRequest(
                Request.Method.GET, ServerUrl.NOC_SIGN_UP + builder.toString(), new Listener<String>() {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                parseData(jsonString);
            }

            @Override
            public void onError(NetroidError error) {
                if (getActivity() == null) {
                    return;
                }
                TipMsgHelper.ShowMsg(getActivity(), getString(R.string.network_error));
            }

            @Override
            public void onFinish() {
                if (getActivity() == null) {
                    return;
                }
                dismissLoadingDialog();
                super.onFinish();
            }
        });
        request.addHeader("Accept-Encoding", "*");
        request.start(getActivity());
    }

    private void back2MyWork(){
        Class<?> cls=null;
        if(backArgs==BACK_NOC_MY_WORK_PAGE){
            cls=NocMyWorkActivity.class;
            NocMyWorkFragment.freshData=true;
        }else{
            cls=HomeActivity.class;
            NocWorksFragment.freshData=true;
        }
        Intent intent =new Intent(getActivity(),cls );
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    private void parseData(String jsonString) {
        if (jsonString != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                if (jsonObject != null) {
                    int code = jsonObject.optInt("code");
                    if (code == 0) {
                        TipMsgHelper.ShowLMsg(getActivity(),getString(R.string.noc_join_success));
                        back2MyWork();
                    }else{
                        TipMsgHelper.ShowLMsg(getActivity(),getString(R.string.noc_join_failure));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
