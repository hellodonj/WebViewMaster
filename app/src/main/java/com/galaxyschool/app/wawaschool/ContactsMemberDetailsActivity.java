package com.galaxyschool.app.wawaschool;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import com.galaxyschool.app.wawaschool.fragment.FriendDetailsFragment;
import com.galaxyschool.app.wawaschool.fragment.GroupMemberDetailsFragment;
import com.galaxyschool.app.wawaschool.fragment.PersonInfoFragment;

public class ContactsMemberDetailsActivity extends BaseFragmentActivity {

   public static final String EXTRA_MEMBER_TYPE = "type";
   public static final String EXTRA_MEMBER_ROLE = "role";
   public static final String EXTRA_MEMBER_ID = "id";

   public static final int MEMBER_TYPE_FRIEND = 0;
   public static final int MEMBER_TYPE_GROUP = 1;
   public static final int MEMBER_TYPE_PERSON = 2;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_contacts);

      Bundle args = getIntent().getExtras();
      int type = args.getInt(EXTRA_MEMBER_TYPE);
      Fragment fragment = null;
      String tag = null;
      if (type == MEMBER_TYPE_PERSON) {
         fragment = new PersonInfoFragment();
         tag = PersonInfoFragment.TAG;
      } else if (type == MEMBER_TYPE_FRIEND) {
         fragment = new FriendDetailsFragment();
         tag = FriendDetailsFragment.TAG;
      } else if (type == MEMBER_TYPE_GROUP) {
         fragment = new GroupMemberDetailsFragment();
         tag = GroupMemberDetailsFragment.TAG;
      }
      if (fragment != null) {
         FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
         fragment.setArguments(args);
         ft.replace(R.id.contacts_layout, fragment, tag);
         ft.commit();
      }
   }

}