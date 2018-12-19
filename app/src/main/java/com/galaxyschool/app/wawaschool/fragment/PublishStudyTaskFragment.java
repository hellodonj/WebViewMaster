package com.galaxyschool.app.wawaschool.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.ContactsPickerActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.DateUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;
import com.galaxyschool.app.wawaschool.pojo.UploadCourseType;
import com.galaxyschool.app.wawaschool.pojo.UploadParameter;
import com.galaxyschool.app.wawaschool.views.DatePopupView;
import com.galaxyschool.app.wawaschool.views.ToolbarTopView;
import com.lqwawa.apps.views.ContainsEmojiEditText;

import java.util.Date;

/**
 * Created by Administrator on 2016/6/15.
 */
public class PublishStudyTaskFragment extends BaseFragment implements View.OnClickListener {

    public static final String TAG = PublishStudyTaskFragment.class.getSimpleName();

    public static final int TITLE_MAX_LEN = 40;

    private ContainsEmojiEditText titleView;
    private TextView startDateView;
    private TextView endDateView;
    private LinearLayout commitTaskLayout;
    private TextView commitTaskView;

    private String headerTitle;
    private Date defaultDate;
    private String startDateStr;
    private String endDateStr;
    private UploadParameter uploadParameter;

    private boolean isCommit = false;
    private TextView okTextView,cancelTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.publish_study_task, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    private void initViews() {
        if (getArguments() != null) {
            headerTitle = getArguments().getString(ActivityUtils.EXTRA_HEADER_TITLE);
            defaultDate = (Date) getArguments().getSerializable(ActivityUtils.EXTRA_DEFAULT_DATE);
            uploadParameter = (UploadParameter) getArguments().getSerializable(UploadParameter.class.getSimpleName());
        }
        ToolbarTopView toolbarTopView = (ToolbarTopView) findViewById(R.id.toolbar_top_view);
        toolbarTopView.getBackView().setVisibility(View.VISIBLE);
        toolbarTopView.getTitleView().setText(headerTitle);
        toolbarTopView.getCommitView().setVisibility(View.GONE);
        int textColor = getResources().getColor(R.color.text_green);
        toolbarTopView.getCommitView().setTextColor(textColor);
        toolbarTopView.getCommitView().setText(R.string.confirm);
        toolbarTopView.getBackView().setOnClickListener(this);
        toolbarTopView.getCommitView().setOnClickListener(this);

        okTextView = (TextView) findViewById(R.id.btn_bottom_ok);
        okTextView.setOnClickListener(this);
        cancelTextView = (TextView) findViewById(R.id.btn_bottom_cancel);
        cancelTextView.setOnClickListener(this);

        titleView = (ContainsEmojiEditText) findViewById(R.id.study_task_title_text);
        startDateView = (TextView) findViewById(R.id.study_task_start_date_text);
        endDateView = (TextView) findViewById(R.id.study_task_end_date_text);
        commitTaskLayout = (LinearLayout) findViewById(R.id.commit_task_layout);
        commitTaskView = (TextView) findViewById(R.id.commit_task_view);
        titleView.setMaxlen(TITLE_MAX_LEN);
        startDateView.setOnClickListener(this);
        endDateView.setOnClickListener(this);
        commitTaskView.setOnClickListener(this);

        if(uploadParameter != null) {
            commitTaskLayout.setVisibility(uploadParameter.getTaskType() == StudyTaskType
                    .WATCH_HOMEWORK ? View.VISIBLE : View.GONE);
        }

        if (defaultDate == null) {
            defaultDate = new Date();
        }
        String dateStr = DateUtils.getDateStr(defaultDate, "yyyy-MM-dd");
        Date endDate = DateUtils.getNextDate(defaultDate);
        startDateStr = dateStr;
        endDateStr = DateUtils.getDateStr(endDate, "yyyy-MM-dd");
        startDateView.setText(startDateStr);
        endDateView.setText(endDateStr);
        if (uploadParameter != null) {
            titleView.setText(uploadParameter.getFileName());
            if (uploadParameter.getTaskType()==StudyTaskType.INTRODUCTION_WAWA_COURSE){
                titleView.setEnabled(false);
            }
        }
    }

    private void updateTaskCommitView(boolean isCommit) {
        Drawable unselect = getResources().getDrawable(R.drawable.unselect);
        Drawable select = getResources().getDrawable(R.drawable.select);
        if(isCommit) {
            commitTaskView.setCompoundDrawablesWithIntrinsicBounds(null, null, select, null);
        } else {
            commitTaskView.setCompoundDrawablesWithIntrinsicBounds(null, null, unselect, null);
        }
    }

    private boolean checkDate() {
        boolean isOk = true;
        String curDateStr = DateUtils.getDateStr(new Date(), "yyyy-MM-dd");
        int result = DateUtils.compareDate(startDateStr, curDateStr);
        if (result < 0) {
            isOk = false;
            TipMsgHelper.ShowLMsg(getActivity(), R.string.date_above_cur_date);
            return isOk;
        }
        result = DateUtils.compareDate(endDateStr, curDateStr);
        if (result < 0) {
            isOk = false;
            TipMsgHelper.ShowLMsg(getActivity(), R.string.date_above_cur_date);
            return isOk;
        }
        result = DateUtils.compareDate(endDateStr, startDateStr);
        if (result < 0) {
            isOk = false;
            TipMsgHelper.ShowLMsg(getActivity(), R.string.end_date_above_start_date);
            return isOk;
        }
        return isOk;
    }

    private void enterContactsPicker(UploadParameter uploadParameter) {
        Bundle args = getArguments();
        if (uploadParameter != null) {
            args.putSerializable(UploadParameter.class.getSimpleName(), uploadParameter);
        }

        args.putInt(ContactsPickerActivity.EXTRA_UPLOAD_TYPE, UploadCourseType.STUDY_TASK);

        args.putBoolean(ContactsPickerActivity.EXTRA_USE_EXTENDED_PICKER, true);
        args.putInt(
            ContactsPickerActivity.EXTRA_PICKER_TYPE, ContactsPickerActivity.PICKER_TYPE_GROUP);
        args.putInt(
            ContactsPickerActivity.EXTRA_GROUP_TYPE, ContactsPickerActivity.GROUP_TYPE_CLASS);
        args.putBoolean(ContactsPickerActivity.EXTRA_PUBLISH_RESOURCE, true);
        args.putInt(
            ContactsPickerActivity.EXTRA_MEMBER_TYPE, ContactsPickerActivity.MEMBER_TYPE_STUDENT);
        args.putInt(
            ContactsPickerActivity.EXTRA_PICKER_MODE, ContactsPickerActivity.PICKER_MODE_MULTIPLE);
        args.putString(
            ContactsPickerActivity.EXTRA_PICKER_CONFIRM_BUTTON_TEXT, getString(R.string.send));
        args.putBoolean(ContactsPickerActivity.EXTRA_PICKER_SUPERUSER, true);
        args.putInt(ContactsPickerActivity.EXTRA_ROLE_TYPE, ContactsPickerActivity
            .ROLE_TYPE_TEACHER);
        Fragment fragment;
        if (args.getBoolean(ContactsPickerActivity.EXTRA_USE_EXTENDED_PICKER)) {
            fragment = new ContactsExtendedPickerEntryFragment();
        } else {
            fragment = new ContactsPickerEntryFragment();
        }
        fragment.setArguments(args);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.activity_body, fragment, ContactsPickerEntryFragment.TAG)
        .hide(PublishStudyTaskFragment.this);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.toolbar_top_back_btn) {
            popStack();
        } else if (v.getId() == R.id.toolbar_top_commit_btn) {
//            boolean isOk = checkDate();
//            if (!isOk) {
//                return;
//            }
//            UploadParameter uploadParameter = (UploadParameter) getArguments().getSerializable(UploadParameter.class.getSimpleName());
//            if (uploadParameter != null) {
//                uploadParameter.setStartDate(startDateStr);
//                uploadParameter.setEndDate(endDateStr);
//
//                String title = titleView.getText().toString().trim();
//                if (TextUtils.isEmpty(title)) {
//                    TipMsgHelper.ShowLMsg(getActivity(), R.string.pls_input_title);
//                    return;
//                }
//                UIUtils.hideSoftKeyboard(getActivity());
//                uploadParameter.setFileName(title);
//                enterContactsPicker(uploadParameter);
//            }

        } else if (v.getId() == R.id.study_task_start_date_text) {
            UIUtils.hideSoftKeyboard(getActivity());
            DatePopupView stateDatePopView = new DatePopupView(getActivity(), startDateStr, new DatePopupView.OnDateChangeListener() {
                @Override
                public void onDateChange(String dateStr) {
                    if (!startDateStr.equals(dateStr)) {
                        startDateStr = dateStr;
                        startDateView.setText(dateStr);
                    }
                }
            });
            stateDatePopView.showAtLocation(getView(), Gravity.BOTTOM, 0, 0);
        } else if (v.getId() == R.id.study_task_end_date_text) {
            UIUtils.hideSoftKeyboard(getActivity());
            DatePopupView endDatePopView = new DatePopupView(getActivity(), endDateStr, new DatePopupView.OnDateChangeListener() {
                @Override
                public void onDateChange(String dateStr) {
                    if (!endDateStr.equals(dateStr)) {
                        endDateStr = dateStr;
                        endDateView.setText(dateStr);
                    }
                }
            });
            endDatePopView.showAtLocation(getView(), Gravity.BOTTOM, 0, 0);
        } else if (v.getId() == R.id.commit_task_view) {
            isCommit = !isCommit;
            updateTaskCommitView(isCommit);
            if(uploadParameter != null) {
                uploadParameter.setTaskType(!isCommit ? StudyTaskType.WATCH_HOMEWORK :
                        StudyTaskType.SUBMIT_HOMEWORK);
            }
        }else if (v.getId() == R.id.btn_bottom_ok){
            //
            boolean isOk = checkDate();
            if (!isOk) {
                return;
            }
            UploadParameter uploadParameter = (UploadParameter) getArguments().getSerializable(UploadParameter.class.getSimpleName());
            if (uploadParameter != null) {
                uploadParameter.setStartDate(startDateStr);
                uploadParameter.setEndDate(endDateStr);

                String title = titleView.getText().toString().trim();
                if (TextUtils.isEmpty(title)) {
                    TipMsgHelper.ShowLMsg(getActivity(), R.string.pls_input_title);
                    return;
                }
                UIUtils.hideSoftKeyboard(getActivity());
                uploadParameter.setFileName(title);
                enterContactsPicker(uploadParameter);
            }
        }else if (v.getId() == R.id.btn_bottom_cancel){
            //取消
            if (getActivity() != null) {
//                getActivity().finish();
                popStack();
            }
        }
    }
}
