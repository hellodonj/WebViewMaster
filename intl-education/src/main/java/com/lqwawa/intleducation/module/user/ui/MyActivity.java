package com.lqwawa.intleducation.module.user.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
//import com.hyphenate.chat.EMClient;
//import com.hyphenate.chat.EMMessage;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseActivity;
import com.lqwawa.intleducation.base.utils.LogUtil;
import com.lqwawa.intleducation.base.utils.StringUtils;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.TopBar;
//import com.lqwawa.intleducation.module.chat.EaseHelper;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.intleducation.module.user.vo.PersonalInfo;
import com.lqwawa.intleducation.module.user.vo.PersonalInfoVo;
import com.lqwawa.intleducation.module.user.vo.UserInfoVo;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;
import java.util.Date;

/**
 * Created by XChen on 2016/12/2.
 * email:man0fchina@foxmail.com
 * 我的
 */
public class MyActivity extends MyBaseActivity implements View.OnClickListener {
    private static final String TAG = "MyActivity";
    private TopBar topBar;
    private ImageView imageViewCover;//封面
    private ImageView imageViewAvatar;//头像
    private TextView textViewNickname;//昵称
    private LinearLayout layMessage;//消息
    private TextView textViewMessageCount;//未读消息数量
    private LinearLayout layPersonalContacts;//个人通讯录
    private LinearLayout layClasslContacts;//班级通讯录
    private LinearLayout laySchoolsOfConcern; //关注的学校
    private LinearLayout layMyCart;//我的购物车
    private TextView textViewObjCountInCart;//购物车内课程数量
    private LinearLayout layMyOrder;//我的订单
    private TextView textViewOrderCount;//订单数量
    private LinearLayout layMyCollection;//我的收藏
    private TextView textViewCollectionCount;//收藏数量
    private LinearLayout layBindPhone;//绑定手机
    private LinearLayout layFeedback;//意见反馈
    private LinearLayout laySetting;//设置
    PersonalInfoVo personalInfoVo = null;

    //下拉刷新
    private PullToRefreshView pullToRefreshView;
    //数据列表
    private ListView listView;
    //加载失败图片
    private RelativeLayout loadFailedLayout;
    //重新加载
    private Button btnReload;

    private PersonalInfo personalInfo;

    private int img_width;
    private int img_height;
    private ImageOptions imageOptionsAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        topBar = (TopBar) findViewById(R.id.top_bar);
        imageViewCover = (ImageView) findViewById(R.id.cover_iv);//封面
        imageViewAvatar = (ImageView) findViewById(R.id.avatar_iv);//头像
        textViewNickname = (TextView) findViewById(R.id.nickname_tv);//昵称
        layMessage = (LinearLayout) findViewById(R.id.message_layout);//消息
        textViewMessageCount = (TextView) findViewById(R.id.message_count_tv);//未读消息数量
        layPersonalContacts = (LinearLayout) findViewById(R.id.personal_contacts_layout);//个人通讯录
        layClasslContacts = (LinearLayout) findViewById(R.id.class_contacts_layout);//班级通讯录
        laySchoolsOfConcern = (LinearLayout) findViewById(R.id.schools_of_concern_layout); //关注的学校
        layMyCart = (LinearLayout) findViewById(R.id.my_cart_layout);//我的购物车
        textViewObjCountInCart = (TextView) findViewById(R.id.obj_count_in_cart_tv);//购物车内课程数量
        layMyOrder = (LinearLayout) findViewById(R.id.my_order_layout);//我的订单
        textViewOrderCount = (TextView) findViewById(R.id.my_order_count_tv);//订单数量
        layMyCollection = (LinearLayout) findViewById(R.id.my_collection_layout);//我的收藏
        textViewCollectionCount = (TextView) findViewById(R.id.my_collection_count_tv);//收藏数量
        layBindPhone = (LinearLayout) findViewById(R.id.bind_phone_layout);//绑定手机
        layFeedback = (LinearLayout) findViewById(R.id.feedback_layout);//意见反馈
        laySetting = (LinearLayout) findViewById(R.id.setting_layout);//设置
        pullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        listView = (ListView) findViewById(R.id.listView);
        loadFailedLayout = (RelativeLayout) findViewById(R.id.load_failed_layout);
        btnReload = (Button) findViewById(R.id.reload_bt);
        initViews();
        initData();
//        EaseHelper.getInstance().addNewMessageListener(newMessageListener);
    }

    private void initViews() {
        topBar.setBack(true);
        topBar.setTranslationBackground(true);
        imageViewAvatar.setOnClickListener(this);
        layMessage.setOnClickListener(this);
        layPersonalContacts.setOnClickListener(this);
        layClasslContacts.setOnClickListener(this);
        laySchoolsOfConcern.setOnClickListener(this);
        layMyCart.setOnClickListener(this);
        layMyOrder.setOnClickListener(this);
        layMyCollection.setOnClickListener(this);
        layBindPhone.setOnClickListener(this);
        layFeedback.setOnClickListener(this);
        laySetting.setOnClickListener(this);
        btnReload.setOnClickListener(this);

        pullToRefreshView.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                getData();
            }
        });
        pullToRefreshView.setLoadMoreEnable(false);
        pullToRefreshView.setLastUpdated(new Date().toLocaleString());

        int p_width = getWindowManager().getDefaultDisplay().getWidth();
        img_width = p_width;
        img_height = img_width * 315 / 720;
        imageViewCover.setLayoutParams(new FrameLayout.LayoutParams(img_width, img_height));
        imageOptionsAvatar = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setCrop(false)
                .setCircular(true)
                .setLoadingDrawableId(R.drawable.ic_avatar_def)//加载中默认显示图片
                .setFailureDrawableId(R.drawable.ic_avatar_def)//加载失败后默认显示图片
                .build();


    }

    private void initData() {
        pullToRefreshView.showRefresh();
        getData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        EaseHelper.getInstance().removeNewMessageListener(newMessageListener);
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.reload_bt) {
            pullToRefreshView.showRefresh();
            getData();
        }else  if(view.getId() ==R.id.avatar_iv) {
            startActivityForResult(new Intent(activity, PersonalInfoActivity.class)
                            .putExtra("personalInfo", personalInfo),
                    PersonalInfoActivity.REQUEST_CODE_PERSONAL_INFO);
        }else if(view.getId() == R.id.message_layout) {
            startActivityForResult(new Intent(activity, MessageListActivity.class), MessageListActivity.Rs_MessageRead);
        }else if(view.getId() == R.id.personal_contacts_layout) {
            startActivity(new Intent(activity, PersonalContactsActivity.class));
        }else if(view.getId() == R.id.class_contacts_layout){
            startActivity(new Intent(activity, ClassContactsActivity.class));
        }else if(view.getId() == R.id.schools_of_concern_layout) {
            startActivity(new Intent(activity, MyAttentionOrganListActivity.class));
        }else if(view.getId() == R.id.my_cart_layout) {//我的购物车　暂不实现
        }else if(view.getId() == R.id.my_order_layout) {//我的订单
            startActivityForResult(new Intent(activity, MyOrderListActivity.class),
                    MyOrderListActivity.Rs_deleteOrder);
        }else if(view.getId() ==  R.id.my_collection_layout) {//我的收藏
            startActivityForResult(new Intent(activity, MyCollectionListActivity.class),
                    MyOrderListActivity.Rs_deleteOrder);
        }else if(view.getId() == R.id.bind_phone_layout) {//绑定手机 //咱不实现
        }else if(view.getId() == R.id.feedback_layout) {//意见反馈
            startActivity(new Intent(activity, FeedbackActivity.class));
        }else if(view.getId() == R.id.setting_layout) {
            startActivityForResult(new Intent(activity, SettingActivity.class),
                    SettingActivity.REQUEST_CODE_SETTING);
        }
    }

    private void getData() {
        loadFailedLayout.setVisibility(View.GONE);
        RequestVo requestVo = new RequestVo();

        LogUtil.d(TAG, requestVo.getParams().toString());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetPersonalInfo + requestVo.getParams());

        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefreshView.onHeaderRefreshComplete();
                personalInfoVo = JSON.parseObject(s,
                        new TypeReference<PersonalInfoVo>() {
                        });
                if (personalInfoVo != null
                        && personalInfoVo.getUser() != null
                        && personalInfoVo.getUser().size() > 0) {
                    PersonalInfo info = personalInfoVo.getUser().get(0);
                    UserInfoVo userInfo = UserHelper.getUserInfo();
                    userInfo.setThumbnail(info.getThumbnail());
                    userInfo.setUserName(info.getName());
                    UserHelper.setUserInfo(userInfo);
                }
                updateViews();
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "拉取通知列表失败:" + throwable.getMessage());
                loadFailedLayout.setVisibility(View.VISIBLE);
                pullToRefreshView.onHeaderRefreshComplete();
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private void updateViews() {
        updateUnreadMsgCount();
        if (personalInfoVo != null) {
            if (personalInfoVo.getCode() == 0) {
                if (StringUtils.isIntString(personalInfoVo.getOrderCount())) {
                    textViewOrderCount.setText(personalInfoVo.getOrderCount());
                }
                if (StringUtils.isIntString(personalInfoVo.getCollectCount())) {
                    textViewCollectionCount.setText(personalInfoVo.getCollectCount());
                }
                if (personalInfoVo.getUser() != null) {
                    if (personalInfoVo.getUser().size() > 0) {
                        personalInfo = personalInfoVo.getUser().get(0);

                        x.image().bind(imageViewAvatar,
                                personalInfo.getThumbnail().trim(),
                                imageOptionsAvatar);

                        textViewNickname.setText(personalInfo.getName() + "");
                    }
                }
            }
        }
    }

    private void updateUnreadMsgCount() {
        try {
//            int newMsgCount = EMClient.getInstance().chatManager().getUnreadMsgsCount();
//            if (personalInfoVo != null) {
//                newMsgCount += personalInfoVo.getNewNoticeCount();
//            }
//            textViewMessageCount.setText(newMsgCount + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PersonalInfoActivity.REQUEST_CODE_PERSONAL_INFO) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    int newNoticeCount = personalInfoVo.getNewNoticeCount();
                    personalInfoVo = (PersonalInfoVo) data.getSerializableExtra("personalInfo");
                    if (personalInfoVo != null) {
                        personalInfoVo.setNewNoticeCount(newNoticeCount);
                    }
                    updateViews();
                }
            }
        } else if (requestCode == SettingActivity.REQUEST_CODE_SETTING) {
            if (resultCode == Activity.RESULT_OK) {
                initData();
            }
        } else if (requestCode == MessageListActivity.Rs_MessageRead) {
            if (resultCode == Activity.RESULT_OK) {
                initData();
            }
        } else if (requestCode == MyOrderListActivity.Rs_deleteOrder) {
            if (resultCode == Activity.RESULT_OK) {
                initData();
            }
        }
    }

//    private EaseHelper.NewMessageListener newMessageListener = new EaseHelper.NewMessageListener() {
//        @Override
//        public void onReceived(EMMessage message) {
//            activity.runOnUiThread(new Runnable() {
//                public void run() {
//                    updateUnreadMsgCount();
//                }
//            });
//        }
//
//        @Override
//        public void onReaded() {
//            activity.runOnUiThread(new Runnable() {
//                public void run() {
//                    updateUnreadMsgCount();
//                }
//            });
//        }
//    };


}
