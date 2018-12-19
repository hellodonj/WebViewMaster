package com.galaxyschool.app.wawaschool.pojo;

import java.io.Serializable;
import java.util.List;

public class LearnTaskInfo implements Serializable {
    private String create_id;
    private String create_name;
    private String create_time;
    private String total_score;
    private String student_name;
    private String student_id;
    private String student_commit_time;
    private String student_score;
    private String need_read_answer_area;
    private int learnType;
    private String objectiveTotalScore;//客观题的总分
    private String studentObjectiveTotalScore;//学生客观题的得分
    private String subjectiveTotalScore;//主观题的总分
    private String studentSubjectTotalScore;//学生主观题的得分
    private boolean hasSubjectProblem;//是否有主观题
    private String taskTitle;//任务的title
    private int ansWrongCount;//答错题的数量
    private List<ExerciseItem> exercise_item_list;

    public int getAnsWrongCount() {
        return ansWrongCount;
    }

    public void setAnsWrongCount(int ansWrongCount) {
        this.ansWrongCount = ansWrongCount;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public boolean isHasSubjectProblem() {
        return hasSubjectProblem;
    }

    public void setHasSubjectProblem(boolean hasSubjectProblem) {
        this.hasSubjectProblem = hasSubjectProblem;
    }

    public String getObjectiveTotalScore() {
        return objectiveTotalScore;
    }

    public void setObjectiveTotalScore(String objectiveTotalScore) {
        this.objectiveTotalScore = objectiveTotalScore;
    }

    public String getStudentObjectiveTotalScore() {
        return studentObjectiveTotalScore;
    }

    public void setStudentObjectiveTotalScore(String studentObjectiveTotalScore) {
        this.studentObjectiveTotalScore = studentObjectiveTotalScore;
    }

    public String getSubjectiveTotalScore() {
        return subjectiveTotalScore;
    }

    public void setSubjectiveTotalScore(String subjectiveTotalScore) {
        this.subjectiveTotalScore = subjectiveTotalScore;
    }

    public String getStudentSubjectTotalScore() {
        return studentSubjectTotalScore;
    }

    public void setStudentSubjectTotalScore(String studentSubjectTotalScore) {
        this.studentSubjectTotalScore = studentSubjectTotalScore;
    }

    public int getLearnType() {
        return learnType;
    }

    public void setLearnType(int learnType) {
        this.learnType = learnType;
    }

    public String getCreate_id() {
        return create_id;
    }

    public void setCreate_id(String create_id) {
        this.create_id = create_id;
    }

    public String getCreate_name() {
        return create_name;
    }

    public void setCreate_name(String create_name) {
        this.create_name = create_name;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getTotal_score() {
        return total_score;
    }

    public void setTotal_score(String total_score) {
        this.total_score = total_score;
    }

    public String getStudent_name() {
        return student_name;
    }

    public void setStudent_name(String student_name) {
        this.student_name = student_name;
    }

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public String getStudent_commit_time() {
        return student_commit_time;
    }

    public void setStudent_commit_time(String student_commit_time) {
        this.student_commit_time = student_commit_time;
    }

    public String getStudent_score() {
        return student_score;
    }

    public void setStudent_score(String student_score) {
        this.student_score = student_score;
    }

    public String getNeed_read_answer_area() {
        return need_read_answer_area;
    }

    public void setNeed_read_answer_area(String need_read_answer_area) {
        this.need_read_answer_area = need_read_answer_area;
    }

    public List<ExerciseItem> getExercise_item_list() {
        return exercise_item_list;
    }

    public void setExercise_item_list(List<ExerciseItem> exercise_item_list) {
        this.exercise_item_list = exercise_item_list;
    }
}
