package com.lqwawa.mooc.modle.implementationplan;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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

    private View mRootView;
    private TopBar mTopBar;
    private ContainsEmojiEditText mLearningTargetEt, mMainDifficultyEt, mCommonProblemEt;
    private RelativeLayout mRlAttachmentsImg1, mRlAttachmentsImg2, mRlAttachmentsImg3;
    private ImageView mAddImg1, mAddImg2, mAddImg3;
    private ImageView mDeleteImg1, mDeleteImg2, mDeleteImg3;

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
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.attachments_add_1) {

        } else if (id == R.id.attachments_add_2) {

        } else if (id == R.id.attachments_add_3) {

        }
    }
}
