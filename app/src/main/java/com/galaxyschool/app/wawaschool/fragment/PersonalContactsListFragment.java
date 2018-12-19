package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.*;

import com.duowan.mobile.netroid.NetroidError;
import com.galaxyschool.app.wawaschool.*;
import com.galaxyschool.app.wawaschool.chat.activity.ChatActivity;
import com.galaxyschool.app.wawaschool.chat.applib.controller.HXSDKHelper;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.PrefsManager;
import com.galaxyschool.app.wawaschool.common.SharedPreferencesHelper;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.ContactsFriendListResult;
import com.galaxyschool.app.wawaschool.pojo.ContactsNewFriendRequestCountResult;
import com.galaxyschool.app.wawaschool.pojo.HomeworkChildListResult;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.StudentMemberInfo;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.UserInfoResult;
import com.galaxyschool.app.wawaschool.views.slidelistview.SlideListView;
import com.galaxyschool.app.wawaschool.views.sortlistview.ClearEditText;
import com.galaxyschool.app.wawaschool.views.sortlistview.SideBar;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.pojo.ContactsFriendInfo;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.sortlistview.SortListViewHelper;
import com.galaxyschool.app.wawaschool.views.sortlistview.SortModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonalContactsListFragment extends ContactsListFragment {

    public static final String TAG = PersonalContactsListFragment.class.getSimpleName();

    public interface Constants {
        public static final String EXTRA_CHAT_WITH_FRIEND = "chatWithFriend";
    }

    private TextView indicatorView;
    private ClearEditText searchBar;
    private ContactsFriendListResult dataListResult;
    private List<SortModel> sortDataList;
    private boolean chatWithFriend;
    private ListView childContactsFamilyListView;

    private TextView keywordView;
    private String keyword = "";
    private boolean isFirstIn=true;
    private String familyListViewTag;
    private View myFamilyContactsLayout;
    private String roles;
    private List<StudentMemberInfo> childList = new ArrayList<>();
    private boolean isFirstLoad = true;
    private boolean haveFamily = false;
    public static boolean hasContentChanged;
    public static final int REQUEST_CODE_PERSONAL_CONTACTS_LIST = 1201;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.contacts_sortlist_with_search_bar, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        refreshData();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void refreshData() {
        loadViews();
    }

    private void init() {
        if (getArguments() != null) {
            this.chatWithFriend = getArguments().getBoolean(Constants.EXTRA_CHAT_WITH_FRIEND);
        }
        initViews();
    }

    private void initViews() {

        this.searchBar = (ClearEditText) findViewById(R.id.search_keyword);
        this.searchBar.setHint(R.string.personal_contacts_hint_tips);
//        this.searchBar.setHintTextColor(getResources().getColor(R.color.black));
        this.searchBar.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        this.searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideSoftKeyboard(getActivity());
                    String keywords=v.getText().toString().trim();
                    if (!TextUtils.isEmpty(keywords)) {
                        loadContacts(v.getText().toString());
                    }
                    return true;
                }
                return false;
            }
        });
        this.searchBar.setOnClearClickListener(new ClearEditText.OnClearClickListener() {
            @Override
            public void onClearClick() {
//                    getCurrAdapterViewHelper().setData(sortDataList);
                keyword = "";
                getCurrAdapterViewHelper().clearData();
                getPageHelper().clear();
                loadUserInfo();
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
                        loadContacts(keywordView.getText().toString());
                    }
                }
            });
            view.setVisibility(View.VISIBLE);
        }
        findViewById(R.id.contacts_search_bar_layout).setVisibility(
                chatWithFriend ? View.GONE : View.VISIBLE);


        PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);

        SlideListView listView = (SlideListView) findViewById(R.id.contacts_list_view);
        if (listView != null) {
            listView.setSlideMode(SlideListView.SlideMode.NONE);
            int topMargin = (int)(10 * MyApplication.getDensity());
            listView.setPadding(0,topMargin,0,0);
            if (listView.getHeaderViewsCount() <= 0) {

                //新朋友布局
                View headerView = LayoutInflater.from(getActivity()).inflate(
                        R.layout.layout_common_header_list_view, null);
                ImageView imageView = (ImageView) headerView.findViewById(R.id.contacts_item_icon);
                imageView.setImageResource(R.drawable.new_friends);
                TextView textView = (TextView) headerView.findViewById(R.id.contacts_item_title);
                textView.setText(R.string.new_friends);
                textView = (TextView) headerView.findViewById(R.id.contacts_item_indicator);
                this.indicatorView = textView;

                //关注的人布局
                View subscribedPersonView = LayoutInflater.from(getActivity()).inflate(
                        R.layout.layout_common_header_list_view, null);
                ImageView subscribedImageView = (ImageView) subscribedPersonView.
                        findViewById(R.id.contacts_item_icon);
                subscribedImageView.setImageResource(R.drawable.icon_concerned_people);
                TextView subscribedTextView = (TextView) subscribedPersonView.
                        findViewById(R.id.contacts_item_title);
                subscribedTextView.setText(R.string.has_subscribed_person);

                //家庭通讯录布局
                View headerFamilyView = LayoutInflater.from(getActivity()).inflate(
                        R.layout.layout_header_view_family,null);
                headerFamilyView.setPadding(0,topMargin,0,0);

                if (!this.chatWithFriend) {
                    listView.addHeaderView(subscribedPersonView);
                    listView.addHeaderView(headerView);
                    //没有分割线
                    listView.addHeaderView(headerFamilyView,null,false);
                }
                initHeaderFamilyViewLayout(headerFamilyView);
                subscribedPersonView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        enterSubscribePersonListActivity();
                    }
                });
                headerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        enterFriendRequests();
                    }
                });
            }

//            AdapterViewHelper listViewHelper = new AdapterViewHelper(getActivity(),
//                    listView, R.layout.contacts_list_item) {
            SortListViewHelper listViewHelper = new SortListViewHelper(getActivity(),
                    listView, R.layout.contacts_sortlist_item, R.id.contacts_item_catalog,
                    (SideBar) findViewById(R.id.contacts_sort_sidebar),
                    (TextView) findViewById(R.id.contacts_sort_tips)) {
                @Override
                public void loadData() {
                        loadUserInfo();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    SortModel sortModel =
                            (SortModel) getDataAdapter().getItem(position);
                    if (sortModel == null) {
                        return view;
                    }
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = sortModel;
                    final ContactsFriendInfo data = (ContactsFriendInfo) sortModel.getData();
                    ImageView imageView = (ImageView) view.findViewById(R.id.contacts_item_icon);
                    if (imageView != null) {
                        getThumbnailManager().displayUserIcon(
                                AppSettings.getFileUrl(data.getHeadPicUrl()), imageView);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                    //进入个人名片
                                    if (!TextUtils.isEmpty(data.getMemberId())) {
                                        ActivityUtils.enterPersonalSpace(getActivity(),
                                                data.getMemberId());
                                    }
                            }
                        });
                    }
                    TextView textView = (TextView) view.findViewById(R.id.contacts_item_title);
                    if (textView != null) {
                        textView.setText(data.getNoteName());
                    }
                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        return;
                    }
                    ContactsFriendInfo data = (ContactsFriendInfo)((SortModel) holder.data).getData();
                    if (data == null){
                        return;
                    }
                    if (chatWithFriend) {
                        enterConversation(data);
                        finish();
                    } else {
                        //进入个人名片
                        if (!TextUtils.isEmpty(data.getMemberId())) {
                            ActivityUtils.enterPersonalSpace(getActivity(), data.getMemberId());
                        }
                    }
                }
            };

//            this.searchBar.setHint(R.string.search_friend);
//            this.searchBar.setFocusable(false);
//            this.searchBar.setFocusableInTouchMode(false);
//            this.searchBar.setInputType(InputType.TYPE_NULL);
//            this.searchBar.setKeyListener(null);
//            this.searchBar.setOnClickListener(this);
//            if (this.chatWithFriend) {
//                this.searchBar.setVisibility(View.GONE);
//            }

            listViewHelper.setSearchBar(this.searchBar, false);
            listViewHelper.showSideBar(true);
            listViewHelper.setData(null);

            setCurrAdapterViewHelper(listView, listViewHelper);
        }

    }

    private void enterSubscribePersonListActivity() {

        Intent intent = new Intent(getActivity(),SubscribePersonListActivity.class);
        //关注的人
        startActivityForResult(intent,SubscribePersonListFragment.
                REQUEST_CODE_SUBSCRIBE_PERSONAL_LIST);
    }

    private void loadUserInfo() {

        Map<String, Object> params = new HashMap();
        params.put("UserId",getMemeberId());
        //是否需要返回 HaveFamily 属性，默认 false
        params.put("IsReturnHaveFamily",true);
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
                        roles = getUserInfo().getRoles();
                        //返回是否有家庭通讯录
                        haveFamily = userInfo.isHaveFamily();
                        //刷新布局
                        showHeaderFamilyViewByRoles();
                        refreshView();
                    }
                });
    }

    private void initHeaderFamilyViewLayout(View headerFamilyView) {
        myFamilyContactsLayout =headerFamilyView.findViewById(R.id.layout_my_family_child);
        myFamilyContactsLayout.setOnClickListener(this);

        childContactsFamilyListView = (ListView) headerFamilyView.findViewById(R.id
                .contacts_family_list_view);
        if (childContactsFamilyListView !=null){
        }
    }

    private void showHeaderFamilyViewByRoles() {

        // 0老师 1学生 2家长 3游客
        if (!TextUtils.isEmpty(roles)){
            //家庭通讯录：仅对学生，家长可见。单纯老师身份的账号，登录后看不到“家庭通讯录”。
            //目前只有家长有孩子，需要显示“孩子的家庭通讯录”。学生显示“我的家庭通讯录”，对于其他角色，需要根据
            //haveFamily字段来判断是否需要显示“我的家庭通讯录”。

            if (roles.contains(String.valueOf(RoleType.ROLE_TYPE_PARENT))){
                if (myFamilyContactsLayout != null) {
                    myFamilyContactsLayout.setVisibility(View.GONE);
                }
                childContactsFamilyListView.setVisibility(View.VISIBLE);
                //加载ListView
                if (isFirstLoad) {
                    initChildInfoListView();
                }
            }else if (roles.contains(String.valueOf(RoleType.ROLE_TYPE_STUDENT))){
                //如果是学生的话，隐藏“孩子家庭通讯录”，展示“我的家庭通讯录”。
                //需要判断是否包含学生
                if (myFamilyContactsLayout != null) {
                    myFamilyContactsLayout.setVisibility(View.VISIBLE);
                    initMyFamilyLayout();
                }
                childContactsFamilyListView.setVisibility(View.GONE);
            }else {
                //其他角色，需要根据是否有家庭通讯录来进行显示和隐藏。
                showFamilyLayoutByUserFamilyState();
            }

        }else {
            //防止roles为空的情况
            showFamilyLayoutByUserFamilyState();
        }

        isFirstLoad = false;
    }

    private void initMyFamilyLayout() {
        //头像
        ImageView imageView = (ImageView) myFamilyContactsLayout.findViewById
                (R.id.user_icon_child);
        if (imageView != null){
            getThumbnailManager().displayUserIconWithDefault(
                    AppSettings.getFileUrl(getUserInfo().getHeaderPic()),imageView,
                    R.drawable.default_user_icon);
            //点击头像进入详情页面
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityUtils.enterPersonalSpace(getActivity(),getUserInfo().getMemberId());
                }
            });
        }

        //标题
        TextView title = (TextView) myFamilyContactsLayout.findViewById(R.id.title_child);
        if (title != null){
            title.setText(getString(R.string.myself_family_contacts));
        }
    }

    private void showFamilyLayoutByUserFamilyState() {
        if(haveFamily){
            //显示我的家庭通讯录
            myFamilyContactsLayout.setVisibility(View.VISIBLE);
            initMyFamilyLayout();
            childContactsFamilyListView.setVisibility(View.GONE);

        }else {
            myFamilyContactsLayout.setVisibility(View.GONE);
            childContactsFamilyListView.setVisibility(View.GONE);
        }
    }

    private void initChildInfoListView() {
        AdapterViewHelper adapterViewHelper = new AdapterViewHelper(getActivity(),
                childContactsFamilyListView,R.layout.item_child_family_contacts_layout) {
            @Override
            public void loadData() {
                loadChildInfo();
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view =super.getView(position, convertView, parent);

                final StudentMemberInfo data = (StudentMemberInfo)
                        getDataAdapter().getData().get(position);

                if ( data == null ){
                    return view;
                }

                ViewHolder holder = (ViewHolder) view.getTag();
                if (holder == null){
                    holder = new ViewHolder();
                }

                holder.data = data;

                //头像
                ImageView imageView = (ImageView) view.findViewById(R.id.user_icon);
                if (imageView != null){
                    getThumbnailManager().displayUserIconWithDefault(
                            AppSettings.getFileUrl(data.getHeadPicUrl()),imageView,R.drawable
                                    .default_user_icon);
                    //点击头像进入孩子的个人详情
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ActivityUtils.enterPersonalSpace(getActivity(),data.getMemberId());
                        }
                    });
                }

                //标题
                TextView textView = (TextView) view.findViewById(R.id.title);
                if (textView != null){
                    //真实姓名优先显示，真实姓名为空显示账户名。
                    String childName = data.getRealName();
                    if (TextUtils.isEmpty(childName)){
                        childName = data.getNickName();
                    }
                    textView.setText(getString(R.string.my_family,childName));
                }

                view.setTag(holder);

                return view;
            }

            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {

                ViewHolder holder = (ViewHolder) view.getTag();
                if (holder == null){
                    return;
                }

                StudentMemberInfo data = (StudentMemberInfo) holder.data;
                if (data != null){
                    enterMyFamilyActivity(data.getMemberId());
                }
            }
        };
        familyListViewTag = String.valueOf(childContactsFamilyListView.getId());
        addAdapterViewHelper(familyListViewTag,adapterViewHelper);
    }

    private void loadChildInfo() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("MemberId",getMemeberId());

        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_STUDENT_BY_PARENT_URL, params,
                new DefaultDataListener<HomeworkChildListResult>(
                        HomeworkChildListResult.class) {
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

                        updateChildLayout(getResult());
                    }
                });

    }

    private void updateChildLayout(HomeworkChildListResult result) {
        List<StudentMemberInfo> list = result.getModel().getData();
        if (list == null || list.size() <= 0){
            childList.clear();
            getAdapterViewHelper(familyListViewTag).update();
            return;
        }
        childList = list;

       getAdapterViewHelper(familyListViewTag).setData(childList);

    }

    private void refreshView(){
        if (!TextUtils.isEmpty(roles)){
            //家长的话，需要刷新一下绑定的孩子界面。目前只有家长才有孩子。
            if (roles.contains(String.valueOf(RoleType.ROLE_TYPE_PARENT))){
                loadChildInfo();
            }
        }

            loadContacts(keywordView.getText().toString());
    }

    private void loadViews() {
        isFirstIn=true;
        getPageHelper().clear();
        loadUserInfo();
        if (!this.chatWithFriend) {
            loadNewFriendRequestCount();
        }
    }

    private List<SortModel> updateViews(ContactsFriendListResult result) {
        List<ContactsFriendInfo> list = result.getModel().getPersonalMailListList();
        List<SortModel> sortList = processDataList(list);
        if (sortList == null || sortList.size() <= 0){
            TipsHelper.showToast(getActivity(),
                    getString(R.string.no_data));
        }
        getCurrAdapterViewHelper().setData(sortList);
        //控制sidebar的显示
        SortListViewHelper helper = (SortListViewHelper) getCurrAdapterViewHelper();
        if (helper != null){
            if (sortList != null && sortList.size() > 0){
                helper.showSideBar(true);
            }else {
                //没有联系人需要隐藏sidebar
                helper.showSideBar(false);
            }
        }
        dataListResult = result;
        return sortList;
    }

    private List<SortModel> processDataList(List<ContactsFriendInfo> list) {
        if (list != null && list.size() > 0) {
            List<SortModel> sortList = new ArrayList<SortModel>();
            SortModel sortModel = null;
            for (ContactsFriendInfo obj : list) {
                sortModel = new SortModel();
                sortModel.setName(obj.getNoteName());
                sortModel.setSortLetters(obj.getFirstLetter());
                sortModel.setData(obj);
                sortList.add(sortModel);
            }
            return sortList;
        }
        return null;
    }

    private void loadContacts(String keyword) {
        if (!((MyApplication) getActivity().getApplication()).hasLogined()) {
            return;
        }

        keyword = keyword.trim();

        if (isFirstIn){

        }else {
            if (getCurrAdapterViewHelper().hasData()){

            }else {

                if (TextUtils.isEmpty(keyword)) {
//                    return;
                }
            }
        }

        if (!keyword.equals(this.keyword)) {
            getCurrAdapterViewHelper().clearData();
            getPageHelper().clear();
        }
        this.keyword = keyword;
        UIUtils.hideSoftKeyboard(getActivity());
        if (getUserInfo() == null){
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("Key", keyword);//按关键字搜索，本地搜索。

        DefaultPullToRefreshListener listener =
                new DefaultPullToRefreshListener<ContactsFriendListResult>(
                        ContactsFriendListResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                isFirstIn=false;
                if(getActivity() == null) {
                    return;
                }
                SharedPreferencesHelper.setString(getActivity(),
                        PrefsManager.PrefsItems.CONTACTS_PERSONAL_LIST_RESULT, jsonString);
                super.onSuccess(jsonString);
                ContactsFriendListResult result = getResult();
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
                        getActivity(), PrefsManager.PrefsItems.CONTACTS_PERSONAL_LIST_RESULT);
                if (TextUtils.isEmpty(jsonString)) {
                    return;
                }
                super.onSuccess(jsonString);
                ContactsFriendListResult result = getResult();
                if (result == null || !result.isSuccess()
                        || result.getModel() == null) {
                    return;
                }
                sortDataList = updateViews(result);
            }
        };
        postRequest(ServerUrl.CONTACTS_FRIEND_LIST_URL, params, listener);
    }

    private void loadNewFriendRequestCount() {
        if (!((MyApplication) getActivity().getApplication()).hasLogined()) {
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        DefaultListener listener =
                new DefaultListener<ContactsNewFriendRequestCountResult>(
                        ContactsNewFriendRequestCountResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                ContactsNewFriendRequestCountResult result = getResult();
                if (result == null || !result.isSuccess()
                        || result.getModel() == null) {
                    return;
                }
                int count = result.getModel().getApprovalCount();
                if (count > 0) {
                    String str = null;
                    if (count > 99) {
                        str = "99+";
                    } else {
                        str = String.valueOf(count);
                    }
                    indicatorView.setVisibility(View.VISIBLE);
                    indicatorView.setText(str);
                } else {
                    indicatorView.setVisibility(View.INVISIBLE);
                }
            }
        };
        postRequest(ServerUrl.CONTACTS_NEW_FRIEND_REQUEST_COUNT_URL, params, listener);
    }

    private void enterFriendDetails(ContactsFriendInfo data) {
        Bundle args = new Bundle();
        args.putInt(ContactsMemberDetailsActivity.EXTRA_MEMBER_TYPE,
            ContactsMemberDetailsActivity.MEMBER_TYPE_FRIEND);
        args.putString(ContactsMemberDetailsActivity.EXTRA_MEMBER_ID, data.getId());
        Intent intent = new Intent(getActivity(), ContactsMemberDetailsActivity.class);
        intent.putExtras(args);
        if (getParentFragment() != null) {
            getParentFragment().startActivityForResult(intent,
                    FriendDetailsFragment.Constants.REQUEST_CODE_FRIEND_DETAILS);
        } else {
            startActivityForResult(intent,
                    FriendDetailsFragment.Constants.REQUEST_CODE_FRIEND_DETAILS);
        }
    }

    private void enterFriendRequests() {
        Intent intent = new Intent(getActivity(), ContactsFriendRequestListActivity.class);
        startActivityForResult(intent,ContactsFriendRequestListFragment.
                REQUEST_CODE_CONTACTS_FRIEND_REQUEST_LIST);
    }

    private void enterSearchFriend() {
        Intent intent = new Intent(getActivity(), ContactsSearchFriendActivity.class);
        startActivity(intent);
    }

    private void enterConversation(ContactsFriendInfo data) {
        String userId = data.getMemberId();
        String userName = data.getNoteName();
        String userAvatar = AppSettings.getFileUrl(data.getHeadPicUrl());
        enterConversation(userId, userName, userAvatar);
    }

    private void enterConversation(String userId, String nickname, String avatar) {
        if (!HXSDKHelper.getInstance().isLogined()) {
            TipsHelper.showToast(getActivity(),
                    R.string.chat_service_not_works);
            return;
        }
        String userName = "hx" + userId;
        Bundle args = new Bundle();
        args.putInt(ChatActivity.EXTRA_CHAT_TYPE, ChatActivity.CHATTYPE_SINGLE);
        args.putString(ChatActivity.EXTRA_USER_ID, userName);
        args.putString(ChatActivity.EXTRA_USER_AVATAR, avatar);
        args.putString(ChatActivity.EXTRA_USER_NICKNAME, nickname);
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtras(args);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.search_keyword) {
            enterSearchFriend();
        }else if (v.getId() == R.id.layout_my_family_child){
            //学生进入家庭通讯录
            enterMyFamilyActivity(getMemeberId());
        }else {
            super.onClick(v);
        }
    }

    private void enterMyFamilyActivity(String childId) {
        Intent intent = new Intent(getActivity(),MyFamilyActivity.class);
        intent.putExtra("childId",childId);
        intent.putExtra("roles",roles);
        startActivityForResult(intent,MyFamilyListFragment.REQUEST_CODE_MY_FAMILY_LIST);
    }

    public static void setHasContentChanged(boolean hasContentChanged) {
        PersonalContactsListFragment.hasContentChanged = hasContentChanged;
    }

    public static boolean hasContentChanged() {
        return hasContentChanged;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == FriendDetailsFragment.Constants.REQUEST_CODE_FRIEND_DETAILS) {
                boolean changed = data.getBooleanExtra(
                        FriendDetailsFragment.Constants.EXTRA_FRIEND_DETAILS_CHANGED, false);
                if (!changed) {
                    return;
                }
                //TODO: update contacts list
                String friendId = data.getStringExtra(FriendDetailsFragment.Constants.EXTRA_FRIEND_ID);
                List<SortModel> list = getCurrAdapterViewHelper().getData();
                SortModel target = null;
                for (int i = 0; i < list.size(); i++) {
                    target = list.get(i);
                    if (friendId.equals(((ContactsFriendInfo) target.getData()).getId())) {
                        break;
                    }
                }
                if (target != null) {
                    changed = data.getBooleanExtra(
                            FriendDetailsFragment.Constants.EXTRA_FRIEND_DELETED, false);
                    if (changed) {
                        getCurrAdapterViewHelper().getData().remove(target);
                        getCurrAdapterViewHelper().update();
                        return;
                    }

                    changed = data.getBooleanExtra(
                            FriendDetailsFragment.Constants.EXTRA_FRIEND_REMARK_CHANGED, false);
                    if (changed) {
                        ((ContactsFriendInfo) target.getData()).setNoteName(
                                data.getStringExtra(FriendDetailsFragment.Constants.EXTRA_FRIEND_REMARK));
                        getCurrAdapterViewHelper().update();
                    }
                }
            }
        }

        if (data == null){
            if (requestCode == ContactsFriendRequestListFragment.
                    REQUEST_CODE_CONTACTS_FRIEND_REQUEST_LIST){
                //新朋友
                if (ContactsFriendRequestListFragment.hasMessageHandled()){
                    ContactsFriendRequestListFragment.setHasMessageHandled(false);
                    //朋友请求处理后需要刷新页面
                    refreshData();
                }
            }else if (requestCode == PersonalSpaceFragment.REQUEST_CODE_PERSONAL_SPACE){
                //删除好友成功之后，需要刷新页面。
                if (PersonalSpaceFragment.hasUnbindFriendRelationship()){
                    PersonalSpaceFragment.setHasUnbindFriendRelationship(false);
                    //只有删除好友的时候，才需要刷新消息列表。
                    setHasContentChanged(true);
                    //刷新页面
                    refreshData();
                }

                //备注名修改
                if (PersonalSpaceFragment.hasRemarkNameChanged()){
                    PersonalSpaceFragment.setHasRemarkNameChanged(false);
                    refreshData();
                }
            }else if (requestCode == MyFamilyListFragment.REQUEST_CODE_MY_FAMILY_LIST){
                //家庭通讯录解绑
                if (MyFamilyListFragment.hasUnbindRelationship()){
                    MyFamilyListFragment.setHasUnbindRelationship(false);
                    //刷新页面
                    refreshData();
                }
            }else if (requestCode == ContactsSearchFriendFragment.REQUEST_CODE_SEARCH_FRIEND){
                //添加好友页面好友关系改变
                if (ContactsSearchFriendFragment.hasRelationShipChanged()){
                    ContactsSearchFriendFragment.setHasRelationShipChanged(false);
                    //刷新页面
                    refreshData();
                }
            }else if (requestCode == SubscribePersonListFragment.
                    REQUEST_CODE_SUBSCRIBE_PERSONAL_LIST){
                //关注的人页面数据改变
                if (SubscribePersonListFragment.hasFriendContentChanged()){
                    SubscribePersonListFragment.setHasFriendContentChanged(false);
                    //刷新页面
                    refreshData();
                }
            }
        }
    }

}
