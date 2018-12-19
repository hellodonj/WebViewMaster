package com.galaxyschool.app.wawaschool.fragment.resource;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.DensityUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.pojo.CommitTask;
import com.galaxyschool.app.wawaschool.pojo.RoleType;

/**
 * 作业通用Helper
 *
 * @param <T>
 */
public abstract class HomeworkCommitResourceAdapterViewHelper<T> extends AdapterViewHelper<T> {

    private Activity activity;
    private int roleType=-1;
    private String memberId;

    public HomeworkCommitResourceAdapterViewHelper(Activity activity,
                                                   AdapterView adapterView,int roleType,
                                                   String memberId) {
        this(activity, adapterView, R.layout.item_commited_homework_new,roleType, memberId);
    }

    public HomeworkCommitResourceAdapterViewHelper(Activity activity,
                                                   AdapterView adapterView, int itemViewLayout,
                                                   int roleType,String memberId) {
        super(activity, adapterView, itemViewLayout);
        this.activity = activity;
        this.memberId = memberId;
        this.roleType=roleType;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        final CommitTask data = (CommitTask) getDataAdapter().getItem(position);
        if (data == null) {
            return view;
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder();
        }
        holder.data = data;

        //头像
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_student_icon);
        if (imageView != null) {
            MyApplication.getThumbnailManager(this.activity).displayUserIconWithDefault(
                    AppSettings.getFileUrl(data.getHeadPicUrl()), imageView,
                    R.drawable.default_user_icon);
            //点击头像进入个人详情
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //游客之类的memberId为空的不给点击。
                    if (!TextUtils.isEmpty(data.getStudentId())) {
                        ActivityUtils.enterPersonalSpace(activity, data.getStudentId());
                    }
                }
            });
        }

        //小红点
        //标识老师是否阅读了学生提交的作业，如果是老师角色，阅读后小红点消失，否则不消失。
        //仅对老师显示
        imageView = (ImageView) view.findViewById(R.id.red_point);
        if (imageView != null) {
            if (roleType==RoleType.ROLE_TYPE_TEACHER){
                if (data.isRead()){
                    imageView.setVisibility(View.INVISIBLE);
                }else {
                    imageView.setVisibility(View.VISIBLE);
                }
            }else {
                imageView.setVisibility(View.INVISIBLE);
            }
        }


        //学生姓名
        TextView textView = (TextView) view.findViewById(R.id.tv_student_name);
        if (textView != null) {
            textView.setText(data.getStudentName());
        }

        //作业图片
        imageView = (ImageView) view.findViewById(R.id.iv_icon);
        if (imageView != null) {
            //之前宽 90 高 120  //设置布局为A4比例
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) imageView.getLayoutParams();
            int width = DensityUtils.dp2px(activity,90);
            layoutParams.width = width;
            layoutParams.height = width * 210/297;
            imageView.setLayoutParams(layoutParams);

            MyApplication.getThumbnailManager(this.activity).displayUserIconWithDefault(
                    AppSettings.getFileUrl(data.getStudentResUrl()), imageView,
                    R.drawable.default_cover);
        }

        //标题
        textView = (TextView) view.findViewById(R.id.tv_title);
        if (textView != null) {
            textView.setText(data.getStudentResTitle());
        }

        //提交时间
        textView = (TextView) view.findViewById(R.id.tv_commit_time);
        if (textView != null) {
            String commitTime = data.getCommitTime();
            if (!TextUtils.isEmpty(commitTime)) {
                if (commitTime.contains(":")) {
                    //精确到分
                    commitTime = commitTime.substring(0,commitTime.lastIndexOf(":"));
                }
                textView.setText(commitTime);
            }
        }
        //显示任务类型
        textView= (TextView) view.findViewById(R.id.commit_type);
        if (textView!=null){
            int commitType=data.getCommitType();
            if (commitType==0){
                textView.setVisibility(View.GONE);
            }else{
                textView.setVisibility(View.VISIBLE);
                if (commitType==1){
                    //改成复述课件
                    textView.setText(R.string.retell_course_new);
                }else if (commitType==2){
                    //改成做任务单
                    textView.setText(R.string.do_task);
                }else if (commitType==3){
                    textView.setText(R.string.ask_question);
                }else if (commitType==4){
                    textView.setText(R.string.create_course);
                }
            }
        }


        view.setTag(holder);
        return view;
    }

    @Override
    public void onItemClick(AdapterView parent, View view, int position, long id) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            return;
        }
        CommitTask data = (CommitTask) holder.data;
//            页面跳转
            if(data != null && !TextUtils.isEmpty(data.getStudentResId())) {
//                StudyTaskOpenHelper.openTask(activity, data.getStudentResId(), null, 0);
                updateLookTaskStatus(data.getCommitTaskId(),data.isRead());
            }
    }

    /**
     * 更新任务查看状态
     * @param commitTaskId
     */
    protected abstract void updateLookTaskStatus(int commitTaskId,boolean isRead);


}
