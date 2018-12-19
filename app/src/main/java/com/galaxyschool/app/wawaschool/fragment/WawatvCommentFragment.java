package com.galaxyschool.app.wawaschool.fragment;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.*;

import com.alibaba.fastjson.JSON;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.Request;
import com.galaxyschool.app.wawaschool.Note.OnlineMediaPaperActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.ShellActivity;
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
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.ToolbarTopView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Author: wangchao
 * Time: 2015/11/11 11:30
 */
public class WawatvCommentFragment extends ContactsListFragment implements TextView.OnEditorActionListener {

    public static final int MAX_COMMENT_LENTH = 200;

    public static final String TAG = WawatvCommentFragment.class.getSimpleName();

    private CourseInfo courseInfo;
    private ToolbarTopView toolbarTopView;
    private PullToRefreshView pullToRefreshView;
    private EditText commentSendTxt;
    private boolean isUpdate = false;
    private boolean isPortrait;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wawatv_commet, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        handleSoftKeyBoard();
        initViews();
    }

    /**
     * 当前屏幕是横屏时设置软键盘的方式
     */
    private void handleSoftKeyBoard() {
        int orientation = getArguments().getInt(ShellActivity.EXTRA_ORIENTAION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MASK_ADJUST
                    | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadViews();
    }

    private void initViews() {

        this.courseInfo = getArguments().getParcelable(CourseInfo.class.getSimpleName());
        isPortrait = getArguments().getBoolean("isPortrait", false);
        toolbarTopView = (ToolbarTopView) findViewById(R.id.toolbartopview);
        if (toolbarTopView != null) {
            toolbarTopView.getBackView().setVisibility(View.VISIBLE);
            String commentCount = getString(R.string.n_comment, 0);
            if (courseInfo != null) {
                commentCount = getString(R.string.n_comment, courseInfo.getCommentnum());
            }
            toolbarTopView.getTitleView().setText(commentCount);
            toolbarTopView.getBackView().setOnClickListener(this);
        }

        pullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);
        final ListView listView = (ListView) findViewById(R.id.listview);
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
                    } else if (!TextUtils.isEmpty(data.getAccount())) {
                        senderName.setText(data.getAccount());
                    } else {
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
                                                        if (code == 0) {
                                                            long id = Long.parseLong(courseId);
                                                            List dataList = getCurrAdapterViewHelper().getData();
                                                            if (dataList != null && dataList.size() > 0) {
                                                                for (Object item : dataList) {
                                                                    CommentData data = (CommentData) item;
                                                                    if (data != null && id > 0 && id == data.getId()) {
                                                                        data.setPraisenum(data.getPraisenum() + 1);
                                                                        data.setIsPraise(true);
                                                                    }
                                                                }
                                                            }
                                                            getCurrAdapterViewHelper().setData(dataList);
                                                        }
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

            TextView tv_send = (TextView) findViewById(R.id.comment_send_btn);
            tv_send.setText(R.string.send);
            tv_send.setOnClickListener(this);
            commentSendTxt = (EditText) findViewById(R.id.comment_send_content);
            commentSendTxt.addTextChangedListener(new MaxLengthWatcher(MAX_COMMENT_LENTH, commentSendTxt));
            commentSendTxt.setOnEditorActionListener(this);

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
            pullToRefreshView.showRefresh();
            loadComments();
        }
    }

    private void loadComments() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("courseId", String.valueOf(courseInfo.getId()));
            jsonObject.put("pageIndex", getPageHelper().getFetchingPageIndex());
            jsonObject.put("pageSize", getPageHelper().getPageSize());
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
                pullToRefreshView.hideRefresh();
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

                                String commentCount = getString(R.string.n_comment, total);
                                toolbarTopView.getTitleView().setText(commentCount);
                                if (isUpdate) {
                                    isUpdate = false;
                                    if (courseInfo != null) {
                                        new WawaCourseUtils(getActivity()).updatePraiseCommentCount(
                                                String.valueOf(courseInfo.getId()), courseInfo.getResourceType(), -1, total);
                                    }
                                }
                            } else {
                                if (getPageHelper().getFetchingPageIndex() == 0) {
                                    String commentCount = getString(R.string.n_comment, 0);
                                    toolbarTopView.getTitleView().setText(commentCount);
                                    getCurrAdapterViewHelper().clearData();
                                }
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
                    OnlineMediaPaperActivity.setHasContentChanged(true);
                    commentSendTxt.setText("");
                    getCurrAdapterViewHelper().clearData();
                    getPageHelper().setFetchingPageIndex(0);
                    pullToRefreshView.showRefresh();
                    showLoadingDialog();
                    isUpdate = true;
                    loadComments();
                }
            }
        });
    }

//    private void sendComment(String content) {
//        UserInfo userInfo = getUserInfo();
//        if (courseInfo == null) {
//            return;
//        }
//        showLoadingDialog();
//        UIUtils.hideSoftKeyboard(getActivity());
//
//        final JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("courseId", courseInfo.getId());
//            //support anonym comment
//            try {
//                if (userInfo != null && !TextUtils.isEmpty(userInfo.getMemberId())) {
//                    jsonObject.put("account", userInfo.getNickName());
//                    jsonObject.put("createName", URLEncoder.encode(userInfo.getRealName(), "utf-8"));
//                    jsonObject.put("headPic", userInfo.getHeaderPic());
//                    jsonObject.put("memberId", userInfo.getMemberId());
//                }
//                jsonObject.put("content", URLEncoder.encode(content, "utf-8"));
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        StringBuilder builder = new StringBuilder();
//        builder.append("?j=" + jsonObject.toString());
//        String url = ServerUrl.WAWATV_CREATE_COMMENT_URL + builder.toString();
//        ThisStringRequest request = new ThisStringRequest(
//            Request.Method.GET, url, new Listener<String>() {
//            @Override
//            public void onSuccess(String jsonString) {
//                if (getActivity() == null) {
//                    return;
//                }
//                try {
//                    JSONObject dataJsonObject = new JSONObject(jsonString);
//                    if (dataJsonObject != null) {
//                        int code = dataJsonObject.optInt("code");
//                        String message = dataJsonObject.optString("message");
//                        TipMsgHelper.ShowLMsg(getActivity(), message);
//                        if (code == 0) {
//                            commentSendTxt.setText("");
//                            getCurrAdapterViewHelper().clearData();
//                            getPageHelper().setFetchingPageIndex(0);
//                            pullToRefreshView.showRefresh();
//                            showLoadingDialog();
//                            loadComments();
//                            isUpdate = true;
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onError(NetroidError error) {
////                if (getActivity() == null) {
////                    return;
////                }
////                TipMsgHelper.ShowMsg(getActivity(), getString(R.string.network_error));
//            }
//
//            @Override
//            public void onFinish() {
//                super.onFinish();
//                if (getActivity() == null) {
//                    return;
//                }
//                dismissLoadingDialog();
//            }
//        });
//        request.addHeader("Accept-Encoding", "*");
//        request.start(getActivity());
//    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.toolbar_top_back_btn) {
            if (getActivity() != null) {
                //隐藏软键盘
                hideSoftKeyboard(getActivity());
            }

            if (isPortrait) {
                if (getActivity() != null) {
                    getActivity().finish();
                }
            } else {
                FragmentManager fragmentManager = getFragmentManager();
                if (fragmentManager != null) {
                    fragmentManager.popBackStack();
                }
            }
        } else if (v.getId() == R.id.comment_send_btn) {
            String content = commentSendTxt.getText().toString().trim();
            if (TextUtils.isEmpty(content)) {
                TipMsgHelper.ShowLMsg(getActivity(), R.string.pls_input_comment_content);
                return;
            }
            if (courseInfo != null) {
                sendComment(courseInfo.getId(), content);
            }
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            String content = commentSendTxt.getText().toString().trim();
            if (TextUtils.isEmpty(content)) {
                TipMsgHelper.ShowLMsg(getActivity(), R.string.pls_input_comment_content);
                return true;
            }
            if (courseInfo != null) {
                sendComment(courseInfo.getId(), content);
            }
            return true;
        }
        return false;
    }

    public class MaxLengthWatcher implements TextWatcher {

        private int maxLen = 0;
        private EditText editText = null;


        public MaxLengthWatcher(int maxLen, EditText editText) {
            this.maxLen = maxLen;
            this.editText = editText;
        }

        public void afterTextChanged(Editable arg0) {

        }

        public void beforeTextChanged(
                CharSequence arg0, int arg1, int arg2,
                int arg3) {

        }

        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            Editable editable = editText.getText();
            int len = editable.length();

            if (len > maxLen) {
                TipMsgHelper.ShowLMsg(getActivity(), getString(R.string.n_max_comment_lenth, MAX_COMMENT_LENTH));
                int selEndIndex = Selection.getSelectionEnd(editable);
                String str = editable.toString();
                String newStr = str.substring(0, maxLen);
                editText.setText(newStr);
                editable = editText.getText();

                int newLen = editable.length();
                if (selEndIndex > newLen) {
                    selEndIndex = editable.length();
                }
                Selection.setSelection(editable, selEndIndex);

            }
        }
    }


    public void popDeleteDialog(final long id) {
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

    public void popDeleteCommentData(long id) {
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
                if (getPageHelper() != null) {
                    getPageHelper().setFetchingPageIndex(0);
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
