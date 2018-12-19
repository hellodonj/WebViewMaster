package com.lqwawa.intleducation.module.user.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.List;

/**
 * Created by XChen on 2016/12/1.
 * email:man0fchina@foxmail.com
 */

@Table(name = "AreaVo")
public class AreaVo extends BaseVo {
    public AreaVo(){

    }
    public AreaVo(AreaVo vo){
        id = vo.getId();
        isDelete = vo.isIsDelete();
        parentId = vo.getParentId();
        createTime = vo.getCreateTime();
        level = vo.getLevel();
        levelName = vo.getLevelName();
        levelNum = vo.getLevelNum();
        name = vo.getName();
        deleteTime = vo.getDeleteTime();
    }
    @Column(name = "isDelete")
    private boolean isDelete;
    @Column(name = "id", isId = true)
    private int id;
    @Column(name = "parentId")
    private int parentId;
    @Column(name = "createTime")
    private String createTime;
    @Column(name = "level")
    private String level;
    @Column(name = "name")
    private String name;
    @Column(name = "levelName")
    private String levelName;
    @Column(name = "levelNum")
    private int levelNum;
    @Column(name = "deleteTime")
    private String deleteTime;

    public boolean isIsDelete() {
        return isDelete;
    }

    public void setIsDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public int getLevelNum() {
        return levelNum;
    }

    public void setLevelNum(int levelNum) {
        this.levelNum = levelNum;
    }

    public String getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(String deleteTime) {
        this.deleteTime = deleteTime;
    }
}
