package com.lqwawa.intleducation.module.user.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;

/**
 * Created by XChen on 2016/12/3.
 * email:man0fchina@foxmail.com
 */

public class MyClassVo extends BaseVo {

    /**
     * isDelete : false
     * id : 26
     * createTime : 2016-11-10 18:00:15
     * count : 2
     * name : 你好周小雨
     * createName : cx1老师
     * introduction : dfsd
     * deleteTime :
     * groupUuid :
     * createId : 4
     */

    private boolean isDelete;
    private long id;
    private String createTime;
    private int count;
    private String name;
    private String createName;
    private String introduction;
    private String deleteTime;
    private String groupUuid;
    private long createId;

    public boolean isIsDelete() {
        return isDelete;
    }

    public void setIsDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(String deleteTime) {
        this.deleteTime = deleteTime;
    }

    public String getGroupUuid() {
        return groupUuid;
    }

    public void setGroupUuid(String groupUuid) {
        this.groupUuid = groupUuid;
    }

    public long getCreateId() {
        return createId;
    }

    public void setCreateId(long createId) {
        this.createId = createId;
    }
}
