package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.CaptureActivity;
import com.galaxyschool.app.wawaschool.ChoiceBooksActivity;
import com.galaxyschool.app.wawaschool.ContactsQrCodeDetailsActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.SchoolClassListActivity;
import com.galaxyschool.app.wawaschool.SubscribeSearchActivity;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.ShareUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.lqwawa.intleducation.module.onclass.OnlineClassListActivity;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.SubscribeClassInfo;
import com.galaxyschool.app.wawaschool.views.PopupMenu;
import com.lqwawa.intleducation.module.organcourse.OrganCourseClassifyActivity;
import com.lqwawa.intleducation.module.organcourse.OrganLibraryType;
import com.oosic.apps.share.ShareInfo;
import com.oosic.apps.share.SharedResource;
import com.umeng.socialize.media.UMImage;

import java.util.ArrayList;
import java.util.List;

public class SchoolSpaceBaseFragment extends ContactsListFragment {

    public interface Constants {
        String EXTRA_SCHOOL_ID = "schoolId";
        String EXTRA_SCHOOL_NAME = "schoolName";
    }

    public static final int MENU_ID_ADD_AUTHORITY = 0;
    public static final int MENU_ID_SCAN = 1;
    public static final int MENU_ID_SHARE = 2;

    public interface ITabEntityTypeInfo {

        // 学校介绍
        int TAB_ENTITY_TYPE_SCHOOL_INTRODUCTION = 0;
        // 校园动态
        int TAB_ENTITY_TYPE_SCHOOL_CAMPUS_DYNAMICS = 1;
        // 学校班级
        int TAB_ENTITY_TYPE_SCHOOL_CLASS = 2;
        // 校本资源库
        int TAB_ENTITY_TYPE_SCHOOL_BASED_CURRICULUM = 3;
        // 精品资源库
        int TAB_ENTITY_TYPE_CHOICE_BOOKS = 4;
        // 校长助手(校园巡查)
        int TAB_ENTITY_TYPE_CAMPUS_PATROL = 5;
        // 校园电视台
        int TAB_ENTITY_TYPE_CAMPUS_DIRECT = 6;
        // 习课程馆
        int TAB_ENTITY_TYPE_LQCOURSE_SHOP = 7;
        // 视频馆
        int TAB_ENTITY_TYPE_VIDEO_LIBRARY = 8;
        // 图书馆
        int TAB_ENTITY_TYPE_LIBRARY = 9;
        // 练测馆
        int TAB_ENTITY_TYPE_PRACTICE_LIBRARY = 10;
        // 全脑馆
        int TAB_ENTITY_TYPE_BRAIN_LIBRARY = 11;
        //三习教案馆
        int TAB_ENTITY_TYPE_TEACHING_PLAN = 12;

        // 学习任务
        int TAB_ENTITY_TYPE_LEARNING_TASKS = 100;
        // 创E学堂
        int TAB_ENTITY_TYPE_GEN_E_SCHOOL = 101;
        // 班级信息
        int TAB_ENTITY_TYPE_CLASS_INFORMATION = 102;
        // 通知
        int TAB_ENTITY_TYPE_NOTICE = 103;
        // 秀秀
        int TAB_ENTITY_TYPE_WAWA_SHOW = 104;
        // 关联学程
        int TAB_ENTITY_TYPE_RELEVANCE_COURSE = 105;
        // 空中课堂
        int TAB_ENTITY_TYPE_AIR_CLASSROOM = 106;
        // 表演课堂
        int TAB_ENTITY_TYPE_ACT_CLASSROOM = 107;
        // 班级学程
        int TAB_ENTITY_TYPE_CLASS_LESSON = 108;
        // 点评统计
        int TAB_ENTITY_TYPE_COMMENT_STATISTIC = 109;
        // 班级帮辅
        int TAB_ENTITY_TYPE_CLASS_TUTOR = 110;
    }

    protected String schoolId;
    protected String schoolName;
    protected SchoolInfo schoolInfo;
    protected SubscribeClassInfo classInfo;

    protected ImageView schoolIconView;
    protected TextView schoolNameView;
    protected ImageView schoolQrCodeView;
    protected TextView schoolFollowersCountView;
    protected TextView schoolViewCountView;
    protected View countLayout;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findViews();
        initViews();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void findViews() {
        schoolIconView = (ImageView) findViewById(R.id.user_icon);
        schoolNameView = (TextView) findViewById(R.id.user_name);
        schoolFollowersCountView = (TextView) findViewById(R.id.user_followers_count);
        schoolViewCountView = (TextView) findViewById(R.id.user_view_count);
        countLayout = findViewById(R.id.layout_count);
        schoolQrCodeView = (ImageView) findViewById(R.id.user_qrcode);
    }

    private void initViews() {
        TextView textView = (TextView) findViewById(R.id.share_btn);
        if (textView != null) {
            textView.setOnClickListener(this);
            textView.setVisibility(View.VISIBLE);
        }

        schoolIconView.setOnClickListener(this);
        schoolQrCodeView.setOnClickListener(this);
    }

    protected void loadSchoolInfo() {

    }

    protected void updateSchoolInfoViews() {
        if (schoolInfo != null) {
            schoolId = schoolInfo.getSchoolId();
            schoolName = schoolInfo.getSchoolName();
            getThumbnailManager().displayUserIconWithDefault(
                    AppSettings.getFileUrl(schoolInfo.getSchoolLogo()),
                    schoolIconView, R.drawable.default_school_icon);
            schoolNameView.setText(schoolInfo.getSchoolName());
            schoolFollowersCountView.setText(getString(R.string.str_follow_people, Utils.transferNumberData(schoolInfo.getAttentionNumber())));
            schoolViewCountView.setText(getString(R.string.Str_view_people, Utils.transferNumberData(schoolInfo.getBrowseNum())));
        } else {
            schoolNameView.setText(null);
            schoolIconView.setImageResource(R.drawable.default_school_icon);
            schoolFollowersCountView.setText(getString(R.string.default_attention_count));
            schoolViewCountView.setText(getString(R.string.default_look_through_count));
        }

    }

    protected void enterSchoolQrCode() {
        if (schoolInfo == null) {
            return;
        }

        Bundle args = new Bundle();
        args.putString(ContactsQrCodeDetailsActivity.EXTRA_TITLE,
                getActivity().getString(R.string.school_qrcode));
        args.putInt(ContactsQrCodeDetailsActivity.EXTRA_TARGET_TYPE,
                ContactsQrCodeDetailsActivity.TARGET_TYPE_SCHOOL);
        args.putString(ContactsQrCodeDetailsActivity.EXTRA_TARGET_ID,
                schoolInfo.getSchoolId());
        args.putString(ContactsQrCodeDetailsActivity.EXTRA_TARGET_ICON,
                schoolInfo.getSchoolLogo());
        args.putString(ContactsQrCodeDetailsActivity.EXTRA_TARGET_NAME,
                schoolInfo.getSchoolName());
        args.putString(ContactsQrCodeDetailsActivity.EXTRA_TARGET_QR_CODE,
                schoolInfo.getQRCode());
        Intent intent = new Intent(getActivity(), ContactsQrCodeDetailsActivity.class);
        intent.putExtras(args);
        startActivity(intent);
    }

    protected void enterSchoolClasses() {
        if (schoolInfo == null) {
            return;
        }

        if (schoolInfo.isOnlineSchool()) {
            OnlineClassListActivity.show(getContext(), schoolId, schoolName);
        } else {
            Bundle args = new Bundle();
            args.putString(SchoolClassListActivity.EXTRA_SCHOOL_ID, schoolInfo.getSchoolId());
            args.putString(SchoolClassListActivity.EXTRA_SCHOOL_NAME, schoolInfo.getSchoolName());
            args.putBoolean(SchoolClassListActivity.EXTRA_IS_TEACHER, schoolInfo.isTeacher());
            //是否要显示隐藏的菜单
            args.putBoolean(SchoolClassListActivity.EXTRA_IS_NEED_SHOW_MENU, true);
            args.putSerializable(ActivityUtils.EXTRA_SCHOOL_INFO, schoolInfo);
            Intent intent = new Intent(getActivity(), SchoolClassListActivity.class);
            intent.putExtras(args);
            startActivity(intent);
        }
    }

    protected void enterChoiceBooks() {
        Intent intent = new Intent(getActivity(), ChoiceBooksActivity.class);
        intent.putExtra(SchoolInfo.class.getSimpleName(), schoolInfo);
        startActivity(intent);
    }

    protected void shareSchoolSpace() {
        if (schoolInfo == null) {
            return;
        }
        ShareInfo shareInfo = new ShareInfo();
        shareInfo.setTitle(schoolInfo.getSchoolName());
        shareInfo.setContent(" ");
        String serverUrl = ServerUrl.SHARE_SCHOOL_SPACE_URL;
        String url = serverUrl + String.format(
                ServerUrl.SHARE_SCHOOL_SPACE_PARAMS, schoolInfo.getSchoolId());
        shareInfo.setTargetUrl(url);
        UMImage umImage = null;
        if (!TextUtils.isEmpty(schoolInfo.getSchoolLogo())) {
            umImage = new UMImage(getActivity(), AppSettings.getFileUrl(schoolInfo.getSchoolLogo()));
        } else {
            umImage = new UMImage(getActivity(), R.drawable.ic_launcher);
        }
        shareInfo.setuMediaObject(umImage);
        SharedResource resource = new SharedResource();
        resource.setId(schoolInfo.getSchoolId());
        resource.setTitle(schoolInfo.getSchoolName());
        resource.setDescription("");
        resource.setShareUrl(serverUrl);
        if (!TextUtils.isEmpty(schoolInfo.getSchoolLogo())) {
            resource.setThumbnailUrl(AppSettings.getFileUrl(schoolInfo.getSchoolLogo()));
        }
        resource.setType(SharedResource.RESOURCE_TYPE_HTML);
        resource.setFieldPatches(SharedResource.FIELD_PATCHES_SCHOOL_SHARE_URL);
        shareInfo.setSharedResource(resource);
        ShareUtils shareUtils = new ShareUtils(getActivity());
        shareUtils.share(getView(), shareInfo);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.user_qrcode) {
            enterSchoolQrCode();
        } else if (v.getId() == R.id.share_btn) {
            shareSchoolSpace();
        } else {
            super.onClick(v);
        }
    }

    protected void showMoreMenu(View view) {

        List<PopupMenu.PopupMenuData> items = new ArrayList();
        PopupMenu.PopupMenuData data = null;

        //添加机构
        data = new PopupMenu.PopupMenuData(0, R.string.add_authority,
                MENU_ID_ADD_AUTHORITY);
        items.add(data);

        //扫一扫
        data = new PopupMenu.PopupMenuData(0,
                R.string.scan_me, MENU_ID_SCAN);
        items.add(data);

        //分享
        data = new PopupMenu.PopupMenuData(0,
                R.string.share, MENU_ID_SHARE);
        items.add(data);

        if (items.size() <= 0) {
            return;
        }

        AdapterView.OnItemClickListener itemClickListener =
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        if (view.getTag() == null) {
                            return;
                        }
                        PopupMenu.PopupMenuData data = (PopupMenu.PopupMenuData) view.getTag();
                        if (data.getId() == MENU_ID_SCAN) {
                            CaptureActivity.start(getActivity());
                        } else if (data.getId() == MENU_ID_ADD_AUTHORITY) {
                            enterSubscribeSearch();
                        } else if (data.getId() == MENU_ID_SHARE) {
                            shareSchoolSpace();
                        }
                    }
                };
        PopupMenu popupMenu = new PopupMenu(getActivity(), itemClickListener, items);
        popupMenu.showAsDropDown(view, view.getWidth(), 0);
    }

    private void enterSubscribeSearch() {
        Intent intent = new Intent(getActivity(), SubscribeSearchActivity.class);
        intent.putExtra(SubscribeSearchActivity.EXTRA_SUBSCRIPE_SEARCH_TYPE,
                SubscribeSearchActivity.SUBSCRIPE_SEARCH_SCHOOL);
        startActivity(intent);
    }

    /**
     * 进入学程馆界面
     *
     * @param schoolInfo
     * @param type       学程馆类型
     */
    public static void enterLqCourseShop(Activity activity, SchoolInfo schoolInfo,
                                         int type) {
        int libraryType = getLibraryType(type);
        if (schoolInfo == null || activity == null || libraryType < 0) {
            return;
        }
        OrganCourseClassifyActivity.show(activity, schoolInfo.getSchoolId(), schoolInfo.getRoles(),
                libraryType);
    }

    /**
     * @param type
     * @return 学程馆类型 0 习课程馆 1练测馆  2 图书馆  3 视频馆
     */
    private static int getLibraryType(int type) {
        switch (type) {
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_LQCOURSE_SHOP:
                return OrganLibraryType.TYPE_LQCOURSE_SHOP;
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_VIDEO_LIBRARY:
                return OrganLibraryType.TYPE_VIDEO_LIBRARY;
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_LIBRARY:
                return OrganLibraryType.TYPE_LIBRARY;
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_PRACTICE_LIBRARY:
                return OrganLibraryType.TYPE_PRACTICE_LIBRARY;
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_BRAIN_LIBRARY:
                return OrganLibraryType.TYPE_BRAIN_LIBRARY;
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_TEACHING_PLAN:
                return OrganLibraryType.TYPE_TEACHING_PLAN;
        }
        return -1;
    }

}
