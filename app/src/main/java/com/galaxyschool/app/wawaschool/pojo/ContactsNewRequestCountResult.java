package com.galaxyschool.app.wawaschool.pojo;

import com.lqwawa.lqbaselib.net.library.Model;
import com.lqwawa.lqbaselib.net.library.ModelResult;

public class ContactsNewRequestCountResult
        extends ModelResult<ContactsNewRequestCountResult.NewRequestCountModel> {

    public static class NewRequestCountModel extends Model {
        private int ApplyClassNum;
        private int ApplyPersonalNum;

        public int getApplyPersonalNum() {
            return ApplyPersonalNum;
        }

        public void setApplyPersonalNum(int applyPersonalNum) {
            ApplyPersonalNum = applyPersonalNum;
        }

        public int getApplyClassNum() {
            return ApplyClassNum;
        }

        public void setApplyClassNum(int applyClassNum) {
            ApplyClassNum = applyClassNum;
        }
    }

}
