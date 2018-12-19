package com.galaxyschool.app.wawaschool.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.duowan.mobile.netroid.NetroidError;
import com.galaxyschool.app.wawaschool.AccountActivity;
import com.galaxyschool.app.wawaschool.PersonalSpaceActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.SchoolSpaceActivity;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.SubscribeSchoolListResult;
import com.galaxyschool.app.wawaschool.pojo.SubscribeUserInfo;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CampusOnlineAttentionFragment extends ContactsListFragment {

    public static final String TAG = CampusOnlineAttentionFragment.class.getSimpleName();
    public static final String LOAD_URL="loadUrl";
    private String loadUrl;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.subscribe_search, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getIntent();
        initViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadAttentionOriza();
    }
    private void getIntent(){
        if (getArguments()!=null){
            loadUrl=getArguments().getString(LOAD_URL);
        }
    }
    private void initViews() {
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            textView.setText(getString(R.string.join_orizatination));
        }
        ImageView backLeft= (ImageView) findViewById(R.id.contacts_header_left_btn);
        if (backLeft!=null){
            backLeft.setOnClickListener(this);
        }
        //隐藏搜索栏
        findViewById(R.id.contacts_search_bar_layout).setVisibility(View.GONE);
        PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);
        ListView listView = (ListView) findViewById(R.id.contacts_list_view);
        if (listView != null) {
            AdapterViewHelper listViewHelper = new AdapterViewHelper(getActivity(),
                    listView, R.layout.subscribe_list_item) {
                @Override
                public void loadData() {
                    loadAttentionOriza();
                }
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    final SchoolInfo data = (SchoolInfo) getDataAdapter().getItem(position);
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
                                AppSettings.getFileUrl(data.getSchoolLogo()), imageView, R.drawable.default_group_icon);
                    }
                    TextView textView = (TextView) view.findViewById(
                            R.id.contacts_item_title);
                    if (textView != null) {
                        textView.setText(data.getSchoolName());
                    }
                    //是否关注
                    textView= (TextView) view.findViewById(R.id.contacts_item_state);
                    if (textView!=null){
                        int state=data.getState();
                        if (state==0){
                            textView.setTextAppearance(getActivity(),R.style
                                    .txt_wawa_normal_green);
                            textView.setBackgroundResource(R.drawable.button_bg_with_round_sides);
                            textView.setText(getString(R.string.plus_follow));
                        }else{
                            textView.setTextAppearance(getActivity(), R.style
                                    .txt_wawa_normal_darkgray);
                            textView.setBackgroundResource
                                    (R.drawable.button_bg_transparent_with_round_sides);
                            textView.setText(getString(R.string.subscribed));
                        }
                        }
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //state=0表示没有关注
                                int state=data.getState();
                                if (state==0){
                                    addSubscribe(data.getSchoolId(),data.getSchoolName());
                                }
                            }
                        });
                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    SchoolInfo data = (SchoolInfo) getDataAdapter().getItem(position);
                    if (data!=null){
                        enterUserSpace(data);
                    }
                }
            };
            setCurrAdapterViewHelper(listView, listViewHelper);
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

    private void enterUserSpace(SchoolInfo data) {
        Bundle args = new Bundle();
        args.putString(SchoolSpaceActivity.EXTRA_SCHOOL_ID, data.getSchoolId());
        args.putString(SchoolSpaceActivity.EXTRA_SCHOOL_NAME, data.getSchoolName());
        Intent intent = new Intent(getActivity(), SchoolSpaceActivity.class);
        intent.putExtras(args);
        startActivity(intent);
    }

    private void enterPersonalSpace(SubscribeUserInfo data) {
        Bundle args = new Bundle();
        args.putString(PersonalSpaceActivity.EXTRA_USER_ID, data.getId());
        args.putString(PersonalSpaceActivity.EXTRA_USER_REAL_NAME, data.getName());
        Intent intent = new Intent(getActivity(), PersonalSpaceActivity.class);
        intent.putExtras(args);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.contacts_header_left_btn){
            CampusOnlineWebFragment fragment= (CampusOnlineWebFragment) getFragmentManager()
                    .findFragmentByTag(CampusOnlineWebFragment.TAG);
            fragment.setWebViewOnResume();
            popStack();
        }
    }
    private void loadAttentionOriza() {
        if (TextUtils.isEmpty(loadUrl)){
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("Pager", getPageHelper().getFetchingPagerArgs());
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("LiveShowUrl",loadUrl);
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
                        updateResult(result);
                    }

                    @Override
                    public void onError(NetroidError error) {
                        super.onError(error);
                    }
                };
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_CAMPUS_ONLINE_ORGANIZATION, params, listener);
    }
    private void updateResult(SubscribeSchoolListResult result){
        List<SchoolInfo> schoolInfos=result.getModel().getSubscribeNoList();
        if (schoolInfos==null||schoolInfos.size()<=0){
            TipMsgHelper.ShowLMsg(getActivity(),getString(R.string.no_data));
            return;
        }
        if (getPageHelper().isFetchingPageIndex(result.getModel().getPager())) {
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
                getCurrAdapterViewHelper().getData().addAll(schoolInfos);
                getCurrAdapterView().setSelection(position);
            } else {
                getCurrAdapterViewHelper().setData(schoolInfos);
            }
        }
    }
    //增加一个关注机构
    private void addSubscribe(final String subscribeUserId,final String schoolName) {
        if (TextUtils.isEmpty(subscribeUserId)){
            return;
        }
        if (getUserInfo() == null){
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("SchoolId", subscribeUserId);
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
                        }
                        TipsHelper.showToast(getActivity(),R.string.subscribe_success);
                        //如果关注机构成功校园空间的机构更新--以广播的形式发送
                        MySchoolSpaceFragment.sendBrocast(getActivity());
                        if (getCurrAdapterViewHelper().hasData()){
                            List<SchoolInfo> list= getCurrAdapterViewHelper().getData();
                            if (list != null && list.size() > 0){
                                for (SchoolInfo info : list){
                                    if (info.getSchoolId().equals(subscribeUserId)){
                                        info.setState(1);
                                        break;
                                    }
                                }
                                getCurrAdapterViewHelper().update();
                            }
                        }
                    }
                };
        listener.setShowLoading(true);
        String  serverUrl= ServerUrl.SUBSCRIBE_ADD_SCHOOL_URL;
        RequestHelper.sendPostRequest(getActivity(), serverUrl, params, listener);
    }
}
