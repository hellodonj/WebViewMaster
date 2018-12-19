package com.galaxyschool.app.wawaschool.fragment;

import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.Request;
import com.galaxyschool.app.wawaschool.AirClassroomDetailActivity;
import com.galaxyschool.app.wawaschool.CommonFragmentActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.lqwawa.lqbaselib.net.ThisStringRequest;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.Emcee;
import com.galaxyschool.app.wawaschool.pojo.EmceeList;
import com.galaxyschool.app.wawaschool.pojo.EmceeListResult;
import com.galaxyschool.app.wawaschool.pojo.PublishClass;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.sortlistview.ClearEditText;
import com.osastudio.common.utils.LQImageLoader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AirClassroomFragment extends ContactsListFragment implements View.OnClickListener {
    public static String TAG = AirClassroomFragment.class.getSimpleName();
    private TextView searchBtn;
    private EditText searchEditText;
    private RadioButton deleteAllClassRB;
    //班主任
    private boolean isHeadMaster;
    //是不是老师
    private boolean isTeacher;
    private ImageView mIvPublicityPage;

    private LinearLayout mSearchLayout;
    private String schoolId, classId;
    //是否来直播的筛选
    private boolean fromOnlineFilter;
    private String filterStartTimeBegin;
    private String filterStartTimeEnd;
    private String filterFinishTimeBegin;
    private String filterFinishTimeEnd;
    private int filterOnlineType = -1;
    private int roleType;
    private boolean isFirstIn = true;

    public interface Constants {
        String EXTRA_CONTACTS_TYPE = "type";
        String EXTRA_CONTACTS_ID = "id";
        String EXTRA_CONTACTS_NAME = "name";
        String EXTRA_CONTACTS_SCHOOL_ID = "schoolId";
        String EXTRA_CONTACTS_SCHOOL_NAME = "schoolName";
        String EXTRA_CONTACTS_GRADE_ID = "gradeId";
        String EXTRA_CONTACTS_GRADE_NAME = "gradeName";
        String EXTRA_CONTACTS_CLASS_ID = "classId";
        String EXTRA_CONTACTS_CLASS_NAME = "className";
        String EXTRA_IS_TEACHER = "isTeacher";
        String EXTRA_IS_HEADMASTER = "isHeadMaster";
        String EXTRA_IS_SCHOOLINFO = "schoolInfo";
        String EXTRA_ROLE_TYPE = "role_type";
        String ExTRA_CLASS_INFO = "class_info";
        //直播筛选传的参数
        String EXTRA_FROM_ONLINE_FILTER = "from_online_filter";
        //开始时间起
        String EXTRA_FILTER_START_TIME_BEGIN = "filter_start_time_begin";
        //开始时间止
        String EXTRA_FILTER_START_TIME_END = "filter_start_time_end";
        //结束时间起
        String EXTRA_FILTER_FINISH_TIME_BEGIN = "filter_finish_time_begin";
        //结束时间止
        String EXTRA_FILTER_FINISH_TIME_END = "filter_finish_time_end";
        //直播的类型
        String EXTRA_FILTER_ONLINE_TYPE = "filter_online_type";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_resource_list_with_search_bar, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoadIntent();
        initViews();
        setData();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (pageHelper != null) {
            pageHelper.setFetchingPageIndex(0);
        }
        loadOnlineData();
    }

    private void getLoadIntent() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            schoolId = bundle.getString(Constants.EXTRA_CONTACTS_SCHOOL_ID);
            classId = bundle.getString(Constants.EXTRA_CONTACTS_CLASS_ID);
            isHeadMaster = bundle.getBoolean(Constants.EXTRA_IS_HEADMASTER, false);
            isTeacher = bundle.getBoolean(Constants.EXTRA_IS_TEACHER, false);
            roleType = bundle.getInt(Constants.EXTRA_ROLE_TYPE);
            fromOnlineFilter = bundle.getBoolean(Constants.EXTRA_FROM_ONLINE_FILTER);
            if (fromOnlineFilter) {
                //来自直播的筛选界面
                filterStartTimeBegin = bundle.getString(Constants.EXTRA_FILTER_START_TIME_BEGIN);
                filterStartTimeEnd = bundle.getString(Constants.EXTRA_FILTER_START_TIME_END);
                filterFinishTimeBegin = bundle.getString(Constants.EXTRA_FILTER_FINISH_TIME_BEGIN);
                filterFinishTimeEnd = bundle.getString(Constants.EXTRA_FILTER_FINISH_TIME_END);
                filterOnlineType = bundle.getInt(Constants.EXTRA_FILTER_ONLINE_TYPE, -1);
            }
        }
    }

    private void initViews() {
        //搜索相关的数据
        initSearchData();
        initTitle();
        mIvPublicityPage = (ImageView) findViewById(R.id.iv_publicity_page);
        //处理来自筛选的数据以及UI过滤
        handleFromFilterData();
    }

    private void handleFromFilterData() {
        if (mSearchLayout != null && fromOnlineFilter) {
            mSearchLayout.setVisibility(View.GONE);
            findViewById(R.id.tv_create_online).setVisibility(View.GONE);
            findViewById(R.id.contacts_header_right_btn).setVisibility(View.GONE);
        }
    }

    private void setData() {
        PullToRefreshView pullToRefreshView = (PullToRefreshView)
                findViewById(R.id.contacts_pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);
        GridView listView = (GridView) findViewById(R.id.resource_list_view);
        if (listView != null) {
            AdapterViewHelper adapterViewHelper = new AdapterViewHelper(getActivity(),
                    listView, R.layout.resource_item_online) {
                @Override
                public void loadData() {
                    loadOnlineData();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    if (view != null) {
                        if (getData() == null) {
                            return view;
                        }
                        final Emcee data = (Emcee) getData().get(position);
                        if (data == null) {
                            return view;
                        }
                        ViewHolder holder = (ViewHolder) view.getTag();
                        if (holder == null) {
                            holder = new ViewHolder();
                        }
                        holder.data = data;
                        //直播封面 cover
                        ImageView thumbnail = (ImageView) view.findViewById(R.id.resource_thumbnail);
                        String coverUrl = data.getCoverUrl();
                        if (!TextUtils.isEmpty(coverUrl) && coverUrl.contains("coverUrl")) {
                            JSONObject jsonObject = JSON.parseObject(coverUrl, Feature.AutoCloseSource);
                            coverUrl = jsonObject.getString("coverUrl");
                        }
                        LQImageLoader.displayImage(AppSettings.getFileUrl(coverUrl), thumbnail, R.drawable.online_list_default);
                        //直播时间 以区间的形式来显示
                        TextView startAndEndTime = (TextView) view.findViewById(R.id.online_time);
                        startAndEndTime.setText(getCurrentOnlineTime(data));
                        //直播的标题
                        TextView onlineTitle = (TextView) view.findViewById(R.id.resource_title);
                        onlineTitle.setText(data.getTitle());
                        //当前直播的状态
                        TextView onlineStatus = (TextView) view.findViewById(R.id.show_online_state);
                        //当前在线的人数
                        TextView onlineCount = (TextView) view.findViewById(R.id.show_online_count);
                        int status = data.getState();
                        if (status == 0) {
                            onlineStatus.setBackgroundResource(R.drawable.live_trailer);
                            onlineStatus.setText(R.string.live_trailer);
                            //预告不显示浏览数
                            onlineCount.setVisibility(View.VISIBLE);
                        } else if (status == 1) {
                            onlineStatus.setBackgroundResource(R.drawable.live_living);
                            onlineStatus.setText(R.string.live_living);
                            onlineCount.setVisibility(View.VISIBLE);
                        } else if (status == 2) {
                            onlineStatus.setBackgroundResource(R.drawable.live_review);
                            onlineStatus.setText(R.string.live_review);
                            onlineCount.setVisibility(View.VISIBLE);
                        }

                        int browseCount = data.getBrowseCount();
                        String stringText = null;
                        //如果浏览数大于一万显示 1.x万
                        if (browseCount >= 10000) {
                            double count = div(browseCount, 10000, 1);
                            stringText = getString(R.string.online_count, String.valueOf(count) + "万");
                        } else {
                            stringText = getString(R.string.online_count, String.valueOf(browseCount));
                        }
                        if (data.isEbanshuLive()) {
                            stringText = stringText + " | " + getString(R.string.live_type_blackboard);
                        } else {
                            stringText = stringText + " | " + getString(R.string.live_type_video);
                        }
                        //直播中显示多少人在上课
                        if (status == 1) {
                            stringText = getString(R.string.str_how_many_people_online, data.getOnlineNum())
                                    + " | " + stringText;
                        }
                        onlineCount.setText(stringText);
                        //直播的主持人
                        TextView onlineHost = (TextView) view.findViewById(R.id.show_online_author);
                        StringBuilder builder = new StringBuilder();
                        List<EmceeList> emceeLists = data.getEmceeList();
                        if (emceeLists != null && emceeLists.size() > 0) {
                            for (int i = 0; i < emceeLists.size(); i++) {
                                String hostName = emceeLists.get(i).getRealName();
                                if (i == 0) {
                                    builder.append(hostName);
                                } else {
                                    builder.append(",").append(hostName);
                                }
                            }
                        }
                        onlineHost.setText(builder.toString());
                        //删除当前的按钮
                        ImageView iconDelete = (ImageView) view.findViewById(R.id.resource_delete);
                        if (isHeadMaster) {
                            iconDelete.setVisibility(View.VISIBLE);
                        } else if (TextUtils.equals(getMemeberId(), data.getAcCreateId())
                                 && isTeacher) {
                            iconDelete.setVisibility(View.VISIBLE);
                        } else {
                            iconDelete.setVisibility(View.INVISIBLE);
                        }
                        iconDelete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                popDialogDeleteOnlineData(data);
                            }
                        });
                        view.setTag(holder);
                    }
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder.data == null) {
                        return;
                    }
                    //如果当前用户学生判断当前的直播有没有加入我的直播中
                    if (roleType == RoleType.ROLE_TYPE_STUDENT) {
                        analysisCurrentLiveAddMyLive((Emcee) holder.data);
                    } else {
                        enterAirClassroomDetail((Emcee) holder.data);
                    }
                }
            };
            setCurrAdapterViewHelper(listView, adapterViewHelper);
        }
    }

    private void popDialogDeleteOnlineData(Emcee data) {
        List<PublishClass> publishClassList = data.getPublishClassList();
        if (publishClassList == null || publishClassList.size() == 0){
            return;
        }
        if (TextUtils.equals(getMemeberId(),data.getAcCreateId())
                && publishClassList.size() > 1){
            popCanSelectDeleteWayDialog(data);
        } else {
            popDeleteCurrentClassDialog(data);
        }
    }

    private void popCanSelectDeleteWayDialog(final Emcee data){
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(
                getActivity(),
                null,
                R.layout.layout_change_selected_icon,
                getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                },
                getString(R.string.confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (deleteAllClassRB != null && deleteAllClassRB.isChecked()){
                            //删除所有的班级
                            deleteCurrentItem(data, true);
                        } else{
                            //删除当前的班级
                            deleteCurrentItem(data, false);
                        }
                    }
                });
        messageDialog.show();
        deleteAllClassRB = (RadioButton) messageDialog.getContentView().findViewById(R.id.rb_all_Class);
    }

    private void popDeleteCurrentClassDialog(final Emcee data){
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(
                getActivity(),
                null,
                getString(R.string.confirm_delete_online, data.getTitle()),
                getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                },
                getString(R.string.confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        deleteCurrentItem(data, false);
                    }
                });
        messageDialog.show();
    }

    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
     * 定精度，以后的数字四舍五入。
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public double div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    private boolean campareIsBelongCurrentClass(Emcee data) {
        List<PublishClass> publishClassList = data.getPublishClassList();
        if (publishClassList != null && publishClassList.size() > 0) {
            for (int i = 0; i < publishClassList.size(); i++) {
                PublishClass publishClass = publishClassList.get(i);
                if (publishClass.isBelong() && classId.equals(publishClass.getClassId())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void deleteCurrentItem(final Emcee data, final boolean deleteAll) {
        if (data == null) return;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("Id", data.getId());
        if (deleteAll) {
            //删除直播
            params.put("ClassId", "");
        } else {
            //删除直播对象
            params.put("ClassId", classId);
        }
        RequestHelper.RequestListener listener =
                new RequestHelper.RequestDataResultListener<DataModelResult>(
                        getActivity(), DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()) {
                            return;
                        } else {
                            String errorMessage = getResult().getErrorMessage();
                            if (TextUtils.isEmpty(errorMessage)) {
                                getCurrAdapterViewHelper().getData().remove(data);
                                getCurrAdapterViewHelper().update();
                                if (!getCurrAdapterViewHelper().hasData()) {
                                    if (searchEditText != null) {
                                        String searchText = searchEditText.getText().toString().trim();
                                        if (TextUtils.isEmpty(searchText)) {
                                            if (fromOnlineFilter) {
                                                mIvPublicityPage.setVisibility(View.GONE);//显示宣传页
                                            } else {
                                                mIvPublicityPage.setVisibility(View.VISIBLE);//显示宣传页
                                            }
                                            mIvPublicityPage.setImageResource(R.drawable.air);
                                            mSearchLayout.setVisibility(View.GONE);
                                        }
                                    }
                                }
                                TipMsgHelper.ShowMsg(getActivity(), R.string.cs_delete_success);
                                //同步删除青岛那边的数据
                                if (deleteAll) {
                                    deleteQingdaoMateriaItem(data);
                                }
                            } else {
                                TipMsgHelper.ShowLMsg(getActivity(), errorMessage);
                            }

                        }
                    }
                };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.DELETE_AIRCLASS_ONLINE_LIST_NEW_BASE_URL, params, listener);
    }

    /**
     * 删除青岛的数据list接口
     *
     * @param data
     */
    private void deleteQingdaoMateriaItem(Emcee data) {
        String url = ServerUrl.DELETE_AIRCLASSROOM_MATERIA_TO_QINGDAO;
        StringBuilder tempUrl = new StringBuilder(url);
        tempUrl.append("&").append("memberId=" + getMemeberId()).append("&").append("aid=" + data
                .getId()).append("&").append("schoolId=" + data.getSchoolId());
        ThisStringRequest request = new ThisStringRequest(Request.Method.GET, tempUrl.toString(), new
                Listener<String>() {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
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
                    }
                });
        request.addHeader("Accept-Encoding", "*");
        request.start(getActivity());
    }

    /**
     * 获取当前直播显示的起止时间
     *
     * @param data
     * @return
     */
    private String getCurrentOnlineTime(Emcee data) {
        String beginTime = data.getStartTime();
        String endTime = data.getEndTime();
        if (!TextUtils.isEmpty(beginTime)) {
            beginTime = beginTime.substring(0, beginTime.length() - 3);
        }
        if (!TextUtils.isEmpty(endTime)) {
            endTime = endTime.substring(endTime.length() - 8, endTime.length() - 3);
        }
        String showTime = beginTime + " -- " + endTime;
        if (TextUtils.isEmpty(showTime)) {
            return "";
        }
        return showTime;
    }

    /**
     * 初始化搜索的数据
     */
    private void initSearchData() {
        //搜索的总布局
        mSearchLayout = (LinearLayout) findViewById(R.id.contacts_search_bar_layout);
        //搜索按钮
        searchBtn = (TextView) findViewById(R.id.search_btn);
        if (searchBtn != null) {
            //将搜索的按钮设置筛选的字体
            searchBtn.setText(getString(R.string.filter));
            searchBtn.setVisibility(View.VISIBLE);
            searchBtn.setOnClickListener(this);
        }
        //搜索文本框
        final ClearEditText editText = (ClearEditText) findViewById(R.id.search_keyword);
        if (editText != null) {
            editText.setHint(getString(R.string.str_search_data_hint));
            editText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        hideSoftKeyboard(getActivity());
                        //加载相应的数据
                        loadOnlineData();
                        return true;
                    }
                    return false;
                }
            });
            //清空文本框中内容
            editText.setOnClearClickListener(new ClearEditText.OnClearClickListener() {
                @Override
                public void onClearClick() {
                    editText.setText("");
                    getCurrAdapterViewHelper().clearData();
                    getPageHelper().clear();
                    //加载数据
                    loadOnlineData();
                }
            });
            editText.setInputType(InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            searchEditText = editText;
        }
    }

    private void initTitle() {
        //标题
        TextView headTitle = (TextView) findViewById(R.id.contacts_header_title);
        if (headTitle != null) {
            String titleText = getString(R.string.air_classroom);
            if (fromOnlineFilter) {
                titleText = getString(R.string.filter) + " - " + getString(R.string.air_classroom);
            }
            headTitle.setText(titleText);
        }
        //空中课堂的新建
        TextView createOnline = (TextView) findViewById(R.id.tv_create_online);
        if (createOnline != null) {
            if (isHeadMaster || isTeacher) {
                createOnline.setText(getString(R.string.create));
                createOnline.setVisibility(View.VISIBLE);
                createOnline.setOnClickListener(this);
            }
        }
        //课程表
        TextView timeTable = (TextView) findViewById(R.id.contacts_header_right_btn);
        if (timeTable != null) {
            timeTable.setTextSize(16);
            timeTable.setText(getString(R.string.timetable));
            timeTable.setVisibility(View.VISIBLE);
            timeTable.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.search_btn) {
//            hideSoftKeyboard(getActivity());
//            loadOnlineData();
            enterOnlineFilterActivity();
        } else if (v.getId() == R.id.tv_create_online) {
            //创建一个直播项
            createOnline();
        } else if (v.getId() == R.id.contacts_header_right_btn) {
            //课堂表
            enterTimeTableActivity();
        } else {
            super.onClick(v);
        }
    }

    private void enterAirClassroomDetail(Emcee data) {
        //将下拉刷新的下标清空一下
        pageHelper.setFetchingPageIndex(0);
        Intent intent = new Intent(getActivity(), AirClassroomDetailActivity.class);
        Bundle bundle = getArguments();
        if (bundle != null) {
            data.setBrowseCount(data.getBrowseCount() + 1);
            bundle.putSerializable(AirClassroomDetailActivity.EMECCBEAN, data);
        }
        intent.putExtras(bundle);
        getActivity().startActivityForResult(intent, ActivityUtils.REQUEST_CODE_RETURN_REFRESH);
    }

    /**
     * 进入直播的筛选界面
     */
    private void enterOnlineFilterActivity() {
        Intent intent = new Intent(getActivity(), CommonFragmentActivity.class);
        Bundle bundle = getArguments();
        bundle.putInt("fromType", OnlinePickerFragment.FromType.FROM_AIRCLASS);
        bundle.putInt("picker_model", OnlinePickerFragment.PickerModel.MULTIPLE_MODEL);
        bundle.putSerializable(CommonFragmentActivity.EXTRA_CLASS_OBJECT, OnlinePickerFragment.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * 创建一个直播列表
     */
    private void createOnline() {
        ActivityUtils.createOnlineData(getActivity(),getArguments());
    }

    /**
     * 进入课程表的activity view
     */
    private void enterTimeTableActivity() {
        Intent intent = new Intent(getActivity(), CommonFragmentActivity.class);
        Bundle bundle = getArguments();
        bundle.putSerializable(CommonFragmentActivity.EXTRA_CLASS_OBJECT, LiveTimetableFragment.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * 更新空中课堂的列表
     */
    private void loadOnlineData() {
        if (TextUtils.isEmpty(schoolId)) {
            return;
        }
        String searhTitle = searchEditText.getText().toString().trim();
        //每次加载拉取16数据
        pageHelper.setPageSize(16);
        Map<String, Object> params = new HashMap();
        //必填
        params.put("SchoolId", schoolId);
        params.put("Pager", getPageHelper().getFetchingPagerArgs());
        //非必填
        params.put("ClassId", classId);
        //memberId为空 为学生拉取空中课堂的列表
        params.put("MemberId", "");
        params.put("Title", searhTitle);
        //来自筛选的界面
        if (fromOnlineFilter) {
            params.put("StartTimeBegin", filterStartTimeBegin);
            params.put("StartTimeEnd", filterStartTimeEnd);
            params.put("EndTimeBegin", filterFinishTimeBegin);
            params.put("EndTimeEnd", filterFinishTimeEnd);
            //板书（true） 视频(false)
            if (filterOnlineType == OnlinePickerFragment.OnlineType.VIDEO_TYPE) {
                params.put("IsEbanshuLive", false);
            } else if (filterOnlineType == OnlinePickerFragment.OnlineType.E_BLACKBOARD_TYPE) {
                params.put("IsEbanshuLive", true);
            }
        }
        DefaultPullToRefreshDataListener listener = new DefaultPullToRefreshDataListener<EmceeListResult>(
                EmceeListResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                EmceeListResult result = getResult();
                if (result == null || !result.isSuccess()
                        || result.getModel() == null) {
                    return;
                }
                upDateAirClassList(result);
            }

            @Override
            public void onError(NetroidError error) {
                super.onError(error);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        };
        if (!isFirstIn) {
            listener.setShowPullToRefresh(false);
        } else {
            isFirstIn = false;
        }
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.GET_AIR_CLASSROOM_LIST_BASE_URL, params, listener);
    }

    /**
     * 更新空中课堂的列表
     *
     * @param result
     */
    private void upDateAirClassList(EmceeListResult result) {
        List<Emcee> onlineList = result.getModel().getData();
        if (onlineList == null || onlineList.size() <= 0) {
            if (TextUtils.isEmpty(searchEditText.getText().toString().trim())) {
                if (pageHelper.getFetchingPageIndex() == 0) {
                    if (searchEditText != null) {
                        String searchText = searchEditText.getText().toString().trim();
                        if (TextUtils.isEmpty(searchText)) {
                            if (fromOnlineFilter){
                                mIvPublicityPage.setVisibility(View.GONE);//显示宣传页
                                TipMsgHelper.ShowLMsg(getActivity(), getString(R.string.no_data));
                            } else {
                                mIvPublicityPage.setVisibility(View.VISIBLE);//显示宣传页
                            }
                            mIvPublicityPage.setImageResource(R.drawable.air);
                            mSearchLayout.setVisibility(View.GONE);
                        }
                    }
                    getCurrAdapterViewHelper().clearData();
                    getCurrAdapterViewHelper().update();
                } else {
                    TipMsgHelper.ShowLMsg(getActivity(), getString(R.string.no_more_data));
                }
            } else {
                if (pageHelper.getFetchingPageIndex() == 0) {
                    getCurrAdapterViewHelper().clearData();
                    getCurrAdapterViewHelper().update();
                } else {
                    TipMsgHelper.ShowLMsg(getActivity(), getString(R.string.no_more_data));
                }
            }
            return;
        }
        mSearchLayout.setVisibility(fromOnlineFilter ? View.GONE : View.VISIBLE);
        mIvPublicityPage.setVisibility(View.GONE);//隐藏宣传页
        mIvPublicityPage.setImageBitmap(null);
        if (getPageHelper().isFetchingPageIndex(result.getModel().getPager())) {
            if (getPageHelper().isFetchingFirstPage()) {
                getCurrAdapterViewHelper().clearData();
            }
            getPageHelper().updateByPagerArgs(result.getModel().getPager());
            getPageHelper().setCurrPageIndex(getPageHelper().getFetchingPageIndex());
            if (getCurrAdapterViewHelper().hasData()) {
                int position = getCurrAdapterViewHelper().getData().size();
                if (position > 0) {
                    position--;
                }
                getCurrAdapterViewHelper().getData().addAll(onlineList);
                getCurrAdapterView().setSelection(position);
            } else {
                getCurrAdapterViewHelper().setData(onlineList);
            }
        }
    }

    /**
     * 仅当roleType为学生时
     * 判断当前的直播记录有没有被加入我的直播列表中
     *
     * @param data
     */
    private void analysisCurrentLiveAddMyLive(final Emcee data) {
        Map<String, Object> param = new HashMap<>();
        param.put("SchoolId", schoolId);
        param.put("ClassId", classId);
        param.put("MemberId", getMemeberId());
        param.put("ExtId", data.getId());
        DefaultModelListener listener = new DefaultModelListener<ModelResult>(ModelResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                try {
                    org.json.JSONObject jsonObject = new org.json.JSONObject(jsonString);
                    boolean isAddMyLive = jsonObject.optBoolean("Model");
                    data.setAddMyLived(isAddMyLive);
                    enterAirClassroomDetail(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.JUDGE_LIVE_ADD_MY_LIVE_BASE_URL,
                param, listener);
    }
}
