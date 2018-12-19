package com.galaxyschool.app.wawaschool.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.fragment.contacts.ContactItem;
import com.galaxyschool.app.wawaschool.fragment.contacts.ContactsPickerListener;
import com.galaxyschool.app.wawaschool.fragment.library.MyFragmentPagerAdapter;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.views.MyViewPager;
import java.util.ArrayList;
import java.util.List;


/**
 * ================================================
 * 作    者：Blizzard-liu
 * 版    本：1.0
 * 创建日期：2017/11/23 16:28
 * 描    述： 选择班级  选择小组
 * 修订历史：
 * ================================================
 */

public class PickerClassAndGroupFragment extends ContactsPickerEntryFragment
        implements View.OnClickListener, ContactsPickerListener {

    public static final String TAG = PickerClassAndGroupFragment.class.getSimpleName();
    private GroupExpandPickerFragment groupExpandPickerFragment;
    private GroupExpandPickerGroupFragment pickerGroupFragment;
    MyViewPager viewPager;
    FragmentPagerAdapter fragmentAdapter;
    ContactsPickerListener pickerListener;
    private TextView confirmTextV;
    private TextView clearTextV;

    List<Fragment> fragments = new ArrayList<>();
    List<TextView> tabViews = new ArrayList<>();
    private List<ContactItem> selectDataList = new ArrayList<>();
    private boolean hasSelectGroupData;
    private boolean hasSelectClassData;

    public static PickerClassAndGroupFragment newInstance(Bundle args) {
        PickerClassAndGroupFragment fragment = new PickerClassAndGroupFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.contacts_extended, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
        setInternalPickerListener(this);
    }

    private void initView() {
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
            tabViews.add(textView);
            textView.setText(getString(R.string.select_class));
            textView.setVisibility(View.VISIBLE);
            textView.setOnClickListener(this);
        }

        textView = (TextView) findViewById(R.id.contacts_personal);
        if (textView != null) {
            tabViews.add(textView);
            textView.setText(getString(R.string.select_group));
            textView.setVisibility(View.VISIBLE);
            textView.setOnClickListener(this);
        }

        clearTextV = (TextView) findViewById(R.id.contacts_picker_clear);
        if (clearTextV != null) {
            clearTextV.setEnabled(false);
            clearTextV.setOnClickListener(this);
        }

        confirmTextV = (TextView) findViewById(R.id.contacts_picker_confirm);
        if (confirmTextV != null) {
            confirmTextV.setEnabled(false);
            confirmTextV.setOnClickListener(this);
        }
        findViewById(R.id.contacts_picker_bar_layout).setVisibility(View.VISIBLE);
        initFragments();
        initTabs();
    }

    private void initFragments() {
        this.viewPager = (MyViewPager) getView().findViewById(R.id.contacts_view_pager);
        groupExpandPickerFragment = new GroupExpandPickerFragment();
        groupExpandPickerFragment.setPickerListener(this);
        groupExpandPickerFragment.setMultiSelection(true);
        groupExpandPickerFragment.setArguments(getArguments());
        pickerGroupFragment = new GroupExpandPickerGroupFragment();
        pickerGroupFragment.setArguments(getArguments());
        pickerGroupFragment.setPickerListener(this);
        pickerGroupFragment.setMultiSelection(true);
        fragments.add(groupExpandPickerFragment);
        fragments.add(pickerGroupFragment);

        this.fragmentAdapter = new MyFragmentPagerAdapter(getChildFragmentManager(),
                this.fragments);
        this.viewPager.setAdapter(this.fragmentAdapter);
    }


    private void initTabs() {
        int tabsCount = this.tabViews.size();
        if (tabsCount > 0) {
            this.tabViews.get(0).setEnabled(false);
            for (int i = 1; i < tabsCount; i++) {
                this.tabViews.get(i).setEnabled(true);
            }
        }
        this.tabViews.get(0).setBackgroundResource(R.drawable.tab_button_l);
        this.tabViews.get(tabsCount - 1)
                .setBackgroundResource(R.drawable.tab_button_r);
        for (int i = 1; i < tabsCount - 1; i++) {
            this.tabViews.get(i).setBackgroundResource(R.drawable.tab_button_m);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contacts_back_btn) {
            finish();
        } else if (v.getId() == R.id.contacts_group){
            //班级通讯录
//            groupExpandPickerFragment.selectAllContacts(false);
            tabViews.get(0).setEnabled(false);
            tabViews.get(1).setEnabled(true);
            this.viewPager.setCurrentItem(0);
        } else if (v.getId() == R.id.contacts_personal){
            //小组
//            pickerGroupFragment.selectAllContacts(false);
            tabViews.get(0).setEnabled(true);
            tabViews.get(1).setEnabled(false);
            this.viewPager.setCurrentItem(1);
        } else if (v.getId() == R.id.contacts_picker_confirm){
            commitSelectData();
        } else if (v.getId() == R.id.contacts_picker_clear){
            clearSelectData();
        }
    }

    @Override
    void enterGroupContacts() {

    }

    void completePickContacts(List<ContactItem> result) {

        boolean superUser = getArguments().getBoolean(
                Constants.EXTRA_PICKER_SUPERUSER, false);
        if (!superUser) {
            if (this.pickerListener != null) {
                this.pickerListener.onContactsPicked(result);
            } else {
                notifyPickedResult(result);
            }
        } else {
//            superWorks(result);
            if (result != null && result.size() > 0) {
                selectDataList.addAll(result);
            }
        }
    }

    private void commitSelectData(){
        selectDataList.clear();
        if (groupExpandPickerFragment != null){
            groupExpandPickerFragment.completePickContacts();
        }
        if (pickerGroupFragment != null){
            pickerGroupFragment.completePickContacts();
        }
        if (selectDataList.size() == 0){
            TipsHelper.showToast(getActivity(), R.string.pls_select_a_class_at_least);
        } else {
            superWorks(selectDataList);
        }
    }

    private void clearSelectData(){
        if (groupExpandPickerFragment != null) {
            groupExpandPickerFragment.selectAllContacts(false);
        }
        if (pickerGroupFragment != null) {
            pickerGroupFragment.selectAllContacts(false);
        }
    }

    @Override
    public void onContactsPicked(List<ContactItem> result) {
        completePickContacts(result);
    }

    /**
     * fromType = 0 班级小组 fromType == 1 班级
     * @param fromType
     * @param selected
     */
    public void setItemSelected(int fromType,boolean selected){
        if (fromType == 0){
            //小组
            hasSelectGroupData = selected;
        } else {
            //班级
            hasSelectClassData = selected;
        }
        notifyPickerBar(hasSelectClassData || hasSelectGroupData);
    }

    private void notifyPickerBar(boolean selected) {
        clearTextV.setEnabled(selected);
        confirmTextV.setEnabled(selected);
    }

    public interface Constatnts {
    }
}
