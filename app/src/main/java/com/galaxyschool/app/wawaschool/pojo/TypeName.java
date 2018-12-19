package com.galaxyschool.app.wawaschool.pojo;

import java.util.List;

public class TypeName {

    String Id;
    String Name;
    List<String> Level_list;

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

    public List<String> getLevel_list() {
        return Level_list;
    }

    public void setLevel_list(List<String> level_list) {
        Level_list = level_list;
    }

}
