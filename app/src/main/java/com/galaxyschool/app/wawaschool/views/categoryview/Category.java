package com.galaxyschool.app.wawaschool.views.categoryview;

import java.util.List;

/**
 * Created by Administrator on 2016/7/23.
 */
public class Category {

    private int Type;//1:版本 2：学段3：年级4：学科5：套系 6：套系7：出版社
    public static final int CATEGOTY_TYPE_VERSION=1;//版本
    public static final int CATEGOTY_TYPE_LEVEL=2;//学段LevelId
    public static final int CATEGOTY_TYPE_GRADE=3;//：年级出版社
    public static final int CATEGOTY_TYPE_SUBJECT=4;//学科
    public static final int CATEGOTY_TYPE_VOLUME=5;//套系
    public static final int CATEGOTY_TYPE_LANGUAGE=6;//语言
    public static final int CATEGOTY_TYPE_PUBLISHER=7;//出版社
    private String TypeName;
    private List<CategoryValue> DetailList;
    private String value;//选中的名称集合
    private String ids;//选中的名称id集合
    private boolean slide;//gridview popwindow 展开还是收起
    private boolean expanded=true;//expandgridview 的group是展开还是收起,（在筛选界面使用）
    private int selectMode;//单选还是多选
    public static final int SELECT_SINGLE_MODE=1;
    public static final int SELECT_MULTIPLIE_MODE=0;

    public static final int CATEGOTY_ROW_COUNT_PHONE=4;//手机一行展示属性个数
    public static final int CATEGOTY_ROW_COUNT_PANEL=6;//平板一行展示属性个数
    public static final int CATEGOTY_WORD_COUNT_PHONE=4;//手机一个属性最多显示字数，多余打省略号
    public static final int CATEGOTY_WORD_COUNT_PANEL=6;//平板一个属性最多显示字数，多余打省略号


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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<CategoryValue> getDetailList() {
        return DetailList;
    }

    public void setDetailList(List<CategoryValue> detailList) {
        DetailList = detailList;
    }

    public boolean isSlide() {
        return slide;
    }

    public void setSlide(boolean slide) {
        this.slide = slide;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public int getSelectMode() {
        return selectMode;
    }

    public void setSelectMode(int selectMode) {
        this.selectMode = selectMode;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }
}
