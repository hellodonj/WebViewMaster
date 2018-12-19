package com.galaxyschool.app.wawaschool;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import com.galaxyschool.app.wawaschool.fragment.AnswerCardDetailFragment;
import com.galaxyschool.app.wawaschool.pojo.ExerciseAnswerCardParam;
import com.galaxyschool.app.wawaschool.pojo.LearnTaskInfo;

public class AnswerCardDetailActivity extends BaseFragmentActivity{
    private Fragment fragment = null;

    public static void start(Activity activity,
                             ExerciseAnswerCardParam cardParam){
        if (activity == null){
            return;
        }
        Intent intent = new Intent(activity,AnswerCardDetailActivity.class);
        Bundle args = new Bundle();
        args.putSerializable(ExerciseAnswerCardParam.class.getSimpleName(),cardParam);
        intent.putExtras(args);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
        Bundle args = getIntent().getExtras();
        fragment = new AnswerCardDetailFragment();
        if (args != null) {
            fragment.setArguments(args);
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.activity_body, fragment, AnswerCardDetailFragment.TAG);
        ft.commit();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
