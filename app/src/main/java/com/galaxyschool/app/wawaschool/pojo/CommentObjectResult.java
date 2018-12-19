package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelDataParser;
import com.lqwawa.lqbaselib.net.library.ModelListDataParser;

import java.util.List;

/**
 * 评论信息
 */
public class CommentObjectResult
        extends DataModelResult<CommentObjectInfo> {

    public CommentObjectResult parse(String jsonString) {
        CommentObjectResult result = this;
        if (result.getModel() == null) {
            result = JSONObject.parseObject(jsonString,
                    CommentObjectResult.class);
        }
        ModelDataParser dataParser = new ModelDataParser(CommentObjectInfo.class);
        CommentObjectInfo info= (CommentObjectInfo) dataParser.parse(jsonString);
        if (getModel()!=null&&info!=null){
            getModel().setData(info);
        }

        return result;
    }

}
