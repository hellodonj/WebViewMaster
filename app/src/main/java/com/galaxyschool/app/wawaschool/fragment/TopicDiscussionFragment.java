package com.galaxyschool.app.wawaschool.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import com.duowan.mobile.netroid.NetroidError;
import com.galaxyschool.app.wawaschool.HomeworkFinishStatusActivity;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.ShowTopicDiscussionPeopleActivity;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.ExpandDataAdapter;
import com.galaxyschool.app.wawaschool.fragment.library.ExpandListViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.CommentInfo;
import com.galaxyschool.app.wawaschool.pojo.CommentListInfo;
import com.galaxyschool.app.wawaschool.pojo.CommentObjectResult;
import com.galaxyschool.app.wawaschool.pojo.DiscussPersonList;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 话题讨论
 */

public class TopicDiscussionFragment extends ContactsExpandListFragment implements
        View.OnLayoutChangeListener {

    public static final String TAG = TopicDiscussionFragment.class.getSimpleName();

    private CommentObjectResult dataListResult;
    private View headView;
    private View rootView;
    private View rootLayout;
    private int TaskId;
    private String commentTitle;
    private String commentContent;
    private String fromType;
    private EditText commentContentEditText;
    private TextView sendCommentTextView;
    private int parentId=0;
    private String commentToId="";
    private String commentToName="";
    private int commentTaskId=-1;
    private ExpandableListView expandableListView;
    private int roleType=-1;
    private TextView headRightView;
    private List<DiscussPersonList> discussPersonList=new ArrayList<>();
    //屏幕高度
    private int screenHeight = 0;
    //软件盘弹起后所占高度阀值
    private int keyHeight = 0;
    private String taskCreateId = null;
    private View comment_grp;
    private boolean isTopicDiscussionTask,isOwnerTask;
    private boolean isCampusPatrolTag;
    private static boolean hasCommented;
    private boolean hiddenHeaderView = false;//隐藏头部
    private boolean shouldChangeBgColor;
    private boolean isHistoryClass;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.topic_discussion, null);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        refreshData();
    }

    private void refreshData(){
        loadViews();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    void initViews() {
        if (getArguments()!=null){
            shouldChangeBgColor = getArguments().getBoolean(HomeworkFinishStatusActivity
                    .Constants.SHOULD_CHANGE_BG_COLOR);
            //隐藏头部
            hiddenHeaderView = getArguments().getBoolean("hiddenHeaderView");
            //校园巡查标识
            isCampusPatrolTag = getArguments().getBoolean(CampusPatrolMainFragment
                    .IS_CAMPUS_PATROL_TAG);
            TaskId=getArguments().getInt("TaskId");
            roleType=getArguments().getInt("roleType");
            fromType=getArguments().getString("fromType");
            isHistoryClass = getArguments().getBoolean(ActivityUtils.EXTRA_IS_HISTORY_CLASS);
            if (!TextUtils.isEmpty(fromType)){
//                if (fromType.equals("commitHomework")){
//                    //从作业提交页面传入
//
//                }
//                //从作业列表进入
//                else
                if (fromType.equals("homeworkList")){
                    commentTitle=getArguments().getString("commentTitle");
                    commentContent=getArguments().getString("commentContent");
                }
            }
            taskCreateId = getArguments().getString("taskCreateId");
        }

        isTopicDiscussionTask = !TextUtils.isEmpty(fromType) && fromType.equals("homeworkList");
        isOwnerTask = !TextUtils.isEmpty(taskCreateId) && taskCreateId.equals(getMemeberId());

//        rootLayout=findViewById(R.id.root_layout);
        rootView.addOnLayoutChangeListener(TopicDiscussionFragment.this);
        //获取屏幕高度
        screenHeight = getActivity().getWindowManager().getDefaultDisplay().getHeight();
        //阀值设置为屏幕高度的1/3
        keyHeight = screenHeight/3;
        //头布局
        View view = findViewById(R.id.contacts_header_layout);
        if (view != null){
            view.setVisibility(hiddenHeaderView ? View.GONE : View.VISIBLE);
        }

        //标题
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            //标题除了“话题讨论”之外，其他的都叫“讨论”。
            if (isTopicDiscussionTask){
                //话题讨论
                textView.setText(R.string.discuss_topic);
            }else {
                //讨论
                textView.setText(R.string.discussion_text);
            }
        }

        //右侧的布局
        headRightView = (TextView) findViewById(R.id.contacts_header_right_btn);
        if (headRightView != null) {
            headRightView.setVisibility(View.VISIBLE);
            headRightView.setOnClickListener(this);
            headRightView.setTextAppearance(getActivity(), R.style.txt_wawa_big_green);
            headRightView.setText(getString(R.string.n_people_join,0));
        }

        headView = findViewById(R.id.layout_head_topic_discussion);
        if (isTopicDiscussionTask){
            headView.setVisibility(View.VISIBLE);

        }else {
            headView.setVisibility(View.GONE);
        }

        //标题
        textView = (TextView) headView.findViewById(R.id.tv_title);
        if (textView != null) {
            textView.setText(commentTitle);
        }

        //内容
        textView = (TextView) headView.findViewById(R.id.tv_content);
        if (textView != null) {
            textView.setText(commentContent);
        }

        //发送评论布局
         comment_grp=findViewById(R.id.comment_grp);
        if (comment_grp!=null){
            if (shouldChangeBgColor){
                comment_grp.setBackgroundColor(getResources().getColor(R.color.main_bg_color));
            }else {
                comment_grp.setBackgroundColor(getResources().getColor(R.color.white));
            }
            checkIsAllowComment();
        }

        commentContentEditText= (EditText) findViewById(R.id.comment_send_content);
        if (commentContentEditText != null){
            if (shouldChangeBgColor){
                commentContentEditText.setBackgroundColor(getResources().getColor(R.color.white));
            }
        }
        sendCommentTextView= (TextView) findViewById(R.id.comment_send_btn);
        sendCommentTextView.setOnClickListener(this);

        //下拉刷新
        PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);
        if (shouldChangeBgColor){
            pullToRefreshView.setBackgroundColor(getResources().getColor(R.color.main_bg_color));
        }else {
            pullToRefreshView.setBackgroundColor(getResources().getColor(R.color.white));
        }

        expandableListView = (ExpandableListView) findViewById(R.id.expandable_list_view);
        if (expandableListView != null) {
            expandableListView.setGroupIndicator(null);

            ExpandDataAdapter expandDataAdapter=new ExpandDataAdapter(getActivity(),null,R.layout
                    .item_group_view_topic_discussion,R.layout.item_child_view_topic_discussion) {
                @Override
                public int getChildrenCount(int groupPosition) {
                    if (hasData()&&groupPosition<getGroupCount()) {
                        CommentListInfo groupObject = (CommentListInfo) getData().get(groupPosition);
                        if (groupObject!=null){
                            return groupObject.getChildren().size();
                        }
                    }

                    return 0;
                }

                @Override
                public Object getChild(int groupPosition, int childPosition) {
                    CommentListInfo groupObject= (CommentListInfo) getData().get(groupPosition);
                    CommentInfo childObject=groupObject.getChildren().get(childPosition);
                    return childObject;
                }

                @Override
                public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                                         View convertView, ViewGroup parent) {

                    View childView=super.getChildView(groupPosition, childPosition, isLastChild,
                            convertView, parent);
                    final CommentInfo data= (CommentInfo) getChild(groupPosition,childPosition);
                    MyExpandableListViewHolder holder= (MyExpandableListViewHolder) childView.getTag();
                    if (holder==null){
                        holder=new MyExpandableListViewHolder();
                    }

                    holder.childPosition=childPosition;
                    holder.groupPosition=groupPosition;
                    holder.data=data;

                    View childSubLayout = childView.findViewById(R.id.layout_child_sub_view);
                    if (childSubLayout != null){
                        if (shouldChangeBgColor){
                            childSubLayout.setBackgroundColor(getResources().getColor(R.color.line_gray));
                        }
                    }

                    //车车
                    TextView textView= (TextView) childView.findViewById(R.id.reply_name);
                    if (textView!=null){
                        textView.setText(data.getCommentName());
                    }

                    //回复
                    textView= (TextView) childView.findViewById(R.id.reply);
                    if (textView!=null){
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (checkIsAllowReply()){
                                commentContentEditText.setText("");
                                commentContentEditText.setHint(getString(R.string.reply_who,data
                                        .getCommentName()));
                                commentContentEditText.setFocusable(true);
                                commentContentEditText.setFocusableInTouchMode(true);
                                commentContentEditText.requestFocus();
                                //打开软键盘
                                UIUtils.showSoftKeyboardOnEditText(commentContentEditText);
                                //子类的parentId是调用getParentId获取。
                                parentId = data.getParentId();
                                commentToId = data.getCommentId();
                                commentToName = data.getCommentName();
                                }
                            }
                        });
                    }

                    //潇潇：
                    textView= (TextView) childView.findViewById(R.id.reply_to_name);
                    if (textView!=null){
                        textView.setText(data.getCommentToName());
                    }


                    //回复内容
                    textView= (TextView) childView.findViewById(R.id.reply_content);
                    if (textView!=null){
                        textView.setText(data.getComments());
                    }

                    //回复时间
                    textView= (TextView) childView.findViewById(R.id.reply_time);
                    if (textView!=null){
                        textView.setText(data.getCommentTime());
                    }

                    childView.setTag(holder);

                    return childView;
                }

                @Override
                public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                                         ViewGroup parent) {
                    View groupView=super.getGroupView(groupPosition, isExpanded, convertView,
                            parent);

                    if (shouldChangeBgColor){
                        groupView.setBackgroundColor(getResources().getColor(R.color.main_bg_color));
                    }else {
                        groupView.setBackgroundColor(getResources().getColor(R.color.white));
                    }

                    final CommentListInfo data= (CommentListInfo) getGroup(groupPosition);
                    if (data==null){
                        return groupView;
                    }
                    ImageView senderIcon = (ImageView) groupView.findViewById(R.id.comment_sender_icon);
                    //点击头像进入个人详情
                    senderIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //游客之类的memberId为空的不给点击。
                            if (!TextUtils.isEmpty(String.valueOf(data.getCommentId()))) {
                                ActivityUtils.enterPersonalSpace(getActivity(),
                                        String.valueOf(data.getCommentId()));
                            }
                        }
                    });
                    TextView senderName = (TextView) groupView.findViewById(R.id.comment_sender_name);
                    TextView commentContent = (TextView) groupView.findViewById(R.id.comment_content);
                    TextView commentDate = (TextView) groupView.findViewById(R.id.comment_date);
                    TextView commentPraise = (TextView) groupView.findViewById(R.id.comment_praise);
                    //回复
                    TextView commentReply= (TextView) groupView.findViewById(R.id.comment_reply);
//                    //父子之间的分割线
//                    View line=groupView.findViewById(R.id.line);
//
//                    if (line!=null){
//                        if (data.getChildren()!=null&&data.getChildren().size()>0){
//                            line.setVisibility(View.VISIBLE);
//                        }else {
//                            line.setVisibility(View.GONE);
//                        }
//                    }
                    //父之间的分割线
                    View top_line=groupView.findViewById(R.id.top_line);
                    if (top_line!=null){
                        //第一个item不显示顶部分割线
                        if (groupPosition==0){
                            top_line.setVisibility(View.GONE);
                        }else {
                            top_line.setVisibility(View.VISIBLE);
                        }
                    }

                    if (!TextUtils.isEmpty(data.getCommentHeadPicUrl())) {
                        MyApplication.getThumbnailManager(getActivity()).displayThumbnail(AppSettings
                                .getFileUrl(data.getCommentHeadPicUrl()), senderIcon);
                    } else {
                        senderIcon.setImageResource(R.drawable.comment_default_user_ico);
                    }
                    if (!TextUtils.isEmpty(data.getCommentName())) {
                        senderName.setText(data.getCommentName());
                    } else {
                        senderName.setText(R.string.anonym);
                    }
                    commentContent.setText(data.getComments());
                    if (commentDate != null) {
                        String commitTime = data.getCommentTime();
                        if (!TextUtils.isEmpty(commitTime)) {
                            if (commitTime.contains(":")) {
                                //精确到分
                                commitTime = commitTime.substring(0,commitTime.lastIndexOf(":"));
                            }
                            commentDate.setText(commitTime);
                        }
                    }
                    //点赞
                    if (commentPraise!=null) {
                        int praiseCount=data.getPraiseCount();
                        //有评论
                        if (praiseCount>0){

                            commentPraise.setText(String.valueOf(praiseCount));
                            Drawable leftDrawable=getResources().getDrawable
                                    (R.drawable.btn_comment_praise);
                            leftDrawable.setBounds(0,0,leftDrawable.getMinimumWidth(),leftDrawable
                                    .getMinimumHeight());
                            commentPraise.setCompoundDrawables(leftDrawable,
                                    null, null, null);

                        }else {
                            commentPraise.setText("");
                            Drawable leftDrawable=getResources().getDrawable
                                    (R.drawable.comment_praise_pre_ico);
                            leftDrawable.setBounds(0,0,leftDrawable.getMinimumWidth(),leftDrawable
                                    .getMinimumHeight());
                            commentPraise.setCompoundDrawables(leftDrawable,
                                    null, null, null);

                        }
                        commentPraise.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                addPraiseCount(data);
                            }
                        });
                    }

                    if (commentReply!=null) {
                        if (isHistoryClass){
                            commentReply.setVisibility(View.GONE);
                        }
                        //回复
                        commentReply.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (checkIsAllowReply()) {
                                    commentContentEditText.setText("");
                                    commentContentEditText.setHint(getString(R.string.reply_who,data
                                            .getCommentName()));
                                    commentContentEditText.setFocusable(true);
                                    commentContentEditText.setFocusableInTouchMode(true);
                                    commentContentEditText.requestFocus();
                                    //打开软键盘
                                    UIUtils.showSoftKeyboardOnEditText(commentContentEditText);
                                    //父类的parentId是直接调用getId获取
                                    parentId = data.getId();
                                    commentToId = data.getCommentId();
                                    commentToName = data.getCommentName();
                                }
                            }
                        });
                    }

                    MyExpandableListViewHolder holder= (MyExpandableListViewHolder) groupView.getTag();
                    if (holder==null){
                        holder=new MyExpandableListViewHolder();
                        groupView.setTag(holder);
                    }
                    holder.data=data;
                    groupView.setClickable(true);

                    return groupView;
                }
            };

            ExpandListViewHelper expandListViewHelper=new ExpandListViewHelper(getActivity(),
                    expandableListView,expandDataAdapter) {
                @Override
                public void loadData() {
                    loadCommonData();
                }

                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                    MyExpandableListViewHolder holder= (MyExpandableListViewHolder) v.getTag();
                    if (holder==null||holder.data==null){
                        return false;
                    }
                    return true;
                }

                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                    return false;
                }
            };
            expandListViewHelper.setData(null);
            setCurrListViewHelper(expandableListView, expandListViewHelper);
        }
    }

    /**
     * 判断用户是否允许评论
     */
    private void checkIsAllowComment(){

        if (!isCampusPatrolTag && !isHistoryClass) {
            if (roleType == RoleType.ROLE_TYPE_STUDENT) {
                //学生角色不区分任务类型
                comment_grp.setVisibility(View.VISIBLE);

            } else if (roleType == RoleType.ROLE_TYPE_PARENT) {
                //家长角色在话题讨论任务进行了限制
                if (isTopicDiscussionTask) {
                    //话题讨论类型限制家长角色参与讨论
                    //reset now
                    comment_grp.setVisibility(View.VISIBLE);

                } else {
                    //其他类型不限制
                    comment_grp.setVisibility(View.VISIBLE);

                }

            } else if (roleType == RoleType.ROLE_TYPE_TEACHER) {

                //话题讨论类型任务限制非发送话题讨论任务的老师参与讨论

                if (isTopicDiscussionTask) {
                    //话题讨论任务限制
                    if (isOwnerTask) {
                        //自己布置的任务不限制
                        comment_grp.setVisibility(View.VISIBLE);
                    } else {
                        //别人的任务要限制
                        //老师也要放开
                        comment_grp.setVisibility(View.VISIBLE);
                    }

                } else {
                    //其他类型不限制
                    comment_grp.setVisibility(View.VISIBLE);
                }

            } else {
                //其他角色不予以讨论
                comment_grp.setVisibility(View.GONE);
            }
        }else {
            //校园巡查暂时隐藏评论框
            comment_grp.setVisibility(View.GONE);
        }

    }

    /**
     * 判断用户是否允许回复
     * @return
     */
    private boolean checkIsAllowReply() {
        boolean isAllow = false;
        if (!isCampusPatrolTag) {
            if (roleType == RoleType.ROLE_TYPE_PARENT) {
                //话题讨论任务限制家长讨论，其他类型不限制。
                if (isTopicDiscussionTask) {
                    //reset now
                    isAllow = true;
                } else {
                    //其他类型不限制
                    isAllow = true;
                }

            } else if (roleType == RoleType.ROLE_TYPE_TEACHER) {
                //话题讨论任务限制非发布讨论任务老师参与讨论，其他不限制。
                if (isTopicDiscussionTask) {
                    if (isOwnerTask) {
                        //自己的任务可以讨论
                        isAllow = true;
                    } else {
                        //别人的任务不可以讨论
                        //老师也要放开
                        isAllow = true;
                    }

                } else {
                    //其他类型不限制
                    isAllow = true;
                }

            } else if (roleType == RoleType.ROLE_TYPE_STUDENT) {
                //学生不限制
                isAllow = true;
            } else {
                //其他身份不允许讨论
                isAllow = false;

            }
        }else {
            //校园巡查暂时屏蔽回复功能
            isAllow = false;
        }

        return isAllow;
    }

    /**
     * 监听软键盘弹/收起
     * @param v
     * @param left
     * @param top
     * @param right
     * @param bottom
     * @param oldLeft
     * @param oldTop
     * @param oldRight
     * @param oldBottom
     */
    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft,
                               int oldTop, int oldRight, int oldBottom) {

        if(oldBottom != 0 && bottom != 0 &&(oldBottom - bottom > keyHeight)){

//            Toast.makeText(getActivity(), "监听到软键盘弹起...", Toast.LENGTH_SHORT).show();

        }else if(oldBottom != 0 && bottom != 0 &&(bottom - oldBottom > keyHeight)){

//            Toast.makeText(getActivity(), "监听到软件盘关闭...", Toast.LENGTH_SHORT).show();
                resetEditText();
        }
    }

    private class MyExpandableListViewHolder extends ViewHolder {
        int groupPosition;
        int childPosition;
    }
    private void addPraiseCount(final CommentListInfo data) {
        if (data == null){
            return;
        }
        if(data.isHasPraised()){
            TipsHelper.showToast(getActivity(),getString(R.string.have_praised));
            return;
        }
        Map<String,Object> params=new HashMap<>();
        params.put("TaskCommentId", data.getId());

        RequestHelper.RequestListener listener=new RequestHelper
                .RequestDataResultListener<DataModelResult>(getActivity(),DataModelResult.class){

            @Override
            public void onSuccess(String jsonString) {
                if (getActivity()==null){
                    return;
                }
                super.onSuccess(jsonString);

                if (getResult()==null||!getResult().isSuccess()){
                    return;
                }else {
                    data.setHasPraised(true);
                    loadCommonData();
                }
            }
        };

        RequestHelper.sendPostRequest(getActivity(),ServerUrl.ADD_PRAISE_COUNT_URL,params,listener);
    }

    private void loadViews() {
        loadCommonData();
    }

    /**
     * 模拟数据
     */
    private void loadCommonData() {
                Map<String, Object> params = new HashMap();
                //学校Id，必填
                params.put("TaskId",TaskId);

                RequestHelper.sendPostRequest(getActivity(),
                        ServerUrl.GET_COMMENT_LIST_URL, params,
                        new DefaultPullToRefreshDataListener<CommentObjectResult>(
                                CommentObjectResult.class) {
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

                                updateViews(getResult());
                            }
                        });

    }

    private void updateViews(CommentObjectResult result) {
        List<CommentListInfo> list = result.getModel().getData().getCommentList();
        List<DiscussPersonList> peopleResultList=result.getModel().getData().getDiscussPersonList();
        if (peopleResultList!=null&&peopleResultList.size()>0){
            headRightView.setText(getString(R.string.n_people_join,peopleResultList.size()));
            discussPersonList=peopleResultList;
        }
        if (list==null||list.size()<=0){
            return;
        }else {

            int discussCount = 0;
            discussCount += list.size();
            for (CommentListInfo info : list) {
                if (info != null) {
                    List<CommentInfo> childList = info.getChildren();
                    if (childList != null && childList.size() > 0) {
                        discussCount += childList.size();
                    }
                }
            }
            //设置讨论数
            Fragment parentFragment = getParentFragment();
            if (parentFragment != null && parentFragment instanceof HomeworkCommitFragment){
                ((HomeworkCommitFragment)parentFragment).setDiscussionCount(discussCount);
            }
            //设置点赞标志位
            List<CommentListInfo> oldData = getCurrListViewHelper().getData();
            if(oldData != null && oldData.size() > 0){
                for(CommentListInfo item : list){
                    for(CommentListInfo oldItem : oldData){
                        if(item.getId() == oldItem.getId()){
                            item.setHasPraised(oldItem.isHasPraised());
                        }
                    }
                }
            }
            getCurrListViewHelper().setData(list);
            //展开
            expandAllView();
        }
    }



    private void expandAllView() {
        int groupCount = expandableListView.getCount();
        for (int i = 0; i < groupCount; i++) {
            expandableListView.expandGroup(i);
        }
    }



    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.comment_send_btn) {
            //发送
            addTaskComment();

        }else if (v.getId()==R.id.contacts_header_right_btn){
            //查看参与人数
            enterShowTopicDiscussionPeopleActivity();
        }else {
            super.onClick(v);
        }
    }

    private void enterShowTopicDiscussionPeopleActivity() {
        Intent intent=new Intent(getActivity(),ShowTopicDiscussionPeopleActivity.class);
        Bundle bundle=new Bundle();
        String title=headRightView.getText().toString().trim();
        bundle.putString("title", title);
        if (discussPersonList!=null&&discussPersonList.size()>0){
            bundle.putSerializable("list",(Serializable)discussPersonList);
        }
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * 隐藏软键盘逻辑
     */
    public void controlKeyboardHiddenLogic(){
        if (commentContentEditText != null) {
            //隐藏软键盘
            UIUtils.hideSoftKeyboardValid(getActivity(), commentContentEditText);
            resetEditText();
        }
    }

    private void addTaskComment() {
        if (!isLogin()){
            ActivityUtils.enterLogin(getActivity());
            return;
        }
        String comments=commentContentEditText.getText().toString().trim();
        if (TextUtils.isEmpty(comments)){
            TipsHelper.showToast(getActivity(),getString(R.string.pls_input_comment_content));
            return;
        }
        //隐藏软键盘
        UIUtils.hideSoftKeyboardValid(getActivity(),commentContentEditText);
        commentTaskId=TaskId;
        if (getUserInfo() == null){
            return;
        }
        Map<String,Object> params=new HashMap<>();
        params.put("TaskId", commentTaskId);
        //非必填,当评论的对象是针对已存在的学生评论时必填，否则传0,回复操作。
        params.put("ParentId", parentId);
        params.put("Comments", comments);
        params.put("CommentId", getMemeberId());
        String name = null;
        name=getUserInfo().getRealName();
        if (TextUtils.isEmpty(name)){
            name=getUserInfo().getNickName();
        }
        params.put("CommentName", name);
        //非必填,当评论的对象是针对已存在的学生评论时必填，否则传空
        params.put("CommentToId", commentToId);
        //非必填,当评论的对象是针对已存在的学生评论时必填，否则传空
        params.put("CommentToName",commentToName);

        RequestHelper.RequestListener listener=new RequestHelper
                .RequestDataResultListener<DataModelResult>(getActivity(),DataModelResult.class){

            @Override
            public void onSuccess(String jsonString) {
                if (getActivity()==null){
                    return;
                }
                super.onSuccess(jsonString);

                if (getResult()==null||!getResult().isSuccess()){
                    return;
                }else {
                    notifyParentDataSetChanged();
                    TipMsgHelper.ShowMsg(getActivity(),R.string.str_send_comment_success);
                    resetEditText();
                    loadCommonData();
                }
            }

            @Override
            public void onError(NetroidError error) {
                if(getActivity() == null) {
                    return;
                }
                resetEditText();
                super.onError(error);
            }
        };

        RequestHelper.sendPostRequest(getActivity(),ServerUrl.ADD_TASK_COMMENT_URL,params,listener);

    }

    private void notifyParentDataSetChanged() {
        //设置讨论数
        Fragment parentFragment = getParentFragment();
        if (parentFragment != null && parentFragment instanceof HomeworkCommitFragment){
            ((HomeworkCommitFragment)parentFragment).notifyDataSetChanged();
        }else {
            //设置已经评论
            setHasCommented(true);
        }
    }

    private void resetEditText() {
        commentContentEditText.setHint(getString(R.string.say_something));
        commentContentEditText.setText("");
        parentId=0;
        commentToId="";
        commentToName="";
    }

    public static void setHasCommented(boolean hasCommented) {
        TopicDiscussionFragment.hasCommented = hasCommented;
    }

    public static boolean hasCommented() {
        return hasCommented;
    }

}
