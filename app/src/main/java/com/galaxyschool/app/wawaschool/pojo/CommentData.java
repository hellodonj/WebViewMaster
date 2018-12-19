package com.galaxyschool.app.wawaschool.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author: wangchao
 * Time: 2015/11/11 12:31
 */

public class CommentData implements Parcelable{

    private String memberid;
    private String answertime;
    private String createtime;
    private long id;
    private String headpic;
    private String account;
    private String question;
    private String createname;
    private long courseid;
    private int praisenum;
    private boolean isPraise;

    public CommentData() {

    }

    public CommentData(
            String memberid, String answertime, String createtime, long id, String headpic, String account,
            String question, String createname, long courseid, int praisenum) {
        this.memberid = memberid;
        this.answertime = answertime;
        this.createtime = createtime;
        this.id = id;
        this.headpic = headpic;
        this.account = account;
        this.question = question;
        this.createname = createname;
        this.courseid = courseid;
        this.praisenum = praisenum;
    }

    public String getMemberid() {
        return memberid;
    }

    public void setMemberid(String memberid) {
        this.memberid = memberid;
    }

    public String getAnswertime() {
        return answertime;
    }

    public void setAnswertime(String answertime) {
        this.answertime = answertime;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getHeadpic() {
        return headpic;
    }

    public void setHeadpic(String headpic) {
        this.headpic = headpic;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCreatename() {
        return createname;
    }

    public void setCreatename(String createname) {
        this.createname = createname;
    }

    public long getCourseid() {
        return courseid;
    }

    public void setCourseid(long courseid) {
        this.courseid = courseid;
    }

    public int getPraisenum() {
        return praisenum;
    }

    public void setPraisenum(int praisenum) {
        this.praisenum = praisenum;
    }

    public boolean isPraise() {
        return isPraise;
    }

    public void setIsPraise(boolean isPraise) {
        this.isPraise = isPraise;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.memberid);
        dest.writeString(this.answertime);
        dest.writeString(this.createtime);
        dest.writeLong(this.id);
        dest.writeString(this.headpic);
        dest.writeString(this.account);
        dest.writeString(this.question);
        dest.writeString(this.createname);
        dest.writeLong(this.courseid);
        dest.writeInt(this.praisenum);
    }

    protected CommentData(Parcel in) {
        this.memberid = in.readString();
        this.answertime = in.readString();
        this.createtime = in.readString();
        this.id = in.readLong();
        this.headpic = in.readString();
        this.account = in.readString();
        this.question = in.readString();
        this.createname = in.readString();
        this.courseid = in.readLong();
        this.praisenum = in.readInt();
    }

    public static final Creator<CommentData> CREATOR = new Creator<CommentData>() {
        public CommentData createFromParcel(Parcel source) {
            return new CommentData(source);
        }

        public CommentData[] newArray(int size) {
            return new CommentData[size];
        }
    };
}
