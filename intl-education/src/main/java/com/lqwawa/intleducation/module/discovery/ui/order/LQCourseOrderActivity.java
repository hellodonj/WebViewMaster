package com.lqwawa.intleducation.module.discovery.ui.order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.j256.ormlite.field.types.EnumIntegerType;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerItemDecoration;
import com.lqwawa.intleducation.common.Common;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.PayChapterEntity;
import com.lqwawa.intleducation.factory.data.entity.course.NotPurchasedChapterEntity;
import com.lqwawa.intleducation.module.discovery.ui.ConfirmOrderActivity;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailParams;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.PayChapterAdapter;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @desc 购买LQ学程创建订单
 * @author medici
 */
public class LQCourseOrderActivity extends PresenterActivity<LQCourseOrderContract.Presenter>
    implements LQCourseOrderContract.View,View.OnClickListener{

    private static final String KEY_EXTRA_COURSE_ENTITY = "KEY_EXTRA_COURSE_ENTITY";
    private static final String KEY_EXTRA_CHAPTER_ID = "KEY_EXTRA_CHAPTER_ID";
    private static final String KEY_EXTRA_RESOURCE_ID = "KEY_EXTRA_RESOURCE_ID";
    private static final String KEY_EXTRA_LQWAWA_ENTER = "KEY_EXTRA_LQWAWA_ENTER";
    private static final String KEY_EXTRA_SCHOOL_ID = "KEY_EXTRA_SCHOOL_ID";
    private static final String KEY_EXTRA_CURMEMEBER_ID = "KEY_EXTRA_CURMEMEBER_ID";

    private TopBar mTopBar;

    private FrameLayout mAllLayout;
    private TextView mTvAllPrice;
    private CheckBox mAllBox;
    private View mAllDivider;
    private FrameLayout mChapterLayout;
    private CheckBox mChapterBox;
    private LinearLayout mChapterListLayout;
    private TextView mTvAll;
    private RecyclerView mChapterRecycler;
    private PayChapterAdapter mAdapter;

    private TextView mTvPrice;
    private TextView mTvPay;

    // 课程入口参数
    private CourseDetailParams mDetailParams;
    private CourseVo mPayCourse;
    private NotPurchasedChapterEntity mEntity;
    private String mResourceId;
    private int mChapterId;
    // 磕碜所属的课程Id,用来统计使用
    private String mSchoolId;
    // 当前被购买的用户的memberId
    private String mCurMemberId;
    // 是否从两栖蛙蛙调过来的
    private boolean mLQwawaEnter;

    @Override
    protected LQCourseOrderContract.Presenter initPresenter() {
        return new LQCourseOrderPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_lq_course_order;
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        if(bundle.containsKey(ACTIVITY_BUNDLE_OBJECT)){
            mDetailParams = (CourseDetailParams) bundle.getSerializable(ACTIVITY_BUNDLE_OBJECT);
        }else{
            mDetailParams = new CourseDetailParams();
        }
        mPayCourse = (CourseVo) bundle.getSerializable(KEY_EXTRA_COURSE_ENTITY);
        mResourceId = bundle.getString(KEY_EXTRA_RESOURCE_ID);
        mLQwawaEnter = bundle.getBoolean(KEY_EXTRA_LQWAWA_ENTER);
        mChapterId = bundle.getInt(KEY_EXTRA_CHAPTER_ID);
        mSchoolId = bundle.getString(KEY_EXTRA_SCHOOL_ID);
        mCurMemberId = bundle.getString(KEY_EXTRA_CURMEMEBER_ID);
        if(EmptyUtil.isEmpty(mPayCourse) && EmptyUtil.isEmpty(mResourceId) || EmptyUtil.isEmpty(mSchoolId)){
            return false;
        }
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        mTopBar.setBack(true);

        mAllLayout = (FrameLayout) findViewById(R.id.all_layout);
        mTvAllPrice = (TextView) findViewById(R.id.tv_all_total_price);
        mAllBox = (CheckBox) findViewById(R.id.cb_all);
        mAllDivider = findViewById(R.id.all_divider);
        mChapterLayout = (FrameLayout) findViewById(R.id.chapter_layout);
        mChapterBox = (CheckBox) findViewById(R.id.cb_chapter);
        mChapterListLayout = (LinearLayout) findViewById(R.id.chapter_list_layout);
        mTvAll = (TextView) findViewById(R.id.tv_all);
        mChapterRecycler = (RecyclerView) findViewById(R.id.recycler);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mChapterRecycler.setLayoutManager(layoutManager);
        mAdapter = new PayChapterAdapter(mChapterId);
        mChapterRecycler.setAdapter(mAdapter);
        mChapterRecycler.addItemDecoration(new RecyclerItemDecoration(this,RecyclerItemDecoration.VERTICAL_LIST));

        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<PayChapterEntity>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, PayChapterEntity payChapterEntity) {
                super.onItemClick(holder, payChapterEntity);
                if(!payChapterEntity.isBuyed()){
                    payChapterEntity.setSelect(!payChapterEntity.isSelect());
                    mAdapter.notifyDataSetChanged();

                    calculateTotalPrice(false);
                }

                // 联动判断,选择全选，或者不全选
                List<PayChapterEntity> items = mAdapter.getItems();
                // 判断未购买的第一个选择状态，用来判断
                boolean firstChapterStatus = false;
                for (PayChapterEntity entity:items) {
                    if(!entity.isBuyed()){
                        firstChapterStatus = entity.isSelect();
                    }
                }

                // 默认都是一样的
                boolean same = true;
                for (int index = 0;index < items.size(); index++) {
                    PayChapterEntity entity = items.get(index);
                    if(!entity.isBuyed() && entity.isSelect() != firstChapterStatus){
                        // 只判断未购买的章节
                        // 如果发现章节列表中不同了
                        // 当前状态,就是需要设置全选文字
                        mTvAll.setText(R.string.label_all_select);
                        mTvAll.setActivated(true);
                        same = false;
                        break;
                    }
                }


                if(same){
                    // 都是一样的结果
                    if(firstChapterStatus){
                        // 当前全部都是选中 取消选择
                        mTvAll.setText(R.string.label_un_select);
                        mTvAll.setActivated(false);
                    }else{
                        // 当前全部都是未选择 全选
                        mTvAll.setText(R.string.label_all_select);
                        mTvAll.setActivated(true);
                    }
                }
            }
        });

        mTvPrice = (TextView) findViewById(R.id.tv_price);
        mTvPay = (TextView) findViewById(R.id.tv_pay);

        mTvAll.setActivated(true);

        mAllLayout.setOnClickListener(this);
        mChapterLayout.setOnClickListener(this);
        mTvPay.setOnClickListener(this);
        mTvAll.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        String id = null;
        if(EmptyUtil.isNotEmpty(mPayCourse)) id = mPayCourse.getId();
        mPresenter.requestNotPurchasedChapter(mCurMemberId,id,mResourceId);
    }

    @Override
    public void updateNotPurchasedView(@NonNull NotPurchasedChapterEntity entity) {
        mEntity = entity;
        if(EmptyUtil.isNotEmpty(entity.getCourse())){
            mPayCourse = entity.getCourse().get(0);
        }

        mTopBar.setTitle(mPayCourse.getName());
        List<PayChapterEntity> listData = entity.getChapterList();
        for (PayChapterEntity _entity : listData){
            if(!_entity.isBuyed() && (_entity.getId() == mChapterId || _entity.isHighlight())){
                // LQ学程要购买的章节Id,与布置任务需要高亮显示的Id
                _entity.setSelect(true);
            }
        }

        // 判断当前所有的未购买的实体，是不是都选中了
        boolean allSelect = true;
        for (PayChapterEntity _entity:listData) {
            if(!_entity.isBuyed() && !_entity.isSelect()){
                allSelect = false;
            }
        }

        if(allSelect){
            // 真的全部都TM选中了
            mTvAll.setText(R.string.label_un_select);
            mTvAll.setActivated(false);
        }

        mAdapter.replace(listData);

        if(EmptyUtil.isEmpty(listData)){
            // 该课程下，没有发布课程大纲
            // 隐藏章节购买布局
            mChapterLayout.setVisibility(View.GONE);
        }

        List<CourseVo> course = mEntity.getCourse();
        if(EmptyUtil.isNotEmpty(course)){
            CourseVo vo = course.get(0);
            long price = vo.getPrice();
            // 全部购买，显示价格
            mTvAllPrice.setText(Common.Constance.MOOC_MONEY_MARK.concat(Long.toString(price)));
        }


        if(entity.isChapterBuyed()){
            // 已经章节购买过 不允许全部购买
            mAllLayout.setVisibility(View.GONE);
            mAllDivider.setVisibility(View.GONE);
            // 显示章节购买列表
            mChapterListLayout.setVisibility(View.VISIBLE);
            // 章节购买按钮不允许点击
            mChapterLayout.setEnabled(false);
            mChapterBox.setEnabled(false);

            // 按钮点击状态设置
            mAllBox.setChecked(false);
            mChapterBox.setChecked(true);
            calculateTotalPrice(false);
        }else{
            // 没有购买过,允许全部购买
            mAllLayout.setVisibility(View.VISIBLE);
            // 没有购买过
            if(mChapterId > 0 || mLQwawaEnter){
                // 按章节购买
                // 默认选择按章购买
                mAllBox.setChecked(false);
                mChapterBox.setChecked(true);
                mChapterListLayout.setVisibility(View.VISIBLE);

                calculateTotalPrice(false);
            }else{
                calculateTotalPrice(true);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.all_layout){
            // 点击的是全部购买
            mAllBox.setChecked(true);
            mChapterBox.setChecked(false);
            mChapterListLayout.setVisibility(View.GONE);

            calculateTotalPrice(true);
        }else if(viewId == R.id.chapter_layout){
            // 点击的是章程购买
            mAllBox.setChecked(false);
            mChapterBox.setChecked(true);
            mChapterListLayout.setVisibility(View.VISIBLE);

            calculateTotalPrice(false);
        }else if(viewId == R.id.tv_pay){
            // 购买
            if(mAllBox.isChecked()){
                // 购买课程
                ConfirmOrderActivity.start(this,mPayCourse,mSchoolId,null,mLQwawaEnter,mDetailParams,mCurMemberId);
            }else{
                // 购买章节
                ArrayList<PayChapterEntity> entities = new ArrayList<>();
                List<PayChapterEntity> items = mAdapter.getItems();
                for (PayChapterEntity entity:items) {
                    if(!entity.isBuyed() && entity.isSelect()){
                        entities.add(entity);
                    }
                }

                if(EmptyUtil.isEmpty(entities)){
                    UIUtil.showToastSafe(R.string.label_please_select_pay_chapter);
                    return;
                }

                ConfirmOrderActivity.start(this,mPayCourse,mSchoolId,entities,mLQwawaEnter,mDetailParams,mCurMemberId);
            }
        }else if(viewId == R.id.tv_all){
            if(EmptyUtil.isEmpty(mAdapter) || EmptyUtil.isEmpty(mAdapter.getItems())){
                return;
            }

            List<PayChapterEntity> items = mAdapter.getItems();

            if(mTvAll.isActivated()){
                // 激活状态,当前是可全选状态，点击设置全选
                // 文本改成全选
                mTvAll.setText(R.string.label_un_select);
                mTvAll.setActivated(false);

                for (PayChapterEntity entity:items) {
                    if(!entity.isBuyed()){
                        entity.setSelect(true);
                    }
                }
            }else{
                // 不是激活状态，当前是全选状态，点击取消设置全选
                mTvAll.setText(R.string.label_all_select);
                mTvAll.setActivated(true);

                for (PayChapterEntity entity:items) {
                    if(!entity.isBuyed()){
                        entity.setSelect(false);
                    }
                }
            }

            // 全选
            calculateTotalPrice(false);
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 计算总价格
     */
    private void calculateTotalPrice(boolean all){
        if(all){
            // 全部购买
            List<CourseVo> course = mEntity.getCourse();
            if(EmptyUtil.isNotEmpty(course)){
                CourseVo vo = course.get(0);
                long price = vo.getPrice();
                generatePriceForegroundSpan(price);
            }
        }else{
            if(EmptyUtil.isNotEmpty(mAdapter)){
                if(EmptyUtil.isNotEmpty(mAdapter.getItems())){
                    List<PayChapterEntity> items = mAdapter.getItems();
                    long totalPrice = 0L;
                    for (PayChapterEntity entity:items) {
                        if(!entity.isBuyed() && entity.isSelect()){
                            totalPrice += entity.getPrice();
                        }
                    }

                    generatePriceForegroundSpan(totalPrice);
                    // mTvPrice.setText(UIUtil.getString(R.string.label_total_money).concat(Long.toString(totalPrice)));
                }
            }
        }
    }

    /**
     * 生成价格的显示前景
     * @param price 价格
     */
    private void generatePriceForegroundSpan(long price){
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
        String source = UIUtil.getString(R.string.label_total_money);
        SpannableString sourceString = new SpannableString(source);
        sourceString.setSpan(new ForegroundColorSpan(UIUtil.getColor(R.color.textPrimary)), 0, source.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        stringBuilder.append(sourceString);

        String priceStr = Long.toString(price);
        priceStr = Common.Constance.MOOC_MONEY_MARK + priceStr;
        SpannableString priceString = new SpannableString(priceStr);
        priceString.setSpan(new ForegroundColorSpan(UIUtil.getColor(R.color.textMoneyRed)), 0, priceStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        stringBuilder.append(priceString);
        mTvPrice.setText(stringBuilder);
    }

    /**
     * @author medici
     * @param context 上下文对象
     * @param vo 课程对象
     * @param curMemberId 当前被购买人的memberId
     */
    public static void show(@NonNull Context context,
                            @NonNull CourseVo vo,
                            @NonNull String schoolId,
                            @NonNull String curMemberId,
                            CourseDetailParams courseDetailParams){
        Intent intent = new Intent(context,LQCourseOrderActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_EXTRA_COURSE_ENTITY,vo);
        bundle.putString(KEY_EXTRA_SCHOOL_ID,schoolId);
        bundle.putString(KEY_EXTRA_CURMEMEBER_ID,curMemberId);
        if (courseDetailParams != null) {
            bundle.putSerializable(ACTIVITY_BUNDLE_OBJECT, courseDetailParams);
        }
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * @author medici
     * @param context 上下文对象
     * @param vo 课程对象
     * @param chapterId LQ学程章节购买调用
     */
    public static void show(@NonNull Context context,
                            @NonNull CourseDetailParams params,
                            @NonNull CourseVo vo,
                            @NonNull String schoolId, int chapterId,
                            @NonNull String curMemberId){
        Intent intent = new Intent(context,LQCourseOrderActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ACTIVITY_BUNDLE_OBJECT,params);
        bundle.putSerializable(KEY_EXTRA_COURSE_ENTITY,vo);
        bundle.putString(KEY_EXTRA_SCHOOL_ID,schoolId);
        bundle.putInt(KEY_EXTRA_CHAPTER_ID,chapterId);
        bundle.putString(KEY_EXTRA_CURMEMEBER_ID,curMemberId);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * @author medici
     * @param context 上下文对象
     * @desc 两栖蛙蛙学习任务入口
     */
    public static void show(@NonNull Context context,@NonNull String schoolId, @NonNull String id){
        Intent intent = new Intent(context,LQCourseOrderActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_EXTRA_RESOURCE_ID,id);
        bundle.putBoolean(KEY_EXTRA_LQWAWA_ENTER,true);
        bundle.putString(KEY_EXTRA_SCHOOL_ID,schoolId);
        String userId = UserHelper.getUserId();
        bundle.putString(KEY_EXTRA_CURMEMEBER_ID,userId);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
