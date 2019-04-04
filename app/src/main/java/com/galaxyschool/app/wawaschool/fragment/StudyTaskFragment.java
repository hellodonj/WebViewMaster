package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.duowan.mobile.netroid.NetroidError;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.Note.OnlineMediaPaperActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.TaskFinishInfoActivity;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.ArrangeLearningTasksUtil;
import com.galaxyschool.app.wawaschool.common.CampusPatrolUtils;
import com.galaxyschool.app.wawaschool.common.CourseOpenUtils;
import com.galaxyschool.app.wawaschool.common.DateUtils;
import com.galaxyschool.app.wawaschool.common.DensityUtils;
import com.galaxyschool.app.wawaschool.common.EnglishWritingUtils;
import com.galaxyschool.app.wawaschool.common.StudyTaskUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.ExpandDataAdapter;
import com.galaxyschool.app.wawaschool.fragment.library.ExpandListViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.lqwawa.intleducation.common.utils.DateUtil;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.AddedSchoolInfoListResult;
import com.galaxyschool.app.wawaschool.pojo.Emcee;
import com.galaxyschool.app.wawaschool.pojo.EmceeList;
import com.galaxyschool.app.wawaschool.pojo.HomeworkListInfo;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskClassInfo;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskClassInfoListResult;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskFragmentCache;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskInfo;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.views.CalenderPopwindow;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.jeek.calendar.widget.calendar.CalendarUtils;
import com.lqwawa.lqbaselib.pojo.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.galaxyschool.app.wawaschool.R.id.year;

public class StudyTaskFragment extends ContactsExpandListFragment implements View.OnClickListener {

    public static final String TAG = StudyTaskFragment.class.getSimpleName();
    public static final int MAX_PAGE_SIZE = 1;
    public static final int REQUEST_CODE_MEDIAPAPER = 100;
    private TextView headerTitleView, dateView;
    private Date date;
    private CalenderPopwindow popwindow;
    private ExpandableListView expandListView;
    private List<SchoolInfo> schoolInfoList;//加入的学校集合
    private SchoolInfo schoolInfo;//当前学校
    private StudyTaskFragmentCache cache = null;
    private LinearLayout toTaskTipView;
    private PullToRefreshView pullToRefreshView;
    private SparseArray<List<Integer>> mSparseArray = new SparseArray<>();

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_study_task, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        cache = new StudyTaskFragmentCache(getActivity());
        initViews();
        loadSchools();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            if (CampusPatrolUtils.hasStudyTaskAssigned()) {
                CampusPatrolUtils.setHasStudyTaskAssigned(false);
                loadSchools();
            }
        }
    }

    private void updateSchoolView() {
        headerTitleView.setText(schoolInfo == null ? "" : schoolInfo.getSchoolName());
        headerTitleView.setOnClickListener(this);
    }

    private void loadSchools() {
        if (!isLogin()) {
            return;
        }
        Map<String, Object> params = new HashMap();
        if (getUserInfo() != null) {
            params.put("MemberId", getUserInfo().getMemberId());
        }
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_JOIN_SCHOOL_LIST_URL, params,
                new DefaultDataListener<AddedSchoolInfoListResult>
                        (AddedSchoolInfoListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        AddedSchoolInfoListResult result = getResult();
                        if (result == null || !result.isSuccess()
                                || result.getModel() == null) {
                            return;
                        }
                        schoolInfoList = result.getModel().getData();
                        Utils.removeOnlineSchoolInfo(schoolInfoList);
                        if (schoolInfoList == null || schoolInfoList.size() == 0) {
                            return;
                        }
                        Iterator<SchoolInfo> iterator = schoolInfoList.iterator();
                        while (iterator.hasNext()) {
                            SchoolInfo schoolInfo = iterator.next();
                            if (!schoolInfo.isTeacher()) {
                                iterator.remove();
                            }
                        }
                        if (schoolInfoList == null || schoolInfoList.size() == 0)
                            return;
                        if (schoolInfoList.size() > 1) {
                            headerTitleView.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources()
                                    .getDrawable(R.drawable
                                            .down_ico), null);
                            headerTitleView.setCompoundDrawablePadding(DensityUtils.dp2px(getActivity(), 5f));
                        } else {
                            headerTitleView.setVisibility(View.VISIBLE);
                            headerTitleView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        }
                        boolean tag = false;
                        if (!TextUtils.isEmpty(cache.getLatestSchool(getMemeberId()))) {
                            for (SchoolInfo sc : schoolInfoList) {
                                if (sc == null || sc.getSchoolId() == null) {
                                    continue;
                                }
                                if (sc.getSchoolId().equals(cache.getLatestSchool(getMemeberId()))) {
                                    schoolInfo = sc;
                                    schoolInfo.setIsSelect(true);
                                    tag = true;
                                    break;
                                }
                            }
                        }
                        if (!tag) {
                            schoolInfo = schoolInfoList.get(0);
                            schoolInfo.setIsSelect(true);
                        }
                        cache.saveLatestSchool(getMemeberId(), schoolInfo.getSchoolId());
                        updateSchoolView();
                        loadStudyTask();
                    }
                });
    }

    private void loadStudyTask() {
        if (schoolInfo == null) {
            return;
        }
        if (getUserInfo() == null) {
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("SchoolId", schoolInfo.getSchoolId());
        if (getUserInfo() != null) {
            params.put("TaskCreateId", getUserInfo().getMemberId());
            params.put("TaskCreateName", getUserInfo().getRealName());
        }
        params.put("SearchTime", DateUtils.getDateStr(date, DateUtils.DATE_PATTERN_yyyy_MM_dd));
        params.put("Version", 1);
        RequestHelper.RequestDataResultListener<StudyTaskClassInfoListResult> listener = new
                RequestHelper.RequestDataResultListener<StudyTaskClassInfoListResult>(getActivity(),
                        StudyTaskClassInfoListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        StudyTaskClassInfoListResult result = getResult();
                        if (result == null || result.getModel() == null || !result.isSuccess()) {
                            if (getCurrListViewHelper().hasData()) {
                                getCurrListViewHelper().clearData();
                                TipsHelper.showToast(getActivity(), getString(R.string.no_data));
                            }
                            toTaskTipView.setVisibility(View.VISIBLE);
                            pullToRefreshView.setVisibility(View.GONE);
                            return;
                        }
                        List<StudyTaskClassInfo> list = result.getModel().getData();
                        if (list == null || list.size() <= 0) {
                            if (getPageHelper().isFetchingFirstPage()) {
                                getCurrListViewHelper().clearData();
                                TipsHelper.showToast(getActivity(), getString(R.string.no_data));
                            }
                            toTaskTipView.setVisibility(View.VISIBLE);
                            pullToRefreshView.setVisibility(View.GONE);
                            return;
                        }
                        List<StudyTaskClassInfo> classInfos = new ArrayList<StudyTaskClassInfo>();
                        for (StudyTaskClassInfo classInfo : list) {
                            List<StudyTaskInfo> taskInfos = new ArrayList<StudyTaskInfo>();
                            List<StudyTaskInfo> taskInfos1 = classInfo.getTaskInfoList();
                            if (taskInfos1 != null && taskInfos1.size() > 0) {
                                for (StudyTaskInfo taskInfo1 : taskInfos1) {
                                    taskInfos.add(taskInfo1);
                                }
                            }
                            if (taskInfos.size() > 0) {
                                classInfo.setTaskInfoList(taskInfos);
                                classInfos.add(classInfo);
                            }
                        }
                        getCurrListViewHelper().setData(classInfos);
                        toTaskTipView.setVisibility(View.GONE);
                        pullToRefreshView.setVisibility(View.VISIBLE);
                        if (getCurrListViewHelper().getData().size() > 0) {
                            expandAllView();
                        }
                    }
                };
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_STUDY_TASK_URL, params, listener
        );
    }

    private void expandAllView() {
        int groupCount = expandListView.getCount();
        for (int i = 0; i < groupCount; i++) {
            expandListView.expandGroup(i);
        }
    }

    private void initViews() {
        initExpandListView();
        ImageView imageView = (ImageView) findViewById(R.id.contacts_header_left_btn);
        if (imageView != null) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setOnClickListener(this);
        }
        headerTitleView = (TextView) findViewById(R.id.contacts_header_title);
        dateView = (TextView) findViewById(R.id.study_task_date);
        //缓存最近操作时期
//        if (!TextUtils.isEmpty(cache.getLatestDate(getMemeberId()))) {
//            date = DateUtils.getDate(cache.getLatestDate(getMemeberId()), "yyyy-MM-dd E");
//        } else {
//            date = DateUtils.getCurDate();
//        }
        date = DateUtils.getCurDate();
        if (date == null) {
            date = DateUtils.getCurDate();
        }
        String dateStr = getDateStr(date);
        cache.saveLatestDate(getMemeberId(), dateStr);
        dateView.setText(dateStr);
        dateView.setOnClickListener(this);

        imageView = (ImageView) findViewById(R.id.study_task_prev_btn);
        if (imageView != null) {
            imageView.setOnClickListener(this);
        }

        imageView = (ImageView) findViewById(R.id.study_task_next_btn);
        if (imageView != null) {
            imageView.setOnClickListener(this);
        }

        TextView textView = (TextView) findViewById(R.id.study_task_new_btn);
        if (textView != null) {
            textView.setOnClickListener(this);
        }

        textView = (TextView) findViewById(R.id.assign_task_btn);
        if (textView != null) {
            textView.setOnClickListener(this);
        }
        textView = (TextView) findViewById(R.id.back_to_today);
        if (textView != null) {
            textView.setOnClickListener(this);
        }
    }

    protected void initExpandListView() {
        toTaskTipView = (LinearLayout) findViewById(R.id.no_tasks_tip);
        pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        //“讨论”被遮挡可以上拉查看
        setStopPullUpState(false);
        setPullToRefreshView(pullToRefreshView);
        // pullToRefreshView.inVisiableFootView();
        expandListView = ((ExpandableListView) findViewById(R.id.catlog_expand_listview));
        if (expandListView != null) {
            expandListView.setGroupIndicator(null);
            expandListView.setDividerHeight(0);
            ExpandDataAdapter dataAdapter = new ExpandDataAdapter(getActivity(), null,
                    R.layout.contacts_search_expand_list_item,
                    R.layout.item_teacher_study_task_list_homework) {

                @Override
                public Object getChild(int groupPosition, int childPosition) {
                    return ((StudyTaskClassInfo) getData().get(groupPosition))
                            .getTaskInfoList().get(childPosition);
                }


                @Override
                public int getChildrenCount(int groupPosition) {
                    if (hasData() && groupPosition < getGroupCount()) {
                        StudyTaskClassInfo classInfo = (StudyTaskClassInfo)
                                getData().get(groupPosition);
                        if (classInfo != null && classInfo.getTaskInfoList() != null) {
                            return classInfo.getTaskInfoList().size();
                        }
                    }
                    return 0;
                }

                @Override
                public View getChildView(int groupPosition, int childPosition,
                                         boolean isLastChild, View convertView, ViewGroup parent) {
                    View view = super.getChildView(groupPosition, childPosition,
                            isLastChild, convertView, parent);
                    final StudyTaskInfo data = (StudyTaskInfo) getChild(groupPosition, childPosition);
                    MyViewHolder holder = (MyViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new MyViewHolder();
                    }
                    holder.groupPosition = groupPosition;
                    holder.childPosition = childPosition;
                    holder.data = data;
                    if (data == null) return null;
                    //任务类型:0-看微课,1-看课件,2-看作业,3-交作业,4-讨论话题,8-任务单
                    String type = String.valueOf(data.getTaskType());

                    //小红点
                    ImageView imageView = (ImageView) view.findViewById(R.id.red_point);
                    if (imageView != null) {
                        imageView.setVisibility(View.INVISIBLE);
                    }

                    //开始
                    TextView textView = (TextView) view.findViewById(R.id.tv_start_day);

                    //开始日期
                    String startTime = data.getStartTime();
                    //日
                    if (textView != null) {
                        textView.setText(DateUtils.getDateStr(startTime, 2));
                    }
                    //星期
                    textView = (TextView) view.findViewById(R.id.tv_start_weekday);
                    if (textView != null) {
                        textView.setText(DateUtils.getWeekDayName(startTime));
                    }
                    //年份+月份 格式：2016/05
                    textView = (TextView) view.findViewById(R.id.tv_start_date);
                    if (textView != null) {
                        textView.setText(DateUtils.getDateStr(startTime, 0) + "/" + DateUtils.getDateStr(startTime, 1));
                    }

                    //结束
                    textView = (TextView) view.findViewById(R.id.tv_end_day);

                    //结束日期
                    String endTime = data.getEndTime();
                    //日
                    if (textView != null) {
                        textView.setText(DateUtils.getDateStr(endTime, 2));
                    }
                    //星期
                    textView = (TextView) view.findViewById(R.id.tv_end_weekday);
                    if (textView != null) {
                        textView.setText(DateUtils.getWeekDayName(endTime));
                    }
                    ///年份+月份 格式：2016/05
                    textView = (TextView) view.findViewById(R.id.tv_end_date);
                    if (textView != null) {
                        textView.setText(DateUtils.getDateStr(endTime, 0) + "/" + DateUtils.getDateStr(endTime, 1));
                    }

                    //需提交
                    imageView = (ImageView) view.findViewById(R.id.icon_need_to_commit);
                    if (imageView != null) {
                        //只有交作业才显示
                        //目前复述微课也要显示
                        //导读类型也需要显示
                        if (!TextUtils.isEmpty(type)) {
                            if (type.equals("3")
                                    || type.equals("5")
                                    || type.equals("6")
                                    || type.equals("7")
                                    || type.equals("8")
                                    || type.equals("10")
                                    || type.equals("12")
                                    || type.equals("13")
                                    || type.equals("14")
                                    || type.equals("15")
                                    || (type.equals("11") && data.isNeedCommit())) {
                                imageView.setVisibility(View.VISIBLE);
                            } else {
                                imageView.setVisibility(View.GONE);
                            }
                        }
                    }

                    //任务类型:0-看微课,1-看课件,2-看作业,3-交作业,4-讨论话题，5-复述微课 6-导读

                    textView = (TextView) view.findViewById(R.id.tv_homework_type);
                    if (textView != null) {
                        //初始化默认值
                        textView.setBackgroundResource(0);
                        if (!TextUtils.isEmpty(type)) {
                            if (type.equals("0")) {
                                //目前“看微课”名称改为“看课件”，图标替换为看课件的图标，原来的看课件暂时不管，仍然隐藏。
                                textView.setBackgroundResource(R.drawable.scan_class_course);
                            } else if (type.equals("1")) {
                                textView.setBackgroundResource(R.drawable.scan_class_course);
                            } else if (type.equals("2")) {
                                //看作业
                                textView.setBackgroundResource(R.drawable.icon_other);
                            } else if (type.equals("3")) {
                                //交作业，目前看作业和交作业背景图片是一致的，只是用“需提交”来区分。
                                textView.setBackgroundResource(R.drawable.icon_other);
                            } else if (type.equals("4")) {
                                textView.setBackgroundResource(R.drawable.discuss_topic);
                            } else if (type.equals("5") || type.equals("12")) {
                                textView.setBackgroundResource(R.drawable.retell_course_ico);
                            } else if (type.equals("6")) {
                                textView.setBackgroundResource(R.drawable.introduction_type);
                            } else if (type.equals("7")) {
                                textView.setBackgroundResource(R.drawable.english_writing_icon);
                            } else if (type.equals("8") || type.equals("13")) {
                                //做任务单
                                textView.setBackgroundResource(R.drawable.icon_do_task);
                            } else if (type.equals("9")) {
                                //新版看课件
                                textView.setBackgroundResource(R.drawable.scan_class_course);
                            } else if (type.equals("10")) {
                                //听说 + 读写
                                textView.setBackgroundResource(R.drawable.listen_read_and_write_icon);
                            } else if (type.equals("11")) {
                                //综合任务
                                textView.setBackgroundResource(R.drawable.icon_super_task);
                            } else if (type.equals("14") || type.equals("15")){
                                //Q配音
                                textView.setBackgroundResource(R.drawable.icon_q_dubbing);
                            }
                        }
                    }

                    //删除
                    View deleteView = view.findViewById(R.id.layout_delete_homework);
                    if (deleteView != null) {
                        //如果任务的创建者是自己才显示删除的按钮
                        if (TextUtils.equals(getMemeberId(), data.getTaskCreateId())) {
                            deleteView.setVisibility(View.VISIBLE);
                        } else {
                            deleteView.setVisibility(View.INVISIBLE);
                        }
                        deleteView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showRemoveTaskDialog(data);
                            }
                        });
                    }

                    //标题
                    textView = (TextView) view.findViewById(R.id.tv_homework_title);
                    if (textView != null) {
                        textView.setText(data.getTaskTitle());
                    }

                    //作业布置者,只对老师隐藏。
                    textView = (TextView) view.findViewById(R.id.tv_homework_assigner);
                    if (textView != null) {
//                        UserInfo userInfo = getUserInfo();
//                        if (userInfo != null) {
//                            String name = "";
//                            if (!TextUtils.isEmpty(userInfo.getRealName())) {
//                                name = userInfo.getRealName();
//                            } else {
//                                name = userInfo.getNickName();
//                            }
//                            textView.setText(name);
//                        }
                        if (data.getST_StudyGroupId() > 0) {
                            textView.setText(getString(R.string.send_to, data.getST_StudyGroupName()));
                        } else {
                            textView.setText(getString(R.string.send_to_the_whole_class));
                        }
                    }

                    //作业状态布局，仅对教师可见。
                    TextView homeworkStatusLayout = (TextView) view.findViewById(R.id.tv_finish_status);
                    if (homeworkStatusLayout != null) {
                        homeworkStatusLayout.setVisibility(View.VISIBLE);
                    }

                    //全部完成的话，就隐藏已参与/未参与/已完成/未完成，显示“全部完成”,反之隐藏。

                    //总数
                    int taskNum = -1;
                    //已完成/已参与任务数
                    int finishTaskCount = -1;
                    //未完成/未参与任务数
                    int unFinishTaskCount = -1;
                    taskNum = data.getTaskNum();
                    finishTaskCount = data.getFinishTaskCount();
                    unFinishTaskCount = taskNum - finishTaskCount;
                    //暂时屏蔽负值，直接显示“0”。
                    if (unFinishTaskCount < 0) {
                        unFinishTaskCount = 0;
                    }

                    boolean isFinishAll = ((taskNum > 0) && (taskNum == finishTaskCount));
                    //全部参与/完成
                    if (isFinishAll) {
                        //除了话题讨论显示参与，其他都显示完成。
                        if (type.equals("4")) {
                            //全部参与
                            homeworkStatusLayout.setText(getString(R.string.n_people_join, String.valueOf(data
                                    .getFinishTaskCount())));
                        } else {
                            //全部完成
                            homeworkStatusLayout.setText(getString(R.string.n_finish_all, taskNum));
                        }
                    }
                    //部分参与/完成
                    else {
                        if (type.equals("4")) {
                            //已参与/未参与
                            //目前仅显示已参与人数即可
                            homeworkStatusLayout.setText(getString(R.string.n_people_join, String.valueOf(data
                                    .getFinishTaskCount())));
                        } else {
                            //已完成/未完成
                            homeworkStatusLayout.setText(getString(R.string.n_finish, finishTaskCount)
                                    + " / " + getString(R.string.n_unfinish, unFinishTaskCount));
                        }

                    }

                    //讨论数
                    textView = (TextView) view.findViewById(R.id.tv_discuss_count);
                    if (textView != null) {
                        textView.setVisibility(View.INVISIBLE);
                        textView.setText(getString(R.string.discussion, data.getCommentCount()));
                    }
                    int padding = (int) (10 * MyApplication.getDensity());
                    int bottomPadding = (int) (15 * MyApplication.getDensity());
                    if (childPosition == getChildrenCount(groupPosition) - 1) {
                        view.setPadding(padding, padding, padding, bottomPadding);
                    } else {
                        view.setPadding(padding, padding, padding, 0);
                    }

                    view.setTag(holder);
                    return view;
                }

                @Override
                public View getGroupView(int groupPosition, boolean isExpanded,
                                         View convertView, ViewGroup parent) {
                    View view = super.getGroupView(groupPosition, isExpanded, convertView, parent);
                    StudyTaskClassInfo data = (StudyTaskClassInfo) getGroup(groupPosition);
                    View headerView = view.findViewById(R.id.contacts_item_header_layout);
                    if (headerView != null) {
                        headerView.setVisibility(View.GONE);
                    }
                    View contentLayout = view.findViewById(R.id.contacts_item_content_layout);
                    if (contentLayout != null) {
                        contentLayout.setBackgroundColor(getResources().getColor(R.color.main_bg_color));
                    }
                    ImageView imageView = (ImageView) view.findViewById(R.id.contacts_item_icon);
                    if (imageView != null) {
                        imageView.setVisibility(View.GONE);
                    }
                    TextView textView = (TextView) view.findViewById(R.id.contacts_item_title);
                    if (textView != null) {
                        textView.setText(data.getClassName());
                    }
                    imageView = (ImageView) view.findViewById(R.id.contacts_item_arrow);
                    if (imageView != null) {
                        if (data.getTaskInfoList() == null || data.getTaskInfoList().size() == 0) {
                            imageView.setVisibility(View.INVISIBLE);
                        } else {
                            imageView.setVisibility(View.VISIBLE);
                            imageView.setImageResource(isExpanded ?
                                    R.drawable.list_exp_up : R.drawable.list_exp_down);
                        }
                    }

                    //底部分隔线
                    View bottomLineView = view.findViewById(R.id.bottom_line);
                    if (bottomLineView != null) {
                        bottomLineView.setVisibility(View.VISIBLE);
                    }

                    MyViewHolder holder = (MyViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new MyViewHolder();
                    }
                    view.setTag(holder);
                    holder.data = data;
                    return view;
                }

            };

            ExpandListViewHelper listViewHelper = new ExpandListViewHelper(getActivity(),
                    expandListView, dataAdapter) {
                @Override
                public void loadData() {
                    loadStudyTask();
                }

                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {
                    MyViewHolder holder = (MyViewHolder) v.getTag();
                    if (holder == null || holder.data == null) {
                        return false;
                    }
                    StudyTaskInfo taskInfo = (StudyTaskInfo) holder.data;
                    getDataAdapter().notifyDataSetChanged();
                    StudyTaskClassInfo classInfo = (StudyTaskClassInfo) getCurrListViewHelper()
                            .getData().get(groupPosition);
                    String className = classInfo != null ? classInfo.getClassName() : "";
                    if (taskInfo != null) {
                        //进入和班级空间一样的逻辑
                        enterSchoolSpaceStudyTaskActivities(taskInfo, classInfo.getClassId());
                    }
                    return true;
                }

                @Override
                public boolean onGroupClick(ExpandableListView parent, View v,
                                            int groupPosition, long id) {

                    MyViewHolder holder = (MyViewHolder) v.getTag();
                    if (holder == null || holder.data == null) {
                        return false;
                    }
                    StudyTaskClassInfo calalog = (StudyTaskClassInfo) holder.data;
                    if (calalog.getTaskInfoList() != null && calalog.getTaskInfoList().size() > 0) {
                        return false;
                    }
                    getDataAdapter().notifyDataSetChanged();
                    return false;
                }
            };
            listViewHelper.setData(null);
            setCurrListViewHelper(expandListView, listViewHelper);
        }
    }

    /**
     * 个人空间跳转需要和班级空间跳转保持一致
     */
    private void enterSchoolSpaceStudyTaskActivities(StudyTaskInfo data, String classId) {
        if (data != null) {
            UserInfo userInfo = getUserInfo();
            if (userInfo != null) {
                HomeworkListInfo info = data.toHomeworkListInfo(getMemeberId(), userInfo
                        .getNickName());
                boolean isHeadMaster = userInfo.isHeaderTeacher();
                int roleType = RoleType.ROLE_TYPE_TEACHER;
                if (info.getAirClassId() == 0) {
                    CourseOpenUtils.openStudyTask(getActivity(), info, roleType, isHeadMaster,
                            getMemeberId(), null, null, userInfo, false);
                } else {
                    accordingAirClassIdAnalysisData(info, classId);
                }
            }
        }
    }


    /**
     * 根据直播的id拉取详情页数据分析权限问题
     */
    private void accordingAirClassIdAnalysisData(final HomeworkListInfo data, final String classId) {
        Map<String, Object> params = new HashMap<>();
        params.put("Id", data.getAirClassId());
        DefaultDataListener listener = new DefaultDataListener<DataModelResult>(
                DataModelResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                DataModelResult result = getResult();
                if (result == null || !result.isSuccess()
                        || result.getModel() == null) {
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    JSONObject model = jsonObject.optJSONObject("Model");
                    Emcee emcee = com.alibaba.fastjson.JSONObject.parseObject(model.toString(),
                            Emcee.class);
                    analysisCurrentUserIsReporter(data, emcee, classId);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onError(NetroidError error) {
                super.onError(error);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.GET_AIRCLASS_DETAIL_BY_ID_BASE_URL,
                params, listener);
    }

    /**
     * 分析当前的用户是不是小编的身份
     *
     * @param model
     */
    private void analysisCurrentUserIsReporter(HomeworkListInfo data, Emcee model, String classId) {
        if (model != null) {
            List<EmceeList> reporters = model.getEmceeList();
            boolean flag = false;
            for (int i = 0; i < reporters.size(); i++) {
                EmceeList emceeMember = reporters.get(i);
                String classIds = emceeMember.getClassIds();
                String schoolIds = emceeMember.getSchoolIds();
                if (TextUtils.equals(getMemeberId(), emceeMember.getMemberId())
                        && !TextUtils.isEmpty(classIds)) {
                    EmceeList tempData = null;
                    if (classIds.contains(",")) {
                        String[] splitClassArray = classIds.split(",");
                        String[] splitSchoolArray = schoolIds.split(",");
                        for (int j = 0; j < splitClassArray.length; j++) {
                            tempData = new EmceeList();
                            tempData.setMemberId(emceeMember.getMemberId());
                            tempData.setSchoolIds(splitSchoolArray[j]);
                            tempData.setClassIds(splitClassArray[j]);
                            if (TextUtils.equals(classId, splitClassArray[j])) {
                                flag = true;
                                break;
                            }
                        }
                    } else {
                        if (TextUtils.equals(classId, classIds)) {
                            flag = true;
                            break;
                        }
                    }
                }
            }
            //如果flag为true表示是小编的身份
            if (flag) {
                //小编当中判断是不是主编
                if (TextUtils.equals(getMemeberId(), model.getAcCreateId())) {
                    data.setOnlineHost(true);
                } else {
                    data.setOnlineReporter(true);
                }
            }
        }
        UserInfo userInfo = getUserInfo();
        CourseOpenUtils.openStudyTask(getActivity(), data, RoleType.ROLE_TYPE_TEACHER, userInfo.isHeaderTeacher(),
                getMemeberId(), null, null, userInfo, false);
    }

    /**
     * 进入英文写作页面
     *
     * @param taskInfo
     */
    private void enterEnglishWritingCommitActivity(StudyTaskInfo taskInfo) {

        if (taskInfo == null) {
            return;
        }
        //英文写作
        HomeworkListInfo data = new HomeworkListInfo();
        data.setTaskId(taskInfo.getTaskId());
        data.setTaskTitle(taskInfo.getTaskTitle());
        EnglishWritingUtils.enterEnglishWritingPageByRoleType(getActivity(),
                RoleType.ROLE_TYPE_TEACHER,
                getMemeberId(), "", "", data, null);
    }

    private void enterTaskFinishInfo(StudyTaskInfo task, String className) {
        if (task == null) {
            return;
        }
        Intent intent = new Intent(getActivity(), TaskFinishInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(TaskFinishInfoActivity.TASK, task);
        bundle.putString(TaskFinishInfoActivity.CLASS_NAME, className);
        intent.putExtras(bundle);
        startActivityForResult(intent, CampusPatrolPickerFragment.
                REQUEST_CODE_HOMEWORK_TASK_FINISH_INFO);
    }

    void showRemoveTaskDialog(final StudyTaskInfo taskInfo) {
        if (getActivity().isFinishing()) {
            return;
        }
        ContactsMessageDialog dialog = new ContactsMessageDialog(getActivity(),
                R.style.Theme_ContactsDialog,
                null,
                taskInfo != null ? getString(R.string.delete_tip, taskInfo.getTaskTitle()) : "",
                getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, getString(R.string.confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeTask(taskInfo);
                        dialog.dismiss();
                    }
                });
        dialog.setCancelable(true);
        dialog.show();
    }

    private void removeTask(final StudyTaskInfo taskInfo) {
        //从网络删除，然后从本地删除 task（目前的算法没有在删完网络后重新从网络拉取）
        if (taskInfo == null) return;
        if (getMemeberId() == null) return;
        Map<String, Object> params = new HashMap();
        params.put("TaskCreateId", getMemeberId());
        params.put("TaskId", taskInfo.getTaskId());
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.DELETE_STUDY_TASK_URL, params,
                new RequestHelper.RequestDataResultListener<DataModelResult>(getActivity(),
                        DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        DataModelResult result = getResult();
                        if (result == null || !result.isSuccess()) {
                            TipMsgHelper.ShowMsg(getActivity(), getString(R.string.delete_request_failed));
                            return;
                        }
                        TipMsgHelper.ShowMsg(getActivity(), getString(R.string.delete_request_success));
                        List<StudyTaskClassInfo> classInfos = (List<StudyTaskClassInfo>)
                                getCurrListViewHelper().getData();
                        for (StudyTaskClassInfo classInfo1 : classInfos) {
                            List<StudyTaskInfo> taskInfos = classInfo1.getTaskInfoList();
                            for (StudyTaskInfo taskInfo1 : taskInfos) {
                                if (taskInfo1.getTaskId().equals(taskInfo.getTaskId())) {
                                    taskInfos.remove(taskInfo1);
                                    break;
                                }
                            }
                        }
                        //重新拉取数据
                        loadStudyTask();
//                        getCurrListViewHelper().update();
                    }
                });
    }

    private class MyViewHolder extends ViewHolder {
        int groupPosition;
        int childPosition;
    }


    private String getDateStr(Date date) {
        Locale defaultLocale;
        String pattern = "yyyy-MM-dd E";
        String locale = Locale.getDefault().getLanguage();
        if (locale.contains("zh")) {
            defaultLocale = Locale.CHINA;
        } else {
            defaultLocale = Locale.US;
        }
        SimpleDateFormat df = new SimpleDateFormat(pattern, defaultLocale);
        return df.format(date);
    }

    private void updateDateView(Date date) {
        String dateStr = getDateStr(date);
        cache.saveLatestDate(getMemeberId(), dateStr);
        dateView.setText(dateStr);
        loadStudyTask();
    }

    private void toggleSchool(View v) {
        headerTitleView.setVisibility(View.VISIBLE);
        headerTitleView.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable
                .up_ico), null);
        headerTitleView.setCompoundDrawablePadding(DensityUtils.dp2px(getActivity(), 5f));
        showMoreSchools(getActivity(), ToggleSchoolListener, v);

    }

    //展示学校列表
    private void showMoreSchools(Activity activity, AdapterView.OnItemClickListener itemClickListener,
                                 View view) {
        List<com.galaxyschool.app.wawaschool.views.PopupMenu.PopupMenuData> itemDatas
                = new ArrayList<com.galaxyschool.app.wawaschool.views.PopupMenu.PopupMenuData>();
        if (schoolInfoList == null || schoolInfoList.size() == 0) {
            return;
        }
        for (int i = 0; i < schoolInfoList.size(); i++) {
            com.galaxyschool.app.wawaschool.views.PopupMenu.PopupMenuData data
                    = new com.galaxyschool.app.wawaschool.views.PopupMenu.PopupMenuData();
            if (schoolInfoList.get(i) != null && !TextUtils.isEmpty(schoolInfoList.get(i)
                    .getSchoolName())) {
                data.setText(schoolInfoList.get(i).getSchoolName());
                data.setIsSelect(schoolInfoList.get(i).isSelect());
                itemDatas.add(data);
            }
        }
        com.galaxyschool.app.wawaschool.views.PopupMenu popupMenu = new com.galaxyschool.app
                .wawaschool.views.PopupMenu(activity, itemClickListener, itemDatas, 0.5f);
        WindowManager wm = (WindowManager) getActivity()
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();//屏幕宽度
        popupMenu.showAsDropDown(view, (int) (width * (1 - 0.5f) / 2), 0);
        popupMenu.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                headerTitleView.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable
                        .down_ico), null);
                headerTitleView.setCompoundDrawablePadding(DensityUtils.dp2px(getActivity(), 5f));
            }
        });
    }  //切换学校

    private AdapterView.OnItemClickListener ToggleSchoolListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view,
                                int position, long id) {
            for (SchoolInfo classInfo1 : schoolInfoList) {
                classInfo1.setIsSelect(false);
            }
            SchoolInfo schoolInfo1 = schoolInfoList.get(position);
            if (schoolInfo1 == null || schoolInfo1.getSchoolId() == null) {
                return;
            }
            if (schoolInfo1.getSchoolId().equals(schoolInfo.getSchoolId())) {
                return;
            } else {
                schoolInfo = schoolInfo1;
                schoolInfo.setIsSelect(true);
            }
            //更換学校的同时班级列表，点击班级列表时展示新的班级列表，此时还要从新的班级列表中选出第一个班级作为默认的
            // 班级去加载数据
            if (schoolInfo != null) {
                cache.saveLatestSchool(getMemeberId(), schoolInfo.getSchoolId());
                updateSchoolView();
                date = DateUtils.getCurDate();
                if (mSparseArray != null) {

                    mSparseArray.clear();
                }
                if (popwindow != null) {
                    popwindow = null;
                }
                updateDateView(date);
            }
        }
    };

    private void switchPrev() {
        date = DateUtils.getPrevDate(date);
        setPopWindowDateTime();
        updateDateView(date);
    }

    private void switchToday() {
        date = DateUtils.getCurDate();
        setPopWindowDateTime();
        updateDateView(date);
    }

    private void switchNext() {
        date = DateUtils.getNextDate(date);
        setPopWindowDateTime();
        updateDateView(date);
    }

    private void setPopWindowDateTime() {
        if (popwindow != null) {
            popwindow.upDateCalendarView(date);
        }
    }

    private void showTaskTypeDialog() {
        StudyTaskUtils.handleSubjectSettingData(getActivity(),getMemeberId(),v -> {
            ArrangeLearningTasksUtil.getInstance()
                    .setActivity(getActivity())
                    .setCallBackListener(new ArrangeLearningTasksUtil.ArrangeLearningTaskListener() {
                        @Override
                        public void selectedTypeData(String title, int type) {
                            if (schoolInfo != null) {
                                ActivityUtils.enterIntroductionCourseActivity(getActivity(), title, type,
                                        schoolInfo, false, false, null,schoolInfo.getSchoolId(), null);
                            }
                        }
                    })
                    .show();
        });
    }

    /***
     * 获取PopupWindow实例
     */
    private void changeData(View v) {
        if (popwindow != null) {
            if (!popwindow.isShowing()) {
                if (mSparseArray != null) {
                    mSparseArray.clear();
                }
                popwindow.showPopupMenu(v, true);
            }
            return;
        }
        popwindow = new CalenderPopwindow(date, getActivity(), new CalenderPopwindow.OnDatePickListener() {
            @Override
            public void onDatePick(Date monthDay, int position) {
                date = monthDay;
                String dateStr = getDateStr(date);
                cache.saveLatestDate(getMemeberId(), dateStr);
                dateView.setText(dateStr);
                List<Integer> integerList = mSparseArray.get(position);
                if (integerList != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    if (integerList.size() > 0 && integerList.contains(day)) {
                        loadStudyTask();
                    } else {
//                        TipsHelper.showToast(getActivity(), getString(R.string.no_data));
                        toTaskTipView.setVisibility(View.VISIBLE);
                        pullToRefreshView.setVisibility(View.GONE);
                    }

                } else {
                    loadStudyTask();
                }
            }

            @Override
            public void loadDateSignData(String filterStartTimeBegin, int position) {
                List<Integer> integerList = mSparseArray.get(position);
                if (integerList == null) {

                    loadDateSignDatas(filterStartTimeBegin, position);
                } else {
                    String[] splitArray = filterStartTimeBegin.split("-");
                    int year = 0;
                    int month = 0;
                    try {
                        year = Integer.valueOf(splitArray[0]);
                        month = Integer.valueOf(splitArray[1]);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    if (integerList.size() > 0) {
                        CalendarUtils.getInstance(getContext()).setTaskHints(year, month - 1, integerList);
                        if (popwindow != null) {
                            popwindow.updateSignData();
                        }
                    }
                }
            }
        });
        popwindow.resetMonthView();
        popwindow.showPopupMenu(v, false);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.contacts_header_left_btn:
                finish();
                break;
            case R.id.contacts_header_title:
                if (schoolInfoList.size() > 1) {
                    toggleSchool((View) v.getParent());
                }
                break;
            case R.id.study_task_prev_btn:
                switchPrev();
                break;
            case R.id.study_task_next_btn:
                switchNext();
                break;
            case R.id.study_task_new_btn:
            case R.id.assign_task_btn:
                if (schoolInfoList == null || schoolInfoList.size() == 0) {
                    TipMsgHelper.getInstance().showOneTips(getContext(), getResources().getString(R.string.no_class_tip));
                    return;
                }
                showTaskTypeDialog();
                break;
            case R.id.study_task_date:
                changeData((View) v.getParent());
                break;
            case R.id.back_to_today:
                switchToday();
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loadEventData(MessageEvent messageEvent) {
        if (messageEvent != null) {
            String updateAction = messageEvent.getUpdateAction();
            if (TextUtils.equals(updateAction, "load_study_task_data")) {
                //删除提交的学生任务回来刷新数据
                loadStudyTask();
            }
        }
    }

    @Override
    public void onDestroy() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_MEDIAPAPER) {
            if (data != null) {
                Bundle args = data.getExtras();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                PublishStudyTaskFragment fragment = new PublishStudyTaskFragment();
                fragment.setArguments(args);
                ft.replace(R.id.activity_body, fragment, PublishStudyTaskFragment.TAG);
                ft.addToBackStack(null);
                ft.commit();
            }
        }

        if (data == null) {
            if (requestCode == CampusPatrolPickerFragment.REQUEST_CODE_DISCUSSION_TOPIC) {
                //话题讨论
                if (TopicDiscussionFragment.hasCommented()) {
                    TopicDiscussionFragment.setHasCommented(false);
                    loadSchools();
                }
            } else if (requestCode == CampusPatrolPickerFragment
                    .REQUEST_CODE_HOMEWORK_COMMIT) {
                //从提交作业列表页面返回，是否要刷新页面。
                if (HomeworkCommitFragment.hasCommented()) {
                    //reset value
                    HomeworkCommitFragment.setHasCommented(false);
                    //需要刷新
                    loadSchools();
                }
            } else if (requestCode == CampusPatrolPickerFragment
                    .EDIT_NOTE_DETAILS_REQUEST_CODE) {
                if (OnlineMediaPaperActivity.hasContentChanged()) {
                    //看作业，编辑帖子。
                    //reset value
                    OnlineMediaPaperActivity.setHasContentChanged(false);
                    //需要刷新
                    loadSchools();
                }
            } else if (requestCode == CampusPatrolPickerFragment
                    .REQUEST_CODE_DISCUSSION_COURSE_DETAILS) {
                if (PictureBooksDetailFragment.hasCommented()) {
                    //看课件
                    //reset value
                    PictureBooksDetailFragment.setHasCommented(false);
                    //需要刷新
                    loadSchools();
                }
            }
        }
    }


    /**
     * 加载课程日期标记的数据
     */
    private void loadDateSignDatas(String filterStartTimeBegin, final int position) {
        if (schoolInfo == null) {
            return;
        }
        Map<String, Object> param = new ArrayMap<>();
        param.put("SchoolId", schoolInfo.getSchoolId());
        param.put("MemberId", getMemeberId());
        Date date = DateUtils.stringToDate(filterStartTimeBegin, DateUtils.DATE_PATTERN_yyyy_MM_dd);
        final String startTimeBegin = DateUtils.getMonthFirstDay(date, DateUtils.DATE_PATTERN_yyyy_MM_dd);
        param.put("StartTimeBegin", startTimeBegin);
        param.put("StartTimeEnd", DateUtils.getMonthLastDay(date, DateUtils.DATE_PATTERN_yyyy_MM_dd));
        DefaultDataListener listener = new DefaultDataListener<DataModelResult>(DataModelResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                super.onSuccess(jsonString);
                DataModelResult result = getResult();
                if (result != null && result.isSuccess()) {
                    try {
                        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(jsonString);
                        com.alibaba.fastjson.JSONObject model = (com.alibaba.fastjson.JSONObject) jsonObject.get("Model");
                        com.alibaba.fastjson.JSONArray array = model.getJSONArray("Data");
                        if (array != null) {
                            List<String> signData = new ArrayList<>();
                            for (int i = 0; i < array.size(); i++) {
                                com.alibaba.fastjson.JSONObject object = (com.alibaba.fastjson.JSONObject) array.get(i);
                                if (object != null) {
                                    boolean haveLive = object.getBoolean("bolHaveLive");
                                    if (haveLive) {
                                        signData.add(object.getString("Date"));
                                    }
                                }
                            }
                            handleSignTimeData(signData, startTimeBegin, position);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.GET_STUDY_TASKLIST_DATE_SIGN_URL,
                param, listener);
    }

    private void handleSignTimeData(List<String> signTimeData, String searchTime, int position) {

        List<Integer> integerList = new ArrayList<>();
        mSparseArray.put(position, integerList);

        if (signTimeData != null && signTimeData.size() > 0) {
            for (int i = 0, len = signTimeData.size(); i < len; i++) {
                String signTime = signTimeData.get(i);
                signTime = DateUtils.getStringToString(signTime, DateUtils.DATE_PATTERN_yyyy_MM_dd);
                integerList.add(getDayOfString(signTime));
            }
        }
        String[] splitArray = searchTime.split("-");
        int year = 0;
        int month = 0;
        try {
            year = Integer.valueOf(splitArray[0]);
            month = Integer.valueOf(splitArray[1]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (integerList.size() >= 0) {
            CalendarUtils.getInstance(getContext()).setTaskHints(year, month - 1, integerList);
            if (popwindow != null) {
                popwindow.updateSignData();
            }
        }
    }

    private int getDayOfString(String dateString) {
        if (!TextUtils.isEmpty(dateString)) {
            String[] spiltArray = dateString.split("-");
            try {
                return Integer.valueOf(spiltArray[2]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return 1;
    }
}
