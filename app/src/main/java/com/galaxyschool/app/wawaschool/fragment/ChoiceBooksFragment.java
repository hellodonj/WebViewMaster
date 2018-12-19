package com.galaxyschool.app.wawaschool.fragment;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.BookDetailActivity;
import com.galaxyschool.app.wawaschool.CustomerServiceActivity;
import com.galaxyschool.app.wawaschool.NewBookStoreActivity;
import com.galaxyschool.app.wawaschool.PictureBooksActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.DensityUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.fragment.resource.NewResourcePadAdapterViewHelper;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfoListResult;
import com.galaxyschool.app.wawaschool.pojo.SchoolCourseInfo;
import com.galaxyschool.app.wawaschool.pojo.SchoolCourseInfoListResult;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;
import com.galaxyschool.app.wawaschool.views.MyGridView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2016/9/28.
 */

public class ChoiceBooksFragment extends ContactsListFragment {
    public static final String TAG = ChoiceBooksFragment.class.getSimpleName();

    public static final int MAX_SCHOOL_NUM = 6;
    public static final int MAX_SCHOOL_PER_ROW = 3;
    public static final int MAX_PIC_BOOKS_NUM = 4;
    public static final int MAX_PIC_BOOKS_PER_ROW = 2;

    private String schoolGridViewTag;
    private String picBooksGridViewTag;
    private SchoolInfo schoolInfo;
    private SchoolCourseInfo schoolCourseInfo;
    private  boolean isShowPicBooks;
    public static final String  FROM_TYPE="from_type";
    public static final int FROM_HAOM_PAGE=0;//来自学校空间
    public static final int NOT_FROM_HAOM_PAGE=1;//
    private int fromType=FROM_HAOM_PAGE;
    boolean isPick;
    public int taskType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_choice_books, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        loadSchools();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initViews () {
        LinearLayout linearLayout= (LinearLayout) findViewById(R.id.linearLayout);
        if (linearLayout!=null){
            linearLayout.setBackgroundColor(getResources().getColor(R.color.text_white));
        }
        if (getArguments() != null) {
            schoolInfo = (SchoolInfo) getArguments().getSerializable(SchoolInfo.class.getSimpleName());
            fromType = getArguments().getInt(FROM_TYPE);
            isPick = getArguments().getBoolean(ActivityUtils.EXTRA_IS_PICK);
            taskType = getArguments().getInt(ActivityUtils.EXTRA_TASK_TYPE);
        }
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if(isPick){
            String headerTitle=null;
            if(taskType == StudyTaskType.RETELL_WAWA_COURSE) {
                headerTitle = getString(R.string.n_create_task, getString(R.string.retell_course));
            }else if(taskType == StudyTaskType.WATCH_WAWA_COURSE){
                headerTitle = getString(R.string.n_create_task, getString(R.string.look_through_courseware));
            }else if (taskType==StudyTaskType.INTRODUCTION_WAWA_COURSE){
                headerTitle=getString(R.string.appoint_course_no_point);
            }else if (taskType==StudyTaskType.TASK_ORDER){
                headerTitle=getString(R.string.do_task);
            }
            textView.setText(headerTitle);
        }else {
            textView.setText(R.string.choice_books);
        }
        textView = (TextView) findViewById(R.id.contacts_header_right_btn);
        if (isPick) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setText(getString(R.string.open_consultion));
            textView.setVisibility(View.VISIBLE);
            textView.setOnClickListener(this);
        }
        LinearLayout layout = (LinearLayout) findViewById(R.id.pic_book_more_layout);
        layout.setOnClickListener(this);
        if (isPick) {
            ScrollView scrollView= (ScrollView) findViewById(R.id.choice_scrollview_fragment);
            scrollView.setVisibility(View.GONE);
            ListView listView = (ListView) findViewById(R.id.listview);
            //增加距离顶部栏10dp的灰色背景
            View  view=findViewById(R.id.ten_background);
            if (view!=null){
                view.setVisibility(View.VISIBLE);
            }
            if (listView!=null) {
                listView.setVisibility(View.VISIBLE);
                listView.setDivider(new ColorDrawable(getResources().getColor(R.color.main_bg_color)));
                listView.setDividerHeight(1);
                AdapterViewHelper helper = new AdapterViewHelper(getActivity(), listView, R
                        .layout.media_type_list_item_model) {
                    @Override
                    public void loadData() {
                        loadSchools();
                    }
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        SchoolCourseInfo data = (SchoolCourseInfo) getDataAdapter().getItem(position);
                        if (data == null) {
                            return view;
                        }
                        ViewHolder holder = (ViewHolder) view.getTag();
                        if (holder == null) {
                            holder = new ViewHolder();
                        }
                        holder.data = data;
                        ImageView imageView = (ImageView) view.findViewById(R.id.thumbnail);
                        if (imageView != null) {
                            getThumbnailManager().displayUserIconWithDefault(
                                    AppSettings.getFileUrl(data.getCourseLogoUrl()), imageView,
                                    R.drawable.default_school_icon);
                            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
                                    imageView.getLayoutParams();
                            params.width = DensityUtils.dp2px(getActivity(), 60);
                            params.height = DensityUtils.dp2px(getActivity(), 60);
                            params.rightMargin = 0;
                            imageView.setLayoutParams(params);
                        }
                        TextView textView = (TextView) view.findViewById(R.id.name);
                        if (textView != null) {
                            textView.setText(data.getCourseName());
                            textView.setGravity(Gravity.CENTER);
                        }
                        ImageView  arrowRight= (ImageView) view.findViewById(R.id.arrow_right);
                        if (arrowRight!=null){
                            arrowRight.setVisibility(View.GONE);
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
                        SchoolCourseInfo data = (SchoolCourseInfo) holder.data;
                        if(isPick) {
                            if (data.isIsPictureBook()) {
                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                PictureBooksFragment fragment = new PictureBooksFragment();
                                Bundle args = getArguments();
                                int taskType = getArguments().getInt(ActivityUtils.EXTRA_TASK_TYPE);
                                args.putBoolean(ActivityUtils.EXTRA_IS_PICK, isPick);
                                args.putInt(ActivityUtils.EXTRA_TASK_TYPE, taskType);
                                args.putString(PictureBooksDetailFragment.Constants.EXTRA_SCHOOL_ID,
                                        data.getSchoolId());
                                args.putString(PictureBooksDetailFragment.Constants.EXTRA_FEE_SCHOOL_ID,
                                        data.getCourseId());
                                fragment.setArguments(args);
                                ft.replace(R.id.activity_body, fragment, PictureBooksFragment.TAG);
                                ft.addToBackStack(null);
                                ft.commit();
                            } else {
                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                BookStoreListFragment fragment = new BookStoreListFragment();
                                Bundle args = getArguments();
                                int taskType = getArguments().getInt(ActivityUtils.EXTRA_TASK_TYPE);
                                args.putBoolean(ActivityUtils.EXTRA_IS_PICK, isPick);
                                args.putInt(ActivityUtils.EXTRA_TASK_TYPE, taskType);
                                args.putString(BookDetailActivity.SCHOOL_ID, data.getCourseId());
                                args.putString(BookDetailActivity.ORIGIN_SCHOOL_ID, data.getSchoolId());
                                fragment.setArguments(args);
                                ft.replace(R.id.activity_body, fragment, BookStoreListFragment.TAG);
                                ft.addToBackStack(null);
                                ft.commit();
                            }
                        }

                        }
                };
                setCurrAdapterViewHelper(listView, helper);
            }

        } else {
            MyGridView gridView = (MyGridView) findViewById(R.id.schools_gridview);
            if (gridView != null) {
                gridView.setNumColumns(MAX_SCHOOL_PER_ROW);
                AdapterViewHelper gridViewHelper = new AdapterViewHelper(getActivity(),
                        gridView, R.layout.book_store_main_item) {
                    @Override
                    public void loadData() {
                        loadSchools();
                    }

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        SchoolCourseInfo data = (SchoolCourseInfo) getDataAdapter().getItem(position);
                        if (data == null) {
                            return view;
                        }
                        ViewHolder holder = (ViewHolder) view.getTag();
                        if (holder == null) {
                            holder = new ViewHolder();
                        }
                        holder.data = data;
                        FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.item_book_cover);
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
                                frameLayout.getLayoutParams();
                        params.width = DensityUtils.dp2px(getActivity(), 65);
                        params.height = DensityUtils.dp2px(getActivity(), 65);
                        frameLayout.setLayoutParams(params);

                        ImageView imageView = (ImageView) view.findViewById(R.id.item_shelf);
                        if (imageView != null) {
                            imageView.setVisibility(View.GONE);
                        }
                        //开通标识
                        imageView = (ImageView) view.findViewById(R.id.item_open_status);
                        if (imageView != null){
                            if(data.isIsPictureBook()){
                                imageView.setVisibility(View.GONE);
                            }else{
                                if (!data.isOpenCourse()){
                                    //未开通
                                    imageView.setVisibility(View.VISIBLE);
                                }else {
                                    imageView.setVisibility(View.GONE);
                                }
                            }
                        }
                        imageView = (ImageView) view.findViewById(R.id.item_icon);
                        if (imageView != null) {
                            getThumbnailManager().displayThumbnailWithDefault(
                                    AppSettings.getFileUrl(AppSettings.getFileUrl(data.getCourseLogoUrl())),
                                    imageView,
                                    R.drawable.default_school_icon);
                        }
                        TextView textView = (TextView) view.findViewById(R.id.item_title);
                        if (textView != null) {
                            textView.setText(data.getCourseName());
                        }
                        ImageView imageView_desc = (ImageView) view.findViewById(R.id.item_description);
                        if (imageView_desc != null) {
                            imageView_desc.setVisibility(View.GONE);
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
                        SchoolCourseInfo data = (SchoolCourseInfo) holder.data;
                        Intent intent = new Intent(getActivity(), NewBookStoreActivity.class);
                        Bundle args = new Bundle();
                        args.putString(NewBookStoreActivity.SCHOOL_ID, data.getCourseId());
                        args.putString(NewBookStoreActivity.ORIGIN_SCHOOL_ID, data.getSchoolId());
                        args.putBoolean(ActivityUtils.IS_PIC_BOOK_CHOICE, true);
                        intent.putExtras(args);
                        startActivity(intent);
                    }
                };
                schoolGridViewTag = String.valueOf(gridView.getId());
                addAdapterViewHelper(schoolGridViewTag, gridViewHelper);
            }

            gridView = (MyGridView) findViewById(R.id.pic_books_gridview);
            if (gridView != null) {
                if (gridView != null) {
                    gridView.setNumColumns(MAX_PIC_BOOKS_PER_ROW);
                    AdapterViewHelper adapterViewHelper = new NewResourcePadAdapterViewHelper(
                            getActivity(), gridView) {
                        @Override
                        public void loadData() {
                            loadPicBooks();
                        }

                        @Override
                        public void onItemClick(AdapterView parent, View view, int position, long id) {
                            ViewHolder holder = (ViewHolder) view.getTag();
                            if (holder == null) {
                                return;
                            }
                            NewResourceInfo data = (NewResourceInfo) holder.data;
                            if (data != null) {
                                data.setIsFromSchoolResource(true);
                                if (schoolCourseInfo != null) {
//                                ActivityUtils.openPictureDetailActivity(getActivity(), data,
//                                        schoolCourseInfo.getSchoolId(), schoolCourseInfo.getCourseId());
                                    ActivityUtils.openPictureDetailActivity(getActivity(), data);
                                }
                            }
                        }
                    };
                    picBooksGridViewTag = String.valueOf(gridView.getId());
                    addAdapterViewHelper(picBooksGridViewTag, adapterViewHelper);
                }
            }
        }
    }
    private void loadSchools() {
        if (schoolInfo==null){
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("SchoolId", schoolInfo.getSchoolId());
        params.put("Pager", getPageHelper().getFetchingPagerArgs());
        RequestHelper.RequestDataResultListener listener = new RequestHelper
                .RequestDataResultListener(getActivity(), SchoolCourseInfoListResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                SchoolCourseInfoListResult result = (SchoolCourseInfoListResult)getResult();
                if (result == null || !result.isSuccess()
                        || result.getModel() == null) {
                    return;
                }
                updateSchoolListView(result);
            }
        };
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.GET_CHOICE_SCHOOL_LIST_URL,
                params, listener);
    }

    private void updateSchoolListView(SchoolCourseInfoListResult result) {
        if (getPageHelper().isFetchingPageIndex(result.getModel().getPager())) {
            List<SchoolCourseInfo> list = result.getModel().getData();
            if (list == null || list.size() <= 0) {
                if (getCurrAdapterViewHelper().hasData()) {
                    getCurrAdapterViewHelper().clearData();
                }
                TipsHelper.showToast(getActivity(),
                        getString(R.string.no_schools));
                return;
            } else {
//                if (list.size() > MAX_SCHOOL_NUM) {
//                    list = list.subList(0, MAX_SCHOOL_NUM);
//                }
                isShowPicBooks = isShowPicBooks(list);
                if(isShowPicBooks&&!isPick) {
                    loadPicBooks();
                    LinearLayout layout = (LinearLayout) findViewById(R.id.pic_book_more_layout);
                    layout.setVisibility(View.VISIBLE);
                }else{
                    LinearLayout layout = (LinearLayout) findViewById(R.id.pic_book_more_layout);
                    layout.setVisibility(View.GONE);
                }
                if (!isPick) {
                    Iterator<SchoolCourseInfo> iterator = list.iterator();
                    while (iterator.hasNext()) {
                        SchoolCourseInfo schoolCourseInfo = iterator.next();
                        if (schoolCourseInfo.isIsPictureBook()) {
                            iterator.remove();
                        }
                    }
                }else {
                    //选择的时候，过滤掉未开通的课件。保留绘本屋。
                    Iterator<SchoolCourseInfo> iterator = list.iterator();
                    while (iterator.hasNext()) {
                        SchoolCourseInfo schoolCourseInfo = iterator.next();
                        if (!schoolCourseInfo.isOpenCourse() &&
                                !schoolCourseInfo.isIsPictureBook()) {
                            iterator.remove();
                        }
                    }
                }
                if (!isPick) {
                    getAdapterViewHelper(schoolGridViewTag).setData(list);
                }else {
                    getCurrAdapterViewHelper().setData(list);
                }
            }
        }
    }

    private SchoolCourseInfo getPicBookSchoolCourseInfo(List<SchoolCourseInfo> data) {
        SchoolCourseInfo result = null;
        if(data != null && data.size() > 0) {
            for(SchoolCourseInfo info : data) {
                if(info != null && info.isIsPictureBook()) {
                    result = info;
                    break;
                }
            }
        }
        return result;
    }

    private boolean isShowPicBooks(List<SchoolCourseInfo> data) {
        schoolCourseInfo= getPicBookSchoolCourseInfo(data);
        return schoolCourseInfo != null;
    }


    private void loadPicBooks() {
        Map<String, Object> params = new HashMap();
        params.put("Pager", getPageHelper().getFetchingPagerArgs());
        if (schoolCourseInfo != null) {
            params.put("SchoolId", schoolCourseInfo.getCourseId());
        }
        DefaultPullToRefreshDataListener listener =
                new DefaultPullToRefreshDataListener<NewResourceInfoListResult>(
                        NewResourceInfoListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        NewResourceInfoListResult result = getResult();
                        if (result == null || !result.isSuccess()
                                || result.getModel() == null) {
                            return;
                        }
                        updatePicBookListView(result);
                    }
                };
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.PICBOOKS_GET_PICBOOKLIST_URL, params, listener);
    }

    private void updatePicBookListView(NewResourceInfoListResult result) {
        if (getPageHelper().isFetchingPageIndex(result.getModel().getPager())) {
            List<NewResourceInfo> list = result.getModel().getData();
            if (getPageHelper().isFetchingFirstPage()) {
                if (!isPick) {
                    if (getAdapterViewHelper(picBooksGridViewTag).hasData()) {
                        getAdapterViewHelper(picBooksGridViewTag).clearData();
                    }
                }else {
                   if (getCurrAdapterViewHelper().hasData()){
                       getCurrAdapterViewHelper().clearData();
                   }
                }
            }
            if (list == null || list.size() <= 0) {
                if (getPageHelper().isFetchingFirstPage()) {
                    TipsHelper.showToast(getActivity(),
                            getString(R.string.no_excellent_pic_book));
                } else {
                    TipsHelper.showToast(getActivity(),
                            getString(R.string.no_more_data));
                }
                return;
            }
            if (list.size() > MAX_PIC_BOOKS_NUM) {
                list = list.subList(0, MAX_PIC_BOOKS_NUM);
            }
            getPageHelper().updateByPagerArgs(result.getModel().getPager());
            getPageHelper().setCurrPageIndex(
                    getPageHelper().getFetchingPageIndex());
            if (!isPick) {
                if (getAdapterViewHelper(picBooksGridViewTag).hasData()) {
                    getAdapterViewHelper(picBooksGridViewTag).getData().addAll(list);
                    getAdapterViewHelper(picBooksGridViewTag).update();
                } else {
                    getAdapterViewHelper(picBooksGridViewTag).setData(list);
                }
            }else {
                if (getCurrAdapterViewHelper().hasData()) {
                    getCurrAdapterViewHelper().getData().addAll(list);
                    getCurrAdapterViewHelper().update();
                } else {
                    getCurrAdapterViewHelper().setData(list);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.pic_book_more_layout) {
            if(!isShowPicBooks) {
                return;
            }
            Intent intent = new Intent(getActivity(), PictureBooksActivity.class);
            intent.putExtra(PictureBooksDetailFragment.Constants.EXTRA_SCHOOL_ID,
                    schoolCourseInfo.getSchoolId());
            intent.putExtra(PictureBooksDetailFragment.Constants.EXTRA_FEE_SCHOOL_ID,
                    schoolCourseInfo.getCourseId());
            startActivity(intent);
        } else  if(v.getId()==R.id.contacts_header_right_btn){
            CustomerServiceActivity.start(getActivity(), CustomerServiceActivity.SOURCE_TYPE_OPEN_CONSULTION);
        }else if(v.getId() == R.id.contacts_header_left_btn){
            if(isPick) {
                popStack();
            } else {
                super.onClick(v);
            }
        } else {
            super.onClick(v);
        }
    }
}
