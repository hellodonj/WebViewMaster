package com.galaxyschool.app.wawaschool.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.galaxyschool.app.wawaschool.AccountActivity;
import com.galaxyschool.app.wawaschool.CampusPatrolMainActivity;
import com.galaxyschool.app.wawaschool.QrcodeProcessActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.CampusPatrolUtils;
import com.galaxyschool.app.wawaschool.common.DensityUtils;
import com.galaxyschool.app.wawaschool.common.ImageLoader;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.common.WebUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.config.VipConfig;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.pojo.QrcodeSchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfoResult;
import com.galaxyschool.app.wawaschool.pojo.TabEntityPOJO;
import com.galaxyschool.app.wawaschool.views.PopupMenu;
import com.lqwawa.client.pojo.SourceFromType;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.module.discovery.ui.HQCCourseListActivity;
import com.lqwawa.intleducation.module.onclass.OnlineClassRole;
import com.lqwawa.intleducation.module.onclass.school.SchoolInfoFragment;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.lqbaselib.net.Netroid;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchoolSpaceFragment extends SchoolSpaceBaseFragment implements SchoolSpaceBaseFragment.Constants {

    public static final String TAG = SchoolSpaceFragment.class.getSimpleName();
    private View subscribeBar;
    private TextView followBtn, friendBtn;
    private TextView address, phone;
    private ImageView qrCodeImageView;
    private GridView schoolGridView;
    //两栖蛙蛙机构内容的body
    private LinearLayout lqwawaSchoolDetailLayout;
    //在线课堂机构内容的body
    private LinearLayout onlineSchoolDetailLayout;
    private boolean attentionSchoolRequestControl = true;
    private String schoolGridViewTag;
    private String qrCodeImageUrl;
    private String qrCodeImagePath;
    private TextView holder1;
    private TextView holder2;

    public static final int REQUEST_CODE_SCHOOL_SPACE = 408;
    private static boolean hasFocusChanged;

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_school_space, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        if (getUserVisibleHint()) {
            if (schoolInfo != null) {
                updateSchoolInfoViews();
            }
            refreshData();
        }

    }

    private void refreshData() {
        updateSchoolSpaceViewCount();
        loadSchoolInfo();
    }

    private void loadSchoolEntityData() {
        if (!isAdded()) {
            return;
        }
        List<TabEntityPOJO> itemList = new ArrayList<TabEntityPOJO>();
        TabEntityPOJO item = null;

        //学程馆
        item = new TabEntityPOJO();
        item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_LQCOURSE_SHOP;
        item.title = getString(R.string.common_course_library);
        item.resId = R.drawable.icon_lqcourse_shcool_space;
        itemList.add(item);

        //视频馆
        item = new TabEntityPOJO();
        item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_VIDEO_LIBRARY;
        item.title = getString(R.string.common_video_library);
        item.resId = R.drawable.ic_video_library_rect;
        itemList.add(item);

        //图书馆
        item = new TabEntityPOJO();
        item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_LIBRARY;
        item.title = getString(R.string.common_library);
        item.resId = R.drawable.ic_library_rect;
        itemList.add(item);

        //练测馆
        item = new TabEntityPOJO();
        item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_PRACTICE_LIBRARY;
        item.title = getString(R.string.common_practice_library);
        item.resId = R.drawable.ic_practice_library_rect;
        itemList.add(item);

        //全脑馆
        item = new TabEntityPOJO();
        item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_BRAIN_LIBRARY;
        item.title = getString(R.string.common_brain_library);
        item.resId = R.drawable.ic_brain_library_rect;
        itemList.add(item);

        if (schoolInfo != null && schoolInfo.isTeacher()) {
            //校本资源库
            item = new TabEntityPOJO();
            item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_SCHOOL_BASED_CURRICULUM;
            item.title = getString(R.string.school_class_book);
            item.resId = R.drawable.icon_school_based_curriculum;
            itemList.add(item);
        }

        //学校班级
        item = new TabEntityPOJO();
        item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_SCHOOL_CLASS;
        item.title = getString(R.string.school_and_class);
        item.resId = R.drawable.icon_school_class;
        itemList.add(item);

//        if (schoolInfo != null && schoolInfo.isTeacher()) {
//            //精品资源库
//            item = new TabEntityPOJO();
//            item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_CHOICE_BOOKS;
//            item.title = getString(R.string.choice_books);
//            item.resId = R.drawable.ic_lq_galaxy_intl;
//            itemList.add(item);
//        }

        //校园直播台
        item = new TabEntityPOJO();
        item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_CAMPUS_DIRECT;
        item.title = getString(R.string.campus_now_direct);
        item.resId = R.drawable.campus_live_show;
        itemList.add(item);

        //校园动态
        item = new TabEntityPOJO();
        item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_SCHOOL_CAMPUS_DYNAMICS;
        item.title = getString(R.string.school_message);
        item.resId = R.drawable.icon_campus_dynamics;
        itemList.add(item);

        if (schoolInfo.isSchoolInspector() || VipConfig.isVip(getActivity())) {
            //校园巡查
            item = new TabEntityPOJO();
            item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_CAMPUS_PATROL;
            if (CampusPatrolUtils.SHOW_PRINCIPAL_ASSISTANT) {
                //显示校长助手
                item.title = getString(R.string.principal_assistant);
                item.resId = R.drawable.icon_principal_assistant;
            } else {
                //显示校园巡查
                item.title = getString(R.string.campus_patrol);
                item.resId = R.drawable.icon_campus_patrol;
            }
            itemList.add(item);
        }

        //学校介绍
        item = new TabEntityPOJO();
        item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_SCHOOL_INTRODUCTION;
        item.title = getString(R.string.subs_school_introduction);
        item.resId = R.drawable.icon_school_introduction;
        itemList.add(item);

        if (getAdapterViewHelper(schoolGridViewTag).hasData()) {
            getAdapterViewHelper(schoolGridViewTag).clearData();
        }
        getAdapterViewHelper(schoolGridViewTag).setData(itemList);

    }


    private void init() {
        Bundle args = getArguments();
        if (args != null) {
            schoolId = args.getString(Constants.EXTRA_SCHOOL_ID);
            schoolName = args.getString(Constants.EXTRA_SCHOOL_NAME);
        }
        initViews();
    }

    private void initViews() {
        schoolNameView.setText(schoolName);
        ImageView imageView = (ImageView) findViewById(R.id.back_btn);
        if (imageView != null) {
            imageView.setOnClickListener(this);
            imageView.setVisibility(View.GONE);
        }

        //隐藏一些元素
        TextView shareView = (TextView) findViewById(R.id.share_btn);
        if (shareView != null) {
            shareView.setVisibility(View.GONE);
        }

        schoolQrCodeView.setVisibility(View.GONE);
        findViewById(R.id.school_message_list_body).setVisibility(View.GONE);


        TextView titleTextView = (TextView) findViewById(R.id.contacts_header_title);
        if (titleTextView != null) {
            titleTextView.setText(getString(R.string.authority_info));
        }

        imageView = (ImageView) findViewById(R.id.contacts_header_left_btn);
        if (imageView != null) {
            imageView.setOnClickListener(this);
        }

        imageView = (ImageView) findViewById(R.id.contacts_header_right_ico);
        if (imageView != null) {
            imageView.setImageResource(R.drawable.selector_icon_navi_more);
            imageView.setVisibility(View.VISIBLE);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMoreMenu(v);
                }
            });
        }

        TextView textView = (TextView) findViewById(R.id.follow_btn);
        if (textView != null) {
            textView.setOnClickListener(this);
        }
        followBtn = textView;
        textView = (TextView) findViewById(R.id.friend_btn);
        if (textView != null) {
            textView.setOnClickListener(this);
        }
        friendBtn = textView;
        View view = findViewById(R.id.user_subscribe_bar_layout);
        if (view != null) {
            view.setVisibility(View.GONE);
        }
        subscribeBar = view;
        textView = (TextView) findViewById(R.id.header_more);
        if (textView != null) {
            textView.setOnClickListener(this);
        }
        holder1 = (TextView) findViewById(R.id.holder1);
        holder2 = (TextView) findViewById(R.id.holder2);

        //地址、电话、二维码
        address = (TextView) findViewById(R.id.address);
        phone = (TextView) findViewById(R.id.phone);
        qrCodeImageView = (ImageView) findViewById(R.id.contacts_qrcode_image);

        lqwawaSchoolDetailLayout = (LinearLayout) findViewById(R.id.ll_lqwawa_school_detail);
        onlineSchoolDetailLayout = (LinearLayout) findViewById(R.id.ll_online_school_detail);
    }

    private void loadQrCodeImage() {

        if (TextUtils.isEmpty(qrCodeImageUrl)) {
            return;
        }
        qrCodeImagePath = ImageLoader.getCacheImagePath(qrCodeImageUrl);
        File file = new File(qrCodeImagePath);
        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(qrCodeImagePath);
            if (bitmap != null) {
                qrCodeImageView.setImageBitmap(bitmap);
                return;
            }
            file.delete();
        }

        Netroid.downloadFile(getActivity(), qrCodeImageUrl, qrCodeImagePath,
                new Listener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (getActivity() == null) {
                            return;
                        }
                        qrCodeImageView.setImageBitmap(BitmapFactory.decodeFile(qrCodeImagePath));
                    }

                    @Override
                    public void onError(NetroidError error) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onError(error);
                        TipsHelper.showToast(getActivity(),
                                R.string.picture_download_failed);
                    }
                });
    }

    private void initSchoolGridViewHelper() {

        schoolGridView = (GridView) findViewById(R.id.school_grid_view);
        if (schoolGridView != null) {
            AdapterViewHelper schoolGridViewHelper = new AdapterViewHelper(getActivity(),
                    schoolGridView, R.layout.item_tab_entity_gridview) {
                @Override
                public void loadData() {
                    loadSchoolEntityData();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TabEntityPOJO data = (TabEntityPOJO) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    //头像
                    ImageView imageView = (ImageView) view.findViewById(R.id.icon_head);
                    if (imageView != null) {
                        imageView.setImageResource(data.getResId());
                    }
                    //标题
                    TextView textView = (TextView) view.findViewById(R.id.title);
                    if (textView != null) {
                        textView.setText(data.getTitle());
                    }

                    //时间
                    textView = (TextView) view.findViewById(R.id.time);
                    if (textView != null) {
                        textView.setVisibility(View.INVISIBLE);
                    }

                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;
                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null || holder.data == null) {
                        return;
                    }
                    TabEntityPOJO data = (TabEntityPOJO) holder.data;
                    if (data != null) {
                        controlEvent(data.getType());
                    }
                }
            };
            //根据tag来区分不同的数据源
            this.schoolGridViewTag = String.valueOf(schoolGridView.getId());
            addAdapterViewHelper(this.schoolGridViewTag, schoolGridViewHelper);
        }
    }

    private void controlEvent(int type) {
        if (type < 0) {
            return;
        }
        UIUtils.currentSourceFromType = SourceFromType.OTHER;
        switch (type) {

            //学校介绍
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_SCHOOL_INTRODUCTION:
                enterSchoolIntroduction();
                break;

            //校园动态
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_SCHOOL_CAMPUS_DYNAMICS:
                if (!isLogin()) {
                    enterAccountActivity();
                    return;
                }
                enterMoreSchoolMessage();
                break;

            //学校班级
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_SCHOOL_CLASS:
                if (!isLogin()) {
                    enterAccountActivity();
                    return;
                }
                enterSchoolClasses();
                break;

            //校本课程
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_SCHOOL_BASED_CURRICULUM:
                UIUtils.currentSourceFromType = SourceFromType.PUBLIC_LIBRARY;
                ActivityUtils.enterBookStoreListActivity(getActivity(), schoolInfo);
                break;
            //新的精品资源库
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_CHOICE_BOOKS:
                UIUtils.currentSourceFromType = SourceFromType.CHOICE_LIBRARY;
                ActivityUtils.enterBookStoreListActivity(getActivity(), schoolInfo, true);
                break;
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_CAMPUS_DIRECT:
                ActivityUtils.enterCampusDirect(getActivity(), schoolInfo);
                break;
            //校园巡查
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_CAMPUS_PATROL:
                if (CampusPatrolUtils.SHOW_PRINCIPAL_ASSISTANT) {
                    //校长助手
                    Utils.openPrincipalAssistantPage(getActivity(), getUserInfo(), schoolInfo);
                } else {
                    //校园巡查
                    enterCampusPatrol();
                }
                break;

            //习课程馆, 视频馆，图书馆, 练测馆
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_LQCOURSE_SHOP:
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_VIDEO_LIBRARY:
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_LIBRARY:
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_PRACTICE_LIBRARY:
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_BRAIN_LIBRARY:
                enterLqCourseShop(getActivity(), schoolInfo, type);
                break;
            default:
                break;
        }
    }


    private void enterCampusPatrol() {

        if (schoolInfo == null) {
            return;
        }
        Intent intent = new Intent(getActivity(), CampusPatrolMainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(SchoolInfo.class.getSimpleName(), schoolInfo);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void enterSchoolIntroduction() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("SchoolId", schoolId);
        WebUtils.openWebView(getActivity(),
                ServerUrl.SUBSCRIBE_SCHOOL_INTRODUCTION, params, schoolName);
    }


    @Override
    protected void showMoreMenu(View view) {
        List<PopupMenu.PopupMenuData> itemDatas = new ArrayList<>();
        if (schoolInfo != null && schoolInfo.isOnlineSchool()) {
            // 在线课堂机构
            itemDatas.add(new PopupMenu.PopupMenuData(0, R.string.label_save_qrcode));
        } else {
            itemDatas.add(new PopupMenu.PopupMenuData(0, R.string.save_qrcode));
        }
        itemDatas.add(new PopupMenu.PopupMenuData(0, R.string.subscription_recommend));
        if (schoolInfo != null && schoolInfo.isOnlineSchool() && !schoolInfo.isTeacher()) {
            itemDatas.add(new PopupMenu.PopupMenuData(0, R.string.str_to_bo_school_teacher));
        }
        if (schoolInfo != null && schoolInfo.hasSubscribed() && !schoolInfo.hasJoinedSchool()) {
            itemDatas.add(new PopupMenu.PopupMenuData(0, R.string.cancel_follow));
        }
        AdapterView.OnItemClickListener itemClickListener =
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        if (position == 0) {
                            if (schoolInfo != null && schoolInfo.isOnlineSchool()) {
                                //二维码
                                Utils.showViewQrCodeDialog(getActivity(), schoolInfo, null);
                            } else {
                                saveQrCodeImage(qrCodeImageUrl);
                            }
                        } else if (position == 1) {
                            shareSchoolSpace();
                        } else if (position == 2) {
                            if (schoolInfo != null && schoolInfo.isOnlineSchool() && !schoolInfo.isTeacher()) {
                                enterJoinSchoolDetail();
                            } else {
                                subscribeSchool(false);
                            }
                        } else if (position == 3) {
                            subscribeSchool(false);
                        }
                    }
                };
        PopupMenu popupMenu = new PopupMenu(getActivity(), itemClickListener, itemDatas);
        popupMenu.showAsDropDown(view, view.getWidth(), 0);
    }

    private void saveQrCodeImage(String qrCodeImageUrl) {

        if (TextUtils.isEmpty(qrCodeImageUrl)) {
            return;
        }
        String filePath = ImageLoader.saveImage(getActivity(), qrCodeImageUrl);
        if (filePath != null) {
            TipsHelper.showToast(getActivity(),
                    getString(R.string.image_saved_to, filePath));
        } else {
            TipsHelper.showToast(getActivity(), getString(R.string.save_failed));
        }
    }

    private void enterJoinSchoolDetail() {
        Intent intent = new Intent(getActivity(), QrcodeProcessActivity.class);
        QrcodeSchoolInfo data = new QrcodeSchoolInfo();
        data.setId(schoolInfo.getSchoolId());
        data.setSname(schoolInfo.getSchoolName());
        data.setLogoUrl(schoolInfo.getSchoolLogo());
        Bundle args = new Bundle();
        args.putSerializable(ActivityUtils.KEY_QRCODE_SCHOOL_INFO, data);
        intent.putExtras(args);
        startActivity(intent);
    }

    @Override
    protected void updateSchoolInfoViews() {
        super.updateSchoolInfoViews();
        updateSubscribeBar();
    }

    private void updateSubscribeBar() {
        if (schoolInfo == null) {
            return;
        }
        //更新地址、电话、二维码
        address.setText(schoolInfo.getSchoolAddress());
        phone.setText(schoolInfo.getSchoolPhone());
        qrCodeImageUrl = AppSettings.getFileUrl(schoolInfo.getQRCode());
        if (qrCodeImageView != null && !schoolInfo.isOnlineSchool()) {
            loadQrCodeImage();
        }

        if (schoolInfo.hasJoinedSchool()) {
            subscribeBar.setVisibility(View.GONE);
            followBtn.setText(R.string.subscribed);
            holder1.setVisibility(View.GONE);
            holder2.setVisibility(View.GONE);
            followBtn.setEnabled(false);
            friendBtn.setText(R.string.joined_school);
            friendBtn.setEnabled(false);
        } else if (schoolInfo.hasSubscribed()) {
            subscribeBar.setVisibility(View.VISIBLE);
            holder1.setVisibility(View.INVISIBLE);
            holder2.setVisibility(View.INVISIBLE);
            followBtn.setVisibility(View.GONE);
            followBtn.setText(R.string.cancel_follow);
            followBtn.setEnabled(true);
            followBtn.setSelected(true);
            friendBtn.setText(R.string.join_school);
            friendBtn.setEnabled(true);
            friendBtn.setSelected(false);
        } else {
            subscribeBar.setVisibility(View.VISIBLE);
            followBtn.setVisibility(View.VISIBLE);
            holder1.setVisibility(View.GONE);
            holder2.setVisibility(View.GONE);
            followBtn.setText(R.string.follow);
            followBtn.setEnabled(true);
            followBtn.setSelected(false);
            friendBtn.setText(R.string.join_school);
            friendBtn.setEnabled(true);
            friendBtn.setSelected(false);
        }
    }

    @Override
    protected void loadSchoolInfo() {
        if (getUserInfo() == null) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("MemberId", getUserInfo().getMemberId());
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
                        schoolInfo = getResult().getModel();
                        if (schoolInfo != null) {
                            updateSchoolInfoViews();
                            if (schoolInfo.isOnlineSchool()) {
                                //在线课堂数据
                                loadOnlineSchoolData();
                            } else {
                                loadLqWawaSchoolData();
                            }
                        }
                    }
                });
    }

    /**
     * 加载lqwawa school data
     */
    private void loadLqWawaSchoolData() {
        lqwawaSchoolDetailLayout.setVisibility(View.VISIBLE);
        onlineSchoolDetailLayout.setVisibility(View.GONE);
        //followBtn.setVisibility(View.VISIBLE);
        friendBtn.setVisibility(View.VISIBLE);
        initSchoolGridViewHelper();
        loadSchoolEntityData();
    }


    /**
     * 加载online school data
     */
    private void loadOnlineSchoolData() {
        onlineSchoolDetailLayout.setVisibility(View.VISIBLE);
        lqwawaSchoolDetailLayout.setVisibility(View.GONE);
        //followBtn.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) followBtn.getLayoutParams();
        layoutParams.leftMargin = DensityUtils.dp2px(getActivity(), 90);
        layoutParams.rightMargin = DensityUtils.dp2px(getActivity(), 90);
        followBtn.setLayoutParams(layoutParams);

        friendBtn.setVisibility(View.GONE);


        // 将在线课堂片段替换布局
        if (EmptyUtil.isNotEmpty(schoolInfo)) {
            String roles = schoolInfo.getRoles();
            String roleType = getOnlineClassRoleInfo(roles);
            Fragment fragment = SchoolInfoFragment.newInstance(schoolId, schoolName, schoolInfo.getSchoolLogo(), true, roleType);
            FragmentManager fragmentManager = getChildFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.ll_online_school_detail, fragment).commit();
        }
    }

    /**
     * 获取在线课堂角色信息
     *
     * @param roles 角色信息
     * @return 判断顺序 老师->家长->学生
     */
    private String getOnlineClassRoleInfo(@NonNull String roles) {
        // 默认学生身份
        String roleType = OnlineClassRole.ROLE_STUDENT;
        if (UserHelper.isTeacher(roles)) {
            // 老师身份
            roleType = OnlineClassRole.ROLE_TEACHER;
        } else if (UserHelper.isParent(roles)) {
            // 家长身份
            roleType = OnlineClassRole.ROLE_PARENT;
        } else if (UserHelper.isStudent(roles)) {
            // 学生身份
            roleType = OnlineClassRole.ROLE_STUDENT;
        }
        return roleType;
    }

    protected void updateSchoolSpaceViewCount() {
        if (getUserInfo() == null) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("CategoryId", schoolId);
        params.put("BType", "2");
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.UPDATE_BROWSE_NUM_URL,
                params,
                new DefaultListener<ModelResult>(ModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                    }
                });

    }

    /**
     * 判断是否关注了机构
     *
     * @param subscribe 关注
     */
    private void subscribeSchool(boolean subscribe) {
        if (!getMyApplication().hasLogined()) {
            enterAccountActivity();
            return;
        }
        if (schoolInfo == null) {
            return;
        }
        if (!attentionSchoolRequestControl) {
            return;
        }
        attentionSchoolRequestControl = false;
        if (getUserInfo() == null) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("SchoolId", schoolId);
        DefaultListener<ModelResult> listener =
                new DefaultListener<ModelResult>(ModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        boolean subscribe = ((Boolean) getTarget()).booleanValue();
                        if (getResult() == null || !getResult().isSuccess()) {
//                    TipsHelper.showToast(getActivity(), subscribe ?
//                            R.string.subscribe_failed : R.string.subscribe_cancel_failed);
                            return;
                        } else {
                            //改变刷新标识值
                            setHasFocusChanged(true);
                            TipsHelper.showToast(getActivity(), subscribe ?
                                    R.string.follow_success_tips : R.string.subscribe_cancel_success);
                            schoolInfo.setSubscribed(subscribe);
                            updateSubscribeBar();
                            //关注/取消关注成功后，向校园空间发广播
                            MySchoolSpaceFragment.sendBrocast(getActivity());
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        attentionSchoolRequestControl = true;
                    }
                };
        listener.setTarget(subscribe);
        listener.setShowLoading(true);
        String serverUrl = subscribe ? ServerUrl.SUBSCRIBE_ADD_SCHOOL_URL :
                ServerUrl.SUBSCRIBE_REMOVE_SCHOOL_URL;
        RequestHelper.sendPostRequest(getActivity(), serverUrl,
                params, listener);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back_btn) {
            finish();
        } else if (v.getId() == R.id.follow_btn) {
            subscribeSchool(!v.isSelected());
        } else if (v.getId() == R.id.friend_btn) {
            if (isLogin()) {
                ActivityUtils.enterSchoolClassListActivity(getActivity(), schoolInfo);
            } else {
                enterAccountActivity();
            }
        } else if (v.getId() == R.id.header_more) {
            enterMoreSchoolMessage();
        } else {
            super.onClick(v);
        }
    }

    private void enterAccountActivity() {
        //登录
        Intent intent = new Intent(getActivity(), AccountActivity.class);
        Bundle args = new Bundle();
        args.putBoolean(AccountActivity.EXTRA_HAS_LOGINED, false);
        args.putBoolean(AccountActivity.EXTRA_ENTER_HOME_AFTER_LOGIN, false);
        intent.putExtras(args);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
        }
    }

    private void enterMoreSchoolMessage() {
        ActivityUtils.enterSchoolMessageActivity(getActivity(), schoolInfo);
    }

    public static void setHasFocusChanged(boolean hasFocusChanged) {
        SchoolSpaceFragment.hasFocusChanged = hasFocusChanged;
    }

    public static boolean hasFocusChanged() {
        return hasFocusChanged;
    }

}
