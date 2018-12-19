package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.ActClassroomActivity;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.DateUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.pojo.ContactsClassMemberInfo;
import com.galaxyschool.app.wawaschool.pojo.ContactsClassMemberListResult;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.views.DatePickerPopupView;
import com.galaxyschool.app.wawaschool.views.RoundedImageView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 筛选作业
 */
public class ActClassroomPickerFragment extends ContactsPickerFragment implements DatePickerPopupView.OnDatePickerItemSelectedListener {

    public static final String TAG = ActClassroomPickerFragment.class.getSimpleName();
    private TextView mSelectTitle, headTitle;
    private ImageView mSelectIcon;
    private TextView startTime;
    private TextView endTime;
    private TextView mConfirm, mClear;
    private TextView allSelectText;
    private View rootView;
    private GridView gridView;
    private List<ContactsClassMemberInfo> studentLists;
    private String groupId;
    private String today;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_act_classroom_picker, null);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getIntent();
        initView();
        initGridView();
        loadStudentInfo();
    }

    private void getIntent() {
        Bundle bundle = getArguments();
        groupId = bundle.getString(ActClassroomFragment.Constants.EXTRA_CONTACTS_ID);
    }

    private void initView() {
        headTitle = (TextView) findViewById(R.id.contacts_header_title);
        if (headTitle != null) {
            headTitle.setText(getString(R.string.filter));
        }
        //显示学生的title
        mSelectTitle = (TextView) findViewById(R.id.layout_select_all_layout_item_title);
        if (mSelectTitle != null) {
            mSelectTitle.setText(getString(R.string.student));
        }
        //显示小圆圈的勾选状态
        mSelectIcon = (ImageView) findViewById(R.id.layout_select_all_layout_select_all_icon);
        if (mSelectIcon != null) {
            //点击全选字段的文本有相应时间
            mSelectIcon.setOnClickListener(this);
        }
        //全选和取消全选状态的切换
        allSelectText = (TextView) findViewById(R.id.layout_select_all_layout_select_all_title);
        if (allSelectText != null) {
            allSelectText.setOnClickListener(this);
        }

        today = DateUtils.getDateYmdStr();
        //开始时间
        startTime = (TextView) findViewById(R.id.assign_start_date);
        if (startTime != null) {
            startTime.setText(today);
            startTime.setOnClickListener(this);
        }
        //结束时间
        endTime = (TextView) findViewById(R.id.assign_end_date);
        if (endTime != null) {
            endTime.setText(today);
            endTime.setOnClickListener(this);
        }
        //确定
        mConfirm = (TextView) findViewById(R.id.contacts_picker_confirm);
        if (mConfirm != null) {
            mConfirm.setText(getString(R.string.noc_sure));
            mConfirm.setOnClickListener(this);
        }
        //清除
        mClear = (TextView) findViewById(R.id.contacts_picker_clear);
        if (mClear != null) {
            mClear.setText(getString(R.string.clear_screening_case));
            mClear.setOnClickListener(this);
        }
        notifyPickerBar(false);
    }
    private void initGridView() {
        gridView = (GridView) findViewById(R.id.homework_grid_view);
        if (gridView != null) {
            gridView.setNumColumns(4);
            AdapterViewHelper listViewHelper = new AdapterViewHelper(getActivity(),
                    gridView, R.layout.item_screening_gridview) {
                @Override
                public void loadData() {
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    if (view != null) {
                        ContactsClassMemberInfo data = (ContactsClassMemberInfo) getData().get
                                (position);
                        if (data == null) {
                            return view;
                        }
                        ViewHolder holder = (ViewHolder) view.getTag();
                        if (holder == null) {
                            holder = new ViewHolder();
                        }
                        holder.data = data;

                        RoundedImageView thumbnail = (RoundedImageView) view.findViewById(R.id.icon_head);
                        if (thumbnail != null) {
                            MyApplication.getThumbnailManager((Activity) context)
                                    .displayUserIconWithDefault(AppSettings.getFileUrl(data.getHeadPicUrl()),
                                            thumbnail, R.drawable.default_user_icon);
                        }
                        TextView studentName = (TextView) view.findViewById(R.id.title);
                        if (studentName != null) {
                            String realName = data.getRealName();
                            if (!TextUtils.isEmpty(realName)) {
                                studentName.setText(realName);
                            } else {
                                studentName.setText(data.getNoteName());
                            }
                        }
                        ImageView iconSelect = (ImageView) view.findViewById(R.id.icon_selector);
                        if (iconSelect != null) {
                            iconSelect.setSelected(data.isSelect());
                        }
                        view.setTag(holder);
                    }
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) return;
                    changeSelectStatus((ContactsClassMemberInfo) holder.data);
                }
            };
            setCurrAdapterViewHelper(gridView, listViewHelper);
        }
    }

    private void changeSelectStatus(ContactsClassMemberInfo student) {
        boolean flag = student.isSelect();
        student.setIsSelect(!flag);
        getCurrAdapterViewHelper().update();
        if (studentLists.size() > 0) {
            boolean isAllSelect = true;
            boolean isSelect = false;
            for (int i = 0; i < studentLists.size(); i++) {
                ContactsClassMemberInfo studentInfo = studentLists.get(i);
                if (studentInfo.isSelect()) {
                    isSelect = true;
                } else {
                    isAllSelect = false;
                }
            }
            notifyPickerBar(isSelect);
            mSelectIcon.setSelected(isAllSelect);
            if (mSelectIcon.isSelected()){
                allSelectText.setText(getString(R.string.cancel_to_select_all));
            }else {
                allSelectText.setText(getString(R.string.select_all));
            }
        }
    }

    /**
     * 记载班级通讯录并且筛选出学生成员
     */
    private void loadStudentInfo() {
        if (getUserInfo() == null) {
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("Id", this.groupId);
        DefaultPullToRefreshListener listener =
                new DefaultPullToRefreshListener<ContactsClassMemberListResult>(
                        ContactsClassMemberListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        ContactsClassMemberListResult result = getResult();
                        if (result == null || !result.isSuccess()
                                || result.getModel() == null) {
                            return;
                        }
                        updateViews(result);
                    }
                };
        listener.setShowLoading(true);
        postRequest(ServerUrl.CONTACTS_CLASS_MEMBER_LIST_URL, params, listener);
    }

    private void updateViews(ContactsClassMemberListResult result) {
        List<ContactsClassMemberInfo> list = result.getModel().getClassMailListDetailList();
        if (list == null || list.size() <= 0) {
            return;
        }
        studentLists = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            ContactsClassMemberInfo memberInfo = list.get(i);
            if (memberInfo.getRole() == RoleType.ROLE_TYPE_STUDENT && memberInfo.getWorkingState()
                     == 1) {
                studentLists.add(memberInfo);
            }
        }
        if (studentLists.size() > 0) {
            getCurrAdapterViewHelper().setData(studentLists);
        }
    }

    private void notifyPickerBar(boolean selected) {
        boolean flag =campareCurrentTimeIsChange();
        View view = findViewById(R.id.homework_picker_bar_layout);
        if (view != null) {
            if (mClear != null ) {
                if (selected) {
                    mClear.setEnabled(selected);
                } else {
                    if (flag) {
                        mClear.setEnabled(selected);
                    }
                }
            }
            if (mConfirm != null) {
                mConfirm.setEnabled(true);
            }
        }
    }

    /**
     * 比较开始时间与结束时间
     *
     * @param startDateStr
     * @param endDateStr
     * @return
     */
    private boolean checkDate(String startDateStr, String endDateStr) {
        boolean isOk = true;
        int result = DateUtils.compareDate(endDateStr, startDateStr);
        if (result < 0) {
            isOk = false;
            TipMsgHelper.ShowLMsg(getActivity(), R.string.publish_end_time_must_more_than_start_time);
            return isOk;
        }
        return isOk;
    }

    @Override
    public void onClick(View v) {
        if (v == startTime || v == endTime) {
            String dateStr = ((TextView) v).getText().toString();
            showPopupWindow(v, dateStr);
        } else if (v == mSelectIcon || v.getId() == R.id.layout_select_all_layout_select_all_title) {
            selectAllItem(!mSelectIcon.isSelected(),false);
        } else if (v == mConfirm) {
            confirmSelectData();
        } else if (v == mClear) {
            selectAllItem(false,true);
        } else {
            super.onClick(v);
        }
    }

    private void confirmSelectData() {
        //判断日期是否符合规范
        boolean flag=checkDate(startTime.getText().toString().trim(), endTime.getText().toString().trim());
        if (!flag){
            return;
        }
        List<ContactsClassMemberInfo> selectStudents = new ArrayList<>();
        if (studentLists != null && studentLists.size() > 0) {
            for (int i = 0; i < studentLists.size(); i++) {
                ContactsClassMemberInfo singleStudent = studentLists.get(i);
                if (singleStudent.isSelect()) {
                    //把选中的学生增加筛选条件中
                    selectStudents.add(singleStudent);
                }
            }
        }
        Intent intent = new Intent(getActivity(), ActClassroomActivity.class);
        Bundle bundle = getArguments();
        bundle.putBoolean(ActivityUtils.EXTRA_TEMP_DATA,true);
        bundle.putString(ActClassroomActivity.EXTRA_START_TIME,startTime.getText().toString().trim());
        bundle.putString(ActClassroomActivity.EXTRA_END_TIME,endTime.getText().toString().trim());
        bundle.putParcelableArrayList(ActClassroomActivity.EXTRA_STUDENT_LIST, (ArrayList<? extends Parcelable>) selectStudents);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void selectAllItem(boolean isSelect,boolean isClear) {
        if (studentLists != null && studentLists.size() > 0) {
            for (int i = 0; i < studentLists.size(); i++) {
                ContactsClassMemberInfo studentInfo = studentLists.get(i);
                studentInfo.setIsSelect(isSelect);
            }
            mSelectIcon.setSelected(isSelect);
            if (isClear) {
                resetTimeScreen();
            }
            notifyPickerBar(isSelect);
            if (mSelectIcon.isSelected()){
                allSelectText.setText(getString(R.string.cancel_to_select_all));
            }else {
                allSelectText.setText(getString(R.string.select_all));
            }
            getCurrAdapterViewHelper().update();
        }
    }
    private void resetTimeScreen(){
        startTime.setText(today);
        endTime.setText(today);
    }

    private boolean  campareCurrentTimeIsChange(){
        String tempStartTime = startTime.getText().toString().trim();
        String tempEndTime = endTime.getText().toString().trim();
        if (tempStartTime.equals(today) && tempEndTime.equals(today)){
            return true;
        }
        return false;

    }

    private void showPopupWindow(View targetView, String dateStr) {
        String pattern = "yyyy-MM-dd";
        Date currentDate = new Date();
        Date date = DateUtils.parseDateStr(dateStr, pattern);
        DatePickerPopupView datePickerPopupView = new DatePickerPopupView(getActivity(), this, targetView);
        if (date != null) {
            datePickerPopupView.setCurrentYearMonthDay(date.getYear() + 1900, date.getMonth(), date.getDate());
        } else {
            datePickerPopupView.setCurrentYearMonthDay(currentDate.getYear() + 1900, currentDate.getMonth(), currentDate.getDate());
        }
        datePickerPopupView.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
    }

    @Override
    public void onDatePickerItemSelected(String pickedResultStr, View targetView) {
        TextView obj = (TextView) targetView;
        if (pickedResultStr != null) {
            obj.setText(pickedResultStr);
            if (!pickedResultStr.equals(today)){
                notifyPickerBar(true);
            }else {
                notifyPickerBar(false);
            }
        }


    }
}
