package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.galaxyschool.app.wawaschool.AccountActivity;
import com.galaxyschool.app.wawaschool.BookListActivity;
import com.galaxyschool.app.wawaschool.CatalogLessonListActivity;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.PictureBooksDetailActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.TaskOrderDetailActivity;
import com.galaxyschool.app.wawaschool.common.ShareUtils;
import com.galaxyschool.app.wawaschool.common.UploadCourseHelper;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.ExpandDataAdapter;
import com.galaxyschool.app.wawaschool.fragment.library.ExpandListViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.BookCatalog;
import com.galaxyschool.app.wawaschool.pojo.BookCatalogListResult;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.UploadParameter;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.slide.SlideManagerHornForPhone;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.oosic.apps.share.ShareInfo;
import com.oosic.apps.share.ShareType;
import com.oosic.apps.share.SharedResource;
import com.osastudio.common.library.ActivityStack;
import com.umeng.socialize.media.UMImage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookCatalogListFragment extends ContactsExpandListFragment {

    public static final String TAG = BookCatalogListFragment.class.getSimpleName();

    public interface Constants {
        String EXTRA_MODE = BookListActivity.EXTRA_MODE;
        String EXTRA_BOOK_SOURCE = BookListActivity.EXTRA_BOOK_SOURCE;
        String EXTRA_SCHOOL_ID = BookListActivity.EXTRA_SCHOOL_ID;
        String EXTRA_SCHOOL_NAME = BookListActivity.EXTRA_SCHOOL_NAME;
        String EXTRA_TYPE_CODE = BookListActivity.EXTRA_TYPE_CODE;
        String EXTRA_BOOK_PRIMARY_KEY = "bookPrimaryKey";
        String EXTRA_BOOK_ID = "bookId";
        String EXTRA_BOOK_NAME = "bookName";
        String EXTRA_BOOK_COVER = "bookCover";
        String EXTRA_CONFIRM_BUTTON_TEXT = "confirmButtonText";

        int UPLOAD_MODE = BookListActivity.UPLOAD_MODE;
        int REVIEW_MODE = BookListActivity.REVIEW_MODE;

        int PLATFORM_BOOK = BookListActivity.PLATFORM_BOOK;
        int SCHOOL_BOOK = BookListActivity.SCHOOL_BOOK;
        int PERSONAL_BOOK = BookListActivity.PERSONAL_BOOK;
        int PLATFORM_OUTLINE = BookListActivity.PLATFORM_OUTLINE;
    }

    private RadioButton teachingMaterialTypeRadioButton;
    private RadioButton studyCardTypeRadioButton;
    private int mode;
    private boolean isPick;
    private int bookSource;
    private String bookPrimaryKey;
    private String bookId;
    private String bookName;
    private String bookCover;
    private String schoolId;
    private String schoolName;
    private Map<String, BookCatalog> catalogMap = new HashMap<String, BookCatalog>();
    private Map<String, Boolean> catalogSelectedMap = new HashMap<String, Boolean>();
    private DefaultUploadListener<String> defaultListener = new DefaultUploadListener<String>();
    private boolean isUploadStudyCard;
    private UploadParameter mUploadParameter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_book_catalog_list, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            loadViews();
        }
    }

    private void init() {
        this.mode = getArguments().getInt(Constants.EXTRA_MODE, Constants.REVIEW_MODE);
        this.isPick = getArguments().getBoolean(MediaListFragment.EXTRA_IS_PICK);
        this.bookSource = getArguments().getInt(Constants.EXTRA_BOOK_SOURCE);
        this.bookPrimaryKey = getArguments().getString(Constants.EXTRA_BOOK_PRIMARY_KEY);
        this.bookId = getArguments().getString(Constants.EXTRA_BOOK_ID);
        this.bookName = getArguments().getString(Constants.EXTRA_BOOK_NAME);
        this.bookCover = getArguments().getString(Constants.EXTRA_BOOK_COVER);
        this.schoolId = getArguments().getString(Constants.EXTRA_SCHOOL_ID);
        this.schoolName = getArguments().getString(Constants.EXTRA_SCHOOL_NAME);
        this.mUploadParameter = (UploadParameter) getArguments().getSerializable(UploadParameter.class.getSimpleName());
        if (mUploadParameter != null){
            CourseData courseData = mUploadParameter.getCourseData();
            if (courseData != null){
                int resType = courseData.type % ResType.RES_TYPE_BASE;
                if (resType == ResType.RES_TYPE_STUDY_CARD){
                    isUploadStudyCard = true;
                }
            }
        }

        initViews();
    }

    private void initViews() {
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            textView.setText(bookName);
        }

        if (this.mode == Constants.REVIEW_MODE) {
//            View view = findViewById(R.id.contacts_header_layout);
//            if (view != null) {
//                view.setVisibility(View.GONE);
//            }
//            view = findViewById(R.id.book_catalog_list_header_layout);
//            if (view != null) {
//                view.setVisibility(View.VISIBLE);
//            }
//
//            TextView textView = (TextView) findViewById(R.id.book_name);
//            if (textView != null) {
//                textView.setText(bookName);
//            }
//            view = findViewById(R.id.back_btn);
//            if (view != null) {
//                view.setOnClickListener(this);
//            }
//            view = findViewById(R.id.add_btn);
//            if (view != null) {
//                view.setOnClickListener(this);
//                view.setVisibility(this.bookSource == Constants.PERSONAL_BOOK ?
//                        View.GONE : View.VISIBLE);
//            }
//            view = findViewById(R.id.share_btn);
//            if (view != null) {
//                view.setOnClickListener(this);
//            }
        } else if (this.mode == Constants.UPLOAD_MODE) {
//            View view = findViewById(R.id.book_catalog_list_header_layout);
//            if (view != null) {
//                view.setVisibility(View.GONE);
//            }
//            TextView textView = (TextView) findViewById(R.id.book_name);
//            if (textView != null) {
//                textView.setVisibility(View.GONE);
//            }
//            view = findViewById(R.id.contacts_header_layout);
//            if (view != null) {
//                view.setVisibility(View.VISIBLE);
//            }

            textView = (TextView) findViewById(R.id.contacts_header_right_btn);
            if (textView != null) {
                String text = getArguments().getString(Constants.EXTRA_CONFIRM_BUTTON_TEXT);
                if (TextUtils.isEmpty(text)) {
                    text = getString(R.string.confirm);
                }
                textView.setText(text);
//                textView.setBackgroundResource(R.drawable.sel_nav_button_bg);
                if (isUploadStudyCard){
                    textView.setVisibility(View.GONE);
                }else {
                    textView.setVisibility(View.VISIBLE);
                }
                textView.setOnClickListener(this);
            }
        }

        ImageView imageView = (ImageView)findViewById(R.id.contacts_header_left_btn);
        if(imageView != null) {
            imageView.setOnClickListener(this);
        }


        ExpandableListView listView = (ExpandableListView) findViewById(R.id.contacts_list_view);
        if (listView != null) {
            listView.setGroupIndicator(null);
            ExpandDataAdapter dataAdapter = new ExpandDataAdapter(getActivity(),
                    null, R.layout.catalog_list_item, R.layout.catalog_list_child_item) {
                @Override
                public int getChildrenCount(int groupPosition) {
                    BookCatalog data = (BookCatalog) getGroup(groupPosition);
                    if (data.getChildren() != null) {
                        return data.getChildren().size();
                    }
                    return 0;
                }

                @Override
                public Object getChild(int groupPosition, int childPosition) {
                    BookCatalog data = (BookCatalog) getGroup(groupPosition);
                    return data.getChildren().get(childPosition);
                }

                @Override
                public View getChildView(int groupPosition, int childPosition,
                                         boolean isLastChild, View convertView, ViewGroup parent) {
                    View view = super.getChildView(groupPosition, childPosition,
                            isLastChild, convertView, parent);

                    BookCatalog data = (BookCatalog) getChild(groupPosition, childPosition);
                    MyViewHolder holder = (MyViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new MyViewHolder();
                    }
                    holder.data = data;
                    boolean selected = isCatalogSelected(data.getId());
                    TextView textView = (TextView) view.findViewById(R.id.catalog_item_title);
                    if (textView != null) {
                        holder.titleView = textView;
                        textView.setText(data.getName());
                        textView.setBackgroundColor(selected ?
                                Color.parseColor("#009039") : Color.TRANSPARENT);
                        textView.setTextColor(selected ?
                                Color.WHITE : Color.parseColor("#6d6d6d"));
                    }
                    view.setTag(holder);
                    return view;
                }

                @Override
                public View getGroupView(int groupPosition, boolean isExpanded,
                                         View convertView, ViewGroup parent) {
                    View view = super.getGroupView(groupPosition, isExpanded,
                            convertView, parent);
                    BookCatalog data = (BookCatalog) getGroup(groupPosition);
                    if (data == null) {
                        return view;
                    }
                    MyViewHolder holder = (MyViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new MyViewHolder();
                    }
                    holder.data = data;
                    boolean selected = isCatalogSelected(data.getId());
                    ImageView imageView = (ImageView) view.findViewById(R.id.catalog_item_indicator);
                    if (imageView != null) {
                        holder.indicatorView = imageView;
                        if (data.getChildren() != null && data.getChildren().size() > 0
                                && !isExpanded) {
                            imageView.setImageResource(R.drawable.catalog_ring);
                        } else {
                            imageView.setImageResource(R.drawable.catalog_dot);
                        }
                    }
                    TextView textView = (TextView) view.findViewById(R.id.catalog_item_title);
                    if (textView != null) {
                        holder.titleView = textView;
                        textView.setText(data.getName());
                        textView.setBackgroundColor(selected ?
                                Color.parseColor("#009039") : Color.TRANSPARENT);
                        textView.setTextColor(selected ?
                                Color.WHITE : Color.BLACK);
                    }
                    view.setTag(holder);
                    return view;
                }
            };
            ExpandListViewHelper listViewHelper = new ExpandListViewHelper(getActivity(),
                    listView, dataAdapter) {
                @Override
                public void loadData() {
                    loadBookCatalogList();
                }

                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                    clearSelectedCatalogs();
                    MyViewHolder holder = (MyViewHolder) v.getTag();
                    if (holder == null || holder.data == null) {
                        return true;
                    }
                    BookCatalog data = (BookCatalog) holder.data;
                    selectCatalog(data.getId(), true);
                    getDataAdapter().notifyDataSetChanged();
                    if (mode == Constants.REVIEW_MODE) {
                        if(bookSource == Constants.PLATFORM_BOOK) {
                            enterMediaTypeListByCatalog(data);
                        } else {
                            enterLessonListByCatalog(data);
                        }
                    } else if (mode == Constants.UPLOAD_MODE){
                        if (isUploadStudyCard){
                            CourseData courseData = mUploadParameter.getCourseData();
                            if (courseData != null){
                                popSelectSendTypeDialog(data,courseData);
                            }
                        }
                    }
                    return true;
                }

                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                    MyViewHolder holder = (MyViewHolder) v.getTag();
                    if (holder == null || holder.data == null) {
                        return false;
                    }
                    BookCatalog data = (BookCatalog) holder.data;
                    if (data.getChildren() != null && data.getChildren().size() > 0) {
                        return false;
                    }
                    clearSelectedCatalogs();
                    selectCatalog(((BookCatalog) holder.data).getId(), true);
                    getDataAdapter().notifyDataSetChanged();
                    if (mode == Constants.REVIEW_MODE) {
                        if(bookSource == Constants.PLATFORM_BOOK) {
                            enterMediaTypeListByCatalog(data);
                        } else {
                            enterLessonListByCatalog(data);
                        }
                    } else if (mode == Constants.UPLOAD_MODE){
                        if (isUploadStudyCard){
                            CourseData courseData = mUploadParameter.getCourseData();
                            if (courseData != null){
                                popSelectSendTypeDialog(data,courseData);
                            }
                        }
                    }
                    return false;
                }
            };

            setCurrListViewHelper(listView, listViewHelper);
        }
    }

    protected void selectCatalog(String id, boolean selected) {
        this.catalogSelectedMap.put(id, selected);
    }

    protected void clearSelectedCatalogs() {
        this.catalogSelectedMap.clear();
    }

    protected boolean isCatalogSelected(String id) {
        if (!this.catalogSelectedMap.containsKey(id)) {
            return false;
        }
        return this.catalogSelectedMap.get(id);
    }

    protected BookCatalog getSelectedCatalog() {
        for (Map.Entry<String, Boolean> entry : this.catalogSelectedMap.entrySet()) {
            if (entry.getValue().booleanValue()) {
                return this.catalogMap.get(entry.getKey());
            }
        }
        return null;
    }

    private void loadViews() {
        if (getCurrListViewHelper().hasData()) {
            getCurrListViewHelper().update();
        } else {
            loadBookCatalogList();
        }
    }

    private void loadBookCatalogList() {
        Map<String, Object> params = new HashMap();
        String serverUrl = null;
        if (this.bookSource == Constants.PLATFORM_BOOK) {
            params.put("Type", this.mode);
            params.put("ResourceCategory", this.bookSource);
            params.put("TypeCode", getArguments().getString(Constants.EXTRA_TYPE_CODE));
            params.put("OutlineId", bookId);
            serverUrl = ServerUrl.GET_PLATFORM_BOOK_CATALOG_LIST_URL;
        } else if (this.bookSource == Constants.SCHOOL_BOOK) {
            params.put("SchoolId", this.schoolId);
            if(this.mode == Constants.REVIEW_MODE) {
                params.put("BookId", this.bookId);
                serverUrl = ServerUrl.GET_SCHOOL_BOOK_CATALOG_LIST_URL;
            } else {
                params.put("OutlineId", this.bookId);
                serverUrl = ServerUrl.LOAD_SCHOOL_CATALOG_LIST_URL;
            }
        } else if (this.bookSource == Constants.PERSONAL_BOOK) {
            params.put("SchoolId", this.schoolId);
            params.put("BookId", this.bookPrimaryKey);
            serverUrl = ServerUrl.GET_MY_COLLECTION_BOOK_CATALOG_LIST_URL;
        } else if(this.bookSource == Constants.PLATFORM_OUTLINE) {
            params.put("OutlineId", this.bookId);
            serverUrl = ServerUrl.LOAD_PLATFORM_CATALOG_LIST_URL;
        }
        DefaultPullToRefreshDataListener listener =
                new DefaultPullToRefreshDataListener<BookCatalogListResult>(
                        BookCatalogListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        BookCatalogListResult result = getResult();
                        if (result == null || !result.isSuccess()
                                || result.getModel() == null) {
                            return;
                        }
                        updateBookCatalogListView(result);
                    }
                };
        if (serverUrl != null) {
            RequestHelper.sendPostRequest(getActivity(), serverUrl, params, listener);
        }
    }

    private void updateBookCatalogListView(BookCatalogListResult result) {
        List<BookCatalog> list = result.getModel().getData();
        if (list != null && list.size() > 0) {
            BookCatalog cat = null;
            for (int i = 0; i < list.size(); i++) {
                cat = list.get(i);
                this.catalogMap.put(cat.getId(), cat);
                List<BookCatalog> l = cat.getChildren();
                if (l != null && l.size() > 0) {
                    for (int j = 0; j < l.size(); j++) {
                        cat = l.get(j);
                        this.catalogMap.put(cat.getId(), cat);
                    }
                }
            }
        }
        getCurrListViewHelper().setData(list);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contacts_header_right_btn) {
            BookCatalog catalog = getSelectedCatalog();
            if (catalog == null) {
                TipsHelper.showToast(getActivity(), R.string.pls_select_catalog);
                return;
            }
            sendSelectResourceToSomeWhere(catalog);
        }
        /*
        else if (v.getId() == R.id.contacts_header_left_btn) {
            if(!isPick) {
                finish();
            } else {
                FragmentManager fm = getFragmentManager();
                if(fm != null) {
                    fm.popBackStack();
                }
            }
        } else if (v.getId() == R.id.back_btn) {
            if(!isPick) {
                finish();
            } else {
                FragmentManager fm = getFragmentManager();
                if(fm != null) {
                    fm.popBackStack();
                }
            }
        } else if (v.getId() == R.id.add_btn) {
            if (!getMyApplication().hasLogined()) {
                enterLogin();
                return;
            }
            collectBook();
        } else if (v.getId() == R.id.share_btn) {
            if (!getMyApplication().hasLogined()) {
                enterLogin();
                return;
            }
            shareBook();
        } */
        else {
            super.onClick(v);
        }
    }

    private void sendSelectResourceToSomeWhere(BookCatalog catalog){
        if (this.mode == Constants.UPLOAD_MODE) {
            if (getArguments().containsKey(UploadParameter.class.getSimpleName())) {
                if (!TextUtils.isEmpty(schoolId)) {
                    uploadCourseToSChoolSpace(catalog);
                } else {
//                        uploadCourseToCloudResource(catalog);
                }
                UploadParameter uploadParameter= (UploadParameter) getArguments().getSerializable
                        (UploadParameter.class.getSimpleName());
                if (uploadParameter != null){
                    if (uploadParameter.isLocal() && uploadParameter.isTempData()){
                        Intent intent =new Intent();
                        intent.putExtra(SlideManagerHornForPhone.SAVE_PATH,uploadParameter.getFilePath());
                        getActivity().setResult(Activity.RESULT_OK,intent);
                        getActivity().finish();
                        return;
                    }
                }
                MyApplication myapp = (MyApplication)getActivity().getApplication();
                ActivityStack activityStack = myapp.getActivityStack();
                if(activityStack != null) {
                    if (uploadParameter!=null){
                        if (uploadParameter.getFromType() == SlideManagerHornForPhone
                                .FromWhereData.FROM_DO_ONEPAGE_COUSRSE){
                            activityStack.finishUtil(PictureBooksDetailActivity.class);
                            return;
                        }
                        if (uploadParameter.isTempData()){
                            activityStack.finishUtil(TaskOrderDetailActivity.class);
                            return;
                        }
                    }
                    activityStack.finishUtil(PictureBooksDetailActivity.class);
                }
            }
        }
    }

    private void popSelectSendTypeDialog(final BookCatalog catalog, final CourseData courseData){
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(
                getActivity(),
                null,
                R.layout.layout_change_selected_icon,
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
                        if (teachingMaterialTypeRadioButton.isChecked()){
                            courseData.guidanceCardSendFlag = 1;
                        }
                        sendSelectResourceToSomeWhere(catalog);
                    }
                });
        TextView contentTextV = (TextView) messageDialog.getContentView().findViewById(R.id.contacts_dialog_content_title);
        contentTextV.setText(getString(R.string.str_upload_to_public_resource));
        studyCardTypeRadioButton = (RadioButton) messageDialog.getContentView().findViewById(R.id.rb_current_class);
        studyCardTypeRadioButton.setText(getString(R.string.make_task));
        teachingMaterialTypeRadioButton = (RadioButton) messageDialog.getContentView().findViewById(R.id.rb_all_Class);
        teachingMaterialTypeRadioButton.setText(getString(R.string.str_teaching_material));
        teachingMaterialTypeRadioButton.setTextColor(getResources().getColor(R.color.black));
        studyCardTypeRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    studyCardTypeRadioButton.setTextColor(getResources().getColor(R.color.text_green));
                    teachingMaterialTypeRadioButton.setTextColor(getResources().getColor(R.color.black));
                } else {
                    studyCardTypeRadioButton.setTextColor(getResources().getColor(R.color.black));
                    teachingMaterialTypeRadioButton.setTextColor(getResources().getColor(R.color.text_green));
                }
            }
        });
        messageDialog.show();
    }

    private void enterLessonListByCatalog(BookCatalog catalog) {
        Bundle args = getArguments();
        args.putString(CatalogLessonListActivity.EXTRA_BOOK_PRIMARY_KEY, this.bookPrimaryKey);
        args.putString(CatalogLessonListActivity.EXTRA_BOOK_ID, this.bookId);
        args.putString(CatalogLessonListActivity.EXTRA_SCHOOL_ID, this.schoolId);
        args.putString(CatalogLessonListActivity.EXTRA_BOOK_CATALOG_ID, catalog.getId());
        args.putString(CatalogLessonListActivity.EXTRA_BOOK_CATALOG_NAME, catalog.getName());
        Intent intent = new Intent(getActivity(), CatalogLessonListActivity.class);
        intent.putExtras(args);
        startActivity(intent);
    }

    private void enterMediaTypeListByCatalog(BookCatalog catalog) {
        Bundle args = getArguments();
        args.putString(CatalogLessonListActivity.EXTRA_BOOK_PRIMARY_KEY, this.bookPrimaryKey);
        args.putString(CatalogLessonListActivity.EXTRA_BOOK_ID, this.bookId);
        args.putString(CatalogLessonListActivity.EXTRA_SCHOOL_ID, this.schoolId);
        args.putString(CatalogLessonListActivity.EXTRA_BOOK_CATALOG_ID, catalog.getId());
        args.putString(CatalogLessonListActivity.EXTRA_BOOK_CATALOG_NAME, catalog.getName());
        args.putBoolean(MediaListFragment.EXTRA_IS_CLOUD, true);
        args.putBoolean(MediaListFragment.EXTRA_IS_REMOTE, true);
        MediaTypeListFragment fragment = new MediaTypeListFragment();
        fragment.setArguments(args);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.container, fragment, MediaTypeListFragment.TAG);
        ft.addToBackStack(null);
        ft.commit();
    }

    void uploadCourseToSChoolSpace(BookCatalog catalog) {
        UploadParameter uploadParameter = (UploadParameter) getArguments().getSerializable(UploadParameter.class.getSimpleName());
        if (uploadParameter == null) {
            return;
        }
        if (uploadParameter.getUploadSchoolInfo() != null) {
            uploadParameter.setOutlineId(getArguments().getString(Constants.EXTRA_BOOK_ID));
            uploadParameter.setSectionId(catalog.getId());
            if (this.bookSource == BookListActivity.PLATFORM_OUTLINE) {
                uploadParameter.setIsPmaterial(true);
            } else {
                uploadParameter.setIsPmaterial(false);
            }
            new UploadCourseHelper(getActivity()).uploadResource(uploadParameter, ShareType.SHARE_TYPE_PUBLIC_COURSE);
        }
    }

//    void uploadCourseToCloudResource(BookCatalog catalog) {
//        UploadParameter uploadParameter = (UploadParameter) getArguments().getSerializable("uploaParameter");
//        int uploadCourseType = getArguments().getInt("uploadCourseType");
//        if (uploadParameter == null) {
//            return;
//        }
//        uploadParameter.setOutlineId(getArguments().getString(Constants.EXTRA_BOOK_ID));
//        uploadParameter.setSectionId(catalog.getId());
//        new UploadCourseHelper(getActivity()).uploadResource(uploadParameter, uploadCourseType);
//    }

    private void collectBook() {
        if (getUserInfo() == null){
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        if (this.bookSource == Constants.SCHOOL_BOOK) {
            params.put("SchoolId", this.schoolId);
        }
        params.put("OutlineId", this.bookId);
        RequestHelper.RequestListener listener =
                new DefaultDataListener<DataModelResult>(DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() != null && getResult().isSuccess()) {
                            TipsHelper.showToast(getActivity(), R.string.collect_book_success);
                        }
                    }
                };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.COLLECT_BOOK_URL, params, listener);
    }

    private void shareBook() {
        String serverUrl = ServerUrl.SHARE_BOOK_URL;
        String url = null;
        if (bookSource == Constants.SCHOOL_BOOK) {
            url = serverUrl + String.format(
                    ServerUrl.SHARE_SCHOOL_BOOK_PARAMS, bookSource, bookId, schoolId);
        } else {
            url = serverUrl + String.format(
                    ServerUrl.SHARE_PLATFORM_BOOK_PARAMS, bookSource, bookId);
        }

        ShareInfo shareInfo = new ShareInfo();
        shareInfo.setTitle(bookName);
        if (bookSource == Constants.SCHOOL_BOOK) {
            shareInfo.setContent(schoolName);
        } else {
            shareInfo.setContent(getString(R.string.app_name));
        }
        shareInfo.setTargetUrl(url);
        UMImage umImage = null;
        if (!TextUtils.isEmpty(bookCover)) {
            umImage = new UMImage(getActivity(), AppSettings.getFileUrl(bookCover));
        } else {
            umImage = new UMImage(getActivity(), R.drawable.default_book_cover);
        }
        shareInfo.setuMediaObject(umImage);

        SharedResource resource = new SharedResource();
        resource.setShareUrl(serverUrl);
        resource.setSourceType(bookSource);
        resource.setId(bookId);
        resource.setTitle(bookName);
        resource.setThumbnailUrl(AppSettings.getFileUrl(bookCover));
        resource.setSchoolId(schoolId);
        resource.setType(SharedResource.RESOURCE_TYPE_HTML);
        resource.setFieldPatches(SharedResource.FIELD_PATCHES_BOOK_SHARE_URL);

        shareInfo.setSharedResource(resource);
        ShareUtils shareUtils = new ShareUtils(getActivity());
        shareUtils.share(getView(), shareInfo);
    }

    private void enterLogin() {
        Bundle args = new Bundle();
        args.putBoolean(AccountActivity.EXTRA_HAS_LOGINED, false);
        args.putBoolean(AccountActivity.EXTRA_ENTER_HOME_AFTER_LOGIN, false);
        Intent intent = new Intent(getActivity(), AccountActivity.class);
        intent.putExtras(args);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void publishResource(BookCatalog catalog) {

    }

    private void publishLocalResource(final BookCatalog catalog) {

    }

    protected class MyViewHolder extends ViewHolder {
        TextView titleView;
        ImageView indicatorView;
    }

    private class DefaultUploadListener<T> extends Listener<T> {
        @Override
        public void onPreExecute() {
            super.onPreExecute();
            showLoadingDialog();
        }

        @Override
        public void onFinish() {
            super.onFinish();
            dismissLoadingDialog();
        }

        @Override
        public void onSuccess(T o) {
            if(getActivity()==null)return;
            TipsHelper.showToast(getActivity(), R.string.upload_success);
            finish();
        }

        @Override
        public void onError(NetroidError error) {
            if(getActivity()==null)return;
            super.onError(error);
            TipsHelper.showToast(getActivity(), R.string.upload_failure);
        }
    }

}
