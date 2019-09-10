package com.lqwawa.mooc.modle.implementationplan;


import android.app.Dialog;
import android.content.DialogInterface;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.WatchWawaCourseResourceSplicingUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.fragment.ContactsListFragment;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.imagebrowser.GalleryActivity;
import com.galaxyschool.app.wawaschool.pojo.MaterialResourceType;
import com.galaxyschool.app.wawaschool.pojo.MediaInfo;
import com.galaxyschool.app.wawaschool.pojo.ResourceInfoTag;
import com.galaxyschool.app.wawaschool.pojo.weike.MediaData;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.libs.gallery.ImageInfo;
import com.lqwawa.apps.views.ContainsEmojiEditText;
import com.lqwawa.client.pojo.ResourceInfo;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.mooc.EditImplementationPlanActivity;
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
public class ImplementationPlanFragment extends ContactsListFragment {

    public static final String KEY_EXTRA_CHAPTER_ID = "KEY_EXTRA_CHAPTER_ID";
    public static final String KEY_EXTRA_MEMBER_ID = "KEY_EXTRA_MEMBER_ID";
    public static final String KEY_EXTRA_COURSE_ID = "KEY_EXTRA_COURSE_ID";
    public static final String KEY_EXTRA_CLASS_ID = "KEY_EXTRA_CLASS_ID";

    public static final int LEARNING_TARGET_TYPE = 1;
    public static final int MAIN_DIFFICULT_TYPE = 2;
    public static final int COMMON_PROBLEM_TYPE = 3;
    public static final int STEP_TYPE = 4;
    public static final int EDIT_MODE = 2;

    private View mRootView, inflate;
    private TopBar mTopBar;
    private ContainsEmojiEditText mLearningTargetEt, mMainDifficultyEt, mCommonProblemEt, mStepEt;
    private Button mBtnReset, mBtnConfirm, mBtnEdit;
    private LinearLayout mAddAccessories1, mAddAccessories2, mAddAccessories3, mAddAccessories4;
    private TextView mTvAccessories1, mTvAccessories2, mTvAccessories3, mTvAccessories4;
    private List<ResourceInfoTag> resourceInfoTagList1 = new ArrayList<>();
    private List<ResourceInfoTag> resourceInfoTagList2 = new ArrayList<>();
    private List<ResourceInfoTag> resourceInfoTagList3 = new ArrayList<>();
    private List<ResourceInfoTag> resourceInfoTagList4 = new ArrayList<>();
    private SelectPictureListAdapter mPictureListAdapter1;
    private SelectPictureListAdapter mPictureListAdapter2;
    private SelectPictureListAdapter mPictureListAdapter3;
    private SelectPictureListAdapter mPictureListAdapter4;
    private RecyclerView mRecyclerView1, mRecyclerView2, mRecyclerView3, mRecyclerView4;
    private LinearLayout mBottomLayout, mBottomLayout1;

    private String mLearningTargetText, mMainDifficultyText, mCommonProblemText, mStepText;
    private String chapterId, memberId, courseId, classId;
    private TextView choosePhoto, takePhoto, cancel;
    private Dialog dialog;
    private int accessoriesaType;
    private int selectMode = 1;

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
        mStepEt = (ContainsEmojiEditText) findViewById(R.id.step_content);
        mBtnReset = (Button) findViewById(R.id.btn_reset);
        mBtnConfirm = (Button) findViewById(R.id.btn_confirm);
        mBtnEdit = (Button) findViewById(R.id.btn_edit);
        mBtnReset.setOnClickListener(this);
        mBtnConfirm.setOnClickListener(this);
        mBtnEdit.setOnClickListener(this);
        mAddAccessories1 = (LinearLayout) findViewById(R.id.layout_add_accessories_1);
        mAddAccessories2 = (LinearLayout) findViewById(R.id.layout_add_accessories_2);
        mAddAccessories3 = (LinearLayout) findViewById(R.id.layout_add_accessories_3);
        mAddAccessories4 = (LinearLayout) findViewById(R.id.layout_add_accessories_4);
        mTvAccessories1 = (TextView) findViewById(R.id.tv_accessories_1);
        mTvAccessories2 = (TextView) findViewById(R.id.tv_accessories_2);
        mTvAccessories3 = (TextView) findViewById(R.id.tv_accessories_3);
        mTvAccessories4 = (TextView) findViewById(R.id.tv_accessories_4);
        mRecyclerView1 = (RecyclerView) findViewById(R.id.recycler_view_1);
        mRecyclerView2 = (RecyclerView) findViewById(R.id.recycler_view_2);
        mRecyclerView3 = (RecyclerView) findViewById(R.id.recycler_view_3);
        mRecyclerView4 = (RecyclerView) findViewById(R.id.recycler_view_4);
        mBottomLayout = (LinearLayout) findViewById(R.id.bottom_layout);
        mBottomLayout1 = (LinearLayout) findViewById(R.id.bottom_layout_1);
    }

    private void initData() {
        mTopBar.setTitle(getString(R.string.class_implementation_plan));
        mTopBar.setTitleWide(DensityUtil.dip2px(120));
        queryIfExistPlan();
    }

    private void toImageActivity(List<ResourceInfoTag> resourceInfoTagList, int position) {
        ArrayList<ImageInfo> imageItemInfos = new ArrayList<>();
        if (resourceInfoTagList != null && resourceInfoTagList.size() > 0) {
            for (int i = 0; i < resourceInfoTagList.size(); i++) {
                ResourceInfoTag infoTag = resourceInfoTagList.get(i);
                ImageInfo newResourceInfo = new ImageInfo();
                newResourceInfo.setTitle(infoTag.getTitle());
                newResourceInfo.setResourceUrl(AppSettings.getFileUrl(infoTag.getResourcePath()));
                newResourceInfo.setResourceId(infoTag.getResId());
                newResourceInfo.setResourceType(infoTag.getType());
                newResourceInfo.setAuthorId(infoTag.getAuthorId());
                imageItemInfos.add(newResourceInfo);
            }
        }
        if (imageItemInfos != null && imageItemInfos.size() > 0) {
            GalleryActivity.newInstance(getActivity(), imageItemInfos, true, 0, false, false, false);
        }

    }

    private void queryIfExistPlan() {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("chapterId", chapterId);
        if (!TextUtils.isEmpty(memberId)) {
            requestVo.addParams("token", memberId);
        }
        requestVo.addParams("classId", classId);
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
                        mLearningTargetEt.setMinLines(1);
                        mLearningTargetEt.setBackground(null);
                        mLearningTargetEt.setFocusableInTouchMode(false);
                        mLearningTargetEt.setEnabled(false);
                        mMainDifficultyEt.setMinLines(1);
                        mMainDifficultyEt.setBackground(null);
                        mMainDifficultyEt.setFocusableInTouchMode(false);
                        mMainDifficultyEt.setEnabled(false);
                        mStepEt.setMinLines(1);
                        mStepEt.setBackground(null);
                        mStepEt.setFocusableInTouchMode(false);
                        mStepEt.setEnabled(false);
                        mCommonProblemEt.setMinLines(1);
                        mCommonProblemEt.setBackground(null);
                        mCommonProblemEt.setFocusableInTouchMode(false);
                        mCommonProblemEt.setEnabled(false);
                        mBottomLayout.setVisibility(View.GONE);
                        mBottomLayout1.setVisibility(View.VISIBLE);
                        selectMode = EDIT_MODE;
                        getImplementPlan();
                        configPlanView();
                    }else {
                        configPlanView();
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

    private void configPlanView() {
        mRecyclerView1.setLayoutManager(new GridLayoutManager(getActivity(), 5));
        mPictureListAdapter1 = new SelectPictureListAdapter(getActivity(), resourceInfoTagList1, selectMode);
        mRecyclerView1.setAdapter(mPictureListAdapter1);
        mPictureListAdapter1.setOnItemClickListener(new SelectPictureListAdapter.OnItemClickListener() {
            @Override
            public void onAddItemClick(View view, int position) {
                accessoriesaType = LEARNING_TARGET_TYPE;
                choosePhotoDialog();
            }

            @Override
            public void onItemClick(View view, int position) {
                accessoriesaType = LEARNING_TARGET_TYPE;
                toImageActivity(resourceInfoTagList1, position);
            }
        });

        mRecyclerView2.setLayoutManager(new GridLayoutManager(getActivity(), 5));
        mPictureListAdapter2 = new SelectPictureListAdapter(getActivity(), resourceInfoTagList2, selectMode);
        mRecyclerView2.setAdapter(mPictureListAdapter2);
        mPictureListAdapter2.setOnItemClickListener(new SelectPictureListAdapter.OnItemClickListener() {
            @Override
            public void onAddItemClick(View view, int position) {
                accessoriesaType = MAIN_DIFFICULT_TYPE;
                choosePhotoDialog();
            }

            @Override
            public void onItemClick(View view, int position) {
                accessoriesaType = MAIN_DIFFICULT_TYPE;
                toImageActivity(resourceInfoTagList2, position);
            }
        });

        mRecyclerView3.setLayoutManager(new GridLayoutManager(getActivity(), 5));
        mPictureListAdapter3 = new SelectPictureListAdapter(getActivity(), resourceInfoTagList3, selectMode);
        mRecyclerView3.setAdapter(mPictureListAdapter3);
        mPictureListAdapter3.setOnItemClickListener(new SelectPictureListAdapter.OnItemClickListener() {
            @Override
            public void onAddItemClick(View view, int position) {
                accessoriesaType = COMMON_PROBLEM_TYPE;
                choosePhotoDialog();
            }

            @Override
            public void onItemClick(View view, int position) {
                accessoriesaType = COMMON_PROBLEM_TYPE;
                toImageActivity(resourceInfoTagList3, position);
            }
        });

        mRecyclerView4.setLayoutManager(new GridLayoutManager(getActivity(), 5));
        mPictureListAdapter4 = new SelectPictureListAdapter(getActivity(), resourceInfoTagList4, selectMode);
        mRecyclerView4.setAdapter(mPictureListAdapter4);
        mPictureListAdapter4.setOnItemClickListener(new SelectPictureListAdapter.OnItemClickListener() {
            @Override
            public void onAddItemClick(View view, int position) {
                accessoriesaType = STEP_TYPE;
                choosePhotoDialog();
            }

            @Override
            public void onItemClick(View view, int position) {
                accessoriesaType = STEP_TYPE;
                toImageActivity(resourceInfoTagList4, position);
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
        if (EmptyUtil.isNotEmpty(planEntity) && planEntity.getLearningGoal() != null) {
            if (TextUtils.isEmpty(planEntity.getLearningGoal())){
                mLearningTargetEt.setText(getString(R.string.no_content));
            }else {
                mLearningTargetEt.setText(planEntity.getLearningGoal());
            }
            if (TextUtils.isEmpty(planEntity.getDifficultPoint())){
                mMainDifficultyEt.setText(getString(R.string.no_content));
            }else {
                mMainDifficultyEt.setText(planEntity.getDifficultPoint());
            }
            if (TextUtils.isEmpty(planEntity.getStep())){
                mStepEt.setText(getString(R.string.no_content));
            }else {
                mStepEt.setText(planEntity.getStep());
            }
            if (TextUtils.isEmpty(planEntity.getCommonProblem())){
                mCommonProblemEt.setText(getString(R.string.no_content));
            }else {
                mCommonProblemEt.setText(planEntity.getCommonProblem());
            }
        }

        String lgAppendixId = planEntity.getLgAppendixId();
        String lgAppendixUrl = planEntity.getLgAppendixUrl();
        String dpAppendixId = planEntity.getDpAppendixId();
        String dpAppendixUrl = planEntity.getDpAppendixUrl();
        String cpAppendixId = planEntity.getCpAppendixId();
        String cpAppendixUrl = planEntity.getCpAppendixUrl();
        String sAppendixId = planEntity.getStepId();
        String sAppendixUrl = planEntity.getStepUrl();

        if (!TextUtils.isEmpty(lgAppendixUrl)) {
            mTvAccessories1.setText(getString(R.string.label_attachments));
        } else {
            mAddAccessories1.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(dpAppendixUrl)) {
            mTvAccessories2.setText(getString(R.string.label_attachments));
        } else {
            mAddAccessories2.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(sAppendixUrl)){
            mTvAccessories4.setText(getString(R.string.label_attachments));
        }else {
            mAddAccessories4.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(cpAppendixUrl)) {
            mTvAccessories3.setText(getString(R.string.label_attachments));
        } else {
            mAddAccessories3.setVisibility(View.GONE);
        }
        ArrayList<ResourceInfoTag> lgResourceInfoTags = new ArrayList<>();
        if (!TextUtils.isEmpty(lgAppendixId)) {
            if (lgAppendixId.contains(",")) {
                String[] lgTempId = lgAppendixId.split(",");
                String[] lgTempUrl = lgAppendixUrl.split(",");
                for (int i = 0; i < lgTempId.length; i++) {
                    ResourceInfoTag lgInfoTag = new ResourceInfoTag();
                    lgInfoTag.setResId(lgTempId[i]);
                    lgInfoTag.setImgPath(lgTempUrl[i]);
                    lgInfoTag.setResourcePath(lgTempUrl[i]);
                    lgResourceInfoTags.add(lgInfoTag);
                }
            } else {
                ResourceInfoTag lgInfoTag = new ResourceInfoTag();
                lgInfoTag.setResId(lgAppendixId);
                lgInfoTag.setImgPath(lgAppendixUrl);
                lgInfoTag.setResourcePath(lgAppendixUrl);
                lgResourceInfoTags.add(lgInfoTag);
            }
        }

        this.resourceInfoTagList1.addAll(lgResourceInfoTags);
        mRecyclerView1.setAdapter(mPictureListAdapter1);

//        mPictureListAdapter1 = new SelectPictureListAdapter(getActivity(), resourceInfoTagList1, selectMode);
//        mRecyclerView1.setAdapter(mPictureListAdapter1);
//        mPictureListAdapter1.setOnItemClickListener(new SelectPictureListAdapter.OnItemClickListener() {
//            @Override
//            public void onAddItemClick(View view, int position) {
//
//            }
//
//            @Override
//            public void onItemClick(View view, int position) {
//                accessoriesaType = LEARNING_TARGET_TYPE;
//                toImageActivity(resourceInfoTagList1, position);
//            }
//        });

        ArrayList<ResourceInfoTag> dpResourceInfoTags = new ArrayList<>();
        if (!TextUtils.isEmpty(dpAppendixId)) {
            if (dpAppendixId.contains(",")) {
                String[] dpTempId = dpAppendixId.split(",");
                String[] dpTempUrl = dpAppendixUrl.split(",");
                for (int i = 0; i < dpTempId.length; i++) {
                    ResourceInfoTag dpInfoTag = new ResourceInfoTag();
                    dpInfoTag.setResId(dpTempId[i]);
                    dpInfoTag.setImgPath(dpTempUrl[i]);
                    dpInfoTag.setResourcePath(dpTempUrl[i]);
                    dpResourceInfoTags.add(dpInfoTag);
                }
            } else {
                ResourceInfoTag dpInfoTag = new ResourceInfoTag();
                dpInfoTag.setResId(dpAppendixId);
                dpInfoTag.setImgPath(dpAppendixUrl);
                dpInfoTag.setResourcePath(dpAppendixUrl);
                dpResourceInfoTags.add(dpInfoTag);
            }
        }

        this.resourceInfoTagList2.addAll(dpResourceInfoTags);
//        mPictureListAdapter2 = new SelectPictureListAdapter(getActivity(), resourceInfoTagList2, selectMode);
        mRecyclerView2.setAdapter(mPictureListAdapter2);
//        mPictureListAdapter2.setOnItemClickListener(new SelectPictureListAdapter.OnItemClickListener() {
//            @Override
//            public void onAddItemClick(View view, int position) {
//
//            }
//
//            @Override
//            public void onItemClick(View view, int position) {
//                accessoriesaType = MAIN_DIFFICULT_TYPE;
//                toImageActivity(resourceInfoTagList2, position);
//            }
//        });

        ArrayList<ResourceInfoTag> cpResourceInfoTags = new ArrayList<>();
        if (!TextUtils.isEmpty(cpAppendixId)) {
            if (cpAppendixId.contains(",")) {
                String[] cpTempId = cpAppendixId.split(",");
                String[] cpTempUrl = cpAppendixUrl.split(",");
                for (int i = 0; i < cpTempId.length; i++) {
                    ResourceInfoTag cpInfoTag = new ResourceInfoTag();
                    cpInfoTag.setResId(cpTempId[i]);
                    cpInfoTag.setImgPath(cpTempUrl[i]);
                    cpInfoTag.setResourcePath(cpTempUrl[i]);
                    cpResourceInfoTags.add(cpInfoTag);
                }
            } else {
                ResourceInfoTag cpInfoTag = new ResourceInfoTag();
                cpInfoTag.setResId(cpAppendixId);
                cpInfoTag.setImgPath(cpAppendixUrl);
                cpInfoTag.setResourcePath(cpAppendixUrl);
                cpResourceInfoTags.add(cpInfoTag);
            }
        }

        this.resourceInfoTagList3.addAll(cpResourceInfoTags);
//        mPictureListAdapter3 = new SelectPictureListAdapter(getActivity(), resourceInfoTagList3, selectMode);
        mRecyclerView3.setAdapter(mPictureListAdapter3);
//        mPictureListAdapter3.setOnItemClickListener(new SelectPictureListAdapter.OnItemClickListener() {
//            @Override
//            public void onAddItemClick(View view, int position) {
//
//            }
//
//            @Override
//            public void onItemClick(View view, int position) {
//                accessoriesaType = COMMON_PROBLEM_TYPE;
//                toImageActivity(resourceInfoTagList3, position);
//            }
//        });

        ArrayList<ResourceInfoTag> sResourceInfoTags = new ArrayList<>();
        if (!TextUtils.isEmpty(sAppendixId)) {
            if (sAppendixId.contains(",")) {
                String[] sTempId = sAppendixId.split(",");
                String[] sTempUrl = sAppendixUrl.split(",");
                for (int i = 0; i < sTempId.length; i++) {
                    ResourceInfoTag sInfoTag = new ResourceInfoTag();
                    sInfoTag.setResId(sTempId[i]);
                    sInfoTag.setImgPath(sTempUrl[i]);
                    sInfoTag.setResourcePath(sTempUrl[i]);
                    sResourceInfoTags.add(sInfoTag);
                }
            } else {
                ResourceInfoTag sInfoTag = new ResourceInfoTag();
                sInfoTag.setResId(sAppendixId);
                sInfoTag.setImgPath(sAppendixUrl);
                sInfoTag.setResourcePath(sAppendixUrl);
                sResourceInfoTags.add(sInfoTag);
            }
        }

        this.resourceInfoTagList4.addAll(sResourceInfoTags);
//        mPictureListAdapter4 = new SelectPictureListAdapter(getActivity(), resourceInfoTagList4, selectMode);
        mRecyclerView4.setAdapter(mPictureListAdapter4);
//        mPictureListAdapter4.setOnItemClickListener(new SelectPictureListAdapter.OnItemClickListener() {
//            @Override
//            public void onAddItemClick(View view, int position) {
//
//            }
//
//            @Override
//            public void onItemClick(View view, int position) {
//                accessoriesaType = STEP_TYPE;
//                toImageActivity(resourceInfoTagList4, position);
//            }
//        });

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
        } else if (id == R.id.btn_reset) {
            resetPlanData();
        } else if (id == R.id.btn_confirm) {
            getEditContent();
            if (TextUtils.isEmpty(mLearningTargetText) && TextUtils.isEmpty(mMainDifficultyText) &&
                    TextUtils.isEmpty(mCommonProblemText) && TextUtils.isEmpty(mStepText) &&
                    resourceInfoTagList1.isEmpty() && resourceInfoTagList2.isEmpty() &&
                    resourceInfoTagList3.isEmpty() && resourceInfoTagList4.isEmpty()) {
                confirmPlanDialog();
            } else {
                confirmPlanData();
            }
        } else if (id == R.id.btn_edit) {
            //编辑界面
            EditImplementationPlanActivity.start(getActivity(), chapterId, memberId, courseId, classId);
        }
    }

    private void confirmPlanDialog() {
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(getActivity(), null,
                getString(R.string.str_no_edit_content), getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                getActivity().finish();
            }
        });
        messageDialog.show();
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
                if (resourceInfoTags.size() + resourceInfoTagList1.size() > 10) {
                    int count = 10 - resourceInfoTagList1.size();
                    TipsHelper.showToast(getActivity(), String.format(UIUtil.getString(R.string.str_max_select_picture_limit), count));
                    return;
                }
                this.resourceInfoTagList1.addAll(resourceInfoTags);
                if (mPictureListAdapter1 != null) {
                    mPictureListAdapter1.update(this.resourceInfoTagList1);
                }
            } else if (accessoriesaType == MAIN_DIFFICULT_TYPE) {
                if (resourceInfoTags.size() + resourceInfoTagList2.size() > 10) {
                    int count = 10 - resourceInfoTagList2.size();
                    TipsHelper.showToast(getActivity(), String.format(UIUtil.getString(R.string.str_max_select_picture_limit), count));
                    return;
                }
                this.resourceInfoTagList2.addAll(resourceInfoTags);
                if (mPictureListAdapter2 != null) {
                    mPictureListAdapter2.update(this.resourceInfoTagList2);
                }
            } else if (accessoriesaType == COMMON_PROBLEM_TYPE) {
                if (resourceInfoTags.size() + resourceInfoTagList3.size() > 10) {
                    int count = 10 - resourceInfoTagList3.size();
                    TipsHelper.showToast(getActivity(), String.format(UIUtil.getString(R.string.str_max_select_picture_limit), count));
                    return;
                }
                this.resourceInfoTagList3.addAll(resourceInfoTags);
                if (mPictureListAdapter3 != null) {
                    mPictureListAdapter3.update(this.resourceInfoTagList3);
                }
            } else if (accessoriesaType == STEP_TYPE) {
                if (resourceInfoTags.size() + resourceInfoTagList4.size() > 10) {
                    int count = 10 - resourceInfoTagList4.size();
                    TipsHelper.showToast(getActivity(), String.format(UIUtil.getString(R.string.str_max_select_picture_limit), count));
                    return;
                }
                this.resourceInfoTagList4.addAll(resourceInfoTags);
                if (mPictureListAdapter4 != null) {
                    mPictureListAdapter4.update(this.resourceInfoTagList4);
                }
            }
        }
    }

    private void getEditContent() {
        mLearningTargetText = mLearningTargetEt.getText().toString().trim();
        mMainDifficultyText = mMainDifficultyEt.getText().toString().trim();
        mCommonProblemText = mCommonProblemEt.getText().toString().trim();
        mStepText = mStepEt.getText().toString().trim();
    }

    //确认
    private void confirmPlanData() {
        String lgAppendixId = "";
        String lgAppendixUrl = "";
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

        String dpAppendixId = "";
        String dpAppendixUrl = "";
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

        String cpAppendixId = "";
        String cpAppendixUrl = "";
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

        String sAppendixId = "";
        String sAppendixUrl = "";
        for (int i = 0; i < resourceInfoTagList4.size(); i++) {
            ResourceInfoTag data = resourceInfoTagList4.get(i);
            if (i == 0) {
                sAppendixId = data.getResId();
                sAppendixUrl = data.getResourcePath();
            } else {
                sAppendixId = sAppendixId + "," + data.getResId();
                sAppendixUrl = sAppendixUrl + "," + data.getResourcePath();
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
        requestVo.addParams("step", mStepText);
        requestVo.addParams("stepId", sAppendixId);
        requestVo.addParams("stepUrl", sAppendixUrl);
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

    //重置
    private void resetPlanData() {
        mLearningTargetEt.setText("");
        mMainDifficultyEt.setText("");
        mCommonProblemEt.setText("");
        mStepEt.setText("");
        if (mPictureListAdapter1 != null) {
            mPictureListAdapter1.removeAll(this.resourceInfoTagList1);
        }

        if (mPictureListAdapter2 != null) {
            mPictureListAdapter2.removeAll(this.resourceInfoTagList2);
        }

        if (mPictureListAdapter3 != null) {
            mPictureListAdapter3.removeAll(this.resourceInfoTagList3);
        }

        if (mPictureListAdapter4 != null) {
            mPictureListAdapter4.removeAll(this.resourceInfoTagList4);
        }
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
}
