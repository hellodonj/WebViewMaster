package com.lqwawa.mooc.modle.tutorial;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.bitmapmanager.ThumbnailManager;
import com.galaxyschool.app.wawaschool.common.ImageLoader;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.MainApplication;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.ui.QRCodeDialogFragment;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.StringUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.user.UserEntity;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.intleducation.module.discovery.tool.LoginHelper;
import com.lqwawa.intleducation.module.discovery.ui.WebFragment;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.lqbaselib.net.Netroid;
import com.lqwawa.lqbaselib.pojo.MessageEvent;
import com.lqwawa.lqresviewlib.office365.WebActivity;
import com.lqwawa.mooc.modle.tutorial.comment.TutorialCommentFragment;
import com.lqwawa.mooc.modle.tutorial.list.TutorialCourseListFragment;
import com.oosic.apps.share.BaseShareUtils;
import com.oosic.apps.share.ShareInfo;
import com.umeng.socialize.media.UMImage;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mrmedici
 * @desc 帮辅模式助教个人主页页面
 */
public class TutorialHomePageActivity extends PresenterActivity<TutorialHomePageContract.Presenter>
        implements TutorialHomePageContract.View, View.OnClickListener {

    private TopBar mTopBar;
    private ImageButton mIvBack;
    private TextView mTvShare;
    private TextView mTvName;
    private TextView mTvViewerCount;
    private TextView mTvAttentionCount;
    private ImageView mIvQRCode;
    private ImageView mIvSex;
    private ImageView mIvAvatar;
    private View mCourseSubjectLayout;
    private TextView mTvCourseSubject;
    private LinearLayout mBottomLayout;
    private Button mBtnAddTutorial;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private String[] pageTitles = UIUtil.getStringArray(R.array.label_tutorial_page_tabs);

    private TutorialParams mTutorialParams;
    private String mTutorMemberId;
    private String mTutorName;

    private ThumbnailManager thumbnailManager;
    private UserEntity mUserEntity;
    private String mQRCodeImageUrl;
    private String mQRCodeImagePath;
    private String mCourseSubject;
    private String mClassId;
    private boolean mIsHideAddTutorial;

    @Override
    protected TutorialHomePageContract.Presenter initPresenter() {
        return new TutorialHomePagePresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_tutorial_home_page;
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        if (bundle.containsKey(ACTIVITY_BUNDLE_OBJECT)) {
            mTutorialParams = (TutorialParams) bundle.getSerializable(ACTIVITY_BUNDLE_OBJECT);
            if (EmptyUtil.isNotEmpty(mTutorialParams)) {
                mTutorMemberId = mTutorialParams.getTutorMemberId();
                mTutorName = mTutorialParams.getTutorName();
                mClassId = mTutorialParams.getClassId();
            }
        }

        if (EmptyUtil.isEmpty(mTutorMemberId)) {
            return false;
        }

        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        mTopBar.setTitle(R.string.title_personal_page);
        mTopBar.setBack(true);
        mTopBar.setVisibility(View.GONE);
        mIvBack = (ImageButton) findViewById(R.id.iv_back);
        mTvShare = (TextView) findViewById(R.id.tv_share);
        mIvBack.setOnClickListener(this);
        mTvShare.setOnClickListener(this);
        mTvName = (TextView) findViewById(R.id.tv_name);
        mTvViewerCount = (TextView) findViewById(R.id.tv_viewer_count);
        mTvAttentionCount = (TextView) findViewById(R.id.tv_attention_count);
        mIvQRCode = (ImageView) findViewById(R.id.iv_QR_code);
        mIvSex = (ImageView) findViewById(R.id.iv_sex);
        mIvQRCode.setOnClickListener(this);
        mIvAvatar = (ImageView) findViewById(R.id.iv_avatar);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        mCourseSubjectLayout = findViewById(R.id.course_subject_layout);
        mCourseSubjectLayout.setOnClickListener(view -> {
            if (!TextUtils.isEmpty(mCourseSubject)) {
                String title = getString(R.string.course_subject_colon);
                WebActivity.start(true, this, mCourseSubject, title.substring(0,
                        title.length() - 1));
            }
        });
        mTvCourseSubject = (TextView) findViewById(R.id.tv_course_subject);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab != null) {
                    int position = tab.getPosition();
                    if (position == 2) {
                        mBottomLayout.setVisibility(View.GONE);
                    } else {
                        mBottomLayout.setVisibility(mIsHideAddTutorial ? View.GONE : View.VISIBLE);
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mBottomLayout = (LinearLayout) findViewById(R.id.bottom_layout);
        mBtnAddTutorial = (Button) findViewById(R.id.btn_add_tutorial);
        mBtnAddTutorial.setOnClickListener(this);
        mBtnAddTutorial.setText(!TextUtils.isEmpty(mClassId) ? R.string.add_class_tutor :
                R.string.label_add_tutorial);
    }

    @Override
    protected void initData() {
        super.initData();
        thumbnailManager = MyApplication.getThumbnailManager(this);


        // 请求个人信息
        mPresenter.requestUserInfoWithUserId(mTutorMemberId);
        mPresenter.requestTutorSubjectList(mTutorMemberId);

        // 如果当前帮辅老师与自己是同一个人，隐藏按钮
        boolean tutorialMode = MainApplication.isTutorialMode();
        boolean isHide =
                tutorialMode  || TextUtils.equals(mTutorMemberId,UserHelper.getUserId());
        if (!TextUtils.isEmpty(mClassId)) {
            isHide = false;
        }
        mIsHideAddTutorial = isHide;
        if (isHide) {
            mBottomLayout.setVisibility(View.GONE);
        } else {
            // 不相等，不是查看自己个人主页
            // 查询是否已经加入该帮辅
            mPresenter.requestQueryAddedTutorState(UserHelper.getUserId(), mTutorMemberId, mClassId);
        }
    }

    private void initFragments() {
        List<Fragment> fragments = new ArrayList<>();
        WebFragment webFragment = null;
        String introduces = mUserEntity.getPIntroduces();
        if (EmptyUtil.isEmpty(introduces)) {
            if (TextUtils.equals(mTutorMemberId, UserHelper.getUserId())) {
                introduces = getString(R.string.label_null_tutorial_introduces);
            }
        }
        if (!TextUtils.isEmpty(introduces) && introduces.contains("<p>")) {
            webFragment = WebFragment.newInstance("", introduces, "");
        } else {
            webFragment = WebFragment.newInstance("", "", introduces);

        }
        fragments.add(webFragment);
        fragments.add(TutorialCourseListFragment.newInstance(mTutorialParams));
        fragments.add(TutorialCommentFragment.newInstance(mTutorialParams));

        mTabLayout.setupWithViewPager(mViewPager);
        TutorialPagerAdapter adapter = new TutorialPagerAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(adapter);
    }

    @Override
    public void updateUserInfoView(@NonNull UserEntity entity) {
        this.mUserEntity = entity;
        
        initFragments();

        //设置用户名
        String realName = entity.getRealName();
        String userName = entity.getNickName();

        if (TextUtils.isEmpty(realName)) {
            mTvName.setText("" + getString(R.string.container_string, userName));
        } else {
            mTvName.setText(realName + getString(R.string.container_string, userName));
        }

        // 多少人浏览
        StringUtil.fillSafeTextView(mTvViewerCount, getString(R.string.label_viewer_count, entity.getBrowseNum()));
        // 多少人关注
        StringUtil.fillSafeTextView(mTvAttentionCount, getString(R.string.label_attention_count, entity.getAttentionNumber()));

        // 加载头像
        thumbnailManager.displayUserIcon(AppSettings.getFileUrl(
                entity.getHeaderPic()), mIvAvatar);

        // 显示性别
        String sex = entity.getSex();
        if (!TextUtils.isEmpty(sex)) {
            if (sex.equals("男")) {
                mIvSex.setVisibility(View.VISIBLE);
                mIvSex.setImageResource(R.drawable.icon_male);
            } else if (sex.equals("女")) {
                mIvSex.setVisibility(View.VISIBLE);
                mIvSex.setImageResource(R.drawable.icon_female);
            }
        }

        updateSubscribeBar();
    }

    @Override
    public void updateTutorSubjectView(List<String> subjectList) {
        StringBuilder stringBuilder = new StringBuilder();
        if (subjectList != null && !subjectList.isEmpty()) {
            for (int i = 0, size = subjectList.size(); i < size; i++) {
                stringBuilder.append(subjectList.get(i));
                stringBuilder.append(i != size - 1 ? "，" : "");
            }
        }
        mCourseSubject = stringBuilder.toString();
        mTvCourseSubject.setText(mCourseSubject);
    }

    @Override
    public void updateQueryAddedTutorStateView(boolean added) {
        // 广播出去,判断是否显示评论框
        // 需求更改,只有批阅过后的入口显示评论框
        // EventBus.getDefault().post(new EventWrapper(added,EventConstant.TRIGGER_ATTENTION_TUTORIAL_UPDATE));
        mIsHideAddTutorial = added;
        if (added) {
            mBottomLayout.setVisibility(View.GONE);
        } else {
            mBottomLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void updateAddTutorView(boolean result) {
        mIsHideAddTutorial = result;
        if (result) {
            // 加帮辅成功
            mBottomLayout.setVisibility(View.GONE);
            // 广播出去,判断是否显示评论框
            // EventBus.getDefault().post(new EventWrapper(result,EventConstant.TRIGGER_ATTENTION_TUTORIAL_UPDATE));

            // 添加帮辅（班级帮辅）成功后，刷新班级帮辅列表
            MessageEvent messageEvent = new MessageEvent(EventConstant.TRIGGER_ADD_TUTOR_UPDATE);
            EventBus.getDefault().post(messageEvent);

            UIUtil.showToastSafe(R.string.label_added_tutorial_succeed);
        } else {
            UIUtil.showToastSafe(R.string.label_added_tutorial_failed);
        }
    }

    private void updateSubscribeBar() {
        //更新二维码
        mQRCodeImageUrl = AppSettings.getFileUrl(mUserEntity.getQRCode());
        if (mIvAvatar != null) {
            loadQrCodeImage();
        }
    }

    private void loadQrCodeImage() {

        if (TextUtils.isEmpty(mQRCodeImageUrl)) {
            return;
        }
        mQRCodeImagePath = ImageLoader.getCacheImagePath(mQRCodeImageUrl);
        File file = new File(mQRCodeImagePath);
        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(mQRCodeImagePath);
            if (bitmap != null) {
                // mIvQRCode.setImageBitmap(bitmap);
                // return;
            }
            file.delete();
        }

        Netroid.downloadFile(this, mQRCodeImageUrl, mQRCodeImagePath,
                new Listener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (this == null) return;
                        // mIvQRCode.setImageBitmap(BitmapFactory.decodeFile(mQRCodeImagePath));
                    }

                    @Override
                    public void onError(NetroidError error) {
                        if (this == null) return;
                        super.onError(error);
                        TipsHelper.showToast(TutorialHomePageActivity.this,
                                R.string.picture_download_failed);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.iv_back) {
            // 返回
            finish();
        } else if (viewId == R.id.tv_share) {
            // 分享
            if (EmptyUtil.isEmpty(mUserEntity)) return;
            if (!UserHelper.isLogin()) {
                LoginHelper.enterLogin(this);
                return;
            }

            sharePersonal();
        } else if (viewId == R.id.iv_QR_code) {
            if (EmptyUtil.isEmpty(mUserEntity)) return;
            String dialogDesc = mTvName.getText().toString();
            QRCodeDialogFragment.show(getSupportFragmentManager(),
                    getString(R.string.label_personal_qrcode),
                    dialogDesc, mQRCodeImageUrl, () -> saveQrCodeImage(mQRCodeImageUrl));
        } else if (viewId == R.id.btn_add_tutorial) {
            // 先判断是否登录
            if (!UserHelper.isLogin()) {
                LoginHelper.enterLogin(this);
                return;
            }

            // 加帮辅(班级帮辅)
            mPresenter.requestAddTutor(UserHelper.getUserId(), mTutorMemberId,
                    mTutorName, mClassId);
        }
    }

    /**
     * 分享操作
     */
    private void sharePersonal() {
        ShareInfo shareInfo = new ShareInfo();
        shareInfo.setTitle(mUserEntity.getNickName());
        if (!TextUtils.isEmpty(mUserEntity.getRealName())) {
            shareInfo.setTitle(mUserEntity.getRealName());
        }

        shareInfo.setContent(" ");
        String shareUrl = AppConfig.ServerUrl.TutorialShare.replace("{memberId}", mTutorMemberId);
        shareInfo.setTargetUrl(shareUrl);

        UMImage umImage = null;
        if (!TextUtils.isEmpty(mUserEntity.getHeaderPic())) {
            umImage = new UMImage(this, AppSettings.getFileUrl(mUserEntity.getHeaderPic()));
        } else {
            umImage = new UMImage(this, R.drawable.ic_launcher);
        }

        shareInfo.setuMediaObject(umImage);
        BaseShareUtils utils = new BaseShareUtils(this);
        utils.share(this.getWindow().getDecorView(), shareInfo);
    }

    private void saveQrCodeImage(String QRCodeImageUrl) {

        if (TextUtils.isEmpty(QRCodeImageUrl)) {
            return;
        }
        String filePath = ImageLoader.saveImage(this, QRCodeImageUrl);
        if (filePath != null) {
            TipsHelper.showToast(this, getString(R.string.image_saved_to, filePath));
        } else {
            TipsHelper.showToast(this, getString(R.string.save_failed));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LoginHelper.RS_LOGIN) {
            if (UserHelper.isLogin()) {
                // 重新进入该页面
                TutorialHomePageActivity.show(this, mTutorialParams);
                finish();
            }
        }
    }

    /**
     * 申请成为帮辅，注册信息的入口
     *
     * @param context 上下文对象
     */
    public static void show(@NonNull final Context context, @NonNull TutorialParams params) {
        Intent intent = new Intent(context, TutorialHomePageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ACTIVITY_BUNDLE_OBJECT, params);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }


    private class TutorialPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> mFragments;

        public TutorialPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.mFragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return pageTitles[position];
        }
    }
}
