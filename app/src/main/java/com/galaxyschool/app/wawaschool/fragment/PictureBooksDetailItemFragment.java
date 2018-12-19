package com.galaxyschool.app.wawaschool.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
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
import com.lqwawa.lqbaselib.net.ThisStringRequest;
import com.galaxyschool.app.wawaschool.pojo.CommentData;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.lqwawa.apps.views.ContainsEmojiEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * <br/>================================================
 * <br/> 作    者：Blizzard-liu
 * <br/> 版    本：1.0
 * <br/> 创建日期：2018/1/10 17:35
 * <br/> 描    述：
 * <br/> 修订历史：
 * <br/>================================================
 */

public class PictureBooksDetailItemFragment extends ContactsListFragment {
    private static final String TAG = "PictureBooksDetailItemF";
    /**
     * 没有数据提示
     */
    private TextView noMessageTip;
    private ListView listview;
    /**
     * 简介文字
     */
    private TextView picBookBriefTextview;
    /**
     * 评论框
     */
    private ContainsEmojiEditText commentEdittext;
    /**
     * 评论发送按钮
     */
    private TextView sendTextview;
    /**
     * 包裹评论布局
     */
    private FrameLayout sendCommentLayout;
    /**
     * true: 显示评论布局; false:显示简介布局
     */
    private boolean isComment;

    private String courseId;

    private NewResourceInfo newResourceInfo;

    private String briefMsg;


    public interface Constants {
        String EXTRA_isComment = "iscomment";
        String EXTRA_COURSE_ID = "courseId";
        String NEW_RESOURCE_INFO = "NewResourceInfo";
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_picture_books_detail_item, container, false);
    }

    /**
     * @param isComment true: 显示评论布局; false:显示简介布局
     * @return
     */
    public static PictureBooksDetailItemFragment newInstance(boolean isComment,String courseId,NewResourceInfo newResourceInfo) {

        Bundle args = new Bundle();
        args.putBoolean(Constants.EXTRA_isComment, isComment);
        args.putString(Constants.EXTRA_COURSE_ID, courseId);
        args.putParcelable(Constants.NEW_RESOURCE_INFO, newResourceInfo);

        PictureBooksDetailItemFragment fragment = new PictureBooksDetailItemFragment();
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

        isComment = getArguments().getBoolean(Constants.EXTRA_isComment);
        courseId = getArguments().getString(Constants.EXTRA_COURSE_ID);
        newResourceInfo = getArguments().getParcelable(Constants.NEW_RESOURCE_INFO);

    }

    private void initView() {
        noMessageTip = (TextView) findViewById(R.id.no_message_tip);
        picBookBriefTextview = (TextView) findViewById(R.id.pic_book_brief_textview);
        sendTextview = (TextView) findViewById(R.id.send_textview);
        listview = (ListView) findViewById(R.id.listview);
        sendCommentLayout = (FrameLayout) findViewById(R.id.send_comment_layout);
        commentEdittext = (ContainsEmojiEditText) findViewById(R.id.comment_edittext);

        if (isComment) {
            //评论布局
            listview.setVisibility(View.VISIBLE);
            sendCommentLayout.setVisibility(View.GONE);
            sendTextview.setOnClickListener(this);
            initListView();
            loadComments();
        } else {
            //简介布局
            picBookBriefTextview.setVisibility(View.VISIBLE);
            updateBrief();
        }

    }

    private void initListView() {
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
                            if (commentDatas != null && commentDatas.size() > 0) {
                                if (commentDatas.size() > 5){
                                    //自取前5条数据
                                    commentDatas = commentDatas.subList(0,5);
                                }
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
                                noMessageTip.setVisibility(View.GONE);
                                listview.setVisibility(View.VISIBLE);
                            } else {
                                listview.setVisibility(View.GONE);
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
        super.onClick(v);
        switch (v.getId()) {
            case R.id.send_textview:
                sendCommet();
                break;
            default:
                break;
        }

    }

    public int getCurrentCommentNum(){
        List<CommentData> data = getCurrAdapterViewHelper().getData();
        if (data != null){
            return data.size();
        }
        return 0;
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


    /**
     * 暂无简介
     */
    public void setBriefView(String briefView) {
        briefMsg = briefView;
        if (noMessageTip == null || picBookBriefTextview == null) {
            return;
        }

        updateBrief();

    }

    private void updateBrief() {
        if (TextUtils.isEmpty(briefMsg)) {
            noMessageTip.setVisibility(View.VISIBLE);
            noMessageTip.setText(R.string.no_description);
        } else {
            noMessageTip.setVisibility(View.GONE);
            picBookBriefTextview.setVisibility(View.VISIBLE);
            picBookBriefTextview.setText(briefMsg);
        }
    }

}
