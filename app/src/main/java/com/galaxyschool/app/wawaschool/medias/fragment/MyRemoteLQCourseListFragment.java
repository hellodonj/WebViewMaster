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
import com.galaxyschool.app.wawaschool.PictureBooksDetailActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.RefreshUtil;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.WatchWawaCourseResourceSplicingUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.ContactsListFragment;
import com.galaxyschool.app.wawaschool.fragment.MediaListFragment;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.fragment.resource.NewResourcePadAdapterViewHelper;
import com.galaxyschool.app.wawaschool.medias.fragment.SchoolResourceContainerFragment.Constants;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.Calalog;
import com.galaxyschool.app.wawaschool.pojo.MediaInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfoListResult;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.lqwawa.client.pojo.MediaType;
import com.lqwawa.client.pojo.ResourceInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * 远端的LQ课件列表,旨在解耦。
 */
public class MyRemoteLQCourseListFragment extends ContactsListFragment
        implements SchoolResourceContainerFragment.Constants,ISchoolResource {
    public static final String TAG = "MyRemoteLQCourseListFra";
    private int bookSource;
    private String bookPrimaryKey;
    private String bookName;
    private String schoolId;
    private String originSchoolId;
    private String bookCatalogId;
    private NewResourceInfoListResult resourceListResult;
    private List<Calalog> calalogs;
    private List<Calalog> calalogsNochildren;
    private int position;
    private boolean isPick;
    private int curPosition;
    private int taskType;
    public String bookCatalogName,keyWord;
    private SchoolResourceContainerFragment mFragmentByTag;
    private int maxCount = 1;
    private boolean watchWawaCourseSupportMultiType;//看课件
    private boolean isFromStudyTask;
    private boolean isLectureCourse;

    public void setLectureCourse(boolean isLectureCourse){
        this.isLectureCourse = isLectureCourse;
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
    }

    @Override
    public void loadDataLazy() {
        getPageHelper().clear();
        loadResourceList();
    }


    private void init() {
        if (getArguments() != null) {
            bookCatalogName = getArguments().getString(SchoolResourceContainerFragment.Constants.EXTRA_BOOK_CATALOG_NAME);
            bookSource = getArguments().getInt(SchoolResourceContainerFragment.Constants.EXTRA_BOOK_SOURCE);
            bookPrimaryKey = getArguments().getString(SchoolResourceContainerFragment.Constants.EXTRA_BOOK_PRIMARY_KEY);
            schoolId = getArguments().getString(SchoolResourceContainerFragment.Constants.EXTRA_SCHOOL_ID);
            bookCatalogId = getArguments().getString(Constants.EXTRA_BOOK_CATALOG_ID);
            calalogs = (List<Calalog>) getArguments().getSerializable(CatalogLessonListActivity.BOOK_CATALOG_LIST);
            bookName = getArguments().getString(SchoolResourceContainerFragment.Constants.EXTRA_BOOK_NAME);
            originSchoolId = getArguments().getString(Constants.EXTRA_ORIGIN_SCHOOL_ID);
            isPick = getArguments().getBoolean(ActivityUtils.EXTRA_IS_PICK);
            taskType = getArguments().getInt(ActivityUtils.EXTRA_TASK_TYPE);
            isFromStudyTask = getArguments().getBoolean(MediaListFragment.EXTRA_SUPPORT_MULTI_TYPE_WATCH_WAWA_COURSE);
            if (isFromStudyTask) {
                maxCount = getArguments().getInt(ActivityUtils.EXTRA_SELECT_MAX_COUNT);
            }
        }
    }

    private void initCatalogsNochildren() {
        calalogsNochildren = new ArrayList<Calalog>();
        if (calalogs == null || calalogs.size() == 0){
            return;
        }
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

    private void initViews() {
        //看课件多类型
        watchWawaCourseSupportMultiType = WatchWawaCourseResourceSplicingUtils.
                watchWawaCourseSupportMultiType(getArguments());
        if (watchWawaCourseSupportMultiType && maxCount == 0){
            //控制资源最多选多少
            maxCount = WatchWawaCourseResourceSplicingUtils.
                    controlResourcePickedMaxCount(MediaType.MICROCOURSE, maxCount, false);
        }
        //如果是复述课件 课件只能够选择一个
        if (taskType == StudyTaskType.RETELL_WAWA_COURSE){
            maxCount = 1;
        }
        PullToRefreshView pullToRefreshView = (PullToRefreshView)
                findViewById(R.id.contacts_pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);

        GridView listView = (GridView) findViewById(R.id.grid_view);
        AdapterViewHelper adapterViewHelper = null;
        if (listView != null) {
            //            if (isPick){
            listView.setNumColumns(2);
            //设置与搜索栏的距离
//            int  padding = getActivity().getResources().getDimensionPixelSize(R.dimen.vertical_space);
//            listView.setPadding(0,padding,0,padding);
            listView.setBackgroundColor(getResources().getColor(R.color.text_white));
            adapterViewHelper = new NewResourcePadAdapterViewHelper(getActivity(), listView) {
                @Override
                public void loadData() {
                    if (TextUtils.isEmpty(keyWord)) {
                        keyWord = "";
                    }
                    loadResourceList(keyWord);
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

                    TextView tvMark = (TextView) view.findViewById(R.id.tv_auto_mark);
                    if (TextUtils.equals("1",data.getResProperties())){
                        tvMark.setVisibility(View.VISIBLE);
                    }else {
                        tvMark.setVisibility(View.GONE);
                    }

                    ImageView imageView = (ImageView) view.findViewById(R.id.item_selector);
                    if (isPick) {
                        imageView.setVisibility(View.VISIBLE);
                        if (item.isSelect()) {
                            imageView.setSelected(true);
                        } else {
                            imageView.setSelected(false);
                        }
                    } else {
                        imageView.setVisibility(View.GONE);
                    }
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null || holder.data == null) {
                        return;
                    }
                    NewResourceInfo data = (NewResourceInfo) holder.data;
                    if (isPick) {
                        //看课件逻辑
                        if (watchWawaCourseSupportMultiType){
                            if (!data.isSelect()) {
                                int count = getSelectedDataCount();
                                if (count > 0 && count >= maxCount) {
                                    //单选模式
                                    if (maxCount == 1) {
                                        checkAllItems(false,position);
                                    }else {
                                        TipMsgHelper.ShowLMsg(getActivity(), getString(R.string
                                                .n_pick_max_count, String.valueOf(maxCount)));
                                        return;
                                    }
                                }
                            }
                            //多选
                            data.setIsSelect(!data.isSelect());
                            //当前页数据
                            curPosition = position;
                            if (data.isSelect()) {
                                RefreshUtil.getInstance().addId(data.getId());
                            }else {
                                RefreshUtil.getInstance().removeId(data.getId());
                            }
                            getCurrAdapterViewHelper().update();
                        }else {
                            checkItem(data, position);
                        }
                    } else {
                        if (data != null) {
                            //打开任务单进入详情页
                            data.setIsFromSchoolResource(true);
                            data.setIsHideCollectBtn(false);
                            if (mFragmentByTag == null) {
                                mFragmentByTag = (SchoolResourceContainerFragment) getActivity().getSupportFragmentManager().findFragmentByTag(SchoolResourceContainerFragment.TAG);
                            }
                            boolean flag = mFragmentByTag.isFromChoiceLib;
                            if (!flag) {
                                data.setHideDownLoadBtn(!mFragmentByTag.isTeacherInSchool);
                            }
                            data.setIsQualityCourse(flag);
                            String collectId = mFragmentByTag.collectSchoolId;
                            if (!TextUtils.isEmpty(collectId)){
                                data.setCollectionOrigin(collectId);
                            }
                            data.setIsVipSchool(mFragmentByTag.isVipSchool);


                            if (data.getResourceType() == 23) {
                                ActivityUtils.enterTaskOrderDetailActivity(getActivity()
                                        , data);
                                return;
                            }
                            if (bookSource == SchoolResourceContainerFragment.Constants.SCHOOL_BOOK) {
                                if (!TextUtils.isEmpty(originSchoolId) && !TextUtils.isEmpty(schoolId)) {
                                    ActivityUtils.openPictureDetailActivity(getActivity(), data,
                                            originSchoolId, schoolId, true);
                                } else {
                                    ActivityUtils.openPictureDetailActivity(getActivity(),
                                            data, schoolId, null, true);
                                }
                            } else {
                                if (data.isMicroCourse() || data.isOnePage()) {
                                    ActivityUtils.openPictureDetailActivity(getActivity(),
                                            data, PictureBooksDetailActivity
                                                    .FROM_MY_BOOK_SHELF, true);
                                }
                            }
                        }
                    }
                }
            };

            setCurrAdapterViewHelper(listView, adapterViewHelper);
        }
    }


    protected void loadResourceList() {
        loadResourceList("");
    }

    @Override
    public void loadResourceList(String keyword) {
        this.keyWord = keyword;
        Map<String, Object> params = new ArrayMap<>();

        params.put("SchoolId", schoolId);
        params.put("BookId", bookPrimaryKey);
        if (getUserInfo() == null) {
            return;
        }
        params.put("MemberId", getUserInfo().getMemberId());
        if (isLectureCourse) {
            params.put("MType", "12");
        } else {
            params.put("MType", "6");
        }
        params.put("SectionId", bookCatalogId);
        params.put("KeyWord", keyword);
        params.put("Pager", getPageHelper().getFetchingPagerArgs());
        DefaultPullToRefreshDataListener listener =
                new DefaultPullToRefreshDataListener<NewResourceInfoListResult>(
                        NewResourceInfoListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()
                                || getResult().getModel() == null) {
                            return;
                        }
                        updateResourceListView(getResult());
                    }
                };
        String serverUrl = null;
        if (this.bookSource == SchoolResourceContainerFragment.Constants.PLATFORM_BOOK) {
            serverUrl = ServerUrl.GET_PLATFORM_CATALOG_LESSON_LIST_URL;
        } else if (this.bookSource == SchoolResourceContainerFragment.Constants.SCHOOL_BOOK) {
            serverUrl = ServerUrl.GET_SCHOOL_CATALOG_LESSON_LIST_URL;
        } else if (this.bookSource == SchoolResourceContainerFragment.Constants.PERSONAL_BOOK) {
            serverUrl = ServerUrl.GET_MY_COLLECTION_CATALOG_LESSON_LIST_URL;
        }

        if (serverUrl != null) {
            RequestHelper.sendPostRequest(getActivity(), serverUrl, params, listener);
        }
    }

    @Override
    public void clearSerachData() {
        getCurrAdapterViewHelper().clearData();
        getPageHelper().clear();
    }

    protected void updateResourceListView(NewResourceInfoListResult result) {
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

            if (getPageHelper().isFetchingFirstPage()) {
                getCurrAdapterViewHelper().clearData();
            }
            getPageHelper().updateByPagerArgs(result.getModel().getPager());
            getPageHelper().setCurrPageIndex(
                    getPageHelper().getFetchingPageIndex());
            if (isPick) {
                List<NewResourceInfo> list1 = new ArrayList<NewResourceInfo>();
                for (NewResourceInfo info : list) {
                    if (taskType == StudyTaskType.INTRODUCTION_WAWA_COURSE) {
                        if (info.isMicroCourse19()) {
                            list1.add(info);
                        }
                    } else if (taskType == StudyTaskType.RETELL_WAWA_COURSE
                            || taskType == StudyTaskType.NEW_WATACH_WAWA_COURSE
                            || taskType == StudyTaskType.LISTEN_READ_AND_WRITE){
                      if (info.isMicroCourse() || info.isOnePage()){
                          list1.add(info);
                      }
                    } else {
                        if (info.isMicroCourse()) {
                            list1.add(info);
                        }
                    }
                }
                list = list1;
            }
            RefreshUtil.getInstance().refresh(list);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == CatalogSelectActivity.REQUEST_CODE_SELECT_CATALOG) {
            Calalog calalog = (Calalog) data.getSerializableExtra(CatalogLessonListActivity.BOOK_CATALOG_ITEM);
            bookCatalogId = calalog.getId();
            bookCatalogName = calalog.getName();
            loadDataAgain();
        }
    }

    @Override
    public void privousNextClickEvent(int position) {
        if (calalogsNochildren != null && calalogsNochildren.size() > 0) {
            Calalog calalog = calalogsNochildren.get(position);
            bookCatalogId = calalog.getId();
            bookCatalogName = calalog.getName();
            loadDataAgain();
        }
    }

    private void loadDataAgain() {
        setTitle();
        getPageHelper().clear();
        loadResourceList();
    }

    @Override
    public void setTitle() {
        if (mFragmentByTag == null) {
            mFragmentByTag = (SchoolResourceContainerFragment) getActivity().getSupportFragmentManager().findFragmentByTag(SchoolResourceContainerFragment.TAG);
        }
        if (isPick) {
            String title = getString(R.string.microcourse);
            if (taskType == StudyTaskType.LISTEN_READ_AND_WRITE){
                //听说+读写title(指定课件)
                title = getString(R.string.appoint_course_no_point);
            }
            mFragmentByTag.mContactsHeaderTitle.setText(title);
        } else {
            mFragmentByTag.mContactsHeaderTitle.setText(bookCatalogName);
        }

    }

    @Override
    public String getBookCatalogName() {
        return bookCatalogName;
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
                    RefreshUtil.getInstance().removeId(info.getId());
                }
            }
        }
    }

    /**
     * 获得选取的LQ课件资源（仅选取LQ课件）
     * @return
     */
    private List<MediaInfo> getSelectedData() {
        List dataList = getCurrAdapterViewHelper().getData();
        if (dataList != null && dataList.size() > 0) {
            int mediaType = MediaType.MICROCOURSE;
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
                    mediaInfo.setResProperties(newResourceInfo.getResProperties());
                    //我的收藏和个人空间区分，0-个人空间，1-我的收藏
                    mediaInfo.setCourseInfo(newResourceInfo.getCourseInfo());
                    resultList.add(mediaInfo);
                }
            }
            return resultList;
        }
        return null;
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
        //看课件支持多类型
        if (watchWawaCourseSupportMultiType){
            List<MediaInfo> mediaInfoList = getSelectedData();
            if (mediaInfoList == null || mediaInfoList.size() == 0){
                TipMsgHelper.ShowMsg(getActivity(),R.string.pls_select_files);
                return;
            }
            //处理分页数据
            WatchWawaCourseResourceSplicingUtils.splicingSplitPageResources(getActivity(),
                    mediaInfoList, MediaType.MICROCOURSE);
            return;
        }
        if (getCurrAdapterViewHelper() != null) {
            NewResourceInfo newInfo=null;
            List<NewResourceInfo> resourceInfos = getCurrAdapterViewHelper().getData();
            if (resourceInfos==null||resourceInfos.size()==0){
                TipMsgHelper.ShowMsg(getActivity(),R.string.pls_select_files);
                return;
            }
            if (resourceInfos != null && resourceInfos.size() > 0) {
                if (curPosition >= 0 && curPosition < resourceInfos.size()) {
                    NewResourceInfo info = resourceInfos.get(curPosition);
                    if (info != null && info.isSelect()) {
                        newInfo=info;
                    }
                }
                if (newInfo==null){
                    TipMsgHelper.ShowMsg(getActivity(),R.string.pls_select_files);
                    return;
                }
                ArrayList<ResourceInfo> resourceInfo= new ArrayList<ResourceInfo>();
                ResourceInfo resource = new ResourceInfo();
                resource.setTitle(newInfo.getTitle());
                resource.setImgPath(AppSettings.getFileUrl(newInfo.getThumbnail()));
                resource.setResourcePath(AppSettings.getFileUrl(newInfo.getResourceUrl()));
                resource.setShareAddress(newInfo.getShareAddress());
                resource.setResourceType(newInfo.getResourceType());
                String resId=newInfo.getMicroId();
                if (resId.contains("-")){
                    resource.setResId(resId);
                }else {
                    resource.setResId(resId+"-"+newInfo.getResourceType());
                }
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
        }

    }
}
