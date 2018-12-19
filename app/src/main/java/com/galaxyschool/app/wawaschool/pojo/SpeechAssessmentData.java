package com.galaxyschool.app.wawaschool.pojo;
import android.text.SpannableString;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * ======================================================
 * Created by : Brave_Qu on 2018/9/12 0012 16:24
 * Describe:语音评测实体类
 * ======================================================
 */
public class SpeechAssessmentData implements Serializable {
    private String duration;
    private String end;
    private String imageUrl;
    private String mp3Name;
    private String mp3Url;
    private String start;
    private boolean isCheck;
    private int eval_scheme_id = 3;//int 评测方案代号，1声通/2讯飞/3先声，当前为3
    private int eval_score;//评测的得分
    private String ref_text;//原音的文本
    private String eval_result;//评测的结果
    private String my_audio_path;//用户录音的相对路径
    private boolean isShowingText;//显示评测的文本
    private boolean isAlreadyEval;//是否评测过
    private SpannableString spannableString;//转化过后的评测文本
    private boolean isEnglishLanguage;
    private ArrayList<Integer> wordIndex;
    private int speedModel;

    public int getSpeedModel() {
        return speedModel;
    }

    public void setSpeedModel(int speedModel) {
        this.speedModel = speedModel;
    }

    public ArrayList<Integer> getWordIndex() {
        return wordIndex;
    }

    public void setWordIndex(ArrayList<Integer> wordIndex) {
        this.wordIndex = wordIndex;
    }

    public boolean isEnglishLanguage() {
        return isEnglishLanguage;
    }

    public void setIsEnglishLanguage(boolean englishLanguage) {
        isEnglishLanguage = englishLanguage;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getMp3Name() {
        return mp3Name;
    }

    public void setMp3Name(String mp3Name) {
        this.mp3Name = mp3Name;
    }

    public String getMp3Url() {
        return mp3Url;
    }

    public void setMp3Url(String mp3Url) {
        this.mp3Url = mp3Url;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setIsCheck(boolean check) {
        isCheck = check;
    }

    public int getEval_scheme_id() {
        return eval_scheme_id;
    }

    public void setEval_scheme_id(int eval_scheme_id) {
        this.eval_scheme_id = eval_scheme_id;
    }

    public String getRef_text() {
        return ref_text;
    }

    public void setRef_text(String ref_text) {
        this.ref_text = ref_text;
    }

    public String getEval_result() {
        return eval_result;
    }

    public void setEval_result(String eval_result) {
        this.eval_result = eval_result;
    }

    public String getMy_audio_path() {
        return my_audio_path;
    }

    public void setMy_audio_path(String my_audio_path) {
        this.my_audio_path = my_audio_path;
    }

    public boolean isShowingText() {
        return isShowingText;
    }

    public void setIsShowingText(boolean showingText) {
        isShowingText = showingText;
    }

    public boolean isAlreadyEval() {
        return isAlreadyEval;
    }

    public void setIsAlreadyEval(boolean alreadyEval) {
        isAlreadyEval = alreadyEval;
    }

    public SpannableString getSpannableString() {
        return spannableString;
    }

    public void setSpannableString(SpannableString spannableString) {
        this.spannableString = spannableString;
    }

    public int getEval_score() {
        return eval_score;
    }

    public void setEval_score(int eval_score) {
        this.eval_score = eval_score;
    }
}
