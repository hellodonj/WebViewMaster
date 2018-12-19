package com.galaxyschool.app.wawaschool.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.DateUtils;
import com.galaxyschool.app.wawaschool.common.ScreenUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.course.SlideActivityNew;
import com.galaxyschool.app.wawaschool.db.LocalCourseDao;
import com.galaxyschool.app.wawaschool.db.dto.LocalCourseDTO;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseType;
import com.galaxyschool.app.wawaschool.pojo.weike.LocalCourseInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.MaterialType;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.oosic.apps.iemaker.base.BaseSlideManager;
import com.oosic.apps.iemaker.base.BaseUtils;
import com.oosic.apps.iemaker.base.interactionlayer.data.SlideInputParam;
import com.oosic.apps.iemaker.base.interactionlayer.data.User;
import com.osastudio.common.utils.LQImageLoader;

import java.io.File;
import java.io.FileFilter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by wangchao on 3/24/16.
 */
public class LocalCourseFragment extends ContactsListFragment /*implements CommitHelper
        .NoteCommitListener*/ {

    public static final String EXTRA_COURSE_TYPE = "course_type";
    public static final String IS_FROM_NOC_SELECT_COURSE = "is_from_noc_select_course";
    public static final int MAX_COLUMN = 2;

    private static final int REQUEST_SLIDE = 1;
    private static final int REQUEST_LOCAL_COURSE = 2;
    public interface ViewMode {
        int NORMAL = 0;
        int EDIT = 1;
    }

    private int courseType;
    private int shareType;
    private int viewMode = ViewMode.NORMAL;
    private boolean isSelectAll = false;
    private LocalCourseInfo localCourseInfo;
//    protected CommitCourseHelper commitCourseHelper;
    private LinearLayout topLayout;
    private LinearLayout bottomSubLayout0, bottomSubLayout1;
    private ImageView checkBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_local_course, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
//        commitCourseHelper = new CommitCourseHelper(getActivity());
//        commitCourseHelper.setNoteCommitListener(LocalCourseFragment.this);
    }

    @Override
    public void onResume() {
        super.onResume();
        loaViews();
    }

    private void initViews() {
        if (getArguments() != null) {
            courseType = getArguments().getInt(EXTRA_COURSE_TYPE);
        }

        final PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        if (pullToRefreshView != null) {
            setStopPullUpState(true);
            setStopPullDownState(true);
            setPullToRefreshView(pullToRefreshView);
        }

        GridView gridView = (GridView) findViewById(R.id.gridview);
        if (gridView != null) {
            gridView.setNumColumns(2);
            gridView.setPadding(10, 10, 10, 10);
            AdapterViewHelper helper = new AdapterViewHelper(
                getActivity(), gridView, R.layout.local_media_video_grid_item) {
                @Override
                public void loadData() {
                    loadLocalCourses(courseType);
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = convertView;
                    int min_padding = getResources().getDimensionPixelSize(R.dimen.min_padding);
                    int itemSize = (ScreenUtils.getScreenWidth(getActivity()) - min_padding * (MAX_COLUMN + 1)) / MAX_COLUMN;
                    if (view == null) {
                        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        view = inflater.inflate(R.layout.local_media_video_grid_item, parent, false);
                        view.setLayoutParams(new GridView.LayoutParams(itemSize, itemSize));
                    }
                    ImageView thumbnail = (ImageView) view.findViewById(R.id.media_thumbnail);
                    TextView name = (TextView) view.findViewById(R.id.media_name);
                    TextView time = (TextView) view.findViewById(R.id.media_time);
                    ImageView flag = (ImageView) view.findViewById(R.id.media_flag);
                    TextView splitBtn = (TextView) view.findViewById(R.id.media_split_btn);

                    LocalCourseInfo data = (LocalCourseInfo) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;

                    String thumbPath = ThumbnailsHelper.getInstance().getThumbnailPath(data.mPath);
                    //import resource
                    // thumb path
                    if (courseType == CourseType.COURSE_TYPE_LOCAL) {
                        thumbPath = data.mPath + File.separator
                            + Utils.RECORD_HEAD_IMAGE_NAME;
                    }


                    //如果缩略图为白版显示为空时 显示蛙蛙头
                    if (TextUtils.isEmpty(thumbPath)){
                        thumbnail.setImageResource(R.drawable.default_cover);
                    }else {
                        File thumbFile = new File(thumbPath);
                        if (thumbFile.exists()) {
                            LQImageLoader.DIOptBuiderParam param = new LQImageLoader.DIOptBuiderParam();
                            param.mIsCacheInMemory = true;
                            param.mOutWidth = LQImageLoader.OUT_WIDTH;
                            param.mOutHeight = LQImageLoader.OUT_HEIGHT;
                            LQImageLoader.displayImage(thumbPath, thumbnail, param);
                        } else {
                            thumbnail.setImageResource(R.drawable.default_cover);
                        }
                    }


                    name.setText(data.mTitle);
                    name.setSingleLine(false);
                    name.setPadding(5,5,5,5);
                    name.setTextSize(12);
                    name.setLines(2);

                    flag.setVisibility(viewMode == ViewMode.NORMAL ? View.GONE : View.VISIBLE);
                    flag.setImageResource(data.isSelect ? R.drawable.select : R.drawable.unselect);
                    time.setVisibility(View.GONE);
                    splitBtn.setVisibility(View.GONE);
                    long createTime = new File(data.mPath).lastModified();
                    Date date = new Date();
                    date.setTime(createTime);
                    String dateStr = DateUtils.getDateStr(date);
                    time.setText(dateStr);

                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null || holder.data == null) {
                        return;
                    }
                    LocalCourseInfo data = (LocalCourseInfo) holder.data;
                    if (data != null) {
                        if(viewMode == ViewMode.NORMAL) {
                            if (courseType == CourseType.COURSE_TYPE_IMPORT) {
                                enterSlideNew(data, MaterialType.RECORD_BOOK);
                            } else {
                                playbackCourse(data);
                            }
                        } else {
                            data.isSelect = !data.isSelect;
                            getCurrAdapterViewHelper().update();

                            isSelectAll = isSelectAll();
                            checkState(isSelectAll);
                        }
                    }
                }
            };
            setCurrAdapterViewHelper(gridView, helper);
        }

        topLayout = (LinearLayout)findViewById(R.id.top_layout);
        bottomSubLayout0 = (LinearLayout)findViewById(R.id.bottom_sub_layout_0);
        bottomSubLayout1 = (LinearLayout)findViewById(R.id.bottom_sub_layout_1);
        checkBtn = (ImageView) findViewById(R.id.btn_check);
        topLayout.setOnClickListener(this);
        topLayout.setVisibility(View.GONE);
        TextView textView = (TextView) findViewById(R.id.btn_bottom_delete);
        if(textView != null) {
            textView.setOnClickListener(this);
        }

        textView = (TextView) findViewById(R.id.btn_bottom_ok);
        if(textView != null) {
            textView.setOnClickListener(this);
        }

        textView = (TextView) findViewById(R.id.btn_bottom_cancel);
        if(textView != null) {
            textView.setOnClickListener(this);
        }
        initBottomLayout(viewMode);
    }

    private void loaViews() {
        loadLocalCourses(courseType);
        ImageLoader.getInstance().clearMemoryCache();
    }


    private void loadLocalCourses(int courseType) {
        String rootPath = getRootFolder(courseType);
        if (!TextUtils.isEmpty(rootPath) && rootPath.endsWith(File.separator)) {
            rootPath = rootPath.substring(0, rootPath.length() - 1);
        }
        List<LocalCourseInfo> localCourseInfos = loadLocalCoursesByFolder(courseType, rootPath);
        if (localCourseInfos != null && localCourseInfos.size() > 0) {
            Collections.sort(localCourseInfos, modifiedTimeComparator);
        }
        getCurrAdapterViewHelper().setData(localCourseInfos);
    }

    private ArrayList<LocalCourseInfo> loadLocalCoursesByFolder(int courseType, String folder) {
        List<LocalCourseDTO> localCourseDTOs;
        ArrayList<LocalCourseInfo> localCourseList = null;

        LocalCourseDao localCourseDao = new LocalCourseDao(getActivity());
        try {
            localCourseDTOs = localCourseDao.getLocalCoursesByFolder(getMemeberId(), courseType,
                    folder);
            if (localCourseDTOs != null && localCourseDTOs.size() > 0) {
                if (localCourseList == null) {
                    localCourseList = new ArrayList<LocalCourseInfo>();
                }
                for (LocalCourseDTO localCourseDTO : localCourseDTOs) {
                    if (localCourseDTO != null) {
                        String path = localCourseDTO.getmPath();
                        if (!TextUtils.isEmpty(path) && new File(path).exists()) {
                            LocalCourseInfo localCourseInfo  = localCourseDTO.toLocalCourseInfo();
                            localCourseList.add(localCourseInfo);
                        } else if (!TextUtils.isEmpty(path)) {
                            localCourseDao.deleteLocalCoursesByFolder(getMemeberId(), path);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return localCourseList;
    }

    public static class ThumbnailsHelper {
        private static ThumbnailsHelper mInstance;

        public static ThumbnailsHelper getInstance() {
            if (mInstance == null) {
                mInstance = new ThumbnailsHelper();
            }
            return mInstance;
        }

        public String getThumbnailPath(String folder) {
            String thumbPath = null;
            if (!TextUtils.isEmpty(folder)) {
                thumbPath = folder + File.separator
                        + Utils.RECORD_HEAD_IMAGE_NAME;
                if (new File(thumbPath).exists()) {
                    return thumbPath;
                }

                thumbPath = folder + File.separator
                        + Utils.PDF_THUMB_NAME;
                if (new File(thumbPath).exists()) {
                    return thumbPath;
                }

                thumbPath = folder + File.separator
                        + Utils.PDF_PAGE_NAME + "1.jpg";
                if (new File(thumbPath).exists()) {
                    return thumbPath;
                }

                thumbPath = null;
                String filePath = folder;
                if (!filePath.endsWith(File.separator)) {
                    filePath = filePath + File.separator;
                }
                File file = new File(filePath);
                File[] pages = null;
                ArrayList<String> pageNameArray = null;
                pages = file.listFiles(
                        new FileFilter() {
                            public boolean accept(File file) {
                                if ((file.getName().toLowerCase()
                                        .endsWith(".jpg") || file.getName()
                                        .toLowerCase().endsWith(".png"))
                                        && file.getName().toLowerCase()
                                        .contains(Utils.PDF_PAGE_NAME)) {
                                    return true;
                                }
                                return false;
                            }
                        });
                if (pages != null) {
                    for (int i = 0; i < pages.length; i++) {
                        if (pageNameArray == null) {
                            pageNameArray = new ArrayList<String>();
                        }
                        pageNameArray.add(pages[i].getName());
                    }
                    if (pageNameArray != null
                            && pageNameArray.size() > 0) {
                        Collections.sort(
                                pageNameArray, sTitleComparator);
                        thumbPath = filePath + pageNameArray.get(0);
                    }
                }
            }
            return thumbPath;
        }

        public void unifyThumbnail(String folder) {
            if (!TextUtils.isEmpty(folder)) {
                String unifyThumbPath = folder + File.separator + Utils.RECORD_HEAD_IMAGE_NAME;
                if (!new File(unifyThumbPath).exists()) {
                    String existThumbnailPath = getThumbnailPath(folder);
                    if (existThumbnailPath != null) {
                        File file = new File(existThumbnailPath);
                        if (file.exists()) {
                            file.renameTo(new File(unifyThumbPath));
                        }
                    }
                }
            }
        }
    }

    private String getThumbPath(LocalCourseInfo data) {
        String thumbPath = data.mPath + File.separator
            + Utils.PDF_THUMB_NAME;
        if (!new File(thumbPath).exists()) {
            thumbPath = data.mPath + File.separator
                + Utils.PDF_PAGE_NAME + "1.jpg";
            if (!new File(thumbPath).exists()) {
                thumbPath = null;
                String filePath = data.mPath;
                if (!filePath.endsWith(File.separator)) {
                    filePath = filePath + File.separator;
                }
                File file = new File(filePath);
                File[] pages = null;
                ArrayList<String> pageNameArray = null;
                pages = file.listFiles(
                    new FileFilter() {
                        public boolean accept(File file) {
                            if ((file.getName().toLowerCase()
                                .endsWith(".jpg") || file.getName()
                                .toLowerCase().endsWith(".png"))
                                && file.getName().toLowerCase()
                                .contains(Utils.PDF_PAGE_NAME)) {
                                return true;
                            }
                            return false;
                        }
                    });
                if (pages != null) {
                    for (int i = 0; i < pages.length; i++) {
                        if (pageNameArray == null) {
                            pageNameArray = new ArrayList<String>();
                        }
                        pageNameArray.add(pages[i].getName());
                    }

                    if (pageNameArray != null
                        && pageNameArray.size() > 0) {
                        Collections.sort(
                            pageNameArray,
                            sTitleComparator);
                        thumbPath = filePath + pageNameArray.get(0);
                    }
                }
            }
        }
        return thumbPath;
    }

    private Comparator<LocalCourseInfo> modifiedTimeComparator = new Comparator<LocalCourseInfo>() {
        @Override
        public int compare(LocalCourseInfo lhs, LocalCourseInfo rhs) {
            if(lhs != null && rhs != null) {
                if (lhs.mLastModifiedTime < rhs.mLastModifiedTime) {
                    return 1;
                } else if (lhs.mLastModifiedTime > rhs.mLastModifiedTime) {
                    return -1;
                }
            }
            return 0;
        }
    };

    private static Comparator<String> sTitleComparator = new Comparator<String>() {
        public int compare(String obj1, String obj2) {
            ArrayList<Long> obj1NumList = getNumList(getNumStr(obj1));
            ArrayList<Long> obj2NumList = getNumList(getNumStr(obj2));
            int rtn = 0;
            int size = Math.max(obj1NumList.size(), obj2NumList.size());
            for (int i = 0; i < size; i++) {
                long num1 = 0;
                long num2 = 0;

                if (i < obj1NumList.size()) {
                    num1 = obj1NumList.get(i);
                }
                if (i < obj2NumList.size()) {
                    num2 = obj2NumList.get(i);
                }

                if (num1 < num2) {
                    rtn = -1;
                    break;
                } else if (num1 > num2) {
                    rtn = 1;
                    break;
                } else {
                    rtn = 0;
                }
            }

            return rtn;// obj1.compareTo(obj2);
        }

        private String getNumStr(String srcPath) {
            int firstIndex = srcPath.lastIndexOf('/');
            int lastIndex = srcPath.lastIndexOf('.');
            if (lastIndex <= firstIndex)
                lastIndex = srcPath.length();
            String temp = null;
            try {
                temp = srcPath.subSequence(firstIndex + 1, lastIndex)
                    .toString();

                if (temp != null) {
                    firstIndex = temp.indexOf(Utils.PDF_PAGE_NAME)
                        + Utils.PDF_PAGE_NAME.length();
                    if (firstIndex < temp.length()) {
                        temp = temp.substring(firstIndex);
                    }
                }
            } catch (Exception e) {
                temp = null;
                e.printStackTrace();
            }
            return temp;
        }

        private ArrayList<Long> getNumList(String numStr) {
            ArrayList<Long> list = null;
            long num = -1;
            do {
                num = getNum(numStr);
                if (num >= 0) {
                    if (list == null) {
                        list = new ArrayList<Long>();
                    }
                    list.add(num);
                    int index = numStr.indexOf("_");
                    if (index > 0) {
                        numStr = numStr.substring(index);
                    } else {
                        break;
                    }
                }
            } while (num >= 0);

            return list;
        }

        private long getNum(String numStr) {
            while (numStr.startsWith("_")) {
                numStr = numStr.substring(1);
            }
            long num = -1;
            String temp = numStr;
            int index = numStr.indexOf('_');
            if (index > 0) {
                temp = numStr.substring(0, index);

            }
            if (temp != null) {
                try {
                    num = Long.valueOf(temp);
                } catch (NumberFormatException e) {
                    num = -1;
                }
            }
            return num;
        }
    };

    private void enterSlideNew(LocalCourseInfo info, int type) {
        Intent it = new Intent(getActivity(), SlideActivityNew.class);
        it.putExtra(SlideActivityNew.LOAD_FILE_PATH, info.mPath);
        it.putExtra(SlideActivityNew.LOAD_FILE_TITLE, info.mTitle);
        it.putExtra(SlideActivityNew.LOAD_FILE_PAGES, info.mPageCount);
        it.putExtra(SlideActivityNew.COUTSE_TYPE, type);
        it.putExtra(SlideActivityNew.ORIENTATION, info.mOrientation);
        SlideInputParam slideInputParam = getSlideInputParam();
        //support A4 paper ratio for course maker
        if(info.mOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            slideInputParam.mRatioScreenWToH = 297.0f / 210.0f;
        } else {
            slideInputParam.mRatioScreenWToH = 210.0f / 297.0f;
        }
        it.putExtra(SlideInputParam.class.getSimpleName(), getSlideInputParam());
        LocalCourseDao localCourseDao = new LocalCourseDao(getActivity());
        if (localCourseDao != null) {
            try {
                List<LocalCourseDTO> dtos = localCourseDao.getLocalCourseByPath(getMemeberId(),
                        info.mPath);
                if (dtos != null && dtos.size() > 0) {
                    it.putExtra(SlideActivityNew.ORIENTATION, dtos.get(0).getmOrientation());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        startActivityForResult(it, REQUEST_SLIDE);
    }

    private SlideInputParam getSlideInputParam() {
        SlideInputParam param = new SlideInputParam();
        param.mCurUser = new User();
        UserInfo userInfo = getUserInfo();
        if (userInfo != null) {
            param.mCurUser.mId = userInfo.getMemberId();
            if (TextUtils.isEmpty(userInfo.getRealName())) {
                param.mCurUser.mName = userInfo.getRealName();
            } else {
                param.mCurUser.mName = userInfo.getNickName();
            }
        }
        param.mNotShowShareBoxBtn = true;
        int[] rayMenuV = {
                BaseSlideManager.MENU_ID_CAMERA,
                BaseSlideManager.MENU_ID_IMAGE,
                BaseSlideManager.MENU_ID_WHITEBOARD,
                BaseSlideManager.MENU_ID_AUDIO,
                BaseSlideManager.MENU_ID_PERSONAL_MATERIAL
        };
        MyApplication application = (MyApplication) getActivity().getApplicationContext();
        if (application != null){
            UserInfo info = application.getUserInfo();
            if (info != null && info.isTeacher()){
                rayMenuV= Arrays.copyOf(rayMenuV, rayMenuV.length + 1);
                rayMenuV[rayMenuV.length - 1] =  BaseSlideManager.MENU_ID_SCHOOL_MATERIAL;
            }
        }
        param.mRayMenusV = rayMenuV;
        int[] rayMenuH = {
                BaseSlideManager.MENU_ID_CURVE,
                BaseSlideManager.MENU_ID_LASER,
                BaseSlideManager.MENU_ID_ERASER
        };
        param.mRayMenusH = rayMenuH;
        return param;
    }

    private void delete(List<LocalCourseInfo> infos) {
        if (infos != null && infos.size() > 0) {
            for(LocalCourseInfo info : infos) {
                showLoadingDialog();
                if(info != null) {
                    File file = new File(info.mPath);
                    if (file.exists()) {
                        Utils.safeDeleteDirectory(info.mPath);
                    }
                    LocalCourseDao localCourseDao = new LocalCourseDao(getActivity());
                    try {
                        localCourseDao.deleteLocalCoursesByFolder(getMemeberId(), info.mPath);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    getCurrAdapterViewHelper().getData().remove(info);
                }
                getCurrAdapterViewHelper().update();
                dismissLoadingDialog();
            }
        }
    }

    private void playbackCourse(LocalCourseInfo info) {
        String path = info.mPath;
        int resType = BaseUtils.getCoursetType(info.mPath);
        Intent intent = ActivityUtils.getIntentForPlayLocalCourse(
            getActivity(), path, info.mTitle, info.mDescription, info.mOrientation, resType,
                true, false);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private String getRootFolder(int courseType) {
        String rootFolder = Utils.getUserCourseRootPath(getMemeberId(), courseType, false);
        if (rootFolder.endsWith(File.separator)) {
            rootFolder = rootFolder.substring(0, rootFolder.length() - 1);
        }
        return rootFolder;
    }

    private void initBottomLayout(int viewMode) {
        bottomSubLayout0.setVisibility(viewMode == ViewMode.NORMAL ? View.VISIBLE : View.INVISIBLE);
        bottomSubLayout1.setVisibility(viewMode != ViewMode.NORMAL ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.top_layout) {
            isSelectAll = !isSelectAll;
            checkState(isSelectAll);
            checkData(isSelectAll);
        } else if(v.getId() == R.id.btn_bottom_delete){
            enterEditMode();
        } else if(v.getId() == R.id.btn_bottom_ok) {
            delete();
        } else if(v.getId() == R.id.btn_bottom_cancel){
            exitEditMode();
        } else {
            super.onClick(v);
        }
    }

    private void delete() {
        List<LocalCourseInfo> localCourseInfos = getSelectedData();
        if (localCourseInfos == null || localCourseInfos.size() == 0) {
            TipMsgHelper.ShowLMsg(getActivity(), R.string.pls_select_files);
            return;
        }
        exitEditMode();
        delete(localCourseInfos);
    }

    private void enterEditMode() {
        viewMode = ViewMode.EDIT;
        initBottomLayout(viewMode);
        topLayout.setVisibility(viewMode != ViewMode.EDIT ? View.GONE : View.VISIBLE);
        getCurrAdapterViewHelper().update();
    }

    private void exitEditMode() {
        topLayout.setVisibility(View.GONE);
        viewMode = ViewMode.NORMAL;
        initBottomLayout(viewMode);
        isSelectAll = false;
        checkData(isSelectAll);
        checkState(isSelectAll);

    }

    private boolean isSelectAll() {
        List<LocalCourseInfo> localCourseInfos = new ArrayList<LocalCourseInfo>();
        List datas = getCurrAdapterViewHelper().getData();
        if (datas != null && datas.size() > 0) {
            for (Object data : datas) {
                LocalCourseInfo localCourseInfo = (LocalCourseInfo) data;
                if (localCourseInfo != null && localCourseInfo.isSelect) {
                    localCourseInfos.add(localCourseInfo);
                }
            }
            if (localCourseInfos.size() > 0 && datas.size() > 0 && localCourseInfos.size() == datas.size()) {
                return true;
            }
        }

        return false;
    }

    private List<LocalCourseInfo> getSelectedData() {
        List<LocalCourseInfo>  localCourseInfos = new ArrayList<LocalCourseInfo>();
        List datas = getCurrAdapterViewHelper().getData();
        if (datas != null && datas.size() > 0) {
            for (Object data : datas) {
                LocalCourseInfo localCourseInfo = (LocalCourseInfo) data;
                if (localCourseInfo != null && localCourseInfo.isSelect) {
                    localCourseInfos.add(localCourseInfo);
                }
            }
        }
        return localCourseInfos;
    }

    public LocalCourseInfo getSelectData() {
        LocalCourseInfo data = null;
        List<LocalCourseInfo> localCourseList = getCurrAdapterViewHelper().getData();
        if (localCourseList != null && localCourseList.size() > 0) {
            for (LocalCourseInfo info : localCourseList) {
                if (info.mIsCheck) {
                    data = info;
                    break;
                }
            }
        }
        return data;
    }

    private void checkState(boolean isSelectAll) {
        checkBtn.setImageResource(isSelectAll ? R.drawable.select : R.drawable.unselect);
    }

    private void checkData(boolean isSelectAll) {
        List datas = getCurrAdapterViewHelper().getData();
        if (datas != null && datas.size() > 0) {
            for (Object data : datas) {
                LocalCourseInfo info = (LocalCourseInfo) data;
                if (info != null) {
                    info.isSelect = isSelectAll;
                }
            }
        }
        getCurrAdapterViewHelper().update();
    }

}
