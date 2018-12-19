package com.galaxyschool.app.wawaschool.common;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.codingmaster.slib.S;
import com.galaxyschool.app.wawaschool.EnglishWritingCommitActivity;
import com.galaxyschool.app.wawaschool.EnglishWritingCompletedActivity;
import com.galaxyschool.app.wawaschool.fragment.CampusPatrolPickerFragment;
import com.galaxyschool.app.wawaschool.fragment.HomeworkMainFragment;
import com.galaxyschool.app.wawaschool.pojo.HomeworkListInfo;
import com.galaxyschool.app.wawaschool.pojo.RoleType;


/**
 * Created by E450 on 2017/02/23.
 */
public class EnglishWritingUtils {

    //批改网APP KEY
    public static final String APP_KEY = "2b2092d2c728327603238cab7ed172a2";

    //批改网APP SECRET
    public static final String APP_SECRET = "09b3d24ae371c36694ad448d6fce2c11";

    //批改网授权接口
    public static final String GET_ACCESS_TOKEN_URL = "http://api.pigai.org/oauth2/access_token";

    public static final String TIME_AT_GET_ACCESS_TOKEN = "time_at_get_access_token";

    //默认失效时间范围，比默认的7200s要短一点,ms级别。
    public static final long DELTA_TIME_ACCESS_TOKEN_EXPIRE = 6400 * 1000;

    //作文分析异步接口
    public static final String COMPOSITION_ANALYSIS_ASYNC_URL =
            "http://api.pigai.org/essays/rapid_experience_async";

    //作文分析同步接口
    public static final String COMPOSITION_ANALYSIS_URL =
            "http://api.pigai.org/essays/rapid_experience";

    public static final String TITLE = "123木头人";

    public static final String CONTENT = "上山打老虎";

    public static final String UTF_EIGHT_ENCODING = "UTF-8";

    /**
     * 根据角色来跳转页面
     * @param activity
     * @param roleType
     * @param data
     */
    public static void enterEnglishWritingPageByRoleType(Activity activity, int roleType,
                                                         String memberId, String sortStudentId,
                                                         String studentId, HomeworkListInfo data,
                                                         String[] childIdArray) {
        if (activity == null || data == null ){
            return;
        }
        switch (roleType){

            //老师
            case RoleType.ROLE_TYPE_TEACHER :
                enterEnglishWritingCommitActivity(activity,roleType,data);
                break;
            //学生
            case RoleType.ROLE_TYPE_STUDENT :
                //家长
            case RoleType.ROLE_TYPE_PARENT :
                enterEnglishWritingMake(activity,roleType,memberId,sortStudentId,studentId,data,
                        childIdArray);
                break;
        }

    }

    /**
     * 进入英文写作页面
     * @param activity
     * @param roleType
     * @param data
     */
    private static void enterEnglishWritingCommitActivity(Activity activity,int roleType,
                                                          HomeworkListInfo data) {

        if (data == null || activity == null){
            return;
        }
        Intent intent = new Intent(activity,EnglishWritingCommitActivity.class);
        intent.putExtra("roleType", roleType);
        intent.putExtra("taskId",data.getTaskId());
        //标题
        intent.putExtra("taskTitle",data.getTaskTitle());
        //学生id
        intent.putExtra("studentId", "");
        //排序
        intent.putExtra("sortStudentId", "");
        activity.startActivityForResult(intent, CampusPatrolPickerFragment.
                REQUEST_CODE_ENGLISH_WRITING_COMMIT);
    }

    /**
     * 进入学生英文写作的界面
     */
    private static void enterEnglishWritingMake(Activity activity,int roleType,String memberId,
                                                String sortStudentId,String studentId,
                                                HomeworkListInfo data,String[] childIdArray){
        if (activity == null || data == null){
            return;
        }
        Intent intent=new Intent(activity, EnglishWritingCompletedActivity.class);
        if (roleType == RoleType.ROLE_TYPE_STUDENT) {
            if (!TextUtils.isEmpty(memberId)) {
                intent.putExtra(EnglishWritingCompletedActivity.STUDENTID, memberId);
                intent.putExtra(EnglishWritingCompletedActivity.SORTSTUDENTID, memberId);
            }
        } else if (roleType == RoleType.ROLE_TYPE_PARENT) {
            //家长传递多个孩子id，以逗号分隔。
            if (!TextUtils.isEmpty(sortStudentId)) {
                intent.putExtra(EnglishWritingCompletedActivity.SORTSTUDENTID, sortStudentId);
            }
            //单个孩子id
            if (!TextUtils.isEmpty(studentId)) {
                intent.putExtra(EnglishWritingCompletedActivity.STUDENTID, studentId);
            }

            //传递孩子数组
            if (childIdArray != null && childIdArray.length > 0){
                intent.putExtra(HomeworkMainFragment.Constants.EXTRA_CHILD_ID_ARRAY,childIdArray);
            }
        }
        intent.putExtra(EnglishWritingCompletedActivity.ROLETYPE, roleType);
        intent.putExtra(EnglishWritingCompletedActivity.TASKID, data.getTaskId());
        intent.putExtra(EnglishWritingCompletedActivity.TASKTYPE, data.getTaskType());
        activity.startActivityForResult(intent, CampusPatrolPickerFragment.
                REQUEST_CODE_ENGLISH_WRITING_COMMIT);
    }

    /**
     * 计算单词个数
     * @param context
     * @return
     */
    public static int calculateWordsCount(String context){

        int count = 0;
        if (!TextUtils.isEmpty(context)) {
            String [] array = context.split("\\s+");
            int size = array.length;
            if (size > 0){
                for (int i = 0;i < size;i++){
                    String item = array[i];
                    if (!TextUtils.isEmpty(item)) {
                        count = item.split("\\n+").length + count;
                    }
                }
            }
        }
        return count;
    }
}
