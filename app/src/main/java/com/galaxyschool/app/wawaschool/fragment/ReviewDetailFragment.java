package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.SpeechAssessmentActivity;
import com.galaxyschool.app.wawaschool.TeacherReviewDetailActivity;
import com.galaxyschool.app.wawaschool.common.StudyTaskUtils;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.views.MyGridView;
import com.galaxyschool.app.wawaschool.views.ToolbarTopView;
import com.lqwawa.lqbaselib.pojo.MessageEvent;

import java.util.ArrayList;

/**
 * ======================================================
 * Created by : Brave_Qu on 2018/9/18 0018 15:33
 * Describe:点评的详情页（包括老师点评和系统点评）
 * ======================================================
 */
public class ReviewDetailFragment extends ContactsListFragment implements View.OnClickListener {
    public static String TAG = ReviewDetailFragment.class.getSimpleName();
    private Context mContext;
    private LinearLayout teacherEvalLayout;
    private TextView teacherEvalScoreTextV;
    private TextView systemScoreTextV;
    private LinearLayout teacherCommentLayout;
    private TextView teacherCommentTextV;
    private FrameLayout teacherReviewFl;
    private MyGridView myGridView;
    private boolean isVoiceReview;//是否已经语音评测
    private ArrayList<Integer> pageScoreList = new ArrayList<>();
    private String teachReviewScore;
    private String teacherCommentData;
    private int scoreRule;
    private int mOrientation;
    private String onlineResUrl;
    private boolean hasReviewPermission;
    private String commitTaskId;
    private String commitOnlineTaskId;
    private String systemEvalScore;


    public static ReviewDetailFragment newInstance(Bundle args) {
        ReviewDetailFragment fragment = new ReviewDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_review_detail, null);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = activity;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadIntentData();
        initViews();
        initGridView();
        addEventBusReceiver();
    }

    private void loadIntentData() {
        Bundle args = getArguments();
        if (args != null) {
            isVoiceReview = args.getBoolean(TeacherReviewDetailActivity.Constant.IS_EVAL_REVIEW);
            pageScoreList = args.getIntegerArrayList(TeacherReviewDetailActivity.Constant.PAGE_SCORE_LIST);
            teachReviewScore = args.getString(TeacherReviewDetailActivity.Constant.TEACHER_REVIEW_SCORE);
            teacherCommentData = args.getString(TeacherReviewDetailActivity.Constant
                    .TEACHER_COMMENT_DATA);
            onlineResUrl = args.getString(TeacherReviewDetailActivity.Constant.ONLINE_RESURL);
            mOrientation = args.getInt(TeacherReviewDetailActivity.Constant.ORIENTATION);
            scoreRule = args.getInt(TeacherReviewDetailActivity.Constant.SCORE_RULE);
            hasReviewPermission = args.getBoolean(TeacherReviewDetailActivity.Constant.HAS_REVIEW_PERMISSION);
            commitOnlineTaskId = args.getString(TeacherReviewDetailActivity.Constant
                    .COMMIT_TASK_ONLINE_ID);
            commitTaskId = args.getString(TeacherReviewDetailActivity.Constant.COMMIT_TASK_ID);
            if (pageScoreList != null && pageScoreList.size() > 0) {
                int allScore = 0;
                for (int i = 0, len = pageScoreList.size(); i < len; i++) {
                    allScore = pageScoreList.get(i) + allScore;
                }
                allScore = allScore / pageScoreList.size();
                if (scoreRule == 1){
                    systemEvalScore = StudyTaskUtils.percentTransformTenLevel(allScore);
                } else {
                    systemEvalScore = String.valueOf(allScore);
                }
            }
        }
    }

    private void initViews() {
        ToolbarTopView toolbarTopView = (ToolbarTopView) findViewById(R.id.toolbar_top_view);
        toolbarTopView.getBackView().setOnClickListener(this);
        toolbarTopView.getTitleView().setText(R.string.str_eval_test);
        teacherEvalLayout = (LinearLayout) findViewById(R.id.ll_teacher_eval);
        teacherEvalScoreTextV = (TextView) findViewById(R.id.tv_teacher_eval_score);
        systemScoreTextV = (TextView) findViewById(R.id.tv_system_score);
        teacherCommentLayout = (LinearLayout) findViewById(R.id.ll_teacher_comment);
        teacherCommentTextV = (TextView) findViewById(R.id.tv_teacher_comment);
        teacherReviewFl = (FrameLayout) findViewById(R.id.fl_teacher_eval);
        TextView viewDetailTextV = (TextView) findViewById(R.id.tv_view_detail);
        viewDetailTextV.setOnClickListener(this);
        TextView teacherReviewTextV = (TextView) findViewById(R.id.tv_teacher_eval);
        teacherReviewTextV.setOnClickListener(this);
        myGridView = (MyGridView) findViewById(R.id.gv_page_score_list);
        updateEvalData();
    }

    private void updateEvalData() {
        String systemContent = getString(R.string.str_eval_score, systemEvalScore);
        SpannableString systemColorString = new SpannableString(systemContent);
        systemColorString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext,R.color.red)),0,
                (systemContent.length() - 1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        systemScoreTextV.setText(systemColorString);
        if (isVoiceReview) {
            teacherReviewFl.setVisibility(View.GONE);
            teacherEvalLayout.setVisibility(View.VISIBLE);
            teacherCommentLayout.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(teachReviewScore)) {
                String scoreContent = getString(R.string.str_eval_score, teachReviewScore);
                SpannableString ss = new SpannableString(scoreContent);
                ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext,R.color.red)),0,
                        (scoreContent.length() - 1), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                teacherEvalScoreTextV.setText(ss);
            }

            if (TextUtils.isEmpty(teacherCommentData)) {
                teacherCommentTextV.setText(getString(R.string.str_eval_no_comment));
            } else {
                teacherCommentTextV.setText(teacherCommentData);
            }
        } else {
            teacherEvalLayout.setVisibility(View.GONE);
            teacherCommentLayout.setVisibility(View.GONE);
            if (hasReviewPermission) {
                teacherReviewFl.setVisibility(View.VISIBLE);
            } else {
                teacherReviewFl.setVisibility(View.GONE);
            }
        }
    }

    private void initGridView() {
        AdapterViewHelper adapterViewHelper = new AdapterViewHelper(getActivity(),
                myGridView, R.layout.item_eval_page) {
            @Override
            public void loadData() {

            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                if (view != null) {
                    if (pageScoreList == null || pageScoreList.size() == 0){
                        return view;
                    }
                    int pageScore = pageScoreList.get(position);
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = pageScore;
                    TextView pageScoreTextV = (TextView) view.findViewById(R.id.tv_page_score);
                    if (pageScoreTextV != null){
                        if (pageScore < 60){
                            pageScoreTextV.setBackgroundDrawable(ContextCompat.getDrawable
                                    (mContext,R.drawable.red_btn_bg));
                        } else {
                            pageScoreTextV.setBackgroundDrawable(ContextCompat.getDrawable
                                    (mContext,R.drawable.new_btn_bg));
                        }
                        if (scoreRule == 1){
                            pageScoreTextV.setText(StudyTaskUtils.percentTransformTenLevel(pageScore));
                        } else {
                            pageScoreTextV.setText(String.valueOf(pageScore));
                        }
                    }
                    TextView pagePositionTextV = (TextView) view.findViewById(R.id.tv_position);
                    if (pagePositionTextV != null){
                        pagePositionTextV.setText(String.valueOf(position + 1));
                    }

                    view.setTag(holder);
                }
                return view;
            }

            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {

            }
        };
        setCurrAdapterViewHelper(myGridView, adapterViewHelper);
        if (pageScoreList != null && pageScoreList.size() > 0) {
            getCurrAdapterViewHelper().setData(pageScoreList);
        }
    }


    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.toolbar_top_back_btn) {
            finish();
        } else if (viewId == R.id.tv_view_detail) {
            //查询详情
            SpeechAssessmentActivity.start((Activity) mContext, mOrientation, onlineResUrl, scoreRule);
        } else if (viewId == R.id.tv_teacher_eval) {
            //立即点评
            TeacherReviewDetailActivity.start((Activity) mContext, commitOnlineTaskId, commitTaskId, scoreRule,
                    systemEvalScore);
        }
    }

    @Override
    public void onMessageEvent(MessageEvent messageEvent) {
       if (messageEvent != null){
           Bundle  bundle = messageEvent.getBundle();
           if (bundle != null){
               teachReviewScore = bundle.getString("evalScore");
               teacherCommentData = bundle.getString("evalComment");
               if (!TextUtils.isEmpty(teachReviewScore)){
                   isVoiceReview = true;
                   updateEvalData();
               }
           }
       }
    }
}
