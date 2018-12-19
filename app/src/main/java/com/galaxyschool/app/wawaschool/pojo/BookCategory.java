package com.galaxyschool.app.wawaschool.pojo;

import java.util.List;

public class BookCategory {

    public static final int VERSION_TYPE = 1;
    public static final int STAGE_TYPE = 2;
    public static final int GRADE_TYPE = 3;
    public static final int SUBJECT_TYPE = 4;
    public static final int VOLUME_TYPE = 5;
    public static final int LANGUAGE_TYPE = 6;
    public static final int PUBLISHER_TYPE = 7;

    private int Type;
    private String TypeName;
    private List<TypeName> DetailList;

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public String getTypeName() {
        return TypeName;
    }

    public void setTypeName(String typeName) {
        TypeName = typeName;
    }

    public List<com.galaxyschool.app.wawaschool.pojo.TypeName> getDetailList() {
        return DetailList;
    }

    public void setDetailList(List<com.galaxyschool.app.wawaschool.pojo.TypeName> detailList) {
        DetailList = detailList;
    }

}
