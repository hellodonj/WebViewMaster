package com.galaxyschool.app.wawaschool.fragment;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import com.duowan.mobile.netroid.NetroidError;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
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
import com.galaxyschool.app.wawaschool.pojo.PerformClassList;
import com.galaxyschool.app.wawaschool.pojo.PerformClassListResult;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActTopicDiscussionFragment extends ContactsExpandListFragment {

    public static final String TAG = ActTopicDiscussionFragment.class.getSimpleName();

    private String commentToId = "";
    private String commentToName = "";
    private int parentId = 0;
    private ExpandableListView expandableListView;
    private View rootView;

    private PerformClassList performClassList;
    private LinearLayout bottomLayout;

    private TextView sendComment;
    private EditText editText;

    public ActTopicDiscussionFragment(PerformClassList performClassList, LinearLayout bottomLayout) {
        this.bottomLayout = bottomLayout;
        this.performClassList = performClassList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_act_topic_discussion, null);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        loadTopDisData();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    void initViews() {
        sendComment = (TextView) bottomLayout.findViewById(R.id.send_textview);
        if (sendComment != null) {
            sendComment.setOnClickListener(this);
        }
        editText = (EditText) bottomLayout.findViewById(R.id.comment_edittext_send);
        //下拉刷新
        PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);

        expandableListView = (ExpandableListView) findViewById(R.id.expandable_list_view);
        if (expandableListView != null) {
            expandableListView.setGroupIndicator(null);

            ExpandDataAdapter expandDataAdapter = new ExpandDataAdapter(getActivity(), null, R.layout
                    .item_group_view_topic_discussion, R.layout.item_child_view_topic_discussion) {
                @Override
                public int getChildrenCount(int groupPosition) {
                    if (hasData() && groupPosition < getGroupCount()) {
                        CommentListInfo groupObject = (CommentListInfo) getData().get(groupPosition);
                        if (groupObject != null) {
                            return groupObject.getChildren().size();
                        }
                    }

                    return 0;
                }

                @Override
                public Object getChild(int groupPosition, int childPosition) {
                    CommentListInfo groupObject = (CommentListInfo) getData().get(groupPosition);
                    CommentInfo childObject = groupObject.getChildren().get(childPosition);
                    return childObject;
                }

                @Override
                public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                                         View convertView, ViewGroup parent) {

                    View childView = super.getChildView(groupPosition, childPosition, isLastChild,
                            convertView, parent);
                    final CommentInfo data = (CommentInfo) getChild(groupPosition, childPosition);
                    MyExpandableListViewHolder holder = (MyExpandableListViewHolder) childView.getTag();
                    if (holder == null) {
                        holder = new MyExpandableListViewHolder();
                    }
                    holder.childPosition = childPosition;
                    holder.groupPosition = groupPosition;
                    holder.data = data;
                    //设置子布局的颜色
                    View childSubLayout = childView.findViewById(R.id.layout_child_sub_view);
                    if (childSubLayout != null){
                        childSubLayout.setBackgroundColor(getResources().getColor(R.color.line_gray));
                    }

                    //车车
                    TextView textView = (TextView) childView.findViewById(R.id.reply_name);
                    if (textView != null) {
                        textView.setText(data.getCommentName());
                    }

                    //回复
                    textView = (TextView) childView.findViewById(R.id.reply);
                    if (textView != null) {
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                editText.setText("");
                                editText.setHint(getString(R.string.reply_who, data
                                        .getCommentName()));
                                editText.setFocusable(true);
                                editText.setFocusableInTouchMode(true);
                                editText.requestFocus();
                                //打开软键盘
                                UIUtils.showSoftKeyboardOnEditText(editText);
                                //子类的parentId是调用getParentId获取。
                                parentId = data.getParentId();
                                commentToId = data.getCommentId();
                                commentToName = data.getCommentName();
                            }
                        });
                    }

                    //潇潇：
                    textView = (TextView) childView.findViewById(R.id.reply_to_name);
                    if (textView != null) {
                        textView.setText(data.getCommentToName());
                    }


                    //回复内容
                    textView = (TextView) childView.findViewById(R.id.reply_content);
                    if (textView != null) {
                        textView.setText(data.getComments());
                    }

                    //回复时间
                    textView = (TextView) childView.findViewById(R.id.reply_time);
                    if (textView != null) {
                        textView.setText(data.getCommentTime());
                    }

                    childView.setTag(holder);

                    return childView;
                }

                @Override
                public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                                         ViewGroup parent) {
                    View groupView = super.getGroupView(groupPosition, isExpanded, convertView,
                            parent);
                    if (groupView!=null){
                        groupView.setBackgroundColor(getResources().getColor(R.color.main_bg_color));
                    }
                    final CommentListInfo data = (CommentListInfo) getGroup(groupPosition);
                    if (data == null) {
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
                    TextView commentReply = (TextView) groupView.findViewById(R.id.comment_reply);
                    //父之间的分割线
                    View top_line = groupView.findViewById(R.id.top_line);
                    if (top_line != null) {
                        //第一个item不显示顶部分割线
                        if (groupPosition == 0) {
                            top_line.setVisibility(View.GONE);
                        } else {
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
                                commitTime = commitTime.substring(0, commitTime.lastIndexOf(":"));
                            }
                            commentDate.setText(commitTime);
                        }
                    }
                    //点赞
                    if (commentPraise != null) {
                        int praiseCount = data.getPraiseCount();
                        //有评论
                        if (praiseCount > 0) {

                            commentPraise.setText(String.valueOf(praiseCount));
                            Drawable leftDrawable = getResources().getDrawable
                                    (R.drawable.btn_comment_praise);
                            leftDrawable.setBounds(0, 0, leftDrawable.getMinimumWidth(), leftDrawable
                                    .getMinimumHeight());
                            commentPraise.setCompoundDrawables(leftDrawable,
                                    null, null, null);

                        } else {
                            commentPraise.setText("");
                            Drawable leftDrawable = getResources().getDrawable
                                    (R.drawable.comment_praise_pre_ico);
                            leftDrawable.setBounds(0, 0, leftDrawable.getMinimumWidth(), leftDrawable
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

                    if (commentReply != null) {
                        //回复
                        commentReply.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                editText.setText("");
                                editText.setHint(getString(R.string.reply_who, data
                                        .getCommentName()));
                                editText.setFocusable(true);
                                editText.setFocusableInTouchMode(true);
                                editText.requestFocus();
                                //打开软键盘
                                UIUtils.showSoftKeyboardOnEditText(editText);
                                //父类的parentId是直接调用getId获取
                                parentId = data.getId();
                                commentToId = data.getCommentId();
                                commentToName = data.getCommentName();
                            }
                        });
                    }

                    MyExpandableListViewHolder holder = (MyExpandableListViewHolder) groupView.getTag();
                    if (holder == null) {
                        holder = new MyExpandableListViewHolder();
                        groupView.setTag(holder);
                    }
                    holder.data = data;
                    groupView.setClickable(true);

                    return groupView;
                }
            };

            ExpandListViewHelper expandListViewHelper = new ExpandListViewHelper(getActivity(),
                    expandableListView, expandDataAdapter) {
                @Override
                public void loadData() {
                    loadTopDisData();
                }

                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                    MyExpandableListViewHolder holder = (MyExpandableListViewHolder) v.getTag();
                    if (holder == null || holder.data == null) {
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


    private class MyExpandableListViewHolder extends ViewHolder {
        int groupPosition;
        int childPosition;
    }

    private void addPraiseCount(final CommentListInfo data) {
        if (data == null) {
            return;
        }
        if (data.isHasPraised()) {
            TipsHelper.showToast(getActivity(), getString(R.string.have_praised));
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("TaskCommentId", data.getId());

        RequestHelper.RequestListener listener = new RequestHelper
                .RequestDataResultListener<DataModelResult>(getActivity(), DataModelResult.class) {


            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);

                if (getResult() == null || !getResult().isSuccess()) {
                    return;
                } else {
                    data.setHasPraised(true);
                    loadTopDisData();
                }
            }
        };

        RequestHelper.sendPostRequest(getActivity(), ServerUrl.ACT_CLASSROOM_TOP_PRAISE_BASE_URL, params, listener);
    }

    /**
     * 加载表演课堂的评论数据
     */
    private void loadTopDisData(){
        Map<String, Object> params = new HashMap();
        //学校Id，必填
        params.put("ExtId", performClassList.getId());
        params.put("Type", 2);
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_ACT_CLASSROOM_TOP_DIS_BASE_URL, params,
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
                        updateTopViews(getResult());
                    }
                });
    }

    private void updateTopViews(CommentObjectResult result) {
        List<CommentListInfo> list = result.getModel().getData().getCommentList();
        if (list == null || list.size() <= 0) {
            return;
        } else {
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
            //实时更新tab中评论的数量
            Fragment fragment=getParentFragment();
            if (fragment!=null){
                if(fragment instanceof ActClassroomDetailFragment){
                    ((ActClassroomDetailFragment) fragment).setDiscussionCount(discussCount);
                }
            }
            //设置点赞标志位
            List<CommentListInfo> oldData = getCurrListViewHelper().getData();
            if (oldData != null && oldData.size() > 0) {
                for (CommentListInfo item : list) {
                    for (CommentListInfo oldItem : oldData) {
                        if (item.getId() == oldItem.getId()) {
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
        if (v.getId() == R.id.send_textview) {
            //发送
            sendActClassroomComment();
        } else {
            super.onClick(v);
        }
    }

    private void sendActClassroomComment() {

        String comments = editText.getText().toString().trim();
        if (TextUtils.isEmpty(comments)) {
            TipsHelper.showToast(getActivity(), getString(R.string.pls_input_comment_content));
            return;
        }
        //隐藏软键盘
        UIUtils.hideSoftKeyboardValid(getActivity(), editText);
        if (getUserInfo() == null) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("ExtId", performClassList.getId());
        //非必填,当评论的对象是针对已存在的学生评论时必填，否则传0,回复操作。
        params.put("ParentId", parentId);
        params.put("Comments", comments);
        params.put("CommentId", getMemeberId());
        String name = null;
        name = getUserInfo().getRealName();
        if (TextUtils.isEmpty(name)) {
            name = getUserInfo().getNickName();
        }
        params.put("CommentName", name);
        //非必填,当评论的对象是针对已存在的学生评论时必填，否则传空
        params.put("CommentToId", commentToId);
        //非必填,当评论的对象是针对已存在的学生评论时必填，否则传空
        params.put("CommentToName", commentToName);

        params.put("Type",2);

        RequestHelper.RequestListener listener = new RequestHelper
                .RequestDataResultListener<DataModelResult>(getActivity(), DataModelResult.class) {

            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);

                if (getResult() == null || !getResult().isSuccess()) {
                    return;
                } else {
                    TipMsgHelper.ShowMsg(getActivity(), R.string.send_comment_success);
                    resetEditText();
                    loadTopDisData();
                }
            }

            @Override
            public void onError(NetroidError error) {
                if (getActivity() == null) {
                    return;
                }
                resetEditText();
                super.onError(error);
            }
        };

        RequestHelper.sendPostRequest(getActivity(), ServerUrl.ACT_CLASSROOM_TOP_DISCUSS_BASE_URL, params, listener);

    }

    public void resetEditText() {
        editText.setHint(getString(R.string.say_something));
        editText.setText("");
        parentId = 0;
        commentToId = "";
        commentToName = "";
    }
}
