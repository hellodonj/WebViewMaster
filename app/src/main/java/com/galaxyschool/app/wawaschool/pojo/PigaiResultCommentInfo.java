package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by E450 on 2017/02/28.
 * 批改网返回的评论数据
 */
public class PigaiResultCommentInfo {

    @JSONField(name = "class")
    private String type;//错误类型
    private String cat;//提示
    private String msg;//信息

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
