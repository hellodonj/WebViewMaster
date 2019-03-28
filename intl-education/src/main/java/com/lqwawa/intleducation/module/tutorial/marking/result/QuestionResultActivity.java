package com.lqwawa.intleducation.module.tutorial.marking.result;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lqwawa.intleducation.MainApplication;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ToolbarActivity;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.intleducation.module.learn.tool.TaskSliderHelper;
import com.lqwawa.intleducation.module.tutorial.marking.list.TutorialRoleType;
import com.lqwawa.intleducation.module.tutorial.target.TutorialTargetTaskParams;
import com.lqwawa.intleducation.module.tutorial.target.container.TutorialStudentTargetTaskActivity;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.lqbaselib.pojo.MessageEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * @author mrmedici
 * @desc 提交问题给帮辅老师成功页面
 */
public class QuestionResultActivity extends ToolbarActivity implements View.OnClickListener{

    private static final String KEY_EXTRA_MEMBER_ID = "KEY_EXTRA_MEMBER_ID";
    private static final String KEY_EXTRA_TUTOR_NAME = "KEY_EXTRA_TUTOR_NAME";

    private TopBar mTopBar;
    private TextView mTvDescription;
    private Button mBtnAgainSubmit;
    private Button mBtnWatchWork;

    private String mCurMemberId;
    private String mTutorName;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_question_result;
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        mCurMemberId = bundle.getString(KEY_EXTRA_MEMBER_ID);
        mTutorName = bundle.getString(KEY_EXTRA_TUTOR_NAME);
        if(EmptyUtil.isEmpty(mCurMemberId) ||
                EmptyUtil.isEmpty(mTutorName)) return false;
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        mTopBar.setBack(true);
        mTopBar.setTitle(R.string.title_submit_succeed);

        mTvDescription = (TextView) findViewById(R.id.tv_description);
        mBtnAgainSubmit = (Button) findViewById(R.id.btn_again_submit);
        mBtnWatchWork = (Button) findViewById(R.id.btn_watch_work);

        mTvDescription.setText(getString(R.string.label_commit_succeed_tip,mTutorName));

        mBtnAgainSubmit.setOnClickListener(this);
        mBtnWatchWork.setOnClickListener(this);


        EventBus.getDefault().post(new MessageEvent(EventConstant.TRIGGER_UPDATE_LIST_DATA));
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.btn_again_submit){
            finish();
        }else if(viewId == R.id.btn_watch_work){
            boolean tutorialMode = MainApplication.isTutorialMode();
            if(tutorialMode){
                // 帮辅模式的学生提交，说明有申请批阅权限
                // 去主页面的第三个Tab
                if(EmptyUtil.isNotEmpty(TaskSliderHelper.onTutorialMarkingListener)){
                    TaskSliderHelper.onTutorialMarkingListener.skipMyCourseQuestionWork(this);
                    finish();
                }
            }else{
                // 后来更换了学生端的作业列表入口
                // 家长不能替孩子提交作业
                // android:launchMode="singleTask"
                boolean isParent = !TextUtils.equals(UserHelper.getUserId(),mCurMemberId);
                TutorialTargetTaskParams params = new TutorialTargetTaskParams(mCurMemberId,null,getString(R.string.label_committed_works));
                params.setParent(isParent);
                params.setRole(isParent?TutorialRoleType.TUTORIAL_TYPE_PARENT:TutorialRoleType.TUTORIAL_TYPE_STUDENT);
                TutorialStudentTargetTaskActivity.show(this,params);
                finish();
                /*TutorialMarkingParams params = new TutorialMarkingParams(mCurMemberId,TutorialRoleType.TUTORIAL_TYPE_STUDENT);
                TutorialMarkingListActivity.show(this,params);
                finish();*/
            }
        }
    }

    /**
     * 入口
     * @param context 上下文对象
     */
    public static void show(@NonNull Context context,
                            @NonNull String memberId,
                            @NonNull String tutorName){
        Intent intent = new Intent(context,QuestionResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_EXTRA_MEMBER_ID,memberId);
        bundle.putString(KEY_EXTRA_TUTOR_NAME,tutorName);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
