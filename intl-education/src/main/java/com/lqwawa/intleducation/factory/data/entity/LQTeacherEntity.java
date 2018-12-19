package com.lqwawa.intleducation.factory.data.entity;

import com.lqwawa.intleducation.base.vo.BaseVo;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 机构老师实体
 * @date 2018/06/05 16:50
 * @history v1.0
 * **********************************
 */
public class LQTeacherEntity extends BaseVo{

    private String MemberId;
    private String RealName;
    private String NickName;
    private String HeadPicUrl;
    private String HeadPicUrlSrc;
    private Object Mobile;
    private Object BindMobile;
    private String CreatedOn;
    private int RowNumber;

    public String getMemberId() {
        return MemberId;
    }

    public void setMemberId(String MemberId) {
        this.MemberId = MemberId;
    }

    public String getRealName() {
        return RealName;
    }

    public void setRealName(String RealName) {
        this.RealName = RealName;
    }

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String NickName) {
        this.NickName = NickName;
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

    public Object getMobile() {
        return Mobile;
    }

    public void setMobile(Object Mobile) {
        this.Mobile = Mobile;
    }

    public Object getBindMobile() {
        return BindMobile;
    }

    public void setBindMobile(Object BindMobile) {
        this.BindMobile = BindMobile;
    }

    public String getCreatedOn() {
        return CreatedOn;
    }

    public void setCreatedOn(String CreatedOn) {
        this.CreatedOn = CreatedOn;
    }

    public int getRowNumber() {
        return RowNumber;
    }

    public void setRowNumber(int RowNumber) {
        this.RowNumber = RowNumber;
    }
}
