package com.galaxyschool.app.wawaschool.pojo;
import java.io.Serializable;

public class learnTaskCardData implements Serializable {
    private int questionType;//题型
    private int selectType;//0 单选 1 多选
    private boolean isSelect;//是否选择
    private String itemTitle;//选择的title
    private String answerPath;//主观题作答的路径
    private String answerPathTitle;//主观题作答路径的title

    public int getQuestionType() {
        return questionType;
    }

    public void setQuestionType(int questionType) {
        this.questionType = questionType;
    }

    public int getSelectType() {
        return selectType;
    }

    public void setSelectType(int selectType) {
        this.selectType = selectType;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setIsSelect(boolean select) {
        isSelect = select;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public String getAnswerPath() {
        return answerPath;
    }

    public void setAnswerPath(String answerPath) {
        this.answerPath = answerPath;
    }

    public String getAnswerPathTitle() {
        return answerPathTitle;
    }

    public void setAnswerPathTitle(String answerPathTitle) {
        this.answerPathTitle = answerPathTitle;
    }
}
