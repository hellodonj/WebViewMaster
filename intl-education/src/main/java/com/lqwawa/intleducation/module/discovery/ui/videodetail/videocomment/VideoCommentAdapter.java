package com.lqwawa.intleducation.module.discovery.ui.videodetail.videocomment;

import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.image.LQwawaImageUtil;
import com.lqwawa.intleducation.module.discovery.vo.CommentVo;

/**
 * @author: wangchao
 * @date: 2019/05/08
 * @desc: 视频评论列表Adapter
 */
public class VideoCommentAdapter extends RecyclerAdapter<CommentVo> {

    public VideoCommentAdapter() {
        
    }

    @Override
    protected int getItemViewType(int position, CommentVo commentVo) {
        return R.layout.item_course_comment_list;
    }

    @Override
    protected ViewHolder<CommentVo> onCreateViewHolder(View root, int viewType) {
        return new VideoCommentHolder(root);
    }

    private class VideoCommentHolder extends ViewHolder<CommentVo> {

        private ImageView userHeaderImageView;
        private TextView nickNameTextView;
        private RatingBar gradeRatingBar;
        private TextView timeTextView;
        private TextView praiseTextView;
        private TextView replyTextView;
        private TextView contentTextView;
        private View customDivider;

        public VideoCommentHolder(View itemView) {
            super(itemView);
            userHeaderImageView = (ImageView) itemView.findViewById(R.id.user_head_iv);
            nickNameTextView = (TextView) itemView.findViewById(R.id.nick_name_tv);
            gradeRatingBar = (RatingBar) itemView.findViewById(R.id.grade_rating_bar);
            timeTextView = (TextView)itemView.findViewById(R.id.comment_time_tv);
            praiseTextView = (TextView)itemView.findViewById(R.id.comment_praise_tv);
            replyTextView = (TextView)itemView.findViewById(R.id.comment_reply_tv);
            contentTextView = (TextView)itemView.findViewById(R.id.comment_content_tv);
            customDivider = itemView.findViewById(R.id.comment_custom_divider);
        }

        @Override
        protected void onBind(CommentVo vo) {
            LQwawaImageUtil.loadCommonIcon(itemView.getContext(), userHeaderImageView,
                    vo.getThumbnail(), R.drawable.user_header_def);
            nickNameTextView.setText(vo.getCreateName());
            gradeRatingBar.setVisibility(View.GONE);
            timeTextView.setText(vo.getCreateTime());
            praiseTextView.setVisibility(View.GONE);
            replyTextView.setVisibility(View.GONE);
            contentTextView.setText(vo.getContent());
            customDivider.setVisibility(View.GONE);
        }
    }
}
