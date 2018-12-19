package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.CallbackListener;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.UploadUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.db.dto.LocalCourseDTO;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.fragment.resource.HomeworkResourceAdapterViewHelper;
import com.galaxyschool.app.wawaschool.helper.CheckLqShopPmnHelper;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.lqbaselib.net.library.DataResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.HomeworkListInfo;
import com.galaxyschool.app.wawaschool.pojo.HomeworkListResult;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.ShortSchoolClassInfo;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;
import com.galaxyschool.app.wawaschool.pojo.UploadParameter;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseUploadResult;
import com.galaxyschool.app.wawaschool.slide.SlideManagerHornForPhone;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.slidelistview.SlideListView;
import com.lqwawa.client.pojo.MediaType;
import com.lqwawa.lqbaselib.pojo.MessageEvent;
import com.lqwawa.tools.FileZipHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2016/8/4.
 */
public class MyTaskListFragment extends ContactsListFragment {

    public static final String TAG = MyTaskListFragment.class.getSimpleName();

    public static final String EXTRA_IS_SCAN_TASK = "is_scan_task";

    private int roleType = 1; //学生
    private String schoolId;
    private String classId;
    private boolean isScanTask;

    private HomeworkListInfo selectHomeworkInfo;
    private String taskId;

    private UploadParameter uploadParameter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_task_list, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            loadViews();
        }
    }

    private void initViews() {
        if (getArguments() != null) {
            uploadParameter = (UploadParameter) getArguments().getSerializable(UploadParameter.class
                    .getSimpleName());
            isScanTask = getArguments().getBoolean(EXTRA_IS_SCAN_TASK, false);
            if (uploadParameter != null) {
                List<ShortSchoolClassInfo> schoolClassInfos = uploadParameter.getShortSchoolClassInfos();
                if (schoolClassInfos != null && schoolClassInfos.size() > 0) {
                    ShortSchoolClassInfo schoolClassInfo = schoolClassInfos.get(0);
                    if (schoolClassInfo != null) {
                        schoolId = schoolClassInfo.getSchoolId();
                        classId = schoolClassInfo.getClassId();
                    }
                }
            }
        }
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            textView.setText(R.string.commit_task_list);
        }

        textView = (TextView) findViewById(R.id.contacts_header_right_btn);
        if (textView != null) {
            //扫码识任务显示右上角的按钮
            textView.setVisibility(isScanTask ? View.VISIBLE : View.VISIBLE);
            textView.setText(R.string.confirm);
            int textColor = getResources().getColor(R.color.text_green);
            textView.setTextColor(textColor);
            textView.setTextSize(16);
            textView.setOnClickListener(this);
        }

        PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(R.id
                .pull_to_refresh);
        if (pullToRefreshView != null) {
            setPullToRefreshView(pullToRefreshView);
        }

        SlideListView listView = (SlideListView) findViewById(R.id.listview);
        if (listView != null) {
            listView.setSlideMode(SlideListView.SlideMode.NONE);
            listView.setClipToPadding(false);
            listView.setDivider(new ColorDrawable());
            listView.setDividerHeight(getResources().getDimensionPixelOffset(R.dimen.vertical_space));

            //作业通用列表
            AdapterViewHelper listViewHelper = new HomeworkResourceAdapterViewHelper(getActivity(),
                    listView, roleType, getMemeberId(), false) {
                //这里只是学生，班主任判断传false。
                @Override
                public void loadData() {
                    selectHomeworkInfo = null;
                    loadUnFinishedTaskList();
                }

                @Override
                public View getView(final int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    final HomeworkListInfo data =
                            (HomeworkListInfo) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;

                    TextView textView = (TextView) view.findViewById(R.id.tv_discuss_count);
                    if (textView != null) {
                        textView.setVisibility(View.VISIBLE);
                        textView.setText("");
                        textView.setBackgroundResource(data.isSelect() ? R.drawable.select : R
                                .drawable.unselect);
                    }

                    ImageView imageView = (ImageView) view.findViewById(R.id.red_point);
                    if (imageView != null) {
                        imageView.setVisibility(View.INVISIBLE);
                    }

                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        return;
                    }
                    checkAllFileItems(false, position);
                    HomeworkListInfo data = (HomeworkListInfo) holder.data;
                    if (data != null) {
                        data.setIsSelect(!data.isSelect());
                        selectHomeworkInfo = data.isSelect() ? data : null;
                    }
                    getCurrAdapterViewHelper().update();

                }

                @Override
                protected void UpdateStudentIsRead(String taskId, String memberId, String taskType) {

                }

                @Override
                protected void updateReadStatus(String taskId, String memberId) {

                }
            };

            setCurrAdapterViewHelper(listView, listViewHelper);
        }

        LinearLayout bottomLayout = (LinearLayout) findViewById(R.id.bottom_layout);
        //扫码是任务也隐藏Bottom的按钮状态
        bottomLayout.setVisibility(isScanTask ? View.GONE : View.GONE);
        if (isScanTask) {
            textView = (TextView) findViewById(R.id.contacts_picker_clear);
            int text_color = getResources().getColor(R.color.text_green);
            if (textView != null) {
                textView.setText(R.string.save);
                textView.setTextColor(text_color);
                textView.setEnabled(true);
                textView.setOnClickListener(this);
            }
            textView = (TextView) findViewById(R.id.contacts_picker_confirm);
            if (textView != null) {
                textView.setText(R.string.send);
                textView.setTextColor(text_color);
                textView.setEnabled(true);
                textView.setOnClickListener(this);
            }
        }
    }

    private void loadViews() {
        if (getCurrAdapterViewHelper().hasData()) {
            getCurrAdapterViewHelper().update();
        } else {
            loadUnFinishedTaskList();
        }
    }

    //拉取学生的未完成的交作业列表
    private void loadUnFinishedTaskList() {
        Map<String, Object> params = new HashMap<>();
        //学校Id，必填
        params.put("SchoolId", schoolId);
        //班级Id，必填
        params.put("ClassId", classId);
        //角色信息，必填，0-学生,1-家长，2-老师
        int role = Utils.transferRoleType(roleType);
        if (role != -1) {
            params.put("RoleType", role);
        }
        //任务状态(0-未完成,1-已完成),学生，家长角色时必填。
//        if (roleType == RoleType.ROLE_TYPE_STUDENT) {
//            params.put("TaskState", 0);
//        }
        //学生ID，非必填，学生、家长角色时必填
        if (roleType == RoleType.ROLE_TYPE_STUDENT) {
            params.put("StudentId", getMemeberId());
        }

        //任务类型，非必填，所有角色筛选时使用，多个任务时，用逗号进行分隔（1,2,3）,0-看微课,1-看课件,2看作业,
        // 3-交作业,4-讨论话题
//        params.put("TaskTypes", String.valueOf(StudyTaskType.SUBMIT_HOMEWORK));
        StringBuilder builder = new StringBuilder();
        builder.append(StudyTaskType.SUBMIT_HOMEWORK);
        builder.append(",");
        builder.append(StudyTaskType.RETELL_WAWA_COURSE);
//        if (isScanTask){
        //当前放开显示 4 种类型的学习任务
        builder.append(",");
        builder.append(StudyTaskType.INTRODUCTION_WAWA_COURSE);
        builder.append(",");
        builder.append(StudyTaskType.TASK_ORDER);
//        }
        builder.append(",").append(StudyTaskType.LISTEN_READ_AND_WRITE);
        builder.append(",").append(StudyTaskType.SUPER_TASK);
        params.put("TaskTypes", builder.toString());
        //分页信息
        params.put("Pager", getPageHelper().getFetchingPagerArgs());
        params.put("Version", 1);

        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_STUDENT_TASK_LIST_URL, params,
                new DefaultPullToRefreshDataListener<HomeworkListResult>(
                        HomeworkListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()
                                || getResult().getModel() == null) {
                            return;
                        }
                        updateResourceListView(getResult());
                    }
                });

    }

    private void updateResourceListView(HomeworkListResult result) {

        if (getPageHelper().isFetchingPageIndex(result.getModel().getPager())) {
            List<HomeworkListInfo> list = result.getModel().getData();
            if (list == null || list.size() <= 0) {
                if (getPageHelper().isFetchingFirstPage()) {
                    getCurrAdapterViewHelper().clearData();
                    TipsHelper.showToast(getActivity(),
                            getString(R.string.no_data));
                } else {
                    TipsHelper.showToast(getActivity(),
                            getString(R.string.no_more_data));
                }
                return;
            }
            list = getScreenNoCommitTask(list);
            if (getPageHelper().isFetchingFirstPage()) {
                getCurrAdapterViewHelper().clearData();
            }
            getPageHelper().updateByPagerArgs(result.getModel().getPager());
            getPageHelper().setCurrPageIndex(
                    getPageHelper().getFetchingPageIndex());
            if (getCurrAdapterViewHelper().hasData()) {
                int position = getCurrAdapterViewHelper().getData().size();
                if (position > 0) {
                    position--;
                }
                getCurrAdapterViewHelper().getData().addAll(list);
                getCurrAdapterView().setSelection(position);
                result.getModel().setData(getCurrAdapterViewHelper().getData());
            } else {
                getCurrAdapterViewHelper().setData(list);
            }
        }
    }

    private List<HomeworkListInfo> getScreenNoCommitTask(List<HomeworkListInfo> list) {
        List<HomeworkListInfo> data = new ArrayList<>();
        for (int i = 0, len = list.size(); i < len; i++) {
            HomeworkListInfo listInfo = list.get(i);
            if ((TextUtils.equals(listInfo.getTaskType(), String.valueOf(StudyTaskType
                    .SUPER_TASK)) && !listInfo.isNeedCommit())
                    || listInfo.getResPropType() == 1) {
                //不需要提交、答题卡类型的任务
            } else {
                data.add(listInfo);
            }
        }
        return data;
    }

    private void checkAllFileItems(boolean isSelect, int position) {
        List<HomeworkListInfo> homeworkListInfos = getCurrAdapterViewHelper().getData();
        if (homeworkListInfos != null && homeworkListInfos.size() > 0) {
            int size = homeworkListInfos.size();
            for (int i = 0; i < size; i++) {
                HomeworkListInfo info = homeworkListInfos.get(i);
                if (info != null && i != position) {
                    info.setIsSelect(isSelect);
                }
            }
        }
    }

    private void commitHomework(final boolean isSave) {
        if (!isSave) {
            if (selectHomeworkInfo == null) {
                TipsHelper.showToast(getActivity(), R.string.pls_select_homework);
                return;
            }
        }
        final UserInfo userInfo = getUserInfo();
        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
            TipsHelper.showToast(getActivity(), R.string.pls_login);
            ActivityUtils.enterLogin(getActivity());
            return;
        }

        if (!isSave && Integer.valueOf(selectHomeworkInfo.getTaskType()) == StudyTaskType.LISTEN_READ_AND_WRITE) {
            enterListenReadAndWriteDetail();
            return;
        } else if (!isSave && Integer.valueOf(selectHomeworkInfo.getTaskType()) == StudyTaskType.SUPER_TASK) {
            enterSuperTaskDetail();
            return;
        }

        if (selectHomeworkInfo != null) {
            taskId = selectHomeworkInfo.getTaskId();
        }

        if (!isSave && selectHomeworkInfo.getResCourseId() > 0) {
            new CheckLqShopPmnHelper().
                    setActivity(getActivity()).
                    setMemberId(getMemeberId()).
                    setFromType(CheckLqShopPmnHelper.FromType.FROM_LQBLOARD_SEND).
                    setRoleType(RoleType.ROLE_TYPE_STUDENT).
                    setCallBackListener(new CallbackListener() {
                        @Override
                        public void onBack(Object result) {
                            commitSelectStudyTask(userInfo,isSave);
                        }
                    }).
                    setResCourseId(selectHomeworkInfo.getResCourseId()).
                    setClassId(classId).
                    setSchoolId(schoolId).
                    check();
        } else {
            commitSelectStudyTask(userInfo, isSave);
        }
    }

    private void commitSelectStudyTask(final UserInfo userInfo, final boolean isSave) {
        if (uploadParameter != null) {
            //增加参数控制上传的资源是否需要拆分
            uploadParameter.setIsNeedSplit(false);
            final LocalCourseDTO data = uploadParameter.getLocalCourseDTO();
            if (data != null) {
                showLoadingDialog(getString(R.string.upload_and_wait), false);
                FileZipHelper.ZipUnzipParam param = new FileZipHelper.ZipUnzipParam(
                        data.getmPath(), Utils.TEMP_FOLDER + Utils.getFileNameFromPath(data.getmPath())
                        + Utils.COURSE_SUFFIX);
                FileZipHelper.zip(param,
                        new FileZipHelper.ZipUnzipFileListener() {
                            @Override
                            public void onFinish(
                                    FileZipHelper.ZipUnzipResult result) {
                                if (result != null && result.mIsOk) {
                                    uploadParameter.setZipFilePath(result.mParam.mOutputPath);
                                    UploadUtils.uploadResource(getActivity(), uploadParameter, new CallbackListener() {
                                        @Override
                                        public void onBack(final Object result) {
                                            if (getActivity() != null) {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        dismissLoadingDialog();
                                                        if (result != null) {
                                                            CourseUploadResult uploadResult = (CourseUploadResult) result;
                                                            if (uploadResult.code != 0) {
                                                                TipMsgHelper.ShowLMsg(getActivity(), R.string
                                                                        .upload_file_failed);
                                                                return;
                                                            }
                                                            if (uploadResult.data != null && uploadResult.data.size() > 0) {
                                                                final CourseData courseData = uploadResult.data.get(0);
                                                                if (courseData != null) {
                                                                    if (!isSave) {
                                                                        commitStudentHomework(taskId, userInfo, courseData);
                                                                    } else {
                                                                        int mediaType = MediaType.MICROCOURSE;
                                                                        if (courseData.type == ResType
                                                                                .RES_TYPE_ONEPAGE) {
                                                                            mediaType = MediaType.ONE_PAGE;
                                                                        }

                                                                        MediaListFragment.updateMedia
                                                                                (getActivity(), getUserInfo()
                                                                                        , courseData
                                                                                                .getShortCourseInfoList(), mediaType, new CallbackListener() {
                                                                                            @Override
                                                                                            public void onBack(Object result) {
                                                                                                TipMsgHelper
                                                                                                        .ShowLMsg(getActivity(), R.string.scan_task_save_ok);
                                                                                                if (getActivity() != null) {
                                                                                                    getActivity().finish();
                                                                                                }
                                                                                            }
                                                                                        });
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            }
                        });
            }
        }
        CourseData cloudCourseData = uploadParameter.getCourseData();
        if (cloudCourseData != null) {
            commitStudentHomework(taskId, userInfo, cloudCourseData);
        }
    }

    private void commitStudentHomework(String taskId, UserInfo userInfo, CourseData courseData) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("TaskId", taskId);
        params.put("StudentId", userInfo.getMemberId());
        if (courseData != null) {
            params.put("StudentResId", courseData.getIdType());
            params.put("StudentResUrl", courseData.resourceurl);
            params.put("StudentResTitle", courseData.nickname);
        }
        if (selectHomeworkInfo != null && Integer.parseInt(selectHomeworkInfo.getTaskType()) ==
                StudyTaskType.INTRODUCTION_WAWA_COURSE) {
            //如果是扫一扫出来显示做任务单 其他都显示复述课件
            if (isScanTask) {
                params.put("CommitType", SelectedReadingDetailFragment.CommitType.TASK_ORDER);
            } else {
                params.put("CommitType", SelectedReadingDetailFragment.CommitType.
                        RETELL_INTRODUCTION_COURSE);
            }
        }
        RequestHelper.RequestModelResultListener listener = new
                RequestHelper.RequestModelResultListener(getActivity(), DataResult.class) {
                    @Override
                    public void onSuccess(String json) {
                        try {
                            if (getActivity() == null) {
                                return;
                            }
                            DataResult result = JSON.parseObject(json, DataResult.class);
                            if (result != null && result.isSuccess()) {
                                if (isScanTask) {
                                    TipMsgHelper.ShowLMsg(getActivity(), R.string.scan_task_send_ok);
                                    //发送成功后让学习任务页面刷新数据
                                    HomeworkMainFragment.isScanTaskFinished = true;
                                } else {
                                    //课件发送到学习任务列表后，学习任务主页面更新数据
                                    HomeworkMainFragment.hasPublishedCourseToStudyTask = true;
                                    TipMsgHelper.ShowLMsg(getActivity(), R.string.publish_course_ok);
                                }
                                EventBus.getDefault().post(new MessageEvent(EventConstant.TRIGGER_UPDATE_COURSE));
                                if (getActivity() != null) {
                                    if (uploadParameter.isTempData()) {
                                        Intent intent = new Intent();
                                        intent.putExtra(SlideManagerHornForPhone.SAVE_PATH,
                                                uploadParameter.getFilePath());
                                        getActivity().setResult(Activity.RESULT_OK, intent);
                                        getActivity().finish();
                                    } else {
                                        getActivity().finish();
                                    }
                                }
                            } else {
                                TipMsgHelper.ShowLMsg(getActivity(), R.string.publish_course_error);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.PUBLISH_STUDENT_HOMEWORK_URL,
                params, listener);
    }

    /**
     * 进入听说+读写界面发送到学习任务
     */
    private void enterListenReadAndWriteDetail() {
        ListenReadAndWriteStudyTaskFragment fragment = new ListenReadAndWriteStudyTaskFragment();
        Bundle args = getArguments();
        args.putInt("TaskType", Integer.valueOf(selectHomeworkInfo.getTaskType()));
        args.putBoolean(ActivityUtils.EXTRA_IS_PICK, true);
        args.putSerializable(ListenReadAndWriteStudyTaskFragment.Constants.EXTRA_TASK_INFO_DATA, selectHomeworkInfo);
        fragment.setArguments(args);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.add(R.id.activity_body, fragment, TAG);
        ft.show(fragment);
        ft.hide(this);
        ft.addToBackStack(null);
        ft.commit();
    }

    /**
     * 进入综合任务的界面
     */
    private void enterSuperTaskDetail() {
        IntroductionSuperTaskFragment fragment = new IntroductionSuperTaskFragment();
        Bundle args = getArguments();
        args.putInt("TaskType", Integer.valueOf(selectHomeworkInfo.getTaskType()));
        args.putBoolean(ActivityUtils.EXTRA_IS_PICK, true);
        args.putSerializable(ListenReadAndWriteStudyTaskFragment.Constants.EXTRA_TASK_INFO_DATA, selectHomeworkInfo);
        fragment.setArguments(args);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.add(R.id.activity_body, fragment, TAG);
        ft.show(fragment);
        ft.hide(this);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contacts_header_right_btn
                || v.getId() == R.id.contacts_picker_confirm) {
            commitHomework(false);
        } else if (v.getId() == R.id.contacts_picker_clear) {
            commitHomework(true);
        } else {
            super.onClick(v);
        }
    }
}
