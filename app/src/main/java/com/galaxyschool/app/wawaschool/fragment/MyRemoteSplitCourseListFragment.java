package com.galaxyschool.app.wawaschool.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.Request;
import com.galaxyschool.app.wawaschool.PictureBooksDetailActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.CampusPatrolUtils;
import com.galaxyschool.app.wawaschool.common.ScreenUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.lqwawa.lqbaselib.net.ThisStringRequest;
import com.galaxyschool.app.wawaschool.pojo.MediaInfo;
import com.galaxyschool.app.wawaschool.pojo.MyChoiceMode;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfoTag;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.weike.SplitCourseInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.SplitCourseInfoListResult;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.libs.gallery.ImageInfo;
import com.lqwawa.client.pojo.MediaType;
import com.lqwawa.client.pojo.ResourceInfo;
import com.osastudio.common.utils.ConstantSetting;

import java.util.ArrayList;
import java.util.List;

/**
 * 远端的拆分页面。
 */
public class MyRemoteSplitCourseListFragment extends ContactsListFragment {

    public static final String TAG = MyRemoteSplitCourseListFragment.class.getSimpleName();

    private int MAX_COLUMNS = 2;
    private String courseId;
    private String courseName;
    private int mediaType;
    private boolean isPick;//是否需要选择
    private int choiceMode = MyChoiceMode.CHOICE_MODE_MULTIPLE_SUPPORT_ALL;//默认支持全选
    private static final int maxCount = ConstantSetting.SELECT_PPT_PDF_PAGING_NUM;//最大可选数量,非全选时使用。
    private int layoutId;
    private NewResourceInfoTag newResourceInfoTag;
    private static final int HANDLER_WHAT_LOADED_MEDIA = 10;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case HANDLER_WHAT_LOADED_MEDIA:
                    List<MediaInfo> mediaInfos = (List<MediaInfo>) msg.obj;
                    if (mediaInfos != null && mediaInfos.size() > 0){
                        getCurrAdapterViewHelper().setData(mediaInfos);
                    }
                    break;
            }
        }
    };

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
    }

    private void refreshData(){
        getPageHelper().clear();
        loadViews();
    }

    private void updateTitleView(){
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            textView.setText(courseName);
        }
    }

    private boolean isMicrocourse(){
        return mediaType == MediaType.MICROCOURSE || mediaType == MediaType.ONE_PAGE;
    }

    void initViews() {
        if (getArguments() != null) {
            isPick = getArguments().getBoolean(CampusPatrolUtils.Constants.EXTRA_IS_PICK);
            choiceMode = getArguments().getInt(CampusPatrolUtils.Constants.EXTRA_CHOICE_MODE);
            courseId = getArguments().getString(CampusPatrolMainFragment.CAMPUS_PATROL_RESOURCE_ID);
            courseName = getArguments().getString(CampusPatrolMainFragment.
                    CAMPUS_PATROL_RESOURCE_NAME);
            mediaType = getArguments().getInt(CampusPatrolMainFragment.CAMPUS_PATROL_RESOURCE_TYPE);
            newResourceInfoTag = getArguments().getParcelable(NewResourceInfoTag.class
                    .getSimpleName());
        }
        updateTitleView();
        TextView textView = ((TextView) findViewById(R.id.contacts_header_right_btn));
        if (textView != null) {
            if (isPick) {
                textView.setVisibility(View.VISIBLE);
            }else {
                textView.setVisibility(View.INVISIBLE);
            }
            //确认按钮
            textView.setText(getString(R.string.confirm));
            textView.setOnClickListener(this);
        }
        //刷新
        PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);
        pullToRefreshView.setRefreshEnable(false);

        GridView gridView = (GridView) findViewById(R.id.gridview);
        if (gridView == null) {
            return;
        }
        int padding = getActivity().getResources().getDimensionPixelSize(
                R.dimen.resource_gridview_padding);
        gridView.setNumColumns(2);
        gridView.setBackgroundColor(Color.WHITE);
        gridView.setPadding(padding, padding, padding, padding);
        if (isMicrocourse()){
            layoutId = R.layout.campus_patrol_school_based_course_grid_item;
        }else {
            //视频的布局
            layoutId = R.layout.video_grid_item;
        }
        AdapterViewHelper gridViewHelper = new AdapterViewHelper(
                getActivity(), gridView, layoutId) {
            @Override
            public void loadData() {
                loadSplitCourseList();
            }

            @Override
            public View getView(final int position, View convertView, final ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                MediaInfo data = (MediaInfo) getDataAdapter().getItem(position);
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
                //微课尺寸
                if (isMicrocourse()) {
                    params.width = itemSize - getActivity().getResources()
                            .getDimensionPixelSize(R.dimen.resource_gridview_padding);
                    params.height = params.width * 9 / 16;
                }else {
                    //其他尺寸（暂时这样）
                    params.width = itemSize;
                    params.height = params.width * 3 / 4;
                }
                frameLayout.setLayoutParams(params);

                ImageView imageView = (ImageView) view.findViewById(R.id.media_thumbnail);
                if (imageView != null){
                    String thumbnail = data.getThumbnail();
                    getThumbnailManager().displayImageWithDefault(
                            AppSettings.getFileUrl(thumbnail), imageView,R.drawable.default_cover);
                }
                //标题
                TextView textView = (TextView) view.findViewById(R.id.media_name);
                if (textView != null){
                    String title = data.getSubTitle();
                    textView.setText(title);
                    textView.setCompoundDrawables(null,null,null,null);
                    textView.setGravity(Gravity.CENTER);
                }

                //拆分页不显示拆分
                textView = (TextView) view.findViewById(R.id.media_split_btn);
                if (textView != null){
                    textView.setVisibility(View.GONE);
                }
                //分页不显示收藏

                //选择状态
                ImageView flag = (ImageView) view.findViewById(R.id.media_flag);
                if (flag != null){
                    flag.setVisibility(isPick? View.VISIBLE : View.INVISIBLE);
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
                if (!isPick) {
                    openCourse(data, position);
                }else {
                    //选择状态
                    //单选模式
                    if (choiceMode == MyChoiceMode.CHOICE_MODE_ONLY_SINGLE) {
                        operateSingleChoiceItem(false, position);
                    }
                    //支持部分选择模式
                    else if (choiceMode == MyChoiceMode.CHOICE_MODE_MULTIPLE_ONLY_PART) {
                        if (!data.isSelect()) {
                            int count = getSelectedDataCount();
                            if (count > 0 && count >= maxCount) {
                                TipMsgHelper.ShowLMsg(getActivity(), getString(R.string
                                        .n_pick_max_count, maxCount));
                                return;
                            }
                        }

                        //多选操作
                        operateMultipleChoiceItem(data);
                    }
                    //支持全选模式
                    else if (choiceMode == MyChoiceMode.CHOICE_MODE_MULTIPLE_SUPPORT_ALL) {
                        //多选操作
                        operateMultipleChoiceItem(data);
                    }
                }
            }
        };

        setCurrAdapterViewHelper(gridView, gridViewHelper);
    }

    /**
     * 多选模式
     * @param data
     */
    private void operateMultipleChoiceItem(MediaInfo data) {
        if (data != null) {
            //多选
            data.setIsSelect(!data.isSelect());
            //更新一下
            getCurrAdapterViewHelper().update();
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

    private int getSelectedDataCount() {
        List<MediaInfo> mediaInfos = getSelectedData();
        if (mediaInfos == null) {
            return 0;
        }
        return mediaInfos.size();
    }

    private void openImage(int position) {
        List<MediaInfo> mediaInfos = getCurrAdapterViewHelper().getData();

        List<ImageInfo> resourceInfoList = new ArrayList<>();
        for (MediaInfo mediaInfo : mediaInfos) {
            ImageInfo newResourceInfo = new ImageInfo();
            newResourceInfo.setTitle(mediaInfo.getTitle());
            newResourceInfo.setResourceUrl(mediaInfo.getPath());
            newResourceInfo.setId(mediaInfo.getId());
            newResourceInfo.setResourceType(mediaInfo.getResourceType());
            newResourceInfo.setMicroId(mediaInfo.getMicroId());
            newResourceInfo.setAuthorId(mediaInfo.getAuthorId());
            resourceInfoList.add(newResourceInfo);
        }
        ActivityUtils.openImage(getActivity(), resourceInfoList, false, position,false);
    }

    private void openCourse(MediaInfo data, int position) {

        if (data == null){
            return;
        }

        if (!isMicrocourse()){
            //其他类型直接打开
            openImage(position);
        }else {
            int resType = data.getResourceType() % ResType.RES_TYPE_BASE;
            NewResourceInfo resourceInfo = data.getNewResourceInfoTag();
            if (resourceInfo != null) {
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
    }

    void loadViews() {
        if (isMicrocourse()) {
            //微课直接拉取
            loadSplitCourseList();
        }else {
            loadMedia();
        }
    }

    private void loadMedia() {
        if (newResourceInfoTag != null && newResourceInfoTag.getSplitInfoList() != null) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    //其他类型的分页信息是直接传入的
                    List<MediaInfo> mediaInfos = new ArrayList<MediaInfo>();
                    List<NewResourceInfo> resourceInfos = newResourceInfoTag.getSplitInfoList();
                    for (int i = 0, size = resourceInfos.size(); i < size; i++) {
                        NewResourceInfo resourceInfo = resourceInfos.get(i);
                        if (resourceInfo != null) {
                            MediaInfo mediaInfo = new MediaInfo();
                            mediaInfo.setPath(resourceInfo.getResourceUrl());
                            mediaInfo.setMediaType(mediaType);
                            mediaInfo.setId(resourceInfo.getId());
                            mediaInfo.setTitle(resourceInfo.getTitle());
                            //设置分页的标题
                            mediaInfo.setSubTitle(resourceInfo.getTitle());
                            mediaInfo.setThumbnail(resourceInfo.getResourceUrl());
                            mediaInfo.setShareAddress(resourceInfo.getShareAddress());
                            mediaInfo.setResourceType(resourceInfo.getResourceType());
                            mediaInfo.setAuthorId(resourceInfo.getAuthorId());
                            mediaInfos.add(mediaInfo);
                        }
                    }
                    handler.sendMessage(handler.obtainMessage(HANDLER_WHAT_LOADED_MEDIA,
                            mediaInfos));
                }
            }).start();
        }
    }

    /**
     * 拉取分页课程信息
     */
    private void loadSplitCourseList() {
        showLoadingDialog();
        JSONObject jsonObject = new JSONObject();
        if (!TextUtils.isEmpty(courseId)) {
            jsonObject.put("pid", courseId);
        }
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
                    if (result == null || result.getData() == null) {
                        return;
                    }
                    List<SplitCourseInfo> splitCourseInfos = result.getData();
                    List<MediaInfo> mediaInfos = new ArrayList<>();
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

                            //add NewResourceInfoTag
                            NewResourceInfoTag infoTag = new NewResourceInfoTag();
                            infoTag.setResourceUrl(info.getPlayUrl());
                            infoTag.setResourceType(mediaInfo.getResourceType());
                            infoTag.setId(mediaInfo.getId());
                            infoTag.setTitle(mediaInfo.getTitle());
                            infoTag.setMicroId(info.getId()+"");
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
                dismissLoadingDialog();
            }

        });
        request.addHeader("Accept-Encoding", "*");
        request.start(getActivity());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contacts_header_right_btn){
            //确认选择
            selectDataByMediaType();

        }else if (v.getId() == R.id.contacts_header_left_btn){
            //返回逻辑
            if (isPick){
                //选择状态直接弹出
                if (getFragmentManager() != null){
                    getFragmentManager().popBackStack();
                }
            }else {
                //直接返回
                getActivity().finish();
            }
        }
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
                if (mediaInfo.getMediaType() == MediaType.ONE_PAGE || mediaInfo.getMediaType()
                        == MediaType.MICROCOURSE){
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