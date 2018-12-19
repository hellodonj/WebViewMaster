package com.galaxyschool.app.wawaschool.pojo;

import java.util.List;

/**
 * Created by KnIghT on 16-6-21.
 */
public class StudytaskComment {
    private int Id;  //评论表ID
    private int TaskId;     //任务ID 评论对应的任务ID
    private int ParentId;   //父级评论ID 当评论的对象是针对已存在的学生评论时必填，否则传0
    private String Comments;    //评论内容
    private String CommentId;    //评论人ID
    private String CommentName;  //评论人名称
    private String CommentHeadPicUrl; //评论人头像
    private String CommentToId;  //评论对象ID 当评论的对象是针对已存在的学生评论时必填，否则传空
    private String CommentToName;   //评论对象名称 当评论的对象是针对已存在的学生评论时必填，否则传空
    private String CommentTime;//评论时间
    private int PraiseCount;
    private boolean Deleted;    //是否删除 false-未删除，true-已删除

    List<StudytaskComment> Children;    //子评论集合 当某条评论上有其他人追加的评论时此值有值

    private boolean hasPraised;//是否点过赞，手动增加的字段。

    public void setHasPraised(boolean hasPraised) {
        this.hasPraised = hasPraised;
    }

    public boolean isHasPraised() {
        return hasPraised;
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

    public String getComments() {
        return Comments;
    }

    public void setComments(String comments) {
        Comments = comments;
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

    public String getCommentHeadPicUrl() {
        return CommentHeadPicUrl;
    }

    public void setCommentHeadPicUrl(String commentHeadPicUrl) {
        CommentHeadPicUrl = commentHeadPicUrl;
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

    public String getCommentTime() {
        return CommentTime;
    }

    public void setCommentTime(String commentTime) {
        CommentTime = commentTime;
    }

    public int getPraiseCount() {
        return PraiseCount;
    }

    public void setPraiseCount(int praiseCount) {
        PraiseCount = praiseCount;
    }

    public boolean isDeleted() {
        return Deleted;
    }

    public void setDeleted(boolean deleted) {
        Deleted = deleted;
    }

    public List<StudytaskComment> getChildren() {
        return Children;
    }

    public void setChildren(List<StudytaskComment> children) {
        Children = children;
    }
}
