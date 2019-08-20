package com.galaxyschool.app.wawaschool.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.Note.MediaPaperActivity;
import com.galaxyschool.app.wawaschool.Note.OnlineMediaPaperActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.*;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.fragment.resource.NewResourceAdapterViewHelper;
import com.galaxyschool.app.wawaschool.pojo.MediaInfo;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.lqwawa.client.pojo.MediaType;
import com.lqwawa.client.pojo.ResourceInfo;
import com.lqwawa.lqbaselib.net.library.DataResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.CourseInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfoTag;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfoTagListResult;
import com.galaxyschool.app.wawaschool.pojo.TeacherDataStaticsInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.NoteOpenParams;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.sortlistview.ClearEditText;
import com.lqwawa.libs.mediapaper.MediaPaper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class PersonalPostBarListFragment extends ContactsListFragment {

    public static final String TAG = PersonalPostBarListFragment.class.getSimpleName();

    public interface Constants {
        String EXTRA_MEMBER_ID = "memberId";
        String EXTRA_IS_TEACHER = "isTeacher";
    }

    private String memberId;
    private boolean isTeacher;
    private boolean isMyself;
    private TextView keywordView;
    private String keyword = "";
    private NewResourceInfoTagListResult resourceListResult;
    private boolean isCampusPatrolTag;
    private String resourceCountStr;
    private String startDate,endDate;
    private TeacherDataStaticsInfo info;
    private String itemId = null;//标识当前选中的条目id
    private boolean isPick;
    private int maxSelectCount = 1;
    private int taskType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_personal_post_bar_list, null);
    }

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
////            if (!getCurrAdapterViewHelper().hasData()) {
//                loadResourceList();
////            }
//        }
    }

    private void refreshData(){
        getPageHelper().clear();
        loadResourceList();
    }

    private void init() {
        if (getArguments() != null) {
            memberId = getArguments().getString(Constants.EXTRA_MEMBER_ID);
            isTeacher = getArguments().getBoolean(Constants.EXTRA_IS_TEACHER);
            isMyself = getUserInfo().getMemberId().equals(memberId);
            isCampusPatrolTag = getArguments().getBoolean(CampusPatrolMainFragment
                    .IS_CAMPUS_PATROL_TAG);
            resourceCountStr = getArguments().getString(CampusPatrolMainFragment
                    .CAMPUS_PATROL_RESOURCE_COUNT_STR);
            info = (TeacherDataStaticsInfo) getArguments().
                    getSerializable(TeacherDataStaticsInfo.class.getSimpleName());
            startDate = getArguments().getString(CampusPatrolMainFragment.
                    CAMPUS_PATROL_SCREENING_START_DATE);
            endDate = getArguments().getString(CampusPatrolMainFragment.
                    CAMPUS_PATROL_SCREENING_END_DATE);
            isPick = getArguments().getBoolean(ActivityUtils.EXTRA_IS_PICK);
            maxSelectCount = getArguments().getInt(ActivityUtils.EXTRA_SELECT_MAX_COUNT);
            taskType = getArguments().getInt(ActivityUtils.EXTRA_TASK_TYPE);

        }
        initViews();
        refreshData();
    }

    private void initViews() {
        updateTitleView(resourceCountStr);
        TextView textView = ((TextView) findViewById(R.id.contacts_header_right_btn));
        if (textView != null) {
            if (!isCampusPatrolTag) {
                if (isPick){
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(R.string.confirm);
                    textView.setOnClickListener(this);
                } else {
                    textView.setVisibility(View.INVISIBLE);
                }
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
            editText.setHint(getString(R.string.search_title));
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
            if (!isCampusPatrolTag && !isPick) {
                view.setVisibility(View.VISIBLE);
            }else {
                //校园巡查隐藏搜索
                view.setVisibility(View.GONE);
            }
        }

        view = findViewById(R.id.new_btn);
        if(view != null) {
            view.setOnClickListener(this);
            if (!isCampusPatrolTag && !isPick) {
                view.setVisibility(isMyself ? View.VISIBLE : View.GONE);
            }else {
                //校园巡查逻辑
                view.setVisibility(View.GONE);
            }
        }

        PullToRefreshView pullToRefreshView = (PullToRefreshView)
                findViewById(R.id.contacts_pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);

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
                    //红点未读提醒
                    ImageView imageView = (ImageView) view.findViewById(R.id.resource_indicator);
                    if (imageView != null) {
                        //校园巡查要隐藏小红点
                        if (isCampusPatrolTag || isPick){
                            imageView.setVisibility(View.GONE);
                        }
                    }
                    imageView = (ImageView) view.findViewById(R.id.resource_delete);
                    if (imageView != null) {
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                NewResourceInfo data = (NewResourceInfo) v.getTag();
                                if (data != null) {
                                    showDeleteResourceDialog(data,
                                            getString(R.string.delete_postbar));
                                }
                            }
                        });
                        if (!isCampusPatrolTag && !isPick) {
                            if (isMyself) {
                                imageView.setVisibility(View.VISIBLE);
                            }
                        }else {
                            //校园巡查需要隐藏删除按钮
                            imageView.setVisibility(View.GONE);
                        }
                    }

                    ImageView selectImageView = (ImageView) view.findViewById(R.id.item_selector);
                    if (isPick){
                        selectImageView.setVisibility(View.VISIBLE);
                        if(data.isSelect()){
                            selectImageView.setImageResource(R.drawable.contacts_item_sel);
                        }else{
                            selectImageView.setImageResource(R.drawable.contacts_item_unsel);
                        }
                    } else {
                        selectImageView.setVisibility(View.GONE);
                    }

                    TextView textView = (TextView) view.findViewById(R.id.resource_title);
                    if (textView != null){
                        textView.setGravity(Gravity.CENTER_VERTICAL);
                        SpannableString spannableString;
                        if (data.getType() == 1){
                            spannableString = new SpannableString(" " + data.getTitle());
                            //已收藏
                            Drawable leftDrawable =getResources().getDrawable(
                                    R.drawable.icon_star);
                            leftDrawable.setBounds(0,0,leftDrawable.getMinimumWidth(),
                                    leftDrawable.getMinimumHeight());
                            ImageSpan imageSpan = new ImageSpan(leftDrawable,ImageSpan.ALIGN_BASELINE);
                            spannableString.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        } else {
                            spannableString = new SpannableString(data.getTitle());
                        }
                        textView.setText(spannableString);
                    }

                    //优先选择更新的时间
                  TextView  time = (TextView) view.findViewById(R.id.resource_time);
                    if (time != null) {
                        if (TextUtils.isEmpty(data.getUpdatedTime())){
                            time.setText(DateUtils.getStringToString(data.getCreatedTime(),DateUtils.DATE_PATTERN_yyyy_MM_dd_HH_MM));
                        }else {
                            time.setText(DateUtils.getStringToString(data.getUpdatedTime(),DateUtils.DATE_PATTERN_yyyy_MM_dd_HH_MM));
                        }
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
                        if (isPick) {
                            selectData(data);
                        }else {
                            //记录当前点击的条目Id
                            itemId = data.getId();
                            //用于控制note详情页面编辑按钮的显示和隐藏
                            CourseInfo courseInfo = data.getCourseInfo();
                            if (courseInfo != null) {
                                courseInfo.setCampusPatrolTag(isCampusPatrolTag);
                            }
                            ActivityUtils.openOnlineNote(getActivity(), courseInfo, true,
                                    false);
                        }
                    }
                }
            };
            setCurrAdapterViewHelper(listView, adapterViewHelper);
        }
    }

    private void selectData(NewResourceInfo data){
        if (taskType == StudyTaskType.SUBMIT_HOMEWORK || taskType == StudyTaskType.WATCH_HOMEWORK) {
            if (!data.isSelect()) {
                int count = getSelectedDataCount();
                if (count > 0 && count >= maxSelectCount) {
                    TipMsgHelper.ShowLMsg(getActivity(), getString(R.string
                            .n_pick_max_count, String.valueOf(maxSelectCount)));
                    return;
                }
            }
        } else {
            List<NewResourceInfo> list =  getCurrAdapterViewHelper().getData();
            for(NewResourceInfo data1 : list){
                if(data1.isSelect()&&data1!=data){
                    data1.setIsSelect(false);
                    RefreshUtil.getInstance().removeId(data1.getId());
                    break;
                }
            }
        }
        //选择界面，点击选中或者取消
        data.setIsSelect(!data.isSelect());
        if (data.isSelect()) {
            RefreshUtil.getInstance().addId(data.getId());
        }else {
            RefreshUtil.getInstance().removeId(data.getId());
        }
        getCurrAdapterViewHelper().update();
    }

    /**
     * 获得选取的资源数量
     * @return
     */
    private int getSelectedDataCount() {
        List dataList = getCurrAdapterViewHelper().getData();
        if (dataList != null && dataList.size() > 0) {
            List<NewResourceInfo> resultList = new ArrayList<>();
            for (Object data : dataList) {
                NewResourceInfo info = (NewResourceInfo) data;
                if (info != null && info.isSelect()) {
                    resultList.add(info);
                }
            }
            return resultList.size();
        }
        return 0;
    }

    public void getSelectTaskData() {
        if (taskType == StudyTaskType.WATCH_HOMEWORK || taskType == StudyTaskType.SUBMIT_HOMEWORK){
            List<MediaInfo> mediaInfoList = getSelectedData();
            if (mediaInfoList == null || mediaInfoList.size() == 0){
                TipMsgHelper.ShowMsg(getActivity(),R.string.pls_select_files);
                return;
            }
            //处理分页数据
            WatchWawaCourseResourceSplicingUtils.splicingSplitPageResources(getActivity(),
                    mediaInfoList, StudyTaskType.SUBMIT_HOMEWORK);
            return;
        }
        List<NewResourceInfo> list = getCurrAdapterViewHelper().getData();
        if(list==null ||list.size()==0){
            TipMsgHelper.ShowLMsg(getActivity(),getString(R.string.pls_select_files));
            return;
        }
        NewResourceInfo selectData=null;
        for(NewResourceInfo data : list){
            if(data.isSelect()){
                selectData=data;
                break;
            }
        }
        if(selectData==null){
            TipMsgHelper.ShowLMsg(getActivity(), R.string.pls_select_files);
            return;
        }
        ArrayList<ResourceInfo> resourceInfo= new ArrayList<ResourceInfo>();
        ResourceInfo resource = new ResourceInfo();
        resource.setTitle(selectData.getTitle());
        resource.setImgPath(AppSettings.getFileUrl(selectData.getThumbnail()));
        resource.setResourcePath(AppSettings.getFileUrl(selectData.getResourceUrl()));
        resource.setShareAddress(selectData.getShareAddress());
        resource.setResourceType(selectData.getResourceType());
        String resId=selectData.getMicroId();
        if (resId.contains("-")){
            resource.setResId(resId);
        }else {
            resource.setResId(resId+"-"+selectData.getResourceType());
        }
        resourceInfo.add(resource);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("resourseInfoList", resourceInfo);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        if (getActivity() != null) {
            getActivity().setResult(RESULT_OK, intent);
            getActivity().finish();
        }
    }

    private List<MediaInfo> getSelectedData() {
        List dataList = getCurrAdapterViewHelper().getData();
        if (dataList != null && dataList.size() > 0) {
            int mediaType = StudyTaskType.SUBMIT_HOMEWORK;
            List<MediaInfo> resultList = new ArrayList<>();
            for (Object data : dataList) {
                NewResourceInfo newResourceInfo = (NewResourceInfo) data;
                if (newResourceInfo != null && newResourceInfo.isSelect()) {
                    //转换数据
                    MediaInfo mediaInfo = new MediaInfo();
                    mediaInfo.setId(newResourceInfo.getId());
                    //设置作者id
                    mediaInfo.setAuthorId(newResourceInfo.getAuthorId());
                    mediaInfo.setMicroId(newResourceInfo.getMicroId());
                    mediaInfo.setType(newResourceInfo.getType());
                    mediaInfo.setMediaType(mediaType);
                    mediaInfo.setResourceType(newResourceInfo.getResourceType());
                    mediaInfo.setPath(newResourceInfo.getResourceUrl());
                    mediaInfo.setThumbnail(newResourceInfo.getThumbnail());
                    mediaInfo.setShareAddress(newResourceInfo.getShareAddress());
                    mediaInfo.setTitle(newResourceInfo.getTitle());
                    mediaInfo.setUpdateTime(newResourceInfo.getUpdatedTime());
                    //我的收藏和个人空间区分，0-个人空间，1-我的收藏
                    mediaInfo.setCourseInfo(newResourceInfo.getCourseInfo());
                    mediaInfo.setAuthor(newResourceInfo.getAuthorName());
                    if (!TextUtils.isEmpty(newResourceInfo.getCreatedTime())){
                        mediaInfo.setCreateTime(DateUtils.stringToDate(newResourceInfo
                                .getCreatedTime(),DateUtils.DATE_PATTERN_yyyy_MM_dd_HH_MM).getTime());
                    }
                    resultList.add(mediaInfo);
                }
            }
            return resultList;
        }
        return null;
    }



    private void loadResourceList() {
        if (!isCampusPatrolTag) {
            loadResourceList(keywordView.getText().toString());
        }else {
            //校园巡查逻辑
            loadCampusPatrolMaterialData(keywordView.getText().toString());
        }
    }

    private void loadCampusPatrolMaterialData(String keyword) {

        keyword = keyword.trim();
        if (!keyword.equals(this.keyword)) {
            getCurrAdapterViewHelper().clearData();
            getPageHelper().clear();
        }
        this.keyword = keyword;

        if (info == null){
            return;
        }
        Map<String, Object> params = new HashMap();
        //云贴吧 6
        params.put("MType", 6);
        // 用户ID，必须
        params.put("MemberId", info.getTeacherId());
        //关键字,非必填。
        params.put("KeyWord",keyword);

        if (!TextUtils.isEmpty(startDate)) {//时间格式：2016-12-11
            params.put("StrStartTime", startDate);//统计开始时间,非必填。
        }
        if (!TextUtils.isEmpty(endDate)) {//时间格式：2016-12-11
            params.put("StrEndTime", endDate);//统计结束时间,非必填。
        }
        //分页信息,必填。
        params.put("Pager",getPageHelper().getFetchingPagerArgs());
        DefaultPullToRefreshDataListener listener =
                new DefaultPullToRefreshDataListener<NewResourceInfoTagListResult>(
                        NewResourceInfoTagListResult.class) {
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
                ServerUrl.PR_LOAD_MEDIA_LIST_URL, params, listener);
    }

    private void loadResourceList(String keyword) {
        keyword = keyword.trim();
        if (!keyword.equals(this.keyword)) {
            getCurrAdapterViewHelper().clearData();
            getPageHelper().clear();
        }
        this.keyword = keyword;
        Map<String, Object> params = new HashMap();
        params.put("MemberId", memberId);
        params.put("KeyWord", keyword);
        //云贴吧类型：6
        params.put("MType","6");
        params.put("Pager", getPageHelper().getFetchingPagerArgs());
        DefaultPullToRefreshDataListener listener =
                new DefaultPullToRefreshDataListener<NewResourceInfoTagListResult>(
                        NewResourceInfoTagListResult.class) {
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
                ServerUrl.SEARCH_PERSONAL_SPACE_LIST_URL, params, listener);
    }

    private void updateTitleView(String countStr){
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            if (!isCampusPatrolTag) {
                if (isPick){
                    textView.setText(R.string.other);
                } else {
                    textView.setText(R.string.cs_cloud_post_bar);
                }
            }else {
                textView.setText(getString(R.string.cs_cloud_post_bar)+getString(R.string
                        .media_num,countStr));
            }
        }
    }

    private void updateResourceListView(NewResourceInfoTagListResult result) {
        if (getPageHelper().isFetchingPageIndex(result.getModel().getPager())) {
            //获得总记录数
            int totalCount = getPageHelper().getTotalCount();
            List<NewResourceInfoTag> list = result.getModel().getData();
            if (list == null || list.size() <= 0) {
                if (getPageHelper().isFetchingFirstPage()) {
                    getCurrAdapterViewHelper().clearData();
                    //更新记录数
                    updateTitleView(String.valueOf(totalCount));
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

            //更新记录数
            updateTitleView(String.valueOf(totalCount));
        }
    }

    private void showDeleteResourceDialog(final NewResourceInfo data, String title) {
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(
                getActivity(), null,
                title,
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
                        if(data.getType() == 1){//删除收藏的，不级联删除
                            deleteCollectionResource(data);
                        }else{//自己的，级联删除
                            deleteResource(data);
                        }
                    }
                });
        messageDialog.show();
    }
    private void deleteCollectionResource(NewResourceInfo data) {
        Map<String, Object> params = new HashMap();
        params.put("CollectionId", data.getId());
        RequestHelper.RequestListener listener =
                new RequestHelper.RequestListener<DataResult>(
                        getActivity(), DataResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()) {
                            TipsHelper.showToast(getActivity(), R.string.delete_failure);
                            return;
                        } else {
                            TipsHelper.showToast(getActivity(), R.string.delete_success);
                            NewResourceInfo data = (NewResourceInfo) getTarget();
                            getCurrAdapterViewHelper().getData().remove(data);
                            getCurrAdapterViewHelper().update();
                        }
                    }
                };
        listener.setTarget(data);
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.DELETE_COLLECTION_URL,
                params, listener);
    }

    private void deleteResource(NewResourceInfo data) {
        Map<String, Object> params = new HashMap();
        params.put("PostBarId", data.getId());
        RequestHelper.RequestListener listener =
                new RequestHelper.RequestListener<DataResult>(
                        getActivity(), DataResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                if (getResult() == null || !getResult().isSuccess()) {
                    TipsHelper.showToast(getActivity(), R.string.delete_failure);
                    return;
                } else {
                    TipsHelper.showToast(getActivity(), R.string.delete_success);
                    NewResourceInfo data = (NewResourceInfo) getTarget();
                    getCurrAdapterViewHelper().getData().remove(data);
                    getCurrAdapterViewHelper().update();

                }
            }
        };
        listener.setTarget(data);
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.DELETE_POSTBAR_URL,
                params, listener);
    }

    private void createNewResource() {
        long dateTime = System.currentTimeMillis();
        File noteFile = new File(Utils.NOTE_FOLDER, String.valueOf(dateTime));
        String dateTimeStr = DateUtils.transferLongToDate("yyyy-MM-dd HH:mm:ss", dateTime);
        NoteOpenParams openParams = new NoteOpenParams(noteFile.getPath(), dateTimeStr,
            MediaPaperActivity.OPEN_TYPE_EDIT, MediaPaper.PAPER_TYPE_TIEBA, null,
            MediaPaperActivity.SourceType.CLOUD_POST_BAR, false);
        openParams.isTeacher = isTeacher;
        ActivityUtils.openLocalNote(getActivity(), openParams,
                CampusPatrolPickerFragment.CREATE_NEW_RESOURCE_REQUEST_CODE);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.new_btn) {
            createNewResource();
        } else if (v.getId() == R.id.contacts_header_right_btn){
            getSelectTaskData();
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
                    //创建资源返回code
                    refreshData();
                }
            }
        }else {
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
    private void updateReaderNumber(String itemId) {
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
