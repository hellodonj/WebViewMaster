package com.galaxyschool.app.wawaschool.fragment.resource;

import android.app.Activity;
import android.graphics.YuvImage;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.DateUtils;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.pojo.HomeworkListInfo;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.StudyTask;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;

/**
 * 作业通用Helper
 *
 * @param <T>
 */
public abstract class HomeworkResourceAdapterViewHelper<T> extends AdapterViewHelper<T> {

    private Activity activity;
    private int roleType;
    private String memberId;
    private String joni_or_finish_Text;
    private String unjoin_or_unfinish_Text;
    private String finishAllText;
    public String finishStatusText;
    private boolean isHeadMaster;

    public HomeworkResourceAdapterViewHelper(Activity activity,
                                             AdapterView adapterView, int roleType,
                                             String memberId , boolean isHeadMaster) {
        this(activity, adapterView, R.layout.item_common_list_homework, roleType, memberId,
                isHeadMaster);
    }

    public HomeworkResourceAdapterViewHelper(Activity activity,
                                             AdapterView adapterView, int itemViewLayout,
                                             int roleType, String memberId ,boolean isHeadMaster) {
        super(activity, adapterView, itemViewLayout);
        this.activity = activity;
        this.roleType = roleType;
        this.memberId = memberId;
        this.isHeadMaster = isHeadMaster;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        HomeworkListInfo data = (HomeworkListInfo) getDataAdapter().getItem(position);
        if (data == null) {
            return view;
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder();
        }
        holder.data = data;

        //任务类型:0-看微课,1-看课件,2-看作业,3-交作业,4-讨论话题, 导读 6,8-任务单
        String type = data.getTaskType();

        //小红点
       ImageView imageView = (ImageView) view.findViewById(R.id.red_point);
        if (imageView != null) {
            //学生和家长角色对所有作业类型的未读作业均显示小红点
            //目前学生和家长角色需要优先控制“已完成”状态不显示小红点，不管是否阅读
            if (roleType==RoleType.ROLE_TYPE_STUDENT||roleType==RoleType.ROLE_TYPE_PARENT){

                if (data.isStudentIsRead()){
                    imageView.setVisibility(View.INVISIBLE);
                }else {
                    imageView.setVisibility(View.VISIBLE);
                }
            }
            //对于老师角色，只有类型为“交作业”的未读作业才显示。
            //目前老师角色隐藏小红点
            else if (roleType==RoleType.ROLE_TYPE_TEACHER){
                imageView.setVisibility(View.INVISIBLE);
            }

        }

        //开始
        TextView textView = (TextView) view.findViewById(R.id.tv_start_day);

        //开始日期
        String startTime = data.getStartTime();
        //日
        if (textView != null) {
            textView.setText(DateUtils.getDateStr(startTime, 2));
        }
        //星期
        textView = (TextView) view.findViewById(R.id.tv_start_weekday);
        if (textView != null) {
            textView.setText(DateUtils.getWeekDayName(startTime));
        }
        //年份+月份 格式：2016/05
        textView = (TextView) view.findViewById(R.id.tv_start_date);
        if (textView != null) {
            textView.setText(DateUtils.getDateStr(startTime, 0) + "/" +
                    DateUtils.getDateStr(startTime, 1));
        }

        //结束
        textView = (TextView) view.findViewById(R.id.tv_end_day);

        //结束日期
        String endTime = data.getEndTime();
        //日
        if (textView != null) {
            textView.setText(DateUtils.getDateStr(endTime, 2));
        }
        //星期
        textView = (TextView) view.findViewById(R.id.tv_end_weekday);
        if (textView != null) {
            textView.setText(DateUtils.getWeekDayName(endTime));
        }
        ///年份+月份 格式：2016/05
        textView = (TextView) view.findViewById(R.id.tv_end_date);
        if (textView != null) {
            textView.setText(DateUtils.getDateStr(endTime, 0) + "/" +
                    DateUtils.getDateStr(endTime, 1));
        }

        //需提交
        imageView = (ImageView) view.findViewById(R.id.icon_need_to_commit);
        if (imageView != null){
            //只有交作业才显示
            //目前复述微课也要显示
            //目前导读也需要显示
            //英文写作也需要显示
            if (!TextUtils.isEmpty(type)){
                if (type.equals("3")
                        || type.equals("5")
                        || type.equals("6")
                        || type.equals("7")
                        || type.equals("8")
                        || type.equals("10")
                        || type.equals("12")
                        || type.equals("13")
                        || (type.equals("11") && data.isNeedCommit())){
                    imageView.setVisibility(View.VISIBLE);
                }else {
                    imageView.setVisibility(View.GONE);
                }
            }
        }

        //任务类型:0-看微课,1-看课件,2-看作业,3-交作业,4-讨论话题，5-复述微课。

        textView = (TextView) view.findViewById(R.id.tv_homework_type);
        if (textView != null) {
            //初始化默认值
            textView.setBackgroundResource(0);
            if (!TextUtils.isEmpty(type)){
                if (type.equals("0")){
                    //目前“看微课”名称改为“看课件”，图标替换为看课件的图标，原来的看课件暂时不管，仍然隐藏。
                    textView.setBackgroundResource(R.drawable.scan_class_course);
                }else if (type.equals("1")){
                    textView.setBackgroundResource(R.drawable.scan_class_course);
                }else if (type.equals("2")){
                    //看作业
                    textView.setBackgroundResource(R.drawable.icon_other);
                }else if (type.equals("3")){
                    //交作业，目前看作业和交作业背景图片是一致的，只是用“需提交”来区分。
                    textView.setBackgroundResource(R.drawable.icon_other);
                }else if (type.equals("4")){
                    textView.setBackgroundResource(R.drawable.discuss_topic);
                } else if (type.equals("5") || type.equals("12")) {
                    textView.setBackgroundResource(R.drawable.retell_course_ico);
                }else if (type.equals("6")){
                    textView.setBackgroundResource(R.drawable.introduction_type);
                }else if (type.equals("7")){
                    //英文写作
                    textView.setBackgroundResource(R.drawable.english_writing_icon);
                }else if (type.equals("8") || type.equals("13")){
                    //做任务单
                    textView.setBackgroundResource(R.drawable.icon_do_task);
                }else if (type.equals("9")){
                    //新版看课件
                    textView.setBackgroundResource(R.drawable.scan_class_course);
                }else if (type.equals("10")){
                    //听说 + 读写
                    textView.setBackgroundResource(R.drawable.listen_read_and_write_icon);
                }else if (type.equals("11")){
                    textView.setBackgroundResource(R.drawable.icon_super_task);
                }
            }
        }

        //是否是自己发布的作业
        boolean isOwnerTask = !TextUtils.isEmpty(data.getTaskCreateId())
                && !TextUtils.isEmpty(memberId)
                && data.getTaskCreateId().equals(memberId);

        //删除
        View deleteView = view.findViewById(R.id.layout_delete_homework);
        if (deleteView != null) {
            //只有老师显示删除
            if (isHeadMaster){
                deleteView.setVisibility(View.VISIBLE);
                if (roleType == RoleType.ROLE_TYPE_PARENT){
                    deleteView.setVisibility(View.INVISIBLE);
                }
            } else if (roleType == RoleType.ROLE_TYPE_TEACHER){
                //一个班级有可能有很多老师，要根据taskCreateId来判断是不是自己创建的，是的话才能删。
                if (isOwnerTask){
                    //是自己创建的，就显示。
                    deleteView.setVisibility(View.VISIBLE);
                }else {
                    //不是自己创建的，要隐藏。
                    deleteView.setVisibility(View.INVISIBLE);
                }
            } else {
                deleteView.setVisibility(View.INVISIBLE);
            }
        }

        //标题
        textView = (TextView) view.findViewById(R.id.tv_homework_title);
        if (textView != null) {
            textView.setText(data.getTaskTitle());
        }

        //作业布置者,只对发作业的老师隐藏。
        textView = (TextView) view.findViewById(R.id.tv_homework_assigner);
        if (textView != null) {
            //均显示
//            if (type.equals("11")){
//                textView.setVisibility(View.INVISIBLE);
//            } else {
                textView.setVisibility(View.VISIBLE);
                textView.setText(data.getTaskCreateName());
//            }
        }

        //作业状态布局，仅对教师可见。
        TextView homeworkStatusLayout = (TextView) view.findViewById(R.id.tv_finish_status);
        if (homeworkStatusLayout != null) {
            if (roleType == RoleType.ROLE_TYPE_TEACHER) {
                //完成情况仅老师显示
                homeworkStatusLayout.setVisibility(View.VISIBLE);
            } else {
                homeworkStatusLayout.setVisibility(View.INVISIBLE);
            }
        }

        //全部完成的话，就隐藏已参与/未参与/已完成/未完成，显示“全部完成”,反之隐藏。

        //总数
        int taskNum=-1;
        //已完成/已参与任务数
        int finishTaskCount=-1;
        //未完成/未参与任务数
        int unFinishTaskCount=-1;
        taskNum=data.getTaskNum();
        finishTaskCount=data.getFinishTaskCount();
        unFinishTaskCount=taskNum-finishTaskCount;
        //暂时屏蔽负值，直接显示“0”。
        if (unFinishTaskCount < 0){
            unFinishTaskCount = 0;
        }

        boolean isFinishAll=((taskNum>0)&&(taskNum==finishTaskCount));
        //全部参与/完成
        if (isFinishAll) {
            //除了话题讨论显示参与，其他都显示完成。
            if (type.equals("4")){
                //全部参与
                //目前不需要显示全部参与，只需要显示参与人数即可。
                homeworkStatusLayout.setText(activity.getString(R.string.n_people_join,taskNum));
            }else {
                //全部完成
                homeworkStatusLayout.setText(activity.getString(R.string.n_finish_all,taskNum));
            }
        }
        //部分参与/完成
        else {
            if (type.equals("4")) {
                //已参与/未参与
                //只显示已参与人数
                homeworkStatusLayout.setText(activity.getString(R.string.n_people_join,finishTaskCount));
            } else {
                //已完成/未完成
                homeworkStatusLayout.setText(activity.getString(R.string.n_finish,finishTaskCount)
                +" / "+activity.getString(R.string.n_unfinish,unFinishTaskCount));
            }

        }

        //讨论数
        textView = (TextView) view.findViewById(R.id.tv_discuss_count);
        if (textView != null) {
            //看课件的讨论功能暂未实现，需要隐藏一下。
//            if (type.equals("1")){
//                textView.setVisibility(View.INVISIBLE);
//            }else {
                textView.setVisibility(View.INVISIBLE);
//            }
            textView.setText(activity.getString(R.string.discussion,data.getCommentCount()));
        }
        //全部完成
        if (isFinishAll) {
            finishStatusText = finishAllText;
        } else {
            finishStatusText = joni_or_finish_Text + unjoin_or_unfinish_Text;
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
        HomeworkListInfo data = (HomeworkListInfo) holder.data;
        if (data!=null) {
            int taskType = -1;
            if(!TextUtils.isEmpty(data.getTaskType())) {
                taskType = Integer.parseInt(data.getTaskType());
            }
            //只给不是老师身份调更新阅读状态接口
            if (roleType != RoleType.ROLE_TYPE_TEACHER && !data.isStudentIsRead()) {
                if (taskType== StudyTaskType.SUBMIT_HOMEWORK
                        || taskType== StudyTaskType.TOPIC_DISCUSSION
                        || taskType == StudyTaskType.RETELL_WAWA_COURSE
                        || taskType == StudyTaskType.INTRODUCTION_WAWA_COURSE
                        || taskType == StudyTaskType.ENGLISH_WRITING
                        || taskType == StudyTaskType.TASK_ORDER
                        || taskType == StudyTaskType.LISTEN_READ_AND_WRITE
                        || taskType == StudyTaskType.SUPER_TASK
                        || taskType == StudyTaskType.MULTIPLE_TASK_ORDER
                        || taskType == StudyTaskType.MULTIPLE_RETELL_COURSE){
                    //学生更新提交作业和讨论话题已读接口
                    UpdateStudentIsRead(data.getTaskId(),memberId,String.valueOf(taskType));
                }else {
                    //学生更新看微课、看课件、看作业已读接口
                    if (roleType == RoleType.ROLE_TYPE_STUDENT
                            && taskType != StudyTaskType.NEW_WATACH_WAWA_COURSE) {
                        //目前新版看课件已读本地处理，接口不支持。
                        updateReadStatus(data.getTaskId(), memberId);
                    }
                }
            }
        }

    }

    protected abstract void UpdateStudentIsRead(String taskId, String memberId, String taskType);

    protected abstract void updateReadStatus(String taskId, String memberId);

}
