package com.galaxyschool.app.wawaschool.medias.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.Request;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.WawaCourseChoiceActivity;
import com.galaxyschool.app.wawaschool.common.CallbackListener;
import com.galaxyschool.app.wawaschool.common.CampusPatrolUtils;
import com.galaxyschool.app.wawaschool.common.LetvVodHelperNew;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.UploadUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.common.WawaCourseUtils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.db.MediaDao;
import com.galaxyschool.app.wawaschool.db.dto.LocalCourseDTO;
import com.galaxyschool.app.wawaschool.db.dto.MediaDTO;
import com.galaxyschool.app.wawaschool.fragment.CampusPatrolPickerFragment;
import com.galaxyschool.app.wawaschool.fragment.ContactsListFragment;
import com.galaxyschool.app.wawaschool.fragment.SelectedReadingDetailFragment;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.lqwawa.lqbaselib.net.ThisStringRequest;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.MediaInfo;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.ResourceTitle;
import com.galaxyschool.app.wawaschool.pojo.ResourceTitleResult;
import com.galaxyschool.app.wawaschool.pojo.UploadParameter;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseUploadResult;
import com.galaxyschool.app.wawaschool.pojo.weike.MediaData;
import com.galaxyschool.app.wawaschool.pojo.weike.MediaUploadList;
import com.galaxyschool.app.wawaschool.pojo.weike.ShortCourseInfo;
import com.lqwawa.client.pojo.MediaType;
import com.lqwawa.libs.mediapaper.AudioPopwindow;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.galaxyschool.app.wawaschool.views.InputBoxDialog;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.lecloud.skin.ui.utils.VodVideoSettingUtil;
import com.lqwawa.client.pojo.ResourceInfo;
import com.lqwawa.tools.FileZipHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.oosic.apps.iemaker.base.BaseUtils;
import com.osastudio.common.utils.Constants;

import org.json.JSONException;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 本地的音频列表,旨在解耦。
 */
public class MyLocalAudioListFragment extends ContactsListFragment {

    private static final int MAX_ONEPAGE_UPLOAD_COUNT = 1;
    private static final int MAX_RENAME_COUNT = 1;
    private static final int MAX_UPLOAD_COUNT = 3;
    public static final int MAX_PICK_COUNT = 3;
    private boolean isSelectAll;
    //变量
    private int mediaType = MediaType.AUDIO;//音频类型
    private int viewMode = CampusPatrolUtils.ViewMode.NORMAL;
    private int editMode = CampusPatrolUtils.EditMode.UPLOAD;
    private boolean isPick;
    private int layoutId;
    private static final int PIC_MAX_COLUMN = 1;
    private int maxColumn;
    private int maxCount = MAX_PICK_COUNT;
    private View topLayout, bottomLayout, bottomSubLayout0, bottomSubLayout1,
            segLine0, segLine1,bottomSegLine0,bottomSegLine1,createCourse;
    private TextView uploadBtn, renameBtn, deleteBtn;
    private ImageView checkBtn;
    private TextView cancelBtn,selectAllBtn,confirmBtn;
    public static final int MSG_UPLOAD_MEDIA_FINISH = 0;
    private MediaDao mediaDao;

    private String uploadRecordFilePath = null;//音频上传路径
    private AudioPopwindow audioPopwindow;

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
            textView.setText(getString(R.string.audios));
        }
        textView = ((TextView) findViewById(R.id.contacts_header_right_btn));
        if (textView != null) {
            //选择界面的录制按钮
            if (isPick) {
                textView.setVisibility(View.VISIBLE);
            }else {
                textView.setVisibility(View.INVISIBLE);
            }
            textView.setText(getString(R.string.record_video));
            textView.setOnClickListener(this);
        }

        final PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        pullToRefreshView.setRefreshEnable(false);
        setPullToRefreshView(pullToRefreshView);

        GridView gridView = (GridView) findViewById(R.id.grid_view);
        if (gridView != null) {
            //音频的布局
            layoutId = R.layout.local_media_audio_grid_item;
            maxColumn = PIC_MAX_COLUMN;
            gridView.setNumColumns(maxColumn);
            gridView.setBackgroundColor(Color.parseColor("#ebebeb"));
            gridView.setVerticalSpacing(getResources().getDimensionPixelSize(
                    R.dimen.listview_divider_height));
            gridView.setPadding(0, 0, 0, 0);

            AdapterViewHelper helper = new AdapterViewHelper(
                    getActivity(), gridView, layoutId) {

                @Override
                public void loadData() {
                    loadLocalMedias();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = convertView;
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

                    ImageView thumbnail = (ImageView) view.findViewById(R.id.media_thumbnail);
                    TextView name = (TextView) view.findViewById(R.id.media_name);
                    TextView time = (TextView) view.findViewById(R.id.media_time);
                    ImageView flag = (ImageView) view.findViewById(R.id.media_flag);
                    View splitView = view.findViewById(R.id.media_split_btn);

                    //布局
                    thumbnail.setImageResource(R.drawable.icon_media_audio_green);
                    time.setText(data.getUpdateTime());
                    name.setText(Utils.removeFileNameSuffix(data.getTitle()));
                    //判断是否显示拆分页
                    if (splitView != null) {
                        splitView.setVisibility(View.GONE);
                    }

                    flag.setVisibility(viewMode == CampusPatrolUtils.ViewMode.NORMAL ?
                            View.INVISIBLE : View.VISIBLE);
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
                        mediaPlay(data);
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


    private void mediaPlay(MediaInfo data) {
        if (data == null) {
            return;
        }

        //本地音频
        String filePath = data.getPath();
        new LetvVodHelperNew.VodVideoBuilder(getActivity())
                .setNewUI(true)//使用自定义UI
                .setHideBtnMore(true)//个人资源库隐藏收藏pop
                .setTitle(data.getTitle())//视频标题
                .setUrl(filePath)//路径
                .setMediaType(VodVideoSettingUtil.AUDIO_TYPE)
                .create();
    }

    private void initBottomViews() {
        viewMode = isPick ? CampusPatrolUtils.ViewMode.EDIT : CampusPatrolUtils.ViewMode.NORMAL;
        topLayout = findViewById(R.id.top_layout);
        bottomLayout = findViewById(R.id.bottom_layout);
        if (bottomLayout != null){
            if (isPick){
                bottomLayout.setVisibility(View.VISIBLE);
            }else {
                //本地图片暂不支持其他操作
                bottomLayout.setVisibility(View.GONE);
            }
        }
        bottomSubLayout0 = findViewById(R.id.bottom_sub_layout_0);
        bottomSubLayout1 = findViewById(R.id.bottom_sub_layout_1);
        segLine0 = findViewById(R.id.seg_line_0);
        segLine1 = findViewById(R.id.seg_line_1);
        bottomSegLine0 = findViewById(R.id.bottom_seg_line_0);
        bottomSegLine1 = findViewById(R.id.bottom_seg_line_1);
        createCourse = findViewById(R.id.new_btn);
        createCourse.setOnClickListener(this);
        createCourse.setVisibility(View.GONE);

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

    private void refreshData() {
        loadLocalMedias();
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contacts_header_right_btn) {
            //录音
            recordAudio(v);
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

    private void recordAudio(View v) {
        File audioFile = new File(Utils.AUDIO_FOLDER);
        if (!audioFile.exists()) {
            audioFile.mkdirs();
        }
        audioPopwindow = new AudioPopwindow(getActivity(),Utils.AUDIO_FOLDER, new
                AudioPopwindow.OnUploadListener() {
            @Override
            public void onUpload(String path) {
                if (TextUtils.isEmpty(path)) {
                    return;
                }
                uploadRecordFilePath=path;
                File file = new File(path);
                if (file.exists()) {
                    long time = System.currentTimeMillis();
                    String fileName =file.getName();

                    MediaDTO mediaDTO = new MediaDTO();
                    mediaDTO.setPath(path);
                    mediaDTO.setTitle(fileName);
                    mediaDTO.setMediaType(MediaType.AUDIO);
                    mediaDTO.setCreateTime(time);
                    MediaDao mediaDao = new MediaDao(getActivity());
                    mediaDao.addOrUpdateMediaDTO(mediaDTO);
                    loadLocalMedias();//刷新
                    if (CampusPatrolUtils.isAutoUpload) {
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
        if(audioPopwindow!=null){
            audioPopwindow.setAnimationStyle(R.style.AnimBottom);
            audioPopwindow.showPopupMenu(v);
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
        uploadMedias(mediaInfos);
    }

    private void uploadMedias(List<MediaInfo> mediaInfos) {
        List<String> titles = new ArrayList<String>();
        for (MediaInfo mediaInfo : mediaInfos) {
            if (mediaInfo != null) {
                titles.add(Utils.removeFileNameSuffix(mediaInfo.getTitle()));
            }
        }
        checkResourceTitle(mediaInfos, titles, mediaType, CampusPatrolUtils.EditMode.UPLOAD);
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
                        uploadMediasToServer(mediaInfos);
                    }
                }
            }
        };
        listener.setShowErrorTips(false);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.CHECK_CLOUD_RESOURCE_TITLE_URL,
                params, listener);
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
        if(!TextUtils.isEmpty(fileName) && fileName.endsWith(";")) {
            fileName = fileName.substring(0, fileName.length() - 1);
        }
        if(!TextUtils.isEmpty(fileName)) {
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
                        //音频上传成后，删除本地音频文件。
                        if(uploadRecordFilePath != null){
                            File file = new File(uploadRecordFilePath);
                            if(file != null && file.exists()){
                                file.delete();
                            }
                        }
                        //刷新数据
                        refreshData();
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
                            //检查字数是否超过限制，同IOS，最多输入40个字符。
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
                    //删除音频
                    int id = mediaDao.deleteMediaDTOByPath(info.getPath(), mediaType);
                    if (id > 0) {
                        Utils.deleteFile(info.getPath());
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
}
