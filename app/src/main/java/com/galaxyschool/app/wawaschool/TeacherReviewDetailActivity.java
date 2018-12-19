package com.galaxyschool.app.wawaschool;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.galaxyschool.app.wawaschool.fragment.AirClassroomDetailFragment;
import com.galaxyschool.app.wawaschool.fragment.ReviewDetailFragment;
import com.galaxyschool.app.wawaschool.fragment.TeacherReviewFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * ======================================================
 * Created by : Brave_Qu on 2018/9/18 0018 15:17
 * Describe:老师点评和打开详情activity
 * ======================================================
 */
public class TeacherReviewDetailActivity extends BaseFragmentActivity{

    public interface Constant{
        String ENTER_TEACHER_REVIEW_DETAIL = "enter_teacher_review_detail";
        String COMMIT_TASK_ONLINE_ID = "commit_task_online_id";
        String COMMIT_TASK_ID = "commit_task_id";
        String SCORE_RULE = "score_rule";
        String EVAL_SCORE = "eval_score";
        String IS_EVAL_REVIEW = "is_eval_review";
        String PAGE_SCORE_LIST = "page_score_list";
        String TEACHER_REVIEW_SCORE = "teacher_review_score";
        String TEACHER_COMMENT_DATA = "teacher_comment_data";
        String ONLINE_RESURL = "online_resurl";
        String ORIENTATION = "orientation";
        String HAS_REVIEW_PERMISSION = "has_review_permission";
    }

    /**
     * 进入老师打分页
     * @param activity
     * @param commitTaskOnlineId 来自mooc 传
     * @param commitTaskId 来自两栖蛙蛙传
     * @param evalScore 语音评测的分数
     */
    public static void start(Activity activity,
                             String commitTaskOnlineId,
                             String commitTaskId,
                             int scoreRule,
                             String evalScore){
        Intent intent = new Intent(activity,TeacherReviewDetailActivity.class);
        Bundle args = new Bundle();
        args.putString(Constant.COMMIT_TASK_ID,commitTaskId);
        args.putString(Constant.COMMIT_TASK_ONLINE_ID,commitTaskOnlineId);
        args.putInt(Constant.SCORE_RULE,scoreRule);
        args.putBoolean(Constant.ENTER_TEACHER_REVIEW_DETAIL,true);
        args.putString(Constant.EVAL_SCORE,evalScore);
        intent.putExtras(args);
        activity.startActivity(intent);
    }

    /**
     * 进入点评的详情界面
     * @param activity
     * @param isEvalReview 老师是否点评
     * @param hasReviewPermission   是否有点评的权限（跟显示立即点评的权限一样）
     * @param pageScoreList 返回page分数list
     * @param teacherReviewScore 老师点评的分数（没有就传空）
     * @param teacherCommentData 老师的评语（没有传空）
     * @param scoreRule 打分规则
     * @param mOrientation 方向
     * @param onlineResUrl studentResUrl
     *  @param commitTaskOnlineId 来自mooc 传
     * @param commitTaskId 来自两栖蛙蛙传
     */
    public static void start(Activity activity,
                             boolean isEvalReview,
                             boolean hasReviewPermission,
                             ArrayList<Integer> pageScoreList,
                             String teacherReviewScore,
                             String teacherCommentData,
                             int scoreRule,
                             int mOrientation,
                             String onlineResUrl,
                             String commitTaskOnlineId,
                             String commitTaskId){
        Intent intent = new Intent(activity,TeacherReviewDetailActivity.class);
        Bundle args = new Bundle();
        args.putBoolean(Constant.ENTER_TEACHER_REVIEW_DETAIL,false);
        args.putBoolean(Constant.IS_EVAL_REVIEW,isEvalReview);
        args.putBoolean(Constant.HAS_REVIEW_PERMISSION,hasReviewPermission);
        args.putIntegerArrayList(Constant.PAGE_SCORE_LIST, pageScoreList);
        args.putString(Constant.TEACHER_REVIEW_SCORE,teacherReviewScore);
        args.putString(Constant.TEACHER_COMMENT_DATA,teacherCommentData);
        args.putInt(Constant.SCORE_RULE,scoreRule);
        args.putInt(Constant.ORIENTATION, mOrientation);
        args.putString(Constant.ONLINE_RESURL, onlineResUrl);
        args.putString(Constant.COMMIT_TASK_ID,commitTaskId);
        args.putString(Constant.COMMIT_TASK_ONLINE_ID,commitTaskOnlineId);
        intent.putExtras(args);
        activity.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
        Bundle args = getIntent().getExtras();
        if (args != null){
            if (args.getBoolean(Constant.ENTER_TEACHER_REVIEW_DETAIL)){
                //进入老师详情界面
                enterTeacherReviewDetail(args);
            } else {
                //点评的详情情况
                enterScoreListDetail(args);
            }
        }
    }

    private void enterTeacherReviewDetail(Bundle args){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.activity_body, TeacherReviewFragment.newInstance(args), TeacherReviewFragment.TAG);
        ft.commitAllowingStateLoss();
    }

    private void enterScoreListDetail(Bundle args){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.activity_body, ReviewDetailFragment.newInstance(args), ReviewDetailFragment.TAG);
        ft.commitAllowingStateLoss();
    }
}
