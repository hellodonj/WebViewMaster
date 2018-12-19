package com.lqwawa.intleducation.factory.data.entity;

import com.lqwawa.intleducation.base.vo.BaseVo;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 班级加入基本详情信息
 * @date 2018/05/31 17:26
 * @history v1.0
 * **********************************
 */
public class JoinClassEntity extends BaseVo{

    private String ClassId;
    private String ClassMailListId;
    private String ClassName;
    private String ClassQRCode;
    private String GroupId;
    private boolean HeadMaster;
    private String HeadPicUrl;
    private int IsHistory;
    private int Role;
    private String Roles;
    private String SchoolId;
    private String SchoolName;
    private String SchoolQRCode;
    private int Type;
    private int WorkingState;
    private boolean isInClass;

    public String getClassId() {
        return ClassId;
    }

    public void setClassId(String ClassId) {
        this.ClassId = ClassId;
    }

    public String getClassMailListId() {
        return ClassMailListId;
    }

    public void setClassMailListId(String ClassMailListId) {
        this.ClassMailListId = ClassMailListId;
    }

    public String getClassName() {
        return ClassName;
    }

    public void setClassName(String ClassName) {
        this.ClassName = ClassName;
    }

    public String getClassQRCode() {
        return ClassQRCode;
    }

    public void setClassQRCode(String ClassQRCode) {
        this.ClassQRCode = ClassQRCode;
    }

    public String getGroupId() {
        return GroupId;
    }

    public void setGroupId(String GroupId) {
        this.GroupId = GroupId;
    }

    public boolean isHeadMaster() {
        return HeadMaster;
    }

    public void setHeadMaster(boolean HeadMaster) {
        this.HeadMaster = HeadMaster;
    }

    public String getHeadPicUrl() {
        return HeadPicUrl;
    }

    public void setHeadPicUrl(String HeadPicUrl) {
        this.HeadPicUrl = HeadPicUrl;
    }

    public int getIsHistory() {
        return IsHistory;
    }

    public void setIsHistory(int IsHistory) {
        this.IsHistory = IsHistory;
    }

    public int getRole() {
        return Role;
    }

    public void setRole(int Role) {
        this.Role = Role;
    }

    public String getRoles() {
        return Roles;
    }

    public void setRoles(String Roles) {
        this.Roles = Roles;
    }

    public String getSchoolId() {
        return SchoolId;
    }

    public void setSchoolId(String SchoolId) {
        this.SchoolId = SchoolId;
    }

    public String getSchoolName() {
        return SchoolName;
    }

    public void setSchoolName(String SchoolName) {
        this.SchoolName = SchoolName;
    }

    public String getSchoolQRCode() {
        return SchoolQRCode;
    }

    public void setSchoolQRCode(String SchoolQRCode) {
        this.SchoolQRCode = SchoolQRCode;
    }

    public int getType() {
        return Type;
    }

    public void setType(int Type) {
        this.Type = Type;
    }

    public int getWorkingState() {
        return WorkingState;
    }

    public void setWorkingState(int WorkingState) {
        this.WorkingState = WorkingState;
    }

    public boolean isIsInClass() {
        return isInClass;
    }

    public void setIsInClass(boolean isInClass) {
        this.isInClass = isInClass;
    }
}
