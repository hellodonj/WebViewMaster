package com.lqwawa.intleducation.module.discovery.ui;

import java.util.List;

/**
 * Created by XChen on 2017/5/4.
 * email:man0fchina@foxmail.com
 */

public class test {

    /**
     * Model : {"Id":"b2162b13-06f0-4c86-b2a8-d13bea7527eb","MemberId":"283220d6-7d77-4b7b-8e74-ca5b86739f7d","MemberIdInt":17131,"ClassMailListDetailList":[{"Id":"e284fa22-3431-4b5f-bb9f-a4c300a2bd22","MemberId":"283220d6-7d77-4b7b-8e74-ca5b86739f7d","NoteName":"张珂","Nickname":null,"RealName":null,"Role":"0","HeadPicUrl":"20170425112854/283220d6-7d77-4b7b-8e74-ca5b86739f7d/47daf65a-4e0d-4e4f-81ed-1e9d16392fb3.png","Telephone":null,"Email":null,"Identity":null,"WorkingState":1,"HeadTeacherState":1,"IsFriend":0,"FriendId":"00000000-0000-0000-0000-000000000000","GagMark":0,"IsState":1,"StudentName":null,"ParentType":0,"MemberIdInt":17131,"clientVersion":null},{"Id":"5f48ecf0-2dc9-45e8-8be4-a4c300a7ec87","MemberId":"71b8b539-e6e3-4548-9197-739309b4eea6","NoteName":"合肥总监倪楠","Nickname":null,"RealName":null,"Role":"0","HeadPicUrl":"20151219011249/71b8b539-e6e3-4548-9197-739309b4eea6/23190982-b8f8-426d-8d4b-a6bf2d06f5ce.jpg","Telephone":null,"Email":null,"Identity":null,"WorkingState":1,"HeadTeacherState":0,"IsFriend":0,"FriendId":"00000000-0000-0000-0000-000000000000","GagMark":0,"IsState":1,"StudentName":null,"ParentType":0,"MemberIdInt":14449,"clientVersion":null},{"Id":"f6b6d96f-8446-438c-af2c-a5c500eebc98","MemberId":"bc498ffa-5745-4171-8959-a123b18b5e0f","NoteName":"发红包发过火","Nickname":null,"RealName":null,"Role":"1","HeadPicUrl":"20170410050152/bc498ffa-5745-4171-8959-a123b18b5e0f/144ad2be-2bc8-4c1e-9f92-fa773ce11e11.jpg","Telephone":null,"Email":null,"Identity":null,"WorkingState":1,"HeadTeacherState":0,"IsFriend":0,"FriendId":"00000000-0000-0000-0000-000000000000","GagMark":0,"IsState":1,"StudentName":null,"ParentType":0,"MemberIdInt":11068,"clientVersion":null},{"Id":"f53b93cb-5331-4a7c-89af-a5c500eebb7b","MemberId":"a02d741e-9239-4db3-84c7-4a3fef33e002","NoteName":"吴露","Nickname":null,"RealName":null,"Role":"1","HeadPicUrl":"20151228115816/a02d741e-9239-4db3-84c7-4a3fef33e002/d35965d7-e604-4a28-ac78-1523cc093500.jpg","Telephone":null,"Email":null,"Identity":null,"WorkingState":1,"HeadTeacherState":0,"IsFriend":0,"FriendId":"00000000-0000-0000-0000-000000000000","GagMark":0,"IsState":1,"StudentName":null,"ParentType":0,"MemberIdInt":16196,"clientVersion":null},{"Id":"221cacb0-9c2e-421b-b2c5-a4c300a8cee1","MemberId":"5125bce2-14ee-49c3-b109-69176832192f","NoteName":"谢文娟","Nickname":null,"RealName":null,"Role":"1","HeadPicUrl":"20160923040639/5125bce2-14ee-49c3-b109-69176832192f/a98e47e6-cffc-455c-ad2d-8c8ae9fef3e5.jpg","Telephone":null,"Email":null,"Identity":null,"WorkingState":1,"HeadTeacherState":0,"IsFriend":0,"FriendId":"00000000-0000-0000-0000-000000000000","GagMark":0,"IsState":1,"StudentName":null,"ParentType":0,"MemberIdInt":16587,"clientVersion":null},{"Id":"a0050b7c-1b33-402f-80d3-a4c300a6f169","MemberId":"be4bcad4-2889-447c-adb2-5a5ce8d34b04","NoteName":"朱峻","Nickname":null,"RealName":null,"Role":"1","HeadPicUrl":"20150625060816/be4bcad4-2889-447c-adb2-5a5ce8d34b04/7e19b01b-f6e2-4c7c-b469-83629d2358a2.jpg","Telephone":null,"Email":null,"Identity":null,"WorkingState":1,"HeadTeacherState":0,"IsFriend":0,"FriendId":"00000000-0000-0000-0000-000000000000","GagMark":0,"IsState":1,"StudentName":null,"ParentType":0,"MemberIdInt":17586,"clientVersion":null},{"Id":"43ce9fea-e888-42d8-a93f-a5c500eebb8a","MemberId":"a02d741e-9239-4db3-84c7-4a3fef33e002","NoteName":"吴露的家长吴露","Nickname":null,"RealName":null,"Role":"2","HeadPicUrl":"20151228115816/a02d741e-9239-4db3-84c7-4a3fef33e002/d35965d7-e604-4a28-ac78-1523cc093500.jpg","Telephone":null,"Email":null,"Identity":null,"WorkingState":1,"HeadTeacherState":0,"IsFriend":0,"FriendId":"00000000-0000-0000-0000-000000000000","GagMark":0,"IsState":1,"StudentName":"吴露","ParentType":0,"MemberIdInt":16196,"clientVersion":null}],"PersonalList":null,"LQ_SchoolId":"3583ae72-bfdf-40ef-82e0-9d430b31ce45","GroupId":null,"ClassGag":0,"IsTeacher":false,"VersionCode":0,"ErrorMessage":null,"ReturnResult":false,"clientVersion":null}
     * HasError : false
     * ErrorMessage : null
     */

    public ModelBean Model;
    public boolean HasError;
    public String ErrorMessage;

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

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public void setErrorMessage(String ErrorMessage) {
        this.ErrorMessage = ErrorMessage;
    }

    public static class ModelBean {
        /**
         * Id : b2162b13-06f0-4c86-b2a8-d13bea7527eb
         * MemberId : 283220d6-7d77-4b7b-8e74-ca5b86739f7d
         * MemberIdInt : 17131
         * ClassMailListDetailList : [{"Id":"e284fa22-3431-4b5f-bb9f-a4c300a2bd22","MemberId":"283220d6-7d77-4b7b-8e74-ca5b86739f7d","NoteName":"张珂","Nickname":null,"RealName":null,"Role":"0","HeadPicUrl":"20170425112854/283220d6-7d77-4b7b-8e74-ca5b86739f7d/47daf65a-4e0d-4e4f-81ed-1e9d16392fb3.png","Telephone":null,"Email":null,"Identity":null,"WorkingState":1,"HeadTeacherState":1,"IsFriend":0,"FriendId":"00000000-0000-0000-0000-000000000000","GagMark":0,"IsState":1,"StudentName":null,"ParentType":0,"MemberIdInt":17131,"clientVersion":null},{"Id":"5f48ecf0-2dc9-45e8-8be4-a4c300a7ec87","MemberId":"71b8b539-e6e3-4548-9197-739309b4eea6","NoteName":"合肥总监倪楠","Nickname":null,"RealName":null,"Role":"0","HeadPicUrl":"20151219011249/71b8b539-e6e3-4548-9197-739309b4eea6/23190982-b8f8-426d-8d4b-a6bf2d06f5ce.jpg","Telephone":null,"Email":null,"Identity":null,"WorkingState":1,"HeadTeacherState":0,"IsFriend":0,"FriendId":"00000000-0000-0000-0000-000000000000","GagMark":0,"IsState":1,"StudentName":null,"ParentType":0,"MemberIdInt":14449,"clientVersion":null},{"Id":"f6b6d96f-8446-438c-af2c-a5c500eebc98","MemberId":"bc498ffa-5745-4171-8959-a123b18b5e0f","NoteName":"发红包发过火","Nickname":null,"RealName":null,"Role":"1","HeadPicUrl":"20170410050152/bc498ffa-5745-4171-8959-a123b18b5e0f/144ad2be-2bc8-4c1e-9f92-fa773ce11e11.jpg","Telephone":null,"Email":null,"Identity":null,"WorkingState":1,"HeadTeacherState":0,"IsFriend":0,"FriendId":"00000000-0000-0000-0000-000000000000","GagMark":0,"IsState":1,"StudentName":null,"ParentType":0,"MemberIdInt":11068,"clientVersion":null},{"Id":"f53b93cb-5331-4a7c-89af-a5c500eebb7b","MemberId":"a02d741e-9239-4db3-84c7-4a3fef33e002","NoteName":"吴露","Nickname":null,"RealName":null,"Role":"1","HeadPicUrl":"20151228115816/a02d741e-9239-4db3-84c7-4a3fef33e002/d35965d7-e604-4a28-ac78-1523cc093500.jpg","Telephone":null,"Email":null,"Identity":null,"WorkingState":1,"HeadTeacherState":0,"IsFriend":0,"FriendId":"00000000-0000-0000-0000-000000000000","GagMark":0,"IsState":1,"StudentName":null,"ParentType":0,"MemberIdInt":16196,"clientVersion":null},{"Id":"221cacb0-9c2e-421b-b2c5-a4c300a8cee1","MemberId":"5125bce2-14ee-49c3-b109-69176832192f","NoteName":"谢文娟","Nickname":null,"RealName":null,"Role":"1","HeadPicUrl":"20160923040639/5125bce2-14ee-49c3-b109-69176832192f/a98e47e6-cffc-455c-ad2d-8c8ae9fef3e5.jpg","Telephone":null,"Email":null,"Identity":null,"WorkingState":1,"HeadTeacherState":0,"IsFriend":0,"FriendId":"00000000-0000-0000-0000-000000000000","GagMark":0,"IsState":1,"StudentName":null,"ParentType":0,"MemberIdInt":16587,"clientVersion":null},{"Id":"a0050b7c-1b33-402f-80d3-a4c300a6f169","MemberId":"be4bcad4-2889-447c-adb2-5a5ce8d34b04","NoteName":"朱峻","Nickname":null,"RealName":null,"Role":"1","HeadPicUrl":"20150625060816/be4bcad4-2889-447c-adb2-5a5ce8d34b04/7e19b01b-f6e2-4c7c-b469-83629d2358a2.jpg","Telephone":null,"Email":null,"Identity":null,"WorkingState":1,"HeadTeacherState":0,"IsFriend":0,"FriendId":"00000000-0000-0000-0000-000000000000","GagMark":0,"IsState":1,"StudentName":null,"ParentType":0,"MemberIdInt":17586,"clientVersion":null},{"Id":"43ce9fea-e888-42d8-a93f-a5c500eebb8a","MemberId":"a02d741e-9239-4db3-84c7-4a3fef33e002","NoteName":"吴露的家长吴露","Nickname":null,"RealName":null,"Role":"2","HeadPicUrl":"20151228115816/a02d741e-9239-4db3-84c7-4a3fef33e002/d35965d7-e604-4a28-ac78-1523cc093500.jpg","Telephone":null,"Email":null,"Identity":null,"WorkingState":1,"HeadTeacherState":0,"IsFriend":0,"FriendId":"00000000-0000-0000-0000-000000000000","GagMark":0,"IsState":1,"StudentName":"吴露","ParentType":0,"MemberIdInt":16196,"clientVersion":null}]
         * PersonalList : null
         * LQ_SchoolId : 3583ae72-bfdf-40ef-82e0-9d430b31ce45
         * GroupId : null
         * ClassGag : 0
         * IsTeacher : false
         * VersionCode : 0
         * ErrorMessage : null
         * ReturnResult : false
         * clientVersion : null
         */

        public String Id;
        public String MemberId;
        public int MemberIdInt;
        public String PersonalList;
        public String LQ_SchoolId;
        public String GroupId;
        public int ClassGag;
        public boolean IsTeacher;
        public int VersionCode;
        public String ErrorMessage;
        public boolean ReturnResult;
        public String clientVersion;
        public List<ClassMailListDetailListBean> ClassMailListDetailList;

        public String getId() {
            return Id;
        }

        public void setId(String Id) {
            this.Id = Id;
        }

        public String getMemberId() {
            return MemberId;
        }

        public void setMemberId(String MemberId) {
            this.MemberId = MemberId;
        }

        public int getMemberIdInt() {
            return MemberIdInt;
        }

        public void setMemberIdInt(int MemberIdInt) {
            this.MemberIdInt = MemberIdInt;
        }

        public String getPersonalList() {
            return PersonalList;
        }

        public void setPersonalList(String PersonalList) {
            this.PersonalList = PersonalList;
        }

        public String getLQ_SchoolId() {
            return LQ_SchoolId;
        }

        public void setLQ_SchoolId(String LQ_SchoolId) {
            this.LQ_SchoolId = LQ_SchoolId;
        }

        public String getGroupId() {
            return GroupId;
        }

        public void setGroupId(String GroupId) {
            this.GroupId = GroupId;
        }

        public int getClassGag() {
            return ClassGag;
        }

        public void setClassGag(int ClassGag) {
            this.ClassGag = ClassGag;
        }

        public boolean isIsTeacher() {
            return IsTeacher;
        }

        public void setIsTeacher(boolean IsTeacher) {
            this.IsTeacher = IsTeacher;
        }

        public int getVersionCode() {
            return VersionCode;
        }

        public void setVersionCode(int VersionCode) {
            this.VersionCode = VersionCode;
        }

        public String getErrorMessage() {
            return ErrorMessage;
        }

        public void setErrorMessage(String ErrorMessage) {
            this.ErrorMessage = ErrorMessage;
        }

        public boolean isReturnResult() {
            return ReturnResult;
        }

        public void setReturnResult(boolean ReturnResult) {
            this.ReturnResult = ReturnResult;
        }

        public String getClientVersion() {
            return clientVersion;
        }

        public void setClientVersion(String clientVersion) {
            this.clientVersion = clientVersion;
        }

        public List<ClassMailListDetailListBean> getClassMailListDetailList() {
            return ClassMailListDetailList;
        }

        public void setClassMailListDetailList(List<ClassMailListDetailListBean> ClassMailListDetailList) {
            this.ClassMailListDetailList = ClassMailListDetailList;
        }

        public static class ClassMailListDetailListBean {
            /**
             * Id : e284fa22-3431-4b5f-bb9f-a4c300a2bd22
             * MemberId : 283220d6-7d77-4b7b-8e74-ca5b86739f7d
             * NoteName : 张珂
             * Nickname : null
             * RealName : null
             * Role : 0
             * HeadPicUrl : 20170425112854/283220d6-7d77-4b7b-8e74-ca5b86739f7d/47daf65a-4e0d-4e4f-81ed-1e9d16392fb3.png
             * Telephone : null
             * Email : null
             * Identity : null
             * WorkingState : 1
             * HeadTeacherState : 1
             * IsFriend : 0
             * FriendId : 00000000-0000-0000-0000-000000000000
             * GagMark : 0
             * IsState : 1
             * StudentName : null
             * ParentType : 0
             * MemberIdInt : 17131
             * clientVersion : null
             */

            public String Id;
            public String MemberId;
            public String NoteName;
            public String Nickname;
            public String RealName;
            public String Role;
            public String HeadPicUrl;
            public String Telephone;
            public String Email;
            public String Identity;
            public int WorkingState;
            public int HeadTeacherState;
            public int IsFriend;
            public String FriendId;
            public int GagMark;
            public int IsState;
            public String StudentName;
            public int ParentType;
            public int MemberIdInt;
            public String clientVersion;

            public String getId() {
                return Id;
            }

            public void setId(String Id) {
                this.Id = Id;
            }

            public String getMemberId() {
                return MemberId;
            }

            public void setMemberId(String MemberId) {
                this.MemberId = MemberId;
            }

            public String getNoteName() {
                return NoteName;
            }

            public void setNoteName(String NoteName) {
                this.NoteName = NoteName;
            }

            public String getNickname() {
                return Nickname;
            }

            public void setNickname(String Nickname) {
                this.Nickname = Nickname;
            }

            public String getRealName() {
                return RealName;
            }

            public void setRealName(String RealName) {
                this.RealName = RealName;
            }

            public String getRole() {
                return Role;
            }

            public void setRole(String Role) {
                this.Role = Role;
            }

            public String getHeadPicUrl() {
                return HeadPicUrl;
            }

            public void setHeadPicUrl(String HeadPicUrl) {
                this.HeadPicUrl = HeadPicUrl;
            }

            public String getTelephone() {
                return Telephone;
            }

            public void setTelephone(String Telephone) {
                this.Telephone = Telephone;
            }

            public String getEmail() {
                return Email;
            }

            public void setEmail(String Email) {
                this.Email = Email;
            }

            public String getIdentity() {
                return Identity;
            }

            public void setIdentity(String Identity) {
                this.Identity = Identity;
            }

            public int getWorkingState() {
                return WorkingState;
            }

            public void setWorkingState(int WorkingState) {
                this.WorkingState = WorkingState;
            }

            public int getHeadTeacherState() {
                return HeadTeacherState;
            }

            public void setHeadTeacherState(int HeadTeacherState) {
                this.HeadTeacherState = HeadTeacherState;
            }

            public int getIsFriend() {
                return IsFriend;
            }

            public void setIsFriend(int IsFriend) {
                this.IsFriend = IsFriend;
            }

            public String getFriendId() {
                return FriendId;
            }

            public void setFriendId(String FriendId) {
                this.FriendId = FriendId;
            }

            public int getGagMark() {
                return GagMark;
            }

            public void setGagMark(int GagMark) {
                this.GagMark = GagMark;
            }

            public int getIsState() {
                return IsState;
            }

            public void setIsState(int IsState) {
                this.IsState = IsState;
            }

            public String getStudentName() {
                return StudentName;
            }

            public void setStudentName(String StudentName) {
                this.StudentName = StudentName;
            }

            public int getParentType() {
                return ParentType;
            }

            public void setParentType(int ParentType) {
                this.ParentType = ParentType;
            }

            public int getMemberIdInt() {
                return MemberIdInt;
            }

            public void setMemberIdInt(int MemberIdInt) {
                this.MemberIdInt = MemberIdInt;
            }

            public String getClientVersion() {
                return clientVersion;
            }

            public void setClientVersion(String clientVersion) {
                this.clientVersion = clientVersion;
            }
        }
    }
}
