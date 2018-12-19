package com.lqwawa.intleducation.module.discovery.vo;
import com.lqwawa.intleducation.base.vo.BaseVo;

/**
 * Created by XChen on 2017/8/17.
 * email:man0fchina@foxmail.com
 * 精品课程权限列表数据模型
 */

public class HQCPermissionsVo extends BaseVo {

    /**
     * organId : bfbba4e6-c98a-4160-bca4-540087fb1d89
     * isDelete : false
     * id : 5
     * createTime : 2016-08-14 16:58:21
     * secondId : 786
     * paramOneName : 专业课程
     * secondName : 高中
     * paramTwoId : 44
     * paramTwoName : A-LEVEL
     * paramOneId : 75
     * firstName : 国际课程
     * firstId : 776
     * deleteTime :
     */

    private String organId;
    private boolean isDelete;
    private String id;
    private String createTime;
    private String secondId;
    private String paramOneName;
    private String secondName;
    private String paramTwoId;
    private String paramTwoName;
    private String paramOneId;
    private String firstName;
    private String firstId;
    private String deleteTime;

    public String getOrganId() {
        return organId;
    }

    public void setOrganId(String organId) {
        this.organId = organId;
    }

    public boolean isIsDelete() {
        return isDelete;
    }

    public void setIsDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getSecondId() {
        return secondId;
    }

    public void setSecondId(String secondId) {
        this.secondId = secondId;
    }

    public String getParamOneName() {
        return paramOneName;
    }

    public void setParamOneName(String paramOneName) {
        this.paramOneName = paramOneName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getParamTwoId() {
        return paramTwoId;
    }

    public void setParamTwoId(String paramTwoId) {
        this.paramTwoId = paramTwoId;
    }

    public String getParamTwoName() {
        return paramTwoName;
    }

    public void setParamTwoName(String paramTwoName) {
        this.paramTwoName = paramTwoName;
    }

    public String getParamOneId() {
        return paramOneId;
    }

    public void setParamOneId(String paramOneId) {
        this.paramOneId = paramOneId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstId() {
        return firstId;
    }

    public void setFirstId(String firstId) {
        this.firstId = firstId;
    }

    public String getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(String deleteTime) {
        this.deleteTime = deleteTime;
    }
}
