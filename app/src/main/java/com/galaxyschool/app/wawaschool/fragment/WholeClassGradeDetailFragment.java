package com.galaxyschool.app.wawaschool.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.pojo.CommitTask;
import com.galaxyschool.app.wawaschool.pojo.ExerciseAnswerCardParam;
import com.osastudio.common.utils.MyListView;
import java.util.ArrayList;

public class WholeClassGradeDetailFragment extends ContactsListFragment {
    public static final String TAG = WholeClassGradeDetailFragment.class.getSimpleName();
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
    private ExerciseAnswerCardParam cardParam;
    private int fullMarkScore;
    private int [] scoreArray = new int[5];
    private boolean isPercentageSystem = true;//百分制
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_whole_class_grade_detail, null);
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
        mEmptyView = findViewById(R.id.iv_empty);
        mContentView = findViewById(R.id.scrollview_container);
        if (mData == null || mData.size() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
            mContentView.setVisibility(View.GONE);
            return;
        }
        MyListView listView = (MyListView) findViewById(R.id.listview);
        if (listView != null) {
            AdapterViewHelper helper = new AdapterViewHelper(getActivity(), listView, R.layout
                    .item_whole_score_detail) {

                @Override
                public void loadData() {
                    loadTypeList();
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
                    ImageView imageView = (ImageView) view.findViewById(R.id.iv_user_icon);
                    MyApplication.getThumbnailManager(getActivity()).displayImageWithDefault(AppSettings.getFileUrl(data.getHeadPicUrl()),imageView,R.drawable.default_user_icon);
                    TextView userNameView = (TextView) view.findViewById(R.id.tv_user_name);
                    userNameView.setText(data.getStudentName());
                    TextView scoreView = (TextView) view.findViewById(R.id.tv_student_score);
                    if (isPercentageSystem){
                        //百分制
                        scoreView.setText(getString(R.string.str_eval_score, data.getTaskScore()));
                    } else {
                        //十分制
                        scoreView.setText(data.getTaskScore());
                    }
                    scoreView.setTextColor(data.getScoreColor());
                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null || holder.data == null) {
                        return;
                    }
                }
            };
            setCurrAdapterViewHelper(listView, helper);
        }
    }

    protected void loadTypeList() {
        if (mData == null || mData.size() == 0) return;
        if (Utils.isContainEnglish(mData.get(0).getTaskScore())) {
            isPercentageSystem = false;//十分制
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
            int color = isPercentageSystem ? PERCENT_COLOR_LIST[positon] : DEFAULT_COLOR_LIST[positon];
            commitTask.setScoreColor(color);
        }

        getCurrAdapterViewHelper().setData(mData);
    }

    public static WholeClassGradeDetailFragment newInstance(Bundle args) {
        WholeClassGradeDetailFragment fragment = new WholeClassGradeDetailFragment();
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

    public void setEvalAssessment(boolean isEvalAssessment) {
        this.isEvalAssessment = isEvalAssessment;
    }
}
