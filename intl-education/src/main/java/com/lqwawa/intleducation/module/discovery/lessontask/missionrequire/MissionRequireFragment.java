package com.lqwawa.intleducation.module.discovery.lessontask.missionrequire;



import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterFragment;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.module.learn.vo.LqTaskCommitListVo;
import com.lqwawa.intleducation.module.learn.vo.LqTaskInfoVo;
import com.lqwawa.intleducation.module.learn.vo.TaskInfoVo;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 任务要求的Fragment,显示任务要求
 * @date 2018/04/13 10:21
 * @history v1.0
 * **********************************
 */
public class MissionRequireFragment extends PresenterFragment<MissionRequireContract.Presenter>
    implements MissionRequireContract.View{

    private static final String KEY_EXTRA_TASK_INFO = "KEY_EXTRA_TASK_INFO";
    // 空布局
    private LinearLayout mEmptyLayout;
    // 任务要求Text
    private TextView mTxtContent;

    /**
     * 任务要求
     */
    private TaskInfoVo mTaskInfo;

    public static Fragment getInstance(@NonNull LqTaskInfoVo taskInfoVo){
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_EXTRA_TASK_INFO,taskInfoVo);
        Fragment fragment = new MissionRequireFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        mTaskInfo = (TaskInfoVo) bundle.getSerializable(KEY_EXTRA_TASK_INFO);
        // @date   :2018/4/16 0016 下午 12:02
        // @func   :todo 暂时不做判断
        /*if(EmptyUtil.isEmpty(mTaskInfo)){
            return false;
        }*/
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mEmptyLayout = (LinearLayout) mRootView.findViewById(R.id.empty_layout);
        mTxtContent = (TextView) mRootView.findViewById(R.id.tv_content);
    }

    @Override
    protected MissionRequireContract.Presenter initPresenter() {
        return new MissionRequirePresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_mission_require;
    }

    /**
     * 加载到数据,Fragment UI更新
     * @param lqTaskCommitListVo 提交数据
     */
    public void updateViews(LqTaskCommitListVo lqTaskCommitListVo) {
        if(!EmptyUtil.isEmpty(lqTaskCommitListVo) && !EmptyUtil.isEmpty(lqTaskCommitListVo.getTaskInfo())){
            LqTaskInfoVo vo = lqTaskCommitListVo.getTaskInfo();
            if(!EmptyUtil.isEmpty(vo.getDiscussContent())){
                // 有任务要求文本
                mEmptyLayout.setVisibility(View.GONE);
                mTxtContent.setVisibility(View.VISIBLE);
                mTxtContent.setText(vo.getDiscussContent());
            }else{
                // 没有任务要求文本
                mEmptyLayout.setVisibility(View.VISIBLE);
                mTxtContent.setVisibility(View.GONE);
            }
        }
    }
}
