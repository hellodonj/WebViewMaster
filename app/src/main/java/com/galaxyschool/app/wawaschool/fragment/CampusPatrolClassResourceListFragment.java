package com.galaxyschool.app.wawaschool.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.duowan.mobile.netroid.NetroidError;
import com.galaxyschool.app.wawaschool.ClassResourceListActivity;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.Note.OnlineMediaPaperActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.ScreeningHomeworkResultActivity;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.CampusPatrolUtils;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.ClassDataStaticsInfo;
import com.galaxyschool.app.wawaschool.pojo.ClassDataStaticsInfoListResult;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 校园巡查---班级资源页面
 */
public class CampusPatrolClassResourceListFragment extends ContactsListFragment
        implements View.OnClickListener {

    public static final String TAG = CampusPatrolClassResourceListFragment.class.getSimpleName();

    private String startDate,endDate;
    private ClassDataStaticsInfo info;
    private SchoolInfo schoolInfo;

    private static final int RES_TYPE_NOTICE = 0;
    private static final int RES_TYPE_SHOW = 1;
    private static final int RES_TYPE_GEN_E_SCHOOL = 2;
    private static final int RES_TYPE_HOMEWORK = 3;
    private boolean isTempData;
    private List<ItemDataEntity> groupList = new ArrayList<>();
    private static boolean hasDataChanged;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.campus_patrol_class_resource_fragment_list, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void init() {
        if (getArguments() != null) {
            info = (ClassDataStaticsInfo) getArguments().
                    getSerializable(ClassDataStaticsInfo.class.getSimpleName());
            schoolInfo = (SchoolInfo) getArguments().
                    getSerializable(SchoolInfo.class.getSimpleName());
            startDate = getArguments().getString(CampusPatrolMainFragment.
                    CAMPUS_PATROL_SCREENING_START_DATE);
            endDate = getArguments().getString(CampusPatrolMainFragment.
                    CAMPUS_PATROL_SCREENING_END_DATE);
        }
        initViews();
        loadIntentData();
    }
    private void loadIntentData() {
        loadGroupData();
    }

    private void initViews() {
        if(info != null) {
            updateTitleView(String.valueOf(info.getSum()));
        }
        TextView textView = ((TextView) findViewById(R.id.contacts_header_right_btn));
        if (textView != null) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(getString(R.string.screening));
            textView.setOnClickListener(this);
        }
        ImageView imageView = (ImageView) findViewById(R.id.contacts_header_right_ico);
        if (imageView != null) {
            imageView.setVisibility(View.INVISIBLE);
        }

        PullToRefreshView pullToRefreshView = (PullToRefreshView)
                findViewById(R.id.pull_to_refresh);
        pullToRefreshView.setRefreshEnable(false);//屏蔽刷新
        setPullToRefreshView(pullToRefreshView);

        final ListView listView = (ListView) findViewById(R.id.my_list_view);
        if (listView != null) {
            listView.setDividerHeight(0);
            final int padding = (int) (10 * MyApplication.getDensity());
            listView.setPadding(0,padding,0,padding);
            AdapterViewHelper adapterViewHelper = new AdapterViewHelper(getActivity()
            ,listView,R.layout.campus_patrol_expandable_list_group_item) {

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position,convertView,parent);

                    ItemDataEntity data = (ItemDataEntity)getDataAdapter().getItem(position);
                    if (data == null){
                        return view;
                    }
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;

                    ImageView imageView = (ImageView) view.findViewById(R.id.image);
                    if (imageView != null) {
                        imageView.setVisibility(View.GONE);
                    }
                    TextView keyView = (TextView) view.findViewById(R.id.key);
                    if (keyView != null) {
                        keyView.setText(data.key);
                    }

                    TextView valueView = (TextView) view.findViewById(R.id.value);
                    if (valueView != null) {
                        valueView.setText(data.value);
                    }
                    imageView = (ImageView) view.findViewById(R.id.indicator);
                    if (imageView != null) {
                        imageView.setImageResource(R.drawable.rightarrow_black);
                    }

                    //头部分割线
                    View topLine = view.findViewById(R.id.top_line);
                    if (topLine != null){
                        if (position == 0) {
                            topLine.setVisibility(View.VISIBLE);
                        }else {
                            topLine.setVisibility(View.GONE);
                        }
                    }

                    view.setTag(holder);
                    return view;
                }

                @Override
                public void loadData() {
                    loadGroupData();
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        return;
                    }
                    ItemDataEntity data = (ItemDataEntity) holder.data;
                    controlEvent(data);
                }
            };
            setCurrAdapterViewHelper(listView, adapterViewHelper);
        }
    }

    private void controlEvent(ItemDataEntity data) {
       if (data == null){
           return;
       }

        //子条目跳转逻辑
        switch (data.type){

            case RES_TYPE_NOTICE :
            case RES_TYPE_SHOW :
            case RES_TYPE_GEN_E_SCHOOL :
                enterClassResourceListActivity(data);
                break;

            //学习任务
            case RES_TYPE_HOMEWORK :
                enterScreeningHomeworkResultActivity(data);
                break;

            default:
                break;
        }

    }

    private void enterScreeningHomeworkResultActivity(ItemDataEntity data) {
        if (data == null){
            return;
        }
        //支持查看全部任务类型，以逗号分隔。
        String taskTypes = "0,2,3,4,5,6";
        Intent intent = new Intent(getActivity(), ScreeningHomeworkResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(CampusPatrolMainFragment.IS_CAMPUS_PATROL_TAG,true);
        bundle.putString(CampusPatrolMainFragment.CAMPUS_PATROL_TASK_TYPE,taskTypes);
        bundle.putInt(CampusPatrolMainFragment.CAMPUS_PATROL_SEARCH_TYPE,CampusPatrolMainFragment
                .CAMPUS_PATROL_SEARCH_TYPE_CLASS);
        bundle.putString(CampusPatrolMainFragment.CAMPUS_PATROL_RESOURCE_NAME,data.key);
        bundle.putString(CampusPatrolMainFragment.CAMPUS_PATROL_RESOURCE_COUNT_STR,data.value);
        bundle.putSerializable(ClassDataStaticsInfo.class.getSimpleName(),info);
        bundle.putSerializable(SchoolInfo.class.getSimpleName(),schoolInfo);
        bundle.putString(CampusPatrolMainFragment.CAMPUS_PATROL_SCREENING_START_DATE,startDate);
        bundle.putString(CampusPatrolMainFragment.CAMPUS_PATROL_SCREENING_END_DATE,endDate);
        intent.putExtras(bundle);
        startActivity(intent);

    }

    private void enterClassResourceListActivity(ItemDataEntity data) {

        if (data == null){
            return;
        }
        int channelType = -1;
        switch (data.type){
            case RES_TYPE_NOTICE :
                isTempData=false;
            channelType = ClassResourceListActivity.CHANNEL_TYPE_NOTICE;
            break;
            case RES_TYPE_SHOW :
                isTempData=true;
                channelType = ClassResourceListActivity.CHANNEL_TYPE_SHOW;
                break;
            case RES_TYPE_GEN_E_SCHOOL :
                isTempData=false;
                channelType = ClassResourceListActivity.CHANNEL_TYPE_LECTURE;
                break;
        }
        Intent intent = new Intent(getActivity(), ClassResourceListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(CampusPatrolMainFragment.IS_CAMPUS_PATROL_TAG,true);
        bundle.putInt(ClassResourceListActivity.EXTRA_CHANNEL_TYPE,channelType);
        bundle.putBoolean(ClassResourceListActivity.EXTRA_CLASSINFO_TEMP_TYPE_DATA,isTempData);
        bundle.putString(ClassResourceListActivity.EXTRA_SCHOOL_ID,schoolInfo.getSchoolId());
        bundle.putInt(CampusPatrolMainFragment.CAMPUS_PATROL_RESOURCE_TYPE,CampusPatrolMainFragment
                .CAMPUS_PATROL_RESOURCE_TYPE_CLASS);
        bundle.putString(CampusPatrolMainFragment.CAMPUS_PATROL_RESOURCE_COUNT_STR,data.value);
        bundle.putSerializable(ClassDataStaticsInfo.class.getSimpleName(),info);
        bundle.putSerializable(SchoolInfo.class.getSimpleName(),schoolInfo);
        bundle.putString(CampusPatrolMainFragment.CAMPUS_PATROL_SCREENING_START_DATE,startDate);
        bundle.putString(CampusPatrolMainFragment.CAMPUS_PATROL_SCREENING_END_DATE,endDate);
        intent.putExtras(bundle);
        //进入通知、秀秀页面
        startActivityForResult(intent,CampusPatrolPickerFragment.EDIT_NOTE_DETAILS_REQUEST_CODE);
    }

    private void loadGroupData() {
        if (info == null){
            return;
        }
        groupList.clear();
        ItemDataEntity groupItem;

        //通知
        groupItem = new ItemDataEntity();
        groupItem.type = RES_TYPE_NOTICE;
        groupItem.key = getString(R.string.notices);
        groupItem.value = String.valueOf(info.getMsg_NoticeCount());
        groupList.add(groupItem);

        //秀秀
        groupItem = new ItemDataEntity();
        groupItem.type = RES_TYPE_SHOW;
        groupItem.key = getString(R.string.shows);
        groupItem.value = String.valueOf(info.getMsg_MienCount());
        groupList.add(groupItem);

        //创e学堂
        groupItem = new ItemDataEntity();
        groupItem.type = RES_TYPE_GEN_E_SCHOOL;
        groupItem.key = getString(R.string.lectures);
        groupItem.value = String.valueOf(info.getMsg_GenESchoolCount());
        groupList.add(groupItem);

        //学习任务
        groupItem = new ItemDataEntity();
        groupItem.type = RES_TYPE_HOMEWORK;
        groupItem.key = getString(R.string.learning_tasks);
        groupItem.value = String.valueOf(info.getSt_Sum());
        groupList.add(groupItem);

        getCurrAdapterViewHelper().setData(groupList);
    }

    private void search() {
        if (schoolInfo == null || info == null){
            return;
        }

        UIUtils.hideSoftKeyboard(getActivity());

        Map<String, Object> params = new HashMap();
        params.put("SchoolId", schoolInfo.getSchoolId());//学校Id，必填。
        // 3-教师统计,4-班级统计
        params.put("Type", CampusPatrolMainFragment.CAMPUS_PATROL_SEARCH_TYPE_CLASS);
        //班级Id,非必填。
        //此参数在进入班级统计页面再筛选时使用，不与ClassName同时使用
        params.put("ClassId",info.getClassId());

        if (!TextUtils.isEmpty(startDate)) {//时间格式：2016-12-11
            params.put("StrStartTime", startDate);//统计开始时间,非必填。
        }
        if (!TextUtils.isEmpty(endDate)) {//时间格式：2016-12-11
            params.put("StrEndTime", endDate);//统计结束时间,非必填。
        }
        //分页信息,必填。
        params.put("Pager",getPageHelper().getFetchingPagerArgs());
        DefaultPullToRefreshDataListener listener =
                new DefaultPullToRefreshDataListener<ClassDataStaticsInfoListResult>(
                        ClassDataStaticsInfoListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if(getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        ClassDataStaticsInfoListResult result = getResult();
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

    private void updateTitleView(String countStr){
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            textView.setText(info.getClassName()+getString(R.string
                        .media_num,countStr));
        }
    }

    private void updateViews(ClassDataStaticsInfoListResult result) {
        List<ClassDataStaticsInfo> list = result.getModel().getData();
        if (list != null && list.size() > 0){
            //按照个人搜索
           info = list.get(0);
            if (info != null) {
                updateTitleView(String.valueOf(info.getSum()));
                loadGroupData();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contacts_header_right_btn){
            ActivityUtils.enterCampusPatrolPickerActivity(getActivity());
        }else {
            super.onClick(v);
        }
    }

    public static void setHasDataChanged(boolean hasDataChanged) {
        CampusPatrolClassResourceListFragment.hasDataChanged = hasDataChanged;
    }

    public static boolean hasDataChanged() {
        return hasDataChanged;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null){
            if (resultCode == CampusPatrolPickerFragment.RESULT_CODE) {
                if (requestCode == CampusPatrolPickerFragment.REQUEST_CODE) {
                    //筛选结果
                    this.startDate = CampusPatrolUtils.getStartDate(data);
                    this.endDate = CampusPatrolUtils.getEndDate(data);
                    refreshData();
                }
            }
        }else {
            if (requestCode == CampusPatrolPickerFragment.EDIT_NOTE_DETAILS_REQUEST_CODE){
                if (OnlineMediaPaperActivity.hasResourceSended()){
                    OnlineMediaPaperActivity.setHasResourceSended(false);
                    //设置页面刷新标志位
                    setHasDataChanged(true);
                    //刷新当前页面
                    //校园动态统计后再刷新
//                    refreshData();
                }
            }
        }
    }

    private void refreshData() {
        getPageHelper().clear();
        search();
    }

    private final class ItemDataEntity{
        public int type;
        public String key;
        public String value;
    }
}
