package com.galaxyschool.app.wawaschool.pojo;
import com.lqwawa.lqbaselib.net.library.Model;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import java.util.List;

/**
 * ======================================================
 * Created by : Brave_Qu on 2018/9/8 0008 14:21
 * Describe:关联账号的信息
 * ======================================================
 */
public class BindThirdPartyResult extends ModelResult<BindThirdPartyResult.BindThirdPartyModel> {
    public static class BindThirdPartyModel extends Model {
        private List<BindThirdParty> DataList;

        public List<BindThirdParty> getDataList() {
            return DataList;
        }

        public void setDataList(List<BindThirdParty> dataList) {
            DataList = dataList;
        }
    }
}
