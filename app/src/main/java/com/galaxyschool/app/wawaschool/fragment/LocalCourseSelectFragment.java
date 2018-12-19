package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ScreenUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.db.LocalCourseDao;
import com.galaxyschool.app.wawaschool.db.dto.LocalCourseDTO;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseType;
import com.galaxyschool.app.wawaschool.pojo.weike.LocalCourseInfo;
import com.galaxyschool.app.wawaschool.views.HalfRoundedImageView;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.oosic.apps.iemaker.base.BaseSlideManager;
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
import java.util.List;

/**
 * Created by wangchao on 3/24/16.
 */
public class LocalCourseSelectFragment extends ContactsListFragment /*implements CommitHelper
        .NoteCommitListener*/ {

    public static final int MAX_COLUMN = 2;

    private int courseType=CourseType.COURSE_TYPE_LOCAL;
    private LocalCourseInfo getSelectData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_local_course_select, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        loaViews();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
    private int count = 2;//item个数
    private int spacecount = 3;//间隔数
    private void initViews() {

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
                getActivity(), gridView, R.layout.resource_item_pad) {
                @Override
                public void loadData() {
                    loadLocalCourses(courseType);
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    LocalCourseInfo data = (LocalCourseInfo) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;
                    String     thumbPath = data.mPath + File.separator + Utils.RECORD_HEAD_IMAGE_NAME;
                    LQImageLoader.DIOptBuiderParam param = new LQImageLoader.DIOptBuiderParam();
                    param.mIsCacheInMemory = true;
                    param.mOutWidth = LQImageLoader.OUT_WIDTH;
                    param.mOutHeight = LQImageLoader.OUT_HEIGHT;
                    TextView textView = (TextView) view.findViewById(R.id.resource_title);
                    if (textView != null) {
                        textView.setText(data.mTitle);
                    }
                    ImageView flag = (ImageView) view.findViewById(R.id.item_selector);
                    flag.setVisibility( View.VISIBLE);
                    if(getSelectData!=null&&getSelectData==data){
                        flag.setSelected(true);
                    }else{
                        flag.setSelected(false);
                    }
                    FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.resource_frameLayout);
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) frameLayout.getLayoutParams();
                    int windowWith = ScreenUtils.getScreenWidth(getActivity());//屏幕宽度
                    int itemWidth = (windowWith - getActivity().getResources().getDimensionPixelSize(R.dimen.separate_20dp) * spacecount) / count;
                    params.width = itemWidth;
                    params.height = params.width * 9 / 16;
                    frameLayout.setLayoutParams(params);
                    params = (LinearLayout.LayoutParams)textView.getLayoutParams();
                    params.width = itemWidth;
                    textView.setLayoutParams(params);
                    ImageView imageView = (HalfRoundedImageView) view.findViewById(R.id.resource_thumbnail);
                    if (imageView != null) {
                        MyApplication.getThumbnailManager( getActivity()).displayThumbnailWithDefault(
                                thumbPath, imageView, R.drawable.default_cover);
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
                    LocalCourseInfo data = (LocalCourseInfo) holder.data;

                    List<LocalCourseInfo> localCourseInfos = getCurrAdapterViewHelper().getData();
                    if (localCourseInfos != null) {
                        for (LocalCourseInfo localCourseInfo : localCourseInfos) {
                            localCourseInfo.isSelect = false;
                        }

                        data.isSelect = true;
                        getSelectData=data;
                        getCurrAdapterViewHelper().update();
                    }


                }
            };
            setCurrAdapterViewHelper(gridView, helper);
        }

        TextView   textView = (TextView) findViewById(R.id.btn_bottom_ok);
        if(textView != null) {
            textView.setOnClickListener(this);
        }

        textView = (TextView) findViewById(R.id.btn_bottom_cancel);
        if(textView != null) {
            textView.setOnClickListener(this);
        }
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
            setViewVisible(true);
            Collections.sort(localCourseInfos, modifiedTimeComparator);
        }else {
            setViewVisible(false);
            return;
        }
        getCurrAdapterViewHelper().setData(localCourseInfos);
    }
    private void setViewVisible(boolean hasData){
        TextView noDataShow= (TextView) findViewById(R.id.tv_has_no_data);
        if (hasData){
            noDataShow.setVisibility(View.GONE);
            findViewById(R.id.ll_has_data_layout).setVisibility(View.VISIBLE);
        }else {
            noDataShow.setVisibility(View.VISIBLE);
            findViewById(R.id.ll_has_data_layout).setVisibility(View.GONE);
        }
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
                            LocalCourseInfo localCourseInfo = localCourseDTO.toLocalCourseInfo();
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
        int[] rayMenuH = {BaseSlideManager.MENU_ID_CURVE, BaseSlideManager.MENU_ID_LASER,
                BaseSlideManager.MENU_ID_ERASER};
        param.mRayMenusH = rayMenuH;
        return param;
    }


    private String getRootFolder(int courseType) {
        String rootFolder = Utils.getUserCourseRootPath(getMemeberId(), courseType, false);
        if (rootFolder.endsWith(File.separator)) {
            rootFolder = rootFolder.substring(0, rootFolder.length() - 1);
        }
        return rootFolder;
    }

    @Override
    public void onClick(View v) {
      if(v.getId() == R.id.btn_bottom_ok) {
          chooseCourseToSend();
        } else if(v.getId() == R.id.btn_bottom_cancel){
         finish();
        } else {
            super.onClick(v);
        }
    }

//    public LocalCourseInfo getSelectData() {
//        LocalCourseInfo data = null;
//        List<LocalCourseInfo> localCourseList = getCurrAdapterViewHelper().getData();
//        if (localCourseList != null && localCourseList.size() > 0) {
//            for (LocalCourseInfo info : localCourseList) {
//                if (info.mIsCheck) {
//                    data = info;
//                    break;
//                }
//            }
//        }
//        return data;
//    }
public void chooseCourseToSend() {
    if(getSelectData==null||!getSelectData.isSelect){
        TipMsgHelper.ShowLMsg(getActivity(), R.string.no_file_select);
        return;
    }
    Intent intent =new Intent();
    intent.putExtra("NOC_CourseData",getSelectData);
    intent.putExtra("Data_Type",NocLqselectFragment.DATA_TYPE_LOCAL);
    getActivity().setResult(Activity.RESULT_OK,intent);
    finish();
}
}
