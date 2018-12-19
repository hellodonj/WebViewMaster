package com.galaxyschool.app.wawaschool;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.fragment.GroupExpandListFragment;
import com.galaxyschool.app.wawaschool.fragment.PersonalSpaceFragment;
import com.galaxyschool.app.wawaschool.fragment.SubscribePersonListFragment;
import com.galaxyschool.app.wawaschool.fragment.SubscribeSearchFragment;
import com.galaxyschool.app.wawaschool.fragment.TopicDiscussionFragment;
import com.galaxyschool.app.wawaschool.views.PopupMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016.06.18.
 */
public class SubscribePersonListActivity extends BaseFragmentActivity{
    private static final int MENU_ID_SCAN = 0 ;
    private static final int MENU_ID_ADD_FRIEND = 1 ;
    private Fragment fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_with_header);

        initViews();

        Bundle args = getIntent().getExtras();
        Fragment fragment = new SubscribePersonListFragment();
        fragment.setArguments(args);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.contacts_body, fragment, SubscribePersonListFragment.TAG);
        ft.commit();
    }

    private void initViews() {
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            textView.setText(R.string.has_subscribed_person);
        }
        View view = findViewById(R.id.contacts_header_left_btn);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        view.setVisibility(View.VISIBLE);
//      findViewById(R.id.contacts_header_right_btn).setVisibility(View.INVISIBLE);
        textView= (TextView) findViewById(R.id.contacts_header_right_btn);
        if (textView != null) {
            textView.setVisibility(View.GONE);
            textView.setText(getString(R.string.join_class));
            textView.setTextColor(getResources().getColor(R.color.text_green));
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SubscribePersonListActivity.this,
                            ContactsSearchClassActivity.class);
                    startActivity(intent);
                }
            });
        }
        ImageView imageView = (ImageView) findViewById(R.id.contacts_header_right_ico);
        if (imageView != null){
            imageView.setImageResource(R.drawable.selector_icon_plus);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMoreMenu(v);
                }
            });
        }
    }

    private void showMoreMenu(View view) {
        List<PopupMenu.PopupMenuData> items = new ArrayList();
        PopupMenu.PopupMenuData data = null;
        //扫一扫
        data = new PopupMenu.PopupMenuData(0, R.string.scan_me, MENU_ID_SCAN);
        items.add(data);

        //添加个人
        data = new PopupMenu.PopupMenuData(0, R.string.add_sb, MENU_ID_ADD_FRIEND);
        items.add(data);

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
                            ActivityUtils.gotoQrcodeScanning(SubscribePersonListActivity.this);
                        } else if (data.getId() == MENU_ID_ADD_FRIEND ) {
                            //搜索个人页面
                            enterSubscribeSearch();
                        }
                    }
                };
        PopupMenu popupMenu = new PopupMenu(this, itemClickListener, items);
        popupMenu.showAsDropDown(view, view.getWidth(), 0);
    }

    private void enterSubscribeSearch() {
        Intent intent = new Intent(this, SubscribeSearchActivity.class);
        intent.putExtra(SubscribeSearchActivity.EXTRA_SUBSCRIPE_SEARCH_TYPE,
                SubscribeSearchActivity.SUBSCRIPE_SEARCH_USER);
        //搜索个人页面
        startActivityForResult(intent, SubscribeSearchFragment.REQUEST_CODE_SUBSCRIBER_SEARCH);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
