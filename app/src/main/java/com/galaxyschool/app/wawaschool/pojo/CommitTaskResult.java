package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.DataResult;
import com.lqwawa.lqbaselib.net.library.ModelDataParser;
import com.lqwawa.lqbaselib.net.library.Result;

public class CommitTaskResult extends DataResult{

    public CommitId Model;

    public class CommitId{
        public int CommitTaskId;
    }

}
