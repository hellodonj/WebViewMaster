package com.galaxyschool.app.wawaschool.pojo;

import java.io.Serializable;

public class RoleInfo implements Serializable{
    private String Id;
    private String ApplyName;
    private String Role;
    private int RoleType;
    private int State;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getApplyName() {
        return ApplyName;
    }

    public void setApplyName(String applyName) {
        ApplyName = applyName;
    }

    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = role;
    }

    public int getRoleType() {
        return RoleType;
    }

    public void setRoleType(int roleType) {
        RoleType = roleType;
    }

    public int getState() {
        return State;
    }

    public void setState(int state) {
        State = state;
    }
}
