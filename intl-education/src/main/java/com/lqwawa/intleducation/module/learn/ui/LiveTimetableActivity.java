package com.lqwawa.intleducation.module.learn.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jeek.calendar.widget.calendar.CalendarUtils;
import com.jeek.calendar.widget.calendar.OnCalendarClickListener;
import com.jeek.calendar.widget.calendar.schedule.ScheduleLayout;
import com.jeek.calendar.widget.calendar.week.WeekCalendarView;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseFragmentActivity;
import com.lqwawa.intleducation.base.utils.DateUtils;
import com.lqwawa.intleducation.base.utils.StringUtils;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.LqResponseDataVo;
import com.lqwawa.intleducation.base.vo.PagerArgs;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.ui.CustomDialog;
import com.lqwawa.intleducation.common.ui.CustomRaidoSelDialog;
import com.lqwawa.intleducation.common.ui.DatePickerPopupView;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.module.learn.adapter.LiveRoomAdapter;
import com.lqwawa.intleducation.module.learn.tool.LiveDetails;
import com.lqwawa.intleducation.module.learn.vo.LiveListVo;
import com.lqwawa.intleducation.module.learn.vo.LiveVo;
import com.lqwawa.intleducation.module.learn.vo.WawaCalendarFlagVo;
import com.lqwawa.intleducation.module.learn.vo.WawaLiveListVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import org.joda.time.DateTime;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 课程表界面
 * Create by MrChen at 201801
 */

public class LiveTimetableActivity extends MyBaseFragmentActivity
        implements OnCalendarClickListener
        , View.OnClickListener, AdapterView.OnItemClickListener {
    /**
     * 定义课程表的来源类型
     */
    public enum LiveSourceType {
        Type_course,        //来自课程详情
        Type_my_join_live,  //来自我参加的直播
        Type_my_host_live,  //来自我主持的直播
        Type_my_child_live, //来自我孩子的直播
        Type_my_course,     //来自我参加的课程下汇总的直播
        Type_my_child_course,//来自我孩子参加的课程下汇总的直播
        Type_air_class      //来自空中课堂
    }

    /**
     * 跳转到课程表界面
     *
     * @param activity
     * @param sourceType    跳转来源
     * @param courseId      课程的Id 仅在type==Type_course时需要传入
     * @param childMemberId 孩子的MemberId 仅在type==Type_my_child_live需要传
     * @param classId       班级Id 仅type==Type_air_class需要传入
     * @param schoolId      学校Id 仅type==Type_air_class需要传入
     */
    public static void start(Activity activity,
                             LiveSourceType sourceType,
                             String courseId,
                             String childMemberId,
                             String classId,
                             String schoolId) {
        Intent intent = new Intent(activity, LiveTimetableActivity.class);
        intent.putExtra("liveSourceType", sourceType);
        if (sourceType == LiveSourceType.Type_course) {
            intent.putExtra("courseId", courseId);
            // @date   :2018/4/26 0026 下午 6:09
            // @func   :V5.5发现前人留下的坑,具坑，铁坑
            //} else if (sourceType == LiveSourceType.Type_my_child_live) {
        } else if (sourceType == LiveSourceType.Type_my_child_live || sourceType == LiveSourceType.Type_my_child_course) {
            intent.putExtra("childMemberId", childMemberId);
        } else if (sourceType == LiveSourceType.Type_air_class) {
            intent.putExtra("classId", classId);
            intent.putExtra("schoolId", schoolId);
        }
        activity.startActivity(intent);
    }

    private TopBar topBar;
    private TextView textViewTopDate;
    private RelativeLayout loadFailedLayout;
    private Button btnReload;

    private PullToRefreshView pullToRefreshView;

    private ScheduleLayout slSchedule;// 日历控件

    private RelativeLayout rlNoTask;

    //数据列表
    private ListView listView;
    private LiveRoomAdapter mLiveRoomAdapter;
    private String[] dateTypeArray = new String[]{"年", "月", "日"};
    private int mSelectYear, mSelectMonth, mSelectDay;

    private int pageIndex = 0;

    private LiveSourceType liveSourceType;
    private String courseId;
    private String childMemberId;
    private String classId;
    private String schoolId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_timetable);
        getParams();
        initViews();
        DateTime startDate = new DateTime(mSelectYear, mSelectMonth + 1, 1, 0, 0);
        DateTime endDate = new DateTime(mSelectYear, mSelectMonth + 1,
                CalendarUtils.getMonthDays(mSelectYear,
                        mSelectMonth), 0, 0);
        getTimetableFlags(startDate, endDate);
        pullToRefreshView.showRefresh();
        getData();
    }

    /**
     * 获取传参
     */
    private void getParams(){
        liveSourceType = (LiveSourceType) getIntent().getSerializableExtra("liveSourceType");
        courseId = getIntent().getStringExtra("courseId");
        childMemberId = getIntent().getStringExtra("childMemberId");
        classId = getIntent().getStringExtra("classId");
        schoolId = getIntent().getStringExtra("schoolId");

        // @date   :2018/4/26 0026 下午 6:23
        // @func   :这里平板和手机上的业务还不一样 判断不一样 暂时不添加
        // curMemberId = (liveSourceType == LiveSourceType.Type_my_child_live?UserHelper.getUserId():childMemberId);
        /*curMemberId = (liveSourceType == LiveSourceType.Type_my_child_live
                || liveSourceType == LiveSourceType.Type_my_child_course ?
                childMemberId:UserHelper.getUserId());*/
    }

    /**
     * 初始化所有视图
     */
    private void initViews(){
        initTopBar();
        initCalendar();
        initListView();
    }

    /**
     * 初始化TopBar
     */
    private void initTopBar(){
        topBar = (TopBar) findViewById(R.id.top_bar);
        topBar.setBack(true);
        topBar.showBottomSplitView(true);
        topBar.setRightFunctionText1(R.string.today_simple,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (slSchedule != null) {
                            slSchedule.scrollToToday();
                        }
                    }
                });
        topBar.setRightFunctionText1TextColor(getResources().getColor(R.color.com_text_green));

        textViewTopDate = new TextView(activity);
        textViewTopDate.setTextColor(activity.getResources().getColor(R.color.com_text_black));
        textViewTopDate.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                activity.getResources().getDimensionPixelSize(R.dimen.com_font_size_7));
        Drawable dra = getResources().getDrawable(R.drawable.arrow_down_gray_ico);
        dra.setBounds(0, 0, dra.getMinimumWidth(), dra.getMinimumHeight());
        textViewTopDate.setCompoundDrawables(null, null, dra, null);
        textViewTopDate.setCompoundDrawablePadding(10);

        TextPaint tp = textViewTopDate.getPaint();
        tp.setFakeBoldText(true);
        topBar.setTitleContentView(textViewTopDate);
        textViewTopDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerPopupView popupView = new DatePickerPopupView(
                        activity, mSelectYear, mSelectMonth - 1,
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
                popupView.showAtLocation(activity.getWindow().getDecorView(),
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            }
        });
    }

    /**
     * 初始化日历控件
     */
    private void initCalendar(){
        Calendar calendar = Calendar.getInstance();
        mSelectYear = calendar.get(Calendar.YEAR);
        mSelectMonth = calendar.get(Calendar.MONTH);
        mSelectDay = calendar.get(Calendar.DAY_OF_MONTH);
        if (textViewTopDate != null) {
            textViewTopDate.setText(new StringBuffer().append(mSelectYear).append(dateTypeArray[0])
                    .append(mSelectMonth + 1).append(dateTypeArray[1]).toString());
        }
    }

    /**
     * 初始化列表控件
     */
    private void initListView(){
        btnReload = (Button) findViewById(R.id.reload_bt);
        btnReload.setOnClickListener(this);
        rlNoTask = (RelativeLayout) findViewById(R.id.rlNoTask);
        rlNoTask.setOnClickListener(this);
        listView = (ListView) findViewById(R.id.listView);
        pullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        loadFailedLayout = (RelativeLayout) findViewById(R.id.load_failed_layout);
        pullToRefreshView.setLoadMoreEnable(false);
        pullToRefreshView.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                getData();
            }
        });
        pullToRefreshView.setOnFooterRefreshListener(new PullToRefreshView.OnFooterRefreshListener() {
            @Override
            public void onFooterRefresh(PullToRefreshView view) {
                getMore();
            }
        });
        mLiveRoomAdapter = new LiveRoomAdapter(activity, true);
        listView.setAdapter(mLiveRoomAdapter);
        listView.setOnItemClickListener(this);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                if ((liveSourceType == LiveSourceType.Type_my_join_live || liveSourceType == LiveSourceType.Type_my_host_live)) {
                    final LiveVo vo = (LiveVo) mLiveRoomAdapter.getItem(position);
                    if (liveSourceType == LiveSourceType.Type_my_host_live //我主持成直播如果是来自慕课的直播而且我是小编是不可以删除的
                            && !TextUtils.equals(UserHelper.getUserId(), vo.getAcCreateId())) {
                        return false;
                    }

                    if(liveSourceType == LiveSourceType.Type_my_host_live && !vo.isFromMooc()//我主持的直播 我是创建者
                            && TextUtils.equals(UserHelper.getUserId(), vo.getAcCreateId())){
                        if(vo.getPublishClassVoList() != null //该直播在多个班级发布
                                && vo.getPublishClassVoList().size() > 1){
                            CustomRaidoSelDialog.Builder builder = new CustomRaidoSelDialog.Builder(activity);
                            String tipMsg = UIUtil.getString(R.string.delete_online_list_data_title);
                            builder.setMessage(tipMsg);
                            builder.setTitle(UIUtil.getString(R.string.tip));
                            builder.setPositiveButton(UIUtil.getString(R.string.confirm),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            exitLive(vo, which == 1);
                                        }
                                    });

                            builder.setNegativeButton(UIUtil.getString(R.string.cancel),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });

                            builder.create().show();
                            return true;
                        }
                    }

                    CustomDialog.Builder builder = new CustomDialog.Builder(activity);
                    String tipMsg = UIUtil.getString(R.string.exit_live_tip) + "?";
                    if (liveSourceType == LiveSourceType.Type_my_host_live) {
                        if (vo.getState() == 1) {
                            tipMsg = UIUtil.getString(R.string.delete_live_tip2) + "?";
                        } else {
                            tipMsg = UIUtil.getString(R.string.delete_live_tip1) + "?";
                        }
                    }
                    builder.setMessage(tipMsg);
                    builder.setTitle(UIUtil.getString(R.string.tip));
                    builder.setPositiveButton(UIUtil.getString(R.string.confirm),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    exitLive(vo, false);
                                }
                            });

                    builder.setNegativeButton(UIUtil.getString(R.string.cancel),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                    builder.create().show();
                    return true;
                } else {
                    return false;
                }
            }
        });

        slSchedule = (ScheduleLayout) findViewById(R.id.slSchedule);
        slSchedule.setOnCalendarClickListener(this);
        pullToRefreshView.setLastUpdated(new Date().toLocaleString());
    }

    private void exitLive(LiveVo vo, boolean deleteAllPublic) {
        if (vo.isFromMooc()) {
            RequestVo requestVo = new RequestVo();
            requestVo.addParams("liveId", vo.getId());
            if(liveSourceType == LiveSourceType.Type_my_host_live)
            requestVo.addParams("type", 1);
            if(liveSourceType == LiveSourceType.Type_my_join_live)
            requestVo.addParams("type", 0);
            RequestParams params =
                    new RequestParams(AppConfig.ServerUrl.DeleteLive + requestVo.getParams());
            params.setConnectTimeout(10000);
            x.http().get(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String s) {
                    ResponseVo<String> result = JSON.parseObject(s,
                            new TypeReference<ResponseVo<String>>() {
                            });
                    if (result.getCode() == 0) {
                        ToastUtil.showToast(activity,
                                UIUtil.getString(liveSourceType == LiveSourceType.Type_my_join_live ? R.string.exit_live : R.string.delete_live)
                                        + UIUtil.getString(R.string.success));
                        pullToRefreshView.showRefresh();
                        getData();
                    } else {
                        ToastUtil.showToast(activity,
                                UIUtil.getString(liveSourceType == LiveSourceType.Type_my_join_live ? R.string.exit_live : R.string.delete_live)
                                        + UIUtil.getString(R.string.failed)
                                        + ":" + result.getMessage());
                    }
                }

                @Override
                public void onCancelled(CancelledException e) {
                }

                @Override
                public void onError(Throwable throwable, boolean b) {
                    ToastUtil.showToast(activity, UIUtil.getString(R.string.net_error_tip));
                }

                @Override
                public void onFinished() {
                }
            });
        }else { //这是来自空中课堂的直播 调用两栖蛙蛙的接口
            RequestVo requestVo = new RequestVo();
            requestVo.addParams("MemberId", UserHelper.getUserId());
            if(liveSourceType == LiveSourceType.Type_my_join_live) {
                requestVo.addParams("Id", vo.getAirLiveId());
            }else{
                requestVo.addParams("Id", vo.getId());
                if(!deleteAllPublic){
                    requestVo.addParams("ClassId", vo.getClassId());
                }
            }
            RequestParams params =
                    new RequestParams(liveSourceType == LiveSourceType.Type_my_join_live ? AppConfig.ServerUrl.WAWA_DeleteMyLive
                            : AppConfig.ServerUrl.WAWA_DeleteAirClassNew);
            params.setConnectTimeout(10000);
            params.setAsJsonContent(true);
            params.setBodyContent(requestVo.getParamsWithoutToken());
            x.http().post(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String s) {
                    LqResponseDataVo<String> result = JSON.parseObject(s,
                            new TypeReference<LqResponseDataVo<String>>() {
                            });
                    if (result.getErrorCode() == 0) {
                        ToastUtil.showToast(activity,
                                UIUtil.getString(liveSourceType == LiveSourceType.Type_my_join_live ? R.string.exit_live : R.string.delete_live)
                                        + UIUtil.getString(R.string.success));
                        pullToRefreshView.showRefresh();
                        getData();
                    } else {
                        ToastUtil.showToast(activity,
                                UIUtil.getString(liveSourceType == LiveSourceType.Type_my_join_live ? R.string.exit_live : R.string.delete_live)
                                        + UIUtil.getString(R.string.failed)
                                        + ":" + result.getErrorMessage());
                    }
                }

                @Override
                public void onCancelled(CancelledException e) {
                }

                @Override
                public void onError(Throwable throwable, boolean b) {
                    ToastUtil.showToast(activity, UIUtil.getString(R.string.net_error_tip));
                }

                @Override
                public void onFinished() {
                }
            });
        }
    }

    /**
     * 获取第一页数据
     */
    public void getData() {
        pageIndex = 0;
        if (liveSourceType == LiveSourceType.Type_course
                || liveSourceType == LiveSourceType.Type_my_course
                || liveSourceType == LiveSourceType.Type_my_child_course) {
            getCourseLiveData();
        }else if(liveSourceType == LiveSourceType.Type_my_child_live
                || liveSourceType ==LiveSourceType.Type_my_join_live
                || liveSourceType == LiveSourceType.Type_my_host_live
                || liveSourceType == LiveSourceType.Type_air_class){
            getMyLiveData();
        }

    }

    /**
     * 获取课程-课程表的第一页数据
     */
    public void getCourseLiveData(){
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("pageIndex", pageIndex);
        requestVo.addParams("pageSize", AppConfig.PAGE_SIZE);
        if (liveSourceType == LiveSourceType.Type_course) {
            requestVo.addParams("courseId", courseId);
        }else if(liveSourceType == LiveSourceType.Type_my_course
                || liveSourceType == LiveSourceType.Type_my_child_course){
            requestVo.addParams("memberId", liveSourceType ==
                    LiveSourceType.Type_my_child_course
                    ? childMemberId : UserHelper.getUserId());
        }
        String selDateText = new StringBuffer().append(mSelectYear).append("-").
                append(mSelectMonth + 1).append("-").append(mSelectDay).toString();
        requestVo.addParams("startTime", selDateText);
        requestVo.addParams("endTime", selDateText);

        RequestParams params =
                new RequestParams((liveSourceType == LiveSourceType.Type_course ?
                        AppConfig.ServerUrl.GetLiveList
                        : AppConfig.ServerUrl.getMyCourseLiveList) + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefreshView.onHeaderRefreshComplete();
                loadFailedLayout.setVisibility(View.GONE);
                LiveListVo result = JSON.parseObject(s,
                        new TypeReference<LiveListVo>() {
                        });
                if (result.getCode() == 0) {
                    List<LiveVo> list = result.getData();
                    if (list != null && list.size() > 0) {
                        rlNoTask.setVisibility(View.GONE);
                        pullToRefreshView.setLoadMoreEnable(list != null &&
                                list.size() < result.getTotal());
                    } else {
                        rlNoTask.setVisibility(View.VISIBLE);
                    }
                    mLiveRoomAdapter.setData(list);
                    mLiveRoomAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
                pullToRefreshView.onHeaderRefreshComplete();
                pullToRefreshView.onFooterRefreshComplete();
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                pullToRefreshView.onHeaderRefreshComplete();
                loadFailedLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinished() {
            }
        });
    }

    /**
     * 获取我的直播-课程表的第一页数据
     */
    private void getMyLiveData(){
        RequestVo requestVo = new RequestVo();
        if(liveSourceType == LiveSourceType.Type_my_child_live) {
            requestVo.addParams("MemberId", childMemberId);
        }else{
            requestVo.addParams("MemberId", UserHelper.getUserId());
        }
        requestVo.addParams("Flag", liveSourceType
                == LiveSourceType.Type_my_host_live ? 1 : 0);
        String startTimeText = new StringBuffer().append(mSelectYear).append("-")
                .append(mSelectMonth + 1).append("-")
                .append(mSelectDay).append(" 00:00:00").toString();
        String endDateText = new StringBuffer().append(mSelectYear).append("-")
                .append(mSelectMonth + 1).append("-")
                .append(mSelectDay).append(" 23:59:59").toString();
        requestVo.addParams("StartTimeBegin", startTimeText );
        requestVo.addParams("StartTimeEnd", endDateText);
        requestVo.addParams("Pager", new PagerArgs(pageIndex, AppConfig.PAGE_SIZE), true);
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.WAWA_GetMyLiveList);
        params.setConnectTimeout(10000);
        params.setAsJsonContent(true);
        params.setBodyContent(requestVo.getParamsWithoutToken());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefreshView.onHeaderRefreshComplete();
                loadFailedLayout.setVisibility(View.GONE);
                LqResponseDataVo<List<WawaLiveListVo>> result = JSON.parseObject(s,
                        new TypeReference<LqResponseDataVo<List<WawaLiveListVo>>>() {
                        });
                if (result.getErrorCode() == 0) {
                    List<WawaLiveListVo> wawaLiveList = result.getModel().getData();
                    List<LiveVo> list = new ArrayList<>();
                    if (wawaLiveList != null && wawaLiveList.size() > 0) {
                        list = WawaLiveListVo.wawaLiveListToLiveList(wawaLiveList);
                    }
                    pullToRefreshView.setLoadMoreEnable(list != null &&
                            list.size() < result.getModel().getPager().getRowsCount());
                    if (list != null && list.size() > 0) {
                        rlNoTask.setVisibility(View.GONE);
                    } else {
                        rlNoTask.setVisibility(View.VISIBLE);
                    }
                    mLiveRoomAdapter.setData(list);
                    mLiveRoomAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
            }

            @Override
            public void onFinished() {
            }
        });
    }

    /**
     * 上拉加载更多数据
     */
    public void getMore() {
        if(liveSourceType == LiveSourceType.Type_course
                || liveSourceType == LiveSourceType.Type_my_course
                || liveSourceType == LiveSourceType.Type_my_child_course){
            getCourseLiveMore();
        }else if(liveSourceType == LiveSourceType.Type_my_child_live
                || liveSourceType ==LiveSourceType.Type_my_join_live
                || liveSourceType == LiveSourceType.Type_my_host_live){
            getMyLiveMore();
        }
    }

    /**
     * 加载课程-课程表的更多数据
     */
    private void getCourseLiveMore(){
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("pageIndex", pageIndex + 1);
        requestVo.addParams("pageSize", AppConfig.PAGE_SIZE);
        if (liveSourceType == LiveSourceType.Type_course) {
            requestVo.addParams("courseId", courseId);
        }else if(liveSourceType == LiveSourceType.Type_my_course
                || liveSourceType == LiveSourceType.Type_my_child_course){
            requestVo.addParams("memberId", liveSourceType ==
                    LiveSourceType.Type_my_child_course
                    ? childMemberId : UserHelper.getUserId());
        }
        String selDateText = new StringBuffer().append(mSelectYear).append("-").
                append(mSelectMonth + 1).append("-").append(mSelectDay).toString();
        requestVo.addParams("startTime", selDateText);
        requestVo.addParams("endTime", selDateText);

        RequestParams params =
                new RequestParams((liveSourceType == LiveSourceType.Type_course ?
                        AppConfig.ServerUrl.GetLiveList
                        : AppConfig.ServerUrl.getMyCourseLiveList) + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefreshView.onFooterRefreshComplete();
                LiveListVo result = JSON.parseObject(s,
                        new TypeReference<LiveListVo>() {
                        });
                if (result.getCode() == 0) {
                    List<LiveVo> listMore = result.getData();
                    pageIndex++;
                    pullToRefreshView.setLoadMoreEnable(
                            result.getTotal() > (listMore.size() + mLiveRoomAdapter.getCount()));
                    mLiveRoomAdapter.addData(listMore);
                    mLiveRoomAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                pullToRefreshView.onFooterRefreshComplete();
            }

            @Override
            public void onFinished() {
            }
        });
    }

    /**
     * 加载我的直播-课程表的更多数据
     */
    private void getMyLiveMore(){
        RequestVo requestVo = new RequestVo();
        if(liveSourceType == LiveSourceType.Type_my_child_live) {
            requestVo.addParams("MemberId", childMemberId);
        }else{
            requestVo.addParams("MemberId", UserHelper.getUserId());
        }
        requestVo.addParams("Flag", liveSourceType
                == LiveSourceType.Type_my_host_live ? 1 : 0);
        String startTimeText = new StringBuffer().append(mSelectYear).append("-")
                .append(mSelectMonth + 1).append("-")
                .append(mSelectDay).append(" 00:00:00").toString();
        String endDateText = new StringBuffer().append(mSelectYear).append("-")
                .append(mSelectMonth + 1).append("-")
                .append(mSelectDay).append(" 23:59:59").toString();
        requestVo.addParams("StartTimeBegin", startTimeText );
        requestVo.addParams("StartTimeEnd", endDateText);
        requestVo.addParams("Pager", new PagerArgs(pageIndex + 1, AppConfig.PAGE_SIZE), true);
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.WAWA_GetMyLiveList);
        params.setConnectTimeout(10000);
        params.setAsJsonContent(true);
        params.setBodyContent(requestVo.getParamsWithoutToken());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefreshView.onFooterRefreshComplete();
                LqResponseDataVo<List<WawaLiveListVo>> result = JSON.parseObject(s,
                        new TypeReference<LqResponseDataVo<List<WawaLiveListVo>>>() {
                        });
                if (result.getErrorCode() == 0) {
                    List<WawaLiveListVo> wawaLiveList = result.getModel().getData();
                    List<LiveVo> list = new ArrayList<>();
                    pageIndex++;
                    if (wawaLiveList != null && wawaLiveList.size() > 0) {
                        list = WawaLiveListVo.wawaLiveListToLiveList(wawaLiveList);
                    }
                    pullToRefreshView.setLoadMoreEnable(list != null &&
                            mLiveRoomAdapter.getCount() < result.getModel().getPager().getRowsCount());
                    if (list != null && list.size() > 0) {
                        mLiveRoomAdapter.addData(list);
                        mLiveRoomAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                pullToRefreshView.onFooterRefreshComplete();
            }

            @Override
            public void onFinished() {
            }
        });
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
        boolean isMonthChange = false;

        mSelectYear = year;
        if (mSelectMonth != month) {
            isMonthChange = true;
        }
        mSelectMonth = month;
        mSelectDay = day;
        pullToRefreshView.setLastUpdated(new Date().toLocaleString());
        if (textViewTopDate != null) {
            textViewTopDate.setText(new StringBuffer().append(year).append(dateTypeArray[0])
                    .append(month + 1).append(dateTypeArray[1]).toString());
        }
        getData();
        if (isMonthChange) {
            DateTime startDate = new DateTime(getFirstDayOfMonth(year, month));
            DateTime endDate = new DateTime(getLastDayOfMonth(year, month));
            getTimetableFlags(startDate, endDate);
        }
    }

    private long getFirstDayOfMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);
        return calendar.getTime().getTime();
    }

    private long getLastDayOfMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        return calendar.getTime().getTime();
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
        if (textViewTopDate != null) {
            textViewTopDate.setText(new StringBuffer().append(year).append(dateTypeArray[0])
                    .append(month + 1).append(dateTypeArray[1]).toString());
        }
//        DateTime startDate = slSchedule.getStartDate();
//        DateTime endDate = slSchedule.getEndDate();
//        getTimetableFlags(startDate, endDate);
    }

    public void getTimetableFlags(final DateTime startDate, final DateTime endDate) {
        if(liveSourceType == LiveSourceType.Type_course){
            getCourseTimetableFlags(startDate, endDate, courseId);
        } else if(liveSourceType == LiveSourceType.Type_my_child_live
                ||liveSourceType ==LiveSourceType.Type_my_join_live
                ||liveSourceType == LiveSourceType.Type_my_host_live){
            getMyLiveTimetableFlags(startDate, endDate);
        }else if(liveSourceType == LiveSourceType.Type_my_course
                || liveSourceType == LiveSourceType.Type_my_child_course){
            getCourseTimetableFlags(startDate, endDate, "");
        }
    }

    public void getCourseTimetableFlags(
            final DateTime startDate,
            final DateTime endDate,
            final String courseId) {
        String startDateText = startDate.toString(DateUtils.YYYYMMDD);
        String endDateText = endDate.toString(DateUtils.YYYYMMDD);
        RequestVo requestVo = new RequestVo();
        if(liveSourceType == LiveSourceType.Type_course) {
            requestVo.addParams("courseId", courseId);
        }else if(liveSourceType == LiveSourceType.Type_my_course
                || liveSourceType == LiveSourceType.Type_my_child_course){
            requestVo.addParams("memberId", liveSourceType ==
                    LiveSourceType.Type_my_child_course
                    ? childMemberId : UserHelper.getUserId());
        }
        requestVo.addParams("startTime", startDateText);
        requestVo.addParams("endTime", endDateText);
        RequestParams params =
                new RequestParams((liveSourceType == LiveSourceType.Type_course ?
                        AppConfig.ServerUrl.GetDateSignForCourseLive
                        : AppConfig.ServerUrl.getDateSignByMemberId) + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                ResponseVo<List<String>> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<List<String>>>() {
                        });
                if(result.getCode() == 0) {
                    List<String> tasks = result.getData();
                    slSchedule.setTaskHints(startDate.getYear(),
                            startDate.getMonthOfYear(),
                            String.class, tasks);
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
            }

            @Override
            public void onFinished() {
            }
        });
    }

    public void getMyLiveTimetableFlags(final DateTime startDate, final DateTime endDate){
        String startDateText = startDate.toString(DateUtils.YYYYMMDD);
        String endDateText = endDate.toString(DateUtils.YYYYMMDD);
        RequestVo requestVo = new RequestVo();
        if(liveSourceType == LiveSourceType.Type_my_child_live) {
            requestVo.addParams("MemberId", childMemberId);
        }else{
            requestVo.addParams("MemberId", UserHelper.getUserId());
        }
        requestVo.addParams("Flag", liveSourceType
                == LiveSourceType.Type_my_host_live ? 1 : 0);
        requestVo.addParams("StartTimeBegin", startDateText);
        requestVo.addParams("StartTimeEnd", endDateText);
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetDateSignForMyLive);
        params.setConnectTimeout(10000);
        params.setAsJsonContent(true);
        params.setBodyContent(requestVo.getParamsWithoutToken());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                LqResponseDataVo<List<WawaCalendarFlagVo>> result = JSON.parseObject(s,
                        new TypeReference<LqResponseDataVo<List<WawaCalendarFlagVo>>>() {
                        });
                List<String> flags = new ArrayList<>();
                if(!result.isHasError()){
                    List<WawaCalendarFlagVo> flagList = result.getModel().getData();
                    if(flagList != null && flagList.size() > 0){
                        for(int i = 0; i < flagList.size(); i++){
                            flags.add(flagList.get(i).getDate());
                        }
                    }
                }
                slSchedule.setTaskHints(startDate.getYear(),
                        startDate.getMonthOfYear(),
                        String.class, flags);
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
            }

            @Override
            public void onFinished() {
            }
        });
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.reload_bt
                || viewId == R.id.rlNoTask) {
            getData();
        }
    }

    /**
     * @param adapterView
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        LiveVo vo = (LiveVo) mLiveRoomAdapter.getItem(position);
        if (vo == null) {
            return;
        }

        if(vo.isIsDelete()){
            ToastUtil.showToast(activity, activity.getResources().getString(R.string.live_is_invalid));
            return;
        }

        if (liveSourceType == LiveSourceType.Type_course) {//课程下的直播
            LiveDetails.jumpToLiveDetailsFromCourse(activity, vo, UserHelper.getUserId(), false);
        } else if (liveSourceType == LiveSourceType.Type_my_join_live//我加入的直播
                || liveSourceType == LiveSourceType.Type_my_course) {//我的学程下的直播
            if(vo.isFromMooc()){
                LiveDetails.jumpToLiveDetails(activity, vo, false, true, true);
            }else{
                LiveDetails.jumpToAirClassroomLiveDetails(activity, vo, UserHelper.getUserId());
            }
        } else if (liveSourceType == LiveSourceType.Type_my_host_live) {//我主持的直播
            if(vo.isFromMooc()){
                LiveDetails.jumpToLiveDetails(activity, vo, true, true, true);
            }else{
                LiveDetails.jumpToAirClassroomLiveDetails(activity, vo, UserHelper.getUserId());
            }
        } else if (liveSourceType == LiveSourceType.Type_my_child_live//我孩子加入的直播
                || liveSourceType == LiveSourceType.Type_my_child_course) {//我孩子加入的课程下的直播
            if(vo.isFromMooc()){
                LiveDetails.jumpToLiveDetails(activity, childMemberId, vo, true, true);
            }else{
                LiveDetails.jumpToAirClassroomLiveDetails(activity, vo, childMemberId);
            }
        }
    }

    @Override
    protected void onDestroy() {
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
