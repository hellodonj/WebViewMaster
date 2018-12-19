package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.galaxyschool.app.wawaschool.ContactsMemberDetailsActivity;
import com.galaxyschool.app.wawaschool.PersonalSpaceActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.SubscribeSearchActivity;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.ContactsSearchPersonInfo;
import com.galaxyschool.app.wawaschool.pojo.ContactsSearchPersonListResult;
import com.galaxyschool.app.wawaschool.pojo.SubscribeUserInfo;
import com.galaxyschool.app.wawaschool.pojo.SubscribeUserListResult;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.slidelistview.SlideListView;
import com.galaxyschool.app.wawaschool.views.sortlistview.ClearEditText;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactsSearchFriendFragment extends ContactsListFragment {

    public static final String TAG = ContactsSearchFriendFragment.class.getSimpleName();

    private TextView keywordView;
    private String keyword = "";
    private SubscribeUserListResult dataListResult;
    public static final int REQUEST_CODE_SEARCH_FRIEND = 1308;
    private static boolean hasRelationShipChanged;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.contacts_search, null);
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
    }

    private void initViews() {
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            textView.setOnClickListener(this);
            textView.setText(R.string.add_friend);
        }

        ClearEditText editText = (ClearEditText) findViewById(R.id.search_keyword);
        if (editText != null) {
            editText.setHint(getString(R.string.search_account));
            editText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        hideSoftKeyboard(getActivity());
//                        searchKeyword(v.getText().toString());
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
                }
            });
            editText.setInputType(InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

            UIUtils.showSoftKeyboardValid(getActivity());
            editText.requestFocus();
        }
        this.keywordView = editText;

        View view = findViewById(R.id.search_btn);
        if (view != null) {
//            view.setOnClickListener(this);
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

        PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);

        SlideListView listView = (SlideListView) findViewById(R.id.contacts_list_view);
        if (listView != null) {
            listView.setSlideMode(SlideListView.SlideMode.NONE);
            AdapterViewHelper listViewHelper = new AdapterViewHelper(getActivity(),
                    listView, R.layout.contacts_search_add_friend_list_item) {
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
                    ImageView imageView = (ImageView) view.findViewById(R.id.contacts_search_item_icon);
                    if (imageView != null) {
                        getThumbnailManager().displayUserIcon(
                                AppSettings.getFileUrl(data.getThumbnail()), imageView);
                    }
                    TextView textView = (TextView) view.findViewById(R.id.contacts_search_item_title);
                    if (textView != null) {
                        textView.setText(data.getName());
                    }
                    textView = (TextView) view.findViewById(R.id.contacts_search_item_subtitle);
                    if (textView != null) {
                        textView.setText("");
                        textView.setVisibility(View.GONE);
                    }

                    //加好友
                    textView = (TextView) view.findViewById(R.id.contacts_item_state);
                    if (textView != null){

                        if (!data.isFriend()){
                            textView.setTextAppearance(getActivity(),R.style
                                    .txt_wawa_normal_green);
                            textView.setText(getString(R.string.plus_friend));
                            textView.setBackgroundResource(R.drawable.button_bg_with_round_sides);

                        }else {
                            //已是好友
                            textView.setTextAppearance(getActivity(),R.style
                                    .txt_wawa_normal_darkgray);
                            textView.setText(getString(R.string.already_was_friend));
                            textView.setBackgroundResource
                                    (R.drawable.button_bg_transparent_with_round_sides);
                        }

                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (!data.isFriend()){
                                    //加好友
                                    addFriend(data.getId());
                                }else {
                                    return ;
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
                    SubscribeUserInfo data = (SubscribeUserInfo) holder.data;
                    if (data == null){
                        return;
                    }
                    //进入个人名片
                    if (!TextUtils.isEmpty(data.getId())){
                        enterPersonalSpace(data.getId());
                    }
                }
            };
            setCurrAdapterViewHelper(listView, listViewHelper);
        }
    }

    private void addFriend(final String newFriendId) {

        Map<String, Object> params = new HashMap();
        params.put("MemberId", getMemeberId());
        params.put("NewFriendId", newFriendId);
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
                            return;
                        }
                        TipsHelper.showToast(getActivity(),
                                getString(R.string.friend_request_send_success));
                                getCurrAdapterViewHelper().update();
                    }
                };
        listener.setShowLoading(true);
        postRequest(ServerUrl.CONTACTS_ADD_FRIEND_URL, params, listener);


    }

    private void loadViews() {
        getPageHelper().clear();
        searchKeyword(this.keywordView.getText().toString());
    }

    private void searchKeyword(String keyword) {
        keyword = keyword.trim();
        if (TextUtils.isEmpty(keyword)) {
            return;
        }
        if (!keyword.equals(this.keyword)) {
            getCurrAdapterViewHelper().clearData();
            getPageHelper().clear();
        }
        this.keyword = keyword;

        UIUtils.hideSoftKeyboard(getActivity());

        Map<String, Object> params = new HashMap();
        params.put("Type", SubscribeSearchActivity.SUBSCRIPE_SEARCH_USER); //0: user & school, 1:school, 2:user
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("KeyWord", keyword);
        params.put("Pager", getPageHelper().getFetchingPagerArgs());
        DefaultPullToRefreshDataListener listener =
                new DefaultPullToRefreshDataListener<SubscribeUserListResult>(
                        SubscribeUserListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
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
                };
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.SUBSCRIBE_SEARCH_URL, params, listener);
    }

    private void updateViews(SubscribeUserListResult result) {
        if (getPageHelper().isFetchingPageIndex(result.getModel().getPager())) {
            processDataListResult(result);

            List<SubscribeUserInfo> list = result.getModel().getData();
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

    private void enterPersonalSpace(String memberId) {
        if (TextUtils.isEmpty(memberId)){
            return;
        }
        Bundle args = new Bundle();
        args.putString(PersonalSpaceActivity.EXTRA_USER_ID, memberId);
        Intent intent = new Intent(getActivity(), PersonalSpaceActivity.class);
        intent.putExtras(args);
        //个人信息页面
        startActivityForResult(intent, PersonalSpaceFragment.REQUEST_CODE_PERSONAL_SPACE);
    }

    /**
     * 过滤自己
     * @param result
     */
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

    private void enterPersonInfo(SubscribeUserInfo data) {
        Intent intent = new Intent(getActivity(), ContactsMemberDetailsActivity.class);
        Bundle args = new Bundle();
        if (data.isFriend()) {
            args.putInt(ContactsMemberDetailsActivity.EXTRA_MEMBER_TYPE,
                    ContactsMemberDetailsActivity.MEMBER_TYPE_FRIEND);
            args.putString(FriendDetailsFragment.Constants.EXTRA_FRIEND_ID, data.getId());
            args.putString(FriendDetailsFragment.Constants.EXTRA_FRIEND_ICON, data.getThumbnail());
            args.putString(FriendDetailsFragment.Constants.EXTRA_FRIEND_NAME, data.getName());
            args.putString(FriendDetailsFragment.Constants.EXTRA_FRIEND_NICKNAME, data.getName());
        } else {
            args.putInt(ContactsMemberDetailsActivity.EXTRA_MEMBER_TYPE,
                    ContactsMemberDetailsActivity.MEMBER_TYPE_PERSON);
            args.putString(PersonInfoFragment.Constants.EXTRA_PERSON_ID, data.getId());
            args.putString(PersonInfoFragment.Constants.EXTRA_PERSON_ICON, data.getThumbnail());
            args.putString(PersonInfoFragment.Constants.EXTRA_PERSON_NAME, data.getName());
            args.putString(PersonInfoFragment.Constants.EXTRA_PERSON_NICKNAME, data.getName());
            args.putBoolean(PersonInfoFragment.Constants.EXTRA_IS_FRIEND, data.isFriend());
        }
        intent.putExtras(args);
//        startActivity(intent);
        startActivityForResult(intent,
            FriendDetailsFragment.Constants.REQUEST_CODE_FRIEND_DETAILS);
    }

    @Override
    public void onClick(View v) {
//        if (v.getId() == R.id.search_btn) {
//            searchKeyword(this.keywordView.getText().toString());
//        } else {
            super.onClick(v);
//        }
    }

    public static void setHasRelationShipChanged(boolean hasRelationShipChanged) {
        ContactsSearchFriendFragment.hasRelationShipChanged = hasRelationShipChanged;
    }

    public static boolean hasRelationShipChanged() {
        return hasRelationShipChanged;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == FriendDetailsFragment.Constants.REQUEST_CODE_FRIEND_DETAILS) {
                if (data == null) {
                    return;
                }
//                if(getCurrAdapterViewHelper() != null) {
//                    getCurrAdapterViewHelper().clearData();
//                }
//                searchKeyword(this.keyword);
                getPageHelper().clear();
                searchKeyword(this.keywordView.getText().toString());
//                boolean changed = data.getBooleanExtra(
//                        FriendDetailsFragment.Constants.EXTRA_FRIEND_DETAILS_CHANGED, false);
//                if (!changed) {
//                    return;
//                }
//                //TODO: update contacts list
//                String id = data.getStringExtra(FriendDetailsFragment.Constants.EXTRA_FRIEND_ID);
//                List<ContactsSearchPersonInfo> list = getCurrAdapterViewHelper().getData();
//                ContactsSearchPersonInfo target = null;
//                for (int i = 0; i < list.size(); i++) {
//                    target = list.get(i);
//                    if (id.equals(target.getNewFriendId())) {
//                        break;
//                    }
//                }
//                if (target != null) {
//                    changed = data.getBooleanExtra(
//                            FriendDetailsFragment.Constants.EXTRA_FRIEND_DELETED, false);
//                    if (changed) {
//                        target.setIsFriend(false);
//                        return;
//                    }
//                }
            }
        }

        if (data == null){
            if (requestCode == PersonalSpaceFragment.REQUEST_CODE_PERSONAL_SPACE){
                //删除好友成功之后，需要刷新页面。
                if (PersonalSpaceFragment.hasUnbindFriendRelationship()){
                    PersonalSpaceFragment.setHasUnbindFriendRelationship(false);
                    //删除好友的时候，需要刷新消息列表。
                    setHasRelationShipChanged(true);
                    //刷新页面
                    refreshData();
                }

                //修改备注
                if (PersonalSpaceFragment.hasRemarkNameChanged()){
                    PersonalSpaceFragment.setHasRemarkNameChanged(false);
                    //修改备注的时候，需要刷新消息列表。
                    setHasRelationShipChanged(true);
                }
            }
        }
    }

}
