package com.lqwawa.intleducation.factory.data.entity.school;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.lqwawa.intleducation.base.vo.BaseVo;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @desc 接收授权码信息的Entity
 * @author medici
 */
public class CheckSchoolPermissionEntity extends BaseVo{
    private static final int SUCCEED = 0;

    private int code;
    private String message;
    private boolean isAuthorized;
    private boolean isExist;
    private String authorizeCode;
    private String rightValue;
    private String effectiveTime;
    private String failureTime;

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

    public boolean isAuthorized() {
        return isAuthorized;
    }

    public void setAuthorized(boolean authorized) {
        isAuthorized = authorized;
    }

    public boolean isExist() {
        return isExist;
    }

    public void setExist(boolean exist) {
        isExist = exist;
    }

    public String getAuthorizeCode() {
        return authorizeCode;
    }

    public void setAuthorizeCode(String authorizeCode) {
        this.authorizeCode = authorizeCode;
    }

    public String getRightValue() {
        return rightValue;
    }

    public void setRightValue(String rightValue) {
        this.rightValue = rightValue;
    }

    public String getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(String effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    public String getFailureTime() {
        return failureTime;
    }

    public void setFailureTime(String failureTime) {
        this.failureTime = failureTime;
    }

    /**
     * 网络请求是否成功
     * @return true 请求成功 false 请求失败
     */
    public boolean isSucceed(){
        return code == SUCCEED;
    }

    /**
     * 将分类的授权状况组装到集合中
     * @param entities 分类数据的集合
     */
    public void assembleAuthorizedInClassify(@NonNull List<LQCourseConfigEntity> entities){
        if(EmptyUtil.isEmpty(entities)) return;
        if(EmptyUtil.isEmpty(rightValue)) return;
        for (LQCourseConfigEntity entity:entities) {
            if(TextUtils.equals(rightValue,"0")){
                // 全部授权
                entity.setAuthorized(true);
            }

            String[] values = rightValue.split(",");
            if(EmptyUtil.isEmpty(values)){
                break;
            }

            List<String> strings = Arrays.asList(values);

            if(strings.contains(Integer.toString(entity.getId()))){
                // 该分类被授权
                entity.setAuthorized(true);
            }
        }
    }
}
