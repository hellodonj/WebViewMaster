package com.lqwawa.intleducation.factory.data.entity;

import com.lqwawa.intleducation.base.vo.BaseVo;
import com.lqwawa.intleducation.module.discovery.vo.CommentVo;

import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 在线课堂评论数据实体
 * @date 2018/06/02 17:38
 * @history v1.0
 * **********************************
 */
public class OnlineCommentEntity extends BaseVo{

    private int total;
    private int starLevel;
    private boolean isAllowComment;
    private boolean isScored;
    private List<CommentVo> commentList;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getStarLevel() {
        return starLevel;
    }

    public void setStarLevel(int starLevel) {
        this.starLevel = starLevel;
    }

    public boolean isIsAllowComment() {
        return isAllowComment;
    }

    public void setIsAllowComment(boolean isAllowComment) {
        this.isAllowComment = isAllowComment;
    }

    public boolean isIsScored() {
        return isScored;
    }

    public void setIsScored(boolean isScored) {
        this.isScored = isScored;
    }

    public List<CommentVo> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<CommentVo> commentList) {
        this.commentList = commentList;
    }
}
