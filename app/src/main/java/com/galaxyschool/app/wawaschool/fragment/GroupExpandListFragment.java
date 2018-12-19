package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.duowan.mobile.netroid.NetroidError;
import com.galaxyschool.app.wawaschool.ClassDetailsActivity;
import com.galaxyschool.app.wawaschool.ContactsActivity;
import com.galaxyschool.app.wawaschool.ContactsClassRequestListActivity;
import com.galaxyschool.app.wawaschool.ContactsCreateClassActivity;
import com.galaxyschool.app.wawaschool.ContactsQrCodeDetailsActivity;
import com.galaxyschool.app.wawaschool.ContactsSearchClassActivity;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.SchoolSpaceActivity;
import com.galaxyschool.app.wawaschool.SubscribeSchoolListActivity;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.PrefsManager;
import com.galaxyschool.app.wawaschool.common.SharedPreferencesHelper;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.ContactsClassInfo;
import com.galaxyschool.app.wawaschool.pojo.ContactsNewRequestCountResult;
import com.galaxyschool.app.wawaschool.pojo.ContactsSchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.ContactsSchoolListResult;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.UserInfoResult;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.sortlistview.ClearEditText;
import com.galaxyschool.app.wawaschool.views.sortlistview.SideBar;
import com.galaxyschool.app.wawaschool.views.sortlistview.SortExpandDataAdapter;
import com.galaxyschool.app.wawaschool.views.sortlistview.SortExpandListViewHelper;
import com.galaxyschool.app.wawaschool.views.sortlistview.SortModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupExpandListFragment extends ContactsExpandListFragment {

    public static final String TAG = GroupExpandListFragment.class.getSimpleName();

    private TextView indicatorView;
    private ClearEditText searchBar;
    private ContactsSchoolListResult dataListResult;
    private List<SortModel> sortDataList;
    private ContactsClassInfo classInfo;

    private TextView keywordView;
    private String keyword = "";
    private boolean isFirstIn=true;
    private boolean isJoinClassItemAdded,isJoinApprovalItemAdded;
    private View classHeaderView,approvalHeaderView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.contacts_expand_sortlist, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        refreshData();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void refreshData(){
        loadViews();
        loadUserInfo();
    }

    private void initViews() {

        PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);

        ExpandableListView listView = (ExpandableListView) findViewById(
                R.id.contacts_list_view);
        if (listView != null) {
            listView.setGroupIndicator(null);
            if (listView.getHeaderViewsCount() <= 0) {
                boolean showSeparator = false;


                //关注的机构

                    View subscribedSchoolView = LayoutInflater.from(getActivity()).inflate(
                            R.layout.contacts_list_item_with_indicator, null);
                    ImageView subscribedSchoolImageView = (ImageView) subscribedSchoolView.
                            findViewById(R.id.contacts_item_icon);
                subscribedSchoolImageView.setImageResource(R.drawable.icon_concerned_institutions);
                    TextView subscribedSchoolTextView = (TextView) subscribedSchoolView.
                            findViewById(R.id.contacts_item_title);
                subscribedSchoolTextView.setText(R.string.has_subscribed_authority);
                    listView.addHeaderView(subscribedSchoolView);
                subscribedSchoolView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            enterSubscribeSchoolListActivity();
                        }
                    });
                    showSeparator = true;
            }
            SortExpandDataAdapter dataAdapter = new SortExpandDataAdapter(getActivity(),
                    null, R.layout.contacts_expand_list_group_item,
                    R.layout.contacts_expand_list_child_item_with_status, 0) {
                @Override
                public int getChildrenCount(int groupPosition) {
                    SortModel sortModel =
                            (SortModel) getGroup(groupPosition);
                    ContactsSchoolInfo data = (ContactsSchoolInfo) sortModel.getData();
                    if (data.getClassMailList() != null) {
                        return data.getClassMailList().size();
                    }
                    return 0;
                }

                @Override
                public Object getChild(int groupPosition, int childPosition) {
                    SortModel sortModel =
                            (SortModel) getGroup(groupPosition);
                    ContactsSchoolInfo data = (ContactsSchoolInfo) sortModel.getData();
                    return data.getClassMailList().get(childPosition);
                }

                @Override
                public View getChildView(final int groupPosition, int childPosition,
                                         boolean isLastChild, View convertView, ViewGroup parent) {
                    View view = super.getChildView(groupPosition, childPosition,
                            isLastChild, convertView, parent);
                    SortModel sortModel = (SortModel) getGroup(groupPosition);
                    final ContactsSchoolInfo schoolInfo = (ContactsSchoolInfo) sortModel.getData();
                    final ContactsClassInfo data = (ContactsClassInfo) getChild(
                            groupPosition, childPosition);
                    ImageView imageView = (ImageView) view.findViewById(R.id.contacts_item_icon);
                    if (imageView != null) {
                        getThumbnailManager().displayUserIconWithDefault(
                                AppSettings.getFileUrl(data.getHeadPicUrl()), imageView,
                                R.drawable.default_class_icon);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(data.isClass()){
                                    enterClassDetialActivity(data
                                            .getClassId(),ClassDetailsActivity
                                            .FROM_TYPE_CLASS_HEAD_PIC,true,schoolInfo
                                            .getSchoolName(),data.getId(),schoolInfo.getSchoolId
                                            (),data.getGroupId(), data.getIsHistory(),schoolInfo.isOnlineSchool());
                                }else{
                                    enterClassDetialActivity(data
                                            .getClassId(), ClassDetailsActivity
                                            .FROM_TYPE_TEACHER_CONTACT,true,schoolInfo
                                            .getSchoolName(),data.getId(),schoolInfo.getSchoolId
                                            (),data.getGroupId(),schoolInfo.isHasInspectAuth());
                                }
                            }
                        });

                    }
                    TextView textView = (TextView) view.findViewById(R.id.contacts_item_title);
                    if (textView != null) {
                        textView.setText(data.getClassMailName());
                    }
                    textView = (TextView) view.findViewById(R.id.contacts_item_status);
                    if (textView != null) {
                        if (data.isHeadTeacher()) {
                            textView.setText(R.string.header_teacher);
                        } else if (data.isTeacher()) {
                            textView.setText(R.string.teacher);
                        } else if (data.isStudent()) {
                            textView.setText(R.string.student);
                        } else if (data.isParent()) {
                            textView.setText(R.string.parent);
                        } else {
                            textView.setText(null);
                        }
                    }
                    textView = (TextView) view.findViewById(R.id.contacts_item_subtitle);
                    if (textView != null) {
                        textView.setVisibility(data.getIsHeader() ?
                                View.VISIBLE : View.GONE);
                    }
                    textView = (TextView) view.findViewById(R.id.contacts_item_description);
                    if (textView != null) {
                        if (data.isClass()) {
                            textView.setText(data.isHistory() ?
                                    R.string.history_class : R.string.present_class);
                            textView.setBackgroundColor(Color.parseColor(
                                    data.isHistory() ? "#e0888888" : "#e0009039"));
                            textView.setVisibility(View.VISIBLE);
                        } else {
                            textView.setText(null);
                            textView.setVisibility(View.GONE);
                        }
                    }

                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                        view.setTag(holder);
                    }
                    holder.data = data;
                    return view;
                }

                @Override
                public View getGroupView(int groupPosition, boolean isExpanded,
                            View convertView, ViewGroup parent) {
                    View view = super.getGroupView(groupPosition, isExpanded,
                            convertView, parent);
                    SortModel sortModel =
                            (SortModel) getGroup(groupPosition);
                    if (sortModel == null) {
                        return view;
                    }

                    final ContactsSchoolInfo data = (ContactsSchoolInfo) sortModel.getData();
                    ImageView imageView = (ImageView) view.findViewById(R.id.contacts_item_icon);
                    if (imageView != null) {
                        imageView.setVisibility(View.VISIBLE);
                        getThumbnailManager().displayUserIconWithDefault(
                                AppSettings.getFileUrl(data.getLogoUrl()), imageView,R.drawable
                                        .default_school_icon);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //跳转到机构详情
                                enterSchoolSpace(data);
                            }
                        });
                    }
                    TextView textView = (TextView) view.findViewById(R.id.contacts_item_title);
                    if (textView != null) {
                        textView.setText(data.getSchoolName());
                        textView.setTextColor(getResources().getColor(
                                R.color.text_green));
                    }
                    imageView = (ImageView) view.findViewById(R.id.contacts_item_arrow);
                    if (imageView != null) {
                        imageView.setImageResource(isExpanded ?
                                R.drawable.list_exp_up : R.drawable.list_exp_down);
                    }

                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                        view.setTag(holder);
                    }
                    holder.data = sortModel;
                    return view;
                }
            };
            SortExpandListViewHelper listViewHelper =
                    new SortExpandListViewHelper(getActivity(), listView, dataAdapter,
                    (SideBar) findViewById(R.id.contacts_sort_sidebar),
                    (TextView) findViewById(R.id.contacts_sort_tips)) {
                @Override
                public void loadData() {
//                    if (getUserInfo().isHeaderTeacher()) {
//                        loadNewClassRequestCount();
//                    } else {
                        loadUserInfo();
//                    }
                    loadGroups(keywordView.getText().toString());
                }

                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {
                    ViewHolder holder = (ViewHolder) v.getTag();
                    if (holder == null) {
                        return true;
                    }
                    SortModel sortModel =
                            (SortModel) getDataAdapter().getGroup(groupPosition);
                    enterGroupMembers((ContactsSchoolInfo) sortModel.getData(),
                            (ContactsClassInfo) holder.data);
                    classInfo = (ContactsClassInfo) holder.data;
                    return true;
                }

                @Override
                public boolean onGroupClick(ExpandableListView parent, View v,
                                            int groupPosition, long id) {
                    return false;
                }
            };
            this.searchBar = (ClearEditText) findViewById(R.id.search_keyword);
            this.searchBar.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            this.searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        hideSoftKeyboard(getActivity());
                        String keywords=v.getText().toString().trim();
                        if (!TextUtils.isEmpty(keywords)) {
                            loadGroups(v.getText().toString());
                        }
                        return true;
                    }
                    return false;
                }
            });
            this.searchBar.setOnClearClickListener(new ClearEditText.OnClearClickListener() {
                @Override
                public void onClearClick() {
                    keyword = "";
//                    if (getUserInfo().isHeaderTeacher()) {
//                        loadNewClassRequestCount();
//                    } else {
                        loadUserInfo();
//                    }
                    getCurrListViewHelper().clearData();
                    getPageHelper().clear();
                    loadGroups(keywordView.getText().toString());
//                    getCurrListViewHelper().setData(sortDataList);
//                    if (sortDataList != null && sortDataList.size() > 0) {
//                        getCurrListView().expandGroup(0);
//                    }
                }
            });
            this.searchBar.setInputType(InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

            this.keywordView=this.searchBar;

            View view = findViewById(R.id.search_btn);
            if (view != null) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideSoftKeyboard(getActivity());
                        String keywords=keywordView.getText().toString().trim();
                        if (!TextUtils.isEmpty(keywords)){
                            loadGroups(keywordView.getText().toString());
                        }
                    }
                });
                view.setVisibility(View.VISIBLE);
            }
            findViewById(R.id.contacts_search_bar_layout).setVisibility(View.VISIBLE);
//            this.searchBar.setHint(R.string.search_school);
//            this.searchBar.setFocusable(false);
//            this.searchBar.setFocusableInTouchMode(false);
//            this.searchBar.setInputType(InputType.TYPE_NULL);
//            this.searchBar.setKeyListener(null);
//            this.searchBar.setOnClickListener(this);

            listViewHelper.setSearchBar(this.searchBar, false);
            listViewHelper.showSideBar(false);
            listViewHelper.setData(null);

            setCurrListViewHelper(listView, listViewHelper);
        }
    }

    public  void enterClassDetialActivity(String classId, int fromType,
                                                boolean hasJoin, String schoolName, String
                                                        contactId, String schoolId, String
                                                        groupId,int classState,boolean isOnlineSchool) {
        Intent intent = new Intent(getActivity(), ClassDetailsActivity.class);
        Bundle args = new Bundle();
        args.putString(ClassDetailsActivity.GROUP_ID, groupId);
        args.putString(ClassDetailsActivity.SCHOOL_ID, schoolId);
        args.putString(ClassDetailsActivity.CLASS_ID, classId);
        args.putInt(ClassDetailsActivity.FROM_TYPE, fromType);
        args.putBoolean(ClassDetailsActivity.IS_JOIN_CLASS, hasJoin);
        args.putBoolean(ClassContactsDetailsFragment.Constants.IS_ONLINE_SCHOOL,isOnlineSchool);
        args.putString(ClassDetailsActivity.SCHOOL_NAME, schoolName);
        args.putString(ClassDetailsActivity.CONTACT_ID, contactId);
        args.putInt(ClassDetailsActivity.CLASS_STATE, classState);
        intent.putExtras(args);
        //班级二维码
        startActivityForResult(intent, ClassDetailsFragment.REQUEST_CODE_CLASS_DETAILS);
    }

    public  void enterClassDetialActivity(String classId,
                                          int fromType,
                                          boolean hasJoin,
                                          String schoolName,
                                          String contactId,
                                          String schoolId,
                                          String groupId,
                                          boolean isHasInspectAuth){
        Intent intent = new Intent(getActivity(), ClassDetailsActivity.class);
        Bundle args = new Bundle();
        args.putString(ClassDetailsActivity.GROUP_ID, groupId);
        args.putString(ClassDetailsActivity.SCHOOL_ID, schoolId);
        args.putString(ClassDetailsActivity.CLASS_ID, classId);
        args.putInt(ClassDetailsActivity.FROM_TYPE, fromType);
        args.putBoolean(ClassDetailsActivity.IS_JOIN_CLASS, hasJoin);
        args.putString(ClassDetailsActivity.SCHOOL_NAME, schoolName);
        args.putString(ClassDetailsActivity.CONTACT_ID, contactId);
        args.putBoolean(ClassDetailsActivity.EXTRA_CONTACTS_HAS_INSPECT_AUTH,isHasInspectAuth);
        intent.putExtras(args);
        //老师通讯录
        startActivityForResult(intent,ClassDetailsFragment.REQUEST_CODE_CLASS_DETAILS);
    }

    private void enterGroupQrCode(ContactsClassInfo data, String schoolName) {

        Bundle args = new Bundle();

        String title = getString(R.string.class_info);
        if(data.getType() == 1) {
            title = getString(R.string.contacts_info);
        }
        args.putString(ContactsQrCodeDetailsActivity.EXTRA_TITLE, title);
        args.putInt(ContactsQrCodeDetailsActivity.EXTRA_TARGET_TYPE,
                ContactsQrCodeDetailsActivity.TARGET_TYPE_CLASS);
        args.putString(ContactsQrCodeDetailsActivity.EXTRA_TARGET_ID,data.getId());
        args.putString(ContactsQrCodeDetailsActivity.EXTRA_TARGET_NAME,data.getClassMailName());
        args.putString(ContactsQrCodeDetailsActivity.EXTRA_TARGET_DESCRIPTION,schoolName);
        Intent intent = new Intent(getActivity(), ContactsQrCodeDetailsActivity.class);
        intent.putExtras(args);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {

        }
    }


    private void enterSubscribeSchoolListActivity() {

        Intent intent = new Intent(getActivity(),SubscribeSchoolListActivity.class);
        startActivity(intent);
    }

    private void loadViews() {
        isFirstIn=true;
        getPageHelper().clear();
        loadGroups(keywordView.getText().toString());
    }

    private void enterSchoolSpace(ContactsSchoolInfo data) {
        Bundle args = new Bundle();
        args.putString(SchoolSpaceActivity.EXTRA_SCHOOL_ID, data.getSchoolId());
        args.putString(SchoolSpaceActivity.EXTRA_SCHOOL_NAME, data.getSchoolName());
        Intent intent = new Intent(getActivity(), SchoolSpaceActivity.class);
        intent.putExtras(args);
        startActivity(intent);
    }

    private void loadUserInfo() {
        if (getUserInfo() == null){
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("UserId", getUserInfo().getMemberId());
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.LOAD_USERINFO_URL,
                params,
                new DefaultListener<UserInfoResult>(UserInfoResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                if (getResult() == null || !getResult().isSuccess()
                        || getResult().getModel() == null) {
                    return;
                }
                UserInfo userInfo = getResult().getModel();
                userInfo.setMemberId(getUserInfo().getMemberId());
                getMyApplication().setUserInfo(userInfo);

                ExpandableListView listView = (ExpandableListView) findViewById(
                        R.id.contacts_list_view);
//                if (listView.getHeaderViewsCount() > 3) {
//                    return;
//                }

                if (Utils.isRealTeacher(userInfo.getSchoolList())) {
                    if (!isJoinClassItemAdded) {
                        isJoinClassItemAdded = true;
                        classHeaderView = LayoutInflater.from(getActivity()).inflate(
                                R.layout.contacts_list_item_with_indicator, null);
                        ImageView imageView = (ImageView) classHeaderView.findViewById(R.id.contacts_item_icon);
                        imageView.setImageResource(R.drawable.create_class_ico);
                        TextView textView = (TextView) classHeaderView.findViewById(R.id.contacts_item_title);
                        textView.setText(R.string.create_class);
                        listView.addHeaderView(classHeaderView);
                        classHeaderView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                enterCreateClass();
                            }
                        });
                    }
                }else {
                    //已经不是老师了
                    if (classHeaderView != null) {
                        listView.removeHeaderView(classHeaderView);
                        isJoinClassItemAdded = false;
                        classHeaderView = null;
                    }
                }
                if (getUserInfo().isHeaderTeacher()) {
                    loadNewClassRequestCount();
                    if (!isJoinApprovalItemAdded) {
                        isJoinApprovalItemAdded = true;
                        approvalHeaderView = LayoutInflater.from(getActivity()).inflate(
                                R.layout.contacts_list_item_with_indicator, null);
                        ImageView imageView = (ImageView) approvalHeaderView.findViewById(R.id.contacts_item_icon);
                        imageView.setImageResource(R.drawable.approve_class_ico);
                        TextView textView = (TextView) approvalHeaderView.findViewById(R.id.contacts_item_title);
                        textView.setText(R.string.class_request);
                        textView = (TextView) approvalHeaderView.findViewById(R.id.contacts_item_indicator);
                        indicatorView = textView;
                        listView.addHeaderView(approvalHeaderView);
                        approvalHeaderView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                enterClassRequests();
                            }
                        });
                    }
                }else {
                    //已经不是班主任了
                    if (approvalHeaderView != null) {
                        listView.removeHeaderView(approvalHeaderView);
                        isJoinApprovalItemAdded = false;
                        approvalHeaderView = null;
                    }
                }
            }
        });
    }

    private void loadGroups(String keyword) {
        if (!((MyApplication) getActivity().getApplication()).hasLogined()) {
            return;
        }

        keyword = keyword.trim();

        if (isFirstIn){

        }else {
            if (getCurrListViewHelper().hasData()){

            }else {

                if (TextUtils.isEmpty(keyword)) {
//                    return;
                }
            }
        }

        if (!keyword.equals(this.keyword)) {
            getCurrListViewHelper().clearData();
            getPageHelper().clear();
        }
        this.keyword = keyword;
        UIUtils.hideSoftKeyboard(getActivity());
        if (getUserInfo() == null){
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("Role", getUserInfo().getRoles());
        String appName=getApplicationName();
        if (!TextUtils.isEmpty(appName)){
            params.put("AppId",appName);
        }
        params.put("KeyWords", keyword);//按关键字搜索，本地搜索。

        DefaultPullToRefreshListener listener =
                new DefaultPullToRefreshListener<ContactsSchoolListResult>(
                        ContactsSchoolListResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                isFirstIn=false;
                if(getActivity() == null) {
                    return;
                }
                SharedPreferencesHelper.setString(getActivity(),
                        PrefsManager.PrefsItems.CONTACTS_SCHOOL_LIST_RESULT, jsonString);
                super.onSuccess(jsonString);
                ContactsSchoolListResult result = getResult();
                if (result == null || !result.isSuccess()
                        || result.getModel() == null) {
                    return;
                }
                sortDataList = updateViews(result);
            }

            @Override
            public void onError(NetroidError error) {
                isFirstIn=false;
                if(getActivity() == null) {
                    return;
                }
                super.onError(error);
                String jsonString = SharedPreferencesHelper.getString(
                        getActivity(), PrefsManager.PrefsItems.CONTACTS_SCHOOL_LIST_RESULT);
                if (TextUtils.isEmpty(jsonString)) {
                    return;
                }
                super.onSuccess(jsonString);
                ContactsSchoolListResult result = getResult();
                if (result == null || !result.isSuccess()
                        || result.getModel() == null) {
                    return;
                }
                sortDataList = updateViews(result);
            }
        };
        postRequest(ServerUrl.CONTACTS_CLASS_LIST_URL, params, listener);
    }

    public String getApplicationName() {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = getActivity().getApplicationContext().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(getActivity().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        String applicationName =
                (String) packageManager.getApplicationLabel(applicationInfo);
        return applicationName;
    }

    private void loadNewClassRequestCount() {
        if (!((MyApplication) getActivity().getApplication()).hasLogined()) {
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        DefaultListener listener =
                new DefaultListener<ContactsNewRequestCountResult>(
                        ContactsNewRequestCountResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                ContactsNewRequestCountResult result = getResult();
                if (result == null || !result.isSuccess()
                        || result.getModel() == null) {
                    return;
                }
                int count = result.getModel().getApplyClassNum();
                if (count > 0) {
                    String str = null;
                    if (count > 99) {
                        str = "99+";
                    } else {
                        str = String.valueOf(count);
                    }
                    if (indicatorView != null) {
                        indicatorView.setVisibility(View.VISIBLE);
                        indicatorView.setText(str);
                    }
                } else {
                    if (indicatorView != null) {
                        indicatorView.setVisibility(View.INVISIBLE);
                    }
                }
            }
        };
        postRequest(ServerUrl.CONTACTS_NEW_REQUEST_COUNT_URL, params, listener);
    }

    private List<SortModel> updateViews(ContactsSchoolListResult result) {
        List<ContactsSchoolInfo> list = result.getModel().getSchoolList();
        Utils.removeOnlineContactsSchoolInfo(list);
        List<SortModel> sortList = processDataList(list);
        if (sortList == null || sortList.size() <= 0){
            TipsHelper.showToast(getActivity(),
                    getString(R.string.no_data));
        }
        getCurrListViewHelper().setData(sortList);
        if (getCurrListViewHelper().hasData()) {
            getCurrListView().expandGroup(0);
        }
        dataListResult = result;
        return sortList;
    }

    private List<SortModel> processDataList(List<ContactsSchoolInfo> list) {
        if (list != null && list.size() > 0) {
            List<SortModel> sortList = new ArrayList<SortModel>();
            SortModel sortModel = null;
            for (ContactsSchoolInfo obj : list) {
                sortModel = new SortModel();
                sortModel.setName(obj.getSchoolName());
                sortModel.setData(obj);
                sortList.add(sortModel);
            }
            return sortList;
        }
        return null;
    }

    private void enterGroupMembers(ContactsSchoolInfo schoolInfo, ContactsClassInfo classInfo) {
        Bundle args = null;
        if (classInfo.isClass() || classInfo.isSchool()) {
            args = new Bundle();
            args.putInt(ContactsActivity.EXTRA_CONTACTS_TYPE, classInfo.getType());
            args.putString(ContactsActivity.EXTRA_CONTACTS_ID, classInfo.getId());
            args.putString(ContactsActivity.EXTRA_CONTACTS_NAME, classInfo.getClassMailName());
            args.putString(ContactsActivity.EXTRA_CONTACTS_SCHOOL_ID, classInfo.getLQ_SchoolId());
            args.putString(ContactsActivity.EXTRA_CONTACTS_SCHOOL_NAME, schoolInfo.getSchoolName());
            args.putString(ContactsActivity.EXTRA_CONTACTS_CLASS_ID, classInfo.getClassId());
            args.putString(ContactsActivity.EXTRA_CONTACTS_CLASS_NAME, classInfo.getClassMailName());
            args.putString(ContactsActivity.EXTRA_CONTACTS_HXGROUP_ID, classInfo.getGroupId());
            args.putString("from",GroupExpandListFragment.TAG);
        }
        if (schoolInfo.isOnlineSchool()){
            args.putBoolean(ClassContactsDetailsFragment.Constants.IS_ONLINE_SCHOOL, true);
        }
        if (classInfo.isClass()) {
            args.putInt(ClassContactsDetailsFragment.Constants.EXTRA_CLASS_STATUS,
                    classInfo.getIsHistory());
        } else if (classInfo.isSchool()){
            args.putBoolean(ContactsActivity.EXTRA_CONTACTS_HAS_INSPECT_AUTH, schoolInfo.isHasInspectAuth());
        }
        if (args != null) {
            Intent intent = new Intent(getActivity(), ContactsActivity.class);
            intent.putExtras(args);
            if (classInfo.isClass()) {
                startActivityForResult(intent,
                        ClassContactsDetailsFragment.Constants.REQUEST_CODE_CLASS_DETAILS);
            } else {
                startActivity(intent);
            }
        }
    }

    private void enterSearchClass() {
        Intent intent = new Intent(getActivity(), ContactsSearchClassActivity.class);
        startActivity(intent);
    }

    private void enterClassRequests() {
        Intent intent = new Intent(getActivity(), ContactsClassRequestListActivity.class);
        startActivityForResult(intent,ContactsClassRequestListFragment
                .REQUEST_CODE_CONTACTS_CLASS_REQUEST_LIST);
    }

    private void enterCreateClass() {
        Intent intent = new Intent(getActivity(), ContactsCreateClassActivity.class);
        startActivityForResult(intent,
                ContactsCreateClassActivity.REQUEST_CODE_CREATE_CLASS);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.search_keyword) {
            enterSearchClass();
        } else {
            super.onClick(v);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ContactsCreateClassActivity.REQUEST_CODE_CREATE_CLASS) {
                boolean classCreated = data.getBooleanExtra(
                        ContactsCreateClassActivity.EXTRA_CLASS_CREATED, false);
                if (classCreated) {
                    //创建班级需要刷新
                    refreshData();
                    //通过广播的方式发送到校园空间去更新班级的信息
                    Intent broadIntent=new Intent();
                    String action=MySchoolSpaceFragment.ACTION_LOAD_DATA;
                    broadIntent.setAction(action);
                    getActivity().sendBroadcast(broadIntent);
                }
            } else if (requestCode ==
                    ClassContactsDetailsFragment.Constants.REQUEST_CODE_CLASS_DETAILS) {
                //班级内容改变包含：班级名称、班级状态、班主任变化的情况。
                if (ClassContactsDetailsFragment.hasClassContentChanged()) {
                    ClassContactsDetailsFragment.setHasClassContentChanged(false);
                    //刷新页面
                    refreshData();
                }
            }
        }

        //刷新策略
        if (data == null){
            if (requestCode == ContactsClassRequestListFragment
                    .REQUEST_CODE_CONTACTS_CLASS_REQUEST_LIST){
                //审批消息处理后需要刷新
                if (ContactsClassRequestListFragment.hasMessageHandled()){
                    ContactsClassRequestListFragment.setHasMessageHandled(false);
                    //刷新页面
                    refreshData();
                }
            }else if(requestCode == ClassDetailsFragment.REQUEST_CODE_CLASS_DETAILS){
                //从班级二维码页面返回
                //班级详情页面数据改变（本质是ClassContactsDetailsFragment班级改变）
                if (ClassDetailsFragment.hasContentChanged()){
                    ClassDetailsFragment.setHasContentChanged(false);
                    //刷新页面
                    refreshData();
                }
            }else {
                //其他情况,退出班级等。
                if (ClassContactsDetailsFragment.hasClassContentChanged()) {
                    ClassContactsDetailsFragment.setHasClassContentChanged(false);
                    //刷新页面
                    refreshData();
                }
            }
        }
    }

}
