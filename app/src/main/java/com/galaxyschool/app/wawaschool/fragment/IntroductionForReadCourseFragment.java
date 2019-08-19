package com.galaxyschool.app.wawaschool.fragment;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.Request;
import com.galaxyschool.app.wawaschool.ContactsPickerActivity;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.Note.MediaPaperActivity;
import com.galaxyschool.app.wawaschool.PersonalPostBarListActivity;
import com.galaxyschool.app.wawaschool.PickerClassAndGroupActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.WatchWawaCourseResourceListPickerActivity;
import com.galaxyschool.app.wawaschool.adapter.ListenReadAndWriteCourseAdapter;
import com.galaxyschool.app.wawaschool.adapter.WatchWawaCourseListAdapter;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.CallbackListener;
import com.galaxyschool.app.wawaschool.common.CampusPatrolUtils;
import com.galaxyschool.app.wawaschool.common.DateUtils;
import com.galaxyschool.app.wawaschool.common.StudyTaskUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.common.UploadReourceHelper;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.common.WatchWawaCourseResourceOpenUtils;
import com.galaxyschool.app.wawaschool.common.WatchWawaCourseResourceSplicingUtils;
import com.galaxyschool.app.wawaschool.common.WawaCourseUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.db.NoteDao;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.imagebrowser.GalleryActivity;
import com.lqwawa.client.pojo.StudyResPropType;
import com.lqwawa.intleducation.base.utils.DisplayUtil;
import com.lqwawa.intleducation.module.discovery.ui.CourseSelectItemFragment;
import com.lqwawa.intleducation.module.discovery.ui.LQCourseCourseListActivity;
import com.lqwawa.intleducation.module.learn.vo.SectionResListVo;
import com.lqwawa.lqbaselib.net.ThisStringRequest;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.DataResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.Emcee;
import com.galaxyschool.app.wawaschool.pojo.LookResDto;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.NoteInfo;
import com.galaxyschool.app.wawaschool.pojo.PPTAndPDFCourseInfo;
import com.galaxyschool.app.wawaschool.pojo.PPTAndPDFCourseInfoCode;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.ResourceInfoTag;
import com.galaxyschool.app.wawaschool.pojo.ScoreFormula;
import com.galaxyschool.app.wawaschool.pojo.ScoreFormulaListResult;
import com.galaxyschool.app.wawaschool.pojo.ShortSchoolClassInfo;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;
import com.galaxyschool.app.wawaschool.pojo.UploadCourseType;
import com.galaxyschool.app.wawaschool.pojo.UploadParameter;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseUploadResult;
import com.galaxyschool.app.wawaschool.pojo.weike.LocalCourseInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.NoteOpenParams;
import com.galaxyschool.app.wawaschool.pojo.weike.PlaybackParam;
import com.galaxyschool.app.wawaschool.pojo.weike.SplitCourseInfo;
import com.galaxyschool.app.wawaschool.views.DatePopupView;
import com.galaxyschool.app.wawaschool.views.SelectBindChildPopupView;
import com.galaxyschool.app.wawaschool.views.ToolbarTopView;
import com.libs.gallery.ImageInfo;
import com.lqwawa.apps.views.ContainsEmojiEditText;
import com.lqwawa.client.pojo.ResourceInfo;
import com.lqwawa.libs.mediapaper.MediaPaper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.oosic.apps.iemaker.base.BaseUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 布置任务综合界面
 */

public class IntroductionForReadCourseFragment extends ContactsListFragment
        implements SelectBindChildPopupView.OnRelationChangeListener {
    public static final String TAG = IntroductionForReadCourseFragment.class.getSimpleName();
    private View rootView, confirm;
    private EditText editTitle, edittContent, limitWordFrom, limitWordTo;
    private ImageView appointCourse, appointIcon, connectCourse, connectIcon;
    private TextView evalTextView;
    private ToolbarTopView toolbarTopView;
    private String headTitle, titleContent, taskContent;
    private UploadParameter uploadParameter;
    private String workOrderId;
    private String resourceUrl;
    private String connectThumbnail;
    private boolean isRemote;
    private LocalCourseInfo localCourseInfo;
    public static int ISLOCALCOURSECHANGE = 100;
    public static String UPDATETHUMBIAL = "updateThumbial";
    private boolean titleToasted;
    private boolean contentToasted;
    private boolean isFromPersonalLibrary;
    private Date defaultDate;
    private String startDateStr;
    private String endDateStr;
    private TextView startDateView;
    private TextView endDateView;
    private LinearLayout commitTaskLayout;
    private TextView commitTaskView;
    private boolean isCommit = false;
    private int taskType;
    private TextView scoreFormule;
    //打分公式弹出来之后需要的参数
    private int position = 0;
    String[] childNameArray;
    //放置打分班级的Id
    private int[] childFormulaID;
    private int markFormulaId;
    public static final int REQUEST_CODE_MEDIAPAPER = 101;
    private NoteInfo noteInfo;
    private NoteOpenParams mOpenParams;
    public static final String ASSOCIATE_TASK_ORDER = "Associate_task_order";//导读任务单

    //看课件支持多任务
    private View courseLayout;
    private GridView courseGridView;//课件列表
    private GridView listenGridView;
    private GridView readWriteGridView;
    private WatchWawaCourseListAdapter courseListAdapter;
    private List<ResourceInfoTag> resourceInfoTagList = new ArrayList<>();
    private ImageView addCoursewareImageView;
    private View originAddLayout;
    public static final int REQUEST_CODE_PICKER_RESOURCES = 888; //选取资源
    public static final String FROM_LQ_PROGRAM = "from_lq_program";//来自lq学程
    private RadioButton mRbMarkYes;
    private RadioButton mRbMarkNo;
    private RadioButton mRbPercentageSystem, mRbTenSystem;
    private View mSelectMark;
    private RadioButton immediatelyRb;//立即发布
    private LinearLayout publishTimeAndTypeLayout;
    private int currentStudyType;//空中课堂学习类型
    private Emcee onlineRes;
    private List<ShortSchoolClassInfo> schoolClassInfos;
    //听说adapter
    private ListenReadAndWriteCourseAdapter listenAdapter;
    //读写adapter
    private ListenReadAndWriteCourseAdapter readAndWriteAdapter;
    //听说的数据
    private List<ResourceInfoTag> listenData = new ArrayList<>();
    //读写的数据
    private List<ResourceInfoTag> readWriteData = new ArrayList<>();
    private boolean isFromSuperTask;
    private int superTaskType;
    private boolean isOnlineClass;
    private String classId;
    private String schoolId;
    private boolean isFirstIn = true;
    private boolean isTempData;
    private boolean isAutoMark = false;
    private boolean needScore = false;
    private LinearLayout llMark;
    private boolean multipleDoTask;//任务多选

    public interface EditType {
        int titleType = 0;
        int contentType = 1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_introduction_for_read_course, null);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getIntent();
        initViews();
        checkClassPlayEnd();
    }

    public interface deleteIntroType {
        int appoint = 0;
        int connect = 1;
    }

    private void getIntent() {
        if (getArguments() != null) {
            taskType = getArguments().getInt(ActivityUtils.EXTRA_TASK_TYPE);
            headTitle = getArguments().getString(ActivityUtils.EXTRA_HEADER_TITLE);
            defaultDate = (Date) getArguments().getSerializable(ActivityUtils.EXTRA_DEFAULT_DATE);
            currentStudyType = getArguments().getInt(ActivityUtils.EXTRA_STDUY_TYPE);
            onlineRes = (Emcee) getArguments().getSerializable(ActivityUtils.EXTRA_DATA_INFO);
            schoolClassInfos = (List<ShortSchoolClassInfo>) getArguments().getSerializable(ActivityUtils.EXTRA_SCHOOL_INFO_LIST_DATA);
            isFromSuperTask = getArguments().getBoolean(ActivityUtils.EXTRA_FROM_SUPER_TASK);
            if (!isFromSuperTask
                    && (taskType == StudyTaskType.RETELL_WAWA_COURSE
                    || taskType == StudyTaskType.TASK_ORDER
                    || taskType == StudyTaskType.Q_DUBBING)) {
                //读写单、听说课支持多选
                isFromSuperTask = true;
                multipleDoTask = true;
            }
            if (isFromSuperTask) {
                superTaskType = taskType;
                if (taskType == StudyTaskType.TASK_ORDER
                        || taskType == StudyTaskType.RETELL_WAWA_COURSE
                        || taskType == StudyTaskType.WATCH_HOMEWORK
                        || taskType == StudyTaskType.SUBMIT_HOMEWORK
                        || taskType == StudyTaskType.Q_DUBBING) {
                    taskType = StudyTaskType.LISTEN_READ_AND_WRITE;
                }
                uploadParameter = (UploadParameter) getArguments().getSerializable(UploadParameter
                        .class.getSimpleName());
                if (uploadParameter != null) {
                    isTempData = uploadParameter.isTempData();
                }
            }
            isOnlineClass = getArguments().getBoolean(ActivityUtils.EXTRA_IS_ONLINE_CLASS);
            classId = getArguments().getString(ActivityUtils.EXTRA_CLASS_ID);
            schoolId = getArguments().getString(ActivityUtils.EXTRA_SCHOOL_ID);
        }
    }

    private void initViews() {
        toolbarTopView = (ToolbarTopView) findViewById(R.id.toolbar_top_view);
        if (toolbarTopView != null) {
            if (headTitle != null) {
                toolbarTopView.getTitleView().setText(headTitle);
            } else {
                toolbarTopView.getTitleView().setText("");
            }
            toolbarTopView.getBackView().setOnClickListener(this);
        }
        editTitle = (EditText) findViewById(R.id.title_text);
        editTitle.addTextChangedListener(new TextWatchChangeListenter(40, titleToasted, EditType.titleType));
        edittContent = (EditText) findViewById(R.id.student_task_content);
        edittContent.addTextChangedListener(new TextWatchChangeListenter(500, contentToasted,
                EditType.contentType));
        appointCourse = (ImageView) findViewById(R.id.appoint_add);
        appointCourse.setOnClickListener(this);
        appointIcon = (ImageView) findViewById(R.id.appoint_icon);
        appointIcon.setOnClickListener(this);
        connectCourse = (ImageView) findViewById(R.id.connent_course);
        connectCourse.setOnClickListener(this);
        connectIcon = (ImageView) findViewById(R.id.connect_icon);
        connectIcon.setOnClickListener(this);
        confirm = findViewById(R.id.comfirm_commit);
        confirm.setOnClickListener(this);
        evalTextView = (TextView) findViewById(R.id.tv_eval_text);

        startDateView = (TextView) findViewById(R.id.study_task_start_date_text);
        endDateView = (TextView) findViewById(R.id.study_task_end_date_text);
        commitTaskLayout = (LinearLayout) findViewById(R.id.commit_task_layout);
        commitTaskView = (TextView) findViewById(R.id.commit_task_view);
        startDateView.setOnClickListener(this);
        endDateView.setOnClickListener(this);
        commitTaskView.setOnClickListener(this);
        //初始化任务类型的作答方式
        immediatelyRb = (RadioButton) findViewById(R.id.rb_publish_right_now);
        publishTimeAndTypeLayout = (LinearLayout) findViewById(R.id.ll_publish_time_and_type);
        if (isFromSuperTask && !multipleDoTask) {
            //综合任务不显示
            publishTimeAndTypeLayout.setVisibility(View.GONE);
        }

        if (taskType == StudyTaskType.ENGLISH_WRITING) {//英文写作
            findViewById(R.id.ll_appoint_course).setVisibility(View.GONE);
            findViewById(R.id.rl_appoint_course).setVisibility(View.GONE);
            findViewById(R.id.ll_english_write).setVisibility(View.VISIBLE);

            TextView textView = (TextView) findViewById(R.id.tv_title);
            textView.setText(getString(R.string.article_title));
            textView = (TextView) findViewById(R.id.tv_content);
//            textView.setText(getString(R.string.article_request));
            scoreFormule = (TextView) findViewById(R.id.score_formula_click);
            scoreFormule.setOnClickListener(this);
            //打分公式
            scoreFormule = (TextView) findViewById(R.id.score_formula_click);
            scoreFormule.setOnClickListener(this);
            //字数限制
            limitWordFrom = (ContainsEmojiEditText) findViewById(R.id.limit_from);
            limitWordTo = (ContainsEmojiEditText) findViewById(R.id.limit_to);
            loadScoreFormulaData();
        } else if (taskType == StudyTaskType.TOPIC_DISCUSSION) {//话题讨论
            findViewById(R.id.ll_appoint_course).setVisibility(View.GONE);
            findViewById(R.id.rl_appoint_course).setVisibility(View.GONE);
        } else if (taskType == StudyTaskType.WATCH_HOMEWORK && !isFromSuperTask) {//其他
            findViewById(R.id.commit_task_layout).setVisibility(View.VISIBLE);
            TextView view = (TextView) findViewById(R.id.tv_appoint_course);
            view.setText(getString(R.string.appoint_task));
        } else if (taskType == StudyTaskType.INTRODUCTION_WAWA_COURSE) {//导读
            findViewById(R.id.ll_connect_task_list).setVisibility(View.VISIBLE);
            findViewById(R.id.re_connect_task_list).setVisibility(View.VISIBLE);
            setIntroTypeTitle(getString(R.string.appoint_course_point));
        } else if (taskType == StudyTaskType.TASK_ORDER) {//做任务单
            initMarkSore();
            setIntroTypeTitle(getString(R.string.pls_add_work_task));
//            findViewById(R.id.ll_connect_task_list).setVisibility(View.VISIBLE);
//            findViewById(R.id.re_connect_task_list).setVisibility(View.VISIBLE);
//            TextView view = (TextView) findViewById(R.id.tv_str_select);
//            view.setText(getString(R.string.forcedchoice));
        } else if (taskType == StudyTaskType.RETELL_WAWA_COURSE) {
            initMarkSore();
            setIntroTypeTitle(getString(R.string.appoint_course_point));
        } else if (taskType == StudyTaskType.LISTEN_READ_AND_WRITE) {
            //配置听说+读写数据
            if (isFromSuperTask) {
                if (isOtherHomeWork()) {
                    findViewById(R.id.commit_task_layout).setVisibility(View.VISIBLE);
                } else {
                    initMarkSore();
                }
            } else {
                initMarkSore();
            }
            configTSDXData();
        }

        if (defaultDate == null) {
            defaultDate = new Date();
        }
        String dateStr = DateUtils.getDateStr(defaultDate, "yyyy-MM-dd");
        Date endDate = DateUtils.getNextDate(defaultDate);
        startDateStr = dateStr;
        endDateStr = DateUtils.getDateStr(endDate, "yyyy-MM-dd");
        startDateView.setText(startDateStr);
        endDateView.setText(endDateStr);
        //用于返回时更新数据
        if (uploadParameter != null) {
            upDateData();
        }
        if (workOrderId != null) {
            connectCourse.setScaleType(ImageView.ScaleType.CENTER_CROP);
            connectIcon.setVisibility(View.VISIBLE);
            if (connectThumbnail != null) {
                MyApplication.getThumbnailManager(getActivity()).displayThumbnailWithDefault(
                        connectThumbnail, connectCourse, R.drawable.default_cover, 65, 65);
            } else {
                connectCourse.setImageResource(R.drawable.default_cover);
            }
        }
        //用户返回时显示保存的内容
        upDataEdittContent();
        //看课件调用的自身layout
        initCourseLayout();
    }

    private void setIntroTypeTitle(String typeTitle) {
        TextView typeView = (TextView) findViewById(R.id.tv_appoint_course);
        if (typeView != null) {
            typeView.setText(typeTitle);
        }
    }

    /**
     * 打分功能
     */
    private void initMarkSore() {
        llMark = (LinearLayout) findViewById(R.id.ll_mark);
        llMark.setVisibility(View.VISIBLE);
        //评分标准
        mSelectMark = findViewById(R.id.ll_select_mark);
        //是
        mRbMarkYes = (RadioButton) findViewById(R.id.rb_mark_yes);
        //否
        mRbMarkNo = (RadioButton) findViewById(R.id.rb_mark_no);
        //百分制
        mRbPercentageSystem = (RadioButton) findViewById(R.id.rb_percentage_system);
        //十分制
        mRbTenSystem = (RadioButton) findViewById(R.id.rb_ten_system);

        String strPercent = getString(R.string.str_percentage_system);
        String strTen = getString(R.string.str_system_of_ten);

        ForegroundColorSpan colorSpan = new ForegroundColorSpan(getResources().getColor(R.color.com_text_gray));
        RelativeSizeSpan sizeSpan = new RelativeSizeSpan(0.8f);
        SpannableString spannableString = new SpannableString(new StringBuilder(strPercent).append(" ").append(getString(R.string.str_manfei)));
        spannableString.setSpan(colorSpan, strPercent.length(), spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(sizeSpan, strPercent.length(), spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        mRbPercentageSystem.setText(spannableString);

        spannableString = new SpannableString(new StringBuilder(strTen).append(" ").append(getString(R.string.str_ten_info)));
        spannableString.setSpan(colorSpan, strTen.length(), spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(sizeSpan, strTen.length(), spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        mRbTenSystem.setText(spannableString);

        mRbMarkYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isAutoMark) {
                    needScore = isChecked;
                } else if (superTaskType != StudyTaskType.Q_DUBBING){
                    mSelectMark.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
                }
            }
        });
        if (isAutoMark) {
            mRbMarkYes.postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateScoreView(View.GONE);
                }
            }, 1);
        }
    }

    private void initCourseLayout() {
        courseLayout = findViewById(R.id.layout_course_ware_list);
        if (courseLayout != null) {
            if (isWatchWawaCourse() || taskType == StudyTaskType.ENGLISH_WRITING) {
                //看课件显示
                courseLayout.setVisibility(View.VISIBLE);
            } else {
                courseLayout.setVisibility(View.GONE);
            }
        }
        TextView courseTitleView = (TextView) findViewById(R.id.tv_course_ware_title);
        TextView titleHintView = (TextView) findViewById(R.id.tv_add_course_ware_hint);
        if (taskType == StudyTaskType.ENGLISH_WRITING){
            //指定课件(非必选)
            courseTitleView.setText(getString(R.string.appoint_course_point));
            String hint = getString(R.string.str_not_required) + getString(R.string.course_ware_list_join_hint);
            titleHintView.setText(hint);
        }
        courseGridView = (GridView) findViewById(R.id.common_grid_view);
        if (courseGridView != null) {
            courseGridView.setNumColumns(1);
            courseListAdapter = new WatchWawaCourseListAdapter(getActivity(),
                    resourceInfoTagList,
                    result -> {
                if (taskType == StudyTaskType.ENGLISH_WRITING){
                    addCoursewareImageView.setVisibility(View.VISIBLE);
                }
            });
            courseGridView.setAdapter(courseListAdapter);
        }
        addCoursewareImageView = (ImageView) findViewById(R.id.iv_add_course_ware);
        if (addCoursewareImageView != null) {
            addCoursewareImageView.setOnClickListener(this);
        }
        //添加课件的layout
        originAddLayout = findViewById(R.id.layout_origin_add);
        if (originAddLayout != null) {
            if (isWatchWawaCourse()
                    || taskType == StudyTaskType.LISTEN_READ_AND_WRITE
                    || taskType == StudyTaskType.ENGLISH_WRITING) {
                originAddLayout.setVisibility(View.GONE);
            } else {
                originAddLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private void configTSDXData() {
        //听说+读写
        LinearLayout listenReadAndWriteLayout = (LinearLayout) findViewById(R.id.layout_listen_read_and_write);
        listenReadAndWriteLayout.setVisibility(View.VISIBLE);
        View listenLayout = getActivity().getLayoutInflater().inflate(R.layout
                .layout_add_course_res, listenReadAndWriteLayout, false);
        TextView titleTextView = (TextView) listenLayout.findViewById(R.id.tv_appoint_course);
        if (isOtherHomeWork()) {
            titleTextView.setText(getString(R.string.appoint_task));
        } else {
            titleTextView.setText(getString(R.string.appoint_course_point));
        }

        if (superTaskType == StudyTaskType.RETELL_WAWA_COURSE
                || superTaskType == StudyTaskType.Q_DUBBING) {
            findViewById(R.id.common_grid_view).setVisibility(View.GONE);
            listenGridView = (GridView) listenLayout.findViewById(R.id.gv_task);
            listenGridView.setVisibility(View.VISIBLE);
            listenGridView.setNumColumns(1);
            //设置分割线
            listenGridView.setVerticalSpacing(DisplayUtil.dip2px(getActivity(), 1));
            listenGridView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.light_gray));
        } else {
            listenGridView = (GridView) listenLayout.findViewById(R.id.common_grid_view);
            listenGridView.setNumColumns(4);
            listenGridView.setVerticalSpacing(20);
        }
        if (listenData.size() == 0) {
            listenData.add(new ResourceInfoTag());
        }
        listenAdapter = new ListenReadAndWriteCourseAdapter(getActivity(),
                listenData, superTaskType, result -> {
            //删除
            int position = (int) result;
            listenData.remove(position);
            listenAdapter.notifyDataSetChanged();
            updateGridViewHeight(true);
            boolean flag = showScoreView(true);
            updateScoreView(flag ? View.GONE : View.VISIBLE);
            updateEvalCourseViewData();
        }, result -> {
            //打开
            int position = (int) result;
            openListenData(listenAdapter.getItem(position));
        });
        listenAdapter.setCallListener(result -> {
            updateEvalCourseViewData();
        });
        listenGridView.setAdapter(listenAdapter);
        View readWriteLayout = getActivity().getLayoutInflater().inflate(R.layout
                .layout_add_course_res, listenReadAndWriteLayout, false);
        titleTextView = (TextView) readWriteLayout.findViewById(R.id.tv_appoint_course);
        titleTextView.setText(getString(R.string.pls_add_work_task_point));
//        if (superTaskType == StudyTaskType.TASK_ORDER) {
//            findViewById(R.id.common_grid_view).setVisibility(View.GONE);
//            readWriteGridView = (GridView) readWriteLayout.findViewById(R.id.gv_task);
//            readWriteGridView.setVisibility(View.VISIBLE);
//            readWriteGridView.setNumColumns(1);
//            //设置分割线
//            readWriteGridView.setVerticalSpacing(DisplayUtil.dip2px(getActivity(), 1));
//            readWriteGridView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.light_gray));
//        } else {
            readWriteGridView = (GridView) readWriteLayout.findViewById(R.id.common_grid_view);
            readWriteGridView.setNumColumns(4);
            readWriteGridView.setVerticalSpacing(20);
//        }
        if (readWriteData.size() == 0) {
            readWriteData.add(new ResourceInfoTag());
        }
        readAndWriteAdapter = new ListenReadAndWriteCourseAdapter(getActivity(), readWriteData, superTaskType,
                result -> {
                    int position = (int) result;
                    readWriteData.remove(position);
                    readAndWriteAdapter.notifyDataSetChanged();
//                    updateGridViewHeight(false);
                    boolean flag = showScoreView(false);
                    updateScoreView(flag ? View.GONE : View.VISIBLE);
                    if (hasPointData()) {
                        mSelectMark.setVisibility(View.GONE);
                    } else {
                        if (mRbMarkYes.isChecked()) {
                            mSelectMark.setVisibility(View.VISIBLE);
                        }
                    }
                }, result -> {
            //打开
            int position = (int) result;
            openReadAndWriteData(readAndWriteAdapter.getItem(position));
        });
        readWriteGridView.setAdapter(readAndWriteAdapter);
        if (isFromSuperTask) {
            if (superTaskType == StudyTaskType.RETELL_WAWA_COURSE
                    || isOtherHomeWork()
                    || superTaskType == StudyTaskType.Q_DUBBING) {
                listenReadAndWriteLayout.addView(listenLayout);
            } else if (superTaskType == StudyTaskType.TASK_ORDER) {
                listenReadAndWriteLayout.addView(readWriteLayout);
            }
        } else {
            listenReadAndWriteLayout.addView(listenLayout);
            listenReadAndWriteLayout.addView(readWriteLayout);
        }
    }

    private void openListenData(ResourceInfoTag info) {
        if (TextUtils.isEmpty(info.getResId())) {
            //添加资源
            getEditContent();
            if (isOtherHomeWork()) {
                chooseOtherHomeWork();
            } else {
                chooseResources(false);
            }
        } else {
            if (isOtherHomeWork()) {
                ActivityUtils.openOnlineNote(getActivity(), info.toNewResourceInfo()
                                .getCourseInfo(), false,
                        false);
            } else {
                //打开选中的资源
                WatchWawaCourseResourceOpenUtils.openResource(getActivity(), info,
                        true, false, true);
            }
        }
    }

    private void openReadAndWriteData(ResourceInfoTag info) {
        if (TextUtils.isEmpty(info.getResId())) {
            //添加资源
            getEditContent();
            chooseResources(true);
        } else {
            if (info.getResourceType() == ResType.RES_TYPE_STUDY_CARD) {
                loadCourseDetail(info.getResId(), false);
            } else {
                //打开选中的资源
                WatchWawaCourseResourceOpenUtils.openResource(getActivity(), info,
                        true, false, true);
            }
        }
    }

    private boolean showScoreView(boolean isListenData) {
        if (superTaskType == StudyTaskType.Q_DUBBING){
            return true;
        }
        if (taskType == StudyTaskType.TASK_ORDER
                || taskType == StudyTaskType.RETELL_WAWA_COURSE
                || superTaskType == StudyTaskType.RETELL_WAWA_COURSE
                || superTaskType == StudyTaskType.TASK_ORDER) {
            for (ResourceInfoTag tag : isListenData ? listenData : readWriteData) {
                if (tag.isSelected()) {
                    return true;
                }
                if (!TextUtils.isEmpty(tag.getPoint())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasPointData() {
        for (ResourceInfoTag tag : readWriteData) {
            if (!TextUtils.isEmpty(tag.getPoint())) {
                return true;
            }
        }
        return false;
    }

    private void chooseOtherHomeWork() {
        int remainCount = 10;
        if (listenData != null){
            remainCount = 11 - listenData.size();
        }
        if (remainCount <= 0){
            TipsHelper.showToast(getActivity(),getString(R.string.str_max_select_limit));
            return;
        }
        Bundle args = new Bundle();
        args.putString(PersonalPostBarListActivity.EXTRA_MEMBER_ID, getMemeberId());
        args.putBoolean(ActivityUtils.EXTRA_IS_PICK, true);
        args.putInt(ActivityUtils.EXTRA_SELECT_MAX_COUNT, remainCount);
        args.putInt(ActivityUtils.EXTRA_TASK_TYPE,superTaskType);
        Intent intent = new Intent(getActivity(), PersonalPostBarListActivity.class);
        intent.putExtras(args);
        startActivityForResult(intent, REQUEST_CODE_PICKER_RESOURCES);
    }

    private boolean isWatchWawaCourse() {
        return taskType == StudyTaskType.WATCH_WAWA_COURSE;
    }

    private boolean isOtherHomeWork() {
        return superTaskType == StudyTaskType.WATCH_HOMEWORK || superTaskType == StudyTaskType
                .SUBMIT_HOMEWORK;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (isFirstIn && isFromSuperTask) {
            //综合任务的二次编辑走此方法（仅仅一次）
            if (uploadParameter != null) {
                showExistData();
            }
            isFirstIn = false;
        }
        if (uploadParameter != null) {
            ImageLoader.getInstance().clearMemoryCache();
            upDateData();
        }
        if (CampusPatrolUtils.hasStudyTaskAssigned()) {
            //布置作业完成，需要刷新页面。
            finish();
        }
    }

    private void showExistData() {
        if (editTitle != null) {
            editTitle.setText(uploadParameter.getFileName());
        }
        if (edittContent != null) {
            edittContent.setText(uploadParameter.getDisContent());
        }
        if (!TextUtils.isEmpty(uploadParameter.getStartDate())) {
            startDateStr = uploadParameter.getStartDate();
            startDateView.setText(startDateStr);
        }
        if (!TextUtils.isEmpty(uploadParameter.getEndDate())) {
            endDateStr = uploadParameter.getEndDate();
            endDateView.setText(endDateStr);
        }
        if (uploadParameter.getTaskType() == StudyTaskType.ENGLISH_WRITING) {
            int wordCountMin = uploadParameter.getWordCountMin();
            int wordCountMax = uploadParameter.getWordCountMax();
            if (wordCountMax > 0) {
                limitWordFrom.setText(String.valueOf(wordCountMin));
                limitWordTo.setText(String.valueOf(wordCountMax));
            }
            this.resourceInfoTagList.addAll(getResourceData());
            if (courseListAdapter != null) {
                courseListAdapter.update(this.resourceInfoTagList);
            }
            if (resourceInfoTagList != null && resourceInfoTagList.size() > 0){
                addCoursewareImageView.setVisibility(View.GONE);
            }
        } else if (isOtherHomeWork()) {
            setListenData(getResourceData(), true);
            if (uploadParameter.getTaskType() == StudyTaskType.SUBMIT_HOMEWORK) {
                isCommit = true;
            } else {
                isCommit = false;
            }
            updateTaskCommitView(isCommit);
        } else if (uploadParameter.getTaskType() == StudyTaskType.TASK_ORDER) {
            setReadWriteData(getResourceData(), true);
            configMarkData();
        } else if (uploadParameter.getTaskType() == StudyTaskType.RETELL_WAWA_COURSE
                || uploadParameter.getTaskType() == StudyTaskType.Q_DUBBING) {
            setListenData(getResourceData(), true);
            configMarkData();
        } else if (uploadParameter.getTaskType() == StudyTaskType.WATCH_WAWA_COURSE) {
            //看课件的处理方式
            this.resourceInfoTagList.addAll(getResourceData());
            if (courseListAdapter != null) {
                courseListAdapter.update(this.resourceInfoTagList);
            }
        }
    }

    private void configMarkData() {
        if (uploadParameter.NeedScore) {
            mRbMarkYes.setChecked(true);
            if (uploadParameter.ScoringRule == 2) {
                mRbPercentageSystem.setChecked(true);
                mRbTenSystem.setChecked(false);
            } else {
                mRbPercentageSystem.setChecked(false);
                mRbTenSystem.setChecked(true);
            }
        }
    }

    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.toolbar_top_back_btn) {
            UIUtils.hideSoftKeyboard(getActivity());
            finish();
        } else if (v.getId() == R.id.appoint_add) {
            getEditContent();

            if (uploadParameter == null) {
                if (taskType == StudyTaskType.RETELL_WAWA_COURSE || taskType == StudyTaskType.TASK_ORDER) {
                    //复述课件去选取资源（新版） 复用看课件的选取资源
                    chooseResources(false);
                } else {
                    fetchWawaCourse(getArguments().getInt(ActivityUtils.EXTRA_TASK_TYPE), false);
                }
            } else {
                openWawaCourse();
            }

        } else if (v.getId() == R.id.appoint_icon) {
            updateScoreView(View.VISIBLE);
            deleteChoosedImage(deleteIntroType.appoint);
        } else if (v.getId() == R.id.connent_course) {
            if (workOrderId == null || workOrderId.equals("")) {
                fetchWawaCourse(StudyTaskType.TASK_ORDER, true);
//                fetchIntroductionCourse(getArguments().getInt(ActivityUtils.EXTRA_TASK_TYPE));
            } else {
                playTaskOrderCourse();
            }

        } else if (v.getId() == R.id.connect_icon) {
            deleteChoosedImage(deleteIntroType.connect);
        } else if (v.getId() == R.id.comfirm_commit) {
            if (isWatchWawaCourse()) {
                uploadWawaCourse();
            } else {
                commitEdittData();
            }
        } else if (v.getId() == R.id.study_task_start_date_text) {
            if (TextUtils.isEmpty(startDateStr)) {
                return;
            }
            UIUtils.hideSoftKeyboard(getActivity());
            DatePopupView stateDatePopView = new DatePopupView(getActivity(), startDateStr, new DatePopupView.OnDateChangeListener() {
                @Override
                public void onDateChange(String dateStr) {
                    if (!startDateStr.equals(dateStr)) {
                        startDateStr = dateStr;
                        startDateView.setText(dateStr);
                    }
                }
            });
            stateDatePopView.showAtLocation(getView(), Gravity.BOTTOM, 0, 0);
        } else if (v.getId() == R.id.study_task_end_date_text) {
            if (TextUtils.isEmpty(endDateStr)) {
                return;
            }
            UIUtils.hideSoftKeyboard(getActivity());
            DatePopupView endDatePopView = new DatePopupView(getActivity(), endDateStr, new DatePopupView.OnDateChangeListener() {
                @Override
                public void onDateChange(String dateStr) {
                    if (!endDateStr.equals(dateStr)) {
                        endDateStr = dateStr;
                        endDateView.setText(dateStr);
                    }
                }
            });
            endDatePopView.showAtLocation(getView(), Gravity.BOTTOM, 0, 0);
        } else if (v.getId() == R.id.commit_task_view) {
            isCommit = !isCommit;
            updateTaskCommitView(isCommit);
        } else if (v.getId() == R.id.score_formula_click) {
            checkScoreFormula();
        } else if (v.getId() == R.id.iv_add_course_ware) {
            //看课件选取资源
            chooseResources(false);
        }
    }

    /**
     * 选择资源
     * checkAppointTaskOrder 听说读写状态下选取任务单
     */
    private void chooseResources(boolean checkAppointTaskOrder) {
        Intent intent = new Intent(getActivity(), WatchWawaCourseResourceListPickerActivity.class);
        Bundle args = getArguments();
        if (isWatchWawaCourse()) {
            //传递新版看课件类型
            args.putInt(ActivityUtils.EXTRA_TASK_TYPE, StudyTaskType.NEW_WATACH_WAWA_COURSE);
        } else {
            args.putInt(ActivityUtils.EXTRA_TASK_TYPE, taskType);
        }
        args.putBoolean(ActivityUtils.EXTRA_CHOOSE_TASKORDER_DATA, checkAppointTaskOrder);
        args.putBoolean(ActivityUtils.EXTRA_IS_PICK, true);
        if (isFromSuperTask) {
            if (taskType == StudyTaskType.ENGLISH_WRITING){
                args.putInt(ActivityUtils.EXTRA_SELECT_MAX_COUNT, 1);
                args.putInt(ActivityUtils.EXTRA_SUPER_TASK_TYPE, StudyTaskType.NEW_WATACH_WAWA_COURSE);
            } else {
                int remainCount = 10;
                if (superTaskType == StudyTaskType.TASK_ORDER){
                    if (readWriteData != null){
                        remainCount = 11 - readWriteData.size();
                    }
                } else {
                    if (listenData != null){
                        remainCount = 11 - listenData.size();
                    }
                }
                if (remainCount <= 0){
                    TipsHelper.showToast(getActivity(),getString(R.string.str_max_select_limit));
                    return;
                }
                args.putInt(ActivityUtils.EXTRA_SELECT_MAX_COUNT, remainCount);
                args.putInt(ActivityUtils.EXTRA_SUPER_TASK_TYPE, superTaskType);
            }
            args.putBoolean(ActivityUtils.EXTRA_FROM_SUPER_TASK, isFromSuperTask);
        }
        intent.putExtras(args);
        startActivityForResult(intent, REQUEST_CODE_PICKER_RESOURCES);
    }

    /**
     * 上传看课件多任务
     */
    private void uploadWawaCourse() {
        getEditContent();
        //日期
        boolean isOk = checkDate();
        if (!isOk) {
            return;
        }
        //标题
        if (TextUtils.isEmpty(titleContent)) {
            TipMsgHelper.ShowLMsg(getActivity(), getString(R.string.title_cannot_null));
            return;
        }
        //选择课件
        if (resourceInfoTagList.size() == 0) {
            TipMsgHelper.ShowLMsg(getActivity(), getString(R.string.course_ware_list_empty_tips));
            return;
        }
        //上传
        if (uploadParameter == null) {
            //设置teacherId和teacherName的
            uploadParameter = UploadReourceHelper.getUploadParameter(getUserInfo(), null, null,
                    null, 1);
        }
        uploadParameter.setFileName(titleContent);//标题
        uploadParameter.setTaskType(taskType);
        uploadParameter.setDisContent(taskContent);
        uploadParameter.setDescription(taskContent);
        uploadParameter.setStartDate(startDateStr);
        uploadParameter.setEndDate(endDateStr);
        //获取资源对象list
        List<LookResDto> lookResDtoList = Utils.getWatchWawaCourseLookResDtoList(
                resourceInfoTagList, isFromSuperTask);
        uploadParameter.setLookResDtoList(lookResDtoList);
        UIUtils.hideSoftKeyboard(getActivity());
        if (isFromSuperTask) {
            uploadParameter.setTempData(isTempData);
            returnSelectData();
            return;
        }
        uploadParameter.setSubmitType(immediatelyRb.isChecked() ? 0 : 1);
        if (onlineRes != null) {
            //来自空中课堂的布置学习任务
            publishWatchWawaCourseStudyTask(uploadParameter, schoolClassInfos);
        } else {
            enterContactsPicker(uploadParameter);
        }
    }

    private void fetchWawaCourse(int taskType, boolean flag) {
        //保存输入在editText中的内容
//        getEditContent();
        if (taskType == StudyTaskType.WATCH_HOMEWORK || taskType == StudyTaskType.SUBMIT_HOMEWORK) {
            commitHomework(taskType);
            return;
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        Fragment fragment = getFragmentManager().findFragmentByTag(CoursePickerFragment.TAG);
        if (fragment != null) {
            getFragmentManager().beginTransaction().remove(fragment);
        }
        fragment = new CoursePickerFragment();
        Bundle args = new Bundle();
        args.putBoolean(ActivityUtils.EXTRA_IS_PICK, true);
        if (flag) {
            args.putBoolean(ASSOCIATE_TASK_ORDER, true);
        } else {
            args.putBoolean(ASSOCIATE_TASK_ORDER, false);
        }
        args.putInt(ActivityUtils.EXTRA_TASK_TYPE, taskType);
        args.putSerializable(ActivityUtils.EXTRA_DEFAULT_DATE, DateUtils.getCurDate());
        args.putString(ActivityUtils.EXTRA_CLASS_ID, classId);
        args.putString(ActivityUtils.EXTRA_SCHOOL_ID, schoolId);
        args.putBoolean(ActivityUtils.EXTRA_IS_ONLINE_CLASS, isOnlineClass);
        fragment.setArguments(args);
        ft.add(R.id.activity_body, fragment, CoursePickerFragment.TAG);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void fetchIntroductionCourse(int type) {
        getEditContent();
        Bundle args = getArguments();
        ActivityUtils.enterTaskOrderFragment(getFragmentManager(), args);

    }

    //播放任务单课件
    private void playTaskOrderCourse() {
        if (!TextUtils.isEmpty(workOrderId)) {
            loadCourseDetail(workOrderId, false);
        }
    }

    public void loadCourseDetail(String resId, final boolean isFinish) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("resId", resId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringBuilder builder = new StringBuilder();
        builder.append("?j=" + jsonObject.toString());
        String url = ServerUrl.WAWATV_COURSE_DETAIL_URL + builder.toString();
        ThisStringRequest request = new ThisStringRequest(
                Request.Method.GET, url, new Listener<String>() {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                if (jsonString != null) {
                    CourseUploadResult uploadResult = JSON.parseObject(
                            jsonString,
                            CourseUploadResult.class);
                    if (uploadResult != null && uploadResult.code == 0) {
                        CourseData courseData = uploadResult.getData().get(0);
                        if (courseData != null) {
//                            prepareOpenCourse(23,String.valueOf(courseData.id));
                            NewResourceInfo newResourceInfo = courseData.getNewResourceInfo();
                            if (newResourceInfo != null) {
                                PlaybackParam playbackParam = new PlaybackParam();
                                playbackParam.mIsHideCollectTip = true;
                                ActivityUtils.openOnlineOnePage(getActivity(), newResourceInfo,
                                        true, playbackParam);
                            }
                        }
                    } else {
                        TipMsgHelper.ShowLMsg(getActivity(), R.string.resource_not_exist);
                        if (isFinish && getActivity() != null) {
                            getActivity().finish();
                        }
                    }
                }
            }

            @Override
            public void onError(NetroidError error) {
                if (getActivity() == null) {
                    return;
                }
                TipMsgHelper.ShowLMsg(getActivity(), R.string.resource_not_exist);
                if (isFinish && getActivity() != null) {
                    getActivity().finish();
                }
            }
        });
        request.addHeader("Accept-Encoding", "*");
        request.start(getActivity());
    }

    private void prepareOpenCourse(int type, String courseId) {
        WawaCourseUtils utils = new WawaCourseUtils(getActivity());
        utils.loadSplitLearnCardDetail(courseId, true);
        utils.setOnSplitCourseDetailFinishListener(new WawaCourseUtils.OnSplitCourseDetailFinishListener() {
            @Override
            public void onSplitCourseDetailFinish(SplitCourseInfo info) {
                if (info != null) {
                    CourseData courseData = info.getCourseData();
                    if (courseData == null) return;
                    NewResourceInfo newResourceInfo = courseData.getNewResourceInfo();
                    if (newResourceInfo == null) return;
                    ActivityUtils.enterTaskOrderDetailActivity(getActivity(), newResourceInfo);

                }
            }
        });
    }

    public void setData(UploadParameter uploadParameter) {
        this.uploadParameter = uploadParameter;
    }

    public void upDateData() {
        if (taskType == StudyTaskType.WATCH_HOMEWORK
                || taskType == StudyTaskType.SUBMIT_HOMEWORK) {
            if (uploadParameter != null) {
                uploadParameter.setTaskType(!isCommit ? StudyTaskType.WATCH_HOMEWORK :
                        StudyTaskType.SUBMIT_HOMEWORK);
            }
        }
        if (isCommit) {
            commitTaskLayout.setVisibility(uploadParameter.getTaskType() == StudyTaskType
                    .SUBMIT_HOMEWORK ? View.VISIBLE : View.GONE);
        } else {
            commitTaskLayout.setVisibility(uploadParameter.getTaskType() == StudyTaskType
                    .WATCH_HOMEWORK ? View.VISIBLE : View.GONE);
        }

        String appointImageUrl = null;
        appointIcon.setVisibility(View.VISIBLE);
        appointCourse.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //根据类型判断是否是本机课件
        if (uploadParameter.getCourseData() != null) {
            isRemote = false;
            appointImageUrl = uploadParameter.getCourseData().thumbnailurl;
        } else {
            isRemote = true;
            appointImageUrl = uploadParameter.getThumbPath();
        }
        if (appointImageUrl != null) {
            MyApplication.getThumbnailManager(getActivity()).displayThumbnailWithDefault(
                    appointImageUrl, appointCourse, R.drawable.default_cover, 65, 65);
        } else {
            appointCourse.setImageResource(R.drawable.default_cover);
        }

        if (taskType == StudyTaskType.RETELL_WAWA_COURSE
                && TextUtils.equals("1", uploadParameter.getCourseData().getResproperties())) {
            evalTextView.setVisibility(View.VISIBLE);
        } else if (taskType == StudyTaskType.TASK_ORDER
                && !TextUtils.isEmpty(uploadParameter.getCourseData().point)) {
            evalTextView.setVisibility(View.VISIBLE);
        } else {
            evalTextView.setVisibility(View.GONE);
        }

    }

    public void updateAppointTaskOrderData() {
        if (workOrderId != null) {
            connectCourse.setScaleType(ImageView.ScaleType.CENTER_CROP);
            connectIcon.setVisibility(View.VISIBLE);
            if (connectThumbnail != null) {
                MyApplication.getThumbnailManager(getActivity()).displayThumbnailWithDefault(
                        connectThumbnail, connectCourse, R.drawable.default_cover, 65, 65);
            } else {
                connectCourse.setImageResource(R.drawable.default_cover);
            }
        }
    }

    public void deleteChoosedImage(int type) {
        if (type == deleteIntroType.appoint) {
            if (taskType == StudyTaskType.WATCH_HOMEWORK && noteInfo != null) {
                Utils.safeDeleteDirectory(uploadParameter.getFilePath());
                NoteDao noteDao = new NoteDao(getActivity());
                try {
                    noteDao.deleteNoteDTOByDateTime(noteInfo.getDateTime(), 0);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            appointCourse.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            appointCourse.setImageResource(R.drawable.add_course_icon);
            appointIcon.setVisibility(View.GONE);
            evalTextView.setVisibility(View.GONE);
            uploadParameter = null;
            isFromPersonalLibrary = false;
            localCourseInfo = null;
            if (taskType == StudyTaskType.TASK_ORDER) {
                workOrderId = null;
                resourceUrl = null;
            }
        } else if (type == deleteIntroType.connect) {
            connectCourse.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            connectCourse.setImageResource(R.drawable.add_course_icon);
            connectIcon.setVisibility(View.GONE);
            if (taskType == StudyTaskType.TASK_ORDER) {
                uploadParameter = null;
            }
            workOrderId = null;
            resourceUrl = null;
        }
    }

    public void upDataEdittContent() {
        if (titleContent != null) {
            editTitle.setText(titleContent);
        }
        if (taskContent != null) {
            edittContent.setText(taskContent);
        }
    }

    public void commitEdittData() {
        getEditContent();
        boolean isOk = checkDate();
        if (!isOk) {
            return;
        }
        if (titleContent == null || titleContent.equals("")) {
            TipMsgHelper.ShowLMsg(getActivity(), getString(R.string.title_cannot_null));
            return;
        }
        if (taskType == StudyTaskType.ENGLISH_WRITING
                || taskType == StudyTaskType.TOPIC_DISCUSSION
                || taskType == StudyTaskType.LISTEN_READ_AND_WRITE) {
            uploadParameter = UploadReourceHelper.getUploadParameter(getUserInfo(), null, null, null, 1);
        }
        if (uploadParameter == null) {
            if (taskType == StudyTaskType.TASK_ORDER) {
                TipMsgHelper.ShowLMsg(getActivity(), R.string.pls_add_task);
            } else if (taskType == StudyTaskType.WATCH_HOMEWORK) {
                TipMsgHelper.ShowLMsg(getActivity(), R.string.pls_add_work);
            } else {
                TipMsgHelper.ShowLMsg(getActivity(), getString(R.string.appoint_cousrse_cannot_null));
            }
            return;
        }

        if (TextUtils.isEmpty(taskContent)) {
            if (taskType == StudyTaskType.ENGLISH_WRITING) {
//                TipMsgHelper.ShowLMsg(getActivity(), R.string.pls_input_article_request);
                TipMsgHelper.ShowLMsg(getActivity(), R.string.pls_input_description);
                return;
            } else {
//                TipMsgHelper.ShowLMsg(getActivity(), R.string.pls_input_description);
            }

        }

        //这里用来区分英文写作和其他界面的上传的参数
        if (taskType == StudyTaskType.ENGLISH_WRITING) {
            //作文要求
            uploadParameter.setWritingRequire(taskContent);
            //打分公式
            if (markFormulaId == 0) {
                TipMsgHelper.ShowLMsg(getActivity(), R.string.network_error);
                return;
            }
            uploadParameter.setMarkFormula(markFormulaId);
            //作文字数最小值
            String workMin = limitWordFrom.getText().toString().trim();
            if (!TextUtils.isEmpty(workMin)) {
                uploadParameter.setWordCountMin(Integer.valueOf(workMin));
            }
            //作文字数最大值
            String workMax = limitWordTo.getText().toString().trim();
            if (!TextUtils.isEmpty(workMax)) {
                uploadParameter.setWordCountMax(Integer.valueOf(workMax));
            }
            //如果最小值大于最大值给于提示
            boolean flag = compareMinorMax(workMin, workMax);
            if (!flag) {
                TipMsgHelper.ShowLMsg(getActivity(), R.string.min_cannot_more_than_max);
                return;
            }
            List<LookResDto> lookResDtoList = Utils.getWatchWawaCourseLookResDtoList(
                    resourceInfoTagList, isFromSuperTask);
            if (lookResDtoList != null && lookResDtoList.size() > 0) {
                uploadParameter.setLookResDtoList(lookResDtoList);
            }
        } else if (taskType == StudyTaskType.INTRODUCTION_WAWA_COURSE) {
            uploadParameter.setWorkOrderId(workOrderId);
            uploadParameter.setWorkOrderUrl(resourceUrl);
        } else if (taskType == StudyTaskType.LISTEN_READ_AND_WRITE) {
            if (isFromSuperTask) {
                if (superTaskType == StudyTaskType.RETELL_WAWA_COURSE
                        || superTaskType == StudyTaskType.Q_DUBBING) {
                    if (listenData == null || listenData.size() <= 1) {
                        TipMsgHelper.ShowMsg(getActivity(), getString(R.string.appoint_cousrse_cannot_null));
                        return;
                    }
                } else if (superTaskType == StudyTaskType.TASK_ORDER) {
                    if (readWriteData == null || readWriteData.size() <= 1) {
                        TipMsgHelper.ShowMsg(getActivity(), getString(R.string.str_appoint_task_order_not_null));
                        return;
                    }
                } else if (isOtherHomeWork()) {
                    if (listenData == null || listenData.size() <= 1) {
                        TipMsgHelper.ShowMsg(getActivity(), getString(R.string.str_homework_not_null));
                        return;
                    }
                }
            } else {
                if (listenData == null || listenData.size() <= 1) {
                    TipMsgHelper.ShowLMsg(getActivity(), getString(R.string.appoint_cousrse_cannot_null));
                    return;
                } else if (readWriteData == null || readWriteData.size() <= 1) {
                    TipMsgHelper.ShowLMsg(getActivity(), getString(R.string.str_appoint_task_order_not_null));
                    return;
                }
            }
            List<LookResDto> lookResDtos = null;
            if (isFromSuperTask && isOtherHomeWork()) {
                superTaskType = isCommit ? StudyTaskType.SUBMIT_HOMEWORK : StudyTaskType.WATCH_HOMEWORK;
                lookResDtos = getListenReadAndWriteData(true);
            } else {
                lookResDtos = getListenReadAndWriteData(false);
            }
            if (isFromSuperTask && lookResDtos.size() > 10) {
                TipMsgHelper.ShowMsg(getActivity(), R.string.str_max_select_limit);
                return;
            }
            uploadParameter.setLookResDtoList(lookResDtos);
        }
        uploadParameter.setDisContent(taskContent);
        uploadParameter.setStartDate(startDateStr);
        uploadParameter.setDescription(taskContent);
        uploadParameter.setEndDate(endDateStr);
        uploadParameter.setFileName(titleContent);
        if (taskType == StudyTaskType.WATCH_HOMEWORK || taskType == StudyTaskType.SUBMIT_HOMEWORK) {
            uploadParameter.setTaskType(!isCommit ? StudyTaskType.WATCH_HOMEWORK :
                    StudyTaskType.SUBMIT_HOMEWORK);
        } else {
            if (isFromSuperTask && isOtherHomeWork()) {
                superTaskType = isCommit ? StudyTaskType.SUBMIT_HOMEWORK : StudyTaskType.WATCH_HOMEWORK;
            }
            uploadParameter.setTaskType(isFromSuperTask ? superTaskType : taskType);
        }

        if (isAutoMark) {
            uploadParameter.NeedScore = true;
            uploadParameter.ScoringRule = 2;
        } else {
            addMarkScore(uploadParameter);//打分
        }
//            popStack();
        UIUtils.hideSoftKeyboard(getActivity());

        //针对复述课件的图片的处理方式
        setRetellImageCourseData();
//        if (multipleDoTask) {
            //多选的读写单和听说课
            configMultipleBaseData();
//        }
        if (isFromSuperTask && !multipleDoTask) {
            uploadParameter.setTempData(isTempData);
            //综合任务的返回
            returnSelectData();
            return;
        }
        //发布类型
        uploadParameter.setSubmitType(immediatelyRb.isChecked() ? 0 : 1);
        if (onlineRes != null) {
            //空中课堂的布置学习任务
            enterSendOnlineStudyTask();
        } else {
            enterContactsPicker(uploadParameter);
        }
    }

    private void returnSelectData() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(UploadParameter.class.getSimpleName(), uploadParameter);
        intent.putExtras(bundle);
        getActivity().setResult(ActivityUtils.REQUEST_CODE_ADD_RELATION_INFO, intent);
        finish();
    }

    private void configMultipleBaseData() {
        List<LookResDto> resDtos = uploadParameter.getLookResDtoList();
        if (resDtos != null && resDtos.size() > 0) {
            LookResDto dto = resDtos.get(0);
            CourseData courseData = new CourseData();
            courseData.code = dto.getAuthor();
            courseData.resourceurl = dto.getResUrl();
            uploadParameter.setResPropType(dto.getResPropType());
            uploadParameter.setResCourseId(dto.getResCourseId());
            uploadParameter.setCourseId(dto.getCourseId());
            uploadParameter.setCourseTaskType(dto.getCourseTaskType());
            String resId = dto.getResId();
            if (!TextUtils.isEmpty(resId)) {
                if (resId.contains("-")) {
                    String[] array = resId.split("-");
                    if (resId.contains(",") && taskType == StudyTaskType.ENGLISH_WRITING){
                        //已经包含了逗号
                        courseData.resId = resId;
                        courseData.resourceurl = dto.getResUrl();
                        uploadParameter.setType(ResType.RES_TYPE_IMG);
                    } else {
                        courseData.id = Integer.valueOf(array[0]);
                        courseData.type = Integer.valueOf(array[1]);
                        uploadParameter.setType(courseData.type);
                        if (courseData.type == ResType.RES_TYPE_IMG) {
                            List<ResourceInfo> SplitInfoList = dto.getSplitInfoList();
                            if (SplitInfoList != null && SplitInfoList.size() > 0) {
                                courseData.resId = StudyTaskUtils.getPicResourceData(SplitInfoList, false,
                                        false, true);
                                courseData.resourceurl = StudyTaskUtils.getPicResourceData(SplitInfoList,
                                        true, false, false);
                                courseData.code = StudyTaskUtils.getPicResourceData(SplitInfoList,
                                        false, true, false);
                            }
                        }
                    }
                }
            }
            uploadParameter.setCourseData(courseData);
        }
    }

    /**
     * 打分
     *
     * @param uploadParameter
     */
    private void addMarkScore(UploadParameter uploadParameter) {
        if (taskType == StudyTaskType.TASK_ORDER
                || taskType == StudyTaskType.RETELL_WAWA_COURSE
                || taskType == StudyTaskType.LISTEN_READ_AND_WRITE) {
            //听说课  任务单
            if (mRbMarkYes != null && mRbPercentageSystem != null) {
                uploadParameter.NeedScore = mRbMarkYes.isChecked();
                uploadParameter.ScoringRule = mRbPercentageSystem.isChecked() ? 2 : 1;
            }
        }
    }

    /**
     * 设置图片的ImageUrl  和 resourceUrl
     */
    private void setRetellImageCourseData() {
        if (taskType == StudyTaskType.RETELL_WAWA_COURSE || taskType == StudyTaskType.TASK_ORDER) {
            int courseType = uploadParameter.getType();
            if (courseType == ResType.RES_TYPE_IMG) {
                CourseData courseData = uploadParameter.getCourseData();
                ResourceInfoTag resourceInfoTag = uploadParameter.getNewResourceInfoTag();
                if (courseData != null && resourceInfoTag != null) {
                    String resUrl = "";
                    String authorId = "";
                    String resId = "";
                    List<ResourceInfo> resourceInfos = resourceInfoTag.getSplitInfoList();
                    if (resourceInfos != null && resourceInfos.size() > 0) {
                        for (int i = 0; i < resourceInfos.size(); i++) {
                            ResourceInfo info = resourceInfos.get(i);
                            if (i == 0) {
                                resUrl = info.getResourcePath();
                                authorId = info.getAuthorId();
                                resId = info.getResId();
                            } else {
                                resUrl = resUrl + "," + info.getResourcePath();
                                authorId = authorId + "," + info.getAuthorId();
                                resId = resId + "," + info.getResId();
                            }
                        }
                    }
                    if (!TextUtils.isEmpty(resUrl) && !TextUtils.isEmpty(authorId)) {
                        courseData.resourceurl = resUrl;
                        courseData.code = authorId;
                        courseData.resId = resId;
                        uploadParameter.setCourseData(courseData);
                    }
                }
            }
        }
    }

    private void getEditContent() {
        titleContent = editTitle.getText().toString().trim();
        taskContent = edittContent.getText().toString().trim();
    }

    /**
     * 组装指定课件和指定读写单数据
     */
    private List<LookResDto> getListenReadAndWriteData(boolean isOtherHomeWork) {
        List<LookResDto> lookResDtos = new ArrayList<>();
        LookResDto lookResDto;
        for (int i = 0, len = listenData.size() - 1; i < len; i++) {
            ResourceInfoTag info = listenData.get(i);
            lookResDto = new LookResDto();
            lookResDto.setResId(info.getResId());
            lookResDto.setCourseResType(info.getResourceType());
            lookResDto.setResTitle(info.getTitle());
            lookResDto.setAuthor(info.getAuthorId());
            lookResDto.setResUrl(info.getResourcePath());
            lookResDto.setResProperties(info.getResProperties());
            if (isOtherHomeWork || superTaskType == StudyTaskType.Q_DUBBING) {
                lookResDto.setTaskId(superTaskType);
            } else {
                lookResDto.setTaskId(5);
            }
            lookResDto.setImgPath(info.getImgPath());
            lookResDto.setSplitInfoList(info.getSplitInfoList());
            lookResDto.setAuthorName(info.getAuthorName());
            lookResDto.setCreateTime(info.getCreateTime());
            lookResDto.setResCourseId(info.getResCourseId());
            lookResDto.setIsSelect(info.isSelected());
            lookResDto.setPoint(info.getPoint());
            if (!TextUtils.isEmpty(info.getPoint()) && info.getResPropertyMode() == 1) {
                lookResDto.setResPropType(1);
            } else if (superTaskType == StudyTaskType.Q_DUBBING){
                lookResDto.setResPropType(info.getResPropType());
            }
            lookResDto.setResPropertyMode(info.getResPropertyMode());
            lookResDto.setCompletionMode(info.getCompletionMode());
            lookResDto.setCourseTaskType(info.getCourseTaskType());
            lookResDto.setCourseId(info.getCourseId());
            lookResDtos.add(lookResDto);
        }
        for (int i = 0, len = readWriteData.size() - 1; i < len; i++) {
            ResourceInfoTag info = readWriteData.get(i);
            lookResDto = new LookResDto();
            lookResDto.setResId(info.getResId());
            lookResDto.setCourseResType(info.getResourceType());
            lookResDto.setResTitle(info.getTitle());
            lookResDto.setAuthor(info.getAuthorId());
            lookResDto.setResUrl(info.getResourcePath());
            if (isOtherHomeWork || superTaskType == StudyTaskType.Q_DUBBING) {
                lookResDto.setTaskId(superTaskType);
            } else {
                lookResDto.setTaskId(8);
            }
            lookResDto.setImgPath(info.getImgPath());
            lookResDto.setSplitInfoList(info.getSplitInfoList());
            lookResDto.setAuthorName(info.getAuthorName());
            lookResDto.setCreateTime(info.getCreateTime());
            lookResDto.setResCourseId(info.getResCourseId());
            lookResDto.setIsSelect(info.isSelected());
            lookResDto.setPoint(info.getPoint());
            if (!TextUtils.isEmpty(info.getPoint()) && info.getResPropertyMode() == 1) {
                lookResDto.setResPropType(1);
            } else if (superTaskType == StudyTaskType.Q_DUBBING){
                lookResDto.setResPropType(info.getResPropType());
            }
            lookResDto.setResPropertyMode(info.getResPropertyMode());
            lookResDto.setCompletionMode(info.getCompletionMode());
            lookResDto.setCourseTaskType(info.getCourseTaskType());
            lookResDto.setCourseId(info.getCourseId());
            lookResDtos.add(lookResDto);
        }
        return lookResDtos;
    }

    private List<ResourceInfoTag> getResourceData() {
        List<ResourceInfoTag> resourceInfoTags = new ArrayList<>();
        List<LookResDto> lookResDtos = uploadParameter.getLookResDtoList();
        ResourceInfoTag infoTag;
        if (lookResDtos != null && lookResDtos.size() > 0) {
            for (int i = 0, len = lookResDtos.size(); i < len; i++) {
                LookResDto info = lookResDtos.get(i);
                infoTag = new ResourceInfoTag();
                infoTag.setResId(info.getResId());
                infoTag.setResourceType(info.getCourseResType());
                infoTag.setTitle(info.getResTitle());
                infoTag.setAuthorId(info.getAuthor());
                infoTag.setResourcePath(info.getResUrl());
                infoTag.setTaskId(info.getTaskId() + "");
                infoTag.setSplitInfoList(info.getSplitInfoList());
                infoTag.setImgPath(info.getImgPath());
                infoTag.setResProperties(info.getResProperties());
                infoTag.setAuthorName(info.getAuthorName());
                infoTag.setCreateTime(info.getCreateTime());
                infoTag.setResCourseId(info.getResCourseId());
                infoTag.setIsSelected(info.isSelect());
                infoTag.setPoint(info.getPoint());
                infoTag.setResPropertyMode(info.getResPropertyMode());
                infoTag.setCompletionMode(info.getCompletionMode());
                infoTag.setResPropType(info.getResPropType());
                infoTag.setCourseId(info.getCourseId());
                infoTag.setCourseTaskType(info.getCourseTaskType());
                resourceInfoTags.add(infoTag);
            }
        }
        return resourceInfoTags;
    }

    public void setWorkOrderId(String workOrderId) {
        this.workOrderId = workOrderId;
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    public void setConnectThumbnail(String connectThumbnail) {
        this.connectThumbnail = connectThumbnail;
    }

    public void setLocalCourseInfo(LocalCourseInfo localCourseInfo) {
        this.localCourseInfo = localCourseInfo;
    }

    public void setReadWriteData(List<ResourceInfoTag> readWriteData, boolean isSuperTask) {
        // 只对新加入数据处理自动批阅标识
        if (!readWriteData.isEmpty()) {
            for (ResourceInfoTag item : readWriteData) {
                if (item != null && !TextUtils.isEmpty(item.getPoint()) && !isSuperTask) {
                    item.setResPropertyMode(1);
                }
            }
        }
        int length = this.readWriteData.size() - 1;
        this.readWriteData.addAll(length, readWriteData);
        boolean flag = false;
        boolean pointFlag = false;
        for (ResourceInfoTag tag : this.readWriteData) {
            //来自学程
            if (tag.isSelected() && !flag) {
                if (!isSuperTask) {
                    TipMsgHelper.ShowMsg(getActivity(), R.string.str_show_select_lqcourse_mark_score_tip);
                }
                flag = true;
            }

            //兼容point
            if (!TextUtils.isEmpty(tag.getPoint())) {
                pointFlag = true;
            }
        }

        if (flag || pointFlag){
            updateScoreView(View.GONE);
            if (pointFlag){
                mSelectMark.setVisibility(View.GONE);
            }
        }

        if (readAndWriteAdapter != null) {
            readAndWriteAdapter.notifyDataSetChanged();
//            updateGridViewHeight(false);
        }
    }

    public void setListenData(List<ResourceInfoTag> listenData, boolean isSuperTask) {
        boolean isContain = false;
        if (taskType == StudyTaskType.RETELL_WAWA_COURSE) {
            for (ResourceInfoTag tag : listenData) {
                if (TextUtils.equals("1", tag.getResProperties())) {
                    //评测课件 默认 复述 + 评测
                    if (!isSuperTask) {
                        tag.setCompletionMode(2);
                    }
                    isContain = true;
                }
            }
        }
        int length = this.listenData.size() - 1;
        this.listenData.addAll(length, listenData);
        boolean flag = true;
        for (ResourceInfoTag tag : this.listenData) {
            if (tag.isSelected() && flag) {
                if (!isSuperTask) {
                    TipMsgHelper.ShowMsg(getActivity(), R.string.str_show_select_lqcourse_mark_score_tip);
                }
                updateScoreView(View.GONE);
                flag = false;
            }
            if (superTaskType == StudyTaskType.Q_DUBBING){
                //配音
                if (!isSuperTask) {
                    if (tag.getResPropType() == StudyResPropType.DUBBING_BY_WHOLE) {

                    } else {
                        tag.setResPropType(StudyResPropType.DUBBING_BY_SENTENCE);
                    }
                }
                updateScoreView(View.GONE);
            }
        }

        if (flag){
            //没有学程的资源
            updateEvalCourseViewData();
        } else if (isContain){
            updateScoreView(View.GONE);
            mRbTenSystem.setVisibility(View.VISIBLE);
        }

        if (listenAdapter != null) {
            listenAdapter.notifyDataSetChanged();
            updateGridViewHeight(true);
        }
    }

    private void updateEvalCourseViewData(){
        if (taskType == StudyTaskType.RETELL_WAWA_COURSE) {
            if (listenData != null && listenData.size() > 0) {
                boolean isSelect = false;
                boolean isEval = false;
                for (ResourceInfoTag tag : listenData) {
                    if (tag.isSelected()) {
                        isSelect = true;
                    }
                    if (TextUtils.equals("1", tag.getResProperties()) && (tag.getCompletionMode() == 3
                            || tag.getCompletionMode() == 2)) {
                        isEval = true;
                    }
                }
                if (!isSelect) {
                    if (isEval) {
                        mRbMarkYes.setChecked(true);
                        mRbMarkNo.setVisibility(View.INVISIBLE);
                    } else {
                        mRbMarkNo.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    private void openWawaCourse() {
        if (taskType == StudyTaskType.WATCH_HOMEWORK || taskType == StudyTaskType.SUBMIT_HOMEWORK) {
            commitHomework(taskType);
            return;
        }
//        else if (taskType == StudyTaskType.TASK_ORDER) {//预览任务单
//            playTaskOrderCourse();
//            return;
//        }
        if (isRemote) {
//            openLocalCourse();
            //本地
            playCourse();
        } else {
            //在线
            CourseData data = uploadParameter.getCourseData();
            if (data != null) {
                int resType = data.type;
                if (resType >= ResType.RES_TYPE_BASE) {
                    resType = resType % ResType.RES_TYPE_BASE;
                }
                if (data.getNewResourceInfo() != null) {
                    NewResourceInfo resourceInfo = data.getNewResourceInfo();
                    resourceInfo.setAuthorName(uploadParameter.getCreateName());
                    playRemoteResource(resourceInfo, resType);
                }
            }
        }
    }

    private void playRemoteResource(NewResourceInfo resourceInfo, int resType) {
        PlaybackParam playbackParam = new PlaybackParam();
        playbackParam.mIsHideCollectTip = true;
        if (resourceInfo.isOnePage() || resourceInfo.isStudyCard()) {
            //有声相册  和 任务单
            ActivityUtils.openOnlineOnePage(getActivity(), resourceInfo, false, playbackParam);
        } else if (resourceInfo.isMicroCourse()) {
            //微课
            ActivityUtils.playOnlineCourse(getActivity(), resourceInfo.getCourseInfo(),
                    false, playbackParam);
        } else if (resType == ResType.RES_TYPE_PDF || resType == ResType.RES_TYPE_PPT || resType
                == ResType.RES_TYPE_DOC) {
            //ppt 和 pdf
            openPPDAndPDFDetails(resourceInfo);
        } else if (resType == ResType.RES_TYPE_IMG) {
            //打开图片
            openImage(uploadParameter.getNewResourceInfoTag());
        }
    }

    /**
     * 打开ppt或者pdf
     *
     * @param newInfo
     */
    private void openPPDAndPDFDetails(final NewResourceInfo newInfo) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("resId", newInfo.getIdType());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringBuilder builder = new StringBuilder();
        builder.append("?j=" + jsonObject.toString());
        String url = ServerUrl.WAWATV_COURSE_DETAIL_URL + builder.toString();
        final ThisStringRequest request = new ThisStringRequest(
                Request.Method.GET, url, new Listener<String>() {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                if (TextUtils.isEmpty(jsonString)) {
                    return;
                }
                PPTAndPDFCourseInfoCode result = com.alibaba.fastjson.JSONObject.parseObject
                        (jsonString, PPTAndPDFCourseInfoCode.class);
                if (result != null) {
                    List<ImageInfo> resourceInfoList = new ArrayList<>();
                    List<PPTAndPDFCourseInfo> splitCourseInfo = result.getData();
                    if (splitCourseInfo == null || splitCourseInfo.size() == 0) {
                        TipMsgHelper.ShowLMsg(getActivity(), R.string.ppt_pdf_not_have_pic);
                        return;
                    }
                    List<SplitCourseInfo> splitList = splitCourseInfo.get(0).getSplitList();
                    if (splitList == null || splitList.size() == 0) {
                        TipMsgHelper.ShowLMsg(getActivity(), R.string.ppt_pdf_not_have_pic);
                        return;
                    }
                    int type = newInfo.getResourceType();
                    if (type > ResType.RES_TYPE_BASE) {
                        type = type % ResType.RES_TYPE_BASE;
                    }
                    if (splitList.size() > 0) {
                        for (int i = 0; i < splitList.size(); i++) {
                            SplitCourseInfo splitCourse = splitList.get(i);
                            ImageInfo newResourceInfo = new ImageInfo();
                            newResourceInfo.setTitle(splitCourse.getSubResName());
                            newResourceInfo.setResourceUrl(AppSettings.getFileUrl(splitCourse.getPlayUrl()));
                            newResourceInfo.setResourceId(newInfo.getIdType());
                            newResourceInfo.setResourceType(type);
                            newResourceInfo.setAuthorId(newInfo.getAuthorId());
                            resourceInfoList.add(newResourceInfo);
                        }
                    }
                    GalleryActivity.newInstance(getActivity(), resourceInfoList, true, 0, true, true);
                }
            }

            @Override
            public void onError(NetroidError error) {
                if (getActivity() == null) {
                    return;
                }
            }
        });
        request.addHeader("Accept-Encoding", "*");
        request.start(getActivity());
    }

    /**
     * 打开图片
     *
     * @param image
     */
    private void openImage(ResourceInfoTag image) {
        List<ResourceInfo> resourceInfos = image.getSplitInfoList();
        if (resourceInfos != null && resourceInfos.size() > 0) {
            List<ImageInfo> resourceInfoList = new ArrayList<>();
            for (ResourceInfo resourceInfo : resourceInfos) {
                if (resourceInfo != null) {
                    ImageInfo newResourceInfo = new ImageInfo();
                    newResourceInfo.setTitle(resourceInfo.getTitle());
                    newResourceInfo.setResourceUrl(
                            AppSettings.getFileUrl(resourceInfo.getResourcePath()));
                    newResourceInfo.setResourceId(resourceInfo.getResId());
                    newResourceInfo.setResourceType(ResType.RES_TYPE_IMG);
                    newResourceInfo.setAuthorId(resourceInfo.getAuthorId());
                    resourceInfoList.add(newResourceInfo);
                }
            }
            //是否要显示页码
            boolean shouldShowPageNumber = resourceInfoList != null
                    && resourceInfoList.size() > 1;
            ActivityUtils.openImage(getActivity(), resourceInfoList, shouldShowPageNumber,
                    0, true, true);
        }
    }


    private int getMateriaType(String resType) {
        if (!TextUtils.isEmpty(resType)) {
            if (resType.contains("-")) {
                String[] split = resType.split("-");
                return Integer.valueOf(split[1]);
            }
        }
        return -1;
    }


    private void playCourse() {
        if (localCourseInfo != null) {
            playLocalCourse(localCourseInfo, false);
        }
    }

    private void playLocalCourse(LocalCourseInfo info, boolean isShareScreen) {
        Utils.createLocalDiskPath(Utils.ONLINE_FOLDER);
        String path = info.mPath;
        int resType = BaseUtils.getCoursetType(info.mPath);
        Intent it = ActivityUtils.getIntentForPlayLocalCourse(getActivity(), path,
                info.mTitle, info.mDescription,
                info.mOrientation, resType, false, isShareScreen);
        it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //预览不让编辑
        startActivityForResult(it, 2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ISLOCALCOURSECHANGE) {
            if (data != null) {
                String path = data.getStringExtra(UPDATETHUMBIAL);
                if (path != null) {
                    uploadParameter.setThumbPath(path);
                }
            }
        } else if (requestCode == REQUEST_CODE_MEDIAPAPER) {
            if (data != null) {
                Bundle args = data.getExtras();
                uploadParameter = (UploadParameter) args.getSerializable(UploadParameter.class.getSimpleName());
                noteInfo = args.getParcelable(NoteInfo.class.getSimpleName());
                if (uploadParameter != null) {
                    String titleText = editTitle.getText().toString().trim();
                    if (TextUtils.isEmpty(titleText)) {
                        editTitle.setText(uploadParameter.getFileName());
                        editTitle.clearFocus();
                    }
                }
            }
        } else if (requestCode == REQUEST_CODE_PICKER_RESOURCES) {
            //选择看课件多类型资源返回
            if (data != null) {
                if (resultCode == Activity.RESULT_OK) {
                    List<ResourceInfoTag> resultList = data.
                            getParcelableArrayListExtra(MediaListFragment.EXTRA_RESOURCE_INFO_LIST);
                    if (taskType == StudyTaskType.RETELL_WAWA_COURSE || taskType == StudyTaskType
                            .TASK_ORDER) {
                        //复述课件处理方式
                        if (resultList != null) {
                            //个人资源库 、 校本资源库 选取的资源
                            ResourceInfoTag tag = resultList.get(0);
                            final CourseData courseData = new CourseData();
                            String resId = tag.getResId();
                            if (!TextUtils.isEmpty(resId) && resId.contains("-")) {
                                int i = resId.indexOf("-");
                                resId = resId.substring(0, i);
                            }
                            courseData.id = Integer.parseInt(resId);
                            courseData.nickname = tag.getTitle();
                            courseData.type = tag.getResourceType();
                            courseData.resourceurl = tag.getResourcePath();
                            courseData.thumbnailurl = tag.getImgPath();
                            courseData.code = tag.getAuthorId();
                            courseData.shareurl = tag.getShareAddress();
                            courseData.screentype = tag.getScreenType();
                            courseData.resproperties = tag.getResProperties();
                            courseData.point = tag.getPoint();
                            //是否来自选取的LQ学程
                            boolean fromLQProgram = data.getBooleanExtra(FROM_LQ_PROGRAM, false);
                            if (fromLQProgram) {
                                controlLQProgramThumbnail(tag, courseData);
                                return;
                            }
                            if (uploadParameter == null) {
                                uploadParameter = UploadReourceHelper.getUploadParameter
                                        (getUserInfo(), null, courseData, null, courseData.type);
                            }
                            uploadParameter.setNewResourceInfoTag(tag);
//                          if (TextUtils.equals("1", tag.getResProperties())) {
//                                //自动批阅
//                                updateScoreView(View.GONE);
//                                mSelectMark.setVisibility(View.GONE);
//                            }
                            //任务单答题卡
                            if (!TextUtils.isEmpty(tag.getPoint())) {
                                updateScoreView(View.GONE);
                                mSelectMark.setVisibility(View.GONE);
                            }
                            if (!TextUtils.isEmpty(tag.getPoint())) {
                                uploadParameter.setResPropType(1);
                            } else {
                                uploadParameter.setResPropType(0);
                            }
                        } else {
                            //本机课件 选取的资源
                            UploadParameter uploadParameter = (UploadParameter) data.getSerializableExtra("uploadParameter");
                            if (uploadParameter != null) {
                                this.uploadParameter = uploadParameter;
                                int resType = BaseUtils.getCoursetType(uploadParameter.getFilePath());
                                this.uploadParameter.setType(resType);
                                setLocalCourseInfo(uploadParameter.getLocalCourseDTO().toLocalCourseInfo());
                                uploadParameter.setLocalCourseDTO(null);
                            }
                        }
                    } else if (taskType == StudyTaskType.LISTEN_READ_AND_WRITE) {
                        if (resultList != null && resultList.size() > 0) {
                            if (superTaskType == StudyTaskType.TASK_ORDER) {
                                setReadWriteData(resultList, false);
                            } else {
                                setListenData(resultList, false);
                            }
//                            int resType = resultList.get(0).getResourceType() % ResType.RES_TYPE_BASE;
//                            if (resType == ResType.RES_TYPE_STUDY_CARD) {
//                                setReadWriteData(resultList, false);
//                            } else {
//                                setListenData(resultList, false);
//                            }
                        }
                    } else {
                        if (resultList != null && resultList.size() > 0) {
                            //看课件的处理方式
                            this.resourceInfoTagList.addAll(resultList);
                            if (courseListAdapter != null) {
                                courseListAdapter.update(this.resourceInfoTagList);
                            }
                            if (taskType == StudyTaskType.ENGLISH_WRITING){
                                addCoursewareImageView.setVisibility(View.GONE);
                            }
                        }
                    }
                }
            }
        } else if (requestCode == LQCourseCourseListActivity.RC_SelectCourseRes) {
            if (data != null) {
                ArrayList<SectionResListVo> selectedList = (ArrayList<SectionResListVo>) data.getSerializableExtra(CourseSelectItemFragment.RESULT_LIST);
                if (selectedList != null && selectedList.size() > 0) {
                    //处理LQ学程选取的数据
                    SectionResListVo vo = selectedList.get(0);
                    if (vo != null) {
                        setFromLqCourseMarkScore();
                        IntroductionForReadCourseFragment fragment =
                                (IntroductionForReadCourseFragment) getFragmentManager()
                                        .findFragmentByTag(IntroductionForReadCourseFragment.TAG);
                        int taskType = getArguments().getInt(ActivityUtils.EXTRA_TASK_TYPE);
                        if (taskType == StudyTaskType.TASK_ORDER) {
                            CourseData courseData = new CourseData();
                            String resId = vo.getResId();
                            if (!TextUtils.isEmpty(resId) && resId.contains("-")) {
                                int i = resId.indexOf("-");
                                resId = resId.substring(0, i);
                            }
                            courseData.id = Integer.parseInt(resId);
                            courseData.nickname = vo.getName();
                            courseData.type = vo.getResType();
                            courseData.resourceurl = vo.getResourceUrl();
                            String thumbnailUrl = vo.getResourceUrl();
                            //截取zip包的缩略图
                            if (!TextUtils.isEmpty(thumbnailUrl)) {
                                String suffix = ".zip";
                                String headSuffix = "/head.jpg";
                                if (thumbnailUrl.contains(suffix)) {
                                    thumbnailUrl = thumbnailUrl
                                            .substring(0, thumbnailUrl.lastIndexOf(suffix));
                                    thumbnailUrl += headSuffix;
                                }
                            }
                            courseData.thumbnailurl = thumbnailUrl;
                            courseData.screentype = vo.getScreenType();
                            UploadParameter uploadParameter = UploadReourceHelper
                                    .getUploadParameter(getUserInfo(), null, courseData, null, 1);
                            if (uploadParameter != null) {
                                if (!TextUtils.isEmpty(vo.getChapterId())) {
                                    uploadParameter.setResCourseId(Integer.valueOf(vo.getChapterId()));
                                }
                                if (getArguments() != null) {
                                    uploadParameter.setTaskType(taskType);
                                    fragment.setData(uploadParameter);
                                }
                            }
                            if (vo.getResType() == ResType.RES_TYPE_STUDY_CARD) {
                                fragment.setWorkOrderId(vo.getResId());
                            }
                        }
                        int num = getFragmentManager().getBackStackEntryCount();
                        while (num > 1) {
                            popStack();
                            num = num - 1;
                        }
                    }
                }
            }
        }
    }

    private void updateScoreView(int visible) {
        if (taskType == StudyTaskType.RETELL_WAWA_COURSE
                || taskType == StudyTaskType.TASK_ORDER
                || superTaskType == StudyTaskType.RETELL_WAWA_COURSE
                || superTaskType == StudyTaskType.TASK_ORDER
                || superTaskType == StudyTaskType.Q_DUBBING) {
            if (visible == View.VISIBLE) {
                isAutoMark = false;
                mRbMarkNo.setVisibility(View.VISIBLE);
                mRbTenSystem.setVisibility(View.VISIBLE);
            } else {
                isAutoMark = true;
                mRbMarkYes.setChecked(true);
                mRbMarkNo.setVisibility(View.INVISIBLE);
                if (superTaskType == StudyTaskType.Q_DUBBING) {
                    mSelectMark.setVisibility(View.GONE);
                } else {
                    mSelectMark.setVisibility(View.VISIBLE);
                }
                mRbPercentageSystem.setChecked(true);
                mRbTenSystem.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * 处理LQ学程缩略图
     */
    private void controlLQProgramThumbnail(final ResourceInfoTag tag, final CourseData courseData) {
        if (tag == null || courseData == null) {
            return;
        }

     /*   if (TextUtils.equals("1", tag.getResProperties())) {
            //自动批阅
            updateScoreView(View.GONE);
            isAutoMark = true;
        }*/
        if (!TextUtils.isEmpty(tag.getPoint())) {
            //答题卡
            updateScoreView(View.GONE);
            mSelectMark.setVisibility(View.GONE);
        } else {
            setFromLqCourseMarkScore();
        }
        int resourceType = tag.getResourceType();
        //处理ppt、pdf缩略图
        if (WatchWawaCourseResourceSplicingUtils.isPPTOrPDTResource(resourceType)) {
            loadPPTOrPDFDetails(getActivity(), tag, courseData);
        } else {
            //其他资源
            if (uploadParameter == null) {
                uploadParameter = UploadReourceHelper
                        .getUploadParameter(getUserInfo(), null, courseData, null,
                                courseData.type);
            }
            uploadParameter.setNewResourceInfoTag(tag);
            uploadParameter.setResCourseId(tag.getResCourseId());
            if (!TextUtils.isEmpty(tag.getPoint())) {
                uploadParameter.setResPropType(1);
            } else {
                uploadParameter.setResPropType(0);
            }
        }
    }

    private void setFromLqCourseMarkScore() {
        if (taskType == StudyTaskType.RETELL_WAWA_COURSE || taskType == StudyTaskType.TASK_ORDER) {
            if (!isAutoMark) {
                isAutoMark = true;
                TipsHelper.showToast(getActivity(), R.string.str_show_select_lqcourse_mark_score_tip);
                updateScoreView(View.GONE);
            }
        }
    }

    /**
     * 加载ppt、pdf详情
     *
     * @param context
     * @param courseData
     */
    private void loadPPTOrPDFDetails(final Context context, final ResourceInfoTag tag,
                                     final CourseData courseData) {
        if (context == null || tag == null || courseData == null) {
            return;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("resId", tag.getResId());
            StringBuilder builder = new StringBuilder();
            builder.append("?j=" + jsonObject.toString());
            String url = ServerUrl.WAWATV_COURSE_DETAIL_URL + builder.toString();
            final ThisStringRequest request = new ThisStringRequest(
                    Request.Method.GET, url, new Listener<String>() {
                @Override
                public void onSuccess(String jsonString) {
                    if (context == null) {
                        return;
                    }
                    if (TextUtils.isEmpty(jsonString)) {
                        return;
                    }
                    PPTAndPDFCourseInfoCode result = com.alibaba.fastjson.JSONObject.parseObject
                            (jsonString, PPTAndPDFCourseInfoCode.class);
                    if (result != null) {
                        List<PPTAndPDFCourseInfo> courseInfoList = result.getData();
                        if (courseInfoList != null
                                && courseInfoList.size() > 0) {
                            List<SplitCourseInfo> splitList = courseInfoList
                                    .get(0).getSplitList();
                            if (splitList != null && splitList.size() > 0) {
                                //取得第一张作为缩略图
                                SplitCourseInfo info = splitList.get(0);
                                if (info != null) {
                                    String playUrl = info.getPlayUrl();
                                    if (!TextUtils.isEmpty(playUrl)) {
                                        //更新courseData
                                        courseData.thumbnailurl = playUrl;
                                        tag.setImgPath(playUrl);
                                        //更新缩略图
                                        appointIcon.setVisibility(View.VISIBLE);
                                        MyApplication.getThumbnailManager(getActivity())
                                                .displayThumbnailWithDefault(playUrl,
                                                        appointCourse,
                                                        R.drawable.default_cover, 65, 65);
                                    }
                                }
                            }
                        }
                    }
                }

                @Override
                public void onError(NetroidError error) {
                    if (context == null) {
                        return;
                    }
                }

                @Override
                public void onFinish() {
                    if (uploadParameter == null) {
                        uploadParameter = UploadReourceHelper
                                .getUploadParameter(getUserInfo(), null, courseData, null,
                                        courseData.type);
                    }
                    uploadParameter.setNewResourceInfoTag(tag);
                    super.onFinish();
                }
            });
            request.addHeader("Accept-Encoding", "*");
            request.start(context);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 对editText的输入字数进行监听
     */
    private class TextWatchChangeListenter implements TextWatcher {
        private int maxLength;
        private boolean isShowedToastEd = false;
        private int type;

        public TextWatchChangeListenter(int maxLength, boolean isShowedToastEd, int type) {
            this.maxLength = maxLength;
            this.isShowedToastEd = isShowedToastEd;
            this.type = type;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() >= maxLength && !isShowedToastEd) {
                TipMsgHelper.ShowLMsg(getActivity(), getString(R.string
                        .input_max_len, maxLength + ""));
                if (type == EditType.titleType) {
                    titleToasted = true;
                }
                if (type == EditType.contentType) {
                    contentToasted = true;
                }
            }
        }
    }

    public void setFromPersonalLibrary(boolean fromPersonalLibrary) {
        isFromPersonalLibrary = fromPersonalLibrary;
    }

    private void enterContactsPicker(UploadParameter uploadParameter) {
        Bundle args = getArguments();
        if (uploadParameter != null) {
            args.putSerializable(UploadParameter.class.getSimpleName(), uploadParameter);
        }

        args.putInt(ContactsPickerActivity.EXTRA_UPLOAD_TYPE, UploadCourseType.STUDY_TASK);

        args.putBoolean(ContactsPickerActivity.EXTRA_USE_EXTENDED_PICKER, true);
        args.putInt(
                ContactsPickerActivity.EXTRA_PICKER_TYPE, ContactsPickerActivity.PICKER_TYPE_GROUP);
        args.putInt(
                ContactsPickerActivity.EXTRA_GROUP_TYPE, ContactsPickerActivity.GROUP_TYPE_CLASS);
        args.putBoolean(ContactsPickerActivity.EXTRA_PUBLISH_RESOURCE, true);
        args.putInt(
                ContactsPickerActivity.EXTRA_MEMBER_TYPE, ContactsPickerActivity.MEMBER_TYPE_STUDENT);
        args.putInt(
                ContactsPickerActivity.EXTRA_PICKER_MODE, ContactsPickerActivity.PICKER_MODE_MULTIPLE);
        args.putString(
                ContactsPickerActivity.EXTRA_PICKER_CONFIRM_BUTTON_TEXT, getString(R.string.send));
        args.putBoolean(ContactsPickerActivity.EXTRA_PICKER_SUPERUSER, true);
        args.putBoolean(ContactsPickerActivity.EXTRA_IS_ONLINE_CLASS, isOnlineClass);
        args.putInt(ContactsPickerActivity.EXTRA_ROLE_TYPE, ContactsPickerActivity
                .ROLE_TYPE_TEACHER);
        Fragment fragment;
        if (args.getBoolean(ContactsPickerActivity.EXTRA_USE_EXTENDED_PICKER)) {
            // TODO: 2017/11/23 选择班级  选择小组
            PickerClassAndGroupActivity.start(getActivity(), args);
            return;
//            fragment = new ContactsExtendedPickerEntryFragment();
        } else {
            fragment = new ContactsPickerEntryFragment();
        }
        fragment.setArguments(args);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.activity_body, fragment, ContactsPickerEntryFragment.TAG)
                .hide(IntroductionForReadCourseFragment.this);
        ft.addToBackStack(null);
        ft.commit();
    }

    private boolean checkDate() {
        boolean isOk = true;
        String curDateStr = DateUtils.getDateStr(new Date(), "yyyy-MM-dd");
        int result = DateUtils.compareDate(startDateStr, curDateStr);
        if (result < 0) {
            isOk = false;
            TipMsgHelper.ShowLMsg(getActivity(), R.string.date_above_cur_date);
            return isOk;
        }
        result = DateUtils.compareDate(endDateStr, curDateStr);
        if (result < 0) {
            isOk = false;
            TipMsgHelper.ShowLMsg(getActivity(), R.string.date_above_cur_date);
            return isOk;
        }
        result = DateUtils.compareDate(endDateStr, startDateStr);
        if (result < 0) {
            isOk = false;
            TipMsgHelper.ShowLMsg(getActivity(), R.string.end_date_above_start_date);
            return isOk;
        }
        return isOk;
    }

    private void updateTaskCommitView(boolean isCommit) {
        Drawable unselect = getResources().getDrawable(R.drawable.unselect);
        Drawable select = getResources().getDrawable(R.drawable.select);
        if (isCommit) {
            commitTaskView.setCompoundDrawablesWithIntrinsicBounds(null, null, select, null);
        } else {
            commitTaskView.setCompoundDrawablesWithIntrinsicBounds(null, null, unselect, null);
        }
    }

    private void loadScoreFormulaData() {
        DefaultPullToRefreshDataListener listener =
                new DefaultPullToRefreshDataListener<ScoreFormulaListResult>(
                        ScoreFormulaListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        ScoreFormula scoreFormula = com.alibaba.fastjson.JSONObject.parseObject(jsonString, ScoreFormula.class);
                        updateScoreFormulaData(scoreFormula);
                    }
                };
        RequestHelper.sendPostRequest(getActivity(), ServerUrl
                .GET_ENGLISH_WRITING_SCOREFORMULA_BASE_URL, null, listener);
    }

    private void updateScoreFormulaData(ScoreFormula result) {
        //        childNameArray=new String[]{"小学-三四年级打分公式","小学-五六年级打分公式","初中-初中一年级打分公式",
        //                "初中-初中二年级打分公式","初中-初中三年级打分公式","高中-作文打分公式"};
        //        childFormulaID=new int[]{823671,823673,823676, 823681, 823682,823684};
        if (result != null) {
            List<ScoreFormula> scoreFormulas = result.getModel();
            if (scoreFormulas != null && scoreFormulas.size() > 0) {
                childNameArray = new String[scoreFormulas.size()];
                childFormulaID = new int[scoreFormulas.size()];
                for (int i = 0; i < scoreFormulas.size(); i++) {
                    String key = scoreFormulas.get(i).getKey();
                    String value = scoreFormulas.get(i).getValue();
                    childNameArray[i] = value;
                    childFormulaID[i] = Integer.valueOf(key);
                }
            } else {
                childFormulaID = new int[]{0};
                childNameArray = new String[]{" "};
            }
            scoreFormule.setText(childNameArray[0]);
            markFormulaId = childFormulaID[0];
            if (isFromSuperTask && uploadParameter != null) {
                int checkId = uploadParameter.getMarkFormula();
                for (int i = 0; i < childFormulaID.length; i++) {
                    if (checkId == childFormulaID[i]) {
                        markFormulaId = checkId;
                        scoreFormule.setText(childNameArray[i]);
                        break;
                    }
                }
            }
        }
    }

    /**
     * 打分公式之后的回调
     *
     * @param index
     * @param relationType
     */
    @Override
    public void onRelationChange(int index, String relationType) {
        scoreFormule.setText(childNameArray[index]);
        markFormulaId = childFormulaID[index];
    }

    /**
     * 打分公式
     */
    private void checkScoreFormula() {
        hideSoftKeyboard(getActivity());
        if (childNameArray == null) {
            childNameArray = new String[]{" "};
        }
        if (childFormulaID == null) {
            childFormulaID = new int[]{0};
        }
        SelectBindChildPopupView popupView = new SelectBindChildPopupView(getActivity(), position,
                this, childNameArray);
        popupView.showAtLocation(getActivity().getWindow().getDecorView().findViewById(android.R.id.content),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private boolean compareMinorMax(String workMin, String workMax) {
        if (TextUtils.isEmpty(workMin)) {
            return true;
        } else {
            if (TextUtils.isEmpty(workMax)) {
                return false;
            } else {
                //表示最小值与最大值都不为空
                int min = Integer.valueOf(workMin);
                int max = Integer.valueOf(workMax);
                if (min <= max) {
                    return true;
                } else {
                    return false;
                }
            }
        }
    }

    /**
     * 进入作业页面
     *
     * @param taskType
     */
    private void commitHomework(int taskType) {
        if (uploadParameter != null) {
            // 更新帖子相关信息
            mOpenParams.noteInfo = noteInfo;
        } else {
            long dateTime = System.currentTimeMillis();
            File noteFile = new File(Utils.NOTE_FOLDER, String.valueOf(dateTime));
            String dateTimeStr = getCreateTime(dateTime);
            mOpenParams = new NoteOpenParams(noteFile.getPath(), dateTimeStr,
                    MediaPaperActivity.OPEN_TYPE_EDIT, MediaPaper.PAPER_TYPE_TIEBA, null,
                    MediaPaperActivity.SourceType.STUDY_TASK, taskType, false);
            UserInfo userInfo = getUserInfo();
            if (userInfo != null) {
                mOpenParams.isTeacher = userInfo.isTeacher();
            }
            if (!TextUtils.isEmpty(titleContent)) {
                mOpenParams.createTime = titleContent;
            }
        }
        ActivityUtils.openLocalNote(getActivity(), mOpenParams, REQUEST_CODE_MEDIAPAPER);

    }

    @NonNull
    private String getCreateTime(long dateTime) {
        StringBuilder builder = new StringBuilder();
        UserInfo userInfo = getUserInfo();
        if (userInfo != null) {
            String familyName = userInfo.getFamilyName();
            if (!TextUtils.isEmpty(familyName)) {
                builder.append(getString(R.string.n_teacher, familyName));
            }
        }
        String dateTimeStr = DateUtils.transferLongToDate("yyyy-MM-dd HH:mm:ss", dateTime);
        builder.append(dateTimeStr);
        return builder.toString();
    }

    private void checkClassPlayEnd() {
        if (schoolClassInfos == null || schoolClassInfos.size() < 2) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < schoolClassInfos.size(); i++) {
            ShortSchoolClassInfo classInfo = schoolClassInfos.get(i);
            if (stringBuilder.length() == 0) {
                stringBuilder.append(classInfo.getClassId());
            } else {
                stringBuilder.append(",").append(classInfo.getClassId());
            }
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("classIds", stringBuilder.toString());
        RequestHelper.RequestListener listener =
                new RequestHelper.RequestDataResultListener<DataModelResult>(
                        getActivity(), DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        if (!TextUtils.isEmpty(jsonString)) {
                            try {
                                JSONObject jsonObject = new JSONObject(jsonString);
                                JSONArray dataArray = jsonObject.getJSONArray("data");
                                if (dataArray != null && dataArray.length() > 0) {
                                }
                                JSONArray dataHisArray = jsonObject.getJSONArray("dataHis");
                                if (dataHisArray != null && dataHisArray.length() > 0) {
                                    outer:
                                    for (int m = 0; m < dataHisArray.length(); m++) {
                                        String hisClassId = dataHisArray.get(m).toString();
                                        for (int k = 0; k < schoolClassInfos.size(); k++) {
                                            ShortSchoolClassInfo info = schoolClassInfos.get(k);
                                            if (TextUtils.equals(info.getClassId(), hisClassId)) {
                                                schoolClassInfos.remove(k);
                                                continue outer;
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }
                };
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.GET_CHECK_TEACHING_PLAN_BASE_URL, params, listener);
    }

    private void enterSendOnlineStudyTask() {
        if (uploadParameter.getTaskType() == StudyTaskType.TOPIC_DISCUSSION
                || uploadParameter.getTaskType() == StudyTaskType.ENGLISH_WRITING
                || uploadParameter.getCourseData() != null) {
            //英文写作  和 话题讨论 以及在线的课件
            publishStudyTask(uploadParameter, uploadParameter.getCourseData(), schoolClassInfos);
        } else if (taskType == StudyTaskType.LISTEN_READ_AND_WRITE) {
            //听说+读写 发送到班级
            publishListenReadAndWriteStudyTask(uploadParameter, schoolClassInfos);
        } else {
            showLoadingDialog(getString(R.string.upload_and_wait), false);
            UploadReourceHelper.uploadResource(getActivity(), uploadParameter, new CallbackListener() {
                @Override
                public void onBack(final Object result) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CourseUploadResult courseUploadResult = (CourseUploadResult) result;
                                if (courseUploadResult != null && courseUploadResult.code == 0) {
                                    List<CourseData> courseDatas = courseUploadResult.getData();
                                    if (courseDatas != null && courseDatas.size() > 0) {
                                        CourseData courseData = courseDatas.get(0);
                                        if (courseData != null) {
                                            publishStudyTask(uploadParameter, courseData, schoolClassInfos);
                                        }
                                    }
                                } else {
                                    dismissLoadingDialog();
                                }
                            }
                        });
                    }
                }
            });
        }
    }


    //空中课堂学习任务在当前界面发送
    private void publishStudyTask(final UploadParameter uploadParameter, CourseData courseData,
                                  List<ShortSchoolClassInfo> schoolClassInfos) {
        showLoadingDialog();
        JSONObject taskParams = new JSONObject();
        if (uploadParameter != null) {
            try {
                taskParams.put("TaskType", uploadParameter.getTaskType());
                taskParams.put("TaskCreateId", uploadParameter.getMemberId());
                taskParams.put("TaskCreateName", uploadParameter.getCreateName());
                JSONArray schoolArray = new JSONArray();
                JSONObject schoolObject = null;
                if (schoolClassInfos != null && schoolClassInfos.size() > 0) {
                    for (int i = 0; i < schoolClassInfos.size(); i++) {
                        schoolObject = new JSONObject();
                        ShortSchoolClassInfo info = schoolClassInfos.get(i);
                        schoolObject.put("ClassName", info.getClassName());
                        schoolObject.put("ClassId", info.getClassId());
                        schoolObject.put("SchoolName", info.getSchoolName());
                        schoolObject.put("SchoolId", info.getSchoolId());
                        schoolArray.put(schoolObject);
                    }
                }
                taskParams.put("SchoolClassList", schoolArray);
                taskParams.put("TaskTitle", uploadParameter.getFileName());
                if (courseData != null) {
                    if ((uploadParameter.getTaskType() == StudyTaskType.RETELL_WAWA_COURSE
                            || uploadParameter.getTaskType() == StudyTaskType.TASK_ORDER
                            || uploadParameter.getTaskType() == StudyTaskType.ENGLISH_WRITING)
                            && uploadParameter.getType() == ResType.RES_TYPE_IMG) {
                        taskParams.put("ResAuthor", courseData.code);
                        taskParams.put("ResId", courseData.resId);
                    } else {
                        taskParams.put("ResId", courseData.getIdType());
                    }
                    taskParams.put("ResUrl", courseData.resourceurl);
                } else {
                    taskParams.put("ResId", "");
                    taskParams.put("ResUrl", "");
                }
                //学程馆资源的id
                if (uploadParameter.getTaskType() == StudyTaskType.RETELL_WAWA_COURSE
                        || uploadParameter.getTaskType() == StudyTaskType.TASK_ORDER
                        || uploadParameter.getTaskType() == StudyTaskType.Q_DUBBING
                        || uploadParameter.getTaskType() == StudyTaskType.ENGLISH_WRITING) {
                    taskParams.put("ResCourseId", uploadParameter.getResCourseId());
                }
                if (uploadParameter.getTaskType() == StudyTaskType.TASK_ORDER) {
                    taskParams.put("ResPropType", uploadParameter.getResPropType());
                }
                if (uploadParameter.getWorkOrderId() != null) {
                    taskParams.put("WorkOrderId", uploadParameter.getWorkOrderId());
                }
                if (uploadParameter.getWorkOrderUrl() != null) {
                    taskParams.put("WorkOrderUrl", uploadParameter.getWorkOrderUrl());
                }
                taskParams.put("StartTime", uploadParameter.getStartDate());
                taskParams.put("EndTime", uploadParameter.getEndDate());
                //提交时间类型
                taskParams.put("SubmitType", uploadParameter.getSubmitType());
                if (uploadParameter.getTaskType() == StudyTaskType.INTRODUCTION_WAWA_COURSE) {
                    taskParams.put("DiscussContent", uploadParameter.getDisContent());
                } else {
                    taskParams.put("DiscussContent", uploadParameter.getDescription());
                }
                //布置任务之英文写作相关的字段
                //作文要求
                taskParams.put("WritingRequire", uploadParameter.getWritingRequire());
                //打分公式
                taskParams.put("MarkFormula", uploadParameter.getMarkFormula());
                //作业字数最小值
                taskParams.put("WordCountMin", uploadParameter.getWordCountMin());
                //作业字数最大值
                taskParams.put("WordCountMax", uploadParameter.getWordCountMax());

                //打分
                if (uploadParameter.NeedScore) {
                    taskParams.put("NeedScore", true);
                    taskParams.put("ScoringRule", uploadParameter.ScoringRule);
                }
                //空中课堂的布置任务新增字段
                taskParams.put("TaskFlag", currentStudyType);
                taskParams.put("ExtId", onlineRes.getId());
                if (uploadParameter.getCourseId() > 0 && uploadParameter.getCourseTaskType() > 0){
                    taskParams.put("CourseId",uploadParameter.getCourseId());
                    taskParams.put("CourseTaskType",uploadParameter.getCourseTaskType());
                }
                //判断是不是任务单和听说课的多选
                int taskType = uploadParameter.getTaskType();
                if (taskType == StudyTaskType.TASK_ORDER
                        || taskType == StudyTaskType.RETELL_WAWA_COURSE
                        || taskType == StudyTaskType.Q_DUBBING) {
                    List<LookResDto> lookResDtos = uploadParameter.getLookResDtoList();
                    if (lookResDtos != null) {
                        if (lookResDtos.size() == 1) {
                            String point = lookResDtos.get(0).getPoint();
                            if (uploadParameter.NeedScore && !TextUtils.isEmpty(point)) {
                                taskParams.put("ScoringRule", StudyTaskUtils.getScoringRule(point));
                            }
                            if (taskType == StudyTaskType.RETELL_WAWA_COURSE) {
                                //完成方式
                                taskParams.put("RepeatCourseCompletionMode", lookResDtos.get(0).getCompletionMode());
                            } else if (taskType == StudyTaskType.Q_DUBBING) {
                                taskParams.put("ResPropType",lookResDtos.get(0).getResPropType());
                            }
                            if (lookResDtos.get(0).getCourseId() > 0 && lookResDtos.get(0).getCourseTaskType() > 0){
                                taskParams.put("CourseId",lookResDtos.get(0).getCourseId());
                                taskParams.put("CourseTaskType",lookResDtos.get(0).getCourseTaskType());
                            }
                            if ((taskType == StudyTaskType.RETELL_WAWA_COURSE
                                    || taskType == StudyTaskType.TASK_ORDER)
                                    && courseData == null){
                                taskParams.put("ResId", lookResDtos.get(0).getResId());
                                taskParams.put("ResUrl", lookResDtos.get(0).getResUrl());
                            }
                            taskParams.put("ResCourseId", lookResDtos.get(0).getResCourseId());
                            taskParams.put("ResPropType",lookResDtos.get(0).getResPropType());
                        } else if (lookResDtos.size() > 1) {
                            if (taskType == StudyTaskType.RETELL_WAWA_COURSE) {
                                taskParams.put("TaskType", StudyTaskType.MULTIPLE_RETELL_COURSE);
                            } else if (taskType == StudyTaskType.Q_DUBBING) {
                                taskParams.put("TaskType", StudyTaskType.MULTIPLE_Q_DUBBING);
                            } else {
                                taskParams.put("TaskType", StudyTaskType.MULTIPLE_TASK_ORDER);
                            }
                            StudyTaskUtils.addMultipleTaskParams(taskParams, lookResDtos);
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        RequestHelper.RequestDataResultListener listener = new RequestHelper
                .RequestDataResultListener(getActivity(), DataResult.class) {
            @Override
            public void onSuccess(String json) {
                if (getActivity() == null || TextUtils.isEmpty(json)) return;
                try {
                    dismissLoadingDialog();
                    DataResult result = JSON.parseObject(json, DataResult.class);
                    if (result != null && result.isSuccess()) {
                        //布置完成刷新布置任务页面
                        CampusPatrolUtils.setHasStudyTaskAssigned(true);
                        TipMsgHelper.ShowLMsg(getActivity(), R.string.publish_course_ok);
                        finish();
                    } else {
                        TipMsgHelper.ShowLMsg(getActivity(), R.string.publish_course_error);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dismissLoadingDialog();
                finish();
            }

            @Override
            public void onError(NetroidError error) {
                super.onError(error);
                CampusPatrolUtils.setHasStudyTaskAssigned(true);
                dismissLoadingDialog();
            }
        };
        RequestHelper.postRequest(getActivity(), ServerUrl.ADD_AIRCLASS_STUDY_TASK_LIST_BASE_URL, taskParams.toString(),
                listener);
    }

    /**
     * 看课件多类型上传
     *
     * @param uploadParameter
     * @param schoolClassInfos
     */
    private void publishWatchWawaCourseStudyTask(UploadParameter uploadParameter,
                                                 List<ShortSchoolClassInfo> schoolClassInfos) {
        showLoadingDialog();
        JSONObject taskParams = new JSONObject();
        if (uploadParameter != null) {
            try {
                taskParams.put("TaskCreateId", uploadParameter.getMemberId());
                taskParams.put("TaskCreateName", uploadParameter.getCreateName());
                JSONArray schoolArray = new JSONArray();
                JSONObject schoolObject = null;
                if (schoolClassInfos != null && schoolClassInfos.size() > 0) {
                    for (int i = 0; i < schoolClassInfos.size(); i++) {
                        schoolObject = new JSONObject();
                        ShortSchoolClassInfo info = schoolClassInfos.get(i);
                        schoolObject.put("ClassName", info.getClassName());
                        schoolObject.put("ClassId", info.getClassId());
                        schoolObject.put("SchoolName", info.getSchoolName());
                        schoolObject.put("SchoolId", info.getSchoolId());
                        schoolArray.put(schoolObject);
                    }
                }
                taskParams.put("SchoolClassList", schoolArray);

                taskParams.put("TaskTitle", uploadParameter.getFileName());
                taskParams.put("StartTime", uploadParameter.getStartDate());
                taskParams.put("EndTime", uploadParameter.getEndDate());
                //提交时间类型
                taskParams.put("SubmitType", uploadParameter.getSubmitType());
                taskParams.put("DiscussContent", uploadParameter.getDisContent());
                //空中课堂的布置任务新增字段
                taskParams.put("TaskFlag", currentStudyType);
                taskParams.put("ExtId", onlineRes.getId());

                List<LookResDto> lookResDtos = uploadParameter.getLookResDtoList();
                JSONArray lookResArray = new JSONArray();
                JSONObject lookObject = null;
                if (lookResDtos != null && lookResDtos.size() > 0) {
                    for (int i = 0; i < lookResDtos.size(); i++) {
                        lookObject = new JSONObject();
                        LookResDto lookDto = lookResDtos.get(i);
                        lookObject.put("Id", lookDto.getId());
                        lookObject.put("TaskId", lookDto.getTaskId());
                        lookObject.put("ResId", lookDto.getResId());
                        lookObject.put("ResUrl", lookDto.getResUrl());
                        lookObject.put("ResTitle", lookDto.getResTitle() == null ? "" : lookDto
                                .getResTitle());
                        lookObject.put("CreateId", lookDto.getCreateId() == null ? "" : lookDto
                                .getCreateId());
                        lookObject.put("CreateName", lookDto.getCreateName() == null ? "" :
                                lookDto.getCreateName());
                        lookObject.put("CreateTime", lookDto.getCreateTime() == null ? "" :
                                lookDto.getCreateTime());
                        lookObject.put("UpdateId", lookDto.getUpdateId() == null ? "" : lookDto
                                .getUpdateName());
                        lookObject.put("UpdateName", lookDto.getCreateName() == null ? "" :
                                lookDto.getCreateName());
                        lookObject.put("Deleted", lookDto.isDeleted());
                        lookObject.put("Author", lookDto.getAuthor() == null ? "" : lookDto.getAuthor());
                        lookObject.put("ResCourseId", lookDto.getResCourseId());
                        if (lookDto.getCourseId() > 0 && lookDto.getCourseTaskType() > 0){
                            lookObject.put("CourseId",lookDto.getCourseId());
                            lookObject.put("CourseTaskType",lookDto.getCourseTaskType());
                        }
                        lookResArray.put(lookObject);
                    }
                }
                taskParams.put("LookResList", lookResArray);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        RequestHelper.RequestDataResultListener listener = new RequestHelper
                .RequestDataResultListener(getActivity(), DataResult.class) {
            @Override
            public void onSuccess(String json) {
                if (getActivity() == null) {
                    return;
                }
                if (TextUtils.isEmpty(json)) return;
                dismissLoadingDialog();
                try {
                    DataResult result = JSON.parseObject(json, DataResult.class);
                    if (result != null && result.isSuccess()) {
                        CampusPatrolUtils.setHasStudyTaskAssigned(true);
                        TipMsgHelper.ShowLMsg(getActivity(), R.string.publish_course_ok);
                        finish();
                    } else {
                        TipMsgHelper.ShowLMsg(getActivity(), R.string.publish_course_error);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                CampusPatrolUtils.setHasStudyTaskAssigned(true);
                dismissLoadingDialog();
                finish();
            }

            @Override
            public void onError(NetroidError error) {
                super.onError(error);
                dismissLoadingDialog();
            }
        };
        RequestHelper.postRequest(getActivity(),
                ServerUrl.ADD_AIRCLASS_LOOK_STUDY_TASK_BASE_URL, taskParams.toString(), listener);
    }

    /**
     * 听说 + 读写资源的上传
     *
     * @param uploadParameter
     * @param schoolClassInfos
     */
    private void publishListenReadAndWriteStudyTask(UploadParameter uploadParameter,
                                                    List<ShortSchoolClassInfo> schoolClassInfos) {
        showLoadingDialog();
        JSONObject taskParams = new JSONObject();
        if (uploadParameter != null) {
            try {
                taskParams.put("TaskCreateId", uploadParameter.getMemberId());
                taskParams.put("TaskCreateName", uploadParameter.getCreateName());
                //发送到班级
                JSONArray schoolArray = new JSONArray();
                JSONObject schoolObject = null;
                if (schoolClassInfos != null && schoolClassInfos.size() > 0) {
                    for (int i = 0; i < schoolClassInfos.size(); i++) {
                        schoolObject = new JSONObject();
                        ShortSchoolClassInfo info = schoolClassInfos.get(i);
                        schoolObject.put("ClassName", info.getClassName());
                        schoolObject.put("ClassId", info.getClassId());
                        schoolObject.put("SchoolName", info.getSchoolName());
                        schoolObject.put("SchoolId", info.getSchoolId());
                        schoolArray.put(schoolObject);
                    }
                }
                taskParams.put("SchoolClassList", schoolArray);
                taskParams.put("TaskTitle", uploadParameter.getFileName());
                taskParams.put("StartTime", uploadParameter.getStartDate());
                taskParams.put("EndTime", uploadParameter.getEndDate());
                taskParams.put("DiscussContent", uploadParameter.getDisContent());
                //空中课堂的布置任务新增字段
                taskParams.put("TaskFlag", currentStudyType);
                taskParams.put("ExtId", onlineRes.getId());
                //打分
                if (uploadParameter.NeedScore) {
                    taskParams.put("NeedScore", true);
                    taskParams.put("ScoringRule", uploadParameter.ScoringRule);
                }
                List<LookResDto> lookResDtos = uploadParameter.getLookResDtoList();
                JSONArray lookResArray = new JSONArray();
                JSONObject lookObject = null;
                if (lookResDtos != null && lookResDtos.size() > 0) {
                    for (int i = 0; i < lookResDtos.size(); i++) {
                        lookObject = new JSONObject();
                        LookResDto lookDto = lookResDtos.get(i);
                        lookObject.put("TaskType", lookDto.getTaskId());
                        lookObject.put("ResId", lookDto.getResId());
                        lookObject.put("ResUrl", lookDto.getResUrl());
                        lookObject.put("ResTitle", lookDto.getResTitle() == null ? "" : lookDto
                                .getResTitle());
                        lookObject.put("Author", lookDto.getAuthor() == null ? "" : lookDto.getAuthor());
                        lookResArray.put(lookObject);
                    }
                }
                taskParams.put("TSDXResList", lookResArray);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        RequestHelper.RequestDataResultListener listener = new RequestHelper
                .RequestDataResultListener(getActivity(), DataResult.class) {
            @Override
            public void onSuccess(String json) {
                if (getActivity() == null) {
                    return;
                }
                if (TextUtils.isEmpty(json)) return;
                dismissLoadingDialog();
                try {
                    DataResult result = JSON.parseObject(json, DataResult.class);
                    if (result != null && result.isSuccess()) {
                        //布置完成刷新布置任务页面
                        CampusPatrolUtils.setHasStudyTaskAssigned(true);
                        TipMsgHelper.ShowLMsg(getActivity(), R.string.publish_course_ok);
                        finish();
                    } else {
                        String errorMessage = getString(R.string.publish_course_error);
                        if (result != null && !TextUtils.isEmpty(result.getErrorMessage())) {
                            errorMessage = result.getErrorMessage();
                        }
                        TipMsgHelper.ShowLMsg(getActivity(), errorMessage);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dismissLoadingDialog();
                CampusPatrolUtils.setHasStudyTaskAssigned(true);
                finish();
            }

            @Override
            public void onError(NetroidError error) {
                super.onError(error);
                dismissLoadingDialog();
            }
        };
        listener.setShowLoading(true);
        RequestHelper.postRequest(getActivity(), ServerUrl.GET_LISTEN_READANDWRITE_AIRCLASS_TASK_BASE_URL, taskParams.toString(), listener);
    }

    public void updateGridViewHeight(boolean isListenData) {
        ListAdapter listAdapter = null;
        if (isListenData){
            if (listenGridView == null) {
                return;
            }
            if (superTaskType == StudyTaskType.RETELL_WAWA_COURSE
                    || superTaskType == StudyTaskType.Q_DUBBING) {

            } else {
                return;
            }
            listAdapter = listenGridView.getAdapter();
        } else {
            if (readWriteGridView == null) {
                return;
            }
            if (superTaskType != StudyTaskType.TASK_ORDER) {
                return;
            }
            listAdapter = readWriteGridView.getAdapter();
        }
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            View listItem = listAdapter.getView(i, null, listenGridView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        if (isListenData){
            ViewGroup.LayoutParams params = listenGridView.getLayoutParams();
            params.height = totalHeight;
            listenGridView.setLayoutParams(params);
        } else {
            ViewGroup.LayoutParams params = readWriteGridView.getLayoutParams();
            params.height = totalHeight;
            readWriteGridView.setLayoutParams(params);
        }
    }
}
