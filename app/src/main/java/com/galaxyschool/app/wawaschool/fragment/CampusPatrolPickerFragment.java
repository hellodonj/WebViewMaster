package com.galaxyschool.app.wawaschool.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.CampusPatrolUtils;
import com.galaxyschool.app.wawaschool.common.DateUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.views.DatePickerPopupView;

import java.util.Date;

/**
 * 筛选资源页面
 */
public class CampusPatrolPickerFragment extends ContactsPickerFragment implements
        DatePickerPopupView.OnDatePickerItemSelectedListener {

    public static final String TAG = CampusPatrolPickerFragment.class.getSimpleName();

    private TextView screeningStartDate;
    private TextView screeningEndDate;
    private TextView confirmBtn;
    private View rootView;
    public static final String START_DATE = "start_date";
    public static final String END_DATE = "end_date";
    public static final int RESULT_CODE = 1001;
    public static final int REQUEST_CODE = 501;
    public static final int CREATE_NEW_RESOURCE_REQUEST_CODE = 10;
    public static final int CREATE_NEW_RESOURCE_RESULT_CODE = 20;
    public static final int OPEN_COURSE_DETAILS_REQUEST_CODE = 30;
    public static final int OPEN_COURSE_DETAILS_RESULT_CODE = 40;
    public static final int EDIT_NOTE_DETAILS_REQUEST_CODE = 50;

    //更新讨论
    public static final int REQUEST_CODE_DISCUSSION_TOPIC = 108;//讨论话题
    public static final int REQUEST_CODE_DISCUSSION_INTRODUCTION = 208;//导读
    public static final int REQUEST_CODE_DISCUSSION_COURSE_DETAILS = 308;//微课详情
    public static final int REQUEST_CODE_HOMEWORK_COMMIT = 408;//提交作业列表
    public static final int REQUEST_CODE_HOMEWORK_SCREENING_RESULT = 508;//筛选作业结果列表
    public static final int REQUEST_CODE_HOMEWORK_TODAY_TASK = 608;//今日任务列表
    public static final int REQUEST_CODE_HOMEWORK_PICKER = 708;//筛选作业页面
    public static final int REQUEST_CODE_HOMEWORK_TASK_FINISH_INFO = 808;//个人空间--任务详情页面
    public static final int REQUEST_CODE_ENGLISH_WRITING_COMMIT = 908;//英文写作提交页面
    public static final int REQUEST_CODE_ENGLISH_WRITING_COMMENT_DETAILS = 1008;//评论详情页面
    public static final int REQUEST_CODE_FINISH_STATUS = 1108;//已完成未完成头像页面
    public static final int REQUEST_CODE_CAMPUS_PATROL_TEACHER_RESOURCE_LIST = 1208;//老师数据统计页面
    public static final int REQUEST_CODE_CAMPUS_PATROL_CLASS_RESOURCE_LIST = 1308;//班级数据统计页面
    public static final int RESULT_CODE_COMPLETED_HOMEWORK_LIST_FRAGMENT = 1408;//已完成作业列表
    public static final int REQUEST_CODE_PICKED_COURSE = 22;//选择课件
    public static final int RESULT_CODE_PICKED_COURSE = 32;//选择课件

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_campus_patrol_picker, null);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    private void initViews() {

        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            textView.setText(R.string.screening);
        }

        //时间选择

        //今天
        String today = DateUtils.getDateYmdStr();
        //明天
        String tomorrow = DateUtils.getTomorrow();

        screeningStartDate = (TextView) findViewById(R.id.screening_start_date);
        //如果上次没筛选，带入默认日期，否则带入上次筛选的日期。
        if (MyApplication.SCREENING_START_DATE == null){
            screeningStartDate.setText(today);
        }else {
            screeningStartDate.setText(MyApplication.SCREENING_START_DATE);
        }
        screeningStartDate.setOnClickListener(this);

        screeningEndDate = (TextView) findViewById(R.id.screening_end_date);
        //如果上次没筛选，带入默认日期，否则带入上次筛选的日期。
        if (MyApplication.SCREENING_END_DATE == null){
            screeningEndDate.setText(tomorrow);
        }else {
            screeningEndDate.setText(MyApplication.SCREENING_END_DATE);
        }
        screeningEndDate.setOnClickListener(this);

        confirmBtn = (TextView) findViewById(R.id.confirm_btn);
        confirmBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == screeningStartDate || v == screeningEndDate) {
            String dateStr = ((TextView)v).getText().toString();
            showPopupWindow(v, dateStr);
        } else if (v == confirmBtn){
            //确定
            pageSkip();
        }else {
            super.onClick(v);
        }
    }

    private void pageSkip() {
        String startDate = screeningStartDate.getText().toString().trim();
        String endDate = screeningEndDate.getText().toString().trim();
        if (!CampusPatrolUtils.isValidDate(startDate,endDate)){
            TipsHelper.showToast(getActivity(),R.string.txt_end_date_early_than_start_date);
            return;
        }
        Intent intent = new Intent();
        //更新确定选择的日期到内存中
        MyApplication.SCREENING_START_DATE = startDate;
        MyApplication.SCREENING_END_DATE = endDate;
        intent.putExtra(START_DATE,startDate);
        intent.putExtra(END_DATE,endDate);
        getActivity().setResult(RESULT_CODE,intent);
        getActivity().finish();
    }

    private void showPopupWindow(View targetView, String dateStr) {
        String pattern = "yyyy-MM-dd";
        Date currentDate=new Date();
        Date date = DateUtils.parseDateStr(dateStr, pattern);
        DatePickerPopupView datePickerPopupView = new DatePickerPopupView(getActivity(), this, targetView);
        if(date != null) {
            datePickerPopupView.setCurrentYearMonthDay(date.getYear() + 1900, date.getMonth(), date.getDate());
        }else {
            datePickerPopupView.setCurrentYearMonthDay(currentDate.getYear() + 1900, currentDate.getMonth(), currentDate.getDate());
        }
        datePickerPopupView.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
    }

    @Override
    public void onDatePickerItemSelected(String pickedResultStr, View targetView) {
        TextView obj = (TextView) targetView;
        if (!TextUtils.isEmpty(pickedResultStr)) {
            obj.setText(pickedResultStr);
        }
    }
}
