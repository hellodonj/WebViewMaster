package com.galaxyschool.app.wawaschool.pojo;

import java.io.Serializable;
import java.util.List;

public class SchoolMemberModel implements Serializable {
    private List<SchoolClassMemberDetail> Data;

    public SchoolMemberModel() {
    }

    public List<SchoolClassMemberDetail> getData() {
        return Data;
    }

    public void setData(List<SchoolClassMemberDetail> data) {
        Data = data;
    }
}
