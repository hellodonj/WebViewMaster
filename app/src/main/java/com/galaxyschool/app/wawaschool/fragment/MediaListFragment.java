package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.Request;
import com.galaxyschool.app.wawaschool.CampusPatrolSplitCourseListActivity;
import com.galaxyschool.app.wawaschool.Note.OnlineMediaPaperActivity;
import com.galaxyschool.app.wawaschool.PictureBooksDetailActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.WawaCourseChoiceActivity;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.CallbackListener;
import com.galaxyschool.app.wawaschool.common.CampusPatrolUtils;
import com.galaxyschool.app.wawaschool.common.CheckResPermissionHelper;
import com.galaxyschool.app.wawaschool.common.Common;
import com.galaxyschool.app.wawaschool.common.DateUtils;
import com.galaxyschool.app.wawaschool.common.LetvVodHelperNew;
import com.galaxyschool.app.wawaschool.common.RefreshUtil;
import com.galaxyschool.app.wawaschool.common.ScreenUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.common.UploadUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.common.WatchWawaCourseResourceSplicingUtils;
import com.galaxyschool.app.wawaschool.common.WawaCourseUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.config.VipConfig;
import com.galaxyschool.app.wawaschool.db.MediaDao;
import com.galaxyschool.app.wawaschool.db.dto.LocalCourseDTO;
import com.galaxyschool.app.wawaschool.db.dto.MediaDTO;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.MyPageHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.pojo.MaterialResourceType;
import com.lqwawa.lqbaselib.net.ThisStringRequest;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.MediaInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfoTag;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfoTagListResult;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.ResourceTitle;
import com.galaxyschool.app.wawaschool.pojo.ResourceTitleResult;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.SubscribeSchoolListResult;
import com.galaxyschool.app.wawaschool.pojo.TeacherDataStaticsInfo;
import com.galaxyschool.app.wawaschool.pojo.UploadParameter;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseType;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseUploadResult;
import com.galaxyschool.app.wawaschool.pojo.weike.LocalCourseInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.MediaData;
import com.galaxyschool.app.wawaschool.pojo.weike.MediaUploadList;
import com.galaxyschool.app.wawaschool.pojo.weike.ShortCourseInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.SplitCourseInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.SplitCourseInfoListResult;
import com.galaxyschool.app.wawaschool.slide.CreateSlideHelper;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.galaxyschool.app.wawaschool.views.InputBoxDialog;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.ToolbarTopView;
import com.lecloud.skin.ui.utils.VodVideoSettingUtil;
import com.libs.gallery.ImageInfo;
import com.libs.yilib.pickimages.PickMediasFragment;
import com.libs.yilib.pickimages.ScanLocalMediaController;
import com.lqwawa.client.pojo.MediaType;
import com.lqwawa.client.pojo.ResourceInfo;
import com.lqwawa.client.pojo.SourceFromType;
import com.lqwawa.libs.mediapaper.AudioPopwindow;
import com.lqwawa.libs.mediapaper.RecordDialog;
import com.lqwawa.libs.videorecorder.SimpleVideoRecorder;
import com.lqwawa.tools.FileZipHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.oosic.apps.iemaker.base.BaseUtils;
import com.osastudio.common.utils.ConstantSetting;
import com.osastudio.common.utils.Constants;
import com.osastudio.common.utils.FileProviderHelper;
import com.osastudio.common.utils.LQImageLoader;

import org.json.JSONException;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangchao on 12/28/15.
 */
public class MediaListFragment extends ContactsListFragment
        implements ScanLocalMediaController.ScanLocalMediaListener {

    public static final String TAG = MediaListFragment.class.getSimpleName();

    public static final int REQUEST_CODE_CAPTURE_CAMERA = 0;
    public static final int MSG_UPLOAD_MEDIA_FINISH = 0;

    public static final int MAX_COLUMN = 2;

    public static final int PIC_MAX_COLUMN = 4;

    public static final int MAX_ONEPAGE_UPLOAD_COUNT = ConstantSetting.SELECT_COURSEWARE_LOCAL_NUM;
    public static final int MAX_RENAME_COUNT = 1;
    public static final int MAX_UPLOAD_COUNT = ConstantSetting.SELECT_COURSEWARE_PAGING_NUM;
    public static final int MAX_PICK_COUNT = ConstantSetting.SELECT_COURSEWARE_CLOUD_NUM;

    public static final String EXTRA_MEDIA_TYPE = "media_type";
    public static final String EXTRA_MEDIA_NAME = "media_name";
    public static final String EXTRA_IS_REMOTE = "is_remote";
    public static final String EXTRA_IS_PICK = "is_pick";
    public static final String EXTRA_COURSE_ID = "course_id";
    public static final String EXTRA_COURSE_NAME = "course_name";
    public static final String EXTRA_IS_FINISH = "is_finish";
    public static final String EXTRA_IS_SHOW_LQ_TOOLS = "is_show_lq_tools";
    public static final String EXTRA_IS_IMPORT = "is_import";
    public static final String EXTRA_IS_CLOUD = "is_cloud";
    public static final String EXTRA_SHOW_MEDIA_TYPES = "show_media_types";
    public static final String EXTRA_IS_FORM_ONLINE = "is_from_online";
    public static final String EXTRA_SUPPORT_MULTI_TYPE_WATCH_WAWA_COURSE =
            "support_multi_type_watch_wawa_course";//看课件支持多类型
    public static final String EXTRA_RESOURCE_INFO_LIST = "resource_info_list";
    public boolean isShowLQTools;
    private HashMap<String, NewResourceInfoTag> resourceInfoTagHashMap = new HashMap<String, NewResourceInfoTag>();
    private String startDate, endDate;
    private boolean isCampusPatrolTag;
    private TeacherDataStaticsInfo info;
    private static final String LOCAL_MEDIA_REFRESH_ACTION = "local_media_refresh_action";
    //上传本地音频到远端后删除本地音频向本地发送广播，本地接受广播刷新数据
    private MyBroadCastReceiver receiver;
    private NewResourceInfoTag newResourceInfoTag;

    public interface ViewMode {
        int NORMAL = 0;
        int EDIT = 1;
    }

    public interface EditMode {
        int CANCEL = -1;
        int UPLOAD = 0;
        int DELETE = 1;
        int RENAME = 2;
    }

    public interface OnViewModeChangeListener {
        void onViewModeChange(int viewMode, int editMode);
    }

    public interface OnDataLoadedListener {
        void onDataLoaded(int dataSize);
    }

    private boolean isRetellCousrse;
    private boolean isRemote;
    private boolean isPick;
    //是否来自 空中课堂
    private boolean isFromOnline;
    private int mediaType;
    private String mediaName;
    private String courseId;
    private String courseName;
    private int viewMode = ViewMode.NORMAL;
    private int editMode = EditMode.UPLOAD;
    private MediaDao mediaDao;
    private boolean isSelectAll = false;
    private boolean isSplit = false;
    private boolean isAutoUpload = true;

    private ToolbarTopView toolbarTopView;
    private View topLayout, bottomLayout, bottomSubLayout0, bottomSubLayout1,
            segLine0, segLine1, wawacourseToolbarLayout, bottomSegLine0, bottomSegLine1, createCourse, spaceLine;
    private TextView uploadBtn, renameBtn, deleteBtn;
    private ImageView checkBtn;
    private TextView cancelBtn, selectAllBtn, confirmBtn;
    private String picFileName;
    private int maxCount;
    private int maxColumn;
    private int layoutId;

    private ScanLocalMediaController scanLocalMediaController;
    private List<MediaInfo> mediaInfos = new ArrayList<MediaInfo>();

    private OnViewModeChangeListener viewModeChangeListener;
    private OnDataLoadedListener onDataLoadedListener;


    private PullToRefreshView pullToRefreshView;


    private AudioPopwindow audioPopwindow;
    private String uploadRecordFilePath = null;//音频上传路径

    //判断是否收藏的
    private boolean isCollection;
    //收藏的暂时保存的schoolId
    private String collectionSchoolId;
    private boolean isFromChoiceLib;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UPLOAD_MEDIA_FINISH:
                    dismissLoadingDialog();
                    if (msg.obj != null) {
                        MediaUploadList uploadResult = (MediaUploadList) msg.obj;
                        if (uploadResult != null) {
                            if (uploadResult.getCode() == 0) {
                                List<MediaData> datas = uploadResult.getData();
                                if (datas != null && datas.size() > 0) {
                                    updateMediaInfo(getActivity(), getUserInfo(), uploadResult
                                            .getShortCourseInfoList(), mediaType);
                                }
                            } else {
                                TipMsgHelper.ShowLMsg(getActivity(), R.string.upload_file_failed);
                            }
                        }

                    }
                    break;
            }
        }
    };

    private boolean watchWawaCourseSupportMultiType;//看课件支持多类型

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_media_list, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initBroadCastReceiver();
        initViews();
        mediaDao = new MediaDao(getActivity());
        // isAutoUpload = getMyApplication().getPrefsManager().getAutoUploadResource();

        if (!isRemote) {
            ImageLoader.getInstance().clearMemoryCache();
        }
        loadViews();
    }


    @Override
    public void onResume() {
        super.onResume();
        //        loadViews();
        //用于LQ云板点击新建成功之后恢复之前的状态
        if (editMode == EditMode.DELETE) {
            editMode = EditMode.CANCEL;
            exitEditMode();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void initViews() {
        if (getArguments() != null) {
            isRemote = getArguments().getBoolean(EXTRA_IS_REMOTE);
            isPick = getArguments().getBoolean(EXTRA_IS_PICK);
            mediaType = getArguments().getInt(EXTRA_MEDIA_TYPE);
            mediaName = getArguments().getString(EXTRA_MEDIA_NAME);
            courseId = getArguments().getString(EXTRA_COURSE_ID);
            courseName = getArguments().getString(EXTRA_COURSE_NAME);
            isShowLQTools = getArguments().getBoolean(EXTRA_IS_SHOW_LQ_TOOLS);
            isCampusPatrolTag = getArguments().getBoolean(CampusPatrolMainFragment
                    .IS_CAMPUS_PATROL_TAG);
            info = (TeacherDataStaticsInfo) getArguments().
                    getSerializable(TeacherDataStaticsInfo.class.getSimpleName());
            startDate = getArguments().getString(CampusPatrolMainFragment.
                    CAMPUS_PATROL_SCREENING_START_DATE);
            endDate = getArguments().getString(CampusPatrolMainFragment.
                    CAMPUS_PATROL_SCREENING_END_DATE);
            newResourceInfoTag = getArguments().getParcelable(NewResourceInfoTag.class
                    .getSimpleName());
            isFromOnline = getArguments().getBoolean(MediaListFragment.EXTRA_IS_FORM_ONLINE, false);
            watchWawaCourseSupportMultiType = getArguments()
                    .getBoolean(EXTRA_SUPPORT_MULTI_TYPE_WATCH_WAWA_COURSE);
            isSplit = TextUtils.isEmpty(courseId) ? false : true;
            //区分收藏的数据权限
            if (isSplit) {
                if (newResourceInfoTag.getType() == 1) {
                    isCollection = true;
                    collectionSchoolId = newResourceInfoTag.getCollectionOrigin();
                    isFromChoiceLib = newResourceInfoTag.isQualityCourse();
                }
            }
        }
        toolbarTopView = (ToolbarTopView) findViewById(R.id.toolbar_top_view);
        if (toolbarTopView != null) {
            toolbarTopView.setVisibility(isSplit ? View.VISIBLE : View.GONE);
            if (isSplit) {
                toolbarTopView.getTitleView().setText(courseName);
            }
            if (!isRemote) {
                toolbarTopView.getCommitView().setVisibility(mediaType == MediaType.MICROCOURSE ?
                        View.INVISIBLE : View.VISIBLE);
                if (mediaType == MediaType.PICTURE) {
                    toolbarTopView.getCommitView().setText(R.string.take_photo);
                } else if (mediaType == MediaType.AUDIO) {
                    toolbarTopView.getCommitView().setText(R.string.record_video);
                } else if (mediaType == MediaType.VIDEO) {
                    toolbarTopView.getCommitView().setText(R.string.record_video);
                }
            } else {
                toolbarTopView.getCommitView().setVisibility((isPick && isSplit) ? View.VISIBLE :
                        View.INVISIBLE);
                toolbarTopView.getCommitView().setText(R.string.confirm);
            }
            toolbarTopView.getCommitView().setTextColor(getResources().getColor(R.color.text_green));
            //            toolbarTopView.getCommitView().setBackgroundResource(R.drawable.sel_nav_button_bg);
            toolbarTopView.getBackView().setOnClickListener(this);
            toolbarTopView.getCommitView().setOnClickListener(this);
        }

        pullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        if (pullToRefreshView != null) {
            if (!isRemote) {
                setStopPullUpState(true);
                setStopPullDownState(true);
            }
            setPullToRefreshView(pullToRefreshView);
        }

        GridView gridView = (GridView) findViewById(R.id.grid_view);
        if (gridView != null) {
            final int min_padding = getResources().getDimensionPixelSize(R.dimen.min_padding);
            //视频和照片的布局
            layoutId = R.layout.video_grid_item;
            //微课和有声相册用单独的布局
            if (mediaType == MediaType.ONE_PAGE
                    || mediaType == MediaType.MICROCOURSE
                    || mediaType == MediaType.TASK_ORDER) {
                layoutId = R.layout.lq_course_grid_item;
            }
            maxColumn = MAX_COLUMN;
            if (mediaType == MediaType.PICTURE) {
                maxColumn = PIC_MAX_COLUMN;
            }
            gridView.setNumColumns(maxColumn);
            gridView.setBackgroundColor(Color.WHITE);
            gridView.setVerticalSpacing(getResources().getDimensionPixelSize(R.dimen.gridview_spacing));
            gridView.setPadding(min_padding, min_padding, min_padding, min_padding);
            if (mediaType == MediaType.AUDIO) {
                gridView.setNumColumns(1);
                //音频的布局
                layoutId = R.layout.local_media_audio_grid_item;
                gridView.setBackgroundColor(Color.parseColor("#ebebeb"));
                gridView.setVerticalSpacing(getResources().getDimensionPixelSize(R.dimen.listview_divider_height));
                gridView.setPadding(0, 0, 0, 0);
            }

            AdapterViewHelper helper = new AdapterViewHelper(
                    getActivity(), gridView, layoutId) {

                @Override
                public void loadData() {
                    loadDataList();
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
                            if (mediaType == MediaType.PICTURE) {
                                params.width = itemSize;
                                params.height = params.width;
                            } else if (mediaType == MediaType.MICROCOURSE || mediaType == MediaType
                                    .ONE_PAGE || mediaType == MediaType.TASK_ORDER) {
                                params.width = itemSize;
                                params.height = params.width * 9 / 16;
                            } else {
                                params.width = itemSize;
                                params.height = params.width * 3 / 4;
                            }
                            frameLayout.setLayoutParams(params);
                        }
                    }

                    ImageView thumbnail = (ImageView) view.findViewById(R.id.media_thumbnail);
                    TextView name = (TextView) view.findViewById(R.id.media_name);
                    TextView time = (TextView) view.findViewById(R.id.media_time);
                    ImageView flag = (ImageView) view.findViewById(R.id.media_flag);
                    View splitView = view.findViewById(R.id.media_split_btn);
                    //播放暂停按钮
                    ImageView mediaCover = (ImageView) view.findViewById(R.id.media_cover);
                    //收藏
                    ImageView collectImageView = (ImageView) view.findViewById(R.id.icon_collect);
                    if (mediaType == MediaType.AUDIO) {
                        thumbnail.setImageResource(R.drawable.icon_media_audio_green);
                        if (!isRemote) {
                            if (data.getCreateTime() > 0) {
                                String dateFormat = "yyyy-MM-dd HH:mm:ss";
                                time.setText(DateUtils.format(data.getCreateTime(), dateFormat));
                            }
                        } else {
                            time.setText(data.getUpdateTime());
                        }
                    } else if (mediaType == MediaType.VIDEO) {
                        if (!isRemote) {
                            //本机不显示播放按钮

                            thumbnail.setImageBitmap(getVideoThumbnail(data.getPath(), itemSize, itemSize,
                                    MediaStore.Images.Thumbnails.MICRO_KIND));
                        } else {
                            if (!TextUtils.isEmpty(data.getThumbnail())) {
                                LQImageLoader.displayImage(AppSettings.getFileUrl(
                                        data.getThumbnail()), thumbnail, R.drawable.default_cover);
                            } else {
                                thumbnail.setImageResource(R.drawable.default_cover);
                            }
                            //云端显示播放按钮
                            if (mediaCover != null) {
                                if (data.isSelect()) {
                                    mediaCover.setVisibility(View.VISIBLE);
                                } else {
                                    mediaCover.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    } else if (mediaType == MediaType.PICTURE) {
                        LQImageLoader.displayImage(data.getPath(), thumbnail, R.drawable.default_cover);
                    } else if (mediaType == MediaType.ONE_PAGE || mediaType == MediaType.MICROCOURSE || mediaType == MediaType.TASK_ORDER
                            ) {
                        if (!isRemote) {
                            LQImageLoader.DIOptBuiderParam param = new LQImageLoader.DIOptBuiderParam();
                            param.mIsCacheInMemory = true;
                            param.mOutWidth = LQImageLoader.OUT_WIDTH;
                            param.mOutHeight = LQImageLoader.OUT_HEIGHT;
                            param.mDefaultIcon = R.drawable.default_cover;
                            LQImageLoader.displayImage(data.getThumbnail(), thumbnail, param);
                        } else {

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
                        }
                    } else if (mediaType == MediaType.DOC || mediaType == MediaType.PPT || mediaType == MediaType.PDF) {
                        //PPT和PDF缩略图取列表的第一张
                        LQImageLoader.displayImage(data.getThumbnail(), thumbnail,
                                R.drawable.default_cover);
                    }
                    if (mediaType == MediaType.MICROCOURSE || mediaType == MediaType.ONE_PAGE) {
                        if (!isSplit) {
                            name.setText(data.getTitle());
                        } else {
                            name.setText(data.getSubTitle());
                        }
                    } else if (mediaType == MediaType.DOC || mediaType == MediaType.PDF || mediaType == MediaType.PPT) {
                        //pdf、ppt
                        if (!isSplit) {
                            name.setText(data.getTitle());
                        } else {
                            String title = data.getTitle();
                            if (!TextUtils.isEmpty(title)) {
                                if (title.contains("-")) {
                                    title = title.substring(title.lastIndexOf("-") + 1);
                                }
                            }
                            name.setText(title);
                        }
                    } else {
                        name.setText(Utils.removeFileNameSuffix(data.getTitle()));
                        //                        //处理拆分页标题
                        //                        if (!isSplit) {
                        //                            name.setText(Utils.removeFileNameSuffix(data.getTitle()));
                        //                        }else {
                        //                            name.setText(data.getSubTitle());
                        //                        }
                    }

                    //判断对齐方式
                    if (mediaType == MediaType.MICROCOURSE || mediaType == MediaType.ONE_PAGE) {
                        //微课、有声相册居中对齐。
                        name.setGravity(Gravity.CENTER);
                    } else if (mediaType == MediaType.VIDEO || mediaType == MediaType.PICTURE) {
                        //视频、照片居中对齐。
                        name.setGravity(Gravity.CENTER);
                    } else if (mediaType == MediaType.DOC || mediaType == MediaType.PPT || mediaType == MediaType.PDF) {
                        name.setGravity(Gravity.CENTER);
                    }

                    if (mediaType == MediaType.MICROCOURSE || mediaType == MediaType.ONE_PAGE
                            || mediaType == MediaType.TASK_ORDER) {
                        name.setSingleLine(false);
                        name.setPadding(5, 5, 5, 5);
                        name.setTextSize(12);
                        name.setLines(2);
                    }

                    //收藏标识
                    if (collectImageView != null) {
                        if (data.isHasCollected()) {
                            //显示
                            collectImageView.setVisibility(View.VISIBLE);
                        } else {
                            collectImageView.setVisibility(View.GONE);
                        }
                    }

                    //判断是否显示拆分页
                    if (splitView != null) {
                        //默认隐藏查看分页
                        splitView.setVisibility(View.GONE);
                        //只有PPT/PDF/微课才有拆分，其他的没有。
                        if (!isSplit && isRemote) {
                            if (mediaType == MediaType.DOC || mediaType == MediaType.PPT || mediaType == MediaType.PDF) {
                                //选择状态显示，非选择状态隐藏。
                                if (isPick) {
                                    if (isFromOnline) {
                                        splitView.setVisibility(View.GONE);
                                    } else if (watchWawaCourseSupportMultiType) {
                                        //看课件
                                        splitView.setVisibility(View.GONE);
                                    } else {
                                        splitView.setVisibility(View.VISIBLE);
                                    }
                                }
                            } else {
                                if (data.getMediaType() == MediaType.MICROCOURSE) {
                                    int resType = data.getResourceType();
                                    if (resType == ResType.RES_TYPE_COURSE_SPEAKER) {
                                        //resType == ResType.RES_TYPE_OLD_COURSE ||
                                        //                                        resType == ResType.RES_TYPE_COURSE
                                        //老微课不显示详情，打开的时候也不进入详情。10019是单页的微课，不显示拆分。
                                        if (isFromOnline) {
                                            splitView.setVisibility(View.GONE);
                                        } else {
                                            splitView.setVisibility(View.GONE);
                                        }
                                    }
                                }
                            }
                        }

                        splitView.setTag(data);
                        splitView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MediaInfo mediaInfo = (MediaInfo) v.getTag();
                                if (mediaInfo != null) {
                                    if (!isSplit) {
                                        NewResourceInfo newInfo = mediaInfo.getNewResourceInfoTag();
                                        if (newInfo != null) {
                                            collectionSchoolId = newInfo.getCollectionOrigin();
                                        }
                                    }
                                    if (!VipConfig.isVip(getActivity()) && (mediaInfo.getType() == 1 || mediaInfo.isHasCollected()) &&
                                            !TextUtils.isEmpty(collectionSchoolId) && !TextUtils
                                            .equals(getMemeberId(), mediaInfo.getAuthorId())) {
                                        checkCollectionOpenPermission(mediaInfo, 0, true);
                                    } else {
                                        enterSplitResDetail(mediaInfo);
                                    }
                                    //                                    if (!isCampusPatrolTag) {
                                    //                                        FragmentTransaction ft = getActivity().
                                    //                                                getSupportFragmentManager().beginTransaction();
                                    //                                        MediaListFragment fragment = new MediaListFragment();
                                    //                                        Bundle args = getArguments();
                                    //                                        if (mediaInfo.getCourseInfo() != null) {
                                    //                                            args.putString(MediaListFragment.EXTRA_COURSE_ID,
                                    //                                                    String.valueOf(mediaInfo.getCourseInfo().getId()));
                                    //                                        }
                                    //                                        //分页信息
                                    //                                        if (mediaInfo.getNewResourceInfoTag() != null) {
                                    //                                            args.putParcelable(NewResourceInfoTag.class.
                                    //                                                            getSimpleName(),
                                    //                                                    mediaInfo.getNewResourceInfoTag());
                                    //                                        }
                                    //                                        args.putString(MediaListFragment.EXTRA_COURSE_NAME,
                                    //                                                mediaInfo.getTitle());
                                    //                                        fragment.setArguments(args);
                                    //                                        ft.addToBackStack(null);
                                    //                                        ft.add(R.id.container, fragment, MediaListFragment.TAG);
                                    //                                        ft.commit();
                                    //                                    } else {
                                    //                                        enterCampusPatrolSplitCourseListFragment(mediaInfo);
                                    //                                    }

                                }
                            }
                        });
                    }

                    //                    flag.setVisibility(viewMode == ViewMode.NORMAL ? View.INVISIBLE : View.VISIBLE);
                    //                    已收藏的不能重命名

                    ImageView videoTypeView = (ImageView) view.findViewById(R.id.iv_video_type_flag);
                    if (videoTypeView != null) {
                        if (data.getResourceType() == MaterialResourceType.Q_DUBBING_VIDEO) {
                            videoTypeView.setVisibility(View.VISIBLE);
                        } else {
                            videoTypeView.setVisibility(View.GONE);
                        }
                    }

                    TextView tvMark = (TextView) view.findViewById(R.id.tv_auto_mark);
                    if (tvMark != null){
                        if (!TextUtils.isEmpty(data.getResProperties())
                                && TextUtils.equals("1", data.getResProperties())) {
                            tvMark.setVisibility(View.VISIBLE);
                        } else if (!TextUtils.isEmpty(data.getPoint())){
                            //任务单的自动批阅
                            tvMark.setText(getString(R.string.str_auto_mark));
                            tvMark.setVisibility(View.VISIBLE);
                        } else {
                            tvMark.setVisibility(View.GONE);
                        }
                    }

                    if (editMode == EditMode.RENAME) {
                        if (data.isHasCollected()) {
                            flag.setVisibility(View.GONE);
                        } else {
                            flag.setVisibility(View.VISIBLE);
                        }
                    } else {
                        flag.setVisibility(viewMode == ViewMode.NORMAL ? View.INVISIBLE :
                                View.VISIBLE);
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
                    if (viewMode == ViewMode.NORMAL) {
                        //打开资源
                        if (!isSplit) {
                            NewResourceInfo newInfo = data.getNewResourceInfoTag();
                            if (newInfo != null) {
                                collectionSchoolId = newInfo.getCollectionOrigin();
                            }
                        }

                        if (!VipConfig.isVip(getActivity()) && (data.getType() == 1 || data.isHasCollected()) && !TextUtils.isEmpty
                                (collectionSchoolId) && !TextUtils.equals(data.getAuthorId(), getMemeberId())) {
                            checkCollectionOpenPermission(data, position, false);
                        } else {
                            openResource(data, position);
                        }
                    } else {
                        if (!isPick) {
                            if (!data.isSelect()) {
                                int count = getSelectedDataCount();
                                //选择模式
                                if (count > 0 && count >= maxCount) {
                                    if (editMode == EditMode.UPLOAD) {
                                        //单选模式
                                        if (maxCount == 1) {
                                            operateSingleChoiceItem(false, position);
                                        } else {
                                            TipMsgHelper.ShowLMsg(getActivity(), getString(R.string
                                                    .n_upload_max_count, String.valueOf(maxCount)));
                                            return;
                                        }
                                    } else if (editMode == EditMode.RENAME) {
                                        //单选模式
                                        if (maxCount == 1) {
                                            //控制单选逻辑
                                            controlMediaInfoSingleChoiceLogic(data);
                                            getCurrAdapterViewHelper().update();
                                            return;
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
                                        //控制单选逻辑
                                        controlMediaInfoSingleChoiceLogic(data);
                                        getCurrAdapterViewHelper().update();
                                        return;
                                    } else {
                                        TipMsgHelper.ShowLMsg(getActivity(), getString(R.string
                                                .n_pick_max_count, String.valueOf(maxCount)));
                                        return;
                                    }
                                }
                            }
                        }
                        //已收藏的不能重命名
                        if (editMode == EditMode.RENAME) {
                            if (data.isHasCollected() || data.getType() == 1) {
                                //直接打开资源
                                //                                openResource(data, position);
                            } else {
                                //控制选中状态
                                controlMediaInfoSelectStatus(data, !data.isSelect());
                            }
                        } else {
                            //控制选中状态
                            controlMediaInfoSelectStatus(data, !data.isSelect());
                        }

                        getCurrAdapterViewHelper().update();

                        isSelectAll = isSelectAll();
                        checkState(isSelectAll);
                    }
                }
            };
            setCurrAdapterViewHelper(gridView, helper);
        }

        viewMode = isPick ? ViewMode.EDIT : ViewMode.NORMAL;

        topLayout = findViewById(R.id.top_layout);
        bottomLayout = findViewById(R.id.bottom_layout);
        bottomSubLayout0 = findViewById(R.id.bottom_sub_layout_0);
        bottomSubLayout1 = findViewById(R.id.bottom_sub_layout_1);
        segLine0 = findViewById(R.id.seg_line_0);
        segLine1 = findViewById(R.id.seg_line_1);
        bottomSegLine0 = findViewById(R.id.bottom_seg_line_0);
        bottomSegLine1 = findViewById(R.id.bottom_seg_line_1);
        wawacourseToolbarLayout = findViewById(R.id.wawacourse_toolbar_layout);

        createCourse = findViewById(R.id.new_btn);
        spaceLine = findViewById(R.id.wawa_course_space);
        checkBtn = (ImageView) findViewById(R.id.btn_check);
        if (!isPick) {
            topLayout.setVisibility(viewMode == ViewMode.NORMAL ? View.GONE : View.GONE);
        } else {
            topLayout.setVisibility(View.GONE);
        }
        if (!isSplit) {
            if (!isCampusPatrolTag) {
                bottomLayout.setVisibility(isPick ? View.GONE : View.VISIBLE);
            } else {
                //校园巡查逻辑
                bottomLayout.setVisibility(View.GONE);
            }
        } else {
            bottomLayout.setVisibility(View.GONE);
        }
        if (!isRemote) {
            //老版的LQ课件制作
            //          wawacourseToolbarLayout.setVisibility(mediaType == MediaType.ONE_PAGE ? View.VISIBLE : View.GONE);
            createCourse.setVisibility(mediaType == MediaType.ONE_PAGE ? View.VISIBLE : View.GONE);
            createCourse.setOnClickListener(this);
            spaceLine.setVisibility(mediaType == MediaType.ONE_PAGE ? View.VISIBLE : View.GONE);
        } else {
            //            wawacourseToolbarLayout.setVisibility(View.GONE);
            createCourse.setVisibility(View.GONE);
            spaceLine.setVisibility(mediaType == MediaType.ONE_PAGE ? View.VISIBLE : View.GONE);
        }
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
        initBasicLocalView();

        View checkView = findViewById(R.id.top_layout);
        checkView.setOnClickListener(this);
        View view = findViewById(R.id.whiteboard);
        view.setOnClickListener(this);
        view = findViewById(R.id.image);
        view.setOnClickListener(this);
        view = findViewById(R.id.camera);
        view.setOnClickListener(this);
        //全选/取消全选按钮
        view = findViewById(R.id.btn_bottom_select_all);
        view.setOnClickListener(this);
        view = findViewById(R.id.btn_bottom_ok);
        view.setOnClickListener(this);
        view = findViewById(R.id.btn_bottom_cancel);
        view.setOnClickListener(this);
        //        if (!isPick) {
        //            maxCount = mediaType == MediaType.ONE_PAGE ? MAX_ONEPAGE_UPLOAD_COUNT : MAX_UPLOAD_COUNT;
        //        } else {
        //            maxCount = MAX_PICK_COUNT;
        //        }
        if (isPick) {
            if (mediaType == MediaType.PICTURE || mediaType == MediaType.ONE_PAGE || mediaType == MediaType.MICROCOURSE) {
                //图片最多9个
                maxCount = MAX_PICK_COUNT;
            } else if (mediaType == MediaType.AUDIO || mediaType == MediaType.VIDEO) {
                //音频、视频最多9个
                maxCount = MAX_PICK_COUNT;
            } else if (mediaType == MediaType.DOC || mediaType == MediaType.PPT || mediaType == MediaType.PDF) {
                //PPT/PDF/有声相册/微课最多1个，拆分页多个。
                if (!isSplit) {
                    maxCount = MAX_ONEPAGE_UPLOAD_COUNT;
                } else {
                    maxCount = MAX_PICK_COUNT;
                }
            }
            //看课件多类型
            if (watchWawaCourseSupportMultiType) {
                //控制资源最多选多少
                maxCount = WatchWawaCourseResourceSplicingUtils.
                        controlResourcePickedMaxCount(mediaType, maxCount, isSplit);
            }

            //如果数据来自空中课堂
            if (isFromOnline) {
                if (mediaType == MediaType.PICTURE) {
                    maxCount = ConstantSetting.SELECT_PICTURE_NUM;
                } else {
                    maxCount = 1;
                }
            }
        }
    }


    /**
     * 进入查看拆分的界面
     *
     * @param mediaInfo
     */
    private void enterSplitResDetail(MediaInfo mediaInfo) {
        if (!isCampusPatrolTag) {
            FragmentTransaction ft = getActivity().
                    getSupportFragmentManager().beginTransaction();
            MediaListFragment fragment = new MediaListFragment();
            Bundle args = getArguments();
            if (mediaInfo.getCourseInfo() != null) {
                args.putString(MediaListFragment.EXTRA_COURSE_ID,
                        String.valueOf(mediaInfo.getCourseInfo().getId()));
            }
            //分页信息
            if (mediaInfo.getNewResourceInfoTag() != null) {
                args.putParcelable(NewResourceInfoTag.class.
                                getSimpleName(),
                        mediaInfo.getNewResourceInfoTag());
            }
            args.putString(MediaListFragment.EXTRA_COURSE_NAME,
                    mediaInfo.getTitle());
            fragment.setArguments(args);
            ft.addToBackStack(null);
            ft.add(R.id.container, fragment, MediaListFragment.TAG);
            ft.commit();
        } else {
            enterCampusPatrolSplitCourseListFragment(mediaInfo);
        }
    }

    /**
     * 打开资源
     *
     * @param data
     * @param position
     */
    private void openResource(MediaInfo data, int position) {
        UIUtils.currentSourceFromType = SourceFromType.OTHER;
        if (data != null && !TextUtils.isEmpty(data.getPath())) {
            switch (mediaType) {
                case MediaType.MICROCOURSE:
                case MediaType.ONE_PAGE:
                    UIUtils.currentSourceFromType = SourceFromType.PERSONAL_LIBRARY;
                    openCourse(data);
                    break;
                case MediaType.PICTURE:
                    openImage(position);
                    break;
                case MediaType.DOC:
                case MediaType.PDF:
                case MediaType.PPT:
                    if (isSplit) {
                        openImage(position);
                    } else {
                        openImageToPDFAndPPPT(position);
                    }
                    break;
                case MediaType.AUDIO:
                case MediaType.VIDEO:
                    mediaPlay(data);
                    break;
                case MediaType.TASK_ORDER:
                    //非选择界面，跳转到拆分页
                    NewResourceInfoTag newResourceInfoTag = data.getNewResourceInfoTag();
                    newResourceInfoTag.setIsFromLqTask(true);
                    newResourceInfoTag.setTitle(data.getTitle());
                    ActivityUtils.enterTaskOrderDetailActivity(getActivity(), newResourceInfoTag);
                    break;
                default:
            }
        }
    }


    /**
     * 检测收藏的资源是否有权限查看
     */
    private void checkCollectionOpenPermission(final MediaInfo data, final int position, final boolean
            isSplitDetail) {
        NewResourceInfo newResourceInfo = data.getNewResourceInfoTag();
        if (newResourceInfo != null) {
            newResourceInfo.setIsPublicResource(false);
            isFromChoiceLib = newResourceInfo.isQualityCourse();
            collectionSchoolId = newResourceInfo.getCollectionOrigin();
            //校验精品资源库资源的权限
            if (isFromChoiceLib) {
                Map<String, Object> params = new HashMap();
                params.put("MemberId", getMemeberId());
                RequestHelper.RequestListener listener =
                        new RequestHelper.RequestModelResultListener<SubscribeSchoolListResult>(
                                getActivity(), SubscribeSchoolListResult.class) {
                            @Override
                            public void onSuccess(String jsonString) {
                                super.onSuccess(jsonString);
                                SubscribeSchoolListResult result = getResult();
                                List<String> joinSchoolIds = new ArrayList<>();
                                List<SchoolInfo> schoolInfoList = result.getModel().getSubscribeNoList();
                                if (schoolInfoList != null && schoolInfoList.size() > 0) {
                                    for (int i = 0; i < schoolInfoList.size(); i++) {
                                        SchoolInfo info = schoolInfoList.get(i);
                                        if (info.hasJoinedSchool()) {
                                            joinSchoolIds.add(info.getSchoolId());
                                        }
                                    }
                                }
                                boolean flag = false;
                                if (joinSchoolIds.size() > 0) {
                                    String[] splitArray = null;
                                    if (collectionSchoolId.contains(",")) {
                                        splitArray = collectionSchoolId.split(",");
                                    } else {
                                        splitArray = new String[]{collectionSchoolId};
                                    }
                                    for (int i = 0, len = joinSchoolIds.size(); i < len; i++) {
                                        String schoolId = joinSchoolIds.get(i);
                                        for (int j = 0; j < splitArray.length; j++) {
                                            String collectId = splitArray[j];
                                            if (TextUtils.equals(schoolId.toLowerCase(), collectId.toLowerCase())) {
                                                flag = true;
                                            }
                                        }
                                    }
                                }
                                if (flag) {
                                    checkChoicePer(collectionSchoolId, data, position, isSplitDetail);
                                } else {
                                    popPermissionDialog(false, false);
                                }
                            }
                        };
                listener.setShowLoading(true);
                RequestHelper.sendPostRequest(getActivity(), ServerUrl.SUBSCRIBE_SCHOOL_LIST_URL, params, listener);
            } else {
                final int type = newResourceInfo.getResourceType();
                String resId = data.getMicroId();
                if (!resId.contains("-")) {
                    resId = resId + "-" + type;
                }
                String parentId = null;

                if (type == (ResType.RES_TYPE_BASE + ResType.RES_TYPE_COURSE_SPEAKER)) {
                    //如果是分页查询父类的id
                    WawaCourseUtils utils = new WawaCourseUtils(getActivity());
                    utils.loadSplitCourseDetail(Long.parseLong(resId.substring(0, resId.indexOf("-"))));
                    final String finalResId = resId;
                    utils.setOnSplitCourseDetailFinishListener(new WawaCourseUtils.OnSplitCourseDetailFinishListener() {
                        @Override
                        public void onSplitCourseDetailFinish(SplitCourseInfo info) {
                            if (info != null) {
                                checkResPermission(String.valueOf(info.getParentId()), type, finalResId,
                                        isSplitDetail, data, position);
                            }
                        }
                    });
                } else {
                    checkResPermission(parentId, type, resId, isSplitDetail, data, position);
                }
            }
        }
    }

    /**
     * 校验校本资源库的权限
     *
     * @param parentId
     * @param type
     * @param resId
     * @param isSplitDetail
     * @param data
     * @param position
     */
    private void checkResPermission(String parentId, int type, String resId, final boolean
            isSplitDetail, final MediaInfo data, final int position) {
        CheckResPermissionHelper permissionHelper = new CheckResPermissionHelper(getActivity());
        permissionHelper
                .setResType(type)
                .setCouseId(resId)
                .setParentId(parentId)
                .setMemberId(DemoApplication.getInstance().getMemberId())
                .setCollectionId(collectionSchoolId)
                .setCheckListener(new CheckResPermissionHelper
                        .CheckResourceResultListener() {
                    @Override
                    public void onCheckResult(int resType, String courseId,
                                              boolean isPublicResource) {
                        if (isPublicResource) {
                            if (isSplitDetail) {
                                enterSplitResDetail(data);
                            } else {
                                openResource(data, position);
                            }
                        } else {
                            popPermissionDialog(false, false);
                        }
                    }
                })
                .checkResource();
    }

    /**
     * 校验精品资源的权限
     */
    private void checkChoicePer(final String schoolId, final MediaInfo data, final int position, final
    boolean isSplitDetail) {
        if (TextUtils.isEmpty(schoolId) || TextUtils.isEmpty(getMemeberId())) {
            return;
        }
        final int resType = data.getResourceType();
        if (resType > ResType.RES_TYPE_BASE) {
            //拆分
            final int type = resType % ResType.RES_TYPE_BASE;
            WawaCourseUtils wawaCourseUtils = new WawaCourseUtils(getActivity());
            wawaCourseUtils.loadSplitCourseDetail(Long.parseLong(data.getMicroId()));
            wawaCourseUtils.setOnSplitCourseDetailFinishListener(new WawaCourseUtils.OnSplitCourseDetailFinishListener() {

                @Override
                public void onSplitCourseDetailFinish(SplitCourseInfo info) {
                    if (info != null) {
                        checkChoiceResourcePermission(
                                schoolId,
                                info.getParentId() + "-" + type,
                                data,
                                position,
                                isSplitDetail);
                    }
                }
            });
        } else {
            String courseId = data.getMicroId();
            if (!courseId.contains("-")) {
                courseId = courseId + "-" + resType;
            }
            checkChoiceResourcePermission(
                    schoolId,
                    courseId,
                    data,
                    position,
                    isSplitDetail);
        }
    }


    /**
     * 检查是否有精品资源库的权限
     */
    private void checkChoiceResourcePermission(String schoolId, String courseId, final MediaInfo
            data, final int position, final boolean isSplitDetail) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("SchoolIds", schoolId);
        params.put("ResId", courseId);
        params.put("MemberId", getMemeberId());
        final RequestHelper.RequestListener listener =
                new RequestHelper.RequestModelResultListener<ModelResult>(
                        getActivity(), ModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        org.json.JSONObject jsonObject = null;
                        try {
                            jsonObject = new org.json.JSONObject(jsonString);
                            if (jsonObject != null) {
                                boolean flag = jsonObject.optBoolean("Model");
                                if (flag) {
                                    if (isSplitDetail) {
                                        enterSplitResDetail(data);
                                    } else {
                                        openResource(data, position);
                                    }
                                } else {
                                    String errorMessage = jsonObject.optString("errorMessage");
                                    if (TextUtils.isEmpty(errorMessage)) {
                                        popPermissionDialog(true, false);
                                    } else {
                                        popPermissionDialog(true, true);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.CHECK_AUTH_LIB_PERMISSION_BASE_URL, params,
                listener);
    }

    private void popPermissionDialog(boolean isFromChoiceLibPop, boolean isDeleted) {
        String content = getString(R.string.hava_lq_course_permission);
        if (isDeleted) {
            content = getString(R.string.res_is_not_exist);
        } else if (isFromChoiceLibPop) {
            content = getString(R.string.choice_library_resource_over_date);
        }
        ContactsMessageDialog dialog = new ContactsMessageDialog(getActivity(), null, content,
                "", null, getString(R.string.str_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.setIsAutoDismiss(true);
        dialog.setCancelable(false);
        dialog.show();
    }


    /**
     * 控制单选逻辑
     *
     * @param data
     */
    private void controlMediaInfoSingleChoiceLogic(MediaInfo data) {
        if (data == null) {
            return;
        }
        List<MediaInfo> list = getCurrAdapterViewHelper().getData();
        if (list != null && list.size() > 0) {
            for (MediaInfo info : list) {
                if (info != null) {
                    //单选逻辑
                    if (!TextUtils.isEmpty(info.getId())
                            && !TextUtils.isEmpty(data.getId())
                            && info.getId().equals(data.getId())) {
                        //操作的对象，单选和取消单选。
                        controlMediaInfoSelectStatus(data, !data.isSelect());
                    } else {
                        //其余对象，均设置为未选中状态。
                        controlMediaInfoSelectStatus(info, false);
                    }
                }
            }
        }
    }

    /**
     * 控制选中状态
     */
    private void controlMediaInfoSelectStatus(MediaInfo data, boolean selected) {
        if (data != null) {
            //设置选中状态
            data.setIsSelect(selected);
            if (!TextUtils.isEmpty(data.getId())) {
                if (selected) {
                    //选中
                    RefreshUtil.getInstance().addId(data.getId());
                } else {
                    //移除
                    RefreshUtil.getInstance().removeId(data.getId());
                }
            }
        }
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

    private void enterCampusPatrolSplitCourseListFragment(MediaInfo data) {

        if (data == null) {
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
        if (mediaType == MediaType.DOC || mediaType == MediaType.PPT || mediaType == MediaType.PDF) {
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

    public void initBasicLocalView() {

        if (isShowLQTools) {
            bottomSubLayout0.setVisibility(View.VISIBLE);
            bottomSubLayout1.setVisibility(View.GONE);
            segLine0.setVisibility(View.GONE);
            segLine1.setVisibility(View.GONE);
            deleteBtn.setVisibility(View.VISIBLE);
        }
    }

    private void initBottomLayout(int viewMode) {
        bottomSubLayout0.setVisibility(viewMode == ViewMode.NORMAL ? View.VISIBLE : View.INVISIBLE);
        bottomSubLayout1.setVisibility(viewMode != ViewMode.NORMAL ? View.VISIBLE : View.INVISIBLE);
        if (viewMode == ViewMode.NORMAL) {
            if (!isRemote) {
                //只显示本机
                //                if (isShowLQTools){
                //                    bottomSubLayout0.setVisibility(View.VISIBLE);
                //                    bottomSubLayout1.setVisibility(View.GONE);
                //                }else {
                bottomSubLayout0.setVisibility(View.GONE);
                bottomSubLayout1.setVisibility(View.VISIBLE);
                //                }
                // 本机选择后，隐藏“上传”和“删除”按钮。
                uploadBtn.setVisibility(View.GONE);
                //                uploadBtn.setVisibility(View.VISIBLE);
                segLine0.setVisibility(mediaType == MediaType.PICTURE ? View.GONE : View.VISIBLE);
                renameBtn.setVisibility(View.GONE);
                segLine1.setVisibility(View.GONE);
                deleteBtn.setVisibility(mediaType == MediaType.PICTURE ? View.GONE : View.GONE);
                //                deleteBtn.setVisibility(mediaType == MediaType.PICTURE ? View.GONE : View.VISIBLE);
            } else {
                //open first
                //DOC PPT和PDF暂时隐藏上传按钮
                if (mediaType == MediaType.DOC || mediaType == MediaType.PDF || mediaType == MediaType.PPT || mediaType == MediaType.TASK_ORDER) {
                    uploadBtn.setVisibility(View.GONE);
                    segLine0.setVisibility(View.GONE);
                } else {
                    uploadBtn.setVisibility(View.VISIBLE);
                    segLine0.setVisibility(View.VISIBLE);
                }
                //                uploadBtn.setVisibility(View.GONE);
                //                segLine0.setVisibility(View.GONE);
                renameBtn.setVisibility(mediaType == MediaType.MICROCOURSE ? View.GONE :
                        View.VISIBLE);
                segLine1.setVisibility(mediaType == MediaType.MICROCOURSE ? View.GONE :
                        View.VISIBLE);
            }
        }
        //上传（支持上传多个，但是有数量限制）和重命名（单选）的时候，不支持全选，只有删除的时候支持全选。
        if (editMode == EditMode.UPLOAD || editMode == EditMode.RENAME) {
            selectAllBtn.setVisibility(View.GONE);
            bottomSegLine0.setVisibility(View.GONE);
        } else {
            //其他的支持
            selectAllBtn.setVisibility(View.VISIBLE);
            bottomSegLine0.setVisibility(View.VISIBLE);
        }
    }

    private void enterEditMode() {
        topLayout.setVisibility(editMode != EditMode.DELETE ? View.GONE : View.GONE);
        toolbarTopView.getCommitView().setVisibility(View.INVISIBLE);
        viewMode = ViewMode.EDIT;
        initBottomLayout(viewMode);
        getCurrAdapterViewHelper().update();
        if (viewModeChangeListener != null) {
            viewModeChangeListener.onViewModeChange(viewMode, editMode);
        }
    }

    public void exitEditMode() {
        topLayout.setVisibility(View.GONE);
        toolbarTopView.getCommitView().setVisibility(View.VISIBLE);
        viewMode = ViewMode.NORMAL;
        if (isShowLQTools && editMode == EditMode.DELETE) {
            editMode = EditMode.CANCEL;
        }
        //重置未选择时的状态
        if (isRemote && editMode == EditMode.RENAME) {
            editMode = EditMode.CANCEL;
        }
        initBottomLayout(viewMode);
        isSelectAll = false;
        checkData(isSelectAll);
        checkState(isSelectAll);
        if (viewModeChangeListener != null) {
            viewModeChangeListener.onViewModeChange(viewMode, editMode);
        }
    }

    private void checkState(boolean isSelectAll) {
        //更新全选布局
        if (isSelectAll) {
            selectAllBtn.setText(getString(R.string.cancel_to_select_all));

        } else {
            selectAllBtn.setText(getString(R.string.select_all));
        }
        checkBtn.setImageResource(isSelectAll ? R.drawable.select : R.drawable.unselect);
    }

    private void checkData(boolean isSelectAll) {
        List datas = getCurrAdapterViewHelper().getData();
        if (datas != null && datas.size() > 0) {
            for (Object data : datas) {
                MediaInfo mediaInfo = (MediaInfo) data;
                if (mediaInfo != null) {
                    //控制全选/取消全选
                    controlMediaInfoSelectStatus(mediaInfo, isSelectAll);
                }
            }
        }
        getCurrAdapterViewHelper().update();
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

    private int getSelectedDataCount() {
        List<MediaInfo> mediaInfos = getSelectedData();
        if (mediaInfos == null) {
            return 0;
        }
        return mediaInfos.size();
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

    private void delete(final int deleteMediaType) {
        final List<MediaInfo> mediaInfos = getSelectedData();
        if (mediaInfos == null || mediaInfos.size() == 0) {
            TipMsgHelper.ShowLMsg(getActivity(), R.string.pls_select_files);
            return;
        }
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(
                getActivity(),
                null,
                getString(R.string.confirm_delete),
                getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                },
                getString(R.string.confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        deleteCourseData(mediaInfos,deleteMediaType);
                    }
                });
        messageDialog.show();
    }

    private void deleteCourseData(List<MediaInfo> mediaInfos,int mediaType){
        exitEditMode();
        if (!isRemote) {
            showLoadingDialog();
            for (MediaInfo info : mediaInfos) {
                if (info != null && !TextUtils.isEmpty(info.getPath())) {
                    try {
                        if (mediaType == MediaType.ONE_PAGE) {
                            UserInfo userInfo = getUserInfo();
                            if (userInfo != null) {
                                LocalCourseDTO.deleteLocalCourseByPath(getActivity(),
                                        getMemeberId(), info.getPath(), true);
                            }
                        } else if (mediaType == MediaType.AUDIO || mediaType == MediaType.VIDEO) {
                            int id = mediaDao.deleteMediaDTOByPath(info.getPath(), mediaType);
                            if (id > 0) {
                                Utils.deleteFile(info.getPath());
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (mediaType == MediaType.ONE_PAGE) {
                loadOnePages();
            } else if (mediaType == MediaType.AUDIO || mediaType == MediaType.VIDEO) {
                loadLocalMedias();
            }
            dismissLoadingDialog();
            //删除成功的提示
            TipMsgHelper.ShowMsg(getActivity(),R.string.cs_delete_success);
        } else {
            deleteMedias(mediaInfos, mediaType);
        }
    }

    private void deleteMedias(final List<MediaInfo> mediaInfos, final int mediaType) {
        if (mediaType == MediaType.TASK_ORDER) {
            deleteTaskOrder(mediaInfos, mediaType);
            return;
        }
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
                            if (mediaType == MediaType.ONE_PAGE && userInfo != null) {
                                //                                DraftData.updateDraftByResId(getActivity(),
                                //                                        Long.parseLong(info.getMicroId()), 0, userInfo.getMemberId());
                                LocalCourseDTO.updateLocalCourseByResId(getActivity(),
                                        getMemeberId(), Long.parseLong(info.getMicroId()), 0);

                            }
                        }
                    }
                    //删除完毕后，刷新数据。
                    getCurrAdapterViewHelper().update();
                    loadViews();
                }
            }
        });
    }

    /**
     * 删除任务单
     */
    private void deleteTaskOrder(final List<MediaInfo> mediaInfos, final int mediaType) {
        List<ShortCourseInfo> shortCourseInfos = new ArrayList<>();
        if (mediaInfos.size() == 0) {
            return;
        }
        int size = mediaInfos.size();
        for (int i = 0; i < size; i++) {
            ShortCourseInfo info = new ShortCourseInfo();
            MediaInfo mediaInfo = mediaInfos.get(i);
            if (mediaInfo != null) {
                if (!TextUtils.isEmpty(mediaInfo.getMicroId())) {
                    info.setMicroID(mediaInfo.getMicroId() + "-" + mediaInfo.getResourceType());
                }
                info.setMaterialId(mediaInfo.getId());
                if (mediaInfo.isHasCollected()) {//收藏
                    info.setCustomType("5");
                } else {
                    info.setCustomType("0");
                }

            }
            shortCourseInfos.add(info);

        }

        String url = ServerUrl.PR_DELETE_TASK_ORDER_URL;

        RequestHelper.postRequest(getActivity(),
                url, JSONObject.toJSONString(shortCourseInfos),
                new DefaultDataListener<DataModelResult>(DataModelResult.class) {
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
                        TipMsgHelper.ShowLMsg(getActivity(), R.string.cs_delete_success);
                        if (mediaInfos != null && mediaInfos.size() > 0) {
                            for (MediaInfo info : mediaInfos) {
                                if (info != null) {
                                    getCurrAdapterViewHelper().getData().remove(info);

                                }
                                //删除完毕后，刷新数据。
                                getCurrAdapterViewHelper().update();
                                loadViews();
                            }
                        }
                    }
                });
    }


    private void upload(int mediaType) {
        List<MediaInfo> mediaInfos = getSelectedData();
        if (mediaInfos == null || mediaInfos.size() == 0) {
            TipMsgHelper.ShowLMsg(getActivity(), R.string.pls_select_files);
            return;
        }
        exitEditMode();
        if (mediaType == MediaType.ONE_PAGE) {
            uploadCourse(mediaInfos);
        } else {
            uploadMedias(mediaInfos);
        }
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
            showEditDialog(getString(R.string.rename_file), dataTitle, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String title = ((InputBoxDialog) dialog).getInputText().trim();
                    if (TextUtils.isEmpty(title)) {
                        TipMsgHelper.ShowLMsg(getActivity(), R.string.pls_enter_title);
                        return;
                    }
                    if (!TextUtils.isEmpty(dataTitle) && title.equals(dataTitle)) {
                        TipMsgHelper.ShowLMsg(getActivity(), getString(R.string
                                .cloud_resource_rename_exist, dataTitle));
                        return;
                    }
                    //                    title = title.replaceAll("[\\t\\n\\r]", "");
                    //过滤特殊字符
                    boolean isValid = Utils.checkEditTextValid(getActivity(), title);
                    if (!isValid) {
                        return;
                    }
                    if (title.length() > Constants.MAX_TITLE_LENGTH) {
                        TipMsgHelper.ShowLMsg(getActivity(), getString(
                                R.string.words_count_over_limit));
                        return;
                    }
                    dialog.dismiss();
                    rename(mediaInfo, mediaType, title);
                }
            }).show();
        }
    }

    private void rename(MediaInfo mediaInfo, int mediaType, String title) {
        if (!isRemote) {
            if (mediaInfo != null && !TextUtils.isEmpty(mediaInfo.getPath())) {
                try {
                    if (mediaType == MediaType.ONE_PAGE) {
                        UserInfo userInfo = getUserInfo();
                        if (userInfo != null) {
                            //                            DraftData.updateDraftByChwPath(getActivity(), mediaInfo.getPath(), title, userInfo.getMemberId());
                            String newPath = BaseUtils.joinFilePath(new File(mediaInfo.getPath())
                                    .getParent(), title);
                            LocalCourseDTO dto = new LocalCourseDTO();
                            dto.setmPath(newPath);
                            int rtn = LocalCourseDTO.updateLocalCourse(getActivity(), getMemeberId(),
                                    mediaInfo.getPath(), dto);
                            if (rtn > 0) {
                                File oldFile = new File(mediaInfo.getPath());
                                File newFile = new File(newPath);
                                if (oldFile != null && newFile != null) {
                                    oldFile.renameTo(newFile);
                                }
                            }
                        }
                        loadOnePages();
                    } else if (mediaType == MediaType.AUDIO || mediaType == MediaType.VIDEO) {
                        MediaDTO mediaDTO = new MediaDTO();
                        mediaDTO.setTitle(title);
                        mediaDao.updateMediaDTO(mediaInfo.getPath(), mediaType, mediaDTO);
                        loadLocalMedias();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            if ((mediaType == MediaType.MICROCOURSE || mediaType == MediaType.ONE_PAGE
                    || mediaType == MediaType.TASK_ORDER)) {
                //lq课件重命名去掉
                renameMedia(mediaInfo, title);
            } else {
                List<String> titles = new ArrayList<String>();
                titles.add(title);
                List<MediaInfo> mediaInfos = new ArrayList<MediaInfo>();
                mediaInfos.add(mediaInfo);
                //重置重命名
                //            editMode = EditMode.RENAME;
                //            checkResourceTitle(mediaInfos, titles, mediaType, editMode);
                checkResourceTitle(mediaInfos, titles, mediaType, EditMode.RENAME);
            }
        }
    }

    private void renameMedia(final MediaInfo mediaInfo, final String title) {
        final UserInfo userInfo = getUserInfo();
        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
            TipMsgHelper.ShowLMsg(getActivity(), R.string.pls_login);
            return;
        }
        String microId = null;

        if (mediaInfo != null && !TextUtils.isEmpty(mediaInfo.getMicroId())) {
            microId = mediaInfo.getMicroId();
            if (microId.contains("-")) {
                microId = (microId.split("-"))[0];
            }
        } else {
            return;
        }

        if (TextUtils.isEmpty(title)) {
            return;
        }
        showLoadingDialog();
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("memberId", userInfo.getMemberId());
        try {
            jsonObject.put("fileName", URLEncoder.encode(Utils.changeTitleValid(title), "utf-8"));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        jsonObject.put("resId", Long.parseLong(microId));
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
                                upDateRenameData(mediaInfo, title);
                                //                                loadResourceList(mediaType);
                                if (mediaType == MediaType.ONE_PAGE) {
                                    //                                    DraftData.updateDraftByResId(getActivity(), Long.parseLong(mediaInfo.getMicroId()), 0, userInfo.getMemberId());
                                    LocalCourseDTO.updateLocalCourseByResId(getActivity(),
                                            getMemeberId(), Long.parseLong(mediaInfo.getMicroId()), 0);
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
     * 当前（2017.6.27）去掉重命名的检测的代码
     */
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
                        if (editMode == EditMode.RENAME) {
                            TipMsgHelper.ShowLMsg(getActivity(), getString(R.string.cloud_resource_rename_exist, titleList.get(0)));
                        } else if (editMode == EditMode.UPLOAD) {
                            StringBuilder builder = new StringBuilder();
                            int size = titleList.size();
                            String title;
                            for (int i = 0; i < size - 1; i++) {
                                title = titleList.get(i);
                                if (mediaType == MediaType.PICTURE) {
                                    title = Utils.removeFileNameSuffix(title);
                                }
                                builder.append(title + ",");
                            }
                            title = titleList.get(size - 1);
                            if (mediaType == MediaType.PICTURE) {
                                title = Utils.removeFileNameSuffix(title);
                            }
                            builder.append(title);
                            //防止重名后，选择回弹。
                            clickUpload();
                            showUploadInfoDialog(builder.toString());
                        }
                    }
                } else {
                    if (editMode == EditMode.RENAME) {
                        if (mediaInfos != null && mediaInfos.size() > 0 && titles.size() > 0) {
                            renameMedia(mediaInfos.get(0), titles.get(0));
                        }
                    } else if (editMode == EditMode.UPLOAD) {
                        if (mediaType == MediaType.ONE_PAGE) {
                            uploadCourseToServer(mediaInfos);
                        } else {
                            uploadMediasToServer(mediaInfos);
                        }
                    }
                }
            }
        };
        listener.setShowErrorTips(false);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.CHECK_CLOUD_RESOURCE_TITLE_URL,
                params, listener);
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
        dialog.getEditText().setImeOptions(EditorInfo.IME_ACTION_DONE);
        dialog.getEditText().setInputType(EditorInfo.TYPE_CLASS_TEXT);
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


    public void loadViews() {
        //        if (getCurrAdapterViewHelper().hasData()) {
        //            getCurrAdapterViewHelper().update();
        //        } else {
        getPageHelper().clear();
        loadDataList();

        if (!isRemote) {
            ImageLoader.getInstance().clearMemoryCache();
        }
        //        }
    }

    public void clickUpload() {
        if (uploadBtn != null) {
            onClick(uploadBtn);
        }
        //        //隐藏白板+语音等制作布局。
        //        if (wawacourseToolbarLayout != null){
        //            wawacourseToolbarLayout.setVisibility(View.GONE);
        //        }
        if (createCourse != null && spaceLine != null) {
            createCourse.setVisibility(View.GONE);
            spaceLine.setVisibility(View.GONE);
        }
    }

    public void updateViews() {
        loadDataList();
        isSelectAll = false;
        checkState(false);
        if (isRemote) {
            pullToRefreshView.showRefresh();
        }
    }

    private void loadDataList() {
        if (!isRemote) {
            loadLocalData();
        } else {
            if (!isSplit) {
                resourceInfoTagHashMap.clear();
                if (!isCampusPatrolTag) {
                    if (isRemote) {
                        if (isFetchingFirstPage()) {
                            pullToRefreshView.showRefresh();
                        }
                    }
                    loadMediaList(mediaType);
                } else {
                    //校园巡查逻辑
                    loadCampusPatrolMaterialData(mediaType);
                }
            } else {
                //拆分页面禁止滑动
                pullToRefreshView.setRefreshEnable(false);

                if (mediaType == MediaType.MICROCOURSE || mediaType == MediaType.ONE_PAGE) {
                    if (isRemote) {
                        if (isFetchingFirstPage()) {
                            pullToRefreshView.showRefresh();
                        }
                    }
                    loadSplitCourseList(courseId);
                } else {
                    //其他类型的分页信息是直接传入的
                    List<MediaInfo> mediaInfos = new ArrayList<MediaInfo>();
                    if (newResourceInfoTag != null && newResourceInfoTag.getSplitInfoList() != null) {
                        //父类的microId
                        String parentMicroId = newResourceInfoTag.getMicroId();
                        //父类的标题
                        String parentTitle = newResourceInfoTag.getTitle();
                        List<NewResourceInfo> resourceInfos = newResourceInfoTag.getSplitInfoList();
                        for (int i = 0, size = resourceInfos.size(); i < size; i++) {
                            NewResourceInfo resourceInfo = resourceInfos.get(i);
                            MediaInfo mediaInfo = new MediaInfo();
                            mediaInfo.setPath(resourceInfo.getResourceUrl());
                            mediaInfo.setMediaType(mediaType);
                            mediaInfo.setId(resourceInfo.getId());
                            //标题
                            String title = resourceInfo.getTitle();
                            if (!TextUtils.isEmpty(parentTitle) && !TextUtils.isEmpty(title)) {
                                title = parentTitle + "-" + title;
                            }
                            mediaInfo.setTitle(title);
                            //                            //设置分页的标题
                            //                            mediaInfo.setSubTitle(resourceInfo.getTitle());
                            mediaInfo.setThumbnail(resourceInfo.getResourceUrl());
                            mediaInfo.setShareAddress(resourceInfo.getShareAddress());
                            mediaInfo.setResourceType(resourceInfo.getResourceType());
                            //设置resId
                            mediaInfo.setMicroId(parentMicroId);
                            //设置作者id
                            mediaInfo.setAuthorId(resourceInfo.getAuthorId());
                            mediaInfos.add(mediaInfo);
                        }
                    }
                    getCurrAdapterViewHelper().setData(mediaInfos);
                }
            }
        }
    }

    /**
     * 判断是否是加载第一页
     *
     * @return
     */
    private boolean isFetchingFirstPage() {
        boolean isFetchingFirstPage = false;
        MyPageHelper helper = getPageHelper();
        if (helper != null) {
            isFetchingFirstPage = helper.isFetchingFirstPage();
        }
        return isFetchingFirstPage;
    }

    /**
     * 加载校园巡查数据
     *
     * @param mediaType
     */
    private void loadCampusPatrolMaterialData(final int mediaType) {
        if (info == null) {
            return;
        }
        Map<String, Object> params = new HashMap();
        //资源类型,必须,1:微课2:图片3音频4:视频5收藏素材
        if (mediaType == MediaType.ONE_PAGE || mediaType == MediaType.MICROCOURSE) {
            params.put("MType", "1,10");
        } else {
            params.put("MType", mediaType);
        }
        // 用户ID，必须
        params.put("MemberId", info.getTeacherId());
        //关键字,非必填。
        params.put("KeyWord", "");

        if (!TextUtils.isEmpty(startDate)) {//时间格式：2016-12-11
            params.put("StrStartTime", startDate);//统计开始时间,非必填。
        }
        if (!TextUtils.isEmpty(endDate)) {//时间格式：2016-12-11
            params.put("StrEndTime", endDate);//统计结束时间,非必填。
        }
        //分页信息,必填。
        params.put("Pager", getPageHelper().getFetchingPagerArgs());
        DefaultPullToRefreshDataListener listener =
                new DefaultPullToRefreshDataListener<NewResourceInfoTagListResult>(
                        NewResourceInfoTagListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        NewResourceInfoTagListResult result = getResult();
                        if (result == null || !result.isSuccess()
                                || result.getModel() == null) {
                            return;
                        }
                        if (getPageHelper().isFetchingPageIndex(result.getModel().getPager())) {
                            //获得总记录数
                            int totalCount = getPageHelper().getTotalCount();
                            List<NewResourceInfoTag> list = result.getModel().getData();
                            //过滤数据
                            list = Utils.filterPersonalResourceListData(list, mediaType);
                            if (list == null || list.size() <= 0) {
                                if (getPageHelper().isFetchingFirstPage()) {
                                    getCurrAdapterViewHelper().clearData();
                                    //更新标题数量
                                    if (onDataLoadedListener != null) {
                                        onDataLoadedListener.onDataLoaded(totalCount);
                                    }
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
                                    if (mediaType == MediaType.ONE_PAGE && !TextUtils.isEmpty(
                                            resourceInfo.getUpdatedTime())) {
                                        builder.append("?");
                                        builder.append(resourceInfo.getUpdatedTime());
                                    }
                                    mediaInfo.setPath(builder.toString());
                                    if (mediaType == MediaType.ONE_PAGE || mediaType ==
                                            MediaType.MICROCOURSE) {
                                        if (resourceInfo.isMicroCourse()) {
                                            mediaInfo.setMediaType(MediaType.MICROCOURSE);
                                        } else {
                                            mediaInfo.setMediaType(MediaType.ONE_PAGE);
                                        }
                                    } else {
                                        mediaInfo.setMediaType(mediaType);
                                    }
                                    mediaInfo.setId(resourceInfo.getId());
                                    mediaInfo.setMicroId(resourceInfo.getMicroId());
                                    mediaInfo.setTitle(resourceInfo.getTitle());
                                    mediaInfo.setThumbnail(resourceInfo.getThumbnail());
                                    //PPT和PDF缩略图取列表第一张
                                    List<NewResourceInfo> splitList = resourceInfo.getSplitInfoList();
                                    if (splitList != null && splitList.size() > 0) {
                                        String imageUrl = null;
                                        imageUrl = splitList.get(0).getResourceUrl();
                                        if (!TextUtils.isEmpty(imageUrl)) {
                                            if (mediaType == MediaType.DOC || mediaType == MediaType.PDF || mediaType ==
                                                    MediaType.PPT)
                                                mediaInfo.setThumbnail(imageUrl);
                                            //PPT/PDF封面打开路径
                                            mediaInfo.setPath(imageUrl);
                                        }
                                    }
                                    mediaInfo.setUpdateTime(resourceInfo.getUpdatedTime());
                                    mediaInfo.setShareAddress(resourceInfo.getShareAddress());
                                    mediaInfo.setResourceType(resourceInfo.getResourceType());
                                    mediaInfo.setHasSplitData(resourceInfo.getSplitInfoList() != null);
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
                                    if (!isSplit) {
                                        //                                        mediaInfo.setNewResourceInfo(resourceInfo);
                                        mediaInfo.setNewResourceInfoTag(resourceInfo);
                                    }
                                    //设置作者id
                                    mediaInfo.setAuthorId(resourceInfo.getAuthorId());
                                    //设置播放地址
                                    String vUid = "";
                                    String leValue = resourceInfo.getLeValue();
                                    if (!TextUtils.isEmpty(leValue)) {
                                        String[] values = leValue.split("&");
                                        if (values != null && values.length > 2) {
                                            String[] vUidArray = values[2].split("=");
                                            if (vUidArray != null && vUidArray.length > 1) {
                                                vUid = vUidArray[1];
                                            }
                                        }
                                    }
                                    mediaInfo.setVuid(vUid);
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
                            //选择或者编辑状态下，恢复选择状态
                            if (isPick || viewMode == ViewMode.EDIT) {
                                List<MediaInfo> resultList = getCurrAdapterViewHelper().getData();
                                RefreshUtil.getInstance().refresh(resultList);
                            }
                            //更新标题数量
                            if (onDataLoadedListener != null) {
                                onDataLoadedListener.onDataLoaded(totalCount);
                            }
                        }
                    }

                    @Override
                    public void onError(NetroidError error) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onError(error);
                    }
                };
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.PR_LOAD_MEDIA_LIST_URL, params, listener);

    }

    private void loadLocalData() {
        if (mediaType != MediaType.MICROCOURSE) {
            if (mediaType == MediaType.ONE_PAGE) {
                loadOnePages();
            } else if (mediaType == MediaType.PICTURE) {
                loadLocalPictures();
            } else {
                loadLocalMedias();
            }
        }
    }

    private void loadMediaList(final int mediaType) {
        UserInfo userInfo = getUserInfo();
        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
            return;
        }
        Map<String, Object> mParams = new HashMap<String, Object>();
        mParams.put("MemberId", userInfo.getMemberId());
        //收藏时过滤课件不是自己的
        if (isPick) {
            mParams.put("Author", userInfo.getMemberId());
        }
        //LQ微课拉取1和10
        if (mediaType == MediaType.ONE_PAGE || mediaType == MediaType.MICROCOURSE) {
            StringBuilder sb = new StringBuilder();
            sb.append("1").append(",").append("10");
            mParams.put("MType", sb.toString());
        } else if (MediaType.TASK_ORDER == mediaType) {
            mParams.put("CreatorId", getMemeberId());
            mParams.put("IsContainsMyCollection", true);
            mParams.put("PageIndex", getPageHelper().getFetchingPagerArgs().getPageIndex());
            mParams.put("PageSize", getPageHelper().getFetchingPagerArgs().getPageSize());
        } else {
            mParams.put("MType", String.valueOf(mediaType));
        }
        mParams.put("Pager", getPageHelper().getFetchingPagerArgs());
        String url = ServerUrl.SEARCH_PERSONAL_SPACE_LIST_URL;
        if (MediaType.TASK_ORDER == mediaType) {
            url = ServerUrl.GET_GUIDANCE_CARD_URL;
        }
        postRequest(url, mParams, new DefaultDataListener
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

                if (getPageHelper().isFetchingPageIndex(result.getModel().getPager()) || MediaType.TASK_ORDER == mediaType) {
                    List<NewResourceInfoTag> list = result.getModel().getData();
                    //过滤数据
                    list = Utils.filterPersonalResourceListData(list, mediaType);
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
                            if (mediaType == MediaType.ONE_PAGE &&
                                    !TextUtils.isEmpty(resourceInfo.getUpdatedTime())) {
                                builder.append("?");
                                builder.append(resourceInfo.getUpdatedTime());
                            }
                            mediaInfo.setPath(builder.toString());
                            if (mediaType == MediaType.ONE_PAGE || mediaType == MediaType.MICROCOURSE) {
                                if (resourceInfo.isMicroCourse()) {
                                    mediaInfo.setMediaType(MediaType.MICROCOURSE);
                                } else {
                                    mediaInfo.setMediaType(MediaType.ONE_PAGE);
                                }
                            } else {
                                mediaInfo.setMediaType(mediaType);
                            }
                            mediaInfo.setResProperties(resourceInfo.getResProperties());
                            mediaInfo.setId(resourceInfo.getId());
                            mediaInfo.setMicroId(resourceInfo.getMicroId());
                            mediaInfo.setTitle(resourceInfo.getTitle());
                            StringBuilder thumbnailBuilder = new StringBuilder();
                            thumbnailBuilder.append(resourceInfo.getThumbnail());
                            if (mediaType == MediaType.TASK_ORDER && !TextUtils.isEmpty(resourceInfo.getUpdateTime())) {
                                thumbnailBuilder.append("?");
                                thumbnailBuilder.append(resourceInfo.getUpdateTime());
                            }
                            mediaInfo.setThumbnail(thumbnailBuilder.toString());
                            //PPT和PDF缩略图取列表第一张
                            List<NewResourceInfo> splitList = resourceInfo.getSplitInfoList();
                            if (splitList != null && splitList.size() > 0) {
                                String imageUrl = null;
                                imageUrl = splitList.get(0).getResourceUrl();
                                if (!TextUtils.isEmpty(imageUrl)) {
                                    if (mediaType == MediaType.DOC || mediaType == MediaType.PDF || mediaType == MediaType.PPT)
                                        mediaInfo.setThumbnail(imageUrl);
                                    //PPT/PDF封面打开路径
                                    mediaInfo.setPath(imageUrl);
                                }
                            }
                            mediaInfo.setUpdateTime(resourceInfo.getUpdatedTime());
                            mediaInfo.setShareAddress(resourceInfo.getShareAddress());
                            mediaInfo.setResourceType(resourceInfo.getResourceType());
                            //是否有拆分页
                            //                            mediaInfo.setHasSplitData(resourceInfo.getSplitInfoList() != null &&
                            //                            resourceInfo.getSplitInfoList().size() > 0);
                            mediaInfo.setHasSplitData(resourceInfo.getSplitInfoList() != null);
                            //是否收藏
                            //我的收藏和个人空间区分，0-个人空间，1-我的收藏
                            mediaInfo.setHasCollected(resourceInfo.getType() == 1);
                            if (mediaType == MediaType.TASK_ORDER) {
                                mediaInfo.setHasCollected(resourceInfo.isMyCollection());
                            }
                            if (mediaType == MediaType.MICROCOURSE
                                    || mediaType == MediaType.ONE_PAGE
                                    || mediaType == MediaType.PPT
                                    || mediaType == MediaType.PDF
                                    || mediaType == MediaType.DOC) {
                                mediaInfo.setCourseInfo(resourceInfo.getCourseInfo());
                            }

                            mediaInfo.setNewResourceInfoTag(resourceInfo);
                            //设置作者id
                            mediaInfo.setAuthorId(resourceInfo.getAuthorId());
                            //设置播放地址
                            String vUid = "";
                            String leValue = resourceInfo.getLeValue();
                            if (!TextUtils.isEmpty(leValue)) {
                                String[] values = leValue.split("&");
                                if (values != null && values.length > 2) {
                                    String[] vUidArray = values[2].split("=");
                                    if (vUidArray != null && vUidArray.length > 1) {
                                        vUid = vUidArray[1];
                                    }
                                }
                            }
                            mediaInfo.setVuid(vUid);
                            mediaInfo.setPoint(resourceInfo.getPoint());
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
                    //选择或者编辑状态下，恢复选择状态
                    if (isPick || viewMode == ViewMode.EDIT) {
                        List<MediaInfo> resultList = getCurrAdapterViewHelper().getData();
                        RefreshUtil.getInstance().refresh(resultList);
                    }
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                pullToRefreshView.hideRefresh();
            }
        });
    }

    private void loadSplitCourseList(String courseId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pid", courseId);
        StringBuilder builder = new StringBuilder();
        builder.append("?j=" + jsonObject.toJSONString());
        String url = ServerUrl.SPLIT_COURSE_LIST_URL + builder.toString();
        final ThisStringRequest request = new ThisStringRequest(
                Request.Method.GET, url, new Listener<String>() {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                if (jsonString != null) {
                    SplitCourseInfoListResult result = JSON.parseObject(jsonString,
                            SplitCourseInfoListResult.class);
                    if (result != null && result.getData() == null) {
                        return;
                    }
                    mediaInfos.clear();
                    List<SplitCourseInfo> splitCourseInfos = result.getData();
                    for (int i = 0, size = splitCourseInfos.size(); i < size; i++) {
                        SplitCourseInfo info = splitCourseInfos.get(i);
                        if (info != null) {
                            MediaInfo mediaInfo = new MediaInfo();
                            //播放路径
                            mediaInfo.setPath(info.getPlayUrl());
                            mediaInfo.setMediaType(mediaType);
                            mediaInfo.setId(String.valueOf(info.getId()));
                            mediaInfo.setTitle(info.getFullResName());
                            mediaInfo.setSubTitle(info.getSubResName());
                            //缩略图地址
                            mediaInfo.setThumbnail(info.getThumbUrl());
                            mediaInfo.setShareAddress(info.getShareUrl());
                            mediaInfo.setResourceType(info.getSubResType());
                            mediaInfo.setCourseInfo(info.getCourseInfo());
                            //设置资源作者id
                            mediaInfo.setAuthorId(info.getMemberId());

                            //add NewResourceInfoTag
                            NewResourceInfoTag infoTag = new NewResourceInfoTag();
                            infoTag.setResourceUrl(info.getPlayUrl());
                            infoTag.setResourceType(mediaInfo.getResourceType());
                            infoTag.setId(mediaInfo.getId());
                            infoTag.setTitle(mediaInfo.getTitle());
                            infoTag.setMicroId(info.getId() + "");
                            //equals media type
                            infoTag.setType(mediaInfo.getType());
                            infoTag.setShareAddress(mediaInfo.getShareAddress());
                            infoTag.setThumbnail(mediaInfo.getThumbnail());
                            infoTag.setAuthorId(info.getMemberId());
                            infoTag.setAuthorName(info.getCreateName());
                            //设置分页的屏幕方向
                            infoTag.setScreenType(info.getScreenType());
                            mediaInfo.setNewResourceInfoTag(infoTag);
                            mediaInfos.add(mediaInfo);
                        }
                    }
                    getCurrAdapterViewHelper().setData(mediaInfos);
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                pullToRefreshView.hideRefresh();
            }

        });
        request.addHeader("Accept-Encoding", "*");
        request.start(getActivity());
    }

    private void loadLocalMedias() {
        try {
            List<MediaDTO> mediaDTOs = mediaDao.getMediaDTOs(mediaType);
            List<MediaInfo> mediaInfos = new ArrayList<MediaInfo>();
            if (mediaDTOs != null && mediaDTOs.size() > 0) {
                for (MediaDTO dto : mediaDTOs) {
                    if (dto != null) {
                        String path = dto.getPath();
                        if (!TextUtils.isEmpty(path)) {
                            if (new File(path).exists()) {
                                mediaInfos.add(dto.toMediaInfo());
                            } else {
                                mediaDao.deleteMediaDTOByPath(path, mediaType);
                            }
                        }
                    }
                }
            }
            getCurrAdapterViewHelper().setData(mediaInfos);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadLocalPictures() {
        scanLocalMediaController = new ScanLocalMediaController(
                com.libs.yilib.pickimages.MediaType.MEDIA_TYPE_PHOTO, PickMediasFragment.
                getImageFormatList(), this);
        List<String> paths = com.osastudio.common.utils.Utils.getVolumeList(getActivity());
        scanLocalMediaController.start(paths);
        scanLocalMediaController.setSkipKeysOfFolder(AppSettings.getScanFilesSkipKeys());
        mediaInfos.clear();
        //        showLoadingDialog();
    }

    private void loadOnePages() {
        UserInfo userInfo = getUserInfo();
        if (userInfo != null) {
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
                        mediaInfo.setDuration(data.getmDuration());
                        mediaInfos.add(mediaInfo);
                    }
                }
            }
            getCurrAdapterViewHelper().setData(mediaInfos);
        }
    }

    private Bitmap getVideoThumbnail(String videoPath, int width, int height,
                                     int kind) {
        //        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        //        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
        //            ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        //        return bitmap;
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(videoPath);
            bitmap = retriever.getFrameAtTime(0);
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    public void cameraVideo() {
        String dateFormat = "yyyyMMdd_HHmmss";
        String fileName = "VID_" + DateUtils.format(System.currentTimeMillis(), dateFormat) + ".mp4";

        Bundle args = new Bundle();
        args.putString(SimpleVideoRecorder.EXTRA_VIDEO_PATH,
                new File(Utils.VIDEO_FOLDER, fileName).getAbsolutePath());
        Intent intent = new Intent(getActivity(), SimpleVideoRecorder.class);
        intent.putExtras(args);
        try {
            getParentFragment().startActivityForResult(intent,
                    SimpleVideoRecorder.REQUEST_CODE_CAPTURE_VIDEO);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void cameraImage() {
        Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
        String dateFormat = "yyyyMMdd_HHmmss";
        String fileName = "IMG_" + DateUtils.format(System.currentTimeMillis(), dateFormat) + ".jpg";
        picFileName = getSaveImagePath(fileName);
        getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, FileProviderHelper.getUriForFile(getActivity(), new File(picFileName)));
        getImageByCamera.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        getParentFragment().startActivityForResult(getImageByCamera, REQUEST_CODE_CAPTURE_CAMERA);

    }

    public void recordAudio(View v) {
        File audioFile = new File(Utils.AUDIO_FOLDER);
        if (!audioFile.exists()) {
            audioFile.mkdirs();
        }
        audioPopwindow = new AudioPopwindow(getActivity(), Utils.AUDIO_FOLDER, new AudioPopwindow.OnUploadListener() {
            @Override
            public void onUpload(String path) {
                if (TextUtils.isEmpty(path)) {
                    return;
                }
                uploadRecordFilePath = path;
                File file = new File(path);
                if (file.exists()) {
                    long time = System.currentTimeMillis();
                    String fileName = file.getName();

                    MediaDTO mediaDTO = new MediaDTO();
                    mediaDTO.setPath(path);
                    mediaDTO.setTitle(fileName);
                    mediaDTO.setMediaType(MediaType.AUDIO);
                    mediaDTO.setCreateTime(time);
                    MediaDao mediaDao = new MediaDao(getActivity());
                    mediaDao.addOrUpdateMediaDTO(mediaDTO);
                    loadLocalMedias();
                    if (isAutoUpload) {
                        MediaInfo mediaInfo = mediaDTO.toMediaInfo();
                        List<MediaInfo> mediaInfos = new ArrayList<MediaInfo>();
                        if (mediaInfo != null) {
                            mediaInfos.add(mediaInfo);
                        }
                        uploadMedias(mediaInfos);
                    }
                }
            }
        });
        if (audioPopwindow != null) {
            audioPopwindow.setAnimationStyle(R.style.AnimBottom);
            audioPopwindow.showPopupMenu(v);
        }
    }

    public void recordAudio() {
        File audioFile = new File(Utils.AUDIO_FOLDER);
        if (!audioFile.exists()) {
            audioFile.mkdirs();
        }
        int dialogWith = ScreenUtils.getScreenWidth(getActivity()) * 3 / 4;
        RecordDialog dialog = new RecordDialog(getActivity(), dialogWith, Utils.AUDIO_FOLDER, new RecordDialog.RecordFinishListener() {
            @Override
            public void onRecordFinish(String path) {
                if (TextUtils.isEmpty(path)) {
                    return;
                }
                File file = new File(path);
                if (file.exists()) {
                    long time = System.currentTimeMillis();
                    String fileName = Utils.getFileNameFromPath(path);
                    String title = fileName.substring(0, fileName.lastIndexOf("."));
                    if (!TextUtils.isEmpty(title) && TextUtils.isDigitsOnly(title)) {
                        time = Long.parseLong(title);
                        String dateFormat = "yyyyMMdd_HHmmss";
                        fileName = "AUD_" + DateUtils.format(time, dateFormat) + ".m4a";
                        File newFile = new File(Utils.AUDIO_FOLDER, fileName);
                        file.renameTo(newFile);
                        path = newFile.getAbsolutePath();
                    }
                    MediaDTO mediaDTO = new MediaDTO();
                    mediaDTO.setPath(path);
                    mediaDTO.setTitle(fileName);
                    mediaDTO.setMediaType(MediaType.AUDIO);
                    mediaDTO.setCreateTime(time);
                    MediaDao mediaDao = new MediaDao(getActivity());
                    mediaDao.addOrUpdateMediaDTO(mediaDTO);
                    loadLocalMedias();
                    if (isAutoUpload) {
                        MediaInfo mediaInfo = mediaDTO.toMediaInfo();
                        List<MediaInfo> mediaInfos = new ArrayList<MediaInfo>();
                        if (mediaInfo != null) {
                            mediaInfos.add(mediaInfo);
                        }
                        uploadMedias(mediaInfos);
                    }
                }
            }
        });
        dialog.show();
    }

    private void mediaPlay(MediaInfo data) {
        if (data == null) {
            return;
        }

        String filePath = data.getPath();
        if (isRemote) {
            filePath = AppSettings.getFileUrl(data.getPath());
        }
        int media_type = VodVideoSettingUtil.VIDEO_TYPE;
        if (data.getMediaType() == MediaType.AUDIO) {
            media_type = VodVideoSettingUtil.AUDIO_TYPE;
        }
        String leValue = data.getNewResourceInfoTag().getLeValue();

        LetvVodHelperNew.VodVideoBuilder builder = new LetvVodHelperNew.VodVideoBuilder
                (getActivity())
                .setNewUI(true)//使用自定义UI
                .setHideBtnMore(true)//个人资源库隐藏收藏pop
                .setTitle(data.getTitle())//视频标题
                .setMediaType(media_type)//路径
                .setLeStatus(data.getNewResourceInfoTag().getLeStatus());

        if (TextUtils.isEmpty(leValue)) {
            builder.setUrl(filePath);
            builder.create();
        } else {
            String[] values = leValue.split("&");
            String uUid = values[1].split("=")[1];
            String vUid = values[2].split("=")[1];
            builder.setUuid(uUid);
            builder.setVuid(vUid);
            builder.setUrl(filePath);
            builder.create();
        }
    }


    private void openImageToPDFAndPPPT(int position) {
        List<MediaInfo> mediaInfos = getCurrAdapterViewHelper().getData();
        List<ImageInfo> resourceInfoList = new ArrayList<>();
        MediaInfo mediaInfo = mediaInfos.get(position);
        if (mediaInfo.getNewResourceInfoTag() == null || mediaInfo.getNewResourceInfoTag().getSplitInfoList()
                == null || mediaInfo.getNewResourceInfoTag().getSplitInfoList().size() == 0) {
            TipMsgHelper.ShowLMsg(getActivity(), R.string.ppt_pdf_not_have_pic);
            return;
        }
        NewResourceInfoTag infoTag = mediaInfo.getNewResourceInfoTag();
        for (NewResourceInfo ResourceInfo : infoTag.getSplitInfoList()) {
            ImageInfo newResourceInfo = new ImageInfo();
            newResourceInfo.setTitle(mediaInfo.getTitle());
            newResourceInfo.setResourceUrl(AppSettings.getFileUrl(ResourceInfo.getResourceUrl()));
            newResourceInfo.setId(mediaInfo.getId());
            newResourceInfo.setResourceType(mediaInfo.getResourceType());
            newResourceInfo.setMicroId(mediaInfo.getMicroId());
            newResourceInfo.setAuthorId(mediaInfo.getAuthorId());
            resourceInfoList.add(newResourceInfo);
        }
        boolean isHideMoreBtn = infoTag.isQualityCourse() && !VipConfig.isVip(getActivity());
        ActivityUtils.openImage(getActivity(), resourceInfoList, true, 0, isHideMoreBtn, true, false);
    }

    private void openImage(int position) {
        List<MediaInfo> mediaInfos = getCurrAdapterViewHelper().getData();
        List<ImageInfo> resourceInfoList = new ArrayList<>();
        for (MediaInfo mediaInfo : mediaInfos) {
            ImageInfo newResourceInfo = new ImageInfo();
            newResourceInfo.setTitle(mediaInfo.getTitle());
            newResourceInfo.setResourceUrl(AppSettings.getFileUrl(mediaInfo.getPath()));
            newResourceInfo.setId(mediaInfo.getId());
            newResourceInfo.setResourceType(mediaInfo.getResourceType());
            newResourceInfo.setMicroId(mediaInfo.getMicroId());
            newResourceInfo.setAuthorId(mediaInfo.getAuthorId());
            resourceInfoList.add(newResourceInfo);
        }
        ActivityUtils.openImage(getActivity(), resourceInfoList, false, position, false, true,
                false);
    }


    public void createSlide(int slideType) {
        if (viewMode == ViewMode.EDIT) {
            //编辑本机的时候，制作的话，按钮恢复初始状态。
            exitEditMode();
        }

        int haveFree = Utils.checkStorageSpace(getActivity());
        if (haveFree == 0) {
            CreateSlideHelper.CreateSlideParam param = new CreateSlideHelper.CreateSlideParam();
            param.mFragment = getParentFragment();
            param.mEntryType = Common.LIST_TYPE_SHARE;
            param.mEditable = true;
            param.mSlideType = slideType;
            if (slideType == CreateSlideHelper.SLIDETYPE_IMAGE) {
                param.mIsPickOneImage = false;
            }
            UserInfo userInfo = getUserInfo();
            if (userInfo != null && !TextUtils.isEmpty(userInfo.getMemberId())) {
                param.mMemberId = userInfo.getMemberId();
            }
            param.mSlideSaveBtnParam = new CreateSlideHelper.SlideSaveBtnParam(true, true, false);
            param.mOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            CreateSlideHelper.createSlide(param);
        }
    }

    public void openCourseDetail(NewResourceInfo info, int fromType) {
        Intent intent = new Intent(getActivity(), PictureBooksDetailActivity.class);
        intent.putExtra(PictureBooksDetailActivity.NEW_RESOURCE_INFO, info);
        intent.putExtra(PictureBooksDetailActivity.FROM_SOURCE_TYPE, fromType);
        //微课详情页面更新讨论
        startActivityForResult(intent, CampusPatrolPickerFragment.
                REQUEST_CODE_DISCUSSION_COURSE_DETAILS);
    }

    private void openCourse(MediaInfo data) {
        if (!isRemote) {
            openLocalCourse(data);
        } else {
            int resType = data.getResourceType() % ResType.RES_TYPE_BASE;
            if (resType == ResType.RES_TYPE_ONEPAGE) {
                if (data.getNewResourceInfoTag() != null) {
                    if (data != null) {
                        NewResourceInfo resourceInfo = data.getNewResourceInfoTag();
                        int fromWhere = PictureBooksDetailActivity.FROM_CLOUD_SPACE;
                        if (isCampusPatrolTag) {
                            //校园巡查需要显示“分享”
                            fromWhere = PictureBooksDetailActivity.FROM_OTHRE;
                        }
                        openCourseDetail(resourceInfo, fromWhere);
                    }
                }
            } else if (resType == ResType.RES_TYPE_COURSE || resType == ResType.RES_TYPE_COURSE_SPEAKER
                    || resType == ResType.RES_TYPE_OLD_COURSE) {
                if (data.getNewResourceInfoTag() != null) {
                    if (data != null) {
                        NewResourceInfo resourceInfo = data.getNewResourceInfoTag();
                        int fromWhere = PictureBooksDetailActivity.FROM_CLOUD_SPACE;
                        if (resType == ResType.RES_TYPE_COURSE || resType == ResType
                                .RES_TYPE_OLD_COURSE) {
                            fromWhere = PictureBooksDetailActivity.FROM_OTHRE;
                        }
                        if (isCampusPatrolTag) {
                            //校园巡查需要显示“分享”
                            fromWhere = PictureBooksDetailActivity.FROM_OTHRE;
                        }
                        //拆分过来的收藏数据保存
                        if (isCollection && isSplit) {
                            resourceInfo.setType(1);
                            resourceInfo.setCollectionOrigin(collectionSchoolId);
                            resourceInfo.setIsQualityCourse(isFromChoiceLib);
                        }
                        openCourseDetail(resourceInfo, fromWhere);
                    }
                }
            }
        }
    }

    private void openLocalCourse(MediaInfo data) {
        if (data == null) {
            return;
        }
        openLocalCourseDetailForResult(data.getLocalCourseInfo(),
                CampusPatrolPickerFragment.OPEN_COURSE_DETAILS_REQUEST_CODE);
    }

    public void openLocalCourseDetailForResult(LocalCourseInfo info, int requestCode) {
        NewResourceInfo resourceInfo = new NewResourceInfo();
        if (info != null) {
            resourceInfo.setResourceUrl(info.mPath);
            resourceInfo.setTitle(info.mTitle);
            resourceInfo.setThumbnail(info.mPath + File.separator
                    + Utils.RECORD_HEAD_IMAGE_NAME);
            resourceInfo.setDescription(info.mDescription);
            resourceInfo.setScreenType(info.mOrientation);
        }
        Intent intent = new Intent(getActivity(), PictureBooksDetailActivity.class);
        intent.putExtra(NewResourceInfo.class.getSimpleName(), resourceInfo);
        intent.putExtra(LocalCourseInfo.class.getSimpleName(), info);
        intent.putExtra(PictureBooksDetailActivity.FROM_SOURCE_TYPE,
                PictureBooksDetailActivity.FROM_MY_WORK);
        startActivityForResult(intent, requestCode);
    }

    private void uploadCourse(final List<MediaInfo> mediaInfos) {
        List<String> titles = new ArrayList<String>();
        for (MediaInfo mediaInfo : mediaInfos) {
            if (mediaInfo != null) {
                titles.add(Utils.removeFileNameSuffix(mediaInfo.getTitle()));
            }
        }
        final MediaInfo mediaInfo = mediaInfos.get(0);
        if (mediaInfo != null) {
            if (!TextUtils.isEmpty(mediaInfo.getMicroId()) && Long.parseLong(mediaInfo.getMicroId()) > 0) {
                uploadCourseToServer(mediaInfos);
            } else {
                //上传检验
                if (mediaType == MediaType.MICROCOURSE || mediaType == MediaType.ONE_PAGE) {
                    uploadCourseToServer(mediaInfos);
                } else {
                    checkResourceTitle(mediaInfos, titles, mediaType, EditMode.UPLOAD);
                }
            }
        }
    }

    private void uploadCourseToServer(List<MediaInfo> mediaInfos) {
        final UserInfo userInfo = getUserInfo();
        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
            TipMsgHelper.ShowLMsg(getActivity(), R.string.pls_login);
            return;
        }
        final MediaInfo mediaInfo = mediaInfos.get(0);
        if (mediaInfo != null) {
            if (!TextUtils.isEmpty(mediaInfo.getMicroId()) && Long.parseLong(mediaInfo.getMicroId()) > 0) {
                WawaCourseUtils wawaCourseUtils = new WawaCourseUtils(getActivity());
                wawaCourseUtils.loadCourseDetail(mediaInfo.getMicroId());
                wawaCourseUtils.setOnCourseDetailFinishListener(new WawaCourseUtils.OnCourseDetailFinishListener() {
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
        final UploadParameter uploadParameter = UploadUtils.getUploadParameter(userInfo, mediaInfo, 1);
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
                                UploadUtils.uploadResource(getActivity(), uploadParameter, new CallbackListener() {
                                    @Override
                                    public void onBack(Object result) {
                                        dismissLoadingDialog();
                                        if (result != null) {
                                            CourseUploadResult uploadResult = (CourseUploadResult) result;
                                            if (uploadResult != null && uploadResult.code == 0) {
                                                if (uploadResult.data != null && uploadResult.data.size() > 0) {
                                                    CourseData courseData = uploadResult.data.get(0);
                                                    if (courseData != null) {
                                                        //                                                DraftData tempData = new DraftData();
                                                        //                                                tempData.resdId = courseData.id;
                                                        //                                                tempData.title = courseData.nickname;
                                                        //                                                DraftDao draftDao = new DraftDao(getActivity());
                                                        //                                                try {
                                                        //                                                    draftDao.updateDraftByChwPath(mediaInfo.getPath(), userInfo.getMemberId(), tempData);
                                                        //                                                } catch (SQLException e) {
                                                        //                                                    e.printStackTrace();
                                                        //                                                }
                                                        //更新资源操作
                                                        //                                                    LocalCourseDTO dto = new LocalCourseDTO();
                                                        //                                                    dto.setmMicroId(courseData.id);
                                                        //                                                    LocalCourseDTO.updateLocalCourse(getActivity(),
                                                        //                                                            getMemeberId(), mediaInfo.getPath(), dto);
                                                        int tempMediaType = MediaType.MICROCOURSE;
                                                        if (courseData.type == ResType.RES_TYPE_ONEPAGE) {
                                                            tempMediaType = MediaType.ONE_PAGE;
                                                        }
                                                        updateMediaInfo(getActivity(), getUserInfo(),
                                                                uploadResult.getShortCourseInfoList(),
                                                                tempMediaType);
                                                    }
                                                }
                                            } else {
                                                if (getActivity() != null) {
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            //同一资源删除后，30s内不允许再次上传。
                                                            clickUpload();
                                                        }
                                                    });
                                                }

                                            }

                                        }
                                    }
                                });
                            }
                        }
                    });
        }
    }

    public static void updateMedia(final Activity activity, UserInfo userInfo,
                                   List<ShortCourseInfo> shortCourseInfos, final int mediaType) {
        updateMedia(activity, userInfo, shortCourseInfos, mediaType, null);
    }

    public static void updateMedia(final Activity activity, UserInfo userInfo,
                                   List<ShortCourseInfo> shortCourseInfos, final int mediaType,
                                   final CallbackListener listener) {

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
                        if (activity == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()) {
                            return;
                        }
                        if (listener == null) {
                            if (mediaType != MediaType.FLIPPED_CLASSROOM) {
                                TipMsgHelper.ShowLMsg(activity, R.string.upload_file_sucess);
                            }
                        } else {
                            listener.onBack(getResult());
                        }
                    }

                });
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
                        if (mediaType != MediaType.FLIPPED_CLASSROOM) {
                            TipMsgHelper.ShowLMsg(activity, R.string.upload_file_sucess);
                            if (viewModeChangeListener != null) {
                                //                                editMode = EditMode.CANCEL;
                                viewModeChangeListener.onViewModeChange(viewMode, EditMode.CANCEL);
                            }
                            //音频上传成功后，删除本地音频
                            if (uploadRecordFilePath != null && mediaType ==
                                    MediaType.AUDIO) {
                                File file = new File(uploadRecordFilePath);
                                if (file != null && file.exists()) {
                                    file.delete();
                                    Intent broadIntent = new Intent();
                                    broadIntent.setAction(LOCAL_MEDIA_REFRESH_ACTION);
                                    getActivity().sendBroadcast(broadIntent);
                                }
                            }
                        }
                    }

                });
    }

    private void uploadMedias(List<MediaInfo> mediaInfos) {
        List<String> titles = new ArrayList<String>();
        for (MediaInfo mediaInfo : mediaInfos) {
            if (mediaInfo != null) {
                if (mediaType == MediaType.PICTURE) {
                    titles.add(Utils.removeFileNameSuffix(mediaInfo.getTitle()));
                } else {
                    titles.add(Utils.removeFileNameSuffix(mediaInfo.getTitle()));
                }
            }
        }
        //如果是lq课件和 有声相册上传不需要重名检测
        if (mediaType == MediaType.MICROCOURSE || mediaType == MediaType.ONE_PAGE) {
            uploadCourseToServer(mediaInfos);
        } else {
            checkResourceTitle(mediaInfos, titles, mediaType, EditMode.UPLOAD);
        }
    }

    private void uploadMediasToServer(List<MediaInfo> mediaInfos) {
        UserInfo userInfo = getUserInfo();
        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
            TipMsgHelper.ShowLMsg(getActivity(), R.string.pls_login);
            return;
        }

        UploadParameter uploadParameter = UploadUtils.getUploadParameter(userInfo, mediaType, 1);
        List<String> paths = new ArrayList<String>();
        StringBuilder builder = new StringBuilder();
        for (MediaInfo mediaInfo : mediaInfos) {
            if (mediaInfo != null) {
                paths.add(mediaInfo.getPath());
                builder.append(Utils.removeFileNameSuffix(mediaInfo.getTitle()) + ";");
            }
        }
        String fileName = builder.toString();
        if (!TextUtils.isEmpty(fileName) && fileName.endsWith(";")) {
            fileName = fileName.substring(0, fileName.length() - 1);
        }
        if (!TextUtils.isEmpty(fileName)) {
            uploadParameter.setFileName(fileName);
        }
        uploadParameter.setPaths(paths);
        showLoadingDialog();
        UploadUtils.uploadMedia(getActivity(), uploadParameter, new CallbackListener() {
            @Override
            public void onBack(Object result) {
                android.os.Message message = mHandler.obtainMessage();
                message.what = MSG_UPLOAD_MEDIA_FINISH;
                message.obj = result;
                mHandler.sendMessage(message);
            }
        });
    }

    public static String getSaveImagePath(String filename) {
        String picPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/Pictures";
        File picFile = new File(picPath);
        if (!picFile.exists()) {
            picFile.mkdirs();
        }
        return new File(picPath, filename).getAbsolutePath();
    }

    public void selectDataByMediaType() {
        if (!isSplit && (mediaType == MediaType.PPT || mediaType == MediaType.PDF || mediaType == MediaType.DOC)) {
            getMultiSelectData();
        } else {
            getSelectData();
        }
    }

    public void getSelectData() {
        List<MediaInfo> mediaInfos = getSelectedData();
        if (mediaInfos == null || mediaInfos.size() == 0) {
            TipMsgHelper.ShowLMsg(getActivity(), R.string.pls_select_files);
            return;
        }
        if (watchWawaCourseSupportMultiType) {
            //处理分页数据
            WatchWawaCourseResourceSplicingUtils.splicingSplitPageResources(getActivity(),
                    mediaInfos, mediaType);
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
                if (mediaInfo.getMediaType() == MediaType.ONE_PAGE || mediaInfo.getMediaType()
                        == MediaType.MICROCOURSE) {
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
                } else {
                    resourceInfo.setType(transferMediaType(mediaInfo.getMediaType()));
                }
                if (mediaInfo.getCourseInfo() != null) {
                    resourceInfo.setScreenType(mediaInfo.getCourseInfo().getScreenType());
                }
                if (isFromOnline) {
                    String resId = mediaInfo.getMicroId();
                    if (TextUtils.isEmpty(resId)) {
                        NewResourceInfoTag tag = mediaInfo.getNewResourceInfoTag();
                        resId = tag.getMicroId();
                    }
                    if (!TextUtils.isEmpty(resId) && resId.contains("-")) {
                        resourceInfo.setResId(resId);
                    } else {
                        resourceInfo.setResId(resId + "-" + resourceInfo.getResourceType());
                    }
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


    public void setOnViewModeChangeListener(OnViewModeChangeListener listener) {
        viewModeChangeListener = listener;
    }

    public void setOnDataLoadedListener(OnDataLoadedListener listener) {
        onDataLoadedListener = listener;
    }

    //    private int transferType(int mediaType) {
    //        int resType = -1;
    //        switch (mediaType) {
    //            case MediaType.ONE_PAGE:
    //                resType = ResType.RES_TYPE_ONEPAGE;
    //                break;
    //            case MediaType.MICROCOURSE:
    //                resType = ResType.
    //                break;
    //            case MediaType.PICTURE:
    //                break;
    //            case MediaType.AUDIO:
    //                break;
    //            case MediaType.VIDEO:
    //                break;
    //        }
    //    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.toolbar_top_back_btn) {
            if (!isPick) {
                if (viewMode == ViewMode.NORMAL) {
                    if (getFragmentManager() != null) {
                        getFragmentManager().popBackStack();
                    }
                } else {
                    exitEditMode();
                }
            } else {
                if (getFragmentManager() != null) {
                    getFragmentManager().popBackStack();
                }
            }
        } else if (v.getId() == R.id.toolbar_top_commit_btn) {
            if (!isPick) {
                if (mediaType == MediaType.AUDIO) {
                    recordAudio();
                } else if (mediaType == MediaType.VIDEO) {
                    cameraVideo();
                } else if (mediaType == MediaType.PICTURE) {
                    cameraImage();
                }
            } else {
                selectDataByMediaType();
            }
        } else if (v.getId() == R.id.top_layout) {
            isSelectAll = !isSelectAll;
            checkState(isSelectAll);
            checkData(isSelectAll);
        } else if (v.getId() == R.id.whiteboard) {
            createSlide(CreateSlideHelper.SLIDETYPE_WHITEBOARD);
        } else if (v.getId() == R.id.image) {
            createSlide(CreateSlideHelper.SLIDETYPE_IMAGE);
        } else if (v.getId() == R.id.camera) {
            createSlide(CreateSlideHelper.SLIDETYPE_CAMERA);
        } else if (v.getId() == R.id.btn_bottom_upload) {
            if (viewMode == ViewMode.NORMAL) {
                editMode = EditMode.UPLOAD;
                maxCount = MAX_UPLOAD_COUNT;
                if (mediaType == MediaType.ONE_PAGE || mediaType == MediaType.VIDEO || mediaType == MediaType.AUDIO) {
                    maxCount = MAX_ONEPAGE_UPLOAD_COUNT;
                } else if (mediaType == MediaType.PICTURE) {
                    //图片最多上传9个
                    maxCount = MAX_PICK_COUNT;
                }

                if (!isRemote) {
                    enterEditMode();
                } else {
                    viewMode = ViewMode.NORMAL;
                    if (viewModeChangeListener != null) {
                        viewModeChangeListener.onViewModeChange(viewMode, editMode);
                    }
                }
            }
        } else if (v.getId() == R.id.btn_bottom_rename) {
            if (viewMode == ViewMode.NORMAL) {
                editMode = EditMode.RENAME;
                maxCount = MAX_RENAME_COUNT;
                enterEditMode();
            }

        } else if (v.getId() == R.id.btn_bottom_delete) {
            if (viewMode == ViewMode.NORMAL) {
                editMode = EditMode.DELETE;
                enterEditMode();
            }

        } else if (v.getId() == R.id.btn_bottom_ok) {
            if (editMode == EditMode.UPLOAD) {
                upload(mediaType);
            } else if (editMode == EditMode.DELETE) {
                delete(mediaType);
            } else if (editMode == EditMode.RENAME) {
                rename();
            }
        } else if (v.getId() == R.id.btn_bottom_cancel) {
            //取消，变成-1。
            editMode = EditMode.CANCEL;
            exitEditMode();
        } else if (v.getId() == R.id.btn_bottom_select_all) {
            //全选/取消全选
            isSelectAll = !isSelectAll;
            checkState(isSelectAll);
            checkData(isSelectAll);
        } else if (v.getId() == R.id.new_btn) {
            openCourseType();
        }
    }

    private void openCourseType() {
        Intent intent = new Intent(getActivity(), WawaCourseChoiceActivity.class);
        intent.putExtra(SelectedReadingDetailFragment.Constants.INTORDUCTION_CREATE, false);
        startActivityForResult(intent, CampusPatrolPickerFragment.CREATE_NEW_RESOURCE_REQUEST_CODE);
    }

    private void getMultiSelectData() {
        List<MediaInfo> mediaInfos = getSelectedData();
        if (mediaInfos == null || mediaInfos.size() == 0) {
            TipMsgHelper.ShowLMsg(getActivity(), R.string.pls_select_files);
            return;
        }
        ArrayList<ResourceInfo> resourceInfos = new ArrayList<ResourceInfo>();
        int type = -1;
        type = mediaInfos.get(0).getMediaType();
        NewResourceInfoTag newResourceInfoTag = mediaInfos.get(0).getNewResourceInfoTag();
        if (newResourceInfoTag != null) {
            if (watchWawaCourseSupportMultiType) {
                //处理整页数据
                WatchWawaCourseResourceSplicingUtils.splicingFullPageResources(getActivity(),
                        newResourceInfoTag, mediaType);
                return;
            }
            List<NewResourceInfo> newResourceInfos = newResourceInfoTag.getSplitInfoList();
            //来自空中课堂获取资料
            if (isFromOnline) {
                ResourceInfo resourceInfo = new ResourceInfo();
                resourceInfo.setTitle(newResourceInfoTag.getTitle());
                resourceInfo.setImgPath(AppSettings.getFileUrl(newResourceInfoTag
                        .getThumbnail()));
                resourceInfo.setResourcePath(AppSettings.getFileUrl(newResourceInfoTag.getResourceUrl()));
                type = transferMediaType(type);
                if (type > 0) {
                    resourceInfo.setType(type);
                }
                resourceInfo.setShareAddress(newResourceInfoTag.getShareAddress());
                resourceInfo.setResourceType(newResourceInfoTag.getResourceType());
                String resId = newResourceInfoTag.getMicroId();
                if (!TextUtils.isEmpty(resId) && resId.contains("-")) {
                    resourceInfo.setResId(resId);
                } else {
                    resourceInfo.setResId(resId + "-" + newResourceInfoTag.getResourceType());
                }
                resourceInfos.add(resourceInfo);
            } else {
                if (newResourceInfos != null && newResourceInfos.size() > 0) {
                    for (NewResourceInfo info : newResourceInfos) {
                        if (info != null) {
                            ResourceInfo resourceInfo = new ResourceInfo();
                            resourceInfo.setTitle(info.getTitle());
                            resourceInfo.setImgPath(AppSettings.getFileUrl(info.getResourceUrl()));
                            resourceInfo.setResourcePath(AppSettings.getFileUrl(info.getResourceUrl()));
                            type = transferMediaType(type);
                            if (type >= 0) {
                                resourceInfo.setType(type);
                            }
                            resourceInfo.setShareAddress(info.getShareAddress());
                            resourceInfo.setResourceType(info.getResourceType());
                            resourceInfos.add(resourceInfo);
                        }
                    }
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
    public void onSearched(String file, int mediaType) {
        if (mediaType == com.libs.yilib.pickimages.MediaType.MEDIA_TYPE_PHOTO) {
            if (!TextUtils.isEmpty(file)) {
                if (!file.startsWith(Utils.DATA_FOLDER)) {
                    MediaInfo mediaInfo = new MediaInfo();
                    mediaInfo.setTitle(Utils.getFileNameFromPath(file));
                    mediaInfo.setPath(file);
                    mediaInfo.setMediaType(MediaType.PICTURE);
                    mediaInfos.add(mediaInfo);
                }
            } else {

                Collections.sort(mediaInfos, new Comparator<MediaInfo>() {
                    @Override
                    public int compare(MediaInfo info0, MediaInfo info1) {
                        long modifiedTime0 = new File(info0.getPath()).lastModified();
                        long modifiedTime1 = new File(info1.getPath()).lastModified();
                        if (modifiedTime0 < modifiedTime1) {
                            return 1;
                        } else if (modifiedTime0 > modifiedTime1) {
                            return -1;
                        } else {
                            return 0;
                        }
                    }
                });
                dismissLoadingDialog();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        getCurrAdapterViewHelper().setData(mediaInfos);
                    }
                });
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SimpleVideoRecorder.REQUEST_CODE_CAPTURE_VIDEO) {
            if (data == null) {
                return;
            }
            String filePath = data.getStringExtra(SimpleVideoRecorder.EXTRA_VIDEO_PATH);
            if (TextUtils.isEmpty(filePath)) {
                return;
            }
            File file = new File(filePath);
            if (file.exists()) {
                MediaDTO mediaDTO = new MediaDTO();
                mediaDTO.setPath(filePath);
                mediaDTO.setTitle(Utils.getFileNameFromPath(filePath));
                mediaDTO.setMediaType(MediaType.VIDEO);
                mediaDTO.setCreateTime(System.currentTimeMillis());
                MediaDao mediaDao = new MediaDao(getActivity());
                mediaDao.addOrUpdateMediaDTO(mediaDTO);
                loadLocalMedias();
                if (isAutoUpload) {
                    MediaInfo mediaInfo = mediaDTO.toMediaInfo();
                    List<MediaInfo> mediaInfos = new ArrayList<MediaInfo>();
                    if (mediaInfo != null) {
                        mediaInfos.add(mediaInfo);
                    }
                    uploadMedias(mediaInfos);
                }
            }
        } else if (requestCode == REQUEST_CODE_CAPTURE_CAMERA) {
            if (!TextUtils.isEmpty(picFileName) && new File(picFileName).exists()) {
                MediaInfo mediaInfo = new MediaInfo();
                mediaInfo.setTitle(Utils.getFileNameFromPath(picFileName));
                mediaInfo.setPath(picFileName);
                mediaInfo.setMediaType(MediaType.PICTURE);
                List<MediaInfo> infos = getCurrAdapterViewHelper().getData();
                if (infos != null) {
                    getCurrAdapterViewHelper().getData().add(0, mediaInfo);
                    getCurrAdapterViewHelper().update();
                }
                if (isAutoUpload) {
                    List<MediaInfo> mediaInfos = new ArrayList<MediaInfo>();
                    if (mediaInfo != null) {
                        mediaInfos.add(mediaInfo);
                    }
                    uploadMedias(mediaInfos);
                }
            }
        } else if (requestCode == CampusPatrolPickerFragment.REQUEST_CODE) {
            //校园巡查逻辑
            if (data != null) {
                startDate = CampusPatrolUtils.getStartDate(data);
                endDate = CampusPatrolUtils.getEndDate(data);
                loadViews();
            }
        } else if (resultCode == CampusPatrolPickerFragment.CREATE_NEW_RESOURCE_RESULT_CODE) {
            //创建资源返回code
            loadViews();
        } else if (resultCode == CampusPatrolPickerFragment.OPEN_COURSE_DETAILS_RESULT_CODE) {
            //从课件详情页面返回code
            loadViews();
        } else if (requestCode == CampusPatrolPickerFragment.
                REQUEST_CODE_DISCUSSION_COURSE_DETAILS) {
            //微课详情页返回
            if (OnlineMediaPaperActivity.hasMicroCourseCollected()) {
                //收藏返回刷新
                OnlineMediaPaperActivity.setHasMicroCourseCollected(false);
                loadViews();
            }
        } else if (requestCode == CampusPatrolPickerFragment.CREATE_NEW_RESOURCE_REQUEST_CODE) {
            //创建成功
            loadViews();
        } else if (requestCode == CampusPatrolPickerFragment.OPEN_COURSE_DETAILS_REQUEST_CODE) {
            //微课详情页面数据改变
            loadViews();
        } else {
            if (data != null) {
                boolean rtn = CreateSlideHelper.processActivityResule(null,
                        getParentFragment(), requestCode, resultCode, data);
                if (!rtn) {
                    loadOnePages();
                    if (isAutoUpload) {
                        if (viewModeChangeListener != null) {
                            //                        editMode = EditMode.CANCEL;
                            viewModeChangeListener.onViewModeChange(viewMode, EditMode.CANCEL);
                        }
                    }
                }
            }
        }
    }

    /**
     * 更新重新命名的文件
     */
    public void upDateRenameData(MediaInfo mediaInfo, String title) {
        List<MediaInfo> mediaInfoList = getCurrAdapterViewHelper().getData();
        if (mediaInfoList != null && mediaInfoList.size() > 0) {
            for (int i = 0; i < mediaInfoList.size(); i++) {
                MediaInfo info = mediaInfoList.get(i);
                if (info.getId().equals(mediaInfo.getId())) {
                    info.setTitle(title);
                }
            }
            getCurrAdapterViewHelper().setData(mediaInfoList);
        }
    }

    public AudioPopwindow getAudioPopwindow() {
        return audioPopwindow;
    }

    private final class MyBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(LOCAL_MEDIA_REFRESH_ACTION) && !isRemote) {
                loadViews();
            }
        }
    }

    private void initBroadCastReceiver() {
        receiver = new MyBroadCastReceiver();
        IntentFilter filter = new IntentFilter(LOCAL_MEDIA_REFRESH_ACTION);
        getActivity().registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        if (receiver != null) {
            getActivity().unregisterReceiver(receiver);
        }
        super.onDestroy();
    }
}
