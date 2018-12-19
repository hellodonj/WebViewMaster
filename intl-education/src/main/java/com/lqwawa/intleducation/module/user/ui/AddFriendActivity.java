package com.lqwawa.intleducation.module.user.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.module.user.adapter.SearchUserListAdapter;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.intleducation.module.user.vo.SearchUserVo;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;
import java.util.Date;
import java.util.List;

/**
 * Created by XChen on 2016/12/2.
 * email:man0fchina@foxmail.com
 * 添加朋友
 */

public class AddFriendActivity extends MyBaseActivity implements View.OnClickListener,
        ListView.OnItemClickListener {
    private static String TAG = "AddFriendActivity";

    private TopBar topBar;

    private RelativeLayout loadFailedLayout;
    private Button btnReload;
    private PullToRefreshView pullToRefreshView;
    private ListView listView;

    private EditText editTextSearch;
    private ImageView imageViewClear;
    private TextView textViewGoSearch;
    private String lastSearchKey;

    SearchUserListAdapter searchUserListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.com_activity_search_refresh_list);
        topBar = (TopBar)findViewById(R.id.top_bar);
        loadFailedLayout = (RelativeLayout)findViewById(R.id.load_failed_layout);
        btnReload  = (Button)findViewById(R.id.reload_bt);
        pullToRefreshView  = (PullToRefreshView)findViewById(R.id.pull_to_refresh);
        listView  = (ListView)findViewById(R.id.listView);
        editTextSearch  = (EditText)findViewById(R.id.search_et);
        imageViewClear = (ImageView)findViewById(R.id.search_clear_iv);
        textViewGoSearch = (TextView)findViewById(R.id.commit_search_tv);
        initViews();
    }

    private void initViews() {
        //初始化下拉刷新
        pullToRefreshView.setRefreshEnable(false);
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
        topBar.setBack(true);
        btnReload.setOnClickListener(this);
        topBar.setTitle(getResources().getString(R.string.add_new_friend));

        searchUserListAdapter = new SearchUserListAdapter(this);
        listView.setAdapter(searchUserListAdapter);
        listView.setOnItemClickListener(this);

        imageViewClear.setOnClickListener(this);
        textViewGoSearch.setOnClickListener(this);
        textViewGoSearch.setTextColor(getResources().getColorStateList(R.color.com_green_text));
        textViewGoSearch.setEnabled(false);
        editTextSearch.setImeOptions(EditorInfo.IME_ACTION_NONE);
        editTextSearch.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    imageViewClear.setVisibility(View.VISIBLE);
                    textViewGoSearch.setEnabled(true);
                } else {
                    imageViewClear.setVisibility(View.INVISIBLE);
                    textViewGoSearch.setEnabled(false);
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
                    if (editTextSearch.getText().toString().trim().isEmpty()){
                        return false;
                    }
                    getData();
                    hideKeyboard();
                    return true;
                }
                return false;
            }
        });


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.reload_bt) {
            pullToRefreshView.showRefresh();
            getData();
        }else if(view.getId() == R.id.search_clear_iv) {
            editTextSearch.setText("");
        }else if(view.getId() == R.id.commit_search_tv) {
            if (editTextSearch.getText().toString().isEmpty()) {
                return;
            }
            getData();
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SearchUserVo vo = (SearchUserVo) searchUserListAdapter.getItem(position);
        if (vo != null) {
            ContactDetailsActivity.start(activity, vo.getId() + "", false);
        }
    }

    private int pageIndex = 0;

    private void getData() {
        pageIndex = 0;
        getData(AppConfig.PAGE_SIZE);
    }

    private void getData(int pageSize) {
        pullToRefreshView.setRefreshEnable(true);
        loadFailedLayout.setVisibility(View.GONE);
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("pageIndex", pageIndex);
        requestVo.addParams("pageSize", pageSize);
        String searchKey = editTextSearch.getText().toString().trim();
        if (searchKey.isEmpty()){
            searchKey = lastSearchKey;
        }else{
            lastSearchKey = searchKey;
        }
        requestVo.addParams("name", searchKey);
        LogUtil.d(TAG, requestVo.getParams());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.SearchUser + requestVo.getParams());
        pageIndex = 0;
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefreshView.onHeaderRefreshComplete();
                try {
                    ResponseVo<List<SearchUserVo>> result = JSON.parseObject(s,
                            new TypeReference<ResponseVo<List<SearchUserVo>>>() {
                            });
                    if (result.getCode() == 0) {
                        loadFailedLayout.setVisibility(View.GONE);

                        List<SearchUserVo> voList = result.getData();
                        if (voList != null) {
                            for(int i = 0; i < voList.size(); i ++){
                                SearchUserVo vo = voList.get(i);
                                if (vo.getName().equals(UserHelper.getUserName())){
                                    voList.remove(vo);
                                }
                            }
                        }
                        pullToRefreshView.setLoadMoreEnable(voList != null
                                && voList.size() >= AppConfig.PAGE_SIZE);
                        searchUserListAdapter.setData(voList);
                        searchUserListAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    ToastUtil.showToast(activity, activity.getResources().getString(R.string.search)
                            + activity.getResources().getString(R.string.failed) + e.getMessage());
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
        String searchKey = editTextSearch.getText().toString().trim();
        if (searchKey.isEmpty()){
            searchKey = lastSearchKey;
        }else{
            lastSearchKey = searchKey;
        }
        requestVo.addParams("name", searchKey);
        LogUtil.d(TAG, requestVo.getParams());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.SearchUser + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefreshView.onFooterRefreshComplete();
                ResponseVo<List<SearchUserVo>> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<List<SearchUserVo>>>() {
                        });
                if (result.getCode() == 0) {
                    List<SearchUserVo> voList = result.getData();
                    if (voList != null && voList.size() > 0) {
                        pullToRefreshView.setLoadMoreEnable(voList.size() >= AppConfig.PAGE_SIZE);
                        pageIndex++;
                        searchUserListAdapter.addData(voList);
                        searchUserListAdapter.notifyDataSetChanged();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SendFriendRequestActivity.Rc_sendFriendRequest
                && resultCode == RESULT_OK) {
            getData(searchUserListAdapter.getCount());
        }
    }
}
