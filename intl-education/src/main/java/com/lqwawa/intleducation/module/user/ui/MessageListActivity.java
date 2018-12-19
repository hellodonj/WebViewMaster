package com.lqwawa.intleducation.module.user.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RadioGroup;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseFragmentActivity;
import com.lqwawa.intleducation.base.widgets.TopBar;

/**
 * Created by XChen on 2016/12/2.
 * email:man0fchina@foxmail.com
 * 消息列表
 */
public class MessageListActivity extends MyBaseFragmentActivity implements View.OnClickListener {
    private static final String TAG = "MessageListActivity";
    public static int Rs_MessageRead = 1500;

    private TopBar topBar;
    //tab
    private RadioGroup rg_tab;

    MyNoticeListFragment noticeListFragment;
    MyPrivateMsgListFragment privateMsgListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        topBar = (TopBar) findViewById(R.id.top_bar);
        rg_tab = (RadioGroup) findViewById(R.id.rg_tab);
        initViews();
    }

    private void initViews() {
        topBar.setBack(true);
        topBar.setTitle(getResources().getString(R.string.message));

        noticeListFragment = new MyNoticeListFragment();
        privateMsgListFragment = new MyPrivateMsgListFragment();

        FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, noticeListFragment);
        fragmentTransaction.add(R.id.fragment_container, privateMsgListFragment);
        fragmentTransaction.hide(noticeListFragment);
        fragmentTransaction.show(privateMsgListFragment);
        noticeListFragment.setUserVisibleHint(true);
        fragmentTransaction.commit();

        rg_tab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                FragmentTransaction fragmentTransaction =
                        getSupportFragmentManager().beginTransaction();
                fragmentTransaction.hide(noticeListFragment);
                fragmentTransaction.hide(privateMsgListFragment);
                noticeListFragment.setUserVisibleHint(false);
                if (checkedId == R.id.rb_notices) {
                    fragmentTransaction.show(noticeListFragment);
                } else if (checkedId == R.id.rb_private_msg) {
                    fragmentTransaction.show(privateMsgListFragment);
                }
                fragmentTransaction.commitAllowingStateLoss();
            }
        });
    }

    @Override
    public void onClick(View view) {

    }
}
