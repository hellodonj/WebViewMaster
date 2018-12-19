package com.galaxyschool.app.wawaschool.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.Note.OnlineMediaPaperActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfoListResult;

import java.util.HashMap;
import java.util.Map;

public class PlatformResourceListFragment extends PlatformResourceListBaseFragment {

    public static final String TAG = PlatformResourceListFragment.class.getSimpleName();

    public interface Constants {
        public static final String EXTRA_CHANNEL_TYPE = "channelType";

        public static final int CHANNEL_TYPE_NOTICE = NewResourceInfo.TYPE_PLATFORM_NOTICE;
        public static final int CHANNEL_TYPE_NEWS = NewResourceInfo.TYPE_PLATFORM_NEWS;
    }

    protected int channelType;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        refreshData();
    }

    private void refreshData() {
        loadResourceList();
    }

    private void init() {
        channelType = getArguments().getInt(Constants.EXTRA_CHANNEL_TYPE);
        initViews();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initViews() {
        TextView textView = ((TextView) findViewById(R.id.contacts_header_title));
        if (textView != null) {
            if (channelType == Constants.CHANNEL_TYPE_NOTICE) {
                textView.setText(R.string.notices);
            } else if (channelType == Constants.CHANNEL_TYPE_NEWS) {
                textView.setText(R.string.news);
            }
        }
    }

    @Override
    protected void loadResourceList(String keyword) {
        keyword = keyword.trim();
        if (!keyword.equals(this.keyword)) {
            getCurrAdapterViewHelper().clearData();
            getPageHelper().clear();
        }
        this.keyword = keyword;
        if (getUserInfo() == null){
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("KeyWord", keyword);
        params.put("Pager", getPageHelper().getFetchingPagerArgs());
        DefaultPullToRefreshDataListener listener =
                new DefaultPullToRefreshDataListener<NewResourceInfoListResult>(
                        NewResourceInfoListResult.class) {
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
                updateResourceListView(getResult());
            }
        };
        String serverUrl = null;
        if (channelType == Constants.CHANNEL_TYPE_NOTICE) {
            serverUrl = ServerUrl.GET_PLATFORM_NOTICE_LIST_URL;
        } else if (channelType == Constants.CHANNEL_TYPE_NEWS) {
            serverUrl = ServerUrl.GET_PLATFORM_NEWS_LIST_URL;
        }
        if (serverUrl != null) {
            RequestHelper.sendPostRequest(getActivity(), serverUrl, params, listener);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null){
            //刷新帖子
            if (requestCode == CampusPatrolPickerFragment.EDIT_NOTE_DETAILS_REQUEST_CODE){

                //帖子打开后返回列表，需要手动更新阅读人数（手动累加）
                updateReaderNumber(itemId);

                //帖子内容改变需要刷新
                if (OnlineMediaPaperActivity.hasContentChanged()){
                    OnlineMediaPaperActivity.setHasContentChanged(false);
                    //刷新帖子
                    refreshData();
                }
            }
        }
    }
}
