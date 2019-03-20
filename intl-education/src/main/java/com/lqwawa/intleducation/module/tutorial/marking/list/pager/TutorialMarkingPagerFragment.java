package com.lqwawa.intleducation.module.tutorial.marking.list.pager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jeek.calendar.widget.calendar.CalendarUtils;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.CourseEmptyView;
import com.lqwawa.intleducation.base.PresenterFragment;
import com.lqwawa.intleducation.base.utils.DateUtils;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.calender.CalenderPopupWindow;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerItemDecoration;
import com.lqwawa.intleducation.common.utils.DateUtil;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.tutorial.DateFlagEntity;
import com.lqwawa.intleducation.factory.data.entity.tutorial.TaskEntity;
import com.lqwawa.intleducation.factory.data.entity.tutorial.TutorEntity;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.intleducation.module.discovery.adapter.CourseListAdapter;
import com.lqwawa.intleducation.module.learn.tool.TaskSliderHelper;
import com.lqwawa.intleducation.module.tutorial.marking.list.MarkingStateType;
import com.lqwawa.intleducation.module.tutorial.marking.list.OrderByType;
import com.lqwawa.intleducation.module.tutorial.marking.list.TutorialMarkingParams;
import com.lqwawa.intleducation.module.tutorial.marking.list.TutorialRoleType;
import com.lqwawa.intleducation.module.tutorial.marking.require.TaskRequirementActivity;
import com.lqwawa.intleducation.module.tutorial.student.courses.StudentTutorialAdapter;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.lqbaselib.pojo.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.db.Selector;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

/**
 * @author mrmedici
 * @desc 帮辅批阅列表页面
 */
public class TutorialMarkingPagerFragment extends PresenterFragment<TutorialMarkingPagerContract.Presenter>
    implements TutorialMarkingPagerContract.View, View.OnClickListener{

    private ImageView mBtnArrowPre;
    private ImageView mBtnArrowNext;
    private TextView mTvDate;
    private TextView mTvToday;

    private PullToRefreshView mRefreshLayout;
    private RecyclerView mRecycler;
    private CourseEmptyView mEmptyView;
    // Adapter
    private TutorialTaskAdapter mTutorialAdapter;

    private int pageIndex;

    private CalenderPopupWindow mCalenderPopupWindow;


    private Date date = DateUtils.getCurDate();
    private SparseArray<List<Integer>> mSparseArray = new SparseArray<>();

    private TutorialMarkingParams mMarkingParams;
    private String mCurMemberId;
    private String mTutorialRole;
    private int mMarkType;

    public static Fragment newInstance(@NonNull TutorialMarkingParams params){
        Fragment fragment = new TutorialMarkingPagerFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(FRAGMENT_BUNDLE_OBJECT,params);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected TutorialMarkingPagerContract.Presenter initPresenter() {
        return new TutorialMarkingPagerPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_tutorial_marking_pager;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mBtnArrowPre = (ImageView) mRootView.findViewById(R.id.btn_arrow_pre);
        mBtnArrowNext = (ImageView) mRootView.findViewById(R.id.btn_arrow_next);
        mTvDate = (TextView) mRootView.findViewById(R.id.tv_date);
        mTvToday = (TextView) mRootView.findViewById(R.id.tv_today);

        mBtnArrowPre.setOnClickListener(this);
        mBtnArrowNext.setOnClickListener(this);
        mTvDate.setOnClickListener(this);
        mTvToday.setOnClickListener(this);

        mRefreshLayout = (PullToRefreshView) mRootView.findViewById(R.id.refresh_layout);
        mEmptyView = (CourseEmptyView) mRootView.findViewById(R.id.empty_layout);

        mRecycler = (RecyclerView) mRootView.findViewById(R.id.recycler);
        mRecycler.setNestedScrollingEnabled(false);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity()){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mRecycler.setLayoutManager(mLayoutManager);
        mTutorialAdapter = new TutorialTaskAdapter(TextUtils.equals(mTutorialRole,TutorialRoleType.TUTORIAL_TYPE_TUTOR));
        mRecycler.setAdapter(mTutorialAdapter);
        mRecycler.addItemDecoration(new RecyclerItemDecoration(getActivity(),RecyclerItemDecoration.VERTICAL_LIST));

        mTutorialAdapter.setCallback(new TutorialTaskAdapter.EntityCallback() {
            @Override
            public void onRequireClick(View it, int position, @NonNull TaskEntity entity) {
                TaskRequirementActivity.show(getActivity(),entity);
            }

            @Override
            public void onEntityClick(View it, int position, @NonNull TaskEntity entity, int state) {
                if(EmptyUtil.isNotEmpty(TaskSliderHelper.onTaskSliderListener)){
                    TaskSliderHelper.onTutorialMarkingListener.openAssistanceMark(getActivity(),entity);
                }
            }

            @Override
            public void onCheckMark(View it, int position, @NonNull TaskEntity entity, int state) {
                if(EmptyUtil.isNotEmpty(TaskSliderHelper.onTaskSliderListener)){
                    TaskSliderHelper.onTutorialMarkingListener.openAssistanceMark(getActivity(),entity);
                }
            }
        });

        mTutorialAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<TaskEntity>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, TaskEntity entity) {
                super.onItemClick(holder, entity);
                if(EmptyUtil.isNotEmpty(TaskSliderHelper.onTaskSliderListener)){
                    TaskSliderHelper.onTutorialMarkingListener.openAssistanceMark(getActivity(),entity);
                }
            }
        });

        // 下拉刷新
        mRefreshLayout.setLastUpdated(new Date().toLocaleString());
        mRefreshLayout.showRefresh();
        mRefreshLayout.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                loadStudyTask(false);
            }
        });

        mRefreshLayout.setLoadMoreEnable(false);
        mRefreshLayout.setOnFooterRefreshListener(new PullToRefreshView.OnFooterRefreshListener() {
            @Override
            public void onFooterRefresh(PullToRefreshView view) {
                loadStudyTask(true);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }

        date = DateUtils.getCurDate();
        String dateStr = getDateStr(date);
        mTvDate.setText(dateStr);
        loadStudyTask();
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        if(bundle.containsKey(FRAGMENT_BUNDLE_OBJECT)){
            mMarkingParams = (TutorialMarkingParams) bundle.getSerializable(FRAGMENT_BUNDLE_OBJECT);
            if(EmptyUtil.isNotEmpty(mMarkingParams)){
                mCurMemberId = mMarkingParams.getCurMemberId();
                mTutorialRole = mMarkingParams.getRole();
                mMarkType = mMarkingParams.getMarkType();
            }
        }

        if(EmptyUtil.isNotEmpty(mCurMemberId)) return false;

        return super.initArgs(bundle);
    }

    /**
     * 加载作业数据
     */
    private void loadStudyTask(){
        loadStudyTask(false);
    }

    /**
     * 加载作业数据
     * @param moreData 是否加载更多
     */
    private void loadStudyTask(boolean moreData){
        String startTimeBegin = DateUtils.dateToString(getStartTime(date),DateUtils.YYYYMMDDHHMMSS);
        String endTimeBegin = DateUtils.dateToString(getEndTime(date),DateUtils.YYYYMMDDHHMMSS);

        if(moreData){
            pageIndex ++;
        }else{
            pageIndex = 0;
        }

        if(mMarkType == MarkingStateType.MARKING_STATE_NOT){
            // 未批阅页面
            if(mMarkingParams.isTutor()) {
                mPresenter.requestWorkDataWithIdentityId("", mCurMemberId, "", "", startTimeBegin, endTimeBegin, "", "", mMarkType, OrderByType.MARKING_ASC_TIME_DESC, pageIndex);
            }else{
                mPresenter.requestWorkDataWithIdentityId(mCurMemberId,"","", "", startTimeBegin, endTimeBegin, "", "", mMarkType, OrderByType.MARKING_ASC_TIME_DESC, pageIndex);
            }

        }else if(mMarkType == MarkingStateType.MARKING_STATE_HAVE){
            // 已批阅页面
            if(mMarkingParams.isTutor()){
                mPresenter.requestWorkDataWithIdentityId("",mCurMemberId,"","","","",startTimeBegin,endTimeBegin,mMarkType,OrderByType.MARKING_ASC_TIME_DESC,pageIndex);
            }else{
                mPresenter.requestWorkDataWithIdentityId(mCurMemberId,"","","","","",startTimeBegin,endTimeBegin,mMarkType,OrderByType.MARKING_ASC_TIME_DESC,pageIndex);
            }
        }
    }

    private static Date getStartTime(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        return calendar.getTime();

    }

    private  static Date getEndTime(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY,23);
        calendar.set(Calendar.MINUTE,59);
        calendar.set(Calendar.SECOND,59);
        calendar.set(Calendar.MILLISECOND,999);
        return calendar.getTime();
    }

    @Override
    public void updateDataFlagForAssist(@NonNull String startTimeBegin, int position, @NonNull List<DateFlagEntity> entities) {
        ListIterator<DateFlagEntity> iterator = entities.listIterator();
        List<String> signData = new ArrayList<>();
        while (iterator.hasNext()){
            DateFlagEntity entity = iterator.next();
            if(!entity.isBolHaveData()){
                iterator.remove();
                continue;
            }

            signData.add(entity.getDate());

        }
        handleSignTimeData(signData, startTimeBegin, position);
    }

    @Override
    public void updateWorkDataWithIdentityIdView(List<TaskEntity> entities) {
        // 判断有无更多数据,打开或者关闭加载更多
        mRefreshLayout.onHeaderRefreshComplete();
        mRefreshLayout.setLoadMoreEnable(entities.size() >= AppConfig.PAGE_SIZE);
        mTutorialAdapter.replace(entities);

        if(EmptyUtil.isEmpty(entities)){
            // 数据为空
            mRecycler.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        }else{
            // 数据不为空
            mRecycler.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void updateMoreWorkDataWithIdentityIdView(List<TaskEntity> entities) {
        // 关闭加载更多
        mRefreshLayout.onFooterRefreshComplete();
        mRefreshLayout.setLoadMoreEnable(entities.size() >= AppConfig.PAGE_SIZE);
        // 设置数据
        mTutorialAdapter.add(entities);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.btn_arrow_pre){
            switchPrev();
        }else if(viewId == R.id.btn_arrow_next){
            switchNext();
        }else if(viewId == R.id.tv_date){
            changeData((View) v.getParent());
        }else if(viewId == R.id.tv_today){
            switchToday();
        }
    }

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
        if (mCalenderPopupWindow != null) {
            mCalenderPopupWindow.upDateCalendarView(date);
        }
    }

    private void updateDateView(Date date) {
        String dateStr = getDateStr(date);
        mTvDate.setText(dateStr);
        loadStudyTask();
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

    /***
     * 获取PopupWindow实例
     */
    private void changeData(View v) {
        if (mCalenderPopupWindow != null) {
            if (!mCalenderPopupWindow.isShowing()) {
                if (mSparseArray != null) {
                    mSparseArray.clear();
                }
                mCalenderPopupWindow.showPopupMenu(v, true);
            }
            return;
        }
        mCalenderPopupWindow = new CalenderPopupWindow(date, getActivity(), new CalenderPopupWindow.OnDatePickListener() {
            @Override
            public void onDatePick(Date monthDay, int position) {
                date = monthDay;
                String dateStr = getDateStr(date);
                mTvDate.setText(dateStr);
                List<Integer> integerList = mSparseArray.get(position);
                if (integerList != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    if (integerList.size() > 0 && integerList.contains(day)) {
                        loadStudyTask();
                    } else {
                        mEmptyView.setVisibility(View.VISIBLE);
                        mRecycler.setVisibility(View.GONE);
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
                        if (mCalenderPopupWindow != null) {
                            mCalenderPopupWindow.updateSignData();
                        }
                    }
                }
            }
        });
        mCalenderPopupWindow.resetMonthView();
        mCalenderPopupWindow.showPopupMenu(v, false);

    }

    private void loadDateSignDatas(String filterStartTimeBegin, final int position) {
        Date date = DateUtils.stringToDate(filterStartTimeBegin, DateUtils.YYYYMMDD);
        final String startTimeBegin = DateUtils.getMonthFirstDay(date, DateUtils.YYYYMMDD);
        final String startTimeEnd = DateUtils.getMonthLastDay(date, DateUtils.YYYYMMDD);
        mPresenter.requestDateFlagForAssist(position,mCurMemberId,mTutorialRole,startTimeBegin,startTimeEnd,mMarkType);
    }

    /**
     * 解析日期标记
     * @param signTimeData
     * @param searchTime
     * @param position
     */
    private void handleSignTimeData(List<String> signTimeData, String searchTime, int position) {

        List<Integer> integerList = new ArrayList<>();
        mSparseArray.put(position, integerList);

        if (signTimeData != null && signTimeData.size() > 0) {
            for (int i = 0, len = signTimeData.size(); i < len; i++) {
                String signTime = signTimeData.get(i);
                signTime = DateUtils.getStringToString(signTime, DateUtils.YYYYMMDD);
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
            if (mCalenderPopupWindow != null) {
                mCalenderPopupWindow.updateSignData();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(@NonNull MessageEvent event){
        String action = event.getUpdateAction();
        if(TextUtils.equals(action,EventConstant.TRIGGER_UPDATE_LIST_DATA)){
            loadStudyTask();
        }
    }

    @Override
    public void showError(int str) {
        super.showError(str);
        mRefreshLayout.onHeaderRefreshComplete();
        mRefreshLayout.onFooterRefreshComplete();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }
}
