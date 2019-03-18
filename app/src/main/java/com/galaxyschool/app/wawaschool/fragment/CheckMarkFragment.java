package com.galaxyschool.app.wawaschool.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.ArrayMap;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.CourseOpenUtils;
import com.galaxyschool.app.wawaschool.common.DensityUtils;
import com.galaxyschool.app.wawaschool.common.MessageEventConstantUtils;
import com.galaxyschool.app.wawaschool.common.PassParamhelper;
import com.galaxyschool.app.wawaschool.common.StudyTaskUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.common.WatchWawaCourseResourceOpenUtils;
import com.galaxyschool.app.wawaschool.common.WawaCourseUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.helper.ApplyMarkHelper;
import com.galaxyschool.app.wawaschool.imagebrowser.GalleryActivity;
import com.galaxyschool.app.wawaschool.pojo.ExerciseAnswerCardParam;
import com.galaxyschool.app.wawaschool.pojo.ExerciseItem;
import com.galaxyschool.app.wawaschool.pojo.ExerciseItemArea;
import com.galaxyschool.app.wawaschool.pojo.MaterialResourceType;
import com.galaxyschool.app.wawaschool.pojo.ResourceInfoTag;
import com.galaxyschool.app.wawaschool.pojo.weike.MediaData;
import com.libs.gallery.ImageInfo;
import com.lqwawa.intleducation.MainApplication;
import com.lqwawa.intleducation.common.utils.SPUtil;
import com.lqwawa.intleducation.module.tutorial.marking.choice.QuestionResourceModel;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.CheckMarkInfo;
import com.galaxyschool.app.wawaschool.pojo.CheckMarkResult;
import com.galaxyschool.app.wawaschool.pojo.CommitTask;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.StudyTask;
import com.galaxyschool.app.wawaschool.pojo.TaskMarkParam;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.PlaybackParam;
import com.galaxyschool.app.wawaschool.pojo.weike.SplitCourseInfo;
import com.galaxyschool.app.wawaschool.views.CircleImageView;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.lqwawa.lqbaselib.pojo.MessageEvent;
import com.lqwawa.mooc.modle.tutorial.TutorialHomePageActivity;
import com.lqwawa.mooc.modle.tutorial.TutorialParams;
import com.oosic.apps.iemaker.base.SlideManager;
import com.osastudio.common.utils.LQImageLoader;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.galaxyschool.app.wawaschool.fragment.CompletedHomeworkListFragment.ACTION_MARK_SCORE;


/**
 * 查看批阅
 */

public class CheckMarkFragment extends ContactsListFragment {
    public static String TAG = CheckMarkFragment.class.getSimpleName();
    private TextView mTvSore;
    private TextView tvExerciseScoreTextV;
    private boolean isHeadMaster;
    private CommitTask commitTask;
    private ExerciseAnswerCardParam cardParam;
    private ExerciseItem exerciseItem;

    public interface Constants {
        String TASK_TITLE = "title";
        String RES_ID = "res_id";
        String COMMITTASK_ID = "CommitTaskId";
        String TASK_SCORE = "score";
        String TASK_PIC = "picture";
        String ROLE_TYPE = "roleType";
        String STUDENT_ID = "student_id";
        String STUDYTASK = "StudyTask";
        String ACTION_TASKMARKPARAM = "TaskMarkParam";//打分批阅
        String EXTRA_ISONLINEREPORTER = "isOnlineReporter";//是不是来自直播的小编或者主持人
        String EXTRA_ISONLINEHOST = "isOnlineHost";//是不是来自直播的小编或者主持人
        //来自mooc的资源
        String EXTRA_IS_FROM_MOOC = "is_from_mooc";
        //是不是来自mooc试听资源
        String EXTRA_IS_AUDITION = "is_audition";
        String COMMIT_TASK = "commit_task";
    }

    private View rootView;
    private ListView listView;
    private String score, thumbPic, title, resId, CommitTaskId, studentId;
    private int roleType = -1;
    private StudyTask task;
    private TaskMarkParam mTaskMarkParam;
    private boolean isOnlineReporter, isOnlineHost;
    private boolean deleteBtnVisibled;
    private boolean isFromMOOC;
    private boolean isAudition;
    private boolean isOriginal = true;
    private boolean isAnswerTaskOrderQuestion;
    private QuestionResourceModel markModel;
    private boolean isAssistanceModel;//是不是帮辅模式


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_fragment_checkmark, null);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        if (isAnswerTaskOrderQuestion) {
            initAnswerParsingData();
        }
        addEventBusReceiver();
    }

    @Override
    public void onResume() {
        super.onResume();
        registResultBroadcast();
        loadCommonData();
    }

    void initViews() {
        if (getArguments() != null) {
            commitTask = (CommitTask) getArguments().getSerializable(Constants.COMMIT_TASK);
            isHeadMaster = getArguments().getBoolean(HomeworkMainFragment.Constants.EXTRA_IS_HEAD_MASTER);
            resId = getArguments().getString(Constants.RES_ID);
            studentId = getArguments().getString(Constants.STUDENT_ID);
            CommitTaskId = getArguments().getString(Constants.COMMITTASK_ID);
            score = getArguments().getString(Constants.TASK_SCORE);
            thumbPic = getArguments().getString(Constants.TASK_PIC);
            title = getArguments().getString(Constants.TASK_TITLE);
            roleType = getArguments().getInt(Constants.ROLE_TYPE);
            task = (StudyTask) getArguments().getSerializable(Constants.STUDYTASK);
            mTaskMarkParam = (TaskMarkParam) getArguments().getSerializable(Constants.ACTION_TASKMARKPARAM);
            isOnlineReporter = getArguments().getBoolean(Constants.EXTRA_ISONLINEREPORTER);
            isOnlineHost = getArguments().getBoolean(Constants.EXTRA_ISONLINEHOST);
            isFromMOOC = getArguments().getBoolean(Constants.EXTRA_IS_FROM_MOOC);
            isAudition = getArguments().getBoolean(Constants.EXTRA_IS_AUDITION);
            cardParam = (ExerciseAnswerCardParam) getArguments().getSerializable(ExerciseAnswerCardParam.class
                    .getSimpleName());
            if (cardParam != null) {
                initExerciseData();
                if (commitTask == null) {
                    commitTask = cardParam.getCommitTask();
                }
                if (task == null) {
                    task = cardParam.getStudyTask();
                }
            }
            if (commitTask != null && commitTask.isAssistantMark()) {
                //帮辅批阅
                initAssistantMarkData();
            }
        }
        //标题
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            textView.setText(R.string.str_watch_mark);
        }

        TextView courseDetailTextV = (TextView) findViewById(R.id.tv_access_details);
        if (courseDetailTextV != null) {
            courseDetailTextV.setOnClickListener(this);
            if (isAnswerTaskOrderQuestion) {
                courseDetailTextV.setText(getString(R.string.str_look_origin_question));
            }
        }

        findViewById(R.id.ll_course).setOnClickListener(this);
        TextView tvCourseName = (TextView) findViewById(R.id.tv_course_name);//课件名
        if (isAnswerTaskOrderQuestion) {
            //单行显示...
            tvCourseName.setSingleLine(true);
            tvCourseName.setEllipsize(TextUtils.TruncateAt.END);
        }
        tvCourseName.setText(title);
        //得分
        mTvSore = (TextView) findViewById(R.id.tv_score);
        if (commitTask != null && !isAssistanceModel) {
            if (TextUtils.equals(getMemeberId(), commitTask.getStudentId())) {
                markModel = new QuestionResourceModel();
                markModel.setTitle(commitTask.getStudentResTitle());
                markModel.setT_TaskId(commitTask.getTaskId());
                markModel.setStuMemberId(getMemeberId());
                if (task != null) {
                    markModel.setT_TaskType(task.getType());
                    markModel.setT_ClassId(task.getClassId());
                    markModel.setT_ClassName(task.getClassName());
                    markModel.setT_ResCourseId(task.getResCourseId());
                    markModel.setT_CourseId(task.getCourseId());
                    markModel.setT_CourseName(task.getCourseName());
                }
                if (cardParam != null) {
                    if (cardParam.isFromOnlineStudyTask()) {
                        markModel.setT_CommitTaskOnlineId(cardParam.getCommitTaskId());
                    } else {
                        markModel.setT_CommitTaskId(cardParam.getCommitTaskId());
                    }
                } else if (isFromMOOC) {
                    if (!TextUtils.isEmpty(CommitTaskId)) {
                        markModel.setT_CommitTaskOnlineId(Integer.valueOf(CommitTaskId));
                    }
                } else {
                    if (!TextUtils.isEmpty(CommitTaskId)) {
                        markModel.setT_CommitTaskId(Integer.valueOf(CommitTaskId));
                    }
                }
                markModel.setT_AirClassId(commitTask.getAirClassId());
                ApplyMarkHelper.showApplyMarkView(getActivity(), mTvSore);
                mTvSore.setOnClickListener(v -> {
                    if (cardParam == null) {
                        openCourse(commitTask.getStudentResId(), false, true);
                    } else {
                        //读写单的主观题
                        cardParam.setMarkModel(markModel);
                        ApplyMarkHelper.doApplyMarkTask(getActivity(), cardParam, exerciseItem, null);
                    }
                });
            }
        }
        tvExerciseScoreTextV = (TextView) findViewById(R.id.tv_check_score);
        ImageView ivImg = (ImageView) findViewById(R.id.iv_img);//缩略图

        updateScore();

        //之前宽 90 高 120  //设置布局为A4比例
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) ivImg.getLayoutParams();
        int width = DensityUtils.dp2px(getActivity(), 90);
        layoutParams.width = width;
        layoutParams.height = width * 210 / 297;
        ivImg.setLayoutParams(layoutParams);
        LQImageLoader.displayImage(thumbPic, ivImg, R.drawable.default_cover);

        //下拉刷新
        PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
//        pullToRefreshView.setRefreshEnable(false);
        setPullToRefreshView(pullToRefreshView);
        listView = (ListView) findViewById(R.id.contacts_list_view);
        if (listView != null) {
            AdapterViewHelper listViewHelper = new AdapterViewHelper(
                    getActivity(), listView, R.layout.item_check_mark) {
                @Override
                public void loadData() {
                    loadCommonData();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    final CheckMarkInfo.ModelBean data = (CheckMarkInfo.ModelBean) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;

                    //帮辅
                    TextView assistanceTextV = (TextView) view.findViewById(R.id.tv_assistance);
                    if (data.getReviewFlag() == 1) {
                        assistanceTextV.setVisibility(View.VISIBLE);
                    } else {
                        assistanceTextV.setVisibility(View.INVISIBLE);
                    }

                    //标题
                    TextView title = (TextView) view.findViewById(R.id.tv_title);
                    TextView date = (TextView) view.findViewById(R.id.tv_date);//时间
                    CircleImageView imgLeft = (CircleImageView) view.findViewById(R.id.iv_img_left);//左边头像
                    CircleImageView imgRight = (CircleImageView) view.findViewById(R.id.iv_img_right);//右边头像
                    View middleView = view.findViewById(R.id.ll_course);
                    View middleViewItem = view.findViewById(R.id.ll_course_item);

                    ImageView deleteImageVLeft = (ImageView) view.findViewById(R.id.iv_delete_left);
                    ImageView deleteImageVRight = (ImageView) view.findViewById(R.id.iv_delete_right);
                    if (deleteImageVLeft != null) {
                        if (data.isIsTeacher()) {
                            if (data.isShowDeleted()) {
                                deleteImageVLeft.setVisibility(View.VISIBLE);
                            } else {
                                deleteImageVLeft.setVisibility(View.GONE);
                            }
                            deleteImageVLeft.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    deleteImageVLeft.setVisibility(View.GONE);
                                    upDateDeleteButtonShowStatus(null, true);
                                    deleteData(data);
                                }
                            });
                        }
                    }

                    if (deleteImageVRight != null) {
                        if (!data.isIsTeacher()) {
                            if (data.isShowDeleted()) {
                                deleteImageVRight.setVisibility(View.VISIBLE);
                            } else {
                                deleteImageVRight.setVisibility(View.GONE);
                            }
                            deleteImageVRight.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    deleteImageVRight.setVisibility(View.GONE);
                                    upDateDeleteButtonShowStatus(null, true);
                                    deleteData(data);
                                }
                            });
                        }

                    }


                    String createName = data.getCreateName();
                    String suffTitle = getString(R.string.str_mark_ask);

                    if (data.isIsTeacher() || (data.getSubmitRole() == 0 && isAssistanceModel)) {
                        createName = createName + getString(R.string.teacher);
                        suffTitle = getString(R.string.str_mark_teacher);
                        imgLeft.setVisibility(View.INVISIBLE);
                        imgRight.setVisibility(View.VISIBLE);
                        imgLeft.setClickable(false);
                        imgRight.setClickable(true);
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) middleView.getLayoutParams();
                        params.setMargins(DensityUtils.dp2px(getContext(), 7), 0, 0, 0);
                        middleView.setLayoutParams(params);

                        LinearLayout.LayoutParams layoutParams1 = (LinearLayout.LayoutParams) middleViewItem.getLayoutParams();
                        layoutParams1.setMargins(DensityUtils.dp2px(getContext(), 8), 0, 15, 0);

                        middleView.setBackgroundResource(R.drawable.check_mark_right);
                    } else {
                        imgLeft.setVisibility(View.VISIBLE);
                        imgRight.setVisibility(View.INVISIBLE);
                        imgLeft.setClickable(true);
                        imgRight.setClickable(false);
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) middleView.getLayoutParams();
                        params.setMargins(0, 0, DensityUtils.dp2px(getContext(), 7), 0);

                        LinearLayout.LayoutParams layoutParams1 = (LinearLayout.LayoutParams) middleViewItem.getLayoutParams();
                        layoutParams1.setMargins(DensityUtils.dp2px(getContext(), 15), 0, 0, 0);

                        middleView.setBackgroundResource(R.drawable.check_mark_left);

                    }

                    if (getMemeberId().equalsIgnoreCase(data.getCreateId())) {
                        createName = getString(R.string.me);
                        title.setText(new StringBuilder().append(createName).append(suffTitle));
                    } else {
                        SpannableString spannableString = new SpannableString(new StringBuilder(createName).append(suffTitle));
                        ForegroundColorSpan colorSpan = new ForegroundColorSpan(getResources().getColor(R.color.com_text_green));
                        spannableString.setSpan(colorSpan, 0, createName.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

                        title.setText(spannableString);
                    }

                    String commitTime = data.getCreateTime();
                    if (!TextUtils.isEmpty(commitTime)) {
                        if (commitTime.contains(":")) {
                            //精确到分
                            commitTime = commitTime.substring(0, commitTime.lastIndexOf(":"));
                        }
                        date.setText(commitTime);
                    }

                    MyApplication.getThumbnailManager(getActivity()).displayUserIconWithDefault(
                            AppSettings.getFileUrl(data.getHeadPicUrl()), imgLeft,
                            R.drawable.default_user_icon);
                    //点击头像进入个人详情
                    imgLeft.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //游客之类的memberId为空的不给点击。
                            if (!TextUtils.isEmpty(data.getMemberId()) && isAssistanceModel) {
                                ActivityUtils.enterPersonalSpace(getActivity(), data.getMemberId());
                            }
                        }
                    });

                    MyApplication.getThumbnailManager(getActivity()).displayUserIconWithDefault(
                            AppSettings.getFileUrl(data.getHeadPicUrl()), imgRight,
                            R.drawable.default_user_icon);
                    //点击头像进入个人详情
                    imgRight.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //游客之类的memberId为空的不给点击。
                            if (!TextUtils.isEmpty(data.getMemberId()) && isAssistanceModel) {
                                TutorialHomePageActivity.show(getActivity(), new TutorialParams(data.getMemberId()));
                            }
                        }
                    });
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            isOriginal = false;
                            openCourse(data.getResId(), false, false);
                        }
                    });

                    view.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            if (isAssistanceModel) {
                                return true;
                            } else if (isAnswerTaskOrderQuestion) {
                                if (cardParam.isHeadMaster()
                                        || cardParam.isOnlineReporter()
                                        || cardParam.isOnlineHost()) {
                                    //小编主编班主任可以删除全部
                                    upDateDeleteButtonShowStatus(data, true);
                                    return true;
                                }
                            } else {
                                if (isOnlineReporter
                                        || isOnlineHost
                                        || (isHeadMaster && roleType != RoleType.ROLE_TYPE_PARENT)
                                        || (roleType == RoleType.ROLE_TYPE_TEACHER
                                        && task.getCreateId().equalsIgnoreCase(getMemeberId()))) {
                                    //小编主编班主任可以删除全部
                                    upDateDeleteButtonShowStatus(data, true);
                                    return true;
                                }
                            }
                            return false;
                        }
                    });

                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        return;
                    }
                    CheckMarkInfo.ModelBean data = (CheckMarkInfo.ModelBean) holder.data;
                    if (data != null) {
                        openCourse(data.getResId(), false, false);
                    }
                }
            };

            setCurrAdapterViewHelper(listView, listViewHelper);
        }
    }

    private void initAssistantMarkData() {
        isAssistanceModel = true;
        resId = commitTask.getStudentResId();
        thumbPic = commitTask.getStudentResThumbnailUrl();
        title = commitTask.getStudentResTitle();
    }

    private void initExerciseData() {
        isAnswerTaskOrderQuestion = true;
        exerciseItem = cardParam.getExerciseItem();
        if (exerciseItem != null) {
            title = getString(R.string.str_question_name, exerciseItem.getName());
            String typeName = exerciseItem.getType_name() + " (" +
                    getString(R.string.str_eval_score, exerciseItem.getScore()) + " )";
            //显示类型名
            TextView typeNameTextV = (TextView) findViewById(R.id.tv_exercise_type);
            if (typeNameTextV != null) {
                typeNameTextV.setVisibility(View.VISIBLE);
                typeNameTextV.setText(typeName);
            }
            List<MediaData> mediaDataList = exerciseItem.getDatas();
            if (mediaDataList != null && mediaDataList.size() > 0) {
                thumbPic = mediaDataList.get(0).resourceurl;
            }
            if (exerciseItem.getEqState() == 4) {
                score = Utils.changeDoubleToInt(exerciseItem.getStudent_score());
            }
        }
    }

    private void initAnswerParsingData() {
        if (exerciseItem == null) {
            return;
        }
        LinearLayout answerParsingLayout = (LinearLayout) findViewById(R.id.ll_answer_card_parsing);
        answerParsingLayout.setVisibility(View.VISIBLE);
        for (int i = 0; i < 2; i++) {
            View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.item_answer_check_mark, null);
            TextView itemTitleTextV = (TextView) itemView.findViewById(R.id.tv_title_name);
            TextView contentTextV = (TextView) itemView.findViewById(R.id.tv_answer_content);
            TextView picImageView = (TextView) itemView.findViewById(R.id.tv_pic_image);
            ImageView arrowImageV = (ImageView) itemView.findViewById(R.id.iv_arrow_icon);
            LinearLayout itemLayout = (LinearLayout) itemView.findViewById(R.id.ll_item_title);
            LinearLayout flContent = (LinearLayout) itemView.findViewById(R.id.fl_content);
            flContent.setVisibility(View.GONE);
            itemLayout.setOnClickListener(v -> {
                if (flContent.getVisibility() == View.VISIBLE) {
                    arrowImageV.setImageResource(R.drawable.arrow_gray_down_icon);
                    flContent.setVisibility(View.GONE);
                } else {
                    arrowImageV.setImageResource(R.drawable.arrow_gray_up_icon);
                    flContent.setVisibility(View.VISIBLE);
                }
            });
            if (i == 0) {
                //参考答案
                itemTitleTextV.setText(getString(R.string.str_reference_answer_no_point));
                String rightAnswer = exerciseItem.getRight_answer();
                String resUrl = exerciseItem.getRight_answer_res_url();
                contentTextV.setText(rightAnswer);
                if (!TextUtils.isEmpty(resUrl)) {
                    picImageView.setVisibility(View.VISIBLE);
                    if (TextUtils.isEmpty(rightAnswer)) {
                        contentTextV.setVisibility(View.GONE);
                    }
                    StudyTaskUtils.showAnswerCardViewDetail(getActivity(),
                            exerciseItem.getRight_answer_res_id(),
                            resUrl,
                            exerciseItem.getRight_answer_res_name(),
                            picImageView);
                }
            } else {
                //答案解析
                itemTitleTextV.setText(getString(R.string.str_answer_parsing_no_point));
                String content = exerciseItem.getAnalysis();
                if (TextUtils.isEmpty(content)) {
                    content = getString(R.string.str_no_analyse_tip);
                }
                contentTextV.setText(content);
                if (!TextUtils.isEmpty(exerciseItem.getAnalysis_res_url())) {
                    //答案解析的resUrl
                    picImageView.setVisibility(View.VISIBLE);
                    if (TextUtils.isEmpty(exerciseItem.getAnalysis())) {
                        contentTextV.setVisibility(View.GONE);
                    }
                    StudyTaskUtils.showAnswerCardViewDetail(getActivity(),
                            exerciseItem.getAnalysis_res_id(),
                            exerciseItem.getAnalysis_res_url(),
                            exerciseItem.getAnalysis_res_name(),
                            picImageView);
                }
            }
            answerParsingLayout.addView(itemView);
        }
    }

    private void deleteDialog(final CheckMarkInfo.ModelBean data) {
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(getActivity(), null,
                getString(R.string.confirm_delete)
                , getString(R.string.cancel),
                null, getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                deleteData(data);
            }
        });
        messageDialog.show();
    }

    /**
     * 删除批阅提问列表  //小编主编班主任可以删除全部
     *
     * @param data
     */
    private void deleteData(final CheckMarkInfo.ModelBean data) {
        if (getUserInfo() == null) {
            return;
        }
        final Map<String, Object> params = new ArrayMap<>();
        params.put("Id", data.getId());
        final DefaultDataListener listener =
                new DefaultDataListener<DataModelResult>(DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);

                        if (getResult() != null && getResult().isSuccess()) {
                            TipMsgHelper.ShowLMsg(getMyApplication(), R.string.delete_success);
                            getCurrAdapterViewHelper().getData().remove(data);
                            getCurrAdapterViewHelper().update();
                        } else {
                            TipMsgHelper.ShowLMsg(getMyApplication(), R.string.delete_failure);

                        }
                    }
                };
        listener.setShowLoading(true);
        postRequest(ServerUrl.POST_REMOVE_COMMIT_TASKREVIEW, params, listener);
    }


    /**
     * 更新分数
     */
    private void updateScore() {
//        if (isAnswerTaskOrderQuestion) {
        //答题的打分
        if (!TextUtils.isEmpty(score)) {
            tvExerciseScoreTextV.setVisibility(View.VISIBLE);
            tvExerciseScoreTextV.setText(getString(R.string.str_eval_score, score));
        }
//        } else {
//            String str = getString(R.string.str_score);
//            if (TextUtils.isEmpty(score)) {
//                mTvSore.setVisibility(View.GONE);
//            } else {
//                mTvSore.setVisibility(View.VISIBLE);
//                SpannableString spannableString = new SpannableString(new StringBuilder(str).append(score));
//                ForegroundColorSpan colorSpan = new ForegroundColorSpan(getResources().getColor(R.color.com_text_red));
//                spannableString.setSpan(colorSpan, str.length(), spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
//                RelativeSizeSpan sizeSpan = new RelativeSizeSpan(1.2f);
//                spannableString.setSpan(sizeSpan, str.length(), spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
//                mTvSore.setText(spannableString);
//            }
//        }
    }


    /**
     * 模拟数据
     */
    private void loadCommonData() {
        if (isAssistanceModel) {
            loadAssistantMarkData();
        } else if (isAnswerTaskOrderQuestion) {
            loadAnswerCardData();
        } else {
            loadMarkData();
        }
    }

    private void loadAssistantMarkData() {
        Map<String, Object> params = new ArrayMap<>();
        params.put("AssistTask_Id", commitTask.getId());
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_ASSIST_REVIEW_LIST_BASE_URL, params,
                new DefaultPullToRefreshDataListener<CheckMarkResult>(
                        CheckMarkResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        CheckMarkInfo result = JSONObject.parseObject(jsonString,
                                CheckMarkInfo.class);
                        if (result.getErrorCode() != 0 || result.getModel() == null) {
                            return;
                        }
                        JSONObject jsonObject = JSONObject.parseObject(jsonString);
                        if (jsonObject != null) {
                            JSONObject model = jsonObject.getJSONObject("Model");
                            if (model != null) {
                                JSONArray jsonArray = model.getJSONArray("Data");
                                if (jsonArray != null && jsonArray.size() > 0) {
                                    List<CheckMarkInfo.ModelBean> list =
                                            JSONArray.parseArray(jsonArray.toString(), CheckMarkInfo.ModelBean.class);
                                    if (list != null && list.size() > 0) {
                                        Collections.reverse(list);
                                        getCurrAdapterViewHelper().setData(list);
                                    }
                                }
                            }
                        }
                    }
                });
    }

    private void loadMarkData() {
        Map<String, Object> params = new ArrayMap<>();
        if (isFromMOOC) {
            params.put("CommitTaskOnlineId", CommitTaskId);
        } else {
            params.put("CommitTaskId", CommitTaskId);
        }
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_LOADCOMMITTASKREVIEWLIST, params,
                new DefaultPullToRefreshDataListener<CheckMarkResult>(
                        CheckMarkResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
//                            super.onSuccess(jsonString);
                        CheckMarkInfo result = JSONObject.parseObject(jsonString,
                                CheckMarkInfo.class);
                        if (result.getErrorCode() != 0 || result.getModel() == null) {
                            return;
//
                        }
                        List<CheckMarkInfo.ModelBean> list = result.getModel();
                        Collections.reverse(list);
                        removeAssistanceData(list);
                        getCurrAdapterViewHelper().setData(list);

                    }
                });
    }

    private void removeAssistanceData(List<CheckMarkInfo.ModelBean> list) {
        if (list != null && list.size() > 0) {
            Iterator<CheckMarkInfo.ModelBean> iterable = list.iterator();
            if (iterable.hasNext()) {
                CheckMarkInfo.ModelBean modelBean = iterable.next();
                if (modelBean != null && modelBean.getReviewFlag() == 1) {
                    if (commitTask != null && TextUtils.equals(getMemeberId(),
                            commitTask.getStudentId())) {

                    } else {
                        iterable.remove();
                    }
                }
            }
        }
    }

    /**
     * 模拟数据
     */
    private void loadAnswerCardData() {
        Map<String, Object> params = new ArrayMap<>();
        if (cardParam.isFromOnlineStudyTask()) {
            params.put("CommitTaskOnlineId", cardParam.getCommitTaskId());
        } else {
            params.put("CommitTaskId", cardParam.getCommitTaskId());
        }
        if (exerciseItem != null) {
            params.put("EQId", exerciseItem.getIndex());
        }
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.LOAD_SUBJECTIVE_REVIEW_LIST_BASE_URL, params,
                new DefaultPullToRefreshDataListener<DataModelResult>(
                        DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null || TextUtils.isEmpty(jsonString)) {
                            return;
                        }
                        JSONObject jsonObject = JSONObject.parseObject(jsonString);
                        if (jsonObject != null) {
                            JSONObject modeResult = jsonObject.getJSONObject("Model");
                            if (modeResult != null) {
                                JSONArray jsonArray = modeResult.getJSONArray("DataList");
                                if (jsonArray != null) {
                                    List<CheckMarkInfo.ModelBean> list = JSONObject.parseArray
                                            (jsonArray.toString(), CheckMarkInfo.ModelBean.class);
                                    if (list != null && list.size() > 0) {
                                        Collections.reverse(list);
                                        removeAssistanceData(list);
                                        getCurrAdapterViewHelper().setData(list);
                                    }
                                }
                            }
                        }
                    }
                });
    }


    @Override
    public void onClick(View v) {
        if (R.id.tv_access_details == v.getId()) {
            if (isAnswerTaskOrderQuestion) {
                //查看原题
                enterLookOriginQuestion();
            } else {
                //打开微课详情页面
                StudyTask myTask = new StudyTask();
                myTask.setResId(resId);
                Bundle arguments = getArguments();
                arguments.putBoolean(ActivityUtils.EXTRA_IS_NEED_HIDE_COLLECT_BTN, false);
                if (isFromMOOC && isAudition) {
                    //来自mooc并且是试听
                    PassParamhelper mParam = new PassParamhelper();
                    mParam.isFromLQMOOC = true;
                    mParam.isAudition = true;
                    arguments.putSerializable(PassParamhelper.class.getSimpleName(), mParam);
                }
                arguments.putSerializable(PictureBooksDetailFragment.Constants.ACTION_TASKMARKPARAM, mTaskMarkParam);
                CourseOpenUtils.openCourseDetailsDirectly(getActivity(),
                        myTask, roleType, getMemeberId(), studentId, null,
                        false, arguments);
            }
        } else if (v.getId() == R.id.contacts_header_left_btn) {
            backPress();
        } else if (v.getId() == R.id.ll_course) {
            if (isAnswerTaskOrderQuestion) {
                openQuestion();
            } else {
                openCourse(resId, false, false);
            }
        }
    }

    public void backPress() {
        if (isFromMOOC || isAnswerTaskOrderQuestion || isAssistanceModel) {
            getActivity().finish();
        } else {
            popStack();
        }
    }

    /**
     * 打开课件
     *
     * @param
     */
    public void openCourse(String resId,
                           boolean isOpenAnswerQuestion,
                           boolean applyMark) {
        String tempResId = resId;
        int resType = 0;
        if (resId.contains("-")) {
            String[] ids = resId.split("-");
            if (ids != null && ids.length == 2) {
                tempResId = ids[0];
                if (ids[1] != null) {
                    resType = Integer.parseInt(ids[1]);
                }
            }
        }
        if (resType > ResType.RES_TYPE_BASE) {
            //分页信息
            if (TextUtils.isEmpty(tempResId)) {
                return;
            }
            WawaCourseUtils wawaCourseUtils = new WawaCourseUtils(getActivity());
            wawaCourseUtils.loadSplitCourseDetail(Integer.parseInt(tempResId));
            wawaCourseUtils.setOnSplitCourseDetailFinishListener(new WawaCourseUtils
                    .OnSplitCourseDetailFinishListener() {
                @Override
                public void onSplitCourseDetailFinish(SplitCourseInfo info) {
                    if (info != null) {
                        CourseData courseData = info.getCourseData();
                        if (courseData != null) {
                            processOpenImageData(courseData,
                                    isOpenAnswerQuestion,
                                    applyMark);
                        }
                    }
                }
            });
        } else {
            //非分页信息
            WawaCourseUtils wawaCourseUtils = new WawaCourseUtils(getActivity());
            wawaCourseUtils.loadCourseDetail(resId);
            wawaCourseUtils.setOnCourseDetailFinishListener(new WawaCourseUtils.
                    OnCourseDetailFinishListener() {
                @Override
                public void onCourseDetailFinish(CourseData courseData) {
                    processOpenImageData(courseData,
                            isOpenAnswerQuestion,
                            applyMark);
                }
            });
        }
    }

    /**
     * 打开逻辑
     *
     * @param courseData
     */
    private void processOpenImageData(CourseData courseData,
                                      boolean isOpenAnswerQuestion,
                                      boolean applyMark) {
        if (courseData != null) {
            NewResourceInfo newResourceInfo = courseData.getNewResourceInfo();
            PlaybackParam playbackParam = new PlaybackParam();
            if (applyMark) {
                playbackParam.applyMark = true;
                playbackParam.applyMarkdata = markModel;
            } else if (isAnswerTaskOrderQuestion) {
                if (isOpenAnswerQuestion) {
                    //查看原题 跳转到指定的pageIndex
                    playbackParam.mIsHideToolBar = true;
                    ExerciseItem item = cardParam.getExerciseItem();
                    if (item != null) {
                        playbackParam.pageIndex = getQuestionIndex(item);
                    }
                } else if (cardParam.isOnlineReporter()
                        || cardParam.isOnlineHost()
                        || cardParam.getRoleType() == RoleType.ROLE_TYPE_STUDENT) {
                    boolean isMarked = false;
                    if (!TextUtils.isEmpty(score)) {
                        isMarked = true;
                    }
                    mTaskMarkParam = new TaskMarkParam(
                            isMarked,
                            true,
                            getTempRoleType(),
                            String.valueOf(cardParam.getCommitTaskId()),
                            false,
                            true,
                            score,
                            cardParam.isFromOnlineStudyTask());
                    mTaskMarkParam.cardParam = cardParam;
                    playbackParam.taskMarkParam = mTaskMarkParam;
                } else {
                    //游客身份
                    playbackParam.mIsHideToolBar = true;
                }
            } else if (isAssistanceModel) {
                mTaskMarkParam = new TaskMarkParam(
                        false,
                        true,
                        MainApplication.isTutorialMode() ? RoleType.ROLE_TYPE_TEACHER : RoleType.ROLE_TYPE_STUDENT,
                        String.valueOf(commitTask.getId()),
                        false,
                        false,
                        "",
                        true);
                playbackParam.isAssistanceModel = true;
                playbackParam.taskMarkParam = mTaskMarkParam;
            } else {
                if (mTaskMarkParam == null) {
                    //游客身份
                    playbackParam.mIsHideToolBar = true;
                } else {
                    playbackParam.taskMarkParam = mTaskMarkParam;
                }
            }
            if (newResourceInfo.isOnePage() || newResourceInfo.isStudyCard()) {
                ActivityUtils.openOnlineOnePage(getActivity(), newResourceInfo, false, playbackParam);
            } else {
                if (isOriginal) {
                    //需要显示
                    ActivityUtils.playOnlineCourse(getActivity(), newResourceInfo.getCourseInfo(), commitTask,
                            false, playbackParam);
                } else {
                    ActivityUtils.playOnlineCourse(getActivity(), newResourceInfo.getCourseInfo(), false, playbackParam);
                    isOriginal = true;
                }
            }
        }
    }

    private int getQuestionIndex(ExerciseItem item) {
        int pageIndex = 0;
        List<ExerciseItemArea> itemAreas = item.getAreaItemList();
        if (itemAreas != null && itemAreas.size() > 0) {
            for (ExerciseItemArea area : itemAreas) {
                if (!TextUtils.isEmpty(area.getPage_index())) {
                    pageIndex = Integer.valueOf(area.getPage_index());
                }
                break;
            }
        }
        return pageIndex;
    }

    private int getTempRoleType() {
        int tempRoleType = cardParam.getRoleType();
        if (cardParam.isOnlineHost()) {
            //空中课堂 的小编
            tempRoleType = RoleType.ROLE_TYPE_TEACHER;
        } else if (cardParam.isOnlineReporter()) {
            //空中课堂 的主持人
            tempRoleType = RoleType.ROLE_TYPE_EDITOR;
        }
        return tempRoleType;
    }

    /**
     * 打开题目
     */
    private void openQuestion() {
        if (exerciseItem != null) {
            List<MediaData> mediaDataList = exerciseItem.getDatas();
            if (mediaDataList != null && mediaDataList.size() > 0) {
                List<ImageInfo> resourceInfoList = new ArrayList<>();
                for (int i = 0; i < mediaDataList.size(); i++) {
                    ImageInfo newResourceInfo = new ImageInfo();
                    newResourceInfo.setTitle(cardParam.getCommitTaskTitle());
                    newResourceInfo.setResourceUrl(mediaDataList.get(i).resourceurl);
                    resourceInfoList.add(newResourceInfo);
                }
                GalleryActivity.newInstance(getActivity(), resourceInfoList, cardParam);
            }
        }
    }

    /**
     * 查看原题
     */
    private void enterLookOriginQuestion() {
        if (cardParam != null) {
            //记得传当前pageIndex
            openCourse(cardParam.getResId(), true, false);
        }
    }

    private LocalBroadcastManager mBroadcastManager;

    private void registResultBroadcast() {
        if (mBroadcastManager == null) {
            mBroadcastManager = LocalBroadcastManager.getInstance(getMyApplication());
            IntentFilter filter = new IntentFilter(ACTION_MARK_SCORE);
            mBroadcastManager.registerReceiver(mReceiver, filter);
        }

    }

    private void unRegistResultBroadcast() {
        if (mBroadcastManager != null && mReceiver != null) {
            mBroadcastManager.unregisterReceiver(mReceiver);
            mBroadcastManager = null;
            mReceiver = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegistResultBroadcast();
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String markScore = intent.getStringExtra(ACTION_MARK_SCORE);
                if (!TextUtils.isEmpty(markScore)) {
                    score = markScore;
                }
                if (mTaskMarkParam != null) {

                    mTaskMarkParam.isMarked = true;
                }
                if (!TextUtils.isEmpty(score)) {
                    updateScore();
                    if (exerciseItem != null) {
                        exerciseItem.setStudent_score(score);
                    }
                }
            }
        }
    };

    public static CheckMarkFragment newInstance(CommitTask data, String taskScore,
                                                StudyTask task, int roleType, boolean isVisitor, boolean isNeedMark, boolean isOnlineReporter, boolean isOnlineHost, boolean isHeaderTeacher) {

        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.COMMIT_TASK, data);
        bundle.putString(Constants.COMMITTASK_ID, data.getCommitTaskId() + "");
        bundle.putString(Constants.STUDENT_ID, data.getStudentId());
        bundle.putString(Constants.RES_ID, data.getStudentResId() + "");
        bundle.putString(Constants.TASK_TITLE, data.getStudentResTitle());
        bundle.putString(Constants.TASK_SCORE, taskScore);
        bundle.putString(Constants.TASK_PIC, data.getStudentResUrl());
        bundle.putSerializable(Constants.STUDYTASK, task);
        bundle.putInt(Constants.ROLE_TYPE, roleType);
        bundle.putBoolean(Constants.EXTRA_ISONLINEREPORTER, isOnlineReporter);
        bundle.putBoolean(Constants.EXTRA_ISONLINEHOST, isOnlineHost);
        bundle.putBoolean(HomeworkMainFragment.Constants.EXTRA_IS_HEAD_MASTER, isHeaderTeacher);
        if (!isVisitor) {
            int tempRoleType = roleType;
            if (isOnlineReporter) {
                //空中课堂 的小编
                tempRoleType = RoleType.ROLE_TYPE_TEACHER;
            } else if (isOnlineHost) {
                //空中课堂 的主持人
                tempRoleType = RoleType.ROLE_TYPE_EDITOR;
            }
            bundle.putSerializable(Constants.ACTION_TASKMARKPARAM,
                    new TaskMarkParam(data.isHasCommitTaskReview(),
                            task.getScoringRule() == 2,
                            tempRoleType,
                            data.getCommitTaskId() + "",
                            isVisitor,
                            isNeedMark,
                            data.getTaskScore(),
                            false));
        }
        CheckMarkFragment fragment = new CheckMarkFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public void upDateDeleteButtonShowStatus(CheckMarkInfo.ModelBean data, boolean onClick) {
        if (data == null && !deleteBtnVisibled) {
            return;
        }
        List<CheckMarkInfo.ModelBean> listData = getCurrAdapterViewHelper().getData();
        if (listData != null) {
            boolean flag = false;
            for (int i = 0, len = listData.size(); i < len; i++) {
                CheckMarkInfo.ModelBean task = listData.get(i);
                if (data == null) {
                    task.setShowDeleted(false);
                } else {
                    if (data.getId() == task.getId()) {
                        task.setShowDeleted(true);
                        flag = true;
                    } else {
                        task.setShowDeleted(false);
                    }
                }
            }
            deleteBtnVisibled = flag;
            if (onClick) {
                getCurrAdapterViewHelper().update();
            } else {
                rootView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getCurrAdapterViewHelper().update();
                    }
                }, 200);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent messageEvent){
        if (TextUtils.equals(messageEvent.getUpdateAction(),
                MessageEventConstantUtils.SEND_DO_COURSE_PATH_RESULT)) {
            Bundle bundle = messageEvent.getBundle();
            if (bundle != null) {
                String coursePath = bundle.getString(SlideManager.EXTRA_COURSE_PATH);
                String slidePath = bundle.getString(SlideManager.EXTRA_SLIDE_PATH);
                ApplyMarkHelper helper = new ApplyMarkHelper();
                helper.uploadCourse(getActivity(),slidePath,coursePath,commitTask.getId(),false);
            }
        }
    }
}
