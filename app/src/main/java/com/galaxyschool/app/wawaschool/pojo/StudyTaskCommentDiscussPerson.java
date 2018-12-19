package com.galaxyschool.app.wawaschool.pojo;

import java.util.List;

/**
 * Created by KnIghT on 16-6-29.
 */
public class StudyTaskCommentDiscussPerson {
    private List<StudytaskComment> CommentList;
    private List<DiscussPersonList>  DiscussPersonList;

    public List<DiscussPersonList> getDiscussPersonList() {
        return DiscussPersonList;
    }

    public void setDiscussPersonList(List<DiscussPersonList> discussPersonList) {
        DiscussPersonList = discussPersonList;
    }

    public List<StudytaskComment> getCommentList() {
        return CommentList;
    }

    public void setCommentList(List<StudytaskComment> commentList) {
        CommentList = commentList;
    }
}
