package com.lqwawa.intleducation.module.discovery.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.MainApplication;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseActivity;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.utils.ActivityUtil;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.LogUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.StringCallback;
import com.lqwawa.intleducation.factory.data.entity.JoinClassEntity;
import com.lqwawa.intleducation.factory.data.entity.tutorial.TutorChoiceEntity;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.intleducation.factory.event.EventWrapper;
import com.lqwawa.intleducation.lqpay.LqPay;
import com.lqwawa.intleducation.lqpay.PayParams;
import com.lqwawa.intleducation.lqpay.callback.OnPayInfoRequestListener;
import com.lqwawa.intleducation.lqpay.callback.OnPayResultListener;
import com.lqwawa.intleducation.lqpay.enums.PayWay;
import com.lqwawa.intleducation.module.discovery.ui.coin.UserParams;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailParams;
import com.lqwawa.intleducation.module.discovery.ui.order.LQCourseOrderActivity;
import com.lqwawa.intleducation.module.learn.ui.MyCourseDetailsActivity;
import com.lqwawa.intleducation.module.onclass.OnlineClassRole;
import com.lqwawa.intleducation.module.onclass.detail.notjoin.ClassDetailActivity;
import com.lqwawa.intleducation.module.tutorial.marking.choice.TutorChoiceParams;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.intleducation.module.user.vo.UserInfoVo;
import com.osastudio.common.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;


public class PayActivity extends MyBaseActivity implements View.OnClickListener, CommonDialogFragment.WaWaPayListener {

    private static final String ACTIVITY_BUNDLE_OBJECT = "ACTIVITY_BUNDLE_OBJECT";
    private static final String ACTIVITY_BUNDLE_OBJECT1 = "ACTIVITY_BUNDLE_OBJECT1";
    private static final String ACTIVITY_BUNDLE_OBJECT2 = "ACTIVITY_BUNDLE_OBJECT2";

    private TopBar mTopBar;
    private RadioButton mPaywayAlipayBtn;
    private LinearLayout mPaywayAlipay;
    private RadioButton mPaywayWechatpayBtn;
    private LinearLayout mPaywayWechatpay;
    private RadioButton mPaywayUppayBtn;
    private LinearLayout mPaywayUppay;
    private TextView mNeedPayTv;
    private TextView mCommitTv;
    private PayWay mPayWay = PayWay.WaWa;
    private static final String KEY_COURSEID = "courseId";
    private static final String KEY_CLASSID = "KEY_CLASSID";
    private static final String KEY_ORDERID = "orderId";
    private static final String KEY_PRICE = "price";
    private static final String KEY_COURSENAME = "coursename";
    private static final String KEY_IS_LIVE = "isLive";
    private static final String KEY_IS_ONLINE = "KEY_IS_ONLINE";
    private static final String KEY_EXTRA_LQWAWA_ENTER = "KEY_EXTRA_LQWAWA_ENTER";
    // 购买人的ID
    private static final String KEY_EXTRA_BUYER_MEMBER_ID = "KEY_EXTRA_BUYER_MEMBER_ID";
    // 是否是学程按章购买
    private static final String KEY_EXTRA_CHAPTER_BUY_ENTER = "KEY_EXTRA_CHAPTER_BUY_ENTER";
    private static final String KEY_TUTOR_CHOICE_ENTER = "KEY_TUTOR_CHOICE_ENTER";
    private String mPrice;
    private String mCourseId, mOrderId;
    private String mClassId;
    private View mPayresultView;
    private LinearLayout mLlPayway;
    private LinearLayout mLlBtn;
    private RadioButton mPaywayActCodeBtn;
    private LinearLayout mPaywayActivationCode;
    public static final int REQUESTCODE = 101;
    private LinearLayout mPayWawapay;
    private RadioButton mPayWawapayBtn;
    private String currentCoins;
    // 是否是在线课堂支付
    private boolean isOnline;
    // 是否两栖蛙蛙调用
    private boolean isLQwawaEnter;
    // 是否是学程章节购买
    private boolean isChapterBuy;
    // 入口类型
    private CourseDetailParams mDetailParams;
    // 购买人的Id
    private String mBuyerMemberId;
    //是否选中老师界面跳转
    private boolean isTutorChoiceEnter;
    //帮辅老师选中的参数
    private TutorChoiceEntity mChoiceEntity;
    private TutorChoiceParams mParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        initView();
    }

    private void initView() {
        mCourseId = getIntent().getStringExtra(KEY_COURSEID);
        mClassId = getIntent().getStringExtra(KEY_CLASSID);
        mOrderId = getIntent().getStringExtra(KEY_ORDERID);
        mPrice = getIntent().getStringExtra(KEY_PRICE);
        isOnline = getIntent().getBooleanExtra(KEY_IS_ONLINE, false);
        isLQwawaEnter = getIntent().getBooleanExtra(KEY_EXTRA_LQWAWA_ENTER, false);
        isChapterBuy = getIntent().getBooleanExtra(KEY_EXTRA_CHAPTER_BUY_ENTER, false);
        mBuyerMemberId = getIntent().getStringExtra(KEY_EXTRA_BUYER_MEMBER_ID);
        if (getIntent().hasExtra(ACTIVITY_BUNDLE_OBJECT)) {
            mDetailParams = (CourseDetailParams) getIntent().getSerializableExtra(ACTIVITY_BUNDLE_OBJECT);
        }
        isTutorChoiceEnter = getIntent().getBooleanExtra(KEY_TUTOR_CHOICE_ENTER, false);
        if (getIntent().hasExtra(ACTIVITY_BUNDLE_OBJECT1)) {
            mChoiceEntity = (TutorChoiceEntity) getIntent().getSerializableExtra(ACTIVITY_BUNDLE_OBJECT1);
        }
        if (getIntent().hasExtra(ACTIVITY_BUNDLE_OBJECT2)) {
            mParams = (TutorChoiceParams) getIntent().getSerializableExtra(ACTIVITY_BUNDLE_OBJECT2);
        }
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        // mTopBar.setBack(true);
        mTopBar.setTitle(getResources().getString(R.string.pay_way));
        // 设置点击事件
        mTopBar.setLeftFunctionImage1(R.drawable.ic_back_green, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPayOk) {
                    ActivityUtil.finishActivity(LQCourseOrderActivity.class);
                    // 购买课程后,关闭未加入课程详情页
                    if (TextUtils.equals(UserHelper.getUserId(), mBuyerMemberId)) {
                        // 是自己买
                        LocalBroadcastManager.getInstance(UIUtil.getContext()).sendBroadcast(new Intent(CourseDetailsActivity.LQWAWA_PAY_RESULT_ACTION));
                    }
                }
                finish();
            }
        });


        mPaywayAlipayBtn = (RadioButton) findViewById(R.id.payway_alipay_btn);
        mPaywayAlipay = (LinearLayout) findViewById(R.id.payway_alipay);
        mPaywayAlipay.setOnClickListener(this);

        mPaywayWechatpayBtn = (RadioButton) findViewById(R.id.payway_wechatpay_btn);
        mPaywayWechatpay = (LinearLayout) findViewById(R.id.payway_wechatpay);
        mPaywayWechatpay.setOnClickListener(this);

        mPaywayUppayBtn = (RadioButton) findViewById(R.id.payway_uppay_btn);
        mPaywayUppay = (LinearLayout) findViewById(R.id.payway_uppay);
        mPaywayUppay.setOnClickListener(this);

        mPayWawapay = (LinearLayout) findViewById(R.id.payway_wawa);
        mPayWawapayBtn = (RadioButton) findViewById(R.id.payway_wawa_btn);
        mPayWawapay.setOnClickListener(this);

        mLlPayway = (LinearLayout) findViewById(R.id.ll_payway);
        mLlBtn = (LinearLayout) findViewById(R.id.ll_btn);

        mPaywayActCodeBtn = (RadioButton) findViewById(R.id.payway_act_code_btn);
        mPaywayActivationCode = (LinearLayout) findViewById(R.id.payway_activation_code);
        mPaywayActivationCode.setOnClickListener(this);

        mNeedPayTv = (TextView) findViewById(R.id.need_pay_tv);
        mCommitTv = (TextView) findViewById(R.id.commit_tv);
        mCommitTv.setOnClickListener(this);
        mNeedPayTv.setText(new StringBuffer().append("¥").append(mPrice));

        if (isOnline || isChapterBuy || !TextUtils.equals(UserHelper.getUserId(), mBuyerMemberId) || isTutorChoiceEnter) {
            // 在线课堂和LQ学程章节购买,替别人购买都关闭激活码购买 帮辅选择老师支付
            mPaywayActivationCode.setVisibility(View.GONE);
        } else {
            mPaywayActivationCode.setVisibility(View.VISIBLE);
        }

        if (isTutorChoiceEnter) {
            //帮辅选择老师支付
            mPaywayWechatpay.setVisibility(View.GONE);
            mPaywayAlipay.setVisibility(View.GONE);
        } else {
            mPaywayWechatpay.setVisibility(View.VISIBLE);
            mPaywayAlipay.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.commit_tv) {//立即支付
            doPay();
        } else if (i == R.id.payway_alipay) {
            updateView(true, false, false, false, false, PayWay.ALiPay);
        } else if (i == R.id.payway_wechatpay) {
            updateView(false, true, false, false, false, PayWay.WechatPay);
        } else if (i == R.id.payway_uppay) {
            updateView(false, false, true, false, false, PayWay.UPPay);
        } else if (i == R.id.payway_activation_code) {
            updateView(false, false, false, true, false, PayWay.Code);
        } else if (i == R.id.payway_wawa) {
            updateView(false, false, false, false, true, PayWay.WaWa);
        }
    }

    private void updateView(boolean alipay, boolean wechatpay, boolean uppay, boolean code, boolean wawa, PayWay payWay) {
        mPaywayAlipayBtn.setChecked(alipay);
        mPaywayWechatpayBtn.setChecked(wechatpay);
        mPaywayUppayBtn.setChecked(uppay);
        mPaywayActCodeBtn.setChecked(code);
        mPayWawapayBtn.setChecked(wawa);
        mPayWay = payWay;
    }

    /**
     * 支付请求
     */
    private void doPay() {

        if (mPayWay == PayWay.Code) {
            ActiveCodeActivity.newInstance(mOrderId, mPrice, getIntent().getStringExtra(KEY_COURSENAME),
                    getIntent().getBooleanExtra(KEY_IS_LIVE, false), this);
            return;
        }

        if (mPayWay == PayWay.WaWa) {
            //蛙蛙币支付
            getWaWaCoins();

            return;
        }

        PayParams params;
        if (mPayWay == PayWay.WechatPay) {
            params = new PayParams.Builder(this)
                    .wechatAppID(AppConfig.WEIXIN_APPID)
                    .payWay(mPayWay)
                    .orderId(mOrderId)
                    .memberId(UserHelper.getUserId())
                    .build();
        } else {
            params = new PayParams.Builder(this)
                    .payWay(mPayWay)
                    .orderId(mOrderId)
                    .memberId(UserHelper.getUserId())
                    .build();
        }

        LqPay.newInstance(params).requestPayInfo(new OnPayInfoRequestListener() {
            @Override
            public void onPayInfoRequetStart() {
                // TODO 在此处做一些loading操作,progressbar.show();
                showProgressDialog("");
            }

            @Override
            public void onPayInfoRequstSuccess(String result) {
                // TODO 可以将loading状态去掉了。请求预支付信息成功，开始跳转到客户端支付。
                closeProgressDialog();
            }

            @Override
            public void onPayInfoRequestFailure() {
                // / TODO 可以将loading状态去掉了。获取预支付信息失败，会同时得到一个支付失败的回调。可以将loading状态去掉了。
                closeProgressDialog();
            }
        }).toPay(new OnPayResultListener() {
            @Override
            public void onPaySuccess(PayWay payWay) {
                // 支付成功
                LogUtils.logd("pay", " payWay == " + payWay.toString());
                initResultView(true);
                if (isOnline) {
                    // 在线班级支付成功
                    EventBus.getDefault().post(new EventWrapper(null, EventConstant.JOIN_IN_CLASS_EVENT));
                }
            }

            @Override
            public void onPayCancel(PayWay payWay) {
                // 支付流程被用户中途取消
                LogUtils.logd("pay", " payWay == " + payWay.toString());
                ToastUtil.showToast(PayActivity.this, getString(R.string.cancel_pay_result));
                //                initResultView(false);
            }

            @Override
            public void onPayFailure(PayWay payWay, int errCode) {
                // 支付失败，
                //                ToastUtil.showToast(PayActivity.this,
                //                        "errorCode = " + errCode + "   payWay = " + payWay.toString());
                LogUtils.logd("pay", " payWay == " + payWay.toString() + "  errCode ==  " + errCode);
                initResultView(false);
            }
        });
    }

    /**
     * 获取蛙蛙币
     */
    private void getWaWaCoins() {

        RequestVo requestVo = new RequestVo();

        UserInfoVo userInfo = UserHelper.getUserInfo();
        requestVo.addParams("token", userInfo.getToken());
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GET_USER_COINS_COUNT + requestVo.getParams());

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String jsonString) {
                if (TextUtils.isEmpty(jsonString)) {
                    return;
                }

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(jsonString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                int code = jsonObject.optInt("code");

                if (code == 0) {
                    //请求成功
                    JSONObject jsonCoins = jsonObject.optJSONObject("data");

                    if (jsonCoins != null) {
                        currentCoins = jsonCoins.optString("amount");

                    } else {
                        currentCoins = "0";
                    }
                    //舍掉小数取整
                    int pr = (int) Math.floor(Double.parseDouble(mPrice));
                    CommonDialogFragment dialogFragment = CommonDialogFragment.newInstance(Integer.valueOf(currentCoins), pr);
                    dialogFragment.setOnWaWaPayListener(PayActivity.this);
                    dialogFragment.show(getFragmentManager(), "wawa");
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {

            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    // 是否支付成功
    private boolean isPayOk;

    /**
     * 展示支付结果回调界面
     *
     * @param payOk
     */
    private void initResultView(boolean payOk) {
        if (payOk && !isOnline && !getIntent().getBooleanExtra("isLive", false)) {
            // LQ学程
            sendBroadcast(new Intent().setAction(AppConfig.ServerUrl.joinInCourse));
        }
        //销毁提交订单页
        MainApplication.getInstance().finishActivity(ConfirmOrderActivity.class);
        mLlPayway.setVisibility(View.GONE);
        mLlBtn.setVisibility(View.GONE);
        mTopBar.setTitle("");
        if (mPayresultView == null) {
            ViewStub viewStub = (ViewStub) findViewById(R.id.pay_result_view);
            mPayresultView = viewStub.inflate();
        } else {
            mPayresultView.setVisibility(View.VISIBLE);
        }
        ImageView imageview = (ImageView) mPayresultView.findViewById(R.id.pay_iv_result);
        TextView tvResult = (TextView) mPayresultView.findViewById(R.id.pay_tv_result);
        TextView learnBtn = (TextView) mPayresultView.findViewById(R.id.pay_tolearn_btn);
        TextView shopBtn = (TextView) mPayresultView.findViewById(R.id.pay_goshop);

        String memberId = UserHelper.getUserId();
        if (!TextUtils.equals(mBuyerMemberId, memberId)) {
            // 两个Id不相同,说明给别人买的
            // 隐藏去学习
            learnBtn.setVisibility(View.GONE);
        } else {
            learnBtn.setVisibility(View.VISIBLE);
        }


        if (payOk) {//支付成功
            isPayOk = true;
            imageview.setImageResource(R.drawable.pay_result_ok);
            tvResult.setText(R.string.pay_ok);
        } else {//支付失败
            imageview.setImageResource(R.drawable.pay_result_failure);
            tvResult.setText(R.string.pay_failure);
            learnBtn.setVisibility(View.GONE);
        }

        if (isLQwawaEnter && payOk) {
            Intent intent = new Intent();
            intent.setAction("buy_success");
            sendBroadcast(intent);
        }

        learnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2017/7/19 去学习
                if (isOnline) {
                    ClassDetailActivity.show(activity, mClassId);
                    if (TextUtils.equals(UserHelper.getUserId(), mBuyerMemberId)) {
                        // 是自己买
                        LocalBroadcastManager mManager = LocalBroadcastManager.getInstance(UIUtil.getContext());
                        mManager.sendBroadcast(new Intent().setAction(ClassDetailActivity.LQWAWA_PAY_RESULT_ACTION));
                    }

                    finish();

                    // 发送获取班级详情细信息的请求
                    /*OnlineCourseHelper.loadOnlineClassInfo(UserHelper.getUserId(), mClassId, new DataSource.Callback<JoinClassEntity>() {
                        @Override
                        public void onDataNotAvailable(int strRes) {
                            UIUtil.showToastSafe(strRes);
                        }

                        @Override
                        public void onDataLoaded(JoinClassEntity joinClassEntity) {
                            // 进行验证
                            if(!EmptyUtil.isEmpty(joinClassEntity)){
                                if(TextUtils.equals(UserHelper.getUserId(),mBuyerMemberId)){
                                    // 是自己买
                                    LocalBroadcastManager mManager = LocalBroadcastManager.getInstance(UIUtil.getContext());
                                    mManager.sendBroadcast(new Intent().setAction(ClassDetailActivity.LQWAWA_PAY_RESULT_ACTION));
                                }

                                String role = getOnlineClassRoleInfo(joinClassEntity);
                                JoinClassDetailActivity.show(activity,joinClassEntity.getClassId(),joinClassEntity.getSchoolId(),Integer.parseInt(mCourseId),role,false);
                                finish();
                            }
                        }
                    });*/
                } else {
                    if (!getIntent().getBooleanExtra("isLive", false)) {
                        ActivityUtil.finishActivity(CourseDetailsActivity.class);
                        ActivityUtil.finishActivity(LQCourseOrderActivity.class);

                        if (!isLQwawaEnter) {
                            MyCourseDetailsActivity.start(activity, mCourseId,
                                    false, true, UserHelper.getUserId(), false,
                                    false, false, false, mDetailParams, null);
                            if (mPayWay != PayWay.Code)
                                join(mCourseId);
                        }

                    }
                    finish();
                }
            }
        });
        shopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2017/7/19 继续购物
                if (isOnline) {
                    ActivityUtil.finishActivity(ClassDetailActivity.class);
                } else {
                    if (!getIntent().getBooleanExtra("isLive", false)) {
                        // 销毁详情页
                        MainApplication.getInstance().finishActivity(CourseDetailsActivity.class);
                        ActivityUtil.finishActivity(LQCourseOrderActivity.class);
                    }
                }
                finish();
            }
        });
    }

    /**
     * 获取在线课堂角色信息
     *
     * @param entity 数据实体
     * @return 判断顺序 老师->家长->学生
     */
    private String getOnlineClassRoleInfo(@NonNull JoinClassEntity entity) {
        String roles = entity.getRoles();
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


    /**
     * 已有订单去支付
     *
     * @param orderId 订单编号
     * @param price   价格
     * @param context
     */
    public static void newInstance(String orderId, String price, String coursename,
                                   String courseId, boolean isLive, Context context,
                                   @NonNull String curMemberId) {
        Intent starter = new Intent(context, PayActivity.class);
        starter.putExtra(KEY_ORDERID, orderId);
        starter.putExtra(KEY_COURSEID, courseId);
        starter.putExtra(KEY_PRICE, price);
        starter.putExtra(KEY_COURSENAME, coursename);
        starter.putExtra(KEY_IS_LIVE, isLive);
        starter.putExtra(KEY_EXTRA_BUYER_MEMBER_ID, curMemberId);
        context.startActivity(starter);
    }

    /**
     * 在线课堂入口
     */
    public static void newInstance(Context context, String classId,
                                   String orderId, String price, String coursename,
                                   String courseId, @NonNull String mBuyerMemberId) {
        Intent starter = new Intent(context, PayActivity.class);
        starter.putExtra(KEY_ORDERID, orderId);
        starter.putExtra(KEY_CLASSID, classId);
        starter.putExtra(KEY_COURSEID, courseId);
        starter.putExtra(KEY_PRICE, price);
        starter.putExtra(KEY_COURSENAME, coursename);
        starter.putExtra(KEY_IS_ONLINE, true);
        starter.putExtra(KEY_EXTRA_BUYER_MEMBER_ID, mBuyerMemberId);
        context.startActivity(starter);
    }

    /**
     * @desc 两栖蛙蛙调用入口
     */
    public static void newInstance(String orderId, String price, String coursename,
                                   String courseId, boolean isLive, boolean isLQwawaEnter,
                                   boolean isChapterBuy, @NonNull CourseDetailParams params,
                                   Context context,
                                   @NonNull String mBuyerMemberId) {
        Intent starter = new Intent(context, PayActivity.class);
        starter.putExtra(KEY_ORDERID, orderId);
        starter.putExtra(KEY_COURSEID, courseId);
        starter.putExtra(KEY_PRICE, price);
        starter.putExtra(KEY_COURSENAME, coursename);
        starter.putExtra(KEY_IS_LIVE, isLive);
        starter.putExtra(KEY_EXTRA_LQWAWA_ENTER, isLQwawaEnter);
        starter.putExtra(KEY_EXTRA_CHAPTER_BUY_ENTER, isChapterBuy);
        starter.putExtra(ACTIVITY_BUNDLE_OBJECT, params);
        starter.putExtra(KEY_EXTRA_BUYER_MEMBER_ID, mBuyerMemberId);
        context.startActivity(starter);
    }

    /**
     * @des 选择ban
     */
    public static void newInstance(Context context, boolean isTutorChoiceEnter, @NonNull TutorChoiceParams params,
                                   @NonNull TutorChoiceEntity choiceEntity) {
        Intent starter = new Intent(context, PayActivity.class);
        starter.putExtra(KEY_TUTOR_CHOICE_ENTER, isTutorChoiceEnter);
        starter.putExtra(KEY_PRICE, choiceEntity.getMarkingPrice());
        starter.putExtra(ACTIVITY_BUNDLE_OBJECT1, choiceEntity);
        starter.putExtra(ACTIVITY_BUNDLE_OBJECT2, params);
        context.startActivity(starter);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTCODE && resultCode == RESULT_OK) {
            initResultView(true);
        }
    }

    private void join(String courseId) {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("courseId", courseId);
        requestVo.addParams("type", 1);
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.joinInCourse + requestVo.getParams());

        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                ResponseVo<String> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<String>>() {
                        });
                if (result.getCode() == 0) {
                    activity.getApplicationContext().sendBroadcast(new Intent().setAction(AppConfig.ServerUrl.joinInCourse));
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
            }

            @Override
            public void onFinished() {
            }
        });
    }

    @Override
    public void confirm(boolean value) {


        if (value) {
            //余额不足需要充值
            Intent intent = new Intent(this, ChargeCenterActivity.class);
            Bundle bundle = new Bundle();
            UserParams user = UserParams.buildUser(UserHelper.getUserInfo());
            bundle.putSerializable(ChargeCenterActivity.KEY_EXTRA_USER, user);
            intent.putExtras(bundle);
            startActivity(intent);

            return;
        }

        if (isTutorChoiceEnter) {

            UserInfoVo userInfo = UserHelper.getUserInfo();
            int taskId = mParams.getModel().getT_TaskId();
            int taskType = mParams.getModel().getT_TaskType();
            int price = (int) Math.floor(Double.parseDouble(mChoiceEntity.getMarkingPrice()));
            String courseId = mParams.getModel().getT_CourseId();
            String courseName = mParams.getModel().getT_CourseName();
            String memberId = mParams.getMemberId();
            String tutorMemberId = mChoiceEntity.getMemberId();
            String title = mParams.getModel().getTitle();
            RequestVo requestVo = new RequestVo();
            requestVo.addParams("taskId", taskId);
            requestVo.addParams("taskType", taskType);
            requestVo.addParams("token", userInfo.getToken());
            requestVo.addParams("price", price);
            requestVo.addParams("taskName", title);
            requestVo.addParams("title", title);
            requestVo.addParams("memberId", memberId);
            requestVo.addParams("consumeSource", 2);
            if (EmptyUtil.isNotEmpty(courseId) && !courseId.equals("0")) {
                requestVo.addParams("courseId", courseId);
            }
            if (EmptyUtil.isNotEmpty(courseName)) {
                requestVo.addParams("courseName", courseName);
            }
            requestVo.addParams("tutorMemberId", tutorMemberId);
            RequestParams params = new RequestParams(AppConfig.ServerUrl.CreateTutorOrder);
            params.setAsJsonContent(true);
            params.setBodyContent(requestVo.getParams());
            params.setConnectTimeout(10000);
            x.http().post(params, new StringCallback<String>() {
                @Override
                public void onSuccess(String str) {
                    LogUtil.i(PayActivity.class, "request " + params.getUri() + " result :" + str);
                    if (TextUtils.isEmpty(str)) {
                        return;
                    }
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(str);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    int code = jsonObject.optInt("code");
                    int orderId = jsonObject.optInt("orderId");
                    if (code == 0) {
                        // 通过EventBus通知
                        EventBus.getDefault().post(new EventWrapper(orderId, EventConstant.CREATE_TUTOR_ORDER));
                    }
                }

                @Override
                public void onError(Throwable throwable, boolean b) {
                    ToastUtil.showToast(PayActivity.this, R.string.net_error_tip);
                }
            });
        } else {
            String memberId = UserHelper.getUserId();
            RequestVo requestVo = new RequestVo();
            requestVo.addParams("id", mOrderId);
            requestVo.addParams("consumeSource", 2);//表示android手机
            requestVo.addParams("memberId", memberId);
            RequestParams params = new RequestParams(AppConfig.ServerUrl.PAY_USE_WAWA_COIN + requestVo.getParams());

            x.http().get(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String jsonString) {
                    if (TextUtils.isEmpty(jsonString)) {
                        return;
                    }

                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(jsonString);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    int code = jsonObject.optInt("code");

                    if (code == 0) {
                        //请求成功
                        initResultView(true);
                        if (isTutorChoiceEnter) {
                            finish();
                        }
                    } else {
                        UIUtil.showToastSafe(R.string.pay_failure);
                    }

                }

                @Override
                public void onCancelled(CancelledException e) {
                }

                @Override
                public void onError(Throwable throwable, boolean b) {

                }

                @Override
                public void onFinished() {
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isPayOk) {
            ActivityUtil.finishActivity(LQCourseOrderActivity.class);
            // 购买课程后,关闭未加入课程详情页
            if (TextUtils.equals(UserHelper.getUserId(), mBuyerMemberId)) {
                // 是自己买
                LocalBroadcastManager.getInstance(UIUtil.getContext()).sendBroadcast(new Intent(CourseDetailsActivity.LQWAWA_PAY_RESULT_ACTION));
            }
        }
    }
}
