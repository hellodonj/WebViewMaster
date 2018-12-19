package com.lqwawa.intleducation.module.discovery.lessontask;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.module.discovery.lessontask.committedtask.CommittedTaskFragment;
import com.lqwawa.intleducation.module.discovery.lessontask.lessonroot.LessonDetailRootFragment;
import com.lqwawa.intleducation.module.learn.vo.LqTaskCommitListVo;
import com.lqwawa.intleducation.module.learn.vo.SectionResListVo;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 听说课读写单Presenter
 * @date 2018/4/12 0012 下午 5:01
 * @history v1.0
 * **********************************
 */
public class LessonDetailTaskActivity extends PresenterActivity<LessonDetailTaskContract.Presenter>
    implements LessonDetailTaskContract.View,View.OnClickListener{

    private static final String KEY_EXTRA_MEMBER_ID = "KEY_EXTRA_MEMBER_ID";
    private static final String KEY_EXTRA_TYPE = "KEY_EXTRA_TYPE";
    private static final String KEY_EXTRA_CAN_EDIT = "KEY_EXTRA_CAN_EDIT";
    private static final String KEY_EXTRA_DRAWABLE_ID = "KEY_EXTRA_DRAWABLE_ID";
    // 具体的课程节详细信息
    private static final String KEY_EXTRA_SECTION_DETAIL = "KEY_EXTRA_SECTION_DETAIL";

    // 听说课
    public static final int TYPE_LISTEN_AND_SPEAK = 1;
    // 读写单
    public static final int TYPE_READING_AND_WRITING = 2;

    // 标题栏
    private TopBar mTopBar;
    // 资源容器
    private FrameLayout mResLayout;
    // 获取Fragment容器
    private FrameLayout mRootLayout;
    // 资源类型Icon
    private ImageView mResIcon;
    // 资源名称
    private TextView mResName;

    // 家长身份,预备的孩子memberId;
    private String mMemberId;
    // 当前显示类型
    private int mType;
    private boolean mCanEdit;
    // 资源类型图片显示 听说课,读写单 等等
    @DrawableRes
    private int mDrawableRes;
    // 任务信息
    private SectionResListVo mResVo;

    @Override
    protected LessonDetailTaskContract.Presenter initPresenter() {
        return new LessonDetailTaskPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_lesson_detail_task;
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        mResVo = (SectionResListVo) bundle.getSerializable(KEY_EXTRA_SECTION_DETAIL);
        mMemberId = bundle.getString(KEY_EXTRA_MEMBER_ID);
        mType = bundle.getInt(KEY_EXTRA_TYPE);
        mCanEdit = bundle.getBoolean(KEY_EXTRA_CAN_EDIT);
        mDrawableRes = bundle.getInt(KEY_EXTRA_DRAWABLE_ID);
        if(TextUtils.isEmpty(mMemberId) || EmptyUtil.isEmpty(mResVo)){
            return false;
        }

        if(mType != TYPE_LISTEN_AND_SPEAK && mType != TYPE_READING_AND_WRITING){
            return false;
        }
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        mResLayout = (FrameLayout) findViewById(R.id.res_layout);
        mRootLayout = (FrameLayout) findViewById(R.id.root_layout);
        mResIcon = (ImageView) findViewById(R.id.iv_res_icon);
        mResName = (TextView) findViewById(R.id.tv_res_name);

        mTopBar.setBack(true);
        mTopBar.setRightFunctionText1TextColor(R.color.colorAccent);
        mTopBar.setRightFunctionText1(R.string.label_share, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtil.showToastSafe(R.string.label_share);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        mTopBar.setTitle(R.string.title_course_notice);
        if(mType == TYPE_LISTEN_AND_SPEAK){
            // 听说课
            mTopBar.setTitle(R.string.title_listening_and_speaking);
        }else{
            // 读写单
            mTopBar.setTitle(R.string.title_Reading_and_writing_single);
        }

        // 显示资源图片
        mResIcon.setImageResource(mDrawableRes);
        mResName.setText(mResVo.getTaskName());

        // 加载任务数据
        mPresenter.getCommittedTaskByTaskId(mResVo,mCanEdit,mMemberId);

        // 点击课件详情
        mResLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.res_layout){
            UIUtil.showToastSafe(R.string.label_courseware_detail);
        }
    }

    @Override
    public void refreshView(LqTaskCommitListVo commitTaskVo) {
        if(!EmptyUtil.isEmpty(commitTaskVo.getTaskInfo())){
            // 有任务要求
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.root_layout, LessonDetailRootFragment.getInstance(commitTaskVo))
                    .commit();
        }else{
            // 没有任务要求,直接显示提交列表
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.root_layout, CommittedTaskFragment.getInstance(commitTaskVo.getListCommitTaskOnline()))
                    .commit();
        }
    }

    /**
     * 听说课,读写单页面入口 V5.5
     * @param context 上下文对象 课程节详情页面
     * @param type 1 听说课 2 读写单
     * @param vo 听说课或者读写单资源
     * @param canEdit 是否是家长身份
     * @param memberId 孩子的memberId
     */
    public static void start(@NonNull Context context,@IntRange(from = 1,to = 2) int type,
                             @DrawableRes int resIcon,@NonNull SectionResListVo vo,
                             boolean canEdit,@NonNull String memberId){
        Bundle bundle = new Bundle();
        bundle.putString(KEY_EXTRA_MEMBER_ID,memberId);
        bundle.putInt(KEY_EXTRA_TYPE,type);
        bundle.putBoolean(KEY_EXTRA_CAN_EDIT,canEdit);
        bundle.putSerializable(KEY_EXTRA_SECTION_DETAIL,vo);
        bundle.putInt(KEY_EXTRA_DRAWABLE_ID,resIcon);
        Intent intent = new Intent(context,LessonDetailTaskActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
