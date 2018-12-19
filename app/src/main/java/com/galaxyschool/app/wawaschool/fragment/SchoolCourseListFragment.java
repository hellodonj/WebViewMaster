package com.galaxyschool.app.wawaschool.fragment;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.Note.MediaPaperActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.SchoolCourseCategorySelectorActivity;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.DateUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.category.Category;
import com.galaxyschool.app.wawaschool.fragment.category.CategorySelectorView;
import com.galaxyschool.app.wawaschool.fragment.category.CategoryValue;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.*;
import com.galaxyschool.app.wawaschool.pojo.weike.NoteOpenParams;
import com.galaxyschool.app.wawaschool.views.sortlistview.ClearEditText;
import com.lqwawa.libs.mediapaper.MediaPaper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchoolCourseListFragment extends SchoolResourceListFragment
        implements CategorySelectorView.OnCategorySelectListener {

    public static final String TAG = SchoolCourseListFragment.class.getSimpleName();

    public interface Constants extends SchoolResourceListFragment.Constants {
        public static final int UPLOAD_MODE =
                SchoolCourseCategorySelectorActivity.UPLOAD_MODE;
        public static final int REVIEW_MODE =
                SchoolCourseCategorySelectorActivity.REVIEW_MODE;

        public static final int SCHOOL_CATEGORY_STAGE =
                SchoolCourseCategorySelectorActivity.SCHOOL_CATEGORY_STAGE;
        public static final int SCHOOL_CATEGORY_GRADE =
                SchoolCourseCategorySelectorActivity.SCHOOL_CATEGORY_GRADE;
        public static final int SCHOOL_CATEGORY_SUBJECT =
                SchoolCourseCategorySelectorActivity.SCHOOL_CATEGORY_SUBJECT;
    }

    private View filterLayout;
    private CategorySelectorView categoryView;
    private List<Category> allCategories;
    private List<Category> selectedCategories;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_school_course_list, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (allCategories == null) {
            loadCategoryList();
        }
    }

    private void initViews() {
        TextView textView = ((TextView) findViewById(R.id.contacts_header_title));
        if (textView != null) {
            textView.setText(R.string.cloud_classroom);
        }

        ClearEditText editText = (ClearEditText) findViewById(R.id.search_keyword);
        if (editText != null) {
            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        showCategoryView(false);
                        hideSoftKeyboard(getActivity());
                        loadResourceList();
                        return true;
                    }
                    return false;
                }
            });
        }

        View view = findViewById(R.id.search_btn);
        if (view != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCategoryView(false);
                    hideSoftKeyboard(getActivity());
                    loadResourceList();
                }
            });
        }

        view = findViewById(R.id.category_filter_layout);
        if (view != null) {
            view.setOnClickListener(this);
//            view.setVisibility(View.GONE);
        }
        this.filterLayout = view;

        this.categoryView = (CategorySelectorView) findViewById(R.id.category_selector_view);
        if (this.categoryView != null) {
            this.categoryView.setOnCategorySelectListener(this);
            this.categoryView.setFillWithDefault(true);
        }
    }

    private void selectFilterIndicatorView(boolean selected) {
        ImageView imageView = (ImageView) findViewById(R.id.category_filter_indicator);
        if (imageView != null) {
            imageView.setImageResource(selected ?
                    R.drawable.arrow_up_ico : R.drawable.arrow_down_ico);
        }
    }

    private void loadCategoryList() {
        Map<String, Object> params = new HashMap();
        params.put("Type", Constants.UPLOAD_MODE);
        params.put("SchoolId", this.schoolId);
        DefaultDataListener listener =
                new DefaultDataListener<SchoolCourseCategorySetResult>(
                        SchoolCourseCategorySetResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        SchoolCourseCategorySetResult result = getResult();
                        if (result == null || !result.isSuccess()
                                || result.getModel() == null) {
                            return;
                        }
                        updateCategoryListView(result);
                    }
                };
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_SCHOOL_COURSE_CATEGORY_URL, params, listener);
    }

    private void updateCategoryListView(SchoolCourseCategorySetResult result) {
        SchoolCourseCategorySet set = result.getModel().getData();
        if (set == null) {
            return;
        }

        List<Category> categoryList = new ArrayList();
        Category cat = null;
        CategoryValue value = null;
        List<SchoolStage> stageList = set.getLevelList();
        cat = new Category();
        cat.setType(Constants.SCHOOL_CATEGORY_STAGE);
        cat.setName(getString(R.string.stage));
        if (stageList != null && stageList.size() > 0) {
            cat.setAllValues(new ArrayList());
            for (SchoolStage obj : stageList) {
                value = new CategoryValue();
                value.setId(obj.getLevelId());
                value.setValue(obj.getLevelName());
                cat.getAllValues().add(value);
            }
            categoryList.add(cat);
        }

        List<SchoolGrade> gradeList = set.getGradeList();
        cat = new Category();
        cat.setType(Constants.SCHOOL_CATEGORY_GRADE);
        cat.setName(getString(R.string.grade));
        if (gradeList != null && gradeList.size() > 0) {
            cat.setAllValues(new ArrayList());
            for (SchoolGrade obj : gradeList) {
                value = new CategoryValue();
                value.setId(obj.getGradeId());
                value.setValue(obj.getGradeName());
                cat.getAllValues().add(value);
            }
            categoryList.add(cat);
        }

        List<SchoolSubject> subjectList = set.getSubjectList();
        cat = new Category();
        cat.setType(Constants.SCHOOL_CATEGORY_SUBJECT);
        cat.setName(getString(R.string.subject));
        if (subjectList != null && subjectList.size() > 0) {
            cat.setAllValues(new ArrayList());
            for (SchoolSubject obj : subjectList) {
                value = new CategoryValue();
                value.setId(obj.getSubjectId());
                value.setValue(obj.getSubjectName());
                cat.getAllValues().add(value);
            }
            categoryList.add(cat);
        }

        allCategories = categoryList;
        categoryView.setAllCategories(categoryList);
        categoryView.setVisibility(filterLayout.isSelected() ? View.VISIBLE : View.GONE);
        selectFilterIndicatorView(filterLayout.isSelected());
    }

    @Override
    public void onCategorySelect(List<Category> categories) {
        this.selectedCategories = categories;
        showCategoryView(false);
        listView.setVisibility(View.VISIBLE);
        newBtn.setVisibility(isTeacher ? View.VISIBLE : View.GONE);
        getPageHelper().clear();
        getCurrAdapterViewHelper().clearData();
        loadResourceList();
    }

    private void showCategoryView(boolean show) {
        filterLayout.setSelected(show);
        selectFilterIndicatorView(show);
        categoryView.setVisibility(show ? View.VISIBLE : View.GONE);
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
        if (selectedCategories != null && selectedCategories.size() > 0) {
            for (Category category : selectedCategories) {
                if (category.getType() == Constants.SCHOOL_CATEGORY_STAGE) {
                    params.put("LevelId", category.getCurrValue().getId());
                } else if (category.getType() == Constants.SCHOOL_CATEGORY_GRADE) {
                    params.put("GradeId", category.getCurrValue().getId());
                } else if (category.getType() == Constants.SCHOOL_CATEGORY_SUBJECT) {
                    params.put("SubjectId", category.getCurrValue().getId());
                }
            }
        }
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
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.GET_SCHOOL_COURSE_LIST_URL,
                params, listener);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.category_filter_layout) {
            v.setSelected(!v.isSelected());
            if(v.isSelected()){
                listView.setVisibility(View.INVISIBLE);
                newBtn.setVisibility(View.GONE);
            }else{
                listView.setVisibility(View.VISIBLE);
                newBtn.setVisibility(isTeacher ? View.VISIBLE : View.GONE);
            }
            selectFilterIndicatorView(v.isSelected());
            if (allCategories == null) {
                loadCategoryList();
            } else {
                this.categoryView.setAllCategories(this.allCategories);
                this.categoryView.setVisibility(v.isSelected() ? View.VISIBLE : View.GONE);
            }
        } else {
            super.onClick(v);
        }
    }

    @Override
    protected void createNewResource() {
        long dateTime = System.currentTimeMillis();
        File noteFile = new File(Utils.NOTE_FOLDER, String.valueOf(dateTime));
        String dateTimeStr = DateUtils.transferLongToDate("yyyy-MM-dd HH:mm:ss", dateTime);
        NoteOpenParams params = new NoteOpenParams(noteFile.getPath(), dateTimeStr,
                MediaPaperActivity.OPEN_TYPE_EDIT, MediaPaper.PAPER_TYPE_TIEBA, null,
                MediaPaperActivity.SourceType.SCHOOL_SPACE, false);
        params.schoolId = schoolId;
        ActivityUtils.openLocalNote(getActivity(), params, 0);
    }

}
