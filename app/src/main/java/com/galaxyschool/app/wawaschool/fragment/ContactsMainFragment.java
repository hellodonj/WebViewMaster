package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.*;
import com.galaxyschool.app.wawaschool.common.LogUtils;
import com.galaxyschool.app.wawaschool.fragment.contacts.ContactItem;
import com.galaxyschool.app.wawaschool.fragment.library.MyFragmentPagerAdapter;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.views.MyViewPager;
import com.galaxyschool.app.wawaschool.views.PopupMenu;

import java.util.ArrayList;
import java.util.List;

public class ContactsMainFragment extends ContactsListFragment
        implements View.OnClickListener {

    public static final String TAG = ContactsMainFragment.class.getSimpleName();

    TextView groupContactsBtn, personalContactsBtn;
    ImageView searchBtn;
    ImageView moreBtn;
    TextView moreText;
    Fragment groupFragment, personalFragment;
    MyViewPager viewPager;
    List<Fragment> fragments;
    FragmentPagerAdapter fragmentAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.contacts_main, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    @Override
    public void finish() {
        getActivity().finish();
    }

    void initViews() {
        View rootView = getView();
        if (rootView == null) {
            return;
        }

        TextView textView = (TextView) rootView.findViewById(R.id.contacts_class);
        if (textView != null) {
            textView.setOnClickListener(this);
        }
        this.groupContactsBtn = textView;

        textView = (TextView) rootView.findViewById(R.id.contacts_personal);
        if (textView != null) {
            textView.setOnClickListener(this);
        }
        this.personalContactsBtn = textView;

        ImageView imageView = (ImageView) rootView.findViewById(R.id.contacts_search_btn);
        if (imageView != null) {
            imageView.setOnClickListener(this);
        }
        this.searchBtn = imageView;

        imageView = (ImageView) rootView.findViewById(R.id.contacts_more_btn);
        if (imageView != null) {
            imageView.setOnClickListener(this);
        }
        this.moreBtn = imageView;

        textView = (TextView) rootView.findViewById(R.id.contacts_more_text);
        if (textView != null) {
            textView.setOnClickListener(this);
        }
        this.moreText = textView;

        this.groupContactsBtn.setEnabled(false);
        this.personalContactsBtn.setEnabled(true);
        if (getUserInfo().isHeaderTeacher()) {
            this.moreBtn.setTag("more");
            this.moreBtn.setImageResource(R.drawable.nav_more_icon);
            this.moreBtn.setVisibility(View.VISIBLE);
            this.moreText.setVisibility(View.GONE);
        } else {
//            this.moreBtn.setTag("addClass");
//            this.moreBtn.setImageResource(R.drawable.nav_add_icon);
            this.moreText.setTag("searchClass");
        }
        this.searchBtn.setTag("searchClass");
        initFragments();
    }

    @Override
    public void onResume() {
    	super.onResume();
    	if (!this.groupContactsBtn.isEnabled()) {
    		enterGroupContacts();
    	} else if (!this.personalContactsBtn.isEnabled()) {
    		enterPersonalContacts();
    	}
    }

    private void initFragments() {
        this.viewPager = (MyViewPager) getView().findViewById(R.id.contacts_view_pager);
        this.fragments = new ArrayList<Fragment>();
        this.groupFragment = new GroupExpandListFragment(); //GroupListFragment();
        this.fragments.add(this.groupFragment);
        this.personalFragment = new PersonalContactsListFragment();
        this.fragments.add(this.personalFragment);
        this.fragmentAdapter = new MyFragmentPagerAdapter(getChildFragmentManager(),
                this.fragments);
        this.viewPager.setAdapter(this.fragmentAdapter);

////        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
//    	this.pickerClassFragment = new GroupListFragment();
////        ft.add(R.id.contacts_body, this.pickerClassFragment, GroupListFragment.TAG);
//        this.personalFragment = new PersonalContactsListFragment();
////        ft.add(R.id.contacts_body, this.personalFragment, PersonalContactsListFragment.TAG);
////        ft.commit();
    }

    public void showMoreMenu(View view) {
        List<PopupMenu.PopupMenuData> itemDatas = new ArrayList<PopupMenu.PopupMenuData>();
        OnItemClickListener itemClickListener = null;
        UserInfo userInfo = getUserInfo();
        if (userInfo.isHeaderTeacher()) {
            itemDatas.add(new PopupMenu.PopupMenuData(0, R.string.scan_me));
            itemDatas.add(new PopupMenu.PopupMenuData(0, R.string.approve));
            itemClickListener = new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    if (position == 0) {
                        enterQrCodeScanning();
                    } else if (position == 1) {
                        enterApproveRequests();
                    }
                }
            };
        } else {
            itemClickListener = new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    if (position == 0) {
//                        enterAddClass();
                    }
                }
            };
        }
//		itemDatas.add(new PopupMenu.PopupMenuData(R.drawable.banji_add_ico, R.string.add_class));
        PopupMenu popupMenu = new PopupMenu(getActivity(), itemClickListener, itemDatas);
        popupMenu.showAsDropDown(view, view.getWidth(), 0);
    }


    void enterGroupContacts() {
//        FragmentManager fm = getChildFragmentManager();
//        this.pickerClassFragment = fm.findFragmentByTag(GroupListFragment.TAG);
//        FragmentTransaction ft = fm.beginTransaction();
//        if (this.pickerClassFragment == null) {
//            this.pickerClassFragment = new GroupListFragment();
////            ft.add(R.id.contacts_body, this.pickerClassFragment, GroupListFragment.TAG);
//        }
//        ft.replace(R.id.contacts_body, this.pickerClassFragment, GroupListFragment.TAG);
////        ft.hide(this.personalFragment);
////        ft.show(this.pickerClassFragment);
//        ft.commit();

        this.viewPager.setCurrentItem(0);
    }

    void enterPersonalContacts() {
//        FragmentManager fm = getChildFragmentManager();
//        this.personalFragment = fm.findFragmentByTag(PersonalContactsListFragment.TAG);
//        FragmentTransaction ft = fm.beginTransaction();
//        if (this.personalFragment == null) {
//            this.personalFragment = new PersonalContactsListFragment();
////            ft.add(R.id.contacts_body, this.personalFragment, PersonalContactsListFragment.TAG);
//        }
//        ft.replace(R.id.contacts_body, this.personalFragment, GroupListFragment.TAG);
////        ft.hide(this.pickerClassFragment);
////        ft.show(this.personalFragment);
//        ft.commit();

        this.viewPager.setCurrentItem(1);
    }

    void enterMore(View view) {
        showMoreMenu(view);
    }

    void enterAssignHomework() {

    }

    void enterApproveRequests() {
        Intent intent = new Intent(getActivity(), ContactsClassRequestListActivity.class);
        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    void enterAddFriend() {
//        Intent intent = new Intent(getActivity(), ContactsAddFriendActivity.class);
//        try {
//            startActivity(intent);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    void enterSearchFriend() {
        Intent intent = new Intent(getActivity(), ContactsSearchFriendActivity.class);
        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void enterContactsPicker() {
        Bundle args = new Bundle();
        args.putInt(ContactsPickerActivity.EXTRA_PICKER_TYPE,
                ContactsPickerActivity.PICKER_TYPE_GROUP
                | ContactsPickerActivity.PICKER_TYPE_PERSONAL);
        args.putInt(ContactsPickerActivity.EXTRA_PICKER_MODE,
                ContactsPickerActivity.PICKER_MODE_SINGLE);
        args.putInt(ContactsPickerActivity.EXTRA_GROUP_TYPE,
                ContactsPickerActivity.GROUP_TYPE_ALL);
        args.putInt(ContactsPickerActivity.EXTRA_MEMBER_TYPE,
                ContactsPickerActivity.MEMBER_TYPE_ALL);
        args.putString(ContactsPickerActivity.EXTRA_PICKER_CONFIRM_BUTTON_TEXT,
                getString(R.string.confirm));
        args.putBoolean(ContactsPickerActivity.EXTRA_USE_EXTENDED_PICKER, true);
        Intent intent = new Intent(getActivity(), ContactsPickerActivity.class);
        intent.putExtras(args);
        try {
            startActivityForResult(intent,
                    ContactsPickerActivity.REQUEST_CODE_PICK_CONTACTS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    void enterAddClass() {
//        Intent intent = new Intent(getActivity(), ContactsAddClassActivity.class);
//        try {
//            startActivity(intent);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    void enterSearchClass() {
        Intent intent = new Intent(getActivity(), ContactsSearchClassActivity.class);
        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void enterQrCodeScanning() {
        Intent intent = new Intent(getActivity(), CaptureActivity.class);
        try {
            startActivity(intent);
        } catch (Exception e) {

        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contacts_class) {
            this.groupContactsBtn.setEnabled(false);
            this.personalContactsBtn.setEnabled(true);
            if (getUserInfo().isHeaderTeacher()) {
                this.moreBtn.setTag("more");
                this.moreBtn.setImageResource(R.drawable.nav_more_icon);
                this.moreBtn.setVisibility(View.VISIBLE);
                this.moreText.setVisibility(View.GONE);
            } else {
//                this.moreBtn.setTag("addClass");
//                this.moreBtn.setImageResource(R.drawable.nav_add_icon);
                this.moreText.setTag("searchClass");
                this.moreBtn.setVisibility(View.GONE);
                this.moreText.setVisibility(View.VISIBLE);
            }
            this.searchBtn.setTag("searchClass");
            enterGroupContacts();
        } else if (v.getId() == R.id.contacts_personal) {
            this.groupContactsBtn.setEnabled(true);
            this.personalContactsBtn.setEnabled(false);
//            this.moreBtn.setTag("addFriend");
//            this.moreBtn.setImageResource(R.drawable.nav_add_icon);
            this.moreBtn.setVisibility(View.GONE);
            this.moreText.setVisibility(View.VISIBLE);
            this.moreText.setTag("searchFriend");
            this.searchBtn.setTag("searchFriend");
            enterPersonalContacts();
        } else if (v.getId() == R.id.contacts_search_btn) {
//            if ("searchFriend".equals((String) v.getTag())) {
//                enterSearchFriend();
//            } else if ("searchClass".equals((String) v.getTag())) {
//                enterSearchClass();
//            }
            finish();
        } else if (v.getId() == R.id.contacts_more_btn) {
            if ("more".equals((String) v.getTag())) {
                enterMore(findViewById(R.id.contacts_header_layout));
            } else if ("addFriend".equals((String) v.getTag())) {
//                enterAddFriend();
            } else if ("addClass".equals((String) v.getTag())) {
//                enterAddClass();
            }
        } else if (v.getId() == R.id.contacts_more_text) {
            enterQrCodeScanning();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ContactsPickerActivity.REQUEST_CODE_PICK_CONTACTS) {
                ArrayList<ContactItem> result =
                        data.getExtras().getParcelableArrayList(
                            ContactsPickerActivity.REQUEST_DATA_PICK_CONTACTS);
                if (result != null && result.size() > 0) {
                    ContactItem obj = null;
                    for (int i = 0; i < result.size(); i++) {
                        obj = result.get(i);
                        LogUtils.log("Contacts Picker", "[" + i + "]: " + obj.getName()
                                + " id: " + obj.getId()
                                + " school: " + obj.getSchoolId()
                                + " class: " + obj.getClassId());
                    }
                }
            }
        }
        Fragment fragment = null;
        if (!this.groupContactsBtn.isEnabled()) {
            fragment = this.groupFragment;
        } else if (!this.personalContactsBtn.isEnabled()) {
            fragment = this.personalFragment;
        }
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

}
