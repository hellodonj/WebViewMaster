package com.galaxyschool.app.wawaschool.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.category.Category;
import com.galaxyschool.app.wawaschool.fragment.category.CategorySelectorFragment;
import com.galaxyschool.app.wawaschool.fragment.category.CategorySelectorView;
import com.galaxyschool.app.wawaschool.fragment.category.CategoryValue;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.*;
import com.galaxyschool.app.wawaschool.views.ContactsInputBoxDialog;

import java.util.*;

public class ContactsClassCategorySelectorFragment extends CategorySelectorFragment
        implements CategorySelectorView.OnCategoryValueSelectListener {

    public static final String TAG = ContactsClassCategorySelectorFragment.class.getSimpleName();

    public interface Constants extends CategorySelectorFragment.Constants {
        String EXTRA_SCHOOL_ID = "schoolId";
        String EXTRA_SCHOOL_NAME = "schoolName";
        int SCHOOL_CATEGORY_SCHOOL = 1;
        int SCHOOL_CATEGORY_STAGE = 2;
        int SCHOOL_CATEGORY_GRADE = 3;
        int SCHOOL_CATEGORY_CLASS = 4;
    }

    protected ContactsSchoolListResult schoolListResult;
    protected ContactsClassCategorySetResult classCategoryResult;
    protected String schoolId;
    protected String schoolName;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getUserVisibleHint()) {
            loadSchoolData();
//            if (schoolListResult == null) {
//                loadSchools();
//            }
//            if (classCategoryResult == null) {
//                loadCategoryList();
//            }
        }
    }

    private void init() {
        Bundle args = getArguments();
        if (args != null) {
            this.schoolId = args.getString(Constants.EXTRA_SCHOOL_ID);
            this.schoolName = args.getString(Constants.EXTRA_SCHOOL_NAME);
        }

//        if (!TextUtils.isEmpty(this.schoolId)) {
//            schoolListResult = new ContactsSchoolListResult();
//            schoolListResult.setModel(new ContactsSchoolListResult.ContactsSchoolListModel());
//            ContactsSchoolInfo schoolInfo = new ContactsSchoolInfo();
//            schoolInfo.setSchoolId(this.schoolId);
//            schoolInfo.setSchoolName(this.schoolName);
//            List<ContactsSchoolInfo> schoolList = new ArrayList();
//            schoolList.add(schoolInfo);
//            schoolListResult.getModel().setSchoolList(schoolList);
//        }

        initViews();
    }

    private void initViews() {
        categoryView = (CategorySelectorView) getView().findViewById(R.id.category_selector_view);
        if (categoryView != null) {
            categoryView.getConfirmButton().setText(R.string.confirm);
            categoryView.setFillWithDefaultCategory(false);
            categoryView.setFillWithDefaultValue(true);
            categoryView.setDefaultPosition(CategorySelectorView.DefaultPosition.LAST);
            categoryView.setDefaultName(getString(R.string.customize));
            categoryView.setNotifyAllSelectedValues(true);
            categoryView.setVisibility(View.GONE);
            categoryView.setOnCategoryValueSelectListener(this);
        }
    }

    protected void loadSchools() {
        if (getUserInfo() == null){
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("Role", getUserInfo().getRoles());
        DefaultListener listener =
                new DefaultListener<ContactsSchoolListResult>(
                        ContactsSchoolListResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if(getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                ContactsSchoolListResult result = getResult();
                if (result == null || !result.isSuccess()
                        || result.getModel() == null) {
                    return;
                }
                schoolListResult = result;
                List<ContactsSchoolInfo> schoolList = schoolListResult.getModel().getSchoolList();
                if (schoolList != null && schoolList.size() > 0) {
                    ContactsSchoolInfo schoolInfo = null;
                    Iterator<ContactsSchoolInfo> iterator = schoolList.iterator();
                    while (iterator.hasNext()) {
                        schoolInfo = iterator.next();
                        if (!schoolInfo.isTeacher()) {
                            iterator.remove();
                        }
                    }
                }
                if (schoolList != null && schoolList.size() > 0) {
                    schoolId = schoolList.get(0).getSchoolId();
                    ContactsSchoolInfo schoolInfo = schoolList.get(0);
                    schoolId = schoolInfo.getSchoolId();
                    schoolName = schoolInfo.getSchoolName();
                    loadCategoryList();
                }
            }
        };
        postRequest(ServerUrl.CONTACTS_CLASS_LIST_URL, params, listener);
    }

    private void loadSchoolData() {
        if (!isLogin()) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("MemberId", getUserInfo().getMemberId());
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.SUBSCRIBE_SCHOOL_LIST_URL, params,
                new RequestHelper.RequestModelResultListener(getActivity(),
                        SubscribeSchoolListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        SubscribeSchoolListResult result = (SubscribeSchoolListResult) getResult();
                        if (result == null || !result.isSuccess()
                                || result.getModel() == null) {
                            return;
                        }
                        updateSchools(result);
                    }
                });

    }

    private void updateSchools(SubscribeSchoolListResult result) {
        List<SchoolInfo> list = result.getModel().getSubscribeNoList();
        if (list != null && list.size() > 0){
            Iterator<SchoolInfo> iterator = list.iterator();
            SchoolInfo schoolInfo = null;
            while (iterator.hasNext()) {
                schoolInfo = iterator.next();
                if (!schoolInfo.isTeacher() || schoolInfo.isOnlineSchool()) {
                    iterator.remove();
                }
            }

            if (list.size() > 0){
                List<ContactsSchoolInfo> data = new ArrayList<>();
                ContactsSchoolInfo contactsSchoolInfo = null;
                for (int i = 0,len = list.size();i < len;i++){
                    SchoolInfo info = list.get(i);
                    contactsSchoolInfo = new ContactsSchoolInfo();
                    contactsSchoolInfo.setSchoolName(info.getSchoolName());
                    contactsSchoolInfo.setSchoolId(info.getSchoolId());
                    contactsSchoolInfo.setLogoUrl(info.getSchoolLogo());
                    data.add(contactsSchoolInfo);
                }
                schoolListResult = new ContactsSchoolListResult();
                schoolListResult.setModel(new ContactsSchoolListResult.ContactsSchoolListModel());
                schoolListResult.getModel().setSchoolList(data);
                schoolId = data.get(0).getSchoolId();
                ContactsSchoolInfo info = data.get(0);
                schoolId = info.getSchoolId();
                schoolName = info.getSchoolName();
                loadCategoryList();
            }
        }
    }


    protected void loadCategoryList() {
        if (TextUtils.isEmpty(this.schoolId)) {
            return;
        }

        Map<String, Object> params = new HashMap();
        params.put("SchoolId", this.schoolId);
        DefaultListener listener =
            new DefaultListener<ContactsClassCategorySetResult>(
                ContactsClassCategorySetResult.class) {
                @Override
                public void onSuccess(String jsonString) {
                    if (getActivity() == null) {
                        return;
                    }
                    super.onSuccess(jsonString);
                    ContactsClassCategorySetResult result = getResult();
                    if (result == null || !result.isSuccess()
                        || result.getModel() == null) {
                        return;
                    }

                    classCategoryResult = result;
                    updateViews();
                }
            };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(getActivity(),
            ServerUrl.CONTACTS_CLASS_CATEGORY_URL, params, listener);
    }

    private void updateViews() {
        List<Category> categoryList = new ArrayList<>();
        Category cat = null;
        CategoryValue value = null;

        if (allCategories == null) {
            List<ContactsSchoolInfo> schoolList = schoolListResult.getModel().getSchoolList();
            cat = new Category();
            cat.setType(Constants.SCHOOL_CATEGORY_SCHOOL);
            cat.setName(getString(R.string.school));
            if (schoolList != null && schoolList.size() > 0) {
                cat.setFillWithDefaultValue(false);
                cat.setAllValues(new ArrayList());
                for (ContactsSchoolInfo obj : schoolList) {
                    value = new CategoryValue();
                    value.setId(obj.getSchoolId());
                    value.setValue(obj.getSchoolName());
                    cat.getAllValues().add(value);
                }
                categoryList.add(cat);
            }
        } else {
            categoryList.add(allCategories.get(0));
        }

        if (classCategoryResult != null) {
            List<SchoolStage> stageList = classCategoryResult.getModel().getLevelList();
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
            }
            categoryList.add(cat);

            List<SchoolGrade> gradeList = classCategoryResult.getModel().getGradeList();
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
            }
            categoryList.add(cat);

            List<SchoolClass> classList = classCategoryResult.getModel().getClassList();
            cat = new Category();
            cat.setType(Constants.SCHOOL_CATEGORY_CLASS);
            cat.setName(getString(R.string.classes));
            if (classList != null && classList.size() > 0) {
                cat.setAllValues(new ArrayList());
                for (SchoolClass obj : classList) {
                    value = new CategoryValue();
                    value.setId(obj.getClassId());
                    value.setValue(obj.getClassName());
                    cat.getAllValues().add(value);
                }
            }
            categoryList.add(cat);
        }

        Category schoolCategory = categoryList.get(0);
        CategoryValue currValue = schoolCategory.getCurrValue();
        int currValuePosition = categoryView.getSelectedValuePosition(schoolCategory);
        if (allCategories == null) {
            allCategories = categoryList;
            categoryView.setAllCategories(categoryList);
        } else {
            allCategories = categoryList;
            categoryView.setAllCategories(categoryList, false);
            allCategories.get(0).setCurrValue(currValue);
            categoryView.selectValue(schoolCategory, currValuePosition, true);
            categoryView.updateViews();
        }
        categoryView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCategorySelect(List<Category> categories) {
        selectedCategories = categories;
    }

    @Override
    public void onCategoryValueSelect(Category category) {
        switch (category.getType()) {
        case Constants.SCHOOL_CATEGORY_SCHOOL:
            if (!category.getCurrValue().getId().equals(this.schoolId)) {
                this.schoolId = category.getCurrValue().getId();
                this.schoolName = category.getCurrValue().getValue();
                classCategoryResult = null;
                updateViews();
                loadCategoryList();
            }
            break;
        case Constants.SCHOOL_CATEGORY_STAGE:
        case Constants.SCHOOL_CATEGORY_GRADE:
        case Constants.SCHOOL_CATEGORY_CLASS:
            if (category.getCurrValue().isDefault()) {
                showCustomizeDialog(category);
            }
            break;
        }
    }

    protected void showCustomizeDialog(final Category category) {
        int titleId = 0;
        switch (category.getType()) {
        case Constants.SCHOOL_CATEGORY_STAGE:
            titleId = R.string.customize_stage;
            break;
        case Constants.SCHOOL_CATEGORY_GRADE:
            titleId = R.string.customize_grade;
            break;
        case Constants.SCHOOL_CATEGORY_CLASS:
            titleId = R.string.customize_class;
            break;
        }
        ContactsInputBoxDialog dialog = new ContactsInputBoxDialog(getActivity(),
                R.style.Theme_ContactsDialog, getString(titleId),
                null, category.getCurrValue().getNewValue(),
                getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        },
                getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = ((ContactsInputBoxDialog) dialog).getInputText().trim();
                if (!TextUtils.isEmpty(text)) {
                    //限制特殊字符
                    if (!Utils.checkEditTextValid(getActivity(),text)){
                        return;
                    }
                    dialog.dismiss();
                    category.getCurrValue().setNewValue(text);
                    categoryView.updateCategoryListView();
                }else {
                    dialog.dismiss();
                }
            }
        });
        //设置不自动消失
        dialog.setIsAutoDismiss(false);
        dialog.show();
    }

    protected boolean isAllCategoriesSelected() {
        if (selectedCategories.size() <= 0
                || selectedCategories.size() != allCategories.size()) {
            return false;
        }

        for (Category category : selectedCategories) {
            if (category.getCurrValue().isDefault()) {
                if (TextUtils.isEmpty(category.getCurrValue().getNewValue())) {
                    return false;
                }
            }
        }
        return true;
    }

    protected boolean isRequiredCategoriesSelected() {
        if (selectedCategories.size() <= 2) {
            return false;
        }

        boolean hasSchool = false;
        boolean hasClass = false;
        for (Category category : selectedCategories) {
            if (category.getType() == Constants.SCHOOL_CATEGORY_SCHOOL) {
                if (category.getCurrValue().isDefault()) {
                    if (!TextUtils.isEmpty(category.getCurrValue().getNewValue())) {
                        hasSchool = true;
                    }
                } else {
                    hasSchool = true;
                }
            } else if (category.getType() == Constants.SCHOOL_CATEGORY_CLASS) {
                if (category.getCurrValue().isDefault()) {
                    if (!TextUtils.isEmpty(category.getCurrValue().getNewValue())) {
                        hasClass = true;
                    }
                } else {
                    hasClass = true;
                }
            }
        }
        return hasSchool && hasClass;
    }

    public static class ClassParams {
        String LevelId;
        String LevelName;
        String GradeId;
        String GradeName;
        String ClassId;
        String ClassName;

        public String getLevelId() {
            return LevelId;
        }

        public void setLevelId(String levelId) {
            LevelId = levelId;
        }

        public String getLevelName() {
            return LevelName;
        }

        public void setLevelName(String levelName) {
            LevelName = levelName;
        }

        public String getGradeId() {
            return GradeId;
        }

        public void setGradeId(String gradeId) {
            GradeId = gradeId;
        }

        public String getGradeName() {
            return GradeName;
        }

        public void setGradeName(String gradeName) {
            GradeName = gradeName;
        }

        public String getClassId() {
            return ClassId;
        }

        public void setClassId(String classId) {
            ClassId = classId;
        }

        public String getClassName() {
            return ClassName;
        }

        public void setClassName(String className) {
            ClassName = className;
        }
    }

}
