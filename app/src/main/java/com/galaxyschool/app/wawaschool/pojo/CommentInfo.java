package com.galaxyschool.app.wawaschool.pojo;

/**
 * Created by Administrator on 2016.06.27.
 * 评论内容
 */
public class CommentInfo {

    private int Id;
    private int TaskId;
    private int ParentId;
    private int PraiseCount;
    private String CommentHeadPicUrl;
    private String Comments;
    private String CommentTime;
    private String CommentId;
    private String CommentName;
    private String CommentToId;
    private String CommentToName;
    private boolean Deleted;

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
}
