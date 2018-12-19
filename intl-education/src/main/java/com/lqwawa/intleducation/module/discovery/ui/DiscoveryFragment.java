package com.lqwawa.intleducation.module.discovery.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseFragment;
import com.lqwawa.intleducation.base.utils.DisplayUtil;
import com.lqwawa.intleducation.base.utils.LogUtil;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.BannerHeaderView;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.NoScrollGridView;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.module.discovery.adapter.ClassifyAdapter;
import com.lqwawa.intleducation.module.discovery.adapter.IndexCourseAdapter;
import com.lqwawa.intleducation.module.discovery.adapter.OrganAdapter;
import com.lqwawa.intleducation.module.discovery.vo.BannerInfoVo;
import com.lqwawa.intleducation.module.discovery.vo.ClassifyVo;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.discovery.vo.DiscoveryItemVo;
import com.lqwawa.intleducation.module.discovery.vo.OrganVo;
import com.lqwawa.intleducation.module.login.ui.LoginActivity;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.intleducation.module.user.ui.MyActivity;
import org.xutils.common.Callback;
import org.xutils.x;
import org.xutils.http.RequestParams;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by XChen on 2016/11/2.
 * email:man0fchina@foxmail.com
 * 发现页面
 */

public class DiscoveryFragment extends MyBaseFragment implements View.OnClickListener {
    private static final String TAG = "DiscoveryFragment";
    private ImageView imageViewUserHead;
    private TextView textViewSearch;
    private PullToRefreshView pullToRefreshView;
    private LinearLayout content_root;
    private List<BannerInfoVo> bannerInfoList;
    private List<ClassifyVo> classifyList;
    private ClassifyAdapter classifyAdapter;
    private List<CourseVo> hotCourseList;
    private IndexCourseAdapter hotCourseAdapter;
    private List<CourseVo> latestCourseList;
    private IndexCourseAdapter latestCourseAdapter;
    private List<OrganVo> organList;
    private OrganAdapter organAdapter;
    private BannerHeaderView bannerHeaderView;
    private NoScrollGridView classifyGridView;
    private NoScrollGridView hotNoScrollGridView;
    private NoScrollGridView latestNoScrollGridView;
    private NoScrollGridView organNoScrollGridView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discovery, container, false);
        imageViewUserHead = (ImageView) view.findViewById(R.id.user_head_iv);
        textViewSearch = (TextView) view.findViewById(R.id.search_tv);
        pullToRefreshView = (PullToRefreshView) view.findViewById(R.id.pull_to_refresh);
        content_root = (LinearLayout) view.findViewById(R.id.content_layout);
        bannerHeaderView = (BannerHeaderView) view.findViewById(R.id.banner_header_view);
        classifyGridView = (NoScrollGridView) view.findViewById(R.id.classify_grid_view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = DiscoveryFragment.this.getActivity();
        initViews();
    }

    private void initViews() {
        textViewSearch.setOnClickListener(this);
        imageViewUserHead.setOnClickListener(this);
        pullToRefreshView.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                getData();
            }
        });
        pullToRefreshView.setLoadMoreEnable(false);
        pullToRefreshView.setLastUpdated(new Date().toLocaleString());

        bannerHeaderView.setOnHeaderViewClickListener(new BannerHeaderView.HeaderViewClickListener() {
            @Override
            public void HeaderViewClick(int position) {

            }
        });

        classifyGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ClassifyVo vo = (ClassifyVo) classifyAdapter.getItem(position);
                if (vo != null) {
                    if (vo.getLevel().equals("1")) {
                        ClassifyIndexActivity.start(activity, vo);
                    } else {
                        startActivity(new Intent(activity, CourseListActivity.class)
                                .putExtra("Level", vo.getLevel())
                                .putExtra("LevelName", vo.getLevelName())
                        );
                    }
                }
            }
        });

        View hotView = activity.getLayoutInflater().inflate(
                R.layout.mod_discovery_item, content_root, false);
        TextView hotTitleText = (TextView) hotView.findViewById(R.id.title_name);
        hotTitleText.setText(getText(R.string.hot_recommended));
        LinearLayout hotTitle = (LinearLayout) hotView.findViewById(R.id.item_title);
        hotTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, CourseListActivity.class)
                                //.putExtra("CourseName", "")
                                .putExtra("Sort", "1")
                                .putExtra("LevelName", getString(R.string.hot_recommended))
                        //.putExtra("Level", "")
                );
            }
        });
        hotNoScrollGridView = (NoScrollGridView) hotView.findViewById(R.id.item_grid_view);
        hotNoScrollGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CourseVo vo = (CourseVo) hotCourseAdapter.getItem(position);
                CourseDetailsActivity.start(activity, vo.getId(), true, UserHelper.getUserId());
            }
        });
        hotNoScrollGridView.setNumColumns(2);
        content_root.addView(hotView);

        View latestView = activity.getLayoutInflater().inflate(
                R.layout.mod_discovery_item, content_root, false);
        TextView latestTitleText = (TextView) latestView.findViewById(R.id.title_name);
        latestTitleText.setText(getText(R.string.latest_update));
        LinearLayout latestTitle = (LinearLayout) latestView.findViewById(R.id.item_title);
        latestTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, CourseListActivity.class)
                                //.putExtra("CourseName", "")
                                .putExtra("Sort", "2")
                                .putExtra("LevelName", getString(R.string.latest_update))
                        //.putExtra("Level", "")
                );
            }
        });
        latestNoScrollGridView = (NoScrollGridView) latestView.findViewById(R.id.item_grid_view);
        latestNoScrollGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CourseVo vo = (CourseVo) latestCourseAdapter.getItem(position);
                CourseDetailsActivity.start(activity, vo.getId(),true, UserHelper.getUserId());
            }
        });
        latestNoScrollGridView.setNumColumns(2);
        content_root.addView(latestView);

        View organView = activity.getLayoutInflater().inflate(
                R.layout.mod_discovery_item, content_root, false);
        TextView organTitleText = (TextView) organView.findViewById(R.id.title_name);
        organTitleText.setText(getText(R.string.organ_in));
        LinearLayout organTitle = (LinearLayout) organView.findViewById(R.id.item_title);
        organTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, OrganListActivity.class));
            }
        });
        organNoScrollGridView = (NoScrollGridView) organView.findViewById(R.id.item_grid_view);
        organNoScrollGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OrganDetailsActivity.start(activity,
                        ((OrganVo) organAdapter.getItem(position)).getId());
            }
        });
        organNoScrollGridView.setNumColumns(4);
        content_root.addView(organView);

        pullToRefreshView.setLastUpdated(new Date().toLocaleString());
        getData();
    }

    private void getData() {
        //更新banner
        updateBannerViwe();
        //添加分类列表
        updateClassifyList();
        //添加热门推荐/最近更新/入驻机构
        updateItems();
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.user_head_iv) {
            if (UserHelper.isLogin()) {
                startActivity(new Intent(activity, MyActivity.class));
            } else {
                LoginActivity.loginForResult(activity);
            }
        }else if(view.getId() == R.id.search_tv) {
            startActivity(new Intent(activity, SearchActivity.class));
        }
    }

    private void updateBannerViwe() {
        bannerInfoList = new ArrayList<BannerInfoVo>();
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetBanners);
        LogUtil.d(TAG, params.getUri());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                ResponseVo<List<BannerInfoVo>> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<List<BannerInfoVo>>>() {
                        });
                if (result.getCode() == 0) {
                    bannerInfoList = result.getData();
                    if (bannerInfoList != null && bannerInfoList.size() > 0) {
                        List<String> imgUrlList = new ArrayList<>();
                        for (int i = 0; i < bannerInfoList.size(); i++) {
                            if (bannerInfoList.get(i).getThumbnail() != null) {
                                imgUrlList.add(bannerInfoList.get(i).getThumbnail());
                            }
                        }
                        bannerHeaderView.setImgUrlData(imgUrlList);
                    }
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "拉取banner信息失败:" + throwable.getMessage());
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private void updateClassifyList() {
        classifyAdapter = new ClassifyAdapter(activity);
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetClassList);
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                ResponseVo<List<ClassifyVo>> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<List<ClassifyVo>>>() {
                        });
                if (result.getCode() == 0) {
                    List<ClassifyVo> classVos = result.getData();
                    if (classVos != null && classVos.size() > 0) {
                        classifyList = new ArrayList<>(classVos);
                        classifyAdapter.setData(classifyList);
                        classifyGridView.setAdapter(classifyAdapter);
                        classifyAdapter.notifyDataSetChanged();
                    } else {
                        classifyAdapter.setData(null);
                    }
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "拉取分类列表失败:" + throwable.getMessage());
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private void updateItems() {
        hotCourseAdapter = new IndexCourseAdapter(activity, false);
        latestCourseAdapter = new IndexCourseAdapter(activity, false);
        organAdapter = new OrganAdapter(activity);

        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetDiscoveryItemList);
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefreshView.onHeaderRefreshComplete();
                DiscoveryItemVo result = JSON.parseObject(s,
                        new TypeReference<DiscoveryItemVo>() {
                        });
                if (result.getCode() == 0) {
                    hotCourseList = result.getRmCourseList();
                    hotCourseAdapter.setData(hotCourseList);
                    hotNoScrollGridView.setAdapter(hotCourseAdapter);
                    hotCourseAdapter.notifyDataSetChanged();
                    latestCourseList = result.getZjCourseList();
                    latestCourseAdapter.setData(latestCourseList);
                    latestNoScrollGridView.setAdapter(latestCourseAdapter);
                    latestCourseAdapter.notifyDataSetChanged();
                    organList = result.getOrganList();
                    organAdapter.setData(organList);
                    organNoScrollGridView.setAdapter(organAdapter);
                    organAdapter.notifyDataSetChanged();
                    content_root.invalidate();
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "拉去热门推荐/最近更新/入驻机构失败:" + throwable.getMessage());
                pullToRefreshView.onHeaderRefreshComplete();
            }

            @Override
            public void onFinished() {
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LoginActivity.REQUEST_CODE_LOGIN
                && resultCode == Activity.RESULT_OK) {

        }
    }
}
