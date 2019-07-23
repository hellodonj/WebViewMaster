package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.category.Category;
import com.galaxyschool.app.wawaschool.fragment.category.CategorySelectorView;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.lqwawa.lqbaselib.net.library.ModelResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactsCreateClassFragment extends ContactsClassCategorySelectorFragment
        implements CategorySelectorView.OnCategoryValueSelectListener {

    public static final String TAG = ContactsCreateClassFragment.class.getSimpleName();

    public interface Constants extends ContactsClassCategorySelectorFragment.Constants {
        public static final int REQUEST_CODE_CREATE_CLASS = 1001;

        public static final String EXTRA_CLASS_CREATED = "classCreated";
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    private void initViews() {
        TextView textView = ((TextView) findViewById(R.id.contacts_header_title));
        if (textView != null) {
            textView.setText(R.string.create_class);
        }
    }

    @Override
    public void onCategorySelect(List<Category> categories) {
        super.onCategorySelect(categories);

        createClass();
    }

    private void createClass() {
        if (!isRequiredCategoriesSelected()) {
            TipsHelper.showToast(getActivity(), R.string.class_name_needed);
            return;
        }

        Category schoolCategory = null;
        Category stageCategory = null;
        Category gradeCategory = null;
        Category classCategory = null;
        Category payClassCategory = null;
        for (Category category : selectedCategories) {
            if (category.getType() == Constants.SCHOOL_CATEGORY_SCHOOL) {
                schoolCategory = category;
            } else if (category.getType() == Constants.SCHOOL_CATEGORY_STAGE) {
                stageCategory = category;
            } else if (category.getType() == Constants.SCHOOL_CATEGORY_GRADE) {
                gradeCategory = category;
            } else if (category.getType() == Constants.SCHOOL_CATEGORY_CLASS) {
                classCategory = category;
            } else if (category.getType() == Constants.SCHOOL_CLASS_JOIN_TYPE) {
                payClassCategory = category;
            }
        }

        ClassParams classParams = new ClassParams();
        if (stageCategory.getCurrValue().isDefault()) {
            classParams.LevelId = "";
            classParams.LevelName = stageCategory.getCurrValue().getNewValue();
        } else {
            classParams.LevelId = stageCategory.getCurrValue().getId();
            classParams.LevelName = stageCategory.getCurrValue().getValue();
        }
        if (gradeCategory.getCurrValue().isDefault()) {
            classParams.GradeId = "";
            classParams.GradeName = gradeCategory.getCurrValue().getNewValue();
        } else {
            classParams.GradeId = gradeCategory.getCurrValue().getId();
            classParams.GradeName = gradeCategory.getCurrValue().getValue();
        }
        if (classCategory.getCurrValue().isDefault()) {
            classParams.ClassId = "";
            classParams.ClassName = classCategory.getCurrValue().getNewValue();
        } else {
            classParams.ClassId = classCategory.getCurrValue().getId();
            classParams.ClassName = classCategory.getCurrValue().getValue();
        }
        if (getUserInfo() == null){
            return;
        }
        //这里限制班级名称创建的长度 最多只能输入40个字符
        if (classParams != null){
            String className = classParams.getLevelName()
                    +classParams.getGradeName()
                    +classParams.getClassName();
            if (!TextUtils.isEmpty(className)){
                //过滤Emoji表情
                if (Utils.containsEmoji(getActivity(),className)){
                    return;
                }
                //过滤特殊字符
                if (!Utils.checkTitleValid(getActivity(),className)){
                    return;
                }
                if (className.length() > 40){
                    TipsHelper.showToast(getActivity(),R.string.class_name_more_than_40_character);
                    return;
                }
            }
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("SchoolId", schoolCategory.getCurrValue().getId());
        params.put("NewModel", classParams);
        //班级价格
        if (payClassCategory != null && !TextUtils.isEmpty(payClassCategory.getCurrValue().getPrice())){
            params.put("Price",Integer.valueOf(payClassCategory.getCurrValue().getPrice()));
        }
        DefaultListener listener =
                new DefaultListener<ModelResult>(ModelResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                ModelResult result = getResult();
                if (result == null || !result.isSuccess()) {
                    //TipsHelper.showToast(getActivity(), R.string.create_class_failed);
                    return;
                }
                TipsHelper.showToast(getActivity(), R.string.create_class_success);

                Bundle args = new Bundle();
                args.putBoolean(Constants.EXTRA_CLASS_CREATED, true);
                args.putString(Constants.EXTRA_SCHOOL_ID, (String) getTarget());
                Intent intent = new Intent();
                intent.putExtras(args);
                getActivity().setResult(Activity.RESULT_OK, intent);
                finish();
            }
        };
        listener.setTarget(schoolCategory.getCurrValue().getId());
        listener.setShowLoading(true);
        postRequest(ServerUrl.CONTACTS_CREATE_CLASS_URL, params, listener);
    }

}
