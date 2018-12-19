package com.galaxyschool.app.wawaschool.pojo;

import java.io.Serializable;

public class LearnTaskPageInfo implements Serializable {
    private LearnTaskInfo exercise_items;
    private String version;
    private String include_skin;
    private String res_id;
    private String name;
    private String member_id;
    private String is_from_school;

    public LearnTaskInfo getExercise_items() {
        return exercise_items;
    }

    public void setExercise_items(LearnTaskInfo exercise_items) {
        this.exercise_items = exercise_items;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getInclude_skin() {
        return include_skin;
    }

    public void setInclude_skin(String include_skin) {
        this.include_skin = include_skin;
    }

    public String getRes_id() {
        return res_id;
    }

    public void setRes_id(String res_id) {
        this.res_id = res_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getIs_from_school() {
        return is_from_school;
    }

    public void setIs_from_school(String is_from_school) {
        this.is_from_school = is_from_school;
    }
}
