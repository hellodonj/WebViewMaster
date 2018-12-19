package com.lqwawa.intleducation.module.onclass.school;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.ImageUtil;
import com.lqwawa.intleducation.factory.data.entity.LQTeacherEntity;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 在线课堂老师列表的Adapter
 * @date 2018/06/05 15:49
 * @history v1.0
 * **********************************
 */
public class SchoolTeacherAdapter extends RecyclerAdapter<LQTeacherEntity>{

    @Override
    protected int getItemViewType(int position, LQTeacherEntity lqTeacherEntity) {
        return R.layout.item_school_info_teacher_layout;
    }

    @Override
    protected RecyclerAdapter.ViewHolder<LQTeacherEntity> onCreateViewHolder(View root, int viewType) {
        return new ViewHolder(root);
    }

    private class ViewHolder extends RecyclerAdapter.ViewHolder<LQTeacherEntity>{

        private ImageView mTeacherAvatar;
        private TextView mTeacherName;

        public ViewHolder(View itemView) {
            super(itemView);
            mTeacherAvatar = (ImageView) itemView.findViewById(R.id.iv_teacher_avatar);
            mTeacherName = (TextView) itemView.findViewById(R.id.tv_teacher_name);
        }

        @Override
        protected void onBind(LQTeacherEntity entity) {
            ImageUtil.fillDefaultView(mTeacherAvatar,entity.getHeadPicUrlSrc());
            mTeacherName.setText(EmptyUtil.isEmpty(entity.getRealName()) ?
                    (EmptyUtil.isEmpty(entity.getNickName()) ? "" : entity.getNickName()) :
                    entity.getRealName());
        }
    }
}
