package com.lqwawa.intleducation.module.discovery.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;

import java.util.List;

/**
 * ================================================
 * 作    者：Blizzard-liu
 * 版    本：1.0
 * 创建日期：2017/8/22 17:32
 * 描    述：
 * 修订历史：
 * ================================================
 */

public class SchoolListVo extends BaseVo{

    /**
     * Model : {"SName":null,"SchoolUrl":null,"GoId":null,"Pager":null,"KeyWords":null,"MemberId":"a7431ae3-3e36-417d-8794-64e6745c0f23","SchoolId":"00000000-0000-0000-0000-000000000000","SubscribeNoList":[{"SchoolId":"e2dc9c74-5c73-48a6-b6cb-66ca2bf120ba","yeyid":null,"LqWaWaExitFlag":0,"SchoolName":"青岛伯克利艺术培训中心","SchoolLogo":"","UnreadCount":0,"CreateTime":"2017-03-14T14:11:11.233","UpdateTime":"2017-03-14T14:11:11.233","State":2,"IsAttention":false,"QRCode":"20160506111529/e2dc9c74-5c73-48a6-b6cb-66ca2bf120ba/0fca9935-8f27-4bd1-91ea-5c625b4354bc.png","GQRCode":null,"AttentionNumber":48,"IsAdmin":false,"IsTeacher":false,"Role":0,"SchoolUrl":null,"AllRoles":"2","Roles":"1","ClassMailDetail":null,"BrowseNum":79,"LiveShowUrl":"","IsSchoolInspector":false,"IsLiveShowMgr":false},{"SchoolId":"bfbba4e6-c98a-4160-bca4-540087fb1d89","yeyid":null,"LqWaWaExitFlag":0,"SchoolName":"两栖蛙蛙体验学校","SchoolLogo":"20160714104014/000000000000000/b60ce190-eb8d-4fc5-8428-3a25ef8021bd.jpg","UnreadCount":0,"CreateTime":"2016-05-30T10:27:58.523","UpdateTime":"2016-08-17T14:58:06.177","State":2,"IsAttention":false,"QRCode":"2016/08/25/bfbba4e6-c98a-4160-bca4-540087fb1d89/ee6ae589-b121-4e69-9701-8b5a5ccb466a.png","GQRCode":null,"AttentionNumber":1225,"IsAdmin":false,"IsTeacher":false,"Role":1,"SchoolUrl":null,"AllRoles":"1,2,3","Roles":"0,1,2","ClassMailDetail":null,"BrowseNum":30267,"LiveShowUrl":"http://m.womob.cn/m/wx/?db=lqwawa&schoolshowId=lqwawa,lq_c","IsSchoolInspector":false,"IsLiveShowMgr":true},{"SchoolId":"9ed2d083-792c-49d1-a148-9ba58bdfbcb7","yeyid":null,"LqWaWaExitFlag":0,"SchoolName":"创意绘本屋","SchoolLogo":null,"UnreadCount":0,"CreateTime":"2017-07-04T13:46:26.193","UpdateTime":"2017-07-04T13:46:26.193","State":1,"IsAttention":false,"QRCode":"2016/10/27/9ed2d083-792c-49d1-a148-9ba58bdfbcb7/06d7c0eb-e1ee-4817-b67a-6bbb245f3083.png","GQRCode":null,"AttentionNumber":6,"IsAdmin":false,"IsTeacher":false,"Role":0,"SchoolUrl":null,"AllRoles":"","Roles":"","ClassMailDetail":null,"BrowseNum":48,"LiveShowUrl":"","IsSchoolInspector":false,"IsLiveShowMgr":false}],"JoinSchoolList":null,"clientVersion":null}
     * HasError : false
     * ErrorMessage : null
     */

    private ModelBean Model;
    private boolean HasError;
    private Object ErrorMessage;

    public ModelBean getModel() {
        return Model;
    }

    public void setModel(ModelBean Model) {
        this.Model = Model;
    }

    public boolean isHasError() {
        return HasError;
    }

    public void setHasError(boolean HasError) {
        this.HasError = HasError;
    }

    public Object getErrorMessage() {
        return ErrorMessage;
    }

    public void setErrorMessage(Object ErrorMessage) {
        this.ErrorMessage = ErrorMessage;
    }

    public static class ModelBean extends BaseVo{
        /**
         * SName : null
         * SchoolUrl : null
         * GoId : null
         * Pager : null
         * KeyWords : null
         * MemberId : a7431ae3-3e36-417d-8794-64e6745c0f23
         * SchoolId : 00000000-0000-0000-0000-000000000000
         * SubscribeNoList : [{"SchoolId":"e2dc9c74-5c73-48a6-b6cb-66ca2bf120ba","yeyid":null,"LqWaWaExitFlag":0,"SchoolName":"青岛伯克利艺术培训中心","SchoolLogo":"","UnreadCount":0,"CreateTime":"2017-03-14T14:11:11.233","UpdateTime":"2017-03-14T14:11:11.233","State":2,"IsAttention":false,"QRCode":"20160506111529/e2dc9c74-5c73-48a6-b6cb-66ca2bf120ba/0fca9935-8f27-4bd1-91ea-5c625b4354bc.png","GQRCode":null,"AttentionNumber":48,"IsAdmin":false,"IsTeacher":false,"Role":0,"SchoolUrl":null,"AllRoles":"2","Roles":"1","ClassMailDetail":null,"BrowseNum":79,"LiveShowUrl":"","IsSchoolInspector":false,"IsLiveShowMgr":false},{"SchoolId":"bfbba4e6-c98a-4160-bca4-540087fb1d89","yeyid":null,"LqWaWaExitFlag":0,"SchoolName":"两栖蛙蛙体验学校","SchoolLogo":"20160714104014/000000000000000/b60ce190-eb8d-4fc5-8428-3a25ef8021bd.jpg","UnreadCount":0,"CreateTime":"2016-05-30T10:27:58.523","UpdateTime":"2016-08-17T14:58:06.177","State":2,"IsAttention":false,"QRCode":"2016/08/25/bfbba4e6-c98a-4160-bca4-540087fb1d89/ee6ae589-b121-4e69-9701-8b5a5ccb466a.png","GQRCode":null,"AttentionNumber":1225,"IsAdmin":false,"IsTeacher":false,"Role":1,"SchoolUrl":null,"AllRoles":"1,2,3","Roles":"0,1,2","ClassMailDetail":null,"BrowseNum":30267,"LiveShowUrl":"http://m.womob.cn/m/wx/?db=lqwawa&schoolshowId=lqwawa,lq_c","IsSchoolInspector":false,"IsLiveShowMgr":true},{"SchoolId":"9ed2d083-792c-49d1-a148-9ba58bdfbcb7","yeyid":null,"LqWaWaExitFlag":0,"SchoolName":"创意绘本屋","SchoolLogo":null,"UnreadCount":0,"CreateTime":"2017-07-04T13:46:26.193","UpdateTime":"2017-07-04T13:46:26.193","State":1,"IsAttention":false,"QRCode":"2016/10/27/9ed2d083-792c-49d1-a148-9ba58bdfbcb7/06d7c0eb-e1ee-4817-b67a-6bbb245f3083.png","GQRCode":null,"AttentionNumber":6,"IsAdmin":false,"IsTeacher":false,"Role":0,"SchoolUrl":null,"AllRoles":"","Roles":"","ClassMailDetail":null,"BrowseNum":48,"LiveShowUrl":"","IsSchoolInspector":false,"IsLiveShowMgr":false}]
         * JoinSchoolList : null
         * clientVersion : null
         */

        private Object SName;
        private Object SchoolUrl;
        private Object GoId;
        private Object Pager;
        private Object KeyWords;
        private String MemberId;
        private String SchoolId;
        private Object JoinSchoolList;
        private Object clientVersion;
        private List<SubscribeNoListBean> SubscribeNoList;

        public Object getSName() {
            return SName;
        }

        public void setSName(Object SName) {
            this.SName = SName;
        }

        public Object getSchoolUrl() {
            return SchoolUrl;
        }

        public void setSchoolUrl(Object SchoolUrl) {
            this.SchoolUrl = SchoolUrl;
        }

        public Object getGoId() {
            return GoId;
        }

        public void setGoId(Object GoId) {
            this.GoId = GoId;
        }

        public Object getPager() {
            return Pager;
        }

        public void setPager(Object Pager) {
            this.Pager = Pager;
        }

        public Object getKeyWords() {
            return KeyWords;
        }

        public void setKeyWords(Object KeyWords) {
            this.KeyWords = KeyWords;
        }

        public String getMemberId() {
            return MemberId;
        }

        public void setMemberId(String MemberId) {
            this.MemberId = MemberId;
        }

        public String getSchoolId() {
            return SchoolId;
        }

        public void setSchoolId(String SchoolId) {
            this.SchoolId = SchoolId;
        }

        public Object getJoinSchoolList() {
            return JoinSchoolList;
        }

        public void setJoinSchoolList(Object JoinSchoolList) {
            this.JoinSchoolList = JoinSchoolList;
        }

        public Object getClientVersion() {
            return clientVersion;
        }

        public void setClientVersion(Object clientVersion) {
            this.clientVersion = clientVersion;
        }

        public List<SubscribeNoListBean> getSubscribeNoList() {
            return SubscribeNoList;
        }

        public void setSubscribeNoList(List<SubscribeNoListBean> SubscribeNoList) {
            this.SubscribeNoList = SubscribeNoList;
        }

        public static class SubscribeNoListBean extends BaseVo{
            /**
             * SchoolId : e2dc9c74-5c73-48a6-b6cb-66ca2bf120ba
             * yeyid : null
             * LqWaWaExitFlag : 0
             * SchoolName : 青岛伯克利艺术培训中心
             * SchoolLogo :
             * UnreadCount : 0
             * CreateTime : 2017-03-14T14:11:11.233
             * UpdateTime : 2017-03-14T14:11:11.233
             * State : 2
             * IsAttention : false
             * QRCode : 20160506111529/e2dc9c74-5c73-48a6-b6cb-66ca2bf120ba/0fca9935-8f27-4bd1-91ea-5c625b4354bc.png
             * GQRCode : null
             * AttentionNumber : 48
             * IsAdmin : false
             * IsTeacher : false
             * Role : 0
             * SchoolUrl : null
             * AllRoles : 2
             * Roles : 1
             * ClassMailDetail : null
             * BrowseNum : 79
             * LiveShowUrl :
             * IsSchoolInspector : false
             * IsLiveShowMgr : false
             */

            private String SchoolId;
            private Object yeyid;
            private int LqWaWaExitFlag;
            private String SchoolName;
            private String SchoolLogo;
            private int UnreadCount;
            private String CreateTime;
            private String UpdateTime;
            private int State;
            private boolean IsAttention;
            private String QRCode;
            private Object GQRCode;
            private int AttentionNumber;
            private boolean IsAdmin;
            private boolean IsTeacher;
            private int Role;
            private Object SchoolUrl;
            private String AllRoles;
            private String Roles;
            private Object ClassMailDetail;
            private int BrowseNum;
            private String LiveShowUrl;
            private boolean IsSchoolInspector;
            private boolean IsLiveShowMgr;

            public String getSchoolId() {
                return SchoolId;
            }

            public void setSchoolId(String SchoolId) {
                this.SchoolId = SchoolId;
            }

            public Object getYeyid() {
                return yeyid;
            }

            public void setYeyid(Object yeyid) {
                this.yeyid = yeyid;
            }

            public int getLqWaWaExitFlag() {
                return LqWaWaExitFlag;
            }

            public void setLqWaWaExitFlag(int LqWaWaExitFlag) {
                this.LqWaWaExitFlag = LqWaWaExitFlag;
            }

            public String getSchoolName() {
                return SchoolName;
            }

            public void setSchoolName(String SchoolName) {
                this.SchoolName = SchoolName;
            }

            public String getSchoolLogo() {
                return SchoolLogo;
            }

            public void setSchoolLogo(String SchoolLogo) {
                this.SchoolLogo = SchoolLogo;
            }

            public int getUnreadCount() {
                return UnreadCount;
            }

            public void setUnreadCount(int UnreadCount) {
                this.UnreadCount = UnreadCount;
            }

            public String getCreateTime() {
                return CreateTime;
            }

            public void setCreateTime(String CreateTime) {
                this.CreateTime = CreateTime;
            }

            public String getUpdateTime() {
                return UpdateTime;
            }

            public void setUpdateTime(String UpdateTime) {
                this.UpdateTime = UpdateTime;
            }

            public int getState() {
                return State;
            }

            public void setState(int State) {
                this.State = State;
            }

            public boolean isIsAttention() {
                return IsAttention;
            }

            public void setIsAttention(boolean IsAttention) {
                this.IsAttention = IsAttention;
            }

            public String getQRCode() {
                return QRCode;
            }

            public void setQRCode(String QRCode) {
                this.QRCode = QRCode;
            }

            public Object getGQRCode() {
                return GQRCode;
            }

            public void setGQRCode(Object GQRCode) {
                this.GQRCode = GQRCode;
            }

            public int getAttentionNumber() {
                return AttentionNumber;
            }

            public void setAttentionNumber(int AttentionNumber) {
                this.AttentionNumber = AttentionNumber;
            }

            public boolean isIsAdmin() {
                return IsAdmin;
            }

            public void setIsAdmin(boolean IsAdmin) {
                this.IsAdmin = IsAdmin;
            }

            public boolean isIsTeacher() {
                return IsTeacher;
            }

            public void setIsTeacher(boolean IsTeacher) {
                this.IsTeacher = IsTeacher;
            }

            public int getRole() {
                return Role;
            }

            public void setRole(int Role) {
                this.Role = Role;
            }

            public Object getSchoolUrl() {
                return SchoolUrl;
            }

            public void setSchoolUrl(Object SchoolUrl) {
                this.SchoolUrl = SchoolUrl;
            }

            public String getAllRoles() {
                return AllRoles;
            }

            public void setAllRoles(String AllRoles) {
                this.AllRoles = AllRoles;
            }

            public String getRoles() {
                return Roles;
            }

            public void setRoles(String Roles) {
                this.Roles = Roles;
            }

            public Object getClassMailDetail() {
                return ClassMailDetail;
            }

            public void setClassMailDetail(Object ClassMailDetail) {
                this.ClassMailDetail = ClassMailDetail;
            }

            public int getBrowseNum() {
                return BrowseNum;
            }

            public void setBrowseNum(int BrowseNum) {
                this.BrowseNum = BrowseNum;
            }

            public String getLiveShowUrl() {
                return LiveShowUrl;
            }

            public void setLiveShowUrl(String LiveShowUrl) {
                this.LiveShowUrl = LiveShowUrl;
            }

            public boolean isIsSchoolInspector() {
                return IsSchoolInspector;
            }

            public void setIsSchoolInspector(boolean IsSchoolInspector) {
                this.IsSchoolInspector = IsSchoolInspector;
            }

            public boolean isIsLiveShowMgr() {
                return IsLiveShowMgr;
            }

            public void setIsLiveShowMgr(boolean IsLiveShowMgr) {
                this.IsLiveShowMgr = IsLiveShowMgr;
            }
        }
    }
}
