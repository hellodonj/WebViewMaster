package com.lqwawa.intleducation.module.discovery.ui.videodetail.videocomment;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.image.LQwawaImageUtil;
import com.lqwawa.intleducation.module.discovery.ui.lesson.detail.LessonSourceParams;
import com.lqwawa.intleducation.module.discovery.vo.CommentVo;

/**
 * @author: wangchao
 * @date: 2019/05/08
 * @desc: 视频评论列表Adapter
 */
public class VideoCommentAdapter extends RecyclerAdapter<CommentVo> {

    private LessonSourceParams lessonSourceParams;
    
    public VideoCommentAdapter() {
        
    }

    public void setLessonSourceParams(LessonSourceParams lessonSourceParams) {
        this.lessonSourceParams = lessonSourceParams;
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
                    vo.getHeadPic(), R.drawable.user_header_def);

            String createName = vo.getCreateName();
            boolean isTeacherTutor =
                    lessonSourceParams != null && (lessonSourceParams.isLecturer() || lessonSourceParams.isAutor());
            boolean isMySelf =
                    !TextUtils.isEmpty(lessonSourceParams.getMemberId()) && !TextUtils.isEmpty(vo.getCreateId())
                            && (vo.getCreateId().equals(lessonSourceParams.getMemberId()));
            boolean isFromTeacherTutor =
                    !TextUtils.isEmpty(lessonSourceParams.getTeacherTutorIds())
                            && !TextUtils.isEmpty(vo.getCreateId())
                            && (lessonSourceParams.getTeacherTutorIds().contains(vo.getCreateId()));
            boolean isVisible =  isTeacherTutor || isMySelf || isFromTeacherTutor;
            if (!isVisible && !TextUtils.isEmpty(createName)) {
                createName =
                        createName.substring(0, 1) +  UIUtil.getString(R.string.label_course_comment_encryption_name);
            }
            nickNameTextView.setText(createName);
            
            gradeRatingBar.setVisibility(View.GONE);
            timeTextView.setText(vo.getCreateTime());
            praiseTextView.setVisibility(View.GONE);
            replyTextView.setVisibility(View.GONE);
            contentTextView.setText(vo.getContent());
            customDivider.setVisibility(View.GONE);
        }
    }
}
