package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.ContactsSelectClassHeadTeacherActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.pojo.ContactsClassMemberInfo;
import com.galaxyschool.app.wawaschool.pojo.HeadMasterInfoList;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;

import java.util.List;

/**
 * 移除通讯录的界面
 */

public class RemoveAddressBookFragment extends ContactsListFragment {
    public static String TAG = RemoveAddressBookFragment.class.getSimpleName();
    public static String HEADMASTER_CLASSMAIL_LIST = "headMaster_classMail_list";
    public static String HEADMASTER_MEMBER_INFO = "headMaster_member_info";
    private TextView titleTextV;
    private ImageView backImageV;
    private TextView removeHintTextV;
    private String schoolId;
    private List<HeadMasterInfoList> classMailList;
    private ContactsClassMemberInfo memberInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_remove_address_book, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadIntentData();
        initViews();
        initListView();
        initData();
    }

    private void loadIntentData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            this.schoolId = bundle.getString(GroupContactsListFragment.Constants.EXTRA_CONTACTS_SCHOOL_ID);
            this.classMailList = (List<HeadMasterInfoList>) bundle.getSerializable(HEADMASTER_CLASSMAIL_LIST);
            this.memberInfo = bundle.getParcelable(HEADMASTER_MEMBER_INFO);
        }
    }

    private void initViews() {
        titleTextV = (TextView) findViewById(R.id.contacts_header_title);
        if (titleTextV != null) {
            titleTextV.setText(getString(R.string.str_remove_person_of_address));
        }
        backImageV = (ImageView) findViewById(R.id.contacts_header_left_btn);
        if (backImageV != null) {
            backImageV.setOnClickListener(this);
        }
        removeHintTextV = (TextView) findViewById(R.id.tv_remove_hint);
        if (removeHintTextV != null && memberInfo != null){
            String teacherName = memberInfo.getNoteName();
            String text = getString(R.string.str_remove_address_book_prompt);
            Spannable wordToSpan = new SpannableString(teacherName + text);
            wordToSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color
                    .text_green)),0,teacherName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            removeHintTextV.setText(wordToSpan);
        }
    }

    private void initData() {
        if (classMailList != null) {
            getCurrAdapterViewHelper().setData(classMailList);
        }
    }


    private void initListView() {
        ListView listView = (ListView) findViewById(R.id.list_View);
        if (listView != null) {
            AdapterViewHelper gridViewHelper = new AdapterViewHelper(getActivity(),
                    listView, R.layout.remove_address_listview_item) {
                @Override
                public void loadData() {
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    if (view == null) {
                        return view;
                    }
                    final HeadMasterInfoList data = (HeadMasterInfoList) getData().get(position);
                    if (data == null) return view;
                    TextView classNameTextV = (TextView) view.findViewById(R.id.tv_class_name);
                    if (classNameTextV != null) {
                        classNameTextV.setText(data.getClassMailName());
                    }
                    ImageView classThumbnail = (ImageView) view.findViewById(R.id.iv_class_thumbnail);
                    if (classThumbnail != null){
                        getThumbnailManager().displayUserIconWithDefault(
                                AppSettings.getFileUrl(data.getHeadPicUrl()), classThumbnail,
                                R.drawable.default_class_icon);
                    }

                    TextView changeRoleTextV = (TextView) view.findViewById(R.id.tv_show_change_headmaster);
                    if (changeRoleTextV != null) {
                        if (data.isSelected()) {
                            changeRoleTextV.setText(R.string.str_already_change_headMaster);
                            changeRoleTextV.setTextColor(getResources().getColor(R.color.darkgray));
                            changeRoleTextV.setBackgroundResource(R.drawable.shape_bg_dark_gray);
                        } else {
                            changeRoleTextV.setText(R.string.str_change_headMaster);
                            changeRoleTextV.setTextColor(getResources().getColor(R.color.text_green));
                            changeRoleTextV.setBackgroundResource(R.drawable.shape_bg_green);
                        }
                        changeRoleTextV.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!data.isSelected()) {
                                    changeHeadMasterRole(data);
                                }
                            }
                        });
                    }
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;
                    view.setTag(holder);
                    return view;
                }
            };
            setCurrAdapterViewHelper(listView, gridViewHelper);
        }
    }

    private void changeHeadMasterRole(HeadMasterInfoList data) {
        Bundle args = new Bundle();
        args.putString(ContactsSelectClassHeadTeacherActivity.EXTRA_SCHOOL_ID, this.schoolId);
        args.putString(ContactsSelectClassHeadTeacherActivity.EXTRA_CLASS_ID, data.getClassId());
        args.putString(ContactsSelectClassHeadTeacherActivity.EXTRA_CLASS_HEADTEACHER_ID, memberInfo.getMemberId());
        args.putString(ContactsSelectClassHeadTeacherActivity.EXTRA_CLASS_HEADTEACHER_NAME, memberInfo.getNoteName());
        Intent intent = new Intent(getActivity(), ContactsSelectClassHeadTeacherActivity.class);
        intent.putExtras(args);
        startActivityForResult(intent, ContactsSelectClassHeadTeacherActivity.REQUEST_CODE_SELECT_CLASS_HEADTEACHER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ContactsSelectClassHeadTeacherActivity.REQUEST_CODE_SELECT_CLASS_HEADTEACHER) {
            if (data != null) {
                boolean masterChanged = data.getBooleanExtra(ContactsSelectClassHeadTeacherFragment.Constants
                        .EXTRA_CLASS_HEADTEACHER_CHANGED, false);
                if (masterChanged) {
                    String classId = data.getStringExtra(ContactsSelectClassHeadTeacherFragment.Constants.EXTRA_CLASS_ID);
                    boolean flag = false;
                    for (int i = 0, len = classMailList.size(); i < len; i++) {
                        HeadMasterInfoList infoList = classMailList.get(i);
                        if (!infoList.isSelected() && TextUtils.equals(infoList.getClassId(), classId)) {
                            infoList.setSelected(true);
                        }
                        if (!infoList.isSelected()) {
                            flag = true;
                        }
                    }
                    if (flag) {
                        getCurrAdapterViewHelper().update();
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra(ContactsSelectClassHeadTeacherFragment.Constants
                                .EXTRA_CLASS_HEADTEACHER_CHANGED, true);
                        intent.putExtra(HEADMASTER_MEMBER_INFO,memberInfo);
                        getActivity().setResult(Activity.RESULT_OK, intent);
                        getActivity().finish();
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contacts_header_left_btn) {
            getActivity().finish();
        }
    }
}
