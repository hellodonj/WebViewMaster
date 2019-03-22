package com.lqwawa.mooc.modle.tutorial;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.bitmapmanager.ThumbnailManager;
import com.galaxyschool.app.wawaschool.common.ImageLoader;
import com.galaxyschool.app.wawaschool.common.ShareUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.StringUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.user.UserEntity;
import com.lqwawa.intleducation.module.discovery.tool.LoginHelper;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.lqbaselib.net.Netroid;
import com.lqwawa.lqresviewlib.office365.WebActivity;
import com.lqwawa.mooc.modle.tutorial.comment.TutorialCommentFragment;
import com.lqwawa.mooc.modle.tutorial.list.TutorialCourseListContract;
import com.lqwawa.mooc.modle.tutorial.list.TutorialCourseListFragment;
import com.oosic.apps.share.ShareInfo;
import com.oosic.apps.share.SharedResource;
import com.umeng.socialize.media.UMImage;

import java.io.File;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;

/**
 * @author mrmedici
 * @desc 帮辅模式助教个人主页页面
 */
public class TutorialHomePageActivity extends PresenterActivity<TutorialHomePageContract.Presenter>
    implements TutorialHomePageContract.View,View.OnClickListener{

    private TopBar mTopBar;
    private ImageButton mIvBack;
    private TextView mTvShare;
    private TextView mTvName;
    private TextView mTvViewerCount;
    private TextView mTvAttentionCount;
    private ImageView mIvQRCode;
    private ImageView mIvSex;
    private ImageView mIvAvatar;

    private View mIntroduceLayout;
    private TextView mTvContent;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private String[] pageTitles = UIUtil.getStringArray(R.array.label_tutorial_page_tabs);

    private TutorialParams mTutorialParams;
    private String mTutorMemberId;

    private ThumbnailManager thumbnailManager;
    private UserEntity mUserEntity;
    private String mQRCodeImageUrl;
    private String mQRCodeImagePath;


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
        if(bundle.containsKey(ACTIVITY_BUNDLE_OBJECT)){
            mTutorialParams = (TutorialParams) bundle.getSerializable(ACTIVITY_BUNDLE_OBJECT);
            if(EmptyUtil.isNotEmpty(mTutorialParams)){
                mTutorMemberId = mTutorialParams.getTutorMemberId();
            }
        }

        if(EmptyUtil.isEmpty(mTutorMemberId)){
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
        mTopBar.setVisibility(ViewPager.GONE);
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
        mIntroduceLayout = findViewById(R.id.introduce_layout);
        mIntroduceLayout.setOnClickListener(this);
        mTvContent = (TextView) findViewById(R.id.tv_content);
        mTvContent.setText(R.string.label_personal_introduce);
        mTvContent.setTextColor(UIUtil.getColor(R.color.textPrimary));
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
    }

    @Override
    protected void initData() {
        super.initData();
        thumbnailManager = MyApplication.getThumbnailManager(this);

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(TutorialCourseListFragment.newInstance(mTutorialParams));
        fragments.add(TutorialCommentFragment.newInstance(mTutorialParams));

        mTabLayout.setupWithViewPager(mViewPager);
        TutorialPagerAdapter adapter = new TutorialPagerAdapter(getSupportFragmentManager(),fragments);
        mViewPager.setAdapter(adapter);

        // 请求个人信息
        mPresenter.requestUserInfoWithUserId(mTutorMemberId);
    }

    @Override
    public void updateUserInfoView(@NonNull UserEntity entity) {
        this.mUserEntity = entity;
        //设置用户名
        String realName = entity.getRealName();
        String userName = entity.getNickName();

        if (TextUtils.isEmpty(realName)) {
            mTvName.setText("" + getString(R.string.container_string, userName));
        } else {
            mTvName.setText(realName + getString(R.string.container_string, userName));
        }

        // 多少人浏览
        StringUtil.fillSafeTextView(mTvViewerCount,getString(R.string.label_viewer_count,entity.getBrowseNum()));
        // 多少人关注
        StringUtil.fillSafeTextView(mTvAttentionCount,getString(R.string.label_attention_count,entity.getAttentionNumber()));

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
                        if(this == null) return;
                        // mIvQRCode.setImageBitmap(BitmapFactory.decodeFile(mQRCodeImagePath));
                    }

                    @Override
                    public void onError(NetroidError error) {
                        if(this == null) return;
                        super.onError(error);
                        TipsHelper.showToast(TutorialHomePageActivity.this,
                                R.string.picture_download_failed);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.iv_back){
            // 返回
            finish();
        }else if(viewId == R.id.tv_share){
            // 分享
            if(EmptyUtil.isEmpty(mUserEntity)) return;
            sharePersonal();
        }else if(viewId == R.id.iv_QR_code){
            if(EmptyUtil.isEmpty(mUserEntity)) return;
            saveQrCodeImage(mQRCodeImageUrl);
        }else if(viewId == R.id.introduce_layout){
            // 个人介绍
            // UIUtil.showToastSafe("个人介绍");
            WebActivity.start(this,mUserEntity.getPIntroduces(),getString(R.string.label_personal_introduce),true);
        }
    }

    /**
     * 分享操作
     */
    private void sharePersonal(){
        ShareInfo shareInfo = new ShareInfo();
        shareInfo.setTitle(mUserEntity.getNickName());
        if (!TextUtils.isEmpty(mUserEntity.getRealName())) {
            shareInfo.setTitle(mUserEntity.getRealName());
        }
        shareInfo.setContent(" ");
        String serverUrl = AppConfig.ServerUrl.TutorialShare.replace("{memberId}",mTutorMemberId);
        String url = serverUrl + String.format(
                ServerUrl.SHARE_PERSONAL_SPACE_PARAMS, mUserEntity.getMemberId());
        shareInfo.setTargetUrl(url);
        UMImage umImage = null;
        if (!TextUtils.isEmpty(mUserEntity.getHeaderPic())) {
            umImage = new UMImage(this, AppSettings.getFileUrl(mUserEntity.getHeaderPic()));
        } else {
            umImage = new UMImage(this, R.drawable.ic_launcher);
        }
        shareInfo.setuMediaObject(umImage);
        SharedResource resource = new SharedResource();
        resource.setId(mUserEntity.getMemberId());
        resource.setTitle(mUserEntity.getNickName());
        if (!TextUtils.isEmpty(mUserEntity.getRealName())) {
            resource.setTitle(mUserEntity.getRealName());
        }
        resource.setDescription("");
        resource.setShareUrl(serverUrl);
        if (!TextUtils.isEmpty(mUserEntity.getHeaderPic())) {
            resource.setThumbnailUrl(AppSettings.getFileUrl(mUserEntity.getHeaderPic()));
        }
        resource.setType(SharedResource.RESOURCE_TYPE_HTML);
        resource.setFieldPatches(SharedResource.FIELD_PATCHES_PERSON_SHARE_URL);
        shareInfo.setSharedResource(resource);
        ShareUtils shareUtils = new ShareUtils(this);
        shareUtils.share(getWindow().getDecorView().getRootView(), shareInfo);
    }

    private void saveQrCodeImage(String QRCodeImageUrl) {

        if (TextUtils.isEmpty(QRCodeImageUrl)) {
            return;
        }
        String filePath = ImageLoader.saveImage(this, QRCodeImageUrl);
        if (filePath != null) {
            TipsHelper.showToast(this,getString(R.string.image_saved_to, filePath));
        } else {
            TipsHelper.showToast(this, getString(R.string.save_failed));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LoginHelper.RS_LOGIN) {
            if(UserHelper.isLogin()) {
                // 重新进入该页面
                TutorialHomePageActivity.show(this,mTutorialParams);
                finish();
            }
        }
    }

    /**
     * 申请成为帮辅，注册信息的入口
     * @param context 上下文对象
     */
    public static void show(@NonNull final Context context,@NonNull TutorialParams params){
        Intent intent = new Intent(context, TutorialHomePageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ACTIVITY_BUNDLE_OBJECT,params);
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
