package com.lqwawa.intleducation.module.onclass.detail.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.utils.DisplayUtil;
import com.lqwawa.intleducation.base.widgets.AppBarStateChangeListener;
import com.lqwawa.intleducation.base.widgets.ScrollChildSwipeRefreshLayout;
import com.lqwawa.intleducation.common.Common;
import com.lqwawa.intleducation.common.ui.CommentDialog;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.ImageUtil;
import com.lqwawa.intleducation.common.utils.KeyboardUtil;
import com.lqwawa.intleducation.common.utils.TimeUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.ClassDetailEntity;
import com.lqwawa.intleducation.factory.data.entity.JoinClassEntity;
import com.lqwawa.intleducation.factory.data.entity.OnlineCommentEntity;
import com.lqwawa.intleducation.factory.data.entity.school.SchoolInfoEntity;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.intleducation.factory.helper.OnlineCourseHelper;
import com.lqwawa.intleducation.factory.helper.SchoolHelper;
import com.lqwawa.intleducation.module.discovery.tool.LoginHelper;
import com.lqwawa.intleducation.module.discovery.ui.navigator.CourseDetailsNavigator;
import com.lqwawa.intleducation.module.onclass.OnlineClassRole;
import com.lqwawa.intleducation.module.onclass.detail.base.comment.ClassCommentFragment;
import com.lqwawa.intleducation.module.onclass.detail.join.JoinClassDetailActivity;
import com.lqwawa.intleducation.module.onclass.detail.notjoin.ClassDetailActivity;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.lqbaselib.pojo.MessageEvent;
import com.osastudio.common.popmenu.CustomPopWindow;
import com.osastudio.common.popmenu.EntryBean;
import com.osastudio.common.popmenu.PopMenuAdapter;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author mrmedici
 * @desc 班级详情的抽象基类
 */
public abstract class BaseClassDetailActivity<Presenter extends BaseClassDetailContract.Presenter> extends PresenterActivity<Presenter>
        implements BaseClassDetailContract.View<Presenter> ,
        BaseClassDetailNavigator,
        View.OnClickListener{

    protected static final int TYPE_MENU_MENU = 0;
    protected static final int TYPE_MENU_SHARE = 1;
    protected static final int TYPE_MENU_HISTORY = 2;

    protected static final String KEY_EXTRA_CLASS_ID = "KEY_EXTRA_CLASS_ID";
    protected static final String KEY_EXTRA_SCHOOL_ID = "KEY_EXTRA_SCHOOL_ID";
    protected static final String KEY_EXTRA_ID = "KEY_EXTRA_ID";
    protected static final String KEY_EXTRA_ROLE = "KEY_EXTRA_ROLE";
    protected static final String KEY_EXTRA_IS_COURSE_ENTER = "KEY_EXTRA_IS_COURSE_ENTER";
    protected static final String KEY_EXTRA_IS_SCHOOL_ENTER = "KEY_EXTRA_IS_SCHOOL_ENTER";

    protected CoordinatorLayout mCoordinatorLayout;

    // toolbar right
    private ImageView mToolbarRight;
    // 顶部缩略图
    protected ImageView mTopThumbnailView;
    // 班级名称
    protected TextView mClassName;
    // 开课时间
    protected TextView mClassTime;
    // 机构名称
    protected TextView mSchoolName;
    // +关注
    protected TextView mSchoolEnter;
    // 参加人数
    protected TextView mStudyNumber;
    // 班级评分控件
    protected RatingBar mRatingBar;
    // 班级评分
    protected TextView mGradeSource;

    // 公告容器
    protected FrameLayout mNoticeLayout;
    // 公告内容
    protected TextView mNoticeText;

    // Tab
    protected TabLayout mTabLayout;
    protected ViewPager mViewPager;

    private CustomPopWindow mPopWindow;

    protected LinearLayout mClassDetailBottomLayout;
    // 课程价格
    protected TextView mClassPrice;
    // 立即购买
    protected Button mBtnPay;
    
    protected LinearLayout mJoinClassDetailBottomLayout;
    // 新开课
    protected TextView mBtnCreateClass;
    // 完成授课
    protected TextView mBtnGiveLessons;

    protected LinearLayout mCommentLayout;
    // 评论内容
    private EditText mCommentContent;
    // 发送按钮
    private TextView mBtnSend;
    // 评论数据
    private OnlineCommentEntity mCommentEntity;
    // 评论Dialog
    private CommentDialog mCommentDialog;
    // 评论数据
    private CommentDialog.CommentData mCommentData;

    protected String mSchoolId;
    protected String mClassId;
    protected int mId;
    protected String mRole;
    // 是否是机构主页进入
    protected boolean isSchoolEnter;
    // 是否从学程过来
    protected boolean isCourseEnter;
    // 是否授课完成
    protected boolean isGiveFinish;
    // 是否是历史班
    protected boolean isGiveHistory;
    // 是否是家长
    protected boolean isParent;
    // 家长身份，孩子的memberId
    protected String childMemberId;

    // 上级页面传进来的参数
    protected ClassDetailParams mClassDetailParams;
    // 参加班级加入的信息
    protected JoinClassEntity mJoinClassEntity;
    // 当前班级详情的信息
    protected ClassDetailEntity mCurrentEntity;

    // 机构信息
    protected SchoolInfoEntity mSchoolEntity;
    protected String[] tabTexts = UIUtil.getStringArray(R.array.label_new_online_class_details_tabs);

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_class_detail;
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        mSchoolId = bundle.getString(KEY_EXTRA_SCHOOL_ID);
        mClassId = bundle.getString(KEY_EXTRA_CLASS_ID);
        isSchoolEnter = bundle.getBoolean(KEY_EXTRA_IS_SCHOOL_ENTER);
        isCourseEnter = bundle.getBoolean(KEY_EXTRA_IS_COURSE_ENTER);
        mId = bundle.getInt(KEY_EXTRA_ID);
        mRole = bundle.getString(KEY_EXTRA_ROLE);

        mClassDetailParams = (ClassDetailParams) bundle.getSerializable(ACTIVITY_BUNDLE_OBJECT);
        if(EmptyUtil.isNotEmpty(mClassDetailParams)){
            mJoinClassEntity = mClassDetailParams.getJoinClassEntity();
            if(EmptyUtil.isNotEmpty(mJoinClassEntity)){
                mSchoolId = mJoinClassEntity.getSchoolId();
                mClassId = mJoinClassEntity.getClassId();
            }
            isSchoolEnter = mClassDetailParams.isSchoolEnter();
            isCourseEnter = mClassDetailParams.isCourseEnter();
            mId = mClassDetailParams.getId();
            mRole = mClassDetailParams.getRole();
            isGiveFinish = mClassDetailParams.isGiveFinish();
            isGiveHistory = mClassDetailParams.isGiveHistory();
            isParent = mClassDetailParams.isParent();
            childMemberId = mClassDetailParams.getChildMemberId();
        }


        if (EmptyUtil.isEmpty(mClassId) ||
                EmptyUtil.isEmpty(mSchoolId) ||
                mId < 0 || EmptyUtil.isEmpty(mRole)) {
            return false;
        }
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        
        mToolbarRight = (ImageView) findViewById(R.id.iv_toolbar_right);
        mToolbarRight.setOnClickListener(this);


        mTopThumbnailView = (ImageView) findViewById(R.id.iv_class_avatar);
        // 班级名称
        mClassName = (TextView) findViewById(R.id.tv_class_name);
        // 开课时间
        mClassTime = (TextView) findViewById(R.id.tv_class_time);
        // 机构名称
        mSchoolName = (TextView) findViewById(R.id.tv_school);
        mSchoolEnter = (TextView) findViewById(R.id.tv_school_enter);
        mStudyNumber = (TextView) findViewById(R.id.tv_study_number);
        // 班级评分控件
        mRatingBar = (RatingBar) findViewById(R.id.grade_rating_bar);
        // 班级评分
        mGradeSource = (TextView) findViewById(R.id.tv_grade_source);
        // 公告容器
        mNoticeLayout = (FrameLayout) findViewById(R.id.notice_container);
        // 公告内容
        mNoticeText = (TextView) findViewById(R.id.txt_notice);

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setOffscreenPageLimit(tabTexts.length);

        mClassDetailBottomLayout = (LinearLayout) findViewById(R.id.class_detail_bottom_layout);
        mClassPrice = (TextView) findViewById(R.id.tv_class_money);
        mBtnPay = (Button) findViewById(R.id.btn_enter_pay);
        mJoinClassDetailBottomLayout =
                (LinearLayout) findViewById(R.id.join_class_detail_bottom_layout);
        mBtnCreateClass = (Button) findViewById(R.id.btn_new_class);
        mBtnGiveLessons = (Button) findViewById(R.id.btn_complete_give_lessons);
        
        mCommentLayout = (LinearLayout) findViewById(R.id.comment_layout);
        mCommentContent = (EditText) findViewById(R.id.et_comment_content);
        mBtnSend = (TextView) findViewById(R.id.btn_send);
        mCommentLayout.setVisibility(View.GONE);

        mBtnSend.setOnClickListener(v -> {
            // 提交评论
            commitComment(mCommentData);
        });

        // @func   :点击文本框,显示评论对话框
        mCommentContent.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                triggerCommentDialog(mCommentData);
                return true;
            }
            return false;
        });

        if (isSchoolEnter || true) {
            mSchoolEnter.setVisibility(View.GONE);
            mSchoolName.setEnabled(false);
        }

        mSchoolName.setOnClickListener(v -> {

            subscribeSchool();
        });

        // 点击学校名称,进入学校主页
        mSchoolEnter.setOnClickListener(v -> {
            subscribeSchool();
        });
    }

    /**
     * 检查关注信息
     */
    private void subscribeSchool() {
        // 点击关注
        // 进入机构主页
        if (!UserHelper.isLogin()) {
            LoginHelper.enterLogin(this);
            return;
        }

        if (EmptyUtil.isEmpty(mSchoolEntity)) return;

        if (mSchoolEntity.hasJoinedSchool() || mSchoolEntity.hasSubscribed()) {
            // 已关注
            sendSchoolSpaceRefreshBroadcast();
        } else {
            // 如果没有关注 +关注
            SchoolHelper.requestSubscribeSchool(mSchoolId, new DataSource.Callback<Object>() {
                @Override
                public void onDataNotAvailable(int strRes) {
                    UIUtil.showToastSafe(strRes);
                }

                @Override
                public void onDataLoaded(Object object) {
                    // 关注成功,发送广播,刷新UI
                    sendSchoolSpaceRefreshBroadcast();
                }
            });
        }
    }

    /**
     * 打开评论对话框
     *
     * @param data 评论数据
     */
    private void triggerCommentDialog(CommentDialog.CommentData data) {
        if (EmptyUtil.isEmpty(mCommentEntity)) {
            // 数据未准备好
            return;
        }

        if (!UserHelper.isLogin()) {
            // 验证是否登录,没有登录,请求登录
            LoginHelper.enterLogin(this);
            return;
        }

        int currentScort = -1;
        if (mCommentEntity.getStarLevel() > 0) {//已经评过分了
            currentScort = mCommentEntity.getStarLevel();
        }
        int commentType = CommentDialog.TYPE_COMMENT_LOW_PERMISSION;
        if (this instanceof ClassDetailActivity || OnlineClassRole.ROLE_PARENT.equals(mRole)) {
            // 如果是家长身份,也是低优先级
            commentType = CommentDialog.TYPE_COMMENT_LOW_PERMISSION;
        } else if (this instanceof JoinClassDetailActivity) {
            commentType = CommentDialog.TYPE_COMMENT_HIGH_PERMISSION;
        }
        mCommentDialog = new CommentDialog(this, currentScort, commentType, OnlineClassRole.ROLE_PARENT.equals(mRole), data, new CommentDialog.CommitCallBack() {
            @Override
            public void dismiss(CommentDialog.CommentData module) {
                // 课程评价片段显示
                // 记录当前文本
                BaseClassDetailActivity.this.mCommentData = module;
                mCommentContent.setText(module.getContent());
                if (BaseClassDetailActivity.this instanceof CourseDetailsNavigator) {
                    // 回调接口,显示课程评价,隐藏按钮
                    CourseDetailsNavigator navigator = (CourseDetailsNavigator) BaseClassDetailActivity.this;
                    navigator.setContent(module);
                }
            }

            @Override
            public void triggerSend(CommentDialog.CommentData module) {
                BaseClassDetailActivity.this.mCommentData = module;
                if (mCommentDialog.isShowing()) {
                    mCommentDialog.dismiss();
                    commitComment(module);
                }
            }
        });

        if (mCommentDialog != null && !mCommentDialog.isShowing()) {
            Window window = mCommentDialog.getWindow();
            mCommentDialog.show();
            window.setGravity(Gravity.BOTTOM);
        }
    }

    /**
     * 提交评论
     *
     * @param data 评论内容和评分
     */
    public void commitComment(CommentDialog.CommentData data) {
        if (null == data || TextUtils.isEmpty(data.getContent())) {
            UIUtil.showToastSafe(R.string.enter_evaluation_content_please);
            return;
        }

        if (!EmptyUtil.isEmpty(mCurrentEntity.getData())) {
            ClassDetailEntity.DataBean dataBean = mCurrentEntity.getData().get(0);
            mPresenter.requestCommitComment(0, dataBean.getId(), null,
                    data.getContent(), data.getScort());
        }
    }

    @Override
    public void commitCommentResult(boolean isSucceed) {
        // 清除评论区域的内容
        mCommentContent.getText().clear();
        mCommentData = null;
        // 重新加载评论
        MessageEvent messageEvent = new MessageEvent(EventConstant.TRIGGER_CLASS_DETAIL_COMMENTS_UPDATE);
        EventBus.getDefault().post(messageEvent);
        // 更新评论数
        refreshData();
        if (isSucceed) {
            // 隐藏软件盘
            KeyboardUtil.hideSoftInput(BaseClassDetailActivity.this);
        }
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter.requestSchoolInfo(mSchoolId);
        mPresenter.requestClassDetail(mId, false);
        // 因为有地方用到了JoinClassEntity实体
        // 班级详情有调用,可新版本只处理了部分入口有该实体
        // 所有发生请求
        String memberId = UserHelper.getUserId();
        // 发送获取班级详情细信息的请求
        OnlineCourseHelper.loadOnlineClassInfo(memberId, mClassId, new DataSource.Callback<JoinClassEntity>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                showError(strRes);
            }

            @Override
            public void onDataLoaded(JoinClassEntity joinClassEntity) {
                // 进行验证
                mJoinClassEntity = joinClassEntity;
            }
        });
    }

    @Override
    public void updateSchoolInfoView(@NonNull SchoolInfoEntity entity) {
        mSchoolEntity = entity;
        // 获取到机构详情,显示已关注,+关注信息
        mSchoolEnter.setText(getString(R.string.label_enter_school));
        mSchoolEnter.setTextColor(UIUtil.getColor(R.color.colorAccent));
        mSchoolEnter.setBackgroundResource(R.drawable.bg_rectangle_accent_radius_10);
    }

    @Override
    public void updateClassDetailView(boolean refreshHeader, @NonNull ClassDetailEntity entity) {
        mCurrentEntity = entity;
        // 加载到信息实体
        List<ClassDetailEntity.DataBean> listData = entity.getData();
        if (!EmptyUtil.isEmpty(listData)) {
            ClassDetailEntity.DataBean dataBean = listData.get(0);

            // 获取到是否已经结束授课 我的订单进入外面没有传该信息
            this.isGiveFinish = dataBean.isGiveFinish();
            this.isGiveHistory = dataBean.isGiveHistory();

            boolean isHeadMaster = UserHelper.getUserId().equals(dataBean.getCreateId());
            if((!isParent && mRole.equals(OnlineClassRole.ROLE_TEACHER)) || !isGiveHistory || (!isGiveHistory && isHeadMaster)){
                // 前面判断是否显示班级详情
                // 后面判断是否显示分享
                // 最后判断是否显示设为历史课
                // 两个都满足了,显示Pop
                mToolbarRight.setVisibility(View.VISIBLE);
            }else{
                mToolbarRight.setVisibility(View.GONE);
            }

            ImageUtil.fillDefaultView(mTopThumbnailView, dataBean.getThumbnailUrl());
            mClassName.setText(EmptyUtil.isEmpty(dataBean.getName()) ? "" : dataBean.getName());
            mClassTime.setText(getString(R.string.label_online_class_detail_time) + getCurrentOnlineTime(dataBean));
            mSchoolName.setText(EmptyUtil.isEmpty(dataBean.getOrganName()) ? "" : dataBean.getOrganName());
            // 显示参加人数
            mStudyNumber.setText(String.format(UIUtil.getString(R.string.label_study_number),dataBean.getJoinCount()));
            // 显示评分 根据总评分/评分人数 = 平均评分
            float score = dataBean.getTotalScore() == 0 ? 0 :
                    1.0f * dataBean.getTotalScore() / dataBean.getScoreNum();
            mRatingBar.setRating(score);
            // 显示评论人数
            mGradeSource.setText("(" + dataBean.getScoreNum() + ")");
            if (dataBean.getPrice() == 0) {
                // 免费班级
                mClassPrice.setText(getString(R.string.label_class_gratis));
                // 改成我要学习
                mBtnPay.setText(R.string.label_to_study);
            } else {
                // 收费班级
                mClassPrice.setText(String.format("%s%d", Common.Constance.MOOC_MONEY_MARK, dataBean.getPrice()));
                mBtnPay.setText(R.string.buy_immediately);
            }
        }
    }

    /**
     * 发送一个去空中学校并且刷新的广播
     */
    private void sendSchoolSpaceRefreshBroadcast() {
        //关注/取消关注成功后，向校园空间发广播
        Intent broadIntent = new Intent();
        broadIntent.setAction("action_change_lqCourse_tab");
        broadIntent.putExtra("schoolId", mSchoolId);
        broadIntent.putExtra("classId", mClassId);
        sendBroadcast(broadIntent);
    }

    /**
     * 获取当前直播显示的起止时间
     *
     * @param dataBean 数据实体
     * @return 返回处理过需要显示的直播时间信息
     */
    private String getCurrentOnlineTime(@NonNull ClassDetailEntity.DataBean dataBean) {
        String beginTime = dataBean.getStartTime();
        String endTime = dataBean.getEndTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long beginMillis = TimeUtil.string2Millis(beginTime, format);
        long endMillis = TimeUtil.string2Millis(endTime, format);

        beginTime = TimeUtil.millis2String(beginMillis, new SimpleDateFormat("yyyy-MM-dd"));
        endTime = TimeUtil.millis2String(endMillis, new SimpleDateFormat("yyyy-MM-dd"));

        if (!TextUtils.isEmpty(beginTime) && !TextUtils.isEmpty(endTime)) {
            String showTime = beginTime + " -- " + endTime;
            return showTime;
        }
        return "";
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.iv_toolbar_right){
            // 点击菜单功能
            showPop(v);
        }
    }

    /**
     *
     * 弹出菜单
     * @param view
     */
    private void showPop(View view) {
        if (mPopWindow == null) {
            View contentView = UIUtil.inflate(R.layout.pop_menu);
            contentView.setBackgroundResource(R.drawable.bg_pop_menu);
            //处理popWindow 显示内容
            handleLogic(contentView, true);
            int width = ViewGroup.LayoutParams.WRAP_CONTENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            // if(this instanceof JoinClassDetailActivity)
            width =  DisplayUtil.dip2px(UIUtil.getContext(),180);
            mPopWindow = new CustomPopWindow.PopupWindowBuilder(this)
                    //显示的布局，还可以通过设置一个View
                    .setView(contentView)
                    .size(width,height)
                    //创建PopupWindow
                    .create();
        }

        //水平偏移量
        int xOff = mPopWindow.getWidth() - view.getWidth() + DisplayUtil.dip2px(UIUtil.getContext(), 0);
        mPopWindow.showAsDropDown(view, -xOff, -DisplayUtil.dip2px(UIUtil.getContext(), 10));
    }

    /**
     * 处理弹出显示内容、点击事件等逻辑
     *
     * @param contentView
     */
    private boolean handleLogic(View contentView, boolean isShowPop) {
        final List<EntryBean> items = assembleMenuData();
        if (items.size() <= 0) {
            return false;
        }
        if (!isShowPop) {
            return true;
        }
        ListView listView = (ListView) contentView.findViewById(R.id.pop_menu_list);
        PopMenuAdapter adapter = new PopMenuAdapter(this, items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mPopWindow != null) {
                    mPopWindow.dissmiss();
                }
                EntryBean data = items.get(i);
                switch (data.id) {
                    case TYPE_MENU_SHARE:
                        // 分享
                        share();
                        break;
                    case TYPE_MENU_MENU:
                        // 班级详情
                        enterClassDetail(mJoinClassEntity);
                        break;
                    case TYPE_MENU_HISTORY:
                        // 设成历史班
                        setHistoryClass();
                        break;
                    default:
                        break;
                }
            }
        });
        return true;
    }

    /**
     * 获取到Menu数据
     * @return Menu集合
     */
    protected abstract List<EntryBean> assembleMenuData();

    /**
     * 分享班级课程
     */
    private void share() {
        if (EmptyUtil.isNotEmpty(mCurrentEntity) && EmptyUtil.isNotEmpty(mCurrentEntity.getData())) {
            ClassDetailEntity.DataBean dataBean = mCurrentEntity.getData().get(0);
            final String title = dataBean.getName();
            StringBuilder titleBuilder = new StringBuilder();
            StringBuilder descriptionBuilder = new StringBuilder();
            titleBuilder.append(UIUtil.getString(R.string.label_share_online_class_title));
            titleBuilder.append(title);

            // 显示评分 根据总评分/评分人数 = 平均评分
            float score = dataBean.getTotalScore() == 0 ? 0 :
                    1.0f * dataBean.getTotalScore() / dataBean.getScoreNum();
            String teachers = dataBean.getTeachersName();
            if(EmptyUtil.isNotEmpty(teachers) && teachers.length() > 7){
                teachers = teachers.substring(0,7) + "...";
            }
            descriptionBuilder.append(teachers + "\n");
            if(dataBean.getPrice() == 0){
                descriptionBuilder.append(UIUtil.getString(R.string.label_class_gratis) + "\n");
            }else{
                descriptionBuilder.append(Common.Constance.MOOC_MONEY_MARK + " " + dataBean.getPrice() + "\n");
            }
            for(int index = 0; index < Math.ceil(score);index++){
                descriptionBuilder.append("\u2B50");
            }
            final String description = dataBean.getOrganName();
            final String thumbnailUrl = dataBean.getThumbnailUrl();
            final String url = AppConfig.ServerUrl.OnlineSchoolClassShareUrl.replace("{id}", Integer.toString(dataBean.getId()));
            mPresenter.share(titleBuilder.toString(), descriptionBuilder.toString(), thumbnailUrl, url);
        }
    }

    /**
     * 进入班级详情
     * @param entity 班级信息
     */
    private void enterClassDetail(@NonNull JoinClassEntity entity) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        intent.setClassName(getPackageName(),"com.galaxyschool.app.wawaschool.OpenCourseHelpActivity");
        if(isGiveHistory){
            // 如果授课结束,传3
            entity.setIsHistory(3);
        }
        bundle.putSerializable(JoinClassEntity.class.getSimpleName(),entity);
        bundle.putBoolean("isEnterClassDetail",true);
        bundle.putBoolean("isOnlineSchool",true);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    protected abstract void setHistoryClass();

    @Override
    public void refreshData() {
        mPresenter.requestClassDetail(mId, true);
    }

    @Override
    public void setOnlineCommentEntity(OnlineCommentEntity entity) {
        mCommentEntity = entity;
    }

    @Override
    public boolean onSupportNavigateUp() {
        // 进入主页面
        if(mClassDetailParams != null && mClassDetailParams.isPushEnter() && mClassDetailParams.isHome()){
            Intent intent = new Intent();
            intent.setClassName(getPackageName(),"com.galaxyschool.app.wawaschool.HomeActivity");
            startActivity(intent);
        }
        return super.onSupportNavigateUp();
    }
}
