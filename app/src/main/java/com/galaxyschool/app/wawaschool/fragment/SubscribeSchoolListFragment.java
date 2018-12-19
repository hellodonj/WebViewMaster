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
import com.galaxyschool.app.wawaschool.NewBookStoreActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.SchoolSpaceActivity;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.SubscribeSchoolListResult;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.slidelistview.SlideListView;
import com.galaxyschool.app.wawaschool.views.sortlistview.ClearEditText;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubscribeSchoolListFragment extends ContactsListFragment {

    public static final String TAG = SubscribeSchoolListFragment.class.getSimpleName();

    private SubscribeSchoolListResult dataListResult;
    private TextView keywordView;
    private String keyword = "";
    private boolean isFirstIn=true;
    private boolean isFromHappyStudy;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.school_contacts_list, null);
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
        //搜索页面
        if (SubscribeSearchFragment.hasSchoolFocusChanged()) {
            SubscribeSearchFragment.setHasSchoolFocusChanged(false);
            //刷新页面
            refreshData();
        }
    }

    private void refreshData(){
        loadViews();
    }

    void initViews() {
        if (getArguments() != null) {
            isFromHappyStudy = getArguments().getBoolean(
                    AttendOrAttentionSchoolFragment.IS_FROM_HAPPY_STUDY);
        }
        //搜索
        ClearEditText editText = (ClearEditText) findViewById(R.id.search_keyword);
        if (editText != null) {
            editText.setHint(getString(R.string.subscribe_search_school));
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
//            UIUtils.showSoftKeyboardValid(getActivity());
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

        //刷新
        PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);

        SlideListView listView = (SlideListView) findViewById(R.id.contacts_list_view);
        if (listView != null) {
            listView.setSlideMode(SlideListView.SlideMode.NONE);

            AdapterViewHelper listViewHelper = new AdapterViewHelper(getActivity(),
                    listView, R.layout.contacts_list_item) {
                @Override
                public void loadData() {
//                    loadContacts();
                    searchKeyword(keywordView.getText().toString());
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    final SchoolInfo data =
                            (SchoolInfo) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;
                    ImageView imageView = (ImageView) view.findViewById(R.id.contacts_item_icon);
                    if (imageView != null) {
                        getThumbnailManager().displayUserIconWithDefault(
                                AppSettings.getFileUrl(data.getSchoolLogo()), imageView,
                                R.drawable.default_school_icon);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //头像点击总是要进入详情
                                enterSchoolSpace(data);
                            }
                        });

                    }
                    TextView textView = (TextView) view.findViewById(R.id.contacts_item_title);
                    if (textView != null) {
                        textView.setText(data.getSchoolName());
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
                    //只有是从快乐学习进入的，才进入资源库页面。
                    if (isFromHappyStudy){
                        enterSchoolResourceLibrary(((SchoolInfo)holder.data).getSchoolId());
                    }else {
                        enterSchoolSpace((SchoolInfo) holder.data);
                        //否则进入机构名片
                    }
                }
            };

            setCurrAdapterViewHelper(listView, listViewHelper);
        }
    }


    /**
     * 进入校本资源库
     * @param schoolId
     */
    private void enterSchoolResourceLibrary(String schoolId) {
        Intent intent = new Intent(getActivity(), NewBookStoreActivity.class);
        Bundle args = new Bundle();
        args.putString(NewBookStoreActivity.SCHOOL_ID,schoolId);
        intent.putExtras(args);
        startActivity(intent);
    }
    /**
     * 搜索本地资源
     * @param keyword
     */
    private void searchKeyword(String keyword) {
        keyword = keyword.trim();
        if (isFirstIn) {

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
        params.put("KeyWords", keyword);

        DefaultPullToRefreshListener listener =
                new DefaultPullToRefreshListener<SubscribeSchoolListResult>(
                        SubscribeSchoolListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        isFirstIn=false;
                        if(getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        SubscribeSchoolListResult result = getResult();
                        if (result == null || !result.isSuccess()
                                || result.getModel() == null) {
                            return;
                        }
                        updateViews(result);
                    }

                    @Override
                    public void onError(NetroidError error) {
                        isFirstIn=false;
                        if(getActivity() == null) {
                            return;
                        }
                        super.onError(error);
                    }
                };
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.SUBSCRIBE_SCHOOL_LIST_URL, params, listener);

    }

    private void loadViews() {
//        if (getCurrAdapterViewHelper().hasData()) {
//            getCurrAdapterViewHelper().update();
//        } else {
//            loadContacts();
        isFirstIn=true;
        getPageHelper().clear();
        searchKeyword(this.keywordView.getText().toString());
//        }
    }

    private void updateViews(SubscribeSchoolListResult result) {
        List<SchoolInfo> list = result.getModel().getSubscribeNoList();
        if (list == null || list.size() <= 0){
            TipsHelper.showToast(getActivity(),
                    getString(R.string.no_data));
        }
        getCurrAdapterViewHelper().setData(list);
        dataListResult = result;
    }

    private void loadContacts() {
        if (getUserInfo() == null){
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("KeyWords", keyword);

        DefaultPullToRefreshListener listener =
                new DefaultPullToRefreshListener<SubscribeSchoolListResult>(
                        SubscribeSchoolListResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if(getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                SubscribeSchoolListResult result = getResult();
                if (result == null || !result.isSuccess()
                        || result.getModel() == null) {
                    return;
                }
                updateViews(result);
            }
        };
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.SUBSCRIBE_SCHOOL_LIST_URL, params, listener);
    }
    
    private void enterSchoolSpace(SchoolInfo data) {
        Bundle args = new Bundle();
        args.putString(SchoolSpaceActivity.EXTRA_SCHOOL_ID, data.getSchoolId());
        args.putString(SchoolSpaceActivity.EXTRA_SCHOOL_NAME, data.getSchoolName());
        Intent intent = new Intent(getActivity(), SchoolSpaceActivity.class);
        intent.putExtras(args);
        //school space
        startActivityForResult(intent,SchoolSpaceFragment.REQUEST_CODE_SCHOOL_SPACE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null){
            //机构名片
            if (requestCode == SchoolSpaceFragment.REQUEST_CODE_SCHOOL_SPACE){
                if (SchoolSpaceFragment.hasFocusChanged()){
                    SchoolSpaceFragment.setHasFocusChanged(false);
                    //刷新页面
                    refreshData();
                }
            }
        }
    }
}
