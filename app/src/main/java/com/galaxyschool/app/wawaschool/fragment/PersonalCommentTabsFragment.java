package com.galaxyschool.app.wawaschool.fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.lqwawa.apps.views.ContainsEmojiEditText;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskCommentDiscussPerson;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskCommentDiscussPersonResult;
import com.galaxyschool.app.wawaschool.pojo.StudytaskComment;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonalCommentTabsFragment extends ContactsListFragment {
    public static String TAG = PersonalCommentTabsFragment.class.getSimpleName();
    private GridView listView;
    private ContainsEmojiEditText commentEditText;
    private int commitTaskId;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_english_teacher_review_fragment, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadIntentData();
        initViews();
        loadCommonData();
    }

    private void loadIntentData(){
        Bundle args = getArguments();
        if (args != null){
            commitTaskId = args.getInt("student_commit_task_id");
        }
    }

    private void initViews() {
        //输入框
        commentEditText = (ContainsEmojiEditText) getActivity().findViewById(R.id.edit_btn);
        if (commentEditText != null) {
            //设置背景色
            commentEditText.setBackgroundResource(R.drawable.gray_10dp_line_gray_color);
            commentEditText.setMaxlen(40);
        }
        //发送按钮
        TextView sendTextView = (TextView) getActivity().findViewById(R.id.send_btn);
        if (sendTextView != null) {
            sendTextView.setOnClickListener(this);
        }

        listView = (GridView) findViewById(R.id.resource_list_view);
        listView.setNumColumns(1);
        listView.setHorizontalSpacing(0);
        listView.setVerticalSpacing(1);
        //下拉刷新
        PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);
        if (listView != null) {
            AdapterViewHelper helper = new AdapterViewHelper(getActivity(), listView
                    , R.layout.item_english_review) {
                @Override
                public void loadData() {
                    loadCommonData();
                }
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    //灰色背景
                    view.setBackgroundColor(getResources().getColor(R.color.main_bg_color));
                    final StudytaskComment data = (StudytaskComment) getDataAdapter()
                            .getItem(position);
                    if (data == null) {
                        return view;
                    }

                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;

                    //头部分割线
                    View topLineView = view.findViewById(R.id.top_line);
                    if (topLineView != null) {
                        if (position == 0) {
                            topLineView.setVisibility(View.GONE);
                        } else {
                            topLineView.setVisibility(View.VISIBLE);
                        }
                    }

                    ImageView imageView = (ImageView) view.findViewById(R.id.iv_student_icon);
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
                    TextView textView = (TextView) view.findViewById(R.id.tv_student_name);
                    if (textView != null) {
                        textView.setText(data.getCommentName());
                    }
                    textView = (TextView) view.findViewById(R.id.tv_commit_time);
                    if (textView != null) {
                        textView.setVisibility(View.VISIBLE);
                        String commitTime = data.getCommentTime();
                        if (!TextUtils.isEmpty(commitTime)) {
                            if (commitTime.contains(":")) {
                                //精确到分
                                commitTime = commitTime.substring(0, commitTime.lastIndexOf(":"));
                            }
                            textView.setText(commitTime);
                        }
                    }
                    textView = (TextView) view.findViewById(R.id.tv_title);
                    if (textView != null) {
                        textView.setText(data.getComments());
                    }

                    //隐藏作业图标
                    View iconLayout = view.findViewById(R.id.layout_icon);
                    if (iconLayout != null) {
                        iconLayout.setVisibility(View.GONE);
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
                    StudytaskComment data = (StudytaskComment) holder.data;
                    if (data == null) {
                        return;
                    }
                }
            };
            setCurrAdapterViewHelper(listView, helper);
        }
    }



    /**
     * 拉取点评记录
     */
    private void loadCommonData() {
        Map<String, Object> params = new HashMap<>();
        params.put("TaskId", commitTaskId);
        //视情况:当不传或传0的时候为任务讨论，当传1时为英文写作的老师点评
        params.put("Type", 1);

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
                        if (result == null || !result.isSuccess()
                                || result.getModel() == null) {
                            return;
                        }
                        StudyTaskCommentDiscussPerson personData = result.getModel().getData();
                        //判断是否可以编辑
                        if (personData != null) {
                            List<StudytaskComment> data = personData.getCommentList();
                            if (data != null) {
                                getCurrAdapterViewHelper().setData(data);
                            }
                        }
                    }
                });

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.send_btn) {
            //发送
            sendComment();
        }
    }

    private void sendComment() {
        if (commentEditText == null){
            return;
        }
        String content = commentEditText.getText().toString();
        if (content.length() == 0) {
            TipsHelper.showToast(getActivity(),getString(R.string.pls_input_comment_content));
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
        //英文写作需要传递commitTaskId
        params.put("TaskId", commitTaskId);
        params.put("Comments", content);
        params.put("CommentId", commentId);
        params.put("CommentName", commentName);
        //视情况:当不传或传0的时候为任务讨论，当传1时为英文写作的老师点评
        params.put("Type",1);
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
                            TipsHelper.showToast(getActivity(),
                                    getString(R.string.upload_comment_error));
                            return;
                        }
                        TipsHelper.showToast(getActivity(), getString(R.string.upload_comment_success));
                        UIUtils.hideSoftKeyboard1(getActivity(), commentEditText);
                        resetEditText();
                        //重新拉取一下数据
                        loadCommonData();
                    }
                });
    }

    /**
     * 重置输入框
     */
    private void resetEditText() {
        if (commentEditText != null){
            commentEditText.setText("");
            commentEditText.setHint(getString(R.string.say_something));
        }
    }
}
