package com.lqwawa.intleducation.module.onclass.detail.base.introduction;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.ImageUtil;
import com.lqwawa.intleducation.factory.data.entity.ClassDetailEntity;

import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 课堂简介授课老师的Adapter
 * @date 2018/06/02 10:54
 * @history v1.0
 * **********************************
 */
public class TeacherAdapter extends RecyclerAdapter<ClassDetailEntity.TeacherBean>{

    public TeacherAdapter(List<ClassDetailEntity.TeacherBean> teacherBeans) {
        super(teacherBeans, null);
    }

    @Override
    protected int getItemViewType(int position, ClassDetailEntity.TeacherBean teacherBean) {
        return R.layout.item_online_class_introduction_teacher_layout;
    }

    @Override
    protected RecyclerAdapter.ViewHolder<ClassDetailEntity.TeacherBean> onCreateViewHolder(View root, int viewType) {
        return new ViewHolder(root);
    }

    private class ViewHolder extends RecyclerAdapter.ViewHolder<ClassDetailEntity.TeacherBean>{

        // 老师肖像
        private ImageView mTeacherAvatar;
        // 老师名称
        private TextView mTeacherName;

        public ViewHolder(View itemView) {
            super(itemView);

            mTeacherAvatar = (ImageView) itemView.findViewById(R.id.iv_teacher_avatar);
            mTeacherName = (TextView) itemView.findViewById(R.id.tv_teacher_name);
        }

        @Override
        protected void onBind(ClassDetailEntity.TeacherBean teacherBean) {
            ImageUtil.fillDefaultView(mTeacherAvatar,teacherBean.getThumbnail());
            mTeacherName.setText(EmptyUtil.isEmpty(teacherBean) ? "" : teacherBean.getUserName());
        }
    }

}
