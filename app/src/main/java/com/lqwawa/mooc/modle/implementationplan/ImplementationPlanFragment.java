package com.lqwawa.mooc.modle.implementationplan;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.StringCallback;
import com.lqwawa.mooc.adapter.SelectPictureListAdapter;
import com.lqwawa.mooc.common.GuidanceResourceType;
import com.lqwawa.mooc.common.GuidanceTaskUtils;
import com.lqwawa.mooc.factory.data.entity.ImplementationPlanEntity;

import org.xutils.common.Callback;
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
    public static final String KEY_EXTRA_IS_EDIT_MODE = "KEY_EXTRA_IS_EDIT_MODE";

    public static final int LEARNING_TARGET_TYPE = 1;
    public static final int MAIN_DIFFICULT_TYPE = 2;
    public static final int COMMON_PROBLEM_TYPE = 3;
    public static final int STEP_TYPE = 4;

    private ContainsEmojiEditText learningTargetEt, mainDifficultyEt, commonProblemEt, stepEt;
    private LinearLayout addAccessories1, addAccessories2, addAccessories3, addAccessories4;
    private TextView tvAccessories1, tvAccessories2, tvAccessories3, tvAccessories4;
    private RecyclerView recyclerView1, recyclerView2, recyclerView3, recyclerView4;
    private Button resetBtn, confirmBtn, editBtn;
    private SelectPictureListAdapter pictureListAdapter1, pictureListAdapter2,
            pictureListAdapter3, pictureListAdapter4;

    private List<ResourceInfoTag> resourceInfoTagList1 = new ArrayList<>();
    private List<ResourceInfoTag> resourceInfoTagList2 = new ArrayList<>();
    private List<ResourceInfoTag> resourceInfoTagList3 = new ArrayList<>();
    private List<ResourceInfoTag> resourceInfoTagList4 = new ArrayList<>();

    private String learningTargetText, mainDifficultyText, commonProblemText, stepText;
    private String chapterId, memberId, courseId, classId;
    private Dialog dialog;
    private int accessoriesType;
    private ImplementationPlanEntity planEntity;
    private boolean isEditMode = false;


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        GuidanceTaskUtils.getInstance().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_implementation_plan, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getIntent();
        initViews();
        initData();
    }

    private void getIntent() {
        if (getArguments() != null) {
            chapterId = getArguments().getString(KEY_EXTRA_CHAPTER_ID);
            memberId = getArguments().getString(KEY_EXTRA_MEMBER_ID);
            courseId = getArguments().getString(KEY_EXTRA_COURSE_ID);
            classId = getArguments().getString(KEY_EXTRA_CLASS_ID);
            isEditMode = getArguments().getBoolean(KEY_EXTRA_IS_EDIT_MODE);
        }
    }

    //初始化控件
    private void initViews() {
        TopBar topBar = (TopBar) findViewById(R.id.top_bar);
        topBar.setLeftFunctionImage1(R.drawable.ic_back_green, v -> {
            if (!isEditMode) {
                if (getActivity() != null) {
                    getActivity().finish();
                }
            } else {
                if (planEntity != null) {
                    isEditMode = false;
                    switchMode();
                } else {
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                }
            }
        });
        topBar.setTitle(getString(R.string.class_implementation_plan));

        learningTargetEt = (ContainsEmojiEditText) findViewById(R.id.learning_target_content);
        mainDifficultyEt = (ContainsEmojiEditText) findViewById(R.id.main_difficulty_content);
        commonProblemEt = (ContainsEmojiEditText) findViewById(R.id.common_problem_content);
        stepEt = (ContainsEmojiEditText) findViewById(R.id.step_content);

        addAccessories1 = (LinearLayout) findViewById(R.id.layout_add_accessories_1);
        addAccessories2 = (LinearLayout) findViewById(R.id.layout_add_accessories_2);
        addAccessories3 = (LinearLayout) findViewById(R.id.layout_add_accessories_3);
        addAccessories4 = (LinearLayout) findViewById(R.id.layout_add_accessories_4);

        tvAccessories1 = (TextView) findViewById(R.id.tv_accessories_1);
        tvAccessories2 = (TextView) findViewById(R.id.tv_accessories_2);
        tvAccessories3 = (TextView) findViewById(R.id.tv_accessories_3);
        tvAccessories4 = (TextView) findViewById(R.id.tv_accessories_4);

        recyclerView1 = (RecyclerView) findViewById(R.id.recycler_view_1);
        recyclerView2 = (RecyclerView) findViewById(R.id.recycler_view_2);
        recyclerView3 = (RecyclerView) findViewById(R.id.recycler_view_3);
        recyclerView4 = (RecyclerView) findViewById(R.id.recycler_view_4);

        resetBtn = (Button) findViewById(R.id.btn_reset);
        confirmBtn = (Button) findViewById(R.id.btn_confirm);
        editBtn = (Button) findViewById(R.id.btn_edit);
        resetBtn.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);
        editBtn.setOnClickListener(this);
    }

    private void initData() {
        if (!isEditMode) {
            getImplementPlan();
        }

        configPlanView();
        switchMode();
    }

    private void switchMode() {
        setTargetEditText();

        setTargetEditTextContent(planEntity, isEditMode);
        setAccessoryText(planEntity);

        pictureListAdapter1.setEditMode(isEditMode);
        pictureListAdapter2.setEditMode(isEditMode);
        pictureListAdapter3.setEditMode(isEditMode);
        pictureListAdapter4.setEditMode(isEditMode);
        
        setBottomButtons();
    }

    private void setTargetEditText() {
        setTargetEditText(learningTargetEt, isEditMode);
        setTargetEditText(mainDifficultyEt, isEditMode);
        setTargetEditText(commonProblemEt, isEditMode);
        setTargetEditText(stepEt, isEditMode);
    }

    private void setTargetEditText(ContainsEmojiEditText editText, boolean isEditMode) {
        Drawable drawable = getResources().getDrawable(R.drawable.topic_input_gray_bg);
        editText.setMinLines(isEditMode ? 5 : 1);
        editText.setBackground(isEditMode ? drawable : null);
        editText.setFocusableInTouchMode(isEditMode);
        editText.setEnabled(isEditMode);
    }

    private void setBottomButtons() {
        resetBtn.setVisibility(View.GONE);
        confirmBtn.setVisibility(View.GONE);
        editBtn.setVisibility(View.GONE);
        if (isEditMode) {
            confirmBtn.setVisibility(View.VISIBLE);
            if (planEntity == null) {
                resetBtn.setVisibility(View.VISIBLE);
            }
        } else {
            editBtn.setVisibility(View.VISIBLE);
        }
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
        if (imageItemInfos.size() > 0) {
            GalleryActivity.newInstance(getActivity(), imageItemInfos, true, position, false, false, false);
        }

    }


    private void configPlanView() {
        recyclerView1.setLayoutManager(new GridLayoutManager(getActivity(), 5));
        pictureListAdapter1 = new SelectPictureListAdapter(getActivity(), resourceInfoTagList1,
                isEditMode);
        recyclerView1.setAdapter(pictureListAdapter1);
        pictureListAdapter1.setOnItemClickListener(new SelectPictureListAdapter.OnItemClickListener() {
            @Override
            public void onAddItemClick(View view, int position) {
                accessoriesType = LEARNING_TARGET_TYPE;
                choosePhotoDialog();
            }

            @Override
            public void onItemClick(View view, int position) {
                accessoriesType = LEARNING_TARGET_TYPE;
                toImageActivity(resourceInfoTagList1, position);
            }
        });

        recyclerView2.setLayoutManager(new GridLayoutManager(getActivity(), 5));
        pictureListAdapter2 = new SelectPictureListAdapter(getActivity(), resourceInfoTagList2, isEditMode);
        recyclerView2.setAdapter(pictureListAdapter2);
        pictureListAdapter2.setOnItemClickListener(new SelectPictureListAdapter.OnItemClickListener() {
            @Override
            public void onAddItemClick(View view, int position) {
                accessoriesType = MAIN_DIFFICULT_TYPE;
                choosePhotoDialog();
            }

            @Override
            public void onItemClick(View view, int position) {
                accessoriesType = MAIN_DIFFICULT_TYPE;
                toImageActivity(resourceInfoTagList2, position);
            }
        });

        recyclerView3.setLayoutManager(new GridLayoutManager(getActivity(), 5));
        pictureListAdapter3 = new SelectPictureListAdapter(getActivity(), resourceInfoTagList3, isEditMode);
        recyclerView3.setAdapter(pictureListAdapter3);
        pictureListAdapter3.setOnItemClickListener(new SelectPictureListAdapter.OnItemClickListener() {
            @Override
            public void onAddItemClick(View view, int position) {
                accessoriesType = COMMON_PROBLEM_TYPE;
                choosePhotoDialog();
            }

            @Override
            public void onItemClick(View view, int position) {
                accessoriesType = COMMON_PROBLEM_TYPE;
                toImageActivity(resourceInfoTagList3, position);
            }
        });

        recyclerView4.setLayoutManager(new GridLayoutManager(getActivity(), 5));
        pictureListAdapter4 = new SelectPictureListAdapter(getActivity(), resourceInfoTagList4, isEditMode);
        recyclerView4.setAdapter(pictureListAdapter4);
        pictureListAdapter4.setOnItemClickListener(new SelectPictureListAdapter.OnItemClickListener() {
            @Override
            public void onAddItemClick(View view, int position) {
                accessoriesType = STEP_TYPE;
                choosePhotoDialog();
            }

            @Override
            public void onItemClick(View view, int position) {
                accessoriesType = STEP_TYPE;
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
                    planEntity = results.getData();
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

    private void setTargetEditTextContent(ImplementationPlanEntity planEntity, boolean isEditMode) {

        String placeHolderStr = (!isEditMode && planEntity != null)?
                getString(R.string.no_content) : "";
        String hintStr = !isEditMode ? "" : getString(R.string.label_input_text_tip);
        learningTargetEt.setHint(hintStr);
        mainDifficultyEt.setHint(hintStr);
        commonProblemEt.setHint(hintStr);
        stepEt.setHint(hintStr);

        if (planEntity == null || TextUtils.isEmpty(planEntity.getLearningGoal())) {
            learningTargetEt.setText(placeHolderStr);
        } else {
            learningTargetEt.setText(planEntity.getLearningGoal());
        }
        if (planEntity == null || TextUtils.isEmpty(planEntity.getDifficultPoint())) {
            mainDifficultyEt.setText(placeHolderStr);
        } else {
            mainDifficultyEt.setText(planEntity.getDifficultPoint());
        }

        if (planEntity == null || TextUtils.isEmpty(planEntity.getCommonProblem())) {
            commonProblemEt.setText(placeHolderStr);
        } else {
            commonProblemEt.setText(planEntity.getCommonProblem());
        }
        if (planEntity == null || TextUtils.isEmpty(planEntity.getStep())) {
            stepEt.setText(placeHolderStr);
        } else {
            stepEt.setText(planEntity.getStep());
        }
    }

    private void setAccessoryText(ImplementationPlanEntity planEntity) {
        tvAccessories1.setVisibility(View.VISIBLE);
        tvAccessories2.setVisibility(View.VISIBLE);
        tvAccessories3.setVisibility(View.VISIBLE);
        tvAccessories4.setVisibility(View.VISIBLE);
        
        tvAccessories1.setText(!isEditMode ? R.string.label_attachments :
                R.string.label_add_attachments);
        addAccessories1.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
        if (!isEditMode && planEntity != null) {
            addAccessories1.setVisibility(!TextUtils.isEmpty(planEntity.getLgAppendixUrl()) ?
                    View.VISIBLE : View.GONE);
        }

        tvAccessories2.setText(!isEditMode ? R.string.label_attachments :
                R.string.label_add_attachments);
        addAccessories2.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
        if (!isEditMode && planEntity != null) {
            addAccessories2.setVisibility(!TextUtils.isEmpty(planEntity.getDpAppendixUrl()) ?
                    View.VISIBLE : View.GONE);
        }

        tvAccessories3.setText(!isEditMode ? R.string.label_attachments :
                R.string.label_add_attachments);
        addAccessories3.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
        if (!isEditMode && planEntity != null) {
            addAccessories3.setVisibility(!TextUtils.isEmpty(planEntity.getCpAppendixUrl()) ?
                    View.VISIBLE : View.GONE);
        }

        tvAccessories4.setText(!isEditMode ? R.string.label_attachments :
                R.string.label_add_attachments);
        addAccessories4.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
        if (!isEditMode && planEntity != null) {
            addAccessories4.setVisibility(!TextUtils.isEmpty(planEntity.getStepUrl()) ?
                    View.VISIBLE : View.GONE);
        }

    }

    private void configPlanData(ImplementationPlanEntity planEntity) {
        
        setAccessoryText(planEntity);

        setTargetEditTextContent(planEntity, isEditMode);

        ArrayList<ResourceInfoTag> lgResourceInfoTags =
                getResourceInfoTags(planEntity.getLgAppendixId(), planEntity.getLgAppendixUrl());
        this.resourceInfoTagList1.addAll(lgResourceInfoTags);
        pictureListAdapter1.notifyDataSetChanged();

        ArrayList<ResourceInfoTag> dpResourceInfoTags =
                getResourceInfoTags(planEntity.getDpAppendixId(), planEntity.getDpAppendixUrl());
        this.resourceInfoTagList2.addAll(dpResourceInfoTags);
        pictureListAdapter2.notifyDataSetChanged();

        ArrayList<ResourceInfoTag> cpResourceInfoTags =
                getResourceInfoTags(planEntity.getCpAppendixId(), planEntity.getCpAppendixUrl());
        this.resourceInfoTagList3.addAll(cpResourceInfoTags);
        pictureListAdapter3.notifyDataSetChanged();

        ArrayList<ResourceInfoTag> sResourceInfoTags =
                getResourceInfoTags(planEntity.getStepId(), planEntity.getStepUrl());
        this.resourceInfoTagList4.addAll(sResourceInfoTags);
        pictureListAdapter4.notifyDataSetChanged();
    }

    private ArrayList<ResourceInfoTag> getResourceInfoTags(String lgAppendixId, String lgAppendixUrl) {
        ArrayList<ResourceInfoTag> lgResourceInfoTags = new ArrayList<>();
        if (!TextUtils.isEmpty(lgAppendixId)) {
            String[] lgTempId = lgAppendixId.split(",");
            String[] lgTempUrl = lgAppendixUrl.split(",");
            for (int i = 0; i < lgTempId.length; i++) {
                ResourceInfoTag lgInfoTag = new ResourceInfoTag();
                lgInfoTag.setResId(lgTempId[i]);
                lgInfoTag.setImgPath(lgTempUrl[i]);
                lgInfoTag.setResourcePath(lgTempUrl[i]);
                lgResourceInfoTags.add(lgInfoTag);
            }
        }
        return lgResourceInfoTags;
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.abroad_choosephoto) {
            if (accessoriesType == LEARNING_TARGET_TYPE) {
                doGuidanceTypeWork(GuidanceResourceType.PHOTO, resourceInfoTagList1.size());
            } else if (accessoriesType == MAIN_DIFFICULT_TYPE) {
                doGuidanceTypeWork(GuidanceResourceType.PHOTO, resourceInfoTagList2.size());
            } else if (accessoriesType == COMMON_PROBLEM_TYPE) {
                doGuidanceTypeWork(GuidanceResourceType.PHOTO, resourceInfoTagList3.size());
            } else if (accessoriesType == STEP_TYPE) {
                doGuidanceTypeWork(GuidanceResourceType.PHOTO, resourceInfoTagList4.size());
            }
            dialog.dismiss();
        } else if (id == R.id.abroad_takephoto) {
            doGuidanceTypeWork(GuidanceResourceType.TAKE_CAMERA, 0);
            dialog.dismiss();
        } else if (id == R.id.abroad_choose_cancel) {
            dialog.dismiss();
        } else if (id == R.id.btn_reset) {
            resetPlanData();
        } else if (id == R.id.btn_confirm) {
            getEditContent();
            boolean isAllEmpty = TextUtils.isEmpty(learningTargetText) && TextUtils.isEmpty(mainDifficultyText) &&
                    TextUtils.isEmpty(commonProblemText) && TextUtils.isEmpty(stepText) &&
                    resourceInfoTagList1.isEmpty() && resourceInfoTagList2.isEmpty() &&
                    resourceInfoTagList3.isEmpty() && resourceInfoTagList4.isEmpty();
            if (isAllEmpty) {
                if (planEntity == null) {
                    confirmPlanDialog();
                } else {
                    confirmPlanData(true);
                }
            } else {
                confirmPlanData(false);
            }
        } else if (id == R.id.btn_edit) {
            isEditMode = true;
            switchMode();
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
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        });
        messageDialog.show();
    }

    private void doGuidanceTypeWork(int guidanceType, int selectCount) {
        GuidanceTaskUtils.getInstance()
                .setContext(getActivity())
                .setFromStudyTaskIntro(true)
                .setCallBackListener(result -> updateListData((List<MediaData>) result))
                .doGuidanceTypeWork(guidanceType, selectCount);
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
            if (accessoriesType == LEARNING_TARGET_TYPE) {
                this.resourceInfoTagList1.addAll(resourceInfoTags);
                if (pictureListAdapter1 != null) {
                    pictureListAdapter1.update(this.resourceInfoTagList1);
                }
            } else if (accessoriesType == MAIN_DIFFICULT_TYPE) {
                this.resourceInfoTagList2.addAll(resourceInfoTags);
                if (pictureListAdapter2 != null) {
                    pictureListAdapter2.update(this.resourceInfoTagList2);
                }
            } else if (accessoriesType == COMMON_PROBLEM_TYPE) {
                this.resourceInfoTagList3.addAll(resourceInfoTags);
                if (pictureListAdapter3 != null) {
                    pictureListAdapter3.update(this.resourceInfoTagList3);
                }
            } else if (accessoriesType == STEP_TYPE) {
                this.resourceInfoTagList4.addAll(resourceInfoTags);
                if (pictureListAdapter4 != null) {
                    pictureListAdapter4.update(this.resourceInfoTagList4);
                }
            }
        }
    }

    private void getEditContent() {
        learningTargetText = learningTargetEt.getText().toString().trim();
        mainDifficultyText = mainDifficultyEt.getText().toString().trim();
        commonProblemText = commonProblemEt.getText().toString().trim();
        stepText = stepEt.getText().toString().trim();
    }

    //确认
    private void confirmPlanData(final boolean isAllEmpty) {
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
        
        if (!isAllEmpty && planEntity != null) {
            planEntity.setLearningGoal(learningTargetText);
            planEntity.setLgAppendixId(lgAppendixId);
            planEntity.setLgAppendixUrl(lgAppendixUrl);

            planEntity.setDifficultPoint(mainDifficultyText);
            planEntity.setDpAppendixId(dpAppendixId);
            planEntity.setDpAppendixUrl(dpAppendixUrl);

            planEntity.setCommonProblem(commonProblemText);
            planEntity.setCpAppendixId(cpAppendixId);
            planEntity.setCpAppendixUrl(cpAppendixUrl);

            planEntity.setStep(stepText);
            planEntity.setStepId(sAppendixId);
            planEntity.setStepUrl(sAppendixUrl);
        }

        RequestVo requestVo = new RequestVo();
        requestVo.addParams("chapterId", chapterId);
        requestVo.addParams("courseId", courseId);
        if (!TextUtils.isEmpty(memberId)) {
            requestVo.addParams("token", memberId);
        }
        requestVo.addParams("classId", classId);
        requestVo.addParams("learningGoal", learningTargetText);
        requestVo.addParams("lgAppendixId", lgAppendixId);
        requestVo.addParams("lgAppendixUrl", lgAppendixUrl);
        requestVo.addParams("difficultPoint", mainDifficultyText);
        requestVo.addParams("dpAppendixId", dpAppendixId);
        requestVo.addParams("dpAppendixUrl", dpAppendixUrl);
        requestVo.addParams("commonProblem", commonProblemText);
        requestVo.addParams("cpAppendixId", cpAppendixId);
        requestVo.addParams("cpAppendixUrl", cpAppendixUrl);
        requestVo.addParams("step", stepText);
        requestVo.addParams("stepId", sAppendixId);
        requestVo.addParams("stepUrl", sAppendixUrl);
        RequestParams params = new RequestParams(AppConfig.ServerUrl.postSaveImplementPlan);
        params.setAsJsonContent(true);
        params.setBodyContent(requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().post(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String result) {
                ResponseVo<String> results = JSON.parseObject(result,
                        new TypeReference<ResponseVo<String>>() {
                        });
                if (results.isSucceed()) {
                    if (isAllEmpty) {
                        if (getActivity() != null) {
                            getActivity().finish();
                        }
                    } else {
                        if (planEntity == null) {
                            if (getActivity() != null) {
                                getActivity().finish();
                            }
                        } else {
                            isEditMode = false;
                            switchMode();
                        }
                    }
                }
            }
        });
    }

    //重置
    private void resetPlanData() {
        learningTargetEt.setText("");
        mainDifficultyEt.setText("");
        commonProblemEt.setText("");
        stepEt.setText("");

        resourceInfoTagList1.clear();
        pictureListAdapter1.notifyDataSetChanged();
        resourceInfoTagList2.clear();
        pictureListAdapter2.notifyDataSetChanged();
        resourceInfoTagList3.clear();
        pictureListAdapter3.notifyDataSetChanged();
        resourceInfoTagList4.clear();
        pictureListAdapter4.notifyDataSetChanged();
    }

    //选择照片方式弹框
    public void choosePhotoDialog() {
        dialog = new Dialog(getContext(), R.style.ActionSheetDialogStyle);
        View inflate =
                LayoutInflater.from(getContext()).inflate(R.layout.view_choosephoto_dialog, null);
        TextView choosePhoto = (TextView) inflate.findViewById(R.id.abroad_choosephoto);
        TextView takePhoto = (TextView) inflate.findViewById(R.id.abroad_takephoto);
        TextView cancel = (TextView) inflate.findViewById(R.id.abroad_choose_cancel);
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

    public void onBackPress() {
        if (!isEditMode) {
            if (getActivity() != null) {
                getActivity().finish();
            }
        } else {
            if (planEntity != null) {
                isEditMode = false;
                switchMode();
            } else {
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        }
    }
}
