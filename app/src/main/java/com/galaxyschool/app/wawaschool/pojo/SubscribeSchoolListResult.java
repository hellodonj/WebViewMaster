package com.galaxyschool.app.wawaschool.pojo;

import com.lqwawa.lqbaselib.net.library.Model;
import com.lqwawa.lqbaselib.net.library.ModelResult;

import java.util.List;

public class SubscribeSchoolListResult
        extends ModelResult<SubscribeSchoolListResult.SubscribeSchoolListModel> {

    public static class SubscribeSchoolListModel extends Model {
        private List<SchoolInfo> SubscribeNoList;

        public List<SchoolInfo> getSubscribeNoList() {
            return SubscribeNoList;
        }

        public void setSubscribeNoList(List<SchoolInfo> subscribeNoList) {
            SubscribeNoList = subscribeNoList;
        }
    }

}
