package com.lqwawa.intleducation.module.discovery.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by XChen on 2017/4/19.
 * email:man0fchina@foxmail.com
 */
@Table(name = "FilterHistoryVo")
public class FilterHistoryVo extends BaseVo {
    @Column(name = "id", isId = true)
    private int id;
    @Column(name = "userId", property = "NOT NULL")
    private String userId;
    @Column(name = "topLevel")
    private String topLevel;
    @Column(name = "label1")
    private int label1;
    @Column(name = "label2")
    private int label2;
    @Column(name = "label3")
    private int label3;
    @Column(name = "statusFilter")
    private String statusFilter;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTopLevel() {
        return topLevel;
    }

    public void setTopLevel(String topLevel) {
        this.topLevel = topLevel;
    }

    public int getLabel1() {
        return label1;
    }

    public void setLabel1(int label1) {
        this.label1 = label1;
    }

    public int getLabel2() {
        return label2;
    }

    public void setLabel2(int label2) {
        this.label2 = label2;
    }

    public int getLabel3() {
        return label3;
    }

    public void setLabel3(int label3) {
        this.label3 = label3;
    }

    public String getStatusFilter() {
        return statusFilter;
    }

    public void setStatusFilter(String statusFilter) {
        this.statusFilter = statusFilter;
    }
}
