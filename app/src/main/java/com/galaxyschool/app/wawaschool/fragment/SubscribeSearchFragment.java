package com.galaxyschool.app.wawaschool.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.*;

import com.duowan.mobile.netroid.NetroidError;
import com.galaxyschool.app.wawaschool.AccountActivity;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.PersonalContactsActivity;
import com.galaxyschool.app.wawaschool.PersonalSpaceActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.SchoolClassListActivity;
import com.galaxyschool.app.wawaschool.SchoolSpaceActivity;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.lqwawa.intleducation.module.onclass.OnlineClassListActivity;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.SubscribeUserInfo;
import com.galaxyschool.app.wawaschool.pojo.SubscribeUserListResult;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.sortlistview.ClearEditText;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubscribeSearchFragment extends ContactsListFragment {

    public static final String TAG = SubscribeSearchFragment.class.getSimpleName();


    public interface Constants {
        String EXTRA_SUBSCRIPE_SEARCH_TYPE = "subscripe_search_type";

        int SUBSCRIPE_SEARCH_ALL = 0;
        int SUBSCRIPE_SEARCH_SCHOOL = 1;
        int SUBSCRIPE_SEARCH_USER = 2;
        String IS_NEED_SHOW_JOIN_STATE = "isNeedShowJoinState";
    }


    private TextView keywordView;
    private String keyword = "";
    private int subscripeSearchType;
    private SubscribeUserListResult dataListResult;
    private boolean hasSearched=false;
    private boolean isFirstLoadedSchoolInfo=true;
    //是否需要隐藏关注状态
    private boolean isNeedHiddenSubscribeState = false;
    //是否需要显示加入状态
    public boolean isNeedShowJoinState = false;
    private static boolean hasPersonFocusChanged,hasSchoolFocusChanged;//关注点改变
    public static final int REQUEST_CODE_SUBSCRIBER_SEARCH = 308;//request code

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.subscribe_search, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        refreshData();
    }

    private void  refreshData(){
        loadViews();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initViews() {
        if(getArguments() != null) {
            subscripeSearchType = getArguments().getInt(Constants.EXTRA_SUBSCRIPE_SEARCH_TYPE,
                    Constants.SUBSCRIPE_SEARCH_ALL);
            isNeedHiddenSubscribeState = getArguments().getBoolean(PersonalContactsActivity
            .IS_NEED_HIDDEN_SUBSCRIBE_STATE);
            isNeedShowJoinState = getArguments().getBoolean(Constants.IS_NEED_SHOW_JOIN_STATE);
        }
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            textView.setOnClickListener(this);
//            textView.setText(R.string.search);
            if (subscripeSearchType==Constants.SUBSCRIPE_SEARCH_SCHOOL){
                if (isNeedHiddenSubscribeState){
                    //加入班级
                    textView.setText(getString(R.string.join_class));
                }else {
                    if (isNeedShowJoinState){
                        //加入机构
                        textView.setText(getString(R.string.add_school));

                    }else {
                        //添加机构
                        textView.setText(getString(R.string.add_authority));
                    }
                }


            }else if (subscripeSearchType==Constants.SUBSCRIPE_SEARCH_USER){
                if (isNeedHiddenSubscribeState){
                    //添加好友
                    textView.setText(getString(R.string.add_friend));
                }else {
                    textView.setText(getString(R.string.add_sb));
                }
            }

        }

        ClearEditText editText = (ClearEditText) findViewById(R.id.search_keyword);
        if (editText != null) {
            if(subscripeSearchType == Constants.SUBSCRIPE_SEARCH_SCHOOL) {
                editText.setHint(getString(R.string.subscribe_search_school_tips));
            } else if(subscripeSearchType == Constants.SUBSCRIPE_SEARCH_USER) {
                editText.setHint(getString(R.string.subscribe_search_user_tips));
            } else {
                editText.setHint(getString(R.string.default_subscribe_search_school_tips));
            }
            editText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        hideSoftKeyboard(getActivity());
                        String keywords=v.getText().toString().trim();
                        if (!TextUtils.isEmpty(keywords)) {
                            searchKeyword(v.getText().toString());
                        }
                        return true;
                    }
                    return false;
                }
            });
            editText.setOnClearClickListener(new ClearEditText.OnClearClickListener() {
                @Override
                public void onClearClick() {
                    keyword = "";
                    getCurrAdapterViewHelper().clearData();
                    getPageHelper().clear();
                    searchKeyword(keywordView.getText().toString());
                }
            });
            editText.setInputType(InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            UIUtils.showSoftKeyboardValid(getActivity());
            editText.requestFocus();
        }
        keywordView = editText;

        View view = findViewById(R.id.search_btn);
        if (view != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSoftKeyboard(getActivity());
                    String keywords=keywordView.getText().toString().trim();
                    if (!TextUtils.isEmpty(keywords)){
                        searchKeyword(keywordView.getText().toString());
                    }
                }
            });
            view.setVisibility(View.VISIBLE);
        }
        view = findViewById(R.id.contacts_search_bar_layout);
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }

        PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);

        ListView listView = (ListView) findViewById(R.id.contacts_list_view);
        if (listView != null) {
            AdapterViewHelper listViewHelper = new AdapterViewHelper(getActivity(),
                    listView, R.layout.subscribe_list_item) {
                @Override
                public void loadData() {
                    searchKeyword(keywordView.getText().toString());
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    final SubscribeUserInfo data =
                            (SubscribeUserInfo) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;
                    ImageView imageView = (ImageView) view.findViewById(
                            R.id.contacts_item_icon);
                    if (imageView != null) {
                        getThumbnailManager().displayUserIconWithDefault(
                                AppSettings.getFileUrl(data.getThumbnail()), imageView,
                                data.isPerson() ? R.drawable.default_user_icon : R.drawable.default_school_icon);
                        //头像点击操作
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                enterSpaceByData(data);
                            }
                        });
                    }
                    TextView textView = (TextView) view.findViewById(
                            R.id.contacts_item_title);
                    if (textView != null) {
                        textView.setText(data.getName());
                    }

                    //是否关注
                    textView= (TextView) view.findViewById(R.id.contacts_item_state);
                    //是否关注，包含了仅关注和已加入
                    final boolean hasSubscribed = data.hasSubscribed();
                    //是否加入
                    final boolean hasJoined = data.hasJoined();
                    if (textView!=null){
                        if (isNeedHiddenSubscribeState){
                            textView.setVisibility(View.GONE);
                        }else {
                            textView.setVisibility(View.VISIBLE);
                        }
                        if (!hasSubscribed){
                            textView.setTextAppearance(getActivity(),R.style
                            .txt_wawa_normal_green);
                            //显示加入
                            if (isNeedShowJoinState){
                                textView.setText(getString(R.string.add));
                            }else {
                                textView.setText(getString(R.string.plus_follow));
                            }

                            textView.setBackgroundResource(R.drawable.button_bg_with_round_sides);
                        }else {
                            //已关注
                            textView.setTextAppearance(getActivity(),R.style
                                    .txt_wawa_normal_darkgray);
                            textView.setBackgroundResource
                                    (R.drawable.button_bg_transparent_with_round_sides);

                            if (isNeedShowJoinState){
                                //显示已加入
                                if (hasJoined){
                                    textView.setText(getString(R.string.joined));
                                }else {
                                    //仅仅是关注状态，还是属于“未加入”，显示“加入”
                                    textView.setText(R.string.add);
                                    textView.setTextAppearance(getActivity(),R.style
                                            .txt_wawa_normal_green);
                                    textView.setBackgroundResource(R.drawable.
                                            button_bg_with_round_sides);
                                }

                            }else {
                                //显示已关注
                                textView.setText(getString(R.string.subscribed));
                            }

                        }

                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (isNeedShowJoinState){
                                    //加入逻辑
                                    if (hasJoined){
                                        return;
                                    }else {
                                        joinSchool(data);
                                    }

                                }else {
                                    //关注逻辑
                                    //关注
                                    if (hasSubscribed){
                                        return;
                                    }else {
                                        //加关注
                                        addSubscribe(data.getId());
                                    }
                                }

                            }
                        });
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
                    enterUserSpace((SubscribeUserInfo) holder.data);
                }
            };
            setCurrAdapterViewHelper(listView, listViewHelper);
        }
    }

    private void joinSchool(SubscribeUserInfo data) {
        SchoolInfo schoolInfo = new SchoolInfo();
        schoolInfo.setSchoolId(data.getId());
        schoolInfo.setSchoolName(data.getName());
        if (isLogin()) {
            if (data.isOnlineSchool()){
                OnlineClassListActivity.show(getActivity(),schoolInfo.getSchoolId(),schoolInfo.getSchoolName());
            } else {
                ActivityUtils.enterSchoolClassListActivity(getActivity(), schoolInfo);
            }
        } else {
            enterAccountActivity();
        }
    }

    private void enterAccountActivity() {
        //登录
        Intent intent = new Intent(getActivity(), AccountActivity.class);
        Bundle args = new Bundle();
        args.putBoolean(AccountActivity.EXTRA_HAS_LOGINED, false);
        args.putBoolean(AccountActivity.EXTRA_ENTER_HOME_AFTER_LOGIN, false);
        intent.putExtras(args);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
        }
    }

    private void addSubscribe(final String subscribeUserId) {

        if (TextUtils.isEmpty(subscribeUserId)){
            return;
        }
        if (getUserInfo() == null){
            return;
        }
        Map<String, Object> params = new HashMap();
        if (subscripeSearchType==Constants.SUBSCRIPE_SEARCH_SCHOOL){
            params.put("MemberId", getUserInfo().getMemberId());
            params.put("SchoolId", subscribeUserId);
        }else if (subscripeSearchType==Constants.SUBSCRIPE_SEARCH_USER){
            params.put("FAttentionId", getUserInfo().getMemberId());
            params.put("TAttentionId", subscribeUserId);
        }

        DefaultDataListener<DataModelResult> listener =
                new DefaultDataListener<DataModelResult>(DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()) {
                            return;
                        } else {
                            //关注/取消关注成功后，设置本页面刷新标识
                            if (subscripeSearchType==Constants.SUBSCRIPE_SEARCH_SCHOOL){
                                setHasSchoolFocusChanged(true);
                            }else if (subscripeSearchType==Constants.SUBSCRIPE_SEARCH_USER){
                                setHasPersonFocusChanged(true);
                            }
                            TipsHelper.showToast(getActivity(),R.string.subscribe_success );
                            if (getCurrAdapterViewHelper().hasData()){
                                List<SubscribeUserInfo> list= getCurrAdapterViewHelper().getData();
                                if (list != null && list.size() > 0){
                                    for (SubscribeUserInfo info : list){
                                        if (info.getId().equals(subscribeUserId)){
                                            info.setState(SubscribeUserInfo.USER_STATE_SUBSCRIBED);
                                            break;
                                        }
                                    }
                                    getCurrAdapterViewHelper().update();
                                    //关注/取消关注成功后，向校园空间发广播，
                                    MySchoolSpaceFragment.sendBrocast(getActivity());
                                }
                            }
                        }
                    }
                };
        listener.setShowLoading(true);
        String serverUrl="";
        if (subscripeSearchType==Constants.SUBSCRIPE_SEARCH_SCHOOL){
            serverUrl=ServerUrl.SUBSCRIBE_ADD_SCHOOL_URL;
        }else if(subscripeSearchType==Constants.SUBSCRIPE_SEARCH_USER) {
            serverUrl=ServerUrl.SUBSCRIBE_ADD_PERSON_URL;
        }
        RequestHelper.sendPostRequest(getActivity(), serverUrl, params, listener);

    }

    private void loadViews() {
        getPageHelper().clear();
        searchKeyword(this.keywordView.getText().toString());
    }

    private void searchKeyword(String keyword) {
        keyword = keyword.trim();
        if (subscripeSearchType==Constants.SUBSCRIPE_SEARCH_SCHOOL) {
            if (isFirstLoadedSchoolInfo) {
                //第一次进入刷全部机构

            } else {
                if (getCurrAdapterViewHelper().hasData()) {
                    //有数据就忽略关键字为空检查

                } else {
                    if (TextUtils.isEmpty(keyword)) {
//                        return;
                    }
                }
            }
        }else if (subscripeSearchType==Constants.SUBSCRIPE_SEARCH_USER){
            if (TextUtils.isEmpty(keyword)) {
                return;
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
        params.put("Type", subscripeSearchType); //0: user & school, 1:school, 2:user
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("KeyWord", keyword);
        params.put("Pager", getPageHelper().getFetchingPagerArgs());
        DefaultPullToRefreshDataListener listener =
                new DefaultPullToRefreshDataListener<SubscribeUserListResult>(
                        SubscribeUserListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        isFirstLoadedSchoolInfo=false;
                        if(getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()
                                || getResult().getModel() == null) {
                            return;
                        }
                        updateViews(getResult());
                    }

                    @Override
                    public void onError(NetroidError error) {
                        isFirstLoadedSchoolInfo=false;
                        if(getActivity() == null) {
                            return;
                        }
                        super.onError(error);
                    }
                };
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.SUBSCRIBE_SEARCH_URL, params, listener);
    }

    private void updateViews(SubscribeUserListResult result) {
        if (getPageHelper().isFetchingPageIndex(result.getModel().getPager())) {
            processDataListResult(result);
            List<SubscribeUserInfo> list = result.getModel().getData();
            Utils.removeOnlineSubscribeUserInfo(list);
            if (list == null || list.size() <= 0) {
                if (getPageHelper().isFetchingFirstPage()) {
                    getCurrAdapterViewHelper().clearData();
                    TipsHelper.showToast(getActivity(),
                            getString(R.string.no_data));
                } else {
                    TipsHelper.showToast(getActivity(),
                            getString(R.string.no_more_data));
                }
                return;
            }

            if (getPageHelper().isFetchingFirstPage()) {
                getCurrAdapterViewHelper().clearData();
            }
            getPageHelper().updateByPagerArgs(result.getModel().getPager());
            getPageHelper().setCurrPageIndex(
                    getPageHelper().getFetchingPageIndex());
            if (getCurrAdapterViewHelper().hasData()) {
                int position = getCurrAdapterViewHelper().getData().size();
                if (position > 0) {
                    position--;
                }
                getCurrAdapterViewHelper().getData().addAll(list);
                getCurrAdapterView().setSelection(position);
                dataListResult.getModel().setData(getCurrAdapterViewHelper().getData());
            } else {
                getCurrAdapterViewHelper().setData(list);
                dataListResult = result;
            }
        }
    }

    private void processDataListResult(SubscribeUserListResult result) {
        UserInfo userInfo = getUserInfo();
        List<SubscribeUserInfo> list = (List) result.getModel().getData();
        if (list != null && list.size() > 0) {
            for (SubscribeUserInfo obj : list) {
                if (obj.getId().equals(userInfo.getMemberId())) {
                    list.remove(obj);
                    break;
                }
            }
        }
    }

    private void enterSpaceByData(SubscribeUserInfo data) {
        if (data.isPerson()) {
            //头像点击始终进入详情页面
            enterPersonalSpace(data);
        } else if (data.isSchool()) {
            //头像点击始终进入详情页面
                enterSchoolSpace(data);
        }
    }

    private void enterUserSpace(SubscribeUserInfo data) {
        if (data.isPerson()) {
            enterPersonalSpace(data);
        } else if (data.isSchool()) {
            if (isNeedHiddenSubscribeState) {
                //从班级通讯录里面过来，需要点击条目进入班级。
                if (data.isOnlineSchool()){
                    OnlineClassListActivity.show(getActivity(),data.getId(),data.getName());
                } else {
                    enterClassSpace(data);
                }
            } else {
                enterSchoolSpace(data);
            }
        }
    }

    private void enterClassSpace(SubscribeUserInfo data) {

        Bundle args = new Bundle();
        args.putString("schoolId", data.getId());
        args.putString("schoolName", data.getName());
        Intent intent = new Intent(getActivity(), SchoolClassListActivity.class);
        intent.putExtras(args);
        startActivity(intent);
    }

    private void enterPersonalSpace(SubscribeUserInfo data) {
        Bundle args = new Bundle();
        args.putString(PersonalSpaceActivity.EXTRA_USER_ID, data.getId());
        args.putString(PersonalSpaceActivity.EXTRA_USER_REAL_NAME, data.getName());
        Intent intent = new Intent(getActivity(), PersonalSpaceActivity.class);
        intent.putExtras(args);
        //个人信息页面
        startActivityForResult(intent,PersonalSpaceFragment.REQUEST_CODE_PERSONAL_SPACE);
    }

    private void enterSchoolSpace(SubscribeUserInfo data) {
        Bundle args = new Bundle();
        args.putString(SchoolSpaceActivity.EXTRA_SCHOOL_ID, data.getId());
        args.putString(SchoolSpaceActivity.EXTRA_SCHOOL_NAME, data.getName());
        Intent intent = new Intent(getActivity(), SchoolSpaceActivity.class);
        intent.putExtras(args);
        //school space
        startActivityForResult(intent,SchoolSpaceFragment.REQUEST_CODE_SCHOOL_SPACE);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    public static void setHasPersonFocusChanged(boolean hasPersonFocusChanged) {
        SubscribeSearchFragment.hasPersonFocusChanged = hasPersonFocusChanged;
    }

    public static boolean hasPersonFocusChanged() {
        return hasPersonFocusChanged;
    }

    public static void setHasSchoolFocusChanged(boolean hasSchoolFocusChanged) {
        SubscribeSearchFragment.hasSchoolFocusChanged = hasSchoolFocusChanged;
    }

    public static boolean hasSchoolFocusChanged() {
        return hasSchoolFocusChanged;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null){
            //个人空间返回
            if (requestCode == PersonalSpaceFragment.REQUEST_CODE_PERSONAL_SPACE){
                if (PersonalSpaceFragment.hasFocusChanged()){
                    PersonalSpaceFragment.setHasFocusChanged(false);
                    //设置本页面需要刷新标识
                    setHasPersonFocusChanged(true);
                    //关注/取消关注发生改变，需要刷新数据。
                    refreshData();
                }
            }else if (requestCode == SchoolSpaceFragment.REQUEST_CODE_SCHOOL_SPACE){
                //机构名片
                if (SchoolSpaceFragment.hasFocusChanged()){
                    SchoolSpaceFragment.setHasFocusChanged(false);
                    //设置本页面需要刷新标识
                    setHasSchoolFocusChanged(true);
                    //刷新页面
                    refreshData();
                }
            }
        }
    }
}
