package com.lqwawa.intleducation.module.learn.adapter;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseAdapter;
import com.lqwawa.intleducation.base.utils.StringUtils;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.NoScrollListView;
import com.lqwawa.intleducation.module.learn.vo.AnswerVo;
import com.lqwawa.intleducation.module.learn.vo.ExamActionType;
import com.lqwawa.intleducation.module.learn.vo.ExamCexerVo;
import com.lqwawa.intleducation.module.learn.vo.ExamItemVo;
import com.lqwawa.intleducation.module.learn.vo.ExamOptionsVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.osastudio.common.utils.TipMsgHelper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by XChen on 2016/11/29.
 * email:man0fchina@foxmail.com
 */

public class ExamPagerAdapter extends PagerAdapter {
    private Activity activity;
    private List<ExamItemVo> list;
    private LayoutInflater inflater;
    private List<AnswerVo> answerList;
    private MyBaseAdapter.OnContentChangedListener onContentChangedListener;
    private int type;
    private int examActionType; //用于老师查看试卷和批阅
    private int roleType;
    private static int[] pageTypeResIds = new int[]{
            R.string.exam_single
            , R.string.exam_multiple
            , R.string.exam_judgment
            , R.string.exam_essayquestion};
    private static String[] answerArray = new String[]{
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J"
    };

    public ExamPagerAdapter(Activity activity, int type,
                            MyBaseAdapter.OnContentChangedListener listener) {
        this.activity = activity;
        this.type = type;
        this.inflater = LayoutInflater.from(activity);
        list = new ArrayList<ExamItemVo>();
        this.onContentChangedListener = listener;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    //断是否由对象生成界面
    public boolean isViewFromObject(View arg0, Object arg1) {
        // TODO Auto-generated method stub
        return arg0 == arg1;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final ViewHolder holder;
        final ExamItemVo vo = list.get(position);
        View convertView = inflater.inflate(R.layout.mod_learn_exam_pager_item, null);
        convertView.setBackgroundColor(activity.getResources().getColor(R.color.com_bg_little_gray));
        holder = new ViewHolder(convertView);
        convertView.setTag(holder);

        SpannableStringBuilder builder = new SpannableStringBuilder("(" +
                activity.getResources().getString(
                        pageTypeResIds[vo.getCexer().getExerciseType() - 1]) + ")");
        ForegroundColorSpan yellowSpan = new ForegroundColorSpan(
                activity.getResources().getColor(R.color.com_text_pink));
        builder.setSpan(yellowSpan, 0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.exam_title_tv.setText(builder);
        holder.exam_title_tv.append(vo.getCexer().getQuestion());
        holder.exam_title_tv.append(" (" + vo.getCexer().getScore()
                + activity.getResources().getString(R.string.points) + ")");

        holder.single_root_lay.setVisibility(View.GONE);
        holder.essay_question_root_lay.setVisibility(View.GONE);
        switch (vo.getCexer().getExerciseType()) {
            case 1: {
                holder.single_root_lay.setVisibility(View.VISIBLE);
                holder.showSelectItem(position, vo, type, vo.getCexer().getItemNum(), true);
                break;
            }
            case 2: {
                holder.single_root_lay.setVisibility(View.VISIBLE);
                holder.showSelectItem(position, vo, type, vo.getCexer().getItemNum(), false);
                break;
            }
            case 3: {
                holder.single_root_lay.setVisibility(View.VISIBLE);
                holder.showSelectItem(position, vo, type, 2, true);
                break;
            }
            case 4: {
                holder.essay_question_root_lay.setVisibility(View.VISIBLE);
                if (type == 1) {
                    holder.question_et.setEnabled(true);
                    holder.question_et.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            answerList.get(position).setAnswer(s.toString());
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });
                    if (answerList.get(position).getAnswer() != null){
                        holder.question_et.setText(answerList.get(position).getAnswer());
                    }
                } else {
                    holder.question_et.setEnabled(false);
                    if (vo.getUexer() != null) {
                        String answer = vo.getUexer().getAnswer();
                        if (examActionType == ExamActionType.MARK) {
                            holder.question_et.setText(getAnswerPatch());
                            holder.question_et.append(answer);
                        } else {
                            holder.question_et.setText(answer);
                        }
                    }
                }
                break;
            }
            default:
                break;
        }
        if (type < 2) {
            holder.result_root_lay.setVisibility(View.GONE);
        } else {
            if (vo.getCexer().getExerciseType() != 4) {
                holder.result_tv.setVisibility(View.VISIBLE);
                holder.result_tv.setText(activity.getResources().getString(R.string.right_answer));
                SpannableStringBuilder rightAnswerBuilder =
                        new SpannableStringBuilder(vo.getCexer().getAnswer());
                ForegroundColorSpan greenSpan = new ForegroundColorSpan(
                        activity.getResources().getColor(R.color.com_text_green));
                rightAnswerBuilder.setSpan(greenSpan, 0, rightAnswerBuilder.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.result_tv.append(rightAnswerBuilder);
                if (vo.getUexer() != null
                        && vo.getCexer().getAnswer().equals(vo.getUexer().getAnswer())) {//答案正确
                    holder.result_tv.append("  " +
                            activity.getResources().getString(R.string.you_are_right));
                    holder.result_tv.setVisibility(View.GONE);
                } else {
                    if (examActionType != ExamActionType.WATCH_TEST) {
                        if (vo.getUexer() != null && !TextUtils.isEmpty(vo.getUexer().getAnswer())) {
                            //老师显示Ta的答案，其他显示你的答案
                            int answerId = R.string.your_answer;
                            if (roleType == UserHelper.MoocRoleType.TEACHER) {
                                answerId = R.string.his_answer0;
                            }
                            holder.result_tv.append(", " + activity.getResources().getString(answerId));
                            SpannableStringBuilder errorAnswerBuilder =
                                    new SpannableStringBuilder(vo.getUexer().getAnswer());
                            ForegroundColorSpan redSpan = new ForegroundColorSpan(
                                    activity.getResources().getColor(R.color.com_text_red));
                            errorAnswerBuilder.setSpan(redSpan, 0, errorAnswerBuilder.length(),
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            holder.result_tv.append(errorAnswerBuilder);
                        } else {
                            int answerId = R.string.you_are_not_answer;
                            if (roleType == UserHelper.MoocRoleType.TEACHER) {
                                answerId = R.string.him_not_answer;
                            }
                            SpannableStringBuilder errorAnswerBuilder =
                                    new SpannableStringBuilder("  " +
                                            activity.getResources().getString(answerId));
                            ForegroundColorSpan redSpan = new ForegroundColorSpan(
                                    activity.getResources().getColor(R.color.com_text_red));
                            errorAnswerBuilder.setSpan(redSpan, 0, errorAnswerBuilder.length(),
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            holder.result_tv.append(errorAnswerBuilder);
                        }
                    }
                }
                holder.parsing_et.setVisibility(View.VISIBLE);
                holder.parsing_et.setText(activity.getResources().getString(R.string.exam_item_parsing)
                + vo.getCexer().getRemark());
                holder.answer_tv.setVisibility(View.GONE);
                holder.mark_layout.setVisibility(View.GONE);
            } else {
                holder.result_tv.setVisibility(View.GONE);
                holder.parsing_et.setVisibility(View.VISIBLE);
                holder.parsing_et.setText("" + activity.getResources().getString(R.string.exam_item_parsing)
                        + vo.getCexer().getRemark());
                holder.answer_tv.setVisibility(roleType == UserHelper.MoocRoleType.TEACHER ? View
                        .VISIBLE : View.GONE);
                holder.answer_tv.setText(activity.getResources().getString(R.string
                        .exam_item_answer, vo.getCexer().getAnswer()));
                holder.mark_layout.setVisibility(examActionType == ExamActionType.MARK ?View
                        .VISIBLE : View.GONE);
            }
            if (vo.getUexer() != null) {
//                holder.score_tv.setVisibility(View.VISIBLE);
                holder.score_tv.setVisibility(View.GONE);
                if (vo.getUexer().getScore() >= 0) {
                    holder.score_tv.setText(activity.getResources().getString(R.string.result_score_with_colon)
                            + vo.getUexer().getScore()
                            + activity.getResources().getString(R.string.points));
                } else {
                    if (roleType != UserHelper.MoocRoleType.TEACHER && vo.getUexer().getScore()
                            == -1 && vo.getCexer().getExerciseType() == 4) {
//                        holder.score_tv.setText(R.string.result_score_with_colon);
                        holder.score_tv.setText(getScorePatch());
                        holder.score_tv.setVisibility(View.VISIBLE);
                    } else {
                        holder.score_tv.setVisibility(View.GONE);
                    }
                }
            }
//            else {
//                holder.score_tv.setText(activity.getResources().getString(R.string.result_score_with_colon)
//                        + 0
//                        + activity.getResources().getString(R.string.points));
//            }
            if (examActionType == ExamActionType.WATCH_TEST) {
                holder.score_tv.setVisibility(View.GONE);
            }

            holder.mark_et.addTextChangedListener(new MarkTextWatcher(vo.getCexer().getScore(),
                    position));
        }

        ((ViewPager) container).addView(convertView);
        return convertView;
    }

    private SpannableStringBuilder getAnswerPatch() {
            String prefixStr = activity.getResources().getString(R.string.his_answer1);
        SpannableStringBuilder answerPatchBuilder =
                new SpannableStringBuilder(prefixStr);
        ForegroundColorSpan redSpan = new ForegroundColorSpan(
                activity.getResources().getColor(R.color.com_text_gray));
        answerPatchBuilder.setSpan(redSpan, 0, answerPatchBuilder.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return answerPatchBuilder;
    }

    private SpannableStringBuilder getScorePatch() {
        String prefixStr = activity.getResources().getString(R.string.mooc_unmark);
        SpannableStringBuilder answerPatchBuilder =
                new SpannableStringBuilder(prefixStr);
        ForegroundColorSpan redSpan = new ForegroundColorSpan(
                activity.getResources().getColor(R.color.com_text_red));
        answerPatchBuilder.setSpan(redSpan, 0, answerPatchBuilder.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return answerPatchBuilder;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (object != null && object instanceof ImageView) {
            container.removeView((View)object);
        }
    }

    private class ViewHolder {
        TextView exam_title_tv;

        LinearLayout single_root_lay;
        NoScrollListView single_options_list;

        void showSelectItem(final int position, final ExamItemVo vo, int type,
                            final int itemCount, final boolean singleSelect) {
            if (itemCount > 0 && itemCount <= 10) {
                List<ExamOptionsVo> optionsVos = new ArrayList<>();
                for (int i = 0; i < itemCount; i++) {
                    ExamOptionsVo optionsVo = new ExamOptionsVo();
                    optionsVo.setName(answerArray[i]);
                    optionsVo.setContent(getItem(i, vo.getCexer()));
                    optionsVo.setRightAnswer(vo.getCexer().getAnswer().contains(answerArray[i]));

                    if (type == 1) {
                        if (answerList.get(position).getAnswer() != null){
                            if (answerList.get(position).getAnswer().contains(answerArray[i])) {
                                optionsVo.setSelected(true);
                            }
                        }
                    } else {
                        if (vo.getUexer() != null) {
                            if (vo.getUexer().getAnswer().contains(answerArray[i])) {
                                optionsVo.setSelected(true);
                            }
                        }
                    }
                    optionsVos.add(optionsVo);
                }
                final ExamOptionsAdapter adapter = new ExamOptionsAdapter(activity, singleSelect, type);
                single_options_list.setAdapter(adapter);
                if(type == 1) {
                    single_options_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            adapter.setSelected(i);
                            if (singleSelect) {
                                answerList.get(position).setAnswer(answerArray[i]);
                                if(onContentChangedListener != null){
                                    onContentChangedListener.OnContentChanged();
                                }
                            } else {
                                String answerString = "";
                                for (int j = 0; j < itemCount; j++) {
                                    if (adapter.isSelected(j)) {
                                        answerString += answerArray[j];
                                    }
                                }
                                answerList.get(position).setAnswer(answerString);
                            }
                        }
                    });
                }
                adapter.setData(optionsVos);
                adapter.notifyDataSetChanged();
            }
        }

        String getItem(int index, ExamCexerVo vo) {
            int itemCount = vo.getItemNum();
            if(vo.getExerciseType() == 3){
                itemCount = 2;
            }
            if (index >= 0 && index < itemCount) {
                switch (index) {
                    case 0:
                        return vo.getItemA();
                    case 1:
                        return vo.getItemB();
                    case 2:
                        return vo.getItemC();
                    case 3:
                        return vo.getItemD();
                    case 4:
                        return vo.getItemE();
                    case 5:
                        return vo.getItemF();
                    case 6:
                        return vo.getItemG();
                    case 7:
                        return vo.getItemH();
                    case 8:
                        return vo.getItemI();
                    case 9:
                        return vo.getItemJ();
                    default:
                        return vo.getItemA();
                }
            }
            return vo.getItemA();
        }

        LinearLayout essay_question_root_lay;
        EditText question_et;

        LinearLayout result_root_lay;
        TextView result_tv;
        TextView score_tv;
        TextView answer_tv;
        TextView parsing_et;
        View mark_layout;
        EditText mark_et;

        public ViewHolder(View parentView) {
            exam_title_tv = (TextView)parentView.findViewById(R.id.exam_title_tv);

            single_root_lay = (LinearLayout)parentView.findViewById(R.id.single_root_lay);
            single_options_list = (NoScrollListView) parentView.findViewById(R.id.single_options_list);

            essay_question_root_lay = (LinearLayout)parentView.findViewById(R.id.essay_question_root_lay);
            question_et = (EditText)parentView.findViewById(R.id.question_et);

            result_root_lay = (LinearLayout)parentView.findViewById(R.id.result_root_lay);
            result_tv = (TextView)parentView.findViewById(R.id.result_tv);
            score_tv = (TextView)parentView.findViewById(R.id.score_tv);
            answer_tv = (TextView)parentView.findViewById(R.id.answer_tv);
            parsing_et = (TextView)parentView.findViewById(R.id.parsing_et);
            mark_layout = parentView.findViewById(R.id.exam_mark_ll);
            mark_et = (EditText)parentView.findViewById(R.id.mark_et);
        }
    }

    public List<AnswerVo> getUexerJson() {
        List<AnswerVo> validAnswers = new ArrayList<>();
        for (int i = 0; i < answerList.size(); i++){
            if (!answerList.get(i).getAnswer().isEmpty()) {
                validAnswers.add(answerList.get(i));
                try {
                    String contentString = answerList.get(i).getAnswer().trim();
                    contentString = URLEncoder.encode(contentString, "utf-8");
                    contentString = contentString.replaceAll("%0A", "\n");
                    answerList.get(i).setAnswer(contentString);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return validAnswers;
    }

    public List<AnswerVo> getTeacherMarkJson() {
        List<AnswerVo> validAnswers = new ArrayList<>();
        for (int i = 0; i < answerList.size(); i++){
            if (answerList.get(i).getType() == 4
                    && !TextUtils.isEmpty(answerList.get(i).getScore())) {
                validAnswers.add(answerList.get(i));
            }
        }
        return validAnswers;
    }

    /**
     * 下拉刷新设置数据
     *
     * @param list
     */
    public void setData(List<ExamItemVo> list) {
        if (list != null) {
            this.list = new ArrayList<ExamItemVo>(list);
            answerList = new ArrayList<AnswerVo>();
            for (int i = 0; i < this.list.size(); i++) {
                AnswerVo e = new AnswerVo();
                if (examActionType == ExamActionType.MARK) {
                    e.setExerId(this.list.get(i).getUexer().getId());
                } else {
                    e.setExerId(this.list.get(i).getCexer().getId());
                }
                e.setType(this.list.get(i).getCexer().getExerciseType());
                e.setAnswer("");
                e.setScore("");
                answerList.add(e);
            }
        } else {
            this.list.clear();
        }
    }

    /**
     * 上拉加载更多，新增数据
     *
     * @param list
     */
    public void addData(List<ExamItemVo> list) {
        this.list.addAll(list);
    }

    public boolean isDoAll() {
        if (answerList == null) {
            return false;
        }
        for (int i = 0; i < answerList.size(); i++) {
            if (!StringUtils.isValidString(answerList.get(i).getAnswer())) {
                return false;
            }
        }
        return true;
    }

    public boolean isDoAllQuestions() {
        if (answerList == null) {
            return false;
        }
        for (int i = 0; i < answerList.size(); i++) {
            if (answerList.get(i).getType() == 4
                    && TextUtils.isEmpty(answerList.get(i).getScore())) {
                return false;
            }
        }
        return true;
    }


    public void setExamActionType(int examActionType) {
        this.examActionType = examActionType;
    }

    public void setRoleType(int roleType) {
        this.roleType = roleType;
    }

    private class MarkTextWatcher implements TextWatcher {

        private int score;
        private int position;

        public MarkTextWatcher(int score, int position) {
            this.score = score;
            this.position = position;
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            String textContent = s.toString().trim();
            if (!TextUtils.isEmpty(textContent) && textContent.length() >= 1) {
                int data = Integer.valueOf(textContent);
                if (data >= 0 && data <= score) {
                    answerList.get(position).setScore(textContent);
                } else {
                    int len = s.length();
                    s.delete(len - 1, len);
                    TipMsgHelper.ShowLMsg(activity, activity.getResources().getString(R.string
                            .exam_valid_score, score));
                }
            } else {
                //清空分数
                answerList.get(position).setScore("");
            }

        }
    }
}
