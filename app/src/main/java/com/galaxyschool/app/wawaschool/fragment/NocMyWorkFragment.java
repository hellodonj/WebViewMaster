package com.galaxyschool.app.wawaschool.fragment;
import android.content.Context;
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
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.Request;
import com.galaxyschool.app.wawaschool.NocEntryInfoActivity;
import com.galaxyschool.app.wawaschool.NocMatchInfoActivity;
import com.galaxyschool.app.wawaschool.NocMyWorkActivity;
import com.galaxyschool.app.wawaschool.PictureBooksDetailActivity;
import com.galaxyschool.app.wawaschool.R;

import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.NocHelper;
import com.galaxyschool.app.wawaschool.common.PassParamhelper;
import com.galaxyschool.app.wawaschool.common.ScreenUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.db.MediaDao;
import com.galaxyschool.app.wawaschool.db.dto.LocalCourseDTO;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.lqwawa.lqbaselib.net.ThisStringRequest;
import com.galaxyschool.app.wawaschool.pojo.MediaInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfoTag;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.weike.SplitCourseInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.SplitCourseInfoListResult;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.osastudio.common.utils.LQImageLoader;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 * Created by wangchao on 12/28/15.
 */
public class NocMyWorkFragment extends ContactsListFragment  {
    public static final String TAG = NocMyWorkFragment.class.getSimpleName();
    public static final int MAX_COLUMN = 2;
    private int mediaType=1;
    private HashMap<String, NewResourceInfoTag> resourceInfoTagHashMap = new HashMap<String, NewResourceInfoTag>();
    private int maxColumn;
    private int layoutId;
    private List<MediaInfo> mediaInfos = new ArrayList<MediaInfo>();
    private PullToRefreshView pullToRefreshView;
    public static final String EXTRA_COURSE_ID = "course_id";
    public static final String EXTRA_COURSE_NAME = "course_name";
    public static final String EXTRA_IS_SPLIT = "course_is_split";
    public static final String EXTRA_COURSE_AUTHOR_NAME = "course_author_name";
    private String courseId;
    private String title;
    private boolean isSplit = false;
    private boolean isSelectAll = false;
    public interface ViewMode {
        int NORMAL = 0;
        int EDIT = 1;
    }
    private int viewMode = ViewMode.NORMAL;
    private LinearLayout bottomNormalLayout;
    private LinearLayout bottomEditLayout;
    private  TextView   selectAllBtn;
    private boolean  noData=false;
    private TextView joinGameView;
    private String mCourseSplitCourseName;
    public static boolean freshData=true;

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_noc_my_work, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        freshData=true;
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(freshData){
            freshData=false;
            loadViews();
        }
    }
    private void init(){
        initIntent();
        initViews();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
    private void initIntent(){
        Intent intent=getActivity().getIntent();
        title=intent.getStringExtra(EXTRA_COURSE_NAME);
        courseId=intent.getStringExtra(EXTRA_COURSE_ID);
        isSplit=intent.getBooleanExtra(EXTRA_IS_SPLIT,false);
        if (isSplit){
            mCourseSplitCourseName = intent.getStringExtra(EXTRA_COURSE_AUTHOR_NAME);
        }
    }
    private void initGirdView(){
    GridView gridView = (GridView) findViewById(R.id.grid_view);
    if (gridView != null) {
        final int min_padding = getResources().getDimensionPixelSize(R.dimen.min_padding);
        layoutId = R.layout.lq_course_grid_item;
        maxColumn = MAX_COLUMN;
        gridView.setNumColumns(maxColumn);
        gridView.setBackgroundColor(Color.WHITE);
        gridView.setVerticalSpacing(getResources().getDimensionPixelSize(R.dimen.gridview_spacing));
        gridView.setPadding(min_padding, min_padding, min_padding, min_padding);
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
                MediaInfo data = (MediaInfo) getDataAdapter().getItem(position);
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
                    params.width = itemSize - getActivity().getResources()
                            .getDimensionPixelSize(R.dimen.resource_gridview_padding);
                    params.height = params.width * 9 / 16;
                    frameLayout.setLayoutParams(params);
                }
                ImageView flag = (ImageView) view.findViewById(R.id.media_flag);
                flag.setVisibility(viewMode == ViewMode.NORMAL ? View.INVISIBLE :
                        View.VISIBLE);
                flag.setImageResource(data.isSelect() ? R.drawable.select :
                        R.drawable.unselect);
                ImageView thumbnail = (ImageView) view.findViewById(R.id.media_thumbnail);
                TextView name = (TextView) view.findViewById(R.id.media_name);
                View splitView = view.findViewById(R.id.media_split_btn);
//                if (data.isHasCollected()){
//                    Drawable leftDrawable =getResources().getDrawable(
//                            R.drawable.icon_star);
//                    leftDrawable.setBounds(0,0,leftDrawable.getMinimumWidth(),
//                            leftDrawable.getMinimumHeight());
//                    name.setCompoundDrawables(leftDrawable,null,null,null);
//                }else {
//                    name.setCompoundDrawables(null,null,null,null);
//                }

                LQImageLoader.displayImage(data.getThumbnail(), thumbnail, R.drawable.default_cover);
                if (!isSplit) {
                    name.setText(data.getTitle());
                } else {
                    name.setText(data.getSubTitle());
                }
                name.setSingleLine(false);
                name.setPadding(5,5,5,5);
                name.setTextSize(12);
                name.setLines(2);
                name.setGravity(Gravity.CENTER);
                //判断是否显示拆分页
                if (splitView != null) {
                    int resType = data.getResourceType();
                    if(resType == ResType.RES_TYPE_COURSE_SPEAKER){
                        splitView.setVisibility(View.VISIBLE);
                    }else{
                        splitView.setVisibility(View.GONE);
                    }
                    splitView.setTag(data);
                    splitView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MediaInfo mediaInfo = (MediaInfo) v.getTag();
                            if (mediaInfo != null) {
                                    Intent  intent = new Intent(getActivity(), NocMyWorkActivity.class);
                                    intent.putExtra(EXTRA_COURSE_ID, mediaInfo.getMicroId());
                                    intent.putExtra(EXTRA_COURSE_NAME, mediaInfo.getTitle());
                                    intent.putExtra(EXTRA_IS_SPLIT, true);
                                    intent.putExtra(EXTRA_COURSE_AUTHOR_NAME,mediaInfo.getAuthor());
                                    startActivity(intent);

                            }
                        }
                    });
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
                if(viewMode == ViewMode.NORMAL){
                        openCourse(data);
                }else{
                    data.setIsSelect(!data.isSelect());
                    getCurrAdapterViewHelper().update();
                    isSelectAll = isSelectAll();
                    checkState(isSelectAll);
                }
            }
        };
        setCurrAdapterViewHelper(gridView, helper);
    }

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
    private void initNormalView(){
        TextView rightBtn = (TextView) findViewById(R.id.contacts_header_right_btn);
        rightBtn.setText(R.string.activity_details);
        rightBtn.setOnClickListener(this);
        joinGameView = (TextView) findViewById(R.id.btn_bottom_joingame);
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if(isSplit&&title!=null){
            textView.setText(title);
            rightBtn.setVisibility(View.GONE);
            joinGameView.setVisibility(View.GONE);
        }else{
            textView.setText(R.string.noc_competition);
            rightBtn.setVisibility(View.VISIBLE);
            joinGameView.setVisibility(View.VISIBLE);
        }

        pullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        if (pullToRefreshView != null) {
//            setStopPullUpState(true);
//            setStopPullDownState(true);
            setPullToRefreshView(pullToRefreshView);
        }
        findViewById(R.id.btn_bottom_joingame).setOnClickListener(this);
        findViewById(R.id.btn_bottom_delete).setOnClickListener(this);
        bottomEditLayout  =(LinearLayout)findViewById(R.id.bottom_edit_layout);
        bottomNormalLayout=(LinearLayout)findViewById(R.id.bottom_normal_layout);
        initBottomLayout();
        selectAllBtn = (TextView) findViewById(R.id.btn_bottom_select_all);
        selectAllBtn.setOnClickListener(this);
        findViewById(R.id.btn_bottom_ok).setOnClickListener(this);
        findViewById(R.id.btn_bottom_cancel).setOnClickListener(this);


    }
    private void initBottomLayout(){
        if(isSplit){
            bottomEditLayout.setVisibility(View.GONE);
            bottomNormalLayout.setVisibility(View.GONE);
        }else{
            if(viewMode==ViewMode.EDIT){
                bottomEditLayout.setVisibility(View.VISIBLE);
                bottomNormalLayout.setVisibility(View.GONE);
            }else{
                bottomNormalLayout.setVisibility(View.VISIBLE);
                bottomEditLayout.setVisibility(View.GONE);
            }
        }
    }
    private void initViews() {
        initNormalView();
        initGirdView();
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
    private void deleteMediaList() {
        final List<MediaInfo> mediaInfos = getSelectedData();
        if (mediaInfos == null || mediaInfos.size() == 0) {
            TipMsgHelper.ShowLMsg(getActivity(), R.string.pls_select_files);
            return;
        }
        if (mediaInfos.size() == 0) {
            return;
        }
        pullToRefreshView.showRefresh();
        StringBuilder builder = new StringBuilder();
        int size = mediaInfos.size();
        for (int i = 0; i < size - 1; i++) {
            MediaInfo mediaInfo = mediaInfos.get(i);
            if (mediaInfo != null) {
                builder.append(mediaInfo.getId() + ",");
            }
        }
        builder.append(mediaInfos.get(size - 1).getId());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ids", builder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        builder = new StringBuilder();
        builder.append("?j=" + jsonObject.toString());
        ThisStringRequest request = new ThisStringRequest(
                Request.Method.GET, ServerUrl.DELETE_NOC_MY_WORK + builder.toString(), new Listener<String>() {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                if (jsonString != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(jsonString);
                        if (jsonObject != null) {
                            int code = jsonObject.optInt("Code");
                            if (code == 0) {
                                TipMsgHelper.ShowLMsg(getActivity(), R.string.cs_delete_success);
                            }else{
                                TipMsgHelper.ShowLMsg(getActivity(), R.string.Delete_failed);
                            }
                            if (mediaInfos != null && mediaInfos.size() > 0) {
                                for (MediaInfo info : mediaInfos) {
                                    if (info != null) {
                                        getCurrAdapterViewHelper().getData().remove(info);
                                    }
                                }
                            }
                            //删除完毕后，刷新数据。
                            viewMode= ViewMode.NORMAL;
                            initBottomLayout();
                            getCurrAdapterViewHelper().update();
                            if(getCurrAdapterViewHelper().getData()
                                    ==null||getCurrAdapterViewHelper().getData().size()==0){
                                noData=true;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(NetroidError error) {
                if (getActivity() == null) {
                    return;
                }
                TipMsgHelper.ShowMsg(getActivity(), getString(R.string.network_error));
            }

            @Override
            public void onFinish() {
                if (getActivity() == null) {
                    return;
                }
                super.onFinish();
                pullToRefreshView.hideRefresh();
            }
        });
        request.addHeader("Accept-Encoding", "*");
        request.start(getActivity());
    }

    public void loadViews() {
        getPageHelper().clear();
        loadDataList();
        pullToRefreshView.showRefresh();
    }

    private void loadDataList() {
        if(isSplit){
            loadSplitCourseList(courseId);
        }else{
            loadMediaList();
        }
    }

    private void loadMediaList() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("memberId", getMemeberId());
            jsonObject.put("pageIndex", getPageHelper().getFetchingPageIndex());
            jsonObject.put("pageSize", getPageHelper().getPageSize());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringBuilder builder = new StringBuilder();
        builder.append("?j=" + jsonObject.toString());
        ThisStringRequest request = new ThisStringRequest(
                Request.Method.GET, ServerUrl.GET_NOC_MY_WORK + builder.toString(), new Listener<String>() {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                parseData(jsonString);
            }

            @Override
            public void onError(NetroidError error) {
                if (getActivity() == null) {
                    return;
                }
                TipMsgHelper.ShowMsg(getActivity(), getString(R.string.network_error));
            }

            @Override
            public void onFinish() {
                if (getActivity() == null) {
                    return;
                }
                super.onFinish();
                pullToRefreshView.hideRefresh();
            }
        });
        request.addHeader("Accept-Encoding", "*");
        request.start(getActivity());
    }

    private void parseData(String jsonString) {
        if (jsonString != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                if (jsonObject != null) {
                    int code = jsonObject.optInt("Code");
                    if (code == 0) {
                        JSONArray jsonArray = jsonObject.optJSONArray("data");
                        if (jsonArray == null || jsonArray.length() <= 0) {
                            if (getPageHelper().isFetchingFirstPage()) {
                                TipsHelper.showToast(getActivity(),
                                        getString(R.string.no_data));
                                if(getCurrAdapterViewHelper().hasData()){
                                    getCurrAdapterViewHelper().clearData();
                                    getPageHelper().clear();
                                }
                                noData=true;
                            } else {
                                TipsHelper.showToast(getActivity(),
                                        getString(R.string.no_more_data));
                            }
                            return;
                        }
                        noData=false;
                        if (getPageHelper().isFetchingFirstPage()) {
                            if(getCurrAdapterViewHelper().hasData()){
                                getCurrAdapterViewHelper().clearData();
                                getPageHelper().clear();
                            }
                        }
                        getPageHelper().setCurrPageIndex(getPageHelper().getFetchingPageIndex());
                        List<MediaInfo> mediaInfos=new ArrayList<MediaInfo>();
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject object =(JSONObject)jsonArray.get(i);
                            MediaInfo item=  NocHelper.pase2MediaInfo(object);
                            mediaInfos.add(item);
                        }
                        if(!getPageHelper().isFetchingFirstPage()){
                            getPageHelper().setCurrPageIndex( getPageHelper().getFetchingPageIndex());
                        }
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
                        isSelectAll = isSelectAll();
                        checkState(isSelectAll);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadSplitCourseList(String courseId) {
        String url=null;
        JSONObject jsonObject = new JSONObject();
       try{
           jsonObject.put("pid", courseId);
           StringBuilder builder = new StringBuilder();
           builder.append("?j=" + jsonObject.toString());
            url = ServerUrl.SPLIT_COURSE_LIST_URL + builder.toString();
       }catch (Exception e){
           e.printStackTrace();
       }
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
                            mediaInfo.setMicroId(String.valueOf(info.getId()));
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
                            infoTag.setMicroId(mediaInfo.getId());
                            infoTag.setTitle(mediaInfo.getTitle());
                            infoTag.setMicroId(info.getId()+"");
                            //equals media type
                            infoTag.setType(mediaInfo.getType());
                            infoTag.setShareAddress(mediaInfo.getShareAddress());
                            infoTag.setThumbnail(mediaInfo.getThumbnail());
                            infoTag.setAuthorId(info.getMemberId());
                            if (TextUtils.isEmpty(mCourseSplitCourseName)){
                                infoTag.setAuthorName(info.getCreateName());
                            }else {
                                infoTag.setAuthorName(mCourseSplitCourseName);
                            }
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

    public void openCourseDetail(NewResourceInfo info, int fromType) {
        Intent intent = new Intent(getActivity(), PictureBooksDetailActivity.class);
        intent.putExtra(PictureBooksDetailActivity.NEW_RESOURCE_INFO, info);
        intent.putExtra(PictureBooksDetailActivity.FROM_SOURCE_TYPE, fromType);
        intent.putExtra(PictureBooksDetailFragment.Constants.EXTRA_COURSE_MODE_FROM,
                PictureBooksDetailFragment.CourseModelFrom.fromNocModel);
        //微课详情页面更新讨论
        startActivityForResult(intent,CampusPatrolPickerFragment.
                REQUEST_CODE_DISCUSSION_COURSE_DETAILS);
    }

    private void openCourse(MediaInfo data) {
        if (data == null){
            return;
        }
        if( data.getResourceType()>ResType.RES_TYPE_BASE){
            NewResourceInfo resourceInfo = data.getNewResourceInfoTag();
            int  fromWhere = PictureBooksDetailActivity.FROM_OTHRE;
            openCourseDetail(resourceInfo, fromWhere);
        }else{
           NocHelper.prepareEnterNocDetail2(data,getActivity());
        }

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.btn_bottom_joingame :
                NocResourceInfoFragment.backArgs=NocResourceInfoFragment.BACK_NOC_MY_WORK_PAGE;
                Intent intent =new Intent(getActivity(), NocEntryInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_bottom_delete :
                if(noData){
                    TipsHelper.showToast(getActivity(), getString(R.string.no_data));
                    return;
                }
                viewMode=ViewMode.EDIT;
                initBottomLayout();
                getCurrAdapterViewHelper().update();
                break;
            case R.id.btn_bottom_ok :
                deleteMediaList();
                break;
            case R.id.btn_bottom_cancel :
                viewMode=ViewMode.NORMAL;
                initBottomLayout();
                if(isSelectAll){
                    isSelectAll = !isSelectAll;
                    checkState(isSelectAll);
                    checkData(isSelectAll);
                }
                getCurrAdapterViewHelper().update();
                break;
            case R.id.btn_bottom_select_all :
                isSelectAll = !isSelectAll;
                checkState(isSelectAll);
                checkData(isSelectAll);
                break;
            case R.id.contacts_header_right_btn :
                enterMatchDetail();
                break;

        }
    }

    private void enterMatchDetail(){
        Intent intent = new Intent(getActivity(), NocMatchInfoActivity.class);
        startActivity(intent);
    }

    private void checkState(boolean isSelectAll) {
        //更新全选布局
        if (isSelectAll){
            selectAllBtn.setText(getString(R.string.cancel_to_select_all));
        }else {
            selectAllBtn.setText(getString(R.string.select_all));
        }
    }
}
