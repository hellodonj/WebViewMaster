package com.galaxyschool.app.wawaschool.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.AchievementStaticsItemActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.pojo.CommitTask;
import com.galaxyschool.app.wawaschool.pojo.ExerciseAnswerCardParam;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.views.ToolbarTopView;
import com.lqwawa.apps.views.DrawPointView;
import com.lqwawa.apps.views.charts.PieHelper;
import com.lqwawa.apps.views.charts.PieView;
import com.lqwawa.lqbaselib.common.DoubleOperationUtil;
import com.osastudio.common.utils.MyListView;

import java.util.ArrayList;
import java.util.List;

/**
 * ================================================
 * 作    者：Blizzard-liu
 * 版    本：1.0
 * 创建日期：2017/10/13 15:11
 * 描    述：成绩统计
 * 修订历史：
 * ================================================
 */

public class AchievementStatisticsFragment extends ContactsListFragment {
    public static final String TAG = AchievementStatisticsFragment.class.getSimpleName();
    private PieView mPieView;
    private View mEmptyView, mContentView;
    private final int[] DEFAULT_COLOR_LIST = {
            Color.parseColor("#38940a"),
            Color.parseColor("#4fd70c"),
            Color.parseColor("#b4d883"),
            Color.parseColor("#0444e7"),
            Color.parseColor("#4b8fff"),
            Color.parseColor("#9bbbf1"),
            Color.parseColor("#ede36e"),
            Color.parseColor("#f7ba17"),
            Color.parseColor("#ec7a00"),
            Color.parseColor("#ff3b3d")
    };

    private final int[] PERCENT_COLOR_LIST = {
            Color.parseColor("#76c905"),
            Color.parseColor("#38c2e0"),
            Color.parseColor("#ffe827"),
            Color.parseColor("#ff9f22"),
            Color.parseColor("#ff3b0d")
    };

    private String[] DEFAULT_TITLE_LIST = new String[5];

    private final String[] DEFAULT_LETTER_LIST = {
            "A+", "A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D"
    };
    private ArrayList<CommitTask> mData;
    private int roleType;
    private boolean isEvalAssessment;
    private int classMemberAllCount;
    private String averageScoreData;
    private String maxScoreData;
    private String minScoreData;
    private ExerciseAnswerCardParam cardParam;
    private int fullMarkScore;
    private int [] scoreArray = new int[5];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_achivement_statistics, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        initData();
    }


    private void initData() {
        if (fullMarkScore > 0){
            scoreArray[0] = (int) (fullMarkScore * 0.9);
            scoreArray[1] = (int) (fullMarkScore * 0.8);
            scoreArray[2] = (int) (fullMarkScore * 0.7);
            scoreArray[3] = (int) (fullMarkScore * 0.6);
            scoreArray[4] = (int) (fullMarkScore * 0.5);
            for (int i = 0; i < 4; i++){
                if (i == 0) {
                    DEFAULT_TITLE_LIST[i] = scoreArray[i] + "-" + fullMarkScore + getString(R.string.str_scores);
                } else {
                    DEFAULT_TITLE_LIST[i] = scoreArray[i] + "-" + (scoreArray[i-1]-1) + getString(R.string.str_scores);
                }
            }
            //答题卡的信息
            DEFAULT_TITLE_LIST[4] = getString(R.string.str_less_than) + (int)(fullMarkScore * 0.6) +
                    getString(R.string.str_scores);
        } else {
            DEFAULT_TITLE_LIST[0] = "90-100" + getString(R.string.str_scores);
            DEFAULT_TITLE_LIST[1] = "80-89" + getString(R.string.str_scores);
            DEFAULT_TITLE_LIST[2] = "70-79" + getString(R.string.str_scores);
            DEFAULT_TITLE_LIST[3] = "60-69" + getString(R.string.str_scores);
            DEFAULT_TITLE_LIST[4] = getString(R.string.str_less_than) + "60" + getString(R.string.str_scores);
        }
        loadTypeList();
    }

    private void initViews() {
        if (getArguments() != null) {
            roleType = getArguments().getInt(ActivityUtils.EXTRA_USER_ROLE_TYPE, -1);
            classMemberAllCount = getArguments().getInt(Constants.CLASS_MEMBER_ALL_COUNT);
            if (isEvalAssessment) {
                mData = getArguments().getParcelableArrayList(Constants.EVAL_DATA_LIST);
            } else {
                mData = getArguments().getParcelableArrayList(Constants.RETELL_DATA_LIST);
            }
            cardParam = (ExerciseAnswerCardParam) getArguments().getSerializable(ExerciseAnswerCardParam.class
                    .getSimpleName());
            if (cardParam != null && !TextUtils.isEmpty(cardParam.getExerciseTotalScore())){
                fullMarkScore = Integer.valueOf(cardParam.getExerciseTotalScore());
            }
        }
      /*  ToolbarTopView toolbarTopView = (ToolbarTopView) findViewById(R.id.toolbar_top_view);
        if (toolbarTopView != null) {
            toolbarTopView.getBackView().setVisibility(View.VISIBLE);
            toolbarTopView.getTitleView().setText(R.string.str_achievement_statistic);
            toolbarTopView.getBackView().setOnClickListener(this);
        }*/

        mEmptyView = findViewById(R.id.iv_empty);
        mContentView = findViewById(R.id.scrollview_container);
        if (mData == null || mData.size() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
            mContentView.setVisibility(View.GONE);
            findViewById(R.id.ll_score_layout).setVisibility(View.GONE);
            findViewById(R.id.tv_person_count).setVisibility(View.GONE);
            return;
        }
        if (fullMarkScore > 0){
            handleExerciseData();
        } else {
            handleStudyTaskScore();
        }
        TextView tvAverage = (TextView) findViewById(R.id.tv_avi);
        tvAverage.setText(getString(R.string.str_eval_score, Utils.changeDoubleToInt(averageScoreData)));

        TextView tvMax = (TextView) findViewById(R.id.tv_max);
        tvMax.setText(getString(R.string.str_eval_score, Utils.changeDoubleToInt(maxScoreData)));

        TextView tvMin = (TextView) findViewById(R.id.tv_min);
        tvMin.setText(getString(R.string.str_eval_score, Utils.changeDoubleToInt(minScoreData)));

        TextView tvCount = (TextView) findViewById(R.id.tv_person_count);
        if (cardParam != null) {
            //答题卡的满分
            findViewById(R.id.ll_full_score).setVisibility(View.VISIBLE);
            TextView fullScoreTextV = (TextView) findViewById(R.id.tv_full_score);
            if (fullScoreTextV != null) {
                fullScoreTextV.setText(getString(R.string.str_eval_score, cardParam.getExerciseTotalScore()));
            }
            tvCount.setText(getString(R.string.str_exercise_commit_number, mData.size() + "/" + classMemberAllCount));
        } else {
            tvCount.setText(getString(R.string.commit_and_all_person, mData.size() + "/" + classMemberAllCount));
        }

        //饼状图
        mPieView = (PieView) findViewById(R.id.pie_view);
//        ViewGroup.LayoutParams layoutParams = mPieView.getLayoutParams();
//        int wideth = ScreenUtils.getScreenWidth(getActivity()) - DensityUtils.dp2px(getActivity(), 50);
//        layoutParams.width =  wideth;
//        layoutParams.height =  wideth;
//        mPieView.setLayoutParams(layoutParams);

        MyListView listView = (MyListView) findViewById(R.id.listview);
        if (listView != null) {
            AdapterViewHelper helper = new AdapterViewHelper(getActivity(), listView, R.layout
                    .item_achievement_statistics) {
                @Override
                public void loadData() {
                    loadTypeList();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    ScoreItem data = (ScoreItem) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;
                    DrawPointView spotView = (DrawPointView) view.findViewById(R.id.spot_view);
                    TextView scoreRange = (TextView) view.findViewById(R.id.tv_score_range);
                    TextView tvNum = (TextView) view.findViewById(R.id.tv_num);
                    spotView.setPointColor(data.color);
                    scoreRange.setText(data.title);
                    tvNum.setText(data.num + getString(R.string.str_people));
                    ImageView arrowRightImageV = (ImageView) view.findViewById(R.id.iv_arrow_icon);
                    if (arrowRightImageV != null) {
                        //右箭头只针对老师身份显示
                        if (roleType == RoleType.ROLE_TYPE_TEACHER) {
                            arrowRightImageV.setVisibility(View.VISIBLE);
                        } else {
                            arrowRightImageV.setVisibility(View.GONE);
                        }
                    }
                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null || holder.data == null) {
                        return;
                    }
                    if (roleType != RoleType.ROLE_TYPE_TEACHER) {
                        //只要老师才可以查看学生分数人员
                        return;
                    }
                    ScoreItem data = (ScoreItem) holder.data;
                    AchievementStaticsItemActivity.start(
                            getActivity(),
                            data.data,
                            data.title,
                            data.type,
                            cardParam);
                }
            };
            setCurrAdapterViewHelper(listView, helper);
        }
    }

    private SpannableString getSpan(String str) {
        SpannableString sp = new SpannableString(str);
        ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.RED);
        ForegroundColorSpan blackSpan = new ForegroundColorSpan(Color.BLACK);

        sp.setSpan(blackSpan, 0, str.length() - 2, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        sp.setSpan(redSpan, str.length() - 2, str.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return sp;

    }


    protected void loadTypeList() {
        if (mData == null || mData.size() == 0) return;

        boolean isPercentageSystem = true;//百分制
        int subsection = 5;
        if (Utils.isContainEnglish(mData.get(0).getTaskScore())) {
            isPercentageSystem = false;//十分制
            subsection = 10;
        }

        List<ScoreItem> mediaItems = new ArrayList<ScoreItem>(subsection);//定义一个数组代表各分数段的人数
        for (int i = 0; i < subsection; i++) {
            mediaItems.add(new ScoreItem());
        }

        for (int i = 0; i < mData.size(); i++) {
            CommitTask commitTask = mData.get(i);
            String taskScore = commitTask.getTaskScore();
            if (TextUtils.isEmpty(taskScore)) {
                break;
            }
            int positon = 0;
            if (isPercentageSystem) {
                //百分制
                if (fullMarkScore > 0){
                    double score = Double.valueOf(taskScore);
                    if (score >= scoreArray[0] && score <= fullMarkScore) {
                        positon = 0;
                    } else if (score >= scoreArray[1]) {
                        positon = 1;
                    } else if (score >= scoreArray[2]) {
                        positon = 2;
                    } else if (score >= scoreArray[3]) {
                        positon = 3;
                    } else {
                        positon = 4;
                    }
                } else {
                    double score = Double.valueOf(taskScore);
                    if (score >= 90 && score <= 100) {
                        positon = 0;
                    } else if (score >= 80) {
                        positon = 1;
                    } else if (score >= 70) {
                        positon = 2;
                    } else if (score >= 60) {
                        positon = 3;
                    } else {
                        positon = 4;
                    }
                }

            } else {
                //十分制
                for (int i1 = 0; i1 < DEFAULT_LETTER_LIST.length; i1++) {
                    if (taskScore.equals(DEFAULT_LETTER_LIST[i1])) {
                        positon = i1;
                        break;
                    }
                }
            }
            if (positon < 0) {
                break;
            }
            //从大到小排
            mediaItems.get(positon).num += 1;//此分数段的人数加一
            mediaItems.get(positon).data.add(commitTask);

        }

        final ArrayList<PieHelper> pieHelperArrayList = new ArrayList<PieHelper>();

        for (int i = 0; i < mediaItems.size(); i++) {
            ScoreItem item = mediaItems.get(i);
            item.color = isPercentageSystem ? PERCENT_COLOR_LIST[i] : DEFAULT_COLOR_LIST[i];
            item.title = isPercentageSystem ? DEFAULT_TITLE_LIST[i] : DEFAULT_LETTER_LIST[i];
            if (item.num > 0) {
                pieHelperArrayList.add(new PieHelper(100f * item.num / mData.size(),
                        isPercentageSystem ? PERCENT_COLOR_LIST[i] : DEFAULT_COLOR_LIST[i]));
            }
        }

        mPieView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPieView.setDate(pieHelperArrayList);
            }
        }, 200);

        getCurrAdapterViewHelper().setData(mediaItems);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.toolbar_top_back_btn) {
//          finish();
            popStack();
        }
    }

    protected class ScoreItem {
        int type;
        int color;
        String title;
        int num;
        ArrayList<CommitTask> data = new ArrayList<>();
    }

    public static AchievementStatisticsFragment newInstance(Bundle args) {
        AchievementStatisticsFragment fragment = new AchievementStatisticsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public interface Constants {
        String RETELL_DATA_LIST = "retell_data_list";
        String EVAL_DATA_LIST = "eval_data_list";
        String DATA_TYPE = "data_type";
        String TITLE = "title";
        String HAS_EVAL_DATA = "has_eval_score";
        String CLASS_MEMBER_ALL_COUNT = "class_member_all_count";
        String SCORE_RULE = "score_rule";
    }

    private void handleStudyTaskScore() {
        boolean isPercentageSystem = true;
        if (Utils.isContainEnglish(mData.get(0).getTaskScore())) {
            isPercentageSystem = false;//十分制
        }
        double maxScore = 0;
        double minScore = 100;
        double averageScore = 0;
        double maxScorePosition = 9;
        double minScorePosition = 0;
        double averagePosition = 0;
        for (int i = 0, len = mData.size(); i < len; i++) {
            CommitTask commitTask = mData.get(i);
            if (isPercentageSystem) {
                double taskScore = Double.valueOf(commitTask.getTaskScore());
                if (maxScore < taskScore) {
                    maxScore = taskScore;
                }
                if (minScore > taskScore) {
                    minScore = taskScore;
                }
                averageScore = DoubleOperationUtil.add(averageScore,taskScore);
            } else {
                int position = getTenLevelScorePosition(commitTask.getTaskScore());
                if (maxScorePosition > position) {
                    maxScorePosition = position;
                }
                if (minScorePosition < position) {
                    minScorePosition = position;
                }
                averagePosition = DoubleOperationUtil.add(averagePosition,position);
            }
        }

        if (isPercentageSystem) {
            averageScoreData = Utils.changeDoubleToInt(String.valueOf(averageScore / mData.size()));
            maxScoreData = Utils.changeDoubleToInt(String.valueOf(maxScore));
            minScoreData = Utils.changeDoubleToInt(String.valueOf(minScore));
        } else {
            averageScoreData = DEFAULT_LETTER_LIST[(int) (averagePosition / mData.size())];
            maxScoreData = DEFAULT_LETTER_LIST[(int) maxScorePosition];
            minScoreData = DEFAULT_LETTER_LIST[(int) minScorePosition];
        }
    }

    /**
     * 分析任务单答题卡的数据
     */
    private void handleExerciseData() {
        double maxScore = 0;
        double minScore = fullMarkScore;
        double averageScore = 0;
        for (int i = 0, len = mData.size(); i < len; i++) {
            CommitTask commitTask = mData.get(i);
            double taskScore = Double.valueOf(commitTask.getTaskScore());
            if (maxScore < taskScore) {
                maxScore = taskScore;
            }
            if (minScore > taskScore) {
                minScore = taskScore;
            }
            averageScore = DoubleOperationUtil.add(averageScore,taskScore);
        }
        averageScoreData = Utils.changeDoubleToInt(String.valueOf(averageScore / mData.size()));
        maxScoreData = String.valueOf(maxScore);
        minScoreData = String.valueOf(minScore);
    }

    private int getTenLevelScorePosition(String taskScore) {
        for (int i = 0, len = DEFAULT_LETTER_LIST.length; i < len; i++) {
            if (TextUtils.equals(taskScore, DEFAULT_LETTER_LIST[i])) {
                return i;
            }
        }
        return 0;
    }

    public void setEvalAssessment(boolean isEvalAssessment) {
        this.isEvalAssessment = isEvalAssessment;
    }
}
