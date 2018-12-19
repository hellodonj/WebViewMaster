package com.galaxyschool.app.wawaschool;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.fragment.ContactsSearchFriendFragment;
import com.galaxyschool.app.wawaschool.fragment.PersonalContactsListFragment;
import com.galaxyschool.app.wawaschool.views.PopupMenu;

import java.util.ArrayList;
import java.util.List;

public class PersonalContactsActivity extends BaseFragmentActivity
        implements PersonalContactsListFragment.Constants {
   private Bundle args;
   private static final int MENU_ID_SCAN = 0;
   private static final int MENU_ID_ADD_FRIEND = 1;
   public static final String IS_NEED_HIDDEN_SUBSCRIBE_STATE = "is_need_hidden_subscribe_state" ;
   private Fragment fragment;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_contacts_with_header);

      args = getIntent().getExtras();
      initViews();

      fragment = new PersonalContactsListFragment();
      fragment.setArguments(args);
      FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      ft.replace(R.id.contacts_body, fragment, PersonalContactsListFragment.TAG);
      ft.commit();
   }

   private void initViews() {
      TextView textView = (TextView) findViewById(R.id.contacts_header_title);
      if (textView != null) {
         textView.setText(R.string.personal_contacts);
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
//         if (args != null){
//            if (args.getBoolean(PersonalContactsListFragment.Constants.EXTRA_CHAT_WITH_FRIEND)){
//               //消息界面进入不显示“新增好友”
//               textView.setVisibility(View.INVISIBLE);
//            }else {
//               textView.setVisibility(View.INVISIBLE);
//            }
//         }else {
//            textView.setVisibility(View.INVISIBLE);
//         }
//
//         textView.setText(getString(R.string.add_good_friend));
//         textView.setTextColor(getResources().getColor(R.color.text_green));
//         textView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//               Intent intent = new Intent(PersonalContactsActivity.this,
//                       ContactsSearchFriendActivity.class);
//               startActivity(intent);
//            }
//         });
      }
      ImageView imageView = (ImageView) findViewById(R.id.contacts_header_right_ico);
      if (imageView != null) {
         if (args != null){
            if (args.getBoolean(PersonalContactsListFragment.Constants.EXTRA_CHAT_WITH_FRIEND)){
               //消息界面进入不显示“加号”
               imageView.setVisibility(View.INVISIBLE);
            }else {
               imageView.setVisibility(View.VISIBLE);
            }
         }else {
            imageView.setVisibility(View.VISIBLE);
         }
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

      //添加好友
      data = new PopupMenu.PopupMenuData(0, R.string.add_friend, MENU_ID_ADD_FRIEND);
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
                       enterCaptureActivity();
                    } else if (data.getId() == MENU_ID_ADD_FRIEND ) {
                       //搜索个人页面
                       enterSearchFriend();
                    }
                 }
              };
      PopupMenu popupMenu = new PopupMenu(this, itemClickListener, items);
      popupMenu.showAsDropDown(view, view.getWidth(), 0);
   }

   private void enterCaptureActivity() {

      Intent intent = new Intent(this, CaptureActivity.class);
      startActivity(intent);
   }

   private void enterSearchFriend() {
      Intent intent = new Intent(this, ContactsSearchFriendActivity.class);
      //添加好友
      startActivityForResult(intent, ContactsSearchFriendFragment.REQUEST_CODE_SEARCH_FRIEND);
   }

   private void enterSubscribeSearch() {
      Intent intent = new Intent(this, SubscribeSearchActivity.class);
      intent.putExtra(SubscribeSearchActivity.EXTRA_SUBSCRIPE_SEARCH_TYPE,
              SubscribeSearchActivity.SUBSCRIPE_SEARCH_USER);
      //是否需要隐藏搜索关注页面的“关注”状态按钮
      intent.putExtra(IS_NEED_HIDDEN_SUBSCRIBE_STATE,false);
      startActivity(intent);
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      if (fragment != null){
         fragment.onActivityResult(requestCode, resultCode, data);
      }
   }
}