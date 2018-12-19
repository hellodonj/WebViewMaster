package com.galaxyschool.app.wawaschool.pojo;

import java.util.List;

/**
 * Created by E450 on 2017/02/28.
 * 批改网返回的句子信息
 */
public class PigaiResultSentenceInfo {

    private int sid;//第几句
    private int pid;// 段落, 从1开始
    private String text;//句子内容
    private List<PigaiResultCommentInfo> comment;//评论数组内容

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<PigaiResultCommentInfo> getComment() {
        return comment;
    }

    public void setComment(List<PigaiResultCommentInfo> comment) {
        this.comment = comment;
    }
}
