package com.lqwawa.intleducation.module.user.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
//import com.hyphenate.chat.EMClient;
//import com.hyphenate.chat.EMConversation;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseActivity;
import com.lqwawa.intleducation.base.utils.LogUtil;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.ui.CustomDialog;
//import com.lqwawa.intleducation.module.chat.db.InviteMessgeDao;
//import com.lqwawa.intleducation.module.chat.ui.ChatActivity;
import com.lqwawa.intleducation.module.discovery.ui.TeacherDetailsActivity;
import com.lqwawa.intleducation.module.login.ui.LoginActivity;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.intleducation.module.user.vo.SomebodyInfo;
import com.lqwawa.intleducation.module.user.vo.SomebodyInfoVo;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;
import java.util.Map;

/**
 * Created by XChen on 2016/12/2.
 * email:man0fchina@foxmail.com
 * 他人详情页
 */
public class ContactDetailsActivity extends MyBaseActivity implements View.OnClickListener {
    private static String TAG = "ContactDetailsActivity";
    public static int Rs_delete_friend = 1036;

    private TopBar topBar;
    //头像
    private ImageView imageViewUserHead;
    //姓名
    private TextView textViewName;
    //跳转到老师主页 默认隐藏
    private TextView textViewSeeTeacherPage;
    //个人介绍/老师介绍 标题
    private TextView textViewIntroTitle;
    //个人介绍/老师介绍 内容
    private TextView textViewIntro;
    //所在地 默认invisible
    private View layCity;
    //所在地
    private TextView textViewCity;
    //聊天按钮 /加好友
    private Button buttonChat;
    //删除好友按钮
    private Button buttonDeleteFriend;

    private boolean isFriend = false;
    private SomebodyInfoVo somebodyInfoVo;

    private ImageOptions imageOptions;

    public static void start(Activity activity, String id, boolean isComeFromChat) {
        activity.startActivity(new Intent(activity, ContactDetailsActivity.class)
                .putExtra("id", id)
                .putExtra("isComeFromChat", isComeFromChat));
    }

    public static void startForResult(Activity activity, String id, boolean isComeFromChat) {
        activity.startActivityForResult(new Intent(activity, ContactDetailsActivity.class)
                .putExtra("id", id)
                .putExtra("isComeFromChat", isComeFromChat), Rs_delete_friend);
    }

    public static void startForResult(Activity activity, String id) {
        if (UserHelper.isLogin()) {
            if (id.equals(UserHelper.getUserId())) {
                return;
            }
        }
        activity.startActivityForResult(new Intent(activity, ContactDetailsActivity.class)
                .putExtra("id", id), Rs_delete_friend);
    }

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);
        topBar = (TopBar) findViewById(R.id.top_bar);
        imageViewUserHead = (ImageView) findViewById(R.id.user_head_iv);
        textViewName = (TextView) findViewById(R.id.name_tv);
        textViewSeeTeacherPage = (TextView) findViewById(R.id.see_teacher_page_tv);
        textViewIntroTitle = (TextView) findViewById(R.id.personal_intro_title_tv);
        textViewIntro = (TextView) findViewById(R.id.personal_intro_tv);
        layCity = findViewById(R.id.city_layout);
        textViewCity = (TextView) findViewById(R.id.city_tv);
        buttonChat = (Button) findViewById(R.id.chat_bt);
        buttonDeleteFriend = (Button) findViewById(R.id.delete_friend_bt);
        id = getIntent().getStringExtra("id");
        initViews();
    }

    private void initViews() {
        imageOptions = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setRadius(16)
                .setCrop(false)
                .setLoadingDrawableId(R.drawable.contact_head_def)//加载中默认显示图片
                .setFailureDrawableId(R.drawable.contact_head_def)//加载失败后默认显示图片
                .build();
        topBar.setBack(true);
        textViewSeeTeacherPage.setOnClickListener(this);
        buttonChat.setOnClickListener(this);
        buttonDeleteFriend.setOnClickListener(this);
        topBar.setTitle(getResources().getString(R.string.personal_info));
        if (getIntent().getBooleanExtra("isComeFromChat", false)) {
            buttonChat.setVisibility(View.GONE);
        }
        getData();
    }


    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.see_teacher_page_tv) {//跳转到老师的主页
            if (somebodyInfoVo != null) {
                if (somebodyInfoVo.getId() != null) {
                    TeacherDetailsActivity.start(activity, somebodyInfoVo.getId());
                }
            }
        }else if(view.getId() == R.id.chat_bt) {//聊天/加为好友
            if (somebodyInfoVo != null) {
                if (isFriend) {
                    if (getIntent().getBooleanExtra("isComeFromChat", false)) {
                        finish();
                    } else {
//                        ChatActivity.start(activity, false,
//                                somebodyInfoVo.getHxAccount(),
//                                somebodyInfoVo.getName(), true);
                    }
                } else {
                    SendFriendRequestActivity.start(activity, somebodyInfoVo.getId());
                }
            }
        }else if(view.getId() == R.id.delete_friend_bt) {//删除好友
            if (somebodyInfoVo != null) {
                if (isFriend) {
                    deleteFriend();
                }
            }
        }
    }

    private void deleteFriend() {
        CustomDialog.Builder builder = new CustomDialog.Builder(activity);
        builder.setMessage(activity.getResources().getString(R.string.delete) +
                activity.getResources().getString(R.string.friend)
                + "?");
        builder.setTitle(activity.getResources().getString(R.string.tip));
        builder.setPositiveButton(activity.getResources().getString(R.string.delete),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        doDeleteFriend();
                    }
                });

        builder.setNegativeButton(activity.getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }

    private void doDeleteFriend() {
        if (!UserHelper.isLogin()) {//未登录状态 跳转到登陆界面
            LoginActivity.loginForResult(this);
            return;
        }
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("friendId", somebodyInfoVo.getId());
        LogUtil.d(TAG, requestVo.getParams());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.DeleteFriend + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                ResponseVo<String> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<String>>() {
                        });
                if (result.getCode() == 0) {
                    ToastUtil.showToast(activity, activity.getResources().getString(R.string.delete)
                            + activity.getResources().getString(R.string.friend)
                            + getResources().getString(R.string.success)
                            + "!");
//                    Map<String, EMConversation> newMsgs =
//                            EMClient.getInstance().chatManager().getAllConversations();
//                    for (EMConversation conversation : newMsgs.values()) {
//                        LogUtil.e("test", conversation.getUserName() + ":" + somebodyInfoVo.getHxAccount());
//                        if (conversation.getUserName().equals(somebodyInfoVo.getHxAccount())) {
//                            try {
//                                // delete conversation
//                                EMClient.getInstance().chatManager().deleteConversation(conversation.conversationId(), true);
//                                InviteMessgeDao dao = new InviteMessgeDao(activity);
//                                dao.deleteMessage(conversation.conversationId());
//                                sendBroadcast(new Intent().setAction(AppConfig.ServerUrl.DealFriendRequest));
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
                    setResult(Activity.RESULT_OK);
                    finish();
                } else {
                    ToastUtil.showToast(activity, activity.getResources().getString(R.string.delete)
                            + activity.getResources().getString(R.string.friend)
                            + getResources().getString(R.string.failed)
                            + result.getMessage());
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "删除:" + throwable.getMessage());
                ToastUtil.showToast(activity, getResources().getString(R.string.net_error_tip));
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private void getData() {
        final RequestVo requestVo = new RequestVo();
        requestVo.addParams("userId", id);
        LogUtil.d(TAG, requestVo.getParams());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetUserInfoById + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                closeProgressDialog();
                ResponseVo<SomebodyInfo> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<SomebodyInfo>>() {
                        });
                if (result.getCode() == 0) {
                    isFriend = result.getData().isIsFriend();
                    if (result.getData().getUser() != null &&
                            result.getData().getUser().size() > 0) {
                        somebodyInfoVo = result.getData().getUser().get(0);
                        updateViews();
                    }
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                closeProgressDialog();
                LogUtil.d(TAG, "获取个人详情失败:" + throwable.getMessage());
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private void updateViews() {
        if (somebodyInfoVo != null) {
            textViewName.setText("" + somebodyInfoVo.getName());
            textViewIntro.setText(somebodyInfoVo.getIntroduction());
            textViewCity.setText(somebodyInfoVo.getLevelName() + "");
            x.image().bind(imageViewUserHead,
                    ("" + somebodyInfoVo.getThumbnail()).trim(),
                    imageOptions);
            if (somebodyInfoVo.getUserType() == 2) {//学生
                layCity.setVisibility(View.VISIBLE);
                textViewSeeTeacherPage.setVisibility(View.GONE);
                textViewIntroTitle.setText(getResources().getString(R.string.personal_introduction));
            } else {//老师
                layCity.setVisibility(View.INVISIBLE);
                textViewSeeTeacherPage.setVisibility(View.VISIBLE);
                textViewIntroTitle.setText(getResources().getString(R.string.teacher)
                        + getResources().getString(R.string.introduction)
                        + "：");
            }
            buttonChat.setVisibility(View.VISIBLE);
            if (isFriend) {
                buttonChat.setText(getResources().getString(R.string.chat));
                buttonDeleteFriend.setVisibility(View.VISIBLE);
                buttonDeleteFriend.setText(getResources().getString(R.string.delete_friend));
            } else {
                buttonChat.setText(getResources().getString(R.string.add_as_friend));
                buttonDeleteFriend.setVisibility(View.GONE);
            }
            if (UserHelper.isLogin()) {
                if (id.equals(UserHelper.getUserId())) {
                    buttonDeleteFriend.setVisibility(View.GONE);
                    buttonChat.setVisibility(View.GONE);
                }
            }
        } else {
            buttonDeleteFriend.setVisibility(View.GONE);
            buttonChat.setVisibility(View.GONE);
        }
    }
}
