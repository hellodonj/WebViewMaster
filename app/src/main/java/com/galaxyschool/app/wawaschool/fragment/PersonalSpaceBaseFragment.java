package com.galaxyschool.app.wawaschool.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.ContactsQrCodeDetailsActivity;
import com.galaxyschool.app.wawaschool.PersonalPostBarListActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ShareUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfoListResult;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.oosic.apps.share.ShareInfo;
import com.oosic.apps.share.SharedResource;
import com.umeng.socialize.media.UMImage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class PersonalSpaceBaseFragment extends ContactsListFragment {

    public interface Constants {
        public static final String EXTRA_USER_ID = "userId";
        public static final String EXTRA_USER_NAME = "userName";
        public static final String EXTRA_USER_REAL_NAME = "userRealName";
        public static final String EXTRA_FROM_WHERE_COMEIN = "from_where_comein";
    }
    public interface FromWhereEnter{
        int fromPersonSpace = 100;
    }

    protected int isWhereEnter = -1;
    protected String userId;
    protected String userName;
    protected String userRealName;
    protected String remarkName;
    protected UserInfo userInfo;
    protected NewResourceInfoListResult resourceListResult;

    protected ImageView userIconView;
    protected TextView userNameView;
    protected ImageView userQrCodeView;
    protected TextView userFollowersCountView;
    protected TextView userViewCountView;
    protected View countLayoutView;
    protected List<NewResourceInfo> list;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findViews();
        initViews();
    }

    private void findViews() {
        userIconView = (ImageView) findViewById(R.id.user_icon);
        userNameView = (TextView) findViewById(R.id.user_name);
        countLayoutView = findViewById(R.id.layout_count);
        userFollowersCountView = (TextView) findViewById(R.id.user_followers_count);
        userViewCountView = (TextView) findViewById(R.id.user_view_count);
        userQrCodeView = (ImageView) findViewById(R.id.user_qrcode);
    }

    private void initViews() {
        TextView textView = (TextView) findViewById(R.id.share_btn);
        if (textView != null) {
            textView.setOnClickListener(this);
            textView.setVisibility(View.VISIBLE);
        }

        userIconView.setOnClickListener(this);
        userQrCodeView.setOnClickListener(this);
    }

    protected abstract void loadUserInfo();

    public void updatePersonalSpaceViewCount() {
        if (getUserInfo() == null){
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("CategoryId", userId);
        params.put("BType", "1");
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.UPDATE_BROWSE_NUM_URL,
                params,
                new DefaultListener<ModelResult>(ModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()) {
                            return;
                        }
//                    if(userInfo != null) {
//                        userInfo.setBrowseNum(userInfo.getBrowseNum()+1);
//                        userViewCountView.setText(getString(R.string.n_browse, userInfo.getBrowseNum()));
//                    }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        loadUserInfo(); // try to update subscribed count
                    }
                });
    }


    protected void updateUserInfoViews() {
        if (userInfo != null) {
            userId = userInfo.getMemberId();
            userName = userInfo.getNickName();
            userRealName = userInfo.getRealName();
            remarkName = userInfo.getNoteName();

            getThumbnailManager().displayUserIcon(AppSettings.getFileUrl(
                    userInfo.getHeaderPic()), userIconView);
            userNameView.setText(TextUtils.isEmpty(userRealName) ? userName : userRealName);
            userFollowersCountView.setText(getString(R.string.n_followed,
                    userInfo.getAttentionNumber()));
            userViewCountView.setText(getString(R.string.n_browse, userInfo.getBrowseNum()));
        } else {
            userNameView.setText(null);
//            userIconView.setImageBitmap(null);
            userIconView.setImageResource(R.drawable.default_user_icon);
            userFollowersCountView.setText(getString(R.string.default_attention_count));
            userViewCountView.setText(getString(R.string.default_look_through_count));
        }
    }

    protected void enterMoreResource() {
        if (userInfo == null) {
            return;
        }
        Bundle args = new Bundle();
        args.putString(PersonalPostBarListActivity.EXTRA_MEMBER_ID, userId);
        args.putBoolean(PersonalPostBarListActivity.EXTRA_IS_TEACHER, userInfo.isTeacher());
        Intent intent = new Intent(getActivity(), PersonalPostBarListActivity.class);
        intent.putExtras(args);
        startActivity(intent);
    }

    private void enterUserQrCode() {
        if (userInfo == null) {
            return;
        }

        Bundle args = new Bundle();
        args.putString(ContactsQrCodeDetailsActivity.EXTRA_TITLE,
                getActivity().getString(R.string.personal_qrcode));
        args.putInt(ContactsQrCodeDetailsActivity.EXTRA_TARGET_TYPE,
                ContactsQrCodeDetailsActivity.TARGET_TYPE_PERSON);
        args.putString(ContactsQrCodeDetailsActivity.EXTRA_TARGET_ID,
                userInfo.getMemberId());
        args.putString(ContactsQrCodeDetailsActivity.EXTRA_TARGET_ICON,
                userInfo.getHeaderPic());
//        if(!TextUtils.isEmpty(userInfo.getRealName())) {
//            args.putString(ContactsQrCodeDetailsActivity.EXTRA_TARGET_NAME,
//                userInfo.getRealName());
//        } else {
//            args.putString(ContactsQrCodeDetailsActivity.EXTRA_TARGET_NAME,
//                userInfo.getNickName());
//        }
        args.putString(ContactsQrCodeDetailsActivity.EXTRA_TARGET_NAME,
                userInfo.getRealName());
        args.putString(ContactsQrCodeDetailsActivity.EXTRA_TARGET_DESCRIPTION,
                userInfo.getNickName());
        args.putString(ContactsQrCodeDetailsActivity.EXTRA_TARGET_QR_CODE,
                userInfo.getQRCode());
        Intent intent = new Intent(getActivity(), ContactsQrCodeDetailsActivity.class);
        intent.putExtras(args);
        startActivity(intent);
    }

    protected void sharePersonalSpace() {
        if (userInfo == null) {
            return;
        }
        ShareInfo shareInfo = new ShareInfo();
        shareInfo.setTitle(userInfo.getNickName());
        if (!TextUtils.isEmpty(userInfo.getRealName())) {
            shareInfo.setTitle(userInfo.getRealName());
        }
        shareInfo.setContent(" ");
        String serverUrl = ServerUrl.SHARE_PERSONAL_SPACE_URL;
        String url = serverUrl + String.format(
                ServerUrl.SHARE_PERSONAL_SPACE_PARAMS, userInfo.getMemberId());
        shareInfo.setTargetUrl(url);
        UMImage umImage = null;
        if (!TextUtils.isEmpty(userInfo.getHeaderPic())) {
            umImage = new UMImage(getActivity(), AppSettings.getFileUrl(userInfo.getHeaderPic()));
        } else {
            umImage = new UMImage(getActivity(), R.drawable.ic_launcher);
        }
        shareInfo.setuMediaObject(umImage);
        SharedResource resource = new SharedResource();
        resource.setId(userInfo.getMemberId());
        resource.setTitle(userInfo.getNickName());
        if (!TextUtils.isEmpty(userInfo.getRealName())) {
            resource.setTitle(userInfo.getRealName());
        }
        resource.setDescription("");
        resource.setShareUrl(serverUrl);
        if (!TextUtils.isEmpty(userInfo.getHeaderPic())) {
            resource.setThumbnailUrl(AppSettings.getFileUrl(userInfo.getHeaderPic()));
        }
        resource.setType(SharedResource.RESOURCE_TYPE_HTML);
        resource.setFieldPatches(SharedResource.FIELD_PATCHES_PERSON_SHARE_URL);
        shareInfo.setSharedResource(resource);
        ShareUtils shareUtils = new ShareUtils(getActivity());
        shareUtils.share(getView(), shareInfo);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.user_qrcode) {
            enterUserQrCode();
        }
        if (v.getId() == R.id.share_btn) {
            sharePersonalSpace();
        } else {
            super.onClick(v);
        }
    }

}
