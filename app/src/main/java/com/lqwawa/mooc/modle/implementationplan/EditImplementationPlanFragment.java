package com.lqwawa.mooc.modle.implementationplan;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.WatchWawaCourseResourceSplicingUtils;
import com.galaxyschool.app.wawaschool.fragment.ContactsListFragment;
import com.galaxyschool.app.wawaschool.pojo.MaterialResourceType;
import com.galaxyschool.app.wawaschool.pojo.MediaInfo;
import com.galaxyschool.app.wawaschool.pojo.ResourceInfoTag;
import com.galaxyschool.app.wawaschool.pojo.weike.MediaData;
import com.lqwawa.apps.views.ContainsEmojiEditText;
import com.lqwawa.client.pojo.ResourceInfo;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.mooc.adapter.SelectPictureListAdapter;
import com.lqwawa.mooc.common.GuidanceResourceType;
import com.lqwawa.mooc.common.GuidanceTaskUtils;
import com.lqwawa.mooc.factory.data.entity.ImplementationPlanEntity;

import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述: 课中实施方案
 * 作者|时间: djj on 2019/9/3 0003 上午 10:14
 */
public class EditImplementationPlanFragment extends ContactsListFragment {

    public static final String KEY_EXTRA_CHAPTER_ID = "KEY_EXTRA_CHAPTER_ID";
    public static final String KEY_EXTRA_MEMBER_ID = "KEY_EXTRA_MEMBER_ID";
    public static final String KEY_EXTRA_COURSE_ID = "KEY_EXTRA_COURSE_ID";
    public static final String KEY_EXTRA_CLASS_ID = "KEY_EXTRA_CLASS_ID";

    public static final int LEARNING_TARGET_TYPE = 1;
    public static final int MAIN_DIFFICULT_TYPE = 2;
    public static final int COMMON_PROBLEM_TYPE = 3;

    private View mRootView, inflate;
    private TopBar mTopBar;
    private ContainsEmojiEditText mLearningTargetEt, mMainDifficultyEt, mCommonProblemEt;
    private List<ResourceInfoTag> resourceInfoTagList1 = new ArrayList<>();
    private List<ResourceInfoTag> resourceInfoTagList2 = new ArrayList<>();
    private List<ResourceInfoTag> resourceInfoTagList3 = new ArrayList<>();
    private SelectPictureListAdapter mPictureListAdapter1;
    private SelectPictureListAdapter mPictureListAdapter2;
    private SelectPictureListAdapter mPictureListAdapter3;
    private FrameLayout mResetContainer;
    private Button  mBtnConfirm, mBtnEdit;
    private String mLearningTargetText, mMainDifficultyText, mCommonProblemText;
    private TextView mTvAccessories1,mTvAccessories2,mTvAccessories3;
    private RecyclerView  mRecyclerView1,mRecyclerView2,mRecyclerView3;

    private String chapterId;
    private String memberId;
    private String courseId;
    private String classId;
    private TextView cancel;
    private TextView takePhoto;
    private TextView choosePhoto;
    private Dialog dialog;
    private int accessoriesaType;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        GuidanceTaskUtils.getInstance().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_implementation_plan, container, false);
        return mRootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getIntent();
        initViews();
        initData();
    }

    //界面之间的值
    private void getIntent() {
        Bundle bundle = getArguments();
        chapterId = bundle.getString(KEY_EXTRA_CHAPTER_ID);
        memberId = bundle.getString(KEY_EXTRA_MEMBER_ID);
        courseId = bundle.getString(KEY_EXTRA_COURSE_ID);
        classId = bundle.getString(KEY_EXTRA_CLASS_ID);
    }

    //初始化控件
    private void initViews() {
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        mTopBar.setBack(true);
        mLearningTargetEt = (ContainsEmojiEditText) findViewById(R.id.learning_target_content);
        mMainDifficultyEt = (ContainsEmojiEditText) findViewById(R.id.main_difficulty_content);
        mCommonProblemEt = (ContainsEmojiEditText) findViewById(R.id.common_problem_content);
        mResetContainer = (FrameLayout) findViewById(R.id.reset_container);
        mBtnConfirm = (Button) findViewById(R.id.btn_confirm);
        mBtnEdit = (Button) findViewById(R.id.btn_edit);
        mBtnConfirm.setOnClickListener(this);
        mBtnEdit.setOnClickListener(this);
        mTvAccessories1 = (TextView) findViewById(R.id.tv_accessories_1);
        mTvAccessories2 = (TextView) findViewById(R.id.tv_accessories_2);
        mTvAccessories3 = (TextView) findViewById(R.id.tv_accessories_3);
        mRecyclerView1 = (RecyclerView) findViewById(R.id.recycler_view_1);
        mRecyclerView2 = (RecyclerView) findViewById(R.id.recycler_view_2);
        mRecyclerView3 = (RecyclerView) findViewById(R.id.recycler_view_3);
    }

    private void initData() {
        mTopBar.setTitle(getString(R.string.class_implementation_plan));
        mTopBar.setTitleWide(DensityUtil.dip2px(120));
        mRecyclerView1.setLayoutManager( new GridLayoutManager(getActivity(),5));
        mPictureListAdapter1 = new SelectPictureListAdapter(getActivity(),resourceInfoTagList1);
        mRecyclerView1.setAdapter(mPictureListAdapter1);
        mPictureListAdapter1.setOnItemClickListener(new SelectPictureListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                accessoriesaType = LEARNING_TARGET_TYPE;
                choosePhotoDialog();
            }
        });

        mRecyclerView2.setLayoutManager( new GridLayoutManager(getActivity(),5));
        mPictureListAdapter2 = new SelectPictureListAdapter(getActivity(),resourceInfoTagList2);
        mRecyclerView2.setAdapter(mPictureListAdapter2);
        mPictureListAdapter2.setOnItemClickListener(new SelectPictureListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                accessoriesaType = MAIN_DIFFICULT_TYPE;
                choosePhotoDialog();
            }
        });

        mRecyclerView3.setLayoutManager( new GridLayoutManager(getActivity(),5));
        mPictureListAdapter3 = new SelectPictureListAdapter(getActivity(),resourceInfoTagList3);
        mRecyclerView3.setAdapter(mPictureListAdapter3);
        mPictureListAdapter3.setOnItemClickListener(new SelectPictureListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                accessoriesaType = COMMON_PROBLEM_TYPE;
                choosePhotoDialog();
            }
        });

        queryIfExistPlan();
    }

    private void queryIfExistPlan() {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("chapterId", chapterId);
        if (!TextUtils.isEmpty(memberId)) {
            requestVo.addParams("token", memberId);
        }
        requestVo.addParams("classId",classId);
        RequestParams params = new RequestParams(AppConfig.ServerUrl.postQueryIfExistPlan);
        params.setAsJsonContent(true);
        params.setBodyContent(requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                ResponseVo<String> results = JSON.parseObject(result,
                        new TypeReference<ResponseVo<String>>() {
                        });
                if (results.getCode() == 0) {
                    //创建过
                    if (results.isExist()) {
                        getImplementPlan();
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                UIUtil.showToastSafe(com.lqwawa.intleducation.R.string.net_error_tip);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }


    //获取课中实施方案
    private void getImplementPlan() {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("chapterId", chapterId);
        if (!TextUtils.isEmpty(memberId)) {
            requestVo.addParams("token", memberId);
        }
        requestVo.addParams("classId", classId);
        RequestParams params = new RequestParams(AppConfig.ServerUrl.postGetImplementPlan);
        params.setAsJsonContent(true);
        params.setBodyContent(requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                ResponseVo<ImplementationPlanEntity> results = JSON.parseObject(result,
                        new TypeReference<ResponseVo<ImplementationPlanEntity>>() {
                        });
                if (results.isSucceed()) {
                    ImplementationPlanEntity planEntity = results.getData();
                    configPlanData(planEntity);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                UIUtil.showToastSafe(com.lqwawa.intleducation.R.string.net_error_tip);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void configPlanData(ImplementationPlanEntity planEntity) {
        mResetContainer.setVisibility(View.GONE);
        mLearningTargetEt.setText(planEntity.getLearningGoal());
        mMainDifficultyEt.setText(planEntity.getDifficultPoint());
        mCommonProblemEt.setText(planEntity.getCommonProblem());

        String lgAppendixId = planEntity.getLgAppendixId();
        String lgAppendixUrl = planEntity.getLgAppendixUrl();
        String dpAppendixId = planEntity.getDpAppendixId();
        String dpAppendixUrl = planEntity.getDpAppendixUrl();
        String cpAppendixId = planEntity.getCpAppendixId();
        String cpAppendixUrl = planEntity.getCpAppendixUrl();

        if (lgAppendixUrl != null) {
            mTvAccessories1.setText(getString(R.string.label_attachments));
        }
        if (dpAppendixUrl != null) {
            mTvAccessories2.setText(getString(R.string.label_attachments));
        }
        if (cpAppendixUrl != null) {
            mTvAccessories3.setText(getString(R.string.label_attachments));
        }
        ArrayList<ResourceInfoTag> lgResourceInfoTags = new ArrayList<>();
        ResourceInfoTag lgInfoTag = new ResourceInfoTag();
        String[] lgTempId = lgAppendixId.split(",");
        String[] lgTempUrl = lgAppendixUrl.split(",");
        for (int i = 0; i < lgTempId.length; i++) {
            lgInfoTag.setResId(lgTempId[i]);
            lgInfoTag.setImgPath(lgTempUrl[i]);
            lgInfoTag.setResourcePath(lgTempUrl[i]);
            lgResourceInfoTags.add(lgInfoTag);
        }

        this.resourceInfoTagList1.addAll(lgResourceInfoTags);
        mRecyclerView1.setAdapter(mPictureListAdapter1);

        ArrayList<ResourceInfoTag> dpResourceInfoTags = new ArrayList<>();
        ResourceInfoTag dpInfoTag = new ResourceInfoTag();
        String[] dpTempId = dpAppendixId.split(",");
        String[] dpTempUrl = dpAppendixUrl.split(",");
        for (int i = 0; i < dpTempId.length; i++) {
            dpInfoTag.setResId(dpTempId[i]);
            dpInfoTag.setImgPath(dpTempUrl[i]);
            dpInfoTag.setResourcePath(dpTempUrl[i]);
            dpResourceInfoTags.add(dpInfoTag);
        }

        this.resourceInfoTagList2.addAll(dpResourceInfoTags);
        mRecyclerView2.setAdapter(mPictureListAdapter2);

        ArrayList<ResourceInfoTag> cpResourceInfoTags = new ArrayList<>();
        ResourceInfoTag cpInfoTag = new ResourceInfoTag();
        String[] cpTempId = cpAppendixId.split(",");
        String[] cpTempUrl = cpAppendixUrl.split(",");
        for (int i = 0; i < cpTempId.length; i++) {
            cpInfoTag.setResId(cpTempId[i]);
            cpInfoTag.setImgPath(cpTempUrl[i]);
            cpInfoTag.setResourcePath(cpTempUrl[i]);
            cpResourceInfoTags.add(cpInfoTag);
        }

        this.resourceInfoTagList3.addAll(cpResourceInfoTags);
        mRecyclerView3.setAdapter(mPictureListAdapter3);

    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.abroad_choosephoto) {
            doGuidanceTypeWork(GuidanceResourceType.PHOTO);
            dialog.dismiss();
        } else if (id == R.id.abroad_takephoto) {
            doGuidanceTypeWork(GuidanceResourceType.TAKE_CAMERA);
            dialog.dismiss();
        } else if (id == R.id.abroad_choose_cancel) {
            dialog.dismiss();
        } else if (id == R.id.btn_confirm) {
            getEditContent();
            confirmPlanData();
        }
    }

    //确认
    private void confirmPlanData() {
        String lgAppendixId = null;
        String lgAppendixUrl = null;
        for (int i = 0; i < resourceInfoTagList1.size(); i++) {
            ResourceInfoTag data = resourceInfoTagList1.get(i);
            if (i == 0) {
                lgAppendixId = data.getResId();
                lgAppendixUrl = data.getResourcePath();
            } else {
                lgAppendixId = lgAppendixId + "," + data.getResId();
                lgAppendixUrl = lgAppendixUrl + "," + data.getResourcePath();
            }
        }

        String dpAppendixId = null;
        String dpAppendixUrl = null;
        for (int i = 0; i < resourceInfoTagList2.size(); i++) {
            ResourceInfoTag data = resourceInfoTagList2.get(i);
            if (i == 0) {
                dpAppendixId = data.getResId();
                dpAppendixUrl = data.getResourcePath();
            } else {
                dpAppendixId = dpAppendixId + "," + data.getResId();
                dpAppendixUrl = dpAppendixUrl + "," + data.getResourcePath();
            }
        }

        String cpAppendixId = null;
        String cpAppendixUrl = null;
        for (int i = 0; i < resourceInfoTagList3.size(); i++) {
            ResourceInfoTag data = resourceInfoTagList3.get(i);
            if (i == 0) {
                cpAppendixId = data.getResId();
                cpAppendixUrl = data.getResourcePath();
            } else {
                cpAppendixId = cpAppendixId + "," + data.getResId();
                cpAppendixUrl = cpAppendixUrl + "," + data.getResourcePath();
            }
        }

        RequestVo requestVo = new RequestVo();
        requestVo.addParams("chapterId", chapterId);
        requestVo.addParams("courseId", courseId);
        if (!TextUtils.isEmpty(memberId)) {
            requestVo.addParams("token", memberId);
        }
        requestVo.addParams("classId", classId);
        requestVo.addParams("learningGoal", mLearningTargetText);
        requestVo.addParams("lgAppendixId", lgAppendixId);
        requestVo.addParams("lgAppendixUrl", lgAppendixUrl);
        requestVo.addParams("difficultPoint", mMainDifficultyText);
        requestVo.addParams("dpAppendixId", dpAppendixId);
        requestVo.addParams("dpAppendixUrl", dpAppendixUrl);
        requestVo.addParams("commonProblem", mCommonProblemText);
        requestVo.addParams("cpAppendixId", cpAppendixId);
        requestVo.addParams("cpAppendixUrl", cpAppendixUrl);
        RequestParams params = new RequestParams(AppConfig.ServerUrl.postSaveImplementPlan);
        params.setAsJsonContent(true);
        params.setBodyContent(requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                ResponseVo<String> results = JSON.parseObject(result,
                        new TypeReference<ResponseVo<String>>() {
                        });
                if (results.isSucceed()) {
                    getActivity().finish();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                UIUtil.showToastSafe(com.lqwawa.intleducation.R.string.net_error_tip);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    //选择照片方式弹框
    public void choosePhotoDialog() {
        dialog = new Dialog(getContext(), R.style.ActionSheetDialogStyle);
        inflate = LayoutInflater.from(getContext()).inflate(R.layout.view_choosephoto_dialog, null);
        choosePhoto = (TextView) inflate.findViewById(R.id.abroad_choosephoto);
        takePhoto = (TextView) inflate.findViewById(R.id.abroad_takephoto);
        cancel = (TextView) inflate.findViewById(R.id.abroad_choose_cancel);
        choosePhoto.setOnClickListener(this);
        takePhoto.setOnClickListener(this);
        cancel.setOnClickListener(this);
        dialog.setContentView(inflate);
        Window window = dialog.getWindow();
        if (dialog != null && window != null) {
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams attr = window.getAttributes();
            if (attr != null) {
                attr.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                attr.width = ViewGroup.LayoutParams.MATCH_PARENT;
                attr.gravity = Gravity.BOTTOM;//设置dialog 在布局中的位置
                window.setAttributes(attr);
            }
        }
        dialog.show();
    }

    private void doGuidanceTypeWork(int guidanceType) {
        GuidanceTaskUtils.getInstance()
                .setContext(getActivity())
                .setFromStudyTaskIntro(true)
                .setCallBackListener(result -> updateListData((List<MediaData>) result))
                .doGuidanceTypeWork(guidanceType);
    }

    private void updateListData(List<MediaData> datas) {
        ArrayList<ResourceInfoTag> resourceInfoTags = new ArrayList<>();
        for (int i = 0; i < datas.size(); i++) {
            MediaData mediaData = datas.get(i);
            MediaInfo mediaInfo = new MediaInfo();
            mediaInfo.setResourceUrl(mediaData.resourceurl);
            mediaInfo.setTitle(mediaData.originname);
            mediaInfo.setAuthor(mediaData.createname);
            mediaInfo.setThumbnail(mediaData.resourceurl);
            mediaInfo.setResourceType(mediaData.type);
            mediaInfo.setPath(mediaData.resourceurl);
            mediaInfo.setMediaType(mediaData.type);
            mediaInfo.setMicroId(String.valueOf(mediaData.id));
            mediaInfo.setAuthorId(mediaData.createid);
            ResourceInfoTag infoTag = WatchWawaCourseResourceSplicingUtils.getResourceInfoTagByMediaInfo(mediaInfo, mediaData.type);
            //增加分页
            if (mediaData.type == MaterialResourceType.PICTURE) {
                List<ResourceInfo> splitInfo = new ArrayList<>();
                ResourceInfo newResourceInfo = new ResourceInfo();
                newResourceInfo.setAuthorId(mediaInfo.getAuthorId());
                newResourceInfo.setTitle(mediaInfo.getTitle());
                newResourceInfo.setImgPath(mediaInfo.getThumbnail());
                newResourceInfo.setResourcePath(mediaInfo.getResourceUrl());
                newResourceInfo.setResId(infoTag.getResId());
                newResourceInfo.setResourceType(mediaInfo.getResourceType());
                splitInfo.add(newResourceInfo);
                infoTag.setSplitInfoList(splitInfo);
            }
            resourceInfoTags.add(infoTag);
        }
        if (resourceInfoTags.size() > 0) {
            if (accessoriesaType == LEARNING_TARGET_TYPE) {
                this.resourceInfoTagList1.addAll(resourceInfoTags);
                if (mPictureListAdapter1 != null) {
                    mPictureListAdapter1.update(this.resourceInfoTagList1);
                }
            } else if (accessoriesaType == MAIN_DIFFICULT_TYPE) {
                this.resourceInfoTagList2.addAll(resourceInfoTags);
                if (mPictureListAdapter2 != null) {
                    mPictureListAdapter2.update(this.resourceInfoTagList2);
                }
            } else if (accessoriesaType == COMMON_PROBLEM_TYPE) {
                this.resourceInfoTagList3.addAll(resourceInfoTags);
                if (mPictureListAdapter3 != null) {
                    mPictureListAdapter3.update(this.resourceInfoTagList3);
                }
            }
        }
    }

    private void getEditContent() {
        mLearningTargetText = mLearningTargetEt.getText().toString().trim();
        mMainDifficultyText = mMainDifficultyEt.getText().toString().trim();
        mCommonProblemText = mCommonProblemEt.getText().toString().trim();
    }
}
