package com.galaxyschool.app.wawaschool.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ScreenUtils;
import com.galaxyschool.app.wawaschool.helper.DoAnswerCardHelper;
import com.galaxyschool.app.wawaschool.helper.DoTaskOrderHelper;
import com.galaxyschool.app.wawaschool.pojo.ExerciseAnswerCardParam;
import com.galaxyschool.app.wawaschool.pojo.ExerciseItem;
import com.lqwawa.client.pojo.LearnTaskCardType;
import java.util.List;

/**
 * 答题卡
 */

public class AnswerCardPopWindow extends PopupWindow {

    private Activity mContext;
    private View mRootView;
    //题目答题list选项
    private List<ExerciseItem> exerciseItems;
    private ExerciseAnswerCardParam cardParam;
    private DoAnswerCardHelper cardHelper;

    public AnswerCardPopWindow(Context context,
                               ExerciseAnswerCardParam cardParam,
                               List<ExerciseItem> exerciseItems) {
        super(context);
        this.mContext = (Activity) context;
        this.cardParam = cardParam;
        this.exerciseItems = exerciseItems;
        this.mRootView = LayoutInflater.from(mContext).inflate(R.layout.layout_answercard_popwindow, null);
        LinearLayout testQuestionsLayout = (LinearLayout) mRootView.findViewById(R.id.ll_root_layout);
        cardHelper = new DoAnswerCardHelper(mContext,testQuestionsLayout);
        setProperty();
        initData();
    }

    private void initData() {
        if (exerciseItems != null && exerciseItems.size() > 0) {
            for (int i = 0; i < exerciseItems.size(); i++) {
                ExerciseItem item = exerciseItems.get(i);
                if (item != null && !TextUtils.isEmpty(item.getType())) {
                    int type = Integer.valueOf(item.getType());
                    cardHelper.dealDiffTypeData(type, item);
                }
            }
        }
    }

    /**
     * 获取学生提交的答案
     */
    public void commitAnswerQuestion() {
        if (exerciseItems != null && exerciseItems.size() > 0) {
            for (int i = 0; i < exerciseItems.size(); i++) {
                ExerciseItem item = exerciseItems.get(i);
                if (item != null && !TextUtils.isEmpty(item.getType())) {
                    int type = Integer.valueOf(item.getType());
                    cardHelper.getStudentCommitData(type,item,i);
                }
            }
            DoTaskOrderHelper helper = new DoTaskOrderHelper(mContext);
            helper.setExerciseAnswerCardParam(cardParam).
                    setExerciseItem(exerciseItems).
                    commit();
        }
    }

    /**
     * 判断是否完成了所有的作业
     *
     * @return
     */
    public boolean isFinishAllQuestion() {
        if (exerciseItems != null && exerciseItems.size() > 0) {
            for (int i = 0; i < exerciseItems.size(); i++) {
                ExerciseItem item = exerciseItems.get(i);
                if (item != null && !TextUtils.isEmpty(item.getType())) {
                    int type = Integer.valueOf(item.getType());
                    cardHelper.getStudentCommitData(type,item,i);
                }
            }
            for (int m = 0; m < exerciseItems.size(); m++) {
                ExerciseItem itemData = exerciseItems.get(m);
                int type = Integer.valueOf(itemData.getType());
                if (type == LearnTaskCardType.SUBJECTIVE_PROBLEM) {
                    if (itemData.getCardData() == null || itemData.getCardData().size() == 0) {
                        return false;
                    }
                } else if (TextUtils.isEmpty(itemData.getStudent_answer())) {
                    return false;
                }

                if (type == LearnTaskCardType.SINGLE_CHOICE_CORRECTION) {
                    //单选改错
                    String answerObj = itemData.getStudent_answer();
                    if (TextUtils.isEmpty(answerObj)) {
                        return false;
                    }
                    JSONObject jsonObject = JSONObject.parseObject(answerObj);
                    String itemIndex = jsonObject.getString("item_index");
                    String answerText = jsonObject.getString("answer_text");
                    if (TextUtils.isEmpty(itemIndex) || TextUtils.isEmpty(answerText)) {
                        return false;
                    }
                }

                if (type == LearnTaskCardType.FILL_CONTENT
                        || type == LearnTaskCardType.LISTEN_FILL_CONTENT) {
                    //填空和听力填空
                    String fillInString = itemData.getStudent_answer();
                    if (TextUtils.isEmpty(fillInString)) {
                        return false;
                    }
                    JSONArray jsonArray = JSONObject.parseArray(fillInString);
                    if (jsonArray != null && jsonArray.size() > 0) {
                        for (int i = 0; i < jsonArray.size(); i++) {
                            if (TextUtils.isEmpty(jsonArray.get(i).toString())) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    public void setRequestCodeData(int requestCode, Intent data) {
        if (cardHelper != null){
            cardHelper.setRequestCodeData(requestCode,data);
        }
    }

    private void setProperty() {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        this.setContentView(mRootView);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
        this.setTouchable(true);
        this.setOutsideTouchable(true);
        this.update();
        ColorDrawable dw = new ColorDrawable(0000000000);
        this.setBackgroundDrawable(dw);
    }

    public void showPopupMenu(View v) {
        if (!this.isShowing()) {
            this.showAtLocation(mRootView, Gravity.NO_GRAVITY, 0, 0);
        } else {
            this.dismiss();
        }
    }

    public void showPopupMenu() {
        if (!this.isShowing()) {
            this.showAtLocation(mRootView, Gravity.NO_GRAVITY, 0, 0);
        } else {
            this.dismiss();
        }
    }

    public void showPopupMenuRight() {
        if (!this.isShowing()) {
            this.showAtLocation(mRootView, Gravity.NO_GRAVITY, ScreenUtils.getScreenWidth
                    (mContext), 0);
        } else {
            this.dismiss();
        }
    }

    /**
     * 重新设定窗口大小
     *
     * @param ratio
     */
    public void resizePopupWindowWith(float ratio) {
        if (ratio <= 0) {
            return;
        }
        Display display = mContext.getWindowManager().getDefaultDisplay();
        int width = (int) (display.getWidth() * ratio);
        setWidth(width);
    }
}
