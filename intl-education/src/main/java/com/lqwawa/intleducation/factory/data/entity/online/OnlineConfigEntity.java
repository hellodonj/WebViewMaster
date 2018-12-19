package com.lqwawa.intleducation.factory.data.entity.online;

import com.lqwawa.intleducation.base.vo.BaseVo;

import java.util.List;

/**
 * @author medici
 * @desc 在线学习的分类数据实体
 */
public class OnlineConfigEntity extends BaseVo{

    private int firstId;
    private int fourthId;
    private int id;
    private String name;
    private int num;
    private int secondId;
    private int thirdId;
    private String thumbnail;
    private String thumbnailPad;
    private List<OnlineLabelEntity> childList;

    public int getFirstId() {
        return firstId;
    }

    public void setFirstId(int firstId) {
        this.firstId = firstId;
    }

    public int getFourthId() {
        return fourthId;
    }

    public void setFourthId(int fourthId) {
        this.fourthId = fourthId;
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

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getSecondId() {
        return secondId;
    }

    public void setSecondId(int secondId) {
        this.secondId = secondId;
    }

    public int getThirdId() {
        return thirdId;
    }

    public void setThirdId(int thirdId) {
        this.thirdId = thirdId;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getThumbnailPad() {
        return thumbnailPad;
    }

    public void setThumbnailPad(String thumbnailPad) {
        this.thumbnailPad = thumbnailPad;
    }

    public List<OnlineLabelEntity> getChildList() {
        return childList;
    }

    public void setChildList(List<OnlineLabelEntity> childList) {
        this.childList = childList;
    }

    public static class OnlineLabelEntity extends BaseVo{

        private int id;
        private String name;

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
    }
}
