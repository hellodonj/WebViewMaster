package com.galaxyschool.app.wawaschool.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.galaxyschool.app.wawaschool.BookListActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.fragment.resource.NewResourceAdapterViewHelper;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.CourseInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfoListResult;
import com.galaxyschool.app.wawaschool.pojo.ResType;

import java.util.HashMap;
import java.util.Map;

public class SchoolLessonListFragment extends SchoolResourceListBaseFragment {

    public static final String TAG = SchoolLessonListFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_school_lesson_list, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    private void initViews() {
        TextView textView = ((TextView) findViewById(R.id.contacts_header_title));
        if (textView != null) {
            textView.setText(R.string.public_course);
        }

        View view = findViewById(R.id.contacts_search_list_item_layout);
        if (view != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enterSearchCourse();
                }
            });
        }
        ImageView imageView = (ImageView) findViewById(R.id.contacts_search_item_icon);
        if (imageView != null) {
            imageView.setImageResource(R.drawable.search_course_ico);
        }
        textView = (TextView) findViewById(R.id.contacts_search_item_title);
        if (textView != null) {
            textView.setText(R.string.search_course);
        }
        textView = (TextView) findViewById(R.id.contacts_search_item_subtitle);
        if (textView != null) {
            textView.setText(R.string.search_course_hint);
        }

        GridView listView = (GridView) findViewById(R.id.resource_list_view);
        if (listView != null) {
            AdapterViewHelper adapterViewHelper = new NewResourceAdapterViewHelper(
                    getActivity(), listView) {
                @Override
                public void loadData() {
                    loadResourceList();
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        return;
                    }
                    final NewResourceInfo data = (NewResourceInfo) holder.data;
                    if(data != null) {
                        int resType = data.getResourceType() % ResType.RES_TYPE_BASE;
                        if(resType == ResType.RES_TYPE_COURSE || resType == ResType.RES_TYPE_COURSE_SPEAKER
                            || resType == ResType.RES_TYPE_OLD_COURSE) {
                            CourseInfo courseInfo = data.getCourseInfo();
                            if (courseInfo != null) {
                                ActivityUtils.playOnlineCourse(getActivity(), courseInfo, false,
                                        null);
                            }
                        } else if(resType == ResType.RES_TYPE_ONEPAGE) {
                            ActivityUtils.openOnlineOnePage(getActivity(), data, false, null);
                        }
                    }
                }
            };
            setCurrAdapterViewHelper(listView, adapterViewHelper);
        }
    }

    @Override
    protected void loadResourceList(String keyword) {
        keyword = keyword.trim();
        if (!keyword.equals(this.keyword)) {
            getCurrAdapterViewHelper().clearData();
            getPageHelper().clear();
        }
        this.keyword = keyword;
        Map<String, Object> params = new HashMap();
        params.put("SchoolId", schoolId);
        params.put("Keyword", keyword);
        params.put("Pager", getPageHelper().getFetchingPagerArgs());
        DefaultPullToRefreshDataListener listener =
                new DefaultPullToRefreshDataListener<NewResourceInfoListResult>(
                        NewResourceInfoListResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                if (getResult() == null || !getResult().isSuccess()
                        || getResult().getModel() == null) {
                    return;
                }
                updateResourceListView(getResult());
            }
        };
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_SCHOOL_LESSON_LIST_URL, params, listener);
    }

    private void enterSearchCourse() {
        Bundle args = new Bundle();
        args.putInt(BookListActivity.EXTRA_MODE, BookListActivity.REVIEW_MODE);
        args.putInt(BookListActivity.EXTRA_BOOK_SOURCE, BookListActivity.SCHOOL_BOOK);
        args.putString(BookListActivity.EXTRA_SCHOOL_ID, this.schoolId);
        args.putString(BookListActivity.EXTRA_SCHOOL_NAME, this.schoolName);
        Intent intent = new Intent(getActivity(), BookListActivity.class);
        intent.putExtras(args);
        startActivity(intent);
    }

}
