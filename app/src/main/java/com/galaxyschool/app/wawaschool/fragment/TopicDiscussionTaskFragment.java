package com.galaxyschool.app.wawaschool.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.common.UploadReourceHelper;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.ScoreFormula;
import com.galaxyschool.app.wawaschool.pojo.ScoreFormulaListResult;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;
import com.galaxyschool.app.wawaschool.pojo.UploadParameter;
import com.galaxyschool.app.wawaschool.views.SelectBindChildPopupView;
import com.galaxyschool.app.wawaschool.views.ToolbarTopView;
import com.lqwawa.apps.views.ContainsEmojiEditText;

import java.util.List;

/**
 * Created by Administrator on 2016/6/15.
 */
public class TopicDiscussionTaskFragment extends ContactsListFragment implements View.OnClickListener,
        SelectBindChildPopupView.OnRelationChangeListener {

    public static final String TAG = TopicDiscussionFragment.class.getSimpleName();

    public static final int TITLE_MAX_LEN = 40;
    public static final int DESCRIPTION_MAX_LEN = 500;

    private ContainsEmojiEditText titleView;
    private ContainsEmojiEditText descriptionView,limitWordFrom,limitWordTo;

    private String headerTitle;

    private int taskType;
    private TextView scoreFormule;
    private View mRootView;
    //打分公式弹出来之后需要的参数
    private int position=0;
    String [] childNameArray;
    //放置打分班级的Id
    private int [] childFormulaID;
    private int markFormulaId;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView=inflater.inflate(R.layout.fragment_topic_discussion_task, null);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getIntent();
        initViews();
    }
    private void getIntent(){
        taskType=getArguments().getInt(ActivityUtils.EXTRA_TASK_TYPE);
        if (taskType==StudyTaskType.ENGLISH_WRITING){
            TextView textView= (TextView) findViewById(R.id.title);
            textView.setText(getString(R.string.article_title));
            textView= (TextView) findViewById(R.id.content);
            textView.setText(getString(R.string.article_request));
            findViewById(R.id.layout_score_formula).setVisibility(View.VISIBLE);
            scoreFormule= (TextView) findViewById(R.id.score_formula_click);
            scoreFormule.setOnClickListener(this);
            //打分公式
            scoreFormule= (TextView) findViewById(R.id.score_formula_click);
            scoreFormule.setOnClickListener(this);
            findViewById(R.id.article_limit_layout).setVisibility(View.VISIBLE);
            //字数限制
            limitWordFrom= (ContainsEmojiEditText) findViewById(R.id.limit_from);
            limitWordTo= (ContainsEmojiEditText) findViewById(R.id.limit_to);
            loadScoreFormulaData();
        }
    }
    private void initViews() {
        if (getArguments() != null) {
            headerTitle = getArguments().getString(ActivityUtils.EXTRA_HEADER_TITLE);
        }
        ToolbarTopView toolbarTopView = (ToolbarTopView) findViewById(R.id.toolbar_top_view);
        toolbarTopView.getBackView().setVisibility(View.VISIBLE);
        toolbarTopView.getTitleView().setText(headerTitle);
        toolbarTopView.getCommitView().setVisibility(View.VISIBLE);
        int textColor = getResources().getColor(R.color.text_green);
        toolbarTopView.getCommitView().setTextColor(textColor);
        toolbarTopView.getCommitView().setText(R.string.confirm);
        toolbarTopView.getBackView().setOnClickListener(this);
        toolbarTopView.getCommitView().setOnClickListener(this);

        titleView = (ContainsEmojiEditText) findViewById(R.id.topic_discussion_title_text);
        descriptionView = (ContainsEmojiEditText) findViewById(R.id.topic_discussion_description_text);

        titleView.setMaxlen(TITLE_MAX_LEN);
        descriptionView.setMaxlen(DESCRIPTION_MAX_LEN);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.toolbar_top_back_btn) {
            popStack();
        }else if (v.getId()==R.id.score_formula_click) {
            checkScoreFormula();
        } else if (v.getId() == R.id.toolbar_top_commit_btn) {
            UploadParameter uploadParameter = UploadReourceHelper.getUploadParameter(getUserInfo(), null, null, null, 1);
            if (uploadParameter != null) {
                String title = titleView.getText().toString().trim();
                if (TextUtils.isEmpty(title)) {
                    if (taskType==StudyTaskType.ENGLISH_WRITING){
                        TipMsgHelper.ShowLMsg(getActivity(),R.string.pls_input_article_title);
                    }else {
                        TipMsgHelper.ShowLMsg(getActivity(), R.string.pls_input_title);
                    }
                    return;
                }
                uploadParameter.setFileName(title);
                String description = descriptionView.getText().toString().trim();
                if (TextUtils.isEmpty(description)) {
                    if (taskType==StudyTaskType.ENGLISH_WRITING){
                        TipMsgHelper.ShowLMsg(getActivity(), R.string.pls_input_article_request);
                    }else {
                        TipMsgHelper.ShowLMsg(getActivity(), R.string.pls_input_description);
                    }
                    return;
                }
                uploadParameter.setDescription(description);
                //这里用来区分英文写作和话题讨论的上传的参数
                if (taskType==StudyTaskType.ENGLISH_WRITING){
                    uploadParameter.setTaskType(StudyTaskType.ENGLISH_WRITING);
                    //作文要求
                    uploadParameter.setWritingRequire(description);
                    //打分公式
                    if (markFormulaId==0){
                        TipMsgHelper.ShowLMsg(getActivity(),R.string.network_error);
                        return;
                    }
                    uploadParameter.setMarkFormula(markFormulaId);
                    //作文字数最小值
                    String workMin=limitWordFrom.getText().toString().trim();
                    if (!TextUtils.isEmpty(workMin)){
                        uploadParameter.setWordCountMin(Integer.valueOf(workMin));
                    }
                    //作文字数最大值
                    String workMax=limitWordTo.getText().toString().trim();
                    if (!TextUtils.isEmpty(workMax)){
                        uploadParameter.setWordCountMax(Integer.valueOf(workMax));
                    }
                    //如果最小值大于最大值给于提示
                    boolean flag=compareMinorMax(workMin,workMax);
                    if (!flag){
                        TipMsgHelper.ShowLMsg(getActivity(),R.string.min_cannot_more_than_max);
                        return;
                    }
                }else {
                    uploadParameter.setTaskType(StudyTaskType.TOPIC_DISCUSSION);
                    uploadParameter.setDescription(description);
                }

                UIUtils.hideSoftKeyboard(getActivity());
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Bundle args = getArguments();
                args.putSerializable(UploadParameter.class.getSimpleName(), uploadParameter);
                PublishStudyTaskFragment fragment = new PublishStudyTaskFragment();
                fragment.setArguments(args);
                ft.hide(this);
                ft.show(fragment);
                ft.add(R.id.activity_body, fragment, PublishStudyTaskFragment.TAG);
                ft.addToBackStack(null);
                ft.commit();
            }
        }
    }
    private boolean compareMinorMax(String workMin,String workMax){
        if (TextUtils.isEmpty(workMin)){
            return true;
        }else {
            if (TextUtils.isEmpty(workMax)){
                return false;
            }else {
                //表示最小值与最大值都不为空
                int min=Integer.valueOf(workMin);
                int max=Integer.valueOf(workMax);
                if (min<=max){
                    return true;
                }else {
                    return false;
                }
            }
        }
    }

    /**
     * 打分公式
     */
    private void checkScoreFormula(){
        if (childNameArray==null){
            childNameArray=new String[]{" "};
        }
        if (childFormulaID==null){
            childFormulaID=new int[]{0};
        }
        SelectBindChildPopupView popupView = new SelectBindChildPopupView(getActivity(), position,
                this, childNameArray);
        popupView.showAtLocation(mRootView, Gravity.BOTTOM, 0, 0);
    }
    private void loadScoreFormulaData(){
        DefaultPullToRefreshDataListener listener =
                new DefaultPullToRefreshDataListener<ScoreFormulaListResult>(
                        ScoreFormulaListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        ScoreFormula scoreFormula = JSONObject.parseObject(jsonString, ScoreFormula.class);
                        updateScoreFormulaData(scoreFormula);
                    }
                };
        RequestHelper.sendPostRequest(getActivity(), ServerUrl
                .GET_ENGLISH_WRITING_SCOREFORMULA_BASE_URL, null, listener);
    }
    private void updateScoreFormulaData(ScoreFormula result){
//        childNameArray=new String[]{"小学-三四年级打分公式","小学-五六年级打分公式","初中-初中一年级打分公式",
//                "初中-初中二年级打分公式","初中-初中三年级打分公式","高中-作文打分公式"};
//        childFormulaID=new int[]{823671,823673,823676, 823681, 823682,823684};
        if (result!=null) {
            List<ScoreFormula> scoreFormulas = result.getModel();
            if (scoreFormulas != null && scoreFormulas.size() > 0) {
                childNameArray = new String[scoreFormulas.size()];
                childFormulaID = new int[scoreFormulas.size()];
                for (int i = 0; i < scoreFormulas.size(); i++) {
                    String key = scoreFormulas.get(i).getKey();
                    String value = scoreFormulas.get(i).getValue();
                    childNameArray[i] = value;
                    childFormulaID[i] = Integer.valueOf(key);
                }
            }else {
                childFormulaID=new int[]{0};
                childNameArray=new String[]{" "};
            }
            scoreFormule.setText(childNameArray[0]);
            markFormulaId = childFormulaID[0];
        }
    }
    /**
     * 打分公式之后的回调
     * @param index
     * @param relationType
     */
    @Override
    public void onRelationChange(int index, String relationType) {
        scoreFormule.setText(childNameArray[index]);
        markFormulaId=childFormulaID[index];
    }
}
