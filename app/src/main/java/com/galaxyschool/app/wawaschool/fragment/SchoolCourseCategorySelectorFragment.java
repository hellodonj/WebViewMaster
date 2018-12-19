package com.galaxyschool.app.wawaschool.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.CallbackListener;
import com.galaxyschool.app.wawaschool.common.NoteHelper;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.category.Category;
import com.galaxyschool.app.wawaschool.fragment.category.CategorySelectorFragment;
import com.galaxyschool.app.wawaschool.fragment.category.CategorySelectorView;
import com.galaxyschool.app.wawaschool.fragment.category.CategoryValue;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.*;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseUploadResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchoolCourseCategorySelectorFragment extends CategorySelectorFragment {

    public static final String TAG = SchoolCourseCategorySelectorFragment.class.getSimpleName();

    public interface Constants extends CategorySelectorFragment.Constants {
        public static final String EXTRA_MODE = "mode";
        public static final String EXTRA_SCHOOL_ID = "schoolId";

        public static final int UPLOAD_MODE = 1;
        public static final int REVIEW_MODE = 2;

        public static final int SCHOOL_CATEGORY_STAGE = 1;
        public static final int SCHOOL_CATEGORY_GRADE = 2;
        public static final int SCHOOL_CATEGORY_SUBJECT = 3;
    }

    private int mode;
    private String schoolId;
    private UploadParameter uploadParameter;
    private NoteInfo noteInfo;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getUserVisibleHint()) {
            if (allCategories == null) {
                loadCategoryList();
            }
        }
    }

    private void init() {
        mode = getArguments().getInt(Constants.EXTRA_MODE);
        schoolId = getArguments().getString(Constants.EXTRA_SCHOOL_ID);
        uploadParameter = (UploadParameter) getArguments().getSerializable(
                UploadParameter.class.getSimpleName());
        noteInfo = getArguments().getParcelable(NoteInfo.class.getSimpleName());

        initViews();
    }

    private void initViews() {
        TextView textView = ((TextView) findViewById(R.id.contacts_header_title));
        if (textView != null) {
            textView.setText(R.string.send_to_cloud_classroom);
        }

        categoryView = (CategorySelectorView) getView().findViewById(
                R.id.category_selector_view);
        if (categoryView != null) {
            categoryView.getConfirmButton().setText(R.string.send);
            categoryView.setFillWithDefault(true);
            categoryView.setVisibility(View.GONE);
        }
    }

    private void loadCategoryList() {
        Map<String, Object> params = new HashMap();
        params.put("Type", this.mode);
        if (this.mode == Constants.REVIEW_MODE) {
            params.put("SchoolId", this.schoolId);
        }
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
        listener.setShowLoading(true);
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
        categoryView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCategorySelect(List<Category> categories) {
        selectedCategories = categories;

        publishResource();
    }

    private void publishResource() {
        if (uploadParameter == null) {
            return;
        }

        Category stageCategory = null;
        Category gradeCategory = null;
        Category subjectCategory = null;
        for (Category category : selectedCategories) {
            if (category.getType() == Constants.SCHOOL_CATEGORY_STAGE) {
                stageCategory = category;
            } else if (category.getType() == Constants.SCHOOL_CATEGORY_GRADE) {
                gradeCategory = category;
            } else if (category.getType() == Constants.SCHOOL_CATEGORY_SUBJECT) {
                subjectCategory = category;
            }
        }

        final String stageId = stageCategory != null ?
                stageCategory.getCurrValue().getId() : "";
        final String gradeId = gradeCategory != null ?
                gradeCategory.getCurrValue().getId() : "";
        final String subjectId = subjectCategory != null ?
                subjectCategory.getCurrValue().getId() : "";
        CourseData courseData = uploadParameter.getCourseData();
        if (courseData != null) {
            courseData.levelId = stageId;
            courseData.gradeId = gradeId;
            courseData.subjectId = subjectId;
            NoteHelper.updateNoteToSchoolCourse(getActivity(), getUserInfo(),
                    courseData, schoolId);
            return;
        }

        if (noteInfo == null) {
            return;
        }

        final long dateTime = noteInfo.getDateTime();
        showLoadingDialog(getString(R.string.cs_loading_wait), true);
        NoteHelper.uploadNote(getActivity(), uploadParameter, dateTime,
                new CallbackListener() {
            @Override
            public void onBack(Object result) {
                dismissLoadingDialog();
                CourseUploadResult uploadResult = (CourseUploadResult) result;
                if (uploadResult != null && uploadResult.code != 0) {
                    TipsHelper.showToast(getActivity(), R.string.send_failure);
                    return;
                }
                if (uploadResult.data != null && uploadResult.data.size() > 0) {
                    CourseData courseData = uploadResult.data.get(0);
                    if (courseData != null) {
                        courseData.levelId = stageId;
                        courseData.gradeId = gradeId;
                        courseData.subjectId = subjectId;
                        NoteHelper.updateNoteToSchoolCourse(getActivity(),
                                getUserInfo(), courseData, schoolId);
                    }
                }
            }
        });
    }

}
