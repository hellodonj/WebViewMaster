package com.galaxyschool.app.wawaschool.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.pojo.ResourceNum;
import com.galaxyschool.app.wawaschool.pojo.ResourceNumListResult;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.views.ToolbarTopView;
import com.libs.yilib.pickimages.ScanLocalMediaController;
import com.lqwawa.client.pojo.MediaType;
import com.lqwawa.client.pojo.SourceFromType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangchao on 12/31/15.
 */
public class MediaTypeListFragment extends ContactsListFragment
        implements ScanLocalMediaController.ScanLocalMediaListener {

    public static final String EXTRA_IS_REMOTE = "is_remote";

    private boolean isRemote = false;
    private boolean isPick = false;
    private boolean isFromOnline=false;
    protected boolean isFromChoiceLib = false;

    private HashMap<Integer, Integer> numHashMap = new HashMap<>();

//    private ScanLocalMediaController scanLocalMediaController;
//
//    private int picNum = 0;
    private List<Integer> mShowMediaTypes;
    private List<SchoolInfo> schoolInfoList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_media_type_list, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadViews();
    }

    private void loadViews() {
        if (getCurrAdapterViewHelper().hasData()) {
            getCurrAdapterViewHelper().update();
        } else {
            if (!isRemote) {
                loadTypeList();
            } else {
                loadReourcesNum();
            }
        }
    }

    private void initViews() {
        if (getArguments() != null) {
            mShowMediaTypes = (List<Integer>) getArguments().getIntegerArrayList(
                    MediaListFragment.EXTRA_SHOW_MEDIA_TYPES);
            isRemote = getArguments().getBoolean(EXTRA_IS_REMOTE);
            isPick = getArguments().getBoolean(MediaListFragment.EXTRA_IS_PICK);
            isFromOnline=getArguments().getBoolean(MediaListFragment.EXTRA_IS_FORM_ONLINE,false);
            isFromChoiceLib = getArguments().getBoolean(ActivityUtils.IS_FROM_CHOICE_LIB);
            schoolInfoList = (List<SchoolInfo>) getArguments().getSerializable(ActivityUtils.EXTRA_SCHOOL_INFO_LIST);
        }
        ToolbarTopView toolbarTopView = (ToolbarTopView) findViewById(R.id.toolbar_top_view);
        if (toolbarTopView != null) {
            toolbarTopView.getBackView().setVisibility(View.VISIBLE);
//            if (!isRemote) {
//                toolbarTopView.getTitleView().setText(R.string.local_resources);
//            } else {
            toolbarTopView.getTitleView().setText(R.string.my_book_shelf);
//            }
            toolbarTopView.getBackView().setOnClickListener(this);
        }

        ListView listView = (ListView) findViewById(R.id.listview);
        if (listView != null) {
            AdapterViewHelper helper = new AdapterViewHelper(getActivity(), listView, R.layout
                    .media_type_list_item) {
                @Override
                public void loadData() {
                    loadTypeList();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);

                    MediaItem data = (MediaItem) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;

                    ImageView thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
                    TextView name = (TextView) view.findViewById(R.id.name);
                    TextView num = (TextView) view.findViewById(R.id.num);

                    thumbnail.setImageResource(data.icon);
                    name.setText(data.title);
                    if (data.num >= 0) {
                        num.setText(getString(R.string.media_num, data.num));
                    }
                    num.setVisibility(isRemote ? View.GONE : View.GONE);

                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null || holder.data == null) {
                        return;
                    }
                    MediaItem data = (MediaItem) holder.data;
                    if (data != null) {
//                        FragmentTransaction ft = getFragmentManager().beginTransaction();
//                        Fragment fragment = new MediaListFragment();
//                        Bundle args = getArguments();
//                        args.putInt(MediaListFragment.EXTRA_MEDIA_TYPE, data.type);
//                        args.putString(MediaListFragment.EXTRA_MEDIA_NAME, getString(data.title));
//                        fragment.setArguments(args);
//                        ft.add(R.id.container, fragment, MediaListFragment.TAG);
//                        ft.addToBackStack(null);
//                        ft.commit();
//                        FragmentTransaction ft = getFragmentManager().beginTransaction();
//                        Fragment fragment = new MediaMainFragment();
//                        Bundle args = getArguments();
//                        args.putInt(MediaListFragment.EXTRA_MEDIA_TYPE, data.type);
//                        args.putString(MediaListFragment.EXTRA_MEDIA_NAME, getString(data.title));
//                        fragment.setArguments(args);
//                        ft.replace(R.id.container, fragment, MediaMainFragment.TAG);
//                        ft.addToBackStack(null);
//                        ft.commit();
                        MediaTypeListFragment.this.onItemClick(data);
                    }
                }
            };
            setCurrAdapterViewHelper(listView, helper);
        }
    }

    protected void onItemClick(MediaItem data) {
        UIUtils.currentSourceFromType = SourceFromType.OTHER;
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment fragment;
        Bundle args = getArguments();
        args.putInt(MediaListFragment.EXTRA_MEDIA_TYPE, data.type);
        args.putString(MediaListFragment.EXTRA_MEDIA_NAME, getString(data.title));
//                        args.putBoolean(MediaListFragment.EXTRA_IS_SPLIT, false);
        /*if (data.type == MediaType.TASK_ORDER) {
            fragment = new TaskOrderFragment();
            fragment.setArguments(args);
            ft.replace(R.id.container, fragment, TaskOrderFragment.TAG);
        } else*/ if (data.type == MediaType.MICROCOURSE) {
            fragment = new CourseMainFragment();
            fragment.setArguments(args);
            ft.replace(R.id.container, fragment, CourseMainFragment.TAG);
        } else if (data.type == -1){
            //我的课程
            fragment = new MyCollectionBookListFragment();
            fragment.setArguments(args);
            ft.replace(R.id.container, fragment, MyCollectionBookListFragment.TAG);
        }else {
            fragment = new MediaMainFragment();
            fragment.setArguments(args);
            ft.replace(R.id.container, fragment, MediaMainFragment.TAG);
        }
        ft.addToBackStack(null);
        ft.commit();
    }

//    private void scanImages() {
//        scanLocalMediaController = new ScanLocalMediaController(
//            com.libs.yilib.pickimages.MediaType.MEDIA_TYPE_PHOTO, PickMediasFragment.getImageFormatList(), this);
//        scanLocalMediaController.start("/mnt");
//    }
//
//    private void updateView() {
//        if(getCurrAdapterViewHelper().hasData()) {
//            MediaItem mediaItem = (MediaItem)getCurrAdapterViewHelper().getData().get(1); // update pic num
//            if(mediaItem != null) {
//                mediaItem.num = picNum;
//            }
//            getCurrAdapterViewHelper().update();
//        }
//    }

    private void loadReourcesNum() {
        UserInfo userInfo = getUserInfo();
        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
            return;
        }
        Map<String, Object> mParams = new HashMap<String, Object>();
        mParams.put("MemberId", userInfo.getMemberId());
        postRequest(ServerUrl.PR_LOAD_MEDIA_NUM_URL, mParams, new DefaultDataListener
                <ResourceNumListResult>(ResourceNumListResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                ResourceNumListResult result = getResult();
                if (result == null || result.getModel() == null || !result.isSuccess()) {
                    return;
                }
                List<ResourceNum> list = result.getModel().getData();
                if (list != null && list.size() > 0) {
                    for (int i = 0, size = list.size(); i < size; i++) {
                        ResourceNum resourceNum = list.get(i);
                        if (resourceNum != null) {
                            String type=resourceNum.getMType();
                            String number=resourceNum.getMNumber();
                            if (!TextUtils.isEmpty(type)&&!TextUtils.isEmpty(number)){
                                numHashMap.put(Integer.parseInt(type), Integer.parseInt(number));
                            }
                        }
                    }
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                loadTypeList();
            }
        });
    }

    protected boolean isShowMediaType(int type) {
        return mShowMediaTypes == null ? true : mShowMediaTypes.contains(type);
    }

    protected void loadTypeList() {
        int courseNum = 0;
        int picNum = 0;
        int audioNum = 0;
        int videoNum = 0;
        int onePageNum = 0;
        int taskOrderNum = 0;
        int pptNum = 0;
        int pdfNum = 0;
        int docNum = 0;
        if (isRemote) {
            if (numHashMap != null && numHashMap.size() > 0) {
                courseNum = numHashMap.get(MediaType.MICROCOURSE);
                picNum = numHashMap.get(MediaType.PICTURE);
                audioNum = numHashMap.get(MediaType.AUDIO);
                videoNum = numHashMap.get(MediaType.VIDEO);
                onePageNum = numHashMap.get(MediaType.ONE_PAGE);
                pptNum = numHashMap.get(MediaType.PPT);
                pdfNum = numHashMap.get(MediaType.PDF);
                docNum = numHashMap.get(MediaType.DOC);
                if (numHashMap.containsKey(MediaType.TASK_ORDER)){
                    taskOrderNum = numHashMap.get(MediaType.TASK_ORDER);
                }
            }
        }

//        if (!isRemote) {
//            courseNum = getLocalCourseCount();
//            picNum = 0;
//            audioNum = getLocalMediaCount(MediaType.AUDIO);
//            videoNum = getLocalMediaCount(MediaType.VIDEO);
//            scanImages();
//        } else {
//            if (numHashMap != null && numHashMap.size() > 0) {
//                courseNum = numHashMap.get(1);
//                picNum = numHashMap.get(2);
//                audioNum = numHashMap.get(3);
//                videoNum = numHashMap.get(4);
//            }
//        }

        List<MediaItem> mediaItems = new ArrayList<MediaItem>();
        MediaItem item;
        UserInfo userInfo = getUserInfo();

        //我的课程
        if (!isPick) {
            if (isShowMediaType(MediaType.BOOK_CLASS) && isRealTeacher()) {
                item = new MediaItem();
                item.icon = R.drawable.icon_my_course;
                item.type = -1;
                item.title = R.string.my_bookshelf_text; //更新为我的书架
                item.num = 0;
                //如果是从个人素材过来的，不添加此项。
                mediaItems.add(item);
            }
        }

        //LQ课件(原来的有声相册),contains OnePage and MicroCourse
        if (isShowMediaType(MediaType.lQ_CLASS)) {
            item = new MediaItem();
            item.icon = R.drawable.icon_lq_course;
            item.type = MediaType.ONE_PAGE;
            item.title = R.string.microcourse;
            item.num = onePageNum;
            mediaItems.add(item);
        }

        //放开老师、学生、家长、游客
        if (userInfo != null ) {
            //任务单
            if (!isPick&&isShowMediaType(MediaType.TASK_ORDER)) {
                item = new MediaItem();
                item.icon = R.drawable.task_order_icon;
                item.type = MediaType.TASK_ORDER;
                item.title = R.string.task_order;
                item.num = taskOrderNum;
                mediaItems.add(item);
            }
            //根据条件判断是否来自于添加空中课堂的资料
            if (isPick&&isFromOnline&&isShowMediaType(MediaType.TASK_ORDER)){
                item = new MediaItem();
                item.icon = R.drawable.task_order_icon;
                item.type = MediaType.TASK_ORDER;
                item.title = R.string.task_order;
                item.num = taskOrderNum;
                mediaItems.add(item);
            }
        }
//        item = new MediaItem();
//        item.icon = R.drawable.resource_weike_ico;
//        item.type = MediaType.MICROCOURSE;
//        item.title = R.string.microcourse;
//        item.num = courseNum;
//        mediaItems.add(item);

        if (isShowMediaType(MediaType.VIDEO)) {
            item = new MediaItem();
            item.icon = R.drawable.resource_video_ico;
            item.type = MediaType.VIDEO;
            item.title = R.string.videos;
            item.num = videoNum;
            mediaItems.add(item);
        }

        //PDF
        if (isShowMediaType(MediaType.PDF)) {
            item = new MediaItem();
            item.icon = R.drawable.icon_personal_resource_pdf;
            item.type = MediaType.PDF;
            item.title = R.string.pdf_file;
            item.num = pdfNum;
            mediaItems.add(item);
        }

        //PPT
        if (isShowMediaType(MediaType.PPT)) {
            item = new MediaItem();
            item.icon = R.drawable.icon_ppt;
            item.type = MediaType.PPT;
            item.title = R.string.ppt_file;
            item.num = pptNum;
            mediaItems.add(item);
        }

        //图片
        if (isShowMediaType(MediaType.PICTURE)) {
            item = new MediaItem();
            item.icon = R.drawable.resource_pic_ico;
            item.type = MediaType.PICTURE;
            item.title = R.string.pictures;
            item.num = picNum;
            mediaItems.add(item);
        }

        //音频
        if (isShowMediaType(MediaType.AUDIO)) {
            item = new MediaItem();
            item.icon = R.drawable.resource_audio_ico;
            item.type = MediaType.AUDIO;
            item.title = R.string.audios;
            item.num = audioNum;
            mediaItems.add(item);
        }

        //DOC
        if (isShowMediaType(MediaType.DOC)) {
            item = new MediaItem();
            item.icon = R.drawable.icon_doc;
            item.type = MediaType.DOC;
            item.title = R.string.DOC;
            item.num = docNum;
            mediaItems.add(item);
        }

        getCurrAdapterViewHelper().setData(mediaItems);
    }

//    private int getLocalMediaCount(int mediaType) {
//        MediaDao mediaDao = new MediaDao(getActivity());
//        try {
//            List<MediaDTO> mediaDTOs = mediaDao.getMediaDTOs(mediaType);
//            if (mediaDTOs != null) {
//                return mediaDTOs.size();
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return 0;
//    }
//
//    private int getLocalCourseCount() {
//        ArrayList<DraftData> datas = DraftData.getAllDraftsInType(getActivity(), DraftData.TYPE_NOTE);
//        if (datas != null) {
//            return datas.size();
//        }
//        return 0;
//    }

    /**
     * 是否是实体机构的老师
     *
     * @param
     */
    private boolean isRealTeacher() {
        if (schoolInfoList != null && schoolInfoList.size() > 0) {
            for (SchoolInfo info : schoolInfoList){
                if (info.isOnlineSchool()) continue;
                if (info.isTeacher()) return true;
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.toolbar_top_back_btn) {
            if (getActivity() != null) {
                getActivity().finish();
            }
        }
    }

    @Override
    public void onSearched(String file, int mediaType) {
//        if (mediaType == com.libs.yilib.pickimages.MediaType.MEDIA_TYPE_PHOTO) {
//            if (!TextUtils.isEmpty(file)) {
//                if (!file.startsWith(Utils.DATA_FOLDER)) {
//                    picNum = picNum + 1;
//                }
//            } else {
//                final Handler h = new Handler(Looper.getMainLooper());
//                h.post(new Runnable() {
//                    public void run() {
//                        updateView();
//                    }
//                });
//            }
//        }
    }

    protected class MediaItem {
        int type;
        int icon;
        int title;
        int num;
    }
}
