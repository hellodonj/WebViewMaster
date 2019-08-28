package com.lqwawa.intleducation.module.user.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseAdapter;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.common.ui.CustomDialog;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.StringUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.JoinClassEntity;
import com.lqwawa.intleducation.factory.data.entity.OnlineClassEntity;
import com.lqwawa.intleducation.factory.helper.OnlineCourseHelper;
import com.lqwawa.intleducation.factory.helper.OrderHelper;
import com.lqwawa.intleducation.lqpay.PayStatus;
import com.lqwawa.intleducation.module.discovery.ui.PayActivity;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailParams;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailType;
import com.lqwawa.intleducation.module.discovery.ui.order.LQCourseOrderActivity;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.learn.tool.LiveDetails;
import com.lqwawa.intleducation.module.learn.ui.MyCourseDetailsActivity;
import com.lqwawa.intleducation.module.learn.vo.LiveVo;
import com.lqwawa.intleducation.module.onclass.OnlineClassRole;
import com.lqwawa.intleducation.module.onclass.detail.join.JoinClassDetailActivity;
import com.lqwawa.intleducation.module.onclass.detail.notjoin.ClassDetailActivity;
import com.lqwawa.intleducation.module.onclass.detail.notjoin.ClassInfoParams;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.intleducation.module.user.vo.MyOrderVo;
import com.osastudio.common.utils.XImageLoader;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XChen on 2016/11/12.
 * email:man0fchina@foxmail.com
 */

public class MyOrderListAdapter extends MyBaseAdapter {
    private Activity activity;
    private List<MyOrderVo> list;
    private LayoutInflater inflater;
    private int img_width;
    private int img_height;
    ImageOptions imageOptions;
    ImageOptions liveImageOptions;
    ImageOptions onlineImageOptions;
    ImageOptions orderImageOptions;
    ImageOptions courseImageOptions;
    ImageOptions classImageOptions;
    private static final int[] orderStatusResId = new int[]{
            R.string.order_status_0,
            R.string.order_status_1,
            R.string.order_status_2};

    private static final int[] orderTuttorStatusResId = new int[]{
            R.string.order_status_3,
            R.string.order_status_1,
            R.string.order_status_2};

    private static final int[] orderTutorStatus = new int[]{
            R.string.tutor_order_status_1,
            R.string.tutor_order_status_2,
            R.string.tutor_order_status_3};

    private static final int[] orderStatusColorId = new int[]{
            R.color.com_text_red,
            R.color.com_text_lq_green,
            R.color.text_gray};

    private static final int[] orderTutorStatusColor = new int[]{
            R.color.com_text_lq_green,
            R.color.com_text_lq_green,
            R.color.text_gray};

    private final int[] mCourseTypesBgId = new int[]{
            R.drawable.shape_course_type_read,
            R.drawable.shape_course_type_learn,
            R.drawable.shape_course_type_practice,
            R.drawable.shape_course_type_exam,
            R.drawable.shape_course_type_video
    };

    private String[] mCourseTypeNames;

    private OnContentChangedListener onContentChangedListener;

    public MyOrderListAdapter(Activity activity, OnContentChangedListener listener) {
        this.activity = activity;
        this.onContentChangedListener = listener;
        this.inflater = LayoutInflater.from(activity);
        list = new ArrayList<MyOrderVo>();

        int p_width = activity.getWindowManager().getDefaultDisplay().getWidth();
        img_width = p_width / 4;
        img_height = img_width * 297 / 210;

        imageOptions = XImageLoader.buildImageOptions(ImageView.ScaleType.CENTER_CROP,
                R.drawable.default_cover_h, false, false, null);

        onlineImageOptions = XImageLoader.buildImageOptions(ImageView.ScaleType.CENTER_CROP,
                R.drawable.default_cover_h, false, false, null);

        liveImageOptions = XImageLoader.buildImageOptions(ImageView.ScaleType.CENTER_CROP,
                R.drawable.default_cover_h, false, false, null);

        orderImageOptions = XImageLoader.buildImageOptions(ImageView.ScaleType.CENTER_CROP,
                        R.drawable.ic_task_not_flag_l, false, false, null);

        courseImageOptions = XImageLoader.buildImageOptions(ImageView.ScaleType.CENTER_CROP,
                R.drawable.ic_lqc_l, false, false, null);

        classImageOptions = XImageLoader.buildImageOptions(ImageView.ScaleType.FIT_CENTER,
                R.drawable.default_group_icon, false, false, null, img_width, img_height);

        mCourseTypeNames =
                activity.getResources().getStringArray(R.array.course_type_names);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final MyOrderVo vo = list.get(position);
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.mod_user_my_order_list_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        String formatName = getViewNameByVo(vo);
        if (EmptyUtil.isEmpty(formatName)) {
            // 自己买给自己
            holder.mBuyerLayout.setVisibility(View.GONE);
        } else {
            // 我买给别人,或者别人买给我
            holder.mBuyerLayout.setVisibility(View.VISIBLE);
            StringUtil.fillSafeTextView(holder.mtvBuyer, formatName);
        }

        holder.organName.setText(vo.getOrganName());
        holder.course_name.setText(vo.getCourseName());
        holder.teacher_name.setText(vo.getTeachersName());
        holder.course_price.setText(new StringBuffer().append("¥").append(vo.getPrice()));
//        holder.order_create_time_tv.setText(vo.getCreateTime() + "");

        holder.mTvCourseType.setVisibility(vo.getType() == 0 ? View.VISIBLE : View.GONE);
        int courseType = vo.getAssortment();
        if (courseType >= 0 && courseType < mCourseTypeNames.length) {
            holder.mTvCourseType.setBackgroundResource(mCourseTypesBgId[courseType]);
            holder.mTvCourseType.setText(mCourseTypeNames[courseType]);
        }
        holder.course_name.setTextColor(activity.getResources().getColor(
                vo.isDeleted() ? R.color.com_text_gray : R.color.com_text_black));
        /*holder.organName.setTextColor(activity.getResources().getColor(
                vo.isDeleted() ? R.color.com_text_gray : R.color.com_text_black));
        holder.teacher_name.setTextColor(activity.getResources().getColor(
                vo.isDeleted() ? R.color.com_text_gray : R.color.com_text_black));*/
        holder.organName.setTextColor(activity.getResources().getColor(
                vo.isDeleted() ? R.color.com_text_gray : R.color.com_text_gray));
        holder.teacher_name.setTextColor(activity.getResources().getColor(
                vo.isDeleted() ? R.color.com_text_gray : R.color.com_text_gray));
        holder.course_price.setTextColor(activity.getResources().getColor(
                vo.isDeleted() ? R.color.com_text_gray : R.color.com_text_red));
        holder.status_tv.setText("");
        //0等待付款 1交易成功 2交易关闭
        if (vo.getStatus() >= 0 && vo.getStatus() < 3) {
            holder.status_tv.setText(
                    activity.getResources().getString(orderStatusResId[vo.getStatus()])
            );
            holder.status_tv.setTextColor(activity.getResources().getColor(orderStatusColorId[vo.getStatus()]));
            holder.delete_order_tv.setBackgroundResource(R.drawable.btn_green_stroke_bg_selector);
            holder.delete_order_tv.setTextColor(activity.getResources().getColor(R.color.com_text_green));
            holder.delete_order_tv.setVisibility(View.VISIBLE);
            holder.imageViewAngleLive.setVisibility(vo.getType() == 1 ? View.VISIBLE : View.GONE);
            if (vo.getStatus() == PayStatus.PAY_CANCEL) {//等待付款
                holder.rebuy_tv.setVisibility(View.VISIBLE);
                holder.rebuy_tv.setText(activity.getResources().getString(R.string.cancel_order));
                holder.delete_order_tv.setText(activity.getResources().getString(R.string.to_pay));
                holder.delete_order_tv.setTextColor(activity.getResources().getColor(
                        vo.isDeleted() ? R.color.com_text_light_gray : R.color.com_text_green));
                holder.delete_order_tv.setBackground(activity.getResources().getDrawable(
                        vo.isDeleted() ? R.drawable.shape_circle_gray_stroke_h1_radius_18 : R.drawable.btn_green_stroke_bg_selector));
            } else if (vo.getStatus() == PayStatus.PAY_OK) {//交易成功
                holder.rebuy_tv.setVisibility(View.VISIBLE);
                holder.rebuy_tv.setText(activity.getResources().getString(R.string.delete_order));
                holder.delete_order_tv.setTextColor(activity.getResources().getColor(
                        vo.isDeleted() ? R.color.com_text_light_gray : R.color.com_text_green));
                holder.delete_order_tv.setBackground(activity.getResources().getDrawable(
                        vo.isDeleted() ? R.drawable.shape_circle_gray_stroke_h1_radius_18 : R.drawable.btn_green_stroke_bg_selector));
                if (vo.isIsExpire()) {
                    holder.delete_order_tv.setVisibility(View.GONE);
                    holder.delete_order_tv.setBackgroundResource(R.drawable.shape_circle_gray_stroke_h1_radius_18);
                    holder.delete_order_tv.setTextColor(activity.getResources().getColor(R.color.com_text_gray));
                }
                if (vo.isIsJoin()) {
                    holder.delete_order_tv.setText(activity.getResources().getString(R.string.to_learn));

                } else {
                    holder.delete_order_tv.setText(activity.getResources().getString(R.string.to_join));
                }

                // 已完成删除去学习和立即参加按钮
                if (getPayDirection(vo) == PayDirection.SELF_TO_OTHER) {
                    // 我买给其它人
                    holder.delete_order_tv.setVisibility(View.GONE);
                } else {
                    //
                    holder.delete_order_tv.setVisibility(View.VISIBLE);
                }
            } else if (vo.getStatus() == PayStatus.PAY_FAILURE) {//交易关闭
                holder.delete_order_tv.setTextColor(activity.getResources().getColor(R.color.com_text_green));
                holder.delete_order_tv.setBackground(activity.getResources().getDrawable(R.drawable.btn_green_stroke_bg_selector));
                holder.delete_order_tv.setText(activity.getResources().getString(R.string.delete_order));
                holder.rebuy_tv.setVisibility(View.GONE);
            }

            //帮辅订单
            if (vo.getType() == 6) {
                String typeName = "";
                if (vo.getTaskType() == 5) {
                    typeName = String.format(UIUtil.getString(R.string.label_task_type_template), UIUtil.getString(R.string.label_tutorial_task_type_listen_read_course));
                } else if (vo.getTaskType() == 8) {
                    typeName = String.format(UIUtil.getString(R.string.label_task_type_template), UIUtil.getString(R.string.label_tutorial_task_type_do_task));
                }
                SpannableString spannableString =
                        new SpannableString(typeName + vo.getTaskName());
                if (!TextUtils.isEmpty(typeName)) {
                    spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#009039")),
                            0, typeName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                holder.course_name.setText(spannableString);

                holder.delete_order_tv.setVisibility(View.GONE);
                StringUtil.fillSafeTextView(holder.teacher_name, String.format(UIUtil.getString(R.string.label_commit_placeholder_teacher), vo.getRealName()));
//                holder.teacher_name.setText("提交给" + vo.getRealName() + "老师");
                holder.status_tv.setText(activity.getResources().getString(orderTuttorStatusResId[vo.getStatus()]));
                holder.status_tv.setTextColor(activity.getResources().getColor(orderTutorStatusColor[vo.getStatus()]));
                holder.mTutorStatusTv.setText(activity.getResources().getString(orderTutorStatus[vo.getStatus()]));
                holder.mTutorStatusTv.setTextColor(activity.getResources().getColor(orderTutorStatusColor[vo.getStatus()]));
                //0等待批阅 1交易成功 2交易关闭
                if (vo.getStatus() == PayStatus.PAY_CANCEL) {//等待批阅
                    holder.rebuy_tv.setVisibility(View.GONE);
                    holder.mTutorStatusTv.setVisibility(View.VISIBLE);
                    holder.mTutorStatusTv.setBackground(activity.getResources().getDrawable(R.drawable.btn_green_stroke_bg_selector));
                } else if (vo.getStatus() == PayStatus.PAY_OK) {//交易成功
                    holder.rebuy_tv.setVisibility(View.VISIBLE);
                    holder.mTutorStatusTv.setVisibility(View.GONE);
                    holder.mTutorStatusTv.setBackground(activity.getResources().getDrawable(R.drawable.btn_green_stroke_bg_selector));
                } else if (vo.getStatus() == PayStatus.PAY_FAILURE) {//交易关闭
                    holder.rebuy_tv.setVisibility(View.GONE);
                    holder.mTutorStatusTv.setVisibility(View.VISIBLE);
                    holder.mTutorStatusTv.setBackground(activity.getResources().getDrawable(R.drawable.shape_circle_gray_stroke_h1_radius_18));
                }
            } else if (vo.getType() == 7) {
                holder.course_name.setText(String.format(UIUtil.getString(R.string.label_join_class_order), vo.getClassName()));
                holder.organName.setText(vo.getSchoolName());
                if (vo.getStatus() == PayStatus.PAY_OK) {//交易成功
                    holder.delete_order_tv.setVisibility(View.GONE);
                    holder.rebuy_tv.setVisibility(View.VISIBLE);
                    holder.mTutorStatusTv.setVisibility(View.GONE);
                } else if (vo.getStatus() == PayStatus.PAY_FAILURE) {//交易关闭
                    holder.delete_order_tv.setVisibility(View.GONE);
                    holder.rebuy_tv.setVisibility(View.GONE);
                    holder.mTutorStatusTv.setVisibility(View.VISIBLE);
                }
            } else {
                holder.mTutorStatusTv.setVisibility(View.GONE);
            }
        }


        holder.rebuy_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vo.getStatus() == PayStatus.PAY_CANCEL) {//等待付款
                    // TODO: 2017/7/21 取消订单
                    CustomDialog.Builder builder = new CustomDialog.Builder(activity);
                    builder.setMessage(activity.getResources().getString(R.string.confirm)
                            + activity.getResources().getString(R.string.label_cancel_order)
                            + "?");
                    builder.setTitle(activity.getResources().getString(R.string.tip));
                    builder.setPositiveButton(activity.getResources().getString(R.string.confirm),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    doCancelOrDeleteOrder(vo, holder);
                                }
                            });

                    builder.setNegativeButton(activity.getResources().getString(R.string.cancel),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                    builder.create().show();

                    // doCancelOrDeleteOrder(vo,holder);
                } else if (vo.getStatus() == PayStatus.PAY_OK) {//付款成功
                    // TODO: 2017/9/20 删除订单
                    CustomDialog.Builder builder = new CustomDialog.Builder(activity);
                    builder.setMessage(activity.getResources().getString(R.string.confirm)
                            + activity.getResources().getString(R.string.delete_order)
                            + "?");
                    builder.setTitle(activity.getResources().getString(R.string.tip));
                    builder.setPositiveButton(activity.getResources().getString(R.string.confirm),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    doCancelOrDeleteOrder(vo, holder);
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
            }
        });
        holder.delete_order_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vo.isDeleted() && vo.getStatus() != PayStatus.PAY_FAILURE) {
                    if (vo.getType() == 1) {
                        UIUtil.showToastSafe(R.string.live_is_invalid);
                        return;
                    } else if (vo.getType() == 0) {
                        UIUtil.showToastSafe(R.string.course_is_invalid);
                        return;
                    } else if (vo.getType() == 3) {
                        if (vo.getStatus() == PayStatus.PAY_OK) {
                            // 去学习
                            // 已经被删除的历史班
                            // UIUtil.showToastSafe(R.string.label_online_course_invalid);
                            // 发送获取班级详情细信息的请求
                            OnlineClassEntity modelEntity = new OnlineClassEntity();
                            modelEntity.setClassId(vo.getClassId());
                            modelEntity.setId(Integer.parseInt(vo.getCourseId()));
                            ClassInfoParams params = new ClassInfoParams(modelEntity);
                            ClassDetailActivity.show(activity, params);
                            /*OnlineCourseHelper.loadOnlineClassInfo(UserHelper.getUserId(), vo.getClassId(), new DataSource.Callback<JoinClassEntity>() {
                                @Override
                                public void onDataNotAvailable(int strRes) {
                                    UIUtil.showToastSafe(strRes);
                                }

                                @Override
                                public void onDataLoaded(JoinClassEntity joinClassEntity) {
                                    // 进行验证
                                    if(!EmptyUtil.isEmpty(joinClassEntity)){
                                        // String role = getOnlineClassRoleInfo(joinClassEntity);
                                        // JoinClassDetailActivity.show(activity,vo.getClassId(),joinClassEntity.getSchoolId(),Integer.parseInt(vo.getCourseId()),role,false);
                                        boolean needToJoin = joinClassEntity.isIsInClass();
                                        String role = getOnlineClassRoleInfo(joinClassEntity);
                                        if(needToJoin || OnlineClassRole.ROLE_TEACHER.equals(role)){
                                            // 已经加入班级 或者是老师身份
                                            JoinClassDetailActivity.show(activity,joinClassEntity.getClassId(),joinClassEntity.getSchoolId(),Integer.parseInt(vo.getCourseId()),role,false);
                                        }
                                    }
                                }
                            });*/
                        }
                    }
                    /*ToastUtil.showToast(activity, activity.getResources().getString(
                            vo.getType() == 1 ? R.string.live_is_invalid : R.string.course_is_invalid));*/
                }

                if (vo.getStatus() == PayStatus.PAY_CANCEL) {//等待付款
                    // TODO: 2017/7/18 去付款
                    if (vo.getType() == 3) {
                        // 3是空中课堂
                        // 发送请求获取schoolId
                        // 发送获取班级详情细信息的请求
                        if (vo.isDeleted()) {
                            // 已经是历史班了

                            // 去学习，如果已经是授课结束班，可以进入浏览
                            // 去付款，如果已经加入班级（在其它地方付款过了，虽然是授课结束班），但可以去浏览
                            // 去付款，没有加入班级，是授课结束班，提示已授课结束
                            // 提示该班级已完成授课
                            // 现在改成设成历史班
                            UIUtil.showToastSafe(R.string.label_this_class_give_history);
                            /*AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            // 设置参数
                            builder.setMessage(activity.getResources().getString(R.string.order_has_join_tip))
                                    .setPositiveButton(activity.getResources().getString(R.string.i_know),
                                            null);
                            builder.create().show();*/
                            /*CustomDialog.Builder builder = new CustomDialog.Builder(activity);
                            builder.setMessage(activity.getResources().getString(R.string.course_code_out_time_tip));
                            builder.setPositiveButton(activity.getResources().getString(R.string.buy_immediately),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            ConfirmOrderActivity.start(activity, vo.getCourseId());
                                        }
                                    });

                            builder.setNegativeButton(activity.getResources().getString(R.string.cancel),
                                    new android.content.DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });

                            builder.create().show();*/
                            return;
                        }

                        if (getPayDirection(vo) != PayDirection.SELF_TO_OTHER) {
                            // 给自己付款
                            OnlineCourseHelper.loadOnlineClassInfo(UserHelper.getUserId(), vo.getClassId(), new DataSource.Callback<JoinClassEntity>() {
                                @Override
                                public void onDataNotAvailable(int strRes) {
                                    UIUtil.showToastSafe(strRes);
                                }

                                @Override
                                public void onDataLoaded(JoinClassEntity joinClassEntity) {
                                    // 进行验证
                                    if (!EmptyUtil.isEmpty(joinClassEntity)) {
                                        // String role = getOnlineClassRoleInfo(joinClassEntity);
                                        // JoinClassDetailActivity.show(activity,vo.getClassId(),joinClassEntity.getSchoolId(),Integer.parseInt(vo.getCourseId()),role,false);
                                        boolean needToJoin = joinClassEntity.isIsInClass();
                                        String role = getOnlineClassRoleInfo(joinClassEntity);
                                        if (needToJoin || OnlineClassRole.ROLE_TEACHER.equals(role)) {
                                            // 已经加入班级 或者是老师身份
                                            JoinClassDetailActivity.show(activity, joinClassEntity.getClassId(), joinClassEntity.getSchoolId(), Integer.parseInt(vo.getCourseId()), role, false);
                                        } else {

                                            PayActivity.newInstance(activity,
                                                    vo.getClassId(),
                                                    String.valueOf(vo.getId()),
                                                    String.valueOf(vo.getPrice()),
                                                    vo.getCourseName(), vo.getCourseId(),
                                                    UserHelper.getUserId());
                                            // 未加入班级
                                            // ClassDetailActivity.show(activity,joinClassEntity.getClassId(),joinClassEntity.getSchoolId(),Integer.parseInt(vo.getCourseId()),role,false);
                                        }
                                    }
                                }
                            });
                        } else {
                            // 需要判断受益人有没有参加到课堂
                            OnlineCourseHelper.loadOnlineClassInfo(vo.getMemberId(), vo.getClassId(), new DataSource.Callback<JoinClassEntity>() {
                                @Override
                                public void onDataNotAvailable(int strRes) {
                                    UIUtil.showToastSafe(strRes);
                                }

                                @Override
                                public void onDataLoaded(JoinClassEntity joinClassEntity) {
                                    // 进行验证
                                    if (!EmptyUtil.isEmpty(joinClassEntity)) {
                                        boolean needToJoin = joinClassEntity.isIsInClass();
                                        if (!needToJoin) {
                                            // 给别人付款 传受益人的Id
                                            PayActivity.newInstance(activity,
                                                    vo.getClassId(),
                                                    String.valueOf(vo.getId()),
                                                    String.valueOf(vo.getPrice()),
                                                    vo.getCourseName(), vo.getCourseId(),
                                                    vo.getMemberId());
                                        } else {
                                            UIUtil.showToastSafe(R.string.label_online_member_in_class_warning);
                                        }
                                    }
                                }
                            });
                        }

                    } else {

                        // 如果是学程
                        if (vo.getType() == 0) {
                            OrderHelper.checkOrder(vo.getId(), new DataSource.Callback<ResponseVo<Object>>() {
                                @Override
                                public void onDataNotAvailable(int strRes) {
                                    UIUtil.showToastSafe(strRes);
                                }

                                @Override
                                public void onDataLoaded(ResponseVo<Object> responseVo) {
                                    if (responseVo.isSucceed()) {
                                        CourseDetailParams params = new CourseDetailParams(CourseDetailType.COURSE_DETAIL_ORDER_ENTER);
                                        params.setLibraryType(vo.getLibraryType());
                                        params.setIsVideoCourse(vo.getType() == 2);
                                        if (getPayDirection(vo) != PayDirection.SELF_TO_OTHER) {
                                            // 自己买给自己，传自己的Id
                                            PayActivity.newInstance(String.valueOf(vo.getId()),
                                                    String.valueOf(vo.getPrice()), vo.getCourseName(), vo.getCourseId(),
                                                    vo.getType() == 1, false, !vo.isBuyAll(), params, activity,
                                                    UserHelper.getUserId());
                                        } else {
                                            PayActivity.newInstance(String.valueOf(vo.getId()),
                                                    String.valueOf(vo.getPrice()), vo.getCourseName(), vo.getCourseId(),
                                                    vo.getType() == 1, false, !vo.isBuyAll(), params, activity,
                                                    vo.getMemberId());
                                        }
                                        /*PayActivity.newInstance(result,
                                         *//*isLive ? String.valueOf(liveDetailsVo.getLive().getPrice())
                                    : String.valueOf(courseVo.getPrice())*//*
                                                price,
                                                isLive ? liveDetailsVo.getLive().getTitle() : courseVo.getName(),
                                                isLive ? liveDetailsVo.getLive().getId() : courseId,isLive,
                                                isLQwawaEnter,isChapterBuy,
                                                ConfirmOrderActivity.this);*/


                                    } else {
                                        // {"message":"该订单对应课程已购买,不能继续付款","code":-1}
                                        UIUtil.showToastSafe(R.string.tip_order_expire);
                                    }
                                }
                            });
                        } else {
                            PayActivity.newInstance(String.valueOf(vo.getId()),
                                    String.valueOf(vo.getPrice()), vo.getCourseName(), vo.getCourseId(),
                                    vo.getType() == 1, activity, UserHelper.getUserId());

                        }
                    }

                    /*if (!vo.isIsJoin() || vo.isIsExpire()) {
                        PayActivity.newInstance(String.valueOf(vo.getId()),
                                String.valueOf(vo.getPrice()), vo.getCourseName(), vo.getCourseId(), activity);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        // 设置参数
                        builder.setMessage(activity.getResources().getString(R.string.order_has_join_tip))
                                .setPositiveButton(activity.getResources().getString(R.string.i_know),
                                        null);
                        builder.create().show();
                    }*/
//                  ConfirmOrderActivity.start(activity,vo.getCourseId());
                } else if (vo.getStatus() == PayStatus.PAY_OK) {//交易成功
                    if (vo.isIsExpire()) {
                        CustomDialog.Builder builder = new CustomDialog.Builder(activity);
                        builder.setMessage(activity.getResources().getString(R.string.course_code_out_time_tip)
                        );
                        builder.setPositiveButton(activity.getResources().getString(R.string.buy_immediately),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        // 激活码过期
                                        CourseVo courseVo = new CourseVo();
                                        courseVo.setId(vo.getCourseId());
                                        courseVo.setName(vo.getCourseName());
                                        courseVo.setOrganName(vo.getOrganId());
                                        String memberId = UserHelper.getUserId();
                                        LQCourseOrderActivity.show(activity, courseVo,
                                                vo.getOrganId(), memberId, null);
                                        // ConfirmOrderActivity.start(activity, vo.getCourseId());
                                    }
                                });

                        builder.setNegativeButton(activity.getResources().getString(R.string.cancel),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                        builder.create().show();
                    } else {
                        // TODO: 2017/7/18 去学习
                        if (!vo.isIsJoin()) {
                            if (getPayDirection(vo) == PayDirection.SELF_TO_SELF &&
                                    !TextUtils.equals(UserHelper.getUserId(), vo.getMemberId())) {
                                // 自己买给自己,并且受益人不是自己,那就是给别人下单,别人自己买了
                                UIUtil.showToastSafe(R.string.label_other_order_disabled);
                            } else {
                                doRejoin(vo.getCourseId(), vo.getType());
                            }
                        } else {
                            if (vo.getType() == 1) {
                                LiveVo liveVo = LiveVo.fromMyOrder(vo);
                                LiveDetails.jumpToLiveDetails(activity, liveVo, false, true, false);
                                if (liveVo.getState() != 0) {
                                    liveVo.setBrowseCount(liveVo.getBrowseCount() + 1);
                                }
                            } else if (vo.getType() == 0) {
                                // 0是课程
                                CourseDetailParams params = new CourseDetailParams(CourseDetailType.COURSE_DETAIL_ORDER_ENTER);
                                params.setLibraryType(vo.getLibraryType());
                                params.setIsVideoCourse(vo.getType() == 2);
                                MyCourseDetailsActivity.start(activity, vo.getCourseId(),
                                        false, true, UserHelper.getUserId(), false, false, false,
                                        false, params, null);
                            } else if (vo.getType() == 3) {
                                // 3是空中课堂
                                // 发送请求获取schoolId
                                // 发送获取班级详情细信息的请求
                                OnlineCourseHelper.loadOnlineClassInfo(UserHelper.getUserId(), vo.getClassId(), new DataSource.Callback<JoinClassEntity>() {
                                    @Override
                                    public void onDataNotAvailable(int strRes) {
                                        UIUtil.showToastSafe(strRes);
                                    }

                                    @Override
                                    public void onDataLoaded(JoinClassEntity joinClassEntity) {
                                        // 进行验证
                                        if (!EmptyUtil.isEmpty(joinClassEntity)) {
                                            // String role = getOnlineClassRoleInfo(joinClassEntity);
                                            // JoinClassDetailActivity.show(activity,vo.getClassId(),joinClassEntity.getSchoolId(),Integer.parseInt(vo.getCourseId()),role,false);
                                            boolean needToJoin = joinClassEntity.isIsInClass();
                                            String role = getOnlineClassRoleInfo(joinClassEntity);
                                            if (needToJoin || OnlineClassRole.ROLE_TEACHER.equals(role)) {
                                                // 已经加入班级 或者是老师身份
                                                JoinClassDetailActivity.show(activity, joinClassEntity.getClassId(), joinClassEntity.getSchoolId(), Integer.parseInt(vo.getCourseId()), role, false);
                                            } else {
                                                // 未加入班级
                                                ClassDetailActivity.show(activity, joinClassEntity.getClassId(), joinClassEntity.getSchoolId(), Integer.parseInt(vo.getCourseId()), role, false);
                                            }
                                        }
                                    }
                                });

                            }
                        }
                    }
                } else if (vo.getStatus() == PayStatus.PAY_FAILURE) {//交易关闭
                    // TODO: 2017/7/18 删除订单
                    CustomDialog.Builder builder = new CustomDialog.Builder(activity);
                    builder.setMessage(activity.getResources().getString(R.string.confirm)
                            + activity.getResources().getString(R.string.delete_order)
                            + "?");
                    builder.setTitle(activity.getResources().getString(R.string.tip));
                    builder.setPositiveButton(activity.getResources().getString(R.string.confirm),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    doCancelOrDeleteOrder(vo, holder);
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
            }
        });

        if (vo.getType() == 1) {
            holder.teacher_name.setVisibility(View.VISIBLE);
            // 直播
            XImageLoader.loadImage(holder.course_iv,
                    vo.getThumbnailUrl(),
                    imageOptions);
        } else if (vo.getType() == 0) {
            holder.teacher_name.setVisibility(View.GONE);
            // 课程
            XImageLoader.loadImage(holder.course_iv,
                    vo.getThumbnailUrl(),
                    imageOptions);
        } else if (vo.getType() == 6) {
            // 帮辅订单
            if (vo.getTaskType() == 5) {
                XImageLoader.loadImage(holder.course_iv,
                        vo.getThumbnailUrl(),
                        courseImageOptions);
            } else if (vo.getTaskType() == 8) {
                XImageLoader.loadImage(holder.course_iv,
                        vo.getThumbnailUrl(),
                        orderImageOptions);
            }
        } else if (vo.getType() == 7) {
            // 加入班级
            XImageLoader.loadImage(holder.course_iv,
                    vo.getThumbnailUrl(),
                    classImageOptions);
        } else {
            holder.teacher_name.setVisibility(View.VISIBLE);
            // 空中课堂
            XImageLoader.loadImage(holder.course_iv,
                    vo.getThumbnailUrl(),
                    imageOptions);
        }

        if (vo.getType() == 0) {
            // 学生
            holder.mTvBuyType.setVisibility(View.VISIBLE);
            if (vo.isBuyAll()) {
                // 全部购买
                holder.mTvBuyType.setText(R.string.label_buy_all);
            } else {
                holder.mTvBuyType.setText(String.format(UIUtil.getString(R.string.label_order_buy_number_chapter), vo.getBuyChapterNum()));
            }
        } else if (vo.getType() == 6) {
            holder.mTvBuyType.setVisibility(View.VISIBLE);
            holder.mTvBuyType.setText(R.string.label_tutorial);
        } else {
            holder.mTvBuyType.setVisibility(View.GONE);
        }


        if (vo.getType() == 1 || vo.getType() == 0) {
            // 课程和直播
            FrameLayout.LayoutParams layoutParams =
                    (FrameLayout.LayoutParams) holder.course_iv.getLayoutParams();
            layoutParams.width = img_width;
            layoutParams.height = img_height;
            holder.course_iv.setLayoutParams(layoutParams);
            // holder.coverLay.setLayoutParams(new LinearLayout.LayoutParams(img_width, img_height));
        } else if (vo.getType() != 6) {
                // 空中课堂
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) holder.course_iv.getLayoutParams();
                layoutParams.width = img_height;
                layoutParams.height = img_width;
                holder.course_iv.setLayoutParams(layoutParams);
                // holder.coverLay.setLayoutParams(new RelativeLayout.LayoutParams(img_height, img_width));
            }

        return convertView;
    }

    /**
     * 根据订单信息获取要显示购买方，支付方，受益方的信息
     *
     * @param vo 订单信息
     * @return 我买给谁, 谁买给我
     */
    private String getViewNameByVo(@NonNull MyOrderVo vo) {
        // 购买者
        String buyerId = vo.getBuyerId();
        // 付款者
        String payerId = vo.getPayerId();
        // 受益人的Id
        String memberId = vo.getMemberId();

        String formatName = null;
        if (EmptyUtil.isEmpty(buyerId)) {
            // 没有下单的人,说明自己买
            return null;
        } else {
            // buyerId不为空
            if (EmptyUtil.isEmpty(payerId)) {
                // 付款人为空，说明下单但是未付款
                if (TextUtils.equals(buyerId, memberId)) {
                    // 下单人等于受益人 自己买
                    return null;
                } else {
                    if (TextUtils.equals(memberId, UserHelper.getUserId())) {
                        // 获益人是自己
                        // 别人买给我
                        formatName = String.format(UIUtil.getString(R.string.label_who_buyer_self), vo.getBuyerName());
                    } else {
                        // 我买给别人
                        formatName = String.format(UIUtil.getString(R.string.label_self_buyer_who), vo.getRealName());
                    }
                }
            } else {
                if (TextUtils.equals(payerId, memberId)) {
                    // 付款人==受益人
                    // 自己买
                    return null;
                } else {
                    if (TextUtils.equals(payerId, UserHelper.getUserId())) {
                        // 我买给别人 受益者姓名
                        formatName = String.format(UIUtil.getString(R.string.label_self_buyer_who), vo.getRealName());
                    } else {
                        // 别人买给我
                        formatName = String.format(UIUtil.getString(R.string.label_who_buyer_self), vo.getPayerName());
                    }
                }

            }
        }

        return formatName;
    }

    /**
     * 根据订单信息获取要显示购买方，支付方，受益方的信息
     *
     * @param vo 订单信息
     * @return PayDirection 我买给谁,谁买给我
     */
    private PayDirection getPayDirection(@NonNull MyOrderVo vo) {
        // 购买者
        String buyerId = vo.getBuyerId();
        // 付款者
        String payerId = vo.getPayerId();
        // 受益人的Id
        String memberId = vo.getMemberId();

        String formatName = null;
        if (EmptyUtil.isEmpty(buyerId)) {
            // 没有下单的人,说明自己买
            return PayDirection.SELF_TO_SELF;
        } else {
            // buyerId不为空
            if (EmptyUtil.isEmpty(payerId)) {
                // 付款人为空，说明下单但是未付款
                if (TextUtils.equals(buyerId, memberId)) {
                    // 下单人等于受益人 自己买
                    return PayDirection.SELF_TO_SELF;
                } else {
                    if (TextUtils.equals(memberId, UserHelper.getUserId())) {
                        // 获益人是自己
                        // 别人买给我
                        return PayDirection.OTHER_TO_SELF;
                    } else {
                        // 我买给别人
                        return PayDirection.SELF_TO_OTHER;
                    }
                }
            } else {
                if (TextUtils.equals(payerId, memberId)) {
                    // 付款人==受益人
                    // 自己买
                    return PayDirection.SELF_TO_SELF;
                } else {
                    if (TextUtils.equals(payerId, UserHelper.getUserId())) {
                        // 我买给别人 受益者姓名
                        return PayDirection.SELF_TO_OTHER;
                    } else {
                        // 别人买给我
                        return PayDirection.OTHER_TO_SELF;
                    }
                }

            }
        }
    }

    private enum PayDirection {
        // 自己买给自己,买给其他人,其他人买给自己
        SELF_TO_SELF, SELF_TO_OTHER, OTHER_TO_SELF
    }

    /**
     * 跳转到支付页面
     *
     * @param vo 订单实体
     */
    private void toPayActivity(@NonNull MyOrderVo vo) {

    }

    /**
     * 获取空中课堂角色信息
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
     * 取消或删除订单
     *
     * @param
     */
    private void doCancelOrDeleteOrder(final MyOrderVo vo, final ViewHolder holder) {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("id", vo.getId());
        RequestParams params =
                new RequestParams((vo.getStatus() == PayStatus.PAY_CANCEL ?
                        AppConfig.ServerUrl.CancelOrderById :
                        AppConfig.ServerUrl.DeleteOrderById) + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                ResponseVo<String> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<String>>() {
                        });
                if (result.getCode() == 0) {
                    ToastUtil.showToast(activity, activity.getResources().getString(
                            vo.getStatus() == PayStatus.PAY_CANCEL ? R.string.cancel_order
                                    : R.string.delete_order)
                            + activity.getResources().getString(R.string.success));

                    if (vo.getStatus() == PayStatus.PAY_FAILURE
                            || vo.getStatus() == PayStatus.PAY_OK) {
                        activity.setResult(Activity.RESULT_OK);
                        list.remove(vo);
                    } else {
                        vo.setStatus(PayStatus.PAY_FAILURE);
                    }
                    notifyDataSetChanged();
                } else {
                    ToastUtil.showToast(activity, activity.getResources().getString(
                            vo.getStatus() == PayStatus.PAY_CANCEL ? R.string.cancel_order
                                    : R.string.delete_order)
                            + activity.getResources().getString(R.string.failed)
                            + " error code:" + result.getCode());
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {

                ToastUtil.showToast(activity, activity.getResources().getString(R.string.net_error_tip));
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private class ViewHolder {
        RelativeLayout coverLay;
        ImageView course_iv;
        TextView course_name;
        TextView teacher_name;
        TextView course_price;
        TextView order_number_tv;
        TextView order_create_time_tv;
        TextView organName;
        TextView status_tv;
        TextView rebuy_tv;
        TextView delete_order_tv;
        ImageView imageViewAngleLive;
        TextView mTvBuyType;
        // 买给谁,谁买给我
        FrameLayout mBuyerLayout;
        TextView mtvBuyer;
        TextView mTvCourseType;
        TextView mTutorStatusTv;

        public ViewHolder(View parentView) {
            coverLay = (RelativeLayout) parentView.findViewById(R.id.cover_lay);
            course_iv = (ImageView) parentView.findViewById(R.id.course_iv);
            course_name = (TextView) parentView.findViewById(R.id.course_name);
            teacher_name = (TextView) parentView.findViewById(R.id.teacher_name);
            course_price = (TextView) parentView.findViewById(R.id.course_price);
            order_number_tv = (TextView) parentView.findViewById(R.id.order_number_tv);
            order_create_time_tv = (TextView) parentView.findViewById(R.id.order_create_time_tv);
            organName = (TextView) parentView.findViewById(R.id.organ_name);
            status_tv = (TextView) parentView.findViewById(R.id.status_tv);
            rebuy_tv = (TextView) parentView.findViewById(R.id.rebuy_tv);
            delete_order_tv = (TextView) parentView.findViewById(R.id.delete_order_tv);
            imageViewAngleLive = (ImageView) parentView.findViewById(R.id.angle_live_iv);
            mTvBuyType = (TextView) parentView.findViewById(R.id.tv_buy_type);
            mBuyerLayout = (FrameLayout) parentView.findViewById(R.id.buyer_layout);
            mtvBuyer = (TextView) parentView.findViewById(R.id.tv_buyer);
            mTvCourseType = (TextView) parentView.findViewById(R.id.tv_course_type);
            mTutorStatusTv = (TextView) parentView.findViewById(R.id.tutor_status_tv);

        }
    }

    /**
     * 下拉刷新设置数据
     *
     * @param list
     */
    public void setData(List<MyOrderVo> list) {
        if (list != null) {
            this.list = new ArrayList<MyOrderVo>(list);
        } else {
            this.list.clear();
        }
    }

    /**
     * 上拉加载更多，新增数据
     *
     * @param list
     */
    public void addData(List<MyOrderVo> list) {
        this.list.addAll(list);
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
                    activity.sendBroadcast(new Intent().setAction(AppConfig.ServerUrl.joinInCourse));
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

    /**
     * 课程直播重新加入，空中课堂不许reJoin
     *
     * @param courseId
     * @param type
     */
    private void doRejoin(String courseId, final int type) {
        RequestVo requestVo = new RequestVo();
        if (type == 1) {
            requestVo.addParams("liveId", courseId);
            requestVo.addParams("type", 1);
        } else if (type == 0) {
            requestVo.addParams("courseId", courseId);
        }
        RequestParams params =
                new RequestParams((type == 1 ? AppConfig.ServerUrl.AddToMyLive
                        : AppConfig.ServerUrl.reJoinInCourse) + requestVo.getParams());

        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                ResponseVo<String> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<String>>() {
                        });
                if (result.getCode() == 0) {
                    ToastUtil.showToast(activity, (activity.getResources().getString(
                            type == 1 ? R.string.join_live_success : R.string.join_success)));
                    if (onContentChangedListener != null) {
                        onContentChangedListener.OnContentChanged();
                    }
                    if (type == 1) {
                        activity.sendBroadcast(new Intent().setAction(AppConfig.ServerUrl.AddToMyLive));
                    } else {
                        activity.sendBroadcast(new Intent().setAction(AppConfig.ServerUrl.joinInCourse));
                    }
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                ToastUtil.showToast(activity, activity.getResources().getString(R.string.net_error_tip));
            }

            @Override
            public void onFinished() {
            }
        });
    }
}
