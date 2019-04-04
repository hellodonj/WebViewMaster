package com.galaxyschool.app.wawaschool.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.galaxyschool.app.wawaschool.config.VipConfig;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.libs.gallery.ImageInfo;

import java.util.ArrayList;
import java.util.List;

import static com.galaxyschool.app.wawaschool.common.PrefsManager.PrefsItems.GET_APPLICATION_ENVIRONMENT_MODEL;
import static com.galaxyschool.app.wawaschool.common.PrefsManager.PrefsItems.GET_CAMPUS_MODEL_IP;
import static com.galaxyschool.app.wawaschool.common.PrefsManager.PrefsItems.LQWAWA_VIP;
import static com.galaxyschool.app.wawaschool.common.PrefsManager.PrefsItems.SCHOOLID_SAVE_TAG;
import static com.galaxyschool.app.wawaschool.common.PrefsManager.PrefsItems.SCHOOL_INSIDE_WIFI_HEADURL_VALUE;

public class PrefsManager {

    private Context context;

    public interface PrefsFiles {
        String APP_SETTINGS = "app_settings";
        String APP_DATA = "app_data";
    }

    public interface PrefsItems {
        String MEMBERID = "memberid";
        String NICKNAME = "nickname";
        String PASSWORD = "password";
        String PASSWORD_COPY = "pcopy";
        String REALNAME = "realname";
        String SEX = "sex";
        String BIRTHDAY = "birthday";
        String EMAIL = "email";
        String BINDMOBILE = "bindmobile";
        String MOBILE = "mobile";
        String HEADERPIC = "headerpic";
        String HEADERTEACHER = "headerteacher";
        String STATE = "state";
        String QRCODE = "qrcode";
        String ROLES = "roles";
        String ALL_ROLES = "all_roles";
        String YEID = "yeid";
        String INTRO = "intro";
        String LOCATION = "location";
        String CONTACTS_SCHOOL_LIST_RESULT = "contactsSchoolListResult";
        String CONTACTS_PERSONAL_LIST_RESULT = "contactsPersonalListResult";
        String CURR_SCHOOL_ID = "currSchoolId";

        String AUTO_UPLOAD_RESOURCE = "autoUploadResource";
        String NOTICT_AVOID_DISTURB_VOICE_OPEN_STATE= "noticeAvoidDisturbVoiceOpenState";
        String NOTICT_AVOID_DISTURB_SHAKE_OPEN_STATE= "noticeAvoidDisturbShakeOpenState";
        String SCHOOLID_SAVE_TAG= "_SchoolId_MySchoolSpaceFragment";
        //保存学校相关数据的tag
        String ALL_SCHOOLINFO_LIST = "all_schoolInfo_list";
        //获取当前的应用模式
        String GET_APPLICATION_ENVIRONMENT_MODEL = "get_application_environment_model";
        //获取校内模式的ip
        String GET_CAMPUS_MODEL_IP = "get_campus_model_ip";
        //把校内网的ip报存到本地
        String SCHOOL_INSIDE_WIFI_HEADURL_VALUE = "school_inside_wifi_headurl_value";

        //vip标识
        String LQWAWA_VIP = "lqwawa_vip";
        //保存图片资源到本地
        String PICTURE_RESOURCE_DATA_LIST = "picture_resource_data_list";
        String MAKING_COURSE_TIPS = "making_course_tips";
        //做任务的tips
        String DO_TASK_ORDER_TIPS = "do_task_order_tips";
        //切换帮辅模式的
        String CHANGE_ASSISTANT_MODEL_TIPS = "change_assistant_model_tips";

    }

    public PrefsManager(Context context) {
        this.context = context;
        init();
    }

    private void init() {

    }

    public void cleanup() {

    }

    public SharedPreferences getPrefs(String fileName) {
        SharedPreferences prefs = null;
        if (!TextUtils.isEmpty(fileName)) {
            prefs = this.context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        }
        return prefs;
    }

    public String getStringValue(
        SharedPreferences prefs, String key,
        String defaultValue) {
        if (prefs != null && !TextUtils.isEmpty(key)) {
            return prefs.getString(key, defaultValue);
        }
        return defaultValue;
    }

    public boolean setStringValue(
        SharedPreferences prefs, String key,
        String value) {
        if (prefs != null && !TextUtils.isEmpty(key)) {
            return prefs.edit().putString(key, value).commit();
        }
        return false;
    }

    public boolean getBooleanValue(
        SharedPreferences prefs, String key,
        boolean defaultValue) {
        if (prefs != null && !TextUtils.isEmpty(key)) {
            return prefs.getBoolean(key, defaultValue);
        }
        return defaultValue;
    }

    public boolean setBooleanValue(
        SharedPreferences prefs, String key,
        boolean value) {
        if (prefs != null && !TextUtils.isEmpty(key)) {
            return prefs.edit().putBoolean(key, value).commit();
        }
        return false;
    }

    public int getIntegerValue(
        SharedPreferences prefs, String key,
        int defaultValue) {
        if (prefs != null && !TextUtils.isEmpty(key)) {
            return prefs.getInt(key, defaultValue);
        }
        return defaultValue;
    }

    public boolean setIntegerValue(SharedPreferences prefs, String key, int value) {
        if (prefs != null && !TextUtils.isEmpty(key)) {
            return prefs.edit().putInt(key, value).commit();
        }
        return false;
    }

    public long getLongValue(
        SharedPreferences prefs, String key,
        long defaultValue) {
        if (prefs != null && !TextUtils.isEmpty(key)) {
            return prefs.getLong(key, defaultValue);
        }
        return defaultValue;
    }

    public boolean setLongValue(SharedPreferences prefs, String key, long value) {
        if (prefs != null && !TextUtils.isEmpty(key)) {
            return prefs.edit().putLong(key, value).commit();
        }
        return false;
    }

    public float getFloatValue(
        SharedPreferences prefs, String key,
        float defaultValue) {
        if (prefs != null && !TextUtils.isEmpty(key)) {
            return prefs.getFloat(key, defaultValue);
        }
        return defaultValue;
    }

    public boolean setFloatValue(SharedPreferences prefs, String key, float value) {
        if (prefs != null && !TextUtils.isEmpty(key)) {
            return prefs.edit().putFloat(key, value).commit();
        }
        return false;
    }

    public SharedPreferences getAppSettingsPrefs() {
        return getPrefs(PrefsFiles.APP_SETTINGS);
    }

    public SharedPreferences getAppDataPrefs() {
        return getPrefs(PrefsFiles.APP_DATA);
    }
    public UserInfo getUserInfo() {
        SharedPreferences prefs = getAppSettingsPrefs();
        String memberId = getStringValue(prefs, PrefsItems.MEMBERID, "");
        String nickname = getStringValue(prefs, PrefsItems.NICKNAME, "");
        String password = getStringValue(prefs, PrefsItems.PASSWORD, "");
        String realname = getStringValue(prefs, PrefsItems.REALNAME, "");
        String sex = getStringValue(prefs, PrefsItems.SEX, "");
        String birthday = getStringValue(prefs, PrefsItems.BIRTHDAY, "");
        String email = getStringValue(prefs, PrefsItems.EMAIL, "");
        String bindmobile = getStringValue(prefs, PrefsItems.BINDMOBILE, "");
        String mobile = getStringValue(prefs, PrefsItems.MOBILE, "");
        String headerpic = getStringValue(prefs, PrefsItems.HEADERPIC, "");
        int headerteacher = getIntegerValue(prefs, PrefsItems.HEADERTEACHER, 0);
        int state = getIntegerValue(prefs, PrefsItems.STATE, 0);
        String qrcode = getStringValue(prefs, PrefsItems.QRCODE, "");
        String roles = getStringValue(prefs, PrefsItems.ROLES, "");
        String allRoles = getStringValue(prefs,PrefsItems.ALL_ROLES,"");
        String yeid = getStringValue(prefs, PrefsItems.YEID, "");
        String intro = getStringValue(prefs, PrefsItems.INTRO, "");
        String location = getStringValue(prefs, PrefsItems.LOCATION, "");

        UserInfo result = new UserInfo();
        result.setMemberId(memberId);
        result.setNickName(nickname);
        if (!TextUtils.isEmpty(password)) {
            result.setPassword(new String(Utils.decodeHexString(password.trim())));
        }
        result.setRealName(realname);
        result.setSex(sex);
        result.setBirthday(birthday);
        result.setEmail(email);
        result.setBindMobile(bindmobile);
        result.setMobile(mobile);
        result.setHeaderPic(headerpic);
        result.setHeaderTeacher(headerteacher);
        result.setState(state);
        result.setQRCode(qrcode);
        result.setRoles(roles);
        result.setAllRoles(allRoles);
        result.setYeid(yeid);
        result.setPIntroduces(intro);
        result.setLocation(location);
        return result;
    }

    public String getUserPassword() {
        SharedPreferences prefs = getAppSettingsPrefs();
        String password = getStringValue(prefs, PrefsItems.PASSWORD_COPY, "");
        if (!TextUtils.isEmpty(password)) {
            return new String(Utils.decodeHexString(password.trim()));
        }
        return "";
    }

    public boolean setUserPassword(String password) {
        String newPassword = password != null ?
                Utils.toHexString(password.trim().getBytes()) : "";
        SharedPreferences prefs = getAppSettingsPrefs();
        return setStringValue(prefs, PrefsItems.PASSWORD_COPY,
                newPassword != null ? newPassword : "");
    }

    public boolean setUserInfo(UserInfo userInfo) {
        setVip(userInfo.getMemberId());
        SharedPreferences prefs = getAppSettingsPrefs();
        boolean result = true;
        if (userInfo != null) {
            result = result
                & setStringValue(
                prefs, PrefsItems.MEMBERID,
                userInfo.getMemberId() != null ? userInfo.getMemberId() : "");
            result = result
                & setStringValue(
                prefs, PrefsItems.NICKNAME,
                userInfo.getNickName() != null ? userInfo.getNickName() : "");
            String password = userInfo.getPassword() != null ? Utils
                .toHexString(userInfo.getPassword().trim().getBytes()) : "";
            result = result
                & setStringValue(
                prefs, PrefsItems.PASSWORD,
                password != null ? password : "");
            result = result
                & setStringValue(
                prefs, PrefsItems.REALNAME,
                userInfo.getRealName() != null ? userInfo.getRealName() : "");
            result = result
                & setStringValue(
                prefs, PrefsItems.SEX,
                userInfo.getSex() != null ? userInfo.getSex() : "");
            result = result
                & setStringValue(
                prefs, PrefsItems.BIRTHDAY,
                userInfo.getBirthday() != null ? userInfo.getBirthday() : "");
            result = result
                & setStringValue(
                prefs, PrefsItems.EMAIL,
                userInfo.getEmail() != null ? userInfo.getEmail() : "");
            result = result
                    & setStringValue(
                    prefs, PrefsItems.BINDMOBILE,
                    userInfo.getBindMobile() != null ? userInfo.getBindMobile() : "");
            result = result
                & setStringValue(
                prefs, PrefsItems.MOBILE,
                userInfo.getMobile() != null ? userInfo.getMobile() : "");
            result = result
                & setStringValue(
                prefs, PrefsItems.HEADERPIC,
                userInfo.getHeaderPic() != null ? userInfo.getHeaderPic() : "");
            result = result
                & setIntegerValue(
                prefs, PrefsItems.HEADERTEACHER,
                userInfo.getHeaderTeacher());
            result = result
                & setIntegerValue(
                prefs, PrefsItems.STATE,
                userInfo.getState());
            result = result
                & setStringValue(
                prefs, PrefsItems.QRCODE,
                userInfo.getQRCode());
            result = result
                & setStringValue(
                prefs, PrefsItems.ROLES,
                userInfo.getRoles() != null ? userInfo.getRoles() : "");
            result = result & setStringValue(
                    prefs,PrefsItems.ALL_ROLES,
                    !TextUtils.isEmpty(userInfo.getAllRoles()) ? userInfo.getAllRoles() : ""
            );
            result = result
                & setStringValue(
                prefs, PrefsItems.YEID,
                userInfo.getYeid() != null ? userInfo.getYeid() : "");
            result = result
                & setStringValue(
                prefs, PrefsItems.INTRO,
                userInfo.getPIntroduces() != null ? userInfo.getPIntroduces() : "");
            result = result
                & setStringValue(
                prefs, PrefsItems.LOCATION,
                userInfo.getLocation() != null ? userInfo.getLocation() : "");
        }
        return result;
    }

    public boolean clearUserLoginInfo() {
        SharedPreferences prefs = getAppSettingsPrefs();
        boolean result = true;
        result = setStringValue(prefs, PrefsItems.MEMBERID, "");
        result = result & setStringValue(prefs, PrefsItems.PASSWORD, "");
        result = result & setStringValue(prefs, PrefsItems.PASSWORD_COPY, "");
        return result;
    }

    public boolean clearUserInfo() {
        return setUserInfo(new UserInfo());
    }

    public boolean setContactsGroupList(String jsonString) {
        return setStringValue(getAppDataPrefs(), PrefsItems.CONTACTS_SCHOOL_LIST_RESULT,
                jsonString);
    }

    public String getContactsGroupList() {
        return getStringValue(getAppDataPrefs(), PrefsItems.CONTACTS_SCHOOL_LIST_RESULT,
                "");
    }

    public boolean clearContactsGroupList() {
        return getAppDataPrefs().edit().remove(PrefsItems.CONTACTS_SCHOOL_LIST_RESULT)
                .commit();
    }

    public String getCurrSchoolId() {
        return getStringValue(getAppDataPrefs(), PrefsItems.CURR_SCHOOL_ID, "");
    }

    public boolean setCurrSchoolId(String schoolId) {
        return setStringValue(getAppDataPrefs(), PrefsItems.CURR_SCHOOL_ID, schoolId);
    }

    public boolean clearCurrSchoolId() {
        return getAppDataPrefs().edit().remove(PrefsItems.CURR_SCHOOL_ID).commit();
    }

    public Boolean getAutoUploadResource() {
        return getBooleanValue(getAppDataPrefs(), PrefsItems.AUTO_UPLOAD_RESOURCE, /*true*/ false);
    }

    public boolean setAutoUploadResource(boolean isAutoUpload) {
        return setBooleanValue(getAppDataPrefs(), PrefsItems.AUTO_UPLOAD_RESOURCE, isAutoUpload);
    }

    public Boolean getNoticeAvoidDisturbVoiceOpenState() {
        return getBooleanValue(getAppDataPrefs(), PrefsItems
                .NOTICT_AVOID_DISTURB_VOICE_OPEN_STATE, true);
    }
    public boolean setNoticeAvoidDisturbVoiceOpenState(boolean isOpen) {
        return setBooleanValue(getAppDataPrefs(), PrefsItems.NOTICT_AVOID_DISTURB_VOICE_OPEN_STATE, isOpen);
    }

    public Boolean getNoticeAvoidDisturbShakeOpenState() {
        return getBooleanValue(getAppDataPrefs(), PrefsItems
                .NOTICT_AVOID_DISTURB_SHAKE_OPEN_STATE, true);
    }
    public boolean setNoticeAvoidDisturbShakeOpenState(boolean isOpen) {
        return setBooleanValue(getAppDataPrefs(), PrefsItems.NOTICT_AVOID_DISTURB_SHAKE_OPEN_STATE, isOpen);
    }

    public String getLatestSchool(Context context,String memberId) {
        return SharedPreferencesHelper.getString(context, memberId + SCHOOLID_SAVE_TAG);
    }
    public void saveLatestSchool(Context context,String memberId, String schoolId) {
        SharedPreferencesHelper.setString(context, memberId + SCHOOLID_SAVE_TAG,
                schoolId);
    }

    /**
     * 获取临时保存的校园内网的IP地址
     * @param context
     * @param memberId 用户的MemberId
     * @return headUrl
     */
    public String getSchoolInsideWiFiHeadUrl(Context context,String memberId){
        return SharedPreferencesHelper.getString(context,SCHOOL_INSIDE_WIFI_HEADURL_VALUE);
    }

    /**
     * 临时保存校园内网的IP地址
     * @param context
     * @param memberId 用户的memberId
     * @param headUrl 拉取的内网ip
     */
    public void saveSchoolInsideWiFiHeadUrl(Context context,String memberId,String headUrl){
        SharedPreferencesHelper.setString(context,SCHOOL_INSIDE_WIFI_HEADURL_VALUE,
                headUrl);
    }

    /**
     * 保存当前应用的选中的模式
     * @param context
     * @param memberId
     * @param model 0 通用模式 1 校本模式
     */
    public void saveCurrentApplicationModel(Context context,String memberId,String model){
        SharedPreferencesHelper.setString(context,GET_APPLICATION_ENVIRONMENT_MODEL,
                model);
    }

    /**
     * 获取当前的应用模式
     * @param context
     * @param memberId
     * @return model
     */
    public String getCurrentApplicationModel(Context context,String memberId){
        String applicationModel = SharedPreferencesHelper.getString(context,GET_APPLICATION_ENVIRONMENT_MODEL);
        if (TextUtils.isEmpty(applicationModel)){
            applicationModel = SharedPreferencesHelper.getString(context,memberId +
                    GET_APPLICATION_ENVIRONMENT_MODEL);
        }
        return applicationModel;
    }

    /**
     * 保存当前保存的ip
     * @param context
     * @param memberId
     * @param ip 自定义输入的ip地址
     */
    public void saveCampusModelIp(Context context,String memberId,String ip){
        SharedPreferencesHelper.setString(context,GET_CAMPUS_MODEL_IP,ip);
    }

    /**
     * 获取当前的保存的校本ip
     * @param context
     * @param memberId
     * @return ip
     */
    public String getCampusModelIp(Context context,String memberId){
        String campusIP = SharedPreferencesHelper.getString(context,GET_CAMPUS_MODEL_IP);
        if (TextUtils.isEmpty(campusIP)){
            campusIP = SharedPreferencesHelper.getString(context,memberId + GET_CAMPUS_MODEL_IP);
        }
        return campusIP;
    }


    /**
     * 获取所有学校的list(包括加入和关注)
     */
    public List<SchoolInfo> getAllSchoolInfoList(String memberId){
        return getDataList(memberId + PrefsItems.ALL_SCHOOLINFO_LIST);
    }

    /**
     * 获取加入机构的list
     * @return
     */
    public List<SchoolInfo> getJoinSchoolList(String memberId){
        return getSelectData(memberId,1);
    }

    /**
     * 获取加入机构的id_list
     * @return
     */
    public List<String> getJoinSchoolIdList(String memberId){
        return getSelectData(memberId,2);
    }

    /**
     * 获取所有学校的id_list(包括加入和关注)
     * @return
     */
    public List<String> getAllSchoolIdList(String memberId){
        return getSelectData(memberId,3);
    }
    private <T> List<T> getSelectData(String memberId,int selectType){
        List<SchoolInfo> joinSchoolList = new ArrayList<>();
        List<String> allSchoolIds = new ArrayList<>();
        List<String> joinSchoolIds = new ArrayList<>();
        List<SchoolInfo> schoolInfoList = getAllSchoolInfoList(memberId);
        if (schoolInfoList != null && schoolInfoList.size() > 0){
            for (int i = 0,len = schoolInfoList.size();i < len;i++){
                SchoolInfo schoolInfo = schoolInfoList.get(i);
                if (schoolInfo != null){
                    if (schoolInfo.hasJoinedSchool()){
                        joinSchoolList.add(schoolInfo);
                        joinSchoolIds.add(schoolInfo.getSchoolId());
                    }
                    allSchoolIds.add(schoolInfo.getSchoolId());
                }
            }
        }
        if (selectType == 1){
            //已经加入的机构列表
            return (List<T>) joinSchoolList;
        }else if (selectType == 2){
            //已经加入的学校id_list表
            return (List<T>) joinSchoolIds;
        }else {
            //所有的id_list
            return (List<T>) allSchoolIds;
        }
    }

    /**
     * 保存List
     * @param tag
     * @param datalist
     */
    public void setDataList(String tag, List datalist) {
        SharedPreferences.Editor editor = getAppDataPrefs().edit();
        if (null == datalist || datalist.size() <= 0)
            return;

        Gson gson = new Gson();
        //转换成json数据，再保存
        String strJson = gson.toJson(datalist);
        editor.clear();
        editor.putString(tag, strJson);
        editor.apply();
    }

    /**
     * 获取schoolList
     * @param tag
     * @return
     */
    public List getDataList(String tag) {
        List<SchoolInfo> datalist=new ArrayList<SchoolInfo>();
        String strJson = getAppDataPrefs().getString(tag, null);
        if (null == strJson) {
            return datalist;
        }
        Gson gson = new Gson();
        datalist = gson.fromJson(strJson, new TypeToken<List<SchoolInfo>>() { }.getType());
        return datalist;
    }

    /**
     * 获取资源图片的存储
     * @param tag
     * @return
     */
    public List getPictureDataList(String tag) {
        List<ImageInfo> imageInfos=new ArrayList<>();
        String strJson = getAppDataPrefs().getString(tag, null);
        if (null == strJson) {
            return imageInfos;
        }
        Gson gson = new Gson();
        imageInfos = gson.fromJson(strJson, new TypeToken<List<ImageInfo>>() { }.getType());
        return imageInfos;
    }

    public void setVip(String memberId) {
        boolean isVip = false;
        ArrayList<String> vipList = VipConfig.getVipList();
        if (!TextUtils.isEmpty(memberId) && vipList.contains(memberId.toUpperCase())) {
            isVip = true;
        }

        SharedPreferencesHelper.setBoolean(context,LQWAWA_VIP, isVip);
    }

    public boolean isVip() {
        return SharedPreferencesHelper.getBoolean(context, LQWAWA_VIP,false);
    }

    public boolean enableMakingCourseTips(boolean enabled) {
        return setBooleanValue(getAppSettingsPrefs(), PrefsItems.MAKING_COURSE_TIPS, enabled);
    }

    public boolean isMakingCourseTipsEnabled() {
        return getBooleanValue(getAppSettingsPrefs(), PrefsItems.MAKING_COURSE_TIPS, true);
    }

    public boolean enableDoTaskOrderTips(boolean enabled){
        return setBooleanValue(getAppSettingsPrefs(), PrefsItems.DO_TASK_ORDER_TIPS, enabled);
    }

    public boolean isDoTaskOrderTipsEnabled(){
        return getBooleanValue(getAppSettingsPrefs(), PrefsItems.DO_TASK_ORDER_TIPS, false);
    }

    public boolean enableChangeAssistantModelTip(boolean enabled){
        return setBooleanValue(getAppSettingsPrefs(), PrefsItems.CHANGE_ASSISTANT_MODEL_TIPS, enabled);
    }

    public boolean isAssistantModelTipEnable(){
        return getBooleanValue(getAppSettingsPrefs(), PrefsItems.CHANGE_ASSISTANT_MODEL_TIPS, false);
    }
}
