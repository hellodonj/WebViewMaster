package com.galaxyschool.app.wawaschool.pojo;

import com.lqwawa.lqbaselib.net.library.Model;
import com.lqwawa.lqbaselib.net.library.ModelResult;

import java.util.List;

public class PushMessageListResult
        extends ModelResult<PushMessageListResult.PushMessageListModel> {

    public static class PushMessageListModel extends Model {
        private List<PushMessage> InformationList;

        public List<PushMessage> getInformationList() {
            return InformationList;
        }

        public void setInformationList(List<PushMessage> informationList) {
            InformationList = informationList;
        }
    }

}
