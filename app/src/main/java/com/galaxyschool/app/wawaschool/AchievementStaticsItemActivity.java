package com.galaxyschool.app.wawaschool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.galaxyschool.app.wawaschool.fragment.AchievementStatisticsFragment;
import com.galaxyschool.app.wawaschool.fragment.AchievementStatisticsItemFragment;
import com.galaxyschool.app.wawaschool.pojo.CommitTask;
import com.galaxyschool.app.wawaschool.pojo.ExerciseAnswerCardParam;

import java.util.ArrayList;

public class AchievementStaticsItemActivity extends BaseFragmentActivity {

    public static void start(Activity activity,
                             ArrayList<CommitTask> data,
                             String title,
                             int type,
                             ExerciseAnswerCardParam cardParam) {
        Intent intent = new Intent(activity, AchievementStaticsItemActivity.class);
        Bundle args = new Bundle();
        args.putParcelableArrayList(AchievementStatisticsFragment.Constants.RETELL_DATA_LIST, data);
        args.putString(AchievementStatisticsFragment.Constants.TITLE, title);
        args.putInt(AchievementStatisticsFragment.Constants.DATA_TYPE, type);
        if (cardParam != null) {
            args.putSerializable(ExerciseAnswerCardParam.class.getSimpleName(), cardParam);
        }
        intent.putExtras(args);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
        AchievementStatisticsItemFragment fragment = new AchievementStatisticsItemFragment();
        fragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_body, fragment, AchievementStatisticsItemFragment.TAG)
                .commit();
    }

}
