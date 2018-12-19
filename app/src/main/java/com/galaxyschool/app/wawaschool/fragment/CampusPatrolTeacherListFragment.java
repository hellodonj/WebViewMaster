package com.galaxyschool.app.wawaschool.fragment;

import android.content.Intent;
import android.graphics.Color;
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
import android.widget.ListView;
import android.widget.TextView;

import com.duowan.mobile.netroid.NetroidError;
import com.galaxyschool.app.wawaschool.CampusPatrolTeacherResourceListActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.TeacherDataStaticsInfo;
import com.galaxyschool.app.wawaschool.pojo.TeacherDataStaticsInfoListResult;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.slidelistview.SlideListView;
import com.galaxyschool.app.wawaschool.views.sortlistview.ClearEditText;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 校园巡查---按老师查询。
 */
public class CampusPatrolTeacherListFragment extends ContactsListFragment {

    public static final String TAG = CampusPatrolTeacherListFragment.class.getSimpleName();

    private TeacherDataStaticsInfoListResult dataListResult;
    private TextView keywordView;
    private String keyword = "";
    private String startDate,endDate;
    private SchoolInfo schoolInfo;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.campus_patrol_fragment_list, null);
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
        if (getUserVisibleHint()){
            if (CampusPatrolClassListFragment.hasDataChanged()){
                //班级列表数据改变了
                CampusPatrolClassListFragment.setHasDataChanged(false);
                refreshData();
            }
        }
    }

    void initViews() {

        if (getArguments() != null){
            schoolInfo = (SchoolInfo) getArguments().
                    getSerializable(SchoolInfo.class.getSimpleName());
        }

        //搜索
        ClearEditText editText = (ClearEditText) findViewById(R.id.search_keyword);
        if (editText != null) {
            editText.setHint(getString(R.string.search));
            editText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        hideSoftKeyboard(getActivity());
                        String keywords=v.getText().toString().trim();
                        if (!TextUtils.isEmpty(keywords)) {
                            search(v.getText().toString());
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
                    search(keywordView.getText().toString());
                }
            });
            editText.setInputType(InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
//            editText.requestFocus();
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
                        search(keywordView.getText().toString());
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
                R.id.pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);

        ListView listView = (ListView) findViewById(R.id.list_view);
        if (listView != null) {
            listView.setDividerHeight(0);

            AdapterViewHelper listViewHelper = new AdapterViewHelper(getActivity(),
                    listView, R.layout.campus_patrol_main_fragment_list_item) {
                @Override
                public void loadData() {
                    search(keywordView.getText().toString());
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    final TeacherDataStaticsInfo data =
                            (TeacherDataStaticsInfo) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;
                    //头像
                    ImageView imageView = (ImageView) view.findViewById(R.id.iv_icon);
                    if (imageView != null) {
                        getThumbnailManager().displayUserIconWithDefault(
                                AppSettings.getFileUrl(data.getTeacherHeadPic()), imageView,
                                R.drawable.default_user_icon);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //头像点击事件
//                                ActivityUtils.enterPersonalSpace(getActivity(),data.getTeacherId());
                            }
                        });

                    }
                    //序号
                    TextView textView = (TextView) view.findViewById(R.id.tv_left);
                    if (textView != null) {
                        textView.setText(String.valueOf(position + 1));
                    }

                    //真实姓名
                    textView = (TextView) view.findViewById(R.id.tv_title);
                    if (textView != null) {
                        textView.setText(data.getTeacherRealName());
                    }

                    //用户名
                    textView = (TextView) view.findViewById(R.id.tv_content);
                    if (textView != null) {
                        textView.setText(data.getTeacherNickName());
                    }

                    //数量
                    textView = (TextView) view.findViewById(R.id.tv_right);
                    if (textView != null) {
                        textView.setText(String.valueOf(data.getSum()));
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
                    TeacherDataStaticsInfo data = (TeacherDataStaticsInfo)holder.data;
                    enterCampusPatrolTeacherResourceListActivity(data);
                }
            };

            setCurrAdapterViewHelper(listView, listViewHelper);
        }
    }


    /**
     * 进入学校班级页面
     */
    private void enterCampusPatrolTeacherResourceListActivity(TeacherDataStaticsInfo info) {
        if (info == null){
            return;
        }
        Intent intent = new Intent(getActivity(), CampusPatrolTeacherResourceListActivity.class);
        Bundle args = new Bundle();
        args.putSerializable(TeacherDataStaticsInfo.class.getSimpleName(),info);
        args.putSerializable(SchoolInfo.class.getSimpleName(),schoolInfo);
        args.putString(CampusPatrolMainFragment.CAMPUS_PATROL_SCREENING_START_DATE,startDate);
        args.putString(CampusPatrolMainFragment.CAMPUS_PATROL_SCREENING_END_DATE,endDate);
        intent.putExtras(args);
        startActivityForResult(intent,CampusPatrolPickerFragment.
                REQUEST_CODE_CAMPUS_PATROL_TEACHER_RESOURCE_LIST);
    }


    private void refreshData(){
        getPageHelper().clear();
        search(this.keywordView.getText().toString());
    }

    public void refreshData(String startDate, String endDate){
        this.startDate = startDate;
        this.endDate = endDate;
        refreshData();
    }

    /**
     * 按照关键字和筛选的时间来搜索
     */
    private void search(String keyword){

        keyword = keyword.trim();
        if (!keyword.equals(this.keyword)) {
            getCurrAdapterViewHelper().clearData();
            getPageHelper().clear();
        }
        this.keyword = keyword;

        UIUtils.hideSoftKeyboard(getActivity());
        if (getUserInfo() == null){
            return;
        }
        if (schoolInfo == null){
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("SchoolId", schoolInfo.getSchoolId());//学校Id，必填。
        // 3-教师统计,4-班级统计
        params.put("Type", CampusPatrolMainFragment.CAMPUS_PATROL_SEARCH_TYPE_TEACHER);
        //教师真实姓名或者用户名,非必填。
        params.put("TeacherName",keyword);

        if (!TextUtils.isEmpty(startDate)) {//时间格式：2016-12-11
            params.put("StrStartTime", startDate);//统计开始时间,非必填。
        }
        if (!TextUtils.isEmpty(endDate)) {//时间格式：2016-12-11
            params.put("StrEndTime", endDate);//统计结束时间,非必填。
        }
        //分页信息,必填。
        params.put("Pager",getPageHelper().getFetchingPagerArgs());
        DefaultPullToRefreshDataListener listener =
                new DefaultPullToRefreshDataListener<TeacherDataStaticsInfoListResult>(
                        TeacherDataStaticsInfoListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if(getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        TeacherDataStaticsInfoListResult result = getResult();
                        if (result == null || !result.isSuccess()
                                || result.getModel() == null) {
                            return;
                        }
                        updateViews(result);
                    }

                    @Override
                    public void onError(NetroidError error) {
                        if(getActivity() == null) {
                            return;
                        }
                        super.onError(error);
                    }
                };
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_DATA_STATICS_LIST_LIST_URL, params, listener);

    }

    private void updateViews(TeacherDataStaticsInfoListResult result) {
        if (getPageHelper().isFetchingPageIndex(result.getModel().getPager())) {
            List<TeacherDataStaticsInfo> list = result.getModel().getData();
            if (list == null || list.size() <= 0) {
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
                getCurrAdapterViewHelper().getData().addAll(list);
                getCurrAdapterView().setSelection(position);
                result.getModel().setData(getCurrAdapterViewHelper().getData());
            } else {
                getCurrAdapterViewHelper().setData(list);
                dataListResult = result;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null){
            if (requestCode == CampusPatrolPickerFragment.
                    REQUEST_CODE_CAMPUS_PATROL_TEACHER_RESOURCE_LIST){
                //老师资源统计页面
                if (CampusPatrolTeacherResourceListFragment.hasDataChanged()){
                    CampusPatrolTeacherResourceListFragment.setHasDataChanged(false);
                    refreshData();
                }
            }
        }
    }
}
