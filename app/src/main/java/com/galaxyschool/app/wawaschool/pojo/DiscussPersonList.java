package com.galaxyschool.app.wawaschool.pojo;

import java.io.Serializable;

/**
 * Created by Administrator on 2016.06.29.
 */
public class DiscussPersonList implements Serializable{

    private String HeadPicUrl;
    private String Name;
    private String Id;

    public String getHeadPicUrl() {
        return HeadPicUrl;
    }

    public void setHeadPicUrl(String headPicUrl) {
        HeadPicUrl = headPicUrl;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }
}
