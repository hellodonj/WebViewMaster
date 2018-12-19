package com.lqwawa.intleducation.module.learn.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;

import java.util.List;

/**
 * Created by XChen on 2017/7/10.
 * email:man0fchina@foxmail.com
 */

public class LqTaskCommentVo extends BaseVo {
    private String CommentHeadPicUrl;
    private int Id;
    private int ExtId;
    private int Type;
    private int TaskId;
    private int ParentId;
    private int PraiseCount;
    private String Comments;
    private String CommentTime;
    private String CommentId;
    private String CommentName;
    private String HeadPicUrl;
    private String HeadPicUrlSrc;
    private String CommentToId;
    private String CommentToName;
    private boolean Deleted;
    private List<LqTaskCommentVo> Children;

    public String getCommentHeadPicUrl() {
        return CommentHeadPicUrl;
    }

    public void setCommentHeadPicUrl(String CommentHeadPicUrl) {
        this.CommentHeadPicUrl = CommentHeadPicUrl;
    }

    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public int getExtId() {
        return ExtId;
    }

    public void setExtId(int ExtId) {
        this.ExtId = ExtId;
    }

    public int getType() {
        return Type;
    }

    public void setType(int Type) {
        this.Type = Type;
    }

    public int getTaskId() {
        return TaskId;
    }

    public void setTaskId(int TaskId) {
        this.TaskId = TaskId;
    }

    public int getParentId() {
        return ParentId;
    }

    public void setParentId(int ParentId) {
        this.ParentId = ParentId;
    }

    public int getPraiseCount() {
        return PraiseCount;
    }

    public void setPraiseCount(int PraiseCount) {
        this.PraiseCount = PraiseCount;
    }

    public String getComments() {
        return Comments;
    }

    public void setComments(String Comments) {
        this.Comments = Comments;
    }

    public String getCommentTime() {
        return CommentTime;
    }

    public void setCommentTime(String CommentTime) {
        this.CommentTime = CommentTime;
    }

    public String getCommentId() {
        return CommentId;
    }

    public void setCommentId(String CommentId) {
        this.CommentId = CommentId;
    }

    public String getCommentName() {
        return CommentName;
    }

    public void setCommentName(String CommentName) {
        this.CommentName = CommentName;
    }

    public String getHeadPicUrl() {
        return HeadPicUrl;
    }

    public void setHeadPicUrl(String HeadPicUrl) {
        this.HeadPicUrl = HeadPicUrl;
    }

    public String getHeadPicUrlSrc() {
        return HeadPicUrlSrc;
    }

    public void setHeadPicUrlSrc(String HeadPicUrlSrc) {
        this.HeadPicUrlSrc = HeadPicUrlSrc;
    }

    public String getCommentToId() {
        return CommentToId;
    }

    public void setCommentToId(String CommentToId) {
        this.CommentToId = CommentToId;
    }

    public String getCommentToName() {
        return CommentToName;
    }

    public void setCommentToName(String CommentToName) {
        this.CommentToName = CommentToName;
    }

    public boolean isDeleted() {
        return Deleted;
    }

    public void setDeleted(boolean Deleted) {
        this.Deleted = Deleted;
    }

    public List<LqTaskCommentVo> getChildren() {
        return Children;
    }

    public void setChildren(List<LqTaskCommentVo> Children) {
        this.Children = Children;
    }
}
