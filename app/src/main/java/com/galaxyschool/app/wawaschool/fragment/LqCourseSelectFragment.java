package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.CampusPatrolUtils;
import com.galaxyschool.app.wawaschool.common.ScreenUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.fragment.resource.NewResourcePadAdapterViewHelper;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.MediaInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfoListResult;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.ToolbarTopView;
import com.lqwawa.client.pojo.MediaType;
import com.osastudio.common.utils.LQImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 显示个人资源库信息 isPick 区分复述课件之本机课件和个人资源库信息
 */
public class LqCourseSelectFragment extends ContactsListFragment {

    public static final String TAG = LqCourseSelectFragment.class.getSimpleName();
    private int curPosition;
    private PullToRefreshView pullToRefreshView;
    private ToolbarTopView toolbarTopView;
    private int type=-1;
    private String title="";
    public static String FROM_MEDIA_TYPE="media_type";
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lq_course_select, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getIntent();
        initViews();
        loadViews();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void getIntent(){
        Bundle bundle=getArguments();
        if (bundle!=null){
            type=bundle.getInt(FROM_MEDIA_TYPE);
            title=bundle.getString(ActivityUtils.EXTRA_HEADER_TITLE);
        }
    }
    void initViews() {
        toolbarTopView= (ToolbarTopView) findViewById(R.id.toolbar_top_view);
        if (toolbarTopView!=null){
            toolbarTopView.getTitleView().setText(title);
            toolbarTopView.getBackView().setVisibility(View.VISIBLE);
            toolbarTopView.getBackView().setOnClickListener(this);
        }
        TextView tvClear = (TextView) findViewById(R.id.contacts_picker_clear);
        if (tvClear != null) {
            tvClear.setOnClickListener(this);
            tvClear.setText(R.string.confirm);
        }
        TextView tvConfirm = (TextView) findViewById(R.id.contacts_picker_confirm);
        if (tvConfirm != null) {
            tvConfirm.setOnClickListener(this);
            tvConfirm.setText(R.string.cancel);
        }
        pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);

        GridView gridView = (GridView) findViewById(R.id.gridview);
        if (gridView == null) {
            return;
        }
        final int padding = getActivity().getResources().getDimensionPixelSize(R.dimen.min_padding);
        gridView.setNumColumns(2);
        gridView.setBackgroundColor(Color.WHITE);
        gridView.setPadding(padding, padding, padding, padding);
        if (type== MediaType.MICROCOURSE) {
            NewResourcePadAdapterViewHelper gridViewHelper = new NewResourcePadAdapterViewHelper(
                    getActivity(), gridView) {
                @Override
                public void loadData() {
                    loadTypeCourse();
                }

                @Override
                public View getView(final int position, View convertView, final ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    NewResourceInfo data = (NewResourceInfo) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;

                    NewResourceInfo item = (NewResourceInfo) holder.data;

                    ImageView imageView = (ImageView) view.findViewById(R.id.item_selector);
                    imageView.setVisibility(View.VISIBLE);
                    if (item != null && item.isSelect()) {
                        imageView.setSelected(true);
                    } else {
                        imageView.setSelected(false);
                    }
                    TextView textView = (TextView) view.findViewById(R.id.resource_title);
                    if (textView != null) {
                        if (data.getType() == 1) {
                            textView.setGravity(Gravity.CENTER_VERTICAL);
                            //已收藏
                            Drawable leftDrawable = getResources().getDrawable(
                                    R.drawable.icon_star);
                            leftDrawable.setBounds(0, 0, leftDrawable.getMinimumWidth(),
                                    leftDrawable.getMinimumHeight());
                            textView.setGravity(Gravity.CENTER);
                            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textView.getLayoutParams();
                            params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                            textView.setLayoutParams(params);
                            textView.setCompoundDrawables(leftDrawable, null, null, null);
                        } else {
                            textView.setCompoundDrawables(null, null, null, null);
                        }
                    }
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null || holder.data == null) {
                        return;
                    }
                    NewResourceInfo info = (NewResourceInfo) holder.data;
                    checkItem(info, position);
                }
            };
            setCurrAdapterViewHelper(gridView, gridViewHelper);
        }else {
            gridView.setVerticalSpacing(getResources().getDimensionPixelSize(
                    R.dimen.gridview_spacing));
            AdapterViewHelper helper = new AdapterViewHelper(
                    getActivity(), gridView, R.layout.video_grid_item) {

                @Override
                public void loadData() {
                    loadTypeCourse();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = convertView;
                    int min_padding = getResources().getDimensionPixelSize(R.dimen.min_padding);
                    int itemSize = (ScreenUtils.getScreenWidth(getActivity()) -
                            min_padding * (2 + 1)) / 2;

                    if (view == null) {
                        LayoutInflater inflater = (LayoutInflater) getActivity().
                                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        view = inflater.inflate(R.layout.video_grid_item, parent, false);
                    }
                    NewResourceInfo data = (NewResourceInfo) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;

                    LinearLayout.LayoutParams params = null;
                    FrameLayout frameLayout = (FrameLayout) view.findViewById(
                            R.id.resource_frameLayout);
                    params = (LinearLayout.LayoutParams) frameLayout.getLayoutParams();
                    if (frameLayout != null && params != null) {
                        params.width = itemSize;
                        params.height = params.width * 3 / 4;
                        frameLayout.setLayoutParams(params);
                    }
                    ImageView thumbnail = (ImageView) view.findViewById(R.id.media_thumbnail);
                    TextView name = (TextView) view.findViewById(R.id.media_name);
                    ImageView mediaCover = (ImageView) view.findViewById(R.id.media_cover);
                    //布局
                    if (!TextUtils.isEmpty(data.getThumbnail())) {
                        LQImageLoader.displayImage(AppSettings.getFileUrl(
                                data.getThumbnail()), thumbnail, R.drawable.default_cover);
                    } else {
                        thumbnail.setImageResource(R.drawable.default_cover);
                    }
                    //云端显示播放按钮
                    if (mediaCover != null){
                        mediaCover.setVisibility(View.VISIBLE);
                    }
                    name.setText(Utils.removeFileNameSuffix(data.getTitle()));
                    //视频、照片居中对齐。
                    name.setGravity(Gravity.CENTER);
                    ImageView imageView = (ImageView) view.findViewById(R.id.media_flag);
                    imageView.setVisibility(View.VISIBLE);
                    if (data.isSelect()) {
                        imageView.setImageResource(R.drawable.select);
                    } else {
                        imageView.setImageResource(R.drawable.unselect);
                    }
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    NewResourceInfo info = (NewResourceInfo) getData().get(position);
                    if (info == null) {
                        return;
                    }
                    checkItem(info, position);
                }
            };
            setCurrAdapterViewHelper(gridView, helper);
        }
    }


    void loadViews() {
        if (getCurrAdapterViewHelper().hasData()) {
            getCurrAdapterViewHelper().update();
        } else {
            loadTypeCourse();
            //如果是加载个人资源库时加载动画
            loadingData();
        }
    }


    public void loadTypeCourse(){
        if (pullToRefreshView != null) {
            pullToRefreshView.showRefresh();
        }
        loadCourses();
    }


    public void loadCourses() {
        UserInfo userInfo = getUserInfo();
        Map<String, Object> params = new HashMap();
        if (userInfo != null) {
            params.put("MemberId", userInfo.getMemberId());
        }
        StringBuilder sb = new StringBuilder();
        if (type== MediaType.MICROCOURSE) {
            sb.append("1").append(",").append("10");
            params.put("MType", sb.toString());
        }else {
            params.put("MType", String.valueOf(type));
        }
        params.put("Pager", getPageHelper().getFetchingPagerArgs());
        RequestHelper.RequestDataResultListener listener = new RequestHelper
                .RequestDataResultListener<NewResourceInfoListResult>(getActivity(),
                NewResourceInfoListResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                NewResourceInfoListResult result = getResult();
                if (result == null || !result.isSuccess()) {
                    return;
                }
                if (result.getModel() == null) {
                    return;
                }


                if (getPageHelper().isFetchingFirstPage()) {
                    getCurrAdapterViewHelper().clearData();
                }
                getPageHelper().updateByPagerArgs(result.getModel().getPager());
                getPageHelper().setCurrPageIndex(
                        getPageHelper().getFetchingPageIndex());

                if (getPageHelper().isFetchingPageIndex(result.getModel().getPager())) {
                    List<NewResourceInfo> list = result.getModel().getData();
                    List<NewResourceInfo> datas = new ArrayList<NewResourceInfo>();
                    if (list != null && list.size() > 0) {
                        //根据条件判断第一页是否有数据
                        if (getPageHelper().getFetchingPageIndex() == 0) {
                            setViewVisible(true);
                        }
                        getPageHelper().setCurrPageIndex(getPageHelper().getFetchingPageIndex());
                        for (NewResourceInfo info : list) {
                            if(info.getType() != 1){
                                datas.add(info);
                            }
                        }
                    }else {
                        //如果没有相应的数据显示textview
                        if (getPageHelper().getFetchingPageIndex() == 0) {
                            setViewVisible(false);
                            return;
                        }
                    }
                    if (getCurrAdapterViewHelper().hasData()) {
                        int position = getCurrAdapterViewHelper().getData().size();
                        if (position > 0) {
                            position--;
                        }
                        getCurrAdapterViewHelper().getData().addAll(datas);
                        getCurrAdapterView().setSelection(position);
                    } else {
                        getCurrAdapterViewHelper().setData(datas);
                    }
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (pullToRefreshView != null) {
                    pullToRefreshView.hideRefresh();
                }
                dismissLoadingDialog();
            }
        };
        listener.setShowLoading(false);
        postRequest(ServerUrl.PR_NEW_LOAD_MEDIA_LIST_URL, params, listener);
    }

    private void setViewVisible(boolean hasData){
        TextView noDataShow= (TextView) findViewById(R.id.tv_has_no_data);
        if (hasData){
            noDataShow.setVisibility(View.GONE);
            findViewById(R.id.ll_has_data_layout).setVisibility(View.VISIBLE);
        }else {
            if (type==MediaType.MICROCOURSE){
                noDataShow.setText(getString(R.string.personal_library_no_content_lq_course));
            }else {
                if (type==MediaType.VIDEO){
                    noDataShow.setText(getString(R.string.personal_library_no_content_video));
                }
            }
            noDataShow.setVisibility(View.VISIBLE);
            findViewById(R.id.ll_has_data_layout).setVisibility(View.GONE);
        }
    }

    private void checkItem(NewResourceInfo info, int position) {
        if (info != null) {
            info.setIsSelect(!info.isSelect());
            curPosition = position;
        }
        checkAllItems(false, position);
        getCurrAdapterViewHelper().update();
    }

    private void checkAllItems(boolean isCheck, int position) {
        List<NewResourceInfo> data = getCurrAdapterViewHelper().getData();
        if (data != null && data.size() > 0) {
            int size = data.size();
            for (int i = 0; i < size; i++) {
                NewResourceInfo info = data.get(i);
                if (info != null && i != position) {
                    info.setIsSelect(isCheck);
                }
            }
        }
    }

    public NewResourceInfo getSelectData() {
        if (getCurrAdapterViewHelper() != null) {
            List<NewResourceInfo> resourceInfos = getCurrAdapterViewHelper().getData();
            if (resourceInfos != null && resourceInfos.size() > 0) {
                if (curPosition >= 0 && curPosition < resourceInfos.size()) {
                    NewResourceInfo info = resourceInfos.get(curPosition);
                    if (info != null && info.isSelect()) {
                        return info;
                    }
                }
            }
        }
        return null;
    }



    @Override
    public void onClick(View v) {
        super.onClick(v);
      if (v.getId() == R.id.contacts_picker_clear) {
          //确认
            chooseCourseToSend();
        } else if (v.getId() == R.id.contacts_picker_confirm) {
          //取消
          popStack();
        }else if (v.getId() == R.id.toolbar_top_back_btn){
          popStack();
      }
    }

    /**
     * 加载数据动画
     */
    public void loadingData(){
        showLoadingDialog(getString(R.string.downloading),false).show();
    }

    /**
     * 点击确定按钮去发送当前选择的选项
     */
    public void chooseCourseToSend() {
        NewResourceInfo selectData=   getSelectData();
        if(selectData==null||selectData.isSelect()==false){
            TipMsgHelper.ShowLMsg(getActivity(), R.string.no_file_select);
            return;
        }
        CourseData courseData = new CourseData();
        courseData.id = Integer.parseInt(selectData.getMicroId());
        courseData.nickname = selectData.getTitle();
        courseData.type = selectData.getResourceType();
        courseData.resourceurl = selectData.getResourceUrl();
        courseData.thumbnailurl=selectData.getThumbnail();
        Intent intent =new Intent();
        intent.putExtra("Data_Type",NocLqselectFragment.DATA_TYPE_REMOTE);
        intent.putExtra("NOC_CourseData",courseData);
        getActivity().setResult(Activity.RESULT_OK,intent);
        finish();
    }
}