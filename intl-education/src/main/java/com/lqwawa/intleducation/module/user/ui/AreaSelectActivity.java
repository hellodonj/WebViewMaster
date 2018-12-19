package com.lqwawa.intleducation.module.user.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseActivity;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.module.user.adapter.AreaAdapter;
import com.lqwawa.intleducation.module.user.tool.AreaHelper;
import com.lqwawa.intleducation.module.user.vo.AreaVo;
import java.util.List;
/**
 * Created by XChen on 2016/12/2.
 * email:man0fchina@foxmail.com
 * 选择区域 省、市、区/县
 */

public class AreaSelectActivity extends MyBaseActivity implements View.OnClickListener {
    private static final String TAG = "AreaSelectActivity";
    public static final int Rc_AreaSelect = 1005;

    private TopBar topBar;
    private ListView listViewProvince;
    private ListView listViewCity;
    private ListView listViewArea;

    private int levelNum = 1;
    private List<AreaVo> provinceList;
    private AreaAdapter provinceAdapter;
    private List<AreaVo> cityList;
    private AreaAdapter cityAdapter;
    private List<AreaVo> areaList;
    private AreaAdapter areaAdapter;
    private AreaVo provinceVo;
    private AreaVo cityVo;
    private AreaVo areaVo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_select);
        topBar = (TopBar)findViewById(R.id.top_bar);
        listViewProvince  = (ListView)findViewById(R.id.province_list_view);
        listViewCity  = (ListView)findViewById(R.id.city_list_view);
        listViewArea  = (ListView)findViewById(R.id.area_list_view);
        initViews();
    }

    private void initViews() {
        provinceAdapter = new AreaAdapter(this);
        cityAdapter = new AreaAdapter(this);
        areaAdapter = new AreaAdapter(this);
        listViewProvince.setAdapter(provinceAdapter);
        listViewCity.setAdapter(cityAdapter);
        listViewArea.setAdapter(areaAdapter);
        topBar.setBack(true);
        topBar.setLeftFunctionImage1(R.drawable.ic_back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (levelNum == 3 || levelNum == 2){
                    updateList(levelNum - 1);
                }else {
                    finish();
                }
            }
        });
        topBar.setTitle(activity.getResources().getString(R.string.select_area));

        listViewProvince.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                provinceVo = (AreaVo) provinceAdapter.getItem(position);
                updateList(provinceVo.getLevelNum() + 1);
            }
        });
        listViewCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cityVo = (AreaVo) cityAdapter.getItem(position);
                updateList(cityVo.getLevelNum() + 1);
            }
        });
        listViewArea.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                areaVo = (AreaVo) areaAdapter.getItem(position);
                setResult(Activity.RESULT_OK, new Intent().putExtra("areaVo", areaVo));
                finish();
            }
        });
        updateList(1);
    }

    private void updateList(int setlevelNum) {
        levelNum = setlevelNum;
        if (levelNum == 1) {
            listViewProvince.setVisibility(View.VISIBLE);
            listViewCity.setVisibility(View.INVISIBLE);
            listViewArea.setVisibility(View.INVISIBLE);
            provinceList = AreaHelper.getProvinceList();
            if (provinceList == null) {
                reloadAreaList();
            } else {
                provinceAdapter.setData(provinceList);
            }
            provinceAdapter.notifyDataSetChanged();
        } else if (levelNum == 2) {
            listViewProvince.setVisibility(View.INVISIBLE);
            listViewCity.setVisibility(View.VISIBLE);
            listViewArea.setVisibility(View.INVISIBLE);
            cityList = AreaHelper.getAreaSubList(provinceVo.getId(), levelNum);
            cityAdapter.setData(cityList);
            cityAdapter.notifyDataSetChanged();
        } else if (levelNum == 3) {
            listViewProvince.setVisibility(View.INVISIBLE);
            listViewCity.setVisibility(View.INVISIBLE);
            listViewArea.setVisibility(View.VISIBLE);
            areaList = AreaHelper.getAreaSubList(cityVo.getId(), levelNum);
            areaAdapter.setData(areaList);
            areaAdapter.notifyDataSetChanged();
        }
    }

    private void reloadAreaList() {
        showProgressDialog("获取地区列表中…");
        AreaHelper.reloadAreaList(new AreaHelper.OnReloadAreaListListener() {
            @Override
            public void onSuccess() {
                closeProgressDialog();
                provinceList = AreaHelper.getProvinceList();
                areaAdapter.setData(provinceList);
            }

            @Override
            public void onFailed() {
                closeProgressDialog();
                ToastUtil.showToast(activity, "获取地区列表失败");
            }
        });
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onBackPressed() {
        if (levelNum == 3 || levelNum == 2){
            updateList(levelNum - 1);
        }else {
            finish();
        }
    }
}
