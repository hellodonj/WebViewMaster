package com.galaxyschool.app.wawaschool.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.Request;
import com.galaxyschool.app.wawaschool.AirClassroomDetailActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.DateUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.pojo.Emcee;
import com.galaxyschool.app.wawaschool.pojo.EmceeList;
import com.galaxyschool.app.wawaschool.pojo.EmceeListResult;
import com.galaxyschool.app.wawaschool.pojo.PublishClass;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.jeek.calendar.widget.calendar.CalendarUtils;
import com.jeek.calendar.widget.calendar.OnCalendarClickListener;
import com.jeek.calendar.widget.calendar.schedule.ScheduleLayout;
import com.jeek.calendar.widget.calendar.week.WeekCalendarView;
import com.lqwawa.intleducation.common.ui.DatePickerPopupView;
import com.lqwawa.lqbaselib.net.ThisStringRequest;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.osastudio.common.utils.LQImageLoader;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LiveTimetableFragment extends ContactsListFragment implements
        OnCalendarClickListener, View.OnClickListener {
    private TextView timeTitleTextV;
    private ImageView backImageV;
    private TextView rightTextV;
    private RadioButton deleteAllClassRB;
    private PullToRefreshView pullToRefreshView;
    private ScheduleLayout slSchedule;// 日历控件
    private String[] dateTypeArray = new String[]{"年", "月", "日"};
    private int mSelectYear, mSelectMonth, mSelectDay;
    private String schoolId;
    private String classId;
    private String filterStartTimeBegin;
    private String filterStartTimeEnd;
    private int roleType;
    private boolean isHeadMaster;
    private boolean isTeacher;
    private boolean selectDayChanged;
    private LinearLayout defaultHolderBg;
    private boolean isHistoryClass;
    private boolean isFinishLecture;//是否完成授课
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_live_timetable, null);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadIntentData();
        initView();
        initData();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void loadIntentData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            schoolId = bundle.getString(AirClassroomFragment.Constants.EXTRA_CONTACTS_SCHOOL_ID);
            classId = bundle.getString(AirClassroomFragment.Constants.EXTRA_CONTACTS_CLASS_ID);
            roleType = bundle.getInt(AirClassroomFragment.Constants.EXTRA_ROLE_TYPE);
            isHeadMaster = bundle.getBoolean(AirClassroomFragment.Constants.EXTRA_IS_HEADMASTER, false);
            isTeacher = bundle.getBoolean(AirClassroomFragment.Constants.EXTRA_IS_TEACHER, false);
            isHistoryClass = bundle.getBoolean(ActivityUtils.EXTRA_IS_HISTORY_CLASS,false);
            isFinishLecture = bundle.getBoolean(ActivityUtils.EXTRA_IS_FINISH_LECTURE,false);
            filterStartTimeBegin = DateUtils.getDateYmdStr();
            filterStartTimeEnd = DateUtils.getDateYmdStr();
        }
    }

    private void initView() {
        //日历
        slSchedule = (ScheduleLayout) findViewById(com.lqwawa.intleducation.R.id.slSchedule);
        slSchedule.setOnCalendarClickListener(this);
        //没有数据默认的背景
        defaultHolderBg = (LinearLayout) findViewById(R.id.layout_place_holder);
        //显示当前的时间
        timeTitleTextV = (TextView) findViewById(R.id.contacts_header_title);
        if (timeTitleTextV != null) {
            //按类型显示小图标
            Drawable drawable = getActivity().getResources().getDrawable(R.drawable.arrow_down_gray_ico);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            timeTitleTextV.setCompoundDrawablePadding(10);
            timeTitleTextV.setCompoundDrawables(null, null, drawable, null);
            TextPaint tp = timeTitleTextV.getPaint();
            tp.setFakeBoldText(true);
            Calendar calendar = Calendar.getInstance();
            mSelectYear = calendar.get(Calendar.YEAR);
            mSelectMonth = calendar.get(Calendar.MONTH);
            mSelectDay = calendar.get(Calendar.DAY_OF_MONTH);
            if (timeTitleTextV != null) {
                timeTitleTextV.setText(new StringBuffer().append(mSelectYear).append(dateTypeArray[0])
                        .append(mSelectMonth + 1).append(dateTypeArray[1]).toString());
            }
            timeTitleTextV.setOnClickListener(this);
        }
        //显示今天的btn
        rightTextV = (TextView) findViewById(R.id.contacts_header_right_btn);
        if (rightTextV != null) {
            rightTextV.setText(R.string.today_simple);
            rightTextV.setTextSize(16);
            rightTextV.setVisibility(View.VISIBLE);
            rightTextV.setOnClickListener(this);
        }
        //返回键
        backImageV = (ImageView) findViewById(R.id.contacts_header_left_btn);
        if (backImageV != null) {
            backImageV.setOnClickListener(this);
        }

        TextView createOnlineBtn = (TextView) findViewById(R.id.tv_create_online);
        if (createOnlineBtn != null){
            if ((isHeadMaster || isTeacher) && !isHistoryClass && !isFinishLecture){
                createOnlineBtn.setVisibility(View.VISIBLE);
                createOnlineBtn.setOnClickListener(this);
            }
        }
    }

    private void initData() {
        pullToRefreshView = (PullToRefreshView)
                findViewById(R.id.pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);
        GridView listView = (GridView) findViewById(R.id.resource_list_view);
        if (listView != null) {
            AdapterViewHelper adapterViewHelper = new AdapterViewHelper(getActivity(),
                    listView, R.layout.resource_item_online) {
                @Override
                public void loadData() {
                    loadTimeTableListData();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    if (view != null) {
                        if (getData() == null) {
                            return view;
                        }
                        final Emcee data = (Emcee) getData().get(position);
                        if (data == null) {
                            return view;
                        }
                        ViewHolder holder = (ViewHolder) view.getTag();
                        if (holder == null) {
                            holder = new ViewHolder();
                        }
                        holder.data = data;
                        //直播封面 cover
                        ImageView thumbnail = (ImageView) view.findViewById(R.id.resource_thumbnail);
                        String coverUrl = data.getCoverUrl();
                        if (!TextUtils.isEmpty(coverUrl) && coverUrl.contains("coverUrl")) {
                            JSONObject jsonObject = JSON.parseObject(coverUrl, Feature.AutoCloseSource);
                            coverUrl = jsonObject.getString("coverUrl");
                        }
                        LQImageLoader.displayImage(AppSettings.getFileUrl(coverUrl), thumbnail, R.drawable.online_list_default);
                        //直播时间 以区间的形式来显示
                        TextView startAndEndTime = (TextView) view.findViewById(R.id.online_time);
                        startAndEndTime.setText(getCurrentOnlineTime(data));
                        //直播的标题
                        TextView onlineTitle = (TextView) view.findViewById(R.id.resource_title);
                        onlineTitle.setText(data.getTitle());
                        //当前直播的状态
                        TextView onlineStatus = (TextView) view.findViewById(R.id.show_online_state);
                        //当前在线的人数
                        TextView onlineCount = (TextView) view.findViewById(R.id.show_online_count);
                        onlineCount.setVisibility(View.VISIBLE);
                        int status = data.getState();
                        if (status == 0) {
                            onlineStatus.setBackgroundResource(R.drawable.live_trailer);
                            onlineStatus.setText(R.string.live_trailer);
                        } else if (status == 1) {
                            onlineStatus.setBackgroundResource(R.drawable.live_living);
                            onlineStatus.setText(R.string.live_living);
                        } else if (status == 2) {
                            onlineStatus.setBackgroundResource(R.drawable.live_review);
                            onlineStatus.setText(R.string.live_review);
                        }

                        int browseCount = data.getBrowseCount();
                        String stringText = null;
                        //如果浏览数大于一万显示 1.x万
                        if (browseCount >= 10000) {
                            double count = div(browseCount, 10000, 1);
                            stringText = getString(R.string.online_count, String.valueOf(count) + "万");
                        } else {
                            stringText = getString(R.string.online_count, String.valueOf(browseCount));
                        }
                        if (data.isEbanshuLive()) {
                            stringText = stringText + " | " + getString(R.string.live_type_blackboard);
                        } else {
                            stringText = stringText + " | " + getString(R.string.live_type_video);
                        }
                        //直播中显示多少人在上课
                        if (status == 1) {
                            stringText = getString(R.string.str_how_many_people_online, data.getOnlineNum())
                                    + " | " + stringText;
                        }
                        onlineCount.setText(stringText);
                        //直播的主持人
                        TextView onlineHost = (TextView) view.findViewById(R.id.show_online_author);
                        StringBuilder builder = new StringBuilder();
                        List<EmceeList> emceeLists = data.getEmceeList();
                        if (emceeLists != null && emceeLists.size() > 0) {
                            for (int i = 0; i < emceeLists.size(); i++) {
                                String hostName = emceeLists.get(i).getRealName();
                                if (i == 0) {
                                    builder.append(hostName);
                                } else {
                                    builder.append(",").append(hostName);
                                }
                            }
                        }
                        onlineHost.setText(builder.toString());
                        //删除当前的按钮
                        ImageView iconDelete = (ImageView) view.findViewById(R.id.resource_delete);
                        if (isHistoryClass || isFinishLecture){
                            //历史班不显示删除按钮
                            iconDelete.setVisibility(View.INVISIBLE);
                        } else {
                            if (isHeadMaster) {
                                iconDelete.setVisibility(View.VISIBLE);
                            } else if (TextUtils.equals(getMemeberId(), data.getAcCreateId())
                                    && isTeacher) {
                                iconDelete.setVisibility(View.VISIBLE);
                            } else {
                                iconDelete.setVisibility(View.INVISIBLE);
                            }
                        }
                        iconDelete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                popDialogDeleteOnlineData(data);
                            }
                        });
                        view.setTag(holder);
                    }
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder.data == null) {
                        return;
                    }
                    //如果当前用户学生判断当前的直播有没有加入我的直播中
                    if (roleType == RoleType.ROLE_TYPE_STUDENT) {
                        analysisCurrentLiveAddMyLive((Emcee) holder.data);
                    } else {
                        enterAirClassroomDetail((Emcee) holder.data);
                    }
                }
            };
            setCurrAdapterViewHelper(listView, adapterViewHelper);
        }
    }

    private void popDialogDeleteOnlineData(Emcee data) {
        List<PublishClass> publishClassList = data.getPublishClassList();
        if (publishClassList == null || publishClassList.size() == 0){
            return;
        }
        if (TextUtils.equals(getMemeberId(),data.getAcCreateId())
                && publishClassList.size() > 1){
            popCanSelectDeleteWayDialog(data);
        } else {
            popDeleteCurrentClassDialog(data);
        }
    }

    private void popCanSelectDeleteWayDialog(final Emcee data){
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(
                getActivity(),
                null,
                R.layout.layout_change_selected_icon,
                getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                },
                getString(R.string.confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (deleteAllClassRB != null && deleteAllClassRB.isChecked()){
                            //删除所有的班级
                            deleteCurrentItem(data, true);
                        } else{
                            //删除当前的班级
                            deleteCurrentItem(data, false);
                        }
                    }
                });
        messageDialog.show();
        deleteAllClassRB = (RadioButton) messageDialog.getContentView().findViewById(R.id.rb_all_Class);

    }

    private void popDeleteCurrentClassDialog(final Emcee data){
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(
                getActivity(),
                null,
                getString(R.string.confirm_delete_online, data.getTitle()),
                getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                },
                getString(R.string.confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        deleteCurrentItem(data, false);
                    }
                });
        messageDialog.show();
    }



    /**
     * 获取当前直播显示的起止时间
     *
     * @param data
     * @return
     */
    private String getCurrentOnlineTime(Emcee data) {
        String beginTime = data.getStartTime();
        String endTime = data.getEndTime();
        if (!TextUtils.isEmpty(beginTime)) {
            beginTime = beginTime.substring(0, beginTime.length() - 3);
        }
        if (!TextUtils.isEmpty(endTime)) {
            endTime = endTime.substring(endTime.length() - 8, endTime.length() - 3);
        }
        String showTime = beginTime + " -- " + endTime;
        if (TextUtils.isEmpty(showTime)) {
            return "";
        }
        return showTime;
    }

    public double div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    private void deleteCurrentItem(final Emcee data, final boolean isCreator) {
        if (data == null) return;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("Id", data.getId());
        if (isCreator) {
            //删除直播
            params.put("ClassId", "");
        } else {
            //删除直播对象
            params.put("ClassId", classId);
        }
        RequestHelper.RequestListener listener =
                new RequestHelper.RequestDataResultListener<DataModelResult>(
                        getActivity(), DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()) {
                            return;
                        } else {
                            String errorMessage = getResult().getErrorMessage();
                            if (TextUtils.isEmpty(errorMessage)) {
                                getCurrAdapterViewHelper().getData().remove(data);
                                getCurrAdapterViewHelper().update();
                                TipMsgHelper.ShowMsg(getActivity(), R.string.cs_delete_success);
                                //同步删除青岛那边的数据
                                if (isCreator) {
                                    deleteQingdaoMateriaItem(data);
                                }
                                if (getCurrAdapterViewHelper().getData() == null
                                        || getCurrAdapterViewHelper().getData().size() == 0){
                                    //如果数据被删除完 更新作业标签数据
                                    loadDateSignData();
                                }
                            } else {
                                TipMsgHelper.ShowLMsg(getActivity(), errorMessage);
                            }

                        }
                    }
                };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.DELETE_AIRCLASS_ONLINE_LIST_NEW_BASE_URL, params, listener);
    }

    /**
     * 删除青岛的数据list接口
     *
     * @param data
     */
    private void deleteQingdaoMateriaItem(Emcee data) {
        String url = ServerUrl.DELETE_AIRCLASSROOM_MATERIA_TO_QINGDAO;
        StringBuilder tempUrl = new StringBuilder(url);
        tempUrl.append("&").append("memberId=" + getMemeberId()).append("&").append("aid=" + data
                .getId()).append("&").append("schoolId=" + data.getSchoolId());
        ThisStringRequest request = new ThisStringRequest(Request.Method.GET, tempUrl.toString(), new
                Listener<String>() {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                    }

                    @Override
                    public void onError(NetroidError error) {
                        if (getActivity() == null) {
                            return;
                        }
                        TipMsgHelper.ShowMsg(getActivity(), getString(R.string.network_error));
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                    }
                });
        request.addHeader("Accept-Encoding", "*");
        request.start(getActivity());
    }

    /**
     * 仅当roleType为学生时
     * 判断当前的直播记录有没有被加入我的直播列表中
     *
     * @param data
     */
    private void analysisCurrentLiveAddMyLive(final Emcee data) {
        Map<String, Object> param = new HashMap<>();
        param.put("SchoolId", schoolId);
        param.put("ClassId", classId);
        param.put("MemberId", getMemeberId());
        param.put("ExtId", data.getId());
        DefaultModelListener listener = new DefaultModelListener<ModelResult>(ModelResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                try {
                    org.json.JSONObject jsonObject = new org.json.JSONObject(jsonString);
                    boolean isAddMyLive = jsonObject.optBoolean("Model");
                    data.setAddMyLived(isAddMyLive);
                    enterAirClassroomDetail(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.JUDGE_LIVE_ADD_MY_LIVE_BASE_URL,
                param, listener);
    }

    private void enterAirClassroomDetail(Emcee data) {
        //将下拉刷新的下标清空一下
        pageHelper.setFetchingPageIndex(0);
        Intent intent = new Intent(getActivity(), AirClassroomDetailActivity.class);
        Bundle bundle = getArguments();
        if (bundle != null) {
            data.setBrowseCount(data.getBrowseCount() + 1);
            bundle.putSerializable(AirClassroomDetailActivity.EMECCBEAN, data);
        }
        intent.putExtras(bundle);
        getActivity().startActivityForResult(intent, ActivityUtils.REQUEST_CODE_RETURN_REFRESH);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contacts_header_left_btn) {
            getActivity().finish();
        } else if (v.getId() == R.id.contacts_header_right_btn) {
            //今
            filterTodayTime();
        } else if (v.getId() == R.id.contacts_header_title) {
            //title的时间选择
            filterTimePickerData();
        } else if (v.getId() == R.id.tv_create_online){
            //创建直播
            createOnlineData();
        } else {
            super.onClick(v);
        }
    }

    private void createOnlineData(){
        Bundle bundle = getArguments();
        if (bundle != null){
            bundle.putString(AirClassroomFragment.Constants.EXTRA_FILTER_START_TIME_BEGIN,
                    changTimeFormat(filterStartTimeBegin,true));
            bundle.putString(AirClassroomFragment.Constants.EXTRA_FILTER_START_TIME_END,
                    changTimeFormat(filterStartTimeBegin,false));
        }
        ActivityUtils.createOnlineData(getActivity(),bundle);
    }

    private void filterTodayTime() {
        if (slSchedule != null) {
            slSchedule.scrollToToday();
        }
    }

    private void filterTimePickerData() {
        DatePickerPopupView popupView = new DatePickerPopupView(
                getActivity(), mSelectYear, mSelectMonth - 1,
                DatePickerPopupView.BlackType.BLACK_TOP,
                new DatePickerPopupView.PopupWindowListener() {
                    @Override
                    public void onOKClick(int year, int month) {
                        slSchedule.scrollToMonth(year, month);
                    }

                    @Override
                    public void onBottomButtonClickListener() {

                    }

                    @Override
                    public void onDismissListener() {

                    }
                });
        popupView.setAnimationStyle(R.style.AnimBottom);
        popupView.showAtLocation(getActivity().getWindow().getDecorView(),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    /**
     * 选择了日期
     *
     * @param year
     * @param month
     * @param day
     */
    @Override
    public void onClickDate(int year, int month, int day) {
        //有没有切换月（相应的改变小绿点）
        boolean isChangeMonth = false;
        mSelectYear = year;
        if (mSelectMonth != month) {
            isChangeMonth = true;
        }
        mSelectMonth = month;
        //有没有切换天数
        if (mSelectDay != day || isChangeMonth){
            selectDayChanged = true;
        } else {
            selectDayChanged = false;
        }
        mSelectDay = day;
        if (timeTitleTextV != null) {
            timeTitleTextV.setText(new StringBuffer().append(year).append(dateTypeArray[0])
                    .append(month + 1).append(dateTypeArray[1]).toString());
        }
        filterStartTimeBegin = getTimeData(year, month, day);
        filterStartTimeEnd = getTimeData(year, month, day);
        loadTimeTableListData();
        if (isChangeMonth) {
            loadDateSignData();
        }
    }

    /**
     * 切换了月/周
     *
     * @param year
     * @param month
     * @param day
     */
    @Override
    public void onPageChange(int year, int month, int day) {
        mSelectYear = year;
        mSelectMonth = month;
        mSelectDay = day;
        if (timeTitleTextV != null) {
            timeTitleTextV.setText(new StringBuffer().append(year).append(dateTypeArray[0])
                    .append(month + 1).append(dateTypeArray[1]).toString());
        }
    }

    private String changTimeFormat(String timeStr, boolean isStartTime) {
        StringBuilder builder = new StringBuilder();
        builder.append(timeStr).append(" ");
        if (isStartTime) {
            builder.append("00:00");
        } else {
            builder.append("23:59");
        }
        return builder.toString();
    }

    private String getTimeData(int year, int month, int day) {
        StringBuilder builder = new StringBuilder();
        builder.append(year).append("-").append(month + 1).append("-").append(day);
        return builder.toString();
    }

    private void loadData() {
        loadDateSignData();
        loadTimeTableListData();
    }

    /**
     * 加载课程日期标记的数据
     */
    private void loadDateSignData() {
        Map<String, Object> param = new HashMap<>();
        param.put("SchoolId", schoolId);
        param.put("ClassId", classId);
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
                        JSONObject jsonObject = JSON.parseObject(jsonString);
                        JSONObject model = (JSONObject) jsonObject.get("Model");
                        JSONArray array = model.getJSONArray("Data");
                        if (array != null){
                            List<String> signData = new ArrayList<>();
                            for (int i = 0;i < array.size();i++){
                               JSONObject object = (JSONObject) array.get(i);
                                if (object != null){
                                    boolean haveLive = object.getBoolean("bolHaveLive");
                                    if (haveLive){
                                        signData.add(object.getString("Date"));
                                    }
                                }
                            }
                            handleSignTimeData(signData,startTimeBegin);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.GET_DATESIGN_FOR_AIRCLASS_BASE_URL,
                param, listener);
    }
    private void handleSignTimeData(List<String> signTimeData,String searchTime){
        List<Integer> tasks = new ArrayList<>();
        if (signTimeData != null && signTimeData.size() > 0){
            for (int i = 0,len = signTimeData.size();i < len;i++){
                String signTime = signTimeData.get(i);
                signTime = DateUtils.getStringToString(signTime,DateUtils.DATE_PATTERN_yyyy_MM_dd);
                tasks.add(getDayOfString(signTime));
            }
        }
        String [] splitArray = searchTime.split("-");
        int year = Integer.valueOf(splitArray[0]);
        int month = Integer.valueOf(splitArray[1]);
        slSchedule.setTaskHints(year,month,Integer.class,tasks);
    }

    private int getDayOfString(String dateString){
        if (!TextUtils.isEmpty(dateString)){
            String [] spiltArray = dateString.split("-");
            return Integer.valueOf(spiltArray[2]);
        }
        return 0;
    }

    /**
     * 加载的当前日期列表数据
     */
    private void loadTimeTableListData() {
        //切换之后参数置0处理
        if (selectDayChanged){
            pageHelper.setFetchingPageIndex(0);
        }
        if (TextUtils.isEmpty(schoolId)) {
            return;
        }
        //每次加载拉取16数据
        pageHelper.setPageSize(16);
        Map<String, Object> params = new HashMap();
        //必填
        params.put("SchoolId", schoolId);
        params.put("Pager", getPageHelper().getFetchingPagerArgs());
        //非必填
        params.put("ClassId", classId);
        //memberId为空 为学生拉取空中课堂的列表
        params.put("MemberId", "");
        params.put("Title", "");
        //来自筛选的界面
        params.put("StartTimeBegin", changTimeFormat(filterStartTimeBegin, true));
        params.put("StartTimeEnd", changTimeFormat(filterStartTimeEnd, false));
        params.put("EndTimeBegin", "");
        params.put("EndTimeEnd", "");
        DefaultPullToRefreshDataListener listener = new DefaultPullToRefreshDataListener<EmceeListResult>(
                EmceeListResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                EmceeListResult result = getResult();
                if (result == null || !result.isSuccess()
                        || result.getModel() == null) {
                    return;
                }
                upDateAirClassList(result);
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
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.GET_AIR_CLASSROOM_LIST_BASE_URL, params, listener);
    }

    private void upDateAirClassList(EmceeListResult result) {
        List<Emcee> onlineList = result.getModel().getData();
        if (onlineList == null || onlineList.size() <= 0) {
            if (selectDayChanged){
                pullToRefreshView.setVisibility(View.GONE);
                defaultHolderBg.setVisibility(View.VISIBLE);
                getCurrAdapterViewHelper().clearData();
                selectDayChanged = false;
            } else {
                if (pageHelper.getFetchingPageIndex() == 0){
                    pullToRefreshView.setVisibility(View.GONE);
                    defaultHolderBg.setVisibility(View.VISIBLE);
                }else {
                    TipMsgHelper.ShowLMsg(getActivity(), getString(R.string.no_more_data));
                }
            }
            return;
        }
        selectDayChanged = false;
        defaultHolderBg.setVisibility(View.GONE);
        pullToRefreshView.setVisibility(View.VISIBLE);
        if (getPageHelper().isFetchingPageIndex(result.getModel().getPager())) {
            if (getPageHelper().isFetchingFirstPage()) {
                getCurrAdapterViewHelper().clearData();
            }
            getPageHelper().updateByPagerArgs(result.getModel().getPager());
            getPageHelper().setCurrPageIndex(getPageHelper().getFetchingPageIndex());
            if (getCurrAdapterViewHelper().hasData()) {
                int position = getCurrAdapterViewHelper().getData().size();
                if (position > 0) {
                    position--;
                }
                getCurrAdapterViewHelper().getData().addAll(onlineList);
                getCurrAdapterView().setSelection(position);
            } else {
                getCurrAdapterViewHelper().setData(onlineList);
            }
        }
    }

    @Override
    public void onDestroy() {
        //WeekCalendarView中的lastSelectWeekNumber静态变量没有重新初始化
        super.onDestroy();
        if (slSchedule != null){
            Calendar calendar = Calendar.getInstance();
            WeekCalendarView.lastSelectWeekNumber
                    = CalendarUtils.getWeek(calendar.get(Calendar.YEAR)
                    , calendar.get(Calendar.MONTH)
                    , calendar.get(Calendar.DAY_OF_MONTH));
        }
    }
}
