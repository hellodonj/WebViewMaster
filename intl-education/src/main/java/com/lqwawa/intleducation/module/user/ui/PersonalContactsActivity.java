package com.lqwawa.intleducation.module.user.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseActivity;
import com.lqwawa.intleducation.base.utils.LogUtil;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.SideBar;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.module.user.adapter.ContactListAdapter;
import com.lqwawa.intleducation.module.user.adapter.SearchContactListAdapter;
import com.lqwawa.intleducation.module.user.vo.ContactVo;
import com.lqwawa.intleducation.module.user.vo.PersonalContactVo;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by XChen on 2016/12/2.
 * email:man0fchina@foxmail.com
 * 个人通讯录
 */

public class PersonalContactsActivity extends MyBaseActivity implements View.OnClickListener,
        ListView.OnItemClickListener {
    private static String TAG = "PersonalContactsActivity";

    private TopBar topBar;

    private RelativeLayout loadFailedLayout;
    private Button btnReload;
    private PullToRefreshView pullToRefreshView;
    private ListView listView;

    private TextView dialog;
    private SideBar sideBar;

    private ImageView imageViewNewFriend;
    private TextView textViewName;
    private TextView textViewNewFriendApplyCount;
    private LinearLayout layNewFriendRoot;

    private EditText editTextSearch;
    private ImageView imageViewClear;
    private TextView textViewGoSearch;
    private ListView listViewSearch;

    ContactListAdapter contactListAdapter;
    SearchContactListAdapter searchContactListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_contacts);
        topBar = (TopBar) findViewById(R.id.top_bar);
        loadFailedLayout = (RelativeLayout) findViewById(R.id.load_failed_layout);
        btnReload = (Button) findViewById(R.id.reload_bt);
        pullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        listView = (ListView) findViewById(R.id.listView);
        dialog = (TextView) findViewById(R.id.dialog);
        sideBar = (SideBar) findViewById(R.id.sidebar);
        editTextSearch = (EditText) findViewById(R.id.search_et);
        imageViewClear = (ImageView) findViewById(R.id.search_clear_iv);
        textViewGoSearch = (TextView) findViewById(R.id.commit_search_tv);
        listViewSearch = (ListView) findViewById(R.id.search_list);
        initViews();
    }

    private void initViews() {
        //初始化右侧筛选条
        sideBar.setTextView(dialog);
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position = contactListAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    listView.setSelection(position);
                }
            }
        });

        //初始化下拉刷新
        pullToRefreshView.setLoadMoreEnable(false);
        pullToRefreshView.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                getData();
            }
        });
        pullToRefreshView.setOnFooterRefreshListener(new PullToRefreshView.OnFooterRefreshListener() {
            @Override
            public void onFooterRefresh(PullToRefreshView view) {
                getMore();
            }
        });
        pullToRefreshView.setLastUpdated(new Date().toLocaleString());
        //初始化顶部工具条
        topBar.setRightFunctionImage1(R.drawable.ic_add, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, AddFriendActivity.class));
            }
        });
        topBar.setBack(true);
        topBar.setTitle(getResources().getString(R.string.personal_contacts));

        //加载失败时重新加载按钮
        btnReload.setOnClickListener(this);

        //新朋友
        View headView = getLayoutInflater()
                .inflate(R.layout.mod_user_person_contact_list_item, null);

        imageViewNewFriend = (ImageView) headView.findViewById(R.id.user_head_iv);
        textViewNewFriendApplyCount = (TextView) headView.findViewById(R.id.new_msg_count_tv);
        layNewFriendRoot = (LinearLayout) headView.findViewById(R.id.root_layout);
        textViewName = (TextView) headView.findViewById(R.id.name_tv);
        textViewNewFriendApplyCount.setVisibility(View.GONE);
        layNewFriendRoot.setOnClickListener(this);
        textViewName.setText(getResources().getString(R.string.new_friend));
        listView.addHeaderView(headView);

        //初始化数据适配器
        contactListAdapter = new ContactListAdapter(this);
        listView.setAdapter(contactListAdapter);
        listView.setOnItemClickListener(this);

        //初始化搜索相关
        imageViewClear.setOnClickListener(this);
        textViewGoSearch.setOnClickListener(this);
        editTextSearch.setOnClickListener(this);
        editTextSearch.setImeOptions(EditorInfo.IME_ACTION_NONE);
        textViewGoSearch.setTextColor(getResources().getColor(R.color.com_text_gray));
        textViewGoSearch.setText(getResources().getString(R.string.cancel));
        textViewGoSearch.setVisibility(View.GONE);
        searchContactListAdapter = new SearchContactListAdapter(this);
        listViewSearch.setAdapter(searchContactListAdapter);
        listViewSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    ContactVo vo = (ContactVo) searchContactListAdapter.getItem(position);
                    if (vo != null) {
                        ContactDetailsActivity.start(activity, vo.getId() + "", false);
                    }
                } catch (Exception e) {
                    LogUtil.d(TAG, e.getMessage());
                }
            }
        });

        editTextSearch.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    search();
                    imageViewClear.setVisibility(View.VISIBLE);
                    textViewGoSearch.setVisibility(View.VISIBLE);
                } else {
                    imageViewClear.setVisibility(View.INVISIBLE);
                    textViewGoSearch.setVisibility(View.GONE);
                    hideSearchList();
                }
                editTextSearch.setImeOptions(s.length() > 0
                        ? EditorInfo.IME_ACTION_SEARCH
                        : EditorInfo.IME_ACTION_NONE);
                editTextSearch.setMaxLines(1);
                editTextSearch.setInputType(EditorInfo.TYPE_CLASS_TEXT
                        | EditorInfo.TYPE_TEXT_FLAG_AUTO_COMPLETE
                        | EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            }
        });

        editTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (editTextSearch.getText().toString().isEmpty()){
                        return false;
                    }
                    search();
                    hideKeyboard();
                    return true;
                }
                return false;
            }
        });

        //初始化列表
        pullToRefreshView.showRefresh();
        getData();
    }

    //本地搜索名片夹
    private void search() {
        List<ContactVo> list = new ArrayList<ContactVo>();
        String searchKey = editTextSearch.getText().toString().trim();
        for (int i = 0; i < contactListAdapter.getCount(); i++) {
            ContactVo vo = (ContactVo) contactListAdapter.getItem(i);

            if (vo.getName().contains(searchKey)
                    || vo.getAccount().contains(searchKey)) {
                list.add(vo);
            }
        }
        showSearchList();
        searchContactListAdapter.setData(list);
        searchContactListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.reload_bt) {
            pullToRefreshView.showRefresh();
            getData();
        }else if(view.getId() == R.id.root_layout) {
            NewFriendListActivity.startForResult(activity);
        }else if(view.getId() == R.id.search_clear_iv) {
            editTextSearch.setText("");
        }else if(view.getId() == R.id.commit_search_tv) {
            editTextSearch.setText("");
        }else if(view.getId() == R.id.search_et){
            if (textViewGoSearch.getVisibility() == View.GONE){
                textViewGoSearch.setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            ContactVo vo = (ContactVo) contactListAdapter.getItem(position - 1);
            if (vo != null) {
                ContactDetailsActivity.startForResult(activity, vo.getId() + "");
            }
        } catch (Exception e) {
            LogUtil.d(TAG, e.getMessage());
        }
    }

    private int pageIndex = 0;

    private void getData() {
        pageIndex = 0;
        loadFailedLayout.setVisibility(View.GONE);
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("pageIndex", pageIndex);
        requestVo.addParams("pageSize", Integer.MAX_VALUE);
        LogUtil.d(TAG, requestVo.getParams());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetPersonalContactsList + requestVo.getParams());
        pageIndex = 0;
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefreshView.onHeaderRefreshComplete();
                ResponseVo<PersonalContactVo> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<PersonalContactVo>>() {
                        });
                if (result.getCode() == 0) {
                    loadFailedLayout.setVisibility(View.GONE);
                    List<ContactVo> voList = result.getData().getFriendList();
                    contactListAdapter.setData(voList);
                    contactListAdapter.notifyDataSetChanged();
                    if (result.getData().getFriendRequestCount() > 0){
                        textViewNewFriendApplyCount.setVisibility(View.VISIBLE);
                        textViewNewFriendApplyCount.setText(
                                "" + result.getData().getFriendRequestCount());
                    }else{
                        textViewNewFriendApplyCount.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "获取个人通讯录失败:" + throwable.getMessage());
                loadFailedLayout.setVisibility(View.VISIBLE);
                pullToRefreshView.onFooterRefreshComplete();
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private void getMore() {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("pageIndex", pageIndex + 1);
        requestVo.addParams("pageSize", AppConfig.PAGE_SIZE);
        LogUtil.d(TAG, requestVo.getParams());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetPersonalContactsList + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefreshView.onFooterRefreshComplete();
                ResponseVo<PersonalContactVo> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<PersonalContactVo>>() {
                        });
                if (result.getCode() == 0) {
                    List<ContactVo> voList = result.getData().getFriendList();
                    if (voList != null && voList.size() > 0) {
                        pullToRefreshView.setLoadMoreEnable(voList.size() >= AppConfig.PAGE_SIZE);
                        pageIndex++;
                        contactListAdapter.addData(voList);
                        contactListAdapter.notifyDataSetChanged();
                    } else {
                        ToastUtil.showToastBottom(getApplicationContext(), R.string.no_more_data);
                    }
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "获取个人通讯录失败:" + throwable.getMessage());
                pullToRefreshView.onFooterRefreshComplete();
            }

            @Override
            public void onFinished() {
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (listViewSearch.getVisibility() == View.VISIBLE){
            hideSearchList();
            return;
        }
        super.onBackPressed();
    }
    
    private void showSearchList(){
        listViewSearch.setVisibility(View.VISIBLE);
    }
    
    private void hideSearchList(){
        listViewSearch.setVisibility(View.GONE);
        textViewGoSearch.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ContactDetailsActivity.Rs_delete_friend
                && resultCode == Activity.RESULT_OK){
            pullToRefreshView.showRefresh();
            getData();
        }else if (requestCode == NewFriendListActivity.Rs_new_friend_done
                && resultCode == Activity.RESULT_OK){
            pullToRefreshView.showRefresh();
            getData();
        }else {
            getData();
        }
    }
}
