package com.lqwawa.intleducation.module.learn.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;
import com.lqwawa.intleducation.module.user.vo.MyOrderVo;

import java.io.Serializable;
import java.util.List;

/**
 * ================================================
 * 作    者：Blizzard-liu
 * 版    本：1.0
 * 创建日期：2017/8/11 13:31
 * 描    述：
 * 修订历史：
 * ================================================
 */

public class LiveListVo extends BaseVo {

    /**
     * code : 0
     * message : null
     * total : 4
     * data : [{"id":620,"state":1,"startTime":"2017-08-07 16:26:00","order":null,"courseId":418,"createId":"527f4621-1d28-4c0a-9cd5-11ddb650f15d","endTime":null,"courseLiveId":null,"createName":"刘佳默","emceeIds":"527f4621-1d28-4c0a-9cd5-11ddb650f15d","title":"TEST","coverUrl":"http://file.lqwawa.com/uploadfiles//airclass/527f4621-1d28-4c0a-9cd5-11ddb650f15d/20170807042243/109d2f3f-15e1-40e9-968c-113815f8701d.jpg","schoolId":"bfbba4e6-c98a-4160-bca4-540087fb1d89","schoolName":"两栖蛙蛙体验学校","leUuid":"b68e945493","leVuid":"","leAcid":"A20170807000007x","intro":"CCCCCC","acCreateId":null,"emceeNames":"刘佳默"}]
     */

    private int code;
    private String message;
    private int total;
    private List<LiveVo> data;

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

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<LiveVo> getData() {
        return data;
    }

    public void setData(List<LiveVo> data) {
        this.data = data;
    }
}
