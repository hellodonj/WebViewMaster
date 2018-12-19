package com.galaxyschool.app.wawaschool.pojo;

import java.util.List;

/**
 * Created by Administrator on 2016.06.27.
 */
public class CommentListInfo {

    private int Id;//评论表ID
    private int TaskId;//任务ID
    private int ParentId;//父级评论ID
    private int PraiseCount;//评论数
    private String CommentHeadPicUrl;//评论人头像地址
    private String Comments;//评论内容
    private String CommentTime;//评论时间
    private String CommentId;//评论人ID
    private String CommentName;//评论人名称
    private String CommentToId;//评论对象ID
    private String CommentToName;//评论对象名称
    private boolean Deleted;//是否删除
    private boolean hasPraised;//是否已经点过赞，自己增加的，非服务端返回。
    private List<CommentInfo> Children;//子评论集合

    public void setHasPraised(boolean hasPraised) {
        this.hasPraised = hasPraised;
    }

    public boolean isHasPraised() {
        return hasPraised;
    }

    public void setCommentHeadPicUrl(String commentHeadPicUrl) {
        CommentHeadPicUrl = commentHeadPicUrl;
    }

    public String getCommentHeadPicUrl() {
        return CommentHeadPicUrl;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getTaskId() {
        return TaskId;
    }

    public void setTaskId(int taskId) {
        TaskId = taskId;
    }

    public int getParentId() {
        return ParentId;
    }

    public void setParentId(int parentId) {
        ParentId = parentId;
    }

    public int getPraiseCount() {
        return PraiseCount;
    }

    public void setPraiseCount(int praiseCount) {
        PraiseCount = praiseCount;
    }

    public String getComments() {
        return Comments;
    }

    public void setComments(String comments) {
        Comments = comments;
    }

    public String getCommentTime() {
        return CommentTime;
    }

    public void setCommentTime(String commentTime) {
        CommentTime = commentTime;
    }

    public String getCommentId() {
        return CommentId;
    }

    public void setCommentId(String commentId) {
        CommentId = commentId;
    }

    public String getCommentName() {
        return CommentName;
    }

    public void setCommentName(String commentName) {
        CommentName = commentName;
    }

    public String getCommentToId() {
        return CommentToId;
    }

    public void setCommentToId(String commentToId) {
        CommentToId = commentToId;
    }

    public String getCommentToName() {
        return CommentToName;
    }

    public void setCommentToName(String commentToName) {
        CommentToName = commentToName;
    }

    public boolean isDeleted() {
        return Deleted;
    }

    public void setDeleted(boolean deleted) {
        Deleted = deleted;
    }

    public List<CommentInfo> getChildren() {
        return Children;
    }

    public void setChildren(List<CommentInfo> children) {
        Children = children;
    }
}
