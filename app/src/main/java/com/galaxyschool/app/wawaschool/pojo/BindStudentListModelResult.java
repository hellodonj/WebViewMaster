package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.JSONObject;
import com.lqwawa.lqbaselib.net.library.Model;
import com.lqwawa.lqbaselib.net.library.ModelDataParser;
import com.lqwawa.lqbaselib.net.library.ModelResult;

import java.util.List;

/**
 * 绑定学生
 */
public class BindStudentListModelResult
        extends ModelResult<BindStudentListModelResult.BindStudentListModel> {

    public static class BindStudentListModel extends Model{
        private String Id;
        private String ClassId;
        private String Recipient;
        private int CheckState;
        private int IsRead;
        private int Type;
        private String MemberId;
        private String HeadPicUrl;
        private int VersionCode;
        private String StudentName;
        private int ParentType;
        private String ValidationMessage;
        private String SchoolID;
        private String role;

        private String NewFriendId;
        private String NewFriendName;

        public String getId() {
            return Id;
        }

        public void setId(String id) {
            Id = id;
        }

        public String getClassId() {
            return ClassId;
        }

        public void setClassId(String classId) {
            ClassId = classId;
        }

        public String getRecipient() {
            return Recipient;
        }

        public void setRecipient(String recipient) {
            Recipient = recipient;
        }

        public int getCheckState() {
            return CheckState;
        }

        public void setCheckState(int checkState) {
            CheckState = checkState;
        }

        public int getIsRead() {
            return IsRead;
        }

        public void setIsRead(int isRead) {
            IsRead = isRead;
        }

        public int getType() {
            return Type;
        }

        public void setType(int type) {
            Type = type;
        }

        public String getMemberId() {
            return MemberId;
        }

        public void setMemberId(String memberId) {
            MemberId = memberId;
        }

        public String getHeadPicUrl() {
            return HeadPicUrl;
        }

        public void setHeadPicUrl(String headPicUrl) {
            HeadPicUrl = headPicUrl;
        }

        public int getVersionCode() {
            return VersionCode;
        }

        public void setVersionCode(int versionCode) {
            VersionCode = versionCode;
        }

        public String getStudentName() {
            return StudentName;
        }

        public void setStudentName(String studentName) {
            StudentName = studentName;
        }

        public int getParentType() {
            return ParentType;
        }

        public void setParentType(int parentType) {
            ParentType = parentType;
        }

        public String getValidationMessage() {
            return ValidationMessage;
        }

        public void setValidationMessage(String validationMessage) {
            ValidationMessage = validationMessage;
        }

        public String getSchoolID() {
            return SchoolID;
        }

        public void setSchoolID(String schoolID) {
            SchoolID = schoolID;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getNewFriendId() {
            return NewFriendId;
        }

        public void setNewFriendId(String newFriendId) {
            NewFriendId = newFriendId;
        }

        public String getNewFriendName() {
            return NewFriendName;
        }

        public void setNewFriendName(String newFriendName) {
            NewFriendName = newFriendName;
        }

        private List<StudentListInfo> StudentList;

        public void setStudentList(List<StudentListInfo> studentList) {
            StudentList = studentList;
        }

        public List<StudentListInfo> getStudentList() {
            return StudentList;
        }
    }
}
