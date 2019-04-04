package com.lqwawa.intleducation.module.discovery.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;

import java.util.List;

/**
 * Created by XChen on 2016/11/14.
 * email:man0fchina@foxmail.com
 */

public class CourseDetailsVo extends BaseVo {

    public static final int SUCCEED = 0;

    private List<CourseVo> course;
    private List<ChapterVo> chapterList;
    private List<ChapterVo> chapters;
    private List<CommentVo> commentList;
    private int code;// 0
    private int starLevel;
    private boolean isAllowComment;
    private boolean isCollect;
    private boolean isExpire;
    private boolean isBuy;
    private boolean isJoin;
    private String chapterName;
    private String sectionName;
    private int cexamSize;
    private boolean buyAll;
    private int courseScore;
    private int total;
    // 绑定的班级,学校
    private String bindSchoolId;
    private String bindClassId;

    // V5.14新加的字段
    private int isOrganTutorStatus;

    public CourseDetailsVo(){
        course = null;
        chapterList = null;
        chapters = null;
        commentList = null;
    }

    public int getCexamSize() {
        return cexamSize;
    }

    public void setCexamSize(int cexamSize) {
        this.cexamSize = cexamSize;
    }

    public List<ChapterVo> getChapters() {
        return chapters;
    }

    public void setChapters(List<ChapterVo> chapters) {
        this.chapters = chapters;
    }

    public int getCourseScore() {
        return courseScore;
    }

    public void setCourseScore(int courseScore) {
        this.courseScore = courseScore;
    }

    public boolean isAllowComment() {
        return isAllowComment;
    }

    public void setAllowComment(boolean allowComment) {
        isAllowComment = allowComment;
    }

    public boolean isIsCollect() {
        return isCollect;
    }

    public void setIsCollect(boolean collect) {
        isCollect = collect;
    }

    public List<ChapterVo> getChapterList() {
        return chapterList;
    }

    public void setChapterList(List<ChapterVo> chapterList) {
        this.chapterList = chapterList;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<CommentVo> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<CommentVo> commentList) {
        this.commentList = commentList;
    }

    public List<CourseVo> getCourse() {
        if(course == null) return null;
        // 遍历 如果课程有绑定到班级
        for (CourseVo vo:course) {
            vo.setBindSchoolId(getBindSchoolId());
            vo.setBindClassId(getBindClassId());
        }
        return course;
    }

    public void setCourse(List<CourseVo> course) {
        this.course = course;
    }
    public boolean getIsAllowComment() {
        return isAllowComment;
    }

    public void setIsAllowComment(boolean isAllowComment) {
        this.isAllowComment = isAllowComment;
    }

    public int getStarLevel() {
        return starLevel;
    }

    public void setStarLevel(int starLevel) {
        this.starLevel = starLevel;
    }
    public boolean isIsBuy() {
        return isBuy;
    }

    public void setIsBuy(boolean buy) {
        isBuy = buy;
    }

    public boolean isIsJoin() {
        return isJoin;
    }

    public void setIsJoin(boolean join) {
        isJoin = join;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public boolean isBuyAll() {
        return buyAll;
    }

    public void setBuyAll(boolean buyAll) {
        this.buyAll = buyAll;
    }

    public boolean isIsExpire() {
        return isExpire;
    }

    public void setIsExpire(boolean expire) {
        isExpire = expire;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getBindSchoolId() {
        return bindSchoolId;
    }

    public void setBindSchoolId(String bindSchoolId) {
        this.bindSchoolId = bindSchoolId;
    }

    public String getBindClassId() {
        return bindClassId;
    }

    public void setBindClassId(String bindClassId) {
        this.bindClassId = bindClassId;
    }

    public int getIsOrganTutorStatus() {
        return isOrganTutorStatus;
    }

    public void setIsOrganTutorStatus(int isOrganTutorStatus) {
        this.isOrganTutorStatus = isOrganTutorStatus;
    }

    /**
     * 网络请求是否成功
     * @return true 请求成功 false 请求失败
     */
    public boolean isSucceed(){
        return code == SUCCEED;
    }
}
