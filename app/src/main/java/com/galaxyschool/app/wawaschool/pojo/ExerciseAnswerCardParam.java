package com.galaxyschool.app.wawaschool.pojo;

import com.lqwawa.intleducation.module.tutorial.marking.choice.QuestionResourceModel;

import java.io.Serializable;
import java.util.List;

/**
 * 答题卡信息处理类
 */

public class ExerciseAnswerCardParam implements Serializable {
    //是不是任务单自动批阅的标识（答题的时候传true）
    private boolean showExerciseButton;
    //显不显示答案解析
    private boolean showExerciseNode;
    //server返回的答题卡jsonString
    private String exerciseAnswerString;
    //taskId
    private String taskId;
    //资源的resId
    private String resId;
    //学生的studentId 家长传学生的
    private String studentId;
    //答题卡的总分
    private String exerciseTotalScore;
    //学生提交列表项的commitTaskId
    private int commitTaskId;
    //学生提交的姓名
    private String studentName;
    //任务的title
    private String commitTaskTitle;
    //是不是班主任的角色
    private boolean isHeadMaster;
    //角色信息
    private int roleType;
    //主编
    private boolean isOnlineReporter;
    //小编
    private boolean isOnlineHost;
    //题号的数据
    private ExerciseItem exerciseItem;
    //方向
    private int screenType;
    //学生的总分
    private String studentTotalScore;
    //学生还有未完成主观题未批阅数量
    private int unFinishSubjectCount;
    //学生提交的答题卡信息
    private String studentCommitAnswerString;
    //每道题的正确信息（包括学生的作答情况）
    private List<ExerciseItem> questionDetails;
    private boolean hasSubjectProblem;//是否包含主观题
    private boolean fromOnlineStudyTask;//来自在线课堂的数据
    private String schoolId;
    private String schoolName;
    private String classId;
    private String className;
    private String taskScoreRemark;//老师评语
    private CommitTask commitTask;
    private StudyTask studyTask;
    private int pageIndex;
    private int exerciseIndex;
    private QuestionResourceModel markModel;//申请批阅的数据

    public QuestionResourceModel getMarkModel() {
        return markModel;
    }

    public void setMarkModel(QuestionResourceModel markModel) {
        this.markModel = markModel;
    }


    public int getExerciseIndex() {
        return exerciseIndex;
    }

    public void setExerciseIndex(int exerciseIndex) {
        this.exerciseIndex = exerciseIndex;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public CommitTask getCommitTask() {
        return commitTask;
    }

    public void setCommitTask(CommitTask commitTask) {
        this.commitTask = commitTask;
    }

    public StudyTask getStudyTask() {
        return studyTask;
    }

    public void setStudyTask(StudyTask studyTask) {
        this.studyTask = studyTask;
    }

    public String getTaskScoreRemark() {
        return taskScoreRemark;
    }

    public void setTaskScoreRemark(String taskScoreRemark) {
        this.taskScoreRemark = taskScoreRemark;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public boolean isFromOnlineStudyTask() {
        return fromOnlineStudyTask;
    }

    public void setFromOnlineStudyTask(boolean fromOnlineStudyTask) {
        this.fromOnlineStudyTask = fromOnlineStudyTask;
    }

    public boolean isHasSubjectProblem() {
        return hasSubjectProblem;
    }

    public void setHasSubjectProblem(boolean hasSubjectProblem) {
        this.hasSubjectProblem = hasSubjectProblem;
    }

    public List<ExerciseItem> getQuestionDetails() {
        return questionDetails;
    }

    public void setQuestionDetails(List<ExerciseItem> questionDetails) {
        this.questionDetails = questionDetails;
    }

    public String getStudentCommitAnswerString() {
        return studentCommitAnswerString;
    }

    public void setStudentCommitAnswerString(String studentCommitAnswerString) {
        this.studentCommitAnswerString = studentCommitAnswerString;
    }

    public int getUnFinishSubjectCount() {
        return unFinishSubjectCount;
    }

    public void setUnFinishSubjectCount(int unFinishSubjectCount) {
        this.unFinishSubjectCount = unFinishSubjectCount;
    }

    public String getStudentTotalScore() {
        return studentTotalScore;
    }

    public void setStudentTotalScore(String studentTotalScore) {
        this.studentTotalScore = studentTotalScore;
    }

    public int getScreenType() {
        return screenType;
    }

    public void setScreenType(int screenType) {
        this.screenType = screenType;
    }

    public ExerciseItem getExerciseItem() {
        return exerciseItem;
    }

    public void setExerciseItem(ExerciseItem exerciseItem) {
        this.exerciseItem = exerciseItem;
    }

    public boolean isHeadMaster() {
        return isHeadMaster;
    }

    public void setIsHeadMaster(boolean headMaster) {
        isHeadMaster = headMaster;
    }

    public int getRoleType() {
        return roleType;
    }

    public void setRoleType(int roleType) {
        this.roleType = roleType;
    }

    public boolean isOnlineReporter() {
        return isOnlineReporter;
    }

    public void setIsOnlineReporter(boolean onlineReporter) {
        isOnlineReporter = onlineReporter;
    }

    public boolean isOnlineHost() {
        return isOnlineHost;
    }

    public void setIsOnlineHost(boolean onlineHost) {
        isOnlineHost = onlineHost;
    }

    public String getCommitTaskTitle() {
        return commitTaskTitle;
    }

    public void setCommitTaskTitle(String commitTaskTitle) {
        this.commitTaskTitle = commitTaskTitle;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public int getCommitTaskId() {
        return commitTaskId;
    }

    public void setCommitTaskId(int commitTaskId) {
        this.commitTaskId = commitTaskId;
    }

    public String getExerciseTotalScore() {
        return exerciseTotalScore;
    }

    public void setExerciseTotalScore(String exerciseTotalScore) {
        this.exerciseTotalScore = exerciseTotalScore;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getResId() {
        return resId;
    }

    public void setResId(String resId) {
        this.resId = resId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public boolean isShowExerciseButton() {
        return showExerciseButton;
    }

    public void setShowExerciseButton(boolean showExerciseButton) {
        this.showExerciseButton = showExerciseButton;
    }

    public boolean isShowExerciseNode() {
        return showExerciseNode;
    }

    public void setShowExerciseNode(boolean showExerciseNode) {
        this.showExerciseNode = showExerciseNode;
    }

    public String getExerciseAnswerString() {
        return exerciseAnswerString;
    }

    public void setExerciseAnswerString(String exerciseAnswerString) {
        this.exerciseAnswerString = exerciseAnswerString;
    }
}
