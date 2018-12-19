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
import com.lqwawa.intleducation.module.user.adapter.MyClassListAdapter;
import com.lqwawa.intleducation.module.user.vo.MyClassVo;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 * Created by XChen on 2016/12/2.
 * email:man0fchina@foxmail.com
 * 班级通讯录
 */

public class ClassContactsActivity extends MyBaseActivity implements View.OnClickListener,
        ListView.OnItemClickListener{
    private static String TAG = "ClassContactsActivity";

    private TopBar topBar;

    private RelativeLayout loadFailedLayout;
    private Button btnReload;
    private PullToRefreshView pullToRefreshView;
    private ListView listView;

    private EditText editTextSearch;
    private ImageView imageViewClear;
    private TextView textViewGoSearch;
    private ListView listViewSearch;

    MyClassListAdapter myClassListAdapter;
    MyClassListAdapter searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.com_activity_search_refresh_list);
        topBar = (TopBar)findViewById(R.id.top_bar);
        loadFailedLayout = (RelativeLayout)findViewById(R.id.load_failed_layout);
        btnReload  = (Button)findViewById(R.id.reload_bt);
        pullToRefreshView  = (PullToRefreshView)findViewById(R.id.pull_to_refresh);
        listView  = (ListView)findViewById(R.id.listView);
        editTextSearch = (EditText)findViewById(R.id.search_et);
        imageViewClear  = (ImageView)findViewById(R.id.search_clear_iv);
        textViewGoSearch = (TextView)findViewById(R.id.commit_search_tv);
        listViewSearch  = (ListView)findViewById(R.id.search_list);
        initViews();
    }

    private void initViews() {
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
        topBar.setBack(true);
        btnReload.setOnClickListener(this);
        topBar.setTitle(getResources().getString(R.string.class_contacts));

        View headView = getLayoutInflater()
                .inflate(R.layout.mod_user_person_contact_list_item, null);

        myClassListAdapter = new MyClassListAdapter(this);
        listView.setAdapter(myClassListAdapter);
        listView.setOnItemClickListener(this);


        //初始化搜索相关
        imageViewClear.setOnClickListener(this);
        textViewGoSearch.setOnClickListener(this);
        editTextSearch.setOnClickListener(this);
        editTextSearch.setImeOptions(EditorInfo.IME_ACTION_NONE);
        textViewGoSearch.setTextColor(getResources().getColor(R.color.com_text_gray));
        textViewGoSearch.setText(getResources().getString(R.string.cancel));
        textViewGoSearch.setVisibility(View.GONE);
        searchAdapter = new MyClassListAdapter(this);
        listViewSearch.setAdapter(searchAdapter);
        listViewSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    MyClassVo vo = (MyClassVo) searchAdapter.getItem(position);
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

        pullToRefreshView.showRefresh();
        getData();
    }

    //本地搜索
    private void search() {
        List<MyClassVo> list = new ArrayList<MyClassVo>();
        String searchKey = editTextSearch.getText().toString().trim();
        for (int i = 0; i < myClassListAdapter.getCount(); i++) {
            MyClassVo vo = (MyClassVo) myClassListAdapter.getItem(i);

            if (vo.getName().contains(searchKey)) {
                list.add(vo);
            }
        }
        showSearchList();
        searchAdapter.setData(list);
        searchAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.reload_bt) {
            pullToRefreshView.showRefresh();
            getData();
        }else if(view.getId() == R.id.search_clear_iv) {
            editTextSearch.setText("");
        }else if(view.getId() == R.id.commit_search_tv) {
            editTextSearch.setText("");
        }else if(view.getId() == R.id.search_et) {
            if (textViewGoSearch.getVisibility() == View.GONE) {
                textViewGoSearch.setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MyClassVo vo = (MyClassVo) myClassListAdapter.getItem(position);
        if (vo != null) {
            ClassMembersActivity.start(activity, vo.getId() + "", vo.getName(), vo.getGroupUuid(), false);
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
                new RequestParams(AppConfig.ServerUrl.GetClassContactsList + requestVo.getParams());
        pageIndex = 0;
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefreshView.onHeaderRefreshComplete();
                ResponseVo<List<MyClassVo>> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<List<MyClassVo>>>() {
                        });
                if (result.getCode() == 0) {
                    loadFailedLayout.setVisibility(View.GONE);
                    List<MyClassVo> voList = result.getData();
                    myClassListAdapter.setData(voList);
                    myClassListAdapter.notifyDataSetChanged();
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
                new RequestParams(AppConfig.ServerUrl.GetClassContactsList + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefreshView.onFooterRefreshComplete();
                ResponseVo<List<MyClassVo>> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<List<MyClassVo>>>() {
                        });
                if (result.getCode() == 0) {
                    List<MyClassVo> voList = result.getData();
                    if (voList != null && voList.size() > 0) {
                        pullToRefreshView.setLoadMoreEnable(voList.size() >= AppConfig.PAGE_SIZE);
                        pageIndex++;
                        myClassListAdapter.addData(voList);
                        myClassListAdapter.notifyDataSetChanged();
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
        if (requestCode == ClassMembersActivity.Rc_ExitGroup && resultCode == Activity.RESULT_OK){
            getData();
        }
    }

}
