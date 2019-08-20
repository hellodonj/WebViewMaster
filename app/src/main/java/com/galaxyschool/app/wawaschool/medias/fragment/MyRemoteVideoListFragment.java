package com.galaxyschool.app.wawaschool.medias.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
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

import com.alibaba.fastjson.JSONObject;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.Request;
import com.galaxyschool.app.wawaschool.CatalogLessonListActivity;
import com.galaxyschool.app.wawaschool.CatalogSelectActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.CampusPatrolUtils;
import com.galaxyschool.app.wawaschool.common.LetvVodHelperNew;
import com.galaxyschool.app.wawaschool.common.RefreshUtil;
import com.galaxyschool.app.wawaschool.common.ScreenUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.common.WatchWawaCourseResourceSplicingUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.db.dto.LocalCourseDTO;
import com.galaxyschool.app.wawaschool.fragment.CampusPatrolPickerFragment;
import com.galaxyschool.app.wawaschool.fragment.ContactsListFragment;
import com.galaxyschool.app.wawaschool.fragment.MediaListFragment;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.medias.activity.MyLocalVideoListActivity;
import com.galaxyschool.app.wawaschool.pojo.MaterialResourceType;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;
import com.lqwawa.lqbaselib.net.ThisStringRequest;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.Calalog;
import com.galaxyschool.app.wawaschool.pojo.MediaInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfoTag;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfoTagListResult;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.ResourceTitle;
import com.galaxyschool.app.wawaschool.pojo.ResourceTitleResult;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.views.CollectPopupView;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.galaxyschool.app.wawaschool.views.InputBoxDialog;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.lecloud.skin.ui.utils.VodVideoSettingUtil;
import com.lqwawa.client.pojo.MediaType;
import com.lqwawa.client.pojo.ResourceInfo;
import com.osastudio.common.utils.ConstantSetting;
import com.osastudio.common.utils.Constants;
import com.osastudio.common.utils.LQImageLoader;

import org.json.JSONException;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.galaxyschool.app.wawaschool.medias.fragment.SchoolResourceContainerFragment.Constants.FROM_SCHOOLRESOURCE;

/**
 * 远端的视频列表,旨在解耦。
 */
public class MyRemoteVideoListFragment extends ContactsListFragment implements ISchoolResource{

    private static final int MAX_ONEPAGE_UPLOAD_COUNT = 1;
    private static final int MAX_RENAME_COUNT = 1;
    private static final int MAX_UPLOAD_COUNT = 3;
    private static final int PIC_MAX_COLUMN = 2;
    public static final int MAX_PICK_COUNT = ConstantSetting.SELECT_MEDIA_CLOUD_NUM;
    private boolean isSelectAll;
    //变量
    private int mediaType = MediaType.VIDEO;//视频类型
    private HashMap<String, NewResourceInfoTag> resourceInfoTagHashMap = new HashMap();
    private int viewMode = CampusPatrolUtils.ViewMode.NORMAL;
    private int editMode = CampusPatrolUtils.EditMode.UPLOAD;
    private boolean isPick;
    private int layoutId;
    private int maxColumn;
    private int maxCount = MAX_PICK_COUNT;
    private View topLayout, bottomLayout, bottomSubLayout0, bottomSubLayout1,
            segLine0, segLine1,bottomSegLine0,bottomSegLine1;
    private TextView uploadBtn, renameBtn, deleteBtn;
    private ImageView checkBtn;
    private TextView cancelBtn,selectAllBtn,confirmBtn;
    private boolean showBottomChoiceLayout = false;//是否显示底部的选择布局



    private String bookPrimaryKey;
    private String bookName;
    private String schoolId;
    private String bookCatalogId;
    private int position;
    private List<Calalog> calalogs;
    private List<Calalog> calalogsNochildren;
    private int taskType;
    public String bookCatalogName,keyWord;
    private NewResourceInfoTagListResult resourceListResult;
    private SchoolResourceContainerFragment mFragmentByTag;
    private int bookSource;
    private boolean mFromSchoolResource;//从校本资源库,精品资源库,我的课程进入
    private boolean isFromOnline;
    private boolean isGetAppointResource;
    private boolean isFromStudyTask;
    private int superTaskType;
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
//        initViews();
        if (isPick) {
            refreshData();
        }

    }

    private void init() {
        if (getArguments() != null) {
            bookCatalogName = getArguments().getString(SchoolResourceContainerFragment.Constants.EXTRA_BOOK_CATALOG_NAME);
            bookSource = getArguments().getInt(SchoolResourceContainerFragment.Constants.EXTRA_BOOK_SOURCE);
            bookPrimaryKey = getArguments().getString(SchoolResourceContainerFragment.Constants.EXTRA_BOOK_PRIMARY_KEY);
            schoolId = getArguments().getString(SchoolResourceContainerFragment.Constants.EXTRA_SCHOOL_ID);
            bookCatalogId = getArguments().getString(SchoolResourceContainerFragment.Constants.EXTRA_BOOK_CATALOG_ID);
            calalogs = (List<Calalog>) getArguments().getSerializable(CatalogLessonListActivity.BOOK_CATALOG_LIST);
            bookName = getArguments().getString(SchoolResourceContainerFragment.Constants.EXTRA_BOOK_NAME);
            mFromSchoolResource = getArguments().getBoolean(FROM_SCHOOLRESOURCE, false);
            if (mFromSchoolResource) {
                isPick = getArguments().getBoolean(ActivityUtils.EXTRA_IS_PICK);
                isFromOnline = getArguments().getBoolean(SchoolResourceContainerFragment.Constants
                        .FROM_AIRCLASS_ONLINE, false);
            } else {
                isPick = getArguments().getBoolean(CampusPatrolUtils.Constants.EXTRA_IS_PICK);
                showBottomChoiceLayout = getArguments().getBoolean(CampusPatrolUtils.Constants
                        .EXTRA_SHOW_CHOICE_BOTTOM_LAYOUT);
            }
            taskType = getArguments().getInt(ActivityUtils.EXTRA_TASK_TYPE);
            isGetAppointResource = getArguments().getBoolean(ActivityUtils
                    .EXTRA_IS_GET_APPOINT_RESOURCE,false);
            isFromStudyTask = getArguments().getBoolean(MediaListFragment.EXTRA_SUPPORT_MULTI_TYPE_WATCH_WAWA_COURSE);
            if (isFromStudyTask) {
                maxCount = getArguments().getInt(ActivityUtils.EXTRA_SELECT_MAX_COUNT);
            }
            superTaskType = getArguments().getInt(ActivityUtils.EXTRA_SUPER_TASK_TYPE);
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


    private void initViews() {
        if (!mFromSchoolResource) {
            findViewById(R.id.contacts_header_layout).setVisibility(View.VISIBLE);
            ImageView imageView = ((ImageView) findViewById(R.id.contacts_header_left_btn));
            if (imageView != null) {
                if (isPick) {
                    imageView.setVisibility(View.INVISIBLE);
                } else {
                    imageView.setVisibility(View.VISIBLE);
                }
            }
            TextView textView = ((TextView) findViewById(R.id.contacts_header_title));
            if (textView != null) {
                textView.setText(getString(R.string.videos));
            }
            textView = ((TextView) findViewById(R.id.contacts_header_right_btn));
            if (textView != null) {
                //选择界面的确定按钮
                if (isPick) {
                    if (showBottomChoiceLayout) {
                        textView.setVisibility(View.INVISIBLE);
                    } else {
                        textView.setVisibility(View.VISIBLE);
                    }
                }
                textView.setText(getString(R.string.confirm));
                textView.setOnClickListener(this);
            }
        }


        final PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);

        GridView gridView = (GridView) findViewById(R.id.grid_view);
        if (gridView != null) {
            final int min_padding = getResources().getDimensionPixelSize(R.dimen.min_padding);
            //视频和照片的布局
            layoutId = R.layout.video_grid_item;
            maxColumn = PIC_MAX_COLUMN;
            gridView.setNumColumns(maxColumn);
            gridView.setBackgroundColor(Color.WHITE);
            gridView.setVerticalSpacing(getResources().getDimensionPixelSize(
                    R.dimen.gridview_spacing));
            gridView.setPadding(min_padding, min_padding, min_padding, min_padding);

            AdapterViewHelper helper = new AdapterViewHelper(
                    getActivity(), gridView, layoutId) {

                @Override
                public void loadData() {
                    if (TextUtils.isEmpty(keyWord)) {
                        keyWord = "";
                    }
                    loadResourceList(keyWord);
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = convertView;
                    int min_padding = getResources().getDimensionPixelSize(R.dimen.min_padding);
                    int itemSize = (ScreenUtils.getScreenWidth(getActivity()) -
                            min_padding * (maxColumn + 1)) / maxColumn;

                    if (view == null) {
                        LayoutInflater inflater = (LayoutInflater) getActivity().
                                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        view = inflater.inflate(layoutId, parent, false);
                    }
                    final MediaInfo data = (MediaInfo) getDataAdapter().getItem(position);
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
                    ImageView flag = (ImageView) view.findViewById(R.id.media_flag);
                    //播放暂停按钮
                    ImageView mediaCover = (ImageView) view.findViewById(R.id.media_cover);
                    View splitView = view.findViewById(R.id.media_split_btn);

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

                    //判断是否显示拆分页
                    if (splitView != null) {
                        splitView.setVisibility(View.GONE);
                    }

                    flag.setVisibility(viewMode == CampusPatrolUtils.ViewMode.NORMAL ?
                            View.INVISIBLE : View.VISIBLE);
                    flag.setImageResource(data.isSelect() ? R.drawable.select :
                            R.drawable.unselect);

                    ImageView videoTypeView = (ImageView) view.findViewById(R.id.iv_video_type_flag);
                    if (videoTypeView != null) {
                        if (data.getResourceType() == MaterialResourceType.Q_DUBBING_VIDEO) {
                            videoTypeView.setVisibility(View.VISIBLE);
                        } else {
                            videoTypeView.setVisibility(View.GONE);
                        }
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
                    MediaInfo data = (MediaInfo) holder.data;
                    if (viewMode == CampusPatrolUtils.ViewMode.NORMAL) {
                        mediaPlay(data,position);
                    } else {
                        if (!isPick) {
                            if (!data.isSelect()) {
                                int count = getSelectedDataCount();
                                //选择模式
                                if (count > 0 && count >= maxCount) {
                                    if (editMode == CampusPatrolUtils.EditMode.UPLOAD) {
                                        //单选模式
                                        if (maxCount == 1) {
                                            operateSingleChoiceItem(false, position);
                                        }else {
                                            TipMsgHelper.ShowLMsg(getActivity(), getString(R.string
                                                    .n_upload_max_count, String.valueOf(maxCount)));
                                            return;
                                        }
                                    } else if (editMode == CampusPatrolUtils.EditMode.RENAME) {
                                        //单选模式
                                        if (maxCount == 1) {
                                            operateSingleChoiceItem(false, position);
                                        }else {
                                            TipMsgHelper.ShowLMsg(getActivity(), getString(R.string
                                                    .n_rename_max_count, String.valueOf(maxCount)));
                                            return;
                                        }
                                    }
                                }
                            }
                        } else {
                            if (!data.isSelect()) {
                                int count = getSelectedDataCount();
                                if (count > 0 && count >= maxCount) {
                                    //单选模式
                                    if (maxCount == 1) {
                                        operateSingleChoiceItem(false, position);
                                    }else {
                                        TipMsgHelper.ShowLMsg(getActivity(), getString(R.string
                                                .n_pick_max_count, String.valueOf(maxCount)));
                                        return;
                                    }
                                }
                            }
                        }
                        data.setIsSelect(!data.isSelect());
                        if (data.isSelect()) {
                            RefreshUtil.getInstance().addId(data.getId());
                        }else {
                            RefreshUtil.getInstance().removeId(data.getId());
                        }

                        getCurrAdapterViewHelper().update();

                        isSelectAll = isSelectAll();
                        checkState(isSelectAll);
                    }
                }
            };
//            if (mFromSchoolResource) {
//                gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//                    @Override
//                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
//                        showCollectPopupView(i);
//                        return true;
//                    }
//                });
//            }

            setCurrAdapterViewHelper(gridView, helper);
        }
        initBottomViews();
    }

    private void showCollectPopupView(int position) {
        NewResourceInfo newResourceInfo = resourceListResult.getModel().getData().get(position);

        CollectPopupView imagePopupView = new CollectPopupView(getActivity(),
                newResourceInfo.getIdType(),
                newResourceInfo.getTitle(),
                newResourceInfo.getAuthorId()
                , getString(R.string.videos)
              );
        imagePopupView.showAtLocation(getActivity().getWindow().getDecorView().findViewById(android.R.id.content)
                , Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }



    private void mediaPlay(MediaInfo data,int position) {
        if (data == null) {
            return;
        }
        if (mFragmentByTag == null) {
            mFragmentByTag = (SchoolResourceContainerFragment) getActivity().getSupportFragmentManager().findFragmentByTag(SchoolResourceContainerFragment.TAG);
        }
        boolean isHideMoreBtn = !mFragmentByTag.isVipSchool && mFragmentByTag.isFromChoiceLib;
        String filePath = AppSettings.getFileUrl(data.getPath());
        String microId = data.getMicroId();
        int resourceType = data.getResourceType();
        String idType = microId + "-" + resourceType;

        String leValue  = data.getNewResourceInfoTag().getLeValue();
        LetvVodHelperNew.VodVideoBuilder builder =new LetvVodHelperNew.VodVideoBuilder
                (getActivity())
                .setNewUI(true)//使用自定义UI
                .setTitle(data.getTitle())//视频标题
                .setMediaType(VodVideoSettingUtil.VIDEO_TYPE)
                .setResId(idType)
                .setResourceType(resourceType)
                .setAuthorId(data.getAuthorId())
                .setLeStatus(data.getNewResourceInfoTag().getLeStatus())
               //对精品和校本资源的收藏标识
                .setIsPublicRes(mFragmentByTag.isVipSchool)
                .setIsFromChoiceLib(mFragmentByTag.isFromChoiceLib)
                .setCollectionSchoolId(mFragmentByTag.collectSchoolId)
                .setHideBtnMore(isHideMoreBtn);

        if (TextUtils.isEmpty(leValue)){
            builder.setUrl(filePath);
            builder.create();
        }else {
            String [] values = leValue.split("&");
            String uUid = values[1].split("=")[1];
            String vUid = values[2].split("=")[1];
            builder.setUuid(uUid);
            builder.setVuid(vUid);
            builder.setUrl(filePath);
            builder.create();
        }
    }

    private void initBottomViews() {
        viewMode = isPick ? CampusPatrolUtils.ViewMode.EDIT : CampusPatrolUtils.ViewMode.NORMAL;
        topLayout = findViewById(R.id.top_layout);
        bottomLayout = findViewById(R.id.bottom_layout);
        if (bottomLayout != null){
            //选择的时候，底部布局和头部的确定按钮不能同时存在。
            if (isPick){
                //选择状态隐藏底部布局
                if (showBottomChoiceLayout){
                    bottomLayout.setVisibility(View.VISIBLE);
                }else {
                    bottomLayout.setVisibility(View.GONE);
                }
            }else {
                bottomLayout.setVisibility(View.VISIBLE);
            }
        }
        bottomSubLayout0 = findViewById(R.id.bottom_sub_layout_0);
        bottomSubLayout1 = findViewById(R.id.bottom_sub_layout_1);
        segLine0 = findViewById(R.id.seg_line_0);
        segLine1 = findViewById(R.id.seg_line_1);
        bottomSegLine0 = findViewById(R.id.bottom_seg_line_0);
        bottomSegLine1 = findViewById(R.id.bottom_seg_line_1);

        checkBtn = (ImageView) findViewById(R.id.btn_check);
        if (!isPick) {
            topLayout.setVisibility(viewMode == CampusPatrolUtils.ViewMode.NORMAL ?
                    View.GONE : View.GONE);
        } else {
            topLayout.setVisibility(View.GONE);
        }

        topLayout.setOnClickListener(this);

        uploadBtn = (TextView) findViewById(R.id.btn_bottom_upload);
        renameBtn = (TextView) findViewById(R.id.btn_bottom_rename);
        deleteBtn = (TextView) findViewById(R.id.btn_bottom_delete);
        uploadBtn.setOnClickListener(this);
        renameBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);

        selectAllBtn = (TextView) findViewById(R.id.btn_bottom_select_all);
        confirmBtn = (TextView) findViewById(R.id.btn_bottom_ok);
        cancelBtn = (TextView) findViewById(R.id.btn_bottom_cancel);
        selectAllBtn.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

        initBottomLayout(viewMode);
        if (isPick) {
            if (mediaType == MediaType.ONE_PAGE || mediaType == MediaType.MICROCOURSE) {
                //PPT/PDF/有声相册/微课最多1个，拆分页多个。
                maxCount = MAX_ONEPAGE_UPLOAD_COUNT;
            }
            //如果来自空中课堂
            if (isFromOnline){
                maxCount=1;
            }
            //看课件多类型
            if (isGetAppointResource){
                maxCount = 1;
            } else if (WatchWawaCourseResourceSplicingUtils.
                    watchWawaCourseSupportMultiType(getArguments()) && maxCount == 0){
                //控制资源最多选多少
                maxCount = WatchWawaCourseResourceSplicingUtils.
                        controlResourcePickedMaxCount(mediaType, maxCount, false);
            }
        }
    }

    private void initBottomLayout(int viewMode) {
        bottomSubLayout0.setVisibility(viewMode == CampusPatrolUtils.ViewMode.NORMAL ?
                View.VISIBLE : View.INVISIBLE);
        bottomSubLayout1.setVisibility(viewMode != CampusPatrolUtils.ViewMode.NORMAL ?
                View.VISIBLE : View.INVISIBLE);
        if (viewMode == CampusPatrolUtils.ViewMode.NORMAL) {
            uploadBtn.setVisibility(View.VISIBLE);
            segLine0.setVisibility(View.VISIBLE);
            renameBtn.setVisibility(View.VISIBLE);
            segLine1.setVisibility(View.VISIBLE);
        }
        //上传（支持上传多个，但是有数量限制）和重命名（单选）的时候，不支持全选，只有删除的时候支持全选。
        if (editMode == CampusPatrolUtils.EditMode.UPLOAD
                || editMode == CampusPatrolUtils.EditMode.RENAME){
            selectAllBtn.setVisibility(View.GONE);
            bottomSegLine0.setVisibility(View.GONE);
        }else {
            //其他的支持
            selectAllBtn.setVisibility(View.VISIBLE);
            bottomSegLine0.setVisibility(View.VISIBLE);
        }
    }

    private boolean isSelectAll() {
        List<MediaInfo> mediaInfos = new ArrayList<MediaInfo>();
        List datas = getCurrAdapterViewHelper().getData();
        if (datas != null && datas.size() > 0) {
            for (Object data : datas) {
                MediaInfo mediaInfo = (MediaInfo) data;
                if (mediaInfo != null && mediaInfo.isSelect()) {
                    mediaInfos.add(mediaInfo);
                }
            }
            if (mediaInfos.size() > 0 && datas.size() > 0 && mediaInfos.size() == datas.size()) {
                return true;
            }
        }

        return false;
    }

    private void checkData(boolean isSelectAll) {
        List datas = getCurrAdapterViewHelper().getData();
        if (datas != null && datas.size() > 0) {
            for (Object data : datas) {
                MediaInfo mediaInfo = (MediaInfo) data;
                if (mediaInfo != null) {
                    mediaInfo.setIsSelect(isSelectAll);
                }
            }
        }
        getCurrAdapterViewHelper().update();
    }

    private void checkState(boolean isSelectAll) {
        //更新全选布局
        if (isSelectAll){
            selectAllBtn.setText(getString(R.string.cancel_to_select_all));

        }else {
            selectAllBtn.setText(getString(R.string.select_all));
        }
        checkBtn.setImageResource(isSelectAll ? R.drawable.select : R.drawable.unselect);
    }

    /**
     * check all file items except position item
     *
     * @param isSelect
     * @param position
     */
    private void operateSingleChoiceItem(boolean isSelect, int position) {
        List<MediaInfo> mediaInfoList = getCurrAdapterViewHelper().getData();
        if (mediaInfoList != null && mediaInfoList.size() > 0) {
            int size = mediaInfoList.size();
            for (int i = 0; i < size; i++) {
                MediaInfo info = mediaInfoList.get(i);
                if (info != null && i != position) {
                    info.setIsSelect(isSelect);
                    RefreshUtil.getInstance().removeId(info.getId());
                }
            }
        }
    }

    private int getSelectedDataCount() {
        List<MediaInfo> mediaInfos = getSelectedData();
        if (mediaInfos == null) {
            return 0;
        }
        return mediaInfos.size();
    }

    private List<MediaInfo> getSelectedData() {
        List<MediaInfo> mediaInfos = new ArrayList<MediaInfo>();
        List datas = getCurrAdapterViewHelper().getData();
        if (datas != null && datas.size() > 0) {
            for (Object data : datas) {
                MediaInfo mediaInfo = (MediaInfo) data;
                if (mediaInfo != null && mediaInfo.isSelect()) {
                    mediaInfos.add(mediaInfo);
                }
            }
        }
        return mediaInfos;
    }

    private void refreshData() {
        loadResourceList("");
    }

    @Override
    public void loadResourceList(String keyword) {
        this.keyWord = keyword;
        resourceInfoTagHashMap.clear();
        UserInfo userInfo = getUserInfo();
        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
            return;
        }
        String serverUrl = null;
        Map<String, Object> params = new ArrayMap<>();
        if (!mFromSchoolResource) {
            params.put("MemberId", userInfo.getMemberId());
            //LQ微课拉取1和10
            if (mediaType == MediaType.ONE_PAGE || mediaType == MediaType.MICROCOURSE) {
                StringBuilder sb = new StringBuilder();
                sb.append("1").append(",").append("10");
                params.put("MType", sb.toString());
            } else {
                params.put("MType", String.valueOf(mediaType));
            }
            params.put("Pager", getPageHelper().getFetchingPagerArgs());
            serverUrl = ServerUrl.SEARCH_PERSONAL_SPACE_LIST_URL;
        } else {
            params.put("SchoolId", schoolId);
            params.put("BookId", bookPrimaryKey);
            params.put("MemberId", getUserInfo().getMemberId());
            params.put("MType", "4");
            params.put("SectionId", bookCatalogId);
            params.put("KeyWord", keyword);
            params.put("Pager", getPageHelper().getFetchingPagerArgs());

            if (this.bookSource == SchoolResourceContainerFragment.Constants.PLATFORM_BOOK) {
                serverUrl = ServerUrl.GET_PLATFORM_CATALOG_LESSON_LIST_URL;
            } else if (this.bookSource == SchoolResourceContainerFragment.Constants.SCHOOL_BOOK) {
                serverUrl = ServerUrl.GET_SCHOOL_CATALOG_LESSON_LIST_URL;
            } else if (this.bookSource == SchoolResourceContainerFragment.Constants.PERSONAL_BOOK) {
                serverUrl = ServerUrl.GET_MY_COLLECTION_CATALOG_LESSON_LIST_URL;
            }
        }
        RequestHelper.sendPostRequest(getActivity(), serverUrl,
                params,
                new DefaultPullToRefreshDataListener<NewResourceInfoTagListResult>
                (NewResourceInfoTagListResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                NewResourceInfoTagListResult result = getResult();
                if (result == null || !result.isSuccess() || result.getModel() == null) {
                    return;
                }

                if (getPageHelper().isFetchingPageIndex(result.getModel().getPager())) {
                    List<NewResourceInfoTag> list = result.getModel().getData();
                    filterOrdinaryVideo(list);
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

                    List<MediaInfo> mediaInfos = null;
                    mediaInfos = new ArrayList<MediaInfo>();
                    for (int i = 0, size = list.size(); i < size; i++) {
                        NewResourceInfoTag resourceInfo = list.get(i);
                        if (resourceInfo != null) {
                            MediaInfo mediaInfo = new MediaInfo();
                            StringBuilder builder = new StringBuilder();
                            builder.append(resourceInfo.getResourceUrl());
                            if(mediaType == MediaType.ONE_PAGE && !TextUtils.isEmpty(
                                    resourceInfo.getUpdatedTime())) {
                                builder.append("?");
                                builder.append(resourceInfo.getUpdatedTime());
                            }
                            mediaInfo.setPath(builder.toString());
                            if(mediaType == MediaType.ONE_PAGE||mediaType == MediaType.MICROCOURSE){
                                if(resourceInfo.isMicroCourse()){
                                    mediaInfo.setMediaType(MediaType.MICROCOURSE);
                                }else{
                                    mediaInfo.setMediaType(MediaType.ONE_PAGE);
                                }
                            }else{
                                mediaInfo.setMediaType(mediaType);
                            }
                            mediaInfo.setId(resourceInfo.getId());
                            mediaInfo.setMicroId(resourceInfo.getMicroId());
                            mediaInfo.setTitle(resourceInfo.getTitle());
                            mediaInfo.setThumbnail(resourceInfo.getThumbnail());
                            //PPT和PDF缩略图取列表第一张
                            List<NewResourceInfo> splitList = resourceInfo.getSplitInfoList();
                            if (splitList != null && splitList.size() > 0){
                                String imageUrl = null;
                                imageUrl = splitList.get(0).getResourceUrl();
                                if (!TextUtils.isEmpty(imageUrl)){
                                    if (mediaType == MediaType.PDF || mediaType == MediaType.PPT)
                                        mediaInfo.setThumbnail(imageUrl);
                                    //PPT/PDF封面打开路径
                                    mediaInfo.setPath(imageUrl);
                                }
                            }
                            mediaInfo.setUpdateTime(resourceInfo.getUpdatedTime());
                            mediaInfo.setShareAddress(resourceInfo.getShareAddress());
                            mediaInfo.setResourceType(resourceInfo.getResourceType());
                            //我的收藏和个人空间区分，0-个人空间，1-我的收藏
                            mediaInfo.setHasCollected(resourceInfo.getType() == 1);
                            if (mediaType == MediaType.MICROCOURSE
                                    || mediaType == MediaType.ONE_PAGE
                                    ||mediaType == MediaType.PPT
                                    || mediaType == MediaType.PDF) {
                                mediaInfo.setCourseInfo(resourceInfo.getCourseInfo());
                            }
                            mediaInfo.setNewResourceInfoTag(resourceInfo);
                            //设置作者id
                            mediaInfo.setAuthorId(resourceInfo.getAuthorId());

                            //传值乐视的vUid
                            String leValue  = resourceInfo.getLeValue();
                            if (!TextUtils.isEmpty(leValue)){
                                String [] values = leValue.split("&");
                                String uUid = values[1].split("=")[1];
                                String vUid = values[2].split("=")[1];
                                mediaInfo.setVuid(vUid);
                            }
                            mediaInfo.setLeStatus(resourceInfo.getLeStatus());
                            mediaInfos.add(mediaInfo);
                            resourceInfoTagHashMap.put(resourceInfo.getId(), resourceInfo);
                        }
                    }

                    if (getPageHelper().isFetchingFirstPage()) {
                        getCurrAdapterViewHelper().clearData();
                    }
                    getPageHelper().updateByPagerArgs(result.getModel().getPager());
                    getPageHelper().setCurrPageIndex(
                            getPageHelper().getFetchingPageIndex());

                    RefreshUtil.getInstance().refresh(mediaInfos);
                    if (getCurrAdapterViewHelper().hasData()) {
                        int position = getCurrAdapterViewHelper().getData().size();
                        if (position > 0) {
                            position--;
                        }
                        getCurrAdapterViewHelper().getData().addAll(mediaInfos);
                        getCurrAdapterView().setSelection(position);
                        resourceListResult.getModel().setData(getCurrAdapterViewHelper().getData());
                    } else {
                        getCurrAdapterViewHelper().setData(mediaInfos);
                        resourceListResult = result;
                    }
                }
            }
        });
    }

    private void filterOrdinaryVideo(List<NewResourceInfoTag> list){
        if (list == null || list.size() == 0){
            return;
        }
        if (superTaskType == StudyTaskType.Q_DUBBING){
            Iterator<NewResourceInfoTag> it = list.iterator();
            while (it.hasNext()){
                NewResourceInfoTag infoTag = it.next();
                if (infoTag.getResourceType() != MaterialResourceType.Q_DUBBING_VIDEO){
                    it.remove();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contacts_header_right_btn) {
            //选择课件
            getSelectData();
        } else if (v.getId() == R.id.top_layout) {
            isSelectAll = !isSelectAll;
            checkState(isSelectAll);
            checkData(isSelectAll);
        } else if (v.getId() == R.id.btn_bottom_upload) {
            if (viewMode == CampusPatrolUtils.ViewMode.NORMAL) {
                editMode = CampusPatrolUtils.EditMode.UPLOAD;
                maxCount = MAX_UPLOAD_COUNT;
                if (mediaType == MediaType.ONE_PAGE) {
                    maxCount = MAX_ONEPAGE_UPLOAD_COUNT;
                }
            }
            //选择本地数据上传
            upload();
        } else if (v.getId() == R.id.btn_bottom_rename) {
            if (viewMode == CampusPatrolUtils.ViewMode.NORMAL) {
                editMode = CampusPatrolUtils.EditMode.RENAME;
                maxCount = MAX_RENAME_COUNT;
                enterEditMode();
            }

        } else if (v.getId() == R.id.btn_bottom_delete) {
            if (viewMode == CampusPatrolUtils.ViewMode.NORMAL) {
                editMode = CampusPatrolUtils.EditMode.DELETE;
                enterEditMode();
            }

        } else if (v.getId() == R.id.btn_bottom_ok) {
            if (!isPick) {
                if (editMode == CampusPatrolUtils.EditMode.UPLOAD) {
                    //进入本地课件页面，选择课件上传，成功后刷新本界面。
                } else if (editMode == CampusPatrolUtils.EditMode.DELETE) {
                    delete(mediaType);
                } else if (editMode == CampusPatrolUtils.EditMode.RENAME) {
                    rename();
                }
            }else {
                //选择课件
                getSelectData();
            }
        } else if (v.getId() == R.id.btn_bottom_cancel) {
            //取消，变成-1。
            editMode = CampusPatrolUtils.EditMode.CANCEL;
            exitEditMode();
        }else if (v.getId() == R.id.btn_bottom_select_all){
            //全选/取消全选
            isSelectAll = !isSelectAll;
            checkState(isSelectAll);
            checkData(isSelectAll);
        }else {
            super.onClick(v);
        }
    }

    /**
     * 上传本地课件
     */
    private void upload() {
        Intent intent = new Intent(getActivity(), MyLocalVideoListActivity.class);
        //是否选择
        intent.putExtra(CampusPatrolUtils.Constants.EXTRA_IS_PICK,true);
        startActivityForResult(intent,CampusPatrolPickerFragment.REQUEST_CODE_PICKED_COURSE);
    }

    private void rename() {
        List<MediaInfo> mediaInfos = getSelectedData();
        if (mediaInfos == null || mediaInfos.size() == 0) {
            TipMsgHelper.ShowLMsg(getActivity(), R.string.pls_select_files);
            return;
        }
        exitEditMode();
        final MediaInfo mediaInfo = mediaInfos.get(0);
        if (mediaInfo != null) {
            String title;
            if (mediaType == MediaType.MICROCOURSE || mediaType == MediaType.ONE_PAGE) {
                title = mediaInfo.getTitle();
            } else {
                title = Utils.removeFileNameSuffix(mediaInfo.getTitle());
            }
            final String dataTitle = title;
            showEditDialog(getString(R.string.rename_file), dataTitle,
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String title = ((InputBoxDialog) dialog).getInputText().trim();
                    if (TextUtils.isEmpty(title)) {
                        TipMsgHelper.ShowLMsg(getActivity(), R.string.pls_enter_title);
                        return;
                    }
                    if (!TextUtils.isEmpty(dataTitle) && title.equals(dataTitle)) {
                        TipMsgHelper.ShowLMsg(getActivity(), getString(R.string
                                .cloud_resource_rename_exist,dataTitle));
                        return;
                    }
                    //过滤特殊字符
                    boolean isValid = Utils.checkEditTextValid(getActivity(),title);
                    if (!isValid){
                        return;
                    }
                    if (title.length() > Constants.MAX_TITLE_LENGTH){
                        TipMsgHelper.ShowLMsg(getActivity(),getString(
                                R.string.words_count_over_limit));
                        return;
                    }
                    dialog.dismiss();
                    rename(mediaInfo, mediaType, title);
                }
            }).show();
        }
    }

    private InputBoxDialog showEditDialog(
            String dialogTitle, String dataTitle,
            DialogInterface.OnClickListener confirmButtonClickListener) {
        InputBoxDialog dialog = new InputBoxDialog(
                getActivity(),
                dialogTitle, dataTitle, getString(R.string.pls_enter_title),
                getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }, getString(R.string.confirm), confirmButtonClickListener);
        //设置不自动消失
        dialog.setIsAutoDismiss(false);
        dialog.show();
        return dialog;
    }

    private void showUploadInfoDialog(String info) {
        ContactsMessageDialog dialog = new ContactsMessageDialog(
                getActivity(),
                null, getString(R.string.upload_exist, info),
                getString(R.string.confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, "", null);
        dialog.show();
    }

    private void rename(MediaInfo mediaInfo, int mediaType, String title) {
        List<String> titles = new ArrayList<String>();
        titles.add(title);
        List<MediaInfo> mediaInfos = new ArrayList<MediaInfo>();
        mediaInfos.add(mediaInfo);
        //重置重命名
        checkResourceTitle(mediaInfos, titles, mediaType, CampusPatrolUtils.EditMode.RENAME);
    }

    private void checkResourceTitle(final List<MediaInfo> mediaInfos, final List<String> titles,
                                    final int mediaType, final int editMode) {
        UserInfo userInfo = getUserInfo();
        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
            TipMsgHelper.ShowLMsg(getActivity(), R.string.pls_login);
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", userInfo.getMemberId());
        //两个都能重命名
        if (mediaType == MediaType.MICROCOURSE || mediaType == MediaType.ONE_PAGE) {
            params.put("MTypes", "1,10");
        } else {
            params.put("MType", String.valueOf(mediaType));
        }
        params.put("Title", titles);
        RequestHelper.RequestDataResultListener listener = new RequestHelper.
                RequestDataResultListener<ResourceTitleResult>(
                getActivity(), ResourceTitleResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                ResourceTitleResult result = getResult();
                if (result == null || result.getModel() == null || !result.isSuccess()) {
                    return;
                }
                ResourceTitle resourceTitle = result.getModel().getData();
                List<String> titleList;
                if (resourceTitle != null) {
                    titleList = resourceTitle.getTitle();
                    if (titleList != null && titleList.size() > 0) {
                        if (editMode == CampusPatrolUtils.EditMode.RENAME) {
                            TipMsgHelper.ShowLMsg(getActivity(), getString(
                                    R.string.cloud_resource_rename_exist, titleList.get(0)));
                        } else if (editMode == CampusPatrolUtils.EditMode.UPLOAD) {
                            StringBuilder builder = new StringBuilder();
                            int size = titleList.size();
                            String title;
                            for (int i = 0; i < size - 1; i++) {
                                title = titleList.get(i);
                                if(mediaType == MediaType.PICTURE) {
                                    title = Utils.removeFileNameSuffix(title);
                                }
                                builder.append(title + ",");
                            }
                            title = titleList.get(size - 1);
                            if(mediaType == MediaType.PICTURE) {
                                title = Utils.removeFileNameSuffix(title);
                            }
                            builder.append(title);
                            //防止重名后，选择回弹。
                            showUploadInfoDialog(builder.toString());
                        }
                    }
                } else {
                    if (editMode == CampusPatrolUtils.EditMode.RENAME) {
                        if (mediaInfos != null && mediaInfos.size() > 0 && titles.size() > 0) {
                            renameMedia(mediaInfos.get(0), titles.get(0));
                        }
                    }
                }
            }
        };
        listener.setShowErrorTips(false);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.CHECK_CLOUD_RESOURCE_TITLE_URL,
                params, listener);
    }

    private void renameMedia(final MediaInfo mediaInfo, final String title) {
        final UserInfo userInfo = getUserInfo();
        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
            TipMsgHelper.ShowLMsg(getActivity(), R.string.pls_login);
            return;
        }
        if (mediaInfo == null || TextUtils.isEmpty(mediaInfo.getMicroId())
                || !TextUtils.isDigitsOnly(mediaInfo.getMicroId())) {
            return;
        }
        if (TextUtils.isEmpty(title)) {
            return;
        }
        showLoadingDialog();
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("memberId", userInfo.getMemberId());
        try {
            jsonObject.put("fileName", URLEncoder.encode(title, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        jsonObject.put("resId", Long.parseLong(mediaInfo.getMicroId()));
        jsonObject.put("resType", mediaInfo.getResourceType());
        StringBuilder builder = new StringBuilder();
        builder.append("?j=" + jsonObject.toJSONString());
        String url = ServerUrl.RENAME_MEDIA_URL + builder.toString();
        final ThisStringRequest request = new ThisStringRequest(
                Request.Method.GET, url, new Listener<String>() {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                dismissLoadingDialog();
                if (jsonString != null) {
                    try {
                        org.json.JSONObject dataJsonObject = new org.json.JSONObject(jsonString);
                        if (dataJsonObject != null) {
                            int code = dataJsonObject.optInt("code");
                            if (code == 0) {
                                TipMsgHelper.ShowLMsg(getActivity(), R.string.rename_success);
                                upDateRenameData(mediaInfo,title);
                            } else {
                                TipMsgHelper.ShowLMsg(getActivity(), R.string.rename_failure);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dismissLoadingDialog();
            }
        });
        request.addHeader("Accept-Encoding", "*");
        request.start(getActivity());
    }

    /**
     * 更新重新命名的文件
     */
    public void  upDateRenameData(MediaInfo mediaInfo,String title){
        List<MediaInfo> mediaInfoList = getCurrAdapterViewHelper().getData();
        if (mediaInfoList !=null && mediaInfoList.size() > 0){
            for (int i = 0;i < mediaInfoList.size();i++){
                MediaInfo info = mediaInfoList.get(i);
                if (info.getId().equals(mediaInfo.getId())){
                    info.setTitle(title);
                }
            }
            getCurrAdapterViewHelper().setData(mediaInfoList);
        }
    }
    private void delete(int mediaType) {
        List<MediaInfo> mediaInfos = getSelectedData();
        if (mediaInfos == null || mediaInfos.size() == 0) {
            TipMsgHelper.ShowLMsg(getActivity(), R.string.pls_select_files);
            return;
        }
        exitEditMode();
        deleteMedias(mediaInfos, mediaType);
    }

    private void deleteMedias(final List<MediaInfo> mediaInfos, final int mediaType) {
        StringBuilder builder = new StringBuilder();
        if (mediaInfos.size() == 0) {
            return;
        }
        int size = mediaInfos.size();
        for (int i = 0; i < size - 1; i++) {
            MediaInfo mediaInfo = mediaInfos.get(i);
            if (mediaInfo != null) {
                builder.append(mediaInfo.getId() + ",");
            }
        }
        builder.append(mediaInfos.get(size - 1).getId());

        Map<String, Object> mParams = new HashMap<String, Object>();
        mParams.put("MaterialId", builder.toString());
        postRequest(ServerUrl.PR_DELETE_MEDIAS_URL, mParams, new DefaultDataListener
                <DataModelResult>(DataModelResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                DataModelResult result = getResult();
                if (result == null || !result.isSuccess()) {
                    return;
                }
                UserInfo userInfo = getUserInfo();
                TipMsgHelper.ShowLMsg(getActivity(), R.string.cs_delete_success);
                if (mediaInfos != null && mediaInfos.size() > 0) {
                    for (MediaInfo info : mediaInfos) {
                        if (info != null) {
                            getCurrAdapterViewHelper().getData().remove(info);
                            if(mediaType == MediaType.ONE_PAGE && userInfo != null) {
                                LocalCourseDTO.updateLocalCourseByResId(getActivity(),
                                        getMemeberId(), Long.parseLong(info.getMicroId()), 0);

                            }
                        }
                    }
                    //删除完毕后，刷新数据。
                    getCurrAdapterViewHelper().update();
                    refreshData();
                }
            }
        });
    }

    private void exitEditMode() {

        topLayout.setVisibility(View.GONE);
        isSelectAll = false;
        checkData(isSelectAll);
        checkState(isSelectAll);
        //重置未选择时的状态
        if (!isPick) {
            viewMode = CampusPatrolUtils.ViewMode.NORMAL;
            if (editMode == CampusPatrolUtils.EditMode.DELETE) {
                editMode = CampusPatrolUtils.EditMode.CANCEL;
            }
            if (editMode == CampusPatrolUtils.EditMode.RENAME) {
                editMode = CampusPatrolUtils.EditMode.CANCEL;
            }
            initBottomLayout(viewMode);
        }
    }

    private void enterEditMode() {

        topLayout.setVisibility(editMode != CampusPatrolUtils.EditMode.DELETE ? View.GONE : View.GONE);
        viewMode = CampusPatrolUtils.ViewMode.EDIT;
        initBottomLayout(viewMode);
        getCurrAdapterViewHelper().update();
    }


    @Override
    public void getSelectData() {
        List<MediaInfo> mediaInfos = getSelectedData();
        if (mediaInfos == null || mediaInfos.size() == 0) {
            TipMsgHelper.ShowLMsg(getActivity(), R.string.pls_select_files);
            return;
        }
        //看课件支持多类型
        if (WatchWawaCourseResourceSplicingUtils.
                watchWawaCourseSupportMultiType(getArguments())){
            //处理整页数据
            WatchWawaCourseResourceSplicingUtils.splicingSplitPageResources(getActivity(),
                    mediaInfos, MediaType.VIDEO);
            return;
        }
        ArrayList<ResourceInfo> resourceInfos = new ArrayList<ResourceInfo>();
        for (MediaInfo mediaInfo : mediaInfos) {
            if (mediaInfo != null) {
                ResourceInfo resourceInfo = new ResourceInfo();
                resourceInfo.setTitle(mediaInfo.getTitle());
                resourceInfo.setImgPath(mediaInfo.getThumbnail());
                resourceInfo.setResourcePath(mediaInfo.getPath());
                resourceInfo.setShareAddress(mediaInfo.getShareAddress());
                resourceInfo.setResourceType(mediaInfo.getResourceType());
                //目前拉取的是有声相册和微课两种类型，传递mediaType的时候要根据resourceType来区分微课和有声相册。
                if (mediaInfo.getMediaType() == MediaType.ONE_PAGE
                        || mediaInfo.getMediaType() == MediaType.MICROCOURSE){
                    int resType = mediaInfo.getResourceType() % ResType.RES_TYPE_BASE;
                    if (resType == ResType.RES_TYPE_ONEPAGE) {
                        //设置有声相册类型
                        resourceInfo.setType(MediaType.ONE_PAGE);
                    } else if (resType == ResType.RES_TYPE_COURSE || resType ==
                            ResType.RES_TYPE_COURSE_SPEAKER ||
                            resType == ResType.RES_TYPE_OLD_COURSE) {
                        //设置微课类型
                        resourceInfo.setType(MediaType.MICROCOURSE);
                    }
                }else {
                    resourceInfo.setType(transferMediaType(mediaInfo.getMediaType()));
                }
                if (mediaInfo.getCourseInfo() != null) {
                    resourceInfo.setScreenType(mediaInfo.getCourseInfo().getScreenType());
                }
                String resId=mediaInfo.getMicroId();
                if (resId.contains("-")){
                    resourceInfo.setResId(resId);
                }else {
                    resourceInfo.setResId(resId+"-"+mediaInfo.getResourceType());
                }
                resourceInfos.add(resourceInfo);
            }
        }
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("resourseInfoList", resourceInfos);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        if (getActivity() != null) {
            getActivity().setResult(RESULT_OK, intent);
            getActivity().finish();
        }
    }

    private int transferMediaType(int mediaType) {
        int type = -1;
        switch (mediaType) {
            case MediaType.PPT:
            case MediaType.PDF:
                type = MediaType.PICTURE;
                break;
            default:
                type = mediaType;
                break;
        }
        return type;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null){
            if (requestCode == CampusPatrolPickerFragment.REQUEST_CODE_PICKED_COURSE){
                //数据选择成功需要刷新数据
                if (resultCode == CampusPatrolPickerFragment.RESULT_CODE_PICKED_COURSE){
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
        Calalog calalog = calalogsNochildren.get(position);
        bookCatalogId = calalog.getId();
        bookCatalogName = calalog.getName();
        loadDataAgain();
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
        if (isPick) {
            mFragmentByTag.mContactsHeaderTitle.setText( getString(R.string.videos));
        } else {
            mFragmentByTag.mContactsHeaderTitle.setText(bookCatalogName);
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
