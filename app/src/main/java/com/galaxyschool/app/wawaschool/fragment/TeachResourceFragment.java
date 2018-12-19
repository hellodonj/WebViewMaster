package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.Request;
import com.galaxyschool.app.wawaschool.AirClassroomActivity;
import com.galaxyschool.app.wawaschool.BookStoreListActivity;
import com.galaxyschool.app.wawaschool.PictureBooksDetailActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.ShellActivity;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.CallbackListener;
import com.galaxyschool.app.wawaschool.common.CampusPatrolUtils;
import com.galaxyschool.app.wawaschool.common.CollectionHelper;
import com.galaxyschool.app.wawaschool.common.DateUtils;
import com.galaxyschool.app.wawaschool.common.DialogHelper;
import com.galaxyschool.app.wawaschool.common.KeyboardStatusDetector;
import com.galaxyschool.app.wawaschool.common.LetvVodHelperNew;
import com.galaxyschool.app.wawaschool.common.PassParamhelper;
import com.galaxyschool.app.wawaschool.common.ScreenUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.common.UploadUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.common.WawaCourseUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.db.MediaDao;
import com.galaxyschool.app.wawaschool.db.dto.MediaDTO;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.imagebrowser.GalleryActivity;
import com.galaxyschool.app.wawaschool.medias.activity.MyLocalPictureListActivity;
import com.galaxyschool.app.wawaschool.medias.fragment.MyLocalPictureListFragment;
import com.galaxyschool.app.wawaschool.medias.fragment.SchoolResourceContainerFragment;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.lqwawa.lqbaselib.net.ThisStringRequest;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.AirClassMaterListResult;
import com.galaxyschool.app.wawaschool.pojo.AirClassroomMateria;
import com.galaxyschool.app.wawaschool.pojo.Emcee;
import com.galaxyschool.app.wawaschool.pojo.EmceeList;
import com.galaxyschool.app.wawaschool.pojo.MediaInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.PPTAndPDFCourseInfo;
import com.galaxyschool.app.wawaschool.pojo.PPTAndPDFCourseInfoCode;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.UploadParameter;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.MediaData;
import com.galaxyschool.app.wawaschool.pojo.weike.MediaUploadList;
import com.galaxyschool.app.wawaschool.pojo.weike.ShortCourseInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.SplitCourseInfo;
import com.galaxyschool.app.wawaschool.views.CircleImageView;
import com.galaxyschool.app.wawaschool.views.HalfRoundedImageView;
import com.galaxyschool.app.wawaschool.views.OnlineResPopupView;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.lecloud.sdk.videoview.IMediaDataVideoView;
import com.lecloud.skin.ui.utils.VodVideoSettingUtil;
import com.libs.gallery.ImageInfo;
import com.lqwawa.apps.views.ContainsEmojiEditText;
import com.lqwawa.client.pojo.MediaType;
import com.lqwawa.client.pojo.ResourceInfo;
import com.lqwawa.intleducation.module.discovery.tool.LoginHelper;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.libs.mediapaper.AudioPopwindow;
import com.lqwawa.libs.videorecorder.SimpleVideoRecorder;
import com.osastudio.common.utils.FileProviderHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeachResourceFragment extends ContactsListFragment {

    public static String TAG = TeachResourceFragment.class.getSimpleName();
    public static final int MSG_UPLOAD_MEDIA_FINISH = 10;
    public static final int MSG_RETURN_NEED_REFRESH_DATA = 11;
    public static final int MSG_SEND_TEXT_FINISH = 12;
    public static final int REQUEST_CODE_CAPTURE_CAMERA = 0;
    public static final int REQUEST_PERSONAL_CLOUD_RESOURCE = 2;
    private ContainsEmojiEditText emojiEditText;
    private ContainsEmojiEditText studentEditText;
    private LinearLayout bottomLayout, bottomStudentLayout;
    private ImageView addOnlineResource;
    private OnlineResPopupView onlineResPopupView;
    private Handler mhandler;
    private Emcee onlineRes;
    private boolean isNeedRefresh = true;
    private AudioPopwindow audioPopwindow;
    private String uploadRecordFilePath = null;//音频上传路径
    private String picFileName;
    private boolean isHeadMaster;
    private int mediaType = -1;
    private NewResourceInfo newResourceInfo = new NewResourceInfo();
    private List<EmceeList> onlineHost = new ArrayList<>();
    private SchoolInfo schoolInfo;
    private IMediaDataVideoView videoView;
    //第一次进入当前界面没有数据不给于toast提示
    private boolean isLogin = false;
    private boolean isFirstIn = true;
    private boolean isReporter;
    private boolean isHistoryClass;

    public TeachResourceFragment() {

    }

    public void setBottomLayoutId(LinearLayout bottomLayout, LinearLayout bottomStudentLayout) {
        this.bottomLayout = bottomLayout;
        this.bottomStudentLayout = bottomStudentLayout;
    }

    public interface ResourceType {
        int PHOTO_ABLUM = 1;
        int TAKE_PHOTO = 2;
        int AUDIO = 3;
        int VIDEO = 4;
        int PERSONAL_LIBRARY = 5;
        int PUBLIC_LIBRARY = 6;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_teach_resource, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isLogin = UserHelper.isLogin();
        loadIntentData();
        initViews();
    }

    private void loadIntentData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            isHeadMaster = bundle.getBoolean(AirClassroomFragment.Constants.EXTRA_IS_HEADMASTER, false);
            onlineRes = (Emcee) bundle.getSerializable(AirClassroomDetailFragment.Contants.EMECCBEAN);
            schoolInfo = (SchoolInfo) bundle.getSerializable(AirClassroomActivity.EXTRA_IS_SCHOOLINFO);
            isHistoryClass = bundle.getBoolean(ActivityUtils.EXTRA_IS_HISTORY_CLASS,false);
        }
    }

    private void initViews() {
        initNormalView();
        initStudentSendView();
        initListView();
        initSoftKeyBoardListener();
    }


    @Override
    public void onResume() {
        super.onResume();
        if (UserHelper.isLogin() && !isLogin()) {
            isLogin = true;
            isNeedRefresh = true;
        }
        if (isNeedRefresh) {
            loadOnlineMateria();
            isNeedRefresh = false;
        }
        continuePlayMedia();
    }

    private void initStudentSendView() {
        if (bottomStudentLayout == null) {
            //保护代码 bottomStudentLayout防止为空
            AirClassroomDetailFragment parentFragment = (AirClassroomDetailFragment) getParentFragment();
            if (parentFragment != null) {
                bottomStudentLayout = parentFragment.getBottomLayout(false);
                if (bottomStudentLayout == null) {
                    return;
                }
            }
        }
        studentEditText = (ContainsEmojiEditText) bottomStudentLayout.findViewById(R.id.comment_edittext_send);
        TextView sendBtn = (TextView) bottomStudentLayout.findViewById(R.id.send_textview);
        sendBtn.setOnClickListener(this);
    }

    private void initNormalView() {
        isReporter = isReporter();
        if (bottomLayout == null) {
            //保护代码 防止bottomLayout为空
            AirClassroomDetailFragment parentFragment = (AirClassroomDetailFragment) getParentFragment();
            if (parentFragment != null) {
                bottomLayout = parentFragment.getBottomLayout(true);
                if (bottomLayout == null) {
                    return;
                }
            }
        }
        emojiEditText = (ContainsEmojiEditText) bottomLayout.findViewById(R.id.comment_edittext);
        emojiEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String content = emojiEditText.getText().toString().trim();
                    if (TextUtils.isEmpty(content)) {
                        TipMsgHelper.ShowMsg(getActivity(), R.string.send_content_cannot_null);
                    } else {
                        UploadUtils.addAirClassroomMateria(getActivity(), onlineRes.getId(),
                                getMemeberId(), content, null, null, null, mhandler);
                        emojiEditText.setText("");
                        UIUtils.hideSoftKeyboardValid(getActivity(), emojiEditText);
                    }
                }
                return true;
            }
        });
        addOnlineResource = (ImageView) bottomLayout.findViewById(R.id.add_online_resource);
        addOnlineResource.setOnClickListener(this);
        mhandler = new Handler() {
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
                                        //在这里添加上传到空中课堂的资料的接口
                                        for (int i = 0; i < datas.size(); i++) {
                                            MediaData data = datas.get(i);
                                            UploadUtils.addAirClassroomMateria(getActivity(), onlineRes.getId(), getMemeberId(), null, data.getIdType(), data.resourceurl,
                                                    data.originname, mhandler);
                                        }
                                    }
                                } else {
                                    TipMsgHelper.ShowLMsg(getActivity(), R.string.upload_file_failed);
                                }
                            }
                        }
                        break;
                    case MSG_RETURN_NEED_REFRESH_DATA:
                        //发送资料成功后刷新数据
                        pageHelper.setFetchingPageIndex(0);
                        loadOnlineMateria();
                        break;
                    case MSG_SEND_TEXT_FINISH:
                        //软件中发送的文本数据
                        Bundle bundle = msg.getData();
                        String textMateria = bundle.getString("data");
                        UploadUtils.addAirClassroomMateria(getActivity(), onlineRes.getId(),
                                getMemeberId(), textMateria, null, null,
                                null, mhandler);
                        break;
                }
            }
        };
    }

    public void updateList(boolean isReporter) {
        isFirstIn = true;
        this.isReporter = isReporter;
        loadOnlineMateria();
    }

    public void loadOnlineMateria() {
        pageHelper.setPageSize(16);
        Map<String, Object> params = new HashMap();
        //必填
        params.put("ExtId", onlineRes.getId());
        params.put("Pager", getPageHelper().getFetchingPagerArgs());
        DefaultDataListener listener = new DefaultDataListener<AirClassMaterListResult>(
                AirClassMaterListResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                AirClassMaterListResult result = getResult();
                if (result == null || !result.isSuccess()
                        || result.getModel() == null) {
                    return;
                }
                updateAirClassroomListResult(result);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        };
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.GET_AIRCLASSROOM_MATERIA_LIST_BASE_URL, params, listener);
    }

    /**
     * Fragment当前状态是否可见
     */
    protected boolean isVisible;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
        } else {
            isVisible = false;
        }
    }

    private void updateAirClassroomListResult(AirClassMaterListResult result) {
        List<AirClassroomMateria> onlineList = result.getModel().getData();
        if (onlineList == null || onlineList.size() <= 0) {
            if (!isFirstIn) {
                if (isVisible) {
                    TipMsgHelper.ShowLMsg(getActivity(), getString(R.string.no_more_data));
                }
            } else {
                isFirstIn = false;
            }
            return;
        }
        if (getPageHelper().isFetchingPageIndex(result.getModel().getPager())) {
            if (getPageHelper().isFetchingFirstPage()) {
                getCurrAdapterViewHelper().clearData();
            }
            getPageHelper().updateByPagerArgs(result.getModel().getPager());
            getPageHelper().setCurrPageIndex(getPageHelper().getFetchingPageIndex());
            if (getCurrAdapterViewHelper().hasData()) {
                int position = getCurrAdapterViewHelper().getData().size();
                if (position > 0) {
                    position--;
                }
                getCurrAdapterViewHelper().getData().addAll(onlineList);
                //移到最前的位置
                getCurrAdapterView().setSelection(position);
            } else {
                getCurrAdapterViewHelper().setData(onlineList);
            }
        }
    }

    /**
     * 获取空中资料的type
     *
     * @param resType
     */
    private int getMateriaType(String resType) {
        if (!TextUtils.isEmpty(resType)) {
            if (resType.contains("-")) {
                String[] split = resType.split("-");
                return Integer.valueOf(split[1]);
            }
        }
        return -1;
    }

    private void initListView() {
        PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        if (pullToRefreshView != null) {
            setPullToRefreshView(pullToRefreshView);
        }
        ListView listView = (ListView) findViewById(R.id.listview);
        if (listView != null) {
            //长按时弹出来dialog收藏
//            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//                @Override
//                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                    AirClassroomMateria data = (AirClassroomMateria) getCurrAdapterViewHelper().getData().get(position);
//                    showPopForCollect(data);
//                    return true;
//                }
//            });
            AdapterViewHelper gridViewHelper = new AdapterViewHelper(getActivity(),
                    listView, R.layout.teach_resource_item) {
                @Override
                public void loadData() {
                    loadOnlineMateria();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    if (view == null) {
                        return view;
                    }
                    final AirClassroomMateria data = (AirClassroomMateria) getData().get(position);
                    if (data == null) {
                        return view;
                    }
                    //Todo 公共资源
                    //头像
                    CircleImageView personIcon = (CircleImageView) view.findViewById(R.id.person_icon);
                    if (personIcon != null) {
                        personIcon.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                ActivityUtils.enterPersonalSpace(getActivity(), String.valueOf(data
//                                        .getMemberId()));
                            }
                        });
                    }
                    //名字
                    TextView personName = (TextView) view.findViewById(R.id.person_name);
                    //时间
                    TextView sendTime = (TextView) view.findViewById(R.id.send_time);
                    //删除
                    TextView deleteBtn = (TextView) view.findViewById(R.id.delete_btn);
                    if (personName != null) {
                        if (!TextUtils.isEmpty(data.getRealName())) {
                            personName.setText(data.getRealName());
                        } else if (!TextUtils.isEmpty(data.getCreateName())) {
                            personName.setText(data.getCreateName());
                        } else if (!TextUtils.isEmpty(data.getNickName())) {
                            personName.setText(data.getNickName());
                        } else {
                            personName.setText(R.string.anonym);
                        }
                    }
                    if (sendTime != null) {
                        sendTime.setText(data.getCreateTime());
                    }
                    if (personIcon != null) {
                        getThumbnailManager().displayThumbnailWithDefault(
                                AppSettings.getFileUrl(data.getHeadPicUrl()), personIcon, R.drawable.default_user_icon);
                    }
                    if (isHistoryClass) {
                        deleteBtn.setVisibility(View.INVISIBLE);
                    } else {
                        //删除按钮的显示状态
                        if (isHeadMaster) {
                            deleteBtn.setVisibility(View.VISIBLE);
                        } else if (isReporter) {
                            deleteBtn.setVisibility(View.VISIBLE);
                        } else if (TextUtils.equals(getMemeberId(), data.getCreateId())) {
                            deleteBtn.setVisibility(View.VISIBLE);
                        } else {
                            deleteBtn.setVisibility(View.INVISIBLE);
                        }
                    }
                    //TODo 删除当前的发表的item  需要根据条件判断当前是否应该显示
                    deleteBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ContactsMessageDialog messageDialog = new ContactsMessageDialog(
                                    getActivity(), null,
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
                                            deleteItemListForMateria(data);
                                        }
                                    });
                            messageDialog.show();
                        }
                    });
                    ///对图片做处理
                    if (data.getResId().contains(",")) {
                        view.findViewById(R.id.video_layout).setVisibility(View.GONE);
                        view.findViewById(R.id.micro_crouse_Layout).setVisibility(View.GONE);
                        view.findViewById(R.id.pic_imageview).setVisibility(View.GONE);
                        view.findViewById(R.id.pdf_ppt_layout).setVisibility(View.GONE);
                        view.findViewById(R.id.audio_layout).setVisibility(View.GONE);
                        view.findViewById(R.id.text_layout).setVisibility(View.GONE);
                        final List<MediaInfo> mediaInfos = dealWithPictureData(data);
                        GridView picGridView = (GridView) view.findViewById(R.id.pic_show_gridview);
                        if (picGridView != null) {
//                            picGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//                                @Override
//                                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                                    MediaInfo mediaInfo = mediaInfos.get(position);
//                                    showPopForCollectForImage(mediaInfo,data);
//                                    return true;
//                                }
//                            });
                            picGridView.setVisibility(View.VISIBLE);
                            AdapterViewHelper picAdapter = new AdapterViewHelper(getActivity(),
                                    picGridView, R.layout.imageview_item) {
                                @Override
                                public void loadData() {

                                }

                                @Override
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    View view = super.getView(position, convertView, parent);
                                    if (view != null) {
                                        MediaInfo mediaInfo = (MediaInfo) getData().get(position);
                                        if (mediaInfo != null) {
                                            ImageView imageView = (ImageView) view.findViewById(R.id
                                                    .pic_imageview);
                                            if (imageView != null) {
                                                getThumbnailManager().displayThumbnailWithDefault
                                                        (mediaInfo.getResourceUrl(), imageView, R.drawable.default_cover);
                                            }
                                        }
                                    }
                                    return view;
                                }

                                @Override
                                public void onItemClick(AdapterView parent, View view, int position, long id) {

                                    List<ImageInfo> resourceInfoList = new ArrayList<>();
                                    for (MediaInfo mediaInfo : mediaInfos) {
                                        ImageInfo newResourceInfo = new ImageInfo();
                                        newResourceInfo.setTitle(mediaInfo.getTitle());
                                        newResourceInfo.setResourceUrl(AppSettings.getFileUrl(mediaInfo.getResourceUrl()));
                                        newResourceInfo.setResourceId(mediaInfo.getId());
                                        newResourceInfo.setResourceType(ResType.RES_TYPE_IMG);
                                        newResourceInfo.setAuthorId(mediaInfo.getAuthorId());
                                        resourceInfoList.add(newResourceInfo);
                                    }
//                                    ActivityUtils.openImage(getActivity(), resourceInfoList, true, position,false,true, !getArguments().getBoolean(ISMOOC));
                                    ActivityUtils.openImage(getActivity(), resourceInfoList,
                                            true, position, false, true, false);

                                }
                            };
                            addAdapterViewHelper("picImage", picAdapter);
                        }
                        getAdapterViewHelper("picImage").setData(mediaInfos);
                    } else {
                        view.findViewById(R.id.pic_show_gridview).setVisibility(View.GONE);
                        int type = getMateriaType(data.getResId());
                        if (type > ResType.RES_TYPE_BASE) {
                            type = type % ResType.RES_TYPE_BASE;
                        }
                        //TODO 公共资源
                        //视频
                        FrameLayout videoLayout = (FrameLayout) view.findViewById(R.id.video_layout);
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) videoLayout.getLayoutParams();
                        int windowWith = ScreenUtils.getScreenWidth(getActivity());//屏幕宽度
                        int itemWidth = (windowWith - getResources().
                                getDimensionPixelSize(R.dimen.separate_20dp) * 5) / 2;
                        params.width = itemWidth;
                        params.height = params.width * 9 / 16;
                        videoLayout.setLayoutParams(params);
                        ImageView thubnail = (ImageView) view.findViewById(R.id.thubnail);
                        if (thubnail != null) {
                            getThumbnailManager().displayThumbnailWithDefault(data.getThumbnail(), thubnail, R
                                    .drawable.default_cover);
                        }
                        //图片
                        ImageView picImage = (ImageView) view.findViewById(R.id.pic_imageview);
                        //微课
                        LinearLayout microCrouseLayout = (LinearLayout) view.findViewById(R.id.micro_crouse_Layout);
                        TextView textView = (TextView) view.findViewById(R.id.resource_title);
                        if (textView != null) {
                            textView.setSingleLine(false);
                            textView.setPadding(5, 5, 5, 5);
                            textView.setTextSize(12);
                            textView.setLines(2);
                            textView.setText(data.getResTitle());
                        }
                        FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.resource_frameLayout);
                        params = (LinearLayout.LayoutParams) frameLayout
                                .getLayoutParams();
                        windowWith = ScreenUtils.getScreenWidth(getActivity());//屏幕宽度
                        itemWidth = (windowWith - getResources().
                                getDimensionPixelSize(R.dimen.separate_20dp) * 5) / 2;
                        params.width = itemWidth;
                        params.height = params.width * 9 / 16;
                        frameLayout.setLayoutParams(params);
                        params = (LinearLayout.LayoutParams) textView.getLayoutParams();
                        params.width = itemWidth;
                        textView.setLayoutParams(params);

                        ImageView imageView = (HalfRoundedImageView) view.findViewById(R.id.resource_thumbnail);
                        if (imageView != null) {
                            //TODO 暂无
                            getThumbnailManager().displayThumbnailWithDefault(AppSettings.getFileUrl(data
                                    .getThumbnail()), imageView, R.drawable.default_cover);
                        }
                        //pdf ppt
                        LinearLayout pdfPptLayout = (LinearLayout) view.findViewById(R.id.pdf_ppt_layout);
                        ImageView pdfPptImageView = (ImageView) view.findViewById(R.id.media_thumbnail);
                        TextView mediaNameView = (TextView) view.findViewById(R.id.media_name);
                        if (mediaNameView != null) {
                            mediaNameView.setText(data.getResTitle());
                        }
                        if (pdfPptImageView != null) {
                            if (type == ResType.RES_TYPE_PDF) {
                                pdfPptImageView.setImageResource(R.drawable.airclass_online_pdf);
                            } else if (type == ResType.RES_TYPE_PPT) {
                                pdfPptImageView.setImageResource(R.drawable.airclass_online_ppt);
                            }
                        }
                        //显示图片的比例
                        params = (LinearLayout.LayoutParams) picImage.getLayoutParams();
                        windowWith = ScreenUtils.getScreenWidth(getActivity());//屏幕宽度
                        itemWidth = (windowWith - getResources().getDimensionPixelSize(R.dimen.separate_20dp) * 5) / 2;
                        params.width = itemWidth;
                        params.height = params.width * 9 / 16;
                        picImage.setLayoutParams(params);
                        if (picImage != null) {
                            getThumbnailManager().displayThumbnailWithDefault(data.getThumbnail(), picImage, R
                                    .drawable.default_cover);
                        }
                        //音频
                        LinearLayout audioLayout = (LinearLayout) view.findViewById(R.id.audio_layout);
                        TextView audioTimeView = (TextView) view.findViewById(R.id.audio_time);
                        if (audioTimeView != null) {
                            //应该是音频的时长
                            if (getMateriaType(data.getResId()) == ResType.RES_TYPE_VOICE) {
                                setAudioDuration(data, audioTimeView);
                            }
                        }
                        //显示发送的文本
                        LinearLayout textLayout = (LinearLayout) view.findViewById(R.id.text_layout);
                        TextView textContent = (TextView) view.findViewById(R.id.text_content);
                        if (textContent != null) {
                            String content = data.getContents();
                            if (!TextUtils.isEmpty(content)) {
                                textContent.setText(content);
                            }
                        }
                        switch (type) {
                            case ResType.RES_TYPE_IMG:
                                textLayout.setVisibility(View.GONE);
                                picImage.setVisibility(View.VISIBLE);
                                videoLayout.setVisibility(View.GONE);
                                microCrouseLayout.setVisibility(View.GONE);
                                pdfPptLayout.setVisibility(View.GONE);
                                audioLayout.setVisibility(View.GONE);
                                break;
                            case ResType.RES_TYPE_VOICE:
                                textLayout.setVisibility(View.GONE);
                                audioLayout.setVisibility(View.VISIBLE);
                                microCrouseLayout.setVisibility(View.GONE);
                                picImage.setVisibility(View.GONE);
                                videoLayout.setVisibility(View.GONE);
                                pdfPptLayout.setVisibility(View.GONE);
                                break;
                            case ResType.RES_TYPE_VIDEO:
                                textLayout.setVisibility(View.GONE);
                                videoLayout.setVisibility(View.VISIBLE);
                                picImage.setVisibility(View.GONE);
                                microCrouseLayout.setVisibility(View.GONE);
                                pdfPptLayout.setVisibility(View.GONE);
                                audioLayout.setVisibility(View.GONE);
                                break;
                            case ResType.RES_TYPE_PDF:
                            case ResType.RES_TYPE_PPT:
                                textLayout.setVisibility(View.GONE);
                                pdfPptLayout.setVisibility(View.VISIBLE);
                                microCrouseLayout.setVisibility(View.GONE);
                                picImage.setVisibility(View.GONE);
                                videoLayout.setVisibility(View.GONE);
                                audioLayout.setVisibility(View.GONE);
                                break;
                            case ResType.RES_TYPE_COURSE:
                                break;
                            case ResType.RES_TYPE_NOTE:
                                break;
                            case ResType.RES_TYPE_ONEPAGE:
                            case ResType.RES_TYPE_COURSE_SPEAKER:
                            case ResType.RES_TYPE_STUDY_CARD:
                            case ResType.RES_TYPE_OLD_COURSE:
                                textLayout.setVisibility(View.GONE);
                                microCrouseLayout.setVisibility(View.VISIBLE);
                                picImage.setVisibility(View.GONE);
                                videoLayout.setVisibility(View.GONE);
                                pdfPptLayout.setVisibility(View.GONE);
                                audioLayout.setVisibility(View.GONE);
                                break;
                            case ResType.RES_TYPE_RESOURCE:
                                break;
                            default:
                                textLayout.setVisibility(View.VISIBLE);
                                microCrouseLayout.setVisibility(View.GONE);
                                picImage.setVisibility(View.GONE);
                                videoLayout.setVisibility(View.GONE);
                                pdfPptLayout.setVisibility(View.GONE);
                                audioLayout.setVisibility(View.GONE);
                                break;
                        }

                        //给单个的view设置监听
                        setViewOnClickListener(data, microCrouseLayout);
                        setViewOnClickListener(data, picImage);
                        setViewOnClickListener(data, videoLayout);
                        setViewOnClickListener(data, pdfPptLayout);
                        setViewOnClickListener(data, audioLayout);

                    }
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;
                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null || holder.data == null) {
                        return;
                    }
//                    openAirClassroomMaterDetail((AirClassroomMateria) holder.data);
                }
            };
            setCurrAdapterViewHelper(listView, gridViewHelper);
        }
    }

    private void setViewOnClickListener(final AirClassroomMateria data, View view) {
        if (view != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openAirClassroomMaterDetail(data);
                }
            });
        }
    }

    private void setAudioDuration(AirClassroomMateria data, TextView audioTimeView) {
        if (data != null) {
            String audioTime = data.getAudioTime();
            if (TextUtils.isEmpty(audioTime)) {
                TotalTimeTask timeTask = new TotalTimeTask(data, audioTimeView);
                timeTask.execute();
            } else {
                audioTimeView.setText(audioTime + "''");
            }
        }
    }

    public class TotalTimeTask extends AsyncTask<Object, Void, String> {
        private String audioUrl;
        private TextView audioTimeView;
        private AirClassroomMateria data;

        public TotalTimeTask(AirClassroomMateria data, TextView audioTimeView) {
            this.data = data;
            this.audioUrl = data.getResUrl();
            this.audioTimeView = audioTimeView;
        }

        @Override
        protected String doInBackground(Object... params) {
            String audioTime = getRingDuring(audioUrl);
            long totalTime = 0;
            if (!TextUtils.isEmpty(audioTime)) {
                totalTime = Long.valueOf(audioTime);
            }
            totalTime = (long) div(totalTime, 1000, 0);
            return totalTime + "";
        }

        @Override
        protected void onPostExecute(String reslut) {
            if (!TextUtils.isEmpty(reslut)) {
                audioTimeView.setText(reslut + "''");
                data.setAudioTime(reslut);
            }
        }
    }

    public double div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_DOWN).doubleValue();
    }

    public static String getRingDuring(String mUri) {
        String duration = null;
        android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();
        try {
            if (mUri != null) {
                HashMap<String, String> headers = null;
                if (headers == null) {
                    headers = new HashMap<>();
                    headers.put("User-Agent", "Mozilla/5.0 (Linux; U; Android 4.4.2; zh-CN; MW-KW-001 Build/JRO03C) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 UCBrowser/1.0.0.001 U4/0.8.0 Mobile Safari/533.1");
                }
                mmr.setDataSource(mUri, headers);
            }
            duration = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);
        } catch (Exception ex) {
        } finally {
            mmr.release();
        }
        return duration;
    }

    /**
     * 打开空中课堂的列表
     *
     * @param data
     */
    private void openAirClassroomMaterDetail(AirClassroomMateria data) {
        int type = getMateriaType(data.getResId());
        if (type > ResType.RES_TYPE_BASE) {
            type = type % ResType.RES_TYPE_BASE;
        }
        switch (type) {
            case ResType.RES_TYPE_IMG:
                openImage(data);
                break;
            case ResType.RES_TYPE_VOICE:
                openVideoOrAudio(data, type);
                break;
            case ResType.RES_TYPE_VIDEO:
                openVideoOrAudio(data, type);
                break;
            case ResType.RES_TYPE_PDF:
            case ResType.RES_TYPE_PPT:
            case ResType.RES_TYPE_DOC:
                openPPDAndPDFDetails(data);
                break;
            case ResType.RES_TYPE_COURSE:
                break;
            case ResType.RES_TYPE_NOTE:
                break;
            case ResType.RES_TYPE_OLD_COURSE:
            case ResType.RES_TYPE_ONEPAGE:
            case ResType.RES_TYPE_COURSE_SPEAKER:
                loadCourseDetails(data);
                break;
            case ResType.RES_TYPE_RESOURCE:
                break;
            case ResType.RES_TYPE_STUDY_CARD:
                loadCourseDetails(data);
                break;
        }
    }

    /**
     * 打开图片
     *
     * @param image
     */
    private void openImage(AirClassroomMateria image) {
        List<ImageInfo> resourceInfoList = new ArrayList<>();
        ImageInfo newResourceInfo = new ImageInfo();
        newResourceInfo.setTitle(image.getResTitle());
        newResourceInfo.setResourceUrl(AppSettings.getFileUrl(image.getResUrl()));
        newResourceInfo.setResourceId(image.getResId());
        newResourceInfo.setAuthorId(image.getCreateId());

        int type = getMateriaType(image.getResId());
        if (type > ResType.RES_TYPE_BASE) {
            type = type % ResType.RES_TYPE_BASE;
        }

        newResourceInfo.setResourceType(type);
        resourceInfoList.add(newResourceInfo);

//        GalleryActivity.newInstance(getActivity(),resourceInfoList,true,0,false,true,
//                !getArguments().getBoolean(ISMOOC));
        GalleryActivity.newInstance(getActivity(), resourceInfoList, true, 0, false, true, false);
    }

    private void showPopForCollectForImage(final MediaInfo data, final AirClassroomMateria airData) {
        int type = data.getResourceType();
        if (type == -1) {
            return;
        }
        String tag = getString(R.string.pictures);
        final String resId = data.getId();
        if (TextUtils.isEmpty(resId)) {
            return;
        }
        final String tagInfo = tag;
        final DialogHelper.CollectDialog collectDialog = DialogHelper.getIt(getActivity())
                .getCollectDialog();
        collectDialog.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CollectionHelper collectionHelper = new CollectionHelper(getActivity());
                        collectionHelper.collectDifferentResource(resId, data.getTitle(), airData
                                .getCreateId(), tagInfo);
                        collectDialog.dismiss();
                    }
                });
    }

    /**
     * 转化服务端返回的多张图片
     *
     * @param data
     * @return
     */
    private List<MediaInfo> dealWithPictureData(AirClassroomMateria data) {
        List<MediaInfo> mediaInfos = new ArrayList<>();
        MediaInfo mediaInfo = new MediaInfo();
        String resId = data.getResId();
        String resUrl = data.getResUrl();
        String resTitle = data.getResTitle();
        String resThumbnail = data.getThumbnail();
        String createId = data.getCreateId();
        if (resId.contains(",")) {
            String[] resIdArray = resId.split(",");
            String[] resUrlArray = resUrl.split(",");
            String[] resTitleArray = resTitle.split(",");
            String[] resThumbnailArray = resUrl.split(",");
            for (int i = 0; i < resIdArray.length; i++) {
                mediaInfo = new MediaInfo();
                mediaInfo.setId(resIdArray[i]);
                mediaInfo.setResourceUrl(resUrlArray[i]);
                mediaInfo.setTitle(resTitleArray[i]);
                mediaInfo.setThumbnail(resUrlArray[i]);
                mediaInfo.setAuthorId(createId);
                mediaInfos.add(mediaInfo);
            }
        } else {
            mediaInfo.setId(resId);
            mediaInfo.setResourceUrl(resUrl);
            mediaInfo.setTitle(resTitle);
            mediaInfo.setThumbnail(resThumbnail);
            mediaInfo.setAuthorId(createId);
            mediaInfos.add(mediaInfo);
        }
        return mediaInfos;
    }

    /**
     * 打开ppt或者pdf
     *
     * @param data
     */
    private void openPPDAndPDFDetails(final AirClassroomMateria data) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("resId", data.getResId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringBuilder builder = new StringBuilder();
        builder.append("?j=" + jsonObject.toString());
        String url = ServerUrl.WAWATV_COURSE_DETAIL_URL + builder.toString();
        final ThisStringRequest request = new ThisStringRequest(
                Request.Method.GET, url, new Listener<String>() {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                if (TextUtils.isEmpty(jsonString)) {
                    return;
                }
                PPTAndPDFCourseInfoCode result = com.alibaba.fastjson.JSONObject.parseObject
                        (jsonString, PPTAndPDFCourseInfoCode.class);
                if (result != null) {
                    List<ImageInfo> resourceInfoList = new ArrayList<>();
                    List<PPTAndPDFCourseInfo> splitCourseInfo = result.getData();
                    List<SplitCourseInfo> splitList = splitCourseInfo.get(0).getSplitList();
                    if (splitList == null || splitList.size() == 0) {
                        TipMsgHelper.ShowLMsg(getActivity(), R.string.ppt_pdf_not_have_pic);
                        return;
                    }
                    int type = getMateriaType(data.getResId());
                    if (type > ResType.RES_TYPE_BASE) {
                        type = type % ResType.RES_TYPE_BASE;
                    }
                    if (splitList.size() > 0) {
                        for (int i = 0; i < splitList.size(); i++) {
                            SplitCourseInfo splitCourse = splitList.get(i);
                            ImageInfo newResourceInfo = new ImageInfo();
                            newResourceInfo.setTitle(splitCourseInfo.get(0).getOriginname());
                            newResourceInfo.setResourceUrl(AppSettings.getFileUrl(splitCourse.getPlayUrl()));
                            newResourceInfo.setResourceId(data.getResId());
                            newResourceInfo.setResourceType(type);
                            newResourceInfo.setAuthorId(data.getCreateId());
                            resourceInfoList.add(newResourceInfo);
                        }
                    }
                    GalleryActivity.newInstance(getActivity(), resourceInfoList, true, 0, false, true, false);
                }
            }

            @Override
            public void onError(NetroidError error) {
                if (getActivity() == null) {
                    return;
                }
            }
        });
        request.addHeader("Accept-Encoding", "*");
        request.start(getActivity());
    }

    /**
     * 打开音频或者视频
     *
     * @param data
     */
    private void openVideoOrAudio(AirClassroomMateria data, int type) {
        pauseMedia();
        int media_type = VodVideoSettingUtil.VIDEO_TYPE;
        if (type == ResType.RES_TYPE_VOICE) {
            media_type = VodVideoSettingUtil.AUDIO_TYPE;
        }
        String filePath = AppSettings.getFileUrl(data.getResUrl());

        String leValue = data.getLeValue();
        LetvVodHelperNew.VodVideoBuilder builder = new LetvVodHelperNew.VodVideoBuilder
                (getActivity())
                .setNewUI(true)//使用自定义UI
                .setTitle(data.getResTitle())//视频标题
                .setMediaType(media_type)
                .setResId(data.getResId())
                .setAuthorId(data.getCreateId())
                .setLeStatus(data.getLeStatus())
                .setHideBtnMore(true);
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


    /**
     * 加载微课数据并且打开
     *
     * @param course
     */
    private void loadCourseDetails(AirClassroomMateria course) {
        String resId = course.getResId();
        String resType = null;
        if (TextUtils.isEmpty(resId)) {
            return;
        }
        if (resId.contains("-")) {
            String[] split = resId.split("-");
            resId = split[0];
            resType = split[1];
        }
        WawaCourseUtils utils = new WawaCourseUtils(getActivity());
        final PassParamhelper mParam = new PassParamhelper();
        mParam.isFromLQMOOC = true;
        //拆分
        if (!TextUtils.isEmpty(resType) && resType.length() > 3) {
            utils.loadSplitCourseDetail(Integer.valueOf(resId));
            utils.setOnSplitCourseDetailFinishListener(new WawaCourseUtils.OnSplitCourseDetailFinishListener() {
                @Override
                public void onSplitCourseDetailFinish(SplitCourseInfo info) {
                    if (info != null) {
                        String tempThumbnail = null;
                        if (TextUtils.isEmpty(newResourceInfo.getThumbnail())) {
                            tempThumbnail = info.getThumbUrl();
                        }
                        newResourceInfo = info.getNewResourceInfo();
                        if (TextUtils.isEmpty(newResourceInfo.getThumbnail()) && !TextUtils.isEmpty
                                (tempThumbnail)) {
                            newResourceInfo.setThumbnail(tempThumbnail);
                        }

                        if (newResourceInfo.isStudyCard()) {

                            ActivityUtils.enterTaskOrderDetailActivity(getActivity(), newResourceInfo, mParam);
                        } else {

                            Bundle bundle = new Bundle();
                            bundle.putSerializable(PassParamhelper.class.getSimpleName(), mParam);
                            newResourceInfo.setIsPublicResource(false);
                            ActivityUtils.openPictureDetailActivity(getActivity(), newResourceInfo, PictureBooksDetailActivity.FROM_OTHRE, false, bundle);
                        }
                    }
                }
            });
        } else {
            utils.loadCourseDetail(resId);
            utils.setOnCourseDetailFinishListener(new WawaCourseUtils.OnCourseDetailFinishListener() {
                @Override
                public void onCourseDetailFinish(CourseData courseData) {
                    if (courseData != null) {
                        String tempThumbnail = null;
                        if (TextUtils.isEmpty(newResourceInfo.getThumbnail())) {
                            tempThumbnail = courseData.imgurl;
                        }
                        newResourceInfo = courseData.getNewResourceInfo();
                        if (TextUtils.isEmpty(newResourceInfo.getThumbnail()) && !TextUtils.isEmpty
                                (tempThumbnail)) {
                            newResourceInfo.setThumbnail(tempThumbnail);
                        }
                        if (newResourceInfo.isStudyCard()) {
                            newResourceInfo.setIsFromSchoolResource(true);
                            newResourceInfo.setIsFromAirClass(true);
                            ActivityUtils.enterTaskOrderDetailActivity(getActivity(), newResourceInfo, mParam);
                        } else {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(PassParamhelper.class.getSimpleName(), mParam);
                            newResourceInfo.setIsPublicResource(false);
                            ActivityUtils.openPictureDetailActivity(getActivity(), newResourceInfo, PictureBooksDetailActivity.FROM_OTHRE, false, bundle);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.add_online_resource) {
            showPopAddResource(OnlineResPopupView.CurrentStatus.addResource);
        } else if (v.getId() == R.id.send_textview) {
            if (UserHelper.isLogin()) {
                sendContent();
            } else {
                LoginHelper.enterLogin(getActivity());
            }
        }
    }

    /**
     * 发送互动交流的内容
     */
    private void sendContent() {
        String content = studentEditText.getText().toString();
        if (TextUtils.isEmpty(content)) {
            TipMsgHelper.ShowMsg(getActivity(), R.string.send_content_cannot_null);
            return;
        }
        UploadUtils.addAirClassroomMateria(getActivity(), onlineRes.getId(),
                getMemeberId(), content, null, null, null, mhandler);
        studentEditText.setText("");
        UIUtils.hideSoftKeyboardValid(getActivity(), studentEditText);
        ;
    }

    private void deleteItemListForMateria(final AirClassroomMateria data) {
        if (data == null) return;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("Id", data.getId());
        RequestHelper.RequestListener listener =
                new RequestHelper.RequestDataResultListener<DataModelResult>(
                        getActivity(), DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()) {
                            return;
                        } else {
                            String errorMessage = getResult().getErrorMessage();
                            if (TextUtils.isEmpty(errorMessage)) {
                                getCurrAdapterViewHelper().getData().remove(data);
                                getCurrAdapterViewHelper().update();
                                TipMsgHelper.ShowMsg(getActivity(), R.string.cs_delete_success);
                            } else {
                                TipMsgHelper.ShowLMsg(getActivity(), errorMessage);
                            }

                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        dismissLoadingDialog();
                    }
                };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.DELETE_AIRCLASSROOM_MATERIA_DATA_BASE_URL, params, listener);
    }

    /**
     * 显示添加直播的资源
     */
    private void showPopAddResource(int currentStatus) {
        UIUtils.hideSoftKeyboardValid(getActivity(), emojiEditText);
        //当前输入框的文本
        String mAlreadyEditText = emojiEditText.getText().toString();
        onlineResPopupView = new OnlineResPopupView(getActivity(), mhandler, currentStatus);
        onlineResPopupView.setIsNew(true);
        //把文本设置到pop中的输入框中
        onlineResPopupView.setDefaultEditText(mAlreadyEditText);

        if (currentStatus == OnlineResPopupView.CurrentStatus.addResource) {
            onlineResPopupView.setTempFlag(false);
        } else {
            onlineResPopupView.setTempFlag(true);
        }
        onlineResPopupView.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                bottomLayout.setVisibility(View.VISIBLE);
                String editText = onlineResPopupView.getCurrentEditTextContent();
                emojiEditText.setText(editText);
                if (!TextUtils.isEmpty(editText)) {
                    emojiEditText.setSelection(editText.length());
                }
            }
        });

        onlineResPopupView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    //相册
                    case 0:
                        mediaType = MediaType.PICTURE;
                        choosePictureToMateria();
                        break;
                    //拍摄
                    case 1:
                        mediaType = MediaType.PICTURE;
                        takePhotoToMateria();
                        break;
                    //视频
                    case 2:
                        mediaType = MediaType.VIDEO;
                        chooseVideoToMateria();
                        break;
                    //音频
                    case 3:
                        mediaType = MediaType.AUDIO;
                        chooseAudioToMateria();
                        break;
                    //个人资源库
                    case 4:
                        personalLibraryToMateria();
                        break;
                    //校本资源库
                    case 5:
                        publicLibraryToMateria();
                        break;
                }
                onlineResPopupView.dismiss();
            }
        });
        ColorDrawable bg = new ColorDrawable(Color.TRANSPARENT);
        onlineResPopupView.setBackgroundDrawable(bg);
        onlineResPopupView.showAtLocation(getView(), Gravity.BOTTOM, 0, 0);
        bottomLayout.setVisibility(View.GONE);
    }

    private void initSoftKeyBoardListener() {
        if (bottomLayout != null) {
            KeyboardStatusDetector.getInstance()
                    .registerFragment(this)
                    .setmVisibilityListener(new KeyboardStatusDetector.KeyboardVisibilityListener() {
                        @Override
                        public void onVisibilityChanged(boolean keyboardVisible) {
                            if (onlineResPopupView != null && onlineResPopupView.isShowing()) {
                                if (keyboardVisible) {
                                    //软键盘可见
                                } else {
                                    //软键盘不可见
                                    onlineResPopupView.dismiss();
                                }
                            }
                        }
                    });
        }
    }

    /**
     * 选择相册中的图片作为材料
     */
    private void choosePictureToMateria() {
        Intent intent = new Intent(getActivity(), MyLocalPictureListActivity.class);
        intent.putExtra(CampusPatrolUtils.Constants.EXTRA_MEDIA_NAME, getString(R.string.start_image));
        intent.putExtra(CampusPatrolUtils.Constants.EXTRA_IS_PICK, true);
        intent.putExtra(MyLocalPictureListFragment.Contants.from_onine, true);
        if (onlineRes != null) {
            intent.putExtra(MyLocalPictureListFragment.Contants.online_id, onlineRes.getId());
        }
        startActivityForResult(intent, ActivityUtils.REQUEST_CODE_RETURN_REFRESH);
    }

    /**
     * 拍摄发送到空中课堂资料
     */
    private void takePhotoToMateria() {
        Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
        String dateFormat = "yyyyMMdd_HHmmss";
        String fileName = "IMG_" + DateUtils.format(System.currentTimeMillis(), dateFormat) + ".jpg";
        picFileName = getSaveImagePath(fileName);
        getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, FileProviderHelper.getUriForFile(getActivity(), new File(picFileName)));
        getImageByCamera.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(getImageByCamera, REQUEST_CODE_CAPTURE_CAMERA);
    }

    /**
     * 音频
     */
    private void chooseAudioToMateria() {
        File audioFile = new File(Utils.AUDIO_FOLDER);
        if (!audioFile.exists()) {
            audioFile.mkdirs();
        }
        pauseMedia();
        audioPopwindow = new AudioPopwindow(getActivity(), Utils.AUDIO_FOLDER, new
                AudioPopwindow.OnUploadListener() {
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
                            MediaInfo mediaInfo = mediaDTO.toMediaInfo();
                            List<MediaInfo> mediaInfos = new ArrayList<MediaInfo>();
                            if (mediaInfo != null) {
                                mediaInfos.add(mediaInfo);
                            }
                            uploadMedias(mediaInfos);
                        }
                    }

                }, new AudioPopwindow.OnDistroyListener() {
            @Override
            public void onDestroy() {
                continuePlayMedia();
            }
        });
        if (audioPopwindow != null) {
            audioPopwindow.setAnimationStyle(R.style.AnimBottom);
            audioPopwindow.showPopupMenu(getView());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        stopRecordingAudio();
    }

    private void stopRecordingAudio() {
        if (audioPopwindow != null && audioPopwindow.isShowing()) {
            audioPopwindow.stopRecordingAudio();
        }
    }

    /**
     * 视频
     */
    private void chooseVideoToMateria() {
        String dateFormat = "yyyyMMdd_HHmmss";
        String fileName = "VID_" + DateUtils.format(System.currentTimeMillis(), dateFormat) + ".mp4";

        Bundle args = new Bundle();
        args.putInt(SimpleVideoRecorder.EXTRA_VIDEO_DURATION, 30);
        args.putString(SimpleVideoRecorder.EXTRA_VIDEO_PATH,
                new File(Utils.VIDEO_FOLDER, fileName).getAbsolutePath());
        Intent intent = new Intent(getActivity(), SimpleVideoRecorder.class);
        intent.putExtras(args);
        try {
            startActivityForResult(intent, SimpleVideoRecorder
                    .REQUEST_CODE_CAPTURE_VIDEO);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 个人资源库
     */
    private void personalLibraryToMateria() {
        Intent intent = new Intent();
        intent.setClass(getActivity(), ShellActivity.class);
        intent.putExtra("Window", "media_type_list");
        intent.putExtra(MediaTypeListFragment.EXTRA_IS_REMOTE, true);
        intent.putExtra(MediaListFragment.EXTRA_IS_PICK, true);
        intent.putExtra(MediaListFragment.EXTRA_IS_FORM_ONLINE, true);
        startActivityForResult(intent, REQUEST_PERSONAL_CLOUD_RESOURCE);
    }

    /**
     * 校本资源库
     */
    private void publicLibraryToMateria() {
        Intent intent = new Intent(getActivity(), BookStoreListActivity.class);
        Bundle args = new Bundle();
        args.putSerializable(AirClassroomActivity.EXTRA_IS_SCHOOLINFO, schoolInfo);
//        args.putBoolean(ActivityUtils.EXTRA_TEMP_DATA, true);
        args.putBoolean(SchoolResourceContainerFragment.Constants.FROM_AIRCLASS_ONLINE, true);
        intent.putExtra(ActivityUtils.EXTRA_IS_PICK, true);
        intent.putExtra(ActivityUtils.EXTRA_IS_PICK_SCHOOL_RESOURCE, true);
        ArrayList<Integer> mediaTypes = new ArrayList<Integer>();
        mediaTypes.add(MediaType.SCHOOL_PICTURE);
        mediaTypes.add(MediaType.SCHOOL_AUDIO);
        mediaTypes.add(MediaType.SCHOOL_VIDEO);
        mediaTypes.add(MediaType.SCHOOL_PPT);
        mediaTypes.add(MediaType.SCHOOL_PDF);
        mediaTypes.add(MediaType.SCHOOL_DOC);
        mediaTypes.add(MediaType.SCHOOL_COURSEWARE);
        mediaTypes.add(MediaType.SCHOOL_TASKORDER);
        intent.putExtras(args);
        startActivityForResult(intent, REQUEST_PERSONAL_CLOUD_RESOURCE);
    }

    private void uploadMedias(List<MediaInfo> mediaInfos) {
        List<String> titles = new ArrayList<String>();
        for (MediaInfo mediaInfo : mediaInfos) {
            if (mediaInfo != null) {
                titles.add(Utils.removeFileNameSuffix(mediaInfo.getTitle()));
            }
        }
        uploadMediasToServer(mediaInfos);
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
                android.os.Message message = mhandler.obtainMessage();
                message.what = MSG_UPLOAD_MEDIA_FINISH;
                message.obj = result;
                mhandler.sendMessage(message);
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
                        TipMsgHelper.ShowLMsg(activity, R.string.upload_file_sucess);
                        //音频上传成后，删除本地音频文件。
                        if (uploadRecordFilePath != null) {
                            File file = new File(uploadRecordFilePath);
                            if (file != null && file.exists()) {
                                file.delete();
                            }
                        }
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

    /**
     * 弹出收藏的dialog
     */
    private void showPopForCollect(final AirClassroomMateria data) {
        int type = getMateriaType(data.getResId());
        if (type == -1) {
            return;
        }
        String tag = "";
        switch (type) {
            case ResType.RES_TYPE_IMG:
                tag = getString(R.string.pictures);
                break;
            case ResType.RES_TYPE_VOICE:
                tag = getString(R.string.audios);
                break;
            case ResType.RES_TYPE_VIDEO:
                tag = getString(R.string.videos);
                break;
            case ResType.RES_TYPE_PDF:
                tag = getString(R.string.txt_pdf);
                break;
            case ResType.RES_TYPE_PPT:
                tag = getString(R.string.txt_ppt);
                break;
            case ResType.RES_TYPE_DOC:
                tag = getString(R.string.DOC);
                break;
            case ResType.RES_TYPE_OLD_COURSE:
            case ResType.RES_TYPE_COURSE:
            case ResType.RES_TYPE_NOTE:
            case ResType.RES_TYPE_ONEPAGE:
            case ResType.RES_TYPE_COURSE_SPEAKER:
                tag = getString(R.string.microcourse);
                break;
            case ResType.RES_TYPE_RESOURCE:
                break;
            case ResType.RES_TYPE_STUDY_CARD:
                tag = getString(R.string.make_task);
                break;
        }
        final String resId = data.getResId();
        if (TextUtils.isEmpty(resId)) {
            return;
        }
        final String tagInfo = tag;
        final DialogHelper.CollectDialog collectDialog = DialogHelper.getIt(getActivity())
                .getCollectDialog();
        collectDialog.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CollectionHelper collectionHelper = new CollectionHelper(getActivity());
                        collectionHelper.collectDifferentResource(resId, data.getResTitle(), data.getCreateId(), tagInfo);
                        collectDialog.dismiss();
                    }
                });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == ActivityUtils.REQUEST_CODE_RETURN_REFRESH) {
                Bundle bundle = data.getExtras();
                if (bundle.getBoolean(ActivityUtils.REQUEST_CODE_NEED_TO_REFRESH)) {
                    pageHelper.setFetchingPageIndex(0);
                    isNeedRefresh = true;
                }
            } else if (requestCode == SimpleVideoRecorder.REQUEST_CODE_CAPTURE_VIDEO) {
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
                    MediaInfo mediaInfo = mediaDTO.toMediaInfo();
                    List<MediaInfo> mediaInfos = new ArrayList<MediaInfo>();
                    if (mediaInfo != null) {
                        mediaInfos.add(mediaInfo);
                    }
                    uploadMedias(mediaInfos);
                }
            } else if (requestCode == REQUEST_PERSONAL_CLOUD_RESOURCE) {
                List<ResourceInfo> resourceInfos = data.getParcelableArrayListExtra("resourseInfoList");
                if (resourceInfos != null && resourceInfos.size() > 0) {
                    if (onlineRes != null) {
                        int activityId = onlineRes.getId();
                        String resId = null;
                        String resourceUrl = null;
                        String originName = null;
                        for (int i = 0; i < resourceInfos.size(); i++) {
                            ResourceInfo resourceInfo = resourceInfos.get(i);
                            if (i == 0) {
                                resId = resourceInfo.getResId();
                                resourceUrl = resourceInfo.getResourcePath();
                                originName = resourceInfo.getTitle();
                            } else {
                                resId = resId + "," + resourceInfo.getResId();
                                resourceUrl = resourceUrl + "," + resourceInfo.getResourcePath();
                                originName = originName + "," + resourceInfo.getTitle();
                            }
                        }
                        UploadUtils.addAirClassroomMateria(getActivity(), activityId, getMemeberId
                                (), null, resId, resourceUrl, originName, mhandler);
                    }

                }
            }
        }
        //将data为空与不为空作区分
        if (requestCode == REQUEST_CODE_CAPTURE_CAMERA) {
            if (!TextUtils.isEmpty(picFileName) && new File(picFileName).exists()) {
                MediaInfo mediaInfo = new MediaInfo();
                mediaInfo.setTitle(Utils.getFileNameFromPath(picFileName));
                mediaInfo.setPath(picFileName);
                mediaInfo.setMediaType(MediaType.PICTURE);
                List<MediaInfo> mediaInfos = new ArrayList<MediaInfo>();
                if (mediaInfo != null) {
                    mediaInfos.add(mediaInfo);
                }
                uploadMedias(mediaInfos);
            }
        }
    }

    @Override
    public boolean onBackPressed() {
        if (onlineResPopupView != null) {
            onlineResPopupView.dismiss();
        }
        return super.onBackPressed();
    }

    public void setSchoolInfo(SchoolInfo schoolInfo) {
        this.schoolInfo = schoolInfo;
    }

    public void pauseMedia() {
        if (videoView != null) {
            videoView.onPause();
        }
    }

    public void continuePlayMedia() {
        if (videoView != null) {
            if (!videoView.isPlaying()) {
                videoView.onResume();
            }
        }
    }

    public void setVideoView(IMediaDataVideoView videoView) {
        this.videoView = videoView;
    }

    private boolean isReporter() {
        Fragment fragment = getParentFragment();
        if (fragment != null && fragment instanceof AirClassroomDetailFragment) {
            AirClassroomDetailFragment detailFragment = (AirClassroomDetailFragment) fragment;
            return detailFragment.isReporterInAllClass;
        }
        return false;
    }

    public void setCurrentPageIndex() {
        if (pageHelper != null) {
            pageHelper.setFetchingPageIndex(0);
        }
    }


}
