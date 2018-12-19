package com.galaxyschool.app.wawaschool.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duowan.mobile.netroid.NetroidError;
import com.galaxyschool.app.wawaschool.ActClassroomDetailActivity;
import com.galaxyschool.app.wawaschool.ActClassroomPickerActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.DialogHelper;
import com.galaxyschool.app.wawaschool.common.ScreenUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.pojo.PerformMember;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.ContactsClassMemberInfo;
import com.galaxyschool.app.wawaschool.pojo.PerformClassList;
import com.galaxyschool.app.wawaschool.pojo.PerformClassListResult;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.sortlistview.ClearEditText;
import com.osastudio.common.utils.LQImageLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ActClassroomFragment extends ContactsListFragment implements View.OnClickListener {
    public static String TAG = ActClassroomFragment.class.getSimpleName();
    private TextView searchBtn;
    private EditText searchEditText;
    private String schoolId, classId;
    //班主任
    private boolean isHeadMaster;
    private String groupId;
    //是否来自筛选的界面
    private boolean isFromScreem = false;
    private String startTime;
    private String endTime;
    private List<ContactsClassMemberInfo> studentList;
    private ImageView mIvPublicityPage;
    private boolean isFirstIn = true;
    private boolean fromMyPerformance;
    private String myPerformanceUserId;
    private boolean isMyPerformanceParent;

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
        String EXTRA_START_TIME = "startTime";
        String EXTRA_END_TIME = "endTime";
        String EXTRA_STUDENT_LIST = "studentList";
        String EXTRA_PERFORM_ID = "perform_id";
        //来自我的表演
        String EXTRA_FROM_MY_PERFORMANCE = "from_my_performance";
        //传过来的学生studentId
        String EXTRA_MY_PERFORMANCE_USERID = "my_performance_userId";
        String EXTRA_MY_PERFORMMANCE_PARENT = "is_my_performance_parent";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_act_classroom_list, null);
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
        loadActClassroomData();
    }

    private void getLoadIntent() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            schoolId = bundle.getString(Constants.EXTRA_CONTACTS_SCHOOL_ID);
            classId = bundle.getString(Constants.EXTRA_CONTACTS_CLASS_ID);
            isHeadMaster = bundle.getBoolean(Constants.EXTRA_IS_HEADMASTER, false);
            groupId = bundle.getString(Constants.EXTRA_CONTACTS_ID);
            isFromScreem = bundle.getBoolean(ActivityUtils.EXTRA_TEMP_DATA, false);
            if (isFromScreem) {
                //筛选
                startTime = bundle.getString(Constants.EXTRA_START_TIME);
                endTime = bundle.getString(Constants.EXTRA_END_TIME);
                studentList = bundle.getParcelableArrayList(Constants.EXTRA_STUDENT_LIST);
            }
            fromMyPerformance = bundle.getBoolean(Constants.EXTRA_FROM_MY_PERFORMANCE, false);
            isMyPerformanceParent = bundle.getBoolean(Constants.EXTRA_MY_PERFORMMANCE_PARENT,false);
            if (fromMyPerformance) {
                isFromScreem = true;
                myPerformanceUserId = bundle.getString(Constants.EXTRA_MY_PERFORMANCE_USERID);
            }
        }
    }

    private void initViews() {
        //搜索相关的数据
        initSearchData();
        initTitle();
        mIvPublicityPage = (ImageView) findViewById(R.id.iv_publicity_page);
    }

    private void setData() {
        PullToRefreshView pullToRefreshView = (PullToRefreshView)
                findViewById(R.id.contacts_pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);
        GridView listView = (GridView) findViewById(R.id.resource_gridview);
        if (listView != null) {
            if (fromMyPerformance){
                //来自我的表演
                listView.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.main_bg_color));
            }
            AdapterViewHelper adapterViewHelper = new AdapterViewHelper(getActivity(),
                    listView, R.layout.item_act_classroom_list_detail) {
                @Override
                public void loadData() {
                    loadActClassroomData();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    if (view != null) {
                        if (getData() == null) {
                            mIvPublicityPage.setVisibility(View.VISIBLE);//显示宣传页
                            mIvPublicityPage.setImageResource(R.drawable.act);
                            return view;
                        }
                        final PerformClassList data = (PerformClassList) getData().get(position);
                        if (data == null) {
                            return view;
                        }
                        ViewHolder holder = (ViewHolder) view.getTag();
                        if (holder == null) {
                            holder = new ViewHolder();
                        }
                        holder.data = data;

                        ImageView thumbnail = (ImageView) view.findViewById(R.id.media_thumbnail);
                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) thumbnail.getLayoutParams();
                        int windowWith = ScreenUtils.getScreenWidth(getActivity());//屏幕宽度
                        int itemWidth = (windowWith - getResources().
                                getDimensionPixelSize(R.dimen.separate_20dp)) / 2;
                        params.width = itemWidth;
                        params.height = params.width * 9 / 16;
                        thumbnail.setLayoutParams(params);
                        if (thumbnail != null) {
                            LQImageLoader.displayImage(AppSettings.getFileUrl(data.getResThumbnail()),
                                    thumbnail, R.drawable.online_list_default);
                        }
                        TextView time = (TextView) view.findViewById(R.id.act_classroom_date);
                        if (time != null) {
                            String publishTime = data.getPublishTime();
                            publishTime = publishTime.substring(0, 10);
                            time.setText(publishTime);
                        }
                        TextView title = (TextView) view.findViewById(R.id.act_class_list_title);
                        if (title != null) {
                            title.setText(data.getTitle());
                        }
                        ImageView arIcon = (ImageView) view.findViewById(R.id.act_class_list_ar);
                        if (data.getType() == 0) {
                            arIcon.setVisibility(View.GONE);
                        } else {
                            arIcon.setVisibility(View.VISIBLE);
                        }

                        //我的表演显示来源
                        TextView sourceFromTextV = (TextView) view.findViewById(R.id.tv_source_from);
                        if (sourceFromTextV != null) {
                            if (fromMyPerformance) {
                                sourceFromTextV.setText(getSourceFromName(data.getPerformMemberList()));
                                sourceFromTextV.setVisibility(View.VISIBLE);
                            } else {
                                sourceFromTextV.setVisibility(View.GONE);
                            }
                        }

                        ImageView deleteIcon = (ImageView) view.findViewById(R.id.act_class_list_delete);
                        if (deleteIcon != null) {
                            if (fromMyPerformance){
                                if (TextUtils.equals(getMemeberId(),data.getCreateId()) && !isMyPerformanceParent){
                                    deleteIcon.setVisibility(View.VISIBLE);
                                } else {
                                    deleteIcon.setVisibility(View.GONE);
                                }
                            } else if (isHeadMaster || getMemeberId().equals(data.getCreateId())) {
                                deleteIcon.setVisibility(View.VISIBLE);
                            } else {
                                deleteIcon.setVisibility(View.GONE);
                            }
                            deleteIcon.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ContactsMessageDialog messageDialog = new ContactsMessageDialog(
                                            context, null,
                                            getString(R.string.want_to_delete_sb, data.getTitle()),
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
                                                    deleteCurrentItem(data);
                                                }
                                            });
                                    messageDialog.show();
                                }
                            });
                        }
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
                    enterActClassroomDetail((PerformClassList) holder.data);
                }
            };
            setCurrAdapterViewHelper(listView, adapterViewHelper);
        }
    }

    private String getSourceFromName(List<PerformMember> performMembers) {
        String sourName = getString(R.string.str_personal);
        if (performMembers != null && performMembers.size() > 0) {
            String userMemberId = myPerformanceUserId;
            if (TextUtils.isEmpty(userMemberId)) {
                userMemberId = DemoApplication.getInstance().getMemberId();
            }
            String sourceFromClass = null;
            String sourceFromPersonal = null;
            for (int i = 0; i < performMembers.size(); i++) {
                if (TextUtils.equals(userMemberId, performMembers.get(i).getMemberId())) {
                    if (TextUtils.isEmpty(performMembers.get(i).getClassName())) {
                        sourceFromPersonal = getString(R.string.str_personal);
                    } else {
                        sourceFromClass = performMembers.get(i).getClassName();
                    }
                }
            }
            if (!TextUtils.isEmpty(sourceFromClass) && !TextUtils.isEmpty(sourceFromPersonal)){
                String from = sourceFromClass + getString(R.string.str_and) + sourceFromPersonal;
                sourName = getString(R.string.str_source_from,from);
            } else if (!TextUtils.isEmpty(sourceFromClass)){
                sourName = getString(R.string.str_source_from, sourceFromClass);
            } else {
                sourName = getString(R.string.str_source_from, sourceFromPersonal);
            }
        }
        return sourName;
    }

    /**
     * 进入表演课堂的详情页
     *
     * @param performClassList
     */
    private void enterActClassroomDetail(PerformClassList performClassList) {
        Intent intent = new Intent(getActivity(), ActClassroomDetailActivity.class);
        Bundle bundle = getArguments();
        bundle.putSerializable(ActivityUtils.EXTRA_ACT_CLASSROOM_DATA, performClassList);
        intent.putExtras(bundle);
        startActivityForResult(intent, ActivityUtils.REQUEST_CODE_RETURN_REFRESH);
    }

    private void deleteCurrentItem(final PerformClassList data) {
        if (data == null) return;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("Id", data.getId());
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
                                    if (searchEditText != null && !isFromScreem) {
                                        String searchText = searchEditText.getText().toString().trim();
                                        if (TextUtils.isEmpty(searchText)) {
                                            mIvPublicityPage.setVisibility(View.VISIBLE);//显示宣传页
                                            mIvPublicityPage.setImageResource(R.drawable.act);
                                            findViewById(R.id.contacts_header_right_btn).setVisibility(View.GONE);
                                        }
                                    }
                                }
                                TipMsgHelper.ShowMsg(getActivity(), R.string.cs_delete_success);
                            } else {
                                TipMsgHelper.ShowLMsg(getActivity(), errorMessage);
                            }
                        }
                    }
                };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.DELETE_ACT_CLASSROOM_BASE_URL,
                params, listener);
    }

    /**
     * 初始化搜索的数据
     */
    private void initSearchData() {
        //搜索按钮
        searchBtn = (TextView) findViewById(R.id.search_btn);
        if (searchBtn != null) {
            searchBtn.setVisibility(View.VISIBLE);
            searchBtn.setOnClickListener(this);
        }
        //搜索文本框
        final ClearEditText editText = (ClearEditText) findViewById(R.id.search_keyword);
        if (editText != null) {
            editText.setHint(getString(R.string.search_title));
            editText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        hideSoftKeyboard(getActivity());
                        //加载相应的数据
                        loadActClassroomData();
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
                    loadActClassroomData();
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
            if (fromMyPerformance) {
                //我的表演
                headTitle.setText(getString(R.string.str_my_performance));
            } else {
                //表演课堂
                headTitle.setText(getString(R.string.act_classroom));
            }
        }
        //筛选
        TextView headRightBtn = (TextView) findViewById(R.id.contacts_header_right_btn);
        //判断是否来自筛选的复用界面
        if (headRightBtn != null && !isFromScreem) {
            headRightBtn.setText(getString(R.string.screening));
            headRightBtn.setTextColor(getResources().getColor(R.color.text_green));
            headRightBtn.setVisibility(View.VISIBLE);
            headRightBtn.setOnClickListener(this);
        }

        if (!TextUtils.isEmpty(myPerformanceUserId) && fromMyPerformance) {
            //我的孩子家长身份进来隐藏top栏
            findViewById(R.id.contacts_header_layout).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        hideSoftKeyboard(getActivity());
        if (v.getId() == R.id.search_btn) {
            loadActClassroomData();
        } else if (v.getId() == R.id.contacts_header_right_btn) {
            enterActClassScreen();
        } else {
            super.onClick(v);
        }
    }

    /**
     * 进入表演课堂的筛选界面
     */
    private void enterActClassScreen() {
        Intent intent = new Intent(getActivity(), ActClassroomPickerActivity.class);
        Bundle bundle = getArguments();
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * 获取表演课堂的列表
     */
    private void loadActClassroomData() {
        if (fromMyPerformance) {
            //我的表演
            loadMyPerformData();
            return;
        }
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
        params.put("Title", searhTitle);
        //来自筛选
        if (isFromScreem) {
            params.put("StartTime", startTime);
            params.put("EndTime", endTime);
            if (studentList != null && studentList.size() > 0) {
                String[] members = new String[studentList.size()];
                for (int i = 0; i < studentList.size(); i++) {
                    members[i] = studentList.get(i).getMemberId();
                }
                params.put("MemberIds", members);
            } else {
                params.put("MemberIds", "");
            }
        } else {
            params.put("StartTime", "");
            params.put("EndTime", "");
            params.put("MemberIds", "");
        }
        DefaultPullToRefreshDataListener listener = new DefaultPullToRefreshDataListener<PerformClassListResult>(
                PerformClassListResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                PerformClassListResult result = getResult();
                if (result == null || !result.isSuccess()
                        || result.getModel() == null) {
                    return;
                }
                upDateActClassList(result);
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
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.GET_ACT_CLASSROOM_LIST_BASE_URL, params, listener);
    }

    /**
     * 更新表演课堂的列表
     *
     * @param result
     */
    private void upDateActClassList(PerformClassListResult result) {
        List<PerformClassList> performClassList = result.getModel().getData();
        if (performClassList == null || performClassList.size() <= 0) {
            if (TextUtils.isEmpty(searchEditText.getText().toString().trim())) {
                if (pageHelper.getFetchingPageIndex() == 0) {
                    getCurrAdapterViewHelper().clearData();
                    getCurrAdapterViewHelper().update();
                    if (isFromScreem) {
                        mIvPublicityPage.setVisibility(View.GONE);//隐藏宣传页
                        mIvPublicityPage.setImageBitmap(null);
                        if (getUserVisibleHint()) {
                            TipMsgHelper.ShowLMsg(getActivity(), getString(R.string.no_data));
                        }
                    } else {
                        if (searchEditText != null) {
                            String searchText = searchEditText.getText().toString().trim();
                            if (TextUtils.isEmpty(searchText)) {
                                mIvPublicityPage.setVisibility(View.VISIBLE);//显示宣传页
                                mIvPublicityPage.setImageResource(R.drawable.act);
                            }
                        }
                        findViewById(R.id.contacts_header_right_btn).setVisibility(View.GONE);
                    }
                } else {
                    TipMsgHelper.ShowLMsg(getActivity(), getString(R.string.no_more_data));
                }
            } else {
                getCurrAdapterViewHelper().clearData();
                getCurrAdapterViewHelper().update();
            }
            return;
        }
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
                getCurrAdapterViewHelper().getData().addAll(performClassList);
                getCurrAdapterView().setSelection(position);
            } else {
                getCurrAdapterViewHelper().setData(performClassList);
            }
        }
    }

    private void loadMyPerformData() {
        Map<String, Object> params = new HashMap();
        //必填
        if (TextUtils.isEmpty(myPerformanceUserId)) {
            params.put("MemberId", DemoApplication.getInstance().getMemberId());
        } else {
            params.put("MemberId", myPerformanceUserId);
        }
//        params.put("StartTime", schoolId);
//        params.put("EndTime", schoolId);
        String searhTitle = searchEditText.getText().toString().trim();
        params.put("Title", searhTitle);
//        params.put("NullableType", schoolId);
        params.put("Pager", getPageHelper().getFetchingPagerArgs());
        DefaultPullToRefreshDataListener listener = new DefaultPullToRefreshDataListener<PerformClassListResult>(
                PerformClassListResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                PerformClassListResult result = getResult();
                if (result == null || !result.isSuccess()
                        || result.getModel() == null) {
                    return;
                }
                upDateActClassList(result);
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
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.GET_MY_PERFORMANCE_BASE_URL, params, listener);
    }
}
