package com.lqwawa.intleducation.factory.data.entity.course;

import android.text.TextUtils;

import com.lqwawa.intleducation.base.vo.BaseVo;
import com.lqwawa.intleducation.common.utils.EmptyUtil;

/**
 * @author mrmedici
 * @desc 班级学程加载的实体
 */
public class ClassCourseEntity extends BaseVo{
    // 全部购买
    private static final String TYPE_BUY_ALL = "0";
    // 分章节购买
    private static final String TYPE_BUY_CHAPTER = "1";
    // 默认返回2 则未购买或者免费课程
    private String buyType;
    private int chaperCount;
    private int chaperBuyCount;
    private String courseId;
    private int id;
    private String name;
    private String thumbnailUrl;

    private boolean hold;
    private boolean checked;
    private int assortment;
    private String level;

    public String getBuyType() {
        return buyType;
    }

    public void setBuyType(String buyType) {
        this.buyType = buyType;
    }

    public int getChaperCount() {
        return chaperCount;
    }

    public void setChaperCount(int chaperCount) {
        this.chaperCount = chaperCount;
    }

    public int getChaperBuyCount() {
        return chaperBuyCount;
    }

    public void setChaperBuyCount(int chaperBuyCount) {
        this.chaperBuyCount = chaperBuyCount;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public boolean isHold() {
        return hold;
    }

    public void setHold(boolean hold) {
        this.hold = hold;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public int getAssortment() {
        return assortment;
    }

    public ClassCourseEntity setAssortment(int assortment) {
        this.assortment = assortment;
        return this;
    }

    public String getLevel() {
        return level;
    }

    public ClassCourseEntity setLevel(String level) {
        this.level = level;
        return this;
    }

    public String getFirstLabelId() {
        if (!TextUtils.isEmpty(level)) {
            if (level.contains(".")) {
                String[] ids = level.split("\\.");
                return ids[0];
            }
        }
        return level;
    }

    /**
     * 是否全部购买
     * @return true 已经全部购买
     */
    public boolean isBuyAll(){
        if(TYPE_BUY_ALL.equals(buyType) ||
                (TYPE_BUY_CHAPTER .equals(buyType)  && chaperCount == chaperBuyCount)){
            return true;
        }
        return false;
    }

    /**
     * 是否章节购买
     * @return true 是章节购买
     */
    public boolean isChapterBuy(){
        // 是否章节购买
        if(TYPE_BUY_CHAPTER.equals(buyType) && chaperCount != chaperBuyCount){
            return true;
        }
        return false;
    }
}
