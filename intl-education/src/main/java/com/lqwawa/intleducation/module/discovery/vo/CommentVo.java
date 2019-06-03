package com.lqwawa.intleducation.module.discovery.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;

import java.util.List;

/**
 * Created by XChen on 2016/11/14.
 * email:man0fchina@foxmail.com
 */

public class CommentVo extends BaseVo {
    private String createTime;// "2016-11-07 13:53:03",
    private List<CommentVo> children;// [],
    private String courseId;// 174,
    private int type;// 1,
    private String createName;// "苏粟",
    private String commentId;// 372,
    private String replyNum;// 0,
    private String starLevel;// 0,
    private String content;// "dewdwe",
    private String id;// 386,
    private String isDelete;// false,
    private String praiseNum;// 0,
    private String thumbnail;// "http://192.168.99.181/course/interCourse/img/2016/06/26/d59e89a8-61b8-45c2-945f-fbdfc78e2c5a.jpg",
    private String deleteTime;// "",
    private String courseName;// "化学实验",
    private String createId;// 8
    private String headPic;

    public List<CommentVo> getChildren() {
        return children;
    }

    public void setChildren(List<CommentVo> children) {
        this.children = children;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCreateId() {
        return createId;
    }

    public void setCreateId(String createId) {
        this.createId = createId;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(String deleteTime) {
        this.deleteTime = deleteTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }

    public String getPraiseNum() {
        return praiseNum;
    }

    public void setPraiseNum(String praiseNum) {
        this.praiseNum = praiseNum;
    }

    public String getReplyNum() {
        return replyNum;
    }

    public void setReplyNum(String replyNum) {
        this.replyNum = replyNum;
    }

    public String getStarLevel() {
        return starLevel;
    }

    public void setStarLevel(String starLevel) {
        this.starLevel = starLevel;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getHeadPic() {
        return headPic;
    }

    public CommentVo setHeadPic(String headPic) {
        this.headPic = headPic;
        return this;
    }
}
