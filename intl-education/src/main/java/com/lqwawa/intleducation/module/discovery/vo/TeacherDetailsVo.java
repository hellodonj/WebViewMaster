package com.lqwawa.intleducation.module.discovery.vo;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.base.vo.BaseVo;

import java.util.List;

/**
 * Created by XChen on 2016/11/16.
 * email:man0fchina@foxmail.com
 */

public class TeacherDetailsVo extends BaseVo{
    boolean isFriend;
    int total;
    List<TeacherVo> teacher;
    List<ResourceVo> materialList;
    int code;

    public boolean isIsFriend() {
        return isFriend;
    }

    public void setIsFriend(boolean friend) {
        isFriend = friend;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<ResourceVo> getMaterialList() {
        return materialList;
    }

    public void setMaterialList(List<ResourceVo> materialList) {
        this.materialList = materialList;
    }

    public List<TeacherVo> getTeacher() {
        return teacher;
    }

    public void setTeacher(List<TeacherVo> teacher) {
        this.teacher = teacher;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
