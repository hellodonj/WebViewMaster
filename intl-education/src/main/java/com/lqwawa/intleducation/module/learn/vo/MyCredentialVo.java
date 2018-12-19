package com.lqwawa.intleducation.module.learn.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;

/**
 * Created by XChen on 2017/1/7.
 * email:man0fchina@foxmail.com
 */

public class MyCredentialVo extends BaseVo{


    /**
     * createTime : 1483757103000
     * createId : 8
     * createName : 就用，咋滴！就用，咋滴就用，咋滴
     * isDelete : false
     * deleteTime : null
     * applyTime : null
     * isPass : false
     * passTime : null
     * certificationName : 测试证书3
     * payTime : null
     * isApply : false
     * isPay : false
     * certificationId : 30
     * id : 36
     */

    private long createTime;
    private int createId;
    private String createName;
    private boolean isDelete;
    private String deleteTime;
    private String applyTime;
    private boolean isPass;
    private String passTime;
    private String certificationName;
    private String payTime;
    private boolean isApply;
    private boolean isPay;
    private String certificationId;
    private String id;

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getCreateId() {
        return createId;
    }

    public void setCreateId(int createId) {
        this.createId = createId;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public boolean isIsDelete() {
        return isDelete;
    }

    public void setIsDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }

    public String getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(String deleteTime) {
        this.deleteTime = deleteTime;
    }

    public String getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(String applyTime) {
        this.applyTime = applyTime;
    }

    public boolean isIsPass() {
        return isPass;
    }

    public void setIsPass(boolean isPass) {
        this.isPass = isPass;
    }

    public String getPassTime() {
        return passTime;
    }

    public void setPassTime(String passTime) {
        this.passTime = passTime;
    }

    public String getCertificationName() {
        return certificationName;
    }

    public void setCertificationName(String certificationName) {
        this.certificationName = certificationName;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public boolean isIsApply() {
        return isApply;
    }

    public void setIsApply(boolean isApply) {
        this.isApply = isApply;
    }

    public boolean isIsPay() {
        return isPay;
    }

    public void setIsPay(boolean isPay) {
        this.isPay = isPay;
    }

    public String getCertificationId() {
        return certificationId;
    }

    public void setCertificationId(String certificationId) {
        this.certificationId = certificationId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
