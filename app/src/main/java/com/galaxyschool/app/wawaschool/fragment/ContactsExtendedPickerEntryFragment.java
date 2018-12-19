package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.ContactsPickerActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.fragment.contacts.ContactItem;
import com.galaxyschool.app.wawaschool.fragment.contacts.ContactsPickerListener;
import com.galaxyschool.app.wawaschool.fragment.library.MyFragmentPagerAdapter;
import com.galaxyschool.app.wawaschool.pojo.UploadParameter;
import com.galaxyschool.app.wawaschool.views.MyViewPager;
import com.osastudio.common.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ContactsExtendedPickerEntryFragment extends ContactsPickerEntryFragment
        implements View.OnClickListener, ContactsPickerListener {

    public static final String TAG = ContactsExtendedPickerEntryFragment.class.getSimpleName();

    TextView groupTab, personalTab, familyTab;
    Fragment groupFragment, personalFragment, familyFragment;
    MyViewPager viewPager;
    List<Fragment> fragments = new ArrayList();
    FragmentPagerAdapter fragmentAdapter;
    ContactsPickerListener pickerListener;
    int pickerType;
    Set<Integer> pickerTypes = new HashSet();
    List<TextView> tabViews = new ArrayList();
    Map<TextView, Fragment> fragmentMap = new HashMap();
    Map<String, ContactItem> pickedContactsMap = new HashMap();
    UploadParameter uploadParameter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.contacts_extended, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getIntent();
        initViews();
    }
    private void getIntent(){
        uploadParameter= (UploadParameter) getArguments().getSerializable(UploadParameter.class
                .getSimpleName());
    }
    void initViews() {
        parsePickerTypes();

        View view = findViewById(R.id.contacts_header_layout);
        if (view != null) {
            view.setVisibility(View.GONE);
        }

        view = findViewById(R.id.contacts_tab_layout);
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }

        ImageView imageView = (ImageView) findViewById(R.id.contacts_back_btn);
        if (imageView != null) {
            imageView.setOnClickListener(this);
        }

        TextView textView = (TextView) findViewById(R.id.contacts_group);
        if (textView != null) {
            textView.setOnClickListener(this);
        }
        this.groupTab = textView;

        textView = (TextView) findViewById(R.id.contacts_personal);
        if (textView != null) {
            textView.setOnClickListener(this);
        }
        this.personalTab = textView;

        textView = (TextView) findViewById(R.id.contacts_family);
        if (textView != null) {
            textView.setOnClickListener(this);
        }
        this.familyTab = textView;

        textView = (TextView) findViewById(R.id.contacts_picker_clear);
        if (textView != null) {
            textView.setOnClickListener(this);
        }

        textView = (TextView) findViewById(R.id.contacts_picker_confirm);
        if (textView != null) {
            textView.setOnClickListener(this);
        }

        initFragments();
        initTabs();
        setInternalPickerListener(this);
    }

    private void parsePickerTypes() {
        this.pickerType = getArguments().getInt(Constants.EXTRA_PICKER_TYPE);
        this.pickerTypes = new HashSet();
        if ((this.pickerType & Constants.PICKER_TYPE_GROUP) != 0) {
            this.pickerTypes.add(Constants.PICKER_TYPE_GROUP);
        } else if ((this.pickerType & Constants.PICKER_TYPE_MEMBER) != 0) {
            this.pickerTypes.add(Constants.PICKER_TYPE_MEMBER);
        }
        if ((this.pickerType & Constants.PICKER_TYPE_PERSONAL) != 0) {
            this.pickerTypes.add(Constants.PICKER_TYPE_PERSONAL);
        }
        if ((this.pickerType & Constants.PICKER_TYPE_FAMILY) != 0) {
            this.pickerTypes.add(Constants.PICKER_TYPE_FAMILY);
        }
    }

    private boolean hasPickerType(int type) {
        return this.pickerTypes.contains(type);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!this.groupTab.isEnabled()) {
            enterGroupContacts();
        } else if (!this.personalTab.isEnabled()) {
            enterPersonalContacts();
        } else if (!this.familyTab.isEnabled()) {
            enterFamilyContacts();
        }
    }

    public void setPickerListener(ContactsPickerListener listener) {
        this.pickerListener = listener;
    }

    private void initFragments() {
        this.viewPager = (MyViewPager) getView().findViewById(R.id.contacts_view_pager);

        if (hasPickerType(Constants.PICKER_TYPE_GROUP)
                || hasPickerType(Constants.PICKER_TYPE_MEMBER)) {
            GroupExpandPickerFragment groupFragment = new GroupExpandPickerFragment(); //GroupListFragment();
            groupFragment.setArguments(getArguments());
            groupFragment.setPickerListener(this);
            this.fragments.add(groupFragment);
            this.groupFragment = groupFragment;
            this.groupTab.setVisibility(View.VISIBLE);
            this.tabViews.add(this.groupTab);
            this.fragmentMap.put(this.groupTab, this.groupFragment);
        }
        if (hasPickerType(Constants.PICKER_TYPE_PERSONAL)) {
            PersonalContactsPickerFragment personalFragment = new PersonalContactsPickerFragment();
            personalFragment.setArguments(getArguments());
            personalFragment.setPickerListener(this);
            this.fragments.add(personalFragment);
            this.personalFragment = personalFragment;
            this.personalTab.setVisibility(View.VISIBLE);
            this.tabViews.add(this.personalTab);
            this.fragmentMap.put(this.personalTab, this.personalFragment);
        }
        if (hasPickerType(Constants.PICKER_TYPE_FAMILY)) {
            FamilyContactsPickerFragment familyFragment = new FamilyContactsPickerFragment();
            familyFragment.setArguments(getArguments());
            familyFragment.setPickerListener(this);
            this.fragments.add(familyFragment);
            this.familyFragment = familyFragment;
            this.familyTab.setVisibility(View.VISIBLE);
            this.tabViews.add(this.familyTab);
            this.fragmentMap.put(this.familyTab, this.familyFragment);
        }

        this.fragmentAdapter = new MyFragmentPagerAdapter(getChildFragmentManager(),
                this.fragments);
        this.viewPager.setAdapter(this.fragmentAdapter);
    }

    void initTabs() {
        int tabsCount = this.tabViews.size();
        if (tabsCount > 0) {
            this.tabViews.get(0).setEnabled(false);
            for (int i = 1; i < tabsCount; i++) {
                this.tabViews.get(i).setEnabled(true);
            }
        }
        if (tabsCount == 1) {
            View view = findViewById(R.id.contacts_header_layout);
            if (view != null) {
                view.setVisibility(View.VISIBLE);
            }
            TextView textView = (TextView) findViewById(R.id.contacts_header_title);
            if (textView != null) {
                if (hasPickerType(Constants.PICKER_TYPE_GROUP)
                        || hasPickerType(Constants.PICKER_TYPE_MEMBER)) {
                    if (getArguments().getInt(Constants.EXTRA_GROUP_TYPE) ==
                            Constants.GROUP_TYPE_SCHOOL) {
                        textView.setText(R.string.select_school);
                    } else {
                        textView.setText(R.string.select_class);

                    }
                } else if (hasPickerType(Constants.PICKER_TYPE_PERSONAL)) {
                    textView.setText(R.string.select_friend);
                } else if (hasPickerType(Constants.PICKER_TYPE_FAMILY)) {
                    textView.setText(R.string.select_my_children);
                }
            }
            ImageView imageView = (ImageView) findViewById(R.id.contacts_header_left_btn);
            if (imageView != null) {
                imageView.setOnClickListener(this);
            }
            view = findViewById(R.id.contacts_tab_layout);
            if (view != null) {
                view.setVisibility(View.GONE);
            }
//            TextView tabView = this.tabViews.get(0);
//            tabView.setBackgroundColor(
//                    getActivity().getResources().getColor(android.R.color.transparent));
//            tabView.setTextColor(
//                    getActivity().getResources().getColor(R.color.text_black));
//            tabView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
//                    getActivity().getResources().getDimension(R.dimen.text_size_title));
//            ViewGroup.LayoutParams params = tabView.getLayoutParams();
//            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
//            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//            tabView.setLayoutParams(params);
        } else {
            this.tabViews.get(0).setBackgroundResource(R.drawable.tab_button_l);
            this.tabViews.get(tabsCount - 1)
                    .setBackgroundResource(R.drawable.tab_button_r);
            for (int i = 1; i < tabsCount - 1; i++) {
                this.tabViews.get(i).setBackgroundResource(R.drawable.tab_button_m);
            }
        }
    }

    void enterGroupContacts() {
        for (int i = 0; i < this.fragments.size(); i++) {
            if (this.fragments.get(i) == this.groupFragment) {
                this.viewPager.setCurrentItem(i);
            }
        }
    }

    void enterPersonalContacts() {
        for (int i = 0; i < this.fragments.size(); i++) {
            if (this.fragments.get(i) == this.personalFragment) {
                this.viewPager.setCurrentItem(i);
            }
        }
    }

    void enterFamilyContacts() {
        for (int i = 0; i < this.fragments.size(); i++) {
            if (this.fragments.get(i) == this.familyFragment) {
                this.viewPager.setCurrentItem(i);
            }
        }
    }

    @Override
    public void onContactsPicked(List<ContactItem> result) {
        this.pickedContactsMap.clear();
        if (result != null && result.size() > 0) {
            for (ContactItem item : result) {
                this.pickedContactsMap.put(item.getId(), item);
            }
        }
        completePickContacts();
    }

    List<ContactItem> getPickedContactsList() {
        if (this.pickedContactsMap.size() > 0) {
            List<ContactItem> result = new ArrayList();
            for (Map.Entry<String, ContactItem> entry :
                    this.pickedContactsMap.entrySet()) {
                result.add(entry.getValue());
            }
            return result;
        }
        return null;
    }

    void completePickContacts() {
        List<ContactItem> result = getPickedContactsList();
//        if (result == null || result.size() <= 0) {
//            Toast.makeText(getActivity(),
//                    R.string.pls_select_a_friend_at_least,
//                    Toast.LENGTH_LONG).show();
//            return;
//        }
        boolean superUser = getArguments().getBoolean(
                Constants.EXTRA_PICKER_SUPERUSER, false);
        if (!superUser) {
            if (this.pickerListener != null) {
                this.pickerListener.onContactsPicked(result);
            } else {
                notifyPickedResult(result);
            }
        } else {
            superWorks(result);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contacts_header_left_btn) {
            exit();
        } else if (v.getId() == R.id.contacts_back_btn) {
            finish();
        } else if (v.getId() == R.id.contacts_picker_clear) {
            this.pickedContactsMap.clear();
        } else if (v.getId() == R.id.contacts_picker_confirm) {
            completePickContacts();
        } else if (v == this.groupTab) {
            this.groupTab.setEnabled(false);
            this.personalTab.setEnabled(true);
            this.familyTab.setEnabled(true);
            enterGroupContacts();
        } else if (v == this.personalTab) {
            this.groupTab.setEnabled(true);
            this.personalTab.setEnabled(false);
            this.familyTab.setEnabled(true);
            enterPersonalContacts();
        } else if (v == this.familyTab) {
            this.groupTab.setEnabled(true);
            this.personalTab.setEnabled(true);
            this.familyTab.setEnabled(false);
            enterFamilyContacts();
        }
    }

    /**
     * 退出当前界面
     */
    private void exit() {
        int num=getFragmentManager().getBackStackEntryCount();

        if (num == 0){
            getActivity().finish();
            return;
        }

        while (num > 1) {
            popStack();
            num = num - 1;
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
                        Utils.log("Contacts Picker", "[" + i + "]: " + obj.getName()
                                + " id: " + obj.getId()
                                + " school: " + obj.getSchoolId()
                                + " class: " + obj.getClassId());
                    }
                }
            }
        }
    }

}
