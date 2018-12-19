package com.galaxyschool.app.wawaschool.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.ClassResourceListActivity;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.Note.MediaPaperActivity;
import com.galaxyschool.app.wawaschool.Note.OnlineMediaPaperActivity;
import com.galaxyschool.app.wawaschool.PictureBooksDetailActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.CampusPatrolUtils;
import com.galaxyschool.app.wawaschool.common.DateUtils;
import com.galaxyschool.app.wawaschool.common.NewResourceDeleteHelper;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.fragment.resource.NewResourceAdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.resource.NewResourcePadAdapterViewHelper;
import com.lqwawa.lqbaselib.net.library.DataResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.ClassDataStaticsInfo;
import com.galaxyschool.app.wawaschool.pojo.CourseInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfoListResult;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.TeacherDataStaticsInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.NoteOpenParams;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.sortlistview.ClearEditText;
import com.lqwawa.libs.mediapaper.MediaPaper;
import com.oosic.apps.share.ShareType;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ClassResourceListBaseFragment extends ContactsListFragment {

    public static final String TAG = ClassResourceListBaseFragment.class.getSimpleName();

    public interface Constants {
        String EXTRA_SCHOOL_ID = "schoolId";
        String EXTRA_CLASS_ID = "classId";
        String EXTRA_CHANNEL_TYPE = "channelType";
        String EXTRA_IS_TEACHER = "isTeacher";
        String EXTRA_ROLE_TYPE = "role_type";
        String EXTRA_IS_HEAD_MASTER = "isHeadMaster";
        String EXTRA_IS_HISTORY = "is_history";
        String EXTRA_CLASSINFO_TEMP_TYPE_DATA = "tempTypeData";
        String EXTRA_IS_ONLINE_SCHOOL_CLASS = "isOnlineSchoolClass";

        int CHANNEL_TYPE_HOMEWORK = NewResourceInfo.TYPE_CLASS_HOMEWORK;
        int CHANNEL_TYPE_NOTICE = NewResourceInfo.TYPE_CLASS_NOTICE;
        int CHANNEL_TYPE_SHOW = NewResourceInfo.TYPE_CLASS_SHOW;
        int CHANNEL_TYPE_COURSE = NewResourceInfo.TYPE_CLASS_COURSE;
        int CHANNEL_TYPE_LECTURE = NewResourceInfo.TYPE_CLASS_LECTURE;
    }

    protected String schoolId;
    protected String classId;
    protected boolean isTeacher;
    protected TextView keywordView;
    protected String keyword = "";
    protected int channelType;
    protected NewResourceInfoListResult resourceListResult;
    protected boolean isHeadMaster;
    protected boolean isHistory;
    //校园巡查
    private boolean isCampusPatrolTag;
    //区分从校园空间进入的临时变量
    private boolean isTempData;
    private String resourceCountStr;
    private String startDate, endDate;
    private TeacherDataStaticsInfo teacherDataStaticsInfo;
    private ClassDataStaticsInfo classDataStaticsInfo;
    private int campusPatrolResourceType;
    private String itemId = null;//标识当前选中的条目id
    public static final int REQUEST_CODE_CHANGE_NOTE_CONTENT = 108;//用于class space fragment帖子刷新。
    public static boolean hasReadMessage;
    private static boolean hasDeletedResource;
    private static boolean hasCreatedResource;
    private boolean isOnlineSchool;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_class_resource_list, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        if (getArguments() != null) {
            schoolId = getArguments().getString(Constants.EXTRA_SCHOOL_ID);
            classId = getArguments().getString(Constants.EXTRA_CLASS_ID);
            isTeacher = getArguments().getBoolean(Constants.EXTRA_IS_TEACHER);
            channelType = getArguments().getInt(Constants.EXTRA_CHANNEL_TYPE);
            isHeadMaster = getArguments().getBoolean(Constants.EXTRA_IS_HEAD_MASTER);
            isHistory = getArguments().getBoolean(Constants.EXTRA_IS_HISTORY);
            //校园巡查逻辑
            isCampusPatrolTag = getArguments().getBoolean(CampusPatrolMainFragment
                    .IS_CAMPUS_PATROL_TAG);
            resourceCountStr = getArguments().getString(CampusPatrolMainFragment
                    .CAMPUS_PATROL_RESOURCE_COUNT_STR);
            teacherDataStaticsInfo = (TeacherDataStaticsInfo) getArguments().
                    getSerializable(TeacherDataStaticsInfo.class.getSimpleName());
            classDataStaticsInfo = (ClassDataStaticsInfo) getArguments().
                    getSerializable(ClassDataStaticsInfo.class.getSimpleName());
            startDate = getArguments().getString(CampusPatrolMainFragment.
                    CAMPUS_PATROL_SCREENING_START_DATE);
            endDate = getArguments().getString(CampusPatrolMainFragment.
                    CAMPUS_PATROL_SCREENING_END_DATE);
            campusPatrolResourceType = getArguments().getInt(CampusPatrolMainFragment
                    .CAMPUS_PATROL_RESOURCE_TYPE);
            isTempData = getArguments().getBoolean(Constants.EXTRA_CLASSINFO_TEMP_TYPE_DATA, false);
            isOnlineSchool = getArguments().getBoolean(ClassResourceListActivity.EXTRA_IS_ONLINE_SCHOOL_CLASS);
        }
        initViews();
        refreshData();
    }

    private void refreshData() {
        getPageHelper().clear();
        loadResourceList();
    }

    private void updateTitleView(String countStr) {
        TextView textView = ((TextView) findViewById(R.id.contacts_header_title));
        if (textView != null) {
            if (channelType == Constants.CHANNEL_TYPE_NOTICE) {
                if (!isCampusPatrolTag) {
                    textView.setText(R.string.notices);
                } else {
                    textView.setText(getString(R.string.notices) + getString(R.string
                            .media_num, countStr));
                }
            } else if (channelType == Constants.CHANNEL_TYPE_SHOW) {
                if (!isCampusPatrolTag) {
                    if (isOnlineSchool) {
                        textView.setText(R.string.str_online_class_message);
                    } else {
                        textView.setText(R.string.shows);
                    }
                } else {
                    textView.setText(getString(R.string.shows) + getString(R.string
                            .media_num, countStr));
                }
            } else if (channelType == Constants.CHANNEL_TYPE_COURSE) {
                if (!isCampusPatrolTag) {
                    textView.setText(R.string.courses);
                } else {
                    textView.setText(getString(R.string.courses) + getString(R.string
                            .media_num, countStr));
                }
            } else if (channelType == Constants.CHANNEL_TYPE_HOMEWORK) {
                if (!isCampusPatrolTag) {
                    textView.setText(R.string.homeworks);
                } else {
                    textView.setText(getString(R.string.homeworks) + getString(R.string
                            .media_num, countStr));
                }
            } else if (channelType == Constants.CHANNEL_TYPE_LECTURE) {
                if (!isCampusPatrolTag) {
                    textView.setText(R.string.lectures);
                } else {
                    textView.setText(getString(R.string.lectures) + getString(R.string
                            .media_num, countStr));
                }
            }
        }
    }

    private void initViews() {
        updateTitleView(resourceCountStr);
        TextView textView = ((TextView) findViewById(R.id.contacts_header_right_btn));
        if (textView != null) {
            if (!isCampusPatrolTag) {
                textView.setVisibility(View.INVISIBLE);
            } else {
                //校园巡查逻辑
                textView.setVisibility(View.VISIBLE);
                textView.setText(getString(R.string.screening));
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityUtils.enterCampusPatrolPickerActivity(getActivity());
                    }
                });
            }
        }

        ClearEditText editText = (ClearEditText) findViewById(R.id.search_keyword);
        if (editText != null) {
            editText.setHint(getString(R.string.search_title_or_author));
            editText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        hideSoftKeyboard(getActivity());
                        loadResourceList();
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
                    loadResourceList();
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
                    loadResourceList();
                }
            });
            view.setVisibility(View.VISIBLE);
        }
        view = findViewById(R.id.contacts_search_bar_layout);
        if (view != null) {
            if (!isCampusPatrolTag) {
                view.setVisibility(View.VISIBLE);
            } else {
                //校园巡查隐藏搜索
                view.setVisibility(View.GONE);
            }
        }

        view = findViewById(R.id.new_btn);
        if (view != null) {
            if (!isCampusPatrolTag) {
                view.setOnClickListener(this);
                if (channelType == Constants.CHANNEL_TYPE_SHOW) {
                    view.setVisibility(View.VISIBLE);
                } else if (channelType == Constants.CHANNEL_TYPE_LECTURE) {
                    view.setVisibility(View.GONE);
                } else if (isTeacher) {
                    view.setVisibility(View.VISIBLE);
                } else {
                    view.setVisibility(View.GONE);
                }

                if (isHistory) {
                    view.setVisibility(View.GONE);
                }
            } else {
                //校园巡查逻辑
                view.setVisibility(View.GONE);
            }
        }

        PullToRefreshView pullToRefreshView = (PullToRefreshView)
                findViewById(R.id.contacts_pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);

        GridView listView = (GridView) findViewById(R.id.resource_list_view);
        if (listView != null) {
            if (channelType == Constants.CHANNEL_TYPE_LECTURE) {
                listView.setNumColumns(2);
                int padding = (int) (10 * MyApplication.getDensity());
                listView.setPadding(0, padding, 0, padding);
                //白色背景
                listView.setBackgroundColor(Color.WHITE);
                AdapterViewHelper adapterViewHelper = new NewResourcePadAdapterViewHelper(
                        getActivity(), listView) {
                    @Override
                    public void loadData() {
                        loadResourceList();
                    }

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        final NewResourceInfo data = (NewResourceInfo) getDataAdapter().getItem
                                (position);
                        if (data == null) {
                            return view;
                        }
                        //红点未读提醒
                        ImageView imageView = (ImageView) view.findViewById(R.id.resource_indicator);
                        if (imageView != null) {
                            if (!isCampusPatrolTag) {
                                imageView.setVisibility(data.isRead() ? View.GONE : View.VISIBLE);
                            } else {
                                //校园巡查需要隐藏小红点
                                imageView.setVisibility(View.GONE);
                            }
                        }
                        //删除按钮
                        final ImageView imageViewDelete = (ImageView) view.findViewById(R.id.delete_btn);
                        channelType = NewResourceInfo.TYPE_CLASS_LECTURE;
                        NewResourceDeleteHelper helper = new NewResourceDeleteHelper(getActivity(),
                                getCurrAdapterViewHelper(), NewResourceDeleteHelper.SHOW_CLASS_HOMEWORK,
                                data, imageViewDelete, channelType);
                        helper.initImageViewEvent(getMemeberId(), getString(R.string
                                .delete_lecture), isHeadMaster);
                        //校园巡查需要隐藏删除按钮
                        if (isCampusPatrolTag) {
                            imageViewDelete.setVisibility(View.GONE);
                        }

                        //优先选择更新的时间
                        TextView time = (TextView) view.findViewById(R.id.resource_time);
                        if (time != null) {
                            if (TextUtils.isEmpty(data.getUpdatedTime())) {
                                time.setText(data.getCreatedTime());
                            } else {
                                time.setText(data.getUpdatedTime());
                            }
                        }
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
                            if (data.isMicroCourse() || data.isOnePage()) {
                                ActivityUtils.openPictureDetailActivity(getActivity(), data,
                                        PictureBooksDetailActivity.FROM_OTHRE, false);
                            }
                            //校园巡查不能操作其他，只能查看。
                            if (!isCampusPatrolTag) {
//                                NoteHelper.markResourceAsRead(getActivity(), data, getMemeberId(),
//                                        getCurrAdapterViewHelper());
                                markResourceAsRead(data);
                            }
                        }
                    }
                };
                setCurrAdapterViewHelper(listView, adapterViewHelper);

            } else {
                AdapterViewHelper adapterViewHelper = new NewResourceAdapterViewHelper(
                        getActivity(), listView) {
                    @Override
                    public void loadData() {
                        loadResourceList();
                    }

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        NewResourceInfo data = (NewResourceInfo) getDataAdapter().getItem(position);
                        if (data == null) {
                            return view;
                        }
                        ImageView imageView = (ImageView) view.findViewById(R.id.resource_indicator);
                        if (imageView != null) {
                            if (!isCampusPatrolTag) {
                                imageView.setVisibility(data.isRead() ? View.GONE : View.VISIBLE);
                            } else {
                                //校园巡查需要隐藏小红点
                                imageView.setVisibility(View.GONE);
                            }
                        }
                        ImageView imageViewDelete = (ImageView) view.findViewById(R.id.resource_delete);
                        String tip = "";
                        if (channelType == Constants.CHANNEL_TYPE_NOTICE) {
                            tip = getString(R.string.delete_note);
                        } else if (channelType == Constants.CHANNEL_TYPE_SHOW) {
                            if (isOnlineSchool) {
                                tip = getString(R.string.str_delete_class_message);
                            } else {
                                tip = getString(R.string.delete_show);
                            }
                        } else if (channelType == Constants.CHANNEL_TYPE_COURSE) {
                            tip = getString(R.string.delete_course);
                        } else if (channelType == Constants.CHANNEL_TYPE_HOMEWORK) {
                            tip = getString(R.string.delete_homework);
                        } else if (channelType == Constants.CHANNEL_TYPE_LECTURE) {
                            tip = getString(R.string.delete_mic_class);
                        }
                        NewResourceDeleteHelper helper = new NewResourceDeleteHelper(getActivity(),
                                getCurrAdapterViewHelper(), NewResourceDeleteHelper.SHOW_CLASS_HOMEWORK,
                                data, imageViewDelete, channelType);
                        helper.initImageViewEvent(getMemeberId(), tip);
                        if (isOnlineSchool) {
                            helper.initImageViewEvent(tip, isTeacher || TextUtils.equals
                                            (getMemeberId(), data.getAuthorId()),
                                    channelType);
                        } else {
                            helper.initImageViewEvent(tip, isHeadMaster, channelType);
                        }
                        //校园巡查需要隐藏删除按钮
                        if (isCampusPatrolTag) {
                            imageViewDelete.setVisibility(View.GONE);
                        }
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
                            //记录当前点击的条目Id
                            itemId = data.getId();
                            //校园巡查不能操作其他，只能查看。
                            if (!isCampusPatrolTag) {
                                markResourceAsRead(data);
                            }
                        }
//                    super.onItemClick(parent, view, position, id);
                        if (data != null) {
                            //校园巡查不能操作其他，只能查看。
                            if (!isCampusPatrolTag) {
//                                NoteHelper.markResourceAsRead(getActivity(), data, getMemeberId(),
//                                        getCurrAdapterViewHelper());
                                markResourceAsRead(data);
                            }
                            int resType = data.getResourceType() % ResType.RES_TYPE_BASE;
                            if (resType == ResType.RES_TYPE_NOTE) {
                                //用于控制note详情页面编辑按钮的显示和隐藏
                                CourseInfo courseInfo = data.getCourseInfo();
                                if (courseInfo != null) {
                                    courseInfo.setCampusPatrolTag(isCampusPatrolTag);
                                    courseInfo.setIsFromShowShow(isTempData);
                                    courseInfo.setSchoolId(schoolId);
                                    courseInfo.setIsTeacher(isTeacher);
                                    courseInfo.setIsOnlineSchool(isOnlineSchool);
                                }
                                ActivityUtils.openOnlineNote(getActivity(),
                                        courseInfo, false, false);
                            } else if (data.isMicroCourse() || data.isOnePage()) {
                                ActivityUtils.openPictureDetailActivity(getActivity(), data,
                                        PictureBooksDetailActivity.FROM_OTHRE, false);
                            }
                        }
                    }
                };
                setCurrAdapterViewHelper(listView, adapterViewHelper);
            }
        }
    }

    protected void loadResourceList() {
        if (!isCampusPatrolTag) {
            loadResourceList(keywordView.getText().toString());
        } else {
            //校园巡查逻辑
            loadCampusPatrolMaterialData(keywordView.getText().toString());
        }
    }

    private void loadCampusPatrolMaterialData(String keyword) {

        keyword = keyword.trim();
        if (!keyword.equals(this.keyword)) {
            getCurrAdapterViewHelper().clearData();
            getPageHelper().clear();
        }
        this.keyword = keyword;

        Map<String, Object> params = new HashMap();
        params.put("ByType", campusPatrolResourceType); //操作,必填，1-根据班级id获取，2-根据用户id获取
        params.put("KeyWord", keyword);//关键字,选填
        params.put("ActionType", channelType);//类型区分,1作业，2通知，3风采，4.课堂，6创e学堂
        if (campusPatrolResourceType == CampusPatrolMainFragment.
                CAMPUS_PATROL_RESOURCE_TYPE_CLASS) {
            if (classDataStaticsInfo != null) {
                params.put("ClassId", classDataStaticsInfo.getClassId());//班级ID,当ByType=1必须
            }
        } else if (campusPatrolResourceType == CampusPatrolMainFragment.
                CAMPUS_PATROL_RESOURCE_TYPE_USER) {
            if (teacherDataStaticsInfo != null) {
                params.put("MemberId", teacherDataStaticsInfo.getTeacherId());//当ByType=2必须
            }
            //可选参数，按照学校id过滤。
            if (!TextUtils.isEmpty(schoolId)) {
                params.put("SchoolId", schoolId);
            }
        }
        if (!TextUtils.isEmpty(startDate)) {//时间格式：2016-12-11
            params.put("StrStartTime", startDate);//统计开始时间,非必填。
        }
        if (!TextUtils.isEmpty(endDate)) {//时间格式：2016-12-11
            params.put("StrEndTime", endDate);//统计结束时间,非必填。
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
                        if (getResult() == null || !getResult().isSuccess()
                                || getResult().getModel() == null) {
                            return;
                        }
                        updateResourceListView(getResult());
                    }
                };
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_UPLOAD_PUBLISH_LIST_LIST_URL, params, listener);
    }

    protected void loadResourceList(String keyword) {

    }

    protected void updateResourceListView(NewResourceInfoListResult result) {
        if (getPageHelper().isFetchingPageIndex(result.getModel().getPager())) {
            //获得总记录数
            int totalCount = getPageHelper().getTotalCount();
            List<NewResourceInfo> list = result.getModel().getData();
            if (list == null || list.size() <= 0) {
                if (getPageHelper().isFetchingFirstPage()) {
                    getCurrAdapterViewHelper().clearData();
                    updateTitleView(String.valueOf(totalCount));
                    TipsHelper.showToast(getActivity(),
                            getString(R.string.no_data));
                } else {
                    TipsHelper.showToast(getActivity(),
                            getString(R.string.no_more_data));
                }
                return;
            }

            if (getPageHelper().isFetchingFirstPage()) {
                getCurrAdapterViewHelper().clearData();
            }
            getPageHelper().updateByPagerArgs(result.getModel().getPager());
            getPageHelper().setCurrPageIndex(
                    getPageHelper().getFetchingPageIndex());
            if (getCurrAdapterViewHelper().hasData()) {
                int position = getCurrAdapterViewHelper().getData().size();
                if (position > 0) {
                    position--;
                }
                getCurrAdapterViewHelper().getData().addAll(list);
                getCurrAdapterView().setSelection(position);
                resourceListResult.getModel().setData(getCurrAdapterViewHelper().getData());
            } else {
                getCurrAdapterViewHelper().setData(list);
                resourceListResult = result;
            }
            updateTitleView(String.valueOf(totalCount));
        }
    }

    protected void markResourceAsRead(NewResourceInfo data) {
        if (getUserInfo() == null || data.isRead()) {
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("Id", data.getId());
        RequestHelper.RequestListener listener =
                new RequestHelper.RequestListener<DataResult>(
                        getActivity(), DataResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()) {
                            return;
                        }
                        NewResourceInfo data = (NewResourceInfo) getTarget();
                        data.setIsRead(true);
                        //设置阅读标志位
                        setHasReadMessage(true);
                        getCurrAdapterViewHelper().update();
                    }
                };
        listener.setTarget(data);
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.MARK_MY_RESOURCE_AS_READ_URL, params, listener);
    }

    public static void setHasReadMessage(boolean hasReadMessage) {
        ClassResourceListBaseFragment.hasReadMessage = hasReadMessage;
    }

    public static boolean isHasReadMessage() {
        return hasReadMessage;
    }

    public static void setHasDeletedResource(boolean hasDeletedResource) {
        ClassResourceListBaseFragment.hasDeletedResource = hasDeletedResource;
    }

    public static boolean hasDeletedResource() {
        return hasDeletedResource;
    }

    public static void setHasCreatedResource(boolean hasCreatedResource) {
        ClassResourceListBaseFragment.hasCreatedResource = hasCreatedResource;
    }

    public static boolean hasCreatedResource() {
        return hasCreatedResource;
    }

    private int transferType(int channelType) {
        int shareType = -1;
        switch (channelType) {
            case Constants.CHANNEL_TYPE_NOTICE:
                shareType = ShareType.SHARE_TYPE_NOTICE;
                break;
            case Constants.CHANNEL_TYPE_HOMEWORK:
                shareType = ShareType.SHARE_TYPE_HOMEWORK;
                break;
            case Constants.CHANNEL_TYPE_SHOW:
                shareType = ShareType.SHARE_TYPE_COMMENT;
                break;
            case Constants.CHANNEL_TYPE_COURSE:
                shareType = ShareType.SHARE_TYPE_COURSE;
                break;
        }
        return shareType;
    }

    private void createNewResource() {
        long dateTime = System.currentTimeMillis();
        File noteFile = new File(Utils.NOTE_FOLDER, String.valueOf(dateTime));
        String dateTimeStr = DateUtils.transferLongToDate("yyyy-MM-dd HH:mm:ss", dateTime);
        NoteOpenParams params = new NoteOpenParams(noteFile.getPath(), dateTimeStr,
                MediaPaperActivity.OPEN_TYPE_EDIT, MediaPaper.PAPER_TYPE_TIEBA, null,
                MediaPaperActivity.SourceType.CLASS_SPACE, false);
        params.schoolId = schoolId;
        params.classId = classId;
        int shareType = transferType(channelType);
        if (shareType > 0) {
            params.shareType = shareType;
            ActivityUtils.openLocalNote(getActivity(), params,
                    CampusPatrolPickerFragment.CREATE_NEW_RESOURCE_REQUEST_CODE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.new_btn) {
            createNewResource();
        } else {
            super.onClick(v);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data != null) {
            if (resultCode == CampusPatrolPickerFragment.RESULT_CODE) {
                if (requestCode == CampusPatrolPickerFragment.REQUEST_CODE) {
                    this.startDate = CampusPatrolUtils.getStartDate(data);
                    this.endDate = CampusPatrolUtils.getEndDate(data);
                    refreshData();
                }
            }
        } else {
            //刷新帖子
            if (requestCode == CampusPatrolPickerFragment.EDIT_NOTE_DETAILS_REQUEST_CODE) {

                //帖子打开后返回列表，需要手动更新阅读人数（手动累加）
                updateReaderNumber(itemId);

                //帖子内容改变需要刷新
                if (OnlineMediaPaperActivity.hasContentChanged()) {
                    OnlineMediaPaperActivity.setHasContentChanged(false);
                    //刷新帖子
                    refreshData();
                }
            } else if (requestCode == CampusPatrolPickerFragment.CREATE_NEW_RESOURCE_REQUEST_CODE) {
                if (MediaPaperActivity.hasResourceSended()) {
                    MediaPaperActivity.setHasResourceSended(false);
                    //设置标志位
                    setHasCreatedResource(true);
                    //创建资源返回code
                    refreshData();
                }
            }
        }
    }

    /**
     * 更新阅读人数
     *
     * @param itemId
     */
    private void updateReaderNumber(String itemId) {
        if (TextUtils.isEmpty(itemId)) {
            return;
        }
        AdapterViewHelper helper = getCurrAdapterViewHelper();
        if (helper != null && helper.hasData()) {
            List<NewResourceInfo> infoList = helper.getData();
            if (infoList != null && infoList.size() > 0) {
                for (NewResourceInfo info : infoList) {
                    if (info != null) {
                        String id = info.getId();
                        if (!TextUtils.isEmpty(id) && id.equals(itemId)) {
                            //找到刚才点击的那个条目(id是唯一的，position不靠谱)，增加阅读数。
                            info.setReadNumber(info.getReadNumber() + 1);
                            break;
                        }
                    }
                }
            }
            //更新一下布局
            helper.update();
        }
    }

}
