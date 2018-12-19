package com.lqwawa.intleducation.module.discovery.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseFragment;
import com.lqwawa.intleducation.base.utils.LogUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.SuperGridView;
import com.lqwawa.intleducation.common.interfaces.OnLoadStatusChangeListener;
import com.lqwawa.intleducation.module.discovery.adapter.OrganItemAdapter;
import com.lqwawa.intleducation.module.discovery.vo.OrganItemVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;
import java.util.List;

/**
 * Created by XChen on 2016/11/10.
 * email:man0fchina@foxmail.com
 * 机构详情页面的三个标签下的页面：课程/老师/证书
 */

public class OrganDetailsItemFragment extends MyBaseFragment implements View.OnClickListener {
    private static final String TAG = "OrganDetailsItemFragment";

    private SuperGridView gridView;
    private Button btnMore;

    private int type = 1;//1课程/2老师/3证书
    private int gridColumnsNum = 2;
    private String organId;
    private String itemName;
    private int pageSize = 8;

    private List<OrganItemVo> organItemList;
    OrganItemAdapter organItemAdapter;
    OnLoadStatusChangeListener onLoadStatusChangeListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orage_details_item, container, false);
        gridView = (SuperGridView)view.findViewById(R.id.super_gridview);
        btnMore = (Button)view.findViewById(R.id.more_bt);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = OrganDetailsItemFragment.this.getActivity();
        type = getArguments().getInt("type", 1);
        organId = getArguments().getString("id");
        LogUtil.d(TAG, type + "");

        switch (type) {
            case 1:
            default:
                itemName = getText(R.string.course).toString();
                gridColumnsNum = 2;
                break;
            case 2:
                itemName = getText(R.string.teacher).toString();
                gridColumnsNum = 3;
                pageSize = 12;
                break;
            case 3:
                itemName = getText(R.string.credential_simple).toString();
                gridColumnsNum = 2;
                break;
        }
        initViews();
    }

    public void setOnLoadStatusChangeListener(OnLoadStatusChangeListener listener) {
        onLoadStatusChangeListener = listener;
    }

    private void initViews() {
        btnMore.setOnClickListener(this);
        organItemAdapter = new OrganItemAdapter(activity, type);

        gridView.setNumColumns(gridColumnsNum);
        gridView.setOnItemClickListener(new SuperGridView.OnItemClickListener() {
            @Override
            public void onItemClick(LinearLayout parent, View view, int position) {
                OrganItemVo vo = (OrganItemVo) organItemAdapter.getItem(position);
                if (type == 1) {//课程
                    CourseDetailsActivity.start(activity, vo.getId(), true, UserHelper.getUserId());
                } else if (type == 2) {//老师
                    TeacherDetailsActivity.start(activity, vo.getId());
                } else if (type == 3) {//证书
                    CredentialDetailsActivity.start(activity, vo.getId());
                }
            }
        });
        btnMore.setText(getText(R.string.more) + itemName);
        btnMore.setVisibility(View.GONE);
        getData();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.more_bt) {
                if (type == 1) {
                    startActivity(new Intent(activity, CourseListActivity.class)
                            .putExtra("OrganId", organId));
                } else if (type == 2) {
                    MoreTeacherActivity.start(activity, organId);
                } else if (type == 3) {
                    MoreCredentialActivity.start(activity, organId);
                }
        }
    }

    public void updateData() {
        getData();
    }

    private int pageIndex = 0;

    private void getData() {
        pageIndex = 0;
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("pageIndex", pageIndex);
        requestVo.addParams("pageSize", pageSize);
        requestVo.addParams("dataType", type);
        requestVo.addParams("organId", organId);
        final RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetOrganItemList + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                if (onLoadStatusChangeListener != null) {
                    onLoadStatusChangeListener.onLoadSuccess();
                }
                ResponseVo<List<OrganItemVo>> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<List<OrganItemVo>>>() {
                        });
                LogUtil.d(TAG, result.getData().toString());
                if (result.getCode() == 0) {
                    organItemList = result.getData();
                    if (organItemList != null && organItemList.size() >= pageSize) {
                        organItemList = organItemList.subList(0, pageSize);
                        btnMore.setVisibility(View.VISIBLE);
                    }else{
                        btnMore.setVisibility(View.GONE);
                    }
                    organItemAdapter.setData(organItemList);
                    gridView.setAdapter(organItemAdapter);
                    organItemAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "拉取入驻机构列表失败:" + throwable.getMessage());
                if (onLoadStatusChangeListener != null) {
                    onLoadStatusChangeListener.onLoadFlailed();
                }
            }

            @Override
            public void onFinished() {
            }
        });
    }
}
