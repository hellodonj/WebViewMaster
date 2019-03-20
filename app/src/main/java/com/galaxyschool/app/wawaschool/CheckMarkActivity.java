package com.galaxyschool.app.wawaschool;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import com.galaxyschool.app.wawaschool.fragment.CheckMarkFragment;
import com.galaxyschool.app.wawaschool.pojo.CommitTask;
import com.galaxyschool.app.wawaschool.pojo.ExerciseAnswerCardParam;
import com.galaxyschool.app.wawaschool.pojo.StudyTask;

public class CheckMarkActivity extends BaseFragmentActivity{
    private CheckMarkFragment fragment = null;

    public static void start(Activity activity, ExerciseAnswerCardParam cardParam){
        Intent intent = new Intent(activity,CheckMarkActivity.class);
        Bundle args = new Bundle();
        if (cardParam != null) {
            args.putSerializable(ExerciseAnswerCardParam.class.getSimpleName(), cardParam);
        }
        intent.putExtras(args);
        activity.startActivity(intent);
    }

    public static void start(Activity activity,
                             CommitTask commitTask,
                             StudyTask studyTask){
        Intent intent = new Intent(activity,CheckMarkActivity.class);
        Bundle args = new Bundle();
        if (commitTask != null) {
            args.putSerializable(CheckMarkFragment.Constants.COMMIT_TASK,commitTask);
        }
        if (studyTask != null) {
            args.putSerializable(CheckMarkFragment.Constants.STUDYTASK,studyTask);
        }
        intent.putExtras(args);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
        Bundle args = getIntent().getExtras();
        fragment = new CheckMarkFragment();
        if (args != null) {
            fragment.setArguments(args);
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.activity_body, fragment, CheckMarkFragment.TAG);
        ft.commit();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN){
            if (fragment != null && fragment.isVisible()) {
                fragment.upDateDeleteButtonShowStatus(null,false);
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}
