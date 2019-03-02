package com.galaxyschool.app.wawaschool.common;

import android.app.Activity;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.fragment.library.DataAdapter;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;
import com.galaxyschool.app.wawaschool.views.ContactsGridDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * ======================================================
 * Created by : Brave_Qu on 2018/6/23 16:06
 * E-Mail Address:863378689@qq.com
 * Describe:布置任务弹出的popWindow
 * ======================================================
 */
public class ArrangeLearningTasksUtil {
    private Activity activity;
    private boolean isIntroSuperTask;
    private String[] taskTypes;
    private ArrangeLearningTaskListener listener;

    public static ArrangeLearningTasksUtil getInstance() {
        return new ArrangeLearningTasksUtil();
    }


    public ArrangeLearningTasksUtil setActivity(Activity activity) {
        this.activity = activity;
        return this;
    }

    public ArrangeLearningTasksUtil setCallBackListener(ArrangeLearningTaskListener listener) {
        this.listener = listener;
        return this;
    }

    public ArrangeLearningTasksUtil setIsIntroSuperTask(boolean isIntroSuperTask) {
        this.isIntroSuperTask = isIntroSuperTask;
        return this;
    }

    public ArrangeLearningTasksUtil show() {
        showTaskTypeDialog();
        return this;
    }

    /**
     * 显示布置任务的Dialog
     */
    private void showTaskTypeDialog() {
        DataAdapter.AdapterViewCreator adapterViewCreator = new DataAdapter.AdapterViewCreator() {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView != null) {
                    String title = (String) convertView.getTag();
                    TextView textView = (TextView) convertView.findViewById(
                            R.id.contacts_dialog_grid_item_title);
                    if (textView != null) {
                        textView.setText(title);
                        if (!com.lqwawa.intleducation.common.utils.Utils.isZh(activity)) {
                            //解决英文状态下显示不全问题
                            textView.setLines(2);
                        }
                    }
                }
                return convertView;
            }
        };
        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                createNewTask(position);
            }
        };

        if (isIntroSuperTask) {
            taskTypes = new String[]{
                    activity.getString(R.string.str_q_dubbing),
                    activity.getString(R.string.do_task),
                    activity.getString(R.string.retell_course),
                    activity.getString(R.string.look_through_courseware),
                    activity.getString(R.string.english_writing),
                    activity.getString(R.string.other),
                    activity.getString(R.string.discuss_topic)
            };
        } else {
            taskTypes = new String[]{
                    activity.getString(R.string.str_super_task),
                    activity.getString(R.string.str_q_dubbing),
                    activity.getString(R.string.do_task),
                    activity.getString(R.string.retell_course),
//                    activity.getString(R.string.str_listen_read_and_write),
                    activity.getString(R.string.look_through_courseware),
                    activity.getString(R.string.english_writing),
//                    activity.getString(R.string.introduction),
                    activity.getString(R.string.discuss_topic),
                    activity.getString(R.string.other),
            };
        }
        List<String> taskTypeList = new ArrayList<String>();
        for (int i = 0, length = taskTypes.length; i < length; i++) {
            taskTypeList.add(taskTypes[i]);
        }
        ContactsGridDialog dialog = new ContactsGridDialog(
                activity,
                R.style.Theme_ContactsDialog,
                isIntroSuperTask ? activity.getString(R.string.str_add_task) : activity.getString
                        (R.string.assign_task_line),
                taskTypeList,
                R.layout.contacts_dialog_grid_text_item,
                adapterViewCreator,
                onItemClickListener,
                activity.getString(R.string.cancel),
                null);
        GridView gridView = (GridView) dialog.getAbsListView();
        if (gridView != null) {
            gridView.setNumColumns(2);
        }
        ImageView leftCancelImageView = dialog.getLeftCancelImageView();
        if (leftCancelImageView != null) {
            leftCancelImageView.setVisibility(View.VISIBLE);
        }

        TextView titleTextView = dialog.getTitleTextView();
        if (titleTextView != null) {
            titleTextView.setTextColor(Color.WHITE);
        }

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = (int) (ScreenUtils.getScreenWidth(activity) * 0.95f);
        dialog.getWindow().setAttributes(params);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        dialog.show();
    }

    //布置任务
    private void createNewTask(int position) {
        int studyTaskType = getStudyTaskType(position);
        if (studyTaskType < 0) {
            return;
        }
        switch (studyTaskType) {
            case StudyTaskType.INTRODUCTION_WAWA_COURSE://导读
                enterIntroductionCourse(activity.getString(R.string.introduction),
                        StudyTaskType.INTRODUCTION_WAWA_COURSE);
                break;
            case StudyTaskType.WATCH_WAWA_COURSE:
                enterIntroductionCourse(activity.getString(R.string.look_through_courseware),
                        StudyTaskType.WATCH_WAWA_COURSE);
                break;
            case StudyTaskType.RETELL_WAWA_COURSE://复述课件
                enterIntroductionCourse(activity.getString(R.string.retell_course),
                        StudyTaskType.RETELL_WAWA_COURSE);
                break;
            case StudyTaskType.WATCH_HOMEWORK://作业
                enterIntroductionCourse(activity.getString(R.string.other),
                        StudyTaskType.WATCH_HOMEWORK);
                break;
            case StudyTaskType.TOPIC_DISCUSSION://话题讨论
                enterIntroductionCourse(activity.getString(R.string.discuss_topic),
                        StudyTaskType.TOPIC_DISCUSSION);
                break;
            case StudyTaskType.ENGLISH_WRITING://英文写作
                enterIntroductionCourse(activity.getString(R.string.english_writing),
                        StudyTaskType.ENGLISH_WRITING);
                break;
            case StudyTaskType.TASK_ORDER://做任务单
                enterIntroductionCourse(activity.getString(R.string.do_task),
                        StudyTaskType.TASK_ORDER);
                break;
            case StudyTaskType.LISTEN_READ_AND_WRITE://听说+读写
                enterIntroductionCourse(activity.getString(R.string.str_listen_read_and_write),
                        StudyTaskType.LISTEN_READ_AND_WRITE);
                break;
            case StudyTaskType.SUPER_TASK://综合任务
                enterIntroductionCourse(activity.getString(R.string.str_super_task),
                        StudyTaskType.SUPER_TASK);
                break;
            case StudyTaskType.Q_DUBBING://q配音
                enterIntroductionCourse(activity.getString(R.string.str_q_dubbing),
                        StudyTaskType.Q_DUBBING);
                break;
        }
    }

    private int getStudyTaskType(int position) {
        int type = -1;
        String selectTextString = taskTypes[position];
        if (TextUtils.equals(selectTextString, activity.getString(R.string.str_super_task))) {
            type = StudyTaskType.SUPER_TASK;
        } else if (TextUtils.equals(selectTextString, activity.getString(R.string.do_task))) {
            type = StudyTaskType.TASK_ORDER;
        } else if (TextUtils.equals(selectTextString, activity.getString(R.string.retell_course))) {
            type = StudyTaskType.RETELL_WAWA_COURSE;
        } else if (TextUtils.equals(selectTextString, activity.getString(R.string.str_listen_read_and_write))) {
            type = StudyTaskType.LISTEN_READ_AND_WRITE;
        } else if (TextUtils.equals(selectTextString, activity.getString(R.string.look_through_courseware))) {
            type = StudyTaskType.WATCH_WAWA_COURSE;
        } else if (TextUtils.equals(selectTextString, activity.getString(R.string.english_writing))) {
            type = StudyTaskType.ENGLISH_WRITING;
        } else if (TextUtils.equals(selectTextString, activity.getString(R.string.introduction))) {
            type = StudyTaskType.INTRODUCTION_WAWA_COURSE;
        } else if (TextUtils.equals(selectTextString, activity.getString(R.string.discuss_topic))) {
            type = StudyTaskType.TOPIC_DISCUSSION;
        } else if (TextUtils.equals(selectTextString, activity.getString(R.string.other))) {
            type = StudyTaskType.WATCH_HOMEWORK;
        } else if (TextUtils.equals(selectTextString, activity.getString(R.string.str_q_dubbing))) {
            type = StudyTaskType.Q_DUBBING;
        }
        return type;
    }


    /**
     * 进入制作界面
     */
    private void enterIntroductionCourse(String title, int type) {
        if (listener != null) {
            listener.selectedTypeData(title, type);
        }
    }

    public interface ArrangeLearningTaskListener {
        void selectedTypeData(String title, int type);
    }

}
