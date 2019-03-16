package com.lqwawa.intleducation.module.tutorial.marking.require;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqwawa.client.pojo.SourceFromType;
import com.lqwawa.intleducation.MainApplication;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.ImageUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.tutorial.TaskEntity;
import com.lqwawa.intleducation.factory.helper.LessonHelper;
import com.lqwawa.intleducation.module.discovery.lessontask.missionrequire.MissionRequireFragment;
import com.lqwawa.intleducation.module.discovery.ui.task.list.TaskCommitParams;
import com.lqwawa.intleducation.module.learn.tool.TaskSliderHelper;
import com.lqwawa.intleducation.module.learn.vo.LqTaskCommitListVo;
import com.lqwawa.intleducation.module.learn.vo.LqTaskInfoVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

/**
 * @author mrmedici
 * @desc 帮辅批阅页面，任务要求的页面
 */
public class TaskRequirementActivity extends PresenterActivity<TaskRequirementContract.Presenter>
    implements TaskRequirementContract.View, View.OnClickListener{

    private static final String KEY_EXTRA_TASK_ENTITY = "KEY_EXTRA_TASK_ENTITY";

    private TopBar mTopBar;
    private ImageView mIvResIcon;
    private TextView mTvResName;
    private TextView mTvAccessDetail;
    private LinearLayout mBodyLayout;

    private TaskEntity mTaskEntity;

    @Override
    protected TaskRequirementContract.Presenter initPresenter() {
        return new TaskRequirementPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_task_requirement;
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        mTaskEntity = (TaskEntity) bundle.getSerializable(KEY_EXTRA_TASK_ENTITY);
        if(EmptyUtil.isEmpty(mTaskEntity)) return false;
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        mTopBar.setBack(true);
        mTopBar.setTitle(R.string.title_watch_task_requirement);

        mIvResIcon = (ImageView) findViewById(R.id.iv_res_icon);
        mTvResName = (TextView) findViewById(R.id.tv_res_name);
        mTvAccessDetail = (TextView) findViewById(R.id.tv_access_details);
        mBodyLayout = (LinearLayout) findViewById(R.id.body_layout);

        mIvResIcon.setOnClickListener(this);
        mTvAccessDetail.setOnClickListener(this);

        mTvResName.setText(mTaskEntity.getTitle());
        ImageUtil.fillDefaultView(mIvResIcon, mTaskEntity.getResThumbnailUrl());

        MissionRequireFragment instance = (MissionRequireFragment) MissionRequireFragment.getInstance(null);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.lay_content,instance)
                .commit();

        // 使用新接口,拉数据
        String userId = UserHelper.getUserId();
        LessonHelper.getNewCommittedTaskByTaskId(Integer.toString(mTaskEntity.getT_TaskId()),
                userId,
                null,
                null, null, TaskCommitParams.TYPE_ALL, new DataSource.Callback<LqTaskCommitListVo>() {
                    @Override
                    public void onDataNotAvailable(int strRes) {

                    }

                    @Override
                    public void onDataLoaded(LqTaskCommitListVo lqTaskCommitListVo) {
                        if(!EmptyUtil.isEmpty(lqTaskCommitListVo) && !EmptyUtil.isEmpty(lqTaskCommitListVo.getTaskInfo())){
                            LqTaskInfoVo vo = lqTaskCommitListVo.getTaskInfo();
                            if(!EmptyUtil.isEmpty(vo.getDiscussContent())){
                                // 有任务要求文本
                                mBodyLayout.setVisibility(View.VISIBLE);
                                instance.updateViews(lqTaskCommitListVo);
                            }else{
                                // 没有任务要求文本
                                mBodyLayout.setVisibility(View.GONE);
                            }
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.tv_access_details){
            if (TaskSliderHelper.onTaskSliderListener != null) {
                String id = mTaskEntity.getResId();
                if(EmptyUtil.isNotEmpty(id) && id.contains("-")){
                    String[] strings = id.split("-");
                    String resId = strings[0];
                    String resType = strings[1];
                    String title = mTaskEntity.getTitle();
                    String resUrl = mTaskEntity.getResUrl();
                    String resThumbnailUrl = mTaskEntity.getResThumbnailUrl();
                    TaskSliderHelper.onTutorialMarkingListener.openCourseWareDetails(
                            this,false,
                            resId,Integer.parseInt(resType),
                            title,1,
                            resUrl,resThumbnailUrl);
                }
            }
        }else if(viewId == R.id.iv_res_icon){
            // 打开课件
            if (TaskSliderHelper.onTaskSliderListener != null) {
                String id = mTaskEntity.getResId();
                if(EmptyUtil.isNotEmpty(id) && id.contains("-")){
                    String[] strings = id.split("-");
                    String resId = strings[0];
                    String resType = strings[1];

                    TaskSliderHelper.onTaskSliderListener.viewCourse(this,resId,Integer.parseInt(resType),"",SourceFromType.STUDY_TASK);
                }
            }
        }
    }

    /**
     * 帮辅批阅任务要求页面的入口
     * @param context 上下文对象
     */
    public static void show(@NonNull Context context,@NonNull TaskEntity taskEntity){
        Intent intent = new Intent(context,TaskRequirementActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_EXTRA_TASK_ENTITY,taskEntity);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
