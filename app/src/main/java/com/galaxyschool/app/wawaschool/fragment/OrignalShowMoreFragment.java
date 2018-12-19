package com.galaxyschool.app.wawaschool.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.Request;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.MyFragmentPagerAdapter;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.fragment.resource.NewResourcePadAdapterViewHelper;
import com.lqwawa.lqbaselib.net.ThisStringRequest;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.TempNewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.TempShowNewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseType;
import com.galaxyschool.app.wawaschool.views.MyViewPager;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.sortlistview.ClearEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by KnIghT on 16-5-10.
 */
public class OrignalShowMoreFragment extends ContactsListFragment {
    public static final String TAG = OrignalShowMoreFragment.class.getSimpleName();
    private static final int MAX_BOOKS_PER_ROW = 4;
    private final static int PAGE_SIZE = 32;
    public static final String SOURCE_TYPE="source_type_orignal_show";
    private MyViewPager mViewPager;
    List<Fragment> mFragments;
    MyFragmentPagerAdapter fragmentAdapter;

    private TextView leftTab, rightTab;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_orignal_shore_main, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }



    @Override
    public void onResume() {
        super.onResume();
    }


    private void initFragment() {

        mViewPager = (MyViewPager) getView().findViewById(R.id.viewpager);
        this.mFragments = new ArrayList<Fragment>();
        Fragment fragment = new OrignalShowMoreChildFragment();
        Bundle args = new Bundle();
        args.putInt(OrignalShowMoreChildFragment.SOURCE_TYPE, OrignalShowMoreChildFragment.SOURCE_TYPE_LATEST);
        fragment.setArguments(args);
        this.mFragments.add(fragment);
        fragment = new OrignalShowMoreChildFragment();
        args = new Bundle();
        args.putInt(OrignalShowMoreChildFragment.SOURCE_TYPE, OrignalShowMoreChildFragment.SOURCE_TYPE_HOT);
        fragment.setArguments(args);
        this.mFragments.add(fragment);
        this.fragmentAdapter = new MyFragmentPagerAdapter(
                getChildFragmentManager(), mFragments);
        mViewPager.setAdapter(this.fragmentAdapter);
    }



    private void initViews() {
        TextView textView = ((TextView) findViewById(R.id.contacts_header_title));
        if (textView != null) {
            textView.setText(R.string.hint_show_book);
        }

        ImageView    rightImageView = ((ImageView) findViewById(R.id.contacts_header_left_btn));
        if (rightImageView != null) {
            rightImageView.setVisibility(View.VISIBLE);
            rightImageView.setOnClickListener(this);
        }
        textView = (TextView) findViewById(R.id.tab_left);
        if (textView != null) {
            textView.setOnClickListener(this);
        }
        this.leftTab = textView;

        textView = (TextView) findViewById(R.id.tab_right);
        if (textView != null) {
            textView.setOnClickListener(this);
        }
        this.rightTab = textView;
        initFragment();
        this.mViewPager.setCurrentItem(0);
        this.leftTab.setEnabled(false);
        this.rightTab.setEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.contacts_header_left_btn:
                if(getActivity() != null) {
                    getActivity().finish();
                }
                break;
            case R.id.tab_left:
                this.rightTab.setEnabled(true);
                this.leftTab.setEnabled(false);
                this.mViewPager.setCurrentItem(0);
                break;
            case R.id.tab_right:
                this.rightTab.setEnabled(false);
                this.leftTab.setEnabled(true);
                this.mViewPager.setCurrentItem(1);
                break;

        }
    }

}
