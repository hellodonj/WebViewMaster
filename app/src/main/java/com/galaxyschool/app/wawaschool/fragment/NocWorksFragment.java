package com.galaxyschool.app.wawaschool.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.Request;
import com.galaxyschool.app.wawaschool.NocEntryInfoActivity;
import com.galaxyschool.app.wawaschool.NocMatchInfoActivity;
import com.galaxyschool.app.wawaschool.NocWorkMoreActivity;
import com.galaxyschool.app.wawaschool.PictureBooksDetailActivity;
import com.galaxyschool.app.wawaschool.common.NocHelper;
import com.galaxyschool.app.wawaschool.common.ScreenUtils;
import com.galaxyschool.app.wawaschool.common.ShareUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.pojo.NocLePlayHelper;
import com.galaxyschool.app.wawaschool.pojo.NocEnterDetailArguments;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.fragment.resource.NewResourcePadAdapterViewHelper;
import com.lqwawa.lqbaselib.net.ThisStringRequest;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.NocWorkGroupTypeInterface;
import com.galaxyschool.app.wawaschool.views.MyGridView;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.oosic.apps.share.ShareInfo;
import com.oosic.apps.share.SharedResource;
import com.umeng.socialize.media.UMImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/11.
 */
public class NocWorksFragment extends ContactsListFragment {
    public static final String TAG = NocWorksFragment.class.getSimpleName();
    private static final int MAX_PIC_BOOKS_PER_ROW = 2;

    private MyGridView preSchoolGridView;
    private MyGridView primaryGridView;
    private MyGridView middleGridView;
    private MyGridView teacherGridView;
    private PullToRefreshView pullToRefreshView;
    private boolean shouldHiddenHeaderView = false;
    public   static boolean freshData=true;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_noc_works, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        freshData=true;
    }

    private void initViews() {
        if (getArguments() != null){
            //头布局隐藏
            shouldHiddenHeaderView = getArguments().getBoolean(HappyLearningFragment
                    .SHOULD_HIDDEN_HEADER_VIEW);
        }
        //头布局
        View headerView = findViewById(R.id.contacts_header_layout);
        if (headerView != null){
            if (shouldHiddenHeaderView){
                headerView.setVisibility(View.GONE);
            }else {
                headerView.setVisibility(View.VISIBLE);
            }
        }
        //NOC图片
        ImageView nocIcon = (ImageView) findViewById(R.id.noc_works_icon);
        if (nocIcon != null){
            LinearLayout.LayoutParams layoutParams=( LinearLayout.LayoutParams)
                    nocIcon.getLayoutParams();
            layoutParams.width= (ScreenUtils.getScreenWidth(getActivity()));
            layoutParams.height=layoutParams.width* 2/5;
            nocIcon.setLayoutParams(layoutParams);
            nocIcon.setOnClickListener(this);
        }

        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        textView.setText(R.string.noc_works_title);
        TextView rightBtn = (TextView) findViewById(R.id.contacts_header_right_btn);
        rightBtn.setText(R.string.activity_details);
        rightBtn.setVisibility(View.VISIBLE);
        rightBtn.setOnClickListener(this);
        pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        setStopPullUpState(true);
        setPullToRefreshView(pullToRefreshView);
        initPreschoolGridview();
        initPrimaryGridView();
        initMiddleGridView();
        initTeacherGridView();
        intTabTitle(R.id.preschool_more_layout,getString(R.string.pre_school_group));
        intTabTitle(R.id.primary_more_layout,getString(R.string.noc_primary_group));
        intTabTitle(R.id.middle_more_layout,getString(R.string.noc_middle_group));
        intTabTitle(R.id.teacher_more_layout,getString(R.string.noc_teacher_group));

        //参与大赛
        textView = (TextView) findViewById(R.id.tv_join);
        if (textView != null){
            textView.setOnClickListener(this);
        }

    }

    /**
     * 初始化每个tab,包含初始化标题。
     * @param layoutId
     * @param title
     */
    private void intTabTitle(int layoutId,String title) {
        View layout = findViewById(layoutId);
        if (layout != null){
            layout.setOnClickListener(this);
            TextView titleTextView = (TextView) layout.findViewById(R.id.title_text_view);
            if (titleTextView != null){
                titleTextView.setText(title);
            }
        }
    }

    private void initPrimaryGridView( ){
        primaryGridView= (MyGridView) findViewById(R.id.primary_gridview);
        initOtherGridview(primaryGridView);
    }

    private void initMiddleGridView( ){
        middleGridView = (MyGridView) findViewById(R.id.middle_gridview);
        initOtherGridview(middleGridView);
    }
    private void initTeacherGridView( ){
        teacherGridView = (MyGridView) findViewById(R.id.teacher_gridview);
        initOtherGridview(teacherGridView);
    }
    private void initPreschoolGridview() {
        preSchoolGridView = (MyGridView) findViewById(R.id.preschool_gridview);
        if (preSchoolGridView != null) {
            preSchoolGridView.setNumColumns(MAX_PIC_BOOKS_PER_ROW);
            AdapterViewHelper adapterViewHelper = new NewResourcePadAdapterViewHelper(
                    getActivity(), preSchoolGridView) {
                @Override
                public void loadData() {
                    loadDatas();
                }


                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        return;
                    }
                    NewResourceInfo data = (NewResourceInfo) holder.data;
                    if (data != null) {
                        NocHelper.prepareEnterNocDetail(data,getActivity());
                    }
                }
            };
            setCurrAdapterViewHelper(preSchoolGridView, adapterViewHelper);
        }
    }
    private void initOtherGridview(MyGridView gridView) {
        if (gridView != null) {
            gridView.setNumColumns(MAX_PIC_BOOKS_PER_ROW);
            gridView.setTag(gridView.getId());
            AdapterViewHelper adapterViewHelper = new NewResourcePadAdapterViewHelper(
                    getActivity(), gridView) {
                @Override
                public void loadData() {
                    loadDatas();
                }


                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        return;
                    }
                    NewResourceInfo data = (NewResourceInfo) holder.data;
                    if (data != null) {
                        NocHelper.prepareEnterNocDetail(data,getActivity());
                    }
                }
            };
            addAdapterViewHelper(gridView.getTag().toString(), adapterViewHelper);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.preschool_more_layout:
                enterMoreNoticeBookEvent(getString(R.string.noc_preschool_group),1);
                break;
            case R.id.primary_more_layout:
                enterMoreNoticeBookEvent(getString(R.string.noc_primary_group),2);
                break;
            case R.id.middle_more_layout:
                enterMoreNoticeBookEvent(getString(R.string.noc_middle_group),3);
                break;
            case R.id.teacher_more_layout:
                enterMoreNoticeBookEvent(getString(R.string.noc_teacher_group),4);
                break;

            case R.id.noc_works_icon://NOC图片
            case R.id.contacts_header_right_btn :
                enterMatchDetail();
                break;

            case R.id.tv_join://参与大赛
                joinGame();
                break;
        }
    }

    private void joinGame() {
        if (isLogin()) {
            NocResourceInfoFragment. backArgs=NocResourceInfoFragment.BACK_NOC_ALL_WORKS_PAGE;
            Intent intent = new Intent(getActivity(), NocEntryInfoActivity.class);
            startActivity(intent);
        }else {
            ActivityUtils.enterLogin(getActivity(),false);
        }
    }

    private void enterMatchDetail(){
        Intent intent = new Intent(getActivity(), NocMatchInfoActivity.class);
        startActivity(intent);
    }

    private void enterMoreNoticeBookEvent(String title,int groupType) {
        Intent intent = new Intent(getActivity(), NocWorkMoreActivity.class);
        intent.putExtra("title",title);
        intent.putExtra("groupType",groupType);
        startActivity(intent);
    }
    @Override
    public void onResume() {
        super.onResume();
        if(freshData){
            freshData=false;
            loadViews();
        }
    }

    private void loadViews() {
        loadDatas();
        pullToRefreshView.showRefresh();
    }


    private void loadDatas() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("pageIndex", getPageHelper().getFetchingPageIndex());
            jsonObject.put("pageSize", getPageHelper().getPageSize());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringBuilder builder = new StringBuilder();
        builder.append("?j=" + jsonObject.toString());
        ThisStringRequest request = new ThisStringRequest(
                Request.Method.GET, ServerUrl.GET_NOC_PUBLIC_WORK + builder.toString(), new Listener<String>() {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                parseData(jsonString);
            }

            @Override
            public void onError(NetroidError error) {
                if (getActivity() == null) {
                    return;
                }
                TipMsgHelper.ShowMsg(getActivity(), getString(R.string.network_error));
            }

            @Override
            public void onFinish() {
                if (getActivity() == null) {
                    return;
                }
                super.onFinish();
                pullToRefreshView.hideRefresh();
            }
        });
        request.addHeader("Accept-Encoding", "*");
        request.start(getActivity());
    }

    private void parseData(String jsonString) {
        if (jsonString != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                if (jsonObject != null) {
                    int code = jsonObject.optInt("Code");
                    if (code == 0) {
                        JSONArray jsonArray = jsonObject.optJSONArray("data");
                        if (jsonArray != null&&jsonArray.length()>0) {
                            List<NewResourceInfo> listPreschool = new ArrayList<NewResourceInfo>();
                            List<NewResourceInfo> listPrimary = new ArrayList<NewResourceInfo>();
                            List<NewResourceInfo> listMiddle = new ArrayList<NewResourceInfo>();
                            List<NewResourceInfo> listTeacher = new ArrayList<NewResourceInfo>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = (JSONObject) jsonArray.get(i);
                                NewResourceInfo item=  NocHelper.pase2NewResourceInfo(object);
                                if (object.optInt("groupType") == NocWorkGroupTypeInterface.GROUP_TYPE_PRESCHOOL) {
                                    listPreschool.add(item);//学前组
                                } else if (object.optInt("groupType") == NocWorkGroupTypeInterface.GROUP_TYPE_PRIMARY) {
                                    listPrimary.add(item);//小学组
                                } else if (object.optInt("groupType") == NocWorkGroupTypeInterface.GROUP_TYPE_MIDDLE) {
                                    listMiddle.add(item);//中学组
                                } else if (object.optInt("groupType") == NocWorkGroupTypeInterface.GROUP_TYPE_TEACHER) {
                                    listTeacher.add(item);//教师组
                                }
                            }
                            getCurrAdapterViewHelper().setData(listPreschool);
                            getAdapterViewHelper(primaryGridView.getTag().toString()).setData(listPrimary);
                            getAdapterViewHelper(middleGridView.getTag().toString()).setData(listMiddle);
                            getAdapterViewHelper(teacherGridView.getTag().toString()).setData(listTeacher);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == ActivityUtils.REQUEST_CODE_RETURN_REFRESH) {
                Bundle bundle = data.getExtras();
                if (bundle.getBoolean(ActivityUtils.REQUEST_CODE_NEED_TO_REFRESH)){
                    pageHelper.setFetchingPageIndex(0);
                    freshData = true;
                }
            }
        }
    }
}
