package com.galaxyschool.app.wawaschool.fragment;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.galaxyschool.app.wawaschool.BasicUserInfoActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.chat.activity.ChatActivity;
import com.galaxyschool.app.wawaschool.chat.applib.controller.HXSDKHelper;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.ImageLoader;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.pojo.UserInfoResult;
import com.galaxyschool.app.wawaschool.views.ContactsInputBoxDialog;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.galaxyschool.app.wawaschool.views.PopupMenu;
import com.lqwawa.lqbaselib.net.Netroid;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonalSpaceFragment extends PersonalSpaceBaseFragment {

    public static final String TAG = PersonalSpaceFragment.class.getSimpleName();

    private View subscribeBar;
    private TextView followBtn, friendBtn;
    private String qrCodeImageUrl;
    private ImageView qrCodeImageView;
    private String qrCodeImagePath;
    private TextView remarkNameTextView;
    private ImageView remarkLineImageView;
    private boolean isMyself;
    private View userAccountLayout, chatLayout;
    private TextView userDetailBtn, userExitAccountBtn, deleteFriendBtn, chatBtn;
    private int fromWhere = -1;
    public static final String ACTION_MODIFY_USER_REMARK_NAME = "action_modify_user_remark_name";
    private boolean canRequest=true;
    private static boolean hasFocusChanged;//关注点改变
    public static final int REQUEST_CODE_PERSONAL_SPACE = 208;//个人页面请求码
    private static boolean hasUnbindFriendRelationship;//好友关系被删除
    private static boolean hasRemarkNameChanged;//备注名改变
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_personal_space, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        refreshData();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void refreshData(){
        updateUserInfoViews();
        updatePersonalSpaceViewCount();
    }

    private void init() {
        userId = getArguments().getString(Constants.EXTRA_USER_ID);
        userName = getArguments().getString(Constants.EXTRA_USER_NAME);
        userRealName = getArguments().getString(Constants.EXTRA_USER_REAL_NAME);
        fromWhere = getArguments().getInt(ChatActivity.EXTRA_FROM_WHERE);
        isMyself = !TextUtils.isEmpty(userId) && userId.equals(getMemeberId());
        isWhereEnter = getArguments().getInt(Constants.EXTRA_FROM_WHERE_COMEIN);
        initViews();
    }

    private void initViews() {
        userNameView.setText(userName);

        ImageView imageView = (ImageView) findViewById(R.id.back_btn);
        if (imageView != null) {
            imageView.setOnClickListener(this);
            imageView.setVisibility(View.GONE);
        }

        //隐藏一些元素
        findViewById(R.id.post_bar_list_body).setVisibility(View.GONE);
        TextView shareView = (TextView) findViewById(R.id.share_btn);
        if (shareView != null) {
            shareView.setVisibility(View.GONE);
        }

        imageView = (ImageView) findViewById(R.id.contacts_header_left_btn);
        if (imageView != null) {
            imageView.setOnClickListener(this);
        }
        TextView titleView = (TextView) findViewById(R.id.contacts_header_title);
        if (titleView != null) {
            titleView.setText(getString(R.string.personal_info));
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

        //先隐藏二维码
        userQrCodeView.setVisibility(View.GONE);

        TextView textView = null;
        //备注布局
        View view = findViewById(R.id.user_real_name_attr);
        if (view != null) {
            //如果是自己，不显示“备注名”
            if (!isMyself && userInfo != null && userInfo.isFriend()) {
                view.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(View.GONE);
            }
            textView = (TextView) view.findViewById(R.id.contacts_attribute_key);
            textView.setText(R.string.remark_suffix);

            //备注名
            remarkNameTextView = (TextView) view.findViewById(R.id.contacts_attribute_value);
            remarkNameTextView.setTextColor(getResources().getColor(R.color.black));
            //编辑按钮
            imageView = (ImageView) view.findViewById(R.id.icon_modify_value);
            if (imageView != null) {
                imageView.setVisibility(View.VISIBLE);
            }

            imageView = (ImageView) view.findViewById(R.id.contacts_attribute_indicator);
            imageView.setVisibility(View.GONE);

            //一整行都能点击编辑按钮
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showModifyRemarkNameDialog();
                }
            });

        }

        //备注名下面的分割线
        remarkLineImageView = (ImageView) findViewById(R.id.user_real_name_line);
        if (!isMyself && userInfo != null && userInfo.isFriend()) {
            remarkLineImageView.setVisibility(View.VISIBLE);
        } else {
            remarkLineImageView.setVisibility(View.GONE);
        }

        view = findViewById(R.id.user_intro_attr);
        if (view != null) {
            textView = (TextView) view.findViewById(R.id.contacts_attribute_key);
            textView.setText(R.string.personal_intro_suffix);
            textView = (TextView) view.findViewById(R.id.contacts_attribute_value);
            textView.setText(null);
            imageView = (ImageView) view.findViewById(R.id.contacts_attribute_indicator);
            imageView.setVisibility(View.GONE);
        }
        view = findViewById(R.id.user_location_attr);
        if (view != null) {
            textView = (TextView) view.findViewById(R.id.contacts_attribute_key);
            textView.setText(R.string.location_suffix);
            textView = (TextView) view.findViewById(R.id.contacts_attribute_value);
            textView.setText(null);
            imageView = (ImageView) view.findViewById(R.id.contacts_attribute_indicator);
            imageView.setVisibility(View.GONE);
        }

        //个人信息布局
        userAccountLayout = findViewById(R.id.user_account_layout);
        if (userAccountLayout != null) {
            //从个人空间来的才显示底部的布局
            if (isMyself && (isWhereEnter == FromWhereEnter.fromPersonSpace)) {
                userAccountLayout.setVisibility(View.VISIBLE);
            } else {
                userAccountLayout.setVisibility(View.GONE);
            }
        }

        //个人详情
        textView = (TextView) findViewById(R.id.user_detail_btn);
        if (textView != null) {
            textView.setOnClickListener(this);
        }

        userDetailBtn = textView;

        //退出账户
        textView = (TextView) findViewById(R.id.user_exit_account_btn);
        if (textView != null) {
            textView.setOnClickListener(this);
        }

        userExitAccountBtn = textView;

        //关注布局
        view = findViewById(R.id.user_subscribe_bar_layout);
        if (view != null) {
            view.setVisibility(View.GONE);
        }
        subscribeBar = view;
        //关注
        textView = (TextView) findViewById(R.id.follow_btn);
        if (textView != null) {
            textView.setOnClickListener(this);
        }
        followBtn = textView;
        //聊天
        textView = (TextView) findViewById(R.id.friend_btn);
        if (textView != null) {
            textView.setOnClickListener(this);
        }
        friendBtn = textView;

        //聊天布局
        chatLayout = findViewById(R.id.user_chat_layout);

        //删除好友
        textView = (TextView) findViewById(R.id.delete_friend_btn);
        if (textView != null) {
            textView.setOnClickListener(this);
        }

        deleteFriendBtn = textView;

        //聊天
        textView = (TextView) findViewById(R.id.chat_btn);
        if (textView != null) {
            textView.setOnClickListener(this);
        }

        chatBtn = textView;

        //二维码
        qrCodeImageView = (ImageView) findViewById(R.id.contacts_qrcode_image);
    }

    private void showModifyRemarkNameDialog() {

        ContactsInputBoxDialog dialog = new ContactsInputBoxDialog(getActivity(),
                R.style.Theme_ContactsDialog, getString(R.string.remark),
                null, remarkName,
                getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        },
                getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String text = ((ContactsInputBoxDialog) dialog).getInputText();
                //当用户只输入空格是给于提示格式不对
                if (!TextUtils.isEmpty(text)){
                    String tempText=text.trim();
                    if (TextUtils.isEmpty(tempText)){
                        TipMsgHelper.ShowMsg(getActivity(),R.string.input_rename_illegal);
                        return;
                    }
                }
                if (!TextUtils.isEmpty(text) && !text.equals(remarkName)) {
                    editFriendRemark(text);
                }
            }
        });
        dialog.show();
        dialog.setInputLimitNumber(40);
    }

    private void editFriendRemark(final String newRemark) {
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getMemeberId());
        params.put("Id", userId);
        params.put("VersionCode", 1);
        params.put("NoteName", newRemark);
        DefaultListener listener =
                new DefaultListener<ModelResult>(ModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        ModelResult result = getResult();
                        if (result == null || !result.isSuccess()) {
                            return;
                        }
                        //设置刷新标识
                        setHasRemarkNameChanged(true);
                        remarkName = newRemark;
                        TipsHelper.showToast(getActivity(), getString(R.string.remark_friend_success));
                        updateRemark(remarkName);
                        //发广播给聊天页面更新好友名称
                        Intent intent = new Intent(ACTION_MODIFY_USER_REMARK_NAME);
                        intent.putExtra("remarkName",remarkName);
                        getActivity().sendBroadcast(intent);

                    }
                };
        listener.setShowLoading(true);
        postRequest(ServerUrl.CONTACTS_MODIFY_FRIEND_REMARK_URL, params, listener);
    }

    private void updateRemark(String remarkName) {
        remarkNameTextView.setText(remarkName);
    }

    private void showMoreMenu(View view) {

        List<PopupMenu.PopupMenuData> itemDatas = new ArrayList();
        itemDatas.add(new PopupMenu.PopupMenuData(0, R.string.save_qrcode));
        itemDatas.add(new PopupMenu.PopupMenuData(0, R.string.subscription_recommend));
        AdapterView.OnItemClickListener itemClickListener =
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        if (position == 0) {
                            saveQrCodeImage(qrCodeImageUrl);
                        } else if (position == 1) {
                            sharePersonalSpace();
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


    @Override
    protected void loadUserInfo() {
        if (getUserInfo() == null){
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("UserId", userId);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.LOAD_USERINFO_URL,
                params,
                new DefaultListener<UserInfoResult>(UserInfoResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()) {
                            return;
                        }
                        userInfo = getResult().getModel();
                        userInfo.setMemberId(userId);
                        updateUserInfoViews();
                    }
                });
    }

    @Override
    protected void updateUserInfoViews() {
        super.updateUserInfoViews();
        userQrCodeView.setOnClickListener(null);

        if (userInfo == null) {
            return;
        }

        //设置用户名
        if (TextUtils.isEmpty(userRealName)) {
            userNameView.setText("" + getString(R.string.container_string, userName));
        } else {
            userNameView.setText(userRealName + getString(R.string.container_string, userName));
        }

        //设置备注名
        if (!TextUtils.isEmpty(remarkName)) {
            remarkNameTextView.setText(remarkName);
        }
        View view = findViewById(R.id.user_real_name_attr);
        if (view != null) {
        if (!isMyself && userInfo != null && userInfo.isFriend()) {
            remarkLineImageView.setVisibility(View.VISIBLE);
            view.setVisibility(View.VISIBLE);
        } else {
            remarkLineImageView.setVisibility(View.GONE);
            view.setVisibility(View.GONE);
        }
        }

        //设置性别
        String sex = null;
        sex = userInfo.getSex();
        if (!TextUtils.isEmpty(sex)) {
            if (sex.equals("男")) {
                userQrCodeView.setVisibility(View.VISIBLE);
                userQrCodeView.setImageResource(R.drawable.icon_male);
            } else if (sex.equals("女")) {
                userQrCodeView.setVisibility(View.VISIBLE);
                userQrCodeView.setImageResource(R.drawable.icon_female);
            }
        }

        TextView textView = null;
        view = findViewById(R.id.user_intro_attr);
        if (view != null) {
            //个人介绍
            WebView webview = (WebView) view.findViewById(R.id.web_view);
            //兼容html图文格式
            if (!TextUtils.isEmpty(userInfo.getPIntroduces())){
                if (webview != null) {
                    webview.getSettings().setDefaultFontSize(40);
                    webview.getSettings().setJavaScriptEnabled(true);
                    webview.getSettings().setBuiltInZoomControls(false);
                    webview.getSettings().setDisplayZoomControls(false);
                    webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY); //取消滚动条白边效果
                    webview.setWebChromeClient(new WebChromeClient());
                    webview.setWebViewClient(new WebViewClient());
                    webview.getSettings().setDefaultTextEncodingName("UTF-8");
                    webview.getSettings().setBlockNetworkImage(false);
                    webview.getSettings().setUseWideViewPort(true);
                    webview.getSettings().setLoadWithOverviewMode(true);
                    webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        webview.getSettings().setMixedContentMode(webview.getSettings()
                                .MIXED_CONTENT_ALWAYS_ALLOW);  //注意安卓5.0以上的权限
                    }
                    webview.loadDataWithBaseURL(null, getNewContent(userInfo.getPIntroduces()), "text/html",
                            "UTF-8", null);
                }
            }
        }
        view = findViewById(R.id.user_location_attr);
        if (view != null) {
            textView = (TextView) view.findViewById(R.id.contacts_attribute_value);
            textView.setText(userInfo.getLocation());
        }

        updateSubscribeBar();
    }

    private String getNewContent(String htmlText) {
        Document doc = Jsoup.parse(htmlText);
        Elements elements = doc.getElementsByTag("img");
        for (Element element : elements) {
            element.attr("word-break","break-all");
        }
        return doc.toString();
    }

    private void updateSubscribeBar() {
        if (userInfo == null) {
            return;
        }
        //更新二维码
        qrCodeImageUrl = AppSettings.getFileUrl(userInfo.getQRCode());
        if (qrCodeImageView != null) {
            loadQrCodeImage();
        }
        //首先判断是不是自己
        if (isMyself){

        }else {
            if (userInfo.isFriend()) {
                //已是好友
//            subscribeBar.setVisibility(View.GONE);
//            followBtn.setText(R.string.subscribed);
//            followBtn.setEnabled(false);
//            friendBtn.setText(R.string.alreay_friend);
//            friendBtn.setEnabled(false);
                chatLayout.setVisibility(View.VISIBLE);
            } else if (userInfo.isOnlySubscribed()) {
                //已关注(只关注,不是因为加好友而间接关注)
                subscribeBar.setVisibility(View.VISIBLE);
                followBtn.setText(R.string.cancel_follow);
                followBtn.setEnabled(true);
                followBtn.setSelected(true);
                friendBtn.setText(R.string.add_as_friend);
                friendBtn.setEnabled(true);
                friendBtn.setSelected(false);
            } else {
                //未关注(包含陌生人和自己)
                if (isMyself) {


                } else {
                    subscribeBar.setVisibility(View.VISIBLE);
                    followBtn.setText(R.string.follow);
                    followBtn.setEnabled(true);
                    followBtn.setSelected(false);
                    friendBtn.setText(R.string.add_as_friend);
                    friendBtn.setEnabled(true);
                    friendBtn.setSelected(false);
                }
            }
        }

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
                        if(getActivity() == null) {
                            return;
                        }
                        qrCodeImageView.setImageBitmap(BitmapFactory.decodeFile(qrCodeImagePath));
                    }

                    @Override
                    public void onError(NetroidError error) {
                        if(getActivity() == null) {
                            return;
                        }
                        super.onError(error);
                        TipsHelper.showToast(getActivity(),
                                R.string.picture_download_failed);
                    }
                });
    }

    private void subscribeUser(boolean subscribe) {
        if (userInfo == null) {
            return;
        }
        if (getUserInfo() == null){
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("FAttentionId", getUserInfo().getMemberId());
        params.put("TAttentionId", userId);
        DefaultDataListener<DataModelResult> listener =
                new DefaultDataListener<DataModelResult>(DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        boolean subscribe = ((Boolean) getTarget()).booleanValue();
                        if (getResult() == null || !getResult().isSuccess()) {
                            return;
                        } else {
                            //设置关注点改变
                            setHasFocusChanged(true);
                            TipsHelper.showToast(getActivity(), subscribe ?
                                    R.string.subscribe_success : R.string.subscribe_cancel_success);
                            userInfo.setSubscribed(subscribe);
                            updateSubscribeBar();

                            loadUserInfo();
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        canRequest=true;
                    }
                };
        listener.setTarget(subscribe);
        String serverUrl = subscribe ? ServerUrl.SUBSCRIBE_ADD_PERSON_URL :
                ServerUrl.SUBSCRIBE_REMOVE_PERSON_URL;
        RequestHelper.sendPostRequest(getActivity(), serverUrl, params, listener);
    }

    private void addFriend() {
        if (userInfo == null) {
            return;
        }
        if (getUserInfo() == null){
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("NewFriendId", userId);
        DefaultListener listener = new DefaultListener<ModelResult>(ModelResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                ModelResult result = getResult();
                if (result == null || !result.isSuccess()) {
//                    TipsHelper.showToast(getActivity(),
//                            R.string.friend_request_send_failed);
                    return;
                } else {
//                    userInfo.setFriend(true);
                    updateSubscribeBar();
                    TipsHelper.showToast(getActivity(),
                            R.string.friend_request_send_success);
                }
            }
        };
        listener.setShowLoading(true);
        postRequest(ServerUrl.CONTACTS_ADD_FRIEND_URL, params, listener);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back_btn) {
            finish();
        } else if (v.getId() == R.id.follow_btn) {
            //关注/取消关注
            if (!getMyApplication().hasLogined()){
                ActivityUtils.enterLogin(getActivity());
                return;
            }
            if(canRequest){
                canRequest=false;
                subscribeUser(!v.isSelected());
            }

        } else if (v.getId() == R.id.friend_btn) {
            //加好友
            if (!getMyApplication().hasLogined()){
                ActivityUtils.enterLogin(getActivity());
                return;
            }
            addFriend();
        } else if (v.getId() == R.id.user_detail_btn) {
            //个人详情
            personalDetailInfo();

        } else if (v.getId() == R.id.user_exit_account_btn) {
            //退出账户
            ActivityUtils.exit(getActivity(), true);

        } else if (v.getId() == R.id.delete_friend_btn) {
            //删除好友
            if (!getMyApplication().hasLogined()){
                ActivityUtils.enterLogin(getActivity());
                return;
            }
            showDeleteFriendDialog();

        } else if (v.getId() == R.id.chat_btn) {
            //聊天
            chat();

        } else {
            super.onClick(v);
        }
    }

    private void chat() {
        if (fromWhere == ChatActivity.FROM_CHAT){
            //如果来自聊天页面，直接返回即可。
             if (getActivity() != null){
                 getActivity().finish();
             }
        }else {
            if (!HXSDKHelper.getInstance().isLogined()) {
                TipsHelper.showToast(getActivity(),
                        R.string.chat_service_not_works);
                return;
            }
            String userName = "hx" + userId;
            Bundle args = new Bundle();
            args.putInt(ChatActivity.EXTRA_CHAT_TYPE, ChatActivity.CHATTYPE_SINGLE);
            args.putString(ChatActivity.EXTRA_USER_ID, userName);
            args.putString(ChatActivity.EXTRA_USER_AVATAR, userInfo.getHeaderPic());
            args.putString(ChatActivity.EXTRA_USER_NICKNAME, userInfo.getNickName());
            args.putInt(ChatActivity.EXTRA_FROM_WHERE, ChatActivity.FROM_FRIEND);
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            intent.putExtras(args);
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {

            }
        }
    }

    private void showDeleteFriendDialog() {
        String name = userInfo.getNoteName();
        if (TextUtils.isEmpty(name)) {
            name = userInfo.getNickName();
        }
        ContactsMessageDialog dialog = new ContactsMessageDialog(getActivity(), null,
                getString(R.string.confirm_to_delete_friend, name),
                getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        },
                getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                deleteFriend();
            }
        });
        dialog.show();
    }

    private void deleteFriend() {
        if (getUserInfo() == null){
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("Id", this.userId);
        params.put("VersionCode", 1);
        DefaultListener listener =
                new DefaultListener<ModelResult>(ModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        ModelResult result = getResult();
                        if (result == null || !result.isSuccess()) {
                            return;
                        }
                        //设置删除好友关系标志位
                        setHasUnbindFriendRelationship(true);
                        //删除环信的id
//                        try {
//                            EMClient.getInstance().contactManager().deleteContact("hx"+userId);
//                        } catch (HyphenateException e) {
//                            e.printStackTrace();
//                        }
                        TipsHelper.showToast(getActivity(), getString(R.string.delete_friend_success));
                        finish();
                    }
                };
        listener.setShowLoading(true);
        postRequest(ServerUrl.CONTACTS_REMOVE_FRIEND_URL, params, listener);

    }

    private void personalDetailInfo() {

        Intent intent = new Intent();
        intent.setClass(getActivity(), BasicUserInfoActivity.class);
        intent.putExtra("origin", MyPersonalSpaceFragment.class.getSimpleName());
        intent.putExtra("userInfo", userInfo);
        startActivityForResult(intent, ActivityUtils.REQUEST_CODE_BASIC_USER_INFO);
    }

       public static void setHasFocusChanged(boolean hasFocusChanged) {
        PersonalSpaceFragment.hasFocusChanged = hasFocusChanged;
    }

    public static boolean hasFocusChanged() {
        return hasFocusChanged;
    }

    public static void setHasUnbindFriendRelationship(boolean hasUnbindFriendRelationship) {
        PersonalSpaceFragment.hasUnbindFriendRelationship = hasUnbindFriendRelationship;
    }

    public static boolean hasUnbindFriendRelationship() {
        return hasUnbindFriendRelationship;
    }

    public static void setHasRemarkNameChanged(boolean hasRemarkNameChanged) {
        PersonalSpaceFragment.hasRemarkNameChanged = hasRemarkNameChanged;
    }

    public static boolean hasRemarkNameChanged() {
        return hasRemarkNameChanged;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==ActivityUtils.REQUEST_CODE_BASIC_USER_INFO){
            refreshData();
        }
    }
}
