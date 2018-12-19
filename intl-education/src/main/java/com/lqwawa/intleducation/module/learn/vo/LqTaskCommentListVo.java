package com.lqwawa.intleducation.module.learn.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;

import java.util.List;

/**
 * Created by XChen on 2017/7/10.
 * email:man0fchina@foxmail.com
 */

public class LqTaskCommentListVo extends BaseVo {
    List<LqTaskCommentVo> CommentList;
    List<LqTaskDiscussPersonVo> DiscussPersonList;

    public List<LqTaskCommentVo> getCommentList() {
        return CommentList;
    }

    public void setCommentList(List<LqTaskCommentVo> commentList) {
        CommentList = commentList;
    }

    public List<LqTaskDiscussPersonVo> getDiscussPersonList() {
        return DiscussPersonList;
    }

    public void setDiscussPersonList(List<LqTaskDiscussPersonVo> discussPersonList) {
        DiscussPersonList = discussPersonList;
    }
}
