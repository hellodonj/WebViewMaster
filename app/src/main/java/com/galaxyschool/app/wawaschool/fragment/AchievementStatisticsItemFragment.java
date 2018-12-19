package com.galaxyschool.app.wawaschool.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.AnswerCardDetailActivity;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.SpeechAssessmentActivity;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.CourseOpenUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.helper.DoTaskOrderHelper;
import com.galaxyschool.app.wawaschool.pojo.CommitTask;
import com.galaxyschool.app.wawaschool.pojo.ExerciseAnswerCardParam;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.StudyTask;
import com.galaxyschool.app.wawaschool.views.RoundedImageView;
import com.galaxyschool.app.wawaschool.views.ToolbarTopView;
import com.osastudio.common.utils.MyListView;

import java.util.ArrayList;

/**
 * ================================================
 * 作    者：Blizzard-liu
 * 版    本：1.0
 * 创建日期：2017/10/13 15:11
 * 描    述：成绩统计
 * 修订历史：
 * ================================================
 */

public class AchievementStatisticsItemFragment extends ContactsListFragment {
    public static final String TAG = AchievementStatisticsItemFragment.class.getSimpleName();
    private View mEmptyView,mContentView;
    private String mTitle;
    private int mType;
    private ArrayList<CommitTask> mData;
    private ExerciseAnswerCardParam cardParam;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_achivement_statistics_item, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();

    }

    private void initViews() {
        if (getArguments() != null) {
            mTitle = getArguments().getString(AchievementStatisticsFragment.Constants.TITLE);
            mType = getArguments().getInt(AchievementStatisticsFragment.Constants.DATA_TYPE);
            mData = getArguments().getParcelableArrayList(AchievementStatisticsFragment.Constants.RETELL_DATA_LIST);
            cardParam = (ExerciseAnswerCardParam) getArguments().getSerializable(ExerciseAnswerCardParam.class
                    .getSimpleName());
        }
        ToolbarTopView toolbarTopView = (ToolbarTopView) findViewById(R.id.toolbar_top_view);
        if (toolbarTopView != null) {
            toolbarTopView.getBackView().setVisibility(View.VISIBLE);
            toolbarTopView.getTitleView().setText(mTitle);
            toolbarTopView.getBackView().setOnClickListener(this);
        }

        mEmptyView =  findViewById(R.id.iv_empty);
        if (mData == null || mData.size() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
            return;
        }

        MyListView listView = (MyListView) findViewById(R.id.listview);
        if (listView != null) {
            AdapterViewHelper helper = new AdapterViewHelper(getActivity(), listView, R.layout
                    .item_achievement_statistics_item) {
                @Override
                public void loadData() {

                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);

                    CommitTask data = (CommitTask) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;

                    RoundedImageView imageView = (RoundedImageView) view.findViewById(R.id.iv_img);
                    TextView tvName = (TextView) view.findViewById(R.id.tv_name);
                    TextView tvScore = (TextView) view.findViewById(R.id.tv_score);
                    MyApplication.getThumbnailManager(getActivity()).displayUserIconWithDefault(
                            AppSettings.getFileUrl(data.getHeadPicUrl()), imageView,
                            R.drawable.default_user_icon);
                    String taskScore = data.getTaskScore();
                    if (!Utils.isContainEnglish(taskScore)) {
                        taskScore = taskScore+getString(R.string.str_scores);
                    }
                    tvScore.setText(taskScore);
                    tvName.setText(data.getStudentName());

                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null || holder.data == null) {
                        return;
                    }
                    CommitTask data = (CommitTask) holder.data;
                    //打开微课详情页面
                    if (data.isEvalType()) {
                        SpeechAssessmentActivity.start(getActivity(),data.getScreenType(),data
                                .getStudentResUrl(),data.getScoreRule());
                    } else if (cardParam != null) {
//                        cardParam.setStudentName(data.getStudentName());
//                        cardParam.setStudentId(data.getStudentId());
//                        cardParam.setCommitTaskId(data.getCommitTaskId());
//                        //从单条纪录进入详情不给批阅和提问
//                        cardParam.setIsOnlineHost(false);
//                        cardParam.setIsOnlineReporter(false);
//                        cardParam.setRoleType(RoleType.ROLE_TYPE_VISITOR);
//                        AnswerCardDetailActivity.start(getActivity(), cardParam);
                        DoTaskOrderHelper.openExerciseDetail(
                                getActivity(),
                                cardParam.getExerciseAnswerString(),
                                cardParam.getTaskId(),
                                data.getStudentId(),
                                cardParam.getResId(),
                                cardParam.getCommitTaskTitle(),
                                null,
                                null,
                                null,
                                null,
                                data.getStudentName(),
                                data.getCommitTaskId(),
                                cardParam.isFromOnlineStudyTask(),
                                false);
                    } else {
                        StudyTask myTask = new StudyTask();
                        myTask.setResId(data.getStudentResId());
                        Bundle arguments = new Bundle();
                        arguments.putBoolean(ActivityUtils.EXTRA_IS_NEED_HIDE_COLLECT_BTN, false);
                        CourseOpenUtils.openCourseDetailsDirectly(getActivity(),
                                myTask, RoleType.ROLE_TYPE_TEACHER, getMemeberId(), data.getStudentId(), null,
                                false, arguments);
                    }
                }
            };
            setCurrAdapterViewHelper(listView, helper);
            getCurrAdapterViewHelper().setData(mData);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.toolbar_top_back_btn) {
            finish();
        }
    }


    public static AchievementStatisticsItemFragment newInstance(ArrayList<CommitTask> data ,String title ,int type) {

        Bundle args = new Bundle();
        args.putParcelableArrayList(AchievementStatisticsFragment.Constants.RETELL_DATA_LIST,data);
        args.putString(AchievementStatisticsFragment.Constants.TITLE,title);
        args.putInt(AchievementStatisticsFragment.Constants.DATA_TYPE,type);
        AchievementStatisticsItemFragment fragment = new AchievementStatisticsItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
