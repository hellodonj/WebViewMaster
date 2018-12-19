package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.PictureBooksDetailActivity;
import com.galaxyschool.app.wawaschool.PictureBooksMoreActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.SharedPreferencesHelper;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.UploadReourceHelper;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.fragment.resource.NewResourcePadAdapterViewHelper;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.AuthorizationInfo;
import com.galaxyschool.app.wawaschool.pojo.AuthorizationInfoResult;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfoListResult;
import com.galaxyschool.app.wawaschool.pojo.PicBookCategory;
import com.galaxyschool.app.wawaschool.pojo.PicBookCategoryListResult;
import com.galaxyschool.app.wawaschool.pojo.PicBookCategoryType;
import com.galaxyschool.app.wawaschool.pojo.PictureBooksFragmentHelper;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfoResult;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;
import com.galaxyschool.app.wawaschool.pojo.SubscribeSchoolListResult;
import com.galaxyschool.app.wawaschool.pojo.UploadParameter;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.sortlistview.ClearEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by KnIghT on 16-5-10.
 */
public class PictureBooksFragment extends ContactsListFragment {
    public static final String TAG = PictureBooksFragment.class.getSimpleName();
    private static final int MAX_BOOKS_PER_ROW = 2;
    private TextView keywordView;
    private String keyword = "";
    private TextView labelTextView;
    private TextView studyStageTextView;
    private TextView languageTextView;

    private TextView selectLabelTextView;
    private TextView selectStudyStageTextView;
    private TextView selectLanguageTextView;

    private ImageView labelImageView;
    private ImageView studyStageImageView;
    private ImageView languageImageView;

    private LinearLayout labelLayout;
    private LinearLayout studyStageLayout;
    private LinearLayout languageLayout;
    private AuthorizationInfo authorizationInfo; //保存授权信息
    String allRoles=null;//用户在原始学校的角色
    private int taskType;

    private List<PicBookCategoryType> labelCategorys;
    private List<PicBookCategoryType> studyStageCategorys;
    private List<PicBookCategoryType> languageCategorys;

    PicBookCategoryType selectLabelCategory;
    PicBookCategoryType selectstudyStageCategory;
    PicBookCategoryType selectLanguageCategory;

    private com.galaxyschool.app.wawaschool.views.PopupMenu popupMenu;
    private List<com.galaxyschool.app.wawaschool.views.PopupMenu.PopupMenuData> itemDatas;
    private List<SchoolInfo> schoolInfoList;//关注的学校集合
    private SchoolInfo schoolInfo;//当前学校
    private TextView headTitletextView;
    PictureBooksFragmentHelper helper = new PictureBooksFragmentHelper();
    private boolean isPick;
    private int curPosition;
    private String schoolId; //用户所在学校Id
    private String feeSchoolId; //购买的课程所在学校Id

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_picture_books, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        if(isPick){
            loadSchoolInfo();
        }
        loadCategoryList();
        loadPicBooks();
    }
    protected void loadSchoolInfo() {

        Map<String, Object> params = new HashMap();
        if (isLogin()) {
            params.put("MemberId", getUserInfo().getMemberId());
        }else{
            return;
        }
        params.put("SchoolId", schoolId);
        params.put("VersionCode", 1);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.SUBSCRIBE_SCHOOL_INFO_URL,
                params,
                new DefaultListener<SchoolInfoResult>(SchoolInfoResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()) {
                            return;
                        }
                        SchoolInfo   schoolInfo = getResult().getModel();
                        allRoles=schoolInfo.getRoles();
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void loadDefautDataCache() {
        if (helper.isNeedUseCache(getMemeberId(), labelCategorys, studyStageCategorys, languageCategorys)) {
            selectLabelCategory = helper.getSelectLabelCategory();
            selectstudyStageCategory = helper.getSelectstudyStageCategory();
            selectLanguageCategory = helper.getSelectLanguageCategory();
            getCurrAdapterViewHelper().setData(helper.getBookList());
        } else {
            resetPage();
            loadDefaultPicBooks();
        }
        initDefaultSchoolView();
        loadCategoryList();
    }

    private void initDefaultSchoolView() {
        schoolInfo = null;
        schoolInfo = initDefaultSchoolInfo();
        headTitletextView.setVisibility(View.VISIBLE);
        headTitletextView.setText(schoolInfo.getSchoolName());
    }

    public boolean isLogin() {
        if (getUserInfo() != null && !TextUtils.isEmpty(getUserInfo().getMemberId())) {
            return true;
        }
        return false;
    }

    private void loadAttentionSchoolInfo() {
        if (getUserInfo() == null){
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());

        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.SUBSCRIBE_SCHOOL_LIST_URL, params,
                new DefaultListener<SubscribeSchoolListResult>(
                        SubscribeSchoolListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        SubscribeSchoolListResult result = getResult();
                        if (result == null || !result.isSuccess()
                                || result.getModel() == null || result.getModel().getSubscribeNoList().size() == 0) {
                            loadDefautDataCache();
                        } else {
                            schoolInfoList = result.getModel().getSubscribeNoList();
                            schoolInfoList.add(0, initDefaultSchoolInfo());
                            if (helper.isNeedUseCache2(getMemeberId(), schoolInfoList, labelCategorys, studyStageCategorys, languageCategorys)) {
                                getCurrAdapterViewHelper().setData(helper.getBookList());
                                schoolInfo = helper.getSchoolInfo();
                                selectLabelCategory = helper.getSelectLabelCategory();
                                selectstudyStageCategory = helper.getSelectstudyStageCategory();
                                selectLanguageCategory = helper.getSelectLanguageCategory();
                            } else {
                                resetPage();
                                if (schoolInfoList != null && schoolInfoList.size() > 0) {
                                    if (!TextUtils.isEmpty(getLatestSchool(getMemeberId()))) {
                                        for (SchoolInfo sc : schoolInfoList) {
                                            if (sc.getSchoolId().equals(getLatestSchool(getMemeberId()))) {
                                                schoolInfo = sc;
                                                schoolInfo.setIsSelect(true);
                                                break;
                                            }
                                        }
                                    }
                                    if (schoolInfo == null) {
                                        schoolInfo = schoolInfoList.get(0);
                                        schoolInfo.setIsSelect(true);
                                    }
                                }
                                if (schoolInfo.getSchoolId().equals("-1")) {
                                    loadDefaultPicBooks();
                                } else {
                                    loadPicBooks();
                                }
                            }
                            initSchoolView();
                            loadCategoryList();
                        }
                    }
                });
    }

    private void initSchoolView() {
        headTitletextView.setVisibility(View.VISIBLE);
        headTitletextView.setText(schoolInfo.getSchoolName());
    }

    private SchoolInfo initDefaultSchoolInfo() {
        SchoolInfo schoolInfo1 = new SchoolInfo();
        schoolInfo1.setSchoolId("-1");
        schoolInfo1.setSchoolName(getString(R.string.choice_picturebooks));
        return schoolInfo1;
    }


    private void updateConditionView(List<PicBookCategory> picBookCategories) {
        for (PicBookCategory picBookCategory : picBookCategories) {//1：年龄段2：语言 3：标签
            if (picBookCategory.getType() == 3) {
                labelTextView.setText(picBookCategory.getTypeName());
                labelCategorys = picBookCategory.getDetailList();
                PicBookCategoryType all = new PicBookCategoryType();
                all.setId("-1");
                all.setName(getString(R.string.all));
                labelCategorys.add(0, all);
            } else if (picBookCategory.getType() == 2) {
                languageTextView.setText(picBookCategory.getTypeName());
                languageCategorys = picBookCategory.getDetailList();
                PicBookCategoryType all = new PicBookCategoryType();
                all.setId("-1");
                all.setName(getString(R.string.all));
                languageCategorys.add(0, all);
            } else if (picBookCategory.getType() == 1) {
                studyStageTextView.setText(picBookCategory.getTypeName());
                studyStageCategorys = picBookCategory.getDetailList();
                PicBookCategoryType all = new PicBookCategoryType();
                all.setId("-1");
                all.setName(getString(R.string.all));
                studyStageCategorys.add(0, all);
            }
        }
        if (isLogin()) {
            if (helper.isNeedUseCache2(getMemeberId(), schoolInfoList, labelCategorys, studyStageCategorys, languageCategorys)) {
                selectLabelTextView.setText(selectLabelCategory.getName());
                selectLanguageTextView.setText(selectLanguageCategory.getName());
                selectStudyStageTextView.setText(selectstudyStageCategory.getName());
            } else {
                selectLabelTextView.setText(getString(R.string.all));
                selectLanguageTextView.setText(getString(R.string.all));
                selectStudyStageTextView.setText(getString(R.string.all));
                selectLabelCategory = labelCategorys.get(0);
                selectLabelCategory.setIsSelect(true);
                selectLanguageCategory = languageCategorys.get(0);
                selectLanguageCategory.setIsSelect(true);
                selectstudyStageCategory = studyStageCategorys.get(0);
                selectstudyStageCategory.setIsSelect(true);
            }
        } else {
            if (helper.isNeedUseCache(getMemeberId(), labelCategorys, studyStageCategorys, languageCategorys)) {
                selectLabelTextView.setText(selectLabelCategory.getName());
                selectLanguageTextView.setText(selectLanguageCategory.getName());
                selectStudyStageTextView.setText(selectstudyStageCategory.getName());
            } else {
                selectLabelTextView.setText(getString(R.string.all));
                selectLanguageTextView.setText(getString(R.string.all));
                selectStudyStageTextView.setText(getString(R.string.all));
                selectLabelCategory = labelCategorys.get(0);
                selectLabelCategory.setIsSelect(true);
                selectLanguageCategory = languageCategorys.get(0);
                selectLanguageCategory.setIsSelect(true);
                selectstudyStageCategory = studyStageCategorys.get(0);
                selectstudyStageCategory.setIsSelect(true);
            }
        }
    }

    private void loadCategoryList() {
        RequestHelper.RequestDataResultListener listener
                = new RequestHelper.RequestDataResultListener<PicBookCategoryListResult>(getActivity(), PicBookCategoryListResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                PicBookCategoryListResult result = getResult();
                if (result == null || result.getModel() == null || !result.isSuccess()) {
                    return;
                }
                List<PicBookCategory> picBookCategories = result.getModel().getData();
                if (picBookCategories != null && picBookCategories.size() > 0) {
                    updateConditionView(picBookCategories);
                }
            }
        };
        listener.setShowLoading(false);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.LOAD_PICBOOK_CATEGORY_URL, null, listener);

    }

    private void loadDefaultPicBooks() {
        //1：年龄段2：语言 3：标签
        Map<String, Object> params = new HashMap();
        String keyword = this.keywordView.getText().toString().trim();
        if (!keyword.equals(this.keyword)) {
            getCurrAdapterViewHelper().clearData();
            getPageHelper().clear();
        }
        this.keyword = keyword;
        params.put("KeyWord", keyword);
        if (selectLabelCategory != null && selectLabelCategory.getId() != null && !selectLabelCategory.getId().equals("-1")) {
            params.put("TagsId", selectLabelCategory.getId());
        }
        if (selectstudyStageCategory != null && selectstudyStageCategory.getId() != null && !selectstudyStageCategory.getId().equals("-1")) {
            params.put("AgeGroupId", selectstudyStageCategory.getId());
        }
        if (selectLanguageCategory != null && selectLanguageCategory.getId() != null && !selectLanguageCategory.getId().equals("-1")) {
            params.put("LanguageId", selectLanguageCategory.getId());
        }
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
                        NewResourceInfoListResult result = getResult();
                        if (result == null || !result.isSuccess()
                                || result.getModel() == null) {
                            return;
                        }
                        updatePicBookListView(result);
                    }
                };
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.PICBOOKS_GET_DEFAULT_PICBOOKLIST_URL, params, listener);
    }

    private void loadPicBooks() {
        //1：年龄段2：语言 3：标签
        Map<String, Object> params = new HashMap();
        String keyword = this.keywordView.getText().toString().trim();
        if (!keyword.equals(this.keyword)) {
            getCurrAdapterViewHelper().clearData();
            getPageHelper().clear();
        }
        this.keyword = keyword;
        params.put("KeyWord", keyword);
        if (selectLabelCategory != null && selectLabelCategory.getId() != null && !selectLabelCategory.getId().equals("-1")) {
            params.put("TagsId", selectLabelCategory.getId());
        }
        if (selectstudyStageCategory != null && selectstudyStageCategory.getId() != null && !selectstudyStageCategory.getId().equals("-1")) {
            params.put("AgeGroupId", selectstudyStageCategory.getId());
        }
        if (selectLanguageCategory != null && selectLanguageCategory.getId() != null && !selectLanguageCategory.getId().equals("-1")) {
            params.put("LanguageId", selectLanguageCategory.getId());
        }
        if (schoolInfo != null) {
            params.put("SchoolId", schoolInfo.getSchoolId());
        }
        params.put("SchoolId", feeSchoolId);
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
                getCurrAdapterViewHelper().clearData();
            }
            if (list == null || list.size() <= 0) {
                if (getPageHelper().isFetchingFirstPage()) {
                    TipsHelper.showToast(getActivity(),
                            getString(R.string.no_data));
                } else {
                    TipsHelper.showToast(getActivity(),
                            getString(R.string.no_more_data));
                }
                helper.saveData(getCurrAdapterViewHelper().getData(), schoolInfo, selectLabelCategory, selectstudyStageCategory, selectLanguageCategory, getMemeberId());
                return;
            }
            getPageHelper().updateByPagerArgs(result.getModel().getPager());
            getPageHelper().setCurrPageIndex(
                    getPageHelper().getFetchingPageIndex());
            if (getCurrAdapterViewHelper().hasData()) {
//                int position = getCurrAdapterViewHelper().getData().size();
//                if (position > 0) {
//                    position--;
//                }
                getCurrAdapterViewHelper().getData().addAll(list);
                getCurrAdapterViewHelper().update();
            } else {
                getCurrAdapterViewHelper().setData(list);
            }
            helper.saveData(getCurrAdapterViewHelper().getData(), schoolInfo, selectLabelCategory, selectstudyStageCategory, selectLanguageCategory, getMemeberId());
        }
    }

    private void saveLatestSchool(String memberId, String schoolId) {//记录最近点击的学校
        SharedPreferencesHelper.setString(getActivity(), memberId + "_SchoolId", schoolId);
    }

    private String getLatestSchool(String memberId) {//加载最近点击的学校
        return SharedPreferencesHelper.getString(getActivity(), memberId + "_SchoolId");
    }
    private View.OnClickListener moreListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            enterPictureMoreActivity();
        }
    };

    private void enterPictureMoreActivity() {
        Intent intent = new Intent(getActivity(), PictureBooksMoreActivity.class);
        Bundle args = new Bundle();
        intent.putExtras(args);
        startActivity(intent);
    }

    private void enterPictureDetailActivity(NewResourceInfo item) {
        Intent intent = new Intent(getActivity(), PictureBooksDetailActivity.class);
        intent.putExtra(PictureBooksDetailActivity.NEW_RESOURCE_INFO, item);
        intent.putExtra(PictureBooksDetailActivity.FROM_SOURCE_TYPE, PictureBooksDetailActivity.FROM_OTHRE);
        startActivity(intent);
    }

    private void initViews() {
        if (getArguments() != null) {
            isPick = getArguments().getBoolean(ActivityUtils.EXTRA_IS_PICK);
            schoolId = getArguments().getString(PictureBooksDetailFragment.Constants.EXTRA_SCHOOL_ID);
            feeSchoolId = getArguments().getString(PictureBooksDetailFragment.Constants.EXTRA_FEE_SCHOOL_ID);
            taskType = getArguments().getInt(ActivityUtils.EXTRA_TASK_TYPE);
        }
        initOrdinaryViews();
        initConditionViews();
        initSearchView();
        initGridView();
    }

    private void initOrdinaryViews() {
        headTitletextView = (TextView) findViewById(R.id.contacts_header_title);
        headTitletextView.setVisibility(View.VISIBLE);
        if(isPick){
            String headerTitle=null;
            if(taskType == StudyTaskType.RETELL_WAWA_COURSE) {
                headerTitle = getString(R.string.n_create_task, getString(R.string.retell_course));
            }else if(taskType == StudyTaskType.WATCH_WAWA_COURSE){
                headerTitle = getString(R.string.n_create_task, getString(R.string.look_through_courseware));
            }else if (taskType==StudyTaskType.INTRODUCTION_WAWA_COURSE){
                headerTitle=getString(R.string.appoint_course_no_point);
            }
            headTitletextView.setText(headerTitle);
        }else {
            headTitletextView.setText(getString(R.string.choice_picturebooks));
        }
        ImageView imageView = ((ImageView) findViewById(R.id.contacts_header_left_btn));
        imageView.setVisibility(View.VISIBLE);
        View headerRootView = findViewById(R.id.contacts_header_layout);
        //headerRootView.setVisibility(isPick ? View.GONE : View.VISIBLE);
        TextView  textView = (TextView) findViewById(R.id.contacts_header_right_btn);
        if(textView!=null){
            if(isPick){
                textView.setText(getString(R.string.confirm));
                textView.setVisibility(View.VISIBLE);
                textView.setOnClickListener(this);
            }else{
                textView.setVisibility(View.GONE);
            }
        }
    }

    private void initConditionViews() {
        labelTextView = (TextView) findViewById(R.id.label_textview);
        studyStageTextView = (TextView) findViewById(R.id.study_stage_textview);
        languageTextView = (TextView) findViewById(R.id.language_textview);

        selectLabelTextView = (TextView) findViewById(R.id.select_label_textview);
        selectStudyStageTextView = (TextView) findViewById(R.id.select_study_stage_textview);
        selectLanguageTextView = (TextView) findViewById(R.id.select_language_textview);

        labelImageView = ((ImageView) findViewById(R.id.label_imageview));
        studyStageImageView = ((ImageView) findViewById(R.id.study_stage_imageview));
        languageImageView = ((ImageView) findViewById(R.id.language_imageview));

        labelLayout = ((LinearLayout) findViewById(R.id.label_layout));
        studyStageLayout = ((LinearLayout) findViewById(R.id.study_stage_layout));
        languageLayout = ((LinearLayout) findViewById(R.id.language_layout));
        labelLayout.setOnClickListener(this);
        studyStageLayout.setOnClickListener(this);
        languageLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.contacts_header_left_btn:
                if (isPick) {
                    popStack();
                } else {
                    super.onClick(v);
                }
                break;
            case R.id.label_layout:
                showLabelSelectors(getActivity(), selectLabelListener, v, labelCategorys, 3);
                break;
            case R.id.study_stage_layout:
                showLabelSelectors(getActivity(), selectstudyStageListener, v, studyStageCategorys, 1);
                break;
            case R.id.language_layout:
                showLabelSelectors(getActivity(), selectLanguagelListener, v, languageCategorys, 2);
                break;
            case R.id.school_more_textview:
                showMoreSchools(getActivity(), ToggleSchoolListener, (View) v.getParent());
                break;
            case R.id.contacts_header_right_btn:
                NewResourceInfo cloudSelectData = getSelectData();
                if (cloudSelectData == null) {
                    TipMsgHelper.ShowLMsg(getActivity(), R.string.no_file_select);
                    return;
                }
                enterPublishStudyTaskFragment();
                //checkAuthorizationCondition(schoolId, feeSchoolId);
                break;

        }
    }

    private void enterPublishStudyTaskFragment(){
        NewResourceInfo cloudSelectData = getSelectData();
        CourseData courseData = new CourseData();
        courseData.id = Integer.parseInt(cloudSelectData.getMicroId());
        courseData.nickname = cloudSelectData.getTitle();
        courseData.type = cloudSelectData.getResourceType();
        courseData.resourceurl = cloudSelectData.getResourceUrl();
        courseData.thumbnailurl=cloudSelectData.getThumbnail();
        courseData.code = cloudSelectData.getAuthorId();
        courseData.shareurl = cloudSelectData.getShareAddress();
        UploadParameter uploadParameter = UploadReourceHelper.getUploadParameter(getUserInfo(), null, courseData, null, 1);
        String headerTitle = getString(R.string.n_create_task, getString(R.string.look_through_courseware));
        if (uploadParameter != null) {
            //  popStack();

            if (getArguments() != null) {
                int taskType = getArguments().getInt(ActivityUtils.EXTRA_TASK_TYPE);
                uploadParameter.setTaskType(taskType);
                if (taskType == StudyTaskType.RETELL_WAWA_COURSE) {
                    headerTitle = getString(R.string.n_create_task, getString(R.string.retell_course));
                }
            }

                uploadParameter.setTaskType(getArguments().getInt(ActivityUtils.EXTRA_COURSE_TYPE));
                IntroductionForReadCourseFragment fragmentCourse= (IntroductionForReadCourseFragment) getActivity().getSupportFragmentManager()
                        .findFragmentByTag(IntroductionForReadCourseFragment.TAG);
                fragmentCourse.setData(uploadParameter);
                int num=getFragmentManager().getBackStackEntryCount();
                while (num > 1) {
                    popStack();
                    num = num - 1;
                }
        }
    }
    private void authorizeToMembers(String schoolId, String feeSchoolId) {
        if (getUserInfo() == null){
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("SchoolId", schoolId);
        params.put("CourseId", feeSchoolId);
        if(!TextUtils.isEmpty(allRoles)) {
            params.put("RoleTypes", allRoles);
        }
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.AUTHONRIZE_TO_MEMBER_URL,
                params, new RequestHelper.RequestModelResultListener<ModelResult>
                        (getActivity(), ModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        ModelResult result = getResult();
                        if (result == null || !result.isSuccess()) {
                            return;
                        }
                        if(result.isSuccess()) {
                            if(authorizationInfo != null) {
                                authorizationInfo.setIsMemberAuthorized(true);
                            }
                            enterPublishStudyTaskFragment();
                        }
                    }
                });
    }
    private boolean checkAuthorizationCondition(final String schoolId, final String feeSchoolId) {
        if (getUserInfo() == null){
            return false;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("SchoolId", schoolId);
        params.put("CourseId", feeSchoolId);
        RequestHelper.RequestDataResultListener<AuthorizationInfoResult>  listener =
                new RequestHelper.RequestDataResultListener<AuthorizationInfoResult>
                        (getActivity(), AuthorizationInfoResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        AuthorizationInfoResult result = (AuthorizationInfoResult)getResult();

                        if (result == null || !result.isSuccess()
                                || result.getModel() == null) {
                            TipsHelper.showToast(getActivity(),getString(R.string.course_is_not_authorize));
                            return;
                        }
                        authorizationInfo=null;
                        authorizationInfo = result.getModel().getData();
                        if(authorizationInfo != null) {
                            if(authorizationInfo.isIsMemberAuthorized()) {
                                enterPublishStudyTaskFragment();
                            }  else {
                                if(authorizationInfo.isIsCanAuthorize()) {
                                    authorizeToMembers(schoolId,feeSchoolId);
                                } else {
                                    TipsHelper.showToast(getActivity(),getString(R.string.course_is_not_authorize));
                                }
                            }
                        }
                    }
                };
        listener.setShowErrorTips(false);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.CHECK_AUTHONRIZE_CONDITION_URL,
                params,listener );
        return false;
    }

    //展示学校列表
    private void showMoreSchools(Activity activity, AdapterView.OnItemClickListener itemClickListener, View AnchorView) {
        List<com.galaxyschool.app.wawaschool.views.PopupMenu.PopupMenuData> itemDatas = new ArrayList<com.galaxyschool.app.wawaschool.views.PopupMenu.PopupMenuData>();
        if (schoolInfoList == null || schoolInfoList.size() == 0) {
            return;
        }
        for (int i = 0; i < schoolInfoList.size(); i++) {
            com.galaxyschool.app.wawaschool.views.PopupMenu.PopupMenuData data = new com.galaxyschool.app.wawaschool.views.PopupMenu.PopupMenuData();
            if (schoolInfoList.get(i) != null && !TextUtils.isEmpty(schoolInfoList.get(i).getSchoolName())) {
                data.setText(schoolInfoList.get(i).getSchoolName());
                data.setIsSelect(schoolInfoList.get(i).isSelect());
                itemDatas.add(data);
            }
        }
        com.galaxyschool.app.wawaschool.views.PopupMenu popupMenu = new com.galaxyschool.app.wawaschool.views.PopupMenu(activity, itemClickListener, itemDatas, 0.25f);
        WindowManager wm = (WindowManager) getActivity()
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();//屏幕宽度
        popupMenu.showAsDropDown(AnchorView);
        popupMenu.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
    }

    //切换学校
    private AdapterView.OnItemClickListener ToggleSchoolListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view,
                                int position, long id) {
            for (SchoolInfo schoolInfo2 : schoolInfoList) {
                schoolInfo2.setIsSelect(false);
            }
            SchoolInfo schoolInfo1 = schoolInfoList.get(position);
            schoolInfo1.setIsSelect(true);
            if (schoolInfo1 == null || schoolInfo1.getSchoolId() == null) {
                return;
            }

            if (schoolInfo1.getSchoolId().equals(schoolInfo.getSchoolId())) {
                return;
            } else {
                if (getUserInfo() != null || !TextUtils.isEmpty(getUserInfo().getMemberId())) {
                    saveLatestSchool(getUserInfo().getMemberId(), schoolInfo1.getSchoolId());
                }
                resetPage();
                schoolInfo = schoolInfo1;
            }
            //更換学校的同时班级列表，点击班级列表时展示新的班级列表，此时还要从新的班级列表中选出第一个班级作为默认的班级去加载数据
            if (schoolInfo != null) {
                headTitletextView.setVisibility(View.VISIBLE);
                headTitletextView.setText(schoolInfo.getSchoolName());
                if (schoolInfo.getSchoolId().equals("-1")) {
                    loadDefaultPicBooks();
                } else {
                    loadPicBooks();
                }
            }
        }
    };


    //1：年龄段2：语言 3：标签
    //展示列表
    private void showLabelSelectors(Activity activity, AdapterView.OnItemClickListener itemClickListener, View AnchorView, List<PicBookCategoryType> categorys, final int type) {
        itemDatas = new ArrayList<com.galaxyschool.app.wawaschool.views.PopupMenu.PopupMenuData>();
        if (categorys == null || categorys.size() == 0) {
            return;
        }
        for (int i = 0; i < categorys.size(); i++) {
            com.galaxyschool.app.wawaschool.views.PopupMenu.PopupMenuData data = new com.galaxyschool.app.wawaschool.views.PopupMenu.PopupMenuData();
            if (categorys.get(i) != null && !TextUtils.isEmpty(categorys.get(i).getName())) {
                data = new com.galaxyschool.app.wawaschool.views.PopupMenu.PopupMenuData();
                data.setText(categorys.get(i).getName());
                if (categorys.get(i).isSelect()) {
                    data.setIsSelect(true);
                }
                itemDatas.add(data);
            }
        }
        WindowManager wm = (WindowManager) getActivity()
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();//屏幕宽度
        popupMenu = new com.galaxyschool.app.wawaschool.views.PopupMenu(activity,
                itemClickListener, itemDatas,width/3);
        popupMenu.showAsDropDown(AnchorView);
        popupMenu.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (type == 1) {
                    studyStageImageView.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down_ico));
                } else if (type == 2) {
                    languageImageView.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down_ico));
                } else if (type == 3) {
                    labelImageView.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down_ico));
                }
            }
        });
        if (type == 1) {
            studyStageImageView.setImageDrawable(getResources().getDrawable(R.drawable.arrow_up_ico));
        } else if (type == 2) {
            languageImageView.setImageDrawable(getResources().getDrawable(R.drawable.arrow_up_ico));
        } else if (type == 3) {
            labelImageView.setImageDrawable(getResources().getDrawable(R.drawable.arrow_up_ico));
        }

    }
    private void updatePopView(int position) {
        for (int i = 0; i < itemDatas.size(); i++) {
            com.galaxyschool.app.wawaschool.views.PopupMenu.PopupMenuData popupMenuData1 = itemDatas.get(i);
            popupMenuData1.setIsSelect(false);
        }
        com.galaxyschool.app.wawaschool.views.PopupMenu.PopupMenuData popupMenuData = itemDatas.get(position);
        popupMenuData.setIsSelect(true);
        popupMenu.updateView();
    }


    //切换标签
    private AdapterView.OnItemClickListener selectLabelListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view,
                                int position, long id) {
            updatePopView(position);
            PicBookCategoryType categoryType = labelCategorys.get(position);
            if (categoryType == null || categoryType.getId() == null) {
                return;
            }
            if (selectLabelCategory != null && selectLabelCategory.getId() != null && selectLabelCategory.getId().equals(categoryType.getId())) {
                return;
            } else {
                for (PicBookCategoryType categoryType1 : labelCategorys) {
                    categoryType1.setIsSelect(false);
                }
                categoryType.setIsSelect(true);
                resetPage();
                selectLabelCategory = categoryType;
            }
            if (selectLabelCategory != null) {
                selectLabelTextView.setText(selectLabelCategory.getName());
                loadPicBooks();
            }
        }
    };
    //切换标签
    private AdapterView.OnItemClickListener selectstudyStageListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view,
                                int position, long id) {
            PicBookCategoryType categoryType = studyStageCategorys.get(position);
            if (categoryType == null || categoryType.getId() == null) {
                return;
            }
            if (selectstudyStageCategory != null && selectstudyStageCategory.getId() != null && selectstudyStageCategory.getId().equals(categoryType.getId())) {
                return;
            } else {
                for (PicBookCategoryType categoryType1 : studyStageCategorys) {
                    categoryType1.setIsSelect(false);
                }
                categoryType.setIsSelect(true);
                resetPage();
                selectstudyStageCategory = categoryType;
            }
            if (selectstudyStageCategory != null) {
                selectStudyStageTextView.setText(selectstudyStageCategory.getName());
                loadPicBooks();
            }
        }
    };
    //切换标签
    private AdapterView.OnItemClickListener selectLanguagelListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view,
                                int position, long id) {
            PicBookCategoryType categoryType = languageCategorys.get(position);
            if (categoryType == null || categoryType.getId() == null) {
                return;
            }
            if (selectLanguageCategory != null && selectLanguageCategory.getId() != null && selectLanguageCategory.getId().equals(categoryType.getId())) {
                return;
            } else {
                for (PicBookCategoryType categoryType1 : languageCategorys) {
                    categoryType1.setIsSelect(false);
                }
                categoryType.setIsSelect(true);
                resetPage();
                selectLanguageCategory = categoryType;
            }
            if (selectLanguageCategory != null) {
                selectLanguageTextView.setText(selectLanguageCategory.getName());
                loadPicBooks();
            }
        }
    };

    private void resetPage() {
        getCurrAdapterViewHelper().clearData();
        getPageHelper().clear();
    }

//    private void loadDataByCondition() {
//        if (schoolInfo != null) {
//            if (schoolInfo.getSchoolId().equals("-1")) {
//                loadDefaultPicBooks();
//            } else {
//                loadPicBooks();
//            }
//        }
//    }

    private void initSearchView() {
        ClearEditText editText = (ClearEditText) findViewById(R.id.search_keyword);
        if (editText != null) {
            editText.setHint(getString(R.string.hint_pic_book));
            editText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        hideSoftKeyboard(getActivity());
                        loadPicBooks();
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
                    getPageHelper().clear();
                    loadPicBooks();
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
                    loadPicBooks();
                }
            });
            view.setVisibility(View.VISIBLE);
        }
        view = findViewById(R.id.contacts_search_bar_layout);
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
    }

    private void initGridView() {
        final PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);
        GridView gridView = (GridView) findViewById(R.id.book_grid_view);
        if (gridView != null) {
            gridView.setNumColumns(MAX_BOOKS_PER_ROW);
            AdapterViewHelper adapterViewHelper = new NewResourcePadAdapterViewHelper(
                    getActivity(), gridView) {
                @Override
                public void loadData() {
                    loadPicBooks();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    NewResourceInfo data = (NewResourceInfo) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;

                    ImageView itemSelector = (ImageView) view.findViewById(R.id.item_selector);
                    itemSelector.setVisibility(isPick ? View.VISIBLE : View.GONE);
                    itemSelector.setImageResource(data.isSelect() ? R.drawable.select : R
                            .drawable.unselect);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        return;
                    }
                    NewResourceInfo data = (NewResourceInfo) holder.data;
                    if (data != null) {
                        if(!isPick) {
                            if (data != null) {
                                data.setIsFromSchoolResource(true);
                                ActivityUtils.openPictureDetailActivity(getActivity(),data);
                            }
                        } else {
                            checkItem(data, position);
                        }
                    }
                }
            };
            setCurrAdapterViewHelper(gridView, adapterViewHelper);
        }
    }

    private void checkItem(NewResourceInfo info, int position) {
        if (info != null) {
            info.setIsSelect(!info.isSelect());
            curPosition = position;
        }
        checkAllItems(false, position);
        getCurrAdapterViewHelper().update();
    }

    private void checkAllItems(boolean isCheck, int position) {
        List<NewResourceInfo> data = getCurrAdapterViewHelper().getData();
        if (data != null && data.size() > 0) {
            int size = data.size();
            for (int i = 0; i < size; i++) {
                NewResourceInfo info = data.get(i);
                if (info != null && i != position) {
                    info.setIsSelect(isCheck);
                }
            }
        }
    }

    public NewResourceInfo getSelectData() {
        if(getCurrAdapterViewHelper() != null) {
            List<NewResourceInfo> resourceInfos = getCurrAdapterViewHelper().getData();
            if (resourceInfos != null && resourceInfos.size() > 0) {
                if (curPosition >= 0 && curPosition < resourceInfos.size()) {
                    NewResourceInfo info = resourceInfos.get(curPosition);
                    if (info != null && info.isSelect()) {
                        return info;
                    }
                }
            }
        }
        return null;
    }
}
