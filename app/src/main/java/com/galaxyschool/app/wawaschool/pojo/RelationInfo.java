package com.galaxyschool.app.wawaschool.pojo;

import java.io.Serializable;

public class RelationInfo implements Serializable{
    private String Id;
    private String ParentId;
    private String ParentNickName;
    private String ParentRealName;
    private String ChildId;
    private String ChildNickName;
    private String ChildRealName;
    private int State;
    private int RelationType;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getParentId() {
        return ParentId;
    }

    public void setParentId(String parentId) {
        ParentId = parentId;
    }

    public String getParentNickName() {
        return ParentNickName;
    }

    public void setParentNickName(String parentNickName) {
        ParentNickName = parentNickName;
    }

    public String getParentRealName() {
        return ParentRealName;
    }

    public void setParentRealName(String parentRealName) {
        ParentRealName = parentRealName;
    }

    public String getChildId() {
        return ChildId;
    }

    public void setChildId(String childId) {
        ChildId = childId;
    }

    public String getChildNickName() {
        return ChildNickName;
    }

    public void setChildNickName(String childNickName) {
        ChildNickName = childNickName;
    }

    public String getChildRealName() {
        return ChildRealName;
    }

    public void setChildRealName(String childRealName) {
        ChildRealName = childRealName;
    }

    public int getState() {
        return State;
    }

    public void setState(int state) {
        State = state;
    }

    public int getRelationType() {
        return RelationType;
    }

    public void setRelationType(int relationType) {
        RelationType = relationType;
    }
}
