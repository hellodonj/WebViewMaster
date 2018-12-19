package com.galaxyschool.app.wawaschool.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.Note.MediaPaperActivity;
import com.galaxyschool.app.wawaschool.Note.OnlineMediaPaperActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.CampusPatrolUtils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfoListResult;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.TeacherDataStaticsInfo;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.sortlistview.ClearEditText;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchoolResourceListBaseFragment extends ContactsListFragment {

    public static final String TAG = SchoolResourceListBaseFragment.class.getSimpleName();

    public interface Constants {
        String EXTRA_SCHOOL_ID = "schoolId";
        String EXTRA_SCHOOL_NAME = "schoolName";
        String EXTRA_IS_TEACHER = "isTeacher";
        String EXTRA_IS_ONLINE_SCHOOL_MESSAGE = "is_online_school_message";
    }

    protected String schoolId;
    protected String schoolName;
    protected boolean isTeacher;
    protected TextView keywordView;
    protected String keyword = "";
    protected NewResourceInfoListResult resourceListResult;
    protected TextView newBtn;
    //校园巡查
    protected boolean isCampusPatrolTag;
    protected String resourceCountStr;
    protected String startDate,endDate;
    protected TeacherDataStaticsInfo info;
    protected SchoolInfo schoolInfo;
    private String title;
    protected String itemId = null;//标识当前选中的条目id
    protected boolean isOnlineSchoolMessage;//在线机构-学校论坛

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (getUserVisibleHint()) {
//            getPageHelper().clear();
//            loadResourceList();
//        }
    }

    private void refreshData(){
        getPageHelper().clear();
        loadResourceList();
    }

    private void loadCampusPatrolMaterialData(String keyword) {
        keyword = keyword.trim();
        if (!keyword.equals(this.keyword)) {
            getCurrAdapterViewHelper().clearData();
            getPageHelper().clear();
        }
        this.keyword = keyword;

        if (schoolInfo == null || info == null){
            return;
        }
        Map<String, Object> params = new HashMap();
        //学校id，必填
        params.put("SchoolId", schoolInfo.getSchoolId());
        //用户id
        params.put("MemberId", info.getTeacherId());
        params.put("KeyWord", keyword);
        //统计开始时间，非必填 格式:"yyyy-MM-dd"
        if (!TextUtils.isEmpty(startDate)) {
            params.put("StrStartTime",startDate);
        }
        //统计结束时间，非必填 格式:"yyyy-MM-dd"
        if (!TextUtils.isEmpty(endDate)) {
            params.put("StrEndTime",endDate);
        }
        //分页对象，必须。
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
                        if (getResult() == null || !getResult().isSuccess()
                                || getResult().getModel() == null) {
                            return;
                        }
                        updateResourceListView(getResult());
                    }
                };
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_STATICS_SCHOOL_DYN_LIST_LIST_URL, params, listener);

    }

    private void init() {
        if (getArguments() != null) {
            schoolId = getArguments().getString(Constants.EXTRA_SCHOOL_ID);
            schoolName = getArguments().getString(Constants.EXTRA_SCHOOL_NAME);
            isTeacher = getArguments().getBoolean(Constants.EXTRA_IS_TEACHER);
            isOnlineSchoolMessage = getArguments().getBoolean(Constants.EXTRA_IS_ONLINE_SCHOOL_MESSAGE);
            //校园巡查
            isCampusPatrolTag = getArguments().getBoolean(CampusPatrolMainFragment
                    .IS_CAMPUS_PATROL_TAG);
            resourceCountStr = getArguments().getString(CampusPatrolMainFragment
                    .CAMPUS_PATROL_RESOURCE_COUNT_STR);
            info = (TeacherDataStaticsInfo) getArguments().
                    getSerializable(TeacherDataStaticsInfo.class.getSimpleName());
            schoolInfo = (SchoolInfo) getArguments().
                    getSerializable(SchoolInfo.class.getSimpleName());
            startDate = getArguments().getString(CampusPatrolMainFragment.
                    CAMPUS_PATROL_SCREENING_START_DATE);
            endDate = getArguments().getString(CampusPatrolMainFragment.
                    CAMPUS_PATROL_SCREENING_END_DATE);
        }
        initViews();
        refreshData();
    }

    private void initViews() {
        TextView textView = ((TextView) findViewById(R.id.contacts_header_right_btn));
        if (textView != null) {
            if (!isCampusPatrolTag) {
                textView.setVisibility(View.INVISIBLE);
            }else {
                //校园巡查逻辑
                textView.setVisibility(View.VISIBLE);
                textView.setText(getString(R.string.screening));
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityUtils.enterCampusPatrolPickerActivity(getActivity());
                    }
                });
            }
        }

        ClearEditText editText = (ClearEditText) findViewById(R.id.search_keyword);
        if (editText != null) {
            editText.setHint(getString(R.string.search_title_or_author));
            editText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        hideSoftKeyboard(getActivity());
                        loadResourceList();
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
                    loadResourceList();
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
                    loadResourceList();
                }
            });
            view.setVisibility(View.VISIBLE);
        }
        view = findViewById(R.id.contacts_search_bar_layout);
        if (view != null) {
            if (!isCampusPatrolTag) {
                view.setVisibility(View.VISIBLE);
            }else {
                //校园巡查隐藏搜索
                view.setVisibility(View.GONE);
            }
        }

        newBtn = (TextView)findViewById(R.id.new_btn);
        if(newBtn != null) {
            if (!isCampusPatrolTag) {
                newBtn.setOnClickListener(this);
                if (isOnlineSchoolMessage){
                    //在线课堂身份全部显示
                    if (schoolInfo != null && schoolInfo.hasJoinedSchool()){
                        newBtn.setVisibility(View.VISIBLE);
                    } else {
                        newBtn.setVisibility(View.GONE);
                    }
                } else {
                    newBtn.setVisibility(isTeacher ? View.VISIBLE : View.GONE);
                }
            }else {
                //校园巡查逻辑
                newBtn.setVisibility(View.GONE);
            }
        }
        PullToRefreshView pullToRefreshView = (PullToRefreshView)
                findViewById(R.id.contacts_pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);

//        ListView listView = (ListView) findViewById(R.id.resource_list_view);
//        if (listView != null) {
//            AdapterViewHelper adapterViewHelper = new NewResourceAdapterViewHelper(
//                    getActivity(), listView) {
//                @Override
//                public void loadData() {
//                    loadResourceList();
//                }
//            };
//            setCurrAdapterViewHelper(listView, adapterViewHelper);
//        }
    }

    protected void loadResourceList() {
        if (!isCampusPatrolTag) {
            loadResourceList(keywordView.getText().toString());
        }else {
            //校园巡查逻辑
            loadCampusPatrolMaterialData(keywordView.getText().toString());
        }
    }

    protected void loadResourceList(String keyword) {
//        keyword = keyword.trim();
//        if (!keyword.equals(this.keyword)) {
//            getCurrAdapterViewHelper().clearData();
//            getPageHelper().clear();
//        }
//        this.keyword = keyword;
//        Map<String, Object> params = new HashMap();
//        params.put("SchoolId", schoolId);
//        params.put("Keyword", keyword);
//        params.put("Pager", getPageHelper().getFetchingPagerArgs());
//        DefaultPullToRefreshDataListener listener =
//                new DefaultPullToRefreshDataListener<NewResourceInfoListResult>(
//                        NewResourceInfoListResult.class) {
//            @Override
//            public void onSuccess(String jsonString) {
//                super.onSuccess(jsonString);
//                if (getResult() == null || !getResult().isSuccess()
//                        || getResult().getModel() == null) {
//                    return;
//                }
//                updateResourceListView(getResult());
//            }
//        };
//        RequestHelper.sendPostRequest(getActivity(), ServerUrl.GET_SCHOOL_COURSE_LIST_URL,
//                params, listener);
    }

    protected void updateTitleView(String title,String countStr){
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            this.title = title;
            if (!isCampusPatrolTag) {
                textView.setText(title);
            }else {
                textView.setText(title+getString(R.string.media_num,countStr));
            }
        }
    }

    protected void updateResourceListView(NewResourceInfoListResult result) {
        if (getPageHelper().isFetchingPageIndex(result.getModel().getPager())) {
            int totalCount = getPageHelper().getTotalCount();
            List<NewResourceInfo> list = result.getModel().getData();
            if (list == null || list.size() <= 0) {
                if (getPageHelper().isFetchingFirstPage()) {
                    getCurrAdapterViewHelper().clearData();
                    updateTitleView(this.title,String.valueOf(totalCount));
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
                resourceListResult.getModel().setData(getCurrAdapterViewHelper().getData());
            } else {
                getCurrAdapterViewHelper().setData(list);
                resourceListResult = result;
            }
            updateTitleView(this.title,String.valueOf(totalCount));
        }
    }

    protected void createNewResource() {
//        long dateTime = System.currentTimeMillis();
//        File noteFile = new File(Utils.NOTE_FOLDER, String.valueOf(dateTime));
//        String dateTimeStr = DateUtils.transferLongToDate("yyyy-MM-dd HH:mm:ss", dateTime);
//        NoteOpenParams params = new NoteOpenParams(noteFile.getPath(), dateTimeStr,
//                MediaPaperActivity.OPEN_TYPE_EDIT, MediaPaper.PAPER_TYPE_TIEBA, null,
//                SchoolResourceListBaseFragment.TAG, false);
//        params.schoolId = schoolId;
//        ActivityUtils.openNote(getActivity(), params);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.new_btn) {
            createNewResource();
        } else {
            super.onClick(v);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data != null){
            if (resultCode == CampusPatrolPickerFragment.RESULT_CODE){
                if (requestCode == CampusPatrolPickerFragment.REQUEST_CODE){
                    this.startDate = CampusPatrolUtils.getStartDate(data);
                    this.endDate = CampusPatrolUtils.getEndDate(data);
                    refreshData();
                }
            }
        }else {
            if (requestCode == CampusPatrolPickerFragment.EDIT_NOTE_DETAILS_REQUEST_CODE){

                //帖子打开后返回列表，需要手动更新阅读人数（手动累加）
                updateReaderNumber(itemId);

                //帖子内容改变需要刷新
                if (OnlineMediaPaperActivity.hasContentChanged()){
                    OnlineMediaPaperActivity.setHasContentChanged(false);
                    //刷新帖子
                    refreshData();
                }
            }else if (requestCode == CampusPatrolPickerFragment.CREATE_NEW_RESOURCE_REQUEST_CODE){
                if (MediaPaperActivity.hasResourceSended()){
                    MediaPaperActivity.setHasResourceSended(false);
                    //创建资源返回code
                    refreshData();
                }
            }
        }
    }

    /**
     * 更新阅读人数
     * @param itemId
     */
    protected void updateReaderNumber(String itemId) {
        if (TextUtils.isEmpty(itemId)){
            return;
        }
        AdapterViewHelper helper = getCurrAdapterViewHelper();
        if (helper != null && helper.hasData()){
            List<NewResourceInfo> infoList = helper.getData();
            if (infoList != null && infoList.size() > 0){
                for (NewResourceInfo info : infoList){
                    if (info != null){
                        String id = info.getId();
                        if (!TextUtils.isEmpty(id) && id.equals(itemId)){
                            //找到刚才点击的那个条目(id是唯一的，position不靠谱)，增加阅读数。
                            info.setReadNumber(info.getReadNumber() + 1);
                            break;
                        }
                    }
                }
            }
            //更新一下布局
            helper.update();
        }
    }
}
