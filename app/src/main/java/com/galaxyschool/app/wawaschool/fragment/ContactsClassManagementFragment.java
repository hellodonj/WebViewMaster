package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.ContactsClassRequestListActivity;
import com.galaxyschool.app.wawaschool.ContactsModifyClassNameActivity;
import com.galaxyschool.app.wawaschool.ContactsSelectClassHeadTeacherActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfoResult;
import com.lqwawa.apps.views.switchbutton.SwitchButton;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;

import java.util.HashMap;
import java.util.Map;

public class ContactsClassManagementFragment extends BaseFragment
        implements View.OnClickListener {

    public static final String TAG = ContactsClassManagementFragment.class.getSimpleName();

    public interface Constants {
        int REQUEST_CODE_MODIFY_CLASS_ATTRIBUTES = 6102;

        String EXTRA_SCHOOL_ID = "schoolId";
        String EXTRA_SCHOOL_NAME = "schoolName";
        String EXTRA_CLASS_ID = "classId";
        String EXTRA_CLASS_NAME = "className";
        String EXTRA_CLASS_MAILID = "class_mailId";
        String EXTRA_CLASS_HEADTEACHER_ID = "headTeacherId";
        String EXTRA_CLASS_HEADTEACHER_NAME = "headTeacherName";
        String EXTRA_CLASS_STATUS = "classStatus";
        String EXTRA_CLASS_ATTRIBUTES_CHANGED = "attributesChanged";
        String EXTRA_CLASS_NAME_CHANGED = "nameChanged";
        String EXTRA_CLASS_HEADTEACHER_CHANGED = "headTeacherChanged";
        String EXTRA_CLASS_STATUS_CHANGED = "statusChanged";

        int CLASS_STATUS_HISTORY = 0;
        int CLASS_STATUS_PRESENT = 1;
        int CLASS_STATUS_END_THE_LECTURE = 3;//结束授课
    }
    private LinearLayout classAttrLayout;
    private String schoolId;
    private String schoolName;
    private String classId;
    private String className;
    private String headTeacherId;
    private String headTeacherName;
    private int classStatus;
    private String classMailId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.contacts_class_management, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        Bundle args = getArguments();
        this.schoolId = args.getString(Constants.EXTRA_SCHOOL_ID);
        this.schoolName = args.getString(Constants.EXTRA_SCHOOL_NAME);
        this.classId = args.getString(Constants.EXTRA_CLASS_ID);
        this.className = args.getString(Constants.EXTRA_CLASS_NAME);
        this.headTeacherId = args.getString(Constants.EXTRA_CLASS_HEADTEACHER_ID);
        this.headTeacherName = args.getString(Constants.EXTRA_CLASS_HEADTEACHER_NAME);
        this.classStatus = args.getInt(Constants.EXTRA_CLASS_STATUS);
        this.classMailId = args.getString(Constants.EXTRA_CLASS_MAILID);

        if (args != null) {
            this.schoolId = args.getString(Constants.EXTRA_SCHOOL_ID);
            this.schoolName = args.getString(Constants.EXTRA_SCHOOL_NAME);
            this.classId = args.getString(Constants.EXTRA_CLASS_ID);
            this.className = args.getString(Constants.EXTRA_CLASS_NAME);
            this.headTeacherId = args.getString(Constants.EXTRA_CLASS_HEADTEACHER_ID);
            this.headTeacherName = args.getString(Constants.EXTRA_CLASS_HEADTEACHER_NAME);
            this.classStatus = args.getInt(Constants.EXTRA_CLASS_STATUS);
            this.classMailId = args.getString(Constants.EXTRA_CLASS_MAILID);
        }
        initViews();
    }

    private void initViews() {
        ImageView imageView = (ImageView) findViewById(R.id.contacts_header_left_btn);
        if (imageView != null) {
            imageView.setOnClickListener(this);
        }

        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            textView.setOnClickListener(this);
            textView.setText(R.string.class_management);
        }

        View itemView = findViewById(R.id.class_name_attr);
        if (itemView != null) {
            textView = (TextView) itemView.findViewById(R.id.contacts_attribute_key);
            if (textView != null) {
                textView.setText(R.string.class_name);
            }
            textView = (TextView) itemView.findViewById(R.id.contacts_attribute_value);
            if (textView != null) {
                textView.setText(this.className);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    modifyClassName();
                }
            });
            this.classAttrLayout = (LinearLayout) itemView;
        }
        itemView = findViewById(R.id.class_headteacher_attr);
        if (itemView != null) {
            textView = (TextView) itemView.findViewById(R.id.contacts_attribute_key);
            if (textView != null) {
                textView.setText(R.string.change_headteacher);
            }
            textView = (TextView) itemView.findViewById(R.id.contacts_attribute_value);
            if (textView != null) {
                textView.setText(this.headTeacherName);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectClassHeadTeacher();
                }
            });
        }

        //加入审批
        itemView = findViewById(R.id.class_join_approve_attr);
        if (itemView != null) {
            textView = (TextView) itemView.findViewById(R.id.contacts_attribute_key);
            if (textView != null) {
                textView.setText(R.string.class_request);
            }
            textView = (TextView) itemView.findViewById(R.id.contacts_attribute_value);
            if (textView != null) {
                textView.setText("");
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enterJoinApproveDetail();
                }
            });
        }

        FrameLayout classStatusLayout = (FrameLayout) findViewById(R.id.class_status_layout);
        if (classStatusLayout != null) {
            //隐藏班级状态栏的显示
            classStatusLayout.setVisibility(View.GONE);
        }

        textView = (TextView) findViewById(R.id.class_status);
        if (textView != null) {
            textView.setText(R.string.class_status);
        }
        SwitchButton button = (SwitchButton) findViewById(R.id.class_status_btn);
        if (button != null) {
            button.setChecked(this.classStatus == Constants.CLASS_STATUS_PRESENT);
            button.setOnCheckedChangeListener(
                    new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    TextView textView = (TextView) findViewById(R.id.present_class);
//                    if (textView != null) {
//                        textView.setEnabled(isChecked);
//                    }
//                    textView = (TextView) findViewById(R.id.history_class);
//                    if (textView != null) {
//                        textView.setEnabled(!isChecked);
//                    }
                            changeClassStatus(isChecked ?
                                    Constants.CLASS_STATUS_PRESENT : Constants.CLASS_STATUS_HISTORY);
                        }
                    });
            textView = (TextView) findViewById(R.id.present_class);
            if (textView != null) {
                textView.setEnabled(button.isChecked());
            }
            textView = (TextView) findViewById(R.id.history_class);
            if (textView != null) {
                textView.setEnabled(!button.isChecked());
            }
        }
        loadSchoolInfo();
    }

    private void setViewVisibility(int visibility){
        if (classAttrLayout != null) {
            classAttrLayout.setVisibility(visibility);
        }
    }


    private void updateClassName(String newClassName) {
        this.className = newClassName;
        View itemView = findViewById(R.id.class_name_attr);
        if (itemView != null) {
            TextView textView = (TextView) itemView.findViewById(R.id.contacts_attribute_key);
            if (textView != null) {
                textView.setText(R.string.class_name);
            }
            textView = (TextView) itemView.findViewById(R.id.contacts_attribute_value);
            if (textView != null) {
                textView.setText(this.className);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    modifyClassName();
                }
            });
        }
    }

    private void updateClassHeadTeacher(String newHeadTeacherId,
                                        String newHeadTeacherName) {
        this.headTeacherId = newHeadTeacherId;
        this.headTeacherName = newHeadTeacherName;
        View itemView = findViewById(R.id.class_headteacher_attr);
        if (itemView != null) {
            TextView textView = (TextView) itemView.findViewById(R.id.contacts_attribute_key);
            if (textView != null) {
                textView.setText(R.string.change_headteacher);
            }
            textView = (TextView) itemView.findViewById(R.id.contacts_attribute_value);
            if (textView != null) {
                textView.setText(this.headTeacherName);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectClassHeadTeacher();
                }
            });
        }
    }

    private void updateClassStatus(int newClassStatus) {
        this.classStatus = newClassStatus;
        SwitchButton button = (SwitchButton) findViewById(R.id.class_status_btn);
        if (button != null) {
            button.setChecked(newClassStatus == Constants.CLASS_STATUS_PRESENT);
            TextView textView = (TextView) findViewById(R.id.present_class);
            if (textView != null) {
                textView.setEnabled(button.isChecked());
            }
            textView = (TextView) findViewById(R.id.history_class);
            if (textView != null) {
                textView.setEnabled(!button.isChecked());
            }
        }
    }

    private void loadSchoolInfo() {
        if (TextUtils.isEmpty(schoolId)){
            setViewVisibility(View.VISIBLE);
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("SchoolId", schoolId);
        params.put("VersionCode", 1);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.SUBSCRIBE_SCHOOL_INFO_URL,
                params,
                new DefaultListener<SchoolInfoResult>(SchoolInfoResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()) {
                            return;
                        }
                        SchoolInfo schoolInfo = getResult().getModel();
                        if (schoolInfo != null) {
                            if (schoolInfo.isOnlineSchool()){
                                setViewVisibility(View.GONE);
                            } else {
                                setViewVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
    }


    @Override
    public boolean onBackPressed() {
        super.onBackPressed();
        notifyChanges();
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        getActivity().setResult(getResultCode(), getResultData());
        getActivity().finish();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contacts_header_left_btn) {
            notifyChanges();
        }
    }

    private void notifyChanges() {
        boolean classNameChanged = !this.className.equals(
                getArguments().getString(Constants.EXTRA_CLASS_NAME));
        boolean classHeadTeacherChanged = !this.headTeacherId.equals(
                getArguments().getString(Constants.EXTRA_CLASS_HEADTEACHER_ID));
        boolean classStatusChanged = this.classStatus !=
                getArguments().getInt(Constants.EXTRA_CLASS_STATUS);

        boolean changed = false;
        if (classNameChanged || classHeadTeacherChanged || classStatusChanged) {
            changed = true;
        }
        if (changed) {
            Bundle data = new Bundle();
            data.putString(Constants.EXTRA_CLASS_ID, this.classId);
            data.putBoolean(Constants.EXTRA_CLASS_ATTRIBUTES_CHANGED, changed);
            if (classNameChanged) {
                data.putBoolean(Constants.EXTRA_CLASS_NAME_CHANGED,
                        classNameChanged);
                data.putString(Constants.EXTRA_CLASS_NAME, this.className);
            }
            if (classHeadTeacherChanged) {
                data.putBoolean(Constants.EXTRA_CLASS_HEADTEACHER_CHANGED,
                        classHeadTeacherChanged);
                data.putString(Constants.EXTRA_CLASS_HEADTEACHER_ID,
                        this.headTeacherId);
                data.putString(Constants.EXTRA_CLASS_HEADTEACHER_NAME,
                        this.headTeacherName);
            }
            if (classStatusChanged) {
                data.putBoolean(Constants.EXTRA_CLASS_STATUS_CHANGED,
                        classStatusChanged);
                data.putInt(Constants.EXTRA_CLASS_STATUS, this.classStatus);
            }
            Intent intent = new Intent();
            intent.putExtras(data);
            setResult(Activity.RESULT_OK, intent);
        }
        finish();
    }

    private void changeClassStatus(int newClassStatus) {
        ClassStatusParams classParams = new ClassStatusParams();
        classParams.setClassId(this.classId);
        int state = 0;
        if (newClassStatus == Constants.CLASS_STATUS_PRESENT) {
            state = ClassStatusParams.STATE_PRESENT;
        } else {
            state = ClassStatusParams.STATE_HISTORY;
        }
        classParams.setClassOperateState(state);
        if (getUserInfo() == null) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("VersionCode", "1");
        params.put("OperateType", "State");
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("SchoolId", this.schoolId);
        params.put("NewModel", classParams);
        DefaultListener listener =
                new DefaultListener<ModelResult>(ModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        ModelResult result = getResult();
                        if (result == null || !result.isSuccess()) {
//                    TipsHelper.showToast(getActivity(), R.string.modify_class_status_failed);
                            updateClassStatus(classStatus);
                            return;
                        }
                        TipsHelper.showToast(getActivity(), R.string.modify_class_status_success);
                        updateClassStatus((Integer) getTarget());
                        //关注/取消关注成功后，向校园空间发广播
                        MySchoolSpaceFragment.sendBrocast(getActivity());
                        //将开课班改为历史班之后结束当前的界面
                        notifyChanges();
                    }
                };
        listener.setTarget(newClassStatus);
        listener.setShowLoading(true);
        postRequest(ServerUrl.CONTACTS_MODIFY_CLASS_ATTRIBUTES_URL, params, listener);
    }

    private void modifyClassName() {
        Bundle args = new Bundle();
        args.putString(ContactsModifyClassNameActivity.EXTRA_SCHOOL_ID, this.schoolId);
        args.putString(ContactsModifyClassNameActivity.EXTRA_SCHOOL_NAME, this.schoolName);
        args.putString(ContactsModifyClassNameActivity.EXTRA_CLASS_ID, this.classId);
        args.putString(ContactsModifyClassNameActivity.EXTRA_CLASS_NAME, this.className);
        args.putBoolean(ContactsModifyClassNameActivity.EXTRA_FROM_MODIFY_CLASS_NAME,true);
        Intent intent = new Intent(getActivity(),
                ContactsModifyClassNameActivity.class);
        intent.putExtras(args);
        startActivityForResult(intent,
                ContactsModifyClassNameActivity.REQUEST_CODE_MODIFY_CLASS_NAME);
    }

    private void selectClassHeadTeacher() {
        Bundle args = new Bundle();
        args.putString(ContactsSelectClassHeadTeacherActivity.EXTRA_SCHOOL_ID, this.schoolId);
        args.putString(ContactsSelectClassHeadTeacherActivity.EXTRA_CLASS_ID, this.classId);
        args.putString(ContactsSelectClassHeadTeacherActivity.EXTRA_CLASS_HEADTEACHER_ID, this.headTeacherId);
        args.putString(ContactsSelectClassHeadTeacherActivity.EXTRA_CLASS_HEADTEACHER_NAME, this.headTeacherName);
        Intent intent = new Intent(getActivity(),
                ContactsSelectClassHeadTeacherActivity.class);
        intent.putExtras(args);
        startActivityForResult(intent,
                ContactsSelectClassHeadTeacherActivity.REQUEST_CODE_SELECT_CLASS_HEADTEACHER);
    }

    /**
     * 跳转到加入审批的详情界面
     */
    private void enterJoinApproveDetail(){
        Intent intent = new Intent(getActivity(), ContactsClassRequestListActivity.class);
        intent.putExtra(ContactsClassRequestListActivity.EXTRA_CLASS_ID,classMailId);
        startActivityForResult(intent,ContactsClassRequestListFragment
                .REQUEST_CODE_CONTACTS_CLASS_REQUEST_LIST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ContactsModifyClassNameActivity.
                    REQUEST_CODE_MODIFY_CLASS_NAME) {
                boolean changed = data.getBooleanExtra(
                        ContactsModifyClassNameActivity.EXTRA_CLASS_NAME_CHANGED, false);
                if (changed) {
                    updateClassName(data.getStringExtra(
                            ContactsModifyClassNameActivity.EXTRA_CLASS_NAME));
                }
            } else if (requestCode == ContactsSelectClassHeadTeacherActivity.
                    REQUEST_CODE_SELECT_CLASS_HEADTEACHER) {
                boolean changed = data.getBooleanExtra(
                        ContactsSelectClassHeadTeacherActivity.
                                EXTRA_CLASS_HEADTEACHER_CHANGED, false);
                if (changed) {
                    updateClassHeadTeacher(
                            data.getStringExtra(
                                    ContactsSelectClassHeadTeacherActivity.
                                            EXTRA_CLASS_HEADTEACHER_ID),
                            data.getStringExtra(
                                    ContactsSelectClassHeadTeacherActivity.
                                            EXTRA_CLASS_HEADTEACHER_NAME));
                    notifyChanges();
                }
            }
        }
    }

    public static final class ClassStatusParams
            extends ContactsClassCategorySelectorFragment.ClassParams {
        public static final int STATE_PRESENT = 1;
        public static final int STATE_HISTORY = 3;

        private int ClassOperateState; //1开课 2停课 3毕业

        public int getClassOperateState() {
            return ClassOperateState;
        }

        public void setClassOperateState(int classOperateState) {
            ClassOperateState = classOperateState;
        }
    }

}
