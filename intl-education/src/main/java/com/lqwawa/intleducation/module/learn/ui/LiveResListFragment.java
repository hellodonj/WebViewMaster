package com.lqwawa.intleducation.module.learn.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lecloud.skin.ui.utils.VodVideoSettingUtil;
import com.libs.gallery.ImageBrowserActivity;
import com.libs.gallery.ImageInfo;
import com.lqwawa.client.pojo.SourceFromType;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.MainApplication;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.helper.SharedPreferencesHelper;
import com.lqwawa.intleducation.base.ui.MyBaseFragment;
import com.lqwawa.intleducation.base.utils.ButtonUtils;
import com.lqwawa.intleducation.base.utils.LogUtil;
import com.lqwawa.intleducation.base.utils.NetWorkUtils;
import com.lqwawa.intleducation.base.utils.StringUtils;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.common.ui.CustomDialog;
import com.lqwawa.intleducation.common.utils.LetvVodHelperNew;
import com.lqwawa.intleducation.factory.helper.LessonHelper;
import com.lqwawa.intleducation.module.discovery.adapter.CourseResListAdapter;
import com.lqwawa.intleducation.module.discovery.tool.LoginHelper;
import com.lqwawa.intleducation.module.discovery.vo.LiveDetailsVo;
import com.lqwawa.intleducation.module.learn.tool.LiveDetails;
import com.lqwawa.intleducation.module.learn.tool.TaskSliderHelper;
import com.lqwawa.intleducation.module.learn.vo.LiveDetailsResVo;
import com.lqwawa.intleducation.module.learn.vo.SectionResListVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.lqresviewlib.LqResViewHelper;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;


public class LiveResListFragment extends MyBaseFragment {
    private ListView listView;
    private PullToRefreshView pullToRefreshView;
    private RelativeLayout loadFailedLayout;
    private Button reloadBt;
    private boolean needFlag;
    private boolean canRead;
    private boolean canEdit = false;

    private LiveDetailsResVo sectionDetailsVo;
    private CourseResListAdapter courseResListAdapter;
    private LiveDetailsVo liveDetailsVo;
    private int roleType;
    private int type = 0;
    private String schoolId = "";
    private boolean isHost = false;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.com_refresh_list, container, false);

        listView = (ListView) view.findViewById(R.id.listView);

        needFlag = true;
        canRead = true;
        canEdit = false;
        type = getArguments().getInt("type");
        isHost = getArguments().getBoolean("isHost");
        roleType = getArguments().getInt("roleType");

        loadFailedLayout = (RelativeLayout) view.findViewById(R.id.load_failed_layout);
        reloadBt = (Button) view.findViewById(R.id.reload_bt);
        pullToRefreshView = (PullToRefreshView) view.findViewById(R.id.pull_to_refresh);
        pullToRefreshView.setLoadMoreEnable(false);
        pullToRefreshView.hideFootView();
        pullToRefreshView.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                getData();
            }
        });
        pullToRefreshView.showRefresh();

        courseResListAdapter = new CourseResListAdapter(activity,
                (!isHost && isMyLive() && canEdit)
                || (UserHelper.isLogin() && StringUtils.isValidString(getExtrasMemberId())));
        listView.setAdapter(courseResListAdapter);
        if (canRead) {
            courseResListAdapter.setOnItemClickListener(new CourseResListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position, View convertView) {
                    {
                        if(!UserHelper.isLogin()){
                            LoginHelper.enterLogin(activity);
                            return;
                        }
                        if(ButtonUtils.isFastDoubleClick()){
                            return;
                        }
                        SectionResListVo resVo = (SectionResListVo) courseResListAdapter.getItem(position);
                        if(resVo.isIsShield()){
                            ToastUtil.showToast(activity, getResources().getString(R.string.res_has_shield));
                            return;
                        }
                        if (resVo.getTaskType() == 1) {//看课件
                            readWeike(resVo, position);
                            if (isMyLive() && !isHost && canEdit) {
                                flagRead(resVo.getId(), resVo.getResId(), resVo.getResType());
                            }
                        } else if (resVo.getTaskType() == 2) {//复述微课
                            if(canEdit || isHost || !isMyLive()){
//                                    SectionTaskDetailsActivity.startLiveForResultEx(activity, resVo,
//                                            getCurrentMemberId(),
//                                            schoolId, isHost,
//                                            activity.getIntent().getBooleanExtra(LiveDetails
//                                                    .KEY_IS_FROM_MY_LIVE, false), roleType);
                                enterSectionTaskDetail(resVo, roleType);
                            }else{
                                readWeike(resVo, position);
                            }
                        } else if (resVo.getTaskType() == 3) {//任务单
                            if(canEdit || isHost || !isMyLive()) {
//                                    SectionTaskDetailsActivity.startLiveForResultEx(activity, resVo,
//                                            getCurrentMemberId(),
//                                            schoolId, isHost,
//                                            activity.getIntent().getBooleanExtra(LiveDetails
//                                                    .KEY_IS_FROM_MY_LIVE, false), roleType);
                                enterSectionTaskDetail(resVo, roleType);

                            }else{
                                if(TaskSliderHelper.onTaskSliderListener != null) {
                                    TaskSliderHelper.onTaskSliderListener.viewCourse(activity,
                                            resVo.getResId(), resVo.getResType(),
                                            schoolId,
                                            false,
                                            activity.getIntent().getBooleanExtra(MyCourseDetailsActivity
                                                    .KEY_IS_FROM_MY_COURSE, false)
                                                    ? SourceFromType.MY_ONLINE_LIVE : SourceFromType.ONLINE_LIVE);
                                }
                            }
                        }
                    }
                }

                @Override
                public void onItemChoice(int position, View convertView) {

                }
            });
        } else if (!needFlag) {
            listView.setVisibility(View.GONE);
        }
        return view;
    }

    protected void enterSectionTaskDetail(SectionResListVo vo, int role) {
        final String taskId = vo.getTaskId();
        if (role == UserHelper.MoocRoleType.STUDENT && !TextUtils.isEmpty(taskId)) {
            LessonHelper.DispatchTask(taskId, UserHelper.getUserId(), null);
        }

        SectionTaskDetailsActivity.startLiveForResultEx(activity, vo,
                getCurrentMemberId(),
                schoolId, isHost,
                activity.getIntent().getBooleanExtra(LiveDetails
                        .KEY_IS_FROM_MY_LIVE, false), roleType);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getData();
    }

    private void readWeike(final SectionResListVo resVo, int position) {
        int resType = resVo.getResType();
        if(resType > 10000){
            resType -= 10000;
        }
        switch (resType) {
            case 1:
                showPic(resVo);
                break;
            case 2:
                playMedia(resVo,VodVideoSettingUtil.AUDIO_TYPE);
                break;
            case 6:
            case 20:
                if(TaskSliderHelper.onTaskSliderListener != null){
                    TaskSliderHelper.onTaskSliderListener
                            .viewPdfOrPPT(activity, "" + resVo.getResId(), resVo.getResType(),
                                    resVo.getName(), resVo.getCreateId(),
                                    activity.getIntent().getBooleanExtra(MyCourseDetailsActivity
                                            .KEY_IS_FROM_MY_COURSE, false)
                                            ? SourceFromType.MY_ONLINE_LIVE : SourceFromType.ONLINE_LIVE);
                }
                break;
            case 24:
            case 25:
                LqResViewHelper.playBaseRes(resVo.getResType(), activity,
                        resVo.getResourceUrl().trim(), resVo.getName());
                break;
            case 5:
            case 16:
            case 17:
            case 18:
            case 19:
            case 3:
            case 23:
                if (!SharedPreferencesHelper.getBoolean(activity,
                        AppConfig.BaseConfig.KEY_ALLOW_4G, false)) {
                    if (NetWorkUtils.isWifiActive(activity.getApplication().getApplicationContext())) {
                        if (resVo.getResType() == 3) {
                            playMedia(resVo,VodVideoSettingUtil.VIDEO_TYPE);
                        } else {
                            if(TaskSliderHelper.onTaskSliderListener != null){
                                TaskSliderHelper.onTaskSliderListener.viewCourse(activity,
                                        resVo.getResId(), resVo.getResType(),
                                        schoolId,
                                        false,
                                        activity.getIntent().getBooleanExtra(MyCourseDetailsActivity
                                                .KEY_IS_FROM_MY_COURSE, false)
                                                ? SourceFromType.MY_ONLINE_LIVE : SourceFromType.ONLINE_LIVE);
                            }
                        }
                    } else {
                        ToastUtil.showToast(activity, activity.getResources().getString(R.string.can_not_use_4g));
                    }
                } else {
                    if (NetWorkUtils.isWifiActive(activity.getApplication().getApplicationContext())) {
                        if (resVo.getResType() == 3) {
                            playMedia(resVo,VodVideoSettingUtil.VIDEO_TYPE);
                        } else {
                            if(TaskSliderHelper.onTaskSliderListener != null){
                                TaskSliderHelper.onTaskSliderListener.viewCourse(activity,
                                        resVo.getResId(), resVo.getResType(),
                                        schoolId,
                                        false,
                                        activity.getIntent().getBooleanExtra(MyCourseDetailsActivity
                                                .KEY_IS_FROM_MY_COURSE, false)
                                                ? SourceFromType.MY_ONLINE_LIVE : SourceFromType.ONLINE_LIVE);
                            }
                        }
                    } else {
                        CustomDialog.Builder builder = new CustomDialog.Builder(activity);
                        builder.setMessage(activity.getResources().getString(R.string.play_use_4g) + "?");
                        builder.setTitle(activity.getResources().getString(R.string.tip));
                        builder.setPositiveButton(activity.getResources().getString(R.string.continue_play),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        if (resVo.getResType() == 3) {
//                                            LqResViewHelper.playBaseRes(resVo.getResType(), activity, resVo.getVuid().trim(), resVo.getName());
                                            playMedia(resVo,VodVideoSettingUtil.VIDEO_TYPE);
                                        } else {
                                            if(TaskSliderHelper.onTaskSliderListener != null){
                                                TaskSliderHelper.onTaskSliderListener.viewCourse(activity,
                                                        resVo.getResId(), resVo.getResType(),
                                                        schoolId,
                                                        false,
                                                        activity.getIntent().getBooleanExtra(MyCourseDetailsActivity
                                                                .KEY_IS_FROM_MY_COURSE, false)
                                                                ? SourceFromType.MY_ONLINE_LIVE : SourceFromType.ONLINE_LIVE);
                                            }
                                        }
                                    }
                                });
                        builder.setNegativeButton(activity.getResources().getString(R.string.cancel),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                        builder.create().show();
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 图片浏览
     * @param resVo
     */
    private void showPic(SectionResListVo resVo) {
        List<ImageInfo> resourceInfoList = new ArrayList<>();
        ImageInfo newResourceInfo = new ImageInfo();
        newResourceInfo.setTitle(resVo.getName());
        newResourceInfo.setResourceUrl(resVo.getResourceUrl().trim());
        newResourceInfo.setResourceId(resVo.getResId()+"-"+resVo.getResType());
        newResourceInfo.setAuthorId(resVo.getCreateId());
        newResourceInfo.setResourceType(resVo.getResType());
        resourceInfoList.add(newResourceInfo);


        Intent intent = new Intent();
        intent.setClassName( MainApplication.getInstance().getPackageName(), "com.galaxyschool.app.wawaschool.imagebrowser.GalleryActivity");
        intent.putParcelableArrayListExtra(ImageBrowserActivity.EXTRA_IMAGE_INFOS, (ArrayList<? extends Parcelable>) resourceInfoList);
        intent.putExtra(ImageBrowserActivity.EXTRA_CURRENT_INDEX, 0);
        intent.putExtra(ImageBrowserActivity.ISPDF, false);

        intent.putExtra(ImageBrowserActivity.KEY_ISHIDEMOREBTN, false);
        intent.putExtra(ImageBrowserActivity.KEY_ISSHOWCOURSEANDREADING, true);
        intent.putExtra(ImageBrowserActivity.KEY_ISSHOWCOLLECT, false);//隐藏收藏功能
        startActivity(intent);
    }

    /**
     * 音视频播放
     * @param resVo
     * @param type
     */
    private void playMedia(SectionResListVo resVo,int type) {
        new LetvVodHelperNew.VodVideoBuilder(activity)
                .setNewUI(true)//使用自定义UI
                .setTitle(resVo.getName())//视频标题
                .setAuthorId(resVo.getCreateId())
                .setResId(resVo.getResId()+"-"+resVo.getResType())
                .setResourceType(resVo.getResType())
                .setVuid(resVo.getVuid())
                .setUrl(resVo.getResourceUrl())
                .setMediaType(type)//设置媒体类型
                .setPackageName(MainApplication.getInstance().getPackageName())
                .setClassName("com.galaxyschool.app.wawaschool.medias.activity.VodPlayActivity")
                .setHideBtnMore(true)
                .setLeStatus(resVo.getLeStatus())
                .setIsPublic(false)
                .create();
    }

    public void getData() {
        loadFailedLayout.setVisibility(View.GONE);
        final RequestVo requestVo = new RequestVo();
        if(!isMyLive()){
            requestVo.addParams("token", getExtrasMemberId());
        }
        requestVo.addParams("liveId", getArguments().getString("liveId"));
        requestVo.addParams("type", getArguments().getInt("type"));
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetLiveResList + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefreshView.onHeaderRefreshComplete();
                LiveDetailsResVo result = JSON.parseObject(s,
                        new TypeReference<LiveDetailsResVo>() {
                        });
                if (result.getCode() == 0) {
                    loadFailedLayout.setVisibility(View.GONE);
                    sectionDetailsVo = result;
                    updateView();
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(getClass().getSimpleName(), "获取我的订单列表失败:" + throwable.getMessage());
                loadFailedLayout.setVisibility(View.VISIBLE);
                pullToRefreshView.onFooterRefreshComplete();
            }

            @Override
            public void onFinished() {
            }
        });
    }

    public void updateLiveInfo(LiveDetailsVo info){
        liveDetailsVo = info;
        schoolId = info.getLive().getSchoolId();
        canEdit = liveDetailsVo.isIsJoin() && !liveDetailsVo.isIsExpire()
                && UserHelper.isLogin();
        if(UserHelper.isLogin() && info != null) {
            isHost = info.getLive().getEmceeIds().contains(UserHelper.getUserId());
        }
        if(courseResListAdapter != null){
            courseResListAdapter.setNeedFlagRead((!isHost && isMyLive() && canEdit)
                    || (UserHelper.isLogin() && StringUtils.isValidString(getExtrasMemberId())));
        }
    }

    private boolean isMyLive(){
        return UserHelper.isLogin() && (!StringUtils.isValidString(getExtrasMemberId())
                || TextUtils.equals(getExtrasMemberId(), UserHelper.getUserId()));
    }

    private String getCurrentMemberId(){
        if(isMyLive()){
            return UserHelper.getUserId();
        }else{
            return getExtrasMemberId();
        }
    }

    private String getExtrasMemberId(){
        return getArguments().getString("memberId");
    }

    private void updateView() {
        courseResListAdapter.setData(null);
        if (sectionDetailsVo != null) {
            if (sectionDetailsVo.getData() != null) {
                if (sectionDetailsVo.getData().size() > 0) {
                    if (sectionDetailsVo.getData().get(0).getData() != null) {
                        List<SectionResListVo> voList = sectionDetailsVo.getData().get(0).getData();
                        if (voList.size() > 0) {
                            voList.get(0).setIsTitle(true);
                        }
                        for (SectionResListVo vo : voList) {
                            vo.setTaskName(getTaskName(0));
                            vo.setTaskType(sectionDetailsVo.getData().get(0).getTaskType());
                        }
                        courseResListAdapter.setData(voList);
                        listView.setAdapter(courseResListAdapter);
                    }
                }
                if (sectionDetailsVo.getData().size() > 1) {
                    if (sectionDetailsVo.getData().get(1).getData() != null) {
                        List<SectionResListVo> voList = sectionDetailsVo.getData().get(1).getData();
                        if (voList.size() > 0) {
                            voList.get(0).setIsTitle(true);
                        }
                        for (SectionResListVo vo : voList) {
                            vo.setTaskName(getTaskName(1));
                            vo.setTaskType(sectionDetailsVo.getData().get(1).getTaskType());
                        }
                        courseResListAdapter.addData(voList);
                    }
                }
                if (sectionDetailsVo.getData().size() > 2) {
                    if (sectionDetailsVo.getData().get(2).getData() != null) {
                        List<SectionResListVo> voList = sectionDetailsVo.getData().get(2).getData();
                        if (voList.size() > 0) {
                            voList.get(0).setIsTitle(true);
                        }
                        for (SectionResListVo vo : voList) {
                            vo.setTaskName(getTaskName(2));
                            vo.setTaskType(sectionDetailsVo.getData().get(2).getTaskType());
                        }
                        courseResListAdapter.addData(voList);
                    }
                }
                courseResListAdapter.notifyDataSetChanged();
            }
        }
    }

    @NonNull
    private String getTaskName(int i) {
        String taskName = "";
        int taskType = sectionDetailsVo.getData().get(i).getTaskType();
        if (taskType == 1) {//看课件
            taskName = getString(R.string.lq_watch_course);
        } else if (taskType == 2) {//复述课件
            taskName = getResources().getString(R.string.retell_course);
        }else if (taskType == 3) {//任务单
            taskName = getResources().getString(R.string.coursetask);
        }
        return taskName;
    }

    protected void flagRead(String id, String resId, int resType) {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("cwareId", id);
        requestVo.addParams("resId", resId);
        requestVo.addParams("resType", resType);
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.CommitLiveRes + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                ResponseVo<String> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<String>>() {
                        });
                if (result.getCode() == 0) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {

                    }
                    getData();
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                ToastUtil.showToast(activity, getResources().getString(R.string.net_error_tip));
            }

            @Override
            public void onFinished() {
            }
        });
    }
}
