package com.lqwawa.mooc.modle.implementationplan;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.fragment.ContactsListFragment;
import com.lqwawa.apps.views.ContainsEmojiEditText;
import com.lqwawa.intleducation.base.widgets.TopBar;

import org.xutils.common.util.DensityUtil;

/**
 * 描述: 课中实施方案
 * 作者|时间: djj on 2019/9/3 0003 上午 10:14
 */
public class ImplementationPlanFragment extends ContactsListFragment {

    public static final String KEY_EXTRA_CHAPTER_ID = "KEY_EXTRA_CHAPTER_ID";
    private View mRootView, inflate;
    private TopBar mTopBar;
    private ContainsEmojiEditText mLearningTargetEt, mMainDifficultyEt, mCommonProblemEt;
    private RelativeLayout mRlAttachmentsImg1, mRlAttachmentsImg2, mRlAttachmentsImg3;
    private ImageView mAddImg1, mAddImg2, mAddImg3;
    private ImageView mDeleteImg1, mDeleteImg2, mDeleteImg3;

    private String chapterId;
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
    }

    private void initData() {
        mTopBar.setTitle(getString(R.string.class_implementation_plan));
        mTopBar.setTitleWide(DensityUtil.dip2px(120));

        queryIfExistPlan();
    }

    private void queryIfExistPlan() {

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
