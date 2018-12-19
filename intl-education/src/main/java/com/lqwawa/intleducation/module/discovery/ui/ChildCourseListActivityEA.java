package com.lqwawa.intleducation.module.discovery.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseActivity;
import com.lqwawa.intleducation.base.utils.LogUtil;
import com.lqwawa.intleducation.base.utils.StringUtils;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.db.DbHelper;
import com.lqwawa.intleducation.common.ui.LinePopupWindow;
import com.lqwawa.intleducation.common.ui.SortLinePopupWindow;
import com.lqwawa.intleducation.module.discovery.adapter.CourseListAdapter;
import com.lqwawa.intleducation.module.discovery.vo.ClassifyVo;
import com.lqwawa.intleducation.module.discovery.vo.CourseSortType;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.discovery.vo.FilterHistoryVo;
import com.lqwawa.intleducation.module.discovery.vo.FilterVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by XChen on 2016/11/13.
 * email:man0fchina@foxmail.com
 * 课程列表
 */
public class ChildCourseListActivityEA extends MyBaseActivity implements View.OnClickListener {
    private static final String TAG = ChildCourseListActivityEA.class.getSimpleName();
    private static final int CLS_ALL_VALUE = -100;
    private static final int sortItems = 4;

    //头部
    private TopBar topBar;
    //下拉刷新
    private PullToRefreshView pullToRefresh;
    //数据列表
    private ListView listView;
    //加载失败图片
    private RelativeLayout loadFailedLayout;
    //重新加载
    private Button reloadBt;

    private CheckBox sortCb1;
    private CheckBox sortCb2;
    private CheckBox sortCb3;
    private CheckBox sortCb4;

    private String paramCourseName = "";
    private String paramSort = "1";
    private String topLevel = "";
    private int param1LabelId = CLS_ALL_VALUE;
    private int param2LabelId = CLS_ALL_VALUE;
    private int param3LabelId = CLS_ALL_VALUE;
    private String paramStatus = "";
    private String paramOrganId;
    private String levelName;
    private String setSubjectLabelId;
    private ClassifyVo classifyVo;
    private List<ClassifyVo> topClassifyList;
    private List<ClassifyVo> classifyList1;
    private List<ClassifyVo> classifyList2;
    private List<ClassifyVo> classifyList3;
    private List<ClassifyVo> validClassifyList1;
    private List<ClassifyVo> validClassifyList2;
    private List<ClassifyVo> validClassifyList3;
    private boolean firstSelectClassify = false;

    ClassifyVo classifyAll = new ClassifyVo();
    ClassifyVo classifyAllGrade = new ClassifyVo();
    ClassifyVo classifyAllTheme = new ClassifyVo();
    ClassifyVo classifyAllSubject = new ClassifyVo();
    /**
     * sort view end
     *****/
    private List<CourseVo> courseList;
    private CourseListAdapter courseListAdapter;

    private List<CourseSortType> statusSortTypeList;

    FilterVo segmentSelect = null;
    FilterVo ageSelect = null;

    public static void start(Activity activity, ClassifyVo vo,
                             String level, String levelName, String sort, String setSubjectLabelId) {
        activity.startActivity(new Intent(activity, ChildCourseListActivityEA.class)
                .putExtra("classify", vo)
                .putExtra("LevelName", levelName)
                .putExtra("Level", level)
                .putExtra("Sort", sort)
                .putExtra("setSubjectLabelId", setSubjectLabelId));
    }

    public static void startFromSearch(Activity activity, ClassifyVo vo, String keyString,
                                       String levelName, String level, String setSubjectLabelId) {
        activity.startActivity(new Intent(activity, ChildCourseListActivityEA.class)
                .putExtra("classify", vo)
                .putExtra("CourseName", keyString)
                .putExtra("LevelName", levelName)
                .putExtra("Level", level)
                .putExtra("setSubjectLabelId", setSubjectLabelId));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_course_list);

        topBar = (TopBar) findViewById(R.id.top_bar);
        pullToRefresh = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        listView = (ListView) findViewById(R.id.listView);
        loadFailedLayout = (RelativeLayout) findViewById(R.id.load_failed_layout);
        reloadBt = (Button) findViewById(R.id.reload_bt);
        findViewById(R.id.reload_bt).setOnClickListener(this);
        sortCb1 = (CheckBox) findViewById(R.id.sort_cb1);
        sortCb2 = (CheckBox) findViewById(R.id.sort_cb2);
        sortCb3 = (CheckBox) findViewById(R.id.sort_cb3);
        sortCb4 = (CheckBox) findViewById(R.id.sort_cb4);

        classifyAll.setLevel("");
        classifyAll.setLabelId(CLS_ALL_VALUE);
        classifyAll.setConfigValue(getResources().getString(R.string.all));
        classifyAllGrade.setLevel("");
        classifyAllGrade.setLabelId(CLS_ALL_VALUE);
        classifyAllGrade.setConfigValue(getResources().getString(R.string.all)
                + getResources().getString(R.string.type));
        classifyAllTheme.setLevel("");
        classifyAllTheme.setLabelId(CLS_ALL_VALUE);
        classifyAllTheme.setConfigValue(getResources().getString(R.string.all)
                + getResources().getString(R.string.course_subject));
        classifyAllSubject.setLevel("");
        classifyAllSubject.setLabelId(CLS_ALL_VALUE);
        classifyAllSubject.setConfigValue(getResources().getString(R.string.all)
                + getResources().getString(R.string.theme));

        paramCourseName = getIntent().getStringExtra("CourseName");
        paramSort = getIntent().getStringExtra("Sort");
        if (paramSort == null) {
            paramSort = "1";
        }
        classifyVo = (ClassifyVo) getIntent().getSerializableExtra("classify");
        topLevel = getIntent().getStringExtra("Level");
        setSubjectLabelId = getIntent().getStringExtra("setSubjectLabelId");
        if(setSubjectLabelId != null && !topLevel.equals(
                        ClassifyIndexActivity.LQGISubClassifyValues.ExaminationForGoingAbroad)){
            findViewById(R.id.subject_lay).setVisibility(View.GONE);
            findViewById(R.id.subject_split).setVisibility(View.GONE);
        }

        if (topLevel != null && topLevel.equals(classifyVo.getLevel())) {
            findViewById(R.id.child_sort_root).setVisibility(View.GONE);
        }

        paramOrganId = getIntent().getStringExtra("OrganId");
        levelName = getIntent().getStringExtra("LevelName");
        topBar.setTitle(getString(R.string.all));
        if (StringUtils.isValidString(levelName)) {
            topBar.setTitle(levelName);
        }

        param1LabelId = CLS_ALL_VALUE;
        param2LabelId = CLS_ALL_VALUE;
        param3LabelId = CLS_ALL_VALUE;

        FilterHistoryVo filterHistoryVo = readFilterHistory();
        if (filterHistoryVo != null){
            param1LabelId = filterHistoryVo.getLabel1();
            param2LabelId = filterHistoryVo.getLabel2();
            param3LabelId = filterHistoryVo.getLabel3();
            paramStatus = filterHistoryVo.getStatusFilter();
        }

        initViews();
        initList();
        initData();

        if(filterHistoryVo == null && paramSort == null){
            saveFilterHistory();
            firstSelectClassify = true;
            SelectChildClassifyActivityEA.startForResult(activity, classifyVo,
                    topLevel.equals(classifyVo.getLevel()) ? "" : topLevel,
                    param1LabelId, param2LabelId, param3LabelId, paramStatus,
                    !StringUtils.isValidString(paramCourseName), setSubjectLabelId);
        }
    }

    private void initViews() {
        for (int i = 0; i < sortItems; i++) {
            getSortCb(i).setOnClickListener(this);
        }
        topBar.setBack(true);
        if (paramCourseName == null && !topLevel.equals(classifyVo.getLevel())) {
            topBar.setRightFunctionImage2(R.drawable.search, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(activity, SearchActivity.class)
                            .putExtra("classify", classifyVo)
                            .putExtra("Level", topLevel));
                }
            });
            topBar.setRightFunctionImage1(R.drawable.ic_menu, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SelectChildClassifyActivityEA.startForResult(activity, classifyVo,
                            topLevel.equals(classifyVo.getLevel()) ? "" : topLevel,
                            param1LabelId, param2LabelId, param3LabelId, paramStatus,
                            !StringUtils.isValidString(paramCourseName), setSubjectLabelId);
                }
            });
        }
        reloadBt.setOnClickListener(this);

        pullToRefresh.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                getData();
            }
        });
        pullToRefresh.setOnFooterRefreshListener(new PullToRefreshView.OnFooterRefreshListener() {
            @Override
            public void onFooterRefresh(PullToRefreshView view) {
                getMore();
            }
        });
        pullToRefresh.setLastUpdated(new Date().toLocaleString());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CourseVo vo = (CourseVo) courseListAdapter.getItem(position);
                CourseDetailsActivity.start(activity, vo.getId(),
                        ClassifyIndexActivity.SubjectLabelIds.Eglish
                                .equals(getIntent().getStringExtra("setSubjectLabelId")),
                        UserHelper.getUserId());
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.reload_bt) {
            pullToRefresh.showRefresh();
            getData();
        }
        if (view.getId() == R.id.sort_cb1) {
            sortCb1Click(view);
        } else if (view.getId() == R.id.sort_cb2) {
            sortCb2Click(view);
        } else if (view.getId() == R.id.sort_cb3) {
            sortCb3Click(view);
        } else if (view.getId() == R.id.sort_cb4) {
            sortCb4Click(view);
        }
    }

    private void initList() {
        topClassifyList = new ArrayList<>();
        topClassifyList.add(classifyAll);
        topClassifyList.addAll(classifyVo.getChildList());

        List<ClassifyVo> pListTemp = new ArrayList<>();
        if (!topLevel.equals(classifyVo.getLevel())) {//不是全部
            pListTemp.addAll(getSelectClassify(topClassifyList, topLevel)
                    .getChildList());
        } else {//全部
            for (int i = 0; i < classifyVo.getChildList().size(); i++) {
                pListTemp.addAll(classifyVo.getChildList().get(i).getChildList());
            }
        }
        classifyList1 = new ArrayList<>();
        classifyList1.add(new ClassifyVo(classifyAllGrade));
        classifyList2 = new ArrayList<>();
        classifyList2.add(new ClassifyVo(classifyAllTheme));

        for (int i = 0; i < pListTemp.size(); i++) {
            classifyList1.add(pListTemp.get(i));
        }
        for(int i = 0; i < classifyList1.size(); i++){
            if(classifyList1.get(i).getChildList() !=null) {
                classifyList2.addAll(classifyList1.get(i).getChildList());
            }
        }
        validClassifyList1 = new ArrayList<>();
        validClassifyList2 = new ArrayList<>();
        validClassifyList3 = new ArrayList<>();
        updateValidList1();
        updateValidList2();


        classifyList3 = new ArrayList<>();
        classifyList3.add(classifyAllSubject);
        classifyList3.get(0).setConfigValue(getResources().getString(R.string.all)
                + getResources().getString(R.string.theme));
        for(int i = 0; i < validClassifyList2.size(); i++){
            if(validClassifyList2.get(i).getChildList() !=null) {
                classifyList3.addAll(validClassifyList2.get(i).getChildList());
            }
        }
        /*List<ClassifyVo> classifyVoParam2List =
                getSelectClassifys(validClassifyList2, param2LabelId);
        if (param2LabelId != CLS_ALL_VALUE) {//不是全部
            for (ClassifyVo classifyVoParam2 : classifyVoParam2List) {
                if (classifyVoParam2.getChildList() != null) {
                    classifyList3.addAll(classifyVoParam2.getChildList());
                }
            }
        } else {//全部
            for (int i = 0; i < classifyList2.size(); i++) {
                if (classifyList2.get(i).getChildList() != null) {
                    classifyList3.addAll(classifyList2.get(i).getChildList());
                }
            }
        }*/
        updateValidList3();

        ClassifyVo classifyVoParam1 = getSelectClassify(classifyList1, param1LabelId);
        if (classifyVoParam1.getLevel().equals("")){
            classifyVoParam1 = classifyAllGrade;
        }
        changeParam1Select(classifyVoParam1);

        for(ClassifyVo classifyVo : validClassifyList3) {
            if (param3LabelId == classifyVo.getLabelId()){
                sortCb3.setText(classifyVo.getConfigValue());
                break;
            }
        }
        statusSortTypeList = new ArrayList<>();
        statusSortTypeList.add(new CourseSortType("-1", getResources().getString(R.string.all)
                + getResources().getString(R.string.course_status), true));
        sortCb4.setText(getResources().getString(R.string.all)
                + getResources().getString(R.string.course_status));
        statusSortTypeList.add(new CourseSortType("0", getString(R.string.course_status_0), false));
        statusSortTypeList.add(new CourseSortType("1", getString(R.string.course_status_1), false));
        statusSortTypeList.add(new CourseSortType("2", getString(R.string.course_status_2), false));
        for(CourseSortType courseSortType : statusSortTypeList) {
            if (paramStatus.equals(courseSortType.getId())){
                courseSortType.setIsSelect(true);
                sortCb4.setText(courseSortType.getName());
            }else{
                courseSortType.setIsSelect(false);
            }
        }
    }

    @Override
    protected void onDestroy() {
        saveFilterHistory();
        super.onDestroy();
    }

    private void sortCb1Click(View view) {
        if (classifyVo == null || classifyList1 == null) {
            return;
        }
        doNotCheckWithout(1);
        SortLinePopupWindow linePopupWindow =
                new SortLinePopupWindow(ChildCourseListActivityEA.this, validClassifyList1, "" + param1LabelId,
                        new SortLinePopupWindow.PopupWindowListener() {
                            @Override
                            public void onItemClickListener(int position, Object object) {
                                ClassifyVo vo = (ClassifyVo) object;
                                doNotCheckAll();
                                changeParam1Select(vo);
                                pullToRefresh.showRefresh();
                                getData();
                            }

                            @Override
                            public void onDismissListener() {
                                doNotCheckAll();
                            }
                        }, false);
        linePopupWindow.showPopupWindow(view, true);
    }

    private void changeParam1Select(ClassifyVo vo) {
        doNotCheckAll();
        param1LabelId = vo.getLabelId();
        sortCb1.setText(vo.getConfigValue());

        classifyList2 = new ArrayList<>();
        classifyList2.add(classifyAllTheme);
        boolean needLoadAll = true;
        if(param1LabelId != CLS_ALL_VALUE) {
            for (int i = 0; i < classifyList1.size(); i++) {
                if(classifyList1.get(i).getLabelId() == param1LabelId
                        && classifyList1.get(i).getChildList() != null) {
                    classifyList2.addAll(classifyList1.get(i).getChildList());
                    needLoadAll = false;
                    break;
                }
            }
        }
        if(needLoadAll) {
            for (int i = 0; i < classifyList1.size(); i++) {
                if (classifyList1.get(i).getChildList() != null) {
                    classifyList2.addAll(classifyList1.get(i).getChildList());
                }
            }
        }
        validClassifyList2 = new ArrayList<>();
        updateValidList2();
        checkSortParam2();

        classifyList3 = new ArrayList<>();
        classifyList3.add(classifyAllSubject);
        needLoadAll = true;
        if (param2LabelId != CLS_ALL_VALUE) {//不是全部
            for (int i = 0; i < validClassifyList2.size(); i++) {
                if (validClassifyList2.get(i).getChildList() != null
                        && classifyList2.get(i).getLabelId() == param2LabelId ) {
                    classifyList3.addAll(classifyList2.get(i).getChildList());
                    needLoadAll = false;
                    break;
                }
            }
        }
        if(needLoadAll){//全部
            for (int i = 0; i < classifyList2.size(); i++) {
                if (classifyList2.get(i).getChildList() != null) {
                    classifyList3.addAll(classifyList2.get(i).getChildList());
                }
            }
        }
        validClassifyList3 = new ArrayList<>();
        updateValidList3();
        checkSortParam3();
    }

    private void checkSortParam2() {
        for (int i = 0; i < validClassifyList2.size(); i++) {
            if (validClassifyList2.get(i).getLabelId() == param2LabelId) {
                return;
            }
        }
        param2LabelId = CLS_ALL_VALUE;
        getSortCb(2).setText(classifyAllTheme.getConfigValue());
    }

    private void sortCb2Click(View view) {
        if (classifyVo == null || validClassifyList2 == null) {
            return;
        }
        doNotCheckWithout(2);
        SortLinePopupWindow linePopupWindow =
                new SortLinePopupWindow(ChildCourseListActivityEA.this, validClassifyList2, "" + param2LabelId,
                        new SortLinePopupWindow.PopupWindowListener() {
                            @Override
                            public void onItemClickListener(int position, Object object) {
                                ClassifyVo vo = (ClassifyVo) object;
                                changeParam2Select(vo);
                                pullToRefresh.showRefresh();
                                getData();
                            }

                            @Override
                            public void onDismissListener() {
                                doNotCheckAll();
                            }
                        }, false);
        linePopupWindow.showPopupWindow(view, true);
    }

    private void changeParam2Select(ClassifyVo vo) {
        doNotCheckAll();
        param2LabelId = vo.getLabelId();
        getSortCb(2).setText(vo.getConfigValue());
        classifyList3 = new ArrayList<>();
        classifyList3.add(classifyAllSubject);
        boolean needLoadAll = true;
        if (param2LabelId != CLS_ALL_VALUE) {//不是全部
            for (int i = 0; i < validClassifyList2.size(); i++) {
                if (validClassifyList2.get(i).getChildList() != null
                        && classifyList2.get(i).getLabelId() == param2LabelId ) {
                    classifyList3.addAll(classifyList2.get(i).getChildList());
                    needLoadAll = false;
                    break;
                }
            }
        }
        if(needLoadAll){//全部
            for (int i = 0; i < classifyList2.size(); i++) {
                if (classifyList2.get(i).getChildList() != null) {
                    classifyList3.addAll(classifyList2.get(i).getChildList());
                }
            }
        }
        validClassifyList3 = new ArrayList<>();
        updateValidList3();
        checkSortParam3();
    }

    public void checkSortParam3() {
        for (int i = 0; i < validClassifyList3.size(); i++) {
            if (validClassifyList3.get(i).getLabelId() == param3LabelId) {
                return;
            }
        }
        param3LabelId = CLS_ALL_VALUE;
        getSortCb(3).setText(classifyAllSubject.getConfigValue());
    }

    private void sortCb3Click(View view) {
        if (classifyVo == null || validClassifyList3 == null) {
            return;
        }
        doNotCheckWithout(3);
        SortLinePopupWindow linePopupWindow =
                new SortLinePopupWindow(ChildCourseListActivityEA.this, validClassifyList3, "" + param3LabelId,
                        new SortLinePopupWindow.PopupWindowListener() {
                            @Override
                            public void onItemClickListener(int position, Object object) {
                                ClassifyVo vo = (ClassifyVo) object;
                                changeParam3Select(vo);
                                pullToRefresh.showRefresh();
                                getData();
                            }

                            @Override
                            public void onDismissListener() {
                                doNotCheckAll();
                            }
                        }, false);
        linePopupWindow.showPopupWindow(view, true);
    }

    private void changeParam3Select(ClassifyVo vo) {
        doNotCheckAll();
        param3LabelId = vo.getLabelId();
        getSortCb(3).setText(vo.getConfigValue());
    }


    private void sortCb4Click(View view) {
        doNotCheckWithout(4);
        List<String> sortList = new ArrayList<String>();

        for (int i = 0; i < statusSortTypeList.size(); i++) {
            String name = statusSortTypeList.get(i).getName();
            sortList.add(name);
        }
        LinePopupWindow linePopupWindow =
                new LinePopupWindow(activity, sortList, sortCb4.getText().toString(),
                        new LinePopupWindow.PopupWindowListener() {
                            @Override
                            public void onItemClickListener(Object object) {
                                String name = (String) object;
                                sortCb4.setText(name);
                                for (int i = 0; i < statusSortTypeList.size(); i++) {
                                    if (name.equals(statusSortTypeList.get(i).getName())) {
                                        paramStatus = statusSortTypeList.get(i).getId();
                                        statusSortTypeList.get(i).setIsSelect(true);
                                    } else {
                                        statusSortTypeList.get(i).setIsSelect(false);
                                    }
                                }

                                pullToRefresh.showRefresh();
                                getData();
                            }

                            @Override
                            public void onDismissListener() {
                                doNotCheckAll();
                            }
                        }, false);
        linePopupWindow.showPopupWindow(view, true);
    }

    private void doNotCheckAll() {
        sortCb1.setChecked(false);
        sortCb2.setChecked(false);
        sortCb3.setChecked(false);
        sortCb4.setChecked(false);
    }

    private void doNotCheckWithout(int index) {
        for (int i = 1; i <= sortItems; i++) {
            if (i != index) {
                getSortCb(i).setChecked(false);
            }
        }
    }

    private CheckBox getSortCb(int index) {
        if (index == 1) {
            return (CheckBox) findViewById(R.id.sort_cb1);
        } else if (index == 2) {
            return (CheckBox) findViewById(R.id.sort_cb2);
        } else if (index == 3) {
            return (CheckBox) findViewById(R.id.sort_cb3);
        } else {
            return (CheckBox) findViewById(R.id.sort_cb4);
        }
    }

    private void initData() {
        courseList = new ArrayList<>();
        courseListAdapter = new CourseListAdapter(this);
        getData();
    }

    private int pageIndex = 0;

    private void getData() {
        pageIndex = 0;
        loadFailedLayout.setVisibility(View.GONE);
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("pageIndex", pageIndex);
        requestVo.addParams("pageSize", AppConfig.PAGE_SIZE);
        if (paramCourseName != null && !paramCourseName.equals("")) {
            try {
                requestVo.addParams("courseName", URLEncoder.encode(paramCourseName.trim(), "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        if (paramSort != null && !paramSort.equals("")) {
            requestVo.addParams("sort", paramSort);
        }
        if (!topLevel.equals(classifyVo.getLevel())) {
            requestVo.addParams("level", topLevel);
        } else if (classifyVo != null) {
            requestVo.addParams("level", classifyVo.getLevel());
        }
        if (param1LabelId > 0) {
            requestVo.addParams("paramOneId", param1LabelId);
        }
        if(setSubjectLabelId != null&& !topLevel.equals(
                ClassifyIndexActivity.LQGISubClassifyValues.ExaminationForGoingAbroad)){
            requestVo.addParams("paramTwoId", setSubjectLabelId);
        }else
        if (param2LabelId > 0) {
            requestVo.addParams("paramTwoId", param2LabelId);
        }
        if (param3LabelId > 0) {
            requestVo.addParams("paramThreeId", param3LabelId);
        }
        if (paramStatus != null && !paramStatus.equals("")) {
            requestVo.addParams("progressStatus", paramStatus);
        }
        if (paramOrganId != null && !paramOrganId.equals("")) {
            requestVo.addParams("organId", paramOrganId);
        }
        if (segmentSelect != null) {
            requestVo.addParams("schoolSectionId", segmentSelect.getId());
        }
        if (ageSelect != null) {
            requestVo.addParams("generationId", ageSelect.getId());
        }

        if(!AppConfig.BaseConfig.needShowPay()){//只显示免费课程
            requestVo.addParams("payType", 0);
        }
        LogUtil.d(TAG, requestVo.getParams());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetCourseList + requestVo.getParams());
        pageIndex = 0;
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefresh.onHeaderRefreshComplete();
                ResponseVo<List<CourseVo>> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<List<CourseVo>>>() {
                        });
                if (result.getCode() == 0) {
                    loadFailedLayout.setVisibility(View.GONE);
                    courseList = result.getData();
                    pullToRefresh.setLoadMoreEnable(
                            courseList != null && courseList.size() >= AppConfig.PAGE_SIZE);
                    courseListAdapter.setData(courseList);
                    listView.setAdapter(courseListAdapter);
                    courseListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "拉取课程列表失败:" + throwable.getMessage());
                loadFailedLayout.setVisibility(View.VISIBLE);
                pullToRefresh.onFooterRefreshComplete();
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
        if (paramCourseName != null && !paramCourseName.equals("")) {
            try {
                requestVo.addParams("courseName", URLEncoder.encode(paramCourseName.trim(), "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        if (paramSort != null && !paramSort.equals("")) {
            requestVo.addParams("sort", paramSort);
        }
        if (!topLevel.equals(classifyVo.getLevel())) {
            requestVo.addParams("level", topLevel);
        } else if (classifyVo != null) {
            requestVo.addParams("level", classifyVo.getLevel());
        }
        if (param1LabelId > 0) {
            requestVo.addParams("paramOneId", param1LabelId);
        }
        if(setSubjectLabelId != null&& !topLevel.equals(
                ClassifyIndexActivity.LQGISubClassifyValues.ExaminationForGoingAbroad)){
            requestVo.addParams("paramTwoId", setSubjectLabelId);
        }else
        if (param2LabelId > 0) {
            requestVo.addParams("paramTwoId", param2LabelId);
        }
        if (param3LabelId > 0) {
            requestVo.addParams("paramThreeId", param3LabelId);
        }
        if (paramStatus != null && !paramStatus.equals("")) {
            requestVo.addParams("progressStatus", paramStatus);
        }
        if (paramOrganId != null && !paramOrganId.equals("")) {
            requestVo.addParams("organId", paramOrganId);
        }
        if (segmentSelect != null) {
            requestVo.addParams("schoolSectionId", segmentSelect.getId());
        }
        if (ageSelect != null) {
            requestVo.addParams("generationId", ageSelect.getId());
        }
        if(!AppConfig.BaseConfig.needShowPay()){//只显示免费课程
            requestVo.addParams("payType", 0);
        }

        LogUtil.d(TAG, requestVo.getParams());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetCourseList + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefresh.onFooterRefreshComplete();
                ResponseVo<List<CourseVo>> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<List<CourseVo>>>() {
                        });
                if (result.getCode() == 0) {
                    List<CourseVo> listMore = result.getData();
                    if (listMore != null && listMore.size() > 0) {
                        pullToRefresh.setLoadMoreEnable(courseList.size() >= AppConfig.PAGE_SIZE);
                        courseList.addAll(listMore);
                        pageIndex++;
                        courseListAdapter.addData(listMore);
                        courseListAdapter.notifyDataSetChanged();
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
                LogUtil.d(TAG, "拉取入驻机构列表失败:" + throwable.getMessage());
                pullToRefresh.onFooterRefreshComplete();
            }

            @Override
            public void onFinished() {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SelectChildClassifyActivity.Rc_SelectChildClasify) {
            if(resultCode == Activity.RESULT_OK ) {
                if (data != null) {
                    this.param1LabelId = data.getIntExtra("param1LabelId", -1);
                    this.sortCb1.setText(data.getStringExtra("param1LabelName")
                            .equals(classifyAll.getConfigValue()) ? classifyAllGrade.getConfigValue()
                            : data.getStringExtra("param1LabelName"));
                    this.param2LabelId = data.getIntExtra("param2LabelId", -1);
                    this.sortCb2.setText(data.getStringExtra("param2LabelName")
                            .equals(classifyAll.getConfigValue()) ? classifyAllTheme.getConfigValue()
                            : data.getStringExtra("param2LabelName"));
                    this.param3LabelId = data.getIntExtra("param3LabelId", -1);
                    this.sortCb3.setText(data.getStringExtra("param3LabelName")
                            .equals(classifyAll.getConfigValue()) ? classifyAllSubject.getConfigValue()
                            : data.getStringExtra("param3LabelName"));
                    this.paramStatus = data.getStringExtra("paramStatus")
                            .equals(classifyAll.getConfigValue()) ? statusSortTypeList.get(0).getName()
                            : data.getStringExtra("paramStatus");
                    if (this.paramStatus.equals("")) {
                        this.paramStatus = "-1";
                    }
                    for(int i = 0; i < validClassifyList1.size(); i++){
                        if(validClassifyList1.get(i).getLabelId() == param1LabelId){
                            changeParam1Select(validClassifyList1.get(i));
                            break;
                        }
                    }
                    for (int i = 0; i < statusSortTypeList.size(); i++) {
                        if (this.paramStatus.equals(statusSortTypeList.get(i).getId())) {
                            statusSortTypeList.get(i).setIsSelect(true);
                            sortCb4.setText(statusSortTypeList.get(i).getName());
                        } else {
                            statusSortTypeList.get(i).setIsSelect(false);
                        }
                    }
                    if (this.classifyVo != null) {
                        if (topLevel.isEmpty()) {
                            topBar.setTitle(classifyVo.getLevelName());
                        } else if (classifyVo.getChildList() != null) {
                            for (int i = 0; i < classifyVo.getChildList().size(); i++) {
                                if (classifyVo.getChildList().get(i).getLevel().equals(this.topLevel)) {
                                    topBar.setTitle(classifyVo.getChildList().get(i).getLevelName());
                                }
                            }
                        }
                    }
                    getData();
                }
            }else{
                if(firstSelectClassify){
                    firstSelectClassify = false;
                    finish();
                }
            }
        }
    }

    private ClassifyVo getSelectClassify(List<ClassifyVo> list, int labelId) {
        for (ClassifyVo vo : list) {
            if (labelId == vo.getLabelId()) {
                return vo;
            }
        }
        return classifyAll;
    }

    private List<ClassifyVo> getSelectClassifys(List<ClassifyVo> list, int labelId) {
        List<ClassifyVo> tempList = new ArrayList<>();
        for (ClassifyVo vo : list) {
            if (labelId == vo.getLabelId()) {
                tempList.add(vo);
            }
        }
        if (tempList.size() == 0) {
            tempList.add(classifyAllTheme);
        }
        return tempList;
    }

    private ClassifyVo getSelectClassify(List<ClassifyVo> list, String level) {
        for (ClassifyVo vo : list) {
            if (level.equals(vo.getLevel())) {
                return vo;
            }
        }
        return classifyAll;
    }

    private void updateValidList1() {
        validClassifyList1 = new ArrayList<>();
        for (int i = 0; i < classifyList1.size(); i++) {
            if (classifyList1.get(i).getLabelId() > 0) {
                for (ClassifyVo vo : validClassifyList1) {
                    if (vo.getLabelId() == classifyVo.getLabelId()) {
                        continue;
                    }
                }
            }
            validClassifyList1.add(classifyList1.get(i));
        }
        for (int i = 0; i < this.validClassifyList1.size(); i++) {
            if (this.validClassifyList1.get(i).getLabelId() == param1LabelId) {
                getSortCb(1).setText(this.validClassifyList1.get(i).getConfigValue());
                return;
            }
        }
        getSortCb(1).setText(classifyAllGrade.getConfigValue());
        param1LabelId = classifyAllGrade.getLabelId();
    }

    private void updateValidList2() {
        validClassifyList2 = new ArrayList<>();
        for (int i = 0; i < classifyList2.size(); i++) {
            if (classifyList2.get(i).getLabelId() > 0) {
                for (ClassifyVo vo : validClassifyList2) {
                    if (vo.getLabelId() == classifyVo.getLabelId()) {
                        continue;
                    }
                }
            }
            validClassifyList2.add(classifyList2.get(i));
        }
        for (int i = 0; i < this.validClassifyList2.size(); i++) {
            if (this.validClassifyList2.get(i).getLabelId() == param2LabelId) {
                getSortCb(2).setText(this.validClassifyList2.get(i).getConfigValue());
                return;
            }
        }
        getSortCb(2).setText(classifyAllTheme.getConfigValue());
        param2LabelId = classifyAllTheme.getLabelId();
    }

    private void updateValidList3() {
        validClassifyList3 = new ArrayList<>();
        for (int i = 0; i < classifyList3.size(); i++) {
            if (classifyList3.get(i).getLabelId() > 0) {
                for (ClassifyVo vo : validClassifyList3) {
                    if (vo.getLabelId() == classifyVo.getLabelId()) {
                        getSortCb(2).setText(this.validClassifyList2.get(i).getConfigValue());
                        continue;
                    }
                }
            }
            validClassifyList3.add(classifyList3.get(i));
        }
        for (int i = 0; i < this.validClassifyList3.size(); i++) {
            if (this.validClassifyList3.get(i).getLabelId() == param3LabelId) {
                return;
            }
        }
        getSortCb(3).setText(classifyAllSubject.getConfigValue());
        param3LabelId = classifyAllSubject.getLabelId();
    }

    private FilterHistoryVo readFilterHistory(){
        FilterHistoryVo filterHistoryVo = null;
        DbManager db = x.getDb(DbHelper.getDaoConfig());
        try {
            List<FilterHistoryVo> list = db.findAll(FilterHistoryVo.class);
            if (list != null && list.size() > 0) {
                for(FilterHistoryVo vo : list) {
                    if (vo.getUserId().equals(UserHelper.getUserId())
                            && vo.getTopLevel().equals(topLevel)) {
                        filterHistoryVo = vo;
                    }
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

        return filterHistoryVo;
    }

    private void saveFilterHistory(){
        DbManager db = x.getDb(DbHelper.getDaoConfig());
        try {
            db.delete(FilterHistoryVo.class,
                    WhereBuilder.b("topLevel", "=", topLevel)
                            .and("userId", "=", UserHelper.getUserId()));
        } catch (DbException e) {
            e.printStackTrace();
        }
        FilterHistoryVo filterHistoryVo = new FilterHistoryVo();
        filterHistoryVo.setUserId(UserHelper.getUserId());
        filterHistoryVo.setTopLevel(topLevel);
        filterHistoryVo.setLabel1(param1LabelId);
        filterHistoryVo.setLabel2(param2LabelId);
        filterHistoryVo.setLabel3(param3LabelId);
        filterHistoryVo.setStatusFilter(paramStatus);
        try {
            db.save(filterHistoryVo);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}
