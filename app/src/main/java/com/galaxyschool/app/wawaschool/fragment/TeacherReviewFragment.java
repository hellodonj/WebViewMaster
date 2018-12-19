package com.galaxyschool.app.wawaschool.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.ArrayMap;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.TeacherReviewDetailActivity;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.views.ToolbarTopView;
import com.lqwawa.apps.views.ContainsEmojiEditText;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.lqwawa.lqbaselib.pojo.MessageEvent;
import com.lqwawa.tools.DensityUtils;
import com.osastudio.common.popmenu.CustomPopWindow;
import com.osastudio.common.popmenu.EntryBean;
import com.osastudio.common.popmenu.PopMenuAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ======================================================
 * Created by : Brave_Qu on 2018/9/18 0018 15:33
 * Describe:老师点评
 * ======================================================
 */
public class TeacherReviewFragment extends ContactsListFragment implements View.OnClickListener {
    public static String TAG = TeacherReviewFragment.class.getSimpleName();
    private TextView systemScoreTextV;
    private EditText inputScore;
    private TextView tenLevelTextV;
    private LinearLayout tenLevelFl;
    private ContainsEmojiEditText commentText;
    private TextView fullMarkHintTextV;
    private Context mContext;
    private String commitTaskId;
    private String commitTaskOnlineId;
    private int scoreRule;
    private String evalScore;
    String[] defaultTenLevel = {"A+", "A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D"};
    private CustomPopWindow mPopWindow;

    public static TeacherReviewFragment newInstance(Bundle args) {
        TeacherReviewFragment fragment = new TeacherReviewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_teacher_review, null);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = activity;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadData();
        initViews();
    }

    private void loadData() {
        Bundle args = getArguments();
        if (args != null) {
            commitTaskId = args.getString(TeacherReviewDetailActivity.Constant.COMMIT_TASK_ID);
            commitTaskOnlineId = args.getString(TeacherReviewDetailActivity.Constant.COMMIT_TASK_ONLINE_ID);
            scoreRule = args.getInt(TeacherReviewDetailActivity.Constant.SCORE_RULE, 2);
            evalScore = args.getString(TeacherReviewDetailActivity.Constant.EVAL_SCORE);
        }
    }

    @SuppressLint("StringFormatInvalid")
    private void initViews() {
        ToolbarTopView toolbarTopView = (ToolbarTopView) findViewById(R.id.toolbar_top_view);
        toolbarTopView.getBackView().setOnClickListener(this);
        toolbarTopView.getTitleView().setText(R.string.str_teacher_review);
        toolbarTopView.getCommitView().setText(getString(R.string.commit));
        toolbarTopView.getCommitView().setVisibility(View.VISIBLE);
        toolbarTopView.getCommitView().setOnClickListener(this);
        toolbarTopView.getCommitView().setTextColor(ContextCompat.getColor(mContext, R.color.text_green));
        systemScoreTextV = (TextView) findViewById(R.id.iv_system_score);
        if (TextUtils.isEmpty(evalScore)) {
            evalScore = "0";
        }
        String systemContent = getString(R.string.str_eval_score, evalScore);
        SpannableString systemColorString = new SpannableString(systemContent);
        systemColorString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.red)), 0,
                (systemContent.length() - 1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        systemScoreTextV.setText(systemColorString);
        inputScore = (EditText) findViewById(R.id.et_teacher_review_score);
        commentText = (ContainsEmojiEditText) findViewById(R.id.et_teacher_comment);
        tenLevelFl = (LinearLayout) findViewById(R.id.fl_percent_score);
        tenLevelTextV = (TextView) findViewById(R.id.tv_ten_level_score);
        fullMarkHintTextV = (TextView) findViewById(R.id.tv_full_mark_hint);
        if (scoreRule == 2) {
            fullMarkHintTextV.setVisibility(View.VISIBLE);
            inputScore.setVisibility(View.VISIBLE);
            tenLevelFl.setVisibility(View.GONE);
        } else {
            fullMarkHintTextV.setVisibility(View.GONE);
            inputScore.setVisibility(View.GONE);
            tenLevelFl.setOnClickListener(this);
            tenLevelFl.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.toolbar_top_back_btn) {
            finish();
        } else if (viewId == R.id.toolbar_top_commit_btn) {
            //提交
            commitEvalSore();
        } else if (viewId == R.id.fl_percent_score) {
            //输入分数
            showPopWindowCheckScore();
        }
    }

    private void showPopWindowCheckScore() {
        if (mPopWindow == null) {
            View contentView = LayoutInflater.from(getContext()).inflate(R.layout.pop_menu, null);
            contentView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.main_bg_color));
            //处理popWindow 显示内容
            handleLogic(contentView);
            mPopWindow = new CustomPopWindow.PopupWindowBuilder(getContext())
                    //显示的布局，还可以通过设置一个View
                    .setView(contentView)
                    //创建PopupWindow
                    .size(DensityUtils.dp2px(getContext(), 100), LinearLayout.LayoutParams
                            .WRAP_CONTENT)
                    .create();
        }
        mPopWindow.showAsDropDown(tenLevelFl);
    }

    /**
     * 处理弹出显示内容、点击事件等逻辑
     *
     * @param contentView
     */
    private void handleLogic(View contentView) {
        EntryBean data = null;
        final List<EntryBean> items = new ArrayList<>();
        for (int i = 0; i < defaultTenLevel.length; i++) {
            data = new EntryBean();
            data.value = defaultTenLevel[i];
            items.add(data);
        }
        if (items.size() <= 0) {
            return;
        }
        ListView myListView = (ListView) contentView.findViewById(R.id.pop_menu_list);
        myListView.setVisibility(View.GONE);
        ListView listView = (ListView) contentView.findViewById(R.id.lv_listview);
        listView.setVisibility(View.VISIBLE);
        listView.setDivider(new ColorDrawable(ContextCompat.getColor(mContext, R.color.line_gray)));
        listView.setDividerHeight(1);
        PopMenuAdapter adapter = new PopMenuAdapter(getContext(), items);
        adapter.setTitleLeftShow(true);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mPopWindow != null) {
                    mPopWindow.dissmiss();
                }
                tenLevelTextV.setText(items.get(i).value);
            }
        });
    }


    private void commitEvalSore() {
        String scoreText = null;
        if (scoreRule == 2) {
            scoreText = inputScore.getText().toString();
        } else {
            scoreText = tenLevelTextV.getText().toString();
        }
        if (TextUtils.isEmpty(scoreText)) {
            TipsHelper.showToast(mContext, R.string.str_score_not_null);
            return;
        }
        if (scoreRule == 2) {
            int scoreData = Integer.valueOf(scoreText);
            if (scoreData > 100){
                TipsHelper.showToast(mContext, R.string.str_score_more_than_hundred);
                return;
            }
            scoreText = String.valueOf(scoreData);
        }
        commitTeacherMark(scoreText);
    }

    private void commitTeacherMark(final String score) {
        Map<String, Object> params = new ArrayMap<>();
        if (TextUtils.isEmpty(commitTaskId)) {
            params.put("CommitTaskOnlineId", Integer.valueOf(commitTaskOnlineId));
        } else {
            params.put("CommitTaskId", Integer.valueOf(commitTaskId));
        }
        params.put("IsTeacher", true);
        params.put("CreateId", getMemeberId());
        params.put("TaskScore", score);
        params.put("ResId", "");
        params.put("ResUrl", "");
        params.put("IsVoiceReview", true);
        params.put("TaskScoreRemark", commentText.getText().toString().trim());
        RequestHelper.sendPostRequest(mContext, ServerUrl.GET_ADDCOMMITTASKREVIEW,
                params, new RequestHelper.RequestDataResultListener<DataModelResult>(mContext,
                        DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        super.onSuccess(jsonString);
                        DataModelResult result = getResult();
                        if (result == null || !result.isSuccess()) {
                            dismissLoadingDialog();
                            return;
                        }
                        TipMsgHelper.ShowLMsg(mContext, R.string.commit_success);
                        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getContext());
                        broadcastManager.sendBroadcast(new Intent(EvalHomeworkListFragment.ACTION_MARK_SCORE));
                        Bundle args = new Bundle();
                        args.putString("evalScore",score);
                        args.putString("evalComment",commentText.getText().toString().trim());
                        EventBus.getDefault().post(new MessageEvent(args,"update_data"));
                        finish();
                    }
                });
    }
}
