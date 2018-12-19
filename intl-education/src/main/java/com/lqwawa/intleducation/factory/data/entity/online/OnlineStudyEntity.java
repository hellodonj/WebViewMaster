package com.lqwawa.intleducation.factory.data.entity.online;

import com.lqwawa.intleducation.base.vo.BaseVo;
import com.lqwawa.intleducation.factory.data.entity.OnlineClassEntity;

import java.util.List;

/**
 * @author medici
 * @desc 在线学习的数据实体
 */
public class OnlineStudyEntity extends BaseVo{

    private static final int SUCCEED = 0;

    private int code;
    private String message;
    private List<OnlineStudyOrganEntity> organList;
    private List<OnlineClassEntity> zxList;
    private List<OnlineClassEntity> rmList;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<OnlineStudyOrganEntity> getOrganList() {
        return organList;
    }

    public void setOrganList(List<OnlineStudyOrganEntity> organList) {
        this.organList = organList;
    }

    public List<OnlineClassEntity> getZxList() {
        return zxList;
    }

    public void setZxList(List<OnlineClassEntity> zxList) {
        this.zxList = zxList;
    }

    public List<OnlineClassEntity> getRmList() {
        return rmList;
    }

    public void setRmList(List<OnlineClassEntity> rmList) {
        this.rmList = rmList;
    }

    /**
     * 网络请求是否成功
     * @return true 请求成功 false 请求失败
     */
    public boolean isSucceed(){
        return code == SUCCEED;
    }
}
