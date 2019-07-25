package com.lqwawa.intleducation.module.discovery.adapter;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.MainApplication;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseAdapter;
import com.lqwawa.intleducation.base.utils.DisplayUtil;
import com.lqwawa.intleducation.base.utils.NumberTool;
import com.lqwawa.intleducation.base.utils.StringUtils;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.common.Common;
import com.lqwawa.intleducation.common.utils.DrawableUtil;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.module.discovery.tool.LoginHelper;
import com.lqwawa.intleducation.module.discovery.ui.CourseDetailsActivity;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailParams;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailType;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.pay.PayCourseDialogFragment;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.pay.PayDialogNavigator;
import com.lqwawa.intleducation.module.discovery.ui.lesson.detail.LessonSourceParams;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.course.chapter.CourseChapterParams;
import com.lqwawa.intleducation.module.discovery.ui.order.LQCourseOrderActivity;
import com.lqwawa.intleducation.module.discovery.vo.ChapterVo;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.learn.ui.CourseExamListActivity;
import com.lqwawa.intleducation.module.learn.ui.ExamsAndTestsActivity;
import com.lqwawa.intleducation.module.learn.ui.LessonDetailsActivity;
import com.lqwawa.intleducation.module.learn.ui.MyCourseDetailsActivity;
import com.lqwawa.intleducation.module.learn.ui.SxLessonDetailsActivity;
import com.lqwawa.intleducation.module.learn.ui.UnitExamListActivity;
import com.lqwawa.intleducation.module.organcourse.OrganLibraryType;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.osastudio.common.utils.TipMsgHelper;

import org.xutils.image.ImageOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XChen on 2016/11/14.
 * email:man0fchina@foxmail.com
 */

public class CourseChapterAdapter extends MyBaseAdapter {
    public static final int TYPE_CWORK = 10;
    public static final int TYPE_CEXAM = 11;
    public static final int TYPE_LESSON = 0;
    public static final int TYPE_EXAM = 1;
    private Activity activity;
    private List<ChapterVo> list;
    private LayoutInflater inflater;
    private int img_width;
    private int img_height;
    ImageOptions imageOptions;
    boolean needFlagRead;
    private OnContentChangedListener listener;
    private String courseId;
    private boolean isCourseSelect;
    private CourseVo courseVo;
    private CourseDetailParams courseDetailParams;
    // 是否从学程馆进来的,并且获取到授权
    private boolean isAuthorized;

    // 是已经加入的学程
    private boolean isJoinCourse;
    private boolean isOnlineTeacher;
    private boolean buyAll;

    // 是否老师看孩子
    private boolean mTeacherVisitor;

    private boolean tutorialMode;

    private OnSelectListener mOnSelectListener;//课程选择

    //是否从班级学程进入
    private boolean isClassCourseEnter;
    private boolean isFromScan;
    private String TAG = getClass().getSimpleName();

    public interface OnSelectListener {
        void onSelect(ChapterVo chapterVo);
    }

    public void setOnSelectListener(OnSelectListener selectListener) {
        this.mOnSelectListener = selectListener;
    }

    public CourseChapterAdapter(Activity activity, String courseId, boolean needFlagRead, boolean isOnlineTeacher, OnContentChangedListener listener) {
        this(activity, courseId, needFlagRead, listener);
        this.isOnlineTeacher = isOnlineTeacher;
    }

    public CourseChapterAdapter(Activity activity, String courseId, boolean needFlagRead, OnContentChangedListener listener) {
        this.activity = activity;
        this.courseId = courseId;
        this.needFlagRead = needFlagRead;
        this.listener = listener;
        this.inflater = LayoutInflater.from(activity);
        list = new ArrayList<ChapterVo>();
        // 是否获取到授权
        isAuthorized = activity.getIntent().getBooleanExtra("isAuthorized", false);

        int p_width = activity.getWindowManager().getDefaultDisplay().getWidth();
        img_width = 2 * p_width / 5;
        img_height = img_width * 10 / 16;

        imageOptions = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setCrop(false)
                //.setSize(img_width,img_height)
                .setLoadingDrawableId(R.drawable.img_def)//加载中默认显示图片
                .setFailureDrawableId(R.drawable.img_def)//加载失败后默认显示图片
                .build();

        tutorialMode = MainApplication.isTutorialMode();
        CourseDetailParams params = getCourseDetailParams();
        // 只有Mooc来的帮辅才作帮辅模式处理
        tutorialMode = tutorialMode && params.getCourseEnterType(false) == CourseDetailType.COURSE_DETAIL_MOOC_ENTER;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final ChapterVo vo = list.get(position);
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        }
        if (convertView == null ||
                (convertView != null && holder.isChildren != vo.getIsChildren())) {
            convertView = inflater.inflate(vo.getIsChildren() ?
                    R.layout.mod_course_lesson_list_item
                    : R.layout.mod_course_chapter_list_item, null);
            holder = new ViewHolder(convertView);
            holder.isChildren = vo.getIsChildren();
            convertView.setTag(holder);
        }
        int role = UserHelper.getCourseAuthorRole(activity.getIntent()
                .getStringExtra("memberId"), courseVo);

        if (vo.getIsChildren()) {
            //课程
            Drawable drawableFlagHere = activity.getResources().getDrawable(R.drawable.ic_flag_here);
            drawableFlagHere.setBounds(0, 0, (int) (DisplayUtil.sp2px(activity, 35)), (int) (DisplayUtil.sp2px(activity, 14)));
            if (vo.isIsHide()) {
                holder.lessonRootLay.setVisibility(View.GONE);
            } else {
                holder.lessonRootLay.setVisibility(View.VISIBLE);
                holder.lessonFlagView.setVisibility(View.VISIBLE);
                holder.mTvLessonState.setVisibility(View.VISIBLE);
                holder.lessonNameTv.setTextColor(activity.getResources().getColor(R.color.textGray));
                holder.lessonNameTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, DisplayUtil.sp2px(activity, 14));
                if (vo.getType() == TYPE_CWORK) {//测验
                    // @date   :2018/4/24 0024 下午 2:12
                    // @func   :测验缩进
                    setLessonViewMargin(holder.lessonLayout, (int) (20 * UIUtil.getResources().getDisplayMetrics().density));
                    holder.lessonFlagView.setVisibility(View.GONE);
                    holder.mTvLessonState.setVisibility(View.GONE);
                    holder.lessonNameTv.setText(activity.getResources().getString(R.string.unit_exam));
                    if ((vo.getFlag() & 0x01) > 0) {
                        holder.lessonNameTv.setTextColor(activity.getResources().getColor(R.color.black));
                        holder.lessonNameTv.setCompoundDrawables(null, null, drawableFlagHere, null);
                    } else {
                        holder.lessonNameTv.setTextColor(activity.getResources().getColor(R.color.black));
                        holder.lessonNameTv.setCompoundDrawables(null, null, null, null);
                    }

                    // 是否是老师
                    // boolean isTeacher = UserHelper.checkCourseAuthor(courseVo,isOnlineTeacher);
                    boolean isTeacher = isTeacher();
                    if (isJoinCourse && (vo.isBuyed() || isTeacher)) {
                        // 购买 或许是老师身份 显示测验
                        holder.resRootLay.setVisibility(View.VISIBLE);
                    } else {
                        // 未购买 不是老师身份
                        holder.resRootLay.setVisibility(View.GONE);
                    }

                    holder.resRootLay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int role = UserHelper.getCourseAuthorRole(activity.getIntent()
                                    .getStringExtra("memberId"), courseVo);
                            if (role == UserHelper.MoocRoleType.STUDENT) {
                                /*boolean isNeedLearn = checkLearningProgress(position);
                                if (isNeedLearn) {
                                    return;
                                }*/
                            }

                            // 判空
                            if (EmptyUtil.isEmpty(courseVo)) return;


                            UnitExamListActivity.start(activity, vo.getCourseId(), vo.getParentId(),
                                    activity.getIntent().getBooleanExtra("canEdit", false),
                                    activity.getIntent().getStringExtra("memberId"),
                                    activity.getIntent().getStringExtra("schoolId"),
                                    courseVo, courseDetailParams,
                                    activity.getIntent().getBooleanExtra(MyCourseDetailsActivity.KEY_IS_FROM_MY_COURSE, false));
                        }
                    });

                } else if (vo.getType() == TYPE_CEXAM) { //考试
                    // @date   :2018/4/24 0024 下午 2:12
                    // @func   :考试不缩进
                    setLessonViewMargin(holder.lessonLayout, (int) (0 * UIUtil.getResources().getDisplayMetrics().density));
                    holder.lessonFlagView.setVisibility(View.GONE);
                    holder.mTvLessonState.setVisibility(View.GONE);
                    if ((vo.getFlag() & 0x02) > 0) {
                        holder.lessonNameTv.setTextColor(activity.getResources().getColor(R.color.black));
                        holder.lessonNameTv.setCompoundDrawables(null, null, drawableFlagHere, null);
                    } else {
                        holder.lessonNameTv.setTextColor(activity.getResources().getColor(R.color.black));
                        holder.lessonNameTv.setCompoundDrawables(null, null, null, null);
                    }
                    // holder.lessonNameTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, DisplayUtil.sp2px(activity, 16));
                    holder.lessonNameTv.setText(activity.getResources().getString(R.string.exam));

                    // 只有全部购买,才显示考试 或者是老师身份
                    // boolean isTeacher = UserHelper.checkCourseAuthor(courseVo,isOnlineTeacher);
                    boolean isTeacher = isTeacher();
                    if (isJoinCourse && (buyAll || isTeacher)) {
                        // 已全部购买 或者是老师身份 显示考试
                        holder.resRootLay.setVisibility(View.VISIBLE);
                    } else {
                        // 不显示考试
                        holder.resRootLay.setVisibility(View.GONE);
                    }

                    holder.resRootLay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int role = UserHelper.getCourseAuthorRole(activity.getIntent()
                                    .getStringExtra("memberId"), courseVo);
                            // 家长，学生身份在考试节点下，都要判断
                            if (role == UserHelper.MoocRoleType.STUDENT || role == UserHelper.MoocRoleType.PARENT) {
                                if (courseVo != null && courseVo.getProgressStatus() != 2) {
                                    TipMsgHelper.ShowLMsg(activity, R.string.course_is_not_published);
                                    return;
                                }

                                boolean isNeedLearn = checkExamProgress(position);
                                if (isNeedLearn) {
                                    return;
                                }
                            }

                            // 判空
                            if (EmptyUtil.isEmpty(courseVo)) return;

                            CourseExamListActivity.start(activity, vo.getCourseId(),
                                    TextUtils.equals(activity.getClass().getSimpleName()
                                            , "MyCourseDetailsActivity"),
                                    activity.getIntent().getBooleanExtra("canEdit", false),
                                    activity.getIntent().getStringExtra("memberId"),
                                    activity.getIntent().getStringExtra("schoolId"),
                                    courseVo,
                                    courseDetailParams,
                                    activity.getIntent().getBooleanExtra(MyCourseDetailsActivity.KEY_IS_FROM_MY_COURSE, false));
                        }
                    });
                } else {//小节
                    // @date   :2018/4/24 0024 下午 2:12
                    // @func   :小节缩进
                    setLessonViewMargin(holder.lessonLayout, (int) (20 * UIUtil.getResources().getDisplayMetrics().density));

                    if (activity.getClass().getSimpleName().equals("MyCourseDetailsActivity")) {
                        if (vo.getStatus() == 1) {
                            holder.lessonFlagView.setBackgroundResource(R.drawable.com_cicle_green_bg);
                        } else {
                            holder.lessonFlagView.setBackgroundResource(R.drawable.com_cicle_gray_bg);
                        }
                    }

                    // V5.11.X改版
                    holder.lessonFlagView.setVisibility(View.GONE);
                    holder.mTvLessonState.setVisibility(View.VISIBLE);
                    // 节标识
                    holder.mTvLessonState.setText(String.format("%d/%d", vo.getFinishNum(), vo.getTotalNum()));
                    if (vo.getFinishNum() == 0) {
                        // 还未开始做
                        int color = UIUtil.getColor(R.color.section_state_idle_color);
                        int radius = DisplayUtil.dip2px(UIUtil.getContext(), 16);
                        holder.mTvLessonState.setBackground(DrawableUtil.createDrawable(color, color, radius));
                    } else if (vo.getFinishNum() < vo.getTotalNum()) {
                        // 做了一半
                        int color = UIUtil.getColor(R.color.section_state_starting_color);
                        int radius = DisplayUtil.dip2px(UIUtil.getContext(), 16);
                        holder.mTvLessonState.setBackground(DrawableUtil.createDrawable(color, color, radius));
                    } else if (vo.getFinishNum() >= vo.getTotalNum()) {
                        // 做完了
                        int color = UIUtil.getColor(R.color.section_state_complete_color);
                        int radius = DisplayUtil.dip2px(UIUtil.getContext(), 16);
                        holder.mTvLessonState.setBackground(DrawableUtil.createDrawable(color, color, radius));
                    }

                    boolean isTeacher = isTeacher();

                    //老师布置任务从班级学程选取资源时显示进度
                    if (isCourseSelect && isClassCourseEnter) {
                        holder.mTvLessonState.setVisibility(View.VISIBLE);
                    } else {
                        if (!isJoinCourse || (isTeacher && !isClassTeacher())) {
                            holder.mTvLessonState.setVisibility(View.GONE);
                        } else {
                            if (vo.isBuyed() || mTeacherVisitor) {
                                holder.mTvLessonState.setVisibility(View.VISIBLE);
                            } else {
                                holder.mTvLessonState.setVisibility(View.GONE);
                            }
                        }
                    }

                    if (vo.getFlag() > 0) {
                        holder.lessonNameTv.setTextColor(activity.getResources().getColor(R.color.textGray));
                        holder.lessonNameTv.setCompoundDrawables(null, null, drawableFlagHere, null);
                    } else {
                        holder.lessonNameTv.setTextColor(activity.getResources().getColor(R.color.textGray));
                        holder.lessonNameTv.setCompoundDrawables(null, null, null, null);
                    }
                    // @date   :2018/4/10 0010 上午 10:13
                    // @func   :V5.5取消课时字符串的显示
                    /*holder.lessonNameTv.setText(
                            StringUtils.getSectionNumString(activity, vo.getSectionName(), vo.getWeekNum())
                                    + "  " + vo.getName());*/


                    holder.lessonNameTv.setText(vo.getName());
                    holder.resRootLay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mOnSelectListener != null) {
                                mOnSelectListener.onSelect(vo);
                                return;
                            }
                            if (tutorialMode && !isJoinCourse) {
                                if (!vo.getParentId().equals(list.get(0).getId())) {
                                    UIUtil.showToastSafe(R.string.label_please_apply_to_be_tutorial);
                                } else {
                                    // 直接拦截
                                    toLessonDetailsActivity(vo, true,false);
                                }
                                return;
                            }


                            if (!isTeacher && !vo.isBuyed() && !vo.getParentId().equals(list.get(0).getId()) && !isAuthorized) {
                                // 不是从线下机构学程馆进来的，需要购买的还是要购买
                                if (mTeacherVisitor) {
                                    UIUtil.showToastSafe(R.string.tip_course_teacher_visitor_not_watch);
                                } else {
                                    ToastUtil.showToast(activity,
                                            activity.getResources().getString(R.string.buy_course_please));
                                }
                            } else {
                                /*if(!UserHelper.isLogin()){
                                    // 前去登录
                                    LoginHelper.enterLogin(activity);
                                    return;
                                }*/

                                int role = UserHelper.getCourseAuthorRole(activity.getIntent()
                                        .getStringExtra("memberId"), courseVo);

                                // 是否试听权限 没有阅读全部
                                boolean isFreeUser = (!vo.isBuyed()) && !UserHelper.checkCourseAuthor(courseVo, isOnlineTeacher);
                                CourseDetailParams params = getCourseDetailParams(courseVo, isFreeUser);

                                if (role == UserHelper.MoocRoleType.STUDENT ||
                                        role == UserHelper.MoocRoleType.PARENT) {
                                    // 11月02日，更改家长看孩子的课程，未购买的章节，也需要判断是否前去购买
                                    /*boolean isNeedLearn = checkLearningProgress(position);
                                    if (isNeedLearn) {
                                        return;
                                    }*/
                                    // 找到小节的单元 parentId = null
                                    /*String parentId = vo.getParentId();
                                    for (ChapterVo chapterVo : list) {
                                        if(!chapterVo.getIsChildren() && chapterVo.getId().equals(parentId)){
                                            if(isJoinCourse && !chapterVo.isBuyed()) {
                                                // 已加入课程,未购买,启动购买页面
                                                LQCourseOrderActivity.show(activity,courseVo);
                                                return;
                                            }
                                        }
                                    }*/

                                    boolean firstChapter = vo.getParentId().equals(list.get(0).getId());

                                    // 不是从学程馆进来的,购买
                                    int intId = Integer.parseInt(vo.getParentId());

                                    if (!firstChapter && isJoinCourse && !vo.isBuyed() && !isAuthorized) {

                                        // TODO 添加老师看学生的逻辑
                                        if (mTeacherVisitor) {
                                            UIUtil.showToastSafe(R.string.tip_course_teacher_visitor_not_watch);
                                            return;
                                        }

                                        if (courseDetailParams != null && courseDetailParams.getSchoolInfoEntity() != null
                                                && !courseDetailParams.getSchoolInfoEntity().hasJoinedSchool()) {
                                            UIUtil.showToastSafe(R.string.join_school_to_learn);
                                            return;
                                        }

                                        if (role == UserHelper.MoocRoleType.PARENT) {
                                            if (activity instanceof FragmentActivity) {
                                                FragmentActivity fragmentActivity = (FragmentActivity) activity;
                                                String childMemberId = activity.getIntent().getStringExtra("memberId");
                                                int intCourseId = Integer.parseInt(courseId);
                                                PayCourseDialogFragment.show(fragmentActivity.getSupportFragmentManager(), courseVo, null, true, childMemberId, intCourseId, PayCourseDialogFragment.TYPE_COURSE, new PayDialogNavigator() {
                                                    @Override
                                                    public void onChoiceConfirm(@NonNull String curMemberId) {
                                                        // 弹框点击确认的回调
                                                        // UIUtil.showToastSafe(curMemberId);
                                                        LQCourseOrderActivity.show(activity, params, courseVo, courseVo.getOrganId(), intId, curMemberId);
                                                    }
                                                });
                                            }
                                        } else {
                                            // 给自己买
                                            // String memberId = UserHelper.getUserId();
                                            // LQCourseOrderActivity.show(activity,params,courseVo,courseVo.getOrganId(),intId,memberId);
                                            if (activity instanceof FragmentActivity) {
                                                FragmentActivity fragmentActivity = (FragmentActivity) activity;
                                                String childMemberId = activity.getIntent().getStringExtra("memberId");
                                                int intCourseId = Integer.parseInt(courseId);
                                                PayCourseDialogFragment.show(fragmentActivity.getSupportFragmentManager(), courseVo, null, intCourseId, PayCourseDialogFragment.TYPE_COURSE, new PayDialogNavigator() {
                                                    @Override
                                                    public void onChoiceConfirm(@NonNull String curMemberId) {
                                                        // 弹框点击确认的回调
                                                        // UIUtil.showToastSafe(curMemberId);
                                                        LQCourseOrderActivity.show(activity, params, courseVo, courseVo.getOrganId(), intId, curMemberId);
                                                    }
                                                });
                                            }
                                        }

                                        return;
                                    }
                                }

                                if (mTeacherVisitor && !vo.isBuyed()) {
                                    UIUtil.showToastSafe(R.string.tip_course_teacher_visitor_not_watch);
                                    return;
                                }

                                // 用户从我的授课进来，在没购买的章节 小节点击之前，需要判断是否是家长，如果是家长，判断
                                // 是不是学程的老师，如果是学程的老师，则提示不允许学习自己的学程
                                if (!vo.isBuyed() && role == UserHelper.MoocRoleType.PARENT) {
                                    // 家长身份，需要判断，自己是不是老师
                                    if ((UserHelper.isCourseTeacher(courseVo) ||
                                            UserHelper.isCourseTutor(courseVo))) {
                                        boolean firstChapter = vo.getParentId().equals(list.get(0).getId());
                                        if (firstChapter)
                                            isFreeUser = true;
                                        if (isJoinCourse) {
                                            // 如果是讲师，就是自己的课程
                                            UIUtil.showToastSafe(R.string.label_not_study_course_for_me);
                                            return;
                                        }
                                    }
                                }

                                if (courseDetailParams != null && courseDetailParams.getSchoolInfoEntity() != null
                                        && !courseDetailParams.getSchoolInfoEntity().hasJoinedSchool()) {
                                    UIUtil.showToastSafe(R.string.join_school_to_learn);
                                    return;
                                }
//                                int examType = vo.getExamType();
                                String memberId = activity.getIntent().getStringExtra("memberId");
                                int teacherType = handleTeacherType();
                                CourseChapterParams courseChapterParams = new CourseChapterParams(memberId, role, teacherType, isFreeUser);
                                courseChapterParams.setCourseParams(params);
                                LessonSourceParams lessonSourceParams = LessonSourceParams.buildParams(courseChapterParams);
                                int libraryType = courseVo == null ? -1 : courseVo.getLibraryType();
                                //点击入口是三习教案馆
                                if (libraryType == OrganLibraryType.TYPE_TEACHING_PLAN) {
                                    ChapterVo chapterVo = list.get(position);
                                    int examType = chapterVo.getExamType();
                                    //vo.getExamType() 1是考试或者测试 0,是普通教案，测试是children层级

                                    if (examType == TYPE_EXAM) {
                                        //courseid,sectionId,token
//                                        CourseDetailParams courseDetailParams = getCourseDetailParams(courseVo, isFreeUser);
                                        ExamsAndTestsActivity.start(activity, courseId, vo.getId(), mTeacherVisitor,vo.getStatus(), lessonSourceParams);
//                                        ExamsAndTestsActivity.start(activity, courseId, vo.getId(), role, mTeacherVisitor, params,vo.getStatus());
                                    } else if (examType == TYPE_LESSON){
                                        //普通教案详情入口
                                        toLessonDetailsActivity(vo, isFreeUser,true);
                                    }
                                } else {
                                    toLessonDetailsActivity(vo, isFreeUser,false);
                                }
                                /*LessonDetailsActivity.start(activity, courseId, vo.getId(),
                                        vo.getSectionName(), vo.getName(),
                                        needFlagRead,
                                        activity.getIntent().getBooleanExtra("isBuy", false)
                                                || needFlagRead
                                                || vo.getParentId().equals(list.get(0).getId()) || isAuthorized,
                                        activity.getIntent().getBooleanExtra("canEdit", false),
                                        vo.getStatus()
                                        , activity.getIntent().getStringExtra("memberId"),
                                        vo.isContainAssistantWork(),
                                        activity.getIntent().getStringExtra("schoolId"),
                                        activity.getIntent().getBooleanExtra
                                                (MyCourseDetailsActivity.KEY_IS_FROM_MY_COURSE,
                                                        false), courseVo,isOnlineTeacher,isFreeUser);*/
                            }
                        }
                    });
                }
            }
        } else {//章节
            if (activity.getClass().getSimpleName().equals("MyCourseDetailsActivity")) {
                holder.chapter_flag_iv.setVisibility(View.VISIBLE);
                if (vo.getStatus() == 1) {
                    holder.chapter_flag_iv.setBackgroundResource(R.drawable.ic_chapter_flag);
                } else {
                    holder.chapter_flag_iv.setBackgroundResource(R.drawable.ic_chapter_normal);
                }
            } else {
                holder.chapter_flag_iv.setVisibility(View.GONE);
            }
            // V5.11.X改版
            holder.chapter_flag_iv.setVisibility(View.GONE);

            if (TextUtils.equals(activity.getClass().getSimpleName(), "MyCourseDetailsActivity") ||
                    TextUtils.equals(activity.getClass().getSimpleName(), "WatchStudentChapterActivity")) {
                holder.mTvChapterState.setActivated(vo.getStatus() == 1);
                if (vo.getStatus() == 1) {
                    // 已完成
                    if (isClassTeacher() && !mTeacherVisitor) {
                        holder.mTvChapterState.setText(R.string.label_all_the_arrangement);
                    } else {
                        holder.mTvChapterState.setText(R.string.label_task_complete);
                    }

                    holder.mTvChapterState.setVisibility(View.VISIBLE);
                } else {
                    holder.mTvChapterState.setText(R.string.label_task_starting);
                    if (isClassTeacher() && !mTeacherVisitor) {
                        holder.mTvChapterState.setVisibility(View.GONE);
                    } else {
                        holder.mTvChapterState.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                holder.mTvChapterState.setVisibility(View.GONE);
            }

            final boolean isTeacher = isTeacher();

            if (isCourseSelect || !isJoinCourse || (isTeacher && !isClassTeacher())) {
                // 是老师但不是班级学程的老师
                holder.mTvChapterState.setVisibility(View.GONE);
            } else {
                if (vo.isBuyed() || mTeacherVisitor) {
                    holder.mTvChapterState.setVisibility(View.VISIBLE);
                    // 添加班级学程的逻辑
                    if (isClassTeacher() && !mTeacherVisitor && vo.getStatus() == 0) {
                        // 班级学程的老师没有布置完成
                        holder.mTvChapterState.setVisibility(View.GONE);
                    } else {
                        holder.mTvChapterState.setVisibility(View.VISIBLE);
                    }
                } else {
                    holder.mTvChapterState.setVisibility(View.GONE);
                }
            }


            holder.chapterNameTv.setText(
                    StringUtils.getChapterNumString(activity, vo.getChapterName(),
                            NumberTool.numberToChinese(vo.getWeekNum())));

            holder.auditionTv.setVisibility(View.GONE);
            boolean isOwner = UserHelper.checkCourseAuthor(courseVo, isOnlineTeacher);
            // if (position == 0 && (!canReadAll() || !vo.isBuyed())) {//发现页面 第一张 显示试听字样
            // TODO 加判断逻辑 老师看孩子
            if (position == 0 && !vo.isBuyed() && (!isOwner || mTeacherVisitor)) {//发现页面 第一张 显示试听字样
                holder.auditionTv.setVisibility(View.VISIBLE);
                if (!isCourseSelect) {
                    holder.chapterTitleTv.setMaxWidth(DisplayUtil.dip2px(UIUtil.getContext(), 200));
                }
            } else {
                holder.auditionTv.setVisibility(View.GONE);
                holder.chapterTitleTv.setMaxWidth(Integer.MAX_VALUE);
            }

            if (isCourseSelect) {//课程选择
                holder.auditionTv.setVisibility(View.GONE);
            }

            holder.chapterTitleTv.setText(vo.getName());
            holder.chapterSplView.setVisibility(position == 0 ? View.GONE : View.VISIBLE);

            // 是否是老师
            // boolean isTeacher = UserHelper.checkCourseAuthor(courseVo,isOnlineTeacher);
            // final boolean isTeacher = isTeacher();
            // TODO 加判断逻辑 老师看孩子
            if (isTeacher && !mTeacherVisitor) {
                holder.tvPrice.setVisibility(View.INVISIBLE);
            } else {
                if (!(vo.getPrice() == 0) && !isCourseSelect) {
                    // 是已经加入课程显示的 并且不是免费的
                    // 不是学习任务选择
                    // V5.9后期，未加入也显示价格
                    holder.tvPrice.setVisibility(View.VISIBLE);
                } else {
                    holder.tvPrice.setVisibility(View.GONE);
                }


                if (vo.isBuyed()) {
                    // 章节已经购买 已购
                    holder.tvPrice.setText(R.string.label_yet_pay);
                    // 灰色 改成绿色
                    // holder.tvPrice.setTextColor(UIUtil.getColor(R.color.textSecond));
                    holder.tvPrice.setTextColor(UIUtil.getColor(R.color.textAccent));
                    holder.tvPrice.setBackground(new ColorDrawable(UIUtil.getColor(R.color.colorLight)));
                } else {
                    // 显示价格
                    holder.tvPrice.setText(Common.Constance.MOOC_MONEY_MARK + Integer.toString(vo.getPrice()));
                    // 红色
                    holder.tvPrice.setTextColor(UIUtil.getColor(R.color.textMoneyRed));
                    holder.tvPrice.setBackgroundResource(R.drawable.btn_red_stroke_radius_16);
                }
            }
            boolean isFreeUser = (!vo.isBuyed()) && !UserHelper.checkCourseAuthor(courseVo, isOnlineTeacher);
            CourseDetailParams params = getCourseDetailParams(courseVo, isFreeUser);
            // 添加点击事件
            holder.tvPrice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!UserHelper.isLogin()) {
                        LoginHelper.enterLogin(activity);
                        return;
                    }

                    // TODO 加判断逻辑 老师看孩子
                    if (mTeacherVisitor) return;

                    if (vo.isBuyed()) {
                        // 必须要是没有购买
                        // 如果已经购买 return
                        return;
                    }


                    int role = UserHelper.getCourseAuthorRole(activity.getIntent()
                            .getStringExtra("memberId"), courseVo);


                    if (role == UserHelper.MoocRoleType.STUDENT ||
                            role == UserHelper.MoocRoleType.PARENT) {

                        // 没有购买 点击购买
                        int intId = Integer.parseInt(vo.getId());
                        // 是否试听权限 没有阅读全部
//                        boolean isFreeUser = (!vo.isBuyed()) && !UserHelper.checkCourseAuthor(courseVo, isOnlineTeacher);
//                        CourseDetailParams params = getCourseDetailParams(courseVo, isFreeUser);

                        if (courseDetailParams != null && courseDetailParams.getSchoolInfoEntity() != null
                                && !courseDetailParams.getSchoolInfoEntity().hasJoinedSchool()) {
                            UIUtil.showToastSafe(R.string.join_school_to_learn);
                            return;
                        }

                        if (role == UserHelper.MoocRoleType.PARENT) {
                            if (activity instanceof FragmentActivity) {
                                FragmentActivity fragmentActivity = (FragmentActivity) activity;
                                String childMemberId = activity.getIntent().getStringExtra("memberId");
                                int intCourseId = Integer.parseInt(courseId);
                                PayCourseDialogFragment.show(fragmentActivity.getSupportFragmentManager(), courseVo, null, true, childMemberId, intCourseId, PayCourseDialogFragment.TYPE_COURSE, new PayDialogNavigator() {
                                    @Override
                                    public void onChoiceConfirm(@NonNull String curMemberId) {
                                        // 弹框点击确认的回调
                                        // UIUtil.showToastSafe(curMemberId);
                                        LQCourseOrderActivity.show(activity, params, courseVo, courseVo.getOrganId(), intId, curMemberId);
                                    }
                                });
                            }

                        } else {
                            // 给自己买
                            // String memberId = UserHelper.getUserId();
                            // LQCourseOrderActivity.show(activity,params,courseVo,courseVo.getOrganId(),intId,memberId);
                            if (activity instanceof FragmentActivity) {
                                FragmentActivity fragmentActivity = (FragmentActivity) activity;
                                String childMemberId = activity.getIntent().getStringExtra("memberId");
                                int intCourseId = Integer.parseInt(courseId);
                                PayCourseDialogFragment.show(fragmentActivity.getSupportFragmentManager(), courseVo, null, intCourseId, PayCourseDialogFragment.TYPE_COURSE, new PayDialogNavigator() {
                                    @Override
                                    public void onChoiceConfirm(@NonNull String curMemberId) {
                                        // 弹框点击确认的回调
                                        // UIUtil.showToastSafe(curMemberId);
                                        LQCourseOrderActivity.show(activity, params, courseVo, courseVo.getOrganId(), intId, curMemberId);
                                    }
                                });
                            }
                        }
                        return;
                    }

                    if (role == UserHelper.MoocRoleType.PARENT) {
                        if (UserHelper.isCourseTeacher(courseVo) || UserHelper.isCourseTutor(courseVo)) {
                            // 如果是讲师，就是自己的课程
                            UIUtil.showToastSafe(R.string.label_not_study_course_for_me);
                            return;
                        }
                    }

                }
            });

            if (vo.isIsHide()) {
                holder.hideLessonIv.setImageDrawable(activity.getResources()
                        .getDrawable(R.drawable.arrow_down_ico));
            } else {
                holder.hideLessonIv.setImageDrawable(activity.getResources()
                        .getDrawable(R.drawable.arrow_up_gray_ico));
            }
            //如果是考试 则隐藏f折叠按钮
//            int examType = vo.getExamType();
            int examType = list.get(position).getExamType();
            holder.hideLessonIv.setVisibility(examType == TYPE_EXAM ? View.GONE : View.VISIBLE);
            holder.titleLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnSelectListener !=null){
                        mOnSelectListener.onSelect(vo);
//                        return;
                    }
                    //三习教案馆考试章节跳转
                    String memberId = activity.getIntent().getStringExtra("memberId");
                    int teacherType = handleTeacherType();
                    CourseChapterParams courseChapterParams = new CourseChapterParams(memberId, role, teacherType, isFreeUser);
                    courseChapterParams.setCourseParams(params);
                    LessonSourceParams lessonSourceParams = LessonSourceParams.buildParams(courseChapterParams);
                    int libraryType = courseVo == null ? -1 : courseVo.getLibraryType();
                    if (libraryType == OrganLibraryType.TYPE_TEACHING_PLAN && examType == TYPE_EXAM) {
                        ExamsAndTestsActivity.start(activity, courseId, vo.getId(), mTeacherVisitor, vo.getStatus(), lessonSourceParams);
                    } else {
                        boolean hide = !list.get(position).isIsHide();
                        list.get(position).setIsHide(hide);
                        for (int i = position + 1; i < list.size(); i++) {
                            if (list.get(i).getIsChildren()
                                    && list.get(i).getType() != TYPE_CEXAM) {
                                list.get(i).setIsHide(hide);
                            } else {
                                break;
                            }
                        }
                        notifyDataSetChanged();
                    }
                }
            });
        }


        if (tutorialMode) {
            // 如果是帮辅模式
            // 不显示章状态
            if (EmptyUtil.isNotEmpty(holder.mTvChapterState))
                holder.mTvChapterState.setVisibility(View.GONE);
            // 不显示节状态
            if (EmptyUtil.isNotEmpty(holder.mTvLessonState))
                holder.mTvLessonState.setVisibility(View.GONE);
            // 不显示价格
            if (EmptyUtil.isNotEmpty(holder.tvPrice))
                holder.tvPrice.setVisibility(View.GONE);
            // 不显示试听
            if (EmptyUtil.isNotEmpty(holder.auditionTv))
                holder.auditionTv.setVisibility(View.GONE);
        }

        return convertView;
    }

    /**
     * 当显示课程小节缩进的时候,需要设置Layout Margin
     *
     * @param view            设置对象
     * @param leftMarginValue 设置值
     */
    private void setLessonViewMargin(@NonNull View view, int leftMarginValue) {
        LinearLayout.LayoutParams mLayoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
        mLayoutParams.setMargins(leftMarginValue, 0, 0, 0);
        view.setLayoutParams(mLayoutParams);
    }

    private boolean canReadAll() {
        boolean isCourseDetails = activity.getClass().getSimpleName().equals("CourseDetailsActivity");
        boolean isClassTeacherDetails = activity.getClass().getSimpleName().equalsIgnoreCase("WatchStudentChapterActivity");
        if (!isCourseDetails && !isClassTeacherDetails) {
            return true;
        } else {
            boolean isFree = activity.getIntent().getIntExtra("payType", 0) == 0;
            boolean isBuy = activity.getIntent().getBooleanExtra("isBuy", false);
            boolean isJoin = activity.getIntent().getBooleanExtra("isJoin", false);
            boolean isExpire = activity.getIntent().getBooleanExtra("isExpire", false);
            boolean canEdit = activity.getIntent().getBooleanExtra("canEdit", false);
            if (canEdit) {
                if (isFree) {
                    return isJoin;
                } else {
                    return isBuy && isJoin && !isExpire;
                }
            } else {
                return false;
            }
        }
    }

    private class ViewHolder {
        boolean isChildren;
        LinearLayout lessonRootLay;
        LinearLayout lessonLayout;
        View chapterSplView;
        LinearLayout resRootLay;
        TextView lessonNameTv;
        View lessonFlagView;
        ImageView chapter_flag_iv;
        TextView mTvChapterState;
        TextView mTvLessonState;


        LinearLayout chapterRootLayout;
        LinearLayout titleLay;
        TextView chapterNameTv;
        TextView chapterTitleTv;
        TextView auditionTv;
        TextView tvPrice;
        ImageView hideLessonIv;

        public ViewHolder(View parent) {
            lessonRootLay = (LinearLayout) parent.findViewById(R.id.lesson_root_lay);
            chapterSplView = parent.findViewById(R.id.chapter_spl_view);
            resRootLay = (LinearLayout) parent.findViewById(R.id.res_root_lay);
            lessonNameTv = (TextView) parent.findViewById(R.id.lesson_name_tv);
            lessonFlagView = parent.findViewById(R.id.lesson_flag_view);
            chapter_flag_iv = (ImageView) parent.findViewById(R.id.chapter_flag_iv);
            mTvChapterState = (TextView) parent.findViewById(R.id.tv_chapter_state);
            mTvLessonState = (TextView) parent.findViewById(R.id.tv_lesson_state);
            lessonLayout = (LinearLayout) parent.findViewById(R.id.lesson_layout);

            chapterRootLayout = (LinearLayout) parent.findViewById(R.id.chapter_root_layout);
            titleLay = (LinearLayout) parent.findViewById(R.id.title_lay);
            chapterNameTv = (TextView) parent.findViewById(R.id.chapter_name_tv);
            chapterTitleTv = (TextView) parent.findViewById(R.id.chapter_title_tv);
            auditionTv = (TextView) parent.findViewById(R.id.audition_tv);
            tvPrice = (TextView) parent.findViewById(R.id.tv_price);
            hideLessonIv = (ImageView) parent.findViewById(R.id.hide_lesson_iv);
        }
    }

    /**
     * @author medici
     * @desc 设置是否全部购买
     */
    public void buyAll(boolean buyAll) {
        this.buyAll = buyAll;
    }

    /**
     * 下拉刷新设置数据
     *
     * @param list
     */
    public void setData(List<ChapterVo> list) {
        if (list != null) {
            if (this.list != null && this.list.size() > 0) {
                this.list.clear();
            } else if (this.list == null) {
                this.list = new ArrayList<ChapterVo>();
            }
            for (int i = 0; i < list.size(); i++) {
                ChapterVo vo = list.get(i);
                this.list.add(vo);
                if (vo.getChildren() != null && vo.getChildren().size() > 0) {
                    for (int j = 0; j < vo.getChildren().size(); j++) {
                        ChapterVo lessonVo = vo.getChildren().get(j);
                        lessonVo.setSectionName(vo.getSectionName());
                        lessonVo.setWeekNum(String.format("%d", j + 1));
                        lessonVo.setIsChildren(true);
                        this.list.add(lessonVo);
                    }
                    if (needFlagRead && vo.getCworkSize() > 0) {
                        ChapterVo lessonVo = new ChapterVo();
                        lessonVo.setParentId(vo.getId());
                        lessonVo.setCourseId(vo.getCourseId());
                        lessonVo.setIsChildren(true);
                        lessonVo.setType(TYPE_CWORK);
                        lessonVo.setIsHide(false);
                        lessonVo.setFlag(vo.getFlag());
                        lessonVo.setBuyed(vo.isBuyed());
                        this.list.add(lessonVo);
                    }
                }
            }
        } else {
            this.list.clear();
        }
    }

    /**
     * 上拉加载更多，新增数据
     *
     * @param list
     */
    public void addData(List<ChapterVo> list) {
        if (this.list == null) {
            this.list = new ArrayList<ChapterVo>();
        }
        for (int i = 0; i < list.size(); i++) {
            ChapterVo vo = list.get(i);
            this.list.add(vo);
            if (vo.getChildren() != null && vo.getChildren().size() > 0) {
                for (int j = 0; j < vo.getChildren().size(); j++) {
                    ChapterVo lessonVo = vo.getChildren().get(j);
                    lessonVo.setIsChildren(true);
                    this.list.add(lessonVo);
                }
                if (needFlagRead && vo.getCworkSize() > 0) {
                    ChapterVo lessonVo = new ChapterVo();
                    lessonVo.setParentId(vo.getId());
                    lessonVo.setCourseId(vo.getCourseId());
                    lessonVo.setIsChildren(true);
                    lessonVo.setType(TYPE_CWORK);
                    lessonVo.setIsHide(false);
                    lessonVo.setBuyed(vo.isBuyed());
                    this.list.add(lessonVo);
                }
            }
        }
    }

    public void setJoinCourse(@NonNull boolean joinCourse) {
        this.isJoinCourse = joinCourse;
    }

    public void setIsFromScan(@NonNull boolean isFromScan) {
        this.isFromScan = isFromScan;
        if (isFromScan) {
            tutorialMode = false;
        }
    }

    public void setTeacherVisitor(boolean visitor) {
        this.mTeacherVisitor = visitor;
    }

    public void setCourseSelect(boolean b) {
        isCourseSelect = b;
    }

    public void setIsClassCourseEnter(boolean isClassCourseEnter) {
        this.isClassCourseEnter = isClassCourseEnter;
    }

    public void setCourseVo(CourseVo courseVo) {
        this.courseVo = courseVo;
    }

    public void setCourseDetailParams(CourseDetailParams params) {
        this.courseDetailParams = params;
        if (courseDetailParams != null && courseVo != null) {
            courseDetailParams.setBindSchoolId(courseVo.getBindSchoolId());
            courseDetailParams.setBindClassId(courseVo.getBindClassId());
        }
    }

    /**
     * 找到未学习的章节开始索引
     *
     * @return
     */
    private int getLearnChapterStartPos() {
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                ChapterVo vo = list.get(i);
                if (vo != null && !vo.getIsChildren() && vo.getStatus() != 1) {
                    return i;
                }
            }
            // 全部学习完
            return list.size() - 1;
        }
        return 0;
    }

    /**
     * 找到未学习的章节结束索引
     *
     * @return
     */
    private int getLearnChapterEndPos() {
        int pos = list != null ? list.size() : 0;
        int startPos = getLearnChapterStartPos();

        if (list != null && list.size() > 0) {
            if (startPos + 1 < list.size()) {
                for (int i = startPos + 1; i < list.size(); i++) {
                    ChapterVo vo = list.get(i);
                    if (vo != null && !vo.getIsChildren()) {
                        pos = i;
                        break;
                    }
                }
            }
        }
        if (pos > 0) {
            pos = pos - 1;
        }
        return pos;
    }

    /**
     * "考试"需判断前一个是否已完成
     *
     * @param position
     * @return
     */
    public boolean checkExamProgress(int position) {
        if (list != null && list.size() > 0 && position < list.size()) {
            ChapterVo chapterVo = list.get(position);
            if (TextUtils.isEmpty(chapterVo.getWeekNum())) {
                if (list.size() == 1) {
                    return false;
                }
                if (position > 1) {
                    for (int i = position; i >= 0; i--) {
                        ChapterVo item = list.get(i);
                        if (!item.getIsChildren()) {
                            if (item.getStatus() != 1) {
                                TipMsgHelper.ShowLMsg(activity, R.string.exam_chapter_first);
                                return true;
                            } else {
                                // 如果前一章已经学习完，继续向前遍历 V5.9新改
                                // return false;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * 学习进度判断，只有之前的章节全部学完才能学当前章
     *
     * @param position
     * @return
     */
    public boolean checkLearningProgress(int position) {
        int learnChapterEndPos = getLearnChapterEndPos();
        int learnChapterStartPos = getLearnChapterStartPos();
        //学习完全部章节
        if (learnChapterStartPos > 0 && learnChapterEndPos > 0 && learnChapterStartPos ==
                learnChapterEndPos) {
            return false;
        }
        if (position > learnChapterEndPos) {
            int startPos = getLearnChapterStartPos();
            if (list != null && list.size() > 0 && startPos < list.size()) {
                ChapterVo chapterVo = list.get(startPos);
                if (chapterVo != null) {
                    TipMsgHelper.ShowLMsg(activity, activity.getResources().getString(R.string
                            .learn_chapter_first, chapterVo.getName()));
                }
            }
            return true;
        } else {

        }
        return false;
    }

    /**
     * @desc 是否是老师身份
     */
    private boolean isTeacher() {
        boolean canEdit = activity.getIntent().getBooleanExtra("canEdit", false);
        if (!canEdit) {
            String memberId = activity.getIntent().getStringExtra("memberId");
            return UserHelper.MoocRoleType.TEACHER == UserHelper.getCourseAuthorRole(memberId, courseVo);
        } else {
            return UserHelper.checkCourseAuthor(courseVo, isOnlineTeacher);
        }
    }

    /**
     * 是否是班级学程的老师
     *
     * @return true 是班级学程的老师,显示状态信息
     */
    private boolean isClassTeacher() {
        CourseDetailParams params = getCourseDetailParams(null, false);
        return params.isClassCourseEnter() && params.isClassTeacher();
    }

    /**
     * 处理业务角色判断
     */
    private int handleBusinessRole() {
        String curMemberId = activity.getIntent().getStringExtra("memberId");
        int role = UserHelper.getCourseAuthorRole(curMemberId, courseVo, isOnlineTeacher);
        return role;
    }

    /**
     * 判断老师类型
     *
     * @return TeacherType
     */
    private int handleTeacherType() {
        int teacherType = UserHelper.TeacherType.TEACHER_DEFAULT;
        if (UserHelper.isCourseTeacher(courseVo)) {
            teacherType = UserHelper.TeacherType.TEACHER_LECTURER;
        } else if (UserHelper.isCourseTutor(courseVo)) {
            teacherType = UserHelper.TeacherType.TEACHER_TUTOR;
        } else if (UserHelper.isCourseCounselor(courseVo)) {
            teacherType = UserHelper.TeacherType.TEACHER_COUNSELOR;
        }

        return teacherType;
    }

    /**
     * 去节详情页面
     *
     * @param vo 章节
     */
    private void toLessonDetailsActivity(@NonNull ChapterVo vo, boolean isFreeUser,boolean isSxLesson) {
        // 判空
        if (EmptyUtil.isEmpty(courseVo)) return;

        String chapterId = vo.getId();
        String sectionName = vo.getSectionName();
        String name = vo.getName();
        boolean canEdit = activity.getIntent().getBooleanExtra("canEdit", false);
        // 当前节的状态
        int status = vo.getStatus();
        String memberId = activity.getIntent().getStringExtra("memberId");
        String schoolId = activity.getIntent().getStringExtra("schoolId");
        // 获取课程大纲所属课程的课程详情参数
        CourseDetailParams courseParams = getCourseDetailParams(courseVo, isFreeUser);

        int role = handleBusinessRole();
        // 自己的真实角色 和 老师类型
        int realRole = UserHelper.getCourseAuthorRole(UserHelper.getUserId(), courseVo, isOnlineTeacher);
        int teacherType = handleTeacherType();

        if (courseParams.isClassTeacher() || (tutorialMode && isJoinCourse)) {
            // 班级学程的老师
            if (mTeacherVisitor) {
                realRole = UserHelper.MoocRoleType.TEACHER;
            } else {
                role = UserHelper.MoocRoleType.TEACHER;
            }
            // 类型等于讲师
            teacherType = UserHelper.TeacherType.TEACHER_LECTURER;
        }

        if (courseParams.isClassParent() || courseParams.isOrganCounselor()) {
            // 这个时候遵从权限最大原则
            if (role != UserHelper.MoocRoleType.TEACHER) {
                // 已经是老师了,不需要变换成老师角色
                // 班级学程的家长,辅导老师身份处理
                // 机构的授权老师,辅导老师身份处理
                if (mTeacherVisitor) {
                    realRole = UserHelper.MoocRoleType.TEACHER;
                } else {
                    role = UserHelper.MoocRoleType.TEACHER;
                }
                // 类型等于辅导老师
                teacherType = UserHelper.TeacherType.TEACHER_COUNSELOR;
            }
        }

        CourseChapterParams params = new CourseChapterParams(memberId, role, teacherType, isFreeUser);
        params.fillVisitorInfo(mTeacherVisitor, realRole);
        params.setCourseParams(courseParams);
        StringBuilder stringBuilder = new StringBuilder();
        if (!TextUtils.isEmpty(courseVo.getTeachersId())) {
            stringBuilder.append(courseVo.getTeachersId());
        }
        if (!TextUtils.isEmpty(courseVo.getTutorId())) {
            stringBuilder.append(",");
            stringBuilder.append(courseVo.getTutorId());
        }
        params.setTeacherTutorIds(stringBuilder.toString());

        boolean isFromMyCourse = activity.getIntent().getBooleanExtra(MyCourseDetailsActivity.KEY_IS_FROM_MY_COURSE, false);

        if (isSxLesson) {
            SxLessonDetailsActivity.start(activity, courseId, chapterId,
                    sectionName, name, needFlagRead, true, canEdit,
                    status, memberId, vo.isContainAssistantWork(),
                    schoolId, isFromMyCourse, courseVo,
                    isOnlineTeacher, isFreeUser, params, null);
        } else {
            LessonDetailsActivity.start(activity, courseId, chapterId,
                    sectionName, name, needFlagRead, true, canEdit,
                    status, memberId, vo.isContainAssistantWork(),
                    schoolId, isFromMyCourse, courseVo,
                    isOnlineTeacher, isFreeUser, params, null);
        }
    }

    /**
     * 获取课程章节的参数
     *
     * @return
     */
    private CourseDetailParams getCourseDetailParams() {
        return getCourseDetailParams(courseVo, false);
    }

    /**
     * 获取课程章节的参数
     *
     * @return
     */
    private CourseDetailParams getCourseDetailParams(@NonNull CourseVo vo, boolean isFreeUser) {
        // 获取课程大纲所属课程的课程详情参数
        CourseDetailParams courseParams = null;
        if (activity.getIntent().hasExtra(CourseDetailsActivity.ACTIVITY_BUNDLE_OBJECT)) {
            courseParams = (CourseDetailParams) activity.getIntent().getSerializableExtra(CourseDetailsActivity.ACTIVITY_BUNDLE_OBJECT);
        } else {
            courseParams = new CourseDetailParams();
        }
        if (courseParams != null && courseVo != null) {
            courseParams.setBindSchoolId(courseVo.getBindSchoolId());
            courseParams.setBindClassId(courseVo.getBindClassId());
            courseParams.setCourseId(courseVo.getId());
            courseParams.setCourseName(courseVo.getName());
        }
        return courseParams;
    }
}
