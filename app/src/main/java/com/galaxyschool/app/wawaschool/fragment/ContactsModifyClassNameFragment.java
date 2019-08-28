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

public class ContactsModifyClassNameFragment extends ContactsClassCategorySelectorFragment
        implements CategorySelectorView.OnCategoryValueSelectListener {

    public static final String TAG = ContactsModifyClassNameFragment.class.getSimpleName();

    public interface Constants extends ContactsClassCategorySelectorFragment.Constants {
        int REQUEST_CODE_MODIFY_CLASS_NAME = 10011;

        String EXTRA_CLASS_NAME_CHANGED = "classNameChanged";
        String EXTRA_CLASS_ID = "classId";
        String EXTRA_CLASS_NAME = "className";
        String EXTRA_FROM_MODIFY_CLASS_NAME = "from_modify_class_name";
    }

    private String classId;
    private String className;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        this.classId = getArguments().getString(Constants.EXTRA_CLASS_ID);
        this.className = getArguments().getString(Constants.EXTRA_CLASS_NAME);

        initViews();
    }

    private void initViews() {
        TextView textView = ((TextView) findViewById(R.id.contacts_header_title));
        if (textView != null) {
            textView.setText(R.string.modify_class_name);
        }
    }

    @Override
    public void onCategorySelect(List<Category> categories) {
        super.onCategorySelect(categories);

        modifyClassName();
    }

    @Override
    public void finish() {
        getActivity().setResult(getResultCode(), getResultData());
        super.finish();
    }

    private void modifyClassName() {
        if (!isRequiredCategoriesSelected()) {
            TipsHelper.showToast(getActivity(), R.string.class_name_needed);
            return;
        }

        Category stageCategory = null;
        Category gradeCategory = null;
        Category classCategory = null;
        for (Category category : selectedCategories) {
            if (category.getType() == Constants.SCHOOL_CATEGORY_STAGE) {
                stageCategory = category;
            } else if (category.getType() == Constants.SCHOOL_CATEGORY_GRADE) {
                gradeCategory = category;
            } else if (category.getType() == Constants.SCHOOL_CATEGORY_CLASS) {
                classCategory = category;
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
//            classParams.ClassId = "";
            classParams.ClassName = classCategory.getCurrValue().getNewValue();
        } else {
//            classParams.ClassId = classCategory.getCurrValue().getId();
            classParams.ClassName = classCategory.getCurrValue().getValue();
        }
        classParams.ClassId = this.classId;
        if (getUserInfo() == null){
            return;
        }
        //这里限制班级名称创建的长度 最多只能输入40个字符
        if (classParams!=null){
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
        final String remarkName = classParams.getLevelName()
                + classParams.getGradeName()
                + classParams.getClassName();
        Map<String, Object> params = new HashMap();
        params.put("VersionCode", "1");
        params.put("OperateType", "Classname");
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("SchoolId", this.schoolId);
        params.put("NewModel", classParams);
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
//                    TipsHelper.showToast(getActivity(), R.string.modify_class_name_failed);
                    return;
                }
                TipsHelper.showToast(getActivity(), R.string.modify_class_name_success);

                notifyChanges((String) getTarget());

                //修改成功之后更新相应的班级名称
                Intent remarkNameIntent = new Intent(PersonalSpaceFragment.ACTION_MODIFY_USER_REMARK_NAME);
                remarkNameIntent.putExtra("remarkName",remarkName);
                getActivity().sendBroadcast(remarkNameIntent);

                //关注/取消关注成功后，向校园空间发广播
                MySchoolSpaceFragment.sendBrocast(getActivity());
            }
        };
        StringBuilder builder = new StringBuilder();
        if (!TextUtils.isEmpty(classParams.getLevelName())) {
            builder.append(classParams.getLevelName());
        }
        if (!TextUtils.isEmpty(classParams.getGradeName())) {
            builder.append(classParams.getGradeName());
        }
        if (!TextUtils.isEmpty(classParams.getClassName())) {
            builder.append(classParams.getClassName());
        }
        listener.setTarget(builder.toString());
        postRequest(ServerUrl.CONTACTS_MODIFY_CLASS_ATTRIBUTES_URL, params, listener);
    }

    private void notifyChanges(String newClassName) {
        if (!this.className.equals(newClassName)) {
            Bundle args = new Bundle();
            args.putBoolean(Constants.EXTRA_CLASS_NAME_CHANGED, true);
            args.putString(Constants.EXTRA_CLASS_ID, this.classId);
            args.putString(Constants.EXTRA_CLASS_NAME, newClassName);

            Intent intent = new Intent();
            intent.putExtras(args);
            ContactsModifyClassNameFragment.this.setResult(Activity.RESULT_OK, intent);
        }
        finish();
    }

}
