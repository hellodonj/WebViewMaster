package com.lqwawa.intleducation.module.discovery.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseAdapter;
import com.lqwawa.intleducation.base.utils.LogUtil;
import com.lqwawa.intleducation.base.utils.SpannableUtils;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.LinkTouchMovementMethod;
import com.lqwawa.intleducation.common.ui.CommonReplyView;
import com.lqwawa.intleducation.common.ui.CustomDialog;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.ClassDetailEntity;
import com.lqwawa.intleducation.module.discovery.tool.LoginHelper;
import com.lqwawa.intleducation.module.discovery.vo.CommentVo;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.osastudio.common.utils.XImageLoader;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by XChen on 2016/11/14.
 * email:man0fchina@foxmail.com
 */

public class CourseCommentAdapter extends MyBaseAdapter {
    private Activity activity;
    private List<CommentVo> list;
    private LayoutInflater inflater;
    ImageOptions imageOptions;
    OnContentChangedListener onContentChangedListener;
    int p_width;

    public CourseCommentAdapter(Activity activity, OnContentChangedListener listener) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        list = new ArrayList<CommentVo>();
        this.onContentChangedListener = listener;
        p_width = activity.getWindowManager().getDefaultDisplay().getWidth();
        imageOptions = XImageLoader.buildImageOptions(ImageView.ScaleType.CENTER_CROP,
                R.drawable.user_header_def, false, true, null);
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
        ViewHolder holder = null;
        final CommentVo vo = list.get(position);
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
            if (holder.type != vo.getType()) {
                holder = null;
            }
        }
        if (holder == null) {
            if (vo.getType() == 0) {
                convertView = inflater.inflate(R.layout.item_course_comment_list, null);
            } else {
                convertView = inflater.inflate(R.layout.item_course_reply_list, null);
            }
            holder = new ViewHolder(convertView);
            holder.type = vo.getType();
            convertView.setTag(holder);
        }
        String createName = vo.getCreateName();
        // @date   :2018/4/11 0011 下午 2:51
        // @func   :对发送评论者进行名字混淆
        if((EmptyUtil.isNotEmpty(courseVo) &&
                !(UserHelper.isCourseTeacher(courseVo) || UserHelper.isCourseTutor(courseVo)) &&
                !vo.getCreateId().equals(UserHelper.getUserId()))){
            // 自己是学生且当前评论人不是自己  自己是辅导老师,查看学生也匿名
            //if(UserHelper.getRoleWithCourse(vo.getCreateId(),courseVo) != UserHelper.MoocRoleType.TEACHER){
            // 该评论不是老师，助教，辅导老师三个身份之一
            if(UserHelper.getRoleWithCourse(vo.getCreateId(),courseVo) == UserHelper.MoocRoleType.STUDENT ||
                    UserHelper.isCourseCounselor(vo.getCreateId(),courseVo,false)){
                // 该评论是学生或者辅导老师
                // 裁剪+匿名字符串
                if(!EmptyUtil.isEmpty(createName) && createName.length() > 1){
                    // 名字不够长 还匿名什么？当别人不存在嘛 全**
                    createName = createName.substring(0,1)+ UIUtil.getString(R.string.label_course_comment_encryption_name);
                }
                /*if(!UserHelper.isCourseCounselor(courseVo)){
                    // 该评论不是辅导老师
                    // @date   :2018/6/25 0025 下午 5:27
                    // @func   :V5.8更改,辅导老师，评论也是匿名的
                    // 裁剪+匿名字符串
                    if(!EmptyUtil.isEmpty(createName) && createName.length() > 1){
                        // 名字不够长 还匿名什么？当别人不存在嘛 全**
                        createName = createName.substring(0,1)+ UIUtil.getString(R.string.label_course_comment_encryption_name);
                    }
                }*/
            }
        }

        if(EmptyUtil.isNotEmpty(classDetailEntity) &&
                EmptyUtil.isNotEmpty(classDetailEntity.getData()) &&
                !UserHelper.checkOnlineCourseAuthor(classDetailEntity.getData().get(0)) &&
                !vo.getCreateId().equals(UserHelper.getUserId())){
            // 自己是学生且当前评论人不是自己
            if(!classDetailEntity.getData().get(0).getTeachersId().contains(vo.getCreateId())){
                // 该评论不是老师，助教，辅导老师三个身份之一
                // 裁剪+匿名字符串
                if(!EmptyUtil.isEmpty(createName) && createName.length() > 1){
                    // 名字不够长 还匿名什么？当别人不存在嘛 全**
                    createName = createName.substring(0,1)+ UIUtil.getString(R.string.label_course_comment_encryption_name);
                }
            }
        }

        if (vo.getType() == 0) {
            // @date   :2018/4/11 0011 上午 10:59
            // @func   :评论类型
            XImageLoader.loadImage(holder.userHeadIv, (vo.getThumbnail() + "").trim(), imageOptions);
            holder.gradeRatingBar.setRating(Float.parseFloat(vo.getStarLevel()));
            holder.nickNameTv.setText(createName);
            holder.commentPraiseTv.setText(vo.getPraiseNum());
            holder.commentReplyTv.setText(vo.getReplyNum());
            holder.commentContentTv.setText(vo.getContent());
            holder.commentTimeTv.setText(vo.getCreateTime());

            boolean isHaveDelPermissions = false;
            if (UserHelper.isLogin()) {
                if (UserHelper.getUserId().equals(vo.getCreateId())) {
                    isHaveDelPermissions = true;
                }
            }
            holder.commentDeleteTv.setVisibility(View.GONE);
            // 没有评分,隐藏评分
            if(UserHelper.getUserId().equals(vo.getCreateId()) && startLevel != 0){
                // 是自己发送的评论
                // @date   :2018/4/24 0024 下午 3:47
                // @func   :不应该用外面带进来的评分，那是用户信息显示的评分，别的用户看不到评分
                holder.gradeRatingBar.setRating(startLevel);
                // holder.gradeRatingBar.setRating(Float.valueOf(vo.getStarLevel()).intValue());
                holder.gradeRatingBar.setVisibility(View.VISIBLE);
            }else{
                if(Float.valueOf(vo.getStarLevel()).intValue() == 0){
                    holder.gradeRatingBar.setVisibility(View.INVISIBLE);
                }else{
                    holder.gradeRatingBar.setVisibility(View.VISIBLE);
                }
            }
            //点击删除按钮 删除自己的评论
            if (isHaveDelPermissions) {
                holder.commentDeleteTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteComment(vo);
                    }
                });
            }
            //点赞
            holder.commentPraiseTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (UserHelper.isLogin()) {
                        praiseComment(vo);
                    }else{
                        LoginHelper.enterLogin(activity);
                    }
                }
            });

            final String toName = createName;

            //评论
            holder.commentReplyTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (UserHelper.isLogin()) {
                        replyComment(vo,toName);
                    }else{
                        LoginHelper.enterLogin(activity);
                    }
                }
            });
        } else {

            final String toName = createName;

            SpannableString nameSpannable =
                    SpannableUtils.createClickSpannable((createName + "").trim(),
                            activity.getResources().getColor(R.color.com_text_green),
                            activity.getResources().getColor(R.color.com_text_green),
                            /*activity.getResources().getColor(R.color.com_bg_trans_gray),*/
                            activity.getResources().getColor(R.color.transparent),
                            new SpannableUtils.OnClickListener() {
                                @Override
                                public void OnClick(View widget) {
                                    /*if (!UserHelper.isLogin()) {//未登录状态 跳转到登陆界面
                                        LoginHelper.enterLogin(activity);
                                        return;
                                    }
                                    replyComment(vo,toName);*/
                                }
                            });
            holder.commentContentTv.setText(nameSpannable);
            holder.commentContentTv.append(("：" + vo.getContent()).trim());
            /*if (!UserHelper.getUserId().equals(vo.getCreateId())) {//不可以回复自己的回复
                holder.commentContentTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (UserHelper.isLogin()) {
                            replyComment(vo);
                        } else {
                            LoginActivity.loginForResult(activity);
                        }
                    }
                });
            }*/
            /*Paint paint = holder.commentContentTv.getPaint();
            int width = p_width - convertView.getPaddingLeft()
                    - holder.commentContentTv.getPaddingLeft()
                    - holder.commentContentTv.getPaddingRight();
            String text = holder.commentContentTv.getText().toString() + "";
            String timeString = vo.getCreateTime();
            int charWidth = (int) paint.measureText(text, 0, text.length());
            int timeWidth = (int) paint.measureText(timeString, 0, timeString.length());
            if ((width - ((charWidth) % width)) < timeWidth) {*/
                // holder.commentContentTv.append("\r\n");
            //}

            holder.commentContentTv.setMovementMethod(new LinkTouchMovementMethod());
            holder.commentContentTv.setLongClickable(false);

            holder.commentTimeTv.setText(vo.getCreateTime());
        }
        return convertView;
    }

    //删除自己的评论
    private void deleteComment(CommentVo vo) {
        CustomDialog.Builder builder = new CustomDialog.Builder(activity);
        builder.setMessage(activity.getResources().getString(R.string.delete) +
                activity.getResources().getString(R.string.comment)
                + "?");
        builder.setTitle(activity.getResources().getString(R.string.tip));
        builder.setPositiveButton(activity.getResources().getString(R.string.delete),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
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

    //给评论点赞
    private void praiseComment(CommentVo vo) {
        if (!UserHelper.isLogin()) {//未登录状态 跳转到登陆界面
            LoginHelper.enterLogin(activity);
            return;
        }
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("commentId", vo.getId());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.AddPraiseByCommentId + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                ResponseVo<String> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<String>>() {
                        });
                if (result.getCode() == 0) {
                    if (onContentChangedListener != null) {
                        onContentChangedListener.OnContentChanged();
                    }
                    ToastUtil.showToast(activity,
                            activity.getResources().getString(R.string.make_praise)
                                    + activity.getResources().getString(R.string.success)
                                    + "!");
                } else {
                    ToastUtil.showToast(activity,
                            activity.getResources().getString(R.string.make_praise)
                                    + activity.getResources().getString(R.string.failed)
                                    + result.getMessage());
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

    //添加回复
    private void replyComment(@NonNull final CommentVo vo,@NonNull String toName) {
        CommonReplyView.showView(activity, toName, new CommonReplyView.PopupWindowListener() {
            @Override
            public void onBtnSendClickListener(String content) {
                if (content.isEmpty()) {
                    ToastUtil.showToast(activity,
                            activity.getResources().getString(R.string.enter_content_please));
                    return;
                }
                reply(vo, content);
            }
            @Override
            public void onDismiss(String content){

            }
        });
    }

    private void reply(CommentVo vo, String content) {
        if(EmptyUtil.isNotEmpty(mListener)){
            mListener.replyComment(vo,content);
            return;
        }
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("commentId", vo.getId());
        requestVo.addParams("type", 1);
        try {
            String contentString = content.trim();
            contentString = URLEncoder.encode(contentString, "utf-8");
            contentString = contentString.replaceAll("%0A", "\n");
            requestVo.addParams("content", contentString);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.AddCommentOrReply + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                ResponseVo<String> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<String>>() {
                        });
                if (result.getCode() == 0) {
                    if (onContentChangedListener != null) {
                        onContentChangedListener.OnContentChanged();
                    }
                    ToastUtil.showToast(activity,
                            activity.getResources().getString(R.string.commit_reply)
                                    + activity.getResources().getString(R.string.success)
                                    + "!");
                } else {
                    ToastUtil.showToast(activity,
                            activity.getResources().getString(R.string.commit_reply)
                                    + activity.getResources().getString(R.string.failed)
                                    + result.getMessage());
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d("", "拉取入驻机构列表失败:" + throwable.getMessage());

                ToastUtil.showToast(activity, activity.getResources().getString(R.string.net_error_tip));
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private class ViewHolder {
        int type;
        ImageView userHeadIv;
        TextView nickNameTv;
        RatingBar gradeRatingBar;
        TextView commentDeleteTv;
        TextView commentPraiseTv;
        TextView commentReplyTv;
        TextView commentContentTv;
        TextView commentTimeTv;

        public ViewHolder(View parent) {
            commentContentTv = (TextView) parent.findViewById(R.id.comment_content_tv);
            commentTimeTv = (TextView) parent.findViewById(R.id.comment_time_tv);
            userHeadIv = (ImageView) parent.findViewById(R.id.user_head_iv);
            nickNameTv = (TextView) parent.findViewById(R.id.nick_name_tv);
            gradeRatingBar = (RatingBar) parent.findViewById(R.id.grade_rating_bar);
            commentTimeTv = (TextView) parent.findViewById(R.id.comment_time_tv);
            commentDeleteTv = (TextView) parent.findViewById(R.id.comment_delete_tv);
            commentPraiseTv = (TextView) parent.findViewById(R.id.comment_praise_tv);
            commentReplyTv = (TextView) parent.findViewById(R.id.comment_reply_tv);
            commentContentTv = (TextView) parent.findViewById(R.id.comment_content_tv);
        }
    }

    private int startLevel;
    private CourseVo courseVo;
    private ClassDetailEntity classDetailEntity;

    /**
     * 当班级信息接收的时候,刷新Adapter
     * @param classDetailEntity
     */
    public void setData(@NonNull ClassDetailEntity classDetailEntity,@NonNull ReplyListener listener){
        this.classDetailEntity = classDetailEntity;
        this.mListener = listener;
        if(!EmptyUtil.isEmpty(list)){
            // 有数据 指针发生改变,Adapter会刷新
            list = new ArrayList<>(list);
            notifyDataSetChanged();
        }
    }

    private ReplyListener mListener;

    public interface ReplyListener{

        void replyComment(@NonNull CommentVo vo, @NonNull String content);

    }

    /**
     * 当课程信息接收的时候,刷新Adapter
     * @param courseVo
     */
    public void setData(@NonNull CourseVo courseVo){
        this.courseVo = courseVo;
        if(!EmptyUtil.isEmpty(list)){
            // 有数据 指针发生改变,Adapter会刷新
            list = new ArrayList<>(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 更新课程评价数据,设置评分等级
     * @param startLevel 评分等级
     * @param list 数据源
     */
    public void setData(@IntRange(from = 0,to = 5) int startLevel,List<CommentVo> list){
        // 不知道后台返回的数据有没有将已经评分过得评论都进行处理
        // 没有进行评分处理
        this.startLevel = startLevel;
        setData(list);
    }

    /**
     * 下拉刷新设置数据
     *
     * @param list
     */
    public void setData(List<CommentVo> list) {
        if (list != null) {
            if (this.list == null) {
                this.list = new ArrayList<CommentVo>();
            } else {
                this.list.clear();
            }
            for (int i = 0; i < list.size(); i++) {
                CommentVo vo = list.get(i);
                this.list.add(vo);
                if (vo.getChildren() != null && vo.getChildren().size() > 0) {
                    List<CommentVo> childList = vo.getChildren();
                    for (int j = 0; j < childList.size(); j++) {
                        CommentVo childVo = childList.get(j);
                        childVo.setId(vo.getId());
                        this.list.add(childVo);
                    }
                }
            }
        } else {
            this.list.clear();
        }
    }

    /**
     * 上拉加载更多，新增数据
     *
     * @param list
     */
    public void addData(List<CommentVo> list) {
        for (int i = 0; i < list.size(); i++) {
            CommentVo vo = list.get(i);
            this.list.add(vo);
            if (vo.getChildren() != null && vo.getChildren().size() > 0) {
                List<CommentVo> childList = vo.getChildren();
                for (int j = 0; j < childList.size(); j++) {
                    CommentVo childVo = childList.get(j);
                    childVo.setId(vo.getId());
                    this.list.add(childVo);
                }
            }
        }
    }
}
