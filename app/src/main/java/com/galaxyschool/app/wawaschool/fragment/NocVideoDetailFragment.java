package com.galaxyschool.app.wawaschool.fragment;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.Request;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.ScreenUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.WawaCourseUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.lqwawa.lqbaselib.net.ThisStringRequest;
import com.galaxyschool.app.wawaschool.pojo.CommentData;
import com.galaxyschool.app.wawaschool.pojo.NocEnterDetailArguments;
import com.lecloud.sdk.constant.PlayerEvent;
import com.lecloud.sdk.constant.PlayerParams;
import com.lecloud.sdk.surfaceview.ISurfaceView;
import com.lecloud.sdk.surfaceview.impl.BaseSurfaceView;
import com.lecloud.sdk.videoview.VideoViewListener;
import com.lecloud.skin.videoview.vod.UIVodVideoView;
import com.lqwawa.apps.views.ContainsEmojiEditText;
import com.galaxyschool.app.wawaschool.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by KnIghT on 16-5-10.
 */
public class NocVideoDetailFragment extends ContactsListFragment {


    public static final String TAG = NocVideoDetailFragment.class.getSimpleName();
    private ListView listView;
    private  static  boolean hasCommented;
    private TextView titleView;
    private ContainsEmojiEditText commentEditText;
    private TextView commentCountTextview;
    private TextView noCommentTip;
    private TextView noBriefTip;
    private TextView commentTextview;
    private TextView introductionTextview;
    private boolean tag = true;
    private LinearLayout commentLayout;
    private LinearLayout sendCommentLayout;
    private LinearLayout briefLayout;
    private TextView briefContTextView;
    private TextView authorTextView;
    private TextView sourceTextView;
    private TextView nocNumView;
    private TextView readContTextView;
    private  View  commentLineView;
    private    View  introductionLineview;
    private TextView appreciateCountTextview;

    private UIVodVideoView videoView;
    public final static String DATA = "data";
    private int mPlayMode;
    private NocEnterDetailArguments nocArgs;
    private int type=1;//noc评论用
    private boolean hasPraised=false;

    private boolean isNeedRefresh=false;
        LinkedHashMap<String, String> rateMap  = new LinkedHashMap<String, String>();
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_noc_video_detail, null);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            initData();
            initViews();
            updateView();
            updateViewCount();//更新阅读数
            loadComments();//拉取评论
        }
    private void initData() {
        Intent intent = getActivity().getIntent();
        if (intent != null) {
            mBundle = intent.getBundleExtra(DATA);
            if (mBundle == null) {
                Toast.makeText(getActivity(), "no data", Toast.LENGTH_LONG).show();
                return;
            } else {
                mPlayMode = mBundle.getInt(PlayerParams.KEY_PLAY_MODE, -1);
                nocArgs=(NocEnterDetailArguments) mBundle.getSerializable(NocEnterDetailArguments.class
                        .getSimpleName());
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if (videoView != null) {
            videoView.onResume();
        }
    }
    private void updateView(){
        if(nocArgs==null)return;
        if(!TextUtils.isEmpty(nocArgs.getRemark())){
            briefContTextView.setVisibility(View.VISIBLE);
            briefContTextView.setText(nocArgs.getRemark());
            noBriefTip.setVisibility(View.GONE);
        }else{
            briefContTextView.setVisibility(View.GONE);
            noBriefTip.setVisibility(View.VISIBLE);
        }
        titleView.setText(nocArgs.getTitle());
        authorTextView.setText(getString(R.string.author) + nocArgs.getAuthor());

        if(nocArgs.getNocNameForType()==NocEnterDetailArguments.JOIN_NAME_FOR_SCHOOL
                &&!TextUtils.isEmpty(nocArgs.getOrgName())){
            sourceTextView.setVisibility(View.VISIBLE);
            sourceTextView.setText(getString(R.string.n_source, nocArgs.getOrgName()));
        }else{
            sourceTextView.setVisibility(View.GONE);
        }
        nocNumView.setText(getString(R.string.noc_num, nocArgs.getEntryNum()));
        readContTextView.setText(0 + getString(R.string.read_person));
        appreciateCountTextview.setText(nocArgs.getNocPraiseNum() + getString(R.string.praise_count));
    }

    private void initSomeViews() {
        titleView = (TextView) findViewById(R.id.contacts_header_title);//标题
        commentEditText = (ContainsEmojiEditText) findViewById(R.id.comment_edittext);
        noCommentTip = (TextView) findViewById(R.id.no_comment_tip);
        noBriefTip = ((TextView) findViewById(R.id.no_brief_tip));
        findViewById(R.id.send_textview).setOnClickListener(this);//评论按钮

        commentTextview = ((TextView) findViewById(R.id.comment_textview));
        commentTextview.setOnClickListener(this);
        introductionTextview = ((TextView) findViewById(R.id.introduction_textview));
        introductionTextview.setOnClickListener(this);

        commentLayout = (LinearLayout) findViewById(R.id.comment_layout);
        briefLayout = (LinearLayout) findViewById(R.id.brief_layout);
        briefContTextView = (TextView) findViewById(R.id.pic_book_brief_textview);

        authorTextView = (TextView) findViewById(R.id.pic_book_author_textview);//作者
        sourceTextView = (TextView) findViewById(R.id.pic_book_source_textview);//来源

        nocNumView = (TextView) findViewById(R.id.noc_num_textview);//编号
        readContTextView = (TextView) findViewById(R.id.pic_book_read_count_textview);//阅读个数
        commentCountTextview = ((TextView) findViewById(R.id.comment_count_textview));//评论个数
        commentLineView = (View) findViewById(R.id.comment_lineview);//阅读个数
        introductionLineview = ((View) findViewById(R.id.introduction_lineview));//评论个数
        sendCommentLayout=(LinearLayout)this.findViewById(R.id.send_comment_layout);//发表评论
        appreciateCountTextview = ((TextView) findViewById(R.id.appreciate_count_textview));//点赞个数
        findViewById(R.id.praise_btn).setOnClickListener(this);//点赞


    }

    private void initViews() {
        initSomeViews();
        initListView();
        initVideoView();
    }
    private void initVideoView(){
        videoView =  new UIVodVideoView(getActivity());
        videoView.setVideoViewListener(mVideoViewListener);
        String vuid = mBundle.getString(PlayerParams.KEY_PLAY_VUID);
        if(nocArgs == null) return;
        if(TextUtils.isEmpty(vuid) || nocArgs.getLeStatus() != 3){
            videoView.setDataSource(nocArgs.getResourceUrl());
        }else{
            videoView.setDataSource(mBundle);
        }
        final RelativeLayout videoContainer = (RelativeLayout) findViewById(R.id.videoContainer);
        videoContainer.addView((View) videoView, computeContainerSize(getActivity(), 16, 9));
        videoView.setTitle(nocArgs!=null? nocArgs.getTitle():"");

    }



    public static RelativeLayout.LayoutParams computeContainerSize(Context context, int mWidth,
                                                                   int mHeight) {
        int width = ScreenUtils.getScreenWidth(context);
        int height = width * mHeight / mWidth;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        params.width = width;
        params.height = height;
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        return params;
    }
    VideoViewListener mVideoViewListener = new VideoViewListener() {
        @Override
        public void onStateResult(int event, Bundle bundle) {
            handleVideoInfoEvent(event, bundle);// 处理视频信息事件
            handlePlayerEvent(event, bundle);// 处理播放器事件
            handleLiveEvent(event, bundle);// 处理直播类事件,如果是点播，则这些事件不会回调
        }

        @Override
        public String onGetVideoRateList(LinkedHashMap<String, String> map) {
            rateMap = map;
            for (Map.Entry<String, String> rates : map.entrySet()) {
                if (rates.getValue().equals("高清")) {
                    return rates.getKey();
                }
            }
            return "";
        }
    };


    @Override
    public void onPause() {
        super.onPause();
        if (videoView != null) {
            videoView.onPause();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (videoView != null) {
            videoView.onDestroy();
        }

    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        if (videoView != null) {
//            videoView.onConfigurationChanged(newConfig);
//        }
//    }

    /**
     * 处理播放器本身事件，具体事件可以参见IPlayer类
     */


    private ISurfaceView surfaceView;

    private void handlePlayerEvent(int state, Bundle bundle) {
        switch (state) {
            //准备开始播放
            case PlayerEvent.PLAY_PREPARED:
                if (videoView != null && MyApplication.getCdeInitSuccess()) {
                    videoView.onStart();
                }
                break;
            case PlayerEvent.ACTION_LIVE_PLAY_PROTOCOL:
                setActionLiveParameter(bundle.getBoolean(PlayerParams.KEY_PLAY_USEHLS));
                break;

            case PlayerEvent.PLAY_VIDEOSIZE_CHANGED:
                if (videoView!=null && videoView instanceof UIVodVideoView){
                    surfaceView = ((UIVodVideoView) videoView).getSurfaceView();
                    ((BaseSurfaceView) surfaceView).setDisplayMode(BaseSurfaceView.DISPLAY_MODE_SCALE_ZOOM);
                    int width = bundle.getInt(PlayerParams.KEY_WIDTH);
                    int height = bundle.getInt(PlayerParams.KEY_HEIGHT);
                    ((BaseSurfaceView) surfaceView).onVideoSizeChanged(width, height);
                }
                break;

            default:
                break;
        }
    }



    /**
     * 处理直播类事件
     */
    private void handleLiveEvent(int state, Bundle bundle) {
    }

    /**
     * 处理视频信息类事件
     */
    private void handleVideoInfoEvent(int state, Bundle bundle) {
    }

    private void setActionLiveParameter(boolean hls) {
        if (hls) {
            videoView.setCacheWatermark(1000, 100);
            videoView.setMaxDelayTime(50000);
            videoView.setCachePreSize(1000);
            videoView.setCacheMaxSize(40000);
        } else {
            //rtmp
            videoView.setCacheWatermark(500, 100);
            videoView.setMaxDelayTime(1000);
            videoView.setCachePreSize(200);
            videoView.setCacheMaxSize(10000);
        }
    }

    private Bundle mBundle;
    private void setImageLayout() {
        RelativeLayout thumbnailLayout = (RelativeLayout) findViewById(R.id.pic_book_thumbnail_layout);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) thumbnailLayout.getLayoutParams();
        params.width = ScreenUtils.getScreenWidth(getActivity()) / 2-getResources()
                .getDimensionPixelSize(R.dimen.resource_gridview_padding)*2;
        params.height = params.width * 9/ 16;
        thumbnailLayout.setLayoutParams(params);
    }

    private void initListView() {
        listView = (ListView) findViewById(R.id.listview);
        AdapterViewHelper helper = new AdapterViewHelper(
                getActivity(), listView, R.layout
                .wawatv_comment_list_item) {
            @Override
            public void loadData() {
                loadComments();
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final View view = super.getView(position, convertView, parent);
                view.setBackgroundColor(Color.parseColor("#F7F7F7"));
                final CommentData data = (CommentData) getDataAdapter().getItem(position);
                if (data == null) {
                    return view;
                }
                ViewHolder holder = (ViewHolder) view.getTag();
                if (holder == null) {
                    holder = new ViewHolder();
                }

                ImageView senderIcon = (ImageView) view.findViewById(R.id.comment_sender_icon);
                TextView senderName = (TextView) view.findViewById(R.id.comment_sender_name);
                final TextView commentContent = (TextView) view.findViewById(R.id.comment_content);
                TextView commentDate = (TextView) view.findViewById(R.id.comment_date);
                TextView commentPraise = (TextView) view.findViewById(R.id.comment_praise);
                commentPraise.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(data.getHeadpic())) {
                    getThumbnailManager().displayThumbnail(AppSettings.getFileUrl(data.getHeadpic()), senderIcon);
                } else {
                    senderIcon.setImageResource(R.drawable.comment_default_user_ico);
                }
                senderIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityUtils.enterPersonalSpace(getActivity(),
                                data.getMemberid());
                    }
                });

                if (!TextUtils.isEmpty(data.getCreatename())){
                    senderName.setText(data.getCreatename());
                }else if (!TextUtils.isEmpty(data.getAccount())){
                    senderName.setText(data.getAccount());
                }else {
                    senderName.setText(R.string.anonym);
                }

                commentContent.setText(data.getQuestion());
                commentDate.setText(data.getCreatetime());
                view.setTag(holder);
                return view;
            }

            @Override
            public void onItemClick(AdapterView parent, View convertView, int position, long id) {

            }
        };
        setCurrAdapterViewHelper(listView, helper);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.send_textview:
                sendCommet();
                break;
            case R.id.comment_textview:
                commmentTextViewEvent();
                break;
            case R.id.introduction_textview:
                introductionTextViewEvent();
                break;
            case R.id.praise_btn:
                if(hasPraised){
                    TipMsgHelper.ShowLMsg(getActivity(),getString(R.string.have_praised));
                    return;
                }
                if (!isLogin() && getActivity() != null) {
                    TipMsgHelper.ShowLMsg(getActivity(), getString(R.string.pls_login));
                    ActivityUtils.enterLogin(getActivity(), false);
                    return ;
                }
                updatePraiseCount();//更新点攒数
                break;
            case R.id.contacts_header_left_btn:
                if (isNeedRefresh) {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(ActivityUtils.REQUEST_CODE_NEED_TO_REFRESH, true);
                    intent.putExtras(bundle);
                    getActivity().setResult(Activity.RESULT_OK, intent);
                }
                getActivity().finish();
                break;
        }
    }



    private void commmentTextViewEvent() {
        if (tag == false) {
            tag = true;
        }
        if (tag) {
            commentTextview.setTextColor(getResources().getColor(R.color.text_green));
            introductionTextview.setTextColor(getResources().getColor(R.color.gray));
            commentLineView.setVisibility(View.VISIBLE);
            introductionLineview.setVisibility(View.INVISIBLE);
        } else {
            commentTextview.setTextColor(getResources().getColor(R.color.gray));
            introductionTextview.setTextColor(getResources().getColor(R.color.text_green));
            commentLineView.setVisibility(View.INVISIBLE);
            introductionLineview.setVisibility(View.VISIBLE);
        }
        commentLayout.setVisibility(View.VISIBLE);
        sendCommentLayout.setVisibility(View.VISIBLE);
        briefLayout.setVisibility(View.GONE);
    }

    private void introductionTextViewEvent() {
        if (tag == true) {
            tag = false;
        }
        if (tag) {
            commentTextview.setTextColor(getResources().getColor(R.color.text_green));
            introductionTextview.setTextColor(getResources().getColor(R.color.gray));
            commentLineView.setVisibility(View.VISIBLE);
            introductionLineview.setVisibility(View.INVISIBLE);
        } else {
            commentTextview.setTextColor(getResources().getColor(R.color.gray));
            introductionTextview.setTextColor(getResources().getColor(R.color.text_green));
            commentLineView.setVisibility(View.INVISIBLE);
            introductionLineview.setVisibility(View.VISIBLE);
        }
        commentLayout.setVisibility(View.GONE);
        sendCommentLayout.setVisibility(View.GONE);
        briefLayout.setVisibility(View.VISIBLE);
    }

    private void sendComment(long resId, String content) {
        WawaCourseUtils wawaCourseUtils = new WawaCourseUtils(getActivity());
        wawaCourseUtils.sendComment(resId, content,type);
        wawaCourseUtils.setOnCommentSendFinishListener(new WawaCourseUtils.OnCommentSendFinishListener() {
            @Override
            public void onCommentSendFinish(int code) {
                if (code == 0) {
                    //设置已经评论过
                    setHasCommented(true);
                    commentEditText.setText("");
                    getCurrAdapterViewHelper().clearData();
                    showLoadingDialog();
                    loadComments();
                }
            }
        });
    }

    public static void setHasCommented(boolean hasCommented) {
        hasCommented = hasCommented;
    }

    public static boolean hasCommented() {
        return hasCommented;
    }
    private void loadComments() {
        JSONObject jsonObject = new JSONObject();
        try {
            if (!TextUtils.isEmpty(nocArgs.getCourseId())) {
                jsonObject.put("courseId", nocArgs.getCourseId());
                jsonObject.put("type", type);
            }else {
               return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringBuilder builder = new StringBuilder();
        builder.append("?j=" + jsonObject.toString());
        String url = ServerUrl.WAWATV_COMMENT_LIST_URL + builder.toString();
        ThisStringRequest request = new ThisStringRequest(
                Request.Method.GET, url, new Listener<String>() {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                parseComments(jsonString);
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
                super.onFinish();
                dismissLoadingDialog();
            }
        });
        request.addHeader("Accept-Encoding", "*");
        request.start(getActivity());
    }

    private void parseComments(String jsonString) {
        if (jsonString != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                if (jsonObject != null) {
                    int code = jsonObject.optInt("code");
                    if (code == 0) {
                        getPageHelper().updateTotalCountByJsonString(jsonString);
                        JSONArray jsonArray = jsonObject.optJSONArray("data");
                        int total = jsonObject.optInt("total");
                        if (jsonArray != null) {
                            List<CommentData> commentDatas = JSON.parseArray(
                                    jsonArray.toString(), CommentData.class);
                            if (commentDatas != null && commentDatas.size() > 0) {
                                getPageHelper().setCurrPageIndex(getPageHelper().getFetchingPageIndex());
                                if (getPageHelper().getFetchingPageIndex() == 0) {
                                    getCurrAdapterViewHelper().clearData();
                                }
                                if (getCurrAdapterViewHelper().hasData()) {
                                    getCurrAdapterViewHelper().getData().addAll(commentDatas);
                                    getCurrAdapterViewHelper().update();
                                } else {
                                    getCurrAdapterViewHelper().setData(commentDatas);
                                }
                                String commentCount = getString(R.string.comment_person,
                                        String.valueOf(total));
                                commentCountTextview.setText(commentCount);
                                listView.setVisibility(View.VISIBLE);
                                noCommentTip.setVisibility(View.GONE);
                            } else {
                                total = 0;
                                String commentCount = getString(R.string.comment_person,
                                        String.valueOf(total));
                                commentCountTextview.setText(commentCount);
                                listView.setVisibility(View.GONE);
                                noCommentTip.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendCommet() {
        String content = commentEditText.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            TipMsgHelper.ShowLMsg(getActivity(), R.string.pls_input_comment_content);
            return;
        }
        sendComment(Long.parseLong(nocArgs.getCourseId()), content);
    }

    private void updateViewCount() {
        JSONObject jsonObject = new JSONObject();
        try {
            if (!TextUtils.isEmpty(nocArgs.getCourseId())) {
                jsonObject.put("resId", nocArgs.getCourseId());
            }else {
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringBuilder builder = new StringBuilder();
        builder.append("?j=" + jsonObject.toString());
        String url = ServerUrl.NOC_UPADTE_VIEWCOUONT + builder.toString();
        ThisStringRequest request = new ThisStringRequest(
                Request.Method.GET, url, new Listener<String>() {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                parseViewCount(jsonString);
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
                super.onFinish();
                dismissLoadingDialog();
            }
        });
        request.addHeader("Accept-Encoding", "*");
        request.start(getActivity());
    }

    private void parseViewCount(String jsonString) {
        if (jsonString != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                if (jsonObject != null) {
                    int code = jsonObject.optInt("code");
                    if (code == 0) {
                        String viewCount=jsonObject.get("viewCount").toString();
                        readContTextView.setText(viewCount + getString(R.string.read_person));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void updatePraiseCount() {
        JSONObject jsonObject = new JSONObject();
        try {
            if (!TextUtils.isEmpty(nocArgs.getCourseId())) {
                jsonObject.put("resId", nocArgs.getCourseId());
            }else {
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringBuilder builder = new StringBuilder();
        builder.append("?j=" + jsonObject.toString());
        String url = ServerUrl.NOC_UPADTE_PRAISECOUONT + builder.toString();
        ThisStringRequest request = new ThisStringRequest(
                Request.Method.GET, url, new Listener<String>() {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                parsePraiseCount(jsonString);
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
                super.onFinish();
                dismissLoadingDialog();
            }
        });
        request.addHeader("Accept-Encoding", "*");
        request.start(getActivity());
    }

    private void parsePraiseCount(String jsonString) {
        if (jsonString != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                if (jsonObject != null) {
                    int code = jsonObject.optInt("code");
                    if (code == 0) {
                        String praiseNum=jsonObject.get("praiseNum").toString();
                        appreciateCountTextview.setText(praiseNum + getString(R.string.praise_count));
                        hasPraised=true;
                        isNeedRefresh=true;
                        NocWorksFragment.freshData=true;
                        NocMyWorkFragment.freshData=true;
                        NocWorksMoreFragment.freshData=true;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            findViewById(R.id.contacts_header_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.send_comment_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.desc_layout).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.contacts_header_layout).setVisibility(View.GONE);
            findViewById(R.id.send_comment_layout).setVisibility(View.GONE);
            findViewById(R.id.desc_layout).setVisibility(View.GONE);
        }
    }
}
