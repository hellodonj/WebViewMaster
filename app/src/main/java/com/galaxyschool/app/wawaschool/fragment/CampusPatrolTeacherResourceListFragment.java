package com.galaxyschool.app.wawaschool.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.duowan.mobile.netroid.NetroidError;
import com.galaxyschool.app.wawaschool.CampusPatrolSchoolBasedCourseListActivity;
import com.galaxyschool.app.wawaschool.ClassResourceListActivity;
import com.galaxyschool.app.wawaschool.MediaMainActivity;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.Note.OnlineMediaPaperActivity;
import com.galaxyschool.app.wawaschool.PersonalPostBarListActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.SchoolMessageListActivity;
import com.galaxyschool.app.wawaschool.ScreeningHomeworkResultActivity;
import com.galaxyschool.app.wawaschool.TaskOrderlActivity;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.CampusPatrolUtils;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.ExpandDataAdapter;
import com.galaxyschool.app.wawaschool.fragment.library.ExpandListViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.TeacherDataStaticsInfo;
import com.galaxyschool.app.wawaschool.pojo.TeacherDataStaticsInfoListResult;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.lqwawa.client.pojo.MediaType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 校园巡查---老师个人资源页面
 */
public class CampusPatrolTeacherResourceListFragment extends ContactsExpandListFragment
        implements View.OnClickListener {

    public static final String TAG = CampusPatrolTeacherResourceListFragment.class.getSimpleName();

    private String startDate,endDate;
    private TeacherDataStaticsInfo info;
    private SchoolInfo schoolInfo;
    private static final int RES_TYPE_LQ_COURSE = 0;
    private static final int RES_TYPE_IMAGE = 1;
    private static final int RES_TYPE_AUDIO = 2;
    private static final int RES_TYPE_VIDEO = 3;
    private static final int RES_TYPE_PPT = 4;
    private static final int RES_TYPE_PDF = 5;
    private static final int RES_TYPE_CLOUD_POST_BAR = 6;

    private static final int RES_TYPE_SHOW = 7;
    private static final int RES_TYPE_NOTICE = 8;
    private static final int RES_TYPE_GEN_E_SCHOOL = 9;
    private static final int RES_TYPE_CAMPUS_DYNAMICS = 10;

    private static final int RES_TYPE_LOOK_COURSE = 11;
    private static final int RES_TYPE_RETELL_COURSE = 12;
    private static final int RES_TYPE_DISCUSSION_TOPIC = 13;
    private static final int RES_TYPE_HOMEWORK = 14;

    private static final int RES_TYPE_SCHOOL_BASED_COURSE = 15;
    private static final int RES_TYPE_INTRODUCTION_WAWA_COURSE = 16;//导读
    private static final int RES_TYPE_TASK_ORDER = 17;//任务单
    private static final int RES_TYPE_DOC = 18;//DOC
    private List<ItemDataEntity> groupList = new ArrayList<>();
    private List<List<ItemDataEntity>> childList = new ArrayList<>();
    private boolean isTempData;
    private static boolean hasDataChanged;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.campus_patrol_teacher_resource_fragment_list, null);
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

        final ExpandableListView listView = (ExpandableListView) findViewById(R.id
                .expandable_list_view);
        if (listView != null) {
            listView.setGroupIndicator(null);
            listView.setDividerHeight(0);
            int padding = (int) (10 * MyApplication.getDensity());
            listView.setPadding(0,padding,0,padding);
            final ExpandDataAdapter dataAdapter = new ExpandDataAdapter(getActivity(), null,
                    R.layout.campus_patrol_expandable_list_group_item,
                    R.layout.campus_patrol_resource_expandable_list_child_item) {

                @Override
                public Object getChild(int groupPosition, int childPosition) {
                    return childList.get(groupPosition).get(childPosition);
                }


                @Override
                public int getChildrenCount(int groupPosition) {
                    return childList.get(groupPosition).size();
                }

                @Override
                public View getChildView(int groupPosition, int childPosition,
                                         boolean isLastChild, View convertView, ViewGroup parent) {
                    View view = super.getChildView(groupPosition, childPosition,
                            isLastChild, convertView, parent);

                    final ItemDataEntity data = (ItemDataEntity) getChild(groupPosition, childPosition);
                    MyViewHolder holder = (MyViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new MyViewHolder();
                        view.setTag(holder);
                    }
                    holder.groupPosition = groupPosition;
                    holder.childPosition = childPosition;
                    holder.data = data;

                    ImageView imageView = (ImageView) view.findViewById(R.id.image);
                    if (imageView != null) {
                        imageView.setVisibility(View.GONE);
                    }

                    TextView textView = (TextView) view.findViewById(R.id.key);
                    if (textView != null) {
                        textView.setText(data.key);
                    }

                    textView = (TextView) view.findViewById(R.id.value);
                    if (textView != null) {
                        textView.setText(data.value);
                    }

                    //底线
                    View bottom_line = view.findViewById(R.id.bottom_line);
                    if (bottom_line != null){
                        if (childPosition == getChildrenCount(groupPosition) - 1){
                            bottom_line.setVisibility(View.INVISIBLE);
                        }else {
                            bottom_line.setVisibility(View.VISIBLE);
                        }
                    }

                    return view;
                }

                @Override
                public Object getGroup(int groupPosition) {
                    return groupList.get(groupPosition);
                }

                @Override
                public View getGroupView(int groupPosition, boolean isExpanded,
                                         View convertView, ViewGroup parent) {
                    View view = super.getGroupView(groupPosition, isExpanded, convertView, parent);
                    ItemDataEntity data = (ItemDataEntity) getGroup(groupPosition);
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
                        imageView.setImageResource(isExpanded ?
                                R.drawable.icon_white_up_arrow : R.drawable.icon_gray_down_arrow);
                    }

                    //头部分割线
                    View topLine = view.findViewById(R.id.top_line);
                    if (topLine != null){
                        if (groupPosition == 0) {
                            topLine.setVisibility(View.VISIBLE);
                        }else {
                            topLine.setVisibility(View.GONE);
                        }
                    }

                    //点击效果
                    if (isExpanded){
                        view.setBackgroundColor(getResources().
                                getColor(R.color.color_campus_patrol_resource_title));
                        keyView.setTextColor(getResources().getColor(R.color.white));
                        valueView.setTextColor(getResources().getColor(R.color.white));
                    }else {
                        view.setBackgroundColor(getResources().getColor(R.color.white));
                        keyView.setTextColor(getResources().getColor(R.color.black));
                        valueView.setTextColor(getResources().getColor(R.color.black));
                    }

                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                        view.setTag(holder);
                    }
                    holder.data = data;

                    return view;
                }

            };

//            //点击事件
//            listView.setOnGroupExpandListener(
//                    new ExpandableListView.OnGroupExpandListener() {
//                        @Override
//                        public void onGroupExpand(int groupPosition) {
//
//                            for (int i = 0; i < dataAdapter.getGroupCount(); i++) {
//                                if (i != groupPosition && listView.isGroupExpanded(i)) {
//                                    listView.collapseGroup(i);
//                                }
//                            }
//                        }
//                    });

            ExpandListViewHelper listViewHelper = new ExpandListViewHelper(getActivity(),
                    listView, dataAdapter) {
                @Override
                public void loadData() {
                    search();
                }

                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {
                    ViewHolder holder = (ViewHolder) v.getTag();
                    if (holder == null) {
                        return false;
                    }
                    ItemDataEntity data = (ItemDataEntity) holder.data;
                    controlEvent(data);

                    return true;
                }

                @Override
                public boolean onGroupClick(ExpandableListView parent, View v,
                                            int groupPosition, long id) {
                    return false;
                }
            };
            listViewHelper.setData(null);
            setCurrListViewHelper(listView, listViewHelper);
        }

    }

    private void enterSchoolBasedCourseListActivity(ItemDataEntity data) {

        if (data == null){
            return;
        }
        //PPT-1，PDF-2，图片-3，视频-4，音频-5， 蛙蛙微课-6
        int taskTypes = 6;
        Intent intent = new Intent(getActivity(), CampusPatrolSchoolBasedCourseListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(CampusPatrolMainFragment.IS_CAMPUS_PATROL_TAG,true);
        bundle.putInt(CampusPatrolMainFragment.CAMPUS_PATROL_TASK_TYPE,taskTypes);
        bundle.putString(CampusPatrolMainFragment.CAMPUS_PATROL_RESOURCE_NAME,data.key);
        bundle.putString(CampusPatrolMainFragment.CAMPUS_PATROL_RESOURCE_COUNT_STR,data.value);
        bundle.putSerializable(TeacherDataStaticsInfo.class.getSimpleName(),info);
        bundle.putSerializable(SchoolInfo.class.getSimpleName(),schoolInfo);
        bundle.putString(CampusPatrolMainFragment.CAMPUS_PATROL_SCREENING_START_DATE,startDate);
        bundle.putString(CampusPatrolMainFragment.CAMPUS_PATROL_SCREENING_END_DATE,endDate);
        intent.putExtras(bundle);
        startActivity(intent);

    }

    private void enterMediaMainActivity(ItemDataEntity data) {

        if (data == null ){
            return;
        }
        int mediaType = -1;
        switch (data.type){
            case RES_TYPE_LQ_COURSE :
                mediaType = MediaType.ONE_PAGE;
                break;
            case RES_TYPE_IMAGE :
                mediaType = MediaType.PICTURE;
                break;
            case RES_TYPE_AUDIO :
                mediaType = MediaType.AUDIO;
                break;
            case RES_TYPE_VIDEO :
                mediaType = MediaType.VIDEO;
                break;
            case RES_TYPE_PPT :
                mediaType = MediaType.PPT;
                break;
            case RES_TYPE_PDF :
                mediaType = MediaType.PDF;
                break;
            case RES_TYPE_DOC :
                mediaType = MediaType.DOC;
                break;
        }
        Intent intent = new Intent(getActivity(),MediaMainActivity.class);
        Bundle args = new Bundle();
        args.putInt(MediaListFragment.EXTRA_MEDIA_TYPE, mediaType);
        args.putString(MediaListFragment.EXTRA_MEDIA_NAME, data.key);
        args.putString(CampusPatrolMainFragment.CAMPUS_PATROL_RESOURCE_COUNT_STR,data.value);
        args.putSerializable(TeacherDataStaticsInfo.class.getSimpleName(),info);
        args.putBoolean(CampusPatrolMainFragment.IS_CAMPUS_PATROL_TAG,true);
        args.putBoolean(MediaListFragment.EXTRA_IS_FINISH,true);
        args.putString(CampusPatrolMainFragment.CAMPUS_PATROL_SCREENING_START_DATE,startDate);
        args.putString(CampusPatrolMainFragment.CAMPUS_PATROL_SCREENING_END_DATE,endDate);
        intent.putExtras(args);
        startActivity(intent);
    }

    private void controlEvent(ItemDataEntity data) {
       if (data == null){
           return;
       }

        //子条目跳转逻辑
        switch (data.type){

            //资源
            case RES_TYPE_LQ_COURSE :
            case RES_TYPE_IMAGE :
            case RES_TYPE_AUDIO :
            case RES_TYPE_VIDEO :
            case RES_TYPE_PPT :
            case RES_TYPE_PDF :
                enterMediaMainActivity(data);
                break;

            case RES_TYPE_TASK_ORDER://任务单
                enterTaskOrderActivity(data);
                break;

            case RES_TYPE_CLOUD_POST_BAR :
                enterPersonalPostBarListActivity(data);
                break;

            //消息
            case RES_TYPE_SHOW :
            case RES_TYPE_NOTICE :
            case RES_TYPE_GEN_E_SCHOOL :
                enterClassResourceListActivity(data);
                break;

            //校园动态
            case RES_TYPE_CAMPUS_DYNAMICS :
                enterSchoolMessageListActivity(data);
                break;

            //学习任务
            case RES_TYPE_LOOK_COURSE :
            case RES_TYPE_RETELL_COURSE :
            case RES_TYPE_DISCUSSION_TOPIC :
            case RES_TYPE_HOMEWORK :
            case RES_TYPE_INTRODUCTION_WAWA_COURSE ://导读
                enterScreeningHomeworkResultActivity(data);
                break;

            //校本课程
            case RES_TYPE_SCHOOL_BASED_COURSE :
                enterSchoolBasedCourseListActivity(data);
                break;

            default:
                break;
        }
    }

    private void enterTaskOrderActivity(ItemDataEntity data) {
        if (data == null){
            return;
        }
        Intent intent = new Intent(getActivity(), TaskOrderlActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(CampusPatrolMainFragment.IS_CAMPUS_PATROL_TAG,true);
        bundle.putString(CampusPatrolMainFragment.CAMPUS_PATROL_RESOURCE_COUNT_STR,data.value);
        bundle.putSerializable(TeacherDataStaticsInfo.class.getSimpleName(),info);
        bundle.putString(CampusPatrolMainFragment.CAMPUS_PATROL_SCREENING_START_DATE,startDate);
        bundle.putString(CampusPatrolMainFragment.CAMPUS_PATROL_SCREENING_END_DATE,endDate);
        bundle.putBoolean(MediaListFragment.EXTRA_IS_FINISH,true);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void enterScreeningHomeworkResultActivity(ItemDataEntity data) {
        if (data == null){
            return;
        }
        //0-看微课,1-看课件,2看作业,3-交作业,4-讨论话题,5-复述微课,6-导读。
        String taskTypes = "";
        switch (data.type){
            //看课件要传递0
            case RES_TYPE_LOOK_COURSE :
                taskTypes = "0";
                break;
            case RES_TYPE_RETELL_COURSE :
                taskTypes = "5";
                break;
            case RES_TYPE_DISCUSSION_TOPIC :
                taskTypes = "4";
                break;
            case RES_TYPE_HOMEWORK :
                //交作业
                //其他：包含看作业和交作业。
                taskTypes = "2,3";
                break;

            case RES_TYPE_INTRODUCTION_WAWA_COURSE :
                //导读
                taskTypes = "6";
                break;
        }
        //支持查看全部任务类型，以逗号分隔。

        Intent intent = new Intent(getActivity(), ScreeningHomeworkResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(CampusPatrolMainFragment.IS_CAMPUS_PATROL_TAG,true);
        bundle.putString(CampusPatrolMainFragment.CAMPUS_PATROL_TASK_TYPE,taskTypes);
        bundle.putInt(CampusPatrolMainFragment.CAMPUS_PATROL_SEARCH_TYPE,CampusPatrolMainFragment
                .CAMPUS_PATROL_SEARCH_TYPE_TEACHER);
        bundle.putString(CampusPatrolMainFragment.CAMPUS_PATROL_RESOURCE_NAME,data.key);
        bundle.putString(CampusPatrolMainFragment.CAMPUS_PATROL_RESOURCE_COUNT_STR,data.value);
        bundle.putSerializable(TeacherDataStaticsInfo.class.getSimpleName(),info);
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
            case RES_TYPE_SHOW :
                isTempData=true;
                channelType = ClassResourceListActivity.CHANNEL_TYPE_SHOW;
                break;
            case RES_TYPE_NOTICE :
                isTempData=false;
                channelType = ClassResourceListActivity.CHANNEL_TYPE_NOTICE;
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
        bundle.putInt(CampusPatrolMainFragment.CAMPUS_PATROL_RESOURCE_TYPE,
                CampusPatrolMainFragment.CAMPUS_PATROL_RESOURCE_TYPE_USER);
        bundle.putString(CampusPatrolMainFragment.CAMPUS_PATROL_RESOURCE_COUNT_STR,data.value);
        bundle.putSerializable(TeacherDataStaticsInfo.class.getSimpleName(),info);
        bundle.putString(CampusPatrolMainFragment.CAMPUS_PATROL_SCREENING_START_DATE,startDate);
        bundle.putString(CampusPatrolMainFragment.CAMPUS_PATROL_SCREENING_END_DATE,endDate);
        intent.putExtras(bundle);
        //进入通知、秀秀页面
        startActivityForResult(intent,CampusPatrolPickerFragment.EDIT_NOTE_DETAILS_REQUEST_CODE);
    }

    private void enterSchoolMessageListActivity(ItemDataEntity data) {

        if (data == null){
            return;
        }
        Intent intent = new Intent(getActivity(), SchoolMessageListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(CampusPatrolMainFragment.IS_CAMPUS_PATROL_TAG,true);
        bundle.putString(CampusPatrolMainFragment.CAMPUS_PATROL_RESOURCE_COUNT_STR,data.value);
        bundle.putSerializable(TeacherDataStaticsInfo.class.getSimpleName(),info);
        bundle.putSerializable(SchoolInfo.class.getSimpleName(),schoolInfo);
        bundle.putString(CampusPatrolMainFragment.CAMPUS_PATROL_SCREENING_START_DATE,startDate);
        bundle.putString(CampusPatrolMainFragment.CAMPUS_PATROL_SCREENING_END_DATE,endDate);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void enterPersonalPostBarListActivity(ItemDataEntity data) {

        if (data == null){
            return;
        }
        Intent intent = new Intent(getActivity(), PersonalPostBarListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(CampusPatrolMainFragment.IS_CAMPUS_PATROL_TAG,true);
        bundle.putString(CampusPatrolMainFragment.CAMPUS_PATROL_RESOURCE_COUNT_STR,data.value);
        bundle.putSerializable(TeacherDataStaticsInfo.class.getSimpleName(),info);
        bundle.putString(CampusPatrolMainFragment.CAMPUS_PATROL_SCREENING_START_DATE,startDate);
        bundle.putString(CampusPatrolMainFragment.CAMPUS_PATROL_SCREENING_END_DATE,endDate);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void loadGroupData() {
        if (info == null){
            return;
        }
        groupList.clear();
        childList.clear();
        ItemDataEntity groupItem,childItem;
        List<ItemDataEntity> list;

        list = new ArrayList<>();
        //资源
        groupItem = new ItemDataEntity();
        groupItem.key = getString(R.string.txt_resource);
        groupItem.value = String.valueOf(info.getRes_Sum());

        ////任务单
        childItem = new ItemDataEntity();
        childItem.type = RES_TYPE_TASK_ORDER;
        childItem.key = getString(R.string.task_order);
        childItem.value = String.valueOf(info.getRes_WorkOrderCount());
        list.add(childItem);

        ////LQ课件
        childItem = new ItemDataEntity();
        childItem.type = RES_TYPE_LQ_COURSE;
        childItem.key = getString(R.string.microcourse);
        childItem.value = String.valueOf(info.getRes_LQCourseCount());
        list.add(childItem);

        ////图片
        childItem = new ItemDataEntity();
        childItem.type = RES_TYPE_IMAGE;
        childItem.key = getString(R.string.attach_picture);
        childItem.value = String.valueOf(info.getRes_ImgCount());
        list.add(childItem);

        ////音频
        childItem = new ItemDataEntity();
        childItem.type = RES_TYPE_AUDIO;
        childItem.key = getString(R.string.audios);
        childItem.value = String.valueOf(info.getRes_VoiceCount());
        list.add(childItem);

        ////视频
        childItem = new ItemDataEntity();
        childItem.type = RES_TYPE_VIDEO;
        childItem.key = getString(R.string.videos);
        childItem.value = String.valueOf(info.getRes_VideoCount());
        list.add(childItem);

        ////PPT
        childItem = new ItemDataEntity();
        childItem.type = RES_TYPE_PPT;
        childItem.key = getString(R.string.txt_ppt);
        childItem.value = String.valueOf(info.getRes_PPTCount());
        list.add(childItem);

        ////PDF
        childItem = new ItemDataEntity();
        childItem.type = RES_TYPE_PDF;
        childItem.key = getString(R.string.txt_pdf);
        childItem.value = String.valueOf(info.getRes_PDFCount());
        list.add(childItem);

        ////云贴吧
        childItem = new ItemDataEntity();
        childItem.type = RES_TYPE_CLOUD_POST_BAR;
        childItem.key = getString(R.string.cloud_post_bar);
        childItem.value = String.valueOf(info.getRes_FzktCount());
        list.add(childItem);

        //添加到列表
        addData(groupItem,list);

        /**----------------------------group分隔线--------------------------------------------*/
        list = new ArrayList<>();
        //消息
        groupItem = new ItemDataEntity();
        groupItem.key = getString(R.string.message);
        groupItem.value = String.valueOf(info.getMsg_Sum());

        ////秀秀
        childItem = new ItemDataEntity();
        childItem.type = RES_TYPE_SHOW;
        childItem.key = getString(R.string.shows);
        childItem.value = String.valueOf(info.getMsg_MienCount());
        list.add(childItem);

        ////通知
        childItem = new ItemDataEntity();
        childItem.type = RES_TYPE_NOTICE;
        childItem.key = getString(R.string.notices);
        childItem.value = String.valueOf(info.getMsg_NoticeCount());
        list.add(childItem);

        ////创e学堂
        childItem = new ItemDataEntity();
        childItem.type = RES_TYPE_GEN_E_SCHOOL;
        childItem.key = getString(R.string.lectures);
        childItem.value = String.valueOf(info.getMsg_GenESchoolCount());
        list.add(childItem);

        ////校园动态
        childItem = new ItemDataEntity();
        childItem.type = RES_TYPE_CAMPUS_DYNAMICS;
        childItem.key = getString(R.string.school_movement);
        childItem.value = String.valueOf(info.getMsg_SchoolDynCount());
        list.add(childItem);

        //添加到列表
        addData(groupItem,list);

        /**----------------------------group分隔线--------------------------------------------*/
        list = new ArrayList<>();
        //学习任务
        groupItem = new ItemDataEntity();
        groupItem.key = getString(R.string.learning_tasks);
        groupItem.value = String.valueOf(info.getSt_Sum());

        ////看课件
        childItem = new ItemDataEntity();
        childItem.type = RES_TYPE_LOOK_COURSE;
        childItem.key = getString(R.string.look_through_courseware);
        childItem.value = String.valueOf(info.getSt_LookCourseCount());
        list.add(childItem);

        ////复述课件
        childItem = new ItemDataEntity();
        childItem.type = RES_TYPE_RETELL_COURSE;
        childItem.key = getString(R.string.retell_course);
        childItem.value = String.valueOf(info.getSt_RepeatCourseCount());
        list.add(childItem);

        ////讨论话题
        childItem = new ItemDataEntity();
        childItem.type = RES_TYPE_DISCUSSION_TOPIC;
        childItem.key = getString(R.string.discuss_topic);
        childItem.value = String.valueOf(info.getSt_DiscussTopicCount());
        list.add(childItem);

        ////作业
        ///改成其他
        childItem = new ItemDataEntity();
        childItem.type = RES_TYPE_HOMEWORK;
        childItem.key = getString(R.string.other);
        childItem.value = String.valueOf(info.getSt_CommitFzktCount() + info.getSt_LookFzktCount());
        list.add(childItem);

        ////导读
        childItem = new ItemDataEntity();
        childItem.type = RES_TYPE_INTRODUCTION_WAWA_COURSE;
        childItem.key = getString(R.string.introduction);
        childItem.value = String.valueOf(info.getSt_GuideReadCount());
        list.add(childItem);

        //添加到列表
        addData(groupItem,list);

        /**----------------------------group分隔线--------------------------------------------*/
        list = new ArrayList<>();
        //校本课程
        //改成校本资源库
        groupItem = new ItemDataEntity();
        groupItem.key = getString(R.string.public_course);
        groupItem.value = String.valueOf(info.getSchoolBasedCount());

        ////校本课程
        ////改成校本资源库
        childItem = new ItemDataEntity();
        childItem.type = RES_TYPE_SCHOOL_BASED_COURSE;
        childItem.key = getString(R.string.public_course);
        childItem.value = String.valueOf(info.getSchoolBasedCount());
        list.add(childItem);

        //添加条目
        addData(groupItem,list);
        getCurrListViewHelper().setData(childList);
    }


    private void addData(ItemDataEntity groupItem, List<ItemDataEntity> list) {
        groupList.add(groupItem);
        childList.add(list);
    }

    private void search() {
        if (schoolInfo == null || info == null){
            return;
        }

        UIUtils.hideSoftKeyboard(getActivity());

        Map<String, Object> params = new HashMap();
        params.put("SchoolId", schoolInfo.getSchoolId());//学校Id，必填。
        // 3-教师统计,4-班级统计
        params.put("Type", CampusPatrolMainFragment.CAMPUS_PATROL_SEARCH_TYPE_TEACHER);
        //教师真实姓名或者用户名,非必填。
        //里面精确搜索传TeacherNickName
        params.put("TeacherNickName",info.getTeacherNickName());

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

    private void updateTitleView(String countStr){
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            textView.setText(info.getTeacherRealName()+getString(R.string
                        .media_num,countStr));
        }
    }

    private void updateViews(TeacherDataStaticsInfoListResult result) {
        List<TeacherDataStaticsInfo> list = result.getModel().getData();
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

    private class MyViewHolder extends ViewHolder<ItemDataEntity> {
        int groupPosition;
        int childPosition;
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
                    refreshData();
                }
            }
        }
    }

    public static void setHasDataChanged(boolean hasDataChanged) {
        CampusPatrolTeacherResourceListFragment.hasDataChanged = hasDataChanged;
    }

    public static boolean hasDataChanged() {
        return hasDataChanged;
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
