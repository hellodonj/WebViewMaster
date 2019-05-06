package com.lqwawa.intleducation.module.learn.adapter;

import android.app.Activity;
import android.content.DialogInterface;
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
import com.lqwawa.intleducation.base.vo.LqResponseVo;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.LinkTouchMovementMethod;
import com.lqwawa.intleducation.common.ui.CommonReplyView;
import com.lqwawa.intleducation.common.ui.CustomDialog;
import com.lqwawa.intleducation.common.utils.LqServerHelper;
import com.lqwawa.intleducation.module.discovery.tool.LoginHelper;
import com.lqwawa.intleducation.module.learn.vo.LqTaskCommentVo;
import com.lqwawa.intleducation.module.login.ui.LoginActivity;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

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

public class LqTaskCommentAdapter extends MyBaseAdapter {
    private Activity activity;
    private List<LqTaskCommentVo> list;
    private LayoutInflater inflater;
    ImageOptions imageOptions;
    OnContentChangedListener onContentChangedListener;
    int p_width;

    public LqTaskCommentAdapter(Activity activity, OnContentChangedListener listener) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        list = new ArrayList<LqTaskCommentVo>();
        this.onContentChangedListener = listener;
        p_width = activity.getWindowManager().getDefaultDisplay().getWidth();
        imageOptions = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setCrop(false)
                .setCircular(true)//圆形
                .setLoadingDrawableId(R.drawable.user_header_def)//加载中默认显示图片
                .setFailureDrawableId(R.drawable.user_header_def)//加载失败后默认显示图片
                .build();
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
        final LqTaskCommentVo vo = list.get(position);
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
            if (holder.type != vo.getType()) {
                holder = null;
            }
        }
        if (holder == null) {
            if (vo.getType() == 0) {
                convertView = inflater.inflate(R.layout.item_task_comment_layout, null);
            } else {
                convertView = inflater.inflate(R.layout.mod_course_reply_list_item, null);
            }
            holder = new ViewHolder(convertView);
            holder.type = vo.getType();
            convertView.setTag(holder);
        }
        if (vo.getType() == 0) {
            x.image().bind(holder.userHeadIv
                    , LqServerHelper.getFullImgUrl((vo.getCommentHeadPicUrl() + "").trim())
                    , imageOptions);
            holder.gradeRatingBar.setVisibility(View.GONE);
            holder.nickNameTv.setText("" + vo.getCommentName());
            holder.commentPraiseTv.setText("" + vo.getPraiseCount());
            holder.commentContentTv.setText("" + vo.getComments());
            holder.commentTimeTv.setText("" + vo.getCommentTime());
            holder.commentDeleteTv.setVisibility(View.GONE);
            //点赞
            holder.commentPraiseTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (UserHelper.isLogin()) {
                        praiseComment(vo);
                    } else {
                        LoginHelper.enterLogin(activity);
                        // LoginActivity.loginForResult(activity);
                    }
                }
            });
            //评论
            holder.commentReplyTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (UserHelper.isLogin()) {
                        replyComment(vo, false);
                    } else {
                        LoginHelper.enterLogin(activity);
                        // LoginActivity.loginForResult(activity);
                    }
                }
            });
        } else {
            SpannableString nameSpannable =
                    SpannableUtils.createClickSpannable((vo.getCommentName() + "").trim(),
                            activity.getResources().getColor(R.color.com_text_green),
                            activity.getResources().getColor(R.color.com_text_green),
                            activity.getResources().getColor(R.color.com_bg_trans_gray),
                            new SpannableUtils.OnClickListener() {
                                @Override
                                public void OnClick(View widget) {
                                    if(true || !UserHelper.getUserId().equals(vo.getCommentId())) {
                                        replyComment(vo, false);
                                    }
                                }
                            });
            holder.commentContentTv.setText(nameSpannable);
            holder.commentContentTv.append(activity.getResources().getString(R.string.commit_reply));
            SpannableString toNameSpannable =
                    SpannableUtils.createClickSpannable((vo.getCommentToName() + "").trim(),
                            activity.getResources().getColor(R.color.com_text_green),
                            activity.getResources().getColor(R.color.com_text_green),
                            activity.getResources().getColor(R.color.com_bg_trans_gray),
                            new SpannableUtils.OnClickListener() {
                                @Override
                                public void OnClick(View widget) {
                                    if(true || !UserHelper.getUserId().equals(vo.getCommentToId())) {
                                        replyComment(vo, true);
                                    }
                                }
                            });
            holder.commentContentTv.append(toNameSpannable);
            holder.commentContentTv.append(("：" + vo.getComments()).trim());
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
                holder.commentContentTv.append("\r\n");
            //}

            holder.commentContentTv.setMovementMethod(new LinkTouchMovementMethod());
            holder.commentContentTv.setLongClickable(false);

            holder.commentTimeTv.setText(vo.getCommentTime());
        }
        return convertView;
    }

    //删除自己的评论
    private void deleteComment(LqTaskCommentVo vo) {
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
    private void praiseComment(LqTaskCommentVo vo) {
        if (!UserHelper.isLogin()) {//未登录状态 跳转到登陆界面
            LoginHelper.enterLogin(activity);
            return;
        }
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("TaskCommentId", vo.getId());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.LQWW_TASK_ADD_PRAISE);
        params.setAsJsonContent(true);
        params.setBodyContent(requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                LqResponseVo<String> result = JSON.parseObject(s,
                        new TypeReference<LqResponseVo<String>>() {
                        });
                if (result.isHasError() == false) {
                    if (onContentChangedListener != null) {
                        onContentChangedListener.OnContentChanged();
                    }
                    /*ToastUtil.showToast(activity,
                            activity.getResources().getString(R.string.has)
                                    + activity.getResources().getString(R.string.make_praise)
                                    + "!");*/

                    ToastUtil.showToast(activity,
                            activity.getResources().getString(R.string.make_praise)
                                    + activity.getResources().getString(R.string.success)
                                    + "!");
                } else {
                    /*ToastUtil.showToast(activity,
                            activity.getResources().getString(R.string.make_praise)
                                    + activity.getResources().getString(R.string.failed)
                                    + result.getErrorMessage());*/
                    ToastUtil.showToast(activity,
                            activity.getResources().getString(R.string.make_praise)
                                    + activity.getResources().getString(R.string.failed)
                                    + result.getErrorMessage());
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
    private void replyComment(final LqTaskCommentVo vo, final boolean isTo) {
        String hint = isTo ? vo.getCommentToName() : vo.getCommentName();
        CommonReplyView.showView(activity, hint, new CommonReplyView.PopupWindowListener() {
            @Override
            public void onBtnSendClickListener(String content) {
                if (content.isEmpty()) {
                    ToastUtil.showToast(activity,
                            activity.getResources().getString(R.string.enter_content_please));
                    return;
                }
                reply(vo, content, isTo);
            }
            @Override
            public void onDismiss(String content){

            }
        });
    }

    private void reply(LqTaskCommentVo vo, String content, boolean isTo) {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("CommentId", UserHelper.getUserId());
        requestVo.addParams("CommentName", UserHelper.getUserName());
        requestVo.addParams("CommentToId", isTo ? vo.getCommentToId() : vo.getCommentId());
        requestVo.addParams("CommentToName", isTo ? vo.getCommentToName() : vo.getCommentName());
        requestVo.addParams("Comments", content);
        requestVo.addParams("ParentId", vo.getParentId() == 0 ? vo.getId() : vo.getParentId());
        requestVo.addParams("TaskId", vo.getTaskId());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.LQWW_TASK_COMMENT);
        params.setAsJsonContent(true);
        params.setBodyContent(requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                hideKeyboard(activity);
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

    /**
     * 下拉刷新设置数据
     *
     * @param list
     */
    public void setData(List<LqTaskCommentVo> list) {
        if (list != null) {
            if (this.list == null) {
                this.list = new ArrayList<LqTaskCommentVo>();
            } else {
                this.list.clear();
            }
            for (int i = 0; i < list.size(); i++) {
                LqTaskCommentVo vo = list.get(i);
                this.list.add(vo);
                if (vo.getChildren() != null && vo.getChildren().size() > 0) {
                    List<LqTaskCommentVo> childList = vo.getChildren();
                    for (int j = 0; j < childList.size(); j++) {
                        LqTaskCommentVo childVo = childList.get(j);
                        childVo.setTaskId(vo.getTaskId());
                        childVo.setType(1);
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
    public void addData(List<LqTaskCommentVo> list) {
        for (int i = 0; i < list.size(); i++) {
            LqTaskCommentVo vo = list.get(i);
            this.list.add(vo);
            if (vo.getChildren() != null && vo.getChildren().size() > 0) {
                List<LqTaskCommentVo> childList = vo.getChildren();
                for (int j = 0; j < childList.size(); j++) {
                    LqTaskCommentVo childVo = childList.get(j);
                    childVo.setTaskId(vo.getTaskId());
                    this.list.add(childVo);
                }
            }
        }
    }
}
