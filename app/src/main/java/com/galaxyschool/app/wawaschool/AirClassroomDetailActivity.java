package com.galaxyschool.app.wawaschool;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.fragment.AirClassroomDetailFragment;
import com.galaxyschool.app.wawaschool.fragment.AirClassroomFragment;
import com.galaxyschool.app.wawaschool.pojo.Emcee;
import com.galaxyschool.app.wawaschool.pojo.EmceeList;
import com.galaxyschool.app.wawaschool.pojo.PublishClass;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.SubscribeClassInfo;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.lqwawa.client.pojo.SourceFromType;
import com.lqwawa.intleducation.base.utils.LogUtil;
import com.lqwawa.intleducation.module.learn.tool.LiveDetails;
import com.lqwawa.intleducation.module.learn.vo.EmceeListVo;
import com.lqwawa.intleducation.module.learn.vo.LiveVo;
import com.lqwawa.intleducation.module.onclass.OnlineClassRole;
import java.util.ArrayList;
import java.util.List;

public class AirClassroomDetailActivity extends BaseFragmentActivity implements
        AirClassroomDetailFragment.Contants {
    private AirClassroomDetailFragment fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //防止播放乐视视频的时候屏幕闪屏
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.activity_common);

        Bundle args = getIntent().getExtras();
        boolean isMooc = args.getBoolean(ISMOOC);
        boolean isAirClassRoomLive = args.getBoolean("isAirClassRoomLive");
        boolean isOnline = args.getBoolean("isOnline");
        boolean result = args.getBoolean("result");
        boolean isMyCourseChildOnline = args.getBoolean("isMyCourseChildOnline");
        String role = args.getString("role");
        boolean isFromMyLive = args.getBoolean("isFromMyLive");
        Emcee emcee = null;
        if (isMooc) {
            LiveVo dataBean = (LiveVo) args.getSerializable(EMECCBEAN);
            if (dataBean != null) {
                emcee = new Emcee();
                emcee.setAcCreateId(dataBean.getAcCreateId());
                emcee.setAcCreateNickName(dataBean.getCreateName());
                emcee.setAcCreateRealName(dataBean.getCreateName());
                emcee.setCoverUrl(dataBean.getCoverUrl());
                emcee.setCreateTime(dataBean.getStartTime());
                emcee.setStartTime(dataBean.getStartTime());
                emcee.setEndTime(dataBean.getEndTime());
                emcee.setId(Integer.valueOf(dataBean.getId()));
                emcee.setIntro(dataBean.getIntro());
                emcee.setSchoolId(dataBean.getSchoolId());
                emcee.setSchoolName(dataBean.getSchoolName());
                emcee.setLiveType(dataBean.getLiveType());
                emcee.setResTitle(dataBean.getResTitle());
                if (TextUtils.isEmpty(dataBean.getLeAcid())){
                    emcee.setLiveId(dataBean.getLiveId());
                } else {
                    emcee.setLiveId(dataBean.getLeAcid());
                }
                if (TextUtils.isEmpty(dataBean.getLeVuid())){
                    emcee.setDemandId(dataBean.getDemandId());
                } else {
                    emcee.setDemandId(dataBean.getLeVuid());
                }
                emcee.setShareUrl(dataBean.getShareUrl());
                emcee.setEmceeMemberIdStr(dataBean.getEmceeIds());
                emcee.setState(dataBean.getState());
                emcee.setTitle(dataBean.getTitle());
                emcee.setEmceeNames(dataBean.getEmceeNames());
                emcee.setCourseId(dataBean.getCourseId());
                emcee.setBrowseCount(dataBean.getBrowseCount());
                emcee.setCourseIds(dataBean.getCourseIds());
                emcee.setPayType(dataBean.getPayType());
                emcee.setPrice(dataBean.getPayType());
                emcee.setFromMyLive(getIntent().getBooleanExtra(LiveDetails.KEY_IS_FROM_MY_LIVE, false));
                //如果来自空中课堂的数据
                if (isAirClassRoomLive) {
                    if(isOnline){
                        if(OnlineClassRole.ROLE_STUDENT.equals(role)){
                            emcee.setAddMyLived(result);
                        }
                        if (!isMyCourseChildOnline) {
                            //我的课程孩子的直播
                            args.putBoolean("isAirClassRoomLive", false);
                        }
                    } else {
                        emcee.setAddMyLived(true);
                        emcee.setFromMyLive(true);
                    }
                    UIUtils.currentSourceFromType = SourceFromType.AIRCLASS_ONLINE;
                    args.putBoolean(ISMOOC,false);
                    emcee.setIsEbanshuLive(dataBean.isIsEbanshuLive());
                    emcee.setClassId(dataBean.getClassId());
                    emcee.setClassName(dataBean.getClassName());
                    emcee.setDeleted(dataBean.isIsDelete());
                    emcee.setOnlineNum(dataBean.getOnlineNum());
                    emcee.setEmceeList(changeEmceeListData(dataBean.getEmceeList()));
                    emcee.setRoomId(dataBean.getRoomId());
                    emcee.setBrowseCount(emcee.getBrowseCount() + 1);
                    // com.lqwawa.intleducation.common.utils.LogUtil.e(AirClassroomDetailActivity.class,JSON.toJSONString(dataBean.getPublishClassList()));
                    // com.lqwawa.intleducation.common.utils.LogUtil.e(AirClassroomDetailActivity.class,JSON.toJSONString(dataBean.getPublishClassVoList()));
                    emcee.setPublishClassList(changePublishData(dataBean.getPublishClassList()));
                    if (emcee.getPublishClassList() != null){
                        //配置改变数据
                        ConfigPassData(args,emcee);
                    }
                }
                args.putSerializable("emeccBean", emcee);
            }
        }
        fragment = new AirClassroomDetailFragment();
        fragment.setArguments(args);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.activity_body, fragment, AirClassroomDetailFragment.TAG);
        ft.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        if (this.getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            this.finish();
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            if (fragment != null){
                fragment.onBackPress(false);
            }
        }
    }

    private List<EmceeList> changeEmceeListData(List<EmceeListVo> vo){
        if (vo != null && vo.size() > 0){
            List<EmceeList> reporters = new ArrayList<>();
            for (int i = 0,len = vo.size();i < len;i++){
                EmceeListVo listVo = vo.get(i);
                EmceeList reporter = new EmceeList();
                reporter.setId(listVo.getId());
                reporter.setExtId(listVo.getExtId());
                reporter.setType(listVo.getType());
                reporter.setMemberId(listVo.getMemberId());
                reporter.setRealName(listVo.getRealName());
                reporter.setNickName(listVo.getNickName());
                reporter.setHeadPicUrl(listVo.getHeadPicUrl());
                reporter.setCreateId(listVo.getCreateId());
                reporter.setCreateName(listVo.getCreateName());
                reporter.setCreateTime(listVo.getCreateTime());
                reporter.setUpdateId(listVo.getUpdateId());
                reporter.setUpdateName(listVo.getUpdateName());
                reporter.setUpdateTime(listVo.getUpdateTime());
                reporter.setDeleted(listVo.isDeleted());
                reporter.setClassIds((String) listVo.getClassIds());
                reporter.setSchoolIds((String) listVo.getSchoolIds());
                reporters.add(reporter);
            }
            return reporters;
        }
        return null;
    }

    private List<PublishClass> changePublishData(Object object){
        if (object != null){
            // com.lqwawa.intleducation.common.utils.LogUtil.e(AirClassroomDetailActivity.class,object.toString());
            List<PublishClass> publishClasses = JSONObject.parseArray(object.toString(), PublishClass.class);
            return publishClasses;
        }
        return null;
    }

    private void  ConfigPassData(Bundle bundle,Emcee onlineRes){
        SubscribeClassInfo classInfo = new SubscribeClassInfo();
        if (onlineRes != null){
            List<PublishClass> publishClasses = onlineRes.getPublishClassList();
            if (publishClasses != null && publishClasses.size() > 0){
                for (int i = 0;i < publishClasses.size();i++){
                    PublishClass classData = publishClasses.get(i);
                    if (TextUtils.equals(classData.getClassId(),onlineRes.getClassId())){
                        classInfo.setClassId(classData.getClassId());
                        classInfo.setClassName(classData.getClassName());
                        classInfo.setSchoolId(classData.getSchoolId());
                        classInfo.setSchoolName(classData.getSchoolName());
                        break;
                    }
                }
            }
            bundle.putSerializable(AirClassroomActivity.ExTRA_CLASS_INFO,classInfo);
        }

        UserInfo userInfo = DemoApplication.getInstance().getUserInfo();
        if (userInfo != null) {
            boolean isReporter = false;
            List<EmceeList> emceeLists = onlineRes.getEmceeList();
            if (emceeLists != null && emceeLists.size() > 0){
                for (int i = 0;i < emceeLists.size();i++){
                    EmceeList list = emceeLists.get(i);
                    if (TextUtils.equals(list.getMemberId(),userInfo.getMemberId())){
                        isReporter = true;
                        break;
                    }
                }
            }
            String passMemberId = bundle.getString("memberId");
            if (!TextUtils.equals(passMemberId,userInfo.getMemberId())){
                //用户的memberId不相同是家长的身份
                bundle.putInt(AirClassroomActivity.EXTRA_ROLE_TYPE, RoleType.ROLE_TYPE_PARENT);
            } else if (isReporter){
                bundle.putBoolean(AirClassroomActivity.EXTRA_IS_TEACHER,true);
                bundle.putInt(AirClassroomActivity.EXTRA_ROLE_TYPE, RoleType.ROLE_TYPE_TEACHER);
            } else {
                bundle.putInt(AirClassroomActivity.EXTRA_ROLE_TYPE, RoleType.ROLE_TYPE_STUDENT);
            }

            boolean isOnline = bundle.getBoolean("isOnline");
            String role = bundle.getString("role");
            boolean isHeadMaster = bundle.getBoolean("isHeadMaster");
            boolean isFromMyLive = bundle.getBoolean("isFromMyLive");

            // 在线课堂过来的数据 并且是家长,覆盖身份
            if(isOnline && OnlineClassRole.ROLE_PARENT.equals(role)){
                bundle.putInt(AirClassroomActivity.EXTRA_ROLE_TYPE,RoleType.ROLE_TYPE_PARENT);
                bundle.putBoolean(AirClassroomActivity.EXTRA_IS_TEACHER,false);
            }else if(isOnline && OnlineClassRole.ROLE_TEACHER.equals(role)){
                bundle.putInt(AirClassroomActivity.EXTRA_ROLE_TYPE,RoleType.ROLE_TYPE_TEACHER);
                bundle.putBoolean(AirClassroomActivity.EXTRA_IS_TEACHER,true);
            }else if(isOnline && OnlineClassRole.ROLE_STUDENT.equals(role)){
                bundle.putInt(AirClassroomActivity.EXTRA_ROLE_TYPE,RoleType.ROLE_TYPE_STUDENT);
                bundle.putBoolean(AirClassroomActivity.EXTRA_IS_TEACHER,false);
            }

            if(isOnline || isFromMyLive){
                bundle.putBoolean(AirClassroomFragment.Constants.EXTRA_IS_HEADMASTER,isHeadMaster);
            }
        }
        SchoolInfo schoolInfo = new SchoolInfo();
        schoolInfo.setSchoolId(classInfo.getSchoolId());
        schoolInfo.setSchoolName(classInfo.getSchoolName());
        bundle.putSerializable(AirClassroomActivity.EXTRA_IS_SCHOOLINFO,schoolInfo);
        bundle.putString(AirClassroomActivity.EXTRA_CONTACTS_SCHOOL_ID, classInfo.getSchoolId());
        bundle.putString(AirClassroomActivity.EXTRA_CONTACTS_CLASS_ID, classInfo.getClassId());
        bundle.putBoolean(ActivityUtils.EXTRA_IS_ONLINE_CLASS,bundle.getBoolean("isOnline"));
    }
}
