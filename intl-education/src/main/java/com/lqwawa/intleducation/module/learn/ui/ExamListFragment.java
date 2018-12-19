package com.lqwawa.intleducation.module.learn.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.client.pojo.SourceFromType;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseFragment;
import com.lqwawa.intleducation.base.utils.LogUtil;
import com.lqwawa.intleducation.base.utils.StringUtils;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.SuperListView;
import com.lqwawa.intleducation.common.interfaces.OnLoadStatusChangeListener;
import com.lqwawa.intleducation.module.discovery.ui.CourseDetailsItemFragment;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailParams;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.learn.adapter.ExamListAdapter;
import com.lqwawa.intleducation.module.learn.tool.TaskSliderHelper;
import com.lqwawa.intleducation.module.learn.vo.CourseExamDataVo;
import com.lqwawa.intleducation.module.learn.vo.ExamListVo;
import com.oosic.apps.iemaker.base.SlideManager;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;
import java.util.List;

/**
 * Created by XChen on 2016/11/29.
 * email:man0fchina@foxmail.com
 * 作业/考试列表
 */

public class ExamListFragment extends MyBaseFragment implements View.OnClickListener {
    private static final String TAG = "NoticesListFragment";

    public static final String KEY_EXTRA_ONLINE_TEACHER = "KEY_EXTRA_ONLINE_TEACHER";

    //数据列表
    private SuperListView listView;
    private ExamListAdapter testListAdapter;
    private String courseId;
    private int type;//1作业2考试
    private CourseVo courseVo;
    private CourseDetailParams courseDetailParams;
    private OnLoadStatusChangeListener onLoadStatusChangeListener;
    // 是否是在线课堂老师进来的
    private boolean isOnlineTeacher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.com_supperlist, container, false);
        courseId = getArguments().getString("id");
        type = getArguments().getInt("type", 0);
        isOnlineTeacher = getArguments().getBoolean(KEY_EXTRA_ONLINE_TEACHER,false);
        courseVo = (CourseVo) getArguments().getSerializable(CourseVo.class.getSimpleName());
        courseDetailParams = (CourseDetailParams) getArguments().getSerializable(CourseDetailParams.class.getSimpleName());
        listView  = (SuperListView)view.findViewById(R.id.listView);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        registerBoradcastReceiver();
    }

    private void initViews() {
        testListAdapter = new ExamListAdapter(activity,isOnlineTeacher);
        testListAdapter.setCourseVo(courseVo);
        testListAdapter.setCourseDetailParams(courseDetailParams);
        listView.setAdapter(testListAdapter);
        listView.setOnItemClickListener(new SuperListView.OnItemClickListener() {
            @Override
            public void onItemClick(LinearLayout parent, View view, int position) {

            }
        });
        if(type != 0) {//测验和考试统一为一个tab 此处只接收type = 0
            return;
        }
        getData();
    }

    @Override
    public void onClick(View view) {

    }

    public void setOnLoadStatusChangeListener(OnLoadStatusChangeListener listener){
        this.onLoadStatusChangeListener = listener;
    }

    public void updateData() {
        getData();
    }

    private int pageIndex = 0;
    private void getData(){
        pageIndex = 0;
        getData(AppConfig.PAGE_SIZE);
    }
    private void getData(int pageSize) {
        pageIndex = 0;
        RequestVo requestVo = new RequestVo();
        if(!activity.getIntent().getBooleanExtra("canEdit", false)
                && StringUtils.isValidString(activity.getIntent().getStringExtra("memberId"))){
            requestVo.addParams("token", activity.getIntent().getStringExtra("memberId"));
        }
        requestVo.addParams("pageIndex", pageIndex);
        requestVo.addParams("pageSize", pageSize);
        requestVo.addParams("courseId", courseId);
        requestVo.addParams("type", type);

        LogUtil.d(TAG, requestVo.getParams().toString());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetCourseHomeworkOrExamList + requestVo.getParams());
        pageIndex = 0;
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                if (onLoadStatusChangeListener != null) {
                    onLoadStatusChangeListener.onLoadSuccess();
                }
                CourseExamDataVo result = JSON.parseObject(s,
                        new TypeReference<CourseExamDataVo>() {
                        });
                if (result.getCode() == 0) {
                    List<ExamListVo> list = result.getData();
                        if (onLoadStatusChangeListener != null) {
                            onLoadStatusChangeListener.onLoadFinish(list != null &&
                                    list.size() >= AppConfig.PAGE_SIZE);
                        }
                        if (list != null){
                            for(int i =0; i < list.size(); i++){
                                list.get(i).getCexam().setChapterName(result.getChapterName());
                                list.get(i).getCexam().setSectionName(result.getSectionName());
                            }
                        }
                        testListAdapter.setData(list);
                        testListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "拉取通知列表失败:" + throwable.getMessage());
                if (onLoadStatusChangeListener != null) {
                    onLoadStatusChangeListener.onLoadFlailed();
                }
            }

            @Override
            public void onFinished() {
            }
        });
    }

    public void getMore() {
        RequestVo requestVo = new RequestVo();
        if(!activity.getIntent().getBooleanExtra("canEdit", false)
                && StringUtils.isValidString(activity.getIntent().getStringExtra("memberId"))){
            requestVo.addParams("token", activity.getIntent().getStringExtra("memberId"));
        }
        requestVo.addParams("pageIndex", pageIndex + 1);
        requestVo.addParams("pageSize", AppConfig.PAGE_SIZE);
        requestVo.addParams("courseId", courseId);
        requestVo.addParams("type", type);

        LogUtil.d(TAG, requestVo.getParams().toString());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetCourseHomeworkOrExamList + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                ResponseVo<List<ExamListVo>> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<List<ExamListVo>>>() {
                        });
                if (result.getCode() == 0) {
                    List<ExamListVo> listMore = result.getData();
                    pageIndex++;
                    if (onLoadStatusChangeListener != null) {
                        onLoadStatusChangeListener.onLoadFinish(
                                listMore.size() >= AppConfig.PAGE_SIZE);
                    }
                    testListAdapter.addData(listMore);
                    testListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "拉取通知列表失败:" + throwable.getMessage());
            }

            @Override
            public void onFinished() {
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        activity.unregisterReceiver(mBroadcastReceiver);
    }

    /**BroadcastReceiver************************************************/
    protected BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(AppConfig.ServerUrl.userExamSave)//提交试卷成功
                    || action.equals(AppConfig.ServerUrl.userTaskSave) //提交任务单成功
                    || action.equals(AppConfig.ServerUrl.userExamMark) //老师完成批阅成功
                    || action.equals(CourseDetailsItemFragment.LQWAWA_ACTION_CAN_COURSEWARE) // 看课件
                    || action.equals(CourseDetailsItemFragment.LQWAWA_ACTION_LISTEN_AND_WRITE) // 听说课
                    || action.equals(CourseDetailsItemFragment.LQWAWA_ACTION_READ_WRITE_SINGLE)) {// 读写单
                getData();
            }
        }
    };

    /**
     * 注册广播事件
     */
    protected void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        //要接收的类型
        myIntentFilter.addAction(AppConfig.ServerUrl.userExamSave);//提交试卷成功
        myIntentFilter.addAction(AppConfig.ServerUrl.userTaskSave);//提交任务单成功
        myIntentFilter.addAction(AppConfig.ServerUrl.userExamMark);//老师完成批阅成功
        myIntentFilter.addAction(CourseDetailsItemFragment.LQWAWA_ACTION_CAN_COURSEWARE);// 看课件
        myIntentFilter.addAction(CourseDetailsItemFragment.LQWAWA_ACTION_LISTEN_AND_WRITE);// 听说课
        myIntentFilter.addAction(CourseDetailsItemFragment.LQWAWA_ACTION_READ_WRITE_SINGLE);//读写单
        //注册广播
        activity.registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 105){
            if (data != null) {
                String slidePath = data.getStringExtra(SlideManager.EXTRA_SLIDE_PATH);
                if (!TextUtils.isEmpty(slidePath)){
                    TaskSliderHelper.commitTask(activity, slidePath,
                            activity.getIntent().getStringExtra("taskPaperId"),
                            new TaskSliderHelper.OnCommitTaskListener() {
                        @Override
                        public void onCommitSuccess() {
                            getData();
                        }
                    },
                            activity.getIntent().getBooleanExtra(MyCourseDetailsActivity
                                    .KEY_IS_FROM_MY_COURSE, false)
                                    ? SourceFromType.LQ_MY_COURSE : SourceFromType.LQ_COURSE);
                }
            }
        }
    }
}
