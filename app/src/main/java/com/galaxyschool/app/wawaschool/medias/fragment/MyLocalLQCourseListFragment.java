package com.galaxyschool.app.wawaschool.medias.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
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

import com.alibaba.fastjson.JSONObject;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.Request;
import com.galaxyschool.app.wawaschool.CampusPatrolSplitCourseListActivity;
import com.galaxyschool.app.wawaschool.PictureBooksDetailActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.WawaCourseChoiceActivity;
import com.galaxyschool.app.wawaschool.common.CallbackListener;
import com.galaxyschool.app.wawaschool.common.CampusPatrolUtils;
import com.galaxyschool.app.wawaschool.common.ScreenUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.UploadUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.common.WawaCourseUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.db.MediaDao;
import com.galaxyschool.app.wawaschool.db.dto.LocalCourseDTO;
import com.galaxyschool.app.wawaschool.fragment.CampusPatrolMainFragment;
import com.galaxyschool.app.wawaschool.fragment.CampusPatrolPickerFragment;
import com.galaxyschool.app.wawaschool.fragment.ContactsListFragment;
import com.galaxyschool.app.wawaschool.fragment.SelectedReadingDetailFragment;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.lqwawa.lqbaselib.net.ThisStringRequest;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.MediaInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfoTag;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.ResourceTitle;
import com.galaxyschool.app.wawaschool.pojo.ResourceTitleResult;
import com.galaxyschool.app.wawaschool.pojo.UploadParameter;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseType;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseUploadResult;
import com.galaxyschool.app.wawaschool.pojo.weike.LocalCourseInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.ShortCourseInfo;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.galaxyschool.app.wawaschool.views.InputBoxDialog;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.lqwawa.client.pojo.MediaType;
import com.lqwawa.client.pojo.ResourceInfo;
import com.lqwawa.tools.FileZipHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.oosic.apps.iemaker.base.BaseUtils;
import com.osastudio.common.utils.Constants;
import com.osastudio.common.utils.LQImageLoader;

import org.json.JSONException;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 本地的LQ课件列表,旨在解耦。
 */
public class MyLocalLQCourseListFragment extends ContactsListFragment {

    private static final int MAX_ONEPAGE_UPLOAD_COUNT = 1;
    private static final int MAX_RENAME_COUNT = 1;
    private static final int MAX_UPLOAD_COUNT = 3;
    private boolean isSelectAll;
    //变量
    private int mediaType = MediaType.ONE_PAGE;//有声相册类型
    private HashMap<String, NewResourceInfoTag> resourceInfoTagHashMap = new HashMap();
    private int viewMode = CampusPatrolUtils.ViewMode.NORMAL;
    private int editMode = CampusPatrolUtils.EditMode.UPLOAD;
    private boolean isPick;
    private int layoutId;
    private int MAX_COLUMN = 2;
    private int maxColumn;
    private int maxCount;
    private View topLayout, bottomLayout, bottomSubLayout0, bottomSubLayout1,
            segLine0, segLine1,bottomSegLine0,bottomSegLine1,createCourse;
    private TextView uploadBtn, renameBtn, deleteBtn;
    private ImageView checkBtn;
    private TextView cancelBtn,selectAllBtn,confirmBtn;
    private MediaDao mediaDao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_local_lq_course_list, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        mediaDao = new MediaDao(getActivity());
        ImageLoader.getInstance().clearMemoryCache();
        refreshData();
    }
    private void initViews() {
        if (getArguments() != null){
            isPick = getArguments().getBoolean(CampusPatrolUtils.Constants.EXTRA_IS_PICK);
        }
        ImageView imageView = ((ImageView) findViewById(R.id.contacts_header_left_btn));
        if(imageView != null) {
            if (isPick){
                imageView.setVisibility(View.INVISIBLE);
            }else {
                imageView.setVisibility(View.VISIBLE);
            }
        }
        TextView textView = ((TextView) findViewById(R.id.contacts_header_title));
        if(textView != null) {
            if (isPick) {
                textView.setText(getString(R.string.microcourse));
            }else {
                //LQ云板
                textView.setText(getString(R.string.createspace));
            }
        }
        textView = ((TextView) findViewById(R.id.contacts_header_right_btn));
        if (textView != null) {
            //选择界面的确定按钮
            textView.setText(getString(R.string.ok));
            textView.setVisibility(View.INVISIBLE);
            textView.setOnClickListener(this);
        }

        final PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        pullToRefreshView.setRefreshEnable(false);
        setPullToRefreshView(pullToRefreshView);

        GridView gridView = (GridView) findViewById(R.id.grid_view);
        if (gridView != null) {
            final int min_padding = getResources().getDimensionPixelSize(R.dimen.min_padding);
            //微课和有声相册用单独的布局
            if (mediaType == MediaType.ONE_PAGE || mediaType == MediaType.MICROCOURSE){
                layoutId = R.layout.lq_course_grid_item;
            }
            maxColumn = MAX_COLUMN;
            gridView.setNumColumns(maxColumn);
            gridView.setBackgroundColor(Color.WHITE);
            gridView.setVerticalSpacing(getResources().getDimensionPixelSize(
                    R.dimen.gridview_spacing));
            gridView.setPadding(min_padding, min_padding, min_padding, min_padding);

            AdapterViewHelper helper = new AdapterViewHelper(
                    getActivity(), gridView, layoutId) {

                @Override
                public void loadData() {
                    loadOnePages();
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
                    if (mediaType != MediaType.AUDIO) {
                        FrameLayout frameLayout = (FrameLayout) view.findViewById(
                                R.id.resource_frameLayout);
                        params = (LinearLayout.LayoutParams) frameLayout.getLayoutParams();
                        if (frameLayout != null && params != null) {
                            if (mediaType == MediaType.MICROCOURSE
                                    || mediaType == MediaType.ONE_PAGE) {
                                params.width = itemSize - getActivity().getResources()
                                        .getDimensionPixelSize(R.dimen.resource_gridview_padding);
                                params.height = params.width * 9 / 16;
                            }
                            frameLayout.setLayoutParams(params);
                        }
                    }

                    ImageView thumbnail = (ImageView) view.findViewById(R.id.media_thumbnail);
                    TextView name = (TextView) view.findViewById(R.id.media_name);
                    ImageView flag = (ImageView) view.findViewById(R.id.media_flag);
                    View splitView = view.findViewById(R.id.media_split_btn);
                    if (mediaType == MediaType.ONE_PAGE
                            || mediaType == MediaType.MICROCOURSE) {
                        //图片
                        LQImageLoader.DIOptBuiderParam param = new LQImageLoader.DIOptBuiderParam();
                        param.mIsCacheInMemory = true;
                        param.mOutWidth = LQImageLoader.OUT_WIDTH;
                        param.mOutHeight = LQImageLoader.OUT_HEIGHT;
                        LQImageLoader.displayImage(data.getThumbnail(), thumbnail, param);

                        //标题
                        name.setText(data.getTitle());
                        //微课、有声相册居中对齐。
                        name.setGravity(Gravity.CENTER);
                    }

                    //判断是否显示拆分页
                    if (splitView != null) {
                        splitView.setVisibility(View.GONE);
                        //本地课件没有查看分页
//                        if (data.getMediaType() == MediaType.MICROCOURSE){
//                            int resType = data.getResourceType();
//                            if (resType == ResType.RES_TYPE_COURSE_SPEAKER) {
//                                //resType == ResType.RES_TYPE_OLD_COURSE ||
////                                        resType == ResType.RES_TYPE_COURSE
//                                //老微课不显示详情，打开的时候也不进入详情。10019是单页的微课，不显示拆分。
//                                splitView.setVisibility(View.VISIBLE);
//                            }
//                        }
                        splitView.setTag(data);
                        splitView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MediaInfo mediaInfo = (MediaInfo) v.getTag();
                                if (mediaInfo != null) {
                                    //跳转到拆分页面
                                    enterCampusPatrolSplitCourseListFragment(data);
                                }
                            }
                        });
                    }

//                    已收藏的不能重命名
                    if (editMode == CampusPatrolUtils.EditMode.RENAME){
                        if (data.isHasCollected()){
                            flag.setVisibility(View.GONE);
                        }else {
                            flag.setVisibility(View.VISIBLE);
                        }
                    }else {
                        flag.setVisibility(viewMode == CampusPatrolUtils.ViewMode.NORMAL ?
                                View.INVISIBLE : View.VISIBLE);
                    }
                    flag.setImageResource(data.isSelect() ? R.drawable.select :
                            R.drawable.unselect);

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
                        if (data != null && !TextUtils.isEmpty(data.getPath())) {
                            switch (mediaType) {
                                case MediaType.MICROCOURSE:
                                case MediaType.ONE_PAGE:
                                    openCourse(data);
                                    break;
                            }
                        }
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
                        //已收藏的不能重命名
                        if (editMode == CampusPatrolUtils.EditMode.RENAME){
                            if (data.isHasCollected()){
                                //直接打开微课
                                openCourse(data);
                            }else {
                                data.setIsSelect(!data.isSelect());
                            }
                        }else {
                            data.setIsSelect(!data.isSelect());
                        }

                        getCurrAdapterViewHelper().update();

                        isSelectAll = isSelectAll();
                        checkState(isSelectAll);
                    }
                }
            };
            setCurrAdapterViewHelper(gridView, helper);
        }
        initBottomViews();
    }

    private void enterCampusPatrolSplitCourseListFragment(MediaInfo data) {
        if (data == null){
            return;
        }
        Intent intent = new Intent(getActivity(), CampusPatrolSplitCourseListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(CampusPatrolMainFragment.CAMPUS_PATROL_RESOURCE_ID,
                String.valueOf(data.getNewResourceInfoTag().getCourseInfo().getId()));
        int mediaType = data.getMediaType();
        bundle.putInt(CampusPatrolMainFragment.CAMPUS_PATROL_RESOURCE_TYPE,
                mediaType);
        String title = data.getTitle();
        if (mediaType == MediaType.PPT
                || mediaType == MediaType.PDF
                || mediaType == MediaType.DOC) {
            //ppf和pdf去除xxx.ppt或者xxx.pdf
            if (title != null) {
                if (title.contains(".")) {
                    title = title.substring(0, title.lastIndexOf("."));
                }
            }
        }
        bundle.putString(CampusPatrolMainFragment.CAMPUS_PATROL_RESOURCE_NAME,
                title);
        //分页信息
        if (data.getNewResourceInfoTag() != null) {
            bundle.putParcelable(NewResourceInfoTag.class.
                            getSimpleName(),
                    data.getNewResourceInfoTag());
        }
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void initBottomViews() {
        viewMode = isPick ? CampusPatrolUtils.ViewMode.EDIT : CampusPatrolUtils.ViewMode.NORMAL;
        topLayout = findViewById(R.id.top_layout);
        bottomLayout = findViewById(R.id.bottom_layout);
        bottomSubLayout0 = findViewById(R.id.bottom_sub_layout_0);
        bottomSubLayout1 = findViewById(R.id.bottom_sub_layout_1);
        segLine0 = findViewById(R.id.seg_line_0);
        segLine1 = findViewById(R.id.seg_line_1);
        bottomSegLine0 = findViewById(R.id.bottom_seg_line_0);
        bottomSegLine1 = findViewById(R.id.bottom_seg_line_1);
        createCourse = findViewById(R.id.new_btn);
        createCourse.setOnClickListener(this);

        checkBtn = (ImageView) findViewById(R.id.btn_check);
        if (!isPick) {
            createCourse.setVisibility(View.VISIBLE);
            topLayout.setVisibility(viewMode == CampusPatrolUtils.ViewMode.NORMAL ?
                    View.GONE : View.GONE);
        } else {
            createCourse.setVisibility(View.GONE);
            topLayout.setVisibility(View.GONE);
        }

        topLayout.setOnClickListener(this);
        bottomLayout.setVisibility(View.VISIBLE);

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

        //本地隐藏上传和重命名功能。
        uploadBtn.setVisibility(View.GONE);
        segLine0.setVisibility(View.GONE);
        renameBtn.setVisibility(View.GONE);
        segLine1.setVisibility(View.GONE);

        initBottomLayout(viewMode);
        if (isPick) {
            if (mediaType == MediaType.ONE_PAGE || mediaType == MediaType.MICROCOURSE) {
                //PPT/PDF/有声相册/微课最多1个，拆分页多个。
                maxCount = MAX_ONEPAGE_UPLOAD_COUNT;
            }
        }
    }

    private void initBottomLayout(int viewMode) {
        bottomSubLayout0.setVisibility(viewMode == CampusPatrolUtils.ViewMode.NORMAL ?
                View.VISIBLE : View.INVISIBLE);
        bottomSubLayout1.setVisibility(viewMode != CampusPatrolUtils.ViewMode.NORMAL ?
                View.VISIBLE : View.INVISIBLE);

        //本地隐藏上传和重命名功能。
//        if (viewMode == ViewMode.NORMAL) {
//            uploadBtn.setVisibility(View.VISIBLE);
//            segLine0.setVisibility(View.VISIBLE);
//            renameBtn.setVisibility(View.VISIBLE);
//            segLine1.setVisibility(View.VISIBLE);
//        }
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

    private void openCourse(MediaInfo data) {
        openLocalCourse(data);
    }

    private void openLocalCourse(MediaInfo data) {
        if(data == null) {
            return;
        }
        openLocalCourseDetailForResult(data.getLocalCourseInfo(),
                CampusPatrolPickerFragment.OPEN_COURSE_DETAILS_REQUEST_CODE);
    }

    public void openLocalCourseDetailForResult(LocalCourseInfo info, int requestCode) {
        NewResourceInfo resourceInfo = new NewResourceInfo();
        if(info != null) {
            resourceInfo.setResourceUrl(info.mPath);
            resourceInfo.setTitle(info.mTitle);
            resourceInfo.setThumbnail(info.mPath + File.separator
                    + Utils.RECORD_HEAD_IMAGE_NAME);
            resourceInfo.setDescription(info.mDescription);
        }
        Intent intent = new Intent(getActivity(), PictureBooksDetailActivity.class);
        intent.putExtra(NewResourceInfo.class.getSimpleName(), resourceInfo);
        intent.putExtra(LocalCourseInfo.class.getSimpleName(), info);
        intent.putExtra(PictureBooksDetailActivity.FROM_SOURCE_TYPE,
                PictureBooksDetailActivity.FROM_MY_WORK);
        startActivityForResult(intent,requestCode);
    }

    private void refreshData() {
        loadOnePages();
    }

    private void loadOnePages() {
        UserInfo userInfo = getUserInfo();
        if(userInfo != null) {
            List<LocalCourseDTO> datas = LocalCourseDTO.getAllLocalCourses(getActivity(),
                    getMemeberId(), CourseType.COURSE_TYPE_LOCAL);
            List<MediaInfo> mediaInfos = new ArrayList<MediaInfo>();
            if (datas != null && datas.size() > 0) {
                for (LocalCourseDTO data : datas) {
                    if (data != null) {
                        MediaInfo mediaInfo = new MediaInfo();
                        mediaInfo.setPath(data.getmPath());
                        mediaInfo.setTitle(data.getmTitle());
                        mediaInfo.setThumbnail(BaseUtils.joinFilePath(data.getmPath(),
                                BaseUtils.RECORD_HEAD_IMAGE_NAME));
                        mediaInfo.setMediaType(MediaType.ONE_PAGE);
                        mediaInfo.setMicroId(String.valueOf(data.getmMicroId()));
                        mediaInfo.setLocalCourseInfo(data.toLocalCourseInfo());
                        mediaInfo.setDescription(data.getmDescription());
                        mediaInfos.add(mediaInfo);
                    }
                }
            }
            getCurrAdapterViewHelper().setData(mediaInfos);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contacts_header_right_btn) {
            //选择课件
            selectDataByMediaType();
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
                    //上传课件
                    upload();
                } else if (editMode == CampusPatrolUtils.EditMode.DELETE) {
                    delete(mediaType);
                } else if (editMode == CampusPatrolUtils.EditMode.RENAME) {
                    rename();
                }
            }else {
                //上传选中的课件
                upload();
            }
        } else if (v.getId() == R.id.btn_bottom_cancel) {
            //取消，变成-1。
            editMode = CampusPatrolUtils.EditMode.CANCEL;
            exitEditMode();
            //选择状态下直接返回
            if (isPick) {
                goBack(false);
            }
        }else if (v.getId() == R.id.btn_bottom_select_all){
            //全选/取消全选
            isSelectAll = !isSelectAll;
            checkState(isSelectAll);
            checkData(isSelectAll);
        }else if (v.getId()==R.id.new_btn){
            openCourseType();
        }else {
            super.onClick(v);
        }
    }

    private void upload() {
        List<MediaInfo> mediaInfos = getSelectedData();
        if (mediaInfos == null || mediaInfos.size() == 0) {
            TipMsgHelper.ShowLMsg(getActivity(), R.string.pls_select_files);
            return;
        }
        //选择状态下，未返回前，保留选择状态。
        if (!isPick) {
            exitEditMode();
        }
        if (mediaType == MediaType.ONE_PAGE) {
            uploadCourse(mediaInfos);
        }
    }

    private void uploadCourse(List<MediaInfo> mediaInfos) {
        List<String> titles = new ArrayList<String>();
        for (MediaInfo mediaInfo : mediaInfos) {
            if (mediaInfo != null) {
                titles.add(Utils.removeFileNameSuffix(mediaInfo.getTitle()));
            }
        }
        final MediaInfo mediaInfo = mediaInfos.get(0);
        if(mediaInfo != null) {
            if(!TextUtils.isEmpty(mediaInfo.getMicroId())
                    && Long.parseLong(mediaInfo.getMicroId()) > 0){
                uploadCourseToServer(mediaInfos);
            } else {
                //上传检验
//                checkResourceTitle(mediaInfos, titles, mediaType, CampusPatrolUtils.EditMode.UPLOAD);
                uploadCourseToServer(mediaInfos);
            }
        }
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
                            TipMsgHelper.ShowLMsg(getActivity(),
                                    getString(R.string.cloud_resource_rename_exist,
                                            titleList.get(0)));
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
                    } else if (editMode == CampusPatrolUtils.EditMode.UPLOAD) {
                        if(mediaType == MediaType.ONE_PAGE) {
                            uploadCourseToServer(mediaInfos);
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
                                //更新本地数据
                                if(mediaType == MediaType.ONE_PAGE) {
                                    LocalCourseDTO.updateLocalCourseByResId(getActivity(),
                                            getMemeberId(),
                                            Long.parseLong(mediaInfo.getMicroId()), 0);
                                }
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

    private void uploadCourseToServer(List<MediaInfo> mediaInfos) {
        final UserInfo userInfo = getUserInfo();
        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
            TipMsgHelper.ShowLMsg(getActivity(), R.string.pls_login);
            return;
        }
        final MediaInfo mediaInfo = mediaInfos.get(0);
        if (mediaInfo != null) {
            if(!TextUtils.isEmpty(mediaInfo.getMicroId())
                    && Long.parseLong(mediaInfo.getMicroId()) > 0) {
                WawaCourseUtils wawaCourseUtils = new WawaCourseUtils(getActivity());
                wawaCourseUtils.loadCourseDetail(mediaInfo.getMicroId());
                wawaCourseUtils.setOnCourseDetailFinishListener(new WawaCourseUtils.
                        OnCourseDetailFinishListener() {
                    @Override
                    public void onCourseDetailFinish(CourseData courseData) {
                        if (courseData != null && !TextUtils.isEmpty(courseData.resourceurl)) {
                            mediaInfo.setResourceUrl(courseData.resourceurl);
                            uploadCourseToServer(userInfo, mediaInfo);
                        }
                    }
                });
            } else {
                uploadCourseToServer(userInfo, mediaInfo);
            }
        }
    }

    private void uploadCourseToServer(final UserInfo userInfo, final MediaInfo mediaInfo) {
        final UploadParameter uploadParameter = UploadUtils.getUploadParameter(userInfo,
                mediaInfo, 1);
        if (uploadParameter != null) {
            showLoadingDialog();
            FileZipHelper.ZipUnzipParam param = new FileZipHelper.ZipUnzipParam(
                    mediaInfo.getPath(), Utils.TEMP_FOLDER + Utils.getFileNameFromPath(
                    mediaInfo.getPath()) + Utils.COURSE_SUFFIX);
            FileZipHelper.zip(param,
                    new FileZipHelper.ZipUnzipFileListener() {
                        @Override
                        public void onFinish(
                                FileZipHelper.ZipUnzipResult result) {
                            if (result != null && result.mIsOk) {
                                uploadParameter.setZipFilePath(result.mParam.mOutputPath);
                                UploadUtils.uploadResource(getActivity(), uploadParameter,
                                        new CallbackListener() {
                                    @Override
                                    public void onBack(Object result) {
                                        dismissLoadingDialog();
                                        if (result != null ) {
                                            CourseUploadResult uploadResult =
                                                    (CourseUploadResult) result;
                                            if (uploadResult != null && uploadResult.code == 0){
                                                if (uploadResult.data != null
                                                        && uploadResult.data.size() > 0) {
                                                    CourseData courseData = uploadResult.data.get(0);
                                                    if (courseData != null) {
                                                        int tempMediaType = MediaType.MICROCOURSE;
                                                        if(courseData.type ==
                                                                ResType.RES_TYPE_ONEPAGE) {
                                                            tempMediaType = MediaType.ONE_PAGE;
                                                        }
                                                        updateMediaInfo(getActivity(),
                                                                getUserInfo(),
                                                                uploadResult.getShortCourseInfoList(),
                                                                tempMediaType);
                                                    }
                                                }
                                            }else {
                                                //同一资源删除后，30s内不允许再次上传。
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    });
        }
    }

    public void updateMediaInfo(final Activity activity, UserInfo userInfo,
                                List<ShortCourseInfo> shortCourseInfos, final int mediaType) {
        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
            TipMsgHelper.ShowLMsg(activity, R.string.pls_login);
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", userInfo.getMemberId());
        params.put("MType", String.valueOf(mediaType));
        params.put("MaterialList", shortCourseInfos);
        RequestHelper.sendPostRequest(activity, ServerUrl.PR_UPLOAD_WAWAWEIKE_URL,
                params,
                new RequestHelper.RequestDataResultListener<DataModelResult>(
                        activity, DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()) {
                            return;
                        }
                        TipMsgHelper.ShowLMsg(activity, R.string.upload_file_sucess);
                        //选择状态下上传成功直接返回
                        if (isPick) {
                            goBack(true);
                        }
                    }
                });
    }

    private void openCourseType() {
        Intent intent = new Intent(getActivity(), WawaCourseChoiceActivity.class);
        intent.putExtra(SelectedReadingDetailFragment.Constants.INTORDUCTION_CREATE,false);
        startActivityForResult(intent,CampusPatrolPickerFragment.CREATE_NEW_RESOURCE_REQUEST_CODE);
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

    private void rename(MediaInfo mediaInfo, int mediaType, String title) {
        if (mediaInfo != null && !TextUtils.isEmpty(mediaInfo.getPath())) {
            try {
                if (mediaType == MediaType.ONE_PAGE) {
                    UserInfo userInfo = getUserInfo();
                    if(userInfo != null) {
                        String newPath = BaseUtils.joinFilePath(new File(mediaInfo.getPath())
                                .getParent(), title);
                        LocalCourseDTO dto = new LocalCourseDTO();
                        dto.setmPath(newPath);
                        int rtn = LocalCourseDTO.updateLocalCourse(getActivity(), getMemeberId(),
                                mediaInfo.getPath(), dto);
                        if(rtn > 0) {
                            File oldFile = new File(mediaInfo.getPath());
                            File newFile = new File(newPath);
                            if(oldFile != null && newFile != null) {
                                oldFile.renameTo(newFile);
                            }
                        }
                    }
                    //刷新数据
                    refreshData();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void delete(int mediaType) {
        List<MediaInfo> mediaInfos = getSelectedData();
        if (mediaInfos == null || mediaInfos.size() == 0) {
            TipMsgHelper.ShowLMsg(getActivity(), R.string.pls_select_files);
            return;
        }
        exitEditMode();
        showLoadingDialog();
        for (MediaInfo info : mediaInfos) {
            if (info != null && !TextUtils.isEmpty(info.getPath())) {
                try {
                    if (mediaType == MediaType.ONE_PAGE) {
                        UserInfo userInfo = getUserInfo();
                        if(userInfo != null) {
                            LocalCourseDTO.deleteLocalCourseByPath(getActivity(),
                                    getMemeberId(), info.getPath(), true);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        //删除成功要刷新列表
        refreshData();
        dismissLoadingDialog();
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

    private void goBack(boolean uploaded) {
        if (uploaded) {
            Intent intent = new Intent();
            getActivity().setResult(CampusPatrolPickerFragment.RESULT_CODE_PICKED_COURSE, intent);
        }else {
            getActivity().setResult(CampusPatrolPickerFragment.RESULT_CODE_PICKED_COURSE);
        }
        getActivity().finish();
    }

    private void enterEditMode() {

        topLayout.setVisibility(editMode != CampusPatrolUtils.EditMode.DELETE ?
                View.GONE : View.GONE);
        viewMode = CampusPatrolUtils.ViewMode.EDIT;
        initBottomLayout(viewMode);
        getCurrAdapterViewHelper().update();
    }

    private void selectDataByMediaType() {
        getSelectData();
    }

    public void getSelectData() {
        List<MediaInfo> mediaInfos = getSelectedData();
        if (mediaInfos == null || mediaInfos.size() == 0) {
            TipMsgHelper.ShowLMsg(getActivity(), R.string.pls_select_files);
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
                resourceInfos.add(resourceInfo);
            }
        }
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("resourseInfoList", resourceInfos);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        if (getActivity() != null) {
            getActivity().setResult(getActivity().RESULT_OK, intent);
            getActivity().finish();
        }
    }

    private int transferMediaType(int mediaType) {
        int type = -1;
        switch (mediaType) {
            case MediaType.PPT:
            case MediaType.PDF:
            case MediaType.DOC:
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
        if (data == null) {
            if (requestCode == CampusPatrolPickerFragment.CREATE_NEW_RESOURCE_REQUEST_CODE) {
                //创建资源返回code
                refreshData();
            }else if (requestCode == CampusPatrolPickerFragment.OPEN_COURSE_DETAILS_REQUEST_CODE){
                //微课详情页面数据改变
                refreshData();
            }
        }
    }
}
