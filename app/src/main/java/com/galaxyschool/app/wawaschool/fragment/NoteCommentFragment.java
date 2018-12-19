package com.galaxyschool.app.wawaschool.fragment;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
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
import com.galaxyschool.app.wawaschool.Note.OnlineMediaPaperActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.WawaCourseUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.lqwawa.lqbaselib.net.ThisStringRequest;
import com.galaxyschool.app.wawaschool.pojo.CommentData;
import com.galaxyschool.app.wawaschool.pojo.CourseInfo;
import com.galaxyschool.app.wawaschool.views.MyListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Author: wangchao
 * Time: 2015/11/11 11:30
 */
public class NoteCommentFragment extends ContactsListFragment {

    public static final String TAG = NoteCommentFragment.class.getSimpleName();

    private CourseInfo courseInfo;
    private boolean isUpdate = false;
    private TextView moreCommentsBtn;
    private View titleView;

    @Override
    public View onCreateView(
        LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_note_commet, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadViews();
    }

    private void initViews() {
        TextView tv_send_comment= (TextView) findViewById(R.id.tv_send_comment);
        tv_send_comment.setText(R.string.cs_comment);
        this.courseInfo = getArguments().getParcelable(CourseInfo.class.getSimpleName());
        final ListView listView = (MyListView) findViewById(R.id.listview);
        if (listView != null) {
            AdapterViewHelper helper = new AdapterViewHelper(
                getActivity(), listView, R.layout
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
                    TextView commentPraise = (TextView) view.findViewById(R.id.comment_praise);
                    commentPraise.setVisibility(View.VISIBLE);

                    if (!TextUtils.isEmpty(data.getHeadpic())) {
                        getThumbnailManager().displayThumbnail(AppSettings.getFileUrl(data.getHeadpic()), senderIcon);
                    } else {
                        senderIcon.setImageResource(R.drawable.comment_default_user_ico);
                    }
                    //真实姓名优先，没有真实姓名显示账户名
                    if (!TextUtils.isEmpty(data.getCreatename())) {
                        senderName.setText(data.getCreatename());
                    } else if (!TextUtils.isEmpty(data.getAccount())){
                        senderName.setText(data.getAccount());
                    }else {
                        senderName.setText(getString(R.string.anonym));
                    }
                    commentContent.setText(data.getQuestion());
                    commentDate.setText(data.getCreatetime());
                    TextView deleteTextV = (TextView) view.findViewById(R.id.tv_delete_comment);
                    if (deleteTextV != null && courseInfo != null){
                        if (hasDeleteCommentPmn(data)){
                            deleteTextV.setVisibility(View.VISIBLE);
                        } else {
                            deleteTextV.setVisibility(View.GONE);
                        }
                        deleteTextV.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                popDeleteDialog(data.getId());
                            }
                        });
                    }
                    commentPraise.setText(String.valueOf(data.getPraisenum()));
                    commentPraise.setTag(data);
                    commentPraise.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CommentData commentData = (CommentData) v.getTag();
                                if (commentData != null) {
                                    if (commentData.isPraise()) {
                                        TipMsgHelper.ShowMsg(getActivity(), R.string.have_praised);
                                        return;
                                    }
                                    WawaCourseUtils wawaCourseUtils = new WawaCourseUtils(getActivity());
                                    wawaCourseUtils.setOnCoursePraiseFinishListener(
                                        new WawaCourseUtils.OnCoursePraiseFinishListener() {
                                            @Override
                                            public void onCoursePraiseFinish(String courseId, int code, int praiseNum) {
                                                if(code != 0) {
                                                    return;
                                                }
                                                List dataList = getCurrAdapterViewHelper().getData();
                                                if (dataList != null && dataList.size() > 0) {
                                                    for (Object item : dataList) {
                                                        CommentData data = (CommentData) item;
                                                        long id = Long.parseLong(courseId);
                                                        if (data != null && id > 0 && id == data.getId()) {
                                                            data.setPraisenum(data.getPraisenum() + 1);
                                                            data.setIsPraise(true);
                                                        }
                                                    }
                                                }
                                                getCurrAdapterViewHelper().setData(dataList);
                                            }
                                        });
                                    wawaCourseUtils.praiseCourse(String.valueOf(commentData.getId()), 1, 0);
                                }
                            }
                        });

                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View convertView, int position, long id) {

                }
            };
            setCurrAdapterViewHelper(listView, helper);

            titleView = findViewById(R.id.title_layout);
            moreCommentsBtn = (TextView)findViewById(R.id.more_commets_btn);
            moreCommentsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(courseInfo != null) {
                        ActivityUtils.openCommentList(getActivity(), courseInfo, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    }
                }
            });

        }
    }

    private boolean hasDeleteCommentPmn(CommentData data){
        boolean hasPermission = false;
        if (courseInfo.getResourceType() == ResType.RES_TYPE_NOTE){
            //只有帖子才显示删除的权限
            if (TextUtils.equals(getMemeberId(),courseInfo.getCode())){
                hasPermission = true;
            } else if (TextUtils.equals(getMemeberId(),data.getMemberid())){
                hasPermission = true;
            } else if (courseInfo.isOnlineSchool() && courseInfo.isTeacher()){
                hasPermission = true;
            }
        }
        return hasPermission;
    }

    private void loadViews() {
        if (getCurrAdapterViewHelper().hasData()) {
            getCurrAdapterViewHelper().update();
        } else {
            loadComments();
        }
    }


    private void loadComments() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("courseId", String.valueOf(courseInfo.getId()));
            jsonObject.put("pageIndex", 0);
            jsonObject.put("pageSize", 3);
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

                                if (isUpdate) {
                                    isUpdate = false;
                                    if (courseInfo != null) {
                                        new WawaCourseUtils(getActivity()).updatePraiseCommentCount(
                                            String.valueOf(
                                                courseInfo
                                                    .getId()), courseInfo.getResourceType(), -1, total);
                                    }
                                }
                                titleView.setVisibility(View.VISIBLE);
                                moreCommentsBtn.setVisibility(View.VISIBLE);
                            } else {
                                if (getPageHelper().getFetchingPageIndex() == 0) {
                                    getCurrAdapterViewHelper().clearData();
                                }
                                titleView.setVisibility(View.GONE);
                                moreCommentsBtn.setVisibility(View.GONE);
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateComments() {
        getCurrAdapterViewHelper().clearData();
        loadComments();
        isUpdate = true;
    }


    public void popDeleteDialog(final long id){
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(
                getActivity(),
                null,
                getString(R.string.str_confirm_delete_comment),
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
                       popDeleteCommentData(id);
                    }
                });
        messageDialog.show();
    }

    public void popDeleteCommentData(long id){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            if (!TextUtils.isEmpty(getMemeberId())) {
                jsonObject.put("memberId", getMemeberId());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringBuilder builder = new StringBuilder();
        builder.append("?j=" + jsonObject.toString());
        String url = ServerUrl.DELETE_COMMENT_DATA_BASE_URL + builder.toString();
        ThisStringRequest request = new ThisStringRequest(
                Request.Method.GET, url, new Listener<String>() {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                OnlineMediaPaperActivity.setHasContentChanged(true);
               loadComments();
            }

            @Override
            public void onError(NetroidError error) {
                if (getActivity() == null) {
                    return;
                }
                TipMsgHelper.ShowMsg(getActivity(), getString(R.string.network_error));
            }
        });
        request.addHeader("Accept-Encoding", "*");
        request.start(getActivity());
    }
}
