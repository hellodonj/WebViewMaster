package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.DialogHelper;
import com.galaxyschool.app.wawaschool.common.RefreshUtil;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.UploadReourceHelper;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.db.dto.LocalCourseDTO;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.fragment.resource.CollectionFlagAdapterViewHelper;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfoListResult;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;
import com.galaxyschool.app.wawaschool.pojo.UploadParameter;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseType;
import com.galaxyschool.app.wawaschool.views.HalfRoundedImageView;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.ToolbarTopView;
import com.oosic.apps.iemaker.base.BaseUtils;
import com.oosic.apps.iemaker.base.ooshare.MyShareManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 显示个人资源库信息 isPick 区分复述课件之本机课件和个人资源库信息
 */
public class WawaCourseFragment extends ContactsListFragment {

    public static final String TAG = WawaCourseFragment.class.getSimpleName();
    public static final String RETELL_COURSE_TYPE="retell_course_type";
    private MyShareManager mShareManager = null;
    protected DialogHelper.WarningDialog mWarningDialog;
    private boolean isPick;
    private int curPosition;
    private int taskType;
    private int courseType;
    private List<LocalCourseDTO> localCourses;
    private int pickModel = 0;
    public interface RetellCourseType{
        //复述本机课件的类型
        int localCourseType=0;
        //复述个人资源库类型
        int personalCourseType=1;
    }

    public interface PickModelType{
        int SINGLE_MODEL = 0;
        int MULTIPLE_MODEL = 1;
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wawacourse, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadViews();
    }

    void initViews() {
        if (getArguments() != null) {
            isPick = getArguments().getBoolean(ActivityUtils.EXTRA_IS_PICK);
            taskType = getArguments().getInt(ActivityUtils.EXTRA_TASK_TYPE);
            courseType=getArguments().getInt(RETELL_COURSE_TYPE);
        }
        ToolbarTopView toolbarTopView = (ToolbarTopView) findViewById(R.id.toolbar_top_view);
        if (toolbarTopView != null) {
            toolbarTopView.getBackView().setVisibility(View.VISIBLE);
            toolbarTopView.getBackView().setOnClickListener(this);
            //isPick用来区分个人资源库与复述课件时进入个人资源库的状态
            if(isPick){
                String headerTitle=null;
                if(taskType == StudyTaskType.RETELL_WAWA_COURSE) {
                    headerTitle = getString(R.string.local_course_task);
                }else if(taskType == StudyTaskType.WATCH_WAWA_COURSE){
                    headerTitle = getString(R.string.n_create_task, getString(R.string.look_through_courseware));
                }else if (taskType==StudyTaskType.INTRODUCTION_WAWA_COURSE){
                    headerTitle=getString(R.string.appoint_course_no_point);
                }else if (taskType==StudyTaskType.TASK_ORDER){
                    headerTitle=getString(R.string.n_create_task,getString(R.string.pls_add_work_task));
                }else if (taskType == StudyTaskType.LISTEN_READ_AND_WRITE){
                    headerTitle=getString(R.string.appoint_course_no_point);
                    pickModel = PickModelType.MULTIPLE_MODEL;
                }
                toolbarTopView.getTitleView().setText(headerTitle);
                toolbarTopView.getCommitView().setVisibility(View.VISIBLE);
                toolbarTopView.getCommitView().setText(R.string.confirm);
                toolbarTopView.getCommitView().setTextColor(getResources().getColor(R.color.text_green));
                toolbarTopView.getCommitView().setOnClickListener(this);
            }else {
                toolbarTopView.getTitleView().setText(R.string.personal_resource_library);
            }

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
        PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);

        GridView gridView = (GridView) findViewById(R.id.gridview);
        if (gridView == null) {
            return;
        }
        int maxCount = 2;
        gridView.setNumColumns(maxCount);
        int padding = getActivity().getResources().getDimensionPixelSize(R.dimen.min_padding);
        gridView.setBackgroundColor(Color.WHITE);
        gridView.setPadding(padding, padding, padding, padding);
        AdapterViewHelper gridViewHelper = new CollectionFlagAdapterViewHelper(
                getActivity(), gridView,maxCount,padding) {
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

                //收藏标识
                ImageView collectImageView = (ImageView) view.findViewById(R.id.icon_collect);
                if (collectImageView != null){
                    //收藏
                    if (data.getType() == 1){
                        //显示
                        collectImageView.setVisibility(View.VISIBLE);
                    }else {
                        collectImageView.setVisibility(View.GONE);
                    }
                }

                //选择状态
                ImageView imageView = (ImageView) view.findViewById(R.id.resource_selector);
                if (isPick) {
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setSelected(item.isSelect());
                } else {
                    imageView.setVisibility(View.GONE);
                }

                if (courseType == RetellCourseType.localCourseType){
                    //缩略图
                    ImageView thumbnail = (HalfRoundedImageView) view.
                            findViewById(R.id.resource_thumbnail);
                    if (thumbnail != null) {
                        MyApplication.getThumbnailManager(getActivity()).
                                displayThumbnailWithDefault(data.getThumbnail(),thumbnail,R.drawable
                                        .default_cover);
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
                if (isPick) {
                    checkItem(info, position);
                }
            }
        };

        setCurrAdapterViewHelper(gridView, gridViewHelper);
    }

    void loadViews() {
        if (getCurrAdapterViewHelper().hasData()) {
            getCurrAdapterViewHelper().update();
        } else {
            loadTypeCourse();
//            //如果是加载个人资源库时加载动画
//            if (courseType!=RetellCourseType.localCourseType){
//                loadingData();
//            }
        }
    }

    public void updateView() {
        if (getCurrAdapterViewHelper() != null) {
            getCurrAdapterViewHelper().clearData();
        }

        loadTypeCourse();
    }
    public void loadTypeCourse(){
        if (courseType==RetellCourseType.localCourseType){
            loadLocaCourse();
            setStopPullDownState(true);
        }else {
            loadCourses();
        }
    }

    private void loadLocaCourse() {
        localCourses=new ArrayList<>();
        UserInfo userInfo = getUserInfo();
        List<LocalCourseDTO> datas;
        if(userInfo != null) {
            datas = LocalCourseDTO.getAllLocalCourses(getActivity(),
                    getMemeberId(), CourseType.COURSE_TYPE_LOCAL);
            List<NewResourceInfo> newResourceInfos = new ArrayList<NewResourceInfo>();
            if (datas != null && datas.size() > 0) {
                for (LocalCourseDTO data : datas) {
                    if (data != null) {
                        if (taskType == StudyTaskType.RETELL_WAWA_COURSE) {
                            //如果是复述课件来选取数据 不过滤有声相册
                            int type = BaseUtils.getCoursetType(data.getmPath());
                            if (ResType.RES_TYPE_COURSE_SPEAKER == type || type == ResType.RES_TYPE_ONEPAGE) {
                                localCourses.add(data);
                                NewResourceInfo newResourceInfo = new NewResourceInfo();
                                newResourceInfo.setTitle(data.getmTitle());
                                newResourceInfo.setThumbnail(BaseUtils.joinFilePath(data.getmPath(),
                                        BaseUtils.RECORD_HEAD_IMAGE_NAME));
                                newResourceInfo.setMicroId(String.valueOf(data.getmMicroId()));
                                newResourceInfo.setDescription(data.getmDescription());
                                newResourceInfo.setTotalTime((int)data.getmDuration());
                                newResourceInfos.add(newResourceInfo);
                            }
                        } else {
                            //区分本机课件 筛选掉有声相册
                            if (ResType.RES_TYPE_COURSE_SPEAKER == BaseUtils.getCoursetType(data.getmPath())) {
                                localCourses.add(data);
                                NewResourceInfo newResourceInfo = new NewResourceInfo();
                                newResourceInfo.setTitle(data.getmTitle());
                                newResourceInfo.setThumbnail(BaseUtils.joinFilePath(data.getmPath(),
                                        BaseUtils.RECORD_HEAD_IMAGE_NAME));
                                newResourceInfo.setMicroId(String.valueOf(data.getmMicroId()));
                                newResourceInfo.setDescription(data.getmDescription());
                                newResourceInfo.setTotalTime((int)data.getmDuration());
                                newResourceInfos.add(newResourceInfo);
                            }
                        }
                    }
                }
            }

                getCurrAdapterViewHelper().setData(newResourceInfos);
        }
    }
    public void loadCourses() {
        UserInfo userInfo = getUserInfo();
        Map<String, Object> params = new HashMap();
        if (userInfo != null) {
            //收藏时过滤课件不是自己的
            if (isPick){
                params.put("Author", userInfo.getMemberId());
            }
            params.put("MemberId", userInfo.getMemberId());
        }
        params.put("MType", String.valueOf(1));
        params.put("Pager", getPageHelper().getFetchingPagerArgs());
        DefaultPullToRefreshDataListener listener = new
                DefaultPullToRefreshDataListener<NewResourceInfoListResult>(
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
                if (getPageHelper().isFetchingPageIndex(result.getModel().getPager())) {
                    List<NewResourceInfo> list = result.getModel().getData();
                    if (list == null || list.size() <= 0) {
                        if (getPageHelper().isFetchingFirstPage()) {
                            getCurrAdapterViewHelper().clearData();
                            TipsHelper.showToast(getActivity(),
                                    getString(R.string.no_data));
                        } else {
                            TipsHelper.showToast(getActivity(),
                                    getString(R.string.no_more_data));
                        }
                        return;
                    }

                    List<NewResourceInfo> datas = new ArrayList<NewResourceInfo>();
                    for (NewResourceInfo info : list) {
                        if(taskType== StudyTaskType.INTRODUCTION_WAWA_COURSE){
                            if (info != null && info.isMicroCourse()) {
                                datas.add(info);
                            }
                        }else{
                            if (info != null && info.isMicroCourse()) {
                                datas.add(info);
                            }
                        }
                    }

                    if (getPageHelper().isFetchingFirstPage()) {
                        if( getCurrAdapterViewHelper().hasData()) {
                            getCurrAdapterViewHelper().clearData();
                        }
                    }

                    getPageHelper().updateByPagerArgs(result.getModel().getPager());
                    getPageHelper().setCurrPageIndex(
                            getPageHelper().getFetchingPageIndex());

                    RefreshUtil.getInstance().refresh(datas);

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
            }
        };
        listener.setShowLoading(false);
        postRequest(ServerUrl.PR_NEW_LOAD_MEDIA_LIST_URL, params, listener);
    }


    private void checkItem(NewResourceInfo info, int position) {
        if (info != null) {
            info.setIsSelect(!info.isSelect());
            if (info.isSelect()) {
                RefreshUtil.getInstance().addId(info.getId());
            }else {
                RefreshUtil.getInstance().removeId(info.getId());
            }
            curPosition = position;
        }
        if (pickModel == PickModelType.SINGLE_MODEL) {
            checkAllItems(false, position);
        }
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
                    RefreshUtil.getInstance().removeId(info.getId());
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

    /**
     * 点击确定按钮去发送当前选择的选项
     */
    public void chooseCourseToSend() {
        UploadParameter uploadParameter = null;
        NewResourceInfo cloudSelectData = null;
        cloudSelectData = getSelectData();
        if (cloudSelectData == null) {
            TipMsgHelper.ShowLMsg(getActivity(), R.string.no_file_select);
            return;
        }
        CourseData courseData = new CourseData();
        String resId = cloudSelectData.getMicroId();
        if (resId.contains("-")) {
            int i = resId.indexOf("-");
            resId = resId.substring(0, i);
        }
        courseData.id = Integer.parseInt(resId);
        courseData.nickname = cloudSelectData.getTitle();
        courseData.type = cloudSelectData.getResourceType();
        courseData.resourceurl = cloudSelectData.getResourceUrl();
        courseData.thumbnailurl=cloudSelectData.getThumbnail();
        courseData.code = cloudSelectData.getAuthorId();
        courseData.shareurl = cloudSelectData.getShareAddress();
        courseData.screentype = cloudSelectData.getScreenType();
        //复述或者看课件之本机课件
        if (courseType==RetellCourseType.localCourseType){
            uploadParameter = UploadReourceHelper.getUploadParameter(getUserInfo(), localCourses.get(curPosition).toLocalCourseInfo(), null, null, 1);
        }else {
            //复述-个人资源库
            uploadParameter = UploadReourceHelper.getUploadParameter(getUserInfo(), null, courseData, null, 1);
        }
        String headerTitle = getString(R.string.n_create_task, getString(R.string.look_through_courseware));
        if (uploadParameter != null) {
            uploadParameter.setTaskType(StudyTaskType.WATCH_WAWA_COURSE);
            if (getArguments() != null) {
                int taskType = getArguments().getInt(ActivityUtils.EXTRA_TASK_TYPE);
                uploadParameter.setTaskType(taskType);
            }
            //复述课件的选取方式
            if (taskType == StudyTaskType.RETELL_WAWA_COURSE){
                Intent intent = new Intent();
                uploadParameter.setLocalCourseDTO(localCourses.get(curPosition));
                intent.putExtra("uploadParameter",uploadParameter);
                getActivity().setResult(Activity.RESULT_OK,intent);
                getActivity().finish();
                return;
            }
            //用于导读时获取当前的upLoadParameter的数据

                IntroductionForReadCourseFragment fragmentCourse= (IntroductionForReadCourseFragment) getActivity().getSupportFragmentManager()
                                .findFragmentByTag(IntroductionForReadCourseFragment.TAG);
                fragmentCourse.setData(uploadParameter);
                if (courseType==RetellCourseType.personalCourseType){
                    fragmentCourse.setFromPersonalLibrary(true);
                }
                if (localCourses!=null){
                    fragmentCourse.setLocalCourseInfo(localCourses.get(curPosition).toLocalCourseInfo());
                }
                int num=getFragmentManager().getBackStackEntryCount();
                while (num > 1) {
                    popStack();
                    num = num - 1;
                }
//                for (int i=0;i<num;i++){
//                   popStack();
//                }
//                return;

//            popStack();
//            FragmentTransaction ft = getFragmentManager().beginTransaction();
//            Bundle args = getArguments();
//            if (args == null) {
//                args = new Bundle();
//            }
//            args.putSerializable(UploadParameter.class.getSimpleName(), uploadParameter);
//            args.putSerializable(ActivityUtils.EXTRA_HEADER_TITLE, headerTitle);
//            PublishStudyTaskFragment fragment = new PublishStudyTaskFragment();
//            fragment.setArguments(args);
//            ft.replace(R.id.activity_body, fragment, PublishStudyTaskFragment.TAG);
//            ft.addToBackStack(null);
//            ft.commit();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.toolbar_top_back_btn) {
            popStack();
        } else if (v.getId() == R.id.toolbar_top_commit_btn) {
            chooseCourseToSend();
        } else if (v.getId() == R.id.contacts_picker_confirm) {
            RefreshUtil.getInstance().clear();
            updateView();
        }
    }

    /**
     * 加载数据动画
     */
    public void loadingData(){
        showLoadingDialog(getString(R.string.downloading),false).show();
    }
}