package com.galaxyschool.app.wawaschool.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.Note.OnlineMediaPaperActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.ShowTopicDiscussionPeopleActivity;
import com.galaxyschool.app.wawaschool.StudentTasksActivity;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.CallbackListener;
import com.galaxyschool.app.wawaschool.common.ColorUtil;
import com.galaxyschool.app.wawaschool.common.DateUtils;
import com.galaxyschool.app.wawaschool.common.StudyTaskOpenHelper;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.common.WawaCourseUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ExpandDataAdapter;
import com.galaxyschool.app.wawaschool.fragment.library.ExpandListViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.DiscussPersonList;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskCommentDiscussPersonResult;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskFinishInfo;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskFinishInfoResult;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskInfo;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;
import com.galaxyschool.app.wawaschool.pojo.StudytaskComment;
import com.galaxyschool.app.wawaschool.pojo.TaskDetail;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.SplitCourseInfo;
import com.galaxyschool.app.wawaschool.views.MyGridView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskFinishInfoFragment extends ContactsExpandListFragment implements
        View.OnClickListener, View.OnLayoutChangeListener {

    public static final String TAG = TaskFinishInfoFragment.class.getSimpleName();

    private String className = "";
    private int parentId = 0;
    private String commentToId = "";
    private String commentToName = "";
    private LinearLayout taskFinishLayout;
    private LinearLayout taskUnFinishLayout;
    private boolean finishViewTag = true;
    private boolean unFinishViewTag = true;
    private GridView finishGridView;
    private GridView unFinishGridView;
    private ImageView finishLogoView;
    private ImageView unFinishLogoView;
    private ExpandableListView expandListView;
    private EditText commentEditText;
    private TextView sendCommentView;
    private CourseData courseData;
    private CourseData studentCourseData;
    private LinearLayout notTalkLayout, talkLayout;
    private TextView headTitleView;
    private TextView notTalkClassNameView;
    private TextView talkTitleView;
    private TextView talkContnetView;
    private TextView discussContent;
    private LinearLayout introductionLayout;
    private StudyTaskInfo task;
    private TextView discussCounttextView;
    //Activity最外层的Layout视图
    private View activityRootView;
    //屏幕高度
    private int screenHeight = 0;
    //软件盘弹起后所占高度阀值
    private int keyHeight = 0;
    private int unReadBtnClickState=UNREAD_STATE_0;//0数据未加载完成点击无效果，
    // 1数据已加载但是没全部完成点击有效果，2全部完成点击提示全部已读
    private static final int UNREAD_STATE_0=0;
    private static final int UNREAD_STATE_1=1;
    private static final int UNREAD_STATE_2=2;

    private ImageView taskImageView,mediaCover;
    private TextView titleTextView,startTime , endTime,headRightView;
    private FrameLayout frameLayout;
    private List<DiscussPersonList> discussPersonList = new ArrayList<>();
    private LinearLayout showTalksLayout;
    private LinearLayout sendTalksLayout;
    private static boolean hasCommented;
    private static final int REQUEST_CODE_STUDENT_TASK = 11;
    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft,
                               int oldTop, int oldRight, int oldBottom) {
        //现在认为只要控件将Activity向上推的高度超过了1/3屏幕高，就认为软键盘弹起
        if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > keyHeight)) {
        } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > keyHeight)) {
            replaySuccessOrGiveup();//收起键盘时，评论回复的参数要重置回去，commentEditText的text
            // 和hint也要重置回去
        }
    }

    public interface Constants {
        public static final String TASK = "TASK";
        public static final String CLASS_NAME = "CLASS_NAME";
        public static final int MAX_BOOKS_PER_ROW = 4;
        public static final String UN_FINISH_GRIDVIEW_TAG = "UN_FINISH_GRIDVIEW_TAG";
        public static final int MAX_PAGE_SIZE = Integer.MAX_VALUE;
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        activityRootView = inflater.inflate(R.layout.fragment_task_finish, null);
        return activityRootView;
    }

    private void loadIntent() {
        Intent intent = getActivity().getIntent();
        task = (StudyTaskInfo) intent.getSerializableExtra(Constants.TASK);
        className = intent.getStringExtra(Constants.CLASS_NAME);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadIntent();
        initViews();
        refreshData();
    }

    private void refreshData(){
        loadTaskFinishInfo();
        loadTaskComments();
    }

    private void loadTaskFinishInfo() {
        if (task == null) return;
        Map<String, Object> params = new HashMap();
        params.put("TaskId", task.getTaskId());
        RequestHelper.sendPostRequest(getActivity(),
            ServerUrl.GET_TASK_FINISH_INFO_URL, params,
            new RequestHelper.RequestDataResultListener<StudyTaskFinishInfoResult>(getActivity(),
                    StudyTaskFinishInfoResult.class) {
                @Override
                public void onFinish() {
                    super.onFinish();
                }

                @Override
                public void onSuccess(String jsonString) {
                    if (getActivity() == null) {
                        return;
                    }
                    super.onSuccess(jsonString);
                    StudyTaskFinishInfoResult result = getResult();
                    if (result == null || !result.isSuccess()
                            || result.getModel() == null) {
                        if (getCurrAdapterViewHelper().hasData()) {
                            getCurrAdapterViewHelper().getData().clear();
                            getCurrAdapterViewHelper().update();
                        }
                        if (getAdapterViewHelper(Constants.UN_FINISH_GRIDVIEW_TAG).hasData()) {
                            getAdapterViewHelper(Constants.UN_FINISH_GRIDVIEW_TAG).getData().clear();
                            getAdapterViewHelper(Constants.UN_FINISH_GRIDVIEW_TAG).update();
                        }
                        unReadBtnClickState=UNREAD_STATE_1;
                        return;
                    }
                    StudyTaskFinishInfo data = result.getModel().getData();
                    if (data != null) {
                        //资源缩略图
                        String thumbnail = data.getThumbnailUrl();
                        if (!TextUtils.isEmpty(thumbnail)){
                            MyApplication.getThumbnailManager(getActivity()).displayThumbnail
                                    (AppSettings.getFileUrl(thumbnail), taskImageView);
                        }
                        if (data.getCompletedList() != null) {
                            getCurrAdapterViewHelper().setData(data.getCompletedList());
                            TextView     textView = (TextView) findViewById(R.id.task_finish_count);
                            textView.setText(getString(R.string.finish_count,
                                    data.getCompletedList().size()+""));
                        }
                        if (data.getUnCompletedList() != null) {
                            getAdapterViewHelper(Constants.UN_FINISH_GRIDVIEW_TAG).setData(
                                    data.getUnCompletedList());
                            TextView  textView = (TextView) findViewById(R.id.task_unfinish_count);
                            textView.setText(getString(R.string.unfinish_count,
                                    data.getUnCompletedList().size()+""));
                        }
                        //显示学生应完成的任务
                        String disContent=result.getModel().getData().getTaskContent();
                        if (data.getTaskType()==StudyTaskType.INTRODUCTION_WAWA_COURSE) {
                            if (disContent != null && !disContent.equals("")) {
                                introductionLayout.setVisibility(View.VISIBLE);
                                discussContent.setText(disContent);
                            }
                        }
                    }
                    if(data.getUnCompletedList() == null||data.getUnCompletedList().size()==0){
                        unReadBtnClickState=UNREAD_STATE_2;
                    }else{
                        unReadBtnClickState=UNREAD_STATE_1;
                    }
                }
            });
}

    private void loadTaskComments() {
        if (task == null) return;
        Map<String, Object> params = new HashMap();
        params.put("TaskId", task.getTaskId());
        params.put("Pager", getPageHelper().getFetchingPagerArgs());
        RequestHelper.sendPostRequest(getActivity(),
            ServerUrl.GET_TASK_COMMENT_LIST_URL, params,
            new RequestHelper.RequestDataResultListener<StudyTaskCommentDiscussPersonResult>
                    (getActivity(), StudyTaskCommentDiscussPersonResult.class) {
                @Override
                public void onSuccess(String jsonString) {
                    if (getActivity() == null) {
                        return;
                    }
                    super.onSuccess(jsonString);
                    StudyTaskCommentDiscussPersonResult result = getResult();
                    int discussCount = 0;
                    if (result == null || !result.isSuccess()
                            || result.getModel() == null) {
                        if (getCurrListViewHelper().hasData()) {
                            getCurrListViewHelper().getData().clear();
                            getCurrListViewHelper().update();
                        }
                        discussCounttextView.setText(getString(R.string.discussion, discussCount));
                        return;
                    }
                    List<StudytaskComment> data = result.getModel().getData().getCommentList();
                    if (data != null) {
                        discussCount += data.size();
                        for (StudytaskComment item : data) {
                            discussCount += item.getChildren().size();
                        }
                        //设置点赞标志位
                        List<StudytaskComment> oldData = getCurrListViewHelper().getData();
                        if(oldData != null && oldData.size() > 0){
                            for(StudytaskComment item : data){
                                for(StudytaskComment oldItem : oldData){
                                    if(item.getId() == oldItem.getId()){
                                        item.setHasPraised(oldItem.isHasPraised());
                                    }
                                }
                            }
                        }
                        getCurrListViewHelper().setData(data);
                        getCurrListViewHelper().update();
                        expandAllView();
                    }
                    discussCounttextView.setText(getString(R.string.discussion, discussCount));

                    //讨论话题
                    if (task.getTaskType() == StudyTaskType.TOPIC_DISCUSSION) {
                        //参与人数
                        List<DiscussPersonList> peopleResultList = result.getModel().getData()
                                .getDiscussPersonList();
                        if (peopleResultList != null && peopleResultList.size() > 0) {
                            headRightView.setText(getString(R.string.n_people_join,
                                    peopleResultList.size()));
                            discussPersonList = peopleResultList;
                        }
                    }
                }
            });
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    private void expandAllView() {
        int groupCount = expandListView.getCount();
        for (int i = 0; i < groupCount; i++) {
            expandListView.expandGroup(i);
        }
    }

    private TextWatcher watcher = new TextWatcher(){

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            if(s!=null&&s.length()>40){
                TipMsgHelper.ShowLMsg(getActivity(),"输入长度超过限制！");
                String str =s.toString().substring(0,40);
                commentEditText.setText(str);
                commentEditText.setSelection(str.length());
                return;
            }
        }
    };

    private void initViews() {
        if (task == null) return;
        initHeadView();
        initFinishGridview();
        initUnFinishGridview();
        initExpandListView();
        TextView textView = null;
        headTitleView = (TextView) findViewById(R.id.contacts_header_title);
        notTalkClassNameView = (TextView) findViewById(R.id.class_name);
        taskFinishLayout = (LinearLayout) findViewById(R.id.task_finish_layout);
        taskUnFinishLayout = (LinearLayout) findViewById(R.id.task_unfinish_layout);
        taskFinishLayout.setOnClickListener(this);
        taskUnFinishLayout.setOnClickListener(this);
        finishLogoView = (ImageView) findViewById(R.id.finish_logo_view);
        unFinishLogoView = (ImageView) findViewById(R.id.unfinish_logo_view);
        ImageView imageView = (ImageView) getView().findViewById(R.id.contacts_header_left_btn);
        imageView.setOnClickListener(this);
        commentEditText = (EditText) findViewById(R.id.edit_btn);
        commentEditText.addTextChangedListener(watcher);
        sendCommentView = (TextView) findViewById(R.id.send_btn);
        sendCommentView.setOnClickListener(this);
        notTalkLayout = (LinearLayout) findViewById(R.id.not_talk_type);
        talkLayout = (LinearLayout) findViewById(R.id.talk_type);
        talkTitleView = (TextView) findViewById(R.id.task_title);
        talkContnetView = (TextView) findViewById(R.id.task_content);
        TextView openCourseTextView = (TextView) findViewById(R.id.contacts_header_right_btn);
        openCourseTextView.setOnClickListener(this);
        discussCounttextView = (TextView) findViewById(R.id.discuss_count);
        ImageView unReadTipTextView = (ImageView) findViewById(R.id.contacts_header_right_ico);
        if (task.getTaskType() == StudyTaskType.TOPIC_DISCUSSION) {
            talkLayout.setVisibility(View.VISIBLE);
            notTalkLayout.setVisibility(View.GONE);
            headTitleView.setText(getString(R.string.discuss_content, className));
            talkTitleView.setText(task.getTaskTitle());
            talkContnetView.setText(task.getTaskContent());
            openCourseTextView.setVisibility(View.VISIBLE);
            openCourseTextView.setText(getString(R.string.n_people_join,0));
            unReadTipTextView.setVisibility(View.GONE);
        } else {
            talkLayout.setVisibility(View.GONE);
            notTalkLayout.setVisibility(View.VISIBLE);
            headTitleView.setText(task.getTaskTitle());
            notTalkClassNameView.setText(className);


            int taskType = task.getTaskType();
            if (taskType == StudyTaskType.WATCH_WAWA_COURSE
                    || taskType == StudyTaskType.WATCH_RESOURCE
                    || taskType == StudyTaskType.RETELL_WAWA_COURSE||taskType==StudyTaskType.INTRODUCTION_WAWA_COURSE ){
                //看微课要显示播放按钮
                mediaCover.setVisibility(View.VISIBLE);
            }else {
                mediaCover.setVisibility(View.GONE);
            }
            titleTextView.setText(className);
            startTime.setText(getString(R.string.time_assigned,getFormatDate(task.getStartTime())));
            endTime.setText(getString(R.string.time_completed,getFormatDate(task.getEndTime())));

            textView = (TextView) findViewById(R.id.task_finish_count);
            textView.setText(getString(R.string.finish_count, task.getFinishTaskCount()));
            textView = (TextView) findViewById(R.id.task_unfinish_count);
            textView.setText(getString(R.string.unfinish_count, task.getTaskNum() -
                    task.getFinishTaskCount()));
            textView = (TextView) findViewById(R.id.task_time);
            textView.setText(getString(R.string.date_to_date, DateUtils.getDateStr(
                    task.getStartTime(), DateUtils.DATE_PATTERN_yyyy_MM_dd_HH_MM_SS,
                    DateUtils.DATE_PATTERN_yyyy_MM_dd), DateUtils.getDateStr(task.getEndTime(),
                    DateUtils.DATE_PATTERN_yyyy_MM_dd_HH_MM_SS, DateUtils.DATE_PATTERN_yyyy_MM_dd)));
            openCourseTextView.setVisibility(View.VISIBLE);
            //显示学生应完成的任务
            introductionLayout= (LinearLayout) findViewById(R.id.show_introduction_layout);
            discussContent= (TextView) findViewById(R.id.introduction_content);
            openCourseTextView.setOnClickListener(this);
//            StudyTaskHelper studyTaskType = new StudyTaskHelper(getActivity());
//            openCourseTextView.setText(studyTaskType.getTypeName(task.getTaskType()));
            openCourseTextView.setText(getString(R.string.un_read_tip));
            unReadTipTextView.setVisibility(View.GONE);
            unReadTipTextView.setOnClickListener(this);
        }
        screenHeight = getActivity().getWindowManager().getDefaultDisplay().getHeight();
        //阀值设置为屏幕高度的1/3
        keyHeight = screenHeight / 3;
        getPageHelper().setPageSize(Constants.MAX_PAGE_SIZE);
        activityRootView.addOnLayoutChangeListener(this);
        showTalksLayout=(LinearLayout)findViewById(R.id.show_talks_layout);
        sendTalksLayout=(LinearLayout)findViewById(R.id.send_talks_layout);
        if(task!=null&& task.getTaskType()==StudyTaskType.INTRODUCTION_WAWA_COURSE){
            showTalksLayout.setVisibility(View.GONE);
            sendTalksLayout.setVisibility(View.GONE);
        }else{
            showTalksLayout.setVisibility(View.VISIBLE);
            sendTalksLayout.setVisibility(View.VISIBLE);
        }
    }

    private String getFormatDate(String date){

        String yearStr,monthStr,dayStr;
        if (TextUtils.isEmpty(date)){
            return "";
        }

        yearStr = DateUtils.getDateStr(date,0);
        monthStr = DateUtils.getDateStr(date,1);
        dayStr = DateUtils.getDateStr(date,2);
        return yearStr + "-" + monthStr + "-" + dayStr;
    }

    private void initHeadView() {
        headRightView = (TextView) findViewById(R.id.contacts_header_right_btn);
        frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        frameLayout.setOnClickListener(this);
        taskImageView = (ImageView) findViewById(R.id.iv_icon);
        mediaCover = (ImageView) findViewById(R.id.media_cover);
        startTime = (TextView) findViewById(R.id.tv_start_time);
        endTime = (TextView) findViewById(R.id.tv_end_time);
        titleTextView = (TextView) findViewById(R.id.tv_title);
    }

    protected void initExpandListView() {
        expandListView = ((ExpandableListView) findViewById(R.id.catlog_expand_listview));
        if (expandListView != null) {
            expandListView.setGroupIndicator(null);
            expandListView.setDivider(null);
            //expandListView.setChildDivider(getResources().getDrawable(R.drawable.content_line));
            ExpandDataAdapter dataAdapter = new ExpandDataAdapter(getActivity(), null,
                    R.layout.item_expendlistview_group_comment,
                    R.layout.item_expendlistview_child_comment) {

                @Override
                public Object getChild(int groupPosition, int childPosition) {
                    return ((StudytaskComment) getData().get(groupPosition))
                            .getChildren().get(childPosition);
                }


                @Override
                public int getChildrenCount(int groupPosition) {
                    if (hasData() && groupPosition < getGroupCount()) {
                        StudytaskComment comment = (StudytaskComment)
                                getData().get(groupPosition);
                        if (comment != null && comment.getChildren() != null) {
                            return comment.getChildren().size();
                        }
                    }
                    return 0;
                }

                @Override
                public View getChildView(int groupPosition, int childPosition,
                                         boolean isLastChild, View convertView, ViewGroup parent) {
                    View view = super.getChildView(groupPosition, childPosition,
                            isLastChild, convertView, parent);
                    final StudytaskComment data = (StudytaskComment) getChild(groupPosition,
                            childPosition);
                    MyViewHolder holder = (MyViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new MyViewHolder();
                    }
                    holder.groupPosition = groupPosition;
                    holder.childPosition = childPosition;
                    holder.data = data;
                    TextView textView = (TextView) view.findViewById(R.id.who_comment_who);
                    if (textView != null) {
                        textView.setText(getString(R.string.who_reply_who, data.getCommentName(),
                                data.getCommentToName()));
                        ColorUtil.spannableGreenColor(getActivity(), textView, 0,
                                data.getCommentName().length(), textView.length() -
                                        data.getCommentToName().length(), textView.length());
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                commentEditText.setHint(getString(R.string.reply_who,
                                        data.getCommentName()));
                                //打开软键盘
                                UIUtils.showSoftKeyboard1(getActivity());
                                commentEditText.requestFocus();
                                parentId = data.getParentId();
                                commentToId = data.getCommentId();
                                commentToName = data.getCommentName();
                            }
                        });
                    }
                    textView = (TextView) view.findViewById(R.id.comment_time);
                    if (textView != null) {
                        textView.setText(DateUtils.getDateStr(data.getCommentTime(),
                                DateUtils.DATE_PATTERN_yyyy_MM_dd_HH_MM_SS,
                                DateUtils.DATE_PATTERN_yyyy_MM_dd_HH_MM));
                    }
                    textView = (TextView) view.findViewById(R.id.comment_cotent);
                    if (textView != null) {
                        textView.setText(data.getComments());
                    }
                    view.setTag(holder);
                    return view;
                }

                @Override
                public View getGroupView(int groupPosition, boolean isExpanded,
                                         View convertView, ViewGroup parent) {
                    View view = super.getGroupView(groupPosition, isExpanded, convertView, parent);
                    final StudytaskComment data = (StudytaskComment) getGroup(groupPosition);
                    View devider = (View) view.findViewById(R.id.parent_children_devider);
                    if(data.getChildren().size()>0){
                        devider.setVisibility(View.VISIBLE);
                    }else{
                        devider.setVisibility(View.GONE);
                    }

                    //头部分隔线
                    View topLineView = view.findViewById(R.id.top_line);
                    if (topLineView != null){
                      if (groupPosition == 0){
                          topLineView.setVisibility(View.GONE);
                      }else {
                          topLineView.setVisibility(View.VISIBLE);
                      }
                    }
                    ImageView imageView = (ImageView) view.findViewById(R.id.head_img);
                    if (imageView != null) {
                        if (data.getCommentHeadPicUrl() != null) {
                            getThumbnailManager().displayUserIcon(AppSettings.getFileUrl(
                                    data.getCommentHeadPicUrl()), imageView);
                        }
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ActivityUtils.enterPersonalSpace(getActivity(),
                                        data.getCommentId());
                            }
                        });
                    }
                    TextView textView = (TextView) view.findViewById(R.id.comment_name);
                    if (textView != null) {
                        textView.setText(data.getCommentName());
                    }
                    textView = (TextView) view.findViewById(R.id.comment_time);
                    if (textView != null) {
                        textView.setText(DateUtils.getDateStr(data.getCommentTime(),
                                DateUtils.DATE_PATTERN_yyyy_MM_dd_HH_MM_SS,
                                DateUtils.DATE_PATTERN_yyyy_MM_dd_HH_MM));
                    }
                    textView = (TextView) view.findViewById(R.id.comment_cotent);
                    if (textView != null) {
                        textView.setText(data.getComments());
                    }
                    textView = (TextView) view.findViewById(R.id.reply_btn);
                    if (textView != null) {
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                commentEditText.setHint(getString(R.string.reply_who,
                                        data.getCommentName()));
                                //打开软键盘
                                UIUtils.showSoftKeyboard1(getActivity());
                                commentEditText.requestFocus();
                                parentId = data.getId();
                                commentToId = data.getCommentId();
                                commentToName = data.getCommentName();
                            }
                        });
                    }
                    textView = (TextView) view.findViewById(R.id.praise_btn);
                    if (textView != null) {
                        int praiseCount=data.getPraiseCount();
                        //有评论
                        if (praiseCount>0){

                            textView.setText(String.valueOf(praiseCount));
                            Drawable leftDrawable=getResources().getDrawable
                                    (R.drawable.btn_comment_praise);
                            leftDrawable.setBounds(0,0,leftDrawable.getMinimumWidth(),leftDrawable
                                    .getMinimumHeight());
                            textView.setCompoundDrawables(leftDrawable,
                                    null, null, null);

                        }else {
                            textView.setText("");
                            Drawable leftDrawable=getResources().getDrawable
                                    (R.drawable.comment_praise_pre_ico);
                            leftDrawable.setBounds(0,0,leftDrawable.getMinimumWidth(),leftDrawable
                                    .getMinimumHeight());
                            textView.setCompoundDrawables(leftDrawable,
                                    null, null, null);

                        }
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                praiseComment(data);
                            }
                        });
                    }


                    MyViewHolder holder = (MyViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new MyViewHolder();
                    }
                    view.setTag(holder);
                    holder.data = data;
                    view.setClickable(true);
                    return view;
                }

            };

            ExpandListViewHelper listViewHelper = new ExpandListViewHelper(getActivity(),
                    expandListView, dataAdapter) {
                @Override
                public void loadData() {
                    loadTaskComments();
                }

                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {
                    MyViewHolder holder = (MyViewHolder) v.getTag();
                    if (holder == null || holder.data == null) {
                        return false;
                    }
                    return true;
                }

                @Override
                public boolean onGroupClick(ExpandableListView parent, View v,
                                            int groupPosition, long id) {
                    return false;
                }
            };
            listViewHelper.setData(null);
            setCurrListViewHelper(expandListView, listViewHelper);
        }
    }

    private class MyViewHolder extends ViewHolder {
        int groupPosition;
        int childPosition;
    }


    private void initFinishGridview() {
        finishGridView = (MyGridView) findViewById(R.id.students_finish_gridview);
        if (finishGridView != null) {
            finishGridView.setNumColumns(Constants.MAX_BOOKS_PER_ROW);
            AdapterViewHelper gridViewHelper = new AdapterViewHelper(getActivity(),
                    finishGridView, R.layout.item_task_finish) {
                @Override
                public void loadData() {
                    loadTaskFinishInfo();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    final TaskDetail data = (TaskDetail) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    ImageView imageView = (ImageView) view.findViewById(R.id.user_img);
                    if (imageView != null) {
                        if (data.getHeadPicUrl() != null) {
                            getThumbnailManager().displayUserIcon(AppSettings.getFileUrl(
                                    data.getHeadPicUrl()), imageView);
                        }
                    }
                    TextView textView = (TextView) view.findViewById(R.id.user_name);
                    if (textView != null) {
                        textView.setText(data.getStudentName());
                    }
                    textView = (TextView) view.findViewById(R.id.finish_time);
                    if (textView != null) {
                        textView.setVisibility(View.VISIBLE);
                        String timeStr="";
                        if(task.getTaskType()==StudyTaskType.SUBMIT_HOMEWORK||task
                                .getTaskType()==StudyTaskType.RETELL_WAWA_COURSE
                                ||task.getTaskType()==StudyTaskType.INTRODUCTION_WAWA_COURSE){
                            timeStr=data.getCommitTime();
                        }else{
                            timeStr=data.getReadTime();
                        }
                        textView.setText(DateUtils.getDateStr(timeStr,
                                DateUtils.DATE_PATTERN_yyyy_MM_dd_HH_MM_SS,
                                DateUtils.DATE_PATTERN_yyyy_MM_dd_HH_MM));
                    }

                    imageView = (ImageView) view.findViewById(R.id.resource_indicator);
                    if ((task.getTaskType() == StudyTaskType.SUBMIT_HOMEWORK
                            ||task.getTaskType() == StudyTaskType.RETELL_WAWA_COURSE
                            ||task.getTaskType()==StudyTaskType.INTRODUCTION_WAWA_COURSE)
                            && !data.getIsRead()) {
                        imageView.setVisibility(View.VISIBLE);
                    } else {
                        imageView.setVisibility(View.INVISIBLE);
                    }
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;
                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null || holder.data == null) {
                        return;
                    }
                    List<TaskDetail> datas = ((TaskDetail) holder.data).getStudentCommitTaskList();
                    if (datas != null && datas.size() > 0) {
                        Intent intent = new Intent(getActivity(), StudentTasksActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString(StudentTasksActivity.STUDENTID, datas.get(0).
                                getStudentId());
                        bundle.putSerializable(StudentTasksActivity.STUDY_TASK_INFO,task);
                        intent.putExtras(bundle);
                        startActivityForResult(intent,REQUEST_CODE_STUDENT_TASK);
                    }
                }
            };
            setCurrAdapterViewHelper(finishGridView, gridViewHelper);
        }
    }

    private void initUnFinishGridview() {
        unFinishGridView = (MyGridView) findViewById(R.id.students_unfinish_gridview);
        if (unFinishGridView != null) {
            unFinishGridView.setNumColumns(Constants.MAX_BOOKS_PER_ROW);
            AdapterViewHelper gridViewHelper = new AdapterViewHelper(getActivity(),
                    unFinishGridView, R.layout.item_task_finish) {
                @Override
                public void loadData() {
                    loadTaskFinishInfo();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    final TaskDetail data = (TaskDetail) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    ImageView imageView = (ImageView) view.findViewById(R.id.user_img);
                    if (imageView != null) {
                        if (data.getHeadPicUrl() != null) {
                            getThumbnailManager().displayUserIcon(AppSettings.getFileUrl(
                                    data.getHeadPicUrl()), imageView);
                        }
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                ActivityUtils.enterPersonalSpace(getActivity(),data.getStudentId());
                            }
                        });
                    }
                    TextView textView = (TextView) view.findViewById(R.id.user_name);
                    if (textView != null) {
                        textView.setText(data.getStudentName());
                    }
                    textView = (TextView) view.findViewById(R.id.finish_time);
                    if (textView != null) {
                        textView.setVisibility(View.INVISIBLE);
                    }
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;
                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null || holder.data == null) {
                        return;
                    }
                }
            };
            addAdapterViewHelper(Constants.UN_FINISH_GRIDVIEW_TAG, gridViewHelper);
        }
    }

    private void openTask(String resId) {
        if (task == null || TextUtils.isEmpty(resId)) {
            return;
        }
        if (courseData == null) {
            WawaCourseUtils wawaCourseUtils = new WawaCourseUtils(getActivity());
            String tempResId = resId;
            int resType = 0;
            if(resId.contains("-")) {
                String[] ids = resId.split("-");
                if(ids != null && ids.length == 2) {
                    tempResId = ids[0];
                    if(ids[1] != null) {
                        resType = Integer.parseInt(ids[1]);
                    }
                }
            }
            if(resType > ResType.RES_TYPE_BASE) {
                if(TextUtils.isEmpty(tempResId)) {
                    return;
                }
                wawaCourseUtils.loadSplitCourseDetail(Integer.parseInt(tempResId));
                wawaCourseUtils.setOnSplitCourseDetailFinishListener(new WawaCourseUtils
                        .OnSplitCourseDetailFinishListener() {
                    @Override
                    public void onSplitCourseDetailFinish(SplitCourseInfo info) {
                        if(info != null) {
                            CourseData courseData = info.getCourseData();
                            TaskFinishInfoFragment.this.courseData = courseData;
                            StudyTaskOpenHelper.openTask(getActivity(), courseData,task,0,"");
                        }
                    }
                });
            } else {
                wawaCourseUtils.loadCourseDetail(tempResId);
                wawaCourseUtils.setOnCourseDetailFinishListener(new WawaCourseUtils.OnCourseDetailFinishListener() {
                    @Override
                    public void onCourseDetailFinish(CourseData courseData) {
                        TaskFinishInfoFragment.this.courseData = courseData;
                        StudyTaskOpenHelper.openTask(getActivity(), courseData,task,0,"");
                    }
                });
            }
        } else {
            StudyTaskOpenHelper.openTask(getActivity(), courseData,task,0,"");

        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.task_finish_layout:
                controlFinishView();
                break;
            case R.id.task_unfinish_layout:
                controlUnFinishView();
                break;
            case R.id.contacts_header_left_btn:
                getActivity().finish();
                break;
            case R.id.send_btn:
                setndComment();
                break;
            case R.id.contacts_header_right_btn:
                if (task.getTaskType() == StudyTaskType.TOPIC_DISCUSSION){
                    enterShowTopicDiscussionPeopleActivity();
                }else {
                    upReadTipEvent(task.getTaskId());
                }
                break;
            case R.id.contacts_header_right_ico:
//                openTask(task.getResId());

                break;
            case R.id.frame_layout:
                openTask(task.getResId());
                break;
        }
    }

    private void enterShowTopicDiscussionPeopleActivity() {
        Intent intent=new Intent(getActivity(),ShowTopicDiscussionPeopleActivity.class);
        Bundle bundle=new Bundle();
        String title=headRightView.getText().toString().trim();
        bundle.putString("title", title);
        if (discussPersonList != null && discussPersonList.size() > 0){
            bundle.putSerializable("list",(Serializable)discussPersonList);
        }
        intent.putExtras(bundle);
        getActivity().startActivity(intent);
    }

private void upReadTipEvent(String taskId){
    if(unReadBtnClickState==UNREAD_STATE_0){
        return;
    }else if(unReadBtnClickState==UNREAD_STATE_2){
        Toast.makeText(getActivity(), getString(R.string.all_read), Toast.LENGTH_SHORT).show();
        return;
    }
    if (task == null) return;
    Map<String, Object> params = new HashMap();
    params.put("TaskId", taskId);
    RequestHelper.sendPostRequest(getActivity(),
            ServerUrl.PUSH_UNFINISH_TASKS_URL, params,
            new RequestHelper.RequestDataResultListener<DataModelResult>(getActivity(),
                    DataModelResult.class) {
                @Override
                public void onSuccess(String jsonString) {
                    if (getActivity() == null) {
                        return;
                    }
                    super.onSuccess(jsonString);
                    DataModelResult result = getResult();
                    if (result == null || !result.isSuccess()) {
                        Toast.makeText(getActivity(), getString(R.string.not_read_tip_failure),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(getActivity(), getString(R.string.not_read_tip_success),
                            Toast.LENGTH_SHORT).show();
                }
            });

}

    public static void setHasCommented(boolean hasCommented) {
        TaskFinishInfoFragment.hasCommented = hasCommented;
    }

    public static boolean hasCommented() {
        return hasCommented;
    }

    private void setndComment() {
        if (task == null) return;
        String content = commentEditText.getText().toString();
        if (content.length() == 0) {
            Toast.makeText(getActivity(), getString(R.string.pls_input_comment_content),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        String commentId = null;
        String commentName = null;
        UserInfo userInfo = getUserInfo();
        if (userInfo != null) {
            commentId = userInfo.getMemberId();
            if(!TextUtils.isEmpty(userInfo.getRealName())) {
                commentName = userInfo.getRealName();
            } else {
                commentName = userInfo.getNickName();
            }
        }
        Map<String, Object> params = new HashMap();
        params.put("TaskId", task.getTaskId());
        if (parentId != 0) {
            params.put("ParentId", parentId);
        }
        params.put("Comments", content);
        params.put("CommentId", commentId);
        params.put("CommentName", commentName);
        if (commentToId.length() > 0) {
            params.put("CommentToId", commentToId);
        }
        if (commentToName.length() > 0) {
            params.put("CommentToName", commentToName);
        }
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.ADD_TASK_COMMENT_URL, params,
                new RequestHelper.RequestDataResultListener<DataModelResult>(getActivity(),
                        DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        DataModelResult result = getResult();
                        if (result == null || !result.isSuccess()) {
                            Toast.makeText(getActivity(), getString(R.string.upload_comment_error),
                                    Toast.LENGTH_SHORT)
                                    .show();
                            return;
                        }
                        //发送评论成功
                        setHasCommented(true);
                        Toast.makeText(getActivity(), getString(R.string.upload_comment_success),
                                Toast.LENGTH_SHORT)
                                .show();
                        UIUtils.hideSoftKeyboard1(getActivity(), commentEditText);
                        replaySuccessOrGiveup();
                        loadTaskComments();
                    }
                });
    }

    private void praiseComment(final StudytaskComment data) {
        if (data == null){
            return;
        }
        if(data.isHasPraised()){
            TipsHelper.showToast(getActivity(),getString(R.string.have_praised));
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("TaskCommentId", data.getId());
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.ADD_PRAISE_COUNT_URL, params,
                new RequestHelper.RequestDataResultListener<DataModelResult>(getActivity(),
                        DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        DataModelResult result = getResult();
                        if (result == null || !result.isSuccess()) {
                            Toast.makeText(getActivity(), getString(R.string.praise_fail),
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(getActivity(), getString(R.string.praise_success),
                                Toast.LENGTH_SHORT).show();
                        data.setHasPraised(true);
                        loadTaskComments();
                    }
                });
    }

    private void controlFinishView() {
        finishViewTag = !finishViewTag;
        if (finishViewTag) {
            finishGridView.setVisibility(View.VISIBLE);
            finishLogoView.setImageResource(R.drawable.list_exp_up);
        } else {
            finishGridView.setVisibility(View.GONE);
            finishLogoView.setImageResource(R.drawable.list_exp_down);
        }
    }


    private void controlUnFinishView() {
        unFinishViewTag = !unFinishViewTag;
        if (unFinishViewTag) {
            unFinishGridView.setVisibility(View.VISIBLE);
            unFinishLogoView.setImageResource(R.drawable.list_exp_up);
        } else {
            unFinishGridView.setVisibility(View.GONE);
            unFinishLogoView.setImageResource(R.drawable.list_exp_down);
        }
    }

    private void replaySuccessOrGiveup() {
        parentId = 0;
        commentToId = "";
        commentToName = "";
        commentEditText.setText("");
        commentEditText.setHint(getString(R.string.say_something));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //讨论返回后刷新逻辑
        if (data == null){
            if (requestCode == CampusPatrolPickerFragment
                    .REQUEST_CODE_DISCUSSION_COURSE_DETAILS){
                //微课详情
                if (PictureBooksDetailFragment.hasCommented()){
                    //通知之前的页面，需要刷新。
                    setHasCommented(true);
                    //reset value
                    PictureBooksDetailFragment.setHasCommented(false);
                    //需要刷新
                    refreshData();
                }

            }else if (requestCode == CampusPatrolPickerFragment
                    .REQUEST_CODE_DISCUSSION_INTRODUCTION){
                //导读
                if (SelectedReadingDetailFragment.hasCommented()){
                    //通知之前的页面，需要刷新。
                    setHasCommented(true);
                    //reset value
                    SelectedReadingDetailFragment.setHasCommented(false);
                    //需要刷新
                    refreshData();
                }

            }else if (requestCode == CampusPatrolPickerFragment
                    .EDIT_NOTE_DETAILS_REQUEST_CODE){
                //编辑帖子
                if (OnlineMediaPaperActivity.hasContentChanged()){
                    //通知之前的页面，需要刷新。
                    setHasCommented(true);
                    //reset value
                    OnlineMediaPaperActivity.setHasContentChanged(false);
                    //需要刷新
                    refreshData();
                }
            }else if (requestCode == REQUEST_CODE_STUDENT_TASK){
                //已完成学生作业
                if (StudentTasksFragment.hasReaded()){
                    StudentTasksFragment.setHasReaded(false);
                    //刷新
                    refreshData();
                }
            }
        }
    }

}
