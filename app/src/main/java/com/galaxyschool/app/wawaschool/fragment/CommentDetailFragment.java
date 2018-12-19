package com.galaxyschool.app.wawaschool.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.Request;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.WawaCourseUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.pojo.CommentData;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.lqwawa.apps.views.ContainsEmojiEditText;
import com.lqwawa.lqbaselib.net.ThisStringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;

public class CommentDetailFragment extends ContactsListFragment {
    public static String TAG = CommentDetailFragment.class.getSimpleName();
    private TextView noMessageTip;
    private ListView listview;
    /**
     * 评论框
     */
    private ContainsEmojiEditText commentEdittext;
    PullToRefreshView pullToRefreshView;
    /**
     * 评论发送按钮
     */
    private TextView sendTextview;
    private String courseId;

    private NewResourceInfo newResourceInfo;

    public interface Constants {
        String EXTRA_COURSE_ID = "courseId";
        String NEW_RESOURCE_INFO = "NewResourceInfo";
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_comment_detail_layout, container, false);
    }

    public static CommentDetailFragment newInstance(String courseId, NewResourceInfo newResourceInfo) {

        Bundle args = new Bundle();
        args.putString(Constants.EXTRA_COURSE_ID, courseId);
        args.putParcelable(Constants.NEW_RESOURCE_INFO, newResourceInfo);

        CommentDetailFragment fragment = new CommentDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadIntentData();
        initView();
    }

    private void loadIntentData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            courseId = bundle.getString(Constants.EXTRA_COURSE_ID);
            newResourceInfo = bundle.getParcelable(Constants.NEW_RESOURCE_INFO);
        }
    }

    private void initView() {
        TextView headTitleTextV = (TextView) findViewById(R.id.contacts_header_title);
        if (headTitleTextV != null){
            headTitleTextV.setText(getString(R.string.cs_comment));
        }
        ImageView leftImageView = (ImageView) findViewById(R.id.contacts_header_left_btn);
        if (leftImageView != null){
            leftImageView.setOnClickListener(this);
        }
        noMessageTip = (TextView) findViewById(R.id.no_message_tip);
        sendTextview = (TextView) findViewById(R.id.send_textview);
        listview = (ListView) findViewById(R.id.listview);
        commentEdittext = (ContainsEmojiEditText) findViewById(R.id.comment_edittext);
        //评论布局
        listview.setVisibility(View.VISIBLE);
        sendTextview.setOnClickListener(this);
        initListView();
        loadComments();
    }

    private void initListView() {
        pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        setStopPullUpState(true);
        setPullToRefreshView(pullToRefreshView);
        AdapterViewHelper helper = new AdapterViewHelper(
                getActivity(), listview, R.layout
                .comment_detail_list_item) {
            @Override
            public void loadData() {
                loadComments();
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final View view = super.getView(position, convertView, parent);
                final CommentData data = (CommentData) getDataAdapter().getItem(position);
                if (data == null) {
                    return view;
                }
                ViewHolder holder = (ViewHolder) view.getTag();
                if (holder == null) {
                    holder = new ViewHolder();
                }

                ImageView senderIcon = (ImageView) view.findViewById(R.id.comment_sender_icon);
                //点击头像进入个人详情
                senderIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //游客之类的memberId为空的不给点击。
                        if (!TextUtils.isEmpty(data.getMemberid())) {
                            ActivityUtils.enterPersonalSpace(getActivity(), data.getMemberid());
                        }
                    }
                });
                TextView senderName = (TextView) view.findViewById(R.id.comment_sender_name);
                final TextView commentContent = (TextView) view.findViewById(R.id.comment_content);
                TextView commentDate = (TextView) view.findViewById(R.id.comment_date);
                if (!TextUtils.isEmpty(data.getHeadpic())) {
                    getThumbnailManager().displayThumbnail(AppSettings.getFileUrl(data.getHeadpic()), senderIcon);
                } else {
                    senderIcon.setImageResource(R.drawable.comment_default_user_ico);
                }
                if (!TextUtils.isEmpty(data.getCreatename())) {
                    senderName.setText(data.getCreatename());
                } else if (!TextUtils.isEmpty(data.getAccount())) {
                    senderName.setText(data.getAccount());
                } else {
                    senderName.setText(getString(R.string.anonym));
                }
                commentContent.setText(data.getQuestion());
                commentDate.setText(data.getCreatetime());
                view.setTag(holder);
                return view;
            }

            @Override
            public void onItemClick(AdapterView parent, View convertView, int position, long id) {

            }
        };
        setCurrAdapterViewHelper(listview, helper);
    }

    public void loadComments() {
        JSONObject jsonObject = new JSONObject();
        try {
            if (TextUtils.isEmpty(courseId)) {
                jsonObject.put("courseId", newResourceInfo.getMicroId());
            } else {
                jsonObject.put("courseId", courseId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringBuilder builder = new StringBuilder();
        builder.append("?j=" + jsonObject.toString());
        String url = ServerUrl.WAWATV_COMMENT_LIST_URL + builder.toString();
        ThisStringRequest request = new ThisStringRequest(
                Request.Method.GET, url, new Listener<String>() {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                parseComments(jsonString);
            }

            @Override
            public void onError(NetroidError error) {
                if (getActivity() == null) {
                    return;
                }
                TipMsgHelper.ShowMsg(getActivity(), getString(R.string.network_error));
            }

            @Override
            public void onFinish() {
                if (getActivity() == null) {
                    return;
                }
                super.onFinish();
                dismissLoadingDialog();
            }
        });
        request.addHeader("Accept-Encoding", "*");
        request.start(getActivity());
    }

    private void parseComments(String jsonString) {
        if (jsonString != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                if (jsonObject != null) {
                    int code = jsonObject.optInt("code");
                    if (code == 0) {
                        getPageHelper().updateTotalCountByJsonString(jsonString);
                        JSONArray jsonArray = jsonObject.optJSONArray("data");
                        int total = jsonObject.optInt("total");
                        if (jsonArray != null) {
                            List<CommentData> commentDatas = JSON.parseArray(
                                    jsonArray.toString(), CommentData.class);
                            PictureBooksDetailFragment pictureBooksDetailFragment = (PictureBooksDetailFragment) getActivity()
                                    .getSupportFragmentManager().findFragmentByTag(PictureBooksDetailFragment.TAG);
                            TaskOrderDetailFragment taskOrderDetailFragment = (TaskOrderDetailFragment) getActivity()
                                    .getSupportFragmentManager().findFragmentByTag(TaskOrderDetailFragment.TAG);
                            if (commentDatas != null && commentDatas.size() > 0) {
                                getPageHelper().setCurrPageIndex(getPageHelper().getFetchingPageIndex());
                                if (getPageHelper().getFetchingPageIndex() == 0) {
                                    getCurrAdapterViewHelper().clearData();
                                }
                                if (getCurrAdapterViewHelper().hasData()) {
                                    getCurrAdapterViewHelper().getData().addAll(commentDatas);
                                    getCurrAdapterViewHelper().update();
                                } else {
                                    getCurrAdapterViewHelper().setData(commentDatas);
                                }
                                if (pictureBooksDetailFragment != null) {
                                    pictureBooksDetailFragment.setCommentNum(total);
                                }
                                if (taskOrderDetailFragment != null) {
                                    taskOrderDetailFragment.setCommentNum(total);
                                }
                                noMessageTip.setVisibility(View.GONE);
                                pullToRefreshView.setVisibility(View.VISIBLE);
                            } else {
                                pullToRefreshView.setVisibility(View.GONE);
                                noMessageTip.setText(R.string.no_comment);
                                noMessageTip.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_textview:
                sendCommet();
                break;
            case R.id.contacts_header_left_btn:
                popStack();
            default:
                break;
        }
    }

    /**
     * 发送评论
     */
    private void sendCommet() {
        String content = commentEdittext.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            TipMsgHelper.ShowLMsg(getActivity(), R.string.pls_input_comment_content);
            return;
        }
        if (newResourceInfo != null) {
            sendComment(Long.parseLong(newResourceInfo.getMicroId()), content);
        }


    }

    private void sendComment(long resId, String content) {
        WawaCourseUtils wawaCourseUtils = new WawaCourseUtils(getActivity());
        wawaCourseUtils.sendComment(resId, content);
        wawaCourseUtils.setOnCommentSendFinishListener(new WawaCourseUtils.OnCommentSendFinishListener() {
            @Override
            public void onCommentSendFinish(int code) {
                if (code == 0) {
                    //设置已经评论过
                    PictureBooksDetailFragment.setHasCommented(true);
                    commentEdittext.setText("");
                    showLoadingDialog();
                    loadComments();
                }
            }
        });
    }
}
