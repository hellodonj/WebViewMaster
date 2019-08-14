package com.lqwawa.intleducation.module.discovery.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseActivity;
import com.lqwawa.intleducation.base.utils.LogUtil;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerItemDecoration;
import com.lqwawa.intleducation.common.Common;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.ImageUtil;
import com.lqwawa.intleducation.common.utils.StringUtil;
import com.lqwawa.intleducation.factory.data.entity.ClassDetailEntity;
import com.lqwawa.intleducation.factory.data.entity.PayChapterEntity;
import com.lqwawa.intleducation.lqpay.LqPay;
import com.lqwawa.intleducation.lqpay.PayParams;
import com.lqwawa.intleducation.lqpay.callback.OnPayInfoRequestListener;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailParams;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.PayChapterAdapter;
import com.lqwawa.intleducation.module.discovery.ui.order.OrderChapterAdapter;
import com.lqwawa.intleducation.module.discovery.vo.CourseDetailsVo;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.discovery.vo.LiveDetailsVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.osastudio.common.utils.XImageLoader;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by XChen on 2016/11/13.
 * email:man0fchina@foxmail.com
 * 确认订单
 */
public class ConfirmOrderActivity extends MyBaseActivity implements View.OnClickListener {
    private static final String TAG = "ConfirmOrderActivity";
    private static final String KEY_EXTRA_ENTITY = "KEY_EXTRA_ENTITY";
    private static final String KEY_EXTRA_CHAPTER_ENTITIES = "KEY_EXTRA_CHAPTER_ENTITIES";
    private static final String KEY_EXTRA_SCHOOL_ID = "KEY_EXTRA_SCHOOL_ID";
    private static final String ACTIVITY_BUNDLE_OBJECT = "ACTIVITY_BUNDLE_OBJECT";
    private static final String KEY_EXTRA_BUYER_MEMBER_ID = "KEY_EXTRA_BUYER_MEMBER_ID";
    public static final int Rc_pay = 1024;
    private TopBar topBar;
    private TextView organNameTv;
    private TextView teacherNameTv;
    private ImageView courseIv;
    private ImageView mOnlineCourse;
    private TextView courseName;
    private TextView coursePrice;
    private TextView subtotalTv;
    private TextView needPayTv;
    private TextView commitTv;

    private int img_width;
    private int img_height;
    ImageOptions imageOptions;
    private CourseVo courseVo;
    // 用来统计的SchoolId
    private String mSchoolId;
    // 在线课堂班级数据
    private ClassDetailEntity mClassDetailEntity;
    // LQ学程分章节购买的章节
    private LinearLayout mChapterLayout;
    private LinearLayout mSubTotalLayout;
    private RecyclerView mChapterRecycler;
    private OrderChapterAdapter mAdapter;
    private List<PayChapterEntity> mEntities;
    private boolean isLQwawaEnter;
    private String courseId;
    private FrameLayout coverLay;
    private boolean isLive = false;
    private LiveDetailsVo liveDetailsVo;
    // 当前被购买的MemberId
    private String mCurMemberId;

    // 入口类型
    private CourseDetailParams mDetailParams;

    public static void start(Activity activity, CourseVo vo) {
        activity.startActivityForResult(new Intent(activity, ConfirmOrderActivity.class)
                .putExtra("CourseVo", vo), Rc_pay);
    }

    public static void start(Activity activity, String courseId) {
        activity.startActivityForResult(new Intent(activity, ConfirmOrderActivity.class)
                .putExtra("courseId", courseId), Rc_pay);
    }

    public static void start(Activity activity, LiveDetailsVo liveDetailsVo) {
        activity.startActivityForResult(new Intent(activity, ConfirmOrderActivity.class)
                .putExtra("LiveDetailsVo", liveDetailsVo)
                .putExtra("IsLive", true), Rc_pay);
    }

    /**
     * Mooc 分章节购买的订单入口
     * @param activity 上下文对象
     * @param vo 课程数据
     * @param schoolId 购买课程所属的机构 其实可以不用传，通过Vo去拿
     * @param entities 购买的章节数据
     * @param isLQwawaEnter 是否两栖蛙蛙进入
     * @param params 课程详情入口类型
     */
    public static void start(@NonNull Activity activity,
                             @NonNull CourseVo vo,
                             @NonNull String schoolId,
                             @NonNull ArrayList<PayChapterEntity> entities,
                             @NonNull boolean isLQwawaEnter,
                             @NonNull CourseDetailParams params,
                             @NonNull String curMemberId){
        Intent intent = new Intent(activity, ConfirmOrderActivity.class);
        intent.putExtra("CourseVo", vo);
        intent.putExtra("isLQwawaEnter",isLQwawaEnter);
        intent.putExtra(ACTIVITY_BUNDLE_OBJECT,params);
        intent.putExtra(KEY_EXTRA_SCHOOL_ID,schoolId);
        intent.putExtra(KEY_EXTRA_BUYER_MEMBER_ID,curMemberId);
        if(EmptyUtil.isNotEmpty(entities)){
            intent.putExtra(KEY_EXTRA_CHAPTER_ENTITIES,entities);
        }
        activity.startActivityForResult(intent, Rc_pay);
    }

    /**
     * 购买在线课堂
     *
     * @param activity 上下文对象
     * @param entity   数据实体
     */
    public static void start(Activity activity,
                             @NonNull ClassDetailEntity entity,
                             @NonNull String curMemberId) {
        activity.startActivityForResult(new Intent(activity, ConfirmOrderActivity.class)
                .putExtra("KEY_EXTRA_ENTITY", entity)
                .putExtra("IsLive", false)
                .putExtra(KEY_EXTRA_BUYER_MEMBER_ID,curMemberId), Rc_pay);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        topBar = (TopBar) findViewById(R.id.top_bar);
        organNameTv = (TextView) findViewById(R.id.organ_name_tv);
        teacherNameTv = (TextView) findViewById(R.id.teacher_name_tv);
        courseIv = (ImageView) findViewById(R.id.course_iv);
        mOnlineCourse = (ImageView) findViewById(R.id.iv_online_course);
        courseName = (TextView) findViewById(R.id.course_name);
        coursePrice = (TextView) findViewById(R.id.course_price);
        subtotalTv = (TextView) findViewById(R.id.subtotal_tv);
        needPayTv = (TextView) findViewById(R.id.need_pay_tv);
        commitTv = (TextView) findViewById(R.id.commit_tv);
        coverLay = (FrameLayout) findViewById(R.id.cover_lay);
        courseVo = (CourseVo) getIntent().getSerializableExtra("CourseVo");
        mEntities = (List<PayChapterEntity>) getIntent().getSerializableExtra(KEY_EXTRA_CHAPTER_ENTITIES);
        mSchoolId = getIntent().getStringExtra(KEY_EXTRA_SCHOOL_ID);
        courseId = getIntent().getStringExtra("courseId");
        mClassDetailEntity = (ClassDetailEntity) getIntent().getSerializableExtra(KEY_EXTRA_ENTITY);
        liveDetailsVo = (LiveDetailsVo) getIntent().getSerializableExtra("LiveDetailsVo");
        isLive = getIntent().getBooleanExtra("IsLive", false);
        isLQwawaEnter = getIntent().getBooleanExtra("isLQwawaEnter",false);
        mDetailParams = (CourseDetailParams) getIntent().getSerializableExtra(ACTIVITY_BUNDLE_OBJECT);
        mCurMemberId = getIntent().getStringExtra(KEY_EXTRA_BUYER_MEMBER_ID);

        // 只有当按章节购买的时候,才会有
        mChapterLayout = (LinearLayout) findViewById(R.id.chapter_layout);
        mSubTotalLayout = (LinearLayout) findViewById(R.id.sub_total_layout);
        mChapterRecycler = (RecyclerView) findViewById(R.id.recycler);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mChapterRecycler.setLayoutManager(layoutManager);
        mAdapter = new OrderChapterAdapter();
        mChapterRecycler.setAdapter(mAdapter);
        mChapterRecycler.addItemDecoration(new RecyclerItemDecoration(this,RecyclerItemDecoration.VERTICAL_LIST));


        ImageView imageViewAngleLive = (ImageView) findViewById(R.id.angle_live_iv);
        initViews();

        if (isLive) {
            imageViewAngleLive.setVisibility(View.VISIBLE);
            updateView();
        } else if (courseVo != null) {
            courseId = courseVo.getId();
            teacherNameTv.setVisibility(View.GONE);
            if(EmptyUtil.isNotEmpty(mEntities)){
                mChapterLayout.setVisibility(View.VISIBLE);
                mSubTotalLayout.setVisibility(View.GONE);
                // 按章节购买的
                mAdapter.replace(mEntities);
            }else{
                mChapterLayout.setVisibility(View.GONE);
            }
            updateView();
        } else if (courseId != null) {
            teacherNameTv.setVisibility(View.GONE);
            initData();
        } else if (mClassDetailEntity != null) {
            teacherNameTv.setVisibility(View.VISIBLE);
            updateView();
        } else {
            ToastUtil.showToast(activity, getResources().getString(R.string.data_is_empty));
            finish();
        }
    }

    private void initData() {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("dataType", 1);
        requestVo.addParams("id", courseId);
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetCourseDetailsById + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                CourseDetailsVo courseDetailsVo = JSON.parseObject(s,
                        new TypeReference<CourseDetailsVo>() {
                        });
                if (courseDetailsVo.getCode() == 0) {
                    List<CourseVo> voList = courseDetailsVo.getCourse();
                    if (voList != null && voList.size() > 0) {
                        courseVo = voList.get(0);
                        updateView();
                    }
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "获取课程详情失败:type" + ",msg:" + throwable.getMessage());

            }

            @Override
            public void onFinished() {
            }
        });
    }

    private void initViews() {
        //初始化顶部工具条
        topBar.setBack(true);
        topBar.showBottomSplitView(false);
        commitTv.setOnClickListener(this);
        int p_width = activity.getWindowManager().getDefaultDisplay().getWidth();
        img_width = p_width / 4;
        img_height = img_width * 297 / 210;

        imageOptions =  XImageLoader.buildImageOptions(ImageView.ScaleType.CENTER_CROP,
                R.drawable.default_cover_h, false, false, null, img_width, img_height);
    }

    private void updateView() {
        if (isLive) {
            coursePrice.setText(new StringBuffer().append("¥")
                    .append(liveDetailsVo.getLive().getPrice()));
            courseName.setText(liveDetailsVo.getLive().getTitle());
            organNameTv.setText(liveDetailsVo.getLive().getSchoolName());
            teacherNameTv.setText(liveDetailsVo.getLive().getCreateName());
            subtotalTv.setText(new StringBuffer().append("¥")
                    .append(liveDetailsVo.getLive().getPrice()));
            needPayTv.setText(new StringBuffer().append("¥")
                    .append(liveDetailsVo.getLive().getPrice()));
            topBar.setTitle(getResources().getString(R.string.order_detail));

            courseIv.setVisibility(View.VISIBLE);
            mOnlineCourse.setVisibility(View.GONE);
            XImageLoader.loadImage(courseIv,
                    liveDetailsVo.getLive().getCoverUrl().trim(),
                    imageOptions);

            coverLay.setLayoutParams(new LinearLayout.LayoutParams(img_width, img_height));
        } else if (courseVo != null) {
            courseId = courseVo.getId();
            coursePrice.setText(new StringBuffer().append("¥").append(courseVo.getPrice()));
            courseName.setText(courseVo.getName());
            organNameTv.setText(courseVo.getOrganName());
            teacherNameTv.setText(courseVo.getTeachersName());
            subtotalTv.setText(new StringBuffer().append("¥").append(courseVo.getPrice()));
            if(EmptyUtil.isNotEmpty(mEntities)){
                // 按章节购买
                needPayTv.setText(new StringBuffer().append("¥").append(getPrice()));
            }else{
                // 全部购买
                needPayTv.setText(new StringBuffer().append("¥").append(courseVo.getPrice()));

            }
            topBar.setTitle(getResources().getString(R.string.order_detail));

            courseIv.setVisibility(View.VISIBLE);
            mOnlineCourse.setVisibility(View.GONE);
            XImageLoader.loadImage(courseIv,
                    courseVo.getThumbnailUrl().trim(),
                    imageOptions);

            coverLay.setLayoutParams(new LinearLayout.LayoutParams(img_width, img_height));
        } else if (mClassDetailEntity != null) {
            if (EmptyUtil.isNotEmpty(mClassDetailEntity.getData())) {
                ClassDetailEntity.DataBean dataBean = mClassDetailEntity.getData().get(0);
                coursePrice.setText(Common.Constance.MOOC_MONEY_MARK + dataBean.getPrice());
                courseName.setText(EmptyUtil.isEmpty(dataBean.getName()) ? "" : dataBean.getName());
                organNameTv.setText(EmptyUtil.isEmpty(dataBean.getOrganName()) ? "" : dataBean.getOrganName());
                teacherNameTv.setText(EmptyUtil.isEmpty(dataBean.getTeachersName()) ? "" : dataBean.getTeachersName());
                StringUtil.fillSafeTextView(subtotalTv, Common.Constance.MOOC_MONEY_MARK + dataBean.getPrice());
                StringUtil.fillSafeTextView(needPayTv, Common.Constance.MOOC_MONEY_MARK + dataBean.getPrice());
                topBar.setTitle(getResources().getString(R.string.order_detail));

                courseIv.setVisibility(View.GONE);
                mOnlineCourse.setVisibility(View.VISIBLE);
                ImageUtil.fillDefaultView(mOnlineCourse,dataBean.getThumbnailUrl());
            }
        }
    }

    /**
     * 获取订单价格
     */
    private String getPrice(){
        long price = 0;
        if(EmptyUtil.isNotEmpty(mClassDetailEntity)){
            if(EmptyUtil.isNotEmpty(mClassDetailEntity.getData())){
                price = mClassDetailEntity.getData().get(0).getPrice();
            }
        }else if(isLive){
            price = liveDetailsVo.getLive().getPrice();
        }else{

            if(EmptyUtil.isNotEmpty(mEntities)){
                // 按章节购买
                for (PayChapterEntity entity:mEntities) {
                    if(!entity.isBuyed() && entity.isSelect()){
                        price += entity.getPrice();
                    }
                }
            }else{
                price = courseVo.getPrice();
            }
        }

        return String.valueOf(price);
    }

    /**
     * 获取订单类型
     * @return 1直播 0课程 3在线课堂
     */
    private int getType(){
        if(EmptyUtil.isNotEmpty(mClassDetailEntity)){
            if(EmptyUtil.isNotEmpty(mClassDetailEntity.getData())){
                return 3;
            }
        }
        return isLive ? 1 : 0;
    }

    /**
     * 获取直播，课程，在线课堂Id
     * @return 返回要购买的课程，直播，在线课堂id
     */
    private String getCourseId(){
        if(EmptyUtil.isNotEmpty(mClassDetailEntity)){
            if(EmptyUtil.isNotEmpty(mClassDetailEntity.getData())){
                return String.valueOf(mClassDetailEntity.getData().get(0).getId());
            }
        }
        if(EmptyUtil.isNotEmpty(mEntities)){
            return isLive ? liveDetailsVo.getLive().getId() : courseId;
        }else{
            return isLive ? liveDetailsVo.getLive().getId() : courseId;
        }
    }

    /**
     * 当章节购买时，获取chapterIds
     * @return
     */
    private String getChapterIds(){
        if(EmptyUtil.isNotEmpty(mEntities)){
            String chapterIds = "";
            ListIterator<PayChapterEntity> iterator = mEntities.listIterator();
            while(iterator.hasNext()){
                chapterIds += iterator.next().getId();
                if(iterator.hasNext()) chapterIds += ",";
            }
            return chapterIds;
        }else{
            return null;
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.commit_tv
                && (mClassDetailEntity != null || courseVo != null || (isLive && liveDetailsVo != null))) {
            // @date   :2018/6/7 0007 上午 9:26
            // @func   :5.7新添在线课堂订单
            /*PayParams params = new PayParams.Builder(this)
                    .goodsPrice(String.valueOf(isLive ? liveDetailsVo.getLive().getPrice() : courseVo.getPrice()))
                    .courseId(isLive ? ("" + liveDetailsVo.getLive().getId()) : courseId)
                    .memberId(UserHelper.getUserId())
                    .realName(UserHelper.getUserName())
                    .type(isLive ? 1 : 0)
                    .build();*/


            // 章节购买必须要传price 与 chapterIds
            String chapterIds = getChapterIds();
            PayParams params = new PayParams.Builder(this)
                    .goodsPrice(getPrice())
                    .courseId(getCourseId())
                    .schoolId(mSchoolId)
                    .chapterIds(chapterIds)
                    .memberId(UserHelper.getUserId())
                    // 这个Id肯定不是为空，如果给别人购买，则是对方的ID
                    // 如果给自己购买 mCurMemberId == memberId
                    .buyerMemberId(mCurMemberId)
                    .realName(UserHelper.getUserName())
                    .type(getType())
                    .build();


            LqPay.newInstance(params).requestPayInfo(new OnPayInfoRequestListener() {
                @Override
                public void onPayInfoRequetStart() {
                    // TODO 在此处做一些loading操作,progressbar.show();
                    showProgressDialog(getString(R.string.str_pay_progress_tips));
                }

                @Override
                public void onPayInfoRequstSuccess(String result) {
                    // TODO 可以将loading状态去掉了。请求预支付信息成功，开始跳转到客户端支付。
                    closeProgressDialog();
                    if(EmptyUtil.isNotEmpty(mClassDetailEntity) && EmptyUtil.isNotEmpty(mClassDetailEntity.getData())){
                        ClassDetailEntity.DataBean dataBean = mClassDetailEntity.getData().get(0);
                        PayActivity.newInstance(ConfirmOrderActivity.this,
                                dataBean.getClassId(),
                                result,String.valueOf(dataBean.getPrice()),
                                dataBean.getName(),
                                String.valueOf(dataBean.getId()),mCurMemberId);
                        return;
                    }

                    String price = getPrice();
                    // 是否是LQ学程,章节购买
                    boolean isChapterBuy = false;
                    if(EmptyUtil.isNotEmpty(courseVo) && EmptyUtil.isNotEmpty(mEntities)){
                        // 按章节购买
                        isChapterBuy = true;
                    }
                    PayActivity.newInstance(result,
                            /*isLive ? String.valueOf(liveDetailsVo.getLive().getPrice())
                                    : String.valueOf(courseVo.getPrice())*/
                            price,
                            isLive ? liveDetailsVo.getLive().getTitle() : courseVo.getName(),
                            isLive ? liveDetailsVo.getLive().getId() : courseId,isLive,
                            isLQwawaEnter,isChapterBuy,mDetailParams,
                            ConfirmOrderActivity.this,mCurMemberId);
                }

                @Override
                public void onPayInfoRequestFailure() {
                    // / TODO 可以将loading状态去掉了。获取预支付信息失败，会同时得到一个支付失败的回调。可以将loading状态去掉了。
                    closeProgressDialog();
                }
            });

        }
    }

}
