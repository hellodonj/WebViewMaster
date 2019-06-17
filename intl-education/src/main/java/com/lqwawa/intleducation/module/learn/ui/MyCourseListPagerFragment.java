package com.lqwawa.intleducation.module.learn.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.CourseEmptyView;
import com.lqwawa.intleducation.base.ui.MyBaseFragment;
import com.lqwawa.intleducation.base.utils.LogUtil;
import com.lqwawa.intleducation.base.utils.StringUtils;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.common.ui.CustomDialog;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.intleducation.factory.event.EventWrapper;
import com.lqwawa.intleducation.module.discovery.ui.CourseDetailsActivity;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailParams;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailType;
import com.lqwawa.intleducation.module.discovery.ui.mycourse.tab.TabCourseEmptyView;
import com.lqwawa.intleducation.module.discovery.vo.CourseSortType;
import com.lqwawa.intleducation.module.learn.adapter.MyCourseListAdapter;
import com.lqwawa.intleducation.module.learn.vo.MyCourseVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.lqbaselib.pojo.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by XChen on 2017/7/18.
 * email:man0fchina@foxmail.com
 */

public class MyCourseListPagerFragment extends MyBaseFragment implements View.OnClickListener {

    // 去在线学习
    public static final String ACTION_GO_COURSE_SHOP = "ACTION_GO_COURSE_SHOP";

    private static final String TAG = "MyCourseListPagerFragment";
    public static final String KEY_IS_TEACHER = "KEY_IS_TEACHER";
    public static final String KEY_HIDE_SEARCH = "KEY_HIDE_SEARCH";
    public static final String KEY_CONFIG_ENTITY = "KEY_CONFIG_ENTITY";
    public static final String KEY_EXTRA_CONFIG_LEVEL = "KEY_EXTRA_CONFIG_LEVEL";
    public static final String KEY_EXTRA_CONFIG_ONEID = "KEY_EXTRA_CONFIG_ONEID";
    public static final String KEY_EXTRA_CONFIG_TWOID = "KEY_EXTRA_CONFIG_TWOID";

    //下拉刷新
    private PullToRefreshView pullToRefreshView;
    private ScrollView mScrollLayout;
    // 空布局
    private CourseEmptyView mEmptyLayout;
    // 我的自主学习，才展示的Layout
    private TabCourseEmptyView mTabEmptyLayout;
    //数据列表
    private ListView listView;
    //加载失败图片
    private RelativeLayout loadFailedLayout;
    //重新加载
    private Button btnReload;

    private TextView textViewLiveTimetable;//课程表按钮

    private MyCourseListAdapter courseListAdapter;
    private String curMemberId = "";
    private String curSchoolId = "";

    private LinearLayout mSearchLayout;
    private EditText editTextSearch;
    private ImageView imageViewClear;
    private TextView textViewFilter;
    private String searchKeyword;
    private boolean isTeacher;
    private boolean mHideSearch;

    private LQCourseConfigEntity mConfigEntity;
    private String mLevel;
    private int mParamOneId;
    private int mParamTwoId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_course_page_list, container, false);
        pullToRefreshView = (PullToRefreshView) view.findViewById(R.id.pull_to_refresh);
        mScrollLayout = (ScrollView) view.findViewById(R.id.scroll_layout);
        mEmptyLayout = (CourseEmptyView) view.findViewById(R.id.empty_layout);
        mTabEmptyLayout = (TabCourseEmptyView) view.findViewById(R.id.tab_empty_layout);
        listView = (ListView) view.findViewById(R.id.listView);
        loadFailedLayout = (RelativeLayout) view.findViewById(R.id.load_failed_layout);
        btnReload = (Button) view.findViewById(R.id.reload_bt);
        textViewLiveTimetable = (TextView) view.findViewById(R.id.live_timetable_tv);
        textViewLiveTimetable.setOnClickListener(this);
        mSearchLayout = (LinearLayout) view.findViewById(R.id.search_layout);
        editTextSearch = (EditText) view.findViewById(R.id.search_et);
        textViewFilter = (TextView) view.findViewById(R.id.filter_tv);
        textViewFilter.setText(getString(R.string.search));
        textViewFilter.setOnClickListener(this);
        mTabEmptyLayout.setSubmitListener(this);
        // textViewFilter.setVisibility(View.GONE);
        imageViewClear = (ImageView) view.findViewById(R.id.search_clear_iv);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        initViews();
    }

    private void initViews() {
        curMemberId = getArguments().getString("MemberId");
        curSchoolId = getArguments().getString("SchoolId");
        mHideSearch = getArguments().getBoolean(KEY_HIDE_SEARCH);
        if (getArguments().containsKey(KEY_CONFIG_ENTITY)) {
            mConfigEntity = (LQCourseConfigEntity) getArguments().getSerializable(KEY_CONFIG_ENTITY);
        }
        mLevel = getArguments().getString(KEY_EXTRA_CONFIG_LEVEL);
        mParamOneId = getArguments().getInt(KEY_EXTRA_CONFIG_ONEID);
        mParamTwoId = getArguments().getInt(KEY_EXTRA_CONFIG_TWOID);

        if (mHideSearch) {
            // 隐藏搜索
            mSearchLayout.setVisibility(View.GONE);
        }

        isTeacher = getArguments().getBoolean(KEY_IS_TEACHER, false);
        courseListAdapter = new MyCourseListAdapter(activity, isTeacher);
        courseListAdapter.setRoleInfo(curMemberId.equals(UserHelper.getUserId()), curMemberId);
        listView.setAdapter(courseListAdapter);
        btnReload.setOnClickListener(this);

        editTextSearch.setOnClickListener(this);
        textViewFilter.setOnClickListener(this);
        editTextSearch.setImeOptions(EditorInfo.IME_ACTION_NONE);
        imageViewClear.setOnClickListener(this);
        editTextSearch.setHint(R.string.my_course_search_hint);

        editTextSearch.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    imageViewClear.setVisibility(View.VISIBLE);
                } else {
                    imageViewClear.setVisibility(View.INVISIBLE);
                }
                // search();
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
                    if (editTextSearch.getText().toString().isEmpty()) {
                        return false;
                    }
                    search();
                    hideKeyboard();
                    return true;
                }
                return false;
            }
        });

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
        pullToRefreshView.setLoadMoreEnable(false);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyCourseVo vo = (MyCourseVo) courseListAdapter.getItem(position);
                if (vo != null) {
                    if (isTeacher) {
                        // 如果是老师,直接进入已加入详情
                        CourseDetailParams params = new CourseDetailParams(CourseDetailType.COURSE_DETAIL_GIVE_INSTRUCTION_ENTER);
                        params.setLibraryType(vo.getLibraryType());
                        params.setIsVideoCourse(vo.getType() == 2);
                        MyCourseDetailsActivity.start(activity, vo.getCourseId(),
                                TextUtils.equals(UserHelper.getUserId(), curMemberId)
                                , curMemberId, curSchoolId, params);
                    } else {
                        // 学生或家长，需要更新状态
                        CourseDetailsActivity.start(activity, vo.getCourseId(),
                                TextUtils.equals(UserHelper.getUserId(), curMemberId)
                                , curMemberId, true, false);
                    }
                }
            }
        });

        if (!isTeacher) {
            // 如果是老师身份，不允许长按退出课程
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    if (StringUtils.isValidString(curMemberId)
                            && curMemberId.equals(UserHelper.getUserId())) {
                        final MyCourseVo vo = (MyCourseVo) courseListAdapter.getItem(position);
                        CustomDialog.Builder builder = new CustomDialog.Builder(activity);
                        builder.setMessage(activity.getResources().getString(R.string.exit_course_tip)
                                + "?");
                        builder.setTitle(activity.getResources().getString(R.string.tip));
                        builder.setPositiveButton(activity.getResources().getString(R.string.confirm),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        exitCourse(vo);
                                    }
                                });

                        builder.setNegativeButton(activity.getResources().getString(R.string.cancel),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                        builder.create().show();
                        return true;
                    } else {
                        return false;
                    }
                }
            });
        }
        if (StringUtils.isValidString(curMemberId)) {
            pullToRefreshView.showRefresh();
            getData();
        }
    }


    private void search() {
        getData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!getUserVisibleHint()) {
            // 隐藏
            if (EmptyUtil.isNotEmpty(editTextSearch))
                editTextSearch.getText().clear();
        }
    }

    private void exitCourse(MyCourseVo vo) {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("courseId", vo.getCourseId());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.courseDelete + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                ResponseVo<String> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<String>>() {
                        });
                if (result.getCode() == 0) {

                    // 发送刷新标签的通知
                    EventBus.getDefault().post(new EventWrapper(null, EventConstant.TRIGGER_EXIT_COURSE));

                    ToastUtil.showToast(activity, getResources().getString(R.string.exit_course)
                            + getResources().getString(R.string.success));
                    pullToRefreshView.showRefresh();
                    getData();
                } else {
                    ToastUtil.showToast(activity, getResources().getString(R.string.exit_course)
                            + getResources().getString(R.string.failed)
                            + ":" + result.getMessage());
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {

                ToastUtil.showToast(activity, getResources().getString(R.string.net_error_tip));
            }

            @Override
            public void onFinished() {
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.reload_bt) {
            pullToRefreshView.showRefresh();
            getData();
        } else if (view.getId() == R.id.filter_tv) {
            hideKeyboard();
            search();
        } else if (view.getId() == R.id.live_timetable_tv) {
            //跳转到课程表界面
            // @date   :2018/4/26 0026 下午 6:09
            // @func   :V5.5发现前人留下的坑,具坑，铁坑

            /*LiveTimetableActivity.start(activity,
                    TextUtils.equals(UserHelper.getUserId(), curMemberId) ?
                            LiveTimetableActivity.LiveSourceType.Type_my_course :
                            LiveTimetableActivity.LiveSourceType.Type_my_child_course,
                    curMemberId, "", "", "");*/

            LiveTimetableActivity.start(activity,
                    TextUtils.equals(UserHelper.getUserId(), curMemberId) ?
                            LiveTimetableActivity.LiveSourceType.Type_my_course :
                            LiveTimetableActivity.LiveSourceType.Type_my_child_course, ""
                    , curMemberId, "", curSchoolId);
        } else if (view.getId() == R.id.search_clear_iv) {
            editTextSearch.setText("");
            search();
        } else if (view.getId() == R.id.btn_submit) {
            // 去100%习课程
            if (!isTeacher) {
                // 我的习课程
                getActivity().finish();
                Intent broadIntent = new Intent();
                broadIntent.setAction(ACTION_GO_COURSE_SHOP);
                getContext().sendBroadcast(broadIntent);
            }
        }
    }

    private int pageIndex = 0;

    public void getData() {
        if (EmptyUtil.isEmpty(editTextSearch)) return;
        searchKeyword = editTextSearch.getText().toString();

        if (loadFailedLayout == null) {
            return;
        }
        pageIndex = 0;
        loadFailedLayout.setVisibility(View.GONE);
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("pageIndex", pageIndex);
        requestVo.addParams("pageSize", AppConfig.PAGE_SIZE);
        requestVo.addParams("token", curMemberId);
        if (searchKeyword != null) {
            try {
                requestVo.addParams("name", URLEncoder.encode(searchKeyword, "UTF-8"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        LogUtil.d(TAG, requestVo.getParams());
        String url = null;
        if (isTeacher) {
            url = AppConfig.ServerUrl.GetMyEstablishCourseList + requestVo.getParams();
        } else {
            requestVo.addParams("level", mLevel);
            requestVo.addParams("paramOneId", mParamOneId);
            requestVo.addParams("paramTwoId", mParamTwoId);
            url = AppConfig.ServerUrl.GetMyCourseList + requestVo.getParams();
        }
        RequestParams params = new RequestParams(url);
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefreshView.onFooterRefreshComplete();
                pullToRefreshView.onHeaderRefreshComplete();
                ResponseVo<List<MyCourseVo>> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<List<MyCourseVo>>>() {
                        });
                if (result.getCode() == 0) {
                    loadFailedLayout.setVisibility(View.GONE);
                    List<MyCourseVo> courseList = result.getData();
                    pullToRefreshView.setLoadMoreEnable(courseList.size() >= AppConfig.PAGE_SIZE);
                    courseListAdapter.setData(courseList);
                    courseListAdapter.notifyDataSetChanged();

                    if (EmptyUtil.isEmpty(courseList)) {
                        // 数据为空
                        pullToRefreshView.setVisibility(View.VISIBLE);
                        mScrollLayout.setVisibility(View.VISIBLE);
                        listView.setVisibility(View.GONE);
                        if (!isTeacher) {
                            // 我参与的与我孩子的自主学习显示
                            mTabEmptyLayout.setVisibility(View.VISIBLE);
                        } else {
                            // 我的授课显示
                            mEmptyLayout.setVisibility(View.VISIBLE);
                        }
                    } else {
                        // 数据不为空
                        pullToRefreshView.setVisibility(View.VISIBLE);
                        listView.setVisibility(View.VISIBLE);
                        mScrollLayout.setVisibility(View.GONE);
                        mEmptyLayout.setVisibility(View.GONE);
                        mTabEmptyLayout.setVisibility(View.GONE);
                    }
                    // 刷新可滑动的布局
                    pullToRefreshView.refreshContent();
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                loadFailedLayout.setVisibility(View.VISIBLE);
                pullToRefreshView.onFooterRefreshComplete();
                pullToRefreshView.onHeaderRefreshComplete();
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
        requestVo.addParams("token", curMemberId);

        if (searchKeyword != null) {
            try {
                requestVo.addParams("name", URLEncoder.encode(searchKeyword, "UTF-8"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        LogUtil.d(TAG, requestVo.getParams());
        String url = null;
        if (isTeacher) {
            url = AppConfig.ServerUrl.GetMyEstablishCourseList + requestVo.getParams();
        } else {
            requestVo.addParams("level", mLevel);
            requestVo.addParams("paramOneId", mParamOneId);
            requestVo.addParams("paramTwoId", mParamTwoId);
            url = AppConfig.ServerUrl.GetMyCourseList + requestVo.getParams();
        }
        RequestParams params = new RequestParams(url);
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefreshView.onFooterRefreshComplete();
                pullToRefreshView.onHeaderRefreshComplete();
                ResponseVo<List<MyCourseVo>> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<List<MyCourseVo>>>() {
                        });
                if (result.getCode() == 0) {
                    List<MyCourseVo> listMore = result.getData();
                    if (listMore != null && listMore.size() > 0) {
                        pageIndex++;
                        courseListAdapter.addData(listMore);
                        courseListAdapter.notifyDataSetChanged();
                        pullToRefreshView.setLoadMoreEnable(listMore.size() >= AppConfig.PAGE_SIZE);
                    } else {
                        ToastUtil.showToastBottom(activity, R.string.no_more_data);
                        pullToRefreshView.setLoadMoreEnable(false);
                    }
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                pullToRefreshView.onFooterRefreshComplete();
                pullToRefreshView.onHeaderRefreshComplete();
            }

            @Override
            public void onFinished() {
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(@NonNull EventWrapper event) {
        if (EventWrapper.isMatch(event, EventConstant.APPOINT_COURSE_IN_CLASS_EVENT)) {
            // 刷新UI
            getData();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(@NonNull MessageEvent event) {
        if (TextUtils.equals(EventConstant.TRIGGER_UPDATE_COURSE, event.getUpdateAction())) {
            // 发生课程信息更新，刷新UI
            getData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
