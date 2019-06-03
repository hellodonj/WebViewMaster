package com.lqwawa.intleducation.module.discovery.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseFragment;
import com.lqwawa.intleducation.base.utils.LogUtil;
import com.lqwawa.intleducation.base.utils.StringUtils;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.common.ui.LinePopupWindow;
import com.lqwawa.intleducation.common.ui.SortLinePopupWindow;
import com.lqwawa.intleducation.module.discovery.adapter.CourseListAdapter;
import com.lqwawa.intleducation.module.discovery.vo.ClassifyVo;
import com.lqwawa.intleducation.module.discovery.vo.CourseSortType;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.discovery.vo.FilterVo;
import com.lqwawa.intleducation.module.discovery.vo.HQCPermissionsVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by XChen on 2017/7/24.
 * email:man0fchina@foxmail.com
 */

public class HQCCourseListFragment extends MyBaseFragment  implements View.OnClickListener {
    private static final String TAG = LQCourseCourseListActivity.class.getSimpleName();
    public static String LEVEL_BASE1 = "775";//基础课程
    public static String LEVEL_BASE2 = "776";//国际课程
    public static String LEVEL_BASE3 = "777";//阅读课程
    public static String LEVEL_BASE4 = "778";//表演课程
    public static String LEVEL_BASE2_HS = "776.786"; //国际课程-高中

    private String SORT_TYPE_GLASS = "21";
    private String SORT_TYPE_SUBJECTS  = "22";
    private String SORT_TYPE_BOOK_CONCERN  = "23";

    private static final int CLS_ALL_VALUE = -100;
    private static final int sortItems = 4;

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
    private String paramSort = "2";
    private String topLevel = "";
    private int param1LabelId = CLS_ALL_VALUE;
    private int param2LabelId = CLS_ALL_VALUE;
    private int param3LabelId = CLS_ALL_VALUE;
    private String paramStatus = "";
    private String paramOrganId;
    private String levelName;
    private ClassifyVo selectClassVo;
    private List<ClassifyVo> classifyList1;
    private List<ClassifyVo> classifyList2;
    private List<ClassifyVo> classifyList3;
    private List<ClassifyVo> validClassifyList1;
    private List<ClassifyVo> validClassifyList2;
    private List<ClassifyVo> validClassifyList3;
    private boolean firstSelectClassify = false;

    ClassifyVo classifyAll = new ClassifyVo();
    ClassifyVo filterClassifyAll1 = new ClassifyVo();
    ClassifyVo filterClassifyAll2 = new ClassifyVo();
    ClassifyVo filterClassifyAll3 = new ClassifyVo();
    /**
     * sort view end
     *****/
    private List<CourseVo> courseList;
    private CourseListAdapter courseListAdapter;
    private List<CourseSortType> statusSortTypeList;
    private List<HQCPermissionsVo> permissionsVos;

    FilterVo segmentSelect = null;
    FilterVo ageSelect = null;
    private boolean openAll = false;

    private OnCourseSelListener onCourseSelListener = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lq_course_course_list, container, false);

        pullToRefresh = (PullToRefreshView) view.findViewById(R.id.pull_to_refresh);
        listView = (ListView) view.findViewById(R.id.listView);
        loadFailedLayout = (RelativeLayout) view.findViewById(R.id.load_failed_layout);
        reloadBt = (Button) view.findViewById(R.id.reload_bt);
        view.findViewById(R.id.reload_bt).setOnClickListener(this);
        view.findViewById(R.id.tv_phone_number).setOnClickListener(this);
        sortCb1 = (CheckBox) view.findViewById(R.id.sort_cb1);
        sortCb2 = (CheckBox) view.findViewById(R.id.sort_cb2);
        sortCb3 = (CheckBox) view.findViewById(R.id.sort_cb3);
        sortCb4 = (CheckBox) view.findViewById(R.id.sort_cb4);
        topLevel = getArguments().getString("Level");

        classifyAll.setLevel("");
        classifyAll.setLabelId(CLS_ALL_VALUE);
        classifyAll.setConfigValue(activity.getResources().getString(R.string.all));
        filterClassifyAll1.setLevel("");
        filterClassifyAll1.setLabelId(CLS_ALL_VALUE);
        filterClassifyAll1.setConfigValue(activity.getResources().getString(R.string.grade));
        filterClassifyAll2.setLevel("");
        filterClassifyAll2.setLabelId(CLS_ALL_VALUE);
        filterClassifyAll3.setLevel("");
        filterClassifyAll3.setLabelId(CLS_ALL_VALUE);
        selectClassVo = (ClassifyVo) getArguments().getSerializable("classify");

        resetFilterClassify(view);

        paramCourseName = getArguments().getString("CourseName");
        paramSort = getArguments().getString("Sort");
        if (!StringUtils.isValidString(paramSort) || "-1".equals(paramSort)) {
            paramSort = "2";
        }
        selectClassVo = (ClassifyVo) getArguments().getSerializable("classify");
        topLevel = getArguments().getString("Level");

        if (topLevel == null) {
            view.findViewById(R.id.child_sort_root).setVisibility(View.GONE);
        }

        paramOrganId = getArguments().getString("OrganId");
        levelName = getArguments().getString("LevelName");

        param1LabelId = CLS_ALL_VALUE;
        param2LabelId = CLS_ALL_VALUE;
        param3LabelId = CLS_ALL_VALUE;

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        courseList = new ArrayList<>();
        courseListAdapter = new CourseListAdapter(activity);
        if(topLevel != null && selectClassVo.isHaveVipPermissions()) {
            if(initList()) {
                initData();
            }else{
                showNoVipPermissonsPage(true, true);
            }
        }else{
            showNoVipPermissonsPage(true, true);
        }
    }

    public void setOnCourseSelListener(OnCourseSelListener listener){
        this.onCourseSelListener = listener;
    }

    public void updateForSearch(String keyWord){
        this.paramCourseName = keyWord;
        initData();
    }

    private void initViews() {
        for (int i = 0; i < sortItems; i++) {
            getSortCb(i).setOnClickListener(this);
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
                if(activity.getIntent().getBooleanExtra("isForSelRes", false)){
                    if(onCourseSelListener != null){
                        onCourseSelListener.onCourseSel(vo);
                    }
                }else {
                    CourseDetailsActivity.start(activity, vo.getId(), true,
                            UserHelper.getUserId());
                }
            }
        });
    }

    public void showNoVipPermissonsPage(boolean show, boolean withFilter){
        if(getView().findViewById(R.id.no_vip_permissions_lay) != null){
            getView().findViewById(R.id.no_vip_permissions_lay).
                    setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if(withFilter){
            if(getView().findViewById(R.id.child_sort_root) != null){
                getView().findViewById(R.id.child_sort_root).
                        setVisibility(show ? View.GONE : View.VISIBLE);
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.reload_bt) {
            initData();
        }
        if (view.getId() == R.id.sort_cb1) {
            sortCb1Click(view);
        } else if (view.getId() == R.id.sort_cb2) {
            sortCb2Click(view);
        } else if (view.getId() == R.id.sort_cb3) {
            sortCb3Click(view);
        } else if (view.getId() == R.id.sort_cb4) {
            sortCb4Click(view);
        } else if (view.getId() == R.id.tv_phone_number) {
            phoneClick();
        }
    }

    private void resetFilterClassify(View view){
        view.findViewById(R.id.rb3_lay).setVisibility(View.VISIBLE);
        view.findViewById(R.id.rb3_split).setVisibility(View.VISIBLE);
        if(LEVEL_BASE1.equals(selectClassVo.getLevel())) {//基础课程
            filterClassifyAll2.setConfigValue( activity.getResources().getString(R.string.course_subject));//科目
            filterClassifyAll3.setConfigValue(activity.getResources().getString(R.string.book_concern));//出版社
        }else if(LEVEL_BASE2.equals(selectClassVo.getLevel())) {//国际课程
            if(LEVEL_BASE2_HS.equals(topLevel)) {
                filterClassifyAll1.setConfigValue(activity.getResources().getString(R.string.type));
                filterClassifyAll2.setConfigValue(activity.getResources().getString(R.string.classification));//分类 国际课程在高中阶段是有分类的
                filterClassifyAll3.setConfigValue(activity.getResources().getString(R.string.course_subject));
            }else{
                filterClassifyAll1.setConfigValue(activity.getResources().getString(R.string.grade));
                filterClassifyAll2.setConfigValue(activity.getResources().getString(R.string.course_subject));
                view.findViewById(R.id.rb3_lay).setVisibility(View.GONE);
                view.findViewById(R.id.rb3_split).setVisibility(View.GONE);
            }
        }else if(LEVEL_BASE3.equals(selectClassVo.getLevel())) {//阅读课程
            filterClassifyAll2.setConfigValue(activity.getResources().getString(R.string.language));//语言
            filterClassifyAll3.setConfigValue(activity.getResources().getString(R.string.course_subject));//科目
        }else if(LEVEL_BASE4.equals(selectClassVo.getLevel())) {//表演课程 表演课程只有年级和科目
            filterClassifyAll2.setConfigValue(activity.getResources().getString(R.string.course_subject));
            filterClassifyAll3.setConfigValue("");
            view.findViewById(R.id.rb3_lay).setVisibility(View.GONE);
            view.findViewById(R.id.rb3_split).setVisibility(View.GONE);
        }
    }

    /**
     * 选择幼儿园/小学/初中/高中
     * @param level
     */
    public void changeSection(String level){
        this.topLevel = level;

        resetFilterClassify(getView());
        if(initList()) {
            showNoVipPermissonsPage(false, true);
            initData();
        }else{
            showNoVipPermissonsPage(true, true);
        }
    }

    private boolean initList() {
        List<ClassifyVo> pListTemp = new ArrayList<>();
        if(getSelectClassify(selectClassVo.getChildList(), topLevel).getChildList()!= null) {
            pListTemp.addAll(getSelectClassify(selectClassVo.getChildList(), topLevel).getChildList());
        }

        List<HQCPermissionsVo> allPermissions = (ArrayList<HQCPermissionsVo>)
                activity.getIntent().getSerializableExtra("PermissionsList");
        if(allPermissions == null){//权限列表为空
            return false;
        }
        if(allPermissions.size() == 0){//权限列表为空
            return false;
        }
        permissionsVos = new ArrayList<>();
        for (HQCPermissionsVo permissionsVo : allPermissions) {
            if (TextUtils.equals(permissionsVo.getFirstId(), "" + selectClassVo.getId())//过滤第一级 例如基础课程
                    && (TextUtils.equals(permissionsVo.getFirstId() + "."
                            + permissionsVo.getSecondId(), "" + topLevel)//过滤第二级 幼儿园等
                    || TextUtils.equals(permissionsVo.getSecondId(), "0"))) {//或者是第二级已经全部开通的情况
                permissionsVos.add(permissionsVo);
            }
        }

        if(permissionsVos.size() == 0){//适配的权限列表为空
            return false;
        }
        /**
         * 第一级（例如国际课程）和第二级（例如幼儿园）的权限列表都有 那就继续吧
         */

        openAll = false;
        for (HQCPermissionsVo permissionsVo : allPermissions) {
            if (TextUtils.equals(permissionsVo.getFirstId(), "" + selectClassVo.getId())//过滤第一级 例如基础课程
                    && (TextUtils.equals(permissionsVo.getSecondId(), "0")//所有的第二级已经全部开通
                    || (TextUtils.equals(permissionsVo.getFirstId() + "." //当前的第二级是开通的
                    + permissionsVo.getSecondId(), topLevel)
                    && TextUtils.equals(permissionsVo.getParamOneId(), "0")
                    && TextUtils.equals(permissionsVo.getParamTwoId(), "0")))) {
                openAll = true;
                break;
            }
        }
        if(openAll) {//年级（例幼儿园）全部开通了则不需要进行权限的判断
            classifyList1 = new ArrayList<>();
            classifyList1.add(new ClassifyVo(filterClassifyAll1));
            classifyList2 = new ArrayList<>();
            classifyList2.add(new ClassifyVo(filterClassifyAll2));

            if (LEVEL_BASE1.equals(selectClassVo.getLevel())) {
                for (int i = 0; i < pListTemp.size(); i++) {
                    if (pListTemp.get(i).getConfigType().equals(SORT_TYPE_GLASS)) {
                        classifyList1.add(pListTemp.get(i));
                    } else if (pListTemp.get(i).getConfigType().equals(SORT_TYPE_SUBJECTS)) {
                        classifyList2.add(pListTemp.get(i));
                    }
                }
                validClassifyList1 = new ArrayList<>();
                validClassifyList2 = new ArrayList<>();
                validClassifyList3 = new ArrayList<>();
                updateValidList1();
                updateValidList2();
                classifyList3 = new ArrayList<>();
                classifyList3.add(filterClassifyAll3);
                for (int i = 0; i < selectClassVo.getChildList().size(); i++) {
                    if (SORT_TYPE_BOOK_CONCERN.equals(selectClassVo.getChildList().get(i).getConfigType())) {
                        classifyList3.add(selectClassVo.getChildList().get(i));
                    }
                }
                updateValidList3();
            } else if (LEVEL_BASE2.equals(selectClassVo.getLevel())) {
                if (LEVEL_BASE2_HS.equals(topLevel)) {//国际课程-高中
                    for (int i = 0; i < pListTemp.size(); i++) {
                        classifyList1.add(pListTemp.get(i));
                    }
                    for (int i = 0; i < classifyList1.size(); i++) {
                        if (classifyList1.get(i).getChildList() != null) {
                            classifyList2.addAll(classifyList1.get(i).getChildList());
                        }
                    }
                    validClassifyList1 = new ArrayList<>();
                    validClassifyList2 = new ArrayList<>();
                    validClassifyList3 = new ArrayList<>();
                    updateValidList1();
                    updateValidList2();
                    classifyList3 = new ArrayList<>();
                    classifyList3.add(filterClassifyAll3);
                    for (int i = 0; i < validClassifyList2.size(); i++) {
                        if (validClassifyList2.get(i).getChildList() != null) {
                            classifyList3.addAll(validClassifyList2.get(i).getChildList());
                        }
                    }

                    updateValidList3();

                    ClassifyVo classifyVoParam1 = getSelectClassify(classifyList1, param1LabelId);
                    if (classifyVoParam1.getLevel().equals("")) {
                        classifyVoParam1 = filterClassifyAll1;
                    }
                    changeParam1Select(classifyVoParam1);

                    for (ClassifyVo classifyVo : validClassifyList3) {
                        if (param3LabelId == classifyVo.getLabelId()) {
                            sortCb3.setText(classifyVo.getConfigValue());
                            break;
                        }
                    }
                } else {
                    for (int i = 0; i < pListTemp.size(); i++) {
                        if (pListTemp.get(i).getConfigType().equals(SORT_TYPE_GLASS)) {
                            classifyList1.add(pListTemp.get(i));
                        } else if (pListTemp.get(i).getConfigType().equals(SORT_TYPE_SUBJECTS)) {
                            classifyList2.add(pListTemp.get(i));
                        }
                    }
                    validClassifyList1 = new ArrayList<>();
                    validClassifyList2 = new ArrayList<>();
                    validClassifyList3 = new ArrayList<>();
                    updateValidList1();
                    updateValidList2();
                }
            } else if (LEVEL_BASE3.equals(selectClassVo.getLevel())) {
                for (int i = 0; i < pListTemp.size(); i++) {
                    if (pListTemp.get(i).getConfigType().equals(SORT_TYPE_GLASS)) {
                        classifyList1.add(pListTemp.get(i));
                    } else if (pListTemp.get(i).getConfigType().equals(SORT_TYPE_SUBJECTS)) {
                        classifyList2.add(pListTemp.get(i));
                    }
                }
                validClassifyList1 = new ArrayList<>();
                validClassifyList2 = new ArrayList<>();
                validClassifyList3 = new ArrayList<>();
                updateValidList1();
                updateValidList2();
                classifyList3 = new ArrayList<>();
                classifyList3.add(filterClassifyAll3);
                List<ClassifyVo> classifyVoParam2List =
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
                }
                updateValidList3();
            } else if (LEVEL_BASE4.equals(selectClassVo.getLevel())) {
                for (int i = 0; i < pListTemp.size(); i++) {
                    if (pListTemp.get(i).getConfigType().equals(SORT_TYPE_GLASS)) {
                        classifyList1.add(pListTemp.get(i));
                    } else if (pListTemp.get(i).getConfigType().equals(SORT_TYPE_SUBJECTS)) {
                        classifyList2.add(pListTemp.get(i));
                    }
                }
                validClassifyList1 = new ArrayList<>();
                validClassifyList2 = new ArrayList<>();
                validClassifyList3 = new ArrayList<>();
                updateValidList1();
                updateValidList2();
            }
        }else {//年级（例幼儿园）米有全部开通
            classifyList1 = new ArrayList<>();
            classifyList1.add(new ClassifyVo(filterClassifyAll1));
            classifyList2 = new ArrayList<>();
            classifyList2.add(new ClassifyVo(filterClassifyAll2));

            if (LEVEL_BASE1.equals(selectClassVo.getLevel())) {
                for (int i = 0; i < pListTemp.size(); i++) {
                    if (pListTemp.get(i).getConfigType().equals(SORT_TYPE_GLASS)) {
                        if (checkPermissionsParamOne("" + pListTemp.get(i).getLabelId())) {
                            classifyList1.add(pListTemp.get(i));
                        }
                    } else if (pListTemp.get(i).getConfigType().equals(SORT_TYPE_SUBJECTS)) {
                        if (checkPermissionsParamTwo("" + pListTemp.get(i).getLabelId())) {
                            classifyList2.add(pListTemp.get(i));
                        }
                    }
                }
                validClassifyList1 = new ArrayList<>();
                validClassifyList2 = new ArrayList<>();
                validClassifyList3 = new ArrayList<>();
                updateValidList1();
                updateValidList2();
                classifyList3 = new ArrayList<>();
                classifyList3.add(filterClassifyAll3);
                for (int i = 0; i < selectClassVo.getChildList().size(); i++) {
                    if (SORT_TYPE_BOOK_CONCERN.equals(selectClassVo.getChildList().get(i).getConfigType())) {
                        classifyList3.add(selectClassVo.getChildList().get(i));
                    }
                }
                updateValidList3();
            } else if (LEVEL_BASE2.equals(selectClassVo.getLevel())) {
                if (LEVEL_BASE2_HS.equals(topLevel)) {//国际课程-高中
                    for (int i = 0; i < pListTemp.size(); i++) {
                        if (checkPermissionsParamOne("" + pListTemp.get(i).getLabelId())) {
                            classifyList1.add(pListTemp.get(i));
                        }
                    }
                    for (int i = 0; i < classifyList1.size(); i++) {
                        if (classifyList1.get(i).getChildList() != null) {
                            for(int j = 0; j < classifyList1.get(i).getChildList().size(); j++) {
                                if (checkPermissionsParamTwo(
                                        "" + classifyList1.get(i).getChildList().get(j).getLabelId())) {
                                    classifyList2.add(classifyList1.get(i).getChildList().get(j));
                                }
                            }
                        }
                    }
                    validClassifyList1 = new ArrayList<>();
                    validClassifyList2 = new ArrayList<>();
                    validClassifyList3 = new ArrayList<>();
                    updateValidList1();
                    updateValidList2();
                    classifyList3 = new ArrayList<>();
                    classifyList3.add(filterClassifyAll3);
                    for (int i = 0; i < validClassifyList2.size(); i++) {
                        if (validClassifyList2.get(i).getChildList() != null) {
                            classifyList3.addAll(validClassifyList2.get(i).getChildList());
                        }
                    }

                    updateValidList3();

                    ClassifyVo classifyVoParam1 = getSelectClassify(classifyList1, param1LabelId);
                    if (classifyVoParam1.getLevel().equals("")) {
                        classifyVoParam1 = filterClassifyAll1;
                    }
                    changeParam1Select(classifyVoParam1);

                    for (ClassifyVo classifyVo : validClassifyList3) {
                        if (param3LabelId == classifyVo.getLabelId()) {
                            sortCb3.setText(classifyVo.getConfigValue());
                            break;
                        }
                    }
                } else {
                    for (int i = 0; i < pListTemp.size(); i++) {
                        if (pListTemp.get(i).getConfigType().equals(SORT_TYPE_GLASS)) {
                            if (checkPermissionsParamOne("" + pListTemp.get(i).getLabelId())) {
                                classifyList1.add(pListTemp.get(i));
                            }
                        } else if (pListTemp.get(i).getConfigType().equals(SORT_TYPE_SUBJECTS)) {
                            if (checkPermissionsParamTwo("" + pListTemp.get(i).getLabelId())) {
                                classifyList2.add(pListTemp.get(i));
                            }
                        }
                    }
                    validClassifyList1 = new ArrayList<>();
                    validClassifyList2 = new ArrayList<>();
                    validClassifyList3 = new ArrayList<>();
                    updateValidList1();
                    updateValidList2();
                }
            } else if (LEVEL_BASE3.equals(selectClassVo.getLevel())) {
                for (int i = 0; i < pListTemp.size(); i++) {
                    if (pListTemp.get(i).getConfigType().equals(SORT_TYPE_GLASS)) {
                        if (checkPermissionsParamOne("" + pListTemp.get(i).getLabelId())) {
                            classifyList1.add(pListTemp.get(i));
                        }
                    } else if (pListTemp.get(i).getConfigType().equals(SORT_TYPE_SUBJECTS)) {
                        if (checkPermissionsParamTwo("" + pListTemp.get(i).getLabelId())) {
                            classifyList2.add(pListTemp.get(i));
                        }
                    }
                }
                validClassifyList1 = new ArrayList<>();
                validClassifyList2 = new ArrayList<>();
                validClassifyList3 = new ArrayList<>();
                updateValidList1();
                updateValidList2();
                classifyList3 = new ArrayList<>();
                classifyList3.add(filterClassifyAll3);
                List<ClassifyVo> classifyVoParam2List =
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
                }
                updateValidList3();
            } else if (LEVEL_BASE4.equals(selectClassVo.getLevel())) {
                for (int i = 0; i < pListTemp.size(); i++) {
                    if (pListTemp.get(i).getConfigType().equals(SORT_TYPE_GLASS)) {
                        if (checkPermissionsParamOne("" + pListTemp.get(i).getLabelId())) {
                            classifyList1.add(pListTemp.get(i));
                        }
                    } else if (pListTemp.get(i).getConfigType().equals(SORT_TYPE_SUBJECTS)) {
                        if (checkPermissionsParamTwo("" + pListTemp.get(i).getLabelId())) {
                            classifyList2.add(pListTemp.get(i));
                        }
                    }
                }
                validClassifyList1 = new ArrayList<>();
                validClassifyList2 = new ArrayList<>();
                validClassifyList3 = new ArrayList<>();
                updateValidList1();
                updateValidList2();
            }
        }

        ClassifyVo classifyVoParam1 = getSelectClassify(classifyList1, param1LabelId);
        if (classifyVoParam1.getLevel().equals("")){
            classifyVoParam1 = filterClassifyAll1;
        }
        changeParam1Select(classifyVoParam1);

        for(ClassifyVo selectClassVo : validClassifyList3) {
            if (param3LabelId == selectClassVo.getLabelId()){
                sortCb3.setText(selectClassVo.getConfigValue());
                break;
            }
        }

        statusSortTypeList = new ArrayList<>();
        statusSortTypeList.add(new CourseSortType("-1",activity.getResources().getString(R.string.course_status), true));
        sortCb4.setText(activity.getResources().getString(R.string.course_status));
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

        return true;
    }

    private boolean checkPermissionsParamOne(String param){
        if(permissionsVos == null){
            return false;
        }
        boolean result = false;
        for(int i = 0; i < permissionsVos.size(); i++){
            if (TextUtils.equals(param, permissionsVos.get(i).getParamOneId())
                    || TextUtils.equals("0", permissionsVos.get(i).getParamOneId())){
                result = true;
                break;
            }
        }
        return result;
    }

    private boolean checkPermissionsParamTwo(String param){
        if(permissionsVos == null){
            return false;
        }
        boolean result = false;
        for (int i = 0; i < permissionsVos.size(); i++) {
            if (TextUtils.equals(param, permissionsVos.get(i).getParamTwoId())
                    || "0".equals(permissionsVos.get(i).getParamTwoId())) {
                result = true;
                break;
            }
        }
        return result;
    }

    private boolean checkPermissionsParamOneAndTwo(String paramOne, String paramTwo){
        if(permissionsVos == null){
            return false;
        }
        boolean result = false;
        for(int i = 0; i < permissionsVos.size(); i++){
            if ((TextUtils.equals(paramOne, permissionsVos.get(i).getParamOneId())
                    || TextUtils.equals("0", permissionsVos.get(i).getParamOneId()))
                    && (TextUtils.equals(paramTwo, permissionsVos.get(i).getParamTwoId())
                    || "0".equals(permissionsVos.get(i).getParamTwoId()))){
                result = true;
                break;
            }
        }
        return result;
    }

    private void sortCb1Click(View view) {
        if (selectClassVo == null || classifyList1 == null) {
            return;
        }
        doNotCheckWithout(1);
        SortLinePopupWindow linePopupWindow =
                new SortLinePopupWindow(activity, validClassifyList1, "" + param1LabelId,
                        new SortLinePopupWindow.PopupWindowListener() {
                            @Override
                            public void onItemClickListener(int position, Object object) {
                                ClassifyVo vo = (ClassifyVo) object;
                                doNotCheckAll();
                                changeParam1Select(vo);
                                initData();
                            }

                            @Override
                            public void onDismissListener() {
                                doNotCheckAll();
                            }
                        }, true);
        linePopupWindow.showPopupWindow(view, true);
    }

    private void changeParam1Select(ClassifyVo vo) {
        doNotCheckAll();
        param1LabelId = vo.getLabelId();
        sortCb1.setText(vo.getConfigValue());

        if(openAll){
            if(LEVEL_BASE2.equals(selectClassVo.getLevel())){
                if(LEVEL_BASE2_HS.equals(topLevel)) {
                    classifyList2 = new ArrayList<>();
                    classifyList2.add(filterClassifyAll2);
                    boolean needLoadAll = true;
                    if (param1LabelId != CLS_ALL_VALUE) {
                        for (int i = 0; i < classifyList1.size(); i++) {
                            if (classifyList1.get(i).getLabelId() == param1LabelId
                                    && classifyList1.get(i).getChildList() != null) {
                                classifyList2.addAll(classifyList1.get(i).getChildList());
                                needLoadAll = false;
                                break;
                            }
                        }
                    }
                    if (needLoadAll) {
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
                    classifyList3.add(filterClassifyAll3);
                    needLoadAll = true;
                    if (param2LabelId != CLS_ALL_VALUE) {//不是全部
                        for (int i = 0; i < validClassifyList2.size(); i++) {
                            if (validClassifyList2.get(i).getChildList() != null
                                    && classifyList2.get(i).getLabelId() == param2LabelId) {
                                classifyList3.addAll(classifyList2.get(i).getChildList());
                                needLoadAll = false;
                                break;
                            }
                        }
                    }
                    if (needLoadAll) {//全部
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
            }
        } else {
            classifyList2 = new ArrayList<>();
            classifyList2.add(filterClassifyAll2);
            boolean needLoadAll = true;
            if (param1LabelId != CLS_ALL_VALUE) {
                needLoadAll = false;
                if (LEVEL_BASE2.equals(selectClassVo.getLevel())
                        && LEVEL_BASE2_HS.equals(topLevel)) {//国际课程高中特殊情况处理
                    for (int i = 0; i < classifyList1.size(); i++) {
                        if (classifyList1.get(i).getLabelId() == param1LabelId
                                && classifyList1.get(i).getChildList() != null) {
                            for (ClassifyVo classifyVo : classifyList1.get(i).getChildList()) {
                                if (checkPermissionsParamOneAndTwo(
                                        "" + param1LabelId, "" + classifyVo.getLabelId())) {
                                    classifyList2.add(classifyVo);
                                }
                            }
                            break;
                        }
                    }
                } else {
                    List<ClassifyVo> pListTemp = new ArrayList<>();
                    if (getSelectClassify(selectClassVo.getChildList(), topLevel).getChildList() != null) {
                        pListTemp.addAll(getSelectClassify(selectClassVo.getChildList(), topLevel).getChildList());
                    }
                    for (int i = 0; i < pListTemp.size(); i++) {
                        if (pListTemp.get(i).getConfigType().equals(SORT_TYPE_SUBJECTS)) {
                            if (checkPermissionsParamOneAndTwo("" + param1LabelId,
                                    "" + pListTemp.get(i).getLabelId())) {
                                classifyList2.add(pListTemp.get(i));
                            }
                        }
                    }
                }
            }
            if (needLoadAll) {
                if (LEVEL_BASE2.equals(selectClassVo.getLevel())
                        && LEVEL_BASE2_HS.equals(topLevel)) {//国际课程高中特殊情况处理
                    for (int i = 0; i < classifyList1.size(); i++) {
                        if (classifyList1.get(i).getChildList() != null) {
                            for (int j = 0; j < classifyList1.get(i).getChildList().size(); j++) {
                                if (checkPermissionsParamOneAndTwo("" + classifyList1.get(i).getLabelId(),
                                        "" + classifyList1.get(i).getChildList().get(j).getLabelId())) {
                                    classifyList2.add(classifyList1.get(i).getChildList().get(j));
                                }
                            }
                        }
                    }
                } else {
                    List<ClassifyVo> pListTemp = new ArrayList<>();
                    if (getSelectClassify(selectClassVo.getChildList(), topLevel).getChildList() != null) {
                        pListTemp.addAll(getSelectClassify(selectClassVo.getChildList(), topLevel).getChildList());
                    }
                    for (int i = 0; i < pListTemp.size(); i++) {
                        if (pListTemp.get(i).getConfigType().equals(SORT_TYPE_SUBJECTS)) {
                            if (checkPermissionsParamTwo("" + pListTemp.get(i).getLabelId())) {
                                classifyList2.add(pListTemp.get(i));
                            }
                        }
                    }
                }
            }
            validClassifyList2 = new ArrayList<>();
            updateValidList2();
            checkSortParam2();

            if (LEVEL_BASE2.equals(selectClassVo.getLevel())
                    && LEVEL_BASE2_HS.equals(topLevel)) {//国际课程高中特殊情况处理
                classifyList3 = new ArrayList<>();
                classifyList3.add(filterClassifyAll3);
                needLoadAll = true;
                if (param2LabelId != CLS_ALL_VALUE) {//不是全部
                    for (int i = 0; i < validClassifyList2.size(); i++) {
                        if (validClassifyList2.get(i).getChildList() != null
                                && classifyList2.get(i).getLabelId() == param2LabelId) {
                            classifyList3.addAll(classifyList2.get(i).getChildList());
                            needLoadAll = false;
                            break;
                        }
                    }
                }
                if (needLoadAll) {//全部
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
        }
    }


    private void checkSortParam2() {
        for (int i = 0; i < validClassifyList2.size(); i++) {
            if (validClassifyList2.get(i).getLabelId() == param2LabelId) {
                return;
            }
        }
        param2LabelId = CLS_ALL_VALUE;
        getSortCb(2).setText(filterClassifyAll2.getConfigValue());
    }

    private void sortCb2Click(View view) {
        if (selectClassVo == null || validClassifyList2 == null) {
            return;
        }
        doNotCheckWithout(2);
        SortLinePopupWindow linePopupWindow =
                new SortLinePopupWindow(activity, validClassifyList2, "" + param2LabelId,
                        new SortLinePopupWindow.PopupWindowListener() {
                            @Override
                            public void onItemClickListener(int position, Object object) {
                                ClassifyVo vo = (ClassifyVo) object;
                                changeParam2Select(vo);
                                initData();
                            }

                            @Override
                            public void onDismissListener() {
                                doNotCheckAll();
                            }
                        }, true);
        linePopupWindow.showPopupWindow(view, true);
    }

    private void changeParam2Select(ClassifyVo vo) {
        doNotCheckAll();
        param2LabelId = vo.getLabelId();
        getSortCb(2).setText(vo.getConfigValue());
        classifyList3 = new ArrayList<>();
        classifyList3.add(filterClassifyAll3);
        ClassifyVo classifyVoParam2 = getSelectClassify(validClassifyList2, param2LabelId);
        if (classifyVoParam2.getLevel().equals("")){
            classifyVoParam2 = filterClassifyAll2;
        }
        if(LEVEL_BASE2.equals(selectClassVo.getLevel()) && LEVEL_BASE2_HS.equals(topLevel)){
            classifyList3 = new ArrayList<>();
            classifyList3.add(filterClassifyAll3);
            boolean needLoadAll = true;
            if (param2LabelId != CLS_ALL_VALUE) {//不是全部
                for (int i = 0; i < validClassifyList2.size(); i++) {
                    if (validClassifyList2.get(i).getChildList() != null
                            && classifyList2.get(i).getLabelId() == param2LabelId) {
                        classifyList3.addAll(classifyList2.get(i).getChildList());
                        needLoadAll = false;
                        break;
                    }
                }
            }
            if (needLoadAll) {//全部
                for (int i = 0; i < classifyList2.size(); i++) {
                    if (classifyList2.get(i).getChildList() != null) {
                        classifyList3.addAll(classifyList2.get(i).getChildList());
                    }
                }
            }
            validClassifyList3 = new ArrayList<>();
            updateValidList3();
            checkSortParam3();
        }else if(LEVEL_BASE3.equals(selectClassVo.getLevel())) {
            if (param2LabelId != CLS_ALL_VALUE) {//不是全部
                if (classifyVoParam2.getChildList() != null) {
                    classifyList3.addAll(classifyVoParam2.getChildList());
                }
            } else {//全部
                for (int i = 0; i < validClassifyList2.size(); i++) {
                    if ((validClassifyList2.get(i)).getChildList() != null) {
                        classifyList3.addAll((validClassifyList2.get(i)).getChildList());
                    }
                }
            }
            updateValidList3();
            checkSortParam3();
        }
    }

    public void checkSortParam3() {
        for (int i = 0; i < validClassifyList3.size(); i++) {
            if (validClassifyList3.get(i).getLabelId() == param3LabelId) {
                return;
            }
        }
        param3LabelId = CLS_ALL_VALUE;
        getSortCb(3).setText(filterClassifyAll3.getConfigValue());
    }

    private void sortCb3Click(View view) {
        if (selectClassVo == null || validClassifyList3 == null) {
            return;
        }
        doNotCheckWithout(3);
        SortLinePopupWindow linePopupWindow =
                new SortLinePopupWindow(activity, validClassifyList3, "" + param3LabelId,
                        new SortLinePopupWindow.PopupWindowListener() {
                            @Override
                            public void onItemClickListener(int postion, Object object) {
                                ClassifyVo vo = (ClassifyVo) object;
                                changeParam3Select(vo);
                                initData();
                            }

                            @Override
                            public void onDismissListener() {
                                doNotCheckAll();
                            }
                        }, true);
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

                                initData();
                            }

                            @Override
                            public void onDismissListener() {
                                doNotCheckAll();
                            }
                        }, true);
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
            return (CheckBox) getView().findViewById(R.id.sort_cb1);
        } else if (index == 2) {
            return (CheckBox) getView().findViewById(R.id.sort_cb2);
        } else if (index == 3) {
            return (CheckBox) getView().findViewById(R.id.sort_cb3);
        } else {
            return (CheckBox) getView().findViewById(R.id.sort_cb4);
        }
    }

    private void initData() {
        pullToRefresh.showRefresh();
        getData();
    }

    private int pageIndex = 0;

    private void getData() {
        pageIndex = 0;
        loadFailedLayout.setVisibility(View.GONE);
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("pageIndex", pageIndex);
        requestVo.addParams("pageSize", AppConfig.PAGE_SIZE);
        requestVo.addParams("schoolId", activity.getIntent().getStringExtra("SchoolId"));
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
        if(topLevel == null){
            if (selectClassVo != null) {
                requestVo.addParams("level", selectClassVo.getLevel());
            }
        }else if (!topLevel.equals(selectClassVo.getLevel())) {
            requestVo.addParams("level", topLevel);
        }
        if (param1LabelId > 0) {
            requestVo.addParams("paramOneId", param1LabelId);
        }
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
                new RequestParams(AppConfig.ServerUrl.getCourseListByOrganId + requestVo.getParams());
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
        requestVo.addParams("schoolId", activity.getIntent().getStringExtra("SchoolId"));
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
        if(topLevel == null){
            if (selectClassVo != null) {
                requestVo.addParams("level", selectClassVo.getLevel());
            }
        }else if (!topLevel.equals(selectClassVo.getLevel())) {
            requestVo.addParams("level", topLevel);
        }
        if (param1LabelId > 0) {
            requestVo.addParams("paramOneId", param1LabelId);
        }
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
                new RequestParams(AppConfig.ServerUrl.getCourseListByOrganId + requestVo.getParams());
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
                        ToastUtil.showToastBottom(activity.getApplicationContext(), R.string.no_more_data);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SelectChildClassifyActivity.Rc_SelectChildClasify) {
            if(resultCode == Activity.RESULT_OK ) {
                if (data != null) {
                    this.param1LabelId = data.getIntExtra("param1LabelId", -1);
                    this.sortCb1.setText(data.getStringExtra("param1LabelName")
                            .equals(classifyAll.getConfigValue()) ? filterClassifyAll1.getConfigValue()
                            : data.getStringExtra("param1LabelName"));
                    this.param2LabelId = data.getIntExtra("param2LabelId", -1);
                    this.sortCb2.setText(data.getStringExtra("param2LabelName")
                            .equals(classifyAll.getConfigValue()) ? filterClassifyAll2.getConfigValue()
                            : data.getStringExtra("param2LabelName"));
                    this.param3LabelId = data.getIntExtra("param3LabelId", -1);
                    this.sortCb3.setText(data.getStringExtra("param3LabelName")
                            .equals(classifyAll.getConfigValue()) ? filterClassifyAll3.getConfigValue()
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
                    if (this.selectClassVo != null) {
                        if (topLevel.isEmpty()) {
                            //topBar.setTitle(selectClassVo.getLevelName());
                        } else if (selectClassVo.getChildList() != null) {
                            for (int i = 0; i < selectClassVo.getChildList().size(); i++) {
                                if (selectClassVo.getChildList().get(i).getLevel().equals(this.topLevel)) {
                                    //topBar.setTitle(selectClassVo.getChildList().get(i).getLevelName());
                                }
                            }
                        }
                    }
                    initData();
                }
            }else{
                if(firstSelectClassify){
                    firstSelectClassify = false;
                    activity.finish();
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
            tempList.add(filterClassifyAll2);
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
                    if (vo.getLabelId() == selectClassVo.getLabelId()) {
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
        getSortCb(1).setText(filterClassifyAll1.getConfigValue());
        param1LabelId = filterClassifyAll1.getLabelId();
    }

    private void updateValidList2() {
        validClassifyList2 = new ArrayList<>();
        for (int i = 0; i < classifyList2.size(); i++) {
            if (classifyList2.get(i).getLabelId() > 0) {
                for (ClassifyVo vo : validClassifyList2) {
                    if (vo.getLabelId() == selectClassVo.getLabelId()) {
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
        getSortCb(2).setText(filterClassifyAll2.getConfigValue());
        param2LabelId = filterClassifyAll2.getLabelId();
    }

    private void updateValidList3() {
        validClassifyList3 = new ArrayList<>();
        for (int i = 0; i < classifyList3.size(); i++) {
            if (classifyList3.get(i).getLabelId() > 0) {
                for (ClassifyVo vo : validClassifyList3) {
                    if (vo.getLabelId() == selectClassVo.getLabelId()) {
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
        getSortCb(3).setText(filterClassifyAll3.getConfigValue());
        param3LabelId = filterClassifyAll3.getLabelId();
    }

    public interface OnCourseSelListener{
        void onCourseSel(CourseVo vo);
        void onSearch();
    }

    public void phoneClick() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:400-1007-727"));
        startActivity(intent);
    }
}
