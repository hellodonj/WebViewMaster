package com.galaxyschool.app.wawaschool.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.CampusPatrolUtils;
import com.galaxyschool.app.wawaschool.common.CheckAuthorizationPmnHelper;
import com.galaxyschool.app.wawaschool.common.CheckResPermissionHelper;
import com.galaxyschool.app.wawaschool.common.RefreshUtil;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.UploadReourceHelper;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.fragment.resource.CollectionFlagAdapterViewHelper;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfoListResult;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;
import com.galaxyschool.app.wawaschool.pojo.TeacherDataStaticsInfo;
import com.galaxyschool.app.wawaschool.pojo.UploadParameter;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.lqwawa.client.pojo.ResourceInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by E450 on 2016/12/23.
 */

public class TaskOrderFragment extends ContactsListFragment {
    public static final String TAG = TaskOrderFragment.class.getSimpleName();
    private static final int MAX_PIC_BOOKS_PER_ROW = 2;
    private boolean isPick=true;
    public static final String IS_PICK="is_pick";
    private String startDate,endDate;
    private boolean isCampusPatrolTag;
    private TeacherDataStaticsInfo info;
    private String resourceCountStr;
    private boolean isFinish;
    private boolean isFromOnline;
    private boolean ASSOCIATE_TASK_ORDER;//导读任务单
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_order, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadIntent();
        initViews();
        if (getActivity()!=null){
            hideSoftKeyboard(getActivity());
        }
    }
    private void loadIntent(){
       Bundle args= getArguments();
        if(args!=null){
            isPick= args.getBoolean(IS_PICK);
            isCampusPatrolTag = getArguments().getBoolean(CampusPatrolMainFragment
                    .IS_CAMPUS_PATROL_TAG);
            info = (TeacherDataStaticsInfo) getArguments().
                    getSerializable(TeacherDataStaticsInfo.class.getSimpleName());
            startDate = getArguments().getString(CampusPatrolMainFragment.
                    CAMPUS_PATROL_SCREENING_START_DATE);
            endDate = getArguments().getString(CampusPatrolMainFragment.
                    CAMPUS_PATROL_SCREENING_END_DATE);
            resourceCountStr = getArguments().getString(CampusPatrolMainFragment
                    .CAMPUS_PATROL_RESOURCE_COUNT_STR);
            isFinish = getArguments().getBoolean(MediaListFragment.EXTRA_IS_FINISH);
            isFromOnline=getArguments().getBoolean(MediaListFragment.EXTRA_IS_FORM_ONLINE,false);
            ASSOCIATE_TASK_ORDER  = getArguments().getBoolean(IntroductionForReadCourseFragment.ASSOCIATE_TASK_ORDER,false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        refreshData();
    }

    private void updateTitleView(String countStr){
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            if (!isCampusPatrolTag) {
                textView.setVisibility(View.VISIBLE);
                textView.setText(getString(R.string.task_order));
            }else {
                textView.setText(getString(R.string.task_order)
                        +getString(R.string.media_num,countStr));
            }
        }
    }

    private void initViews(){
        updateTitleView(resourceCountStr);
        ImageView imageView= (ImageView) findViewById(R.id.contacts_header_left_btn);
        if(imageView!=null){
            imageView.setVisibility(View.VISIBLE);
            imageView.setOnClickListener(this);
        }

        TextView textView = ((TextView) findViewById(R.id.contacts_header_right_btn));
        if (textView != null) {
            if (!isCampusPatrolTag) {
                if(isPick){
                    textView.setVisibility(View.VISIBLE);
                }else{
                    textView.setVisibility(View.GONE);
                }
                textView.setText(getString(R.string.ok));
                textView.setOnClickListener(this);
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

        final PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        //setStopPullUpState(true);
        setPullToRefreshView(pullToRefreshView);
        initGridview();
        refreshData();
    }

    private void refreshData() {
        getPageHelper().clear();
        loadCommonData();
    }



    private void loadCommonData() {
        Map<String, Object> params = new HashMap();
        if (!isCampusPatrolTag){
            //收藏时过滤课件不是自己的
            if (isPick) {
                params.put("Author", getMemeberId());
            }
            params.put("CreatorId",getMemeberId());
            params.put("IsContainsMyCollection",true);
        }else {
            //校园巡查逻辑
            if(info == null){
                return;
            }
            params.put("CreatorId",info.getTeacherId());
            if (!TextUtils.isEmpty(startDate)) {//时间格式：2016-12-11
                params.put("StrStartTime", startDate);//统计开始时间,非必填。
            }
            if (!TextUtils.isEmpty(endDate)) {//时间格式：2016-12-11
                params.put("StrEndTime", endDate);//统计结束时间,非必填。
            }
            //校园巡查过滤收藏的任务单
            params.put("IsContainsMyCollection",false);
        }
        params.put("PageIndex", getPageHelper().getFetchingPagerArgs().getPageIndex());
        params.put("PageSize", getPageHelper().getFetchingPagerArgs().getPageSize());
        DefaultPullToRefreshDataListener listener =
                new DefaultPullToRefreshDataListener<NewResourceInfoListResult>(
                        NewResourceInfoListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        int totalCount = 0;
                        try {
                            JSONObject root = new JSONObject(jsonString);
                            JSONObject object = root.optJSONObject("Model");
                            if (object != null && object.has("Total")){
                                totalCount = object.optInt("Total");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        NewResourceInfoListResult result = getResult();
                        if (result == null || !result.isSuccess()
                                || result.getModel() == null) {
                            return;
                        }
                        updateNoteBookListView(result,totalCount);
                    }
                };
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_GUIDANCE_CARD_URL, params, listener);
    }


    private void initGridview() {
        final GridView gridView = (GridView) findViewById(R.id.book_grid_view);
        if (gridView != null) {
            //设置基本参数
            final int maxColumn = 2;
            gridView.setNumColumns(maxColumn);
            final int minPadding = getResources().getDimensionPixelSize
                    (R.dimen.min_padding);
            gridView.setPadding(minPadding, minPadding, minPadding, minPadding);
            gridView.setHorizontalSpacing(minPadding);
            gridView.setVerticalSpacing(minPadding);
                AdapterViewHelper adapterViewHelper = new CollectionFlagAdapterViewHelper(
                    getActivity(), gridView,maxColumn,minPadding) {
                @Override
                public void loadData() {
                    loadCommonData();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                   final  NewResourceInfo data = (NewResourceInfo) getDataAdapter().getItem
                           (position);
                    if (data == null) {
                        return view;
                    }
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;

                    //标题
                    TextView textView = (TextView) view.findViewById(R.id.resource_title);
                    if (textView != null) {
                        textView.setSingleLine(false);
                        textView.setPadding(5,5,5,5);
                        textView.setTextSize(12);
                        textView.setLines(2);
                        textView.setText(data.getTitle());
                    }
                    //收藏标识
                    ImageView collectImageView = (ImageView) view.findViewById(R.id.icon_collect);
                    if (collectImageView != null){
                        if (data.isMyCollection()){
                            //显示
                            collectImageView.setVisibility(View.VISIBLE);
                        }else {
                            collectImageView.setVisibility(View.GONE);
                        }
                    }

                    //选择状态
                   final ImageView selectImageView = (ImageView) view.
                           findViewById(R.id.resource_selector);
                    if(isPick){
                        selectImageView.setVisibility(View.VISIBLE);
                        //选择状态
                        selectImageView.setSelected(data.isSelect());
                    }else{
                        selectImageView.setVisibility(View.GONE);
                    }
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(isPick){
                                //选择界面，点击选中或者取消
                                  List<NewResourceInfo> list =  getCurrAdapterViewHelper().getData();
                                  for(NewResourceInfo data1 : list){
                                      if(data1.isSelect()&&data1!=data){
                                          data1.setIsSelect(false);
                                          RefreshUtil.getInstance().removeId(data1.getId());
                                          break;
                                      }
                                  }
                                 data.setIsSelect(!data.isSelect());
                                if (data.isSelect()) {
                                    RefreshUtil.getInstance().addId(data.getId());
                                }else {
                                    RefreshUtil.getInstance().removeId(data.getId());
                                }
                                 getCurrAdapterViewHelper().update();
                            }else{
                                //非选择界面，跳转到拆分页
                                data.setIsFromLqTask(true);
                                ActivityUtils.enterTaskOrderDetailActivity(getActivity(),data);
                            }
                        }
                    });
                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        return;
                    }
                    NewResourceInfo data = (NewResourceInfo) holder.data;
                }
            };
            setCurrAdapterViewHelper(gridView, adapterViewHelper);
        }
    }

    private void updateNoteBookListView(NewResourceInfoListResult result ,int totalCount) {
        List<NewResourceInfo> list = result.getModel().getData();
        if (list == null || list.size() <= 0) {
            if (getPageHelper().isFetchingFirstPage()) {
                getCurrAdapterViewHelper().clearData();
                TipsHelper.showToast(getActivity(),
                        getString(R.string.no_data));
                updateTitleView(String.valueOf(totalCount));
            } else {
                TipsHelper.showToast(getActivity(),
                        getString(R.string.no_more_data));
            }
            return;
        }

        if (getPageHelper().isFetchingFirstPage()) {
            if( getCurrAdapterViewHelper().hasData()){
                getCurrAdapterViewHelper().clearData();
            }
        }

        getPageHelper().updateByPagerArgs(result.getModel().getPager());
        getPageHelper().setCurrPageIndex(
                getPageHelper().getFetchingPageIndex());

        RefreshUtil.getInstance().refresh(list);
        if ( getCurrAdapterViewHelper().hasData()) {
            getCurrAdapterViewHelper().getData().addAll(list);
            getCurrAdapterViewHelper().update();
        } else {
            getCurrAdapterViewHelper().setData(list);

        }
        updateTitleView(String.valueOf(totalCount));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.contacts_header_left_btn:
                if (!isFinish) {
                    popStack();
                }else {
                    if (getActivity() != null){
                        getActivity().finish();
                    }
                }
                break;
            case R.id.contacts_header_right_btn:
               List<NewResourceInfo> list = getCurrAdapterViewHelper().getData();
                if(list==null ||list.size()==0){
                    TipMsgHelper.ShowLMsg(getActivity(),getString(R.string.no_data));
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
                    TipMsgHelper.ShowLMsg(getActivity(), R.string.no_file_select);
                    return;
                }
                getSelectTaskOrderData(selectData);
                break;

        }
    }

    private void getSelectTaskOrderData(NewResourceInfo selectData) {
        if (isFromOnline){
            ArrayList<ResourceInfo> resourceInfos = new ArrayList<ResourceInfo>();
            ResourceInfo resourceInfo = new ResourceInfo();
            resourceInfo.setTitle(selectData.getTitle());
            resourceInfo.setImgPath(AppSettings.getFileUrl(selectData.getThumbnail()));
            resourceInfo.setResourcePath(AppSettings.getFileUrl(selectData.getResourceUrl()));
            resourceInfo.setShareAddress(selectData.getShareAddress());
            resourceInfo.setResourceType(selectData.getResourceType());
            String resId=selectData.getMicroId();
            if (resId.contains("-")){
                resourceInfo.setResId(resId);
            }else {
                resourceInfo.setResId(resId+"-"+selectData.getResourceType());
            }
            resourceInfos.add(resourceInfo);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("resourseInfoList", resourceInfos);
            Intent intent = new Intent();
            intent.putExtras(bundle);
            if (getActivity() != null) {
                getActivity().setResult(getActivity().RESULT_OK, intent);
                getActivity().finish();
            }
        }else {
            IntroductionForReadCourseFragment fragment = (IntroductionForReadCourseFragment)
                    getFragmentManager().findFragmentByTag(IntroductionForReadCourseFragment.TAG);

            if (ASSOCIATE_TASK_ORDER) {
                fragment.setWorkOrderId(selectData.getMicroId());
                fragment.setResourceUrl(selectData.getResourceUrl());
                fragment.setConnectThumbnail(selectData.getThumbnail());
                getArguments().putInt(ActivityUtils.EXTRA_TASK_TYPE,StudyTaskType.INTRODUCTION_WAWA_COURSE);
            } else {
                int taskType = getArguments().getInt(ActivityUtils.EXTRA_TASK_TYPE);
                if (taskType == StudyTaskType.TASK_ORDER) {
                    CourseData courseData = new CourseData();
                    String resId = selectData.getMicroId();
                    if (resId.contains("-")) {
                        int i = resId.indexOf("-");
                        resId = resId.substring(0, i);
                    }
                    courseData.id = Integer.parseInt(resId);
                    courseData.nickname = selectData.getTitle();
                    courseData.type = selectData.getResourceType();
                    courseData.resourceurl = selectData.getResourceUrl();
                    courseData.thumbnailurl = selectData.getThumbnail();
                    UploadParameter uploadParameter = UploadReourceHelper.getUploadParameter(getUserInfo(),
                            null, courseData, null, 1);
                    if (uploadParameter != null) {
                        if (getArguments() != null) {

                            uploadParameter.setTaskType(taskType);
                            fragment.setData(uploadParameter);
                        }
                    }
                }
            }
            fragment.setWorkOrderId(selectData.getMicroId());
            fragment.setResourceUrl(selectData.getResourceUrl());
            fragment.setConnectThumbnail(selectData.getThumbnail());
            int num=getFragmentManager().getBackStackEntryCount();
            while (num > 1) {
                popStack();
                num = num - 1;
            }
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
        }
    }
}
