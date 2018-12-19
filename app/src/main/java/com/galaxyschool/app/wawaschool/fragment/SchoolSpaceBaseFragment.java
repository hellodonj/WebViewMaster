package com.galaxyschool.app.wawaschool.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.CaptureActivity;
import com.galaxyschool.app.wawaschool.ChoiceBooksActivity;
import com.galaxyschool.app.wawaschool.ContactsQrCodeDetailsActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.SchoolClassListActivity;
import com.galaxyschool.app.wawaschool.SchoolCourseListActivity;
import com.galaxyschool.app.wawaschool.SchoolInfoActivity;
import com.galaxyschool.app.wawaschool.SubscribeSearchActivity;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.NewResourceDeleteHelper;
import com.galaxyschool.app.wawaschool.common.ShareUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.fragment.resource.NewResourceAdapterViewHelper;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.lqwawa.intleducation.module.onclass.OnlineClassListActivity;
import com.lqwawa.lqbaselib.net.library.DataResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.CourseInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfoListResult;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.SubscribeClassInfo;
import com.galaxyschool.app.wawaschool.views.PopupMenu;
import com.oosic.apps.share.ShareInfo;
import com.oosic.apps.share.SharedResource;
import com.umeng.socialize.media.UMImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchoolSpaceBaseFragment extends ContactsListFragment {

    protected int fromType;

    public interface Constants {
        String EXTRA_SCHOOL_ID = "schoolId";
        String EXTRA_SCHOOL_NAME = "schoolName";
        int FROM_MY_SCHOOL_SPACE_FRAGMENT = 1;
        int FROM_SCHOOL_SPACE_FRAGMENT = 2;
    }
    public static final int MENU_ID_ADD_AUTHORITY = 0;
    public static final int MENU_ID_SCAN = 1;
    public static final int MENU_ID_SHARE = 2;

    protected String schoolId;
    protected String schoolName;
    protected SchoolInfo schoolInfo;
    protected SubscribeClassInfo classInfo;

    protected ImageView schoolIconView;
    protected TextView schoolNameView;
    protected ImageView schoolQrCodeView;
    protected TextView schoolFollowersCountView;
    protected TextView schoolViewCountView;
    protected GridView schoolMessageListView;
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
        schoolMessageListView = (GridView) findViewById(R.id.resource_list_view);
    }

    private void showDeleteResourceDialog(final NewResourceInfo data, String title) {
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(
                getActivity(), null,
                title,
                getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                },
                getString(R.string.confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        deleteResource(data);
                    }
                });
        messageDialog.show();
    }

    private void deleteResource(NewResourceInfo data) {
        Map<String, Object> params = new HashMap();
        params.put("IdList", data.getId());
        params.put("Type", "1");
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
                            TipsHelper.showToast(getActivity(), R.string.delete_failure);
                            return;
                        } else {
                            TipsHelper.showToast(getActivity(), R.string.delete_success);
                            NewResourceInfo data = (NewResourceInfo) getTarget();
                            if (data.getType() == NewResourceInfo.TYPE_SCHOOL_NEWS || data.getType() == NewResourceInfo.TYPE_SCHOOL_ACTIVITY) {
                                loadSchoolMessageList();
                            }
                        }
                    }
                };
        listener.setTarget(data);
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.DELETE_OWNER_POSTBAR_URL,
                params, listener);
    }

    private void initViews() {
        TextView textView = (TextView) findViewById(R.id.share_btn);
        if (textView != null) {
            textView.setOnClickListener(this);
            textView.setVisibility(View.VISIBLE);
        }

        schoolIconView.setOnClickListener(this);
        schoolQrCodeView.setOnClickListener(this);

        GridView listView = schoolMessageListView;
        if (listView != null) {
            AdapterViewHelper adapterViewHelper = new NewResourceAdapterViewHelper(
                    getActivity(), listView) {
                @Override
                public void loadData() {
                    loadSchoolMessageList();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    NewResourceInfo data = (NewResourceInfo) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    if (fromType == Constants.FROM_SCHOOL_SPACE_FRAGMENT) {
                        TextView textView = (TextView) view.findViewById(R.id.resource_type);
                        if (textView != null) {
                            textView.setText(NewResourceInfo.getSchoolResourceTypeString(getContext(), data.getType())
                                    + NewResourceInfo.getResourceStateString(getContext(), data.getState()));
                            textView.setVisibility(View.VISIBLE);
                        }
                        ImageView imageView = (ImageView) view.findViewById(R.id.resource_delete);
                        if (!TextUtils.isEmpty(getMemeberId()) && !TextUtils.isEmpty(data.getAuthorId()) && getMemeberId().equals(data.getAuthorId())) {
                            if (imageView != null) {
                                imageView.setVisibility(View.VISIBLE);
                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        NewResourceInfo data = (NewResourceInfo) v.getTag();
                                        if (data != null) {
                                            showDeleteResourceDialog(data,
                                                    getString(R.string.delete_school_message));
                                        }
                                    }
                                });
                            }
                        } else {
                            if (imageView != null) {
                                imageView.setVisibility(View.GONE);
                            }
                        }
                    } else {
                        TextView textView = (TextView) view.findViewById(R.id.resource_type);
                        if (textView != null) {
                            textView.setText(NewResourceInfo.getClassResourceTypeString(getContext(), data.getType()));
                            textView.setVisibility(View.VISIBLE);
                        }
                        ImageView imageView = (ImageView) view.findViewById(R.id.resource_indicator);
                        if (imageView != null) {
                            imageView.setVisibility(data.isRead() ? View.GONE : View.VISIBLE);
                        }
                        ImageView imageViewDelete = (ImageView) view.findViewById(R.id.resource_delete);
                        String tip = "";
                        if (data.getType() == NewResourceInfo.TYPE_CLASS_NOTICE) {
                            tip = getString(R.string.delete_note);
                        } else if (data.getType() == NewResourceInfo.TYPE_CLASS_SHOW) {
                            tip = getString(R.string.delete_show);
                        } else if (data.getType() == NewResourceInfo.TYPE_CLASS_COURSE) {
                            tip = getString(R.string.delete_course);
                        } else if (data.getType() == NewResourceInfo.TYPE_CLASS_HOMEWORK) {
                            tip = getString(R.string.delete_homework);
                        }
                        NewResourceDeleteHelper helper = new NewResourceDeleteHelper(getActivity
                                (), getCurrAdapterViewHelper(), NewResourceDeleteHelper.SHOW_CLASS_HOMEWORK, data, imageViewDelete);
                        helper.initImageViewEvent(getMemeberId(), tip);
                        if(classInfo != null) {
                            helper.initImageViewEvent(tip, classInfo.isHeadMaster(), data.getType());
                        }
                        helper.setDeleteListener(new NewResourceDeleteHelper.DeleteListener() {
                            @Override
                            public void onDelete() {
                                getPageHelper().clear();
                                loadSchoolMessageList();
                            }
                        });
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
                    if (fromType == Constants.FROM_SCHOOL_SPACE_FRAGMENT) {
                        if (data != null) {
                            if (data.getType() == NewResourceInfo.TYPE_SCHOOL_SIGN_ACTIVITY
                                    || data.getType() == NewResourceInfo.TYPE_SCHOOL_VOTE_ACTIVITY) {
                                TipsHelper.showToast(getContext(), R.string.resource_type_not_supported);
                                return;
                            }
                            if (data.getType() == NewResourceInfo.TYPE_SCHOOL_ACTIVITY) {
                                ActivityUtils.openOnlineNote(getActivity(),
                                        data.getCourseInfo(), false, false);
                            } else if (data.getType() == NewResourceInfo.TYPE_SCHOOL_NEWS) {
                                StringBuilder builder = new StringBuilder();
                                builder.append(ServerUrl.WEB_VIEW_NEW_URL);
                                builder.append("?Id=");
                                builder.append(data.getId());
                                builder.append("&MemberId=");
                                builder.append(getUserInfo().getMemberId());
                                builder.append("&SchoolId=");
                                builder.append(schoolId);
                                builder.append("&Type=1");
                                ActivityUtils.openNews(getActivity(), builder.toString(), data.getTitle());
                            }
                        }
                    } else {
                        if (data != null) {
                            markResourceAsRead(data);
                            CourseInfo courseInfo = data.getCourseInfo();
                            if (data.getType() == NewResourceInfo.TYPE_CLASS_HOMEWORK) {
                                courseInfo.setIsHomework(true);
                            } else {
                                courseInfo.setIsHomework(false);
                            }
                            ActivityUtils.openOnlineNote(getActivity(), courseInfo, false, false);
                        }
                    }

                }
            };
            setCurrAdapterViewHelper(listView, adapterViewHelper);
        }
    }

    private void markResourceAsRead(NewResourceInfo data) {
        if (getUserInfo() == null){
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
                        getCurrAdapterViewHelper().update();
                    }
                };
        listener.setTarget(data);
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.MARK_MY_RESOURCE_AS_READ_URL, params, listener);
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
//            schoolFollowersCountView.setText(getString(R.string.n_subscribed, schoolInfo.getAttentionNumber()));
//            schoolViewCountView.setText(getString(R.string.n_browse, schoolInfo.getBrowseNum()));
            schoolFollowersCountView.setText(getString(R.string.str_follow_people, Utils.transferNumberData(schoolInfo.getAttentionNumber())));
            schoolViewCountView.setText(getString(R.string.Str_view_people, Utils.transferNumberData(schoolInfo.getBrowseNum())));
        } else {
            schoolNameView.setText(null);
//            schoolIconView.setImageBitmap(null);
            schoolIconView.setImageResource(R.drawable.default_school_icon);
            schoolFollowersCountView.setText(getString(R.string.default_attention_count));
            schoolViewCountView.setText(getString(R.string.default_look_through_count));
        }

    }

    private void loadSchooResourcelInfo() {
        if (TextUtils.isEmpty(schoolId)) {
            return;
        }
        if (getUserInfo() == null){
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("SchoolId", schoolId);
        params.put("Pager", getPageHelper().getFetchingPagerArgs());
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.GET_SCHOOL_MESSAGE_LIST_URL,
                params,
                new DefaultDataListener<NewResourceInfoListResult>(
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
                        updateSchoolMessageListView(getResult());
                    }
                });
    }

    protected void loadSchoolMessageList() {
        if (fromType == Constants.FROM_SCHOOL_SPACE_FRAGMENT) {
            loadSchooResourcelInfo();
        } else {
            loadMYSchooResourcelInfo();
        }

    }

    private void loadMYSchooResourcelInfo() {
        if (classInfo == null) {
            return;
        }
        if (getUserInfo() == null){
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("ClassId", classInfo.getClassId());
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
                        updateSchoolMessageListView(getResult());
                    }
                };
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_CLASS_MESSAGE_LIST_URL, params, listener);
    }

    protected void updateSchoolMessageListView(NewResourceInfoListResult result) {
        List<NewResourceInfo> list = result.getModel().getData();
        if (list != null && list.size() > 4) {
            list = list.subList(0, 4);
        }
        if (list == null) {
            list = new ArrayList<NewResourceInfo>();
        }
        getCurrAdapterViewHelper().setData(list);
    }


    protected void enterQrcodeScanning() {
        Intent intent = new Intent(getActivity(), CaptureActivity.class);
        startActivity(intent);
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

    protected void enterSchoolDetails() {
        if (schoolInfo == null) {
            return;
        }
        Bundle args = new Bundle();
        args.putString(SchoolInfoActivity.EXTRA_SCHOOL_ID, schoolInfo.getSchoolId());
        args.putString(SchoolInfoActivity.EXTRA_SCHOOL_NAME, schoolInfo.getSchoolName());
        Intent intent = new Intent(getActivity(), SchoolInfoActivity.class);
        intent.putExtras(args);
        startActivity(intent);
    }

    protected void enterSchoolClasses() {
        if (schoolInfo == null) {
            return;
        }

        if(schoolInfo.isOnlineSchool()){
            OnlineClassListActivity.show(getContext(),schoolId,schoolName);
        }else{
            Bundle args = new Bundle();
            args.putString(SchoolClassListActivity.EXTRA_SCHOOL_ID, schoolInfo.getSchoolId());
            args.putString(SchoolClassListActivity.EXTRA_SCHOOL_NAME, schoolInfo.getSchoolName());
            args.putBoolean(SchoolClassListActivity.EXTRA_IS_TEACHER, schoolInfo.isTeacher());
            //是否要显示隐藏的菜单
            args.putBoolean(SchoolClassListActivity.EXTRA_IS_NEED_SHOW_MENU,true);
            args.putSerializable(ActivityUtils.EXTRA_SCHOOL_INFO,schoolInfo);
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



    private void enterMoreSchoolMessage() {
        ActivityUtils.enterSchoolMessageActivity(getActivity(),schoolInfo);
    }


    private void enterMoreSchoolCourse() {
        if (schoolInfo == null) {
            return;
        }
        Bundle args = new Bundle();
        args.putString(SchoolCourseListActivity.EXTRA_SCHOOL_ID, schoolInfo.getSchoolId());
        args.putString(SchoolCourseListActivity.EXTRA_SCHOOL_NAME, schoolInfo.getSchoolName());
        args.putBoolean(SchoolCourseListActivity.EXTRA_IS_TEACHER, schoolInfo.isTeacher());
        Intent intent = new Intent(getActivity(), SchoolCourseListActivity.class);
        intent.putExtras(args);
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
                            enterCaptureActivity();
                        } else if (data.getId() == MENU_ID_ADD_AUTHORITY) {
                            enterSubscribeSearch();
                        }else if (data.getId() == MENU_ID_SHARE){
                            shareSchoolSpace();
                        }
                    }
                };
        PopupMenu popupMenu = new PopupMenu(getActivity(), itemClickListener, items);
        popupMenu.showAsDropDown(view, view.getWidth(), 0);
    }

    private void enterCaptureActivity() {

        Intent intent = new Intent(getActivity(), CaptureActivity.class);
        startActivity(intent);
    }

    private void enterSubscribeSearch() {
        Intent intent = new Intent(getActivity(), SubscribeSearchActivity.class);
        intent.putExtra(SubscribeSearchActivity.EXTRA_SUBSCRIPE_SEARCH_TYPE,
                SubscribeSearchActivity.SUBSCRIPE_SEARCH_SCHOOL);
        startActivity(intent);
    }

}
