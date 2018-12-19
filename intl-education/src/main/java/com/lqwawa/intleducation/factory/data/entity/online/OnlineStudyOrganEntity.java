package com.lqwawa.intleducation.factory.data.entity.online;

import com.lqwawa.intleducation.base.vo.BaseVo;

/**
 * @author medici
 * @desc 在线学习机构数据
 */
public class OnlineStudyOrganEntity extends BaseVo{

    private String id;
    private String thumbnail;
    private String email;
    private String address;
    private String name;
    private String mobile;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

}
