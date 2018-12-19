package com.galaxyschool.app.wawaschool.pojo;

import java.util.List;

/**
 * Created by Administrator on 2016.06.27.
 */
public class CommentObjectInfo {

    private List<CommentListInfo> CommentList;
    private List<DiscussPersonList> DiscussPersonList;

    public List<CommentListInfo> getCommentList() {
        return CommentList;
    }

    public void setCommentList(List<CommentListInfo> commentList) {
        CommentList = commentList;
    }

    public List<com.galaxyschool.app.wawaschool.pojo.DiscussPersonList> getDiscussPersonList() {
        return DiscussPersonList;
    }

    public void setDiscussPersonList(List<com.galaxyschool.app.wawaschool.pojo.DiscussPersonList> discussPersonList) {
        DiscussPersonList = discussPersonList;
    }
}
