package com.lqwawa.intleducation.module.discovery.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseActivity;
import com.lqwawa.intleducation.base.utils.StringUtils;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.NoScrollGridView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.module.discovery.adapter.ClassifyFilterAdapter;
import com.lqwawa.intleducation.module.discovery.vo.ClassifyVo;

import java.util.ArrayList;
import java.util.List;

public class SelectChildClassifyActivityEA extends MyBaseActivity {
    private static final String TAG = "SelectChildClassifyActivity";
    public static final int Rc_SelectChildClasify = 1002;
    private static final int CLS_ALL_VALUE = -100;

    private TopBar topBar;
    private NoScrollGridView topGridView;
    private NoScrollGridView param1GridView;
    private NoScrollGridView param2GridView;
    private NoScrollGridView param3GridView;
    private NoScrollGridView paramStatusGridView;
    private Button confirmBt;
    private ClassifyVo classifyVo;
    private String topLevel;
    private String param1Level = "";
    private String param2Level = "";
    private String param3Level = "";
    private int param1LabelId = -1;
    private int param2LabelId = -1;
    private int param3LabelId = -1;
    private ClassifyFilterAdapter topAdapter;
    private ClassifyFilterAdapter param1Adapter;
    private ClassifyFilterAdapter param2Adapter;
    private ClassifyFilterAdapter param3Adapter;
    private ClassifyFilterAdapter paramStatusAdapter;
    private List<ClassifyVo> topClassifyList = new ArrayList<>();
    private List<ClassifyVo> statusClassifyList = new ArrayList<>();

    ClassifyVo classifyAll = new ClassifyVo();

    public static void startForResult(Activity activity, ClassifyVo vo,
                                      String topLevel,
                                      int param1LabelId,
                                      int param2LabelId,
                                      int param3LabelId,
                                      String paramStatus,
                                      boolean showSearch,
                                      String setSubjectLabelId) {
        activity.startActivityForResult(new Intent(activity, SelectChildClassifyActivityEA.class)
                        .putExtra("classify", vo)
                        .putExtra("Level", topLevel)
                        .putExtra("param1LabelId", param1LabelId)
                        .putExtra("param2LabelId", param2LabelId)
                        .putExtra("param3LabelId", param3LabelId)
                        .putExtra("paramStatus", paramStatus)
                        .putExtra("showSearch", showSearch)
                        .putExtra("setSubjectLabelId", setSubjectLabelId)
                , Rc_SelectChildClasify);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_child_classify);
        assignViews();
        initData();
    }

    private void assignViews() {
        topBar = (TopBar) findViewById(R.id.top_bar);
        topBar.setBack(true);
        topBar.setTitle(getResources().getString(R.string.filter));
        if (getIntent().getBooleanExtra("showSearch", false)) {
            topBar.setRightFunctionImage1(R.drawable.search, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(activity, SearchActivity.class)
                            .putExtra("classify", classifyVo)
                            .putExtra("Level", getIntent().getStringExtra("Level"))
                            .putExtra("setSubjectLabelId",
                                    getIntent().getStringExtra("setSubjectLabelId")));
                }
            });
        }
        topGridView = (NoScrollGridView) findViewById(R.id.top_grid_view);
        param1GridView = (NoScrollGridView) findViewById(R.id.param1_grid_view);
        param2GridView = (NoScrollGridView) findViewById(R.id.param2_grid_view);
        param3GridView = (NoScrollGridView) findViewById(R.id.param3_grid_view);
        paramStatusGridView = (NoScrollGridView) findViewById(R.id.param_status_grid_view);
        confirmBt = (Button) findViewById(R.id.confirm_bt);
        classifyVo = (ClassifyVo) getIntent().getSerializableExtra("classify");
        topLevel = getIntent().getStringExtra("Level");
        param1LabelId = getIntent().getIntExtra("param1LabelId", -1);
        param2LabelId = getIntent().getIntExtra("param2LabelId", -1);
        param3LabelId = getIntent().getIntExtra("param3LabelId", -1);
        confirmBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Activity.RESULT_OK, new Intent()
                        .putExtra("topLevel", topLevel)
                        .putExtra("param1LabelId", param1LabelId)
                        .putExtra("param2LabelId", param2LabelId)
                        .putExtra("param3LabelId", param3LabelId)
                        .putExtra("paramStatus", paramStatusAdapter.getSelect().getLevel())
                        .putExtra("param1LabelName", param1Adapter.getSelect().getConfigValue())
                        .putExtra("param2LabelName", param2Adapter.getSelect().getConfigValue())
                        .putExtra("param3LabelName", param3Adapter.getSelect().getConfigValue()));
                finish();
            }
        });
        if (getIntent().getStringExtra("setSubjectLabelId") != null && !topLevel.equals(
                ClassifyIndexActivity.LQGISubClassifyValues.ExaminationForGoingAbroad)){
            findViewById(R.id.theme_lay).setVisibility(View.GONE);
        }
        ((TextView)findViewById(R.id.title_tv1)).setText(getResources().getString(R.string.type));
        ((TextView)findViewById(R.id.title_tv2)).setText(getResources()
                .getString(R.string.course_subject));
        ((TextView)findViewById(R.id.title_tv3)).setText(getResources().getString(R.string.theme));
    }

    private void initData() {
        topAdapter = new ClassifyFilterAdapter(activity, topSelectItemChangeListener);
        param1Adapter = new ClassifyFilterAdapter(activity, param1SelectItemChangeListener);
        param2Adapter = new ClassifyFilterAdapter(activity, param2SelectItemChangeListener);
        param3Adapter = new ClassifyFilterAdapter(activity, param3SelectItemChangeListener);
        paramStatusAdapter = new ClassifyFilterAdapter(activity, paramStatusSelectItemChangeListener);
        topGridView.setAdapter(topAdapter);
        param1GridView.setAdapter(param1Adapter);
        param2GridView.setAdapter(param2Adapter);
        param3GridView.setAdapter(param3Adapter);
        classifyAll.setConfigValue(getResources().getString(R.string.all));
        classifyAll.setLevel("");
        classifyAll.setLabelId(-100);

        topClassifyList.add(classifyAll);
        topClassifyList.addAll(classifyVo.getChildList());
        topAdapter.setData(topClassifyList);
        topAdapter.setSelectLevel(topLevel);

        if (topLevel.isEmpty()) {
            changeTopSelect(classifyAll);
        } else if (classifyVo != null && classifyVo.getChildList() != null) {
            for (int i = 0; i < classifyVo.getChildList().size(); i++) {
                if (classifyVo.getChildList().get(i).getLevel().equals(topLevel)) {
                    changeTopSelect(classifyVo.getChildList().get(i));
                }
            }
        }
        initStatusGrid();
    }

    private void initStatusGrid(){
        statusClassifyList.add(classifyAll);
        ClassifyVo vo1 = new ClassifyVo();
        vo1.setConfigValue(getResources().getString(R.string.course_status_0));
        vo1.setLevel("0");
        vo1.setLabelId(-201);
        statusClassifyList.add(vo1);
        ClassifyVo vo2 = new ClassifyVo();
        vo2.setConfigValue(getResources().getString(R.string.course_status_1));
        vo2.setLevel("1");
        vo2.setLabelId(-202);
        statusClassifyList.add(vo2);
        ClassifyVo vo3 = new ClassifyVo();
        vo3.setConfigValue(getResources().getString(R.string.course_status_2));
        vo3.setLevel("2");
        vo3.setLabelId(-203);
        statusClassifyList.add(vo3);
        paramStatusGridView.setAdapter(paramStatusAdapter);
        paramStatusAdapter.setData(statusClassifyList);

        String curLevel = getIntent().getStringExtra("paramStatus");
        if(curLevel.equals("-1")){
            curLevel = "";
        }
        paramStatusAdapter.setSelectLevel(curLevel);
    }

    private ClassifyFilterAdapter.OnSelectItemChangeListener topSelectItemChangeListener
            = new ClassifyFilterAdapter.OnSelectItemChangeListener() {
        @Override
        public void OnSelectItemChanged(Object obj) {
            changeTopSelect((ClassifyVo) obj);
        }
    };

    private void changeTopSelect(ClassifyVo vo) {
        if (vo == null) {
            return;
        }
        topLevel = vo.getLevel();
        List<ClassifyVo> pListTemp = new ArrayList<>();
        if (StringUtils.isValidString(vo.getLevel())) {//不是全部
            pListTemp.addAll(vo.getChildList());
        } else {//全部
            for (int i = 0; i < classifyVo.getChildList().size(); i++) {
                pListTemp.addAll(classifyVo.getChildList().get(i).getChildList());
            }
        }
        List<ClassifyVo> pList1 = new ArrayList<>();
        pList1.add(classifyAll);

        for (int i = 0; i < pListTemp.size(); i++) {
            pList1.add(pListTemp.get(i));
        }

        param1Adapter.setData(pList1);
        param1Level = param1Adapter.setSelectLevel(param1Level, param1LabelId);
        param1LabelId = param1Adapter.getSelect().getLabelId();
        param1Adapter.notifyDataSetChanged();

        List<ClassifyVo> pList2 = new ArrayList<>();
        pList2.add(classifyAll);
        boolean needLoadAll = true;
        if(param1LabelId != CLS_ALL_VALUE) {
            for (int i = 0; i < pList1.size(); i++) {
                if(pList1.get(i).getLabelId() == param1LabelId
                        && pList1.get(i).getChildList() != null) {
                    pList2.addAll(pList1.get(i).getChildList());
                    needLoadAll = false;
                    break;
                }
            }
        }
        if(needLoadAll) {
            for (int i = 0; i < pList1.size(); i++) {
                if (pList1.get(i).getChildList() != null) {
                    pList2.addAll(pList1.get(i).getChildList());
                }
            }
        }


        param2Adapter.setData(pList2);
        param2Level = param2Adapter.setSelectLevel(param2Level, param2LabelId);
        param2LabelId = param2Adapter.getSelect().getLabelId();
        param2Adapter.notifyDataSetChanged();

        List<ClassifyVo> pList3 = new ArrayList<>();
        pList3.add(classifyAll);
        ClassifyVo classifyVoParam2 = param2Adapter.getSelect();
        if (StringUtils.isValidString(classifyVoParam2.getLevel())) {//不是全部
            if (classifyVoParam2.getChildList() != null) {
                pList3.addAll(classifyVoParam2.getChildList());
            }
        } else {//全部
            for (int i = 0; i < pList2.size(); i++) {
                if (pList2.get(i).getChildList() != null) {
                    pList3.addAll(pList2.get(i).getChildList());
                }
            }
        }
        param3Adapter.setData(pList3);
        param3Level = param3Adapter.setSelectLevel(param3Level, param3LabelId);
        param3LabelId = param3Adapter.getSelect().getLabelId();
        param3Adapter.notifyDataSetChanged();
    }

    private ClassifyFilterAdapter.OnSelectItemChangeListener param1SelectItemChangeListener
            = new ClassifyFilterAdapter.OnSelectItemChangeListener() {
        @Override
        public void OnSelectItemChanged(Object obj) {
            changeParam1Select((ClassifyVo) obj);
        }
    };

    private void changeParam1Select(ClassifyVo vo) {
        if (vo == null) {
            return;
        }
        param1Level = vo.getLevel();
        param1LabelId = vo.getLabelId();
        List<ClassifyVo> pList2 = new ArrayList<>();
        pList2.add(classifyAll);
        boolean needLoadAll = true;
        List<ClassifyVo> pList1 = param1Adapter.getList();
        if(param1LabelId != CLS_ALL_VALUE) {
            for (int i = 0; i < param1Adapter.getCount(); i++) {
                if(pList1.get(i).getLabelId() == param1LabelId
                        && pList1.get(i).getChildList() != null) {
                    pList2.addAll(pList1.get(i).getChildList());
                    needLoadAll = false;
                    break;
                }
            }
        }
        if(needLoadAll) {
            for (int i = 0; i < pList1.size(); i++) {
                if (pList1.get(i).getChildList() != null) {
                    pList2.addAll(pList1.get(i).getChildList());
                }
            }
        }


        param2Adapter.setData(pList2);
        param2Level = param2Adapter.setSelectLevel(param2Level, param2LabelId);
        param2LabelId = param2Adapter.getSelect().getLabelId();
        param2Adapter.notifyDataSetChanged();

        List<ClassifyVo> pList3 = new ArrayList<>();
        pList3.add(classifyAll);
        ClassifyVo classifyVoParam2 = param2Adapter.getSelect();
        if (StringUtils.isValidString(classifyVoParam2.getLevel())) {//不是全部
            if (classifyVoParam2.getChildList() != null) {
                pList3.addAll(classifyVoParam2.getChildList());
            }
        } else {//全部
            for (int i = 0; i < pList2.size(); i++) {
                if (pList2.get(i).getChildList() != null) {
                    pList3.addAll(pList2.get(i).getChildList());
                }
            }
        }
        param3Adapter.setData(pList3);
        param3Level = param3Adapter.setSelectLevel(param3Level, param3LabelId);
        param3LabelId = param3Adapter.getSelect().getLabelId();
        param3Adapter.notifyDataSetChanged();
    }

    private ClassifyFilterAdapter.OnSelectItemChangeListener param2SelectItemChangeListener
            = new ClassifyFilterAdapter.OnSelectItemChangeListener() {
        @Override
        public void OnSelectItemChanged(Object obj) {
            changeParam2Select((ClassifyVo) obj);
        }
    };

    private void changeParam2Select(ClassifyVo vo) {
        if (vo == null) {
            return;
        }
        param2Level = vo.getLevel();
        param2LabelId = vo.getLabelId();
        List<ClassifyVo> pList3 = new ArrayList<>();
        pList3.add(classifyAll);
        ClassifyVo classifyVoParam2 = param2Adapter.getSelect();
        if (StringUtils.isValidString(classifyVoParam2.getLevel())) {//不是全部
            if (StringUtils.isValidString(topLevel)) {// 顶层不是全部
                if (classifyVoParam2.getChildList() != null) {
                    pList3.addAll(classifyVoParam2.getChildList());
                }
            } else {//顶层是全部
                List<ClassifyVo> pListTemp = new ArrayList<>();
                for (int i = 0; i < classifyVo.getChildList().size(); i++) {
                    pListTemp.addAll(classifyVo.getChildList().get(i).getChildList());
                }
                for (int i = 0; i < pListTemp.size(); i++) {
                    if (pListTemp.get(i).getLabelId() == vo.getLabelId()) {
                        pList3.addAll(pListTemp.get(i).getChildList());
                    }
                }
            }
        } else {//全部
            if (StringUtils.isValidString(topLevel)) {// 顶层不是全部
                for (int i = 0; i < param2Adapter.getCount(); i++) {
                    if (((ClassifyVo) param2Adapter.getItem(i)).getChildList() != null) {
                        pList3.addAll(((ClassifyVo) param2Adapter.getItem(i)).getChildList());
                    }
                }
            } else {//顶层是全部
                List<ClassifyVo> pListTemp = new ArrayList<>();
                for (int i = 0; i < classifyVo.getChildList().size(); i++) {
                    pListTemp.addAll(classifyVo.getChildList().get(i).getChildList());
                }
                for (int i = 0; i < pListTemp.size(); i++) {
                        pList3.addAll(pListTemp.get(i).getChildList());
                }
            }
        }
        param3Adapter.setData(pList3);
        param3Level = param3Adapter.setSelectLevel(param3Level, param3LabelId);
        param3LabelId = param3Adapter.getSelect().getLabelId();
        param3Adapter.notifyDataSetChanged();
    }

    private ClassifyFilterAdapter.OnSelectItemChangeListener param3SelectItemChangeListener
            = new ClassifyFilterAdapter.OnSelectItemChangeListener() {
        @Override
        public void OnSelectItemChanged(Object obj) {
            changeParam3Select((ClassifyVo) obj);
        }
    };

    private void changeParam3Select(ClassifyVo vo) {
        if (vo == null) {
            return;
        }
        param3Level = vo.getLevel();
        param3LabelId = vo.getLabelId();
    }

    private ClassifyFilterAdapter.OnSelectItemChangeListener paramStatusSelectItemChangeListener
            = new ClassifyFilterAdapter.OnSelectItemChangeListener() {
        @Override
        public void OnSelectItemChanged(Object obj) {
            //changeParam3Select((ClassifyVo) obj);
        }
    };
}
