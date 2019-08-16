package com.lqwawa.intleducation.module.onclass.detail.base.comment;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.utils.LogUtil;
import com.lqwawa.intleducation.base.utils.SpannableUtils;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.LinkTouchMovementMethod;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.ui.CommonReplyView;
import com.lqwawa.intleducation.common.ui.CustomDialog;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.StringUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.ClassDetailEntity;
import com.lqwawa.intleducation.module.discovery.tool.LoginHelper;
import com.lqwawa.intleducation.module.discovery.vo.CommentVo;
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
 * @author mrmedici
 * @desc 班级评论的Adapter
 */
public class ClassCommentAdapter extends RecyclerAdapter<CommentVo> {

    private Activity activity;
    private ClassDetailEntity mClassDetailEntity;
    private int startLevel;
    OnContentChangedListener onContentChangedListener;

    private ReplyListener mListener;

    public ClassCommentAdapter(Activity activity, OnContentChangedListener listener) {
        super();
        this.activity = activity;
        this.onContentChangedListener = listener;
    }

    public interface ReplyListener{

        void replyComment(@NonNull CommentVo vo, @NonNull String content);

    }

    public interface OnContentChangedListener {
        void OnContentChanged();
    }

    /**
     * 当班级信息接收的时候,刷新Adapter
     * @param classDetailEntity
     */
    public void setData(@NonNull ClassDetailEntity classDetailEntity,@NonNull ReplyListener listener){
        this.mClassDetailEntity = classDetailEntity;
        this.mListener = listener;
    }

    /**
     * 更新课程评价数据,设置评分等级
     * @param startLevel 评分等级
     */
    public void setData(@IntRange(from = 0,to = 5) int startLevel){
        // 不知道后台返回的数据有没有将已经评分过得评论都进行处理
        // 没有进行评分处理
        this.startLevel = startLevel;
    }

    @Override
    protected int getItemViewType(int position, CommentVo commentVo) {
        if(commentVo.getType() == 0){
            return R.layout.item_comment_layout;
        }else{
            return R.layout.item_reply_comment_layout;
        }
    }

    @Override
    protected ViewHolder<CommentVo> onCreateViewHolder(View root, int viewType) {
        if(viewType == R.layout.item_comment_layout){
            return new CommentViewHolder(root);
        }else if(viewType == R.layout.item_reply_comment_layout){
            return new ReplyCommentViewHolder(root);
        }
        return null;
    }

    /**
     * 评论的ViewHolder
     */
    private class CommentViewHolder extends ViewHolder<CommentVo>{

        private ImageView userHeadIv;
        private TextView nickNameTv;
        private RatingBar gradeRatingBar;
        private TextView commentDeleteTv;
        private TextView commentPraiseTv;
        private TextView commentContentTv;
        private TextView commentTimeTv;
        private TextView commentReplyTv;
        ImageOptions imageOptions;


        public CommentViewHolder(View itemView) {
            super(itemView);

            imageOptions = XImageLoader.buildImageOptions(ImageView.ScaleType.CENTER_CROP,
                    R.drawable.user_header_def, false, true, null);

            userHeadIv = (ImageView) itemView.findViewById(R.id.user_head_iv);
            nickNameTv = (TextView) itemView.findViewById(R.id.nick_name_tv);
            gradeRatingBar = (RatingBar) itemView.findViewById(R.id.grade_rating_bar);
            commentTimeTv = (TextView) itemView.findViewById(R.id.comment_time_tv);
            commentDeleteTv = (TextView) itemView.findViewById(R.id.comment_delete_tv);
            commentPraiseTv = (TextView) itemView.findViewById(R.id.comment_praise_tv);
            commentReplyTv = (TextView) itemView.findViewById(R.id.comment_reply_tv);
            commentContentTv = (TextView) itemView.findViewById(R.id.comment_content_tv);
        }

        @Override
        protected void onBind(CommentVo vo) {
            String nickName = anonymityNickName(vo);


            XImageLoader.loadImage(userHeadIv
                    , (vo.getThumbnail() + "").trim()
                    , imageOptions);

            gradeRatingBar.setRating(Float.parseFloat(vo.getStarLevel()));
            StringUtil.fillSafeTextView(nickNameTv,nickName);
            StringUtil.fillSafeTextView(commentPraiseTv,vo.getPraiseNum());
            StringUtil.fillSafeTextView(commentReplyTv,vo.getReplyNum());
            StringUtil.fillSafeTextView(commentContentTv,vo.getContent());
            StringUtil.fillSafeTextView(commentTimeTv,vo.getCreateTime());

            commentDeleteTv.setVisibility(View.GONE);
            boolean isHaveDelPermissions = false;
            if (UserHelper.isLogin()) {
                if (UserHelper.getUserId().equals(vo.getCreateId())) {
                    isHaveDelPermissions = true;
                }
            }

            //点击删除按钮 删除自己的评论
            if (isHaveDelPermissions) {
                commentDeleteTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteComment(vo);
                    }
                });
            }


            // 没有评分,隐藏评分
            if(UserHelper.getUserId().equals(vo.getCreateId()) && startLevel != 0){
                // 是自己发送的评论
                // @date   :2018/4/24 0024 下午 3:47
                // @func   :不应该用外面带进来的评分，那是用户信息显示的评分，别的用户看不到评分
                gradeRatingBar.setRating(startLevel);
                // holder.gradeRatingBar.setRating(Float.valueOf(vo.getStarLevel()).intValue());
                gradeRatingBar.setVisibility(View.VISIBLE);
            }else{
                if(Float.valueOf(vo.getStarLevel()).intValue() == 0){
                    gradeRatingBar.setVisibility(View.INVISIBLE);
                }else{
                    gradeRatingBar.setVisibility(View.VISIBLE);
                }
            }

            //点赞
            commentPraiseTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (UserHelper.isLogin()) {
                        praiseComment(vo);
                    }else{
                        LoginHelper.enterLogin(activity);
                    }
                }
            });

            final String toName = nickName;

            //评论
            commentReplyTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (UserHelper.isLogin()) {
                        replyComment(vo,toName);
                    }else{
                        LoginHelper.enterLogin(activity);
                    }
                }
            });
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
    }

    /**
     * 回复评论的ViewHolder
     */
    private class ReplyCommentViewHolder extends ViewHolder<CommentVo>{

        private TextView commentContentTv;
        private TextView commentTimeTv;

        public ReplyCommentViewHolder(View itemView) {
            super(itemView);
            commentTimeTv = (TextView) itemView.findViewById(R.id.comment_time_tv);
            commentContentTv = (TextView) itemView.findViewById(R.id.comment_content_tv);
        }

        @Override
        protected void onBind(CommentVo vo) {
            String nickName = anonymityNickName(vo);

            final String toName = nickName;

            SpannableString nameSpannable =
                    SpannableUtils.createClickSpannable((nickName + "").trim(),
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
            commentContentTv.setText(nameSpannable);
            commentContentTv.append(("：" + vo.getContent()).trim());
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

            commentContentTv.setMovementMethod(new LinkTouchMovementMethod());
            commentContentTv.setLongClickable(false);

            commentTimeTv.setText(vo.getCreateTime());


        }
    }

    /**
     * 是否匿名评论或者回复发送姓名
     *
     */
    private String anonymityNickName(@NonNull CommentVo vo){
        String createName = vo.getCreateName();

        if(EmptyUtil.isNotEmpty(mClassDetailEntity) &&
                EmptyUtil.isNotEmpty(mClassDetailEntity.getData()) &&
                !UserHelper.checkOnlineCourseAuthor(mClassDetailEntity.getData().get(0)) &&
                !vo.getCreateId().equals(UserHelper.getUserId())){
            // 自己是学生且当前评论人不是自己
            if(!mClassDetailEntity.getData().get(0).getTeachersId().contains(vo.getCreateId())){
                // 该评论不是老师，助教，辅导老师三个身份之一
                // 裁剪+匿名字符串
                if(!EmptyUtil.isEmpty(createName) && createName.length() > 1){
                    // 名字不够长 还匿名什么？当别人不存在嘛 全**
                    createName = createName.substring(0,1)+ UIUtil.getString(R.string.label_course_comment_encryption_name);
                }
            }
        }

        return createName;
    }



    /**
     * 下拉刷新设置数据
     *
     * @param list
     */
    public void setData(List<CommentVo> list) {
        List<CommentVo> items = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            CommentVo vo = list.get(i);
            items.add(vo);
            if (vo.getChildren() != null && vo.getChildren().size() > 0) {
                List<CommentVo> childList = vo.getChildren();
                for (int j = 0; j < childList.size(); j++) {
                    CommentVo childVo = childList.get(j);
                    childVo.setId(vo.getId());
                    items.add(childVo);
                }
            }
        }

        replace(items);
    }

    /**
     * 上拉加载更多，新增数据
     *
     * @param list
     */
    public void addData(List<CommentVo> list) {
        List<CommentVo> newItems = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            CommentVo vo = list.get(i);
            newItems.add(vo);
            if (vo.getChildren() != null && vo.getChildren().size() > 0) {
                List<CommentVo> childList = vo.getChildren();
                for (int j = 0; j < childList.size(); j++) {
                    CommentVo childVo = childList.get(j);
                    childVo.setId(vo.getId());
                    newItems.add(childVo);
                }
            }
        }

        add(newItems);
    }
}
