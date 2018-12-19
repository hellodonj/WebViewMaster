package com.galaxyschool.app.wawaschool.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.Request;
import com.galaxyschool.app.wawaschool.CampusOnlineWebActivity;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.Note.OnlineMediaPaperActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.ReporterIdentityActivity;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.fragment.resource.NewResourcePadAdapterViewHelper;
import com.lqwawa.lqbaselib.net.ThisStringRequest;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.CampusOnline;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfoListResult;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BroaccastNoteFragment extends ContactsListFragment {

    public static final String TAG = BroaccastNoteFragment.class.getSimpleName();
    private static final int MAX_BOOKS_PER_ROW = 2;
    private boolean isFromCampusOnline;
    private LinearLayout reporterlayout;
    private String url;
    private SchoolInfo schoolInfo;
    private boolean isFromSchool;
    private int page;
    private String loadSchoolUrl;

    public interface OpenType {
        String IS_FROM_CAMPUS_ONLINE = "isFromCampusOnline";
    }

    public interface Contacts {
        String EXTRA_CONTENT_URL = "url";
        String EXTRA_SCHOOL_INFO = "school_info";
    }

    private boolean shouldHiddenHeaderView = false;

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_broadcast_note, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getIntent();
        initViews();
        refreshData();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void refreshData() {
        if (isFromCampusOnline) {
            loadCampusOnlineData();
        } else {
            loadNoticeBooks();
        }
    }

    private void initNoticeBooksGridview() {
        GridView gridView = (GridView) findViewById(R.id.book_grid_view);
        if (gridView != null) {
            gridView.setBackgroundColor(getResources().getColor(R.color.white));
            int padding = (int) (10 * MyApplication.getDensity());
            gridView.setPadding(0, padding, 0, padding);
            gridView.setNumColumns(MAX_BOOKS_PER_ROW);
            AdapterViewHelper adapterViewHelper = new NewResourcePadAdapterViewHelper(
                    getActivity(), gridView) {
                @Override
                public void loadData() {
                    loadNoticeBooks();
                }


                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        return;
                    }
                    NewResourceInfo data = (NewResourceInfo) holder.data;
                    if (data != null) {
                        ActivityUtils.openOnlineNote(getActivity(), data
                                .getCourseInfo(), false, false);
                    }
                }
            };
            setCurrAdapterViewHelper(gridView, adapterViewHelper);
        }
    }


    private void loadNoticeBooks() {
        Map<String, Object> params = new HashMap();
        params.put("Pager", getPageHelper().getFetchingPagerArgs());
        DefaultPullToRefreshDataListener listener =
                new DefaultPullToRefreshDataListener<NewResourceInfoListResult>(
                        NewResourceInfoListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        NewResourceInfoListResult result = getResult();
                        if (result == null || !result.isSuccess()
                                || result.getModel() == null) {
                            return;
                        }
                        updateNoteBookListView(result);
                    }
                };
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_BROADCAST_LIST_URL, params, listener);
    }

    private void updateNoteBookListView(NewResourceInfoListResult result) {
        if (getPageHelper().isFetchingPageIndex(result.getModel().getPager())) {
            List<NewResourceInfo> list = result.getModel().getData();
            if (getPageHelper().isFetchingFirstPage()) {
                if (getCurrAdapterViewHelper().hasData()) {
                    getCurrAdapterViewHelper().clearData();
                }
            }
            if (list == null || list.size() <= 0) {
                if (getPageHelper().isFetchingFirstPage()) {
                    TipsHelper.showToast(getActivity(),
                            getString(R.string.no_data));
                } else {
                    TipsHelper.showToast(getActivity(),
                            getString(R.string.no_more_data));
                }
                return;
            }
            getPageHelper().updateByPagerArgs(result.getModel().getPager());
            getPageHelper().setCurrPageIndex(
                    getPageHelper().getFetchingPageIndex());
            if (getCurrAdapterViewHelper().hasData()) {
                getCurrAdapterViewHelper().getData().addAll(list);
                getCurrAdapterViewHelper().update();
            } else {
                getCurrAdapterViewHelper().setData(list);
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.linearLayout) {
            enterReporterPermissionUi();
        }
    }

    private void enterReporterPermissionUi() {
        Intent intent = new Intent(getActivity(), ReporterIdentityActivity.class);
        intent.putExtra(ReporterIdentityActivity.SCHOOLINFO, (Serializable) schoolInfo);
        startActivity(intent);
    }

    private void getIntent() {
        if (getArguments() != null) {
            isFromCampusOnline = getArguments().getBoolean(OpenType.IS_FROM_CAMPUS_ONLINE, false);
            url = getArguments().getString(Contacts.EXTRA_CONTENT_URL);
            schoolInfo = (SchoolInfo) getArguments().getSerializable(Contacts.EXTRA_SCHOOL_INFO);
            if (!TextUtils.isEmpty(url)) {
                isFromSchool = true;
            }
            //头布局隐藏
            shouldHiddenHeaderView = getArguments().getBoolean(HappyLearningFragment
                    .SHOULD_HIDDEN_HEADER_VIEW);
        }
    }

    private void initViews() {
        //头布局
        View headerView = findViewById(R.id.contacts_header_layout);
        if (headerView != null) {
            if (shouldHiddenHeaderView) {
                headerView.setVisibility(View.GONE);
            } else {
                headerView.setVisibility(View.VISIBLE);
            }
        }

        ImageView imageView = ((ImageView) findViewById(R.id.contacts_header_left_btn));
        reporterlayout = (LinearLayout) findViewById(R.id.linearLayout);
        imageView.setVisibility(View.VISIBLE);
        imageView.setOnClickListener(this);
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (isFromCampusOnline) {
            if (!isFromSchool) {
                textView.setText(getString(R.string.campus_now_direct));
            } else {
                if (schoolInfo != null) {
                    textView.setText(schoolInfo.getSchoolName() + getString(R.string
                            .now_direct));
                    //如果当前用户拥有管理官的权限
                    if (schoolInfo.isLiveShowMgr()) {
                        reporterlayout.setVisibility(View.VISIBLE);
                        reporterlayout.setOnClickListener(this);
                    }
                }
            }
        } else {
            textView.setText(getString(R.string.broadcast_hall));
        }
        final PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        pullToRefreshView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        setPullToRefreshView(pullToRefreshView);
        if (isFromCampusOnline) {
            initCampusOnlineGridview();
        } else {
            initNoticeBooksGridview();
        }
    }

    private void initCampusOnlineGridview() {
        final GridView gridView = (GridView) findViewById(R.id.book_grid_view);
        if (gridView != null) {
            final int columns = 1;
            gridView.setNumColumns(columns);
            final int paddingTop = (int) (10 * MyApplication.getDensity());
            gridView.setVerticalSpacing(paddingTop);
            AdapterViewHelper adapterViewHelper = new AdapterViewHelper(getActivity(),
                    gridView, R.layout.resource_item_campus_tv) {
                @Override
                public void loadData() {
                    loadCampusOnlineData();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    view.setPadding(0, paddingTop, 0, 0);
                    CampusOnline data = (CampusOnline) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;

                    TextView textView = (TextView) view.findViewById(R.id.resource_title);
                    if (textView != null) {
                        //左对齐
                        textView.setGravity(Gravity.LEFT);
                        textView.setText(data.getLname());
                    }
                    FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.resource_frameLayout);
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) frameLayout.getLayoutParams();
                    WindowManager wm = (WindowManager) getActivity()
                            .getSystemService(Context.WINDOW_SERVICE);
                    int windowWith = wm.getDefaultDisplay().getWidth();//屏幕宽度
                    int itemWidth = (windowWith - getActivity().getResources().
                            getDimensionPixelSize(R.dimen.resource_gridview_padding)
                            * (columns + 1)) / columns;
                    params.width = itemWidth;
                    params.height = params.width * 2 / 5;
                    frameLayout.setLayoutParams(params);
                    params = (LinearLayout.LayoutParams) textView.getLayoutParams();
                    params.width = itemWidth;
                    textView.setLayoutParams(params);
                    ImageView imageView = (ImageView) view.findViewById(R.id.resource_thumbnail);
                    if (imageView != null) {
                        MyApplication.getThumbnailManager(getActivity()).displayThumbnailWithDefault(
                                data.getLimg(), imageView, R.drawable.icon_cmapus_live_default_phone);
                    }
                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) return;
                    CampusOnline campusOnline = (CampusOnline) holder.data;
                    if (campusOnline != null) {
                        Intent intent = new Intent(getActivity(), CampusOnlineWebActivity.class);
                        if (isFromSchool) {
                            intent.putExtra(CampusOnlineWebFragment.Constants.EXTRA_SCHOOL_INFO, schoolInfo);
                        }
                        intent.putExtra(CampusOnlineWebFragment.Constants.EXTRA_CONTENT_URL,
                                campusOnline.getLink());
                        intent.putExtra(CampusOnline.class.getSimpleName(), campusOnline);
                        getActivity().startActivity(intent);
                    }
                }
            };
            setCurrAdapterViewHelper(gridView, adapterViewHelper);
        }
    }

    //加载校园直播台的数据
    private void loadCampusOnlineData() {
        page = getPageHelper().getFetchingPageIndex();
        if (page == 0) {
            pageHelper.setFetchingPageIndex(page + 1);
            page = getPageHelper().getFetchingPageIndex();
        }
        if (!isFromSchool) {
            this.url = ServerUrl.GET_CAMPUS_ONLINE_LIST_BASE_URL + "&per=10&page=" + page;
        } else {
            parserUrl(url, page);
        }
        ThisStringRequest request = new ThisStringRequest(Request.Method.GET, this.url, new
                Listener<String>() {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        upDateCampusOnlineData(jsonString);
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
                        super.onFinish();
                        dismissLoadingDialog();
                    }
                });
        request.addHeader("Accept-Encoding", "*");
        request.start(getActivity());
    }

    private void upDateCampusOnlineData(String jsonString) {
        if (TextUtils.isEmpty(jsonString)) return;
        List<CampusOnline> campusOnLineData = null;
        try {
            //返回的jsonString有可能不是json格式的
            campusOnLineData = JSON.parseArray(jsonString, CampusOnline.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (campusOnLineData == null || campusOnLineData.size() <= 0) {
            TipsHelper.showToast(getActivity(), getString(R.string.no_more_data));
            return;
        }
        if (getPageHelper().getFetchingPageIndex() == 1) {
            if (getCurrAdapterViewHelper().hasData()) {
                getCurrAdapterViewHelper().clearData();
            }
        }
        getPageHelper().setCurrPageIndex(
                getPageHelper().getFetchingPageIndex());
        if (getCurrAdapterViewHelper().hasData()) {
            getCurrAdapterViewHelper().getData().addAll(campusOnLineData);
            getCurrAdapterViewHelper().update();
        } else {
            getCurrAdapterViewHelper().setData(campusOnLineData);
        }
    }

    private void parserUrl(String url, int page) {
        if (TextUtils.isEmpty(loadSchoolUrl)) {
            StringBuilder builder = new StringBuilder();
            if (url.contains("?")) {
                url = url.substring(url.indexOf("?") + 1);
                String[] urlArray = url.split("&");
                for (int i = 0; i < urlArray.length; i++) {
                    String flag = urlArray[i];
                    if (flag.contains("schoolshowId")) {
                        url = flag.substring(flag.indexOf("=") + 1);
                    }
                }
                builder.append("&appid=");
                builder.append(url);
                builder.append("&");
                builder.append("page=");
                loadSchoolUrl = builder.toString();
            }
        }
        this.url = ServerUrl.GET_CAMPUS_ONLINE_LIST_BASE_URL + loadSchoolUrl + page;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //帖子内容改变需要刷新页面
        if (data == null) {
            if (requestCode == CampusPatrolPickerFragment.EDIT_NOTE_DETAILS_REQUEST_CODE) {
                if (OnlineMediaPaperActivity.hasContentChanged()) {
                    OnlineMediaPaperActivity.setHasContentChanged(false);
                    refreshData();
                }
            }
        }
    }
}



