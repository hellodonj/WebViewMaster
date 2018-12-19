package com.lqwawa.intleducation.module.discovery.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.utils.LogUtil;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.module.discovery.vo.ClassifyVo;
import com.lqwawa.intleducation.base.ui.MyBaseActivity;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XChen on 2016/11/13.
 * email:man0fchina@foxmail.com
 * 选择课程分类
 */
public class SelectClassifyActivity extends MyBaseActivity implements View.OnClickListener {
    private static final String TAG = "SelectClasifyActivity";
    public static final int Rc_SelectClasify = 1001;

    private TopBar topBar;
    private RadioGroup radioGroupClassify;
    private GridView gridViewSubClassify;

    private String initLevel;
    private List<ClassifyVo> classifyList;
    SubClassiyAdapter subClassiyAdapter;
    private boolean showSearch = true;

    public static void startForResult(Activity activity, String level, boolean showSearch) {
        activity.startActivityForResult(
                new Intent(activity, SelectClassifyActivity.class)
                        .putExtra("showSearch", showSearch).putExtra("level", level),
                Rc_SelectClasify);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_classify);

        topBar = (TopBar) findViewById(R.id.top_bar);
        radioGroupClassify = (RadioGroup) findViewById(R.id.classify_rg);
        gridViewSubClassify = (GridView) findViewById(R.id.common_gridview);
        showSearch = getIntent().getBooleanExtra("showSearch", true);
        initLevel = getIntent().getStringExtra("level");
        initViews();
        getData();
    }

    private void initViews() {
        setResult(Activity.RESULT_CANCELED);
        topBar.setBack(true);
        if (showSearch) {
            topBar.setRightFunctionImage2(R.drawable.search, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(activity, SearchActivity.class));
                }
            });
        }
        subClassiyAdapter = new SubClassiyAdapter(this);
        gridViewSubClassify.setAdapter(subClassiyAdapter);

        gridViewSubClassify.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ClassifyVo vo = (ClassifyVo) subClassiyAdapter.getItem(position);
                if (vo.getLevelName().equals(getString(R.string.all))) {
                    if (getRbIndex(radioGroupClassify.getCheckedRadioButtonId())
                            >= radioGroupClassify.getChildCount() - 1) {
                        ClassifyVo allVo = new ClassifyVo();
                        allVo.setLevel("");
                        allVo.setLevelName(getString(R.string.all));
                        setResult(Activity.RESULT_OK, new Intent().putExtra("Classify", allVo));
                    } else {
                        setResult(Activity.RESULT_OK, new Intent().putExtra("Classify",
                                classifyList.get(getRbIndex(radioGroupClassify.getCheckedRadioButtonId()))));
                    }
                } else {
                    setResult(Activity.RESULT_OK, new Intent().putExtra("Classify", vo));
                }
                finish();
            }
        });
    }

    @Override
    public void onClick(View view) {

    }

    private void getData() {
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetClassList);
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                ResponseVo<List<ClassifyVo>> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<List<ClassifyVo>>>() {
                        });
                if (result.getCode() == 0) {
                    classifyList = result.getData();
                    if (classifyList != null && classifyList.size() > 0) {
                        updateView();
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

    private void updateView() {
        int index = 0;
        boolean isSub = false;
        for (; index < classifyList.size() && index < 5; index++) {
            RadioButton radioButton = (RadioButton) findViewById(getRbResId(index));
            radioButton.setText(classifyList.get(index).getConfigValue());
            radioButton.setTag(classifyList.get(index));
            if (initLevel != null && initLevel.contains(classifyList.get(index).getLevel())) {
                radioButton.setChecked(true);
                updateSubClassifyView(index);
                isSub = true;
            }
        }

        RadioButton radioButton = (RadioButton) findViewById(getRbResId(5));
        ClassifyVo classifyAll = new ClassifyVo();
        classifyAll.setLevel("");
        classifyAll.setLevelName(getString(R.string.all));
        radioButton.setText(classifyAll.getLevelName());
        radioButton.setTag(classifyAll);
        if (!isSub) {
            radioButton.setChecked(true);
            updateSubClassifyView(index);
        }

        radioGroupClassify.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                updateSubClassifyView(getRbIndex(checkedId));
            }
        });
    }

    private int getRbIndex(int id) {
        if (id == R.id.rb0) {
            return 0;
        } else if (id == R.id.rb1) {
            return 1;
        } else if (id == R.id.rb2) {
            return 2;
        } else if (id == R.id.rb3) {
            return 3;
        } else if (id == R.id.rb4) {
            return 4;
        } else {
            return 5;
        }
    }

    private int getRbResId(int index) {
        switch (index) {
            case 0:
                return R.id.rb0;
            case 1:
                return R.id.rb1;
            case 2:
                return R.id.rb2;
            case 3:
                return R.id.rb3;
            case 4:
                return R.id.rb4;
            case 5:
            default:
                return R.id.rb5;
        }
    }

    private void updateSubClassifyView(int index) {
        if (index < classifyList.size()) {
            subClassiyAdapter.setData(classifyList.get(index).getChildList());
            ClassifyVo allVo = new ClassifyVo();
            allVo.setLevel(classifyList.get(index).getLevel());
            allVo.setLevelName(getString(R.string.all));
            subClassiyAdapter.insertData(allVo);
            subClassiyAdapter.notifyDataSetChanged();
        } else if (index == classifyList.size()) {
            subClassiyAdapter.setData(classifyList.get(0).getChildList());
            for (int i = 1; i < classifyList.size(); i++) {
                subClassiyAdapter.addData(classifyList.get(i).getChildList());
            }
            ClassifyVo allVo = new ClassifyVo();
            allVo.setLevel("");
            allVo.setLevelName(getString(R.string.all));
            subClassiyAdapter.insertData(allVo);
            subClassiyAdapter.notifyDataSetChanged();
        }
    }

    private class SubClassiyAdapter extends BaseAdapter {
        private List<ClassifyVo> list;
        Activity parentActivity;

        public SubClassiyAdapter(Activity activity) {
            parentActivity = activity;
            list = new ArrayList<ClassifyVo>();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ClassifyVo vo = list.get(position);
            if (convertView == null) {
                convertView = new LinearLayout(parentActivity);
                ((LinearLayout) convertView).setOrientation(LinearLayout.VERTICAL);
                convertView.setLayoutParams(new AbsListView.LayoutParams(
                        AbsListView.LayoutParams.MATCH_PARENT,
                        AbsListView.LayoutParams.MATCH_PARENT));
                TextView textView = new TextView(parentActivity);
                textView.setLines(1);
                textView.setId(0);
                textView.setPadding(8, 40, 8, 40);
                textView.setGravity(Gravity.CENTER);
                textView.setTextColor(
                        parentActivity.getResources().getColor(R.color.com_text_white));
                ((LinearLayout) convertView).addView(textView);
            }
            convertView.setBackgroundColor(
                    AppConfig.BaseConfig.ClassifybackColors[position % 6]);
            String name = vo.getLevelName() + "";
            name = name.trim();
            if (name.contains("|")) {
                name = name.substring(name.indexOf("|") + 1);
            }
            ((TextView) convertView.findViewById(0)).setText(name);
            return convertView;
        }

        /**
         * 下拉刷新设置数据
         *
         * @param list
         */
        public void setData(List<ClassifyVo> list) {
            if (list != null) {
                this.list = new ArrayList<ClassifyVo>(list);
            } else {
                this.list.clear();
            }
        }

        /**
         * 上拉加载更多，新增数据
         *
         * @param list
         */
        public void addData(List<ClassifyVo> list) {
            this.list.addAll(list);
        }


        /**
         * 上拉加载更多，新增数据
         *
         * @param vo
         */
        public void insertData(ClassifyVo vo) {
            this.list.add(0, vo);
        }
    }
}
