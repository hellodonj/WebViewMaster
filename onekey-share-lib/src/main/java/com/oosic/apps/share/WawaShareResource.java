package com.oosic.apps.share;

import java.io.Serializable;
import java.util.List;

/**
 * @author: wangchao
 * @date: 2018/06/11
 * @desc:
 */
public class WawaShareResource implements Serializable {

    private List<WawaShareData> YunBanMessageList;

    public List<WawaShareData> getYunBanMessageList() {
        return YunBanMessageList;
    }

    public void setYunBanMessageList(List<WawaShareData> YunBanMessageList) {
        this.YunBanMessageList = YunBanMessageList;
    }

    public static class WawaShareData implements Serializable {
        private String ResId;
        private String ResTitle;
        private String ResUrl;
        private String ResThumbnail;

        public String getResId() {
            return ResId;
        }

        public void setResId(String ResId) {
            this.ResId = ResId;
        }

        public String getResTitle() {
            return ResTitle;
        }

        public void setResTitle(String ResTitle) {
            this.ResTitle = ResTitle;
        }

        public String getResUrl() {
            return ResUrl;
        }

        public void setResUrl(String ResUrl) {
            this.ResUrl = ResUrl;
        }

        public String getResThumbnail() {
            return ResThumbnail;
        }

        public void setResThumbnail(String ResThumbnail) {
            this.ResThumbnail = ResThumbnail;
        }
    }
}
