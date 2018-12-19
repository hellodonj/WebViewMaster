package com.galaxyschool.app.wawaschool.medias.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import com.galaxyschool.app.wawaschool.Note.OnlineMediaPaperActivity;
import com.galaxyschool.app.wawaschool.PictureBooksDetailActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.CampusPatrolUtils;
import com.galaxyschool.app.wawaschool.common.ScreenUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.db.dto.LocalCourseDTO;
import com.galaxyschool.app.wawaschool.fragment.CampusPatrolMainFragment;
import com.galaxyschool.app.wawaschool.fragment.CampusPatrolPickerFragment;
import com.galaxyschool.app.wawaschool.fragment.ContactsListFragment;
import com.galaxyschool.app.wawaschool.fragment.MyRemoteSplitCourseListFragment;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.medias.activity.MyLocalLQCourseListActivity;
import com.galaxyschool.app.wawaschool.medias.activity.MyRemoteSplitCourseListActivity;
import com.lqwawa.lqbaselib.net.ThisStringRequest;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.MediaInfo;
import com.galaxyschool.app.wawaschool.pojo.MyChoiceMode;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfoTag;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfoTagListResult;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.ResourceTitle;
import com.galaxyschool.app.wawaschool.pojo.ResourceTitleResult;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.galaxyschool.app.wawaschool.views.InputBoxDialog;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.lqwawa.client.pojo.MediaType;
import com.lqwawa.client.pojo.ResourceInfo;
import com.osastudio.common.utils.Constants;
import com.osastudio.common.utils.LQImageLoader;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 远端的原始的LQ课件列表,旨在解耦。
 */
public class MyOriginalRemoteLQCourseListFragment extends ContactsListFragment {

    public static final String TAG = MyOriginalRemoteLQCourseListFragment.class.getSimpleName();

    private static final int MAX_ONEPAGE_UPLOAD_COUNT = 1;
    private static final int MAX_RENAME_COUNT = 1;
    private static final int MAX_UPLOAD_COUNT = 3;
    private boolean isSelectAll;
    //变量
    private int mediaType = MediaType.MICROCOURSE;//微课类型
    private HashMap<String, NewResourceInfoTag> resourceInfoTagHashMap = new HashMap();
    private int viewMode = CampusPatrolUtils.ViewMode.NORMAL;
    private int editMode = CampusPatrolUtils.EditMode.UPLOAD;
    private boolean isPick;
    private int layoutId;
    private int MAX_COLUMN = 2;
    private int maxColumn;
    private int maxCount;
    private View topLayout, bottomLayout, bottomSubLayout0, bottomSubLayout1,
            segLine0, segLine1,bottomSegLine0,bottomSegLine1;
    private TextView uploadBtn, renameBtn, deleteBtn;
    private ImageView checkBtn;
    private TextView cancelBtn,selectAllBtn,confirmBtn;
    private boolean showBottomChoiceLayout = false;//是否显示底部的选择布局

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_original_remote_lq_course_list, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        refreshData();
    }
    private void initViews() {
        if (getArguments() != null) {
            if (getArguments() != null) {
                isPick = getArguments().getBoolean(CampusPatrolUtils.Constants.EXTRA_IS_PICK);
                showBottomChoiceLayout = getArguments().getBoolean(CampusPatrolUtils.Constants
                        .EXTRA_SHOW_CHOICE_BOTTOM_LAYOUT);
            }
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
                textView.setText(getString(R.string.microcourse));
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

            final PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                    R.id.contacts_pull_to_refresh);
            setPullToRefreshView(pullToRefreshView);

            GridView gridView = (GridView) findViewById(R.id.grid_view);
            if (gridView != null) {
                final int min_padding = getResources().getDimensionPixelSize(R.dimen.min_padding);
                //微课和有声相册用单独的布局
                if (mediaType == MediaType.ONE_PAGE || mediaType == MediaType.MICROCOURSE) {
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
                        loadMediaList();
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
                            //云空间显示收藏图片

                            if (data.isHasCollected()) {
                                Drawable leftDrawable = getResources().getDrawable(
                                        R.drawable.icon_star);
                                leftDrawable.setBounds(0, 0, leftDrawable.getMinimumWidth(),
                                        leftDrawable.getMinimumHeight());
                                name.setCompoundDrawables(leftDrawable, null, null, null);
                            } else {
                                name.setCompoundDrawables(null, null, null, null);
                            }

                            if (!TextUtils.isEmpty(data.getThumbnail())) {
                                StringBuilder builder = new StringBuilder();
                                builder.append(AppSettings.getFileUrl(data.getThumbnail()));
                                if (data.getCourseInfo() != null && !TextUtils.isEmpty(
                                        data.getCourseInfo().getUpdateTime())) {
                                    builder.append("?");
                                    builder.append(data.getCourseInfo().getUpdateTime());
                                }
                                LQImageLoader.displayImage(builder.toString(), thumbnail,
                                        R.drawable.default_cover);
                            } else {
                                thumbnail.setImageResource(R.drawable.whiteboard_color);
                            }

                            //标题
                            name.setText(data.getTitle());
                            //微课、有声相册居中对齐。
                            name.setGravity(Gravity.CENTER);
                        }

                        //判断是否显示拆分页
                        if (splitView != null) {
                            splitView.setVisibility(View.GONE);
                            //本地课件没有查看分页
                            if (data.getMediaType() == MediaType.MICROCOURSE) {
                                int resType = data.getResourceType();
                                if (resType == ResType.RES_TYPE_COURSE_SPEAKER) {
                                    //resType == ResType.RES_TYPE_OLD_COURSE ||
//                                        resType == ResType.RES_TYPE_COURSE
                                    //老微课不显示详情，打开的时候也不进入详情。10019是单页的微课，不显示拆分。
                                    splitView.setVisibility(View.VISIBLE);
                                }
                            }
                            splitView.setTag(data);
                            splitView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    MediaInfo mediaInfo = (MediaInfo) v.getTag();
                                    if (mediaInfo != null) {
                                        //跳转到拆分页面
                                        enterMyRemoteSplitCoursePage(data);
                                    }
                                }
                            });
                        }

//                    已收藏的不能重命名
                        if (editMode == CampusPatrolUtils.EditMode.RENAME) {
                            if (data.isHasCollected()) {
                                flag.setVisibility(View.GONE);
                            } else {
                                flag.setVisibility(View.VISIBLE);
                            }
                        } else {
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
                                            } else {
                                                TipMsgHelper.ShowLMsg(getActivity(), getString(R.string
                                                        .n_upload_max_count, String.valueOf(maxCount)));
                                                return;
                                            }
                                        } else if (editMode == CampusPatrolUtils.EditMode.RENAME) {
                                            //单选模式
                                            if (maxCount == 1) {
                                                operateSingleChoiceItem(false, position);
                                            } else {
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
                                        } else {
                                            TipMsgHelper.ShowLMsg(getActivity(), getString(R.string
                                                    .n_pick_max_count, String.valueOf(maxCount)));
                                            return;
                                        }
                                    }
                                }
                            }
                            //已收藏的不能重命名
                            if (editMode == CampusPatrolUtils.EditMode.RENAME) {
                                if (data.isHasCollected()) {
                                    //直接打开微课
                                    openCourse(data);
                                } else {
                                    data.setIsSelect(!data.isSelect());
                                }
                            } else {
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
    }

    /**
     * 进入拆分页面
     * @param data
     */
    private void enterMyRemoteSplitCoursePage(MediaInfo data) {
        if (data == null) {
            return;
        }
        Bundle bundle = new Bundle();
        //是否选择
        bundle.putBoolean(CampusPatrolUtils.Constants.EXTRA_IS_PICK, isPick);
        //选择模式,默认是支持全选。
        bundle.putInt(CampusPatrolUtils.Constants.EXTRA_CHOICE_MODE,
                MyChoiceMode.CHOICE_MODE_MULTIPLE_SUPPORT_ALL);
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
        if (isPick) {
            //选择状态直接替换fragment
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            Fragment fragment = new MyRemoteSplitCourseListFragment();
            fragment.setArguments(bundle);
            ft.addToBackStack(null);
            ft.add(R.id.activity_body, fragment, MyRemoteLQCourseListFragment.TAG);
            ft.commit();
        } else {
            Intent intent = new Intent(getActivity(), MyRemoteSplitCourseListActivity.class);
            //正常状态直接进入
            intent.putExtras(bundle);
            startActivityForResult(intent,
                    CampusPatrolPickerFragment.REQUEST_CODE_DISCUSSION_COURSE_DETAILS);
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
        int resType = data.getResourceType() % ResType.RES_TYPE_BASE;
        if (resType == ResType.RES_TYPE_ONEPAGE) {
            if (data.getNewResourceInfoTag() != null) {
                if (data != null) {
                    NewResourceInfo resourceInfo = data.getNewResourceInfoTag();
                    int fromWhere = PictureBooksDetailActivity.FROM_CLOUD_SPACE;
                    openCourseDetail(resourceInfo, fromWhere);
                }
            }
        } else if (resType == ResType.RES_TYPE_COURSE || resType == ResType.RES_TYPE_COURSE_SPEAKER
                || resType == ResType.RES_TYPE_OLD_COURSE) {
            if (data.getNewResourceInfoTag() != null) {
                if (data != null) {
                    NewResourceInfo resourceInfo = data.getNewResourceInfoTag();
                    int fromWhere = PictureBooksDetailActivity.FROM_CLOUD_SPACE ;
                    if(resType == ResType.RES_TYPE_COURSE || resType == ResType
                            .RES_TYPE_OLD_COURSE) {
                        fromWhere = PictureBooksDetailActivity.FROM_OTHRE;
                    }
                    openCourseDetail(resourceInfo, fromWhere);
                }
            }
        }
    }

    public void openCourseDetail(NewResourceInfo info, int fromType) {
        Intent intent = new Intent(getActivity(), PictureBooksDetailActivity.class);
        intent.putExtra(PictureBooksDetailActivity.NEW_RESOURCE_INFO, info);
        intent.putExtra(PictureBooksDetailActivity.FROM_SOURCE_TYPE, fromType);
        //微课详情页面更新讨论
        startActivityForResult(intent,CampusPatrolPickerFragment.
                REQUEST_CODE_DISCUSSION_COURSE_DETAILS);
    }

    private void refreshData() {
        loadMediaList();
    }

    private void loadMediaList() {

        UserInfo userInfo = getUserInfo();
        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
            return;
        }
        Map<String, Object> mParams = new HashMap<String, Object>();
        mParams.put("MemberId", userInfo.getMemberId());
        //LQ微课拉取1和10
        if (mediaType == MediaType.ONE_PAGE || mediaType == MediaType.MICROCOURSE){
            StringBuilder sb = new StringBuilder();
            sb.append("1").append(",").append("10");
            mParams.put("MType", sb.toString());
        }else {
            mParams.put("MType", String.valueOf(mediaType));
        }
        mParams.put("Pager", getPageHelper().getFetchingPagerArgs());
        RequestHelper.sendPostRequest(getActivity(),ServerUrl.SEARCH_PERSONAL_SPACE_LIST_URL,
                mParams, new DefaultPullToRefreshDataListener
                <NewResourceInfoTagListResult>(NewResourceInfoTagListResult.class) {
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
                            if(mediaType == MediaType.ONE_PAGE||
                                    mediaType == MediaType.MICROCOURSE){
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
                                    if (mediaType == MediaType.PDF
                                            || mediaType == MediaType.PPT
                                            || mediaType == MediaType.DOC)
                                        mediaInfo.setThumbnail(imageUrl);
                                    //PPT/PDF封面打开路径
                                    mediaInfo.setPath(imageUrl);
                                }
                            }
                            mediaInfo.setUpdateTime(resourceInfo.getUpdatedTime());
                            mediaInfo.setShareAddress(resourceInfo.getShareAddress());
                            mediaInfo.setResourceType(resourceInfo.getResourceType());
                            //是否收藏
                            //我的收藏和个人空间区分，0-个人空间，1-我的收藏
                            mediaInfo.setHasCollected(resourceInfo.getType() == 1);
                            if (mediaType == MediaType.MICROCOURSE
                                    || mediaType == MediaType.ONE_PAGE
                                    || mediaType == MediaType.PPT
                                    || mediaType == MediaType.PDF
                                    || mediaType == MediaType.DOC) {
                                mediaInfo.setCourseInfo(resourceInfo.getCourseInfo());
                            }
                            mediaInfo.setNewResourceInfoTag(resourceInfo);
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
                    if (getCurrAdapterViewHelper().hasData()) {
                        int position = getCurrAdapterViewHelper().getData().size();
                        if (position > 0) {
                            position--;
                        }
                        getCurrAdapterViewHelper().getData().addAll(mediaInfos);
                        getCurrAdapterView().setSelection(position);
                    } else {
                        getCurrAdapterViewHelper().setData(mediaInfos);
                    }
                }
            }
        });
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
                selectDataByMediaType();
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

    private void upload() {
        Intent intent = new Intent(getActivity(), MyLocalLQCourseListActivity.class);
        //是否选择
        intent.putExtra(CampusPatrolUtils.Constants.EXTRA_IS_PICK,true);
        startActivityForResult(intent,CampusPatrolPickerFragment.REQUEST_CODE_PICKED_COURSE);
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
                List<String> titles = new ArrayList<String>();
                titles.add(title);
                List<MediaInfo> mediaInfos = new ArrayList<MediaInfo>();
                mediaInfos.add(mediaInfo);
                //重置重命名
                checkResourceTitle(mediaInfos, titles, mediaType, CampusPatrolUtils.EditMode.RENAME);
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
        if (data != null) {
            if (requestCode == CampusPatrolPickerFragment.REQUEST_CODE_PICKED_COURSE) {
                //数据选择成功需要刷新数据
                if (resultCode == CampusPatrolPickerFragment.RESULT_CODE_PICKED_COURSE) {
                    refreshData();
                }
            }
        } else {
            if (requestCode == CampusPatrolPickerFragment.
                    REQUEST_CODE_DISCUSSION_COURSE_DETAILS) {
                //微课详情页返回，更新收藏的条目。
                if (OnlineMediaPaperActivity.hasMicroCourseCollected()) {
                    //收藏返回刷新
                    OnlineMediaPaperActivity.setHasMicroCourseCollected(false);
                    refreshData();
                } else if (requestCode == CampusPatrolPickerFragment.REQUEST_CODE) {
                    //校园巡查筛选逻辑
//                if (data != null) {
//                    startDate = CampusPatrolUtils.getStartDate(data);
//                    endDate = CampusPatrolUtils.getEndDate(data);
//                    loadViews();
//                }
                }
            }
        }
    }
}