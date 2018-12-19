package com.lqwawa.intleducation.factory.data.entity.response;

import com.lqwawa.intleducation.base.vo.ResponseVo;

/**
 * @author mrmedici
 * @param <T> 检查学程馆权限状态的data返回类
 */
public class CheckPermissionResponseVo<T> extends ResponseVo<T> {

    private String rightValue;

    public void setRightValue(String rightValue) {
        this.rightValue = rightValue;
    }

    public String getRightValue() {
        return rightValue;
    }
}
