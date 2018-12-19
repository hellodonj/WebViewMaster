package com.galaxyschool.app.wawaschool;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.root.robot_pen_sdk.BleConnectActivity;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.CallbackListener;
import com.galaxyschool.app.wawaschool.common.CheckResPermissionHelper;
import com.galaxyschool.app.wawaschool.common.Common;
import com.galaxyschool.app.wawaschool.common.NotificationHelper;
import com.galaxyschool.app.wawaschool.common.StudyTaskOpenHelper;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.common.WawaCourseUtils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.config.VipConfig;
import com.galaxyschool.app.wawaschool.course.DownloadOnePageTask;
import com.galaxyschool.app.wawaschool.db.dto.LocalCourseDTO;
import com.galaxyschool.app.wawaschool.fragment.ActClassroomFragment;
import com.galaxyschool.app.wawaschool.pojo.CourseSectionData;
import com.galaxyschool.app.wawaschool.pojo.CourseSectionDataListResult;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.PerformClassList;
import com.galaxyschool.app.wawaschool.pojo.PerformClassListResult;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskFinishInfo;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskFinishInfoResult;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskInfo;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.PlaybackParam;
import com.galaxyschool.app.wawaschool.pojo.weike.SplitCourseInfo;
import com.galaxyschool.app.wawaschool.slide.CreateSlideHelper;
import com.galaxyschool.app.wawaschool.slide.SlideWawaPageActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.lqwawa.client.pojo.SourceFromType;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.LanguageUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.helper.LQCourseHelper;
import com.lqwawa.intleducation.module.discovery.ui.CourseDetailsActivity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.classifylist.ClassifyListContract;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.CourseFiltrateActivity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.state.GroupFiltrateState;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.lqwawa.zbarlib.ScanActivity;
import com.oosic.apps.iemaker.base.ooshare.ConnectedDevice;
import com.oosic.apps.iemaker.base.ooshare.MyShareManager;
import com.osastudio.common.library.LqBase64Helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CaptureActivity extends ScanActivity {

    public static final String ID_STR = "id=";
    public static final String INPUT_TYPE_STR = "input_type=";

    public static final String WEIKE_STR = "weike";
    public static final String PLAY_STR = "play";
    public static final String TASKID_STR = "taskId";
    // 学程详情的分享
    public static final String COURSE_DETAIL_STR = "courseDetail";
    // 学程标签的分享
    public static final String COURSE_LABEL_STR = "courseLabel";

    public static final String ID_PERFORM="Id=";
    public static final String ASSOCIATED_FIELDS="ShareLqPerformClass";

    public static final String ROBOTPEN_FLAG = "0C";

    private int notificationId = 9999;

    private String schoolId;
    private String classId;
    private boolean isScanTask;
    private String courseSectionDataString ;
    private boolean isPublicRes = true;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        //初始化资源的归属模块
        UIUtils.currentSourceFromType = SourceFromType.OTHER;

        schoolId = getIntent().getStringExtra(SlideWawaPageActivity.SCHOOL_ID);
        classId = getIntent().getStringExtra(SlideWawaPageActivity.CLASS_ID);
        isScanTask = getIntent().getBooleanExtra(SlideWawaPageActivity.IS_SCAN_TASK,false);
    }

    @Override
    public void handleResult(Result result) {
        switchTo(result);
    }

    private void switchTo(Result result) {
        if (result != null) {
            String resultStr = result.getText();
            if (TextUtils.isEmpty(resultStr)) {
                return;
            }
            BarcodeFormat barcodeFormat = result.getBarcodeFormat();
            if (barcodeFormat.equals(BarcodeFormat.QR_CODE)) {
                // QRcode
                switchTo(resultStr);
            } else {
//                if (!resultStr.startsWith(ROBOTPEN_FLAG)) {
//                    TipMsgHelper.ShowMsg(CaptureActivity.this, R.string.barcode_is_not_accept);
//                    finish();
//                    return;
//                }
                // 条形码，扫描智写板条形码直接连接
                BleConnectActivity.start(CaptureActivity.this, resultStr);
                finish();
            }
        }
    }

    private void switchTo(String result) {
        if (!TextUtils.isEmpty(result)) {
            if (result.contains("enc=")) {
                //加密的分享url
                String headUrl = result.substring(0, result.indexOf("?") + 1);
                String bodyUrl = transformResultUrl(result);
                switchTo(headUrl + bodyUrl);
            } else {
                if (result.toLowerCase().contains(ID_STR) && result.toLowerCase().contains(INPUT_TYPE_STR)) {
                    gotoQrcodeProcess(result);
                }
//                else if (result.toLowerCase().contains(MyShareManager.RECEIVER_NICK_NAME
//                        .toLowerCase())) {
////                gotoSmartHubSelect(result);
//                    processSmartHubSelectByUrl(result);
//                }
                else if (result.toLowerCase().contains(WEIKE_STR) /*&& result.toLowerCase().contains
                    (PLAY_STR) && result.toLowerCase().contains(String.valueOf(ResType
                    .RES_TYPE_STUDY_CARD))*/) { //扫描二维码识别学习任务
                    //TODO 快乐学习
                    parseShareUrl(result);
                } else if (result.contains(TASKID_STR)) {
                    //TODO 快乐学习
                    if (!isLogin()) {
                        enterLogin();
                        this.finish();
                        return;
                    }
                    parseTaskIdUrl(result);
                } else if (result.contains(ID_PERFORM) && result.contains(ASSOCIATED_FIELDS)) {
                    if (!isLogin()) {
                        enterLogin();
                        this.finish();
                        return;
                    }
                    enterPerformDetails(result);
                } else if(result.contains(COURSE_DETAIL_STR)){
                    // 学程分享
                    if (!isLogin()) {
                        enterLogin();
                        this.finish();
                        return;
                    }
                    enterCourseDetail(result);
                }else if(result.contains(COURSE_LABEL_STR)){
                    // 学程标签分享
                    if (!isLogin()) {
                        enterLogin();
                        this.finish();
                        return;
                    }
                    enterCourseFiltrateView(result);
                }else{
                    gotoBrowser(result);
                }
            }
        } else {
            Toast.makeText(CaptureActivity.this, R.string.scanning_failed, Toast.LENGTH_LONG).show();
        }
    }

    private String transformResultUrl(String url) {
        int index = url.indexOf("enc=") + 4;
        url = url.substring(index, url.length());
        url = LqBase64Helper.getDecodeString(url);
        return url;
    }

    /**
     * 进入到表演课堂的详情界面
     * @param result
     */
    private void enterPerformDetails(String result){
        String [] dataArray=result.split("[?]");
        final String id=dataArray[1].split("=")[1];
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("Id",Integer.valueOf(id));
        RequestHelper.RequestListener listener =
                new RequestHelper.RequestDataResultListener<PerformClassListResult>(
                        this, PerformClassListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()) {
                            return;
                        }
                        PerformClassListResult result=getResult();
                        List<PerformClassList> list=result.getModel().getData();
                        if (list!=null&&list.size()>0){
                            Intent intent=new Intent(CaptureActivity.this, ActClassroomDetailActivity.class);
                            Bundle bundle=new Bundle();
                            bundle.putInt(ActClassroomFragment.Constants.EXTRA_PERFORM_ID,Integer.valueOf(id));
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }else {
                            TipMsgHelper.ShowMsg(CaptureActivity.this,R.string.resource_no_exist);
                        }
                        finish();

                    }
                };
        RequestHelper.sendPostRequest(this, ServerUrl.GET_ACT_CLASSROOM_DETAIL_DATA_BASE_URL,
                params, listener);
    }

    /**
     * 进入LQ学程标签筛选页面
     * @param result 扫描二维码获取到的内容
     */
    private void enterCourseFiltrateView(@NonNull String result){
        String[] splitStrings = result.split("[?]");
        if(EmptyUtil.isNotEmpty(splitStrings) && splitStrings.length == 2){
            String params = splitStrings[1];
            if(params.contains("&")){
                String[] listParams = params.split("&");
                if(EmptyUtil.isNotEmpty(listParams) && listParams.length == 5){
                    // 获取Level
                    String levelParams = listParams[0];
                    String level = null;
                    if(EmptyUtil.isNotEmpty(levelParams) && levelParams.contains("=")){
                        String[] paramStrings = levelParams.split("=");
                        if(EmptyUtil.isNotEmpty(paramStrings) && paramStrings.length == 2) {
                            if ("level".equals(paramStrings[0])) {
                                level = paramStrings[1];
                            }
                        }

                    }

                    // 获取parentId
                    String parentIdParams = listParams[1];
                    String parentId = null;
                    if(EmptyUtil.isNotEmpty(parentIdParams) && parentIdParams.contains("=")){
                        String[] paramStrings = parentIdParams.split("=");
                        if(EmptyUtil.isNotEmpty(paramStrings) && paramStrings.length == 2) {
                            if ("parentId".equals(paramStrings[0])) {
                                parentId = paramStrings[1];
                            }
                        }

                    }

                    // 获取 paramTwoId
                    String paramTwoIdParams = listParams[2];
                    String paramTwoId = null;
                    if(EmptyUtil.isNotEmpty(paramTwoIdParams) && paramTwoIdParams.contains("=")){
                        String[] paramStrings = paramTwoIdParams.split("=");
                        if(EmptyUtil.isNotEmpty(paramStrings) && paramStrings.length == 2) {
                            if ("paramTwoId".equals(paramStrings[0])) {
                                paramTwoId = paramStrings[1];
                            }
                        }

                    }


                    // 获取 paramThreeId
                    String paramThreeIdParams = listParams[3];
                    String paramThreeId = null;
                    if(EmptyUtil.isNotEmpty(paramThreeIdParams) && paramThreeIdParams.contains("=")){
                        String[] paramStrings = paramThreeIdParams.split("=");
                        if(EmptyUtil.isNotEmpty(paramStrings) && paramStrings.length == 2) {
                            if ("paramThreeId".equals(paramStrings[0])) {
                                paramThreeId = paramStrings[1];
                            }
                        }

                    }

                    // 获取标题
                    String titleParams = listParams[4];
                    String title = null;
                    if(EmptyUtil.isNotEmpty(titleParams) && titleParams.contains("=")){
                        String[] titleStings = titleParams.split("=");
                        if(EmptyUtil.isNotEmpty(titleStings) && titleStings.length == 2) {
                            if ("title".equals(titleStings[0])) {
                                title = titleStings[1];
                            }
                        }
                    }


                    // 非空判断
                    if(EmptyUtil.isEmpty(level) ||
                            EmptyUtil.isEmpty(parentId) ||
                            EmptyUtil.isEmpty(paramTwoId) ||
                            EmptyUtil.isEmpty(paramThreeId) ||
                            EmptyUtil.isEmpty(title)){
                        return;
                    }

                    final int _paramTwoId = Integer.parseInt(paramTwoId);
                    final int _paramThreeId = Integer.parseInt(paramThreeId);
                    final String _title = title;

                    int isZh = LanguageUtil.isZh();
                    // 获取二级标签
                    int _level = 2;
                    int _parentId = Integer.parseInt(parentId);
                    if(level.contains(".")){
                        // 获取二级标签的parentId，获取二级标签列表，然后model跟parentId比较
                        String[] levelStrings = level.split("[\\.]");
                        if(EmptyUtil.isNotEmpty(levelStrings) && levelStrings.length == 2){
                            _parentId = Integer.parseInt(levelStrings[0]);
                        }
                    }

                    final int resultParentId = Integer.parseInt(parentId);
                    LQCourseHelper.requestLQCourseConfigData(isZh, _level, _parentId, new DataSource.Callback<List<LQCourseConfigEntity>>() {
                        @Override
                        public void onDataNotAvailable(int strRes) {
                            UIUtil.showToastSafe(strRes);
                        }

                        @Override
                        public void onDataLoaded(List<LQCourseConfigEntity> entities) {
                            if(EmptyUtil.isNotEmpty(entities)){
                                for (LQCourseConfigEntity entity:entities) {
                                    if(entity.getId() == resultParentId){
                                        // 找到该实体
                                        entity.setParamTwoId(_paramTwoId);
                                        entity.setParamThreeId(_paramThreeId);
                                        entity.setConfigValue(_title);
                                        GroupFiltrateState state = new GroupFiltrateState(entity);
                                        CourseFiltrateActivity.show(CaptureActivity.this,entity,state);
                                        break;
                                    }
                                }
                            }
                        }
                    });

                }
            }
        }
    }

    /**
     * 进入LQ学程课程详情页
     * @param result 扫描二维码获取到的内容
     */
    private void enterCourseDetail(@NonNull String result){
        String[] splitStrings = result.split("[?]");
        if(EmptyUtil.isNotEmpty(splitStrings) && splitStrings.length == 2){
            String params = splitStrings[1];
            if(params.contains("=")){
                String[] paramStrings = params.split("=");
                if(EmptyUtil.isNotEmpty(paramStrings) && paramStrings.length == 2){
                    if("id".equals(paramStrings[0])){
                        String id = paramStrings[1];
                        CourseDetailsActivity.start(this,id,true, UserHelper.getUserId());
                    }
                }
            }
        }
    }

    private void gotoQrcodeProcess(String result) {
        String id = null;
        int type = -1;
        if (!TextUtils.isEmpty(result)) {
            String[] strs = result.split("&");
            if (strs != null && strs.length == 2) {
                if (!TextUtils.isEmpty(strs[0])) {
                    id = strs[0].substring(strs[0].indexOf("=") + 1, strs[0].length());
                }
                if (!TextUtils.isEmpty(strs[1])) {
                    String tempStr = strs[1].substring(strs[1].indexOf("=") + 1, strs[1].length());
                    if (tempStr != null && TextUtils.isDigitsOnly(tempStr)) {
                        type = Integer.parseInt(tempStr);
                    }
                }
            }

            if (!TextUtils.isEmpty(id) && type >= 0) {
                switch (type) {
                    case 0:
                        //TODO 快乐学习
                        if (!isLogin()){
                            enterLogin();
                            this.finish();
                            return;
                        }
                        gotoOthers(result);
                        break;
                    case 1:
                    case 3:
                        gotoSchoolSpace(id);
                        break;
                    case 2:
                        gotoPersonalSpace(id);
                        break;
                    default:
                }
            }
        }
        finish();
    }

    private void gotoBrowser(String result) {
        Uri uri = Uri.parse(result);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finish();
    }

    private void gotoPersonalSpace(String id) {
        Bundle args = new Bundle();
        args.putString(PersonalSpaceActivity.EXTRA_USER_ID, id);
        Intent intent = new Intent(CaptureActivity.this, PersonalSpaceActivity.class);
        intent.putExtras(args);
        startActivity(intent);
        finish();
    }

    private void gotoSchoolSpace(String id) {
        Bundle args = new Bundle();
        args.putString(SchoolSpaceActivity.EXTRA_SCHOOL_ID, id);
        Intent intent = new Intent(CaptureActivity.this, SchoolSpaceActivity.class);
        intent.putExtras(args);
        startActivity(intent);
        finish();
    }


    private void gotoOthers(String result) {
        Intent intent = new Intent(CaptureActivity.this, QrcodeProcessActivity.class);
        intent.putExtra(ActivityUtils.KEY_QRCODE_STRING, result);
        startActivity(intent);
        finish();
    }

    private void gotoSmartHubSelect(String result) {
        Intent intent = new Intent();
        intent.putExtra(ActivityUtils.KEY_QRCODE_STRING, result);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void processSmartHubSelectByUrl(String smarthubUrl) {
        if (TextUtils.isEmpty(smarthubUrl)) {
            return;
        }
        MyShareManager mShareManager = MyShareManager.getInstance(this, null);
        ArrayList<ConnectedDevice> shareboxList = mShareManager
                .getConnectedDevice();

        if (shareboxList == null || shareboxList.size() <= 0) {
            TipMsgHelper.ShowLMsg(CaptureActivity.this, getString(R.string.no_share_play));
            finish();
            return;
        }

        String[] smarthubInfos = smarthubUrl.split("/");
        if (smarthubInfos == null || smarthubInfos.length != 5) {
            return;
        }

        boolean bConnect = false;
        ConnectedDevice connectedDevice = null;
        for (int i = 0; i < shareboxList.size(); i++) {
            ConnectedDevice selectSharebox = shareboxList.get(i);
            String smartHubName = selectSharebox.device.getName();
            String smartHubIP = selectSharebox.device.getSocketAddress()
                    .getIpAddress();

            if (smartHubName.toLowerCase().contains(
                    smarthubInfos[3].toLowerCase())
                    || smartHubIP.equals(smarthubInfos[2])) {
                selectSharebox.setShare(true);
                bConnect = true;
                showSmartHubConnectToast(smartHubName);
                connectedDevice = selectSharebox;
                break;
            } else {
                selectSharebox.setShare(false);
            }
        }

        if (!bConnect) {
            showSmartHubConnectToast(null);
        } else {
            NotificationHelper.removeShareScreenNotification(CaptureActivity.this, notificationId);
            NotificationHelper.showShareScreenNotification(CaptureActivity.this, connectedDevice,
                    notificationId);
        }

        finish();
    }

    private void showSmartHubConnectToast(String deviceName) {
        if (deviceName != null) {
            int index = deviceName.lastIndexOf("_");
            if (index <= 0) {
                index = deviceName.length();
            }
            String showName = deviceName.substring(0, index);
            String showMsg = getString(R.string.connect_to_smarthub, showName);
            TipMsgHelper.ShowLMsg(CaptureActivity.this, showMsg);
        } else {
            TipMsgHelper.ShowLMsg(CaptureActivity.this, getString(R.string.connect_to_smarthub_error));
        }
    }

    private void parseShareUrl(String result) {
        if (result == null || result.length() == 0) {
            return;
        }
        if(!result.contains(ServerUrl.SHARE_BASE_SERVER)) {
            TipMsgHelper.ShowLMsg(CaptureActivity.this, R.string.resource_not_exist);
            finish();
            return;
        }
        String courseId = null;
        String type23=null;
        String subFlag23=null;
        //备注:需要判断用户有没有被授权&auth=true&source=lqwawa
        String auth = null;
        String parentId = null;
        if(!TextUtils.isEmpty(result)) {
            String[] strs = result.split("&");
            if(strs != null && strs.length > 0) {
                for(int i = 0; i < strs.length; i++) {
                    if(!TextUtils.isEmpty(strs[i])) {
                        String value = null;
                        if(strs[i].contains("=")) {
                            value = strs[i].substring(strs[i]
                                    .indexOf("=") + 1, strs[i].length());
                        }
                        if(strs[i].contains("pType")) {
                            type23 = value;
                        } else if(strs[i].contains("subFlag")) {
                            subFlag23 = value;
                        } else if (strs[i].contains("auth")&& (i != 0)) {
                            auth = value;
                        } else if (strs[i].contains("parentId") &&(i != 0)){
                            parentId = value;
                        }
                    }
                }
            }
        }
        if(type23!=null&&type23.equals("23")){
            int startIndex = result.lastIndexOf("vId=");
            int endIndex=0;
            if(result.contains("&")){
                endIndex = result.indexOf("&");
            }else{
                endIndex = result.length();
            }
            if (startIndex >= 0 && endIndex > 0 && startIndex < endIndex) {
                courseId = result.substring(startIndex + 4, endIndex);
            }
            if(subFlag23!=null&&subFlag23.equals("1")){
                if(!courseId.contains("-10023")){
                    courseId=courseId+"-10023";
                }
            }
//            if (TextUtils.isEmpty(sourceTag)){
//                prepareOpenCourse(ResType.RES_TYPE_BASE + ResType.RES_TYPE_STUDY_CARD, courseId,true);
//            }else {
                //判断是否有打开的权限
                if (!isLogin()) {
                    enterLogin();
                    this.finish();
                    return;
                }
                if (!courseId.contains("-")){
                    courseId = courseId + "-" + ResType.RES_TYPE_STUDY_CARD;
                }
                if ("false".equals(auth)){
                    prepareOpenCourse(ResType.RES_TYPE_STUDY_CARD, courseId,null, true);
                } else {
                    isPublicRes = false;
                    prepareOpenCourse(ResType.RES_TYPE_BASE + ResType.RES_TYPE_STUDY_CARD,
                            courseId,null,false);                }

        }else{
            int startIndex = result.lastIndexOf("vId=");
            int endIndex=0;
            if(result.contains("&")){
                endIndex = result.indexOf("&");
            }else{
                endIndex = result.length();
            }
            if (startIndex >= 0 && endIndex > 0 && startIndex < endIndex) {
                courseId = result.substring(startIndex + 4, endIndex);
            }
            if (!TextUtils.isEmpty(courseId)) {
                if(courseId.contains("-")){
                    String type=courseId.substring(courseId.indexOf("-")+1,courseId.length());
                    if (TextUtils.isEmpty(auth)){
                        prepareOpenCourse(Integer.parseInt(type), courseId,null,true);
                    }else {
                        //判断是否有打开的权限
                        if (!isLogin()) {
                            enterLogin();
                            this.finish();
                            return;
                        }
                        isPublicRes = false;
                        prepareOpenCourse(Integer.valueOf(type), courseId,parentId,false);
                    }
                }else{
                    String subFlag=null;
                    String pType=null;
                    String pTypeSubFlag = result.substring(result.indexOf("&")+1,result.length());
                    //解决扫描二维码不能播放的问题
                    if(!TextUtils.isEmpty(pTypeSubFlag)) {
                        String[] pTypeSubFlags = pTypeSubFlag.split("&");
                        if(pTypeSubFlags != null && pTypeSubFlags.length > 0) {
                            for(int i = 0; i < pTypeSubFlags.length; i++) {
                                if(!TextUtils.isEmpty(pTypeSubFlags[i])) {
                                    String value = null;
                                    if(pTypeSubFlags[i].contains("=")) {
                                        value = pTypeSubFlags[i].substring(pTypeSubFlags[i]
                                                .indexOf("=") + 1, pTypeSubFlags[i].length());
                                    }
                                    if(pTypeSubFlags[i].contains("pType")) {
                                        pType = value;
                                    } else if(pTypeSubFlags[i].contains("subFlag")) {
                                        subFlag = value;
                                    } else if (pTypeSubFlags[i].contains("parentId") && (i != 0)){
                                        parentId = value;
                                    } else if (pTypeSubFlags[i].contains("auth")&& (i != 0)) {
                                        auth = value;
                                    }
                                }
                            }
                        }
                    }
//                    if(pTypeSubFlag!=null&&pTypeSubFlag.contains("&")){
//                        pType=  pTypeSubFlag.substring(pTypeSubFlag.indexOf("=")+1,pTypeSubFlag.indexOf
//                                ("&"));
//                        subFlag= pTypeSubFlag.substring(pTypeSubFlag.lastIndexOf("=")+1,pTypeSubFlag
//                                .length());
//                    }else{
//                        pType=  pTypeSubFlag.substring(pTypeSubFlag.indexOf("=")+1,pTypeSubFlag.length());
//                    }
                    int type=0;
                    if(subFlag!=null&&Integer.parseInt(subFlag)==1){
                        type=Integer.parseInt(pType)+ResType.RES_TYPE_BASE;
                    }else{
                        type=Integer.parseInt(pType);
                    }
                    courseId=courseId+"-"+type;

                    if (TextUtils.isEmpty(auth)){
                        prepareOpenCourse(type, courseId,null,true);
                    }else {
                        //判断是否有打开的权限
                        if (!isLogin()) {
                            enterLogin();
                            this.finish();
                            return;
                        }
                        isPublicRes = false;
                        prepareOpenCourse(type, courseId,parentId,false);
                    }
                }

            }
        }

    }

    private void prepareOpenCourse(final int type, final String courseId, final String parentId, final boolean
            isPublicResource){
        WawaCourseUtils utils = new WawaCourseUtils(CaptureActivity.this);
        if(type== ResType.RES_TYPE_ONEPAGE||type==
                ResType.RES_TYPE_COURSE_SPEAKER||type== ResType.RES_TYPE_OLD_COURSE||type==
                ResType.RES_TYPE_COURSE){
            //18 19 5 16
            utils.loadCourseDetail(courseId, true);
            utils.setOnCourseDetailFinishListener(new WawaCourseUtils.OnCourseDetailFinishListener() {
                @Override
                public void onCourseDetailFinish(CourseData courseData) {
                    if (isPublicResource || isMySelfCourse(courseData) || VipConfig.isVip(CaptureActivity.this)) {
                        processData(courseData, courseId,true);
                    }else {
                        checkResourcePermission(type,courseId,null,courseData);
                    }
                }
            });
        }else  if(type==(ResType.RES_TYPE_BASE+ResType
                .RES_TYPE_COURSE_SPEAKER )){
            //10019
            utils.loadSplitCourseDetail(Long.parseLong(courseId.substring(0,courseId.indexOf("-"))));
            utils.setOnSplitCourseDetailFinishListener(new WawaCourseUtils.OnSplitCourseDetailFinishListener() {
                @Override
                public void onSplitCourseDetailFinish(SplitCourseInfo info) {
                    if (info != null) {
                        CourseData courseData = info.getCourseData();
                        if (courseData != null) {
                            if (isPublicResource || isMySelfCourse(courseData) || VipConfig.isVip(CaptureActivity.this)) {
                                processData(courseData, courseId, true);
                            }else {
                                String splitParentId  = parentId;
                                if (TextUtils.isEmpty(parentId)){
                                    splitParentId = String.valueOf(info.getParentId());
                                }
                                checkResourcePermission(type,courseId,splitParentId,courseData);
                            }
                        }
                    }
                }
            });
        }else if(type % ResType.RES_TYPE_BASE==ResType.RES_TYPE_STUDY_CARD ){
            // 10023 23
            //TODO 快乐学习
            if (!isLogin()){
                enterLogin();
                this.finish();
                return;
            }
            String resId = null;
            String [] splitArray = courseId.split("-");
            if (Integer.valueOf(splitArray[1]) == ResType.RES_TYPE_STUDY_CARD) {
                resId = splitArray[0];
            } else {
                resId = courseId;
            }
            utils.loadSplitLearnCardDetail(resId, true);
            final String finalResId = resId;
            utils.setOnSplitCourseDetailFinishListener(new WawaCourseUtils.OnSplitCourseDetailFinishListener() {
                @Override
                public void onSplitCourseDetailFinish(SplitCourseInfo info) {
                    if (info != null) {
                        CourseData courseData = info.getCourseData();
                        if (courseData != null) {
                            if (isPublicResource || isMySelfCourse(courseData) || VipConfig.isVip(CaptureActivity.this)){
                                processData(courseData, courseId,true);
                            }else {
                                String parentId = null;
                                if (finalResId.contains("-")){
                                    parentId = String.valueOf(courseData.parentid);
                                }
                                checkResourcePermission(type,courseId,parentId,courseData);
                            }
                        }
                    }
                }
            });
        }
    }

    private void processData(CourseData courseData, String courseId, final boolean isPublicRescourse) {
        if (courseData != null) {
            int resType = courseData.type % ResType.RES_TYPE_BASE;
            if (resType == ResType.RES_TYPE_COURSE_SPEAKER ||
                    resType == ResType.RES_TYPE_COURSE ||
                    resType == ResType.RES_TYPE_OLD_COURSE) {
                //打开播放页面
                if (isPublicRescourse) {
                    //打开老微课的详情界面取imgurl
                    String tempThumbnail = courseData.imgurl;
                    NewResourceInfo newResourceInfo = courseData.getNewResourceInfo();
                    if (TextUtils.isEmpty(newResourceInfo.getThumbnail()) && !TextUtils.isEmpty(tempThumbnail)) {
                        newResourceInfo.setThumbnail(tempThumbnail);
                    }
                    newResourceInfo.setIsPublicResource(isPublicRes);
                    ActivityUtils.openPictureDetailActivity(this, newResourceInfo,
                            PictureBooksDetailActivity.FROM_OTHRE, false);
                }else {
//                    ActivityUtils.playCourse(CaptureActivity.this, courseData.getCourseInfo(), null);
                    PlaybackParam playbackParam = new PlaybackParam();
                    playbackParam.mIsAuth = true;
                    ActivityUtils.playOnlineCourse(CaptureActivity.this, courseData.getCourseInfo(),
                            false, playbackParam);
                }
                finish();
            } else if (resType == ResType.RES_TYPE_STUDY_CARD) {
                //如果是扫一扫进入任务单详情页 扫码是任务进来直接打开 或者单页的任务单
                if (isScanTask) {
                    if (isPublicRescourse) {
                        getCourseSectionDataList(courseData, courseId);
                    }else {
                        TipMsgHelper.ShowMsg(CaptureActivity.this,R.string.copyright_protected_content);
                        finish();
                    }
                } else {
                    if (isPublicRescourse){
                        NewResourceInfo newResourceInfo = courseData.getNewResourceInfo();
                        newResourceInfo.setIsFromSchoolResource(true);
                        //这个表示不要右上角的返回主页按钮
                        newResourceInfo.setIsFromAirClass(true);
                        newResourceInfo.setIsScanTask(true);
                        if (isPublicRes){
                            newResourceInfo.setIsHasPermission(true);
                        }else {
                            newResourceInfo.setIsHasPermission(false);
                        }
                        ActivityUtils.enterTaskOrderDetailActivity(this,newResourceInfo);
                    }else {
                        PlaybackParam playbackParam = new PlaybackParam();
                        playbackParam.mIsAuth = true;
                        ActivityUtils.openOnlineOnePage(CaptureActivity.this, courseData.getNewResourceInfo
                                (), true,playbackParam);
                    }
                    finish();
                }
            }else if(resType == ResType.RES_TYPE_ONEPAGE){
                if (isPublicRescourse){
                    NewResourceInfo newResourceInfo = courseData.getNewResourceInfo();
                    newResourceInfo.setIsPublicResource(isPublicRes);
                    ActivityUtils.openPictureDetailActivity(this,newResourceInfo,
                            PictureBooksDetailActivity.FROM_OTHRE,false);
                }else {
                    PlaybackParam playbackParam = new PlaybackParam();
                    playbackParam.mIsAuth = true;
                    ActivityUtils.openOnlineOnePage(CaptureActivity.this, courseData.getNewResourceInfo
                            (), true,playbackParam);
                }

                finish();
            }
        }
    }
    /**
     * 判断课件的作者是不是自己
     * @param courseData
     * @return
     */
    private boolean isMySelfCourse(CourseData courseData){
        if (courseData != null){
            String memberId = DemoApplication.getInstance().getMemberId();
            if (!TextUtils.isEmpty(memberId)){
                if (memberId.equals(courseData.code)){
                    return true;
                }
            }
        }
        return false;
    }

    private void getCourseSectionDataList(final CourseData courseData, String courseId) {
        if(TextUtils.isEmpty(courseId)) {
            return;
        }
        String id = courseId;
        if(id.contains("-")) {
            id = id.substring(0, id.indexOf('-'));
        }
        Map<String, Object> params = new HashMap();
        params.put("id", id);
        if(!AppConfig.BaseConfig.needShowPay()){//只显示免费课程
            params.put("payType", 0);
        }
        RequestHelper.RequestListener listener =
                new RequestHelper.RequestResourceResultListener<CourseSectionDataListResult>(
                        CaptureActivity.this, CourseSectionDataListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()) {
                            return;
                        }
                        List<CourseSectionData> list = getResult().getData();
                        if (list == null || list.size() <= 0) {
                            return;
                        }

                        courseSectionDataString = jsonString;
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        downloadOnePage(courseData, courseSectionDataString);
                    }
                };

        listener.setShowLoading(true);
        listener.setTarget(courseData);
        RequestHelper.sendGetRequest(CaptureActivity.this,
                ServerUrl.LQMOOC_COURSE_SECTION_DATA_LIST_URL, params, listener);
    }

    private void downloadOnePage(final CourseData courseData, final String jsonString) {
        DownloadOnePageTask task = new DownloadOnePageTask(CaptureActivity
                .this, courseData.resourceurl, courseData.nickname,
                courseData.screentype, Utils.DOWNLOAD_TEMP_FOLDER, jsonString);
        task.setCallbackListener(new CallbackListener() {
            @Override
            public void onBack(Object result) {
                if (result != null) {
                    LocalCourseDTO localCourseDTO = (LocalCourseDTO)result;
                    openLocalOnePage(localCourseDTO, courseData.screentype, jsonString);
                    finish();
                }
            }
        });
        task.checkCanReplaceIPAddress(courseData.id,courseData.type,task);
    }

    private void openLocalOnePage(LocalCourseDTO data, int screenType, String jsonString) {
        if (data == null) {
            return;
        }
        CreateSlideHelper.CreateSlideParam param = new CreateSlideHelper.CreateSlideParam
                (CaptureActivity.this, null, data.getmPath(), data.getmTitle(), data
                        .getmDescription(), screenType);
        param.mIsScanTask = true;
        param.mSchoolId = schoolId;
        param.mClassId = classId;
        param.courseSectionDataString = jsonString;
        CreateSlideHelper.startSlide(param, Common.ACTIVITY_REQUEST_ATTACHMENGT_EDIT);
    }
    
    private void parseTaskIdUrl(String result) {
        if (result == null || result.length() == 0) {
            return;
        }
        if(!result.contains(ServerUrl.SHARE_BASE_SERVER)) {
            TipMsgHelper.ShowLMsg(CaptureActivity.this, R.string.resource_not_exist);
            finish();
            return;
        }
        int startIndex = result.lastIndexOf("taskId=");
        int endIndex=result.length();
        String taskIdStr=result.substring(startIndex,endIndex);
        String taskIds=null;
        if(taskIdStr.contains("&")){
            taskIds = taskIdStr.substring(0,taskIdStr.indexOf("&"));
        }else{
            taskIds = taskIdStr;
        }
        String[] strs = taskIds.split("=");
        if(strs!=null&&strs.length==2){
            if(strs[0].equals(TASKID_STR)){
                String taskId=strs[1];
                loadTaskFinishInfo(taskId);
            }
        }
    }

    private void loadTaskFinishInfo(String taskId) {
        if (TextUtils.isEmpty(taskId)) {
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("TaskId",taskId);
        RequestHelper.sendPostRequest(CaptureActivity.this,
                ServerUrl.GET_TASK_FINISH_INFO_URL, params,
                new RequestHelper.RequestDataResultListener<StudyTaskFinishInfoResult>
                        (CaptureActivity.this,
                                StudyTaskFinishInfoResult.class) {
                    @Override
                    public void onFinish() {
                        super.onFinish();
                    }

                    @Override
                    public void onSuccess(String jsonString) {
                        if (CaptureActivity.this == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        StudyTaskFinishInfoResult result = getResult();
                        if (result == null || !result.isSuccess()
                                || result.getModel() == null) {
                            return;
                        }
                        StudyTaskFinishInfo data = result.getModel().getData();
                        if (data != null) {
                            //跳转到导读详情页
                            StudyTaskInfo info = new StudyTaskInfo();
                            info.setTaskId(data.getTaskId()+"");
                            info.setTaskType(data.getTaskType());
                            StudyTaskOpenHelper.openTask(CaptureActivity.this, data.getResId(),info,
                                    0,"");
                        }
                    }
                });
    }

    private void checkResourcePermission(int type, String courseId, String parentId, final CourseData courseData){
        CheckResPermissionHelper permissionHelper = new CheckResPermissionHelper(this);
        permissionHelper.setResType(type)
                .setCouseId(courseId)
                .setMemberId(DemoApplication.getInstance().getMemberId())
                .setParentId(parentId)
                .setCheckListener(new CheckResPermissionHelper.CheckResourceResultListener() {
                    @Override
                    public void onCheckResult(int resType, String courseId, boolean isPublicResource) {
                        processData(courseData,courseId,isPublicResource);
                    }
                })
                .checkResource();

    }

    private boolean isLogin(){
        if (this.getApplication()!=null){
            UserInfo userInfo=((MyApplication)this.getApplication()).getUserInfo();
            if (userInfo!=null){
                String memberId=userInfo.getMemberId();
                if (!TextUtils.isEmpty(memberId)){
                    return true;
                }
            }
        }
        return false;
    }
    private void enterLogin() {
        Bundle args = new Bundle();
        args.putBoolean(AccountActivity.EXTRA_HAS_LOGINED, false);
        Intent intent = new Intent(this, AccountActivity.class);
        intent.putExtras(args);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
