package com.galaxyschool.app.wawaschool.pojo;
import android.text.TextUtils;

import com.galaxyschool.app.wawaschool.pojo.weike.MediaData;
import com.lqwawa.client.pojo.LearnTaskCardType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ExerciseItem implements Serializable{
    private String type;//题型
    //选项数 判断题默认为2 单选多选填空最多10个;
    private String item_count;
    //听力原文的文字
    private String src_text;
    private String src_res_id;
    //听力原文的资源地址 资源库的图片或者pdf格式
    private String src_res_url;
    //听力原文的资源名 资源库的图片或者pdf格式
    private String src_res_name;
    //标准答案单选：0->a 多选：0,1,2->ABC 判断 0->对
    //填空题：每空已逗号分开，听力单选：单选答案，单选改错：单选答案和订正答案用逗号分开，主观题：标准答案;
    private String right_answer;
    //正确答案的资源地址 适用于主观题 格式为图片或pdf
    private String right_answer_res_id;
    //正确答案的资源名 资源库的图片或者pdf格式
    private String right_answer_res_url;
    private String right_answer_res_name;
    //学生作答内容
    private String student_answer;
    //答案解析
    private String analysis;
    //单题分值
    private String score;
    //单空分值 每空已逗号分开 仅对填空题生效
    private String subscore;
    //题号 实际就是列表的索引值+1
    private String index;
    //题号名称
    private String name;
    //得分
    private String student_score;
    //单空得分 每空已逗号分开 仅对填空题生效
    private String student_subscore;
    //自定义的题型名称 仅对主观题生效
    private String type_name;
    //是否答对
    private boolean isAnswerRight;
    //答题的状态
    private int eqState;
    //答题区域列表
    private List<ExerciseItemArea> areaItemList;
    private List<learnTaskCardData> cardData;
    private List<MediaData> datas;//主观题在线的数据

    //答案解析的数据
    private int Id;
    private int SubimtNum;//提交的人数
    private int WrongNum;//错误的人数
    private int EmptyNum;
    private double AverageScore;//平均分
    private String CommonError;//共同的错误
    private float errorRate;//错误率
    private List<Integer> subIds = new ArrayList<>();
    private List<Integer> subWrongNum = new ArrayList<>();
    private List<Integer> subSubmintNum = new ArrayList<>();
    private List<Integer> subEmptyNum = new ArrayList<>();
    private List<Double> subAverageScore = new ArrayList<>();
    private List<String> subCommonError = new ArrayList<>();
    private List<Float> subErrorRate = new ArrayList<>();

    public float getErrorRate() {
        return errorRate;
    }

    public void setErrorRate(float errorRate) {
        this.errorRate = errorRate;
    }

    public List<Float> getSubErrorRate() {
        return subErrorRate;
    }

    public void setSubErrorRate(List<Float> subErrorRate) {
        this.subErrorRate = subErrorRate;
    }

    public List<Integer> getSubEmptyNum() {
        return subEmptyNum;
    }

    public void setSubEmptyNum(List<Integer> subEmptyNum) {
        this.subEmptyNum = subEmptyNum;
    }

    public int getEmptyNum() {
        return EmptyNum;
    }

    public void setEmptyNum(int emptyNum) {
        EmptyNum = emptyNum;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public List<Integer> getSubIds() {
        return subIds;
    }

    public void setSubIds(List<Integer> subIds) {
        this.subIds = subIds;
    }

    public int getSubimtNum() {
        return SubimtNum;
    }

    public void setSubimtNum(int subimtNum) {
        SubimtNum = subimtNum;
    }

    public int getWrongNum() {
        return WrongNum;
    }

    public void setWrongNum(int wrongNum) {
        WrongNum = wrongNum;
    }



    public String getCommonError() {
        return CommonError;
    }

    public void setCommonError(String commonError) {
        CommonError = commonError;
    }

    public List<Integer> getSubWrongNum() {
        return subWrongNum;
    }

    public void setSubWrongNum(List<Integer> subWrongNum) {
        this.subWrongNum = subWrongNum;
    }

    public List<Integer> getSubSubmintNum() {
        return subSubmintNum;
    }

    public void setSubSubmintNum(List<Integer> subSubmintNum) {
        this.subSubmintNum = subSubmintNum;
    }

    public double getAverageScore() {
        return AverageScore;
    }

    public void setAverageScore(double averageScore) {
        AverageScore = averageScore;
    }

    public List<Double> getSubAverageScore() {
        return subAverageScore;
    }

    public void setSubAverageScore(List<Double> subAverageScore) {
        this.subAverageScore = subAverageScore;
    }

    public List<String> getSubCommonError() {
        return subCommonError;
    }

    public void setSubCommonError(List<String> subCommonError) {
        this.subCommonError = subCommonError;
    }

    public int getEqState() {
        return eqState;
    }

    public void setEqState(int eqState) {
        this.eqState = eqState;
    }

    public boolean isAnswerRight() {
        if (!TextUtils.isEmpty(type)){
            int itemType = Integer.valueOf(type);
            if (itemType == LearnTaskCardType.SUBJECTIVE_PROBLEM){
                //主观题
                if (eqState == 4){
                    isAnswerRight = true;
                } else {
                    isAnswerRight = false;
                }
            } else {
                //客观题
                if (eqState == 1){
                    isAnswerRight = true;
                } else {
                    isAnswerRight = false;
                }
            }
        }
        return isAnswerRight;
    }

//    {
//         [Description("答案正确")]     //客观题
//        Right = 1,
//         [Description("答案错误")]    //客观题
//        Wrong = 2,
//
//         [Description("未打分")]    //主观题
//        UnScored = 3,
//         [Description("已打分")]    //主观题
//        Scored = 4
//    }

    public void setIsAnswerRight(boolean answerRight) {
        isAnswerRight = answerRight;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getItem_count() {
        return item_count;
    }

    public void setItem_count(String item_count) {
        this.item_count = item_count;
    }

    public String getSrc_text() {
        return src_text;
    }

    public void setSrc_text(String src_text) {
        this.src_text = src_text;
    }

    public String getSrc_res_id() {
        return src_res_id;
    }

    public void setSrc_res_id(String src_res_id) {
        this.src_res_id = src_res_id;
    }

    public String getSrc_res_url() {
        return src_res_url;
    }

    public void setSrc_res_url(String src_res_url) {
        this.src_res_url = src_res_url;
    }

    public String getSrc_res_name() {
        return src_res_name;
    }

    public void setSrc_res_name(String src_res_name) {
        this.src_res_name = src_res_name;
    }

    public String getRight_answer() {
        return right_answer;
    }

    public void setRight_answer(String right_answer) {
        this.right_answer = right_answer;
    }

    public String getRight_answer_res_id() {
        return right_answer_res_id;
    }

    public void setRight_answer_res_id(String right_answer_res_id) {
        this.right_answer_res_id = right_answer_res_id;
    }

    public String getRight_answer_res_url() {
        return right_answer_res_url;
    }

    public void setRight_answer_res_url(String right_answer_res_url) {
        this.right_answer_res_url = right_answer_res_url;
    }

    public String getRight_answer_res_name() {
        return right_answer_res_name;
    }

    public void setRight_answer_res_name(String right_answer_res_name) {
        this.right_answer_res_name = right_answer_res_name;
    }

    public String getStudent_answer() {
        return student_answer;
    }

    public void setStudent_answer(String student_answer) {
        this.student_answer = student_answer;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getSubscore() {
        return subscore;
    }

    public void setSubscore(String subscore) {
        this.subscore = subscore;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStudent_score() {
        return student_score;
    }

    public void setStudent_score(String student_score) {
        this.student_score = student_score;
    }

    public String getStudent_subscore() {
        return student_subscore;
    }

    public void setStudent_subscore(String student_subscore) {
        this.student_subscore = student_subscore;
    }

    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }

    public List<ExerciseItemArea> getAreaItemList() {
        return areaItemList;
    }

    public void setAreaItemList(List<ExerciseItemArea> areaItemList) {
        this.areaItemList = areaItemList;
    }

    public List<learnTaskCardData> getCardData() {
        return cardData;
    }

    public void setCardData(List<learnTaskCardData> cardData) {
        this.cardData = cardData;
    }

    public List<MediaData> getDatas() {
        return datas;
    }

    public void setDatas(List<MediaData> datas) {
        this.datas = datas;
    }

    public String [] getSubScoreArray(){
        String [] subScoreArray = null;
        if (!TextUtils.isEmpty(subscore)){
            if (subscore.contains(",")){
                subScoreArray = subscore.split(",");
            } else {
                subScoreArray = new String[]{subscore};
            }
        }
        return subScoreArray;
    }

    public String [] getStudentSubScoreArray(){
        String [] studentSubScoreArray = null;
        if (!TextUtils.isEmpty(student_subscore)){
            if (student_subscore.contains(",")){
                studentSubScoreArray = student_subscore.split(",");
            } else {
                studentSubScoreArray = new String[]{student_subscore};
            }
        }
        return studentSubScoreArray;
    }
}
