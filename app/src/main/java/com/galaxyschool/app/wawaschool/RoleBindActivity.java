package com.galaxyschool.app.wawaschool;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.pojo.RoleInfo;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.views.MyListView;
import com.galaxyschool.app.wawaschool.views.ToolbarTopView;
import com.lqwawa.lqbaselib.net.NetErrorResult;
import com.lqwawa.lqbaselib.net.PostByMapParamsModelRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoleBindActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private MyApplication myApp;
    private ToolbarTopView toolbarTopView;

    private MyListView roleInfoListView;

    private RoleInfoAdapter roleInfoAdapter;

    private List<RoleInfo> roleInfoList = new ArrayList<RoleInfo>();
    private String memberId = "";
    private UserInfo userInfo;

    private String[] roleStateStrs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_bind);
        initData();
        initViews();
    }

    private void initData() {
        roleStateStrs = getResources().getStringArray(R.array.role_check_state);
        myApp = (MyApplication) getApplication();
        memberId = myApp.getMemberId();
    }

    private void initViews() {
        toolbarTopView = (ToolbarTopView) findViewById(R.id.toolbartopview);
        toolbarTopView.getBackView().setVisibility(View.VISIBLE);
        toolbarTopView.getTitleView().setText(R.string.role);
        toolbarTopView.getCommitView().setVisibility(View.INVISIBLE);
        toolbarTopView.getBackView().setOnClickListener(this);

        roleInfoListView = (MyListView) findViewById(R.id.userinfo_role_info_listview);
        roleInfoListView.setOnItemClickListener(this);

        initRoleInfoLayout();

        loadData();

    }

    private void initRoleInfoLayout() {
        roleInfoList.clear();
        roleInfoAdapter = new RoleInfoAdapter(RoleBindActivity.this);
        roleInfoListView.setAdapter(roleInfoAdapter);
    }

    private void loadData() {
        loadRoleInfoList(memberId);
    }

    private void loadUserInfo(final String memberId) {
        String url = ServerUrl.LOAD_USERINFO_URL;
        if (!TextUtils.isEmpty(url)) {
            Map<String, String> mParams = new HashMap<String, String>();
            mParams.put("UserId", memberId);
            PostByMapParamsModelRequest request = new PostByMapParamsModelRequest(
                url, mParams, new Listener<String>() {
                @Override
                public void onSuccess(String json) {
//                    loadingDialog.dismiss();
                    try {
                        Log.i("", "Login:onSuccess " + json);
                        userInfo = JSON.parseObject(json, UserInfo.class);
                        if (userInfo != null) {
                            userInfo.setMemberId(memberId);
//                            SharedPreferencesUtils.setParam(
//                                RoleBindActivity.this, SharedPreferencesUtils.PrefsItems
//                                    .USER_STATE, userInfo.getState());
                            UserInfo info = myApp.getUserInfo();
                            if (info != null) {
                                info.setState(userInfo.getState());
                            }
                            myApp.setUserInfo(info);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(NetroidError error) {
                    super.onError(error);
//                    loadingDialog.dismiss();
                    String es = error.getMessage();
                    try {
                        NetErrorResult result = JSON.parseObject(es, NetErrorResult.class);
                        if (result != null) {
                            if (result.isHasError()) {
                                TipMsgHelper.ShowMsg(RoleBindActivity.this, result.getErrorMessage());
                            }
                        }
                    } catch (Exception e) {
                        TipMsgHelper.ShowLMsg(RoleBindActivity.this, getString(R.string.network_error));
                    }
                }
            });
            request.addHeader("Accept-Encoding", "*");
            request.start(RoleBindActivity.this);

        }
    }

    private void loadRoleInfoList(final String memberId) {
        String url = ServerUrl.LOAD_ROLEINFOLIST_URL;
        if (!TextUtils.isEmpty(url)) {
            Map<String, String> mParams = new HashMap<String, String>();
            mParams.put("MemberId", memberId);
            PostByMapParamsModelRequest request = new PostByMapParamsModelRequest(
                url, mParams, new Listener<String>() {
                @Override
                public void onSuccess(String json) {
                    try {
                        Log.i("", "Login:onSuccess " + json);
                        JSONObject jsonObject = new JSONObject(json);
                        if (jsonObject != null) {
                            String memId = jsonObject.getString("MemberId");
                            JSONArray jsonArray = jsonObject.optJSONArray("ApplyList");
                            if (!TextUtils.isEmpty(memId) && memberId.equals(memId)) {
                                if (jsonArray != null) {
                                    roleInfoList.clear();
                                    List<RoleInfo> roleInfos = JSON.parseArray(jsonArray.toString(), RoleInfo.class);
                                    if (roleInfos != null && roleInfos.size() > 0) {
                                        for (RoleInfo roleInfo : roleInfos) {
                                            roleInfoList.add(roleInfo);
                                        }
                                    } else {
                                        roleInfoList.add(new RoleInfo());
                                    }
                                    updateRoleInfoList();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(NetroidError error) {
                    super.onError(error);
//                    loadingDialog.dismiss();
                    String es = error.getMessage();
                    try {
                        NetErrorResult result = JSON.parseObject(es, NetErrorResult.class);
                        if (result != null) {
                            if (result.isHasError()) {
                                TipMsgHelper.ShowMsg(RoleBindActivity.this, result.getErrorMessage());
                            }
                        }
                    } catch (Exception e) {
                        TipMsgHelper.ShowLMsg(RoleBindActivity.this, getString(R.string.network_error));
                    }
                }
            });
            request.addHeader("Accept-Encoding", "*");
            request.start(RoleBindActivity.this);
        }
    }


    private void updateRoleInfoList() {
        roleInfoAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.toolbar_top_back_btn:
                finish();
                break;
            case R.id.userinfo_add_role_layout:
                intent.setClass(RoleBindActivity.this, RoleInfoActivity.class);
                intent.putExtra("roleId", "");
                startActivityForResult(intent, ActivityUtils.REQUEST_CODE_ADD_ROLE_INFO);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (roleInfoList != null && roleInfoList.size() > 0) {
            if (position < roleInfoList.size()) {
                RoleInfo roleInfo = roleInfoList.get(position);
                if (roleInfo != null) {
                    if(TextUtils.isEmpty(roleInfo.getId())) {
                        return;
                    }
                    Intent intent = new Intent();
                    intent.setClass(RoleBindActivity.this, RoleInfoActivity.class);
                    intent.putExtra("roleId", roleInfo.getId());
                    intent.putExtra("roleType", roleInfo.getRoleType());
                    startActivity(intent);
                }
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ActivityUtils.REQUEST_CODE_ADD_ROLE_INFO:
                if (data != null) {
                    RoleInfo roleInfo = (RoleInfo) data.getSerializableExtra("roleInfo");
                    if (roleInfo != null) {
                        RoleInfo roleInfo0 = roleInfoList.get(0);
                        if (roleInfo0 != null && TextUtils.isEmpty(roleInfo0.getId())) {
                            roleInfoList.remove(0);
                        }
                        roleInfoList.add(0, roleInfo);
                        updateRoleInfoList();
                        UserInfo userInfo = myApp.getUserInfo();
                        if (userInfo != null) {
                            if (userInfo.getState() == 0 && !TextUtils.isEmpty(memberId)) {
                                loadUserInfo(memberId);
                            }
                        }
                    }
                }
                break;
                default:
                    break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class RoleInfoAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public RoleInfoAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return roleInfoList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.userinfo_role_info_item, null);
            }

            TextView roleInfoTxt = (TextView) convertView.findViewById(R.id.role_info_txt);
            TextView schoolInfoTxt = (TextView) convertView.findViewById(R.id.school_info_txt);
            TextView roleStateTxt = (TextView) convertView.findViewById(R.id.role_state_txt);
            TextView schoolStateTxt = (TextView) convertView.findViewById(R.id.school_state_txt);
            if (roleInfoList != null && roleInfoList.size() > 0) {
                RoleInfo roleInfo = roleInfoList.get(position);
                if (roleInfo != null) {
                    roleInfoTxt.setText(roleInfo.getRole());
                    int state = roleInfo.getState();
                    if (state >= 0 && state < roleStateStrs.length) {
                        roleStateTxt.setText(roleStateStrs[state]);
                        schoolStateTxt.setText(roleStateStrs[state]);
                    }
                    if (!TextUtils.isEmpty(roleInfo.getRole())) {
                        roleStateTxt.setVisibility(View.VISIBLE);
                        schoolStateTxt.setVisibility(View.VISIBLE);
                    } else {
                        roleStateTxt.setVisibility(View.INVISIBLE);
                        schoolStateTxt.setVisibility(View.INVISIBLE);
                    }
                    schoolInfoTxt.setText(roleInfo.getApplyName());
                }
            }
            return convertView;
        }
    }

}
