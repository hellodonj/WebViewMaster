package com.lqwawa.intleducation.factory.data.entity;

import com.lqwawa.intleducation.base.vo.BaseVo;

/**
 * @author mrmedici
 * @desc 判断是否绑定班级，接收服务器的返回
 */
public class LQCourseBindClassEntity extends BaseVo {

    private static final int SUCCEED = 0;

    protected int code;

    protected boolean isBindClass;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isBindClass() {
        return isBindClass;
    }

    public void setBindClass(boolean bindClass) {
        isBindClass = bindClass;
    }

    /**
     * 网络请求是否成功
     * @return true 请求成功 false 请求失败
     */
    public boolean isSucceed(){
        return code == SUCCEED;
    }

}
