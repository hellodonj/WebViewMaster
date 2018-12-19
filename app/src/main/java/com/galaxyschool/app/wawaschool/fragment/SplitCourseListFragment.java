package com.galaxyschool.app.wawaschool.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.Request;
import com.galaxyschool.app.wawaschool.PictureBooksDetailActivity;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.PassParamhelper;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.fragment.resource.NewResourcePadAdapterViewHelper;
import com.galaxyschool.app.wawaschool.pojo.TaskOrderNewResourceInfo;
import com.lqwawa.lqbaselib.net.ThisStringRequest;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.SplitCourseInfoListResult;
import com.galaxyschool.app.wawaschool.pojo.weike.SplitCourseInfo;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangchao on 5/28/16.
 */
public class SplitCourseListFragment extends ContactsListFragment {

    public static final String TAG = SplitCourseListFragment.class.getSimpleName();

    public interface Constants {
        String EXTRA_COURSE_ID = "course_id";
        String EXTRA_COURSE_NAME = "course_name";
        String EXTRA_COURSE_ISPUBLIC_RES = "course_ispublic_res";
        String EXTRA_COURSE_IS_CHOICE_LIB = "course_ischoice_lib";
        String EXTRA_COURSE_IS_HIDEDOWNLOAD_BTN = "is_need_hide_download_btn";
        String EXTRA_COURSE_IS_TASKORDER_SPLIT_DETAIL = "is_task_order_split_detail";
        int MAX_GRID_COLUMN_NUM = 2;
    }

    private String courseId;
    private String courseName;
    private boolean isPublicRes = true;
    int fromType;
    private PassParamhelper mParam;
    private boolean isFromChoiceLib;
    private boolean isHideDownLoadBtn = true;
    private boolean isTaskOrderSplitDetail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_split_course_list, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void initViews() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            courseId = bundle.getString(Constants.EXTRA_COURSE_ID);
            courseName = bundle.getString(Constants.EXTRA_COURSE_NAME);
            isPublicRes = bundle.getBoolean(Constants.EXTRA_COURSE_ISPUBLIC_RES, true);
            isFromChoiceLib = bundle.getBoolean(Constants.EXTRA_COURSE_IS_CHOICE_LIB, false);
            fromType = bundle.getInt(PictureBooksDetailActivity.FROM_SOURCE_TYPE,
                    PictureBooksDetailFragment.Constants.FROM_OTHRE);
            mParam = (PassParamhelper) bundle.getSerializable(PassParamhelper.class.getSimpleName());
            isHideDownLoadBtn = bundle.getBoolean(Constants.EXTRA_COURSE_IS_HIDEDOWNLOAD_BTN,
                    true);
            isTaskOrderSplitDetail = bundle.getBoolean(Constants.EXTRA_COURSE_IS_TASKORDER_SPLIT_DETAIL, false);
        }

        TextView textView = ((TextView) findViewById(R.id.contacts_header_title));
        if (textView != null) {
            textView.setText(courseName);
        }
        textView = ((TextView) findViewById(R.id.contacts_header_right_btn));
        if (textView != null) {
            textView.setVisibility(View.INVISIBLE);
        }

        final PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        setStopPullUpState(true);
        setStopPullDownState(true);
        setPullToRefreshView(pullToRefreshView);

        GridView gridView = (GridView) findViewById(R.id.book_grid_view);
        if (gridView != null) {
            gridView.setNumColumns(Constants.MAX_GRID_COLUMN_NUM);
            AdapterViewHelper adapterViewHelper = new NewResourcePadAdapterViewHelper(
                    getActivity(), gridView) {
                @Override
                public void loadData() {
                    if (isTaskOrderSplitDetail) {
                        loadTaskDatas();
                    } else {
                        loadSplitCourseList();
                    }
                }


                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        return;
                    }
                    NewResourceInfo data = (NewResourceInfo) holder.data;
                    if (data != null) {
                        if (isTaskOrderSplitDetail) {
                            ActivityUtils.openOnlineOnePage(getActivity(), data, true, null);
                        } else {
                            if (!isPublicRes) {
                                data.setIsPublicResource(isPublicRes);
                                data.setParentId(courseId);
                                data.setIsQualityCourse(isFromChoiceLib);
                                data.setHideDownLoadBtn(isHideDownLoadBtn);
                            }

                            Bundle bundle = null;
                            if (mParam != null) {
                                bundle = new Bundle();
                                bundle.putSerializable(PassParamhelper.class.getSimpleName(), mParam);
                            }

                            if (fromType == PictureBooksDetailActivity.FROM_CLOUD_SPACE) {
                                ActivityUtils.openCourseDetail(getActivity(), data,
                                        PictureBooksDetailActivity.FROM_CLOUD_SPACE, bundle);
                            } else {
                                ActivityUtils.openCourseDetail(getActivity(), data,
                                        PictureBooksDetailActivity.FROM_OTHRE, bundle);
                            }
                        }
                    }
                }
            };
            setCurrAdapterViewHelper(gridView, adapterViewHelper);
        }
    }

    private void loadData() {
        if (getCurrAdapterViewHelper().hasData()) {
            getCurrAdapterViewHelper().update();
        } else {
            if (isTaskOrderSplitDetail) {
                loadTaskDatas();
            } else {
                loadSplitCourseList();
            }
        }
    }

    private void loadSplitCourseList() {
        Map<String, Object> params = new HashMap();
        params.put("pid", courseId);
        RequestHelper.RequestListener listener =
                new RequestHelper.RequestResourceResultListener<SplitCourseInfoListResult>(
                        getActivity(), SplitCourseInfoListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()) {
                            return;
                        }
                        List<SplitCourseInfo> list = getResult().getData();
                        if (list == null || list.size() <= 0) {
                            return;
                        }
                        List<NewResourceInfo> resourceInfos = new ArrayList<NewResourceInfo>();
                        for (SplitCourseInfo info : list) {
                            if (info != null) {
                                NewResourceInfo newResourceInfo = info.getNewResourceInfo();
                                if (newResourceInfo != null) {
                                    resourceInfos.add(newResourceInfo);
                                }
                            }
                        }

                        getCurrAdapterViewHelper().clearData();
                        getCurrAdapterViewHelper().setData(resourceInfos);
                    }
                };
        listener.setShowLoading(true);
        RequestHelper.sendGetRequest(getActivity(),
                ServerUrl.SPLIT_COURSE_LIST_URL, params, listener);
    }

    private void loadTaskDatas() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("pid", courseId + "-23");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringBuilder builder = new StringBuilder();
        builder.append("?j=" + jsonObject.toString());
        ThisStringRequest request = new ThisStringRequest(
                Request.Method.GET, ServerUrl.GET_GUIDANCE_CARD_SPLIT_URL + builder.toString(), new Listener<String>() {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                parseData(jsonString);
            }

            @Override
            public void onError(NetroidError error) {
                if (getActivity() == null) {
                    return;
                }
                TipMsgHelper.ShowMsg(getActivity(), getString(R.string.network_error));
            }

            @Override
            public void onFinish() {
                if (getActivity() == null) {
                    return;
                }
                super.onFinish();
                dismissLoadingDialog();
            }
        });
        request.addHeader("Accept-Encoding", "*");
        request.start(getActivity());
    }

    private void parseData(String jsonString) {
        if (jsonString != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                if (jsonObject != null) {
                    int code = jsonObject.optInt("code");
                    if (code == 0) {
                        JSONArray jsonArray = jsonObject.optJSONArray("data");
                        if (jsonArray != null) {
                            List<TaskOrderNewResourceInfo> datas = JSON.parseArray(
                                    jsonArray.toString(), TaskOrderNewResourceInfo.class);
                            if (datas != null && datas.size() > 0) {
                                List<NewResourceInfo> items = new ArrayList<NewResourceInfo>();
                                for (TaskOrderNewResourceInfo tempInfo : datas) {
                                    NewResourceInfo item = TaskOrderNewResourceInfo.pase2NewResourceInfo(tempInfo);
                                    items.add(item);
                                }
                                getCurrAdapterViewHelper().setData(items);
                            } else {
                                if (getCurrAdapterViewHelper().hasData()) {
                                    getCurrAdapterViewHelper().clearData();
                                }
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
