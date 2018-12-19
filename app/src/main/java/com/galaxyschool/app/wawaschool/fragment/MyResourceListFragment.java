package com.galaxyschool.app.wawaschool.fragment;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.fragment.resource.NewResourceAdapterViewHelper;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfoListResult;
import java.util.HashMap;
import java.util.Map;

public class MyResourceListFragment extends ClassResourceListBaseFragment {

    public static final String TAG = MyResourceListFragment.class.getSimpleName();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
//            if (!getCurrAdapterViewHelper().hasData()) {
            if (resourceListResult == null) {
                loadResourceList();
            }
        }
    }

    private void initViews() {
        View view = findViewById(R.id.new_btn);
        if (view != null) {
            view.setVisibility(View.GONE);
        }

        GridView listView = (GridView) findViewById(R.id.resource_list_view);
        if (listView != null) {
            AdapterViewHelper adapterViewHelper = new NewResourceAdapterViewHelper(
                    getActivity(), listView) {
                @Override
                public void loadData() {
                    loadResourceList();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    NewResourceInfo data = (NewResourceInfo) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    ImageView imageView = (ImageView) view.findViewById(R.id.resource_indicator);
                    if (imageView != null) {
                        imageView.setVisibility(data.isRead() ? View.GONE : View.VISIBLE);
                    }
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        return;
                    }
                    NewResourceInfo data = (NewResourceInfo) holder.data;
                    if (data != null) {
                        markResourceAsRead(data);
                    }
                    super.onItemClick(parent, view, position, id);
                }
            };
            setCurrAdapterViewHelper(listView, adapterViewHelper);
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
        params.put("ByType", 2); // my class resources
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("ActionType", channelType);
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
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_CLASS_RESOURCE_LIST_URL, params, listener);
    }
}
