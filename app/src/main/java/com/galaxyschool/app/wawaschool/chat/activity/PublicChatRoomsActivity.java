/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.galaxyschool.app.wawaschool.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.hyphenate.EMChatRoomChangeListener;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCursorResult;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;


public class PublicChatRoomsActivity extends BaseActivity {
	private ProgressBar pb;
	private TextView title;
	private ListView listView;
	private ChatRoomAdapter adapter;
	
	private List<EMChatRoom> chatRoomList;
	private boolean isLoading;
	private boolean isFirstLoading = true;
	private boolean hasMoreData = true;
	private String cursor;
	private final int pagesize = 20;
    private LinearLayout footLoadingLayout;
    private ProgressBar footLoadingPB;
    private TextView footLoadingText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_public_groups);

		// 搜索框
		pb = (ProgressBar) findViewById(R.id.progressBar);
		listView = (ListView) findViewById(R.id.list);
		title = (TextView)findViewById(R.id.tv_title);
		title.setText(getResources().getString(R.string.chat_room));
		chatRoomList = new ArrayList<EMChatRoom>();
		
		View footView = getLayoutInflater().inflate(R.layout.listview_footer_view, null);
        footLoadingLayout = (LinearLayout) footView.findViewById(R.id.loading_layout);
        footLoadingPB = (ProgressBar)footView.findViewById(R.id.loading_bar);
        footLoadingText = (TextView) footView.findViewById(R.id.loading_text);
        listView.addFooterView(footView, null, false);
        footLoadingLayout.setVisibility(View.GONE);
        
        //获取及显示数据
        loadAndShowData();

        EMClient.getInstance().chatroomManager().addChatRoomChangeListener(new EMChatRoomChangeListener(){
            @Override
            public void onChatRoomDestroyed(String roomId, String roomName) {
                chatRoomList.clear();
                if(adapter != null){
                    runOnUiThread(new Runnable(){

                        @Override
                        public void run() {
                            if(adapter != null){
                                adapter.notifyDataSetChanged();
                                loadAndShowData();
                            }
                        }
                        
                    });
                }
            }

            @Override
            public void onMemberJoined(String roomId, String participant) {
            }

            @Override
            public void onMemberExited(String roomId, String roomName,
                    String participant) {
                
            }

            @Override
            public void onRemovedFromChatRoom(String s, String s1, String s2) {

            }

            @Override
            public void onMuteListAdded(String s, List<String> list, long l) {

            }

            @Override
            public void onMuteListRemoved(String s, List<String> list) {

            }

            @Override
            public void onAdminAdded(String s, String s1) {

            }

            @Override
            public void onAdminRemoved(String s, String s1) {

            }

            @Override
            public void onOwnerChanged(String s, String s1, String s2) {

            }

            @Override
            public void onAnnouncementChanged(String s, String s1) {

            }

        });
        
        //设置item点击事件
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                
                final EMChatRoom room = adapter.getItem(position);
                startActivity(new Intent(PublicChatRoomsActivity.this, ChatActivity.class).putExtra("chatType", 3).
                		putExtra("groupId", room.getId()));
                
            }
        });
        listView.setOnScrollListener(new OnScrollListener() {
            
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState == OnScrollListener.SCROLL_STATE_IDLE){
                    if(cursor != null){
                        int lasPos = view.getLastVisiblePosition();
                        if(hasMoreData && !isLoading && lasPos == listView.getCount()-1){
                            loadAndShowData();
                        }
                    }
                }
            }
            
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                
            }
        });
        
	}
	
	private void loadAndShowData(){
		new Thread(new Runnable() {

            public void run() {
                try {
                    isLoading = true;
                    final EMCursorResult<EMChatRoom> result = EMClient.getInstance().chatroomManager()
                            .fetchPublicChatRoomsFromServer(pagesize, cursor);
                    //获取group list
                    final List<EMChatRoom> chatRooms = result.getData();
                    runOnUiThread(new Runnable() {

                        public void run() {
                            chatRoomList.addAll(chatRooms);
                            if(chatRooms.size() != 0){
                                //获取cursor
                                cursor = result.getCursor();
//                                if(chatRooms.size() == pagesize)
//                                    footLoadingLayout.setVisibility(View.VISIBLE);
                            }
                            if(isFirstLoading){
                                pb.setVisibility(View.INVISIBLE);
                                isFirstLoading = false;
                                //设置adapter
                                adapter = new ChatRoomAdapter(PublicChatRoomsActivity.this, 1, chatRoomList);
                                listView.setAdapter(adapter);
                            }else{
                                if(chatRooms.size() < pagesize){
                                    hasMoreData = false;
                                    footLoadingLayout.setVisibility(View.VISIBLE);
                                    footLoadingPB.setVisibility(View.GONE);
                                    footLoadingText.setText("No more data");
                                }
                                adapter.notifyDataSetChanged();
                            }
                            isLoading = false;
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            isLoading = false;
                            pb.setVisibility(View.INVISIBLE);
                            footLoadingLayout.setVisibility(View.GONE);
                            TipMsgHelper.ShowMsg(PublicChatRoomsActivity.this, "加载数据失败，请检查网络或稍后重试");
                        }
                    });
                }
            }
        }).start();
	}
	/**
	 * adapter
	 *
	 */
	private class ChatRoomAdapter extends ArrayAdapter<EMChatRoom> {

		private LayoutInflater inflater;

		public ChatRoomAdapter(Context context, int res, List<EMChatRoom> rooms) {
			super(context, res, rooms);
			this.inflater = LayoutInflater.from(context);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.row_group, null);
			}

			((TextView) convertView.findViewById(R.id.name)).setText(getItem(position).getName());

			return convertView;
		}
	}
	
	public void back(View view){
		finish();
	}
}
