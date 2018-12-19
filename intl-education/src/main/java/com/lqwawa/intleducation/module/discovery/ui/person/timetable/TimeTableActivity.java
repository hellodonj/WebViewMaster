package com.lqwawa.intleducation.module.discovery.ui.person.timetable;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.jeek.calendar.widget.calendar.CalendarUtils;
import com.jeek.calendar.widget.calendar.OnCalendarClickListener;
import com.jeek.calendar.widget.calendar.schedule.ScheduleLayout;
import com.jeek.calendar.widget.calendar.week.WeekCalendarView;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.IBaseActivity;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.ToolbarActivity;
import com.lqwawa.intleducation.base.utils.DateUtils;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.ui.ContactsMessageDialog;
import com.lqwawa.intleducation.common.ui.DatePickerPopupView;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.LiveEntity;
import com.lqwawa.intleducation.factory.helper.AirClassHelper;
import com.lqwawa.intleducation.factory.role.LQwawaRoleType;
import com.lqwawa.intleducation.module.learn.adapter.LiveRoomAdapter;
import com.lqwawa.intleducation.module.learn.tool.LiveDetails;
import com.lqwawa.intleducation.module.learn.vo.LiveVo;
import com.lqwawa.intleducation.module.learn.vo.WawaLiveListVo;
import com.lqwawa.intleducation.module.onclass.OnlineClassRole;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 我的课程表页面
 */
public class TimeTableActivity extends PresenterActivity<TimeTableContract.Presenter>
    implements TimeTableContract.View , View.OnClickListener {

    private TopBar mTopBar;
    private FrameLayout mDatetimeBar;
    private TextView mTvDateTime,mTvToDay;

    // 日历控件
    private ScheduleLayout mScheduleLayout;
    private PullToRefreshView mRefreshLayout;
    private LiveRoomAdapter mLiveRoomAdapter;
    private ListView mListView;

    private TimeTableParams mTimeTableParams;
    private String[] dateTypeArray = new String[]{"年", "月", "日"};
    private int mSelectYear, mSelectMonth, mSelectDay;

    // 多个发布对象时候,选择的选项
    private RadioButton deleteAllClassRB;

    // 返回的接口数据
    private List<String> mClassIds;
    private int pageIndex;


    private String mCurMemberId;
    private int mRole;

    @Override
    protected TimeTableContract.Presenter initPresenter() {
        return new TimeTablePresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_timetable;
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        if(bundle.containsKey(ACTIVITY_BUNDLE_OBJECT)){
            mTimeTableParams = (TimeTableParams) bundle.getSerializable(ACTIVITY_BUNDLE_OBJECT);
            if(EmptyUtil.isNotEmpty(mTimeTableParams)){
                mCurMemberId = mTimeTableParams.getCurMemberId();
                mRole = mTimeTableParams.getRole();
            }
        }

        if(EmptyUtil.isEmpty(mCurMemberId)) return false;
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        mTopBar.setBack(true);
        mTopBar.setTitle(getString(R.string.title_my_timetable));

        mScheduleLayout = (ScheduleLayout) findViewById(R.id.schedule_layout);
        mRefreshLayout = (PullToRefreshView) findViewById(R.id.refresh_layout);
        mListView = (ListView) findViewById(R.id.listView);
        mLiveRoomAdapter = new LiveRoomAdapter(this, true);
        // 课程表,显示来源
        mLiveRoomAdapter.setShowSource(true);
        mListView.setAdapter(mLiveRoomAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LiveVo vo = (LiveVo) mLiveRoomAdapter.getItem(position);
                if (EmptyUtil.isEmpty(vo)) {
                    return;
                }

                if(vo.isIsDelete()){
                    UIUtil.showToastSafe(R.string.live_is_invalid);
                    return;
                }

                boolean isHeadMaster = false;
                // 可能是完成授课
                boolean isGiveFinish = false;
                // 肯定不是历史班级
                boolean isGiveHistory = false;
                // 是否作历史班处理
                boolean isFinishResult = isGiveFinish;
                String curMemberId = UserHelper.getUserId();
                String liveRole = String.valueOf(mRole);
                boolean isFromMyLive = false;
                boolean isMyCourseChildOnline = false;
                if(!TextUtils.equals(UserHelper.getUserId(),mCurMemberId)){
                    liveRole = OnlineClassRole.ROLE_PARENT;
                    curMemberId = mCurMemberId;
                    isMyCourseChildOnline = true;
                    isFromMyLive = true;
                    isHeadMaster = false;

                    if(EmptyUtil.isEmpty(curMemberId)){
                        return;
                    }
                }

                String classId = vo.getClassId();
                // 我的直播都是已经加入的
                LiveDetails.jumpToAirClassroomLiveDetails(TimeTableActivity.this, vo,
                        curMemberId, liveRole, true,
                        isHeadMaster, isFromMyLive, true,
                        classId,isMyCourseChildOnline,isFinishResult,isGiveHistory,true);

            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                LiveVo vo = (LiveVo) mLiveRoomAdapter.getItem(position);
                if(TextUtils.equals(mCurMemberId,vo.getAcCreateId())){
                    mPresenter.requestCheckFinishWithClassId(vo);
                    return true;
                }
                return false;
            }
        });

        mRefreshLayout.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                loadData(false);
            }
        });

        mRefreshLayout.setOnFooterRefreshListener(new PullToRefreshView.OnFooterRefreshListener() {
            @Override
            public void onFooterRefresh(PullToRefreshView view) {
                loadData(true);
            }
        });

        mScheduleLayout.setOnCalendarClickListener(new OnCalendarClickListener() {
            @Override
            public void onClickDate(int year, int month, int day) {
                boolean isMonthChange = false;

                mSelectYear = year;
                if (mSelectMonth != month) {
                    isMonthChange = true;
                }
                mSelectMonth = month;
                mSelectDay = day;

                // 更新时间显示
                updateCalendarBar(year,month,day);

                if (isMonthChange) {
                    DateTime startDate = new DateTime(getFirstDayOfMonth(year, month));
                    DateTime endDate = new DateTime(getLastDayOfMonth(year, month));
                    getTimetableFlags(startDate, endDate);
                }

                loadData(false);
            }

            @Override
            public void onPageChange(int year, int month, int day) {
                mSelectYear = year;
                mSelectMonth = month;
                mSelectDay = day;
                updateCalendarBar(year,month,day);
            }
        });

        initDatetimeBar();
        initCalendarBar();
    }

    /**
     * 有多个发布对象，调用
     * @param data 直播实体
     */
    private void popCanSelectDeleteWayDialog(final LiveVo data){
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(this,
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

    /**
     * 删除直播 只有一个发布对象时候调用
     * @param data 只有一个直播实体
     */
    private void popDeleteCurrentClassDialog(final LiveVo data){
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(this,
                null,
                getString(R.string.label_confirm_delete_online, data.getTitle()),
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
     * 删除直播 直播所在班级结束或者历史提醒
     */
    private void popDeleteWarning(){
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(this,
                null,
                getString(R.string.label_delete_live_status_warning),
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
                    }
                });
        messageDialog.show();
    }

    /**
     * 初始化年月选择器
     */
    private void initDatetimeBar(){
        mDatetimeBar = (FrameLayout) findViewById(R.id.datetime_bar);
        mTvDateTime = (TextView) findViewById(R.id.tv_datetime);
        mTvToDay = (TextView) findViewById(R.id.tv_today);

        mTvDateTime.setOnClickListener(this);
        mTvToDay.setOnClickListener(this);
    }

    /**
     * 初始化日历控件
     */
    private void initCalendarBar(){
        Calendar calendar = Calendar.getInstance();
        mSelectYear = calendar.get(Calendar.YEAR);
        mSelectMonth = calendar.get(Calendar.MONTH);
        mSelectDay = calendar.get(Calendar.DAY_OF_MONTH);
        updateCalendarBar(mSelectYear,mSelectMonth,mSelectDay);
    }

    /**
     * 更新当前年月显示
     */
    private void updateCalendarBar(int year,int month,int day){
        if (EmptyUtil.isNotEmpty(mTvDateTime)) {
            String dateStr = new StringBuffer()
                    .append(year)
                    .append(dateTypeArray[0])
                    .append(month + 1)
                    .append(dateTypeArray[1])
                    .toString();
            mTvDateTime.setText(dateStr);
        }
    }

    /**
     * 获取当年当月的第一天
     * @param year 年
     * @param month 月
     * @return 当年当月的第一天
     */
    private long getFirstDayOfMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);
        return calendar.getTime().getTime();
    }

    /**
     * 获取当年当月的最后一天
     * @param year 年
     * @param month 月
     * @return 当年当月的最后一天
     */
    private long getLastDayOfMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        return calendar.getTime().getTime();
    }

    @Override
    protected void initData() {
        super.initData();
        showLoading();
        mPresenter.requestOnlineClassIds(mCurMemberId,mRole);
    }

    /**
     * 获取当月的有课标志
     * @param startDate 开始时间
     * @param endDate 结束时间
     */
    private void getTimetableFlags(final DateTime startDate, final DateTime endDate){
        // 清除当天的标记
        List<String> timeTableFlags = new ArrayList<>();
        mScheduleLayout.setTaskHints(startDate.getYear(),
                startDate.getMonthOfYear(),
                String.class, timeTableFlags);
        if(EmptyUtil.isEmpty(mClassIds)) return;
        mPresenter.requestTimeTableFlags(startDate,endDate,mClassIds);
    }

    private void loadData(boolean moreData){
        if(EmptyUtil.isEmpty(mClassIds)) return;

        String beginTime = new StringBuffer().append(mSelectYear).append("-")
                .append(mSelectMonth + 1).append("-")
                .append(mSelectDay).append(" 00:00:00").toString();
        String endTime = new StringBuffer().append(mSelectYear).append("-")
                .append(mSelectMonth + 1).append("-")
                .append(mSelectDay).append(" 23:59:59").toString();

        if(!moreData){
            pageIndex = 0;
            mRefreshLayout.showRefresh();
            mPresenter.requestAirClassDataWithTimeTable(pageIndex,beginTime,endTime,mClassIds);
        }else{
            pageIndex++;
            mPresenter.requestAirClassDataWithTimeTable(pageIndex,beginTime,endTime,mClassIds);
        }
    }

    /**
     * 删除当前直播
     * @param data 直播数据
     * @param deleteAll 是否从所有班级删除
     */
    private void deleteCurrentItem(final @NonNull LiveVo data,boolean deleteAll){
        // 删除直播
        mPresenter.requestDeleteLive(Integer.parseInt(data.getId()), data.getClassId(),deleteAll);
    }

    @Override
    public void updateOnlineClassIdsView(List<String> listData) {
        hideLoading();
        mClassIds = listData;
        // 当只有加载到班级Id数据，才能加载课程表和空中课堂数据
        DateTime startDate = new DateTime(mSelectYear, mSelectMonth + 1, 1, 0, 0);
        DateTime endDate = new DateTime(mSelectYear, mSelectMonth + 1,CalendarUtils.getMonthDays(mSelectYear,mSelectMonth), 0, 0);
        getTimetableFlags(startDate, endDate);
        loadData(false);
    }

    @Override
    public void updateTimeTableFlagsView(@NonNull DateTime startDate, @NonNull DateTime endDate, List<String> timeTableFlags) {
        if(EmptyUtil.isNotEmpty(mScheduleLayout)){
            mScheduleLayout.setTaskHints(startDate.getYear(),
                    startDate.getMonthOfYear(),
                    String.class, timeTableFlags);
        }
    }

    @Override
    public void updateAirClassDataView(List<LiveVo> listVos) {
        mRefreshLayout.onHeaderRefreshComplete();
        mRefreshLayout.onFooterRefreshComplete();
        mLiveRoomAdapter.setData(listVos);
        mLiveRoomAdapter.notifyDataSetChanged();
        mRefreshLayout.setLoadMoreEnable(EmptyUtil.isNotEmpty(listVos) && listVos.size() >= AppConfig.PAGE_SIZE);
    }

    @Override
    public void updateMoreAirClassDataView(List<LiveVo> listVos) {
        mRefreshLayout.onHeaderRefreshComplete();
        mRefreshLayout.onFooterRefreshComplete();
        mLiveRoomAdapter.addData(listVos);
        mLiveRoomAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateCheckFinishView(boolean finishOrHistory, @NonNull LiveVo vo) {
        if(!finishOrHistory){
            // 直播的创建者
            List<LiveEntity.PublishClassListBean> publishClasses = JSONObject.parseArray(vo.getPublishClassList().toString(), LiveEntity.PublishClassListBean.class);
            if(publishClasses.size() > 1){
                popCanSelectDeleteWayDialog(vo);
            } else {
                popDeleteCurrentClassDialog(vo);
            }
        }else{
            // 弹窗提醒
            popDeleteWarning();
        }
    }

    @Override
    public void updateDeleteOnlineLiveView(boolean result) {
        if (result) {
            // 删除直播成功
            UIUtil.showToastSafe(R.string.label_delete_live_succeed);
            // 重新拉取数据
            // 当只有加载到班级Id数据，才能加载课程表和空中课堂数据
            DateTime startDate = new DateTime(mSelectYear, mSelectMonth + 1, 1, 0, 0);
            DateTime endDate = new DateTime(mSelectYear, mSelectMonth + 1,CalendarUtils.getMonthDays(mSelectYear,mSelectMonth), 0, 0);
            getTimetableFlags(startDate,endDate);
            loadData(false);
        }else{
            // 删除直播失败
            // 不提示删除失败
            // UIUtil.showToastSafe(R.string.label_delete_live_failed);
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.tv_datetime){
            showDateTimeBarPicker();
        }else if(viewId == R.id.tv_today){
            if (EmptyUtil.isNotEmpty(mScheduleLayout)) {
                mScheduleLayout.scrollToToday();
            }
        }
    }

    private void showDateTimeBarPicker(){
        DatePickerPopupView popupView = new DatePickerPopupView(
                this, mSelectYear, mSelectMonth - 1,
                DatePickerPopupView.BlackType.BLACK_TOP,
                new DatePickerPopupView.PopupWindowListener() {
                    @Override
                    public void onOKClick(int year, int month) {
                        mScheduleLayout.scrollToMonth(year, month);
                    }

                    @Override
                    public void onBottomButtonClickListener() {

                    }

                    @Override
                    public void onDismissListener() {

                    }
                });
        popupView.showAtLocation(getWindow().getDecorView(),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @Override
    public void showError(int str) {
        super.showError(str);
        hideLoading();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mScheduleLayout != null){
            Calendar calendar = Calendar.getInstance();
            WeekCalendarView.lastSelectWeekNumber
                    = CalendarUtils.getWeek(calendar.get(Calendar.YEAR)
                    , calendar.get(Calendar.MONTH)
                    , calendar.get(Calendar.DAY_OF_MONTH));
        }
    }

    /**
     * 我的课程表的入口
     * @param context 上下文对象
     */
    public static void show(@NonNull Context context,@NonNull TimeTableParams params){
        Intent intent = new Intent(context,TimeTableActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ACTIVITY_BUNDLE_OBJECT,params);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
