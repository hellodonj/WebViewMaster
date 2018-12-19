package com.galaxyschool.app.wawaschool.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.CaptureActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.SubscribeSearchActivity;
import com.galaxyschool.app.wawaschool.fragment.library.MyFragmentPagerAdapter;
import com.galaxyschool.app.wawaschool.views.MyViewPager;
import com.galaxyschool.app.wawaschool.views.PopupMenu;
import com.galaxyschool.app.wawaschool.views.sortlistview.ClearEditText;

import java.util.ArrayList;
import java.util.List;

public class SubscribeMainFragment extends ContactsListFragment {

    public static final String TAG = ContactsMainFragment.class.getSimpleName();

    private TextView schoolTab, personTab;
    private ClearEditText searchBar;
    private Fragment schoolFragment, personFragment;
    private MyViewPager viewPager;
    private int subscripeSearchType;
    private TextView rightTextView;
    public static final int MENU_ID_SCAN = 0;
    public static final int MENU_ID_ADD_SB = 1;
    public static final int MENU_ID_ADD_AUTHORITY = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.subscribe_main, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!this.schoolTab.isEnabled()) {
            enterGroupContacts();
        } else if (!this.personTab.isEnabled()) {
            enterPersonalContacts();
        }
    }

    private void initViews() {
        TextView textView = ((TextView) findViewById(R.id.contacts_header_title));
        if (textView != null) {
            textView.setText(R.string.subscribe);
        }
        rightTextView = ((TextView) findViewById(R.id.contacts_header_right_btn));
        if (rightTextView != null) {
            rightTextView.setVisibility(View.GONE);
            rightTextView.setText(getString(R.string.add_sb));
            rightTextView.setTextColor(getResources().getColor(R.color.text_green));
            rightTextView.setOnClickListener(this);
        }

        ImageView imageView = (ImageView) findViewById(R.id.contacts_header_right_ico);
        if (imageView != null) {
            imageView.setImageResource(R.drawable.selector_icon_plus);
            imageView.setOnClickListener(this);
        }

        this.searchBar = (ClearEditText) findViewById(R.id.search_keyword);
        this.searchBar.setVisibility(View.GONE);
        this.searchBar.setFocusable(false);
//        this.searchBar.setHint(R.string.subscribe_search);
        this.searchBar.setFocusableInTouchMode(false);
        this.searchBar.setInputType(InputType.TYPE_NULL);
        this.searchBar.setKeyListener(null);
        this.searchBar.setOnClickListener(this);

        textView = (TextView) findViewById(R.id.contacts_tab_person);
        if (textView != null) {
            textView.setOnClickListener(this);
        }
        this.personTab = textView;

        textView = (TextView) findViewById(R.id.contacts_tab_school);
        if (textView != null) {
            textView.setOnClickListener(this);
        }
        this.schoolTab = textView;

        this.schoolTab.setEnabled(true);
        this.personTab.setEnabled(false);
        updateSearchBarHint();
        initFragments();
    }

    private void initFragments() {
        this.viewPager = (MyViewPager) getView().findViewById(R.id.contacts_view_pager);
        List<Fragment> fragments = new ArrayList<Fragment>();
        this.schoolFragment = new SubscribeSchoolListFragment();
        fragments.add(this.schoolFragment);
        this.personFragment = new SubscribePersonListFragment();
        fragments.add(this.personFragment);
        FragmentPagerAdapter fragmentAdapter = new MyFragmentPagerAdapter(
                getChildFragmentManager(), fragments);
        this.viewPager.setAdapter(fragmentAdapter);
    }

    private void enterGroupContacts() {
        this.viewPager.setCurrentItem(0);
    }

    private void enterPersonalContacts() {
        this.viewPager.setCurrentItem(1);
    }

    private void enterQrcodeScanning() {
        Intent intent = new Intent(getActivity(), CaptureActivity.class);
        startActivity(intent);
    }

    private void enterSubscribeSearch() {
        Intent intent = new Intent(getActivity(), SubscribeSearchActivity.class);
        intent.putExtra(SubscribeSearchActivity.EXTRA_SUBSCRIPE_SEARCH_TYPE, subscripeSearchType);
        startActivity(intent);
    }

    private void updateSearchBarHint() {
        if(!this.personTab.isEnabled()) {
            this.searchBar.setHint(R.string.subscribe_search_user);
            subscripeSearchType = SubscribeSearchActivity.SUBSCRIPE_SEARCH_USER;
            rightTextView.setText(getString(R.string.add_sb));
        }
        if(!this.schoolTab.isEnabled()) {
            this.searchBar.setHint(R.string.subscribe_search_school);
            subscripeSearchType = SubscribeSearchActivity.SUBSCRIPE_SEARCH_SCHOOL;
            rightTextView.setText(getString(R.string.add_authority));
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contacts_header_right_ico){
            showMoreMenu(v);

        }else if (v.getId() == R.id.contacts_header_right_btn) {
//            enterQrcodeScanning();
            enterSubscribeSearch();
        } else if (v.getId() == R.id.search_keyword) {
//            enterSubscribeSearch();
            searchLoacalResources();
        } else if (v.getId() == R.id.contacts_tab_school) {
            this.schoolTab.setEnabled(false);
            this.personTab.setEnabled(true);
            updateSearchBarHint();
            enterGroupContacts();
        } else if (v.getId() == R.id.contacts_tab_person) {
            this.schoolTab.setEnabled(true);
            this.personTab.setEnabled(false);
            updateSearchBarHint();
            enterPersonalContacts();
        } else {
            super.onClick(v);
        }
    }

    private void showMoreMenu(View view) {

        List<PopupMenu.PopupMenuData> items = new ArrayList();
        PopupMenu.PopupMenuData data = null;
            //扫一扫
            data = new PopupMenu.PopupMenuData(0, R.string.scan_me, MENU_ID_SCAN);
            items.add(data);

        if (subscripeSearchType == SubscribeSearchActivity.SUBSCRIPE_SEARCH_USER){
            //添加个人
            data = new PopupMenu.PopupMenuData(0, R.string.add_sb, MENU_ID_ADD_SB);
            items.add(data);
        }else if (subscripeSearchType == SubscribeSearchActivity.SUBSCRIPE_SEARCH_SCHOOL){
            //添加机构
            data = new PopupMenu.PopupMenuData(0, R.string.add_authority, MENU_ID_ADD_AUTHORITY);
            items.add(data);
        }


        if (items.size() <= 0) {
            return;
        }

        AdapterView.OnItemClickListener itemClickListener =
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        if (view.getTag() == null) {
                            return;
                        }
                        PopupMenu.PopupMenuData data = (PopupMenu.PopupMenuData) view.getTag();
                        if (data.getId() == MENU_ID_SCAN) {
                            enterCaptureActivity();
                        } else if (data.getId() == MENU_ID_ADD_SB ||
                                data.getId() == MENU_ID_ADD_AUTHORITY) {
                            enterSubscribeSearch();
                        }
                    }
                };
        PopupMenu popupMenu = new PopupMenu(getActivity(), itemClickListener, items);
        popupMenu.showAsDropDown(view, view.getWidth(), 0);
    }

    private void enterCaptureActivity() {

        Intent intent = new Intent(getActivity(), CaptureActivity.class);
        startActivity(intent);
    }

    private void searchLoacalResources() {
        if (subscripeSearchType == SubscribeSearchActivity.SUBSCRIPE_SEARCH_SCHOOL ){

        }else if (subscripeSearchType == SubscribeSearchActivity.SUBSCRIPE_SEARCH_USER){

        }
    }

}
