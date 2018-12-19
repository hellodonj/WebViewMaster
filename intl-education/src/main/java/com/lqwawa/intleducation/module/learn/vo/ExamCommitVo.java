package com.lqwawa.intleducation.module.learn.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;

/**
 * @author: wangchao
 * @date: 2018/04/14
 * @desc:
 */
public class ExamCommitVo extends BaseVo {

    /**
     * isDelete : false
     * id : 981
     * createTime : 1523625788000
     * paperId : 374
     * weights : 0
     * updateTime : null
     * isFinish : true
     * courseExamId : 1385
     * score : 10
     * class : com.oosic.interCourse.dao.model.UserExam
     * headPic : http://filetestop.lqwawa.com/UploadFiles/20160818041230/bf6da6a1-45a5-482f-9348-5c539a00ac59/7d316c72-65a1-4b50-8297-8da518d8d0ed.jpg
     * type : 1
     * createName : s5000
     * deleteTime : null
     * createId : bf6da6a1-45a5-482f-9348-5c539a00ac59
     */

    private boolean isDelete;
    private int id;
    private long createTime;
    private int paperId;
    private int weights;
    private Object updateTime;
    private boolean isFinish;
    private int courseExamId;
    private int score;
    private String classX;
    private String headPic;
    private int type;
    private String createName;
    private Object deleteTime;
    private String createId;

    public boolean isIsDelete() {
        return isDelete;
    }

    public void setIsDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getPaperId() {
        return paperId;
    }

    public void setPaperId(int paperId) {
        this.paperId = paperId;
    }

    public int getWeights() {
        return weights;
    }

    public void setWeights(int weights) {
        this.weights = weights;
    }

    public Object getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Object updateTime) {
        this.updateTime = updateTime;
    }

    public boolean isIsFinish() {
        return isFinish;
    }

    public void setIsFinish(boolean isFinish) {
        this.isFinish = isFinish;
    }

    public int getCourseExamId() {
        return courseExamId;
    }

    public void setCourseExamId(int courseExamId) {
        this.courseExamId = courseExamId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getClassX() {
        return classX;
    }

    public void setClassX(String classX) {
        this.classX = classX;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public Object getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(Object deleteTime) {
        this.deleteTime = deleteTime;
    }

    public String getCreateId() {
        return createId;
    }

    public void setCreateId(String createId) {
        this.createId = createId;
    }
}
