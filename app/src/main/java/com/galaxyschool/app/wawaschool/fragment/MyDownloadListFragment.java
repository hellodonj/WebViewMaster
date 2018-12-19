package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.BookDetailActivity;
import com.galaxyschool.app.wawaschool.PictureBooksDetailActivity;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.Common;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.db.DownloadCourseDao;
import com.galaxyschool.app.wawaschool.db.dto.DownloadCourseDTO;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.fragment.resource.NewResourcePadAdapterViewHelper;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.sortlistview.ClearEditText;
import com.galaxyschool.app.wawaschool.R;
import com.lqwawa.libs.filedownloader.DownloadListener;
import com.lqwawa.libs.filedownloader.DownloadService;
import com.lqwawa.libs.filedownloader.FileInfo;
import com.lqwawa.tools.FileZipHelper;
import com.osastudio.common.library.Md5FileNameGenerator;
import com.osastudio.common.utils.Utils;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MyDownloadListFragment extends ContactsListFragment {

    public static final String TAG = MyDownloadListFragment.class.getSimpleName();

    private static final int MAX_BOOKS_PER_ROW = 2;

    private TextView keywordView;
    private String keyword = "";
    private Map<String, NewResourceInfo> selectedBooks = new HashMap();
    private Map<String, DownloadCourseDTO> downloadCourseMap = new HashMap<String,
            DownloadCourseDTO>();
    //    private NewResourceInfo emptyBook = new NewResourceInfo();
    private NewResourceInfo currBook;
    //    private String keyword;
    private DownloadCourseDao downloadCourseDao;

    private DownloadService downloadService;
    private boolean beginToDelete;
    private ServiceConnection downloadServiceConn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            downloadService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadService = ((DownloadService.DownloadBinder) service).getService();
            getCurrAdapterViewHelper().update();

            keyword = "";
            loadDownloadCourses();
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        getMyApplication().bindDownloadService(getActivity(), downloadServiceConn);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_download_list, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initViews();
        downloadCourseDao = new DownloadCourseDao(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        loadViews();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (downloadService != null) {
            downloadService.removeDownloadListeners(MyDownloadListFragment.class);
        }
        getMyApplication().unbindDownloadService(downloadServiceConn);
    }

    private void initViews() {
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            textView.setOnClickListener(this);
            textView.setText(R.string.my_download_courses);
        }

        ClearEditText editText = (ClearEditText) findViewById(R.id.search_keyword);
        if (editText != null) {
            editText.setHint(getString(R.string.search_title));
            editText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        hideSoftKeyboard(getActivity());
                        keyword = keywordView.getText().toString().trim();
                        resetDeleteStateIfNecessary();
                        getCurrAdapterViewHelper().clearData();
                        loadDownloadCourses();
//                        loadBookList();
                        return true;
                    }
                    return false;
                }
            });
            editText.setOnClearClickListener(new ClearEditText.OnClearClickListener() {
                @Override
                public void onClearClick() {
                    keyword = "";
                    getCurrAdapterViewHelper().clearData();
                    loadDownloadCourses();
                }
            });
            editText.setInputType(InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        }
        keywordView = editText;

        View view = findViewById(R.id.search_btn);
        if (view != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSoftKeyboard(getActivity());
                    keyword = keywordView.getText().toString().trim();
                    resetDeleteStateIfNecessary();
                    getCurrAdapterViewHelper().clearData();
                    loadDownloadCourses();
//                    loadBookList();
                }
            });
            view.setVisibility(View.VISIBLE);
        }
        view = findViewById(R.id.contacts_search_bar_layout);
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }

        final PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        setStopPullUpState(true);
        setStopPullDownState(true);
        setPullToRefreshView(pullToRefreshView);

        GridView gridView = (GridView) findViewById(R.id.book_grid_view);
        if (gridView != null) {
            gridView.setNumColumns(MAX_BOOKS_PER_ROW);
            AdapterViewHelper gridViewHelper = new BookAdapterViewHelper(getActivity(), gridView);

            setCurrAdapterViewHelper(gridView, gridViewHelper);
        }

        initSelectorViews();
    }

    private void resetDeleteStateIfNecessary() {

        if (beginToDelete){
            beginToDelete = false;
            //已经选择了条目,再搜索的时候需要重置。
            selectedBooks.clear();
            View v = findViewById(R.id.contacts_select_all_layout);
            if (v != null) {
                v.setVisibility(View.GONE);
            }
            v = findViewById(R.id.contacts_action_tools_layout);
            if (v != null) {
                v.setVisibility(View.GONE);
            }
            v = findViewById(R.id.contacts_action_start);
            if (v != null) {
                v.setVisibility(View.VISIBLE);
            }
            ImageView imageView = (ImageView) findViewById(R.id.contacts_select_all_icon);
            if (imageView != null) {
                imageView.setSelected(isAllBooksSelected());
            }
        }
    }

    private void initSelectorViews() {
        TextView textView = (TextView) findViewById(R.id.contacts_action_start);
        if (textView != null) {
            textView.setText(R.string.delete);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!getCurrAdapterViewHelper().hasData()) {
                        return;
                    }
                    //进入删除模式
                    beginToDelete = true;
                    v.setVisibility(View.GONE);
                    v = findViewById(R.id.contacts_select_all_layout);
                    if (v != null) {
                        v.setVisibility(View.VISIBLE);
                    }
                    v = findViewById(R.id.contacts_action_tools_layout);
                    if (v != null) {
                        v.setVisibility(View.VISIBLE);
                    }
                    v = findViewById(R.id.contacts_select_all_icon);
                    if (v != null) {
                        v.setSelected(false);
                    }
                }
            });
        }

        textView = (TextView) findViewById(R.id.contacts_action_left);
        if (textView != null) {
            textView.setText(R.string.cancel);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    beginToDelete = false;
                    selectedBooks.clear();
                    v = findViewById(R.id.contacts_select_all_layout);
                    if (v != null) {
                        v.setVisibility(View.GONE);
                    }
                    v = findViewById(R.id.contacts_action_tools_layout);
                    if (v != null) {
                        v.setVisibility(View.GONE);
                    }
                    v = findViewById(R.id.contacts_action_start);
                    if (v != null) {
                        v.setVisibility(View.VISIBLE);
                    }
                    ImageView imageView = (ImageView) findViewById(R.id.contacts_select_all_icon);
                    if (imageView != null) {
                        imageView.setSelected(isAllBooksSelected());
                    }
                }
            });
        }

        textView = (TextView) findViewById(R.id.contacts_action_right);
        if (textView != null) {
            textView.setText(R.string.confirm);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedBooks.size() <= 0) {
                        return;
                    }
                    List<NewResourceInfo> list = new ArrayList();
                    for (Map.Entry<String, NewResourceInfo> entry : selectedBooks.entrySet()) {
                        list.add(entry.getValue());
                    }
                    selectedBooks.clear();
                    deleteBooks(list);
                }
            });
        }

        View view = findViewById(R.id.contacts_action_tools_layout);
        if (view != null) {
            view.setVisibility(View.GONE);
        }

        view = findViewById(R.id.contacts_select_all_layout);
        if (view != null) {
            view.setBackgroundResource(android.R.color.white);
            view.setVisibility(View.GONE);

            view = view.findViewById(R.id.contacts_select_all);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageView imageView = (ImageView) v.findViewById(R.id.contacts_select_all_icon);
                    if (imageView != null) {
                        imageView.setSelected(!imageView.isSelected());
                        selectAllBooks(imageView.isSelected());
                        notifySelectorViews();
                        getCurrAdapterViewHelper().update();
                    }
                }
            });
        }

        view = findViewById(R.id.contacts_action_bar_layout);
        if (view != null) {
            view.setVisibility(View.GONE);
        }
    }

    private void notifySelectorViews() {
        List<NewResourceInfo> list = getCurrAdapterViewHelper().getData();
        int size = 0;
        if (list != null && list.size() > 0) {
//            for (NewResourceInfo data : list) {
//                if (data != emptyBook) {
//                    size++;
//                }
//            }
            size = list.size();
        }
        View view = findViewById(R.id.contacts_action_bar_layout);
        if (view != null) {
            view.setVisibility(size > 0 ? View.VISIBLE : View.GONE);
        }

        TextView textView = (TextView) findViewById(R.id.contacts_action_right);
        if (textView != null) {
            textView.setEnabled(hasSelectedBooks());
        }

        ImageView imageView = (ImageView) findViewById(R.id.contacts_select_all_icon);
        if (imageView != null) {
            imageView.setSelected(isAllBooksSelected());
        }
    }

    private boolean isSelecting() {
        View v = findViewById(R.id.contacts_action_tools_layout);
        if (v != null) {
            return v.getVisibility() == View.VISIBLE;
        }
        return false;
    }

    private void showSelectorViews(boolean show) {
        View v = findViewById(R.id.contacts_select_all_layout);
        if (v != null) {
            v.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        v = findViewById(R.id.contacts_action_tools_layout);
        if (v != null) {
            v.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        v = findViewById(R.id.contacts_action_start);
        if (v != null) {
            v.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private boolean isBookSelected(NewResourceInfo data) {
        return data != null && selectedBooks.containsKey(data.getResourceId());
    }

    private void selectBook(NewResourceInfo data, boolean selected) {
        if (TextUtils.isEmpty(data.getResourceId())) {
            return;
        }
        if (selected) {
            selectedBooks.put(data.getResourceId(), data);
        } else {
            selectedBooks.remove(data.getResourceId());
        }
    }

    private void selectAllBooks(boolean selected) {
        if (selected) {
            if (getCurrAdapterViewHelper().hasData()) {
                NewResourceInfo data = null;
                for (Object obj : getCurrAdapterViewHelper().getData()) {
                    data = (NewResourceInfo) obj;
                    if (TextUtils.isEmpty(data.getResourceId())) {
                        continue;
                    }
                    selectedBooks.put(data.getResourceId(), data);
                }
            }
        } else {
            selectedBooks.clear();
        }
    }

    private boolean hasSelectedBooks() {
        return selectedBooks.size() > 0;
    }

    private boolean isAllBooksSelected() {
        List<NewResourceInfo> list = getCurrAdapterViewHelper().getData();
        int size = 0;
        if (list != null && list.size() > 0) {
//            for (NewResourceInfo data : list) {
//                if (data != emptyBook) {
//                    size++;
//                }
//            }
            size = list.size();
        }
        return size > 0 && size == selectedBooks.size();
    }

    private void loadViews() {
        if (getCurrAdapterViewHelper().hasData()) {
            getCurrAdapterViewHelper().update();
        } else {
//            loadBookList();

        }
    }

    private void loadDownloadCourses() {
        DownloadCourseDao dao = new DownloadCourseDao(getActivity());
        try {
            List<DownloadCourseDTO> dtos = dao.getDownloadCourseByPath(getMemeberId());
            if (dtos != null && dtos.size() > 0) {
                for (DownloadCourseDTO dto : dtos) {
                    if (dto != null) {
                        downloadCourseMap.put(dto.getId(), dto);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (downloadService != null) {
            List<FileInfo> fileInfos = downloadService.getFileInfoList(getMemeberId(), keyword);
            List<NewResourceInfo> newResourceInfos = new ArrayList<NewResourceInfo>();
            if (fileInfos != null && fileInfos.size() > 0) {
                for (FileInfo info : fileInfos) {
                    if (info != null && !TextUtils.isEmpty(info.getId()) && downloadCourseMap
                            .containsKey(info.getId())) {
                        DownloadCourseDTO dto = downloadCourseMap.get(info.getId());
                        if (dto != null) {
                            NewResourceInfo resourceInfo = new NewResourceInfo();
                            resourceInfo.setResourceId(info.getFileId());
                            resourceInfo.setResourceUrl(info.getFileUrl());
                            resourceInfo.setLocalZipPath(info.getFilePath());
                            resourceInfo.setTitle(dto.getTitle());
                            resourceInfo.setAuthorId(dto.getAuthorId());
                            resourceInfo.setAuthorName(dto.getAuthorName());
                            resourceInfo.setDescription(dto.getIntroduction());
                            if (!info.isDownloaded()) {
                                resourceInfo.setThumbnail(dto.getThumbnail());
                            }
                            resourceInfo.setIsDownloaded(info.isDownloaded());
                            resourceInfo.setScreenType(dto.getScreenType());
                            newResourceInfos.add(resourceInfo);
                        }
                    }
                }
            }

            unzipCourses(newResourceInfos);

            getCurrAdapterViewHelper().setData(newResourceInfos);
            notifySelectorViews();
        }
    }

    private void unzipCourses(List<NewResourceInfo> newResourceInfos) {
        UnzipCoursesAsycTask task = new UnzipCoursesAsycTask(newResourceInfos);
        task.execute();

    }

    private class UnzipCoursesAsycTask extends AsyncTask<Void, Void, Boolean> {

        private List<NewResourceInfo> newResourceInfos;

        public UnzipCoursesAsycTask(List<NewResourceInfo> newResourceInfos) {
            this.newResourceInfos = newResourceInfos;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (newResourceInfos != null && newResourceInfos.size() > 0) {
                for (NewResourceInfo info : newResourceInfos) {
                    String destPath = null;
                    if (info != null && info.isDownloaded()) {
                        destPath = getLocalCoursePath(info);
                        if (!TextUtils.isEmpty(destPath)) {
                            boolean isExist = checkLocalPath(destPath);
                            if (!isExist) {
                                File file = new File(destPath);
                                if (file != null && file.exists()) {
                                    com.galaxyschool.app.wawaschool.common.Utils
                                            .safeDeleteDirectory(destPath);
                                }
                                com.galaxyschool.app.wawaschool.common.Utils
                                        .createLocalDiskPath(destPath);
                                try {
                                    FileZipHelper.unzipMyZip(info
                                            .getLocalZipPath(), destPath);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        String thumbnailPath = getLocalCourseThumbnail(destPath);
                        if (!TextUtils.isEmpty(thumbnailPath)) {
                            info.setThumbnail(thumbnailPath);
                        }
                    }
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            getCurrAdapterViewHelper().update();
        }
    }

    private class DeleteCoursesAsycTask extends AsyncTask<Void, Void, Boolean> {

        private List<NewResourceInfo> newResourceInfos;

        public DeleteCoursesAsycTask(List<NewResourceInfo> newResourceInfos) {
            this.newResourceInfos = newResourceInfos;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (newResourceInfos != null && newResourceInfos.size() > 0) {
                for (NewResourceInfo info : newResourceInfos) {
                    if (info != null) {
                        if (downloadService != null) {
//                            FileInfo fileInfo = downloadService.getFileInfo(getMemeberId(),
//                                    info.getResourceId());
//                            if (info == null) {
//                                info.toFileInfo(getMemeberId());
//                            }
                            FileInfo fileInfo = new FileInfo();
                            fileInfo.setFileId(info.getResourceId());
                            fileInfo.setFilePath(info.getLocalZipPath());
                            fileInfo.setUserId(getMemeberId());
                            downloadService.deleteFile(fileInfo);
                        }

                        try {
                            downloadCourseDao.deleteDownCoursesByResId(getMemeberId(), info
                                    .getResourceId());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        String coursePath = getLocalCoursePath(info);
                        File courseFile = new File(coursePath);
                        if (courseFile != null && courseFile.canRead()) {
                            String parentPath = courseFile.getParent();
                            if (!TextUtils.isEmpty(parentPath)) {
                                com.galaxyschool.app.wawaschool.common.Utils.safeDeleteDirectory
                                        (parentPath);
                            }
                        }
                    }
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            beginToDelete = false;
            showSelectorViews(false);
            notifySelectorViews();
            if (getCurrAdapterViewHelper().hasData() &&
                    newResourceInfos != null && newResourceInfos.size() > 0) {
                //删除操作
                List<NewResourceInfo> resultList = getCurrAdapterViewHelper().getData();
                if (resultList != null && resultList.size() > 0) {
                    //迭代
                    Iterator<NewResourceInfo> iterator = resultList.iterator();
                    while (iterator.hasNext()) {
                        NewResourceInfo resourceInfo = iterator.next();
                        if (resourceInfo != null) {
                            for (NewResourceInfo info : newResourceInfos) {
                                if (info != null) {
                                    String resultId = resourceInfo.getResourceId();
                                    String id = info.getResourceId();
                                    if (!TextUtils.isEmpty(resultId)
                                            && !TextUtils.isEmpty(id)
                                            && resultId.equals(id)) {
                                        //找到项目,删除项目。
                                        iterator.remove();
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            getCurrAdapterViewHelper().update();
        }
    }

    private String getLocalCoursePath(NewResourceInfo info) {
        String destPath = null;
        if (!TextUtils.isEmpty(info.getResourceUrl())) {
            destPath = Common.DownloadWeike + Md5FileNameGenerator.generate(info.getResourceUrl()) + "/";
            if (!TextUtils.isEmpty(info.getResourceId())) {
                String[] ids = info.getResourceId().split("-");
                if (ids != null && ids.length == 2) {
                    if (!TextUtils.isEmpty(ids[1])) {
                        int resType = Integer.parseInt(ids[1]);
                        if (resType > ResType.RES_TYPE_BASE) {
                            destPath = destPath + info.getTitle() + "/";
                        }
                    }
                }
            }
        }
        return destPath;
    }

    private boolean checkLocalPath(String destPath) {
        String path = getLocalCourseThumbnail(destPath);
        return (TextUtils.isEmpty(path) ? false : true);
    }

    private String getLocalCourseThumbnail(String destPath) {
        String path = null;
        String coursePath = findWeikeFolder(destPath);
        if (!TextUtils.isEmpty(coursePath)) {
            File file = new File(coursePath);
            if (file != null && file.exists()) {
                File headFile = new File(file, "head.jpg");
                if (headFile != null && headFile.exists() && headFile.canRead()) {
                    path = headFile.getAbsolutePath();
                }
            }
        }
        return path;
    }

    private String findWeikeFolder(String folder) {
        String result = null;
        if (!TextUtils.isEmpty(folder)) {
            File file = new File(folder);
            if (file.exists() && file.isDirectory()) {
                if (isCourseFolder(folder)) {
                    return folder;
                } else {
                    File[] files = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        result = findWeikeFolder(files[i].getPath());
                        if (result != null) {
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }

    private boolean isCourseFolder(String folder) {
        File headFile = new File(folder, "head.jpg");
        File pageIndexFile = new File(folder, "page_index.xml");
        File courseIndexFile = new File(folder, "course_index.xml");
        if(headFile == null || pageIndexFile == null || courseIndexFile == null) {
            return false;
        }
        if(headFile.exists() || pageIndexFile.exists() || courseIndexFile.exists()) {
            return true;
        }
        return false;
    }

//    private void loadBookList() {
//        loadBookList(this.keywordView.getText().toString());
//    }
//
//    private void loadBookList(String keyword) {
//        keyword = keyword.trim();
//        if (!keyword.equals(this.keyword)) {
//            getCurrAdapterViewHelper().clearData();
//            getPageHelper().clear();
//        }
//        this.keyword = keyword;
//        Map<String, Object> params = new HashMap();
//        params.put("MType", 15);
//        params.put("KeyWord", keyword);
//        params.put("MemberId", getUserInfo().getMemberId());
//        params.put("Pager", getPageHelper().getFetchingPagerArgs());
//        DefaultPullToRefreshDataListener listener =
//                new DefaultPullToRefreshDataListener<NewResourceInfoListResult>(
//                        NewResourceInfoListResult.class) {
//            @Override
//            public void onSuccess(String jsonString) {
//                super.onSuccess(jsonString);
//                NewResourceInfoListResult result = getResult();
//                if (result == null || !result.isSuccess()
//                        || result.getModel() == null) {
//                    return;
//                }
//                updateBookListView(result);
//            }
//        };
//        RequestHelper.sendPostRequest(getActivity(),
//                ServerUrl.GET_MY_DOWNLOAD_LIST_URL, params, listener);
//    }
//
//    private void updateBookListView(NewResourceInfoListResult result) {
//        if (getPageHelper().isFetchingPageIndex(result.getModel().getPager())) {
//            List<NewResourceInfo> list = result.getModel().getData();
//            if (list == null || list.size() <= 0) {
//                if (getPageHelper().isFetchingFirstPage()) {
//                    getCurrAdapterViewHelper().clearData();
//                    TipsHelper.showToast(getActivity(),
//                            getString(R.string.no_data));
//                } else {
//                    TipsHelper.showToast(getActivity(),
//                            getString(R.string.no_more_data));
//                }
//                return;
//            }
//
//            if (getPageHelper().isFetchingFirstPage()) {
//                getCurrAdapterViewHelper().clearData();
//            }
//            getPageHelper().updateByPagerArgs(result.getModel().getPager());
//            getPageHelper().setCurrPageIndex(
//                    getPageHelper().getFetchingPageIndex());
//            if (getCurrAdapterViewHelper().hasData()) {
//                int position = getCurrAdapterViewHelper().getData().size();
//                if (position > 0) {
//                    position--;
//                }
//                getCurrAdapterViewHelper().getData().addAll(list);
//
//                getCurrAdapterView().setSelection(position);
//            } else {
//                //fix bug can not delete books when update data in delete mode
//                if(list != null && list.size() > 0) {
//                    for (NewResourceInfo book : list) {
//                        if(book != null && !TextUtils.isEmpty(book.getId()) && selectedBooks.containsKey(book.getResourceId())) {
//                            selectedBooks.put(book.getResourceId(), book);
//                        }
//                    }
//                }
//                getCurrAdapterViewHelper().setData(list);
//            }
//            notifySelectorViews();
//        }
//    }

    private void enterBookDetails(NewResourceInfo book) {
        currBook = book;
        if (downloadService != null) {
            FileInfo fileInfo = downloadService.getFileInfo(getUserInfo().getMemberId(),
                    book.getResourceId());
            if (fileInfo != null && fileInfo.isDownloaded()) {
                if (book != null) {
                    book.setResourceId(fileInfo.getFileId());
                    book.setLocalZipPath(fileInfo.getFilePath());
                }
                ActivityUtils.openCourseDetail(getActivity(),book,PictureBooksDetailActivity.FROM_MY_DOWNLOAD);
            }
        }
    }

    private void showDeleteBookDialog(final NewResourceInfo data, String title) {
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(
                getActivity(), null,
                title,
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
                        List<NewResourceInfo> list = new ArrayList<>();
                        list.add(data);
                        deleteBooks(list);
                    }
                });
        messageDialog.show();
    }

    private void deleteBooks(List<NewResourceInfo> list) {
        DeleteCoursesAsycTask deleteCoursesAsycTask = new DeleteCoursesAsycTask(list);
        deleteCoursesAsycTask.execute();
    }

//    private void deleteBooks(List<NewResourceInfo> list) {
//        if (list == null || list.size() <= 0) {
//            return;
//        }
//        Map<String, Object> params = new HashMap();
//        params.put("MType", 15);
//        StringBuilder builder = new StringBuilder();
//        Iterator<NewResourceInfo> iterator = list.iterator();
//        NewResourceInfo data = null;
//        while (iterator.hasNext()) {
//            data = iterator.next();
//            if (TextUtils.isEmpty(data.getId())) {
//                continue;
//            }
//            builder.append(data.getId());
//            if (iterator.hasNext()) {
//                builder.append(",");
//            }
//        }
//        params.put("MaterialId", builder.toString());
//        RequestHelper.RequestListener listener =
//                new RequestHelper.RequestListener<DataResult>(
//                        getActivity(), DataResult.class) {
//                    @Override
//                    public void onSuccess(String jsonString) {
//                        if (getActivity() == null) {
//                            return;
//                        }
//                        super.onSuccess(jsonString);
//                        if (getResult() == null || !getResult().isSuccess()) {
//                            TipsHelper.showToast(getActivity(), R.string.delete_failure);
//                            return;
//                        } else {
//                            showSelectorViews(false);
//                            TipsHelper.showToast(getActivity(), R.string.delete_success);
//                            List<NewResourceInfo> list = (List<NewResourceInfo>) getTarget();
//                            String memberId = getUserInfo().getMemberId();
//                            FileInfo fileInfo = null;
//                            for (NewResourceInfo data : list) {
//                                getCurrAdapterViewHelper().getData().remove(data);
//                                selectBook(data, false);
//                                if (downloadService != null) {
//                                    fileInfo = downloadService.getFileInfo(memberId, data.getResourceId());
//                                    if (fileInfo != null) {
//                                        downloadService.deleteFile(fileInfo);
//                                    }
//                                }
//                            }
//
//                            list = getCurrAdapterViewHelper().getData();
////                    Iterator<NewResourceInfo> iterator = list.iterator();
////                    while (iterator.hasNext()) {
////                        if (iterator.next() == emptyBook) {
////                            iterator.remove();
////                        }
////                    }
////                    while (list.size() % MAX_BOOKS_PER_ROW != 0) {
////                        list.add(emptyBook);
////                    }
//
//                            notifySelectorViews();
//                            getCurrAdapterViewHelper().update();
//                        }
//                    }
//                };
//        listener.setTarget(list);
//        listener.setShowLoading(true);
//        RequestHelper.sendPostRequest(getActivity(),
//                ServerUrl.DELETE_MY_DOWNLOAD_BOOK_URL, params, listener);
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == BookDetailActivity.REQUEST_CODE_DELETE_BOOK) {
                if (data.getExtras().containsKey(BookDetailActivity.BOOK_ID)) {
                    String bookId = data.getExtras().getString(BookDetailActivity.BOOK_ID);
                    if (currBook != null && currBook.getId().equals(bookId)) {
                        List<NewResourceInfo> list = getCurrAdapterViewHelper().getData();
                        list.remove(currBook);
//                        Iterator<NewResourceInfo> iterator = list.iterator();
//                        while (iterator.hasNext()) {
//                            if (iterator.next() == emptyBook) {
//                                iterator.remove();
//                            }
//                        }
//                        while (list.size() % MAX_BOOKS_PER_ROW != 0) {
//                            list.add(emptyBook);
//                        }
                        getCurrAdapterViewHelper().update();
                    }
                }
            }
        }
    }

    private class BookAdapterViewHelper extends NewResourcePadAdapterViewHelper {
        public BookAdapterViewHelper(Activity activity, AdapterView adapterView) {
            super(activity, adapterView, R.layout.my_book_item);
        }

        @Override
        public void loadData() {
//            loadBookList();
            loadDownloadCourses();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            NewResourceInfo data = (NewResourceInfo) getDataAdapter().getItem(position);
            if (data == null) {
                return view;
            }

            MyViewHolder holder = null;
            if (view.getTag() == null ||
                    (view.getTag() != null && view.getTag() instanceof ViewHolder)) {
                holder = new MyViewHolder();
            } else {
                holder = (MyViewHolder) view.getTag();
            }
            ImageView imageView = (ImageView) view.findViewById(R.id.resource_thumbnail);
            if (imageView != null) {
                if (!TextUtils.isEmpty(data.getThumbnail()) && !data.getThumbnail().startsWith
                        ("http")) {
                    getThumbnailManager().displayThumbnailWithDefault(data.getThumbnail(), imageView,
                            R.drawable.default_book_cover);
                } else {
                    getThumbnailManager().displayThumbnailWithDefault(
                            AppSettings.getFileUrl(data.getThumbnail()), imageView,
                            R.drawable.default_book_cover);
                }
            }
            TextView textView = (TextView) view.findViewById(R.id.resource_title);
            if (textView != null) {
                textView.setText(data.getTitle());
            }
            imageView = (ImageView) view.findViewById(R.id.item_selector);
            if (imageView != null) {
                if (isSelecting()) {
                    imageView.setSelected(isBookSelected(data));
                    imageView.setVisibility(View.VISIBLE);
                } else {
                    imageView.setVisibility(View.GONE);
                }
            }

            View coverView = view.findViewById(R.id.resource_thumbnail_cover_layout);
            if (coverView != null) {
                holder.coverView = coverView;
            }
            View downloadView = view.findViewById(R.id.download_bar_layout);
            if (downloadView != null) {
                holder.downloadView = downloadView;
            }
            imageView = (ImageView) view.findViewById(R.id.download_state_icon);
            if (imageView != null) {
                holder.stateIconView = imageView;
            }
            textView = (TextView) view.findViewById(R.id.download_state_text);
            if (textView != null) {
                holder.stateTextView = textView;
            }
            ProgressBar progressView = (ProgressBar) view.findViewById(R.id.download_progress);
            if (progressView != null) {
                holder.progressView = progressView;
            }
            textView = (TextView) view.findViewById(R.id.file_size);
            if (textView != null) {
                holder.fileSizeView = textView;
            }

            holder.data = data;
            holder.downloadView.setTag(holder);
            view.setTag(holder);

            if (downloadService == null) {
                return view;
            }

            FileInfo fileInfo = downloadService.getFileInfo(getUserInfo().getMemberId(),
                    data.getResourceId());
            if (fileInfo == null || fileInfo.isDownloadLapsed()) {
                // 未下载
                holder.coverView.setVisibility(View.VISIBLE);
                holder.downloadView.setVisibility(View.INVISIBLE);
            } else if (fileInfo.isDownloaded()) {
                // 已下载
                holder.coverView.setVisibility(View.GONE);
                holder.downloadView.setVisibility(View.INVISIBLE);
            } else {
                holder.coverView.setVisibility(View.VISIBLE);
                holder.downloadView.setVisibility(View.VISIBLE);
                holder.stateTextView.setVisibility(View.VISIBLE);
                holder.stateIconView.setVisibility(View.VISIBLE);
                holder.fileSizeView.setVisibility(View.VISIBLE);
                holder.progressView.setVisibility(View.VISIBLE);
                if (fileInfo.isDownloadWaiting()) {
                    holder.stateTextView.setText(R.string.my_download_waiting);
                    holder.stateIconView.setImageResource(R.drawable.my_waiting_ico);
                    holder.fileSizeView.setVisibility(View.GONE);
                    holder.progressView.setVisibility(View.GONE);
                } else if (fileInfo.isDownloadStarted() || fileInfo.isDownloading()) {
                    holder.stateTextView.setText(R.string.my_downloading);
                    holder.stateIconView.setImageResource(R.drawable.my_downloading_ico);
                } else if (fileInfo.isDownloadPaused()) {
                    holder.stateTextView.setText(R.string.my_download_paused);
                    holder.stateIconView.setImageResource(R.drawable.my_paused_ico);
                } else if (fileInfo.isDownloadFailed()) {
                    holder.stateTextView.setText(R.string.my_download_failed);
                    holder.stateIconView.setImageResource(R.drawable.my_failed_ico);
                }

                holder.fileSizeView.setText(formatFileSize(fileInfo.getDownloadedSize())
                        + " / " + formatFileSize(fileInfo.getFileSize()));

                holder.progressView.setProgress((int) ((float) fileInfo.getDownloadedSize()
                        / (float) fileInfo.getFileSize() * 100f));
                holder.progressView.setMax(100);

                if (holder.downloadListener == null) {
                    holder.downloadListener = new MyDownloadListener(holder);
                    downloadService.addDownloadListener(MyDownloadListFragment.class,
                            fileInfo.getFileId(), holder.downloadListener);
                }
            }

            holder.downloadView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyViewHolder holder = (MyViewHolder) v.getTag();
                    if (holder == null) {
                        return;
                    }
                    NewResourceInfo data = holder.data;
                    FileInfo fileInfo = downloadService.getFileInfo(getUserInfo().getMemberId(),
                            data.getResourceId());
                    if (fileInfo == null) {
                        fileInfo = data.toFileInfo(getUserInfo().getMemberId());
                    }
                    if (holder.downloadListener == null) {
                        holder.downloadListener = new MyDownloadListener(holder);
                        downloadService.addDownloadListener(MyDownloadListFragment.class,
                                fileInfo.getFileId(), holder.downloadListener);
                    }
                    downloadService.downloadFile(fileInfo);
                }
            });

            return view;
        }

        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            MyViewHolder holder = (MyViewHolder) view.getTag();
            if (holder == null || holder.data == null) {
                return;
            }
            NewResourceInfo data = (NewResourceInfo) holder.data;
//            if (TextUtils.isEmpty(data.getId())) {
//                return;
//            }
            if (!isSelecting()) {
                enterBookDetails(data);
                return;
            }
            boolean selected = isBookSelected(data);
            selectBook(data, !selected);
            notifySelectorViews();
            getCurrAdapterViewHelper().update();
        }
    }

    private class MyViewHolder extends ViewHolder<NewResourceInfo> {
        public View coverView;
        public View downloadView;
        public ImageView stateIconView;
        public TextView stateTextView;
        public ProgressBar progressView;
        public TextView fileSizeView;
        public DownloadListener downloadListener;
    }

    private class MyDownloadListener implements DownloadListener {
        private MyViewHolder viewHolder;

        public MyDownloadListener(MyViewHolder viewHolder) {
            this.viewHolder = viewHolder;
        }

        @Override
        public void onPause(FileInfo fileInfo) {
            Utils.log("TEST", "onPause "
                    + fileInfo.getDownloadedSize() + "/" + fileInfo.getFileSize()
                    + " " + fileInfo.getFileUrl());
            viewHolder.stateTextView.setText(R.string.my_download_paused);
            viewHolder.stateIconView.setImageResource(R.drawable.my_paused_ico);
            getCurrAdapterViewHelper().update();
        }

        @Override
        public void onStart(FileInfo fileInfo) {
            Utils.log("TEST", "onStart "
                    + fileInfo.getDownloadedSize() + "/" + fileInfo.getFileSize()
                    + " " + fileInfo.getFileUrl());
            viewHolder.stateTextView.setText(R.string.my_downloading);
            viewHolder.stateIconView.setImageResource(R.drawable.my_downloading_ico);
            viewHolder.downloadView.setVisibility(View.VISIBLE);
            viewHolder.stateTextView.setVisibility(View.VISIBLE);
            viewHolder.stateIconView.setVisibility(View.VISIBLE);
            viewHolder.fileSizeView.setVisibility(View.VISIBLE);
            viewHolder.progressView.setVisibility(View.VISIBLE);
            getCurrAdapterViewHelper().update();
        }

        @Override
        public void onProgress(FileInfo fileInfo) {
            Utils.log("TEST", "onProgress "
                    + fileInfo.getDownloadedSize() + "/" + fileInfo.getFileSize()
                    + " " + fileInfo.getFileUrl());
            viewHolder.stateTextView.setText(R.string.my_downloading);
            viewHolder.stateIconView.setImageResource(R.drawable.my_downloading_ico);
            viewHolder.fileSizeView
                    .setText(formatFileSize(fileInfo.getDownloadedSize())
                            + " / " + formatFileSize(fileInfo.getFileSize()));
            viewHolder.progressView
                    .setProgress((int) ((float) fileInfo.getDownloadedSize()
                            / (float) fileInfo.getFileSize() * 100f));
            getCurrAdapterViewHelper().update();
        }

        @Override
        public void onPrepare(FileInfo fileInfo) {
            Utils.log("TEST", "onPrepare "
                    + fileInfo.getDownloadedSize() + "/" + fileInfo.getFileSize()
                    + " " + fileInfo.getFileUrl());
            viewHolder.stateTextView.setText(R.string.my_download_waiting);
            viewHolder.stateIconView.setImageResource(R.drawable.my_waiting_ico);
            getCurrAdapterViewHelper().update();
        }

        @Override
        public void onFinish(FileInfo fileInfo) {
            Utils.log("TEST", "onFinish "
                    + fileInfo.getDownloadedSize() + "/" + fileInfo.getFileSize()
                    + " " + fileInfo.getFileUrl());
            viewHolder.downloadView.setVisibility(View.INVISIBLE);
            viewHolder.coverView.setVisibility(View.GONE);
            viewHolder.fileSizeView
                    .setText(formatFileSize(fileInfo.getDownloadedSize())
                            + " / " + formatFileSize(fileInfo.getFileSize()));
            viewHolder.progressView
                    .setProgress((int) ((float) fileInfo.getDownloadedSize()
                            / (float) fileInfo.getFileSize() * 100f));
            getCurrAdapterViewHelper().update();
        }

        @Override
        public void onError(FileInfo fileInfo) {
            Utils.log("TEST", "onError "
                    + fileInfo.getDownloadedSize() + "/" + fileInfo.getFileSize()
                    + " " + fileInfo.getFileUrl());
            viewHolder.stateTextView.setText(R.string.my_download_failed);
            viewHolder.stateIconView.setImageResource(R.drawable.my_failed_ico);
            viewHolder.downloadView.setVisibility(View.INVISIBLE);
            viewHolder.fileSizeView
                    .setText(formatFileSize(fileInfo.getDownloadedSize())
                            + " / " + formatFileSize(fileInfo.getFileSize()));
            viewHolder.progressView
                    .setProgress((int) ((float) fileInfo.getDownloadedSize()
                            / (float) fileInfo.getFileSize() * 100f));
            getCurrAdapterViewHelper().update();
        }

        @Override
        public void onDelete(FileInfo fileInfo) {
            Utils.log("TEST", "onDelete "
                    + fileInfo.getDownloadedSize() + "/" + fileInfo.getFileSize()
                    + " " + fileInfo.getFileUrl());
            viewHolder.downloadView.setVisibility(View.INVISIBLE);
            viewHolder.fileSizeView.setText(formatFileSize(0)
                    + " / " + formatFileSize(fileInfo.getFileSize()));
            viewHolder.progressView.setProgress(0);
        }
    }

    private String formatFileSize(long fileSize) {
        return Utils.formatFileSize(fileSize, 1);
    }

}
