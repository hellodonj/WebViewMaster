package com.galaxyschool.app.wawaschool.medias.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.CatalogLessonListActivity;
import com.galaxyschool.app.wawaschool.CatalogSelectActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.CampusPatrolUtils;
import com.galaxyschool.app.wawaschool.common.RefreshUtil;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.WatchWawaCourseResourceSplicingUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.CampusPatrolMainFragment;
import com.galaxyschool.app.wawaschool.fragment.CampusPatrolPickerFragment;
import com.galaxyschool.app.wawaschool.fragment.ContactsListFragment;
import com.galaxyschool.app.wawaschool.fragment.MediaListFragment;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.fragment.resource.NewResourcePadAdapterViewHelper;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.Calalog;
import com.galaxyschool.app.wawaschool.pojo.MediaInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfoListResult;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;
import com.galaxyschool.app.wawaschool.pojo.TeacherDataStaticsInfo;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.lqwawa.client.pojo.MediaType;
import com.lqwawa.client.pojo.ResourceInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * Created by E450 on 2016/12/23.
 */

public class MyTaskOrderFragment extends ContactsListFragment implements ISchoolResource{
    public static final String TAG = MyTaskOrderFragment.class.getSimpleName();
    private static final int MAX_PIC_BOOKS_PER_ROW = 2;
    private boolean isPick=true;
    public static final String IS_PICK="is_pick";
    private String startDate,endDate;
    private boolean isCampusPatrolTag;
    private TeacherDataStaticsInfo info;
    private String resourceCountStr;
    private boolean isFinish;

    private String bookPrimaryKey;
    private String bookName;
    private String schoolId;
    private String bookCatalogId;
    private int position;
    private List<Calalog> calalogs;
    private List<Calalog> calalogsNochildren;
    private int taskType;
    public String bookCatalogName,keyWord;
    private SchoolResourceContainerFragment mFragmentByTag;
    private int bookSource;
    //任务单的默认type是“7” 教辅材type是“8”
    private String defaultMType = "7";
    //教辅材料
    private boolean isTeachingMaterial;

    private int maxCount = 1;
    //是否是多选
    private boolean watchWawaCourseSupportMultiType;

    public MyTaskOrderFragment() {

    }

    public void setIsTeachingMarterialType(boolean isTeachingMaterial){
        this.isTeachingMaterial = isTeachingMaterial;

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        init();

        initCatalogsNochildren();
        return inflater.inflate(R.layout.fragment_my_remote_lq_course_list, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        if (getActivity()!=null){
            hideSoftKeyboard(getActivity());
        }
    }
    @Override
    public void loadDataLazy() {
//        initViews();
        if (isPick) {
            refreshData();
        }
    }

    private void init() {
        if (getArguments() != null) {
            bookSource = getArguments().getInt(SchoolResourceContainerFragment.Constants.EXTRA_BOOK_SOURCE);
            bookCatalogName = getArguments().getString(SchoolResourceContainerFragment.Constants.EXTRA_BOOK_CATALOG_NAME);
            bookPrimaryKey = getArguments().getString(SchoolResourceContainerFragment.Constants.EXTRA_BOOK_PRIMARY_KEY);
            schoolId = getArguments().getString(SchoolResourceContainerFragment.Constants.EXTRA_SCHOOL_ID);
            bookCatalogId = getArguments().getString(SchoolResourceContainerFragment.Constants.EXTRA_BOOK_CATALOG_ID);
            calalogs = (List<Calalog>) getArguments().getSerializable(CatalogLessonListActivity.BOOK_CATALOG_LIST);
            bookName = getArguments().getString(SchoolResourceContainerFragment.Constants.EXTRA_BOOK_NAME);
            isPick = getArguments().getBoolean(ActivityUtils.EXTRA_IS_PICK);
            taskType = getArguments().getInt(ActivityUtils.EXTRA_TASK_TYPE);
            watchWawaCourseSupportMultiType = getArguments().getBoolean(MediaListFragment
                    .EXTRA_SUPPORT_MULTI_TYPE_WATCH_WAWA_COURSE);
            maxCount = getArguments().getInt(ActivityUtils.EXTRA_SELECT_MAX_COUNT);
            if (watchWawaCourseSupportMultiType) {
                maxCount = getArguments().getInt(ActivityUtils.EXTRA_SELECT_MAX_COUNT);
                if (maxCount <= 0){
                    //控制资源最多选多少
                    maxCount = WatchWawaCourseResourceSplicingUtils.
                            controlResourcePickedMaxCount(MediaType.MICROCOURSE,maxCount,false);
                }
            }
            if (taskType == StudyTaskType.TASK_ORDER){
                maxCount = 1;
            }
        }
    }
    private void initCatalogsNochildren() {
        calalogsNochildren = new ArrayList<Calalog>();
        for (Calalog calalog : calalogs) {
            if (calalog.getChildren() != null && calalog.getChildren().size() > 0) {
                calalogsNochildren.addAll(calalog.getChildren());
            } else {
                calalogsNochildren.add(calalog);
            }
        }
        if (bookCatalogId != null && calalogsNochildren.size() > 0) {
            for (int i = 0; i < calalogsNochildren.size(); i++) {
                if (bookCatalogId.equals(calalogsNochildren.get(i).getId())) {
                    position = i;
                    break;
                }
            }
        }

    }
    private void loadIntent(){
       Bundle args= getArguments();
        if(args!=null){
            isPick= args.getBoolean(IS_PICK);
            isCampusPatrolTag = getArguments().getBoolean(CampusPatrolMainFragment
                    .IS_CAMPUS_PATROL_TAG);
            info = (TeacherDataStaticsInfo) getArguments().
                    getSerializable(TeacherDataStaticsInfo.class.getSimpleName());
            startDate = getArguments().getString(CampusPatrolMainFragment.
                    CAMPUS_PATROL_SCREENING_START_DATE);
            endDate = getArguments().getString(CampusPatrolMainFragment.
                    CAMPUS_PATROL_SCREENING_END_DATE);
            resourceCountStr = getArguments().getString(CampusPatrolMainFragment
                    .CAMPUS_PATROL_RESOURCE_COUNT_STR);
            isFinish = getArguments().getBoolean(MediaListFragment.EXTRA_IS_FINISH);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        refreshData();
    }

    private void updateTitleView(String countStr){
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            if (!isCampusPatrolTag) {
                textView.setVisibility(View.VISIBLE);
                textView.setText(getString(R.string.task_order));
            }else {
                textView.setText(getString(R.string.task_order)
                        +getString(R.string.media_num,countStr));
            }
        }
    }

    private void initViews(){
        //区分任务单和教辅材类型
        if (isTeachingMaterial){
            defaultMType = "8";
        }
//        updateTitleView(resourceCountStr);
//        ImageView imageView= (ImageView) findViewById(R.id.contacts_header_left_btn);
//        if(imageView!=null){
//            imageView.setVisibility(View.VISIBLE);
//            imageView.setOnClickListener(this);
//        }
//
//        TextView textView = ((TextView) findViewById(R.id.contacts_header_right_btn));
//        if (textView != null) {
//            if (!isCampusPatrolTag) {
//                if(isPick){
//                    textView.setVisibility(View.VISIBLE);
//                }else{
//                    textView.setVisibility(View.GONE);
//                }
//                textView.setText(getString(R.string.ok));
//                textView.setOnClickListener(this);
//            }else {
//                //校园巡查逻辑
//                textView.setVisibility(View.VISIBLE);
//                textView.setText(getString(R.string.screening));
//                textView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        ActivityUtils.enterCampusPatrolPickerActivity(getActivity());
//                    }
//                });
//            }
//        }

        final PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        //setStopPullUpState(true);
        setPullToRefreshView(pullToRefreshView);
        initGridview();
//        refreshData();
    }

    private void refreshData() {
        getPageHelper().clear();
        loadResourceList("");
    }



    @Override
    public void loadResourceList(String keyword) {
        this.keyWord = keyword;
        Map<String, Object> params = new ArrayMap<>();

        params.put("SchoolId", schoolId);
        params.put("BookId", bookPrimaryKey);
        if (getUserInfo() == null){
            return;
        }
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("MType", defaultMType);
        params.put("SectionId", bookCatalogId);
        params.put("KeyWord", keyword);
        params.put("Pager", getPageHelper().getFetchingPagerArgs());
        String serverUrl = null;
        if (this.bookSource == SchoolResourceContainerFragment.Constants.PLATFORM_BOOK) {
            serverUrl = ServerUrl.GET_PLATFORM_CATALOG_LESSON_LIST_URL;
        } else if (this.bookSource == SchoolResourceContainerFragment.Constants.SCHOOL_BOOK) {
            serverUrl = ServerUrl.GET_SCHOOL_CATALOG_LESSON_LIST_URL;
        } else if (this.bookSource == SchoolResourceContainerFragment.Constants.PERSONAL_BOOK) {
            serverUrl = ServerUrl.GET_MY_COLLECTION_CATALOG_LESSON_LIST_URL;
        }
        DefaultPullToRefreshDataListener listener =
                new DefaultPullToRefreshDataListener<NewResourceInfoListResult>(
                        NewResourceInfoListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        int totalCount = 0;
                        try {
                            JSONObject root = new JSONObject(jsonString);
                            JSONObject object = root.optJSONObject("Model");
                            if (object != null && object.has("Total")){
                                totalCount = object.optInt("Total");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        NewResourceInfoListResult result = getResult();
                        if (result == null || !result.isSuccess()
                                || result.getModel() == null) {
                            return;
                        }
                        updateNoteBookListView(result,totalCount);
                    }
                };
        RequestHelper.sendPostRequest(getActivity(),
                serverUrl, params, listener);
    }


    private void initGridview() {
        GridView gridView = (GridView) findViewById(R.id.grid_view);
        if (gridView != null) {
            gridView.setNumColumns(MAX_PIC_BOOKS_PER_ROW);
            AdapterViewHelper adapterViewHelper = new NewResourcePadAdapterViewHelper(
                    getActivity(), gridView) {
                @Override
                public void loadData() {
                    if (TextUtils.isEmpty(keyWord)) {
                        keyWord = "";
                    }
                    loadResourceList(keyWord);
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                   final  NewResourceInfo data = (NewResourceInfo) getDataAdapter().getItem
                           (position);
                    if (data == null) {
                        return view;
                    }
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;
                   final  ImageView   selectImageView = (ImageView) view.findViewById(R.id
                            .item_selector);
                    if(isPick){
                        selectImageView.setVisibility(View.VISIBLE);
                        if(data.isSelect()){
                            selectImageView.setImageResource(R.drawable.contacts_item_sel);
                        }else{
                            selectImageView.setImageResource(R.drawable.contacts_item_unsel);
                        }
                    }else{
                        selectImageView.setVisibility(View.GONE);
                    }
                    TextView fullMarkScoreView = (TextView) view.findViewById(R.id.tv_auto_mark);
                    if (!TextUtils.isEmpty(data.getPoint())){
                        fullMarkScoreView.setVisibility(View.VISIBLE);
                        fullMarkScoreView.setText(getString(R.string.str_auto_mark));
                    } else {
                        fullMarkScoreView.setVisibility(View.GONE);
                    }
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(isPick){
                                if (watchWawaCourseSupportMultiType){
                                    //多选
                                    if (!data.isSelect()) {
                                        int count = getSelectedDataCount();
                                        if (count > 0 && count >= maxCount) {
                                            TipMsgHelper.ShowLMsg(getActivity(), getString(R.string
                                                    .n_pick_max_count, String.valueOf(maxCount)));
                                            return;
                                        }
                                    }
                                } else {
                                    List<NewResourceInfo> list =  getCurrAdapterViewHelper().getData();
                                    for(NewResourceInfo data1 : list){
                                        if(data1.isSelect()&&data1!=data){
                                            data1.setIsSelect(false);
                                            RefreshUtil.getInstance().removeId(data1.getId());
                                            break;
                                        }
                                    }
                                }
                                //选择界面，点击选中或者取消
                                 data.setIsSelect(!data.isSelect());
                                if (data.isSelect()) {
                                    RefreshUtil.getInstance().addId(data.getId());
                                }else {
                                    RefreshUtil.getInstance().removeId(data.getId());
                                }
                                 getCurrAdapterViewHelper().update();
                            }else{
                                if (mFragmentByTag == null) {
                                    mFragmentByTag = (SchoolResourceContainerFragment) getActivity().getSupportFragmentManager().findFragmentByTag(SchoolResourceContainerFragment.TAG);
                                }
                                boolean flag = mFragmentByTag.isFromChoiceLib;
                                data.setIsQualityCourse(flag);
                                String collectId = mFragmentByTag.collectSchoolId;
                                if (!TextUtils.isEmpty(collectId)){
                                    data.setCollectionOrigin(collectId);
                                }
                                data.setIsVipSchool(mFragmentByTag.isVipSchool);
                               //非选择界面，跳转到拆分页
                                data.setIsFromSchoolResource(true);
                                data.setIsHideCollectBtn(false);
                                ActivityUtils.enterTaskOrderDetailActivity(getActivity(),data);
                            }
                        }
                    });
                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        return;
                    }
                    NewResourceInfo data = (NewResourceInfo) holder.data;
                }
            };
            setCurrAdapterViewHelper(gridView, adapterViewHelper);
        }
    }

    private void updateNoteBookListView(NewResourceInfoListResult result ,int totalCount) {
        List<NewResourceInfo> list = result.getModel().getData();
        if (getPageHelper().isFetchingFirstPage()) {
            if( getCurrAdapterViewHelper().hasData()){
                getCurrAdapterViewHelper().clearData();
            }
        }
        if (list == null || list.size() <= 0) {
            if (getPageHelper().isFetchingFirstPage()) {
                getCurrAdapterViewHelper().clearData();
                    TipsHelper.showToast(getActivity(),
                            getString(R.string.no_data));
//                updateTitleView(String.valueOf(totalCount));
            } else {
                TipsHelper.showToast(getActivity(),
                        getString(R.string.no_more_data));
                getPageHelper().setFetchingPageIndex(getPageHelper().getCurrPageIndex());
            }
            return;
        }
        //getPageHelper().updateByPagerArgs(result.getModel().getPager());
            getPageHelper().setCurrPageIndex(
                    getPageHelper().getFetchingPageIndex());
        RefreshUtil.getInstance().refresh(list);
        if ( getCurrAdapterViewHelper().hasData()) {
            getCurrAdapterViewHelper().getData().addAll(list);
            getCurrAdapterViewHelper().update();
        } else {
            getCurrAdapterViewHelper().setData(list);
        }
//        updateTitleView(String.valueOf(totalCount));
    }


    /**
     * 获得选取的资源数量
     * @return
     */
    private int getSelectedDataCount() {
        List dataList = getCurrAdapterViewHelper().getData();
        if (dataList != null && dataList.size() > 0) {
            List<NewResourceInfo> resultList = new ArrayList<>();
            for (Object data : dataList) {
                NewResourceInfo info = (NewResourceInfo) data;
                if (info != null && info.isSelect()) {
                    resultList.add(info);
                }
            }
            return resultList.size();
        }
        return 0;
    }

    @Override
    public void getSelectData() {
        if (watchWawaCourseSupportMultiType){
            List<MediaInfo> mediaInfoList = getSelectedData();
            if (mediaInfoList == null || mediaInfoList.size() == 0){
                TipMsgHelper.ShowMsg(getActivity(),R.string.pls_select_files);
                return;
            }
            //处理分页数据
            WatchWawaCourseResourceSplicingUtils.splicingSplitPageResources(getActivity(),
                    mediaInfoList, MediaType.TASK_ORDER);
            return;
        }
        List<NewResourceInfo> list = getCurrAdapterViewHelper().getData();
        if(list==null ||list.size()==0){
            TipMsgHelper.ShowLMsg(getActivity(),getString(R.string.pls_select_files));
            return;
        }
        NewResourceInfo selectData=null;
        for(NewResourceInfo data : list){
            if(data.isSelect()){
                selectData=data;
                break;
            }
        }
        if(selectData==null){
            TipMsgHelper.ShowLMsg(getActivity(), R.string.pls_select_files);
            return;
        }
//        IntroductionForReadCourseFragment fragment = (IntroductionForReadCourseFragment)
//        getFragmentManager().findFragmentByTag(IntroductionForReadCourseFragment.TAG);
//        fragment.setWorkOrderId(selectData.getMicroId());
//        fragment.setResourceUrl(selectData.getResourceUrl());
//        fragment.setConnectThumbnail( selectData.getThumbnail());
//        popStack();
        ArrayList<ResourceInfo> resourceInfo= new ArrayList<ResourceInfo>();
        ResourceInfo resource = new ResourceInfo();
        resource.setTitle(selectData.getTitle());
        resource.setImgPath(AppSettings.getFileUrl(selectData.getThumbnail()));
        resource.setResourcePath(AppSettings.getFileUrl(selectData.getResourceUrl()));
        resource.setShareAddress(selectData.getShareAddress());
        resource.setResourceType(selectData.getResourceType());
        String resId=selectData.getMicroId();
        if (resId.contains("-")){
            resource.setResId(resId);
        }else {
            resource.setResId(resId+"-"+selectData.getResourceType());
        }
        resource.setPoint(selectData.getPoint());
        resourceInfo.add(resource);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("resourseInfoList", resourceInfo);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        if (getActivity() != null) {
            getActivity().setResult(RESULT_OK, intent);
            getActivity().finish();
        }
    }

    private List<MediaInfo> getSelectedData() {
        List dataList = getCurrAdapterViewHelper().getData();
        if (dataList != null && dataList.size() > 0) {
            int mediaType = MediaType.TASK_ORDER;
            List<MediaInfo> resultList = new ArrayList<>();
            for (Object data : dataList) {
                NewResourceInfo newResourceInfo = (NewResourceInfo) data;
                if (newResourceInfo != null && newResourceInfo.isSelect()) {
                    //转换数据
                    MediaInfo mediaInfo = new MediaInfo();
                    mediaInfo.setId(newResourceInfo.getId());
                    //设置作者id
                    mediaInfo.setAuthorId(newResourceInfo.getAuthorId());
                    mediaInfo.setMicroId(newResourceInfo.getMicroId());
                    mediaInfo.setType(newResourceInfo.getType());
                    mediaInfo.setMediaType(mediaType);
                    mediaInfo.setResourceType(newResourceInfo.getResourceType());
                    mediaInfo.setPath(newResourceInfo.getResourceUrl());
                    mediaInfo.setThumbnail(newResourceInfo.getThumbnail());
                    mediaInfo.setShareAddress(newResourceInfo.getShareAddress());
                    mediaInfo.setTitle(newResourceInfo.getTitle());
                    mediaInfo.setUpdateTime(newResourceInfo.getUpdatedTime());
                    //我的收藏和个人空间区分，0-个人空间，1-我的收藏
                    mediaInfo.setCourseInfo(newResourceInfo.getCourseInfo());
                    mediaInfo.setPoint(newResourceInfo.getPoint());
                    resultList.add(mediaInfo);
                }
            }
            return resultList;
        }
        return null;
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
            }else if (requestCode == CatalogSelectActivity.REQUEST_CODE_SELECT_CATALOG) {
                Calalog calalog = (Calalog) data.getSerializableExtra(CatalogLessonListActivity.BOOK_CATALOG_ITEM);
                bookCatalogId = calalog.getId();
                bookCatalogName = calalog.getName();
                loadDataAgain();
            }
        }
    }
    /**
     * 下一节
     */
    public void nextSection() {
        if (position == calalogsNochildren.size() - 1) {
            TipsHelper.showToast(getActivity(), R.string.no_next_note);
        } else {
            position = position + 1;
            privousNextClickEvent(position);
        }
    }

    /**
     * 上一节
     */
    public void previousSection() {
        if (position == 0) {
            TipsHelper.showToast(getActivity(), R.string.no_privous_note);
        } else {
            position = position - 1;
            privousNextClickEvent(position);
        }
    }

    /**
     * 查看目录
     */
    @Override
    public void selectCatalogEvent() {
        Intent intent = new Intent();
        intent.setClass(getActivity(), CatalogSelectActivity.class);
        intent.putExtra(CatalogLessonListActivity.BOOK_CATALOG_LIST, (Serializable) calalogs);
        intent.putExtra(CatalogLessonListActivity.EXTRA_BOOK_NAME, bookName);
        if (isPick) {
            startActivityForResult(intent, CatalogSelectActivity.REQUEST_CODE_SELECT_CATALOG);
        } else {
            getActivity().startActivityForResult(intent, CatalogSelectActivity.REQUEST_CODE_SELECT_CATALOG);

        }
    }
    @Override
    public void privousNextClickEvent(int position) {
        if (calalogsNochildren != null && calalogsNochildren.size() > 0) {
            if (position < calalogsNochildren.size()) {
                Calalog calalog = calalogsNochildren.get(position);
                bookCatalogId = calalog.getId();
                bookCatalogName = calalog.getName();
                loadDataAgain();
            }
        }
    }

    private void loadDataAgain() {
       setTitle();
        getPageHelper().clear();
        loadResourceList("");
    }
    @Override
    public void setTitle() {
        if (mFragmentByTag == null) {
            mFragmentByTag = (SchoolResourceContainerFragment) getActivity().getSupportFragmentManager().findFragmentByTag(SchoolResourceContainerFragment.TAG);
        }
        if (mFragmentByTag != null) {
            if (isPick) {
                String title = getString(R.string.task_order);
                if (taskType == StudyTaskType.LISTEN_READ_AND_WRITE) {
                    title = getString(R.string.pls_add_work_task);
                }
                mFragmentByTag.mContactsHeaderTitle.setText(title);
            } else {
                mFragmentByTag.mContactsHeaderTitle.setText(bookCatalogName);
            }
        }
    }

    @Override
    public void clearSerachData() {
        getCurrAdapterViewHelper().clearData();
        getPageHelper().clear();
    }

    @Override
    public String getBookCatalogName() {
        return bookCatalogName;
    }
}
