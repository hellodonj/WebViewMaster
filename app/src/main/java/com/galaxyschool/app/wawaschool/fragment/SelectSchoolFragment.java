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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.galaxyschool.app.wawaschool.pojo.SubscribeSchoolListResult;
import com.galaxyschool.app.wawaschool.pojo.SubscribeUserInfo;
import com.galaxyschool.app.wawaschool.pojo.SubscribeUserListResult;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.sortlistview.ClearEditText;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectSchoolFragment extends ContactsListFragment {

    public static final String TAG = SelectSchoolFragment.class.getSimpleName();
    private SchoolInfo selectSchool=null;
    private boolean isMore=false;//是否是更多
    private TextView rightBtn;
    private TextView keywordView;
    private String keyword = "";
    private   LinearLayout searchLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_school, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }
    private void initMoreView(){
        final PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        if (pageHelper!=null){
            pageHelper.setFetchingPageIndex(0);
        }
        setPullToRefreshView(pullToRefreshView);
        if(isMore){
            rightBtn.setVisibility(View.GONE);
            searchLayout.setVisibility(View.VISIBLE);
        }else{
            rightBtn.setVisibility(View.VISIBLE);
            searchLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDatas();
    }
    private void loadDatas(){
        if(isMore){
            loadAttendSchoolData();
        }else{
            loadAttentionSchoolData();
        }
    }
    private void loadAttendSchoolData() {
        String keyword = this.keywordView.getText().toString().trim();
        if (!keyword.equals(this.keyword)) {
            getCurrAdapterViewHelper().clearData();
            getPageHelper().clear();
        }
        this.keyword = keyword;
        Map<String, Object> params = new HashMap();
        params.put("KeyWord", keyword);
        if(!TextUtils.isEmpty(getMemeberId())){
            params.put("MemberId",getMemeberId());
        }
        params.put("Type", 1);
        params.put("Pager", getPageHelper().getFetchingPagerArgs());


        DefaultPullToRefreshDataListener listener =
                new DefaultPullToRefreshDataListener<SubscribeUserListResult>(
                        SubscribeUserListResult.class) {
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
                        updateAttendSchoolViews(getResult());
                    }
                };
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.SUBSCRIBE_SEARCH_URL, params, listener);
    }

    private void updateAttendSchoolViews(SubscribeUserListResult result) {
        if (getPageHelper().isFetchingPageIndex(result.getModel().getPager())) {
            List<SubscribeUserInfo> list = result.getModel().getData();
            List<SchoolInfo> datas = datas = new ArrayList<SchoolInfo>();
            ;
            if (list != null && list.size() > 0) {
                for (SubscribeUserInfo info : list) {
                    if (info != null) {
                        SchoolInfo schoolInfo = new SchoolInfo();
                        schoolInfo.setSchoolId(info.getId());
                        schoolInfo.setSchoolName(info.getName());
                        schoolInfo.setSchoolLogo(info.getThumbnail());
                        datas.add(schoolInfo);
                    }
                }
            }
            if (datas == null || datas.size() <= 0) {
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
                getCurrAdapterViewHelper().getData().addAll(datas);
                getCurrAdapterView().setSelection(position);
            } else {
                getCurrAdapterViewHelper().setData(datas);
            }
        }
    }

    private void loadAttentionSchoolData() {
        if (getUserInfo() == null){
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.SUBSCRIBE_SCHOOL_LIST_URL, params,
                new DefaultListener<SubscribeSchoolListResult>(
                        SubscribeSchoolListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        SubscribeSchoolListResult result = getResult();
                        if (result == null || !result.isSuccess() || result.getModel() == null ) {
                        } else {
                            updateAttentionSchoolView(result);
                        }
                    }
                });
    }

    private void updateAttentionSchoolView(SubscribeSchoolListResult result) {
        List<SchoolInfo> list = result.getModel().getSubscribeNoList();
        if (list == null || list.size() <= 0) {
            TipsHelper.showToast(getActivity(),
                    getString(R.string.no_data));
            if (getCurrAdapterViewHelper().hasData()) {
                getCurrAdapterViewHelper().clearData();
            }
            return;
        }
        getCurrAdapterViewHelper().setData(list);
    }
    private void init() {
        initViews();
    }

    private void initViews() {
        initNormalView();
        initListView();
    }
    private void initNormalView(){

        searchLayout = (LinearLayout) findViewById(R.id.search_layout);
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        textView.setText(R.string.select_school);
        findViewById(R.id.sure_view).setOnClickListener(this);
        findViewById(R.id.cancel_view).setOnClickListener(this);
        rightBtn = (TextView) findViewById(R.id.contacts_header_right_btn);
        rightBtn.setText(getString(R.string.more_school));
        rightBtn.setOnClickListener(this);

        ClearEditText editText = (ClearEditText) findViewById(R.id.search_keyword);
        if (editText != null) {
            editText.setHint(getString(R.string.hint_school));
            editText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        hideSoftKeyboard(getActivity());
                        loadDatas();
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
                    loadDatas();
                }
            });
            editText.setInputType(InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        }
        keywordView = editText;
        View view = findViewById(R.id.search_btn);
        if (view != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSoftKeyboard(getActivity());
                    loadDatas();
                }
            });
            view.setVisibility(View.VISIBLE);
        }
        view = findViewById(R.id.contacts_search_bar_layout);
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
        initMoreView();
    }
    private void initListView() {
        ListView listView = (ListView) findViewById(R.id.shool_list_view);
        if (listView != null) {
            AdapterViewHelper gridViewHelper = new AdapterViewHelper(getActivity(),
                    listView, R.layout.contacts_list_item_with_selector) {
                @Override
                public void loadData() {
                    loadDatas();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    final SchoolInfo data =
                            (SchoolInfo) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    ImageView imageView = (ImageView) view.findViewById(R.id.contacts_item_icon);
                    if (imageView != null) {
                        getThumbnailManager().displayUserIconWithDefault(AppSettings.getFileUrl(
                                data.getSchoolLogo()), imageView, R.drawable.default_school_icon);
                    }
                    TextView textView = (TextView) view.findViewById(
                            R.id.contacts_item_title);
                    if (textView != null) {
                        textView.setText(data.getSchoolName());
                    }
                    imageView = (ImageView) view.findViewById(R.id.contacts_item_selector);
                    if(selectSchool!=null&&data.getSchoolId().equals(selectSchool.getSchoolId())){
                        data.setIsSelect(true);
                        imageView.setSelected(true);
                    }else{
                        imageView.setSelected(false);
                    }
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;
                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null || holder.data == null) {
                        return;
                    }
                    SchoolInfo data = (SchoolInfo) holder.data;
                    List<SchoolInfo> list =getCurrAdapterViewHelper().getData();
                    for(SchoolInfo item :list){
                        if(item==data){
                            item.setIsSelect(!item.isSelect());
                            if(item.isSelect()){
                                selectSchool=item;
                            }else{
                                selectSchool=null;
                            }
                        }else{
                            item.setIsSelect(false);
                        }
                    }
                    getCurrAdapterViewHelper().update();
                }
            };
            setCurrAdapterViewHelper(listView, gridViewHelper);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.contacts_header_left_btn:
                if(!isMore){
                    finish();
                }else{
                    isMore=false;
                    enterMoreSchool();
                }
                break;
            case R.id.sure_view:
                sendSchool();
                break;
            case R.id.cancel_view:
                finish();
                break;
            case R.id.contacts_header_right_btn:
                isMore=true;
                enterMoreSchool();
                break;

        }
    }
    private void enterMoreSchool( ){
        initMoreView();
        loadDatas();
    }
    private void sendSchool(){
        if(selectSchool==null){
            TipsHelper.showToast(getActivity(),getString(R.string.please_select_school));
        }else{
            if(isMore){
                addSubscribe(selectSchool.getSchoolId());
            }
            Intent intent =new Intent();
            intent.putExtra(SchoolInfo.class.getSimpleName(),selectSchool);
            getActivity().setResult(Activity.RESULT_OK,intent);
            finish();
        }
    }
    private void addSubscribe(final String shcoolId) {
        if (TextUtils.isEmpty(shcoolId)){
            return;
        }
        if (getUserInfo() == null){
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("SchoolId", shcoolId);
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
                            //关注/取消关注成功后，向校园空间发广播
                            MySchoolSpaceFragment.sendBrocast(getActivity());

                        }
                    }
                };
        listener.setShowLoading(true);
        String serverUrl=ServerUrl.SUBSCRIBE_ADD_SCHOOL_URL;
        RequestHelper.sendPostRequest(getActivity(), serverUrl, params, listener);

    }
}
