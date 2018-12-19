package com.galaxyschool.app.wawaschool.fragment.contacts;

import java.util.List;

public interface ContactsPickerListener {

    public void onContactsPicked(List<ContactItem> result);

    public interface GroupPickerListener {
        public void onGroupPicked(List<ContactItem> result);
    }

    public interface GroupMemberPickerListener {
        public void onGroupMemberPicked(List<ContactItem> result);
    }

    public interface PersonalContactsPickerListener {
        public void onPersonalContactsPicked(List<ContactItem> result);
    }

    public interface FamilyContactsPickerListener {
        public void onFamilyContactsPicked(List<ContactItem> result);
    }

}
