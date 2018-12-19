package com.galaxyschool.app.wawaschool.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by renbao on 16-3-23.
 */
public class Calalog implements Serializable{
    private String Id;//����id
    private String Name;//�½���
    private List<Calalog> Children;//�ڼ���

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public List<Calalog> getChildren() {
        return Children;
    }

    public void setChildren(List<Calalog> children) {
        Children = children;
    }
}
