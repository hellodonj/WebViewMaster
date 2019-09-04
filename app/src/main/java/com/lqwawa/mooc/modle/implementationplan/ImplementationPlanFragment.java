package com.lqwawa.mooc.modle.implementationplan;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.fragment.ContactsListFragment;
import com.lqwawa.apps.views.ContainsEmojiEditText;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.mooc.factory.data.entity.ImplementationPlanEntity;

import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * 描述: 课中实施方案
 * 作者|时间: djj on 2019/9/3 0003 上午 10:14
 */
public class ImplementationPlanFragment extends ContactsListFragment {

    public static final String KEY_EXTRA_CHAPTER_ID = "KEY_EXTRA_CHAPTER_ID";
    public static final String KEY_EXTRA_MEMBER_ID = "KEY_EXTRA_MEMBER_ID";
    public static final String KEY_EXTRA_COURSE_ID = "KEY_EXTRA_COURSE_ID";
    private View mRootView, inflate;
    private TopBar mTopBar;
    private ContainsEmojiEditText mLearningTargetEt, mMainDifficultyEt, mCommonProblemEt;
    private RelativeLayout mRlAttachmentsImg1, mRlAttachmentsImg2, mRlAttachmentsImg3;
    private ImageView mAddImg1, mAddImg2, mAddImg3;
    private ImageView mDeleteImg1, mDeleteImg2, mDeleteImg3;
    private FrameLayout mResetContainer,mConfirmContainer;

    private String chapterId;
    private String memberId;
    private String courseId;
    private TextView cancel;
    private TextView takePhoto;
    private TextView choosePhoto;
    private Dialog dialog;
    private static final int PHOTO_REQUEST_CAREMA = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
    }

    //初始化控件
    private void initViews() {
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        mTopBar.setBack(true);
        mLearningTargetEt = (ContainsEmojiEditText) findViewById(R.id.learning_target_content);
        mMainDifficultyEt = (ContainsEmojiEditText) findViewById(R.id.main_difficulty_content);
        mCommonProblemEt = (ContainsEmojiEditText) findViewById(R.id.common_problem_content);
        mAddImg1 = (ImageView) findViewById(R.id.attachments_add_1);
        mAddImg2 = (ImageView) findViewById(R.id.attachments_add_2);
        mAddImg3 = (ImageView) findViewById(R.id.attachments_add_3);
        mAddImg1.setOnClickListener(this);
        mAddImg2.setOnClickListener(this);
        mAddImg3.setOnClickListener(this);
        mResetContainer = (FrameLayout) findViewById(R.id.reset_container);
        mConfirmContainer= (FrameLayout) findViewById(R.id.confirm_container);
        mResetContainer.setOnClickListener(this);
        mConfirmContainer.setOnClickListener(this);
    }

    private void initData() {
        mTopBar.setTitle(getString(R.string.class_implementation_plan));
        mTopBar.setTitleWide(DensityUtil.dip2px(120));

        queryIfExistPlan();
    }

    private void queryIfExistPlan() {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("chapterId", chapterId);
        if (!TextUtils.isEmpty(memberId)) {
            requestVo.addParams("token", memberId);
        }
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
                    } else {
                        ImplementationPlanEntity planEntity = new ImplementationPlanEntity();
                        configPlanData(planEntity);
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
        if (EmptyUtil.isNotEmpty(planEntity)) {
            mLearningTargetEt.setText(planEntity.getLearningGoal());
            mMainDifficultyEt.setText(planEntity.getDifficultPoint());
            mCommonProblemEt.setText(planEntity.getCommonProblem());
        }
        String lgAppendixId = planEntity.getLgAppendixId();
        String lgAppendixUrl = planEntity.getLgAppendixUrl();

    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.attachments_add_1) {
            choosePhotoDialog();
        } else if (id == R.id.attachments_add_2) {
            choosePhotoDialog();
        } else if (id == R.id.attachments_add_3) {
            choosePhotoDialog();
        } else if (id == R.id.abroad_choosephoto) {
            pickAlbum();
        } else if (id == R.id.abroad_takephoto) {
            takePhotos();
        } else if (id == R.id.abroad_choose_cancel) {
            dialog.dismiss();
        } else if (id == R.id.reset_container) {
             resetPlanData();
        } else if (id == R.id.confirm_container) {
             confirmPlanData();
        }
    }

    //确认
    private void confirmPlanData() {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("chapterId", chapterId);
        requestVo.addParams("courseId",courseId);
        if (!TextUtils.isEmpty(memberId)) {
            requestVo.addParams("token", memberId);
        }
        requestVo.addParams("learningGoal",mLearningTargetEt.getText().toString());
        requestVo.addParams("lgAppendixId","");
        requestVo.addParams("lgAppendixUrl","");
        requestVo.addParams("difficultPoint",mMainDifficultyEt.getText().toString());
        requestVo.addParams("dpAppendixId","");
        requestVo.addParams("dpAppendixUrl","");
        requestVo.addParams("commonProblem",mCommonProblemEt.getText().toString());
        requestVo.addParams("cpAppendixId","");
        requestVo.addParams("cpAppendixUrl","");
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

    //拍照
    private void takePhotos() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
    }

    //相册
    private void pickAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

}
