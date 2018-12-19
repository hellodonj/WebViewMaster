package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelDataParser;

public class ResourceTitleResult
    extends DataModelResult<ResourceTitle> {

    public ResourceTitleResult parse(String jsonString) {
        ResourceTitleResult result = this;
        if (result.getModel() == null) {
            result = JSONObject.parseObject(jsonString,
                    ResourceTitleResult.class);
        }
        ModelDataParser parser = new ModelDataParser(ResourceTitle.class);
        ResourceTitle data = (ResourceTitle)parser.parse(jsonString);
        if (getModel() != null && data != null) {
            getModel().setData(data);
        }
        return result;
    }

}
