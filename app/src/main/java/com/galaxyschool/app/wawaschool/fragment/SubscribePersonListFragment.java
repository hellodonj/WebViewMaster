package com.galaxyschool.app.wawaschool.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.duowan.mobile.netroid.NetroidError;
import com.galaxyschool.app.wawaschool.*;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.PersonInfo;
import com.galaxyschool.app.wawaschool.pojo.SubscribePersonListResult;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.slidelistview.SlideListView;
import com.galaxyschool.app.wawaschool.views.sortlistview.ClearEditText;
import com.galaxyschool.app.wawaschool.views.sortlistview.SideBar;
import com.galaxyschool.app.wawaschool.views.sortlistview.SortListViewHelper;
import com.galaxyschool.app.wawaschool.views.sortlistview.SortModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubscribePersonListFragment extends ContactsListFragment {

    public static final String TAG = SubscribePersonListFragment.class.getSimpleName();

    private SubscribePersonListResult dataListResult;
    private TextView keywordView;
    private String keyword = "";
    public static final int REQUEST_CODE_SUBSCRIBE_PERSONAL_LIST = 1508;
    private static boolean hasFriendContentChanged;//删除好友或者是修改备注名

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.contacts_sortlist, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        refreshData();
    }

    private void refreshData(){
        loadViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        //从搜索个人页面返回
        if (SubscribeSearchFragment.hasPersonFocusChanged()) {
            SubscribeSearchFragment.setHasPersonFocusChanged(false);
            //刷新页面
            refreshData();
        }
    }

    private void initViews() {

        //搜索
        ClearEditText editText = (ClearEditText) findViewById(R.id.search_keyword);
        if (editText != null) {
            editText.setHint(getString(R.string.subscribe_search_user));
            editText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
            editText.setOnClearClickListener(new ClearEditText.OnClearClickListener() {
                @Override
                public void onClearClick() {
                    keyword = "";
                    getCurrAdapterViewHelper().clearData();
                    getPageHelper().clear();
                    loadContacts(keywordView.getText().toString());
                }
            });
            editText.setInputType(InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
//            UIUtils.showSoftKeyboardValid(getActivity());
//            editText.requestFocus();
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
                        loadContacts(keywordView.getText().toString());
                    }
                }
            });
            view.setVisibility(View.VISIBLE);
        }
        view = findViewById(R.id.contacts_search_bar_layout);
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }

        //刷新
        PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);

        SlideListView listView = (SlideListView) findViewById(R.id.contacts_list_view);
        if (listView != null) {
            listView.setSlideMode(SlideListView.SlideMode.NONE);

            SortListViewHelper adapterViewHelper = new SortListViewHelper(getActivity(),
                    listView, R.layout.contacts_sortlist_item, R.id.contacts_item_catalog,
                    (SideBar) findViewById(R.id.contacts_sort_sidebar),
                    (TextView) findViewById(R.id.contacts_sort_tips)) {
                @Override
                public void loadData() {
                    loadContacts(keywordView.getText().toString());
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
                    PersonInfo data = (PersonInfo) sortModel.getData();
                    ImageView imageView = (ImageView) view.findViewById(R.id.contacts_item_icon);
                    if (imageView != null) {
                        getThumbnailManager().displayUserIcon(
                                AppSettings.getFileUrl(data.getHeadPicUrl()), imageView);
                    }
                    TextView textView = (TextView) view.findViewById(R.id.contacts_item_title);
                    if (textView != null) {
                        textView.setText(data.getRealName());
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
                    enterPersonalSpace(((PersonInfo) ((SortModel) holder.data).getData()));
                }
            };
            adapterViewHelper.showSideBar(true);

            setCurrAdapterViewHelper(listView, adapterViewHelper);
        }
    }

    private void loadViews() {
        getPageHelper().clear();
        loadContacts(keywordView.getText().toString());
    }

    private void updateViews(SubscribePersonListResult result) {
        List<PersonInfo> list = result.getModel().getData();
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
    }

    private List<SortModel> processDataList(List<PersonInfo> list) {
        if (list != null && list.size() > 0) {
            List<SortModel> sortList = new ArrayList<SortModel>();
            SortModel sortModel = null;
            for (PersonInfo obj : list) {
                sortModel = new SortModel();
                sortModel.setName(obj.getRealName());
                sortModel.setSortLetters(obj.getFirstLetter());
                sortModel.setData(obj);
                sortList.add(sortModel);
            }
            return sortList;
        }
        return null;
    }

    private void loadContacts(String keyword) {
        keyword = keyword.trim();

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
        params.put("KeyWords", keyword);//按关键字搜索，本地搜索。

        DefaultPullToRefreshDataListener listener =
                new DefaultPullToRefreshDataListener<SubscribePersonListResult>(
                        SubscribePersonListResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if(getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                SubscribePersonListResult result = getResult();
                if (result == null || !result.isSuccess()
                        || result.getModel() == null) {
                    return;
                }
                updateViews(result);
            }

                    @Override
                    public void onError(NetroidError error) {
                        if(getActivity() == null) {
                            return;
                        }
                        super.onError(error);
                    }
                };
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.SUBSCRIBE_PERSON_LIST_URL, params, listener);
    }
    
    private void enterPersonalSpace(PersonInfo data) {
        Bundle args = new Bundle();
        args.putString(PersonalSpaceActivity.EXTRA_USER_ID, data.getMemberId());
        args.putString(PersonalSpaceActivity.EXTRA_USER_REAL_NAME, data.getRealName());
        Intent intent = new Intent(getActivity(), PersonalSpaceActivity.class);
        intent.putExtras(args);
        //个人信息页面
        startActivityForResult(intent,PersonalSpaceFragment.REQUEST_CODE_PERSONAL_SPACE);
    }

    public static void setHasFriendContentChanged(boolean hasFriendContentChanged) {
        SubscribePersonListFragment.hasFriendContentChanged = hasFriendContentChanged;
    }

    public static boolean hasFriendContentChanged() {
        return hasFriendContentChanged;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null){
            if (requestCode == PersonalSpaceFragment.REQUEST_CODE_PERSONAL_SPACE){
                //个人信息页面
                if (PersonalSpaceFragment.hasFocusChanged()){
                    PersonalSpaceFragment.setHasFocusChanged(false);
                    //刷新页面
                    refreshData();
                }

                //修改备注
                if (PersonalSpaceFragment.hasRemarkNameChanged()){
                    PersonalSpaceFragment.setHasRemarkNameChanged(false);
                    //刷新标志位
                    setHasFriendContentChanged(true);
                }

                //删除好友
                if (PersonalSpaceFragment.hasUnbindFriendRelationship()){
                    PersonalSpaceFragment.setHasUnbindFriendRelationship(false);
                    //刷新标志位
                    setHasFriendContentChanged(true);
                }
            }
        }
    }
}
