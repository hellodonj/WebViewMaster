package com.lqwawa.intleducation.factory.data.entity.school;

import com.lqwawa.intleducation.base.vo.BaseVo;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 机构信息实体类
 * @date 2018/07/03 17:43
 * @history v1.0
 * **********************************
 */
public class SchoolStarEntity extends BaseVo{

    private static final int SUCCEED = 0;

    private int code;
    private String message;
    private float starLevel;

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

    public float getStarLevel() {
        return starLevel;
    }

    public void setStarLevel(float starLevel) {
        this.starLevel = starLevel;
    }



    /**
     * 网络请求是否成功
     * @return true 请求成功 false 请求失败
     */
    public boolean isSucceed(){
        return code == SUCCEED;
    }
}
