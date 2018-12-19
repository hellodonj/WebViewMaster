package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.ExpandDataAdapter;
import com.galaxyschool.app.wawaschool.fragment.library.ExpandListViewHelper;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.ClassMemberDetail;
import com.galaxyschool.app.wawaschool.pojo.ContactsClassMemberInfo;
import com.galaxyschool.app.wawaschool.pojo.PublishClass;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.SchoolClassMemberDetail;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AirClassroomAddHostFragment extends ContactsExpandPickerFragment {
    public static final String TAG = AirClassroomAddHostFragment.class.getSimpleName();
    public static final String TEACHER_LIST_SELECT = "teacher_list_select";
    public static final String PUBLISH_OBJECT_DATA_LIST = "publish_object_data_list";
    private TextView headTitle, clear, confirm;
    private LinearLayout bottomLayout;
    private ImageView selectAllView;
    private List<ContactsClassMemberInfo> teachersList = new ArrayList<>();
    private List<PublishClass> publishClasses;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_airclass_addhost_list, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getIntent();
        initViews();
        initExpandView();
        loadData();
    }

    private void getIntent() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            teachersList = bundle.getParcelableArrayList(TEACHER_LIST_SELECT);
            publishClasses = (List<PublishClass>) bundle.getSerializable(PUBLISH_OBJECT_DATA_LIST);
        }
    }

    private void initViews() {
        headTitle = (TextView) findViewById(R.id.contacts_header_title);
        if (headTitle != null) {
            headTitle.setText(getString(R.string.add_host_no_point));
        }
        ImageView headLeftBtn = (ImageView) findViewById(R.id.contacts_header_left_btn);
        headLeftBtn.setOnClickListener(this);
        selectAllView = (ImageView) findViewById(R.id.contacts_select_all_icon);
        if (selectAllView != null) {
            selectAllView.setSelected(false);
            selectAllView.setOnClickListener(this);
        }
        TextView tvAllText = (TextView) findViewById(R.id.contacts_select_all_title);
        if (tvAllText != null) {
            tvAllText.setOnClickListener(this);
        }
        bottomLayout = (LinearLayout) findViewById(R.id.contacts_picker_bar_layout);
        if (bottomLayout != null) {
            clear = (TextView) findViewById(R.id.contacts_picker_clear);
            if (clear != null) {
                clear.setEnabled(false);
                clear.setOnClickListener(this);
            }
            confirm = (TextView) findViewById(R.id.contacts_picker_confirm);
            if (confirm != null) {
                confirm.setEnabled(true);
                confirm.setOnClickListener(this);
            }
        }
    }

    private void loadData() {
        JSONArray dataArray = new JSONArray();
        JSONObject dataObject = null;
        for (int i = 0, len = publishClasses.size(); i < len; i++) {
            PublishClass publishClass = publishClasses.get(i);
            dataObject = new JSONObject();
            dataObject.put("SchoolId", publishClass.getSchoolId());
            dataObject.put("SchoolName", publishClass.getSchoolName());
            dataObject.put("ClassId", publishClass.getClassId());
            dataObject.put("ClassName", publishClass.getClassName());
            dataObject.put("Role", publishClass.getRole());
            dataArray.add(dataObject);
        }
        Map<String, Object> param = new HashMap<>();
        param.put("SchoolClassList", dataArray);
        DefaultDataListener listener = new DefaultDataListener<DataModelResult>(DataModelResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                super.onSuccess(jsonString);
                DataModelResult result = getResult();
                if (result != null && result.isSuccess()) {
                    List<SchoolClassMemberDetail> details = JSONObject.parseArray(result
                            .getModel().getData().toString(), SchoolClassMemberDetail.class);
                    if (details != null && details.size() > 0) {
                        configDiffData(details);
                    }
                }
            }
        };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_CLASS_MEMBER_LIST_BASE_URL, param, listener);
    }

    /**
     * 配置相关的数据
     *
     * @param list
     */
    private void configDiffData(List<SchoolClassMemberDetail> list) {
        List<SchoolClassMemberDetail> details = new ArrayList<>();
        //合并数据 以及 过滤数据
        for (int i = 0, len = list.size(); i < len; i++) {
            SchoolClassMemberDetail detail = list.get(i);
            if (detail != null) {
                List<ClassMemberDetail> memberDetails = detail.getMemberList();
                List<ClassMemberDetail> screenMemberData = new ArrayList<>();
                for (int j = 0, length = memberDetails.size(); j < length; j++) {
                    ClassMemberDetail member = memberDetails.get(j);
                    member.setClassName(detail.getClassName());
                    member.setSchoolId(detail.getSchoolId());
                    member.setClassId(detail.getClassId());
                    member.setClassHeadPicUrl(detail.getClassHeadPicUrl());
                    if (member.getRole() == RoleType.ROLE_TYPE_TEACHER) {
                        screenMemberData.add(member);
                    }
                }
                detail.setMemberList(screenMemberData);
                if (details.size() == 0) {
                    details.add(detail);
                } else {
                    boolean flag = false;
                    int position = 0;
                    for (int m = 0; m < details.size(); m++) {
                        SchoolClassMemberDetail tempData = details.get(m);
                        if (tempData.getSchoolId().equals(detail.getSchoolId())) {
                            flag = true;
                            position = m;
                            break;
                        }
                    }
                    if (flag) {
                        List<ClassMemberDetail> theSameSchoolClass = details.get(position).getMemberList();
                        theSameSchoolClass.addAll(detail.getMemberList());
                    } else {
                        details.add(detail);
                    }
                }
            }
        }
        //如果作者是自己 是选中的状态 其他老师默认选中的状态
        for (int i = 0, len = teachersList.size(); i < len; i++) {
            ContactsClassMemberInfo teacherMember = teachersList.get(i);
            for (int j = 0, length = details.size(); j < length; j++) {
                SchoolClassMemberDetail memberDetail = details.get(j);
                List<ClassMemberDetail> memberDetails = memberDetail.getMemberList();
                for (int m = 0; m < memberDetails.size(); m++) {
                    ClassMemberDetail classMemberDetail = memberDetails.get(m);
                    if (TextUtils.equals(getMemeberId(), classMemberDetail.getMemberId())) {
                        classMemberDetail.setIsSelect(true);
                        classMemberDetail.setIsMySelf(true);
                    } else if (TextUtils.equals(classMemberDetail.getMemberId(), teacherMember.getMemberId())
                            && TextUtils.equals(classMemberDetail.getClassId(), teacherMember.getClassId())) {
                        classMemberDetail.setIsSelect(true);
                    }
                }
            }
        }


        if (details.size() > 0) {
            getCurrListViewHelper().setData(details);
            if (getCurrListViewHelper().hasData()) {
                getCurrListView().expandGroup(0);
            }
            notifyPickerBar();
        }
    }

    private void initExpandView() {
        PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        if (pullToRefreshView != null) {
            pullToRefreshView.setRefreshEnable(false);
        }
        ExpandableListView listView = (ExpandableListView) findViewById(
                R.id.contacts_list_view);
        if (listView != null) {
            listView.setGroupIndicator(null);
            ExpandDataAdapter dataAdapter = new ExpandDataAdapter(getActivity(),
                    null, R.layout.contacts_expand_list_group_item_mixed,
                    R.layout.contacts_expand_list_child_item_with_selector) {
                @Override
                public int getChildrenCount(int groupPosition) {
                    SchoolClassMemberDetail schoolDetail = (SchoolClassMemberDetail) getGroup(groupPosition);
                    if (schoolDetail != null && schoolDetail.getMemberList().size() > 0) {
                        return schoolDetail.getMemberList().size();
                    }
                    return 0;
                }

                @Override
                public Object getChild(int groupPosition, int childPosition) {
                    SchoolClassMemberDetail schoolDetail = (SchoolClassMemberDetail) getGroup(groupPosition);
                    return schoolDetail.getMemberList().get(childPosition);
                }

                @Override
                public View getChildView(int groupPosition, int childPosition,
                                         boolean isLastChild, View convertView, ViewGroup parent) {
                    View view = super.getChildView(groupPosition, childPosition, isLastChild, convertView, parent);
                    ClassMemberDetail data = (ClassMemberDetail) getChild(groupPosition, childPosition);
                    if (data == null) {
                        return view;
                    }
                    ImageView imageView = (ImageView) view.findViewById(R.id.contacts_item_icon);
                    if (imageView != null) {
                        getThumbnailManager().displayUserIconWithDefault(data.getClassHeadPicUrl(),
                                imageView, R.drawable.default_group_icon);
                    }
                    TextView textView = (TextView) view.findViewById(R.id.contacts_item_title);
                    if (textView != null) {
                        String userName = data.getRealName();
                        if (TextUtils.isEmpty(userName)) {
                            userName = data.getNickname();
                        }
                        textView.setText(data.getClassName() + userName);
                    }
                    imageView = (ImageView) view.findViewById(R.id.contacts_item_selector);
                    if (imageView != null) {
                        imageView.setVisibility(View.VISIBLE);
                        imageView.setSelected(data.isSelect());
                    }
                    return view;
                }

                @Override
                public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
                    View view = super.getGroupView(groupPosition, isExpanded,
                            convertView, parent);
                    SchoolClassMemberDetail data = (SchoolClassMemberDetail) getGroup(groupPosition);
                    if (data == null) {
                        return view;
                    }
                    ImageView imageView = (ImageView) view.findViewById(R.id.contacts_item_icon);
                    if (imageView != null) {
                        imageView.setVisibility(View.VISIBLE);
                        getThumbnailManager().displayUserIconWithDefault(AppSettings.getFileUrl
                                (data.getSchoolLogoUrl()), imageView, R.drawable.default_school_icon);
                    }
                    TextView textView = (TextView) view.findViewById(R.id.contacts_item_title);
                    if (textView != null) {
                        textView.setText(data.getSchoolName());
                    }
                    imageView = (ImageView) view.findViewById(R.id.contacts_item_arrow);
                    if (imageView != null)
                        imageView.setImageResource(isExpanded ? R.drawable.list_exp_up : R.drawable.list_exp_down);
                    return view;
                }
            };
            ExpandListViewHelper listViewHelper = new ExpandListViewHelper(getActivity(),
                    listView, dataAdapter) {
                @Override
                public void loadData() {
                }

                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {
                    ClassMemberDetail data = (ClassMemberDetail) dataAdapter.getChild(groupPosition, childPosition);
                    if (!data.isMySelf()) {
                        data.setIsSelect(!data.isSelect());
                        notifyPickerBar();
                        getDataAdapter().notifyDataSetChanged();
                    }
                    return true;
                }

                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                    return false;
                }
            };
            listViewHelper.setData(null);
            setCurrListViewHelper(listView, listViewHelper);
        }
    }

    private void notifyPickerBar() {
        List<SchoolClassMemberDetail> list = getCurrListViewHelper().getData();
        if (list != null && list.size() > 0) {
            int count = 0;
            int totalCount = 0;
            int selectCount = 0;
            for (int i = 0; i < list.size(); i++) {
                SchoolClassMemberDetail info = list.get(i);
                if (info != null) {
                    List<ClassMemberDetail> details = info.getMemberList();
                    totalCount = totalCount + details.size();
                    for (int j = 0, len = details.size(); j < len; j++) {
                        ClassMemberDetail memberDetail = details.get(j);
                        if (memberDetail.isSelect()) {
                            ++selectCount;
                            if (!memberDetail.isMySelf()) {
                                ++count;
                            }
                        }
                    }
                }
            }
            if (selectCount == totalCount) {
                selectAllView.setSelected(true);
            } else {
                selectAllView.setSelected(false);
            }

            if (count > 0) {
                clear.setEnabled(true);
            } else {
                clear.setEnabled(false);
            }
        } else {
            clear.setEnabled(false);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contacts_header_left_btn) {
            getActivity().finish();
        } else if (v.getId() == R.id.contacts_picker_clear) {
            clearSelectData(true);
            notifyPickerBar();
        } else if (v.getId() == R.id.contacts_picker_confirm) {
            returnSelectData();
        } else if (v.getId() == R.id.contacts_select_all_icon || v.getId() == R.id.contacts_select_all_title) {
            selectAllData();
            notifyPickerBar();
        }
    }

    private void selectAllData() {
        if (selectAllView.isSelected()) {
            clearSelectData(true);
        } else {
            clearSelectData(false);
        }
        selectAllView.setSelected(!selectAllView.isSelected());
    }

    private void clearSelectData(boolean isClear) {
        List<SchoolClassMemberDetail> list = getCurrListViewHelper().getData();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                SchoolClassMemberDetail info = list.get(i);
                if (info != null) {
                    List<ClassMemberDetail> details = info.getMemberList();
                    for (int j = 0, len = details.size(); j < len; j++) {
                        ClassMemberDetail memberDetail = details.get(j);
                        if (isClear) {
                            if (memberDetail.isSelect() && !memberDetail.isMySelf()) {
                                memberDetail.setIsSelect(!isClear);
                            }
                        } else {
                            memberDetail.setIsSelect(true);
                        }
                    }
                }
            }
            getCurrListViewHelper().getDataAdapter().notifyDataSetChanged();
        }
    }

    private void returnSelectData() {
        List<SchoolClassMemberDetail> list = getCurrListViewHelper().getData();
        List<ContactsClassMemberInfo> selectList = new ArrayList<>();
        for (int i = 0, len = list.size(); i < len; i++) {
            SchoolClassMemberDetail schoolClassMemberDetail = list.get(i);
            List<ClassMemberDetail> classMemberDetails = schoolClassMemberDetail.getMemberList();
            for (int j = 0, length = classMemberDetails.size(); j < length; j++) {
                ClassMemberDetail memberDetail = classMemberDetails.get(j);
                if (memberDetail.isSelect()) {
                    ContactsClassMemberInfo info = new ContactsClassMemberInfo();
                    info.setMemberId(memberDetail.getMemberId());
                    info.setRealName(memberDetail.getRealName());
                    info.setNickname(memberDetail.getNickname());
                    info.setClassId(memberDetail.getClassId());
                    info.setHeadPicUrl(memberDetail.getHeadPicUrl());
                    info.setSchoolId(memberDetail.getSchoolId());
                    if (TextUtils.equals(getMemeberId(), info.getMemberId())) {
                        selectList.add(0, info);
                    } else {
                        selectList.add(info);
                    }
                }
            }
        }
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("teacherHost", (ArrayList<? extends Parcelable>) selectList);
        intent.putExtras(bundle);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }
}
