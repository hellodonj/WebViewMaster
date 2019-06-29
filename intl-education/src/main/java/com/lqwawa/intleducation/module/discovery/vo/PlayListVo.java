package com.lqwawa.intleducation.module.discovery.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;

/**
 * 描述: 播放列表的返回实体
 * 作者|时间: djj on 2019/6/28 0028 下午 4:26
 */
public class PlayListVo extends BaseVo {
    public static final int SUCCEED = 0;

    private int code;// 0
    private String resourceUrl;
    private String nickName;
    private String id;
    private String studyTaskId;
    private String resId;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStudyTaskId() {
        return studyTaskId;
    }

    public void setStudyTaskId(String studyTaskId) {
        this.studyTaskId = studyTaskId;
    }

    public String getResId() {
        return resId;
    }

    public void setResId(String resId) {
        this.resId = resId;
    }

    /**
     * 网络请求是否成功
     *
     * @return true 请求成功 false 请求失败
     */
    public boolean isSucceed() {
        return code == SUCCEED;
    }

}
