package com.galaxyschool.app.wawaschool.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.Request;
import com.galaxyschool.app.wawaschool.PictureBooksDetailActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.ScreenUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.lqwawa.lqbaselib.net.ThisStringRequest;
import com.galaxyschool.app.wawaschool.pojo.CampusPatrolSchoolOutlineMaterial;
import com.galaxyschool.app.wawaschool.pojo.MediaInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfoTag;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.weike.SplitCourseInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.SplitCourseInfoListResult;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.libs.gallery.ImageInfo;
import com.lqwawa.client.pojo.MediaType;

import java.util.ArrayList;
import java.util.List;

/**
 * 校园巡查---校本课程---拆分课程页面。
 */
public class CampusPatrolSplitCourseListFragment extends ContactsListFragment {

    public static final String TAG = CampusPatrolSplitCourseListFragment.class.getSimpleName();

    private int MAX_COLUMNS = 2;
    private CampusPatrolSchoolOutlineMaterial materialInfo;
    private String courseId;
    private String courseName;
    private int mediaType;
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
            materialInfo = (CampusPatrolSchoolOutlineMaterial) getArguments().
                    getSerializable(CampusPatrolMainFragment.CAMPUS_PATROL_RESOURCE_DATA);
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
            textView.setVisibility(View.INVISIBLE);
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
        AdapterViewHelper gridViewHelper = new AdapterViewHelper(
                getActivity(), gridView,R.layout.campus_patrol_school_based_course_grid_item) {
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
                params.width = itemSize - getActivity().getResources()
                                    .getDimensionPixelSize(R.dimen.resource_gridview_padding);
                params.height = params.width * 9 / 16;
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
                    String title = data.getTitle();
                    textView.setText(title);
                    textView.setCompoundDrawables(null,null,null,null);
                }

                //拆分页不显示拆分
                textView = (TextView) view.findViewById(R.id.media_split_btn);
                if (textView != null){
                    textView.setVisibility(View.GONE);
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
                openCourse(data,position);
            }
        };

        setCurrAdapterViewHelper(gridView, gridViewHelper);
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
        ActivityUtils.openImage(getActivity(), resourceInfoList, false, position);
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
                            mediaInfo.setAuthorId(info.getMemberId());

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
        super.onClick(v);
    }
}