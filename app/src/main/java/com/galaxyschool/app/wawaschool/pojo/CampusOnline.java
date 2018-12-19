package com.galaxyschool.app.wawaschool.pojo;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/1/17.
 */

public class CampusOnline implements Serializable{
    private String lname;
    private String limg;
    private String link;
    private String createtime;

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getLimg() {
        return limg;
    }

    public void setLimg(String limg) {
        this.limg = limg;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }
}
