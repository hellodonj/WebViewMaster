package com.galaxyschool.app.wawaschool.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.CampusPatrolSplitCourseListActivity;
import com.galaxyschool.app.wawaschool.PictureBooksDetailActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.CampusPatrolUtils;
import com.galaxyschool.app.wawaschool.common.ScreenUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.CampusPatrolSchoolOutlineMaterial;
import com.galaxyschool.app.wawaschool.pojo.CampusPatrolSchoolOutlineMaterialListResult;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfoTag;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.TeacherDataStaticsInfo;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.lqwawa.client.pojo.MediaType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 校园巡查---校本课程页面。
 */
public class CampusPatrolSchoolBasedCourseListFragment extends ContactsListFragment {

    public static final String TAG = CampusPatrolSchoolBasedCourseListFragment.class.getSimpleName();

    private boolean isCampusPatrolTag;
    private String resourceName,resourceCountStr;
    private String startDate,endDate;
    private TeacherDataStaticsInfo info;
    private SchoolInfo schoolInfo;
    private int taskType = -1;
    protected CampusPatrolSchoolOutlineMaterialListResult resourceListResult;
    private int MAX_COLUMNS = 2;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.campus_patrol_school_based_course_list_fragment, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        refreshData();
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (getUserVisibleHint()) {
//            getPageHelper().clear();
//            loadViews();
//        }
    }

    private void refreshData(){
        getPageHelper().clear();
        loadViews();
    }

    private void updateTitleView(String countStr){
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            if (!isCampusPatrolTag) {
                textView.setText(resourceName);
            }else {
                textView.setText(resourceName+getString(R.string.media_num,countStr));
            }
        }
    }

    void initViews() {
        if (getArguments() != null) {
            isCampusPatrolTag = getArguments().getBoolean(CampusPatrolMainFragment.
            IS_CAMPUS_PATROL_TAG);
            resourceName = getArguments().getString(CampusPatrolMainFragment
                    .CAMPUS_PATROL_RESOURCE_NAME);
            resourceCountStr = getArguments().getString(CampusPatrolMainFragment
                    .CAMPUS_PATROL_RESOURCE_COUNT_STR);
            info = (TeacherDataStaticsInfo) getArguments().
                    getSerializable(TeacherDataStaticsInfo.class.getSimpleName());
            schoolInfo = (SchoolInfo) getArguments().
                    getSerializable(SchoolInfo.class.getSimpleName());
            startDate = getArguments().getString(CampusPatrolMainFragment.
                    CAMPUS_PATROL_SCREENING_START_DATE);
            endDate = getArguments().getString(CampusPatrolMainFragment.
                    CAMPUS_PATROL_SCREENING_END_DATE);
            taskType = getArguments().getInt(CampusPatrolMainFragment.
            CAMPUS_PATROL_TASK_TYPE);
        }
        updateTitleView(resourceCountStr);
        TextView textView = ((TextView) findViewById(R.id.contacts_header_right_btn));
        if (textView != null) {
            if (!isCampusPatrolTag) {
                textView.setVisibility(View.INVISIBLE);
            }else {
                //校园巡查逻辑
                textView.setVisibility(View.VISIBLE);
                textView.setText(getString(R.string.screening));
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityUtils.enterCampusPatrolPickerActivity(getActivity());
                    }
                });
            }
        }
        //刷新
        PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);

        GridView gridView = (GridView) findViewById(R.id.gridview);
        if (gridView == null) {
            return;
        }
        int padding = getActivity().getResources().getDimensionPixelSize(
                R.dimen.resource_gridview_padding);
        gridView.setNumColumns(2);
        gridView.setBackgroundColor(Color.WHITE);
        gridView.setPadding(padding, padding, padding, padding);
        AdapterViewHelper gridViewHelper = new AdapterViewHelper(
                getActivity(), gridView,R.layout.campus_patrol_school_based_course_grid_item) {
            @Override
            public void loadData() {
                loadCourses();
            }

            @Override
            public View getView(final int position, View convertView, final ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                final CampusPatrolSchoolOutlineMaterial data = (CampusPatrolSchoolOutlineMaterial)
                        getDataAdapter().getItem(position);
                if (data == null) {
                    return view;
                }
                ViewHolder holder = (ViewHolder) view.getTag();
                if (holder == null) {
                    holder = new ViewHolder();
                }
                holder.data = data;
                int min_padding = getResources().getDimensionPixelSize(R.dimen.min_padding);
                int itemSize = (ScreenUtils.getScreenWidth(getActivity()) -
                        min_padding * (MAX_COLUMNS + 1)) / MAX_COLUMNS;
                LinearLayout.LayoutParams params = null;
                FrameLayout frameLayout = (FrameLayout) view.findViewById(
                            R.id.resource_frameLayout);
                params = (LinearLayout.LayoutParams) frameLayout.getLayoutParams();
                params.width = itemSize - getActivity().getResources()
                                    .getDimensionPixelSize(R.dimen.resource_gridview_padding);
                params.height = params.width * 9 / 16;
                frameLayout.setLayoutParams(params);

                ImageView imageView = (ImageView) view.findViewById(R.id.media_thumbnail);
                if (imageView != null){
                    getThumbnailManager().displayImageWithDefault(
                            AppSettings.getFileUrl(data.getConvertUrl()),imageView,R.drawable
                                    .default_cover);
                }
                //标题
                TextView textView = (TextView) view.findViewById(R.id.media_name);
                if (textView != null){
                    textView.setText(data.getTitle());
                    textView.setCompoundDrawables(null,null,null,null);
                }

                //拆分
                textView = (TextView) view.findViewById(R.id.media_split_btn);
                if (textView != null){
                    if (data.isNeedToShowSplitPage()) {
                        textView.setVisibility(View.VISIBLE);
                    }else {
                        textView.setVisibility(View.GONE);
                    }
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //查看拆分
                            enterCampusPatrolSplitCourseListFragment(data);
                        }
                    });
                }
                view.setTag(holder);
                return view;
            }

            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                ViewHolder holder = (ViewHolder) view.getTag();
                if (holder == null || holder.data == null) {
                    return;
                }
                CampusPatrolSchoolOutlineMaterial data = (CampusPatrolSchoolOutlineMaterial)
                        holder.data;
                openCourse(data);
            }
        };

        setCurrAdapterViewHelper(gridView, gridViewHelper);
    }

    private void enterCampusPatrolSplitCourseListFragment(CampusPatrolSchoolOutlineMaterial data) {

        if (data == null){
            return;
        }
        Intent intent = new Intent(getActivity(), CampusPatrolSplitCourseListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(CampusPatrolMainFragment.CAMPUS_PATROL_RESOURCE_DATA,data);
        bundle.putString(CampusPatrolMainFragment.CAMPUS_PATROL_RESOURCE_ID,
                data.getNewResourceInfo().getMicroId());
        bundle.putString(CampusPatrolMainFragment.CAMPUS_PATROL_RESOURCE_NAME,
                data.getTitle());
        //media type
        bundle.putInt(CampusPatrolMainFragment.CAMPUS_PATROL_RESOURCE_TYPE,
                MediaType.MICROCOURSE);
        //分页信息
        if (data.getNewResourceTag() != null){
            bundle.putParcelable(NewResourceInfoTag.class.getSimpleName(),
                    data.getNewResourceTag());
        }
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void openCourse(CampusPatrolSchoolOutlineMaterial data) {

        if (data == null){
            return;
        }
        int resType = Integer.parseInt(data.getHeFeiResType()) % ResType.RES_TYPE_BASE;
        NewResourceInfo resourceInfo = data.getNewResourceInfo();
        if (resourceInfo != null) {
            //作者id
            if (info != null) {
                resourceInfo.setAuthorId(info.getTeacherId());
            }
            //目前只有从个人资源库和lq云板里面进入才是“发送”，其余均为“分享”
            int fromWhere = PictureBooksDetailActivity.FROM_OTHRE;
            if (resType == ResType.RES_TYPE_ONEPAGE) {
                ActivityUtils.openCourseDetail(getActivity(), resourceInfo,
                        fromWhere);
            } else if (resType == ResType.RES_TYPE_COURSE
                    || resType == ResType.RES_TYPE_COURSE_SPEAKER
                    || resType == ResType.RES_TYPE_OLD_COURSE) {
                ActivityUtils.openCourseDetail(getActivity(), resourceInfo,
                        fromWhere);
            }
        }
    }

    void loadViews() {
        loadCourses();
    }

    void loadCourses() {
        if (schoolInfo == null ||  info == null){
            return;
        }
        Map<String, Object> params = new HashMap();
        //学校Id,必填
        params.put("SchoolId", schoolInfo.getSchoolId());
        //教师ID,必填
        params.put("TeacherId", info.getTeacherId());
        //非必填，备用:PPT-1，PDF-2，图片-3，视频-4，音频-5， 蛙蛙微课-6
        if (taskType > 0 ) {
            params.put("SchoolMaterialType", taskType);
        }
        if (!TextUtils.isEmpty(startDate)) {//时间格式：2016-12-11
            params.put("StrStartTime", startDate);//统计开始时间,非必填。
        }
        if (!TextUtils.isEmpty(endDate)) {//时间格式：2016-12-11
            params.put("StrEndTime", endDate);//统计结束时间,非必填。
        }
        //分页参数,必填
        params.put("Pager", getPageHelper().getFetchingPagerArgs());
        DefaultPullToRefreshDataListener listener = new DefaultPullToRefreshDataListener
                <CampusPatrolSchoolOutlineMaterialListResult>
                (CampusPatrolSchoolOutlineMaterialListResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                CampusPatrolSchoolOutlineMaterialListResult result = getResult();
                if (result == null || !result.isSuccess()
                        || result.getModel() == null) {
                    getCurrAdapterViewHelper().clearData();
                    updateTitleView(String.valueOf(0));
                    TipsHelper.showToast(getActivity(),
                            getString(R.string.no_data));
                    return;
                }
                updateViews(result);
            }

        };
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_STATICS_SCHOOL_BASED_LIST_LIST_URL, params, listener);
    }

    private void updateViews(CampusPatrolSchoolOutlineMaterialListResult result) {
        if (getPageHelper().isFetchingPageIndex(result.getModel().getPager())) {
            int totalCount = getPageHelper().getTotalCount();
            List<CampusPatrolSchoolOutlineMaterial> list = result.getModel().getData();
            if (list == null || list.size() <= 0) {
                if (getPageHelper().isFetchingFirstPage()) {
                    getCurrAdapterViewHelper().clearData();
                    updateTitleView(String.valueOf(totalCount));
                    TipsHelper.showToast(getActivity(),
                            getString(R.string.no_data));
                } else {
                    TipsHelper.showToast(getActivity(),
                            getString(R.string.no_more_data));
                }
                return;
            }

            if (getPageHelper().isFetchingFirstPage()) {
                getCurrAdapterViewHelper().clearData();
            }
            getPageHelper().updateByPagerArgs(result.getModel().getPager());
            getPageHelper().setCurrPageIndex(
                    getPageHelper().getFetchingPageIndex());
            if (getCurrAdapterViewHelper().hasData()) {
                int position = getCurrAdapterViewHelper().getData().size();
                if (position > 0) {
                    position--;
                }
                getCurrAdapterViewHelper().getData().addAll(list);
                getCurrAdapterView().setSelection(position);
                resourceListResult.getModel().setData(getCurrAdapterViewHelper().getData());
            } else {
                getCurrAdapterViewHelper().setData(list);
                resourceListResult = result;
            }
            updateTitleView(String.valueOf(totalCount));
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data != null){
            if (resultCode == CampusPatrolPickerFragment.RESULT_CODE){
                if (requestCode == CampusPatrolPickerFragment.REQUEST_CODE){
                    this.startDate = CampusPatrolUtils.getStartDate(data);
                    this.endDate = CampusPatrolUtils.getEndDate(data);
                    refreshData();
                }
            }
        }
    }
}