package com.galaxyschool.app.wawaschool.fragment;

import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfoListResult;

import java.util.HashMap;
import java.util.Map;

public class ClassResourceListFragment extends ClassResourceListBaseFragment {

    public static final String TAG = ClassResourceListFragment.class.getSimpleName();

    @Override
    public void onResume() {
        super.onResume();
//        if (getUserVisibleHint()) {
//            getPageHelper().clear();
////            if (!getCurrAdapterViewHelper().hasData()) {
//            loadResourceList();
//            }
//        }
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
        params.put("ByType", 1); // all class resources
        params.put("ClassId", classId);
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
