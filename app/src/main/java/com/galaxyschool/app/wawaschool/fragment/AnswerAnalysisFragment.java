package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.galaxyschool.app.wawaschool.AnswerParsingActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.helper.DoTaskOrderHelper;
import com.galaxyschool.app.wawaschool.pojo.AnswerAnalysisInfo;
import com.galaxyschool.app.wawaschool.pojo.ExerciseAnswerCardParam;
import com.galaxyschool.app.wawaschool.pojo.ExerciseItem;
import com.galaxyschool.app.wawaschool.pojo.LearnTaskInfo;
import com.lqwawa.client.pojo.LearnTaskCardType;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.lqwawa.tools.DensityUtils;
import com.osastudio.common.popmenu.CustomPopWindow;
import com.osastudio.common.popmenu.EntryBean;
import com.osastudio.common.popmenu.PopMenuAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnswerAnalysisFragment extends ContactsListFragment {
    public static final String TAG = AnswerAnalysisFragment.class.getSimpleName();
    private Context mContext;
    private TextView objectiveQuestionView;
    private TextView subjectQuestionView;
    private TextView questionTypeView;
    private LinearLayout llObjectQuestionDetail;
    private LinearLayout llSubjectQuestionDetail;
    private CustomPopWindow mPopWindow;
    private String[] questionTypeName;
    private String[] sortTypeData;
    private int currentQuestionType;
    private int objectiveSortType;
    private int subjectSortType;
    private ExerciseAnswerCardParam cardParam;
    private List<ExerciseItem> exerciseItems;
    private List<ExerciseItem> objectiveListData;
    private List<ExerciseItem> subjectListData;

    public interface QuestionType {
        int OBJECTIVE_QUESTION = 0;//客观题
        int SUBJECT_QUESTION = 1;//主观题
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_answer_analysis, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadIntentData();
        initView();
        loadData();
    }

    private void loadIntentData() {
        questionTypeName = mContext.getResources().getStringArray(R.array.array_question_type_name);
        sortTypeData = mContext.getResources().getStringArray(R.array.array_sort_exercise_data);
        Bundle args = getArguments();
        if (args != null) {
            cardParam = (ExerciseAnswerCardParam) args.getSerializable(ExerciseAnswerCardParam.class.getSimpleName());
            if (cardParam != null) {
                JSONArray jsonArray = JSONObject.parseArray(cardParam.getExerciseAnswerString());
                if (jsonArray != null && jsonArray.size() > 0) {
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    LearnTaskInfo learnTaskInfo = JSONObject.parseObject(jsonObject.toString(),
                            LearnTaskInfo.class);
                    if (learnTaskInfo != null) {
                        exerciseItems = learnTaskInfo.getExercise_item_list();
                    }
                }
            }
        }
    }

    private void initView() {
        objectiveQuestionView = (TextView) findViewById(R.id.tv_left);
        objectiveQuestionView.setOnClickListener(this);
        subjectQuestionView = (TextView) findViewById(R.id.tv_right);
        subjectQuestionView.setOnClickListener(this);
        questionTypeView = (TextView) findViewById(R.id.tv_sort_type);
        questionTypeView.setOnClickListener(this);
        questionTypeView.setText(sortTypeData[0]);
        llObjectQuestionDetail = (LinearLayout) findViewById(R.id.ll_object_question_type_detail);
        llSubjectQuestionDetail = (LinearLayout) findViewById(R.id.ll_subject_question_type_detail);
    }

    private void initDefaultData() {
        handlerQuestionTypeData();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.tv_sort_type) {
            popSelectSortTypeDialog();
        } else if (viewId == R.id.tv_left) {
            //客观题
            currentQuestionType = QuestionType.OBJECTIVE_QUESTION;
            handlerQuestionTypeData();
        } else if (viewId == R.id.tv_right) {
            //主观题
            currentQuestionType = QuestionType.SUBJECT_QUESTION;
            handlerQuestionTypeData();
        } else {
            super.onClick(v);
        }
    }

    private void handlerQuestionTypeData() {
        if (currentQuestionType == QuestionType.OBJECTIVE_QUESTION) {
            //客观题
            objectiveQuestionView.setTextColor(ContextCompat.getColor(mContext, R.color.text_white));
            objectiveQuestionView.setBackground(ContextCompat.getDrawable(mContext, R.drawable
                    .green_10dp_green_left_bg));
            subjectQuestionView.setTextColor(ContextCompat.getColor(mContext, R.color.text_green));
            subjectQuestionView.setBackground(ContextCompat.getDrawable(mContext, R.drawable
                    .green_10dp_green_right));
            questionTypeView.setText(sortTypeData[objectiveSortType]);
            if (llObjectQuestionDetail.getChildCount() == 0) {
                showObjectiveData();
            }
            llObjectQuestionDetail.setVisibility(View.VISIBLE);
            llSubjectQuestionDetail.setVisibility(View.GONE);
        } else {
            //主观题
            objectiveQuestionView.setTextColor(ContextCompat.getColor(mContext, R.color.text_green));
            objectiveQuestionView.setBackground(ContextCompat.getDrawable(mContext, R.drawable
                    .green_10dp_green_left));
            subjectQuestionView.setTextColor(ContextCompat.getColor(mContext, R.color.text_white));
            subjectQuestionView.setBackground(ContextCompat.getDrawable(mContext, R.drawable
                    .green_10dp_green_right_bg));
            if (subjectSortType == 0) {
                questionTypeView.setText(sortTypeData[0]);
            } else {
                questionTypeView.setText(sortTypeData[2]);
            }
            if (llSubjectQuestionDetail.getChildCount() == 0) {
                showSubjectData();
            }
            llSubjectQuestionDetail.setVisibility(View.VISIBLE);
            llObjectQuestionDetail.setVisibility(View.GONE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void popSelectSortTypeDialog() {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.pop_menu, null);
        contentView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.text_white));
        //处理popWindow 显示内容
        handleLogic(contentView);
        mPopWindow = new CustomPopWindow.PopupWindowBuilder(getContext())
                //显示的布局，还可以通过设置一个View
                .setView(contentView)
                //创建PopupWindow
                .size(DensityUtils.dp2px(getContext(), 150), LinearLayout.LayoutParams
                        .WRAP_CONTENT)
                .create();
        mPopWindow.showAsDropDown(questionTypeView,
                -DensityUtils.dp2px(mContext, 10),
                DensityUtils.dp2px(mContext, 10),
                Gravity.NO_GRAVITY);
    }

    /**
     * 处理弹出显示内容、点击事件等逻辑
     *
     * @param contentView
     */
    private void handleLogic(View contentView) {
        EntryBean data = null;
        final List<EntryBean> items = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            data = new EntryBean();
            if (i == 0) {
                data.value = sortTypeData[i];
            } else {
                if (currentQuestionType == QuestionType.OBJECTIVE_QUESTION) {
                    data.value = sortTypeData[i];
                } else {
                    data.value = sortTypeData[2];
                }
            }
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
                if (currentQuestionType == QuestionType.OBJECTIVE_QUESTION) {
                    objectiveSortType = i;
                } else {
                    subjectSortType = i;
                }
                showData(true);
                questionTypeView.setText(items.get(i).value);
            }
        });
    }

    private void loadData() {
        Map<String, Object> params = new HashMap<>();
        if (cardParam != null) {
            params.put("TaskId", cardParam.getTaskId());
            if (cardParam.isFromOnlineStudyTask()){
                //线上
                params.put("SchoolId", cardParam.getSchoolId());
                params.put("ClassId", cardParam.getClassId());
            }
        }
        DefaultDataListener listener = new DefaultDataListener<DataModelResult>(DataModelResult
                .class) {
            @Override
            public void onSuccess(String jsonString) {
                if (TextUtils.isEmpty(jsonString)) {
                    return;
                }
                try {
                    JSONObject result = JSONObject.parseObject(jsonString);
                    if (result != null) {
                        int code = result.getIntValue("ErrorCode");
                        if (code == 0) {
                            //成功
                            JSONObject modelObj = result.getJSONObject("Model");
                            if (modelObj != null) {
                                JSONArray dataListArray = modelObj.getJSONArray("DataList");
                                if (dataListArray != null && dataListArray.size() > 0) {
                                    List<AnswerAnalysisInfo> analysisInfos = JSONObject.parseArray(dataListArray.toString(), AnswerAnalysisInfo.class);
                                    if (analysisInfos != null && analysisInfos.size() > 0) {
                                        if (exerciseItems != null) {
                                            DoTaskOrderHelper.analysisAllCommitData(analysisInfos, exerciseItems);
                                            showData(false);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        RequestHelper.sendPostRequest(mContext, ServerUrl
                .LOAD_EPANSWER_ANALYSIS_BYTASKID_BASE_URL, params, listener);
    }

    private void showData(boolean changeType) {
        if (exerciseItems == null || exerciseItems.size() == 0) {
            return;
        }
        if (subjectListData == null || objectiveListData == null) {
            subjectListData = new ArrayList<>();
            objectiveListData = new ArrayList<>();
            for (int i = 0; i < exerciseItems.size(); i++) {
                int type = Integer.valueOf(exerciseItems.get(i).getType());
                if (type == LearnTaskCardType.SUBJECTIVE_PROBLEM) {
                    subjectListData.add(exerciseItems.get(i));
                } else {
                    objectiveListData.add(exerciseItems.get(i));
                }
            }
        }
        if (!changeType) {
            if (subjectListData.size() > 0 && objectiveListData.size() > 0) {
                //客观和主观题都有
                initDefaultData();
            } else if (subjectListData.size() > 0) {
                llSubjectQuestionDetail.setVisibility(View.VISIBLE);
                llObjectQuestionDetail.setVisibility(View.GONE);
                currentQuestionType = QuestionType.SUBJECT_QUESTION;
                subjectQuestionView.setTextColor(ContextCompat.getColor(mContext, R.color.text_white));
                subjectQuestionView.setBackground(ContextCompat.getDrawable(mContext, R.drawable
                        .green_20dp_green));
                subjectQuestionView.setEnabled(false);
                objectiveQuestionView.setVisibility(View.GONE);
            } else if (objectiveListData.size() > 0) {
                llObjectQuestionDetail.setVisibility(View.VISIBLE);
                llSubjectQuestionDetail.setVisibility(View.GONE);
                currentQuestionType = QuestionType.OBJECTIVE_QUESTION;
                objectiveQuestionView.setTextColor(ContextCompat.getColor(mContext, R.color.text_white));
                objectiveQuestionView.setBackground(ContextCompat.getDrawable(mContext, R.drawable
                        .green_20dp_green));
                objectiveQuestionView.setEnabled(false);
                subjectQuestionView.setVisibility(View.GONE);
            }
        }
        if (currentQuestionType == QuestionType.OBJECTIVE_QUESTION) {
            showObjectiveData();
        } else {
            showSubjectData();
        }
    }

    private void showObjectiveData() {
        llObjectQuestionDetail.removeAllViews();
        if (objectiveListData == null || objectiveListData.size() == 0) {
            return;
        }
        if (objectiveSortType == 0) {
            //题号
            Collections.sort(objectiveListData, ((o1, o2) -> Integer.valueOf(o1.getIndex()) -
                    Integer.valueOf(o2.getIndex())));
        } else {
            //错误率
            Collections.sort(objectiveListData, ((o1, o2) -> (int) (o2.getErrorRate() -
                                o1.getErrorRate())));
        }
        int index = 1;
        for (int i = 0; i < objectiveListData.size(); i++) {
            ExerciseItem itemData = objectiveListData.get(i);
            int type = Integer.valueOf(itemData.getType());
            if (type == LearnTaskCardType.SUBJECTIVE_PROBLEM) {
                continue;
            }
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_answer_card_type_detail, null);
            TextView leftTitleTextV = (TextView) view.findViewById(R.id.tv_title_data);
            TextView rightTextV = (TextView) view.findViewById(R.id.tv_right_flag);
            ImageView arrowRightImageV = (ImageView) view.findViewById(R.id.iv_arrow_right);
            arrowRightImageV.setVisibility(View.VISIBLE);
            String leftTitle = index + "." + questionTypeName[type - 1] + ": " + itemData.getName
                    () + "(" + getString(R.string.str_eval_score, Utils.changeDoubleToInt(itemData.getScore())) + ")";
            leftTitleTextV.setText(leftTitle);
            leftTitleTextV.setSingleLine(true);
            leftTitleTextV.setEllipsize(TextUtils.TruncateAt.END);
            //错误率
            rightTextV.setText(itemData.getErrorRate() + "%");
            view.setOnClickListener(v -> {
                //打开当前题的详情数据
                openQuestionDetail(Integer.valueOf(itemData.getIndex()));
            });
            llObjectQuestionDetail.addView(view);
            index++;
        }
    }

    private void showSubjectData() {
        llSubjectQuestionDetail.removeAllViews();
        if (subjectListData == null || subjectListData.size() == 0) {
            return;
        }
        if (subjectSortType == 0) {
            //题号
            Collections.sort(subjectListData, ((o1, o2) -> Integer.valueOf(o1.getIndex()) -
                    Integer.valueOf(o2.getIndex())));
        } else {
            //平均分
            Collections.sort(subjectListData, ((o1, o2) -> (int) o2.getAverageScore() - (int) o1.getAverageScore()));
        }
        int index = 1;
        for (int i = 0; i < subjectListData.size(); i++) {
            ExerciseItem itemData = subjectListData.get(i);
            int type = Integer.valueOf(itemData.getType());
            if (type == LearnTaskCardType.SUBJECTIVE_PROBLEM) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_answer_card_type_detail, null);
                TextView leftTitleTextV = (TextView) view.findViewById(R.id.tv_title_data);
                TextView rightTextV = (TextView) view.findViewById(R.id.tv_right_flag);
                ImageView arrowRightImageV = (ImageView) view.findViewById(R.id.iv_arrow_right);
                arrowRightImageV.setVisibility(View.VISIBLE);
                String leftTitle = index + "." + itemData.getType_name() + ": " + "(" + getString(R.string.str_eval_score, Utils.changeDoubleToInt(itemData
                        .getScore())) + ")";
                leftTitleTextV.setText(leftTitle);
                leftTitleTextV.setSingleLine(true);
                leftTitleTextV.setEllipsize(TextUtils.TruncateAt.END);
                rightTextV.setText(getString(R.string.str_eval_score, Utils.changeDoubleToInt(String.valueOf(itemData.getAverageScore()))));
                view.setOnClickListener(v -> {
                    //打开当前题的详情数据
                    openQuestionDetail(Integer.valueOf(itemData.getIndex()));
                });
                llSubjectQuestionDetail.addView(view);
                index++;
            }
        }
    }

    private void openQuestionDetail(int questionIndex){
        if (exerciseItems == null || exerciseItems.size() == 0){
            return;
        }
        cardParam.setQuestionDetails(exerciseItems);
        AnswerParsingActivity.start((Activity) mContext,cardParam,questionIndex,true);
    }
}
