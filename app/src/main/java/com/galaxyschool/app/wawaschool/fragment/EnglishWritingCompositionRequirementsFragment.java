package com.galaxyschool.app.wawaschool.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.DateUtils;
import com.galaxyschool.app.wawaschool.pojo.StudyTask;


/**
 * 英文写作---写作要求页面
 */

public class EnglishWritingCompositionRequirementsFragment extends ContactsListFragment {

    public static final String TAG = EnglishWritingCompositionRequirementsFragment.class.getSimpleName();

    private View rootView, englishWritingHeaderView,finishStatusView;
    private TextView commonHeaderTitleTextView,limitFromOrTo,articleTitle;
    private TextView assignTimeTextView;
    private TextView finishTimeTextView;
    private TextView contentTextView;
    private LinearLayout limitLayout;
    private StudyTask task;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_english_writing_composition_requirements, null);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        refreshData();
    }

    private void refreshData() {
        loadViews();
    }

    void initViews() {
        if (getArguments() != null) {
            task = (StudyTask) getArguments().getSerializable("task");
        }

        //标题
        commonHeaderTitleTextView = (TextView) findViewById(R.id.contacts_header_title);
        if (commonHeaderTitleTextView != null) {
            commonHeaderTitleTextView.setText(R.string.composition_requirements);
        }

        englishWritingHeaderView = findViewById(R.id.layout_english_writing_assign_homework);
        englishWritingHeaderView.setOnClickListener(this);

        //布置时间
        assignTimeTextView = (TextView) englishWritingHeaderView.findViewById(R.id.tv_start_time);

        //完成时间
        finishTimeTextView = (TextView) englishWritingHeaderView.findViewById(R.id.tv_end_time);
        //完成状态布局
        finishStatusView = englishWritingHeaderView.findViewById(R.id.layout_finish_state);
        if (finishStatusView != null) {
            finishStatusView.setVisibility(View.GONE);
        }
        //作文要求
        contentTextView = (TextView) findViewById(R.id.tv_content);
        //作文字数限制的区间
        limitLayout= (LinearLayout) findViewById(R.id.article_num_count);
        //限制的开始字数
        limitFromOrTo= (TextView) findViewById(R.id.show_limit_range);
        //作文的标题
        articleTitle= (TextView) findViewById(R.id.tv_title);

    }

    /**
     * 拉取数据
     */
    private void loadCommonData() {

        if (task != null){
            //显示作文的标题
            if (articleTitle!=null){
                articleTitle.setText(task.getTaskTitle());
            }
            //布置时间
            if (assignTimeTextView != null) {
                assignTimeTextView.setText(getString(R.string.assign_date) + "：" +
                        DateUtils.getDateStr(task.getStartTime(), 0) + "-" +
                        DateUtils.getDateStr(task.getStartTime(), 1) + "-" + DateUtils
                        .getDateStr(task.getStartTime(), 2));
            }

            //完成时间
            if (finishTimeTextView != null) {
                finishTimeTextView.setText(getString(R.string.finish_date) + "：" +
                        DateUtils.getDateStr(task.getEndTime(), 0) + "-" +
                        DateUtils.getDateStr(task.getEndTime(), 1) + "-" + DateUtils
                        .getDateStr(task.getEndTime(), 2));
            }

            if (contentTextView != null){
                contentTextView.setText(task.getWritingRequire());
            }
            //根据条件判断是否显示字数的限制
            int maxLimit=task.getWordCountMax();
            if (maxLimit>=1){
                limitLayout.setVisibility(View.VISIBLE);
                limitFromOrTo.setText(""+task.getWordCountMin()+"  -  "+task
                        .getWordCountMax());
            }
        }
    }

    private void loadViews() {
        loadCommonData();
    }

}
