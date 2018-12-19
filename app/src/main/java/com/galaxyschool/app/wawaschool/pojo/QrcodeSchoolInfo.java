package com.galaxyschool.app.wawaschool.pojo;

import java.io.Serializable;

public class QrcodeSchoolInfo implements Serializable{
    private String abbreviate;
    private String address;
    private String id;
    private String intro;
    private String logoUrl;
    private String qrcode;
    private String sname;

    public String getAbbreviate() {
        return abbreviate;
    }

    public void setAbbreviate(String abbreviate) {
        this.abbreviate = abbreviate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }
}
