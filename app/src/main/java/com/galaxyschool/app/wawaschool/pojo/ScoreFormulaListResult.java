package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelListDataParser;

import java.util.List;

public class ScoreFormulaListResult extends DataModelResult<List<ScoreFormula>> {

    public ScoreFormulaListResult parse(String jsonString) {
        ScoreFormulaListResult result = this;
        if (result.getModel() == null) {
            result = JSONObject.parseObject(jsonString, ScoreFormulaListResult.class);
        }
        ModelListDataParser parser = new ModelListDataParser(ScoreFormula.class);
        List<ScoreFormula> list = parser.parse(jsonString);
        if (getModel() != null && list != null) {
            getModel().setData(list);
        }
        return result;
    }

}
