package com.galaxyschool.app.wawaschool.pojo;

import com.lqwawa.lqbaselib.net.library.Model;
import com.lqwawa.lqbaselib.net.library.ModelResult;

import java.util.List;

public class ContactsClassMemberDetailsListResult
        extends ModelResult<ContactsClassMemberDetailsListResult.ContactsClassMemberDetailsListModel> {

    public static class ContactsClassMemberDetailsListModel extends Model {
        private List<ContactsClassMemberDetails> PersonalList;

        public List<ContactsClassMemberDetails> getPersonalList() {
            return PersonalList;
        }

        public void setPersonalList(List<ContactsClassMemberDetails> personalList) {
            PersonalList = personalList;
        }
    }

}
